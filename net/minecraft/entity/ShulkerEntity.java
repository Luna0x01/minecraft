package net.minecraft.entity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.attribute.AttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class ShulkerEntity extends GolemEntity implements Monster {
	private static final UUID field_14765 = UUID.fromString("7E0292F2-9434-48D5-A29F-9583AF7DF27F");
	private static final AttributeModifier field_14766 = new AttributeModifier(field_14765, "Covered armor bonus", 20.0, 0).setSerialized(false);
	protected static final TrackedData<Direction> field_14761 = DataTracker.registerData(ShulkerEntity.class, TrackedDataHandlerRegistry.FACING);
	protected static final TrackedData<Optional<BlockPos>> field_14764 = DataTracker.registerData(
		ShulkerEntity.class, TrackedDataHandlerRegistry.OPTIONAL_BLOCK_POS
	);
	protected static final TrackedData<Byte> field_14769 = DataTracker.registerData(ShulkerEntity.class, TrackedDataHandlerRegistry.BYTE);
	protected static final TrackedData<Byte> field_15060 = DataTracker.registerData(ShulkerEntity.class, TrackedDataHandlerRegistry.BYTE);
	private float field_14767;
	private float field_14768;
	private BlockPos field_14762;
	private int field_14763;

	public ShulkerEntity(World world) {
		super(EntityType.SHULKER, world);
		this.setBounds(1.0F, 1.0F);
		this.prevBodyYaw = 180.0F;
		this.bodyYaw = 180.0F;
		this.isFireImmune = true;
		this.field_14762 = null;
		this.experiencePoints = 5;
	}

	@Nullable
	@Override
	public EntityData initialize(LocalDifficulty difficulty, @Nullable EntityData entityData, @Nullable NbtCompound nbt) {
		this.bodyYaw = 180.0F;
		this.prevBodyYaw = 180.0F;
		this.yaw = 180.0F;
		this.prevYaw = 180.0F;
		this.headYaw = 180.0F;
		this.prevHeadYaw = 180.0F;
		return super.initialize(difficulty, entityData, nbt);
	}

	@Override
	protected void initGoals() {
		this.goals.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goals.add(4, new ShulkerEntity.class_2997());
		this.goals.add(7, new ShulkerEntity.class_3001());
		this.goals.add(8, new LookAroundGoal(this));
		this.attackGoals.add(1, new RevengeGoal(this, true));
		this.attackGoals.add(2, new ShulkerEntity.class_3000(this));
		this.attackGoals.add(3, new ShulkerEntity.class_2999(this));
	}

	@Override
	protected boolean canClimb() {
		return false;
	}

	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.HOSTILE;
	}

	@Override
	protected Sound ambientSound() {
		return Sounds.ENTITY_SHULKER_AMBIENT;
	}

	@Override
	public void playAmbientSound() {
		if (!this.method_13232()) {
			super.playAmbientSound();
		}
	}

	@Override
	protected Sound deathSound() {
		return Sounds.ENTITY_SHULKER_DEATH;
	}

	@Override
	protected Sound getHurtSound(DamageSource damageSource) {
		return this.method_13232() ? Sounds.ENTITY_SHULKER_HURT_CLOSED : Sounds.ENTITY_SHULKER_HURT;
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_14761, Direction.DOWN);
		this.dataTracker.startTracking(field_14764, Optional.empty());
		this.dataTracker.startTracking(field_14769, (byte)0);
		this.dataTracker.startTracking(field_15060, (byte)16);
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(30.0);
	}

	@Override
	protected BodyControl method_13088() {
		return new ShulkerEntity.class_2998(this);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.dataTracker.set(field_14761, Direction.getById(nbt.getByte("AttachFace")));
		this.dataTracker.set(field_14769, nbt.getByte("Peek"));
		this.dataTracker.set(field_15060, nbt.getByte("Color"));
		if (nbt.contains("APX")) {
			int i = nbt.getInt("APX");
			int j = nbt.getInt("APY");
			int k = nbt.getInt("APZ");
			this.dataTracker.set(field_14764, Optional.of(new BlockPos(i, j, k)));
		} else {
			this.dataTracker.set(field_14764, Optional.empty());
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putByte("AttachFace", (byte)this.dataTracker.get(field_14761).getId());
		nbt.putByte("Peek", this.dataTracker.get(field_14769));
		nbt.putByte("Color", this.dataTracker.get(field_15060));
		BlockPos blockPos = this.method_13227();
		if (blockPos != null) {
			nbt.putInt("APX", blockPos.getX());
			nbt.putInt("APY", blockPos.getY());
			nbt.putInt("APZ", blockPos.getZ());
		}
	}

	@Override
	public void tick() {
		super.tick();
		BlockPos blockPos = (BlockPos)this.dataTracker.get(field_14764).orElse(null);
		if (blockPos == null && !this.world.isClient) {
			blockPos = new BlockPos(this);
			this.dataTracker.set(field_14764, Optional.of(blockPos));
		}

		if (this.hasMount()) {
			blockPos = null;
			float f = this.getVehicle().yaw;
			this.yaw = f;
			this.bodyYaw = f;
			this.prevBodyYaw = f;
			this.field_14763 = 0;
		} else if (!this.world.isClient) {
			BlockState blockState = this.world.getBlockState(blockPos);
			if (!blockState.isAir()) {
				if (blockState.getBlock() == Blocks.MOVING_PISTON) {
					Direction direction = blockState.getProperty(PistonBlock.FACING);
					if (this.world.method_8579(blockPos.offset(direction))) {
						blockPos = blockPos.offset(direction);
						this.dataTracker.set(field_14764, Optional.of(blockPos));
					} else {
						this.method_13235();
					}
				} else if (blockState.getBlock() == Blocks.PISTON_HEAD) {
					Direction direction2 = blockState.getProperty(PistonHeadBlock.FACING);
					if (this.world.method_8579(blockPos.offset(direction2))) {
						blockPos = blockPos.offset(direction2);
						this.dataTracker.set(field_14764, Optional.of(blockPos));
					} else {
						this.method_13235();
					}
				} else {
					this.method_13235();
				}
			}

			BlockPos blockPos2 = blockPos.offset(this.method_13226());
			if (!this.world.method_16339(blockPos2)) {
				boolean bl = false;

				for (Direction direction3 : Direction.values()) {
					blockPos2 = blockPos.offset(direction3);
					if (this.world.method_16339(blockPos2)) {
						this.dataTracker.set(field_14761, direction3);
						bl = true;
						break;
					}
				}

				if (!bl) {
					this.method_13235();
				}
			}

			BlockPos blockPos3 = blockPos.offset(this.method_13226().getOpposite());
			if (this.world.method_16339(blockPos3)) {
				this.method_13235();
			}
		}

		float g = (float)this.method_13228() * 0.01F;
		this.field_14767 = this.field_14768;
		if (this.field_14768 > g) {
			this.field_14768 = MathHelper.clamp(this.field_14768 - 0.05F, g, 1.0F);
		} else if (this.field_14768 < g) {
			this.field_14768 = MathHelper.clamp(this.field_14768 + 0.05F, 0.0F, g);
		}

		if (blockPos != null) {
			if (this.world.isClient) {
				if (this.field_14763 > 0 && this.field_14762 != null) {
					this.field_14763--;
				} else {
					this.field_14762 = blockPos;
				}
			}

			this.x = (double)blockPos.getX() + 0.5;
			this.y = (double)blockPos.getY();
			this.z = (double)blockPos.getZ() + 0.5;
			this.prevX = this.x;
			this.prevY = this.y;
			this.prevZ = this.z;
			this.prevTickX = this.x;
			this.prevTickY = this.y;
			this.prevTickZ = this.z;
			double d = 0.5 - (double)MathHelper.sin((0.5F + this.field_14768) * (float) Math.PI) * 0.5;
			double e = 0.5 - (double)MathHelper.sin((0.5F + this.field_14767) * (float) Math.PI) * 0.5;
			double h = d - e;
			double i = 0.0;
			double j = 0.0;
			double k = 0.0;
			Direction direction4 = this.method_13226();
			switch (direction4) {
				case DOWN:
					this.setBoundingBox(new Box(this.x - 0.5, this.y, this.z - 0.5, this.x + 0.5, this.y + 1.0 + d, this.z + 0.5));
					j = h;
					break;
				case UP:
					this.setBoundingBox(new Box(this.x - 0.5, this.y - d, this.z - 0.5, this.x + 0.5, this.y + 1.0, this.z + 0.5));
					j = -h;
					break;
				case NORTH:
					this.setBoundingBox(new Box(this.x - 0.5, this.y, this.z - 0.5, this.x + 0.5, this.y + 1.0, this.z + 0.5 + d));
					k = h;
					break;
				case SOUTH:
					this.setBoundingBox(new Box(this.x - 0.5, this.y, this.z - 0.5 - d, this.x + 0.5, this.y + 1.0, this.z + 0.5));
					k = -h;
					break;
				case WEST:
					this.setBoundingBox(new Box(this.x - 0.5, this.y, this.z - 0.5, this.x + 0.5 + d, this.y + 1.0, this.z + 0.5));
					i = h;
					break;
				case EAST:
					this.setBoundingBox(new Box(this.x - 0.5 - d, this.y, this.z - 0.5, this.x + 0.5, this.y + 1.0, this.z + 0.5));
					i = -h;
			}

			if (h > 0.0) {
				List<Entity> list = this.world.getEntities(this, this.getBoundingBox());
				if (!list.isEmpty()) {
					for (Entity entity : list) {
						if (!(entity instanceof ShulkerEntity) && !entity.noClip) {
							entity.move(MovementType.SHULKER, i, j, k);
						}
					}
				}
			}
		}
	}

	@Override
	public void move(MovementType type, double movementX, double movementY, double movementZ) {
		if (type == MovementType.SHULKER_BOX) {
			this.method_13235();
		} else {
			super.move(type, movementX, movementY, movementZ);
		}
	}

	@Override
	public void updatePosition(double x, double y, double z) {
		super.updatePosition(x, y, z);
		if (this.dataTracker != null && this.ticksAlive != 0) {
			Optional<BlockPos> optional = this.dataTracker.get(field_14764);
			Optional<BlockPos> optional2 = Optional.of(new BlockPos(x, y, z));
			if (!optional2.equals(optional)) {
				this.dataTracker.set(field_14764, optional2);
				this.dataTracker.set(field_14769, (byte)0);
				this.velocityDirty = true;
			}
		}
	}

	protected boolean method_13235() {
		if (!this.hasNoAi() && this.isAlive()) {
			BlockPos blockPos = new BlockPos(this);

			for (int i = 0; i < 5; i++) {
				BlockPos blockPos2 = blockPos.add(8 - this.random.nextInt(17), 8 - this.random.nextInt(17), 8 - this.random.nextInt(17));
				if (blockPos2.getY() > 0 && this.world.method_8579(blockPos2) && this.world.method_16392(this) && this.world.method_16387(this, new Box(blockPos2))) {
					boolean bl = false;

					for (Direction direction : Direction.values()) {
						if (this.world.method_16339(blockPos2.offset(direction))) {
							this.dataTracker.set(field_14761, direction);
							bl = true;
							break;
						}
					}

					if (bl) {
						this.playSound(Sounds.ENTITY_SHULKER_TELEPORT, 1.0F, 1.0F);
						this.dataTracker.set(field_14764, Optional.of(blockPos2));
						this.dataTracker.set(field_14769, (byte)0);
						this.setTarget(null);
						return true;
					}
				}
			}

			return false;
		} else {
			return true;
		}
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		this.velocityX = 0.0;
		this.velocityY = 0.0;
		this.velocityZ = 0.0;
		this.prevBodyYaw = 180.0F;
		this.bodyYaw = 180.0F;
		this.yaw = 180.0F;
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (field_14764.equals(data) && this.world.isClient && !this.hasMount()) {
			BlockPos blockPos = this.method_13227();
			if (blockPos != null) {
				if (this.field_14762 == null) {
					this.field_14762 = blockPos;
				} else {
					this.field_14763 = 6;
				}

				this.x = (double)blockPos.getX() + 0.5;
				this.y = (double)blockPos.getY();
				this.z = (double)blockPos.getZ() + 0.5;
				this.prevX = this.x;
				this.prevY = this.y;
				this.prevZ = this.z;
				this.prevTickX = this.x;
				this.prevTickY = this.y;
				this.prevTickZ = this.z;
			}
		}

		super.onTrackedDataSet(data);
	}

	@Override
	public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
		this.bodyTrackingIncrements = 0;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.method_13232()) {
			Entity entity = source.getSource();
			if (entity instanceof AbstractArrowEntity) {
				return false;
			}
		}

		if (super.damage(source, amount)) {
			if ((double)this.getHealth() < (double)this.getMaxHealth() * 0.5 && this.random.nextInt(4) == 0) {
				this.method_13235();
			}

			return true;
		} else {
			return false;
		}
	}

	private boolean method_13232() {
		return this.method_13228() == 0;
	}

	@Nullable
	@Override
	public Box getBox() {
		return this.isAlive() ? this.getBoundingBox() : null;
	}

	public Direction method_13226() {
		return this.dataTracker.get(field_14761);
	}

	@Nullable
	public BlockPos method_13227() {
		return (BlockPos)this.dataTracker.get(field_14764).orElse(null);
	}

	public void method_13234(@Nullable BlockPos blockPos) {
		this.dataTracker.set(field_14764, Optional.ofNullable(blockPos));
	}

	public int method_13228() {
		return this.dataTracker.get(field_14769);
	}

	public void method_13221(int i) {
		if (!this.world.isClient) {
			this.initializeAttribute(EntityAttributes.GENERIC_ARMOR).method_6193(field_14766);
			if (i == 0) {
				this.initializeAttribute(EntityAttributes.GENERIC_ARMOR).addModifier(field_14766);
				this.playSound(Sounds.ENTITY_SHULKER_CLOSE, 1.0F, 1.0F);
			} else {
				this.playSound(Sounds.ENTITY_SHULKER_OPEN, 1.0F, 1.0F);
			}
		}

		this.dataTracker.set(field_14769, (byte)i);
	}

	public float method_13220(float f) {
		return this.field_14767 + (this.field_14768 - this.field_14767) * f;
	}

	public int method_13229() {
		return this.field_14763;
	}

	public BlockPos method_13230() {
		return this.field_14762;
	}

	@Override
	public float getEyeHeight() {
		return 0.5F;
	}

	@Override
	public int getLookPitchSpeed() {
		return 180;
	}

	@Override
	public int method_13081() {
		return 180;
	}

	@Override
	public void pushAwayFrom(Entity entity) {
	}

	@Override
	public float getTargetingMargin() {
		return 0.0F;
	}

	public boolean method_13231() {
		return this.field_14762 != null && this.method_13227() != null;
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.SHULKER_ENTITIE;
	}

	public DyeColor method_13573() {
		Byte byte_ = this.dataTracker.get(field_15060);
		return byte_ != 16 && byte_ <= 15 ? DyeColor.byId(byte_) : null;
	}

	class class_2997 extends Goal {
		private int field_14772;

		public class_2997() {
			this.setCategoryBits(3);
		}

		@Override
		public boolean canStart() {
			LivingEntity livingEntity = ShulkerEntity.this.getTarget();
			return livingEntity != null && livingEntity.isAlive() ? ShulkerEntity.this.world.method_16346() != Difficulty.PEACEFUL : false;
		}

		@Override
		public void start() {
			this.field_14772 = 20;
			ShulkerEntity.this.method_13221(100);
		}

		@Override
		public void stop() {
			ShulkerEntity.this.method_13221(0);
		}

		@Override
		public void tick() {
			if (ShulkerEntity.this.world.method_16346() != Difficulty.PEACEFUL) {
				this.field_14772--;
				LivingEntity livingEntity = ShulkerEntity.this.getTarget();
				ShulkerEntity.this.getLookControl().lookAt(livingEntity, 180.0F, 180.0F);
				double d = ShulkerEntity.this.squaredDistanceTo(livingEntity);
				if (d < 400.0) {
					if (this.field_14772 <= 0) {
						this.field_14772 = 20 + ShulkerEntity.this.random.nextInt(10) * 20 / 2;
						ShulkerBulletEntity shulkerBulletEntity = new ShulkerBulletEntity(
							ShulkerEntity.this.world, ShulkerEntity.this, livingEntity, ShulkerEntity.this.method_13226().getAxis()
						);
						ShulkerEntity.this.world.method_3686(shulkerBulletEntity);
						ShulkerEntity.this.playSound(
							Sounds.ENTITY_SHULKER_SHOOT, 2.0F, (ShulkerEntity.this.random.nextFloat() - ShulkerEntity.this.random.nextFloat()) * 0.2F + 1.0F
						);
					}
				} else {
					ShulkerEntity.this.setTarget(null);
				}

				super.tick();
			}
		}
	}

	class class_2998 extends BodyControl {
		public class_2998(LivingEntity livingEntity) {
			super(livingEntity);
		}

		@Override
		public void tick() {
		}
	}

	static class class_2999 extends FollowTargetGoal<LivingEntity> {
		public class_2999(ShulkerEntity shulkerEntity) {
			super(shulkerEntity, LivingEntity.class, 10, true, false, livingEntity -> livingEntity instanceof Monster);
		}

		@Override
		public boolean canStart() {
			return this.mob.getScoreboardTeam() == null ? false : super.canStart();
		}

		@Override
		protected Box method_13104(double d) {
			Direction direction = ((ShulkerEntity)this.mob).method_13226();
			if (direction.getAxis() == Direction.Axis.X) {
				return this.mob.getBoundingBox().expand(4.0, d, d);
			} else {
				return direction.getAxis() == Direction.Axis.Z ? this.mob.getBoundingBox().expand(d, d, 4.0) : this.mob.getBoundingBox().expand(d, 4.0, d);
			}
		}
	}

	class class_3000 extends FollowTargetGoal<PlayerEntity> {
		public class_3000(ShulkerEntity shulkerEntity2) {
			super(shulkerEntity2, PlayerEntity.class, true);
		}

		@Override
		public boolean canStart() {
			return ShulkerEntity.this.world.method_16346() == Difficulty.PEACEFUL ? false : super.canStart();
		}

		@Override
		protected Box method_13104(double d) {
			Direction direction = ((ShulkerEntity)this.mob).method_13226();
			if (direction.getAxis() == Direction.Axis.X) {
				return this.mob.getBoundingBox().expand(4.0, d, d);
			} else {
				return direction.getAxis() == Direction.Axis.Z ? this.mob.getBoundingBox().expand(d, d, 4.0) : this.mob.getBoundingBox().expand(d, 4.0, d);
			}
		}
	}

	class class_3001 extends Goal {
		private int field_14776;

		private class_3001() {
		}

		@Override
		public boolean canStart() {
			return ShulkerEntity.this.getTarget() == null && ShulkerEntity.this.random.nextInt(40) == 0;
		}

		@Override
		public boolean shouldContinue() {
			return ShulkerEntity.this.getTarget() == null && this.field_14776 > 0;
		}

		@Override
		public void start() {
			this.field_14776 = 20 * (1 + ShulkerEntity.this.random.nextInt(3));
			ShulkerEntity.this.method_13221(30);
		}

		@Override
		public void stop() {
			if (ShulkerEntity.this.getTarget() == null) {
				ShulkerEntity.this.method_13221(0);
			}
		}

		@Override
		public void tick() {
			this.field_14776--;
		}
	}
}
