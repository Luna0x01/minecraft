package net.minecraft.entity.mob;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.AttributeModifier;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EndermanEntity extends HostileEntity {
	private static final UUID ATTACKING_SPEED_BOOST_UUID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
	private static final AttributeModifier endermanAttributeModifier = new AttributeModifier(ATTACKING_SPEED_BOOST_UUID, "Attacking speed boost", 0.15F, 0)
		.setSerialized(false);
	private static final Set<Block> HOLDABLES = Sets.newIdentityHashSet();
	private boolean field_6155;

	public EndermanEntity(World world) {
		super(world);
		this.setBounds(0.6F, 2.9F);
		this.stepHeight = 1.0F;
		this.goals.add(0, new SwimGoal(this));
		this.goals.add(2, new MeleeAttackGoal(this, 1.0, false));
		this.goals.add(7, new WanderAroundGoal(this, 1.0));
		this.goals.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goals.add(8, new LookAroundGoal(this));
		this.goals.add(10, new EndermanEntity.PlaceBlockGoal(this));
		this.goals.add(11, new EndermanEntity.PickUpBlockGoal(this));
		this.attackGoals.add(1, new RevengeGoal(this, false));
		this.attackGoals.add(2, new EndermanEntity.TeleportTowardsPlayerGoal(this));
		this.attackGoals.add(3, new FollowTargetGoal(this, EndermiteEntity.class, 10, true, false, new Predicate<EndermiteEntity>() {
			public boolean apply(EndermiteEntity endermiteEntity) {
				return endermiteEntity.isPlayerSpawned();
			}
		}));
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
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.track(16, new Short((short)0));
		this.dataTracker.track(17, new Byte((byte)0));
		this.dataTracker.track(18, new Byte((byte)0));
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		BlockState blockState = this.getCarriedBlock();
		nbt.putShort("carried", (short)Block.getIdByBlock(blockState.getBlock()));
		nbt.putShort("carriedData", (short)blockState.getBlock().getData(blockState));
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		BlockState blockState;
		if (nbt.contains("carried", 8)) {
			blockState = Block.get(nbt.getString("carried")).stateFromData(nbt.getShort("carriedData") & '\uffff');
		} else {
			blockState = Block.getById(nbt.getShort("carried")).stateFromData(nbt.getShort("carriedData") & '\uffff');
		}

		this.setCarriedBlock(blockState);
	}

	private boolean isPlayerStaring(PlayerEntity player) {
		ItemStack itemStack = player.inventory.armor[3];
		if (itemStack != null && itemStack.getItem() == Item.fromBlock(Blocks.PUMPKIN)) {
			return false;
		} else {
			Vec3d vec3d = player.getRotationVector(1.0F).normalize();
			Vec3d vec3d2 = new Vec3d(
				this.x - player.x, this.getBoundingBox().minY + (double)(this.height / 2.0F) - (player.y + (double)player.getEyeHeight()), this.z - player.z
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
					.addParticle(
						ParticleType.NETHER_PORTAL,
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
		if (this.tickFire()) {
			this.damage(DamageSource.DROWN, 1.0F);
		}

		if (this.isAngry() && !this.field_6155 && this.random.nextInt(100) == 0) {
			this.method_3080(false);
		}

		if (this.world.isDay()) {
			float f = this.getBrightnessAtEyes(1.0F);
			if (f > 0.5F && this.world.hasDirectSunlight(new BlockPos(this)) && this.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F) {
				this.setTarget(null);
				this.method_3080(false);
				this.field_6155 = false;
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
		double e = this.x + (this.random.nextDouble() - 0.5) * 8.0 - vec3d.x * d;
		double f = this.y + (double)(this.random.nextInt(16) - 8) - vec3d.y * d;
		double g = this.z + (this.random.nextDouble() - 0.5) * 8.0 - vec3d.z * d;
		return this.teleportTo(e, f, g);
	}

	protected boolean teleportTo(double x, double y, double z) {
		double d = this.x;
		double e = this.y;
		double f = this.z;
		this.x = x;
		this.y = y;
		this.z = z;
		boolean bl = false;
		BlockPos blockPos = new BlockPos(this.x, this.y, this.z);
		if (this.world.blockExists(blockPos)) {
			boolean bl2 = false;

			while (!bl2 && blockPos.getY() > 0) {
				BlockPos blockPos2 = blockPos.down();
				Block block = this.world.getBlockState(blockPos2).getBlock();
				if (block.getMaterial().blocksMovement()) {
					bl2 = true;
				} else {
					this.y--;
					blockPos = blockPos2;
				}
			}

			if (bl2) {
				super.refreshPositionAfterTeleport(this.x, this.y, this.z);
				if (this.world.doesBoxCollide(this, this.getBoundingBox()).isEmpty() && !this.world.containsFluid(this.getBoundingBox())) {
					bl = true;
				}
			}
		}

		if (!bl) {
			this.updatePosition(d, e, f);
			return false;
		} else {
			int i = 128;

			for (int j = 0; j < i; j++) {
				double g = (double)j / ((double)i - 1.0);
				float h = (this.random.nextFloat() - 0.5F) * 0.2F;
				float k = (this.random.nextFloat() - 0.5F) * 0.2F;
				float l = (this.random.nextFloat() - 0.5F) * 0.2F;
				double m = d + (this.x - d) * g + (this.random.nextDouble() - 0.5) * (double)this.width * 2.0;
				double n = e + (this.y - e) * g + this.random.nextDouble() * (double)this.height;
				double o = f + (this.z - f) * g + (this.random.nextDouble() - 0.5) * (double)this.width * 2.0;
				this.world.addParticle(ParticleType.NETHER_PORTAL, m, n, o, (double)h, (double)k, (double)l);
			}

			this.world.playSound(d, e, f, "mob.endermen.portal", 1.0F, 1.0F);
			this.playSound("mob.endermen.portal", 1.0F, 1.0F);
			return true;
		}
	}

	@Override
	protected String getAmbientSound() {
		return this.isAngry() ? "mob.endermen.scream" : "mob.endermen.idle";
	}

	@Override
	protected String getHurtSound() {
		return "mob.endermen.hit";
	}

	@Override
	protected String getDeathSound() {
		return "mob.endermen.death";
	}

	@Override
	protected Item getDefaultDrop() {
		return Items.ENDER_PEARL;
	}

	@Override
	protected void dropLoot(boolean allowDrops, int lootingMultiplier) {
		Item item = this.getDefaultDrop();
		if (item != null) {
			int i = this.random.nextInt(2 + lootingMultiplier);

			for (int j = 0; j < i; j++) {
				this.dropItem(item, 1);
			}
		}
	}

	public void setCarriedBlock(BlockState block) {
		this.dataTracker.setProperty(16, (short)(Block.getByBlockState(block) & 65535));
	}

	public BlockState getCarriedBlock() {
		return Block.getStateFromRawId(this.dataTracker.getShort(16) & '\uffff');
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else {
			if (source.getAttacker() == null || !(source.getAttacker() instanceof EndermiteEntity)) {
				if (!this.world.isClient) {
					this.method_3080(true);
				}

				if (source instanceof EntityDamageSource && source.getAttacker() instanceof PlayerEntity) {
					if (source.getAttacker() instanceof ServerPlayerEntity && ((ServerPlayerEntity)source.getAttacker()).interactionManager.isCreative()) {
						this.method_3080(false);
					} else {
						this.field_6155 = true;
					}
				}

				if (source instanceof ProjectileDamageSource) {
					this.field_6155 = false;

					for (int i = 0; i < 64; i++) {
						if (this.teleportRandomly()) {
							return true;
						}
					}

					return false;
				}
			}

			boolean bl = super.damage(source, amount);
			if (source.bypassesArmor() && this.random.nextInt(10) != 0) {
				this.teleportRandomly();
			}

			return bl;
		}
	}

	public boolean isAngry() {
		return this.dataTracker.getByte(18) > 0;
	}

	public void method_3080(boolean bl) {
		this.dataTracker.setProperty(18, (byte)(bl ? 1 : 0));
	}

	static {
		HOLDABLES.add(Blocks.GRASS);
		HOLDABLES.add(Blocks.DIRT);
		HOLDABLES.add(Blocks.SAND);
		HOLDABLES.add(Blocks.GRAVEL);
		HOLDABLES.add(Blocks.YELLOW_FLOWER);
		HOLDABLES.add(Blocks.RED_FLOWER);
		HOLDABLES.add(Blocks.BROWN_MUSHROOM);
		HOLDABLES.add(Blocks.RED_MUSHROOM);
		HOLDABLES.add(Blocks.TNT);
		HOLDABLES.add(Blocks.CACTUS);
		HOLDABLES.add(Blocks.CLAY);
		HOLDABLES.add(Blocks.PUMPKIN);
		HOLDABLES.add(Blocks.MELON_BLOCK);
		HOLDABLES.add(Blocks.MYCELIUM);
	}

	static class PickUpBlockGoal extends Goal {
		private EndermanEntity enderman;

		public PickUpBlockGoal(EndermanEntity endermanEntity) {
			this.enderman = endermanEntity;
		}

		@Override
		public boolean canStart() {
			if (!this.enderman.world.getGameRules().getBoolean("mobGriefing")) {
				return false;
			} else {
				return this.enderman.getCarriedBlock().getBlock().getMaterial() != Material.AIR ? false : this.enderman.getRandom().nextInt(20) == 0;
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
			if (EndermanEntity.HOLDABLES.contains(block)) {
				this.enderman.setCarriedBlock(blockState);
				world.setBlockState(blockPos, Blocks.AIR.getDefaultState());
			}
		}
	}

	static class PlaceBlockGoal extends Goal {
		private EndermanEntity entity;

		public PlaceBlockGoal(EndermanEntity endermanEntity) {
			this.entity = endermanEntity;
		}

		@Override
		public boolean canStart() {
			if (!this.entity.world.getGameRules().getBoolean("mobGriefing")) {
				return false;
			} else {
				return this.entity.getCarriedBlock().getBlock().getMaterial() == Material.AIR ? false : this.entity.getRandom().nextInt(2000) == 0;
			}
		}

		@Override
		public void tick() {
			Random random = this.entity.getRandom();
			World world = this.entity.world;
			int i = MathHelper.floor(this.entity.x - 1.0 + random.nextDouble() * 2.0);
			int j = MathHelper.floor(this.entity.y + random.nextDouble() * 2.0);
			int k = MathHelper.floor(this.entity.z - 1.0 + random.nextDouble() * 2.0);
			BlockPos blockPos = new BlockPos(i, j, k);
			Block block = world.getBlockState(blockPos).getBlock();
			Block block2 = world.getBlockState(blockPos.down()).getBlock();
			if (this.canPlaceOn(world, blockPos, this.entity.getCarriedBlock().getBlock(), block, block2)) {
				world.setBlockState(blockPos, this.entity.getCarriedBlock(), 3);
				this.entity.setCarriedBlock(Blocks.AIR.getDefaultState());
			}
		}

		private boolean canPlaceOn(World world, BlockPos pos, Block blockCarried, Block blockAbove, Block blockBelow) {
			if (!blockCarried.canBePlacedAtPos(world, pos)) {
				return false;
			} else if (blockAbove.getMaterial() != Material.AIR) {
				return false;
			} else {
				return blockBelow.getMaterial() == Material.AIR ? false : blockBelow.renderAsNormalBlock();
			}
		}
	}

	static class TeleportTowardsPlayerGoal extends FollowTargetGoal {
		private PlayerEntity targetPlayer;
		private int lookAtPlayerWarmup;
		private int ticksSinceUnseenTeleport;
		private EndermanEntity enderman;

		public TeleportTowardsPlayerGoal(EndermanEntity endermanEntity) {
			super(endermanEntity, PlayerEntity.class, true);
			this.enderman = endermanEntity;
		}

		@Override
		public boolean canStart() {
			double d = this.getFollowRange();
			List<PlayerEntity> list = this.mob.world.getEntitiesInBox(PlayerEntity.class, this.mob.getBoundingBox().expand(d, 4.0, d), this.targetPredicate);
			Collections.sort(list, this.field_3629);
			if (list.isEmpty()) {
				return false;
			} else {
				this.targetPlayer = (PlayerEntity)list.get(0);
				return true;
			}
		}

		@Override
		public void start() {
			this.lookAtPlayerWarmup = 5;
			this.ticksSinceUnseenTeleport = 0;
		}

		@Override
		public void stop() {
			this.targetPlayer = null;
			this.enderman.method_3080(false);
			EntityAttributeInstance entityAttributeInstance = this.enderman.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED);
			entityAttributeInstance.method_6193(EndermanEntity.endermanAttributeModifier);
			super.stop();
		}

		@Override
		public boolean shouldContinue() {
			if (this.targetPlayer != null) {
				if (!this.enderman.isPlayerStaring(this.targetPlayer)) {
					return false;
				} else {
					this.enderman.field_6155 = true;
					this.enderman.lookAtEntity(this.targetPlayer, 10.0F, 10.0F);
					return true;
				}
			} else {
				return super.shouldContinue();
			}
		}

		@Override
		public void tick() {
			if (this.targetPlayer != null) {
				if (--this.lookAtPlayerWarmup <= 0) {
					this.target = this.targetPlayer;
					this.targetPlayer = null;
					super.start();
					this.enderman.playSound("mob.endermen.stare", 1.0F, 1.0F);
					this.enderman.method_3080(true);
					EntityAttributeInstance entityAttributeInstance = this.enderman.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED);
					entityAttributeInstance.addModifier(EndermanEntity.endermanAttributeModifier);
				}
			} else {
				if (this.target != null) {
					if (this.target instanceof PlayerEntity && this.enderman.isPlayerStaring((PlayerEntity)this.target)) {
						if (this.target.squaredDistanceTo(this.enderman) < 16.0) {
							this.enderman.teleportRandomly();
						}

						this.ticksSinceUnseenTeleport = 0;
					} else if (this.target.squaredDistanceTo(this.enderman) > 256.0 && this.ticksSinceUnseenTeleport++ >= 30 && this.enderman.teleportTo(this.target)) {
						this.ticksSinceUnseenTeleport = 0;
					}
				}

				super.tick();
			}
		}
	}
}
