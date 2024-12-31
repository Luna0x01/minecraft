package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class VexEntity extends HostileEntity {
	protected static final TrackedData<Byte> field_15062 = DataTracker.registerData(VexEntity.class, TrackedDataHandlerRegistry.BYTE);
	private MobEntity field_15063;
	@Nullable
	private BlockPos field_15066;
	private boolean field_15064;
	private int field_15065;

	public VexEntity(World world) {
		super(world);
		this.isFireImmune = true;
		this.entityMotionHelper = new VexEntity.class_3045(this);
		this.setBounds(0.4F, 0.8F);
		this.experiencePoints = 3;
	}

	@Override
	public void move(MovementType type, double movementX, double movementY, double movementZ) {
		super.move(type, movementX, movementY, movementZ);
		this.checkBlockCollision();
	}

	@Override
	public void tick() {
		this.noClip = true;
		super.tick();
		this.noClip = false;
		this.setNoGravity(true);
		if (this.field_15064 && --this.field_15065 <= 0) {
			this.field_15065 = 20;
			this.damage(DamageSource.STARVE, 1.0F);
		}
	}

	@Override
	protected void initGoals() {
		super.initGoals();
		this.goals.add(0, new SwimGoal(this));
		this.goals.add(4, new VexEntity.class_3043());
		this.goals.add(8, new VexEntity.class_3046());
		this.goals.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 3.0F, 1.0F));
		this.goals.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0F));
		this.attackGoals.add(1, new RevengeGoal(this, true, VexEntity.class));
		this.attackGoals.add(2, new VexEntity.class_3044(this));
		this.attackGoals.add(3, new FollowTargetGoal(this, PlayerEntity.class, true));
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(14.0);
		this.initializeAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(4.0);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_15062, (byte)0);
	}

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		MobEntity.registerDataFixes(dataFixer, VexEntity.class);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("BoundX")) {
			this.field_15066 = new BlockPos(nbt.getInt("BoundX"), nbt.getInt("BoundY"), nbt.getInt("BoundZ"));
		}

		if (nbt.contains("LifeTicks")) {
			this.method_13575(nbt.getInt("LifeTicks"));
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		if (this.field_15066 != null) {
			nbt.putInt("BoundX", this.field_15066.getX());
			nbt.putInt("BoundY", this.field_15066.getY());
			nbt.putInt("BoundZ", this.field_15066.getZ());
		}

		if (this.field_15064) {
			nbt.putInt("LifeTicks", this.field_15065);
		}
	}

	public MobEntity method_13593() {
		return this.field_15063;
	}

	@Nullable
	public BlockPos method_13585() {
		return this.field_15066;
	}

	public void method_13590(@Nullable BlockPos blockPos) {
		this.field_15066 = blockPos;
	}

	private boolean method_13581(int i) {
		int j = this.dataTracker.get(field_15062);
		return (j & i) != 0;
	}

	private void method_13576(int i, boolean bl) {
		int j = this.dataTracker.get(field_15062);
		if (bl) {
			j |= i;
		} else {
			j &= ~i;
		}

		this.dataTracker.set(field_15062, (byte)(j & 0xFF));
	}

	public boolean isCharging() {
		return this.method_13581(1);
	}

	public void setCharging(boolean bl) {
		this.method_13576(1, bl);
	}

	public void method_13579(MobEntity mobEntity) {
		this.field_15063 = mobEntity;
	}

	public void method_13575(int i) {
		this.field_15064 = true;
		this.field_15065 = i;
	}

	@Override
	protected Sound ambientSound() {
		return Sounds.ENTITY_VEX_AMBIENT;
	}

	@Override
	protected Sound deathSound() {
		return Sounds.ENTITY_VEX_DEATH;
	}

	@Override
	protected Sound method_13048() {
		return Sounds.ENTITY_VEX_HURT;
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.VEX_ENTITIE;
	}

	@Override
	public int getLightmapCoordinates(float f) {
		return 15728880;
	}

	@Override
	public float getBrightnessAtEyes(float f) {
		return 1.0F;
	}

	@Nullable
	@Override
	public EntityData initialize(LocalDifficulty difficulty, @Nullable EntityData data) {
		this.initEquipment(difficulty);
		this.updateEnchantments(difficulty);
		return super.initialize(difficulty, data);
	}

	@Override
	protected void initEquipment(LocalDifficulty difficulty) {
		this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
		this.method_13077(EquipmentSlot.MAINHAND, 0.0F);
	}

	class class_3043 extends Goal {
		public class_3043() {
			this.setCategoryBits(1);
		}

		@Override
		public boolean canStart() {
			return VexEntity.this.getTarget() != null && !VexEntity.this.getMotionHelper().isMoving() && VexEntity.this.random.nextInt(7) == 0
				? VexEntity.this.squaredDistanceTo(VexEntity.this.getTarget()) > 4.0
				: false;
		}

		@Override
		public boolean shouldContinue() {
			return VexEntity.this.getMotionHelper().isMoving()
				&& VexEntity.this.isCharging()
				&& VexEntity.this.getTarget() != null
				&& VexEntity.this.getTarget().isAlive();
		}

		@Override
		public void start() {
			LivingEntity livingEntity = VexEntity.this.getTarget();
			Vec3d vec3d = livingEntity.getCameraPosVec(1.0F);
			VexEntity.this.entityMotionHelper.moveTo(vec3d.x, vec3d.y, vec3d.z, 1.0);
			VexEntity.this.setCharging(true);
			VexEntity.this.playSound(Sounds.ENTITY_VEX_CHARGE, 1.0F, 1.0F);
		}

		@Override
		public void stop() {
			VexEntity.this.setCharging(false);
		}

		@Override
		public void tick() {
			LivingEntity livingEntity = VexEntity.this.getTarget();
			if (VexEntity.this.getBoundingBox().intersects(livingEntity.getBoundingBox())) {
				VexEntity.this.tryAttack(livingEntity);
				VexEntity.this.setCharging(false);
			} else {
				double d = VexEntity.this.squaredDistanceTo(livingEntity);
				if (d < 9.0) {
					Vec3d vec3d = livingEntity.getCameraPosVec(1.0F);
					VexEntity.this.entityMotionHelper.moveTo(vec3d.x, vec3d.y, vec3d.z, 1.0);
				}
			}
		}
	}

	class class_3044 extends TrackTargetGoal {
		public class_3044(PathAwareEntity pathAwareEntity) {
			super(pathAwareEntity, false);
		}

		@Override
		public boolean canStart() {
			return VexEntity.this.field_15063 != null && VexEntity.this.field_15063.getTarget() != null && this.canTrack(VexEntity.this.field_15063.getTarget(), false);
		}

		@Override
		public void start() {
			VexEntity.this.setTarget(VexEntity.this.field_15063.getTarget());
			super.start();
		}
	}

	class class_3045 extends MoveControl {
		public class_3045(VexEntity vexEntity2) {
			super(vexEntity2);
		}

		@Override
		public void updateMovement() {
			if (this.state == MoveControl.MoveStatus.MOVE_TO) {
				double d = this.targetX - VexEntity.this.x;
				double e = this.targetY - VexEntity.this.y;
				double f = this.targetZ - VexEntity.this.z;
				double g = d * d + e * e + f * f;
				g = (double)MathHelper.sqrt(g);
				if (g < VexEntity.this.getBoundingBox().getAverage()) {
					this.state = MoveControl.MoveStatus.WAIT;
					VexEntity.this.velocityX *= 0.5;
					VexEntity.this.velocityY *= 0.5;
					VexEntity.this.velocityZ *= 0.5;
				} else {
					VexEntity.this.velocityX = VexEntity.this.velocityX + d / g * 0.05 * this.speed;
					VexEntity.this.velocityY = VexEntity.this.velocityY + e / g * 0.05 * this.speed;
					VexEntity.this.velocityZ = VexEntity.this.velocityZ + f / g * 0.05 * this.speed;
					if (VexEntity.this.getTarget() == null) {
						VexEntity.this.yaw = -((float)MathHelper.atan2(VexEntity.this.velocityX, VexEntity.this.velocityZ)) * (180.0F / (float)Math.PI);
						VexEntity.this.bodyYaw = VexEntity.this.yaw;
					} else {
						double h = VexEntity.this.getTarget().x - VexEntity.this.x;
						double i = VexEntity.this.getTarget().z - VexEntity.this.z;
						VexEntity.this.yaw = -((float)MathHelper.atan2(h, i)) * (180.0F / (float)Math.PI);
						VexEntity.this.bodyYaw = VexEntity.this.yaw;
					}
				}
			}
		}
	}

	class class_3046 extends Goal {
		public class_3046() {
			this.setCategoryBits(1);
		}

		@Override
		public boolean canStart() {
			return !VexEntity.this.getMotionHelper().isMoving() && VexEntity.this.random.nextInt(7) == 0;
		}

		@Override
		public boolean shouldContinue() {
			return false;
		}

		@Override
		public void tick() {
			BlockPos blockPos = VexEntity.this.method_13585();
			if (blockPos == null) {
				blockPos = new BlockPos(VexEntity.this);
			}

			for (int i = 0; i < 3; i++) {
				BlockPos blockPos2 = blockPos.add(VexEntity.this.random.nextInt(15) - 7, VexEntity.this.random.nextInt(11) - 5, VexEntity.this.random.nextInt(15) - 7);
				if (VexEntity.this.world.isAir(blockPos2)) {
					VexEntity.this.entityMotionHelper.moveTo((double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.5, (double)blockPos2.getZ() + 0.5, 0.25);
					if (VexEntity.this.getTarget() == null) {
						VexEntity.this.getLookControl().lookAt((double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.5, (double)blockPos2.getZ() + 0.5, 180.0F, 20.0F);
					}
					break;
				}
			}
		}
	}
}
