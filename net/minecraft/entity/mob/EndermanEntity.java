package net.minecraft.entity.mob;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.class_3133;
import net.minecraft.class_4079;
import net.minecraft.class_4342;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.pathing.LandType;
import net.minecraft.entity.attribute.AttributeModifier;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public class EndermanEntity extends HostileEntity {
	private static final UUID ATTACKING_SPEED_BOOST_UUID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
	private static final AttributeModifier endermanAttributeModifier = new AttributeModifier(ATTACKING_SPEED_BOOST_UUID, "Attacking speed boost", 0.15F, 0)
		.setSerialized(false);
	private static final TrackedData<Optional<BlockState>> field_14748 = DataTracker.registerData(
		EndermanEntity.class, TrackedDataHandlerRegistry.OPTIONAL_BLOCK_STATE
	);
	private static final TrackedData<Boolean> field_14749 = DataTracker.registerData(EndermanEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private int field_14750;
	private int field_14751;

	public EndermanEntity(World world) {
		super(EntityType.ENDERMAN, world);
		this.setBounds(0.6F, 2.9F);
		this.stepHeight = 1.0F;
		this.method_13076(LandType.WATER, -1.0F);
	}

	@Override
	protected void initGoals() {
		this.goals.add(0, new SwimGoal(this));
		this.goals.add(2, new MeleeAttackGoal(this, 1.0, false));
		this.goals.add(7, new class_3133(this, 1.0, 0.0F));
		this.goals.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goals.add(8, new LookAroundGoal(this));
		this.goals.add(10, new EndermanEntity.PlaceBlockGoal(this));
		this.goals.add(11, new EndermanEntity.PickUpBlockGoal(this));
		this.attackGoals.add(1, new EndermanEntity.TeleportTowardsPlayerGoal(this));
		this.attackGoals.add(2, new RevengeGoal(this, false));
		this.attackGoals.add(3, new FollowTargetGoal(this, EndermiteEntity.class, 10, true, false, EndermiteEntity::isPlayerSpawned));
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(40.0);
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3F);
		this.initializeAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(7.0);
		this.initializeAttribute(EntityAttributes.GENERIC_FOLLOW_RANGE).setBaseValue(64.0);
	}

	@Override
	public void setTarget(@Nullable LivingEntity target) {
		super.setTarget(target);
		EntityAttributeInstance entityAttributeInstance = this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED);
		if (target == null) {
			this.field_14751 = 0;
			this.dataTracker.set(field_14749, false);
			entityAttributeInstance.method_6193(endermanAttributeModifier);
		} else {
			this.field_14751 = this.ticksAlive;
			this.dataTracker.set(field_14749, true);
			if (!entityAttributeInstance.hasModifier(endermanAttributeModifier)) {
				entityAttributeInstance.addModifier(endermanAttributeModifier);
			}
		}
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_14748, Optional.empty());
		this.dataTracker.startTracking(field_14749, false);
	}

	public void method_13217() {
		if (this.ticksAlive >= this.field_14750 + 400) {
			this.field_14750 = this.ticksAlive;
			if (!this.isSilent()) {
				this.world.playSound(this.x, this.y + (double)this.getEyeHeight(), this.z, Sounds.ENTITY_ENDERMAN_STARE, this.getSoundCategory(), 2.5F, 1.0F, false);
			}
		}
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (field_14749.equals(data) && this.isAngry() && this.world.isClient) {
			this.method_13217();
		}

		super.onTrackedDataSet(data);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		BlockState blockState = this.getCarriedBlock();
		if (blockState != null) {
			nbt.put("carriedBlockState", NbtHelper.method_20139(blockState));
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		BlockState blockState = null;
		if (nbt.contains("carriedBlockState", 10)) {
			blockState = NbtHelper.toBlockState(nbt.getCompound("carriedBlockState"));
			if (blockState.isAir()) {
				blockState = null;
			}
		}

		this.setCarriedBlock(blockState);
	}

	private boolean isPlayerStaring(PlayerEntity player) {
		ItemStack itemStack = player.inventory.field_15083.get(3);
		if (itemStack.getItem() == Blocks.CARVED_PUMPKIN.getItem()) {
			return false;
		} else {
			Vec3d vec3d = player.getRotationVector(1.0F).normalize();
			Vec3d vec3d2 = new Vec3d(
				this.x - player.x, this.getBoundingBox().minY + (double)this.getEyeHeight() - (player.y + (double)player.getEyeHeight()), this.z - player.z
			);
			double d = vec3d2.length();
			vec3d2 = vec3d2.normalize();
			double e = vec3d.dotProduct(vec3d2);
			return e > 1.0 - 0.025 / d ? player.canSee(this) : false;
		}
	}

	@Override
	public float getEyeHeight() {
		return 2.55F;
	}

	@Override
	public void tickMovement() {
		if (this.world.isClient) {
			for (int i = 0; i < 2; i++) {
				this.world
					.method_16343(
						class_4342.field_21361,
						this.x + (this.random.nextDouble() - 0.5) * (double)this.width,
						this.y + this.random.nextDouble() * (double)this.height - 0.25,
						this.z + (this.random.nextDouble() - 0.5) * (double)this.width,
						(this.random.nextDouble() - 0.5) * 2.0,
						-this.random.nextDouble(),
						(this.random.nextDouble() - 0.5) * 2.0
					);
			}
		}

		this.jumping = false;
		super.tickMovement();
	}

	@Override
	protected void mobTick() {
		if (this.method_15574()) {
			this.damage(DamageSource.DROWN, 1.0F);
		}

		if (this.world.isDay() && this.ticksAlive >= this.field_14751 + 600) {
			float f = this.getBrightnessAtEyes();
			if (f > 0.5F && this.world.method_8555(new BlockPos(this)) && this.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F) {
				this.setTarget(null);
				this.teleportRandomly();
			}
		}

		super.mobTick();
	}

	protected boolean teleportRandomly() {
		double d = this.x + (this.random.nextDouble() - 0.5) * 64.0;
		double e = this.y + (double)(this.random.nextInt(64) - 32);
		double f = this.z + (this.random.nextDouble() - 0.5) * 64.0;
		return this.teleportTo(d, e, f);
	}

	protected boolean teleportTo(Entity entity) {
		Vec3d vec3d = new Vec3d(
			this.x - entity.x, this.getBoundingBox().minY + (double)(this.height / 2.0F) - entity.y + (double)entity.getEyeHeight(), this.z - entity.z
		);
		vec3d = vec3d.normalize();
		double d = 16.0;
		double e = this.x + (this.random.nextDouble() - 0.5) * 8.0 - vec3d.x * 16.0;
		double f = this.y + (double)(this.random.nextInt(16) - 8) - vec3d.y * 16.0;
		double g = this.z + (this.random.nextDouble() - 0.5) * 8.0 - vec3d.z * 16.0;
		return this.teleportTo(e, f, g);
	}

	private boolean teleportTo(double x, double y, double z) {
		boolean bl = this.method_13071(x, y, z);
		if (bl) {
			this.world.playSound(null, this.prevX, this.prevY, this.prevZ, Sounds.ENTITY_ENDERMAN_TELEPORT, this.getSoundCategory(), 1.0F, 1.0F);
			this.playSound(Sounds.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
		}

		return bl;
	}

	@Override
	protected Sound ambientSound() {
		return this.isAngry() ? Sounds.ENTITY_ENDERMAN_SCREAM : Sounds.ENTITY_ENDERMAN_AMBIENT;
	}

	@Override
	protected Sound getHurtSound(DamageSource damageSource) {
		return Sounds.ENTITY_ENDERMAN_HURT;
	}

	@Override
	protected Sound deathSound() {
		return Sounds.ENTITY_ENDERMAN_DEATH;
	}

	@Override
	protected void method_4472(boolean bl, int i) {
		super.method_4472(bl, i);
		BlockState blockState = this.getCarriedBlock();
		if (blockState != null) {
			this.method_15560(blockState.getBlock());
		}
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.ENDERMAN_ENTITIE;
	}

	public void setCarriedBlock(@Nullable BlockState block) {
		this.dataTracker.set(field_14748, Optional.ofNullable(block));
	}

	@Nullable
	public BlockState getCarriedBlock() {
		return (BlockState)this.dataTracker.get(field_14748).orElse(null);
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else if (source instanceof ProjectileDamageSource) {
			for (int i = 0; i < 64; i++) {
				if (this.teleportRandomly()) {
					return true;
				}
			}

			return false;
		} else {
			boolean bl = super.damage(source, amount);
			if (source.bypassesArmor() && this.random.nextInt(10) != 0) {
				this.teleportRandomly();
			}

			return bl;
		}
	}

	public boolean isAngry() {
		return this.dataTracker.get(field_14749);
	}

	static class PickUpBlockGoal extends Goal {
		private final EndermanEntity enderman;

		public PickUpBlockGoal(EndermanEntity endermanEntity) {
			this.enderman = endermanEntity;
		}

		@Override
		public boolean canStart() {
			if (this.enderman.getCarriedBlock() != null) {
				return false;
			} else {
				return !this.enderman.world.getGameRules().getBoolean("mobGriefing") ? false : this.enderman.getRandom().nextInt(20) == 0;
			}
		}

		@Override
		public void tick() {
			Random random = this.enderman.getRandom();
			World world = this.enderman.world;
			int i = MathHelper.floor(this.enderman.x - 2.0 + random.nextDouble() * 4.0);
			int j = MathHelper.floor(this.enderman.y + random.nextDouble() * 3.0);
			int k = MathHelper.floor(this.enderman.z - 2.0 + random.nextDouble() * 4.0);
			BlockPos blockPos = new BlockPos(i, j, k);
			BlockState blockState = world.getBlockState(blockPos);
			Block block = blockState.getBlock();
			BlockHitResult blockHitResult = world.method_3615(
				new Vec3d((double)((float)MathHelper.floor(this.enderman.x) + 0.5F), (double)((float)j + 0.5F), (double)((float)MathHelper.floor(this.enderman.z) + 0.5F)),
				new Vec3d((double)((float)i + 0.5F), (double)((float)j + 0.5F), (double)((float)k + 0.5F)),
				class_4079.NEVER,
				true,
				false
			);
			boolean bl = blockHitResult != null && blockHitResult.getBlockPos().equals(blockPos);
			if (block.isIn(BlockTags.ENDERMAN_HOLDABLE) && bl) {
				this.enderman.setCarriedBlock(blockState);
				world.method_8553(blockPos);
			}
		}
	}

	static class PlaceBlockGoal extends Goal {
		private final EndermanEntity entity;

		public PlaceBlockGoal(EndermanEntity endermanEntity) {
			this.entity = endermanEntity;
		}

		@Override
		public boolean canStart() {
			if (this.entity.getCarriedBlock() == null) {
				return false;
			} else {
				return !this.entity.world.getGameRules().getBoolean("mobGriefing") ? false : this.entity.getRandom().nextInt(2000) == 0;
			}
		}

		@Override
		public void tick() {
			Random random = this.entity.getRandom();
			IWorld iWorld = this.entity.world;
			int i = MathHelper.floor(this.entity.x - 1.0 + random.nextDouble() * 2.0);
			int j = MathHelper.floor(this.entity.y + random.nextDouble() * 2.0);
			int k = MathHelper.floor(this.entity.z - 1.0 + random.nextDouble() * 2.0);
			BlockPos blockPos = new BlockPos(i, j, k);
			BlockState blockState = iWorld.getBlockState(blockPos);
			BlockState blockState2 = iWorld.getBlockState(blockPos.down());
			BlockState blockState3 = this.entity.getCarriedBlock();
			if (blockState3 != null && this.method_15861(iWorld, blockPos, blockState3, blockState, blockState2)) {
				iWorld.setBlockState(blockPos, blockState3, 3);
				this.entity.setCarriedBlock(null);
			}
		}

		private boolean method_15861(RenderBlockView renderBlockView, BlockPos blockPos, BlockState blockState, BlockState blockState2, BlockState blockState3) {
			return blockState2.isAir() && !blockState3.isAir() && blockState3.method_16897() && blockState.canPlaceAt(renderBlockView, blockPos);
		}
	}

	static class TeleportTowardsPlayerGoal extends FollowTargetGoal<PlayerEntity> {
		private final EndermanEntity field_14752;
		private PlayerEntity targetPlayer;
		private int lookAtPlayerWarmup;
		private int ticksSinceUnseenTeleport;

		public TeleportTowardsPlayerGoal(EndermanEntity endermanEntity) {
			super(endermanEntity, PlayerEntity.class, false);
			this.field_14752 = endermanEntity;
		}

		@Override
		public boolean canStart() {
			double d = this.getFollowRange();
			this.targetPlayer = this.field_14752
				.world
				.method_16319(
					this.field_14752.x,
					this.field_14752.y,
					this.field_14752.z,
					d,
					d,
					null,
					playerEntity -> playerEntity != null && this.field_14752.isPlayerStaring(playerEntity)
				);
			return this.targetPlayer != null;
		}

		@Override
		public void start() {
			this.lookAtPlayerWarmup = 5;
			this.ticksSinceUnseenTeleport = 0;
		}

		@Override
		public void stop() {
			this.targetPlayer = null;
			super.stop();
		}

		@Override
		public boolean shouldContinue() {
			if (this.targetPlayer != null) {
				if (!this.field_14752.isPlayerStaring(this.targetPlayer)) {
					return false;
				} else {
					this.field_14752.lookAtEntity(this.targetPlayer, 10.0F, 10.0F);
					return true;
				}
			} else {
				return this.target != null && this.target.isAlive() ? true : super.shouldContinue();
			}
		}

		@Override
		public void tick() {
			if (this.targetPlayer != null) {
				if (--this.lookAtPlayerWarmup <= 0) {
					this.target = this.targetPlayer;
					this.targetPlayer = null;
					super.start();
				}
			} else {
				if (this.target != null) {
					if (this.field_14752.isPlayerStaring(this.target)) {
						if (this.target.squaredDistanceTo(this.field_14752) < 16.0) {
							this.field_14752.teleportRandomly();
						}

						this.ticksSinceUnseenTeleport = 0;
					} else if (this.target.squaredDistanceTo(this.field_14752) > 256.0 && this.ticksSinceUnseenTeleport++ >= 30 && this.field_14752.teleportTo(this.target)) {
						this.ticksSinceUnseenTeleport = 0;
					}
				}

				super.tick();
			}
		}
	}
}
