package net.minecraft.entity.mob;

import java.util.Random;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.FindPlayerGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

public class GhastEntity extends FlyingEntity implements Monster {
	private int fireballStrength = 1;

	public GhastEntity(World world) {
		super(world);
		this.setBounds(4.0F, 4.0F);
		this.isFireImmune = true;
		this.experiencePoints = 5;
		this.entityMotionHelper = new GhastEntity.GhastMoveControl(this);
		this.goals.add(5, new GhastEntity.FlyRandomlyGoal(this));
		this.goals.add(7, new GhastEntity.LookAtTargetGoal(this));
		this.goals.add(7, new GhastEntity.ShootFireballGoal(this));
		this.attackGoals.add(1, new FindPlayerGoal(this));
	}

	public boolean isShooting() {
		return this.dataTracker.getByte(16) != 0;
	}

	public void setShooting(boolean shooting) {
		this.dataTracker.setProperty(16, Byte.valueOf((byte)(shooting ? 1 : 0)));
	}

	public int getFireballStrength() {
		return this.fireballStrength;
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.world.isClient && this.world.getGlobalDifficulty() == Difficulty.PEACEFUL) {
			this.remove();
		}
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else if ("fireball".equals(source.getName()) && source.getAttacker() instanceof PlayerEntity) {
			super.damage(source, 1000.0F);
			((PlayerEntity)source.getAttacker()).incrementStat(AchievementsAndCriterions.GHAST);
			return true;
		} else {
			return super.damage(source, amount);
		}
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.track(16, (byte)0);
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(10.0);
		this.initializeAttribute(EntityAttributes.GENERIC_FOLLOW_RANGE).setBaseValue(100.0);
	}

	@Override
	protected String getAmbientSound() {
		return "mob.ghast.moan";
	}

	@Override
	protected String getHurtSound() {
		return "mob.ghast.scream";
	}

	@Override
	protected String getDeathSound() {
		return "mob.ghast.death";
	}

	@Override
	protected Item getDefaultDrop() {
		return Items.GUNPOWDER;
	}

	@Override
	protected void dropLoot(boolean allowDrops, int lootingMultiplier) {
		int i = this.random.nextInt(2) + this.random.nextInt(1 + lootingMultiplier);

		for (int j = 0; j < i; j++) {
			this.dropItem(Items.GHAST_TEAR, 1);
		}

		i = this.random.nextInt(3) + this.random.nextInt(1 + lootingMultiplier);

		for (int k = 0; k < i; k++) {
			this.dropItem(Items.GUNPOWDER, 1);
		}
	}

	@Override
	protected float getSoundVolume() {
		return 10.0F;
	}

	@Override
	public boolean canSpawn() {
		return this.random.nextInt(20) == 0 && super.canSpawn() && this.world.getGlobalDifficulty() != Difficulty.PEACEFUL;
	}

	@Override
	public int getLimitPerChunk() {
		return 1;
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("ExplosionPower", this.fireballStrength);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("ExplosionPower", 99)) {
			this.fireballStrength = nbt.getInt("ExplosionPower");
		}
	}

	@Override
	public float getEyeHeight() {
		return 2.6F;
	}

	static class FlyRandomlyGoal extends Goal {
		private GhastEntity ghast;

		public FlyRandomlyGoal(GhastEntity ghastEntity) {
			this.ghast = ghastEntity;
			this.setCategoryBits(1);
		}

		@Override
		public boolean canStart() {
			MoveControl moveControl = this.ghast.getMotionHelper();
			if (!moveControl.isMoving()) {
				return true;
			} else {
				double d = moveControl.getTargetX() - this.ghast.x;
				double e = moveControl.getTargetY() - this.ghast.y;
				double f = moveControl.getTargetZ() - this.ghast.z;
				double g = d * d + e * e + f * f;
				return g < 1.0 || g > 3600.0;
			}
		}

		@Override
		public boolean shouldContinue() {
			return false;
		}

		@Override
		public void start() {
			Random random = this.ghast.getRandom();
			double d = this.ghast.x + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
			double e = this.ghast.y + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
			double f = this.ghast.z + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
			this.ghast.getMotionHelper().moveTo(d, e, f, 1.0);
		}
	}

	static class GhastMoveControl extends MoveControl {
		private GhastEntity ghast;
		private int collisionCheckCooldown;

		public GhastMoveControl(GhastEntity ghastEntity) {
			super(ghastEntity);
			this.ghast = ghastEntity;
		}

		@Override
		public void updateMovement() {
			if (this.moving) {
				double d = this.targetX - this.ghast.x;
				double e = this.targetY - this.ghast.y;
				double f = this.targetZ - this.ghast.z;
				double g = d * d + e * e + f * f;
				if (this.collisionCheckCooldown-- <= 0) {
					this.collisionCheckCooldown = this.collisionCheckCooldown + this.ghast.getRandom().nextInt(5) + 2;
					g = (double)MathHelper.sqrt(g);
					if (this.willCollide(this.targetX, this.targetY, this.targetZ, g)) {
						this.ghast.velocityX += d / g * 0.1;
						this.ghast.velocityY += e / g * 0.1;
						this.ghast.velocityZ += f / g * 0.1;
					} else {
						this.moving = false;
					}
				}
			}
		}

		private boolean willCollide(double x, double y, double z, double steps) {
			double d = (x - this.ghast.x) / steps;
			double e = (y - this.ghast.y) / steps;
			double f = (z - this.ghast.z) / steps;
			Box box = this.ghast.getBoundingBox();

			for (int i = 1; (double)i < steps; i++) {
				box = box.offset(d, e, f);
				if (!this.ghast.world.doesBoxCollide(this.ghast, box).isEmpty()) {
					return false;
				}
			}

			return true;
		}
	}

	static class LookAtTargetGoal extends Goal {
		private GhastEntity ghast;

		public LookAtTargetGoal(GhastEntity ghastEntity) {
			this.ghast = ghastEntity;
			this.setCategoryBits(2);
		}

		@Override
		public boolean canStart() {
			return true;
		}

		@Override
		public void tick() {
			if (this.ghast.getTarget() == null) {
				this.ghast.bodyYaw = this.ghast.yaw = -((float)MathHelper.atan2(this.ghast.velocityX, this.ghast.velocityZ)) * 180.0F / (float) Math.PI;
			} else {
				LivingEntity livingEntity = this.ghast.getTarget();
				double d = 64.0;
				if (livingEntity.squaredDistanceTo(this.ghast) < d * d) {
					double e = livingEntity.x - this.ghast.x;
					double f = livingEntity.z - this.ghast.z;
					this.ghast.bodyYaw = this.ghast.yaw = -((float)MathHelper.atan2(e, f)) * 180.0F / (float) Math.PI;
				}
			}
		}
	}

	static class ShootFireballGoal extends Goal {
		private GhastEntity ghast;
		public int cooldown;

		public ShootFireballGoal(GhastEntity ghastEntity) {
			this.ghast = ghastEntity;
		}

		@Override
		public boolean canStart() {
			return this.ghast.getTarget() != null;
		}

		@Override
		public void start() {
			this.cooldown = 0;
		}

		@Override
		public void stop() {
			this.ghast.setShooting(false);
		}

		@Override
		public void tick() {
			LivingEntity livingEntity = this.ghast.getTarget();
			double d = 64.0;
			if (livingEntity.squaredDistanceTo(this.ghast) < d * d && this.ghast.canSee(livingEntity)) {
				World world = this.ghast.world;
				this.cooldown++;
				if (this.cooldown == 10) {
					world.syncWorldEvent(null, 1007, new BlockPos(this.ghast), 0);
				}

				if (this.cooldown == 20) {
					double e = 4.0;
					Vec3d vec3d = this.ghast.getRotationVector(1.0F);
					double f = livingEntity.x - (this.ghast.x + vec3d.x * e);
					double g = livingEntity.getBoundingBox().minY + (double)(livingEntity.height / 2.0F) - (0.5 + this.ghast.y + (double)(this.ghast.height / 2.0F));
					double h = livingEntity.z - (this.ghast.z + vec3d.z * e);
					world.syncWorldEvent(null, 1008, new BlockPos(this.ghast), 0);
					FireballEntity fireballEntity = new FireballEntity(world, this.ghast, f, g, h);
					fireballEntity.explosionPower = this.ghast.getFireballStrength();
					fireballEntity.x = this.ghast.x + vec3d.x * e;
					fireballEntity.y = this.ghast.y + (double)(this.ghast.height / 2.0F) + 0.5;
					fireballEntity.z = this.ghast.z + vec3d.z * e;
					world.spawnEntity(fireballEntity);
					this.cooldown = -40;
				}
			} else if (this.cooldown > 0) {
				this.cooldown--;
			}

			this.ghast.setShooting(this.cooldown > 10);
		}
	}
}
