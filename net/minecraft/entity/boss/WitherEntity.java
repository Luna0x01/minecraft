package net.minecraft.entity.boss;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.class_2925;
import net.minecraft.class_2957;
import net.minecraft.class_3133;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.predicate.EntityPredicate;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

public class WitherEntity extends HostileEntity implements RangedAttackMob {
	private static final TrackedData<Integer> field_14717 = DataTracker.registerData(WitherEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> field_14719 = DataTracker.registerData(WitherEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> field_14722 = DataTracker.registerData(WitherEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer>[] field_14720 = new TrackedData[]{field_14717, field_14719, field_14722};
	private static final TrackedData<Integer> field_14721 = DataTracker.registerData(WitherEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private final float[] sideHeadPitches = new float[2];
	private final float[] sideHeadYaws = new float[2];
	private final float[] prevSideHeadPitches = new float[2];
	private final float[] prevSideHeadYaws = new float[2];
	private final int[] field_5376 = new int[2];
	private final int[] field_5377 = new int[2];
	private int field_5378;
	private final class_2925 field_14718 = (class_2925)new class_2925(this.getName(), class_2957.Color.PURPLE, class_2957.Division.PROGRESS).method_12921(true);
	private static final Predicate<Entity> CAN_ATTACK_PREDICATE = new Predicate<Entity>() {
		public boolean apply(@Nullable Entity entity) {
			return entity instanceof LivingEntity && ((LivingEntity)entity).getGroup() != EntityGroup.UNDEAD && ((LivingEntity)entity).method_13948();
		}
	};

	public WitherEntity(World world) {
		super(world);
		this.setHealth(this.getMaxHealth());
		this.setBounds(0.9F, 3.5F);
		this.isFireImmune = true;
		((MobNavigation)this.getNavigation()).setCanSwim(true);
		this.experiencePoints = 50;
	}

	@Override
	protected void initGoals() {
		this.goals.add(0, new WitherEntity.class_2995());
		this.goals.add(1, new SwimGoal(this));
		this.goals.add(2, new ProjectileAttackGoal(this, 1.0, 40, 20.0F));
		this.goals.add(5, new class_3133(this, 1.0));
		this.goals.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goals.add(7, new LookAroundGoal(this));
		this.attackGoals.add(1, new RevengeGoal(this, false));
		this.attackGoals.add(2, new FollowTargetGoal(this, MobEntity.class, 0, false, false, CAN_ATTACK_PREDICATE));
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_14717, 0);
		this.dataTracker.startTracking(field_14719, 0);
		this.dataTracker.startTracking(field_14722, 0);
		this.dataTracker.startTracking(field_14721, 0);
	}

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		MobEntity.registerDataFixes(dataFixer, WitherEntity.class);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("Invul", this.getInvulnerabilityTime());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setInvulnerabilityTime(nbt.getInt("Invul"));
		if (this.hasCustomName()) {
			this.field_14718.setTitle(this.getName());
		}
	}

	@Override
	public void setCustomName(String name) {
		super.setCustomName(name);
		this.field_14718.setTitle(this.getName());
	}

	@Override
	protected Sound ambientSound() {
		return Sounds.ENTITY_WITHER_AMBIENT;
	}

	@Override
	protected Sound getHurtSound(DamageSource damageSource) {
		return Sounds.ENTITY_WITHER_HURT;
	}

	@Override
	protected Sound deathSound() {
		return Sounds.ENTITY_WITHER_DEATH;
	}

	@Override
	public void tickMovement() {
		this.velocityY *= 0.6F;
		if (!this.world.isClient && this.getTrackedEntityId(0) > 0) {
			Entity entity = this.world.getEntityById(this.getTrackedEntityId(0));
			if (entity != null) {
				if (this.y < entity.y || !this.shouldRenderOverlay() && this.y < entity.y + 5.0) {
					if (this.velocityY < 0.0) {
						this.velocityY = 0.0;
					}

					this.velocityY = this.velocityY + (0.5 - this.velocityY) * 0.6F;
				}

				double d = entity.x - this.x;
				double e = entity.z - this.z;
				double f = d * d + e * e;
				if (f > 9.0) {
					double g = (double)MathHelper.sqrt(f);
					this.velocityX = this.velocityX + (d / g * 0.5 - this.velocityX) * 0.6F;
					this.velocityZ = this.velocityZ + (e / g * 0.5 - this.velocityZ) * 0.6F;
				}
			}
		}

		if (this.velocityX * this.velocityX + this.velocityZ * this.velocityZ > 0.05F) {
			this.yaw = (float)MathHelper.atan2(this.velocityZ, this.velocityX) * (180.0F / (float)Math.PI) - 90.0F;
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
				double h = this.getHeadX(j + 1);
				double l = this.getHeadY(j + 1);
				double m = this.getHeadZ(j + 1);
				double n = entity2.x - h;
				double o = entity2.y + (double)entity2.getEyeHeight() - l;
				double p = entity2.z - m;
				double q = (double)MathHelper.sqrt(n * n + p * p);
				float r = (float)(MathHelper.atan2(p, n) * 180.0F / (float)Math.PI) - 90.0F;
				float s = (float)(-(MathHelper.atan2(o, q) * 180.0F / (float)Math.PI));
				this.sideHeadPitches[j] = this.getNextAngle(this.sideHeadPitches[j], s, 40.0F);
				this.sideHeadYaws[j] = this.getNextAngle(this.sideHeadYaws[j], r, 10.0F);
			} else {
				this.sideHeadYaws[j] = this.getNextAngle(this.sideHeadYaws[j], this.bodyYaw, 10.0F);
			}
		}

		boolean bl = this.shouldRenderOverlay();

		for (int t = 0; t < 3; t++) {
			double u = this.getHeadX(t);
			double v = this.getHeadY(t);
			double w = this.getHeadZ(t);
			this.world
				.addParticle(
					ParticleType.SMOKE, u + this.random.nextGaussian() * 0.3F, v + this.random.nextGaussian() * 0.3F, w + this.random.nextGaussian() * 0.3F, 0.0, 0.0, 0.0
				);
			if (bl && this.world.random.nextInt(4) == 0) {
				this.world
					.addParticle(
						ParticleType.MOB_SPELL,
						u + this.random.nextGaussian() * 0.3F,
						v + this.random.nextGaussian() * 0.3F,
						w + this.random.nextGaussian() * 0.3F,
						0.7F,
						0.7F,
						0.5
					);
			}
		}

		if (this.getInvulnerabilityTime() > 0) {
			for (int x = 0; x < 3; x++) {
				this.world
					.addParticle(
						ParticleType.MOB_SPELL,
						this.x + this.random.nextGaussian(),
						this.y + (double)(this.random.nextFloat() * 3.3F),
						this.z + this.random.nextGaussian(),
						0.7F,
						0.7F,
						0.9F
					);
			}
		}
	}

	@Override
	protected void mobTick() {
		if (this.getInvulnerabilityTime() > 0) {
			int i = this.getInvulnerabilityTime() - 1;
			if (i <= 0) {
				this.world.createExplosion(this, this.x, this.y + (double)this.getEyeHeight(), this.z, 7.0F, false, this.world.getGameRules().getBoolean("mobGriefing"));
				this.world.method_4689(1023, new BlockPos(this), 0);
			}

			this.setInvulnerabilityTime(i);
			if (this.ticksAlive % 10 == 0) {
				this.heal(10.0F);
			}
		} else {
			super.mobTick();

			for (int j = 1; j < 3; j++) {
				if (this.ticksAlive >= this.field_5376[j - 1]) {
					this.field_5376[j - 1] = this.ticksAlive + 10 + this.random.nextInt(10);
					if ((this.world.getGlobalDifficulty() == Difficulty.NORMAL || this.world.getGlobalDifficulty() == Difficulty.HARD) && this.field_5377[j - 1]++ > 15) {
						float f = 10.0F;
						float g = 5.0F;
						double d = MathHelper.nextDouble(this.random, this.x - 10.0, this.x + 10.0);
						double e = MathHelper.nextDouble(this.random, this.y - 5.0, this.y + 5.0);
						double h = MathHelper.nextDouble(this.random, this.z - 10.0, this.z + 10.0);
						this.shootSkullAt(j + 1, d, e, h, true);
						this.field_5377[j - 1] = 0;
					}

					int k = this.getTrackedEntityId(j);
					if (k > 0) {
						Entity entity = this.world.getEntityById(k);
						if (entity == null || !entity.isAlive() || this.squaredDistanceTo(entity) > 900.0 || !this.canSee(entity)) {
							this.setTrackedEntityId(j, 0);
						} else if (entity instanceof PlayerEntity && ((PlayerEntity)entity).abilities.invulnerable) {
							this.setTrackedEntityId(j, 0);
						} else {
							this.shootSkullAt(j + 1, (LivingEntity)entity);
							this.field_5376[j - 1] = this.ticksAlive + 40 + this.random.nextInt(20);
							this.field_5377[j - 1] = 0;
						}
					} else {
						List<LivingEntity> list = this.world
							.getEntitiesInBox(
								LivingEntity.class, this.getBoundingBox().expand(20.0, 8.0, 20.0), Predicates.and(CAN_ATTACK_PREDICATE, EntityPredicate.EXCEPT_SPECTATOR)
							);

						for (int l = 0; l < 10 && !list.isEmpty(); l++) {
							LivingEntity livingEntity = (LivingEntity)list.get(this.random.nextInt(list.size()));
							if (livingEntity != this && livingEntity.isAlive() && this.canSee(livingEntity)) {
								if (livingEntity instanceof PlayerEntity) {
									if (!((PlayerEntity)livingEntity).abilities.invulnerable) {
										this.setTrackedEntityId(j, livingEntity.getEntityId());
									}
								} else {
									this.setTrackedEntityId(j, livingEntity.getEntityId());
								}
								break;
							}

							list.remove(livingEntity);
						}
					}
				}
			}

			if (this.getTarget() != null) {
				this.setTrackedEntityId(0, this.getTarget().getEntityId());
			} else {
				this.setTrackedEntityId(0, 0);
			}

			if (this.field_5378 > 0) {
				this.field_5378--;
				if (this.field_5378 == 0 && this.world.getGameRules().getBoolean("mobGriefing")) {
					int m = MathHelper.floor(this.y);
					int n = MathHelper.floor(this.x);
					int o = MathHelper.floor(this.z);
					boolean bl = false;

					for (int p = -1; p <= 1; p++) {
						for (int q = -1; q <= 1; q++) {
							for (int r = 0; r <= 3; r++) {
								int s = n + p;
								int t = m + r;
								int u = o + q;
								BlockPos blockPos = new BlockPos(s, t, u);
								BlockState blockState = this.world.getBlockState(blockPos);
								Block block = blockState.getBlock();
								if (blockState.getMaterial() != Material.AIR && canDestroy(block)) {
									bl = this.world.removeBlock(blockPos, true) || bl;
								}
							}
						}
					}

					if (bl) {
						this.world.syncWorldEvent(null, 1022, new BlockPos(this), 0);
					}
				}
			}

			if (this.ticksAlive % 20 == 0) {
				this.heal(1.0F);
			}

			this.field_14718.setHealth(this.getHealth() / this.getMaxHealth());
		}
	}

	public static boolean canDestroy(Block block) {
		return block != Blocks.BEDROCK
			&& block != Blocks.END_PORTAL
			&& block != Blocks.END_PORTAL_FRAME
			&& block != Blocks.COMMAND_BLOCK
			&& block != Blocks.REPEATING_COMMAND_BLOCK
			&& block != Blocks.CHAIN_COMMAND_BLOCK
			&& block != Blocks.BARRIER
			&& block != Blocks.STRUCTURE_BLOCK
			&& block != Blocks.STRUCTURE_VOID
			&& block != Blocks.PISTON_EXTENSION
			&& block != Blocks.END_GATEWAY;
	}

	public void onSummoned() {
		this.setInvulnerabilityTime(220);
		this.setHealth(this.getMaxHealth() / 3.0F);
	}

	@Override
	public void setInLava() {
	}

	@Override
	public void onStartedTrackingBy(ServerPlayerEntity player) {
		super.onStartedTrackingBy(player);
		this.field_14718.method_12768(player);
	}

	@Override
	public void onStoppedTrackingBy(ServerPlayerEntity player) {
		super.onStoppedTrackingBy(player);
		this.field_14718.method_12769(player);
	}

	private double getHeadX(int headIndex) {
		if (headIndex <= 0) {
			return this.x;
		} else {
			float f = (this.bodyYaw + (float)(180 * (headIndex - 1))) * (float) (Math.PI / 180.0);
			float g = MathHelper.cos(f);
			return this.x + (double)g * 1.3;
		}
	}

	private double getHeadY(int headIndex) {
		return headIndex <= 0 ? this.y + 3.0 : this.y + 2.2;
	}

	private double getHeadZ(int headIndex) {
		if (headIndex <= 0) {
			return this.z;
		} else {
			float f = (this.bodyYaw + (float)(180 * (headIndex - 1))) * (float) (Math.PI / 180.0);
			float g = MathHelper.sin(f);
			return this.z + (double)g * 1.3;
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
		this.shootSkullAt(headIndex, target.x, target.y + (double)target.getEyeHeight() * 0.5, target.z, headIndex == 0 && this.random.nextFloat() < 0.001F);
	}

	private void shootSkullAt(int headIndex, double targetX, double targetY, double targetZ, boolean charged) {
		this.world.syncWorldEvent(null, 1024, new BlockPos(this), 0);
		double d = this.getHeadX(headIndex);
		double e = this.getHeadY(headIndex);
		double f = this.getHeadZ(headIndex);
		double g = targetX - d;
		double h = targetY - e;
		double i = targetZ - f;
		WitherSkullEntity witherSkullEntity = new WitherSkullEntity(this.world, this, g, h, i);
		if (charged) {
			witherSkullEntity.setCharged(true);
		}

		witherSkullEntity.y = e;
		witherSkullEntity.x = d;
		witherSkullEntity.z = f;
		this.world.spawnEntity(witherSkullEntity);
	}

	@Override
	public void rangedAttack(LivingEntity target, float pullProgress) {
		this.shootSkullAt(0, target);
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else if (source == DamageSource.DROWN || source.getAttacker() instanceof WitherEntity) {
			return false;
		} else if (this.getInvulnerabilityTime() > 0 && source != DamageSource.OUT_OF_WORLD) {
			return false;
		} else {
			if (this.shouldRenderOverlay()) {
				Entity entity = source.getSource();
				if (entity instanceof AbstractArrowEntity) {
					return false;
				}
			}

			Entity entity2 = source.getAttacker();
			if (entity2 != null && !(entity2 instanceof PlayerEntity) && entity2 instanceof LivingEntity && ((LivingEntity)entity2).getGroup() == this.getGroup()) {
				return false;
			} else {
				if (this.field_5378 <= 0) {
					this.field_5378 = 20;
				}

				for (int i = 0; i < this.field_5377.length; i++) {
					this.field_5377[i] = this.field_5377[i] + 3;
				}

				return super.damage(source, amount);
			}
		}
	}

	@Override
	protected void dropLoot(boolean allowDrops, int lootingMultiplier) {
		ItemEntity itemEntity = this.dropItem(Items.NETHER_STAR, 1);
		if (itemEntity != null) {
			itemEntity.setCovetedItem();
		}
	}

	@Override
	protected void checkDespawn() {
		this.despawnCounter = 0;
	}

	@Override
	public int getLightmapCoordinates() {
		return 15728880;
	}

	@Override
	public void handleFallDamage(float fallDistance, float damageMultiplier) {
	}

	@Override
	public void addStatusEffect(StatusEffectInstance instance) {
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(300.0);
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.6F);
		this.initializeAttribute(EntityAttributes.GENERIC_FOLLOW_RANGE).setBaseValue(40.0);
		this.initializeAttribute(EntityAttributes.GENERIC_ARMOR).setBaseValue(4.0);
	}

	public float getHeadYaw(int headIndex) {
		return this.sideHeadYaws[headIndex];
	}

	public float getHeadPitch(int headIndex) {
		return this.sideHeadPitches[headIndex];
	}

	public int getInvulnerabilityTime() {
		return this.dataTracker.get(field_14721);
	}

	public void setInvulnerabilityTime(int time) {
		this.dataTracker.set(field_14721, time);
	}

	public int getTrackedEntityId(int headIndex) {
		return this.dataTracker.get(field_14720[headIndex]);
	}

	public void setTrackedEntityId(int headIndex, int id) {
		this.dataTracker.set(field_14720[headIndex], id);
	}

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
	public void method_14057(boolean bl) {
	}

	class class_2995 extends Goal {
		public class_2995() {
			this.setCategoryBits(7);
		}

		@Override
		public boolean canStart() {
			return WitherEntity.this.getInvulnerabilityTime() > 0;
		}
	}
}
