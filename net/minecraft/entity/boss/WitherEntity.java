package net.minecraft.entity.boss;

import com.google.common.collect.ImmutableList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.entity.feature.SkinOverlayOwner;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class WitherEntity extends HostileEntity implements SkinOverlayOwner, RangedAttackMob {
	private static final TrackedData<Integer> TRACKED_ENTITY_ID_1 = DataTracker.registerData(WitherEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> TRACKED_ENTITY_ID_2 = DataTracker.registerData(WitherEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> TRACKED_ENTITY_ID_3 = DataTracker.registerData(WitherEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final List<TrackedData<Integer>> TRACKED_ENTITY_IDS = ImmutableList.of(TRACKED_ENTITY_ID_1, TRACKED_ENTITY_ID_2, TRACKED_ENTITY_ID_3);
	private static final TrackedData<Integer> INVUL_TIMER = DataTracker.registerData(WitherEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final int DEFAULT_INVUL_TIMER = 220;
	private final float[] sideHeadPitches = new float[2];
	private final float[] sideHeadYaws = new float[2];
	private final float[] prevSideHeadPitches = new float[2];
	private final float[] prevSideHeadYaws = new float[2];
	private final int[] skullCooldowns = new int[2];
	private final int[] chargedSkullCooldowns = new int[2];
	private int blockBreakingCooldown;
	private final ServerBossBar bossBar = (ServerBossBar)new ServerBossBar(this.getDisplayName(), BossBar.Color.PURPLE, BossBar.Style.PROGRESS).setDarkenSky(true);
	private static final Predicate<LivingEntity> CAN_ATTACK_PREDICATE = entity -> entity.getGroup() != EntityGroup.UNDEAD && entity.isMobOrPlayer();
	private static final TargetPredicate HEAD_TARGET_PREDICATE = TargetPredicate.createAttackable().setBaseMaxDistance(20.0).setPredicate(CAN_ATTACK_PREDICATE);

	public WitherEntity(EntityType<? extends WitherEntity> entityType, World world) {
		super(entityType, world);
		this.setHealth(this.getMaxHealth());
		this.getNavigation().setCanSwim(true);
		this.experiencePoints = 50;
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(0, new WitherEntity.DescendAtHalfHealthGoal());
		this.goalSelector.add(2, new ProjectileAttackGoal(this, 1.0, 40, 20.0F));
		this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0));
		this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(7, new LookAroundGoal(this));
		this.targetSelector.add(1, new RevengeGoal(this));
		this.targetSelector.add(2, new FollowTargetGoal(this, MobEntity.class, 0, false, false, CAN_ATTACK_PREDICATE));
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(TRACKED_ENTITY_ID_1, 0);
		this.dataTracker.startTracking(TRACKED_ENTITY_ID_2, 0);
		this.dataTracker.startTracking(TRACKED_ENTITY_ID_3, 0);
		this.dataTracker.startTracking(INVUL_TIMER, 0);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("Invul", this.getInvulnerableTimer());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setInvulTimer(nbt.getInt("Invul"));
		if (this.hasCustomName()) {
			this.bossBar.setName(this.getDisplayName());
		}
	}

	@Override
	public void setCustomName(@Nullable Text name) {
		super.setCustomName(name);
		this.bossBar.setName(this.getDisplayName());
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_WITHER_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_WITHER_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_WITHER_DEATH;
	}

	@Override
	public void tickMovement() {
		Vec3d vec3d = this.getVelocity().multiply(1.0, 0.6, 1.0);
		if (!this.world.isClient && this.getTrackedEntityId(0) > 0) {
			Entity entity = this.world.getEntityById(this.getTrackedEntityId(0));
			if (entity != null) {
				double d = vec3d.y;
				if (this.getY() < entity.getY() || !this.shouldRenderOverlay() && this.getY() < entity.getY() + 5.0) {
					d = Math.max(0.0, d);
					d += 0.3 - d * 0.6F;
				}

				vec3d = new Vec3d(vec3d.x, d, vec3d.z);
				Vec3d vec3d2 = new Vec3d(entity.getX() - this.getX(), 0.0, entity.getZ() - this.getZ());
				if (vec3d2.horizontalLengthSquared() > 9.0) {
					Vec3d vec3d3 = vec3d2.normalize();
					vec3d = vec3d.add(vec3d3.x * 0.3 - vec3d.x * 0.6, 0.0, vec3d3.z * 0.3 - vec3d.z * 0.6);
				}
			}
		}

		this.setVelocity(vec3d);
		if (vec3d.horizontalLengthSquared() > 0.05) {
			this.setYaw((float)MathHelper.atan2(vec3d.z, vec3d.x) * (180.0F / (float)Math.PI) - 90.0F);
		}

		super.tickMovement();

		for (int i = 0; i < 2; i++) {
			this.prevSideHeadYaws[i] = this.sideHeadYaws[i];
			this.prevSideHeadPitches[i] = this.sideHeadPitches[i];
		}

		for (int j = 0; j < 2; j++) {
			int k = this.getTrackedEntityId(j + 1);
			Entity entity2 = null;
			if (k > 0) {
				entity2 = this.world.getEntityById(k);
			}

			if (entity2 != null) {
				double e = this.getHeadX(j + 1);
				double f = this.getHeadY(j + 1);
				double g = this.getHeadZ(j + 1);
				double h = entity2.getX() - e;
				double l = entity2.getEyeY() - f;
				double m = entity2.getZ() - g;
				double n = Math.sqrt(h * h + m * m);
				float o = (float)(MathHelper.atan2(m, h) * 180.0F / (float)Math.PI) - 90.0F;
				float p = (float)(-(MathHelper.atan2(l, n) * 180.0F / (float)Math.PI));
				this.sideHeadPitches[j] = this.getNextAngle(this.sideHeadPitches[j], p, 40.0F);
				this.sideHeadYaws[j] = this.getNextAngle(this.sideHeadYaws[j], o, 10.0F);
			} else {
				this.sideHeadYaws[j] = this.getNextAngle(this.sideHeadYaws[j], this.bodyYaw, 10.0F);
			}
		}

		boolean bl = this.shouldRenderOverlay();

		for (int q = 0; q < 3; q++) {
			double r = this.getHeadX(q);
			double s = this.getHeadY(q);
			double t = this.getHeadZ(q);
			this.world
				.addParticle(
					ParticleTypes.SMOKE, r + this.random.nextGaussian() * 0.3F, s + this.random.nextGaussian() * 0.3F, t + this.random.nextGaussian() * 0.3F, 0.0, 0.0, 0.0
				);
			if (bl && this.world.random.nextInt(4) == 0) {
				this.world
					.addParticle(
						ParticleTypes.ENTITY_EFFECT,
						r + this.random.nextGaussian() * 0.3F,
						s + this.random.nextGaussian() * 0.3F,
						t + this.random.nextGaussian() * 0.3F,
						0.7F,
						0.7F,
						0.5
					);
			}
		}

		if (this.getInvulnerableTimer() > 0) {
			for (int u = 0; u < 3; u++) {
				this.world
					.addParticle(
						ParticleTypes.ENTITY_EFFECT,
						this.getX() + this.random.nextGaussian(),
						this.getY() + (double)(this.random.nextFloat() * 3.3F),
						this.getZ() + this.random.nextGaussian(),
						0.7F,
						0.7F,
						0.9F
					);
			}
		}
	}

	@Override
	protected void mobTick() {
		if (this.getInvulnerableTimer() > 0) {
			int i = this.getInvulnerableTimer() - 1;
			this.bossBar.setPercent(1.0F - (float)i / 220.0F);
			if (i <= 0) {
				Explosion.DestructionType destructionType = this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)
					? Explosion.DestructionType.DESTROY
					: Explosion.DestructionType.NONE;
				this.world.createExplosion(this, this.getX(), this.getEyeY(), this.getZ(), 7.0F, false, destructionType);
				if (!this.isSilent()) {
					this.world.syncGlobalEvent(1023, this.getBlockPos(), 0);
				}
			}

			this.setInvulTimer(i);
			if (this.age % 10 == 0) {
				this.heal(10.0F);
			}
		} else {
			super.mobTick();

			for (int j = 1; j < 3; j++) {
				if (this.age >= this.skullCooldowns[j - 1]) {
					this.skullCooldowns[j - 1] = this.age + 10 + this.random.nextInt(10);
					if ((this.world.getDifficulty() == Difficulty.NORMAL || this.world.getDifficulty() == Difficulty.HARD) && this.chargedSkullCooldowns[j - 1]++ > 15) {
						float f = 10.0F;
						float g = 5.0F;
						double d = MathHelper.nextDouble(this.random, this.getX() - 10.0, this.getX() + 10.0);
						double e = MathHelper.nextDouble(this.random, this.getY() - 5.0, this.getY() + 5.0);
						double h = MathHelper.nextDouble(this.random, this.getZ() - 10.0, this.getZ() + 10.0);
						this.shootSkullAt(j + 1, d, e, h, true);
						this.chargedSkullCooldowns[j - 1] = 0;
					}

					int k = this.getTrackedEntityId(j);
					if (k > 0) {
						LivingEntity livingEntity = (LivingEntity)this.world.getEntityById(k);
						if (livingEntity != null && this.canTarget(livingEntity) && !(this.squaredDistanceTo(livingEntity) > 900.0) && this.canSee(livingEntity)) {
							this.shootSkullAt(j + 1, livingEntity);
							this.skullCooldowns[j - 1] = this.age + 40 + this.random.nextInt(20);
							this.chargedSkullCooldowns[j - 1] = 0;
						} else {
							this.setTrackedEntityId(j, 0);
						}
					} else {
						List<LivingEntity> list = this.world.getTargets(LivingEntity.class, HEAD_TARGET_PREDICATE, this, this.getBoundingBox().expand(20.0, 8.0, 20.0));
						if (!list.isEmpty()) {
							LivingEntity livingEntity2 = (LivingEntity)list.get(this.random.nextInt(list.size()));
							this.setTrackedEntityId(j, livingEntity2.getId());
						}
					}
				}
			}

			if (this.getTarget() != null) {
				this.setTrackedEntityId(0, this.getTarget().getId());
			} else {
				this.setTrackedEntityId(0, 0);
			}

			if (this.blockBreakingCooldown > 0) {
				this.blockBreakingCooldown--;
				if (this.blockBreakingCooldown == 0 && this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
					int l = MathHelper.floor(this.getY());
					int m = MathHelper.floor(this.getX());
					int n = MathHelper.floor(this.getZ());
					boolean bl = false;

					for (int o = -1; o <= 1; o++) {
						for (int p = -1; p <= 1; p++) {
							for (int q = 0; q <= 3; q++) {
								int r = m + o;
								int s = l + q;
								int t = n + p;
								BlockPos blockPos = new BlockPos(r, s, t);
								BlockState blockState = this.world.getBlockState(blockPos);
								if (canDestroy(blockState)) {
									bl = this.world.breakBlock(blockPos, true, this) || bl;
								}
							}
						}
					}

					if (bl) {
						this.world.syncWorldEvent(null, 1022, this.getBlockPos(), 0);
					}
				}
			}

			if (this.age % 20 == 0) {
				this.heal(1.0F);
			}

			this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());
		}
	}

	public static boolean canDestroy(BlockState block) {
		return !block.isAir() && !block.isIn(BlockTags.WITHER_IMMUNE);
	}

	public void onSummoned() {
		this.setInvulTimer(220);
		this.bossBar.setPercent(0.0F);
		this.setHealth(this.getMaxHealth() / 3.0F);
	}

	@Override
	public void slowMovement(BlockState state, Vec3d multiplier) {
	}

	@Override
	public void onStartedTrackingBy(ServerPlayerEntity player) {
		super.onStartedTrackingBy(player);
		this.bossBar.addPlayer(player);
	}

	@Override
	public void onStoppedTrackingBy(ServerPlayerEntity player) {
		super.onStoppedTrackingBy(player);
		this.bossBar.removePlayer(player);
	}

	private double getHeadX(int headIndex) {
		if (headIndex <= 0) {
			return this.getX();
		} else {
			float f = (this.bodyYaw + (float)(180 * (headIndex - 1))) * (float) (Math.PI / 180.0);
			float g = MathHelper.cos(f);
			return this.getX() + (double)g * 1.3;
		}
	}

	private double getHeadY(int headIndex) {
		return headIndex <= 0 ? this.getY() + 3.0 : this.getY() + 2.2;
	}

	private double getHeadZ(int headIndex) {
		if (headIndex <= 0) {
			return this.getZ();
		} else {
			float f = (this.bodyYaw + (float)(180 * (headIndex - 1))) * (float) (Math.PI / 180.0);
			float g = MathHelper.sin(f);
			return this.getZ() + (double)g * 1.3;
		}
	}

	private float getNextAngle(float prevAngle, float desiredAngle, float maxDifference) {
		float f = MathHelper.wrapDegrees(desiredAngle - prevAngle);
		if (f > maxDifference) {
			f = maxDifference;
		}

		if (f < -maxDifference) {
			f = -maxDifference;
		}

		return prevAngle + f;
	}

	private void shootSkullAt(int headIndex, LivingEntity target) {
		this.shootSkullAt(
			headIndex, target.getX(), target.getY() + (double)target.getStandingEyeHeight() * 0.5, target.getZ(), headIndex == 0 && this.random.nextFloat() < 0.001F
		);
	}

	private void shootSkullAt(int headIndex, double targetX, double targetY, double targetZ, boolean charged) {
		if (!this.isSilent()) {
			this.world.syncWorldEvent(null, 1024, this.getBlockPos(), 0);
		}

		double d = this.getHeadX(headIndex);
		double e = this.getHeadY(headIndex);
		double f = this.getHeadZ(headIndex);
		double g = targetX - d;
		double h = targetY - e;
		double i = targetZ - f;
		WitherSkullEntity witherSkullEntity = new WitherSkullEntity(this.world, this, g, h, i);
		witherSkullEntity.setOwner(this);
		if (charged) {
			witherSkullEntity.setCharged(true);
		}

		witherSkullEntity.setPos(d, e, f);
		this.world.spawnEntity(witherSkullEntity);
	}

	@Override
	public void attack(LivingEntity target, float pullProgress) {
		this.shootSkullAt(0, target);
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else if (source == DamageSource.DROWN || source.getAttacker() instanceof WitherEntity) {
			return false;
		} else if (this.getInvulnerableTimer() > 0 && source != DamageSource.OUT_OF_WORLD) {
			return false;
		} else {
			if (this.shouldRenderOverlay()) {
				Entity entity = source.getSource();
				if (entity instanceof PersistentProjectileEntity) {
					return false;
				}
			}

			Entity entity2 = source.getAttacker();
			if (entity2 != null && !(entity2 instanceof PlayerEntity) && entity2 instanceof LivingEntity && ((LivingEntity)entity2).getGroup() == this.getGroup()) {
				return false;
			} else {
				if (this.blockBreakingCooldown <= 0) {
					this.blockBreakingCooldown = 20;
				}

				for (int i = 0; i < this.chargedSkullCooldowns.length; i++) {
					this.chargedSkullCooldowns[i] = this.chargedSkullCooldowns[i] + 3;
				}

				return super.damage(source, amount);
			}
		}
	}

	@Override
	protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
		super.dropEquipment(source, lootingMultiplier, allowDrops);
		ItemEntity itemEntity = this.dropItem(Items.NETHER_STAR);
		if (itemEntity != null) {
			itemEntity.setCovetedItem();
		}
	}

	@Override
	public void checkDespawn() {
		if (this.world.getDifficulty() == Difficulty.PEACEFUL && this.isDisallowedInPeaceful()) {
			this.discard();
		} else {
			this.despawnCounter = 0;
		}
	}

	@Override
	public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
		return false;
	}

	@Override
	public boolean addStatusEffect(StatusEffectInstance effect, @Nullable Entity source) {
		return false;
	}

	public static DefaultAttributeContainer.Builder createWitherAttributes() {
		return HostileEntity.createHostileAttributes()
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 300.0)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.6F)
			.add(EntityAttributes.GENERIC_FOLLOW_RANGE, 40.0)
			.add(EntityAttributes.GENERIC_ARMOR, 4.0);
	}

	public float getHeadYaw(int headIndex) {
		return this.sideHeadYaws[headIndex];
	}

	public float getHeadPitch(int headIndex) {
		return this.sideHeadPitches[headIndex];
	}

	public int getInvulnerableTimer() {
		return this.dataTracker.get(INVUL_TIMER);
	}

	public void setInvulTimer(int ticks) {
		this.dataTracker.set(INVUL_TIMER, ticks);
	}

	public int getTrackedEntityId(int headIndex) {
		return this.dataTracker.<Integer>get((TrackedData<Integer>)TRACKED_ENTITY_IDS.get(headIndex));
	}

	public void setTrackedEntityId(int headIndex, int id) {
		this.dataTracker.set((TrackedData<Integer>)TRACKED_ENTITY_IDS.get(headIndex), id);
	}

	@Override
	public boolean shouldRenderOverlay() {
		return this.getHealth() <= this.getMaxHealth() / 2.0F;
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.UNDEAD;
	}

	@Override
	protected boolean canStartRiding(Entity entity) {
		return false;
	}

	@Override
	public boolean canUsePortals() {
		return false;
	}

	@Override
	public boolean canHaveStatusEffect(StatusEffectInstance effect) {
		return effect.getEffectType() == StatusEffects.WITHER ? false : super.canHaveStatusEffect(effect);
	}

	class DescendAtHalfHealthGoal extends Goal {
		public DescendAtHalfHealthGoal() {
			this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.JUMP, Goal.Control.LOOK));
		}

		@Override
		public boolean canStart() {
			return WitherEntity.this.getInvulnerableTimer() > 0;
		}
	}
}
