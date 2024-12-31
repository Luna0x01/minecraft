package net.minecraft.entity.mob;

import javax.annotation.Nullable;
import net.minecraft.class_3133;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.GoToWalkTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.pathing.LandType;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class BlazeEntity extends HostileEntity {
	private float eyeOffset = 0.5F;
	private int eyeOffsetCooldown;
	private static final TrackedData<Byte> field_14744 = DataTracker.registerData(BlazeEntity.class, TrackedDataHandlerRegistry.BYTE);

	public BlazeEntity(World world) {
		super(world);
		this.method_13076(LandType.WATER, -1.0F);
		this.method_13076(LandType.LAVA, 8.0F);
		this.method_13076(LandType.DANGER_FIRE, 0.0F);
		this.method_13076(LandType.DAMAGE_FIRE, 0.0F);
		this.isFireImmune = true;
		this.experiencePoints = 10;
	}

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		MobEntity.registerDataFixes(dataFixer, BlazeEntity.class);
	}

	@Override
	protected void initGoals() {
		this.goals.add(4, new BlazeEntity.ShootFireballGoal(this));
		this.goals.add(5, new GoToWalkTargetGoal(this, 1.0));
		this.goals.add(7, new class_3133(this, 1.0, 0.0F));
		this.goals.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goals.add(8, new LookAroundGoal(this));
		this.attackGoals.add(1, new RevengeGoal(this, true));
		this.attackGoals.add(2, new FollowTargetGoal(this, PlayerEntity.class, true));
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(6.0);
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.23F);
		this.initializeAttribute(EntityAttributes.GENERIC_FOLLOW_RANGE).setBaseValue(48.0);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_14744, (byte)0);
	}

	@Override
	protected Sound ambientSound() {
		return Sounds.ENTITY_BLAZE_AMBIENT;
	}

	@Override
	protected Sound method_13048() {
		return Sounds.ENTITY_BLAZE_HURT;
	}

	@Override
	protected Sound deathSound() {
		return Sounds.ENTITY_BLAZE_DEATH;
	}

	@Override
	public int getLightmapCoordinates(float f) {
		return 15728880;
	}

	@Override
	public float getBrightnessAtEyes(float f) {
		return 1.0F;
	}

	@Override
	public void tickMovement() {
		if (!this.onGround && this.velocityY < 0.0) {
			this.velocityY *= 0.6;
		}

		if (this.world.isClient) {
			if (this.random.nextInt(24) == 0 && !this.isSilent()) {
				this.world
					.playSound(
						this.x + 0.5,
						this.y + 0.5,
						this.z + 0.5,
						Sounds.ENTITY_BLAZE_BURN,
						this.getSoundCategory(),
						1.0F + this.random.nextFloat(),
						this.random.nextFloat() * 0.7F + 0.3F,
						false
					);
			}

			for (int i = 0; i < 2; i++) {
				this.world
					.addParticle(
						ParticleType.SMOKE_LARGE,
						this.x + (this.random.nextDouble() - 0.5) * (double)this.width,
						this.y + this.random.nextDouble() * (double)this.height,
						this.z + (this.random.nextDouble() - 0.5) * (double)this.width,
						0.0,
						0.0,
						0.0
					);
			}
		}

		super.tickMovement();
	}

	@Override
	protected void mobTick() {
		if (this.tickFire()) {
			this.damage(DamageSource.DROWN, 1.0F);
		}

		this.eyeOffsetCooldown--;
		if (this.eyeOffsetCooldown <= 0) {
			this.eyeOffsetCooldown = 100;
			this.eyeOffset = 0.5F + (float)this.random.nextGaussian() * 3.0F;
		}

		LivingEntity livingEntity = this.getTarget();
		if (livingEntity != null && livingEntity.y + (double)livingEntity.getEyeHeight() > this.y + (double)this.getEyeHeight() + (double)this.eyeOffset) {
			this.velocityY = this.velocityY + (0.3F - this.velocityY) * 0.3F;
			this.velocityDirty = true;
		}

		super.mobTick();
	}

	@Override
	public void handleFallDamage(float fallDistance, float damageMultiplier) {
	}

	@Override
	public boolean isOnFire() {
		return this.isFireActive();
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.BLAZE_ENTITIE;
	}

	public boolean isFireActive() {
		return (this.dataTracker.get(field_14744) & 1) != 0;
	}

	public void setFireActive(boolean value) {
		byte b = this.dataTracker.get(field_14744);
		if (value) {
			b = (byte)(b | 1);
		} else {
			b = (byte)(b & -2);
		}

		this.dataTracker.set(field_14744, b);
	}

	@Override
	protected boolean method_3087() {
		return true;
	}

	static class ShootFireballGoal extends Goal {
		private final BlazeEntity blaze;
		private int fireballsFired;
		private int cooldown;

		public ShootFireballGoal(BlazeEntity blazeEntity) {
			this.blaze = blazeEntity;
			this.setCategoryBits(3);
		}

		@Override
		public boolean canStart() {
			LivingEntity livingEntity = this.blaze.getTarget();
			return livingEntity != null && livingEntity.isAlive();
		}

		@Override
		public void start() {
			this.fireballsFired = 0;
		}

		@Override
		public void stop() {
			this.blaze.setFireActive(false);
		}

		@Override
		public void tick() {
			this.cooldown--;
			LivingEntity livingEntity = this.blaze.getTarget();
			double d = this.blaze.squaredDistanceTo(livingEntity);
			if (d < 4.0) {
				if (this.cooldown <= 0) {
					this.cooldown = 20;
					this.blaze.tryAttack(livingEntity);
				}

				this.blaze.getMotionHelper().moveTo(livingEntity.x, livingEntity.y, livingEntity.z, 1.0);
			} else if (d < this.method_14061() * this.method_14061()) {
				double e = livingEntity.x - this.blaze.x;
				double f = livingEntity.getBoundingBox().minY + (double)(livingEntity.height / 2.0F) - (this.blaze.y + (double)(this.blaze.height / 2.0F));
				double g = livingEntity.z - this.blaze.z;
				if (this.cooldown <= 0) {
					this.fireballsFired++;
					if (this.fireballsFired == 1) {
						this.cooldown = 60;
						this.blaze.setFireActive(true);
					} else if (this.fireballsFired <= 4) {
						this.cooldown = 6;
					} else {
						this.cooldown = 100;
						this.fireballsFired = 0;
						this.blaze.setFireActive(false);
					}

					if (this.fireballsFired > 1) {
						float h = MathHelper.sqrt(MathHelper.sqrt(d)) * 0.5F;
						this.blaze.world.syncWorldEvent(null, 1018, new BlockPos((int)this.blaze.x, (int)this.blaze.y, (int)this.blaze.z), 0);

						for (int i = 0; i < 1; i++) {
							SmallFireballEntity smallFireballEntity = new SmallFireballEntity(
								this.blaze.world, this.blaze, e + this.blaze.getRandom().nextGaussian() * (double)h, f, g + this.blaze.getRandom().nextGaussian() * (double)h
							);
							smallFireballEntity.y = this.blaze.y + (double)(this.blaze.height / 2.0F) + 0.5;
							this.blaze.world.spawnEntity(smallFireballEntity);
						}
					}
				}

				this.blaze.getLookControl().lookAt(livingEntity, 10.0F, 10.0F);
			} else {
				this.blaze.getNavigation().stop();
				this.blaze.getMotionHelper().moveTo(livingEntity.x, livingEntity.y, livingEntity.z, 1.0);
			}

			super.tick();
		}

		private double method_14061() {
			EntityAttributeInstance entityAttributeInstance = this.blaze.initializeAttribute(EntityAttributes.GENERIC_FOLLOW_RANGE);
			return entityAttributeInstance == null ? 16.0 : entityAttributeInstance.getValue();
		}
	}
}
