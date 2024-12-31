package net.minecraft.entity.mob;

import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.GoToWalkTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class BlazeEntity extends HostileEntity {
	private float eyeOffset = 0.5F;
	private int eyeOffsetCooldown;

	public BlazeEntity(World world) {
		super(world);
		this.isFireImmune = true;
		this.experiencePoints = 10;
		this.goals.add(4, new BlazeEntity.ShootFireballGoal(this));
		this.goals.add(5, new GoToWalkTargetGoal(this, 1.0));
		this.goals.add(7, new WanderAroundGoal(this, 1.0));
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
		this.dataTracker.track(16, new Byte((byte)0));
	}

	@Override
	protected String getAmbientSound() {
		return "mob.blaze.breathe";
	}

	@Override
	protected String getHurtSound() {
		return "mob.blaze.hit";
	}

	@Override
	protected String getDeathSound() {
		return "mob.blaze.death";
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
				this.world.playSound(this.x + 0.5, this.y + 0.5, this.z + 0.5, "fire.fire", 1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F, false);
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
	protected Item getDefaultDrop() {
		return Items.BLAZE_ROD;
	}

	@Override
	public boolean isOnFire() {
		return this.isFireActive();
	}

	@Override
	protected void dropLoot(boolean allowDrops, int lootingMultiplier) {
		if (allowDrops) {
			int i = this.random.nextInt(2 + lootingMultiplier);

			for (int j = 0; j < i; j++) {
				this.dropItem(Items.BLAZE_ROD, 1);
			}
		}
	}

	public boolean isFireActive() {
		return (this.dataTracker.getByte(16) & 1) != 0;
	}

	public void setFireActive(boolean value) {
		byte b = this.dataTracker.getByte(16);
		if (value) {
			b = (byte)(b | 1);
		} else {
			b = (byte)(b & -2);
		}

		this.dataTracker.setProperty(16, b);
	}

	@Override
	protected boolean method_3087() {
		return true;
	}

	static class ShootFireballGoal extends Goal {
		private BlazeEntity blaze;
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
			} else if (d < 256.0) {
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
						this.blaze.world.syncWorldEvent(null, 1009, new BlockPos((int)this.blaze.x, (int)this.blaze.y, (int)this.blaze.z), 0);

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
	}
}
