package net.minecraft.entity.mob;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.class_3462;
import net.minecraft.class_3804;
import net.minecraft.class_4342;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class PhantomEntity extends FlyingEntity implements Monster {
	private static final TrackedData<Integer> field_17048 = DataTracker.registerData(PhantomEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private Vec3d field_17050 = Vec3d.ZERO;
	private BlockPos field_17051 = BlockPos.ORIGIN;
	private PhantomEntity.class_3518 field_17049 = PhantomEntity.class_3518.CIRCLE;

	public PhantomEntity(World world) {
		super(EntityType.PHANTOM, world);
		this.experiencePoints = 5;
		this.setBounds(0.9F, 0.5F);
		this.entityMotionHelper = new PhantomEntity.class_3524(this);
		this.lookControl = new PhantomEntity.class_3523(this);
	}

	@Override
	protected BodyControl method_13088() {
		return new PhantomEntity.class_3521(this);
	}

	@Override
	protected void initGoals() {
		this.goals.add(1, new PhantomEntity.class_3520());
		this.goals.add(2, new PhantomEntity.class_3526());
		this.goals.add(3, new PhantomEntity.class_3522());
		this.attackGoals.add(1, new PhantomEntity.class_3519());
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.getAttributeContainer().register(EntityAttributes.GENERIC_ATTACK_DAMAGE);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_17048, 0);
	}

	public void method_15868(int i) {
		if (i < 0) {
			i = 0;
		} else if (i > 64) {
			i = 64;
		}

		this.dataTracker.set(field_17048, i);
		this.method_15884();
	}

	public void method_15884() {
		int i = this.dataTracker.get(field_17048);
		this.setBounds(0.9F + 0.2F * (float)i, 0.5F + 0.1F * (float)i);
		this.initializeAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue((double)(6 + i));
	}

	public int method_15876() {
		return this.dataTracker.get(field_17048);
	}

	@Override
	public float getEyeHeight() {
		return this.height * 0.35F;
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (field_17048.equals(data)) {
			this.method_15884();
		}

		super.onTrackedDataSet(data);
	}

	@Override
	public void tick() {
		super.tick();
		if (this.world.isClient) {
			float f = MathHelper.cos((float)(this.getEntityId() * 3 + this.ticksAlive) * 0.13F + (float) Math.PI);
			float g = MathHelper.cos((float)(this.getEntityId() * 3 + this.ticksAlive + 1) * 0.13F + (float) Math.PI);
			if (f > 0.0F && g <= 0.0F) {
				this.world
					.playSound(
						this.x,
						this.y,
						this.z,
						Sounds.ENTITY_PHANTOM_FLAP,
						this.getSoundCategory(),
						0.95F + this.random.nextFloat() * 0.05F,
						0.95F + this.random.nextFloat() * 0.05F,
						false
					);
			}

			int i = this.method_15876();
			float h = MathHelper.cos(this.yaw * (float) (Math.PI / 180.0)) * (1.3F + 0.21F * (float)i);
			float j = MathHelper.sin(this.yaw * (float) (Math.PI / 180.0)) * (1.3F + 0.21F * (float)i);
			float k = (0.3F + f * 0.45F) * ((float)i * 0.2F + 1.0F);
			this.world.method_16343(class_4342.field_21358, this.x + (double)h, this.y + (double)k, this.z + (double)j, 0.0, 0.0, 0.0);
			this.world.method_16343(class_4342.field_21358, this.x - (double)h, this.y + (double)k, this.z - (double)j, 0.0, 0.0, 0.0);
		}

		if (!this.world.isClient && this.world.method_16346() == Difficulty.PEACEFUL) {
			this.remove();
		}
	}

	@Override
	public void tickMovement() {
		if (this.method_15656()) {
			this.setOnFireFor(8);
		}

		super.tickMovement();
	}

	@Override
	protected void mobTick() {
		super.mobTick();
	}

	@Override
	public EntityData initialize(LocalDifficulty difficulty, @Nullable EntityData entityData, @Nullable NbtCompound nbt) {
		this.field_17051 = new BlockPos(this).up(5);
		this.method_15868(0);
		return super.initialize(difficulty, entityData, nbt);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("AX")) {
			this.field_17051 = new BlockPos(nbt.getInt("AX"), nbt.getInt("AY"), nbt.getInt("AZ"));
		}

		this.method_15868(nbt.getInt("Size"));
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("AX", this.field_17051.getX());
		nbt.putInt("AY", this.field_17051.getY());
		nbt.putInt("AZ", this.field_17051.getZ());
		nbt.putInt("Size", this.method_15876());
	}

	@Override
	public boolean shouldRender(double distance) {
		return true;
	}

	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.HOSTILE;
	}

	@Override
	protected Sound ambientSound() {
		return Sounds.ENTITY_PHANTOM_AMBIENT;
	}

	@Override
	protected Sound getHurtSound(DamageSource damageSource) {
		return Sounds.ENTITY_PHANTOM_HURT;
	}

	@Override
	protected Sound deathSound() {
		return Sounds.ENTITY_PHANTOM_DEATH;
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.PHANTOM_ENTITIE;
	}

	@Override
	public class_3462 method_2647() {
		return class_3462.field_16819;
	}

	@Override
	protected float getSoundVolume() {
		return 1.0F;
	}

	@Override
	public boolean canAttackEntity(Class<? extends LivingEntity> clazz) {
		return true;
	}

	static enum class_3518 {
		CIRCLE,
		SWOOP;
	}

	class class_3519 extends Goal {
		private int field_17056 = 20;

		private class_3519() {
		}

		@Override
		public boolean canStart() {
			if (this.field_17056 > 0) {
				this.field_17056--;
				return false;
			} else {
				this.field_17056 = 60;
				Box box = PhantomEntity.this.getBoundingBox().expand(16.0, 64.0, 16.0);
				List<PlayerEntity> list = PhantomEntity.this.world.getEntitiesInBox(PlayerEntity.class, box);
				if (!list.isEmpty()) {
					list.sort((playerEntityx, playerEntity2) -> playerEntityx.y > playerEntity2.y ? -1 : 1);

					for (PlayerEntity playerEntity : list) {
						if (TrackTargetGoal.method_11025(PhantomEntity.this, playerEntity, false, false)) {
							PhantomEntity.this.setTarget(playerEntity);
							return true;
						}
					}
				}

				return false;
			}
		}

		@Override
		public boolean shouldContinue() {
			return TrackTargetGoal.method_11025(PhantomEntity.this, PhantomEntity.this.getTarget(), false, false);
		}
	}

	class class_3520 extends Goal {
		private int field_17058;

		private class_3520() {
		}

		@Override
		public boolean canStart() {
			return TrackTargetGoal.method_11025(PhantomEntity.this, PhantomEntity.this.getTarget(), false, false);
		}

		@Override
		public void start() {
			this.field_17058 = 10;
			PhantomEntity.this.field_17049 = PhantomEntity.class_3518.CIRCLE;
			this.method_15890();
		}

		@Override
		public void stop() {
			PhantomEntity.this.field_17051 = PhantomEntity.this.world
				.method_16373(class_3804.class_3805.MOTION_BLOCKING, PhantomEntity.this.field_17051)
				.up(10 + PhantomEntity.this.random.nextInt(20));
		}

		@Override
		public void tick() {
			if (PhantomEntity.this.field_17049 == PhantomEntity.class_3518.CIRCLE) {
				this.field_17058--;
				if (this.field_17058 <= 0) {
					PhantomEntity.this.field_17049 = PhantomEntity.class_3518.SWOOP;
					this.method_15890();
					this.field_17058 = (8 + PhantomEntity.this.random.nextInt(4)) * 20;
					PhantomEntity.this.playSound(Sounds.ENTITY_PHANTOM_SWOOP, 10.0F, 0.95F + PhantomEntity.this.random.nextFloat() * 0.1F);
				}
			}
		}

		private void method_15890() {
			PhantomEntity.this.field_17051 = new BlockPos(PhantomEntity.this.getTarget()).up(20 + PhantomEntity.this.random.nextInt(20));
			if (PhantomEntity.this.field_17051.getY() < PhantomEntity.this.world.method_8483()) {
				PhantomEntity.this.field_17051 = new BlockPos(
					PhantomEntity.this.field_17051.getX(), PhantomEntity.this.world.method_8483() + 1, PhantomEntity.this.field_17051.getZ()
				);
			}
		}
	}

	class class_3521 extends BodyControl {
		public class_3521(LivingEntity livingEntity) {
			super(livingEntity);
		}

		@Override
		public void tick() {
			PhantomEntity.this.headYaw = PhantomEntity.this.bodyYaw;
			PhantomEntity.this.bodyYaw = PhantomEntity.this.yaw;
		}
	}

	class class_3522 extends PhantomEntity.class_3525 {
		private float field_17061;
		private float field_17062;
		private float field_17063;
		private float field_17064;

		private class_3522() {
		}

		@Override
		public boolean canStart() {
			return PhantomEntity.this.getTarget() == null || PhantomEntity.this.field_17049 == PhantomEntity.class_3518.CIRCLE;
		}

		@Override
		public void start() {
			this.field_17062 = 5.0F + PhantomEntity.this.random.nextFloat() * 10.0F;
			this.field_17063 = -4.0F + PhantomEntity.this.random.nextFloat() * 9.0F;
			this.field_17064 = PhantomEntity.this.random.nextBoolean() ? 1.0F : -1.0F;
			this.method_15891();
		}

		@Override
		public void tick() {
			if (PhantomEntity.this.random.nextInt(350) == 0) {
				this.field_17063 = -4.0F + PhantomEntity.this.random.nextFloat() * 9.0F;
			}

			if (PhantomEntity.this.random.nextInt(250) == 0) {
				this.field_17062++;
				if (this.field_17062 > 15.0F) {
					this.field_17062 = 5.0F;
					this.field_17064 = -this.field_17064;
				}
			}

			if (PhantomEntity.this.random.nextInt(450) == 0) {
				this.field_17061 = PhantomEntity.this.random.nextFloat() * 2.0F * (float) Math.PI;
				this.method_15891();
			}

			if (this.method_15892()) {
				this.method_15891();
			}

			if (PhantomEntity.this.field_17050.y < PhantomEntity.this.y && !PhantomEntity.this.world.method_8579(new BlockPos(PhantomEntity.this).down(1))) {
				this.field_17063 = Math.max(1.0F, this.field_17063);
				this.method_15891();
			}

			if (PhantomEntity.this.field_17050.y > PhantomEntity.this.y && !PhantomEntity.this.world.method_8579(new BlockPos(PhantomEntity.this).up(1))) {
				this.field_17063 = Math.min(-1.0F, this.field_17063);
				this.method_15891();
			}
		}

		private void method_15891() {
			if (BlockPos.ORIGIN.equals(PhantomEntity.this.field_17051)) {
				PhantomEntity.this.field_17051 = new BlockPos(PhantomEntity.this);
			}

			this.field_17061 = this.field_17061 + this.field_17064 * 15.0F * (float) (Math.PI / 180.0);
			PhantomEntity.this.field_17050 = new Vec3d(PhantomEntity.this.field_17051)
				.add(
					(double)(this.field_17062 * MathHelper.cos(this.field_17061)),
					(double)(-4.0F + this.field_17063),
					(double)(this.field_17062 * MathHelper.sin(this.field_17061))
				);
		}
	}

	class class_3523 extends LookControl {
		public class_3523(MobEntity mobEntity) {
			super(mobEntity);
		}

		@Override
		public void tick() {
		}
	}

	class class_3524 extends MoveControl {
		private float field_17067 = 0.1F;

		public class_3524(MobEntity mobEntity) {
			super(mobEntity);
		}

		@Override
		public void updateMovement() {
			if (PhantomEntity.this.horizontalCollision) {
				PhantomEntity.this.yaw += 180.0F;
				this.field_17067 = 0.1F;
			}

			float f = (float)(PhantomEntity.this.field_17050.x - PhantomEntity.this.x);
			float g = (float)(PhantomEntity.this.field_17050.y - PhantomEntity.this.y);
			float h = (float)(PhantomEntity.this.field_17050.z - PhantomEntity.this.z);
			double d = (double)MathHelper.sqrt(f * f + h * h);
			double e = 1.0 - (double)MathHelper.abs(g * 0.7F) / d;
			f = (float)((double)f * e);
			h = (float)((double)h * e);
			d = (double)MathHelper.sqrt(f * f + h * h);
			double i = (double)MathHelper.sqrt(f * f + h * h + g * g);
			float j = PhantomEntity.this.yaw;
			float k = (float)MathHelper.atan2((double)h, (double)f);
			float l = MathHelper.wrapDegrees(PhantomEntity.this.yaw + 90.0F);
			float m = MathHelper.wrapDegrees(k * (180.0F / (float)Math.PI));
			PhantomEntity.this.yaw = MathHelper.method_21517(l, m, 4.0F) - 90.0F;
			PhantomEntity.this.bodyYaw = PhantomEntity.this.yaw;
			if (MathHelper.method_21518(j, PhantomEntity.this.yaw) < 3.0F) {
				this.field_17067 = MathHelper.method_21515(this.field_17067, 1.8F, 0.005F * (1.8F / this.field_17067));
			} else {
				this.field_17067 = MathHelper.method_21515(this.field_17067, 0.2F, 0.025F);
			}

			float n = (float)(-(MathHelper.atan2((double)(-g), d) * 180.0F / (float)Math.PI));
			PhantomEntity.this.pitch = n;
			float o = PhantomEntity.this.yaw + 90.0F;
			double p = (double)(this.field_17067 * MathHelper.cos(o * (float) (Math.PI / 180.0))) * Math.abs((double)f / i);
			double q = (double)(this.field_17067 * MathHelper.sin(o * (float) (Math.PI / 180.0))) * Math.abs((double)h / i);
			double r = (double)(this.field_17067 * MathHelper.sin(n * (float) (Math.PI / 180.0))) * Math.abs((double)g / i);
			PhantomEntity.this.velocityX = PhantomEntity.this.velocityX + (p - PhantomEntity.this.velocityX) * 0.2;
			PhantomEntity.this.velocityY = PhantomEntity.this.velocityY + (r - PhantomEntity.this.velocityY) * 0.2;
			PhantomEntity.this.velocityZ = PhantomEntity.this.velocityZ + (q - PhantomEntity.this.velocityZ) * 0.2;
		}
	}

	abstract class class_3525 extends Goal {
		public class_3525() {
			this.setCategoryBits(1);
		}

		protected boolean method_15892() {
			return PhantomEntity.this.field_17050.method_12126(PhantomEntity.this.x, PhantomEntity.this.y, PhantomEntity.this.z) < 4.0;
		}
	}

	class class_3526 extends PhantomEntity.class_3525 {
		private class_3526() {
		}

		@Override
		public boolean canStart() {
			return PhantomEntity.this.getTarget() != null && PhantomEntity.this.field_17049 == PhantomEntity.class_3518.SWOOP;
		}

		@Override
		public boolean shouldContinue() {
			LivingEntity livingEntity = PhantomEntity.this.getTarget();
			if (livingEntity == null) {
				return false;
			} else if (!livingEntity.isAlive()) {
				return false;
			} else {
				return !(livingEntity instanceof PlayerEntity) || !((PlayerEntity)livingEntity).isSpectator() && !((PlayerEntity)livingEntity).isCreative()
					? this.canStart()
					: false;
			}
		}

		@Override
		public void start() {
		}

		@Override
		public void stop() {
			PhantomEntity.this.setTarget(null);
			PhantomEntity.this.field_17049 = PhantomEntity.class_3518.CIRCLE;
		}

		@Override
		public void tick() {
			LivingEntity livingEntity = PhantomEntity.this.getTarget();
			PhantomEntity.this.field_17050 = new Vec3d(livingEntity.x, livingEntity.y + (double)livingEntity.height * 0.5, livingEntity.z);
			if (PhantomEntity.this.getBoundingBox().expand(0.2F).intersects(livingEntity.getBoundingBox())) {
				PhantomEntity.this.tryAttack(livingEntity);
				PhantomEntity.this.field_17049 = PhantomEntity.class_3518.CIRCLE;
				PhantomEntity.this.world.syncGlobalEvent(1039, new BlockPos(PhantomEntity.this), 0);
			} else if (PhantomEntity.this.horizontalCollision || PhantomEntity.this.hurtTime > 0) {
				PhantomEntity.this.field_17049 = PhantomEntity.class_3518.CIRCLE;
			}
		}
	}
}
