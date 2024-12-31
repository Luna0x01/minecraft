package net.minecraft.entity.mob;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.FindPlayerGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
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
import net.minecraft.world.World;

public class GhastEntity extends FlyingEntity implements Monster {
	private static final TrackedData<Boolean> field_14754 = DataTracker.registerData(GhastEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private int fireballStrength = 1;

	public GhastEntity(World world) {
		super(world);
		this.setBounds(4.0F, 4.0F);
		this.isFireImmune = true;
		this.experiencePoints = 5;
		this.entityMotionHelper = new GhastEntity.GhastMoveControl(this);
	}

	@Override
	protected void initGoals() {
		this.goals.add(5, new GhastEntity.FlyRandomlyGoal(this));
		this.goals.add(7, new GhastEntity.LookAtTargetGoal(this));
		this.goals.add(7, new GhastEntity.ShootFireballGoal(this));
		this.attackGoals.add(1, new FindPlayerGoal(this));
	}

	public boolean isShooting() {
		return this.dataTracker.get(field_14754);
	}

	public void setShooting(boolean shooting) {
		this.dataTracker.set(field_14754, shooting);
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
		} else if (source.getSource() instanceof FireballEntity && source.getAttacker() instanceof PlayerEntity) {
			super.damage(source, 1000.0F);
			return true;
		} else {
			return super.damage(source, amount);
		}
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_14754, false);
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(10.0);
		this.initializeAttribute(EntityAttributes.GENERIC_FOLLOW_RANGE).setBaseValue(100.0);
	}

	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.HOSTILE;
	}

	@Override
	protected Sound ambientSound() {
		return Sounds.ENTITY_GHAST_AMBIENT;
	}

	@Override
	protected Sound getHurtSound(DamageSource damageSource) {
		return Sounds.ENTITY_GHAST_HURT;
	}

	@Override
	protected Sound deathSound() {
		return Sounds.ENTITY_GHAST_DEATH;
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.GHAST_ENTITIE;
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

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		MobEntity.registerDataFixes(dataFixer, GhastEntity.class);
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
		private final GhastEntity ghast;

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
		private final GhastEntity ghast;
		private int collisionCheckCooldown;

		public GhastMoveControl(GhastEntity ghastEntity) {
			super(ghastEntity);
			this.ghast = ghastEntity;
		}

		@Override
		public void updateMovement() {
			if (this.state == MoveControl.MoveStatus.MOVE_TO) {
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
						this.state = MoveControl.MoveStatus.WAIT;
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
		private final GhastEntity ghast;

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
				this.ghast.yaw = -((float)MathHelper.atan2(this.ghast.velocityX, this.ghast.velocityZ)) * (180.0F / (float)Math.PI);
				this.ghast.bodyYaw = this.ghast.yaw;
			} else {
				LivingEntity livingEntity = this.ghast.getTarget();
				double d = 64.0;
				if (livingEntity.squaredDistanceTo(this.ghast) < 4096.0) {
					double e = livingEntity.x - this.ghast.x;
					double f = livingEntity.z - this.ghast.z;
					this.ghast.yaw = -((float)MathHelper.atan2(e, f)) * (180.0F / (float)Math.PI);
					this.ghast.bodyYaw = this.ghast.yaw;
				}
			}
		}
	}

	static class ShootFireballGoal extends Goal {
		private final GhastEntity ghast;
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
			if (livingEntity.squaredDistanceTo(this.ghast) < 4096.0 && this.ghast.canSee(livingEntity)) {
				World world = this.ghast.world;
				this.cooldown++;
				if (this.cooldown == 10) {
					world.syncWorldEvent(null, 1015, new BlockPos(this.ghast), 0);
				}

				if (this.cooldown == 20) {
					double e = 4.0;
					Vec3d vec3d = this.ghast.getRotationVector(1.0F);
					double f = livingEntity.x - (this.ghast.x + vec3d.x * 4.0);
					double g = livingEntity.getBoundingBox().minY + (double)(livingEntity.height / 2.0F) - (0.5 + this.ghast.y + (double)(this.ghast.height / 2.0F));
					double h = livingEntity.z - (this.ghast.z + vec3d.z * 4.0);
					world.syncWorldEvent(null, 1016, new BlockPos(this.ghast), 0);
					FireballEntity fireballEntity = new FireballEntity(world, this.ghast, f, g, h);
					fireballEntity.explosionPower = this.ghast.getFireballStrength();
					fireballEntity.x = this.ghast.x + vec3d.x * 4.0;
					fireballEntity.y = this.ghast.y + (double)(this.ghast.height / 2.0F) + 0.5;
					fireballEntity.z = this.ghast.z + vec3d.z * 4.0;
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
