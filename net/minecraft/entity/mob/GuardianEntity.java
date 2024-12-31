package net.minecraft.entity.mob;

import com.google.common.base.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.entity.ElderGuardianEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.GoToWalkTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

public class GuardianEntity extends HostileEntity {
	private static final TrackedData<Boolean> field_15549 = DataTracker.registerData(GuardianEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Integer> field_14756 = DataTracker.registerData(GuardianEntity.class, TrackedDataHandlerRegistry.INTEGER);
	protected float spikesExtension;
	protected float prevSpikesExtension;
	protected float spikesExtensionRate;
	protected float tailAngle;
	protected float prevTailAngle;
	private LivingEntity cachedBeamTarget;
	private int beamTicks;
	private boolean flopping;
	protected WanderAroundGoal wanderAroundGoal;

	public GuardianEntity(World world) {
		super(world);
		this.experiencePoints = 10;
		this.setBounds(0.85F, 0.85F);
		this.entityMotionHelper = new GuardianEntity.GuardianMoveControl(this);
		this.spikesExtension = this.random.nextFloat();
		this.prevSpikesExtension = this.spikesExtension;
	}

	@Override
	protected void initGoals() {
		GoToWalkTargetGoal goToWalkTargetGoal = new GoToWalkTargetGoal(this, 1.0);
		this.wanderAroundGoal = new WanderAroundGoal(this, 1.0, 80);
		this.goals.add(4, new GuardianEntity.FireBeamGoal(this));
		this.goals.add(5, goToWalkTargetGoal);
		this.goals.add(7, this.wanderAroundGoal);
		this.goals.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goals.add(8, new LookAtEntityGoal(this, GuardianEntity.class, 12.0F, 0.01F));
		this.goals.add(9, new LookAroundGoal(this));
		this.wanderAroundGoal.setCategoryBits(3);
		goToWalkTargetGoal.setCategoryBits(3);
		this.attackGoals.add(1, new FollowTargetGoal(this, LivingEntity.class, 10, true, false, new GuardianEntity.AttackPredicate(this)));
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(6.0);
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.5);
		this.initializeAttribute(EntityAttributes.GENERIC_FOLLOW_RANGE).setBaseValue(16.0);
		this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(30.0);
	}

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		MobEntity.registerDataFixes(dataFixer, GuardianEntity.class);
	}

	@Override
	protected EntityNavigation createNavigation(World world) {
		return new SwimNavigation(this, world);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_15549, false);
		this.dataTracker.startTracking(field_14756, 0);
	}

	public boolean areSpikesRetracted() {
		return this.dataTracker.get(field_15549);
	}

	private void setSpikesRetracted(boolean retracted) {
		this.dataTracker.set(field_15549, retracted);
	}

	public int getWarmupTime() {
		return 80;
	}

	private void setBeamTarget(int progress) {
		this.dataTracker.set(field_14756, progress);
	}

	public boolean hasBeamTarget() {
		return this.dataTracker.get(field_14756) != 0;
	}

	@Nullable
	public LivingEntity getBeamTarget() {
		if (!this.hasBeamTarget()) {
			return null;
		} else if (this.world.isClient) {
			if (this.cachedBeamTarget != null) {
				return this.cachedBeamTarget;
			} else {
				Entity entity = this.world.getEntityById(this.dataTracker.get(field_14756));
				if (entity instanceof LivingEntity) {
					this.cachedBeamTarget = (LivingEntity)entity;
					return this.cachedBeamTarget;
				} else {
					return null;
				}
			}
		} else {
			return this.getTarget();
		}
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		super.onTrackedDataSet(data);
		if (field_14756.equals(data)) {
			this.beamTicks = 0;
			this.cachedBeamTarget = null;
		}
	}

	@Override
	public int getMinAmbientSoundDelay() {
		return 160;
	}

	@Override
	protected Sound ambientSound() {
		return this.isTouchingWater() ? Sounds.ENTITY_GUARDIAN_AMBIENT : Sounds.ENTITY_GUARDIAN_AMBIENT_LAND;
	}

	@Override
	protected Sound method_13048() {
		return this.isTouchingWater() ? Sounds.ENTITY_GUARDIAN_HURT : Sounds.ENTITY_GUARDIAN_HURT_LAND;
	}

	@Override
	protected Sound deathSound() {
		return this.isTouchingWater() ? Sounds.ENTITY_GUARDIAN_DEATH : Sounds.ENTITY_GUARDIAN_DEATH_LAND;
	}

	@Override
	protected boolean canClimb() {
		return false;
	}

	@Override
	public float getEyeHeight() {
		return this.height * 0.5F;
	}

	@Override
	public float getPathfindingFavor(BlockPos pos) {
		return this.world.getBlockState(pos).getMaterial() == Material.WATER ? 10.0F + this.world.getBrightness(pos) - 0.5F : super.getPathfindingFavor(pos);
	}

	@Override
	public void tickMovement() {
		if (this.world.isClient) {
			this.prevSpikesExtension = this.spikesExtension;
			if (!this.isTouchingWater()) {
				this.spikesExtensionRate = 2.0F;
				if (this.velocityY > 0.0 && this.flopping && !this.isSilent()) {
					this.world.playSound(this.x, this.y, this.z, this.method_14091(), this.getSoundCategory(), 1.0F, 1.0F, false);
				}

				this.flopping = this.velocityY < 0.0 && this.world.renderAsNormalBlock(new BlockPos(this).down(), false);
			} else if (this.areSpikesRetracted()) {
				if (this.spikesExtensionRate < 0.5F) {
					this.spikesExtensionRate = 4.0F;
				} else {
					this.spikesExtensionRate = this.spikesExtensionRate + (0.5F - this.spikesExtensionRate) * 0.1F;
				}
			} else {
				this.spikesExtensionRate = this.spikesExtensionRate + (0.125F - this.spikesExtensionRate) * 0.2F;
			}

			this.spikesExtension = this.spikesExtension + this.spikesExtensionRate;
			this.prevTailAngle = this.tailAngle;
			if (!this.isTouchingWater()) {
				this.tailAngle = this.random.nextFloat();
			} else if (this.areSpikesRetracted()) {
				this.tailAngle = this.tailAngle + (0.0F - this.tailAngle) * 0.25F;
			} else {
				this.tailAngle = this.tailAngle + (1.0F - this.tailAngle) * 0.06F;
			}

			if (this.areSpikesRetracted() && this.isTouchingWater()) {
				Vec3d vec3d = this.getRotationVector(0.0F);

				for (int i = 0; i < 2; i++) {
					this.world
						.addParticle(
							ParticleType.BUBBLE,
							this.x + (this.random.nextDouble() - 0.5) * (double)this.width - vec3d.x * 1.5,
							this.y + this.random.nextDouble() * (double)this.height - vec3d.y * 1.5,
							this.z + (this.random.nextDouble() - 0.5) * (double)this.width - vec3d.z * 1.5,
							0.0,
							0.0,
							0.0
						);
				}
			}

			if (this.hasBeamTarget()) {
				if (this.beamTicks < this.getWarmupTime()) {
					this.beamTicks++;
				}

				LivingEntity livingEntity = this.getBeamTarget();
				if (livingEntity != null) {
					this.getLookControl().lookAt(livingEntity, 90.0F, 90.0F);
					this.getLookControl().tick();
					double d = (double)this.getBeamProgress(0.0F);
					double e = livingEntity.x - this.x;
					double f = livingEntity.y + (double)(livingEntity.height * 0.5F) - (this.y + (double)this.getEyeHeight());
					double g = livingEntity.z - this.z;
					double h = Math.sqrt(e * e + f * f + g * g);
					e /= h;
					f /= h;
					g /= h;
					double j = this.random.nextDouble();

					while (j < h) {
						j += 1.8 - d + this.random.nextDouble() * (1.7 - d);
						this.world.addParticle(ParticleType.BUBBLE, this.x + e * j, this.y + f * j + (double)this.getEyeHeight(), this.z + g * j, 0.0, 0.0, 0.0);
					}
				}
			}
		}

		if (this.touchingWater) {
			this.setAir(300);
		} else if (this.onGround) {
			this.velocityY += 0.5;
			this.velocityX = this.velocityX + (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.4F);
			this.velocityZ = this.velocityZ + (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.4F);
			this.yaw = this.random.nextFloat() * 360.0F;
			this.onGround = false;
			this.velocityDirty = true;
		}

		if (this.hasBeamTarget()) {
			this.yaw = this.headYaw;
		}

		super.tickMovement();
	}

	protected Sound method_14091() {
		return Sounds.ENTITY_GUARDIAN_FLOP;
	}

	public float getSpikesExtension(float tickDelta) {
		return this.prevSpikesExtension + (this.spikesExtension - this.prevSpikesExtension) * tickDelta;
	}

	public float getTailAngle(float tickDelta) {
		return this.prevTailAngle + (this.tailAngle - this.prevTailAngle) * tickDelta;
	}

	public float getBeamProgress(float tickDelta) {
		return ((float)this.beamTicks + tickDelta) / (float)this.getWarmupTime();
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.GUARDIAN_ENTITIE;
	}

	@Override
	protected boolean method_3087() {
		return true;
	}

	@Override
	public boolean hasNoSpawnCollisions() {
		return this.world.hasEntityIn(this.getBoundingBox(), this) && this.world.doesBoxCollide(this, this.getBoundingBox()).isEmpty();
	}

	@Override
	public boolean canSpawn() {
		return (this.random.nextInt(20) == 0 || !this.world.receivesSunlight(new BlockPos(this))) && super.canSpawn();
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (!this.areSpikesRetracted() && !source.getMagic() && source.getSource() instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity)source.getSource();
			if (!source.isExplosive()) {
				livingEntity.damage(DamageSource.thorns(this), 2.0F);
			}
		}

		if (this.wanderAroundGoal != null) {
			this.wanderAroundGoal.ignoreChanceOnce();
		}

		return super.damage(source, amount);
	}

	@Override
	public int getLookPitchSpeed() {
		return 180;
	}

	@Override
	public void travel(float f, float g) {
		if (this.canMoveVoluntarily() && this.isTouchingWater()) {
			this.updateVelocity(f, g, 0.1F);
			this.move(MovementType.SELF, this.velocityX, this.velocityY, this.velocityZ);
			this.velocityX *= 0.9F;
			this.velocityY *= 0.9F;
			this.velocityZ *= 0.9F;
			if (!this.areSpikesRetracted() && this.getTarget() == null) {
				this.velocityY -= 0.005;
			}
		} else {
			super.travel(f, g);
		}
	}

	static class AttackPredicate implements Predicate<LivingEntity> {
		private final GuardianEntity guardian;

		public AttackPredicate(GuardianEntity guardianEntity) {
			this.guardian = guardianEntity;
		}

		public boolean apply(@Nullable LivingEntity livingEntity) {
			return (livingEntity instanceof PlayerEntity || livingEntity instanceof SquidEntity) && livingEntity.squaredDistanceTo(this.guardian) > 9.0;
		}
	}

	static class FireBeamGoal extends Goal {
		private final GuardianEntity guardian;
		private int beamTicks;
		private final boolean field_15550;

		public FireBeamGoal(GuardianEntity guardianEntity) {
			this.guardian = guardianEntity;
			this.field_15550 = guardianEntity instanceof ElderGuardianEntity;
			this.setCategoryBits(3);
		}

		@Override
		public boolean canStart() {
			LivingEntity livingEntity = this.guardian.getTarget();
			return livingEntity != null && livingEntity.isAlive();
		}

		@Override
		public boolean shouldContinue() {
			return super.shouldContinue() && (this.field_15550 || this.guardian.squaredDistanceTo(this.guardian.getTarget()) > 9.0);
		}

		@Override
		public void start() {
			this.beamTicks = -10;
			this.guardian.getNavigation().stop();
			this.guardian.getLookControl().lookAt(this.guardian.getTarget(), 90.0F, 90.0F);
			this.guardian.velocityDirty = true;
		}

		@Override
		public void stop() {
			this.guardian.setBeamTarget(0);
			this.guardian.setTarget(null);
			this.guardian.wanderAroundGoal.ignoreChanceOnce();
		}

		@Override
		public void tick() {
			LivingEntity livingEntity = this.guardian.getTarget();
			this.guardian.getNavigation().stop();
			this.guardian.getLookControl().lookAt(livingEntity, 90.0F, 90.0F);
			if (!this.guardian.canSee(livingEntity)) {
				this.guardian.setTarget(null);
			} else {
				this.beamTicks++;
				if (this.beamTicks == 0) {
					this.guardian.setBeamTarget(this.guardian.getTarget().getEntityId());
					this.guardian.world.sendEntityStatus(this.guardian, (byte)21);
				} else if (this.beamTicks >= this.guardian.getWarmupTime()) {
					float f = 1.0F;
					if (this.guardian.world.getGlobalDifficulty() == Difficulty.HARD) {
						f += 2.0F;
					}

					if (this.field_15550) {
						f += 2.0F;
					}

					livingEntity.damage(DamageSource.magic(this.guardian, this.guardian), f);
					livingEntity.damage(DamageSource.mob(this.guardian), (float)this.guardian.initializeAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE).getValue());
					this.guardian.setTarget(null);
				}

				super.tick();
			}
		}
	}

	static class GuardianMoveControl extends MoveControl {
		private final GuardianEntity guardian;

		public GuardianMoveControl(GuardianEntity guardianEntity) {
			super(guardianEntity);
			this.guardian = guardianEntity;
		}

		@Override
		public void updateMovement() {
			if (this.state == MoveControl.MoveStatus.MOVE_TO && !this.guardian.getNavigation().isIdle()) {
				double d = this.targetX - this.guardian.x;
				double e = this.targetY - this.guardian.y;
				double f = this.targetZ - this.guardian.z;
				double g = (double)MathHelper.sqrt(d * d + e * e + f * f);
				e /= g;
				float h = (float)(MathHelper.atan2(f, d) * 180.0F / (float)Math.PI) - 90.0F;
				this.guardian.yaw = this.wrapDegrees(this.guardian.yaw, h, 90.0F);
				this.guardian.bodyYaw = this.guardian.yaw;
				float i = (float)(this.speed * this.guardian.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).getValue());
				this.guardian.setMovementSpeed(this.guardian.getMovementSpeed() + (i - this.guardian.getMovementSpeed()) * 0.125F);
				double j = Math.sin((double)(this.guardian.ticksAlive + this.guardian.getEntityId()) * 0.5) * 0.05;
				double k = Math.cos((double)(this.guardian.yaw * (float) (Math.PI / 180.0)));
				double l = Math.sin((double)(this.guardian.yaw * (float) (Math.PI / 180.0)));
				this.guardian.velocityX += j * k;
				this.guardian.velocityZ += j * l;
				j = Math.sin((double)(this.guardian.ticksAlive + this.guardian.getEntityId()) * 0.75) * 0.05;
				this.guardian.velocityY += j * (l + k) * 0.25;
				this.guardian.velocityY = this.guardian.velocityY + (double)this.guardian.getMovementSpeed() * e * 0.1;
				LookControl lookControl = this.guardian.getLookControl();
				double m = this.guardian.x + d / g * 2.0;
				double n = (double)this.guardian.getEyeHeight() + this.guardian.y + e / g;
				double o = this.guardian.z + f / g * 2.0;
				double p = lookControl.getLookX();
				double q = lookControl.getLookY();
				double r = lookControl.getLookZ();
				if (!lookControl.isActive()) {
					p = m;
					q = n;
					r = o;
				}

				this.guardian.getLookControl().lookAt(p + (m - p) * 0.125, q + (n - q) * 0.125, r + (o - r) * 0.125, 10.0F, 40.0F);
				this.guardian.setSpikesRetracted(true);
			} else {
				this.guardian.setMovementSpeed(0.0F);
				this.guardian.setSpikesRetracted(false);
			}
		}
	}
}
