package net.minecraft.entity.boss.dragon;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathMinHeap;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.dragon.phase.Phase;
import net.minecraft.entity.boss.dragon.phase.PhaseManager;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.MobSpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.EndPortalFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EnderDragonEntity extends MobEntity implements Monster {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final TrackedData<Integer> PHASE_TYPE = DataTracker.registerData(EnderDragonEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TargetPredicate CLOSE_PLAYER_PREDICATE = TargetPredicate.createAttackable().setBaseMaxDistance(64.0);
	private static final int MAX_HEALTH = 200;
	private static final int field_30429 = 400;
	private static final float TAKEOFF_THRESHOLD = 0.25F;
	private static final String DRAGON_DEATH_TIME_KEY = "DragonDeathTime";
	private static final String DRAGON_PHASE_KEY = "DragonPhase";
	public final double[][] segmentCircularBuffer = new double[64][3];
	public int latestSegment = -1;
	private final EnderDragonPart[] parts;
	public final EnderDragonPart head;
	private final EnderDragonPart neck;
	private final EnderDragonPart body;
	private final EnderDragonPart tail1;
	private final EnderDragonPart tail2;
	private final EnderDragonPart tail3;
	private final EnderDragonPart rightWing;
	private final EnderDragonPart leftWing;
	public float prevWingPosition;
	public float wingPosition;
	public boolean slowedDownByBlock;
	public int ticksSinceDeath;
	public float yawAcceleration;
	@Nullable
	public EndCrystalEntity connectedCrystal;
	@Nullable
	private final EnderDragonFight fight;
	private final PhaseManager phaseManager;
	private int ticksUntilNextGrowl = 100;
	private int damageDuringSitting;
	private final PathNode[] pathNodes = new PathNode[24];
	private final int[] pathNodeConnections = new int[24];
	private final PathMinHeap pathHeap = new PathMinHeap();

	public EnderDragonEntity(EntityType<? extends EnderDragonEntity> entityType, World world) {
		super(EntityType.ENDER_DRAGON, world);
		this.head = new EnderDragonPart(this, "head", 1.0F, 1.0F);
		this.neck = new EnderDragonPart(this, "neck", 3.0F, 3.0F);
		this.body = new EnderDragonPart(this, "body", 5.0F, 3.0F);
		this.tail1 = new EnderDragonPart(this, "tail", 2.0F, 2.0F);
		this.tail2 = new EnderDragonPart(this, "tail", 2.0F, 2.0F);
		this.tail3 = new EnderDragonPart(this, "tail", 2.0F, 2.0F);
		this.rightWing = new EnderDragonPart(this, "wing", 4.0F, 2.0F);
		this.leftWing = new EnderDragonPart(this, "wing", 4.0F, 2.0F);
		this.parts = new EnderDragonPart[]{this.head, this.neck, this.body, this.tail1, this.tail2, this.tail3, this.rightWing, this.leftWing};
		this.setHealth(this.getMaxHealth());
		this.noClip = true;
		this.ignoreCameraFrustum = true;
		if (world instanceof ServerWorld) {
			this.fight = ((ServerWorld)world).getEnderDragonFight();
		} else {
			this.fight = null;
		}

		this.phaseManager = new PhaseManager(this);
	}

	public static DefaultAttributeContainer.Builder createEnderDragonAttributes() {
		return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 200.0);
	}

	@Override
	public boolean hasWings() {
		float f = MathHelper.cos(this.wingPosition * (float) (Math.PI * 2));
		float g = MathHelper.cos(this.prevWingPosition * (float) (Math.PI * 2));
		return g <= -0.3F && f >= -0.3F;
	}

	@Override
	public void addFlapEffects() {
		if (this.world.isClient && !this.isSilent()) {
			this.world
				.playSound(
					this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ENDER_DRAGON_FLAP, this.getSoundCategory(), 5.0F, 0.8F + this.random.nextFloat() * 0.3F, false
				);
		}
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.getDataTracker().startTracking(PHASE_TYPE, PhaseType.HOVER.getTypeId());
	}

	public double[] getSegmentProperties(int segmentNumber, float tickDelta) {
		if (this.isDead()) {
			tickDelta = 0.0F;
		}

		tickDelta = 1.0F - tickDelta;
		int i = this.latestSegment - segmentNumber & 63;
		int j = this.latestSegment - segmentNumber - 1 & 63;
		double[] ds = new double[3];
		double d = this.segmentCircularBuffer[i][0];
		double e = MathHelper.wrapDegrees(this.segmentCircularBuffer[j][0] - d);
		ds[0] = d + e * (double)tickDelta;
		d = this.segmentCircularBuffer[i][1];
		e = this.segmentCircularBuffer[j][1] - d;
		ds[1] = d + e * (double)tickDelta;
		ds[2] = MathHelper.lerp((double)tickDelta, this.segmentCircularBuffer[i][2], this.segmentCircularBuffer[j][2]);
		return ds;
	}

	@Override
	public void tickMovement() {
		this.addAirTravelEffects();
		if (this.world.isClient) {
			this.setHealth(this.getHealth());
			if (!this.isSilent() && !this.phaseManager.getCurrent().isSittingOrHovering() && --this.ticksUntilNextGrowl < 0) {
				this.world
					.playSound(
						this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ENDER_DRAGON_GROWL, this.getSoundCategory(), 2.5F, 0.8F + this.random.nextFloat() * 0.3F, false
					);
				this.ticksUntilNextGrowl = 200 + this.random.nextInt(200);
			}
		}

		this.prevWingPosition = this.wingPosition;
		if (this.isDead()) {
			float f = (this.random.nextFloat() - 0.5F) * 8.0F;
			float g = (this.random.nextFloat() - 0.5F) * 4.0F;
			float h = (this.random.nextFloat() - 0.5F) * 8.0F;
			this.world.addParticle(ParticleTypes.EXPLOSION, this.getX() + (double)f, this.getY() + 2.0 + (double)g, this.getZ() + (double)h, 0.0, 0.0, 0.0);
		} else {
			this.tickWithEndCrystals();
			Vec3d vec3d = this.getVelocity();
			float i = 0.2F / ((float)vec3d.horizontalLength() * 10.0F + 1.0F);
			i *= (float)Math.pow(2.0, vec3d.y);
			if (this.phaseManager.getCurrent().isSittingOrHovering()) {
				this.wingPosition += 0.1F;
			} else if (this.slowedDownByBlock) {
				this.wingPosition += i * 0.5F;
			} else {
				this.wingPosition += i;
			}

			this.setYaw(MathHelper.wrapDegrees(this.getYaw()));
			if (this.isAiDisabled()) {
				this.wingPosition = 0.5F;
			} else {
				if (this.latestSegment < 0) {
					for (int j = 0; j < this.segmentCircularBuffer.length; j++) {
						this.segmentCircularBuffer[j][0] = (double)this.getYaw();
						this.segmentCircularBuffer[j][1] = this.getY();
					}
				}

				if (++this.latestSegment == this.segmentCircularBuffer.length) {
					this.latestSegment = 0;
				}

				this.segmentCircularBuffer[this.latestSegment][0] = (double)this.getYaw();
				this.segmentCircularBuffer[this.latestSegment][1] = this.getY();
				if (this.world.isClient) {
					if (this.bodyTrackingIncrements > 0) {
						double d = this.getX() + (this.serverX - this.getX()) / (double)this.bodyTrackingIncrements;
						double e = this.getY() + (this.serverY - this.getY()) / (double)this.bodyTrackingIncrements;
						double k = this.getZ() + (this.serverZ - this.getZ()) / (double)this.bodyTrackingIncrements;
						double l = MathHelper.wrapDegrees(this.serverYaw - (double)this.getYaw());
						this.setYaw(this.getYaw() + (float)l / (float)this.bodyTrackingIncrements);
						this.setPitch(this.getPitch() + (float)(this.serverPitch - (double)this.getPitch()) / (float)this.bodyTrackingIncrements);
						this.bodyTrackingIncrements--;
						this.setPosition(d, e, k);
						this.setRotation(this.getYaw(), this.getPitch());
					}

					this.phaseManager.getCurrent().clientTick();
				} else {
					Phase phase = this.phaseManager.getCurrent();
					phase.serverTick();
					if (this.phaseManager.getCurrent() != phase) {
						phase = this.phaseManager.getCurrent();
						phase.serverTick();
					}

					Vec3d vec3d2 = phase.getPathTarget();
					if (vec3d2 != null) {
						double m = vec3d2.x - this.getX();
						double n = vec3d2.y - this.getY();
						double o = vec3d2.z - this.getZ();
						double p = m * m + n * n + o * o;
						float q = phase.getMaxYAcceleration();
						double r = Math.sqrt(m * m + o * o);
						if (r > 0.0) {
							n = MathHelper.clamp(n / r, (double)(-q), (double)q);
						}

						this.setVelocity(this.getVelocity().add(0.0, n * 0.01, 0.0));
						this.setYaw(MathHelper.wrapDegrees(this.getYaw()));
						Vec3d vec3d3 = vec3d2.subtract(this.getX(), this.getY(), this.getZ()).normalize();
						Vec3d vec3d4 = new Vec3d(
								(double)MathHelper.sin(this.getYaw() * (float) (Math.PI / 180.0)),
								this.getVelocity().y,
								(double)(-MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0)))
							)
							.normalize();
						float s = Math.max(((float)vec3d4.dotProduct(vec3d3) + 0.5F) / 1.5F, 0.0F);
						if (Math.abs(m) > 1.0E-5F || Math.abs(o) > 1.0E-5F) {
							double t = MathHelper.clamp(MathHelper.wrapDegrees(180.0 - MathHelper.atan2(m, o) * 180.0F / (float)Math.PI - (double)this.getYaw()), -50.0, 50.0);
							this.yawAcceleration *= 0.8F;
							this.yawAcceleration = (float)((double)this.yawAcceleration + t * (double)phase.getYawAcceleration());
							this.setYaw(this.getYaw() + this.yawAcceleration * 0.1F);
						}

						float u = (float)(2.0 / (p + 1.0));
						float v = 0.06F;
						this.updateVelocity(0.06F * (s * u + (1.0F - u)), new Vec3d(0.0, 0.0, -1.0));
						if (this.slowedDownByBlock) {
							this.move(MovementType.SELF, this.getVelocity().multiply(0.8F));
						} else {
							this.move(MovementType.SELF, this.getVelocity());
						}

						Vec3d vec3d5 = this.getVelocity().normalize();
						double w = 0.8 + 0.15 * (vec3d5.dotProduct(vec3d4) + 1.0) / 2.0;
						this.setVelocity(this.getVelocity().multiply(w, 0.91F, w));
					}
				}

				this.bodyYaw = this.getYaw();
				Vec3d[] vec3ds = new Vec3d[this.parts.length];

				for (int x = 0; x < this.parts.length; x++) {
					vec3ds[x] = new Vec3d(this.parts[x].getX(), this.parts[x].getY(), this.parts[x].getZ());
				}

				float y = (float)(this.getSegmentProperties(5, 1.0F)[1] - this.getSegmentProperties(10, 1.0F)[1]) * 10.0F * (float) (Math.PI / 180.0);
				float z = MathHelper.cos(y);
				float aa = MathHelper.sin(y);
				float ab = this.getYaw() * (float) (Math.PI / 180.0);
				float ac = MathHelper.sin(ab);
				float ad = MathHelper.cos(ab);
				this.movePart(this.body, (double)(ac * 0.5F), 0.0, (double)(-ad * 0.5F));
				this.movePart(this.rightWing, (double)(ad * 4.5F), 2.0, (double)(ac * 4.5F));
				this.movePart(this.leftWing, (double)(ad * -4.5F), 2.0, (double)(ac * -4.5F));
				if (!this.world.isClient && this.hurtTime == 0) {
					this.launchLivingEntities(
						this.world
							.getOtherEntities(this, this.rightWing.getBoundingBox().expand(4.0, 2.0, 4.0).offset(0.0, -2.0, 0.0), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR)
					);
					this.launchLivingEntities(
						this.world
							.getOtherEntities(this, this.leftWing.getBoundingBox().expand(4.0, 2.0, 4.0).offset(0.0, -2.0, 0.0), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR)
					);
					this.damageLivingEntities(this.world.getOtherEntities(this, this.head.getBoundingBox().expand(1.0), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR));
					this.damageLivingEntities(this.world.getOtherEntities(this, this.neck.getBoundingBox().expand(1.0), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR));
				}

				float ae = MathHelper.sin(this.getYaw() * (float) (Math.PI / 180.0) - this.yawAcceleration * 0.01F);
				float af = MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0) - this.yawAcceleration * 0.01F);
				float ag = this.getHeadVerticalMovement();
				this.movePart(this.head, (double)(ae * 6.5F * z), (double)(ag + aa * 6.5F), (double)(-af * 6.5F * z));
				this.movePart(this.neck, (double)(ae * 5.5F * z), (double)(ag + aa * 5.5F), (double)(-af * 5.5F * z));
				double[] ds = this.getSegmentProperties(5, 1.0F);

				for (int ah = 0; ah < 3; ah++) {
					EnderDragonPart enderDragonPart = null;
					if (ah == 0) {
						enderDragonPart = this.tail1;
					}

					if (ah == 1) {
						enderDragonPart = this.tail2;
					}

					if (ah == 2) {
						enderDragonPart = this.tail3;
					}

					double[] es = this.getSegmentProperties(12 + ah * 2, 1.0F);
					float ai = this.getYaw() * (float) (Math.PI / 180.0) + this.wrapYawChange(es[0] - ds[0]) * (float) (Math.PI / 180.0);
					float aj = MathHelper.sin(ai);
					float ak = MathHelper.cos(ai);
					float al = 1.5F;
					float am = (float)(ah + 1) * 2.0F;
					this.movePart(enderDragonPart, (double)(-(ac * 1.5F + aj * am) * z), es[1] - ds[1] - (double)((am + 1.5F) * aa) + 1.5, (double)((ad * 1.5F + ak * am) * z));
				}

				if (!this.world.isClient) {
					this.slowedDownByBlock = this.destroyBlocks(this.head.getBoundingBox())
						| this.destroyBlocks(this.neck.getBoundingBox())
						| this.destroyBlocks(this.body.getBoundingBox());
					if (this.fight != null) {
						this.fight.updateFight(this);
					}
				}

				for (int an = 0; an < this.parts.length; an++) {
					this.parts[an].prevX = vec3ds[an].x;
					this.parts[an].prevY = vec3ds[an].y;
					this.parts[an].prevZ = vec3ds[an].z;
					this.parts[an].lastRenderX = vec3ds[an].x;
					this.parts[an].lastRenderY = vec3ds[an].y;
					this.parts[an].lastRenderZ = vec3ds[an].z;
				}
			}
		}
	}

	private void movePart(EnderDragonPart enderDragonPart, double dx, double dy, double dz) {
		enderDragonPart.setPosition(this.getX() + dx, this.getY() + dy, this.getZ() + dz);
	}

	private float getHeadVerticalMovement() {
		if (this.phaseManager.getCurrent().isSittingOrHovering()) {
			return -1.0F;
		} else {
			double[] ds = this.getSegmentProperties(5, 1.0F);
			double[] es = this.getSegmentProperties(0, 1.0F);
			return (float)(ds[1] - es[1]);
		}
	}

	private void tickWithEndCrystals() {
		if (this.connectedCrystal != null) {
			if (this.connectedCrystal.isRemoved()) {
				this.connectedCrystal = null;
			} else if (this.age % 10 == 0 && this.getHealth() < this.getMaxHealth()) {
				this.setHealth(this.getHealth() + 1.0F);
			}
		}

		if (this.random.nextInt(10) == 0) {
			List<EndCrystalEntity> list = this.world.getNonSpectatingEntities(EndCrystalEntity.class, this.getBoundingBox().expand(32.0));
			EndCrystalEntity endCrystalEntity = null;
			double d = Double.MAX_VALUE;

			for (EndCrystalEntity endCrystalEntity2 : list) {
				double e = endCrystalEntity2.squaredDistanceTo(this);
				if (e < d) {
					d = e;
					endCrystalEntity = endCrystalEntity2;
				}
			}

			this.connectedCrystal = endCrystalEntity;
		}
	}

	private void launchLivingEntities(List<Entity> entities) {
		double d = (this.body.getBoundingBox().minX + this.body.getBoundingBox().maxX) / 2.0;
		double e = (this.body.getBoundingBox().minZ + this.body.getBoundingBox().maxZ) / 2.0;

		for (Entity entity : entities) {
			if (entity instanceof LivingEntity) {
				double f = entity.getX() - d;
				double g = entity.getZ() - e;
				double h = Math.max(f * f + g * g, 0.1);
				entity.addVelocity(f / h * 4.0, 0.2F, g / h * 4.0);
				if (!this.phaseManager.getCurrent().isSittingOrHovering() && ((LivingEntity)entity).getLastAttackedTime() < entity.age - 2) {
					entity.damage(DamageSource.mob(this), 5.0F);
					this.applyDamageEffects(this, entity);
				}
			}
		}
	}

	private void damageLivingEntities(List<Entity> entities) {
		for (Entity entity : entities) {
			if (entity instanceof LivingEntity) {
				entity.damage(DamageSource.mob(this), 10.0F);
				this.applyDamageEffects(this, entity);
			}
		}
	}

	private float wrapYawChange(double yawDegrees) {
		return (float)MathHelper.wrapDegrees(yawDegrees);
	}

	private boolean destroyBlocks(Box box) {
		int i = MathHelper.floor(box.minX);
		int j = MathHelper.floor(box.minY);
		int k = MathHelper.floor(box.minZ);
		int l = MathHelper.floor(box.maxX);
		int m = MathHelper.floor(box.maxY);
		int n = MathHelper.floor(box.maxZ);
		boolean bl = false;
		boolean bl2 = false;

		for (int o = i; o <= l; o++) {
			for (int p = j; p <= m; p++) {
				for (int q = k; q <= n; q++) {
					BlockPos blockPos = new BlockPos(o, p, q);
					BlockState blockState = this.world.getBlockState(blockPos);
					if (!blockState.isAir() && blockState.getMaterial() != Material.FIRE) {
						if (this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING) && !blockState.isIn(BlockTags.DRAGON_IMMUNE)) {
							bl2 = this.world.removeBlock(blockPos, false) || bl2;
						} else {
							bl = true;
						}
					}
				}
			}
		}

		if (bl2) {
			BlockPos blockPos2 = new BlockPos(i + this.random.nextInt(l - i + 1), j + this.random.nextInt(m - j + 1), k + this.random.nextInt(n - k + 1));
			this.world.syncWorldEvent(2008, blockPos2, 0);
		}

		return bl;
	}

	public boolean damagePart(EnderDragonPart part, DamageSource source, float amount) {
		if (this.phaseManager.getCurrent().getType() == PhaseType.DYING) {
			return false;
		} else {
			amount = this.phaseManager.getCurrent().modifyDamageTaken(source, amount);
			if (part != this.head) {
				amount = amount / 4.0F + Math.min(amount, 1.0F);
			}

			if (amount < 0.01F) {
				return false;
			} else {
				if (source.getAttacker() instanceof PlayerEntity || source.isExplosive()) {
					float f = this.getHealth();
					this.parentDamage(source, amount);
					if (this.isDead() && !this.phaseManager.getCurrent().isSittingOrHovering()) {
						this.setHealth(1.0F);
						this.phaseManager.setPhase(PhaseType.DYING);
					}

					if (this.phaseManager.getCurrent().isSittingOrHovering()) {
						this.damageDuringSitting = (int)((float)this.damageDuringSitting + (f - this.getHealth()));
						if ((float)this.damageDuringSitting > 0.25F * this.getMaxHealth()) {
							this.damageDuringSitting = 0;
							this.phaseManager.setPhase(PhaseType.TAKEOFF);
						}
					}
				}

				return true;
			}
		}
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (source instanceof EntityDamageSource && ((EntityDamageSource)source).isThorns()) {
			this.damagePart(this.body, source, amount);
		}

		return false;
	}

	protected boolean parentDamage(DamageSource source, float amount) {
		return super.damage(source, amount);
	}

	@Override
	public void kill() {
		this.remove(Entity.RemovalReason.KILLED);
		if (this.fight != null) {
			this.fight.updateFight(this);
			this.fight.dragonKilled(this);
		}
	}

	@Override
	protected void updatePostDeath() {
		if (this.fight != null) {
			this.fight.updateFight(this);
		}

		this.ticksSinceDeath++;
		if (this.ticksSinceDeath >= 180 && this.ticksSinceDeath <= 200) {
			float f = (this.random.nextFloat() - 0.5F) * 8.0F;
			float g = (this.random.nextFloat() - 0.5F) * 4.0F;
			float h = (this.random.nextFloat() - 0.5F) * 8.0F;
			this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.getX() + (double)f, this.getY() + 2.0 + (double)g, this.getZ() + (double)h, 0.0, 0.0, 0.0);
		}

		boolean bl = this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT);
		int i = 500;
		if (this.fight != null && !this.fight.hasPreviouslyKilled()) {
			i = 12000;
		}

		if (this.world instanceof ServerWorld) {
			if (this.ticksSinceDeath > 150 && this.ticksSinceDeath % 5 == 0 && bl) {
				ExperienceOrbEntity.spawn((ServerWorld)this.world, this.getPos(), MathHelper.floor((float)i * 0.08F));
			}

			if (this.ticksSinceDeath == 1 && !this.isSilent()) {
				this.world.syncGlobalEvent(1028, this.getBlockPos(), 0);
			}
		}

		this.move(MovementType.SELF, new Vec3d(0.0, 0.1F, 0.0));
		this.setYaw(this.getYaw() + 20.0F);
		this.bodyYaw = this.getYaw();
		if (this.ticksSinceDeath == 200 && this.world instanceof ServerWorld) {
			if (bl) {
				ExperienceOrbEntity.spawn((ServerWorld)this.world, this.getPos(), MathHelper.floor((float)i * 0.2F));
			}

			if (this.fight != null) {
				this.fight.dragonKilled(this);
			}

			this.remove(Entity.RemovalReason.KILLED);
		}
	}

	public int getNearestPathNodeIndex() {
		if (this.pathNodes[0] == null) {
			for (int i = 0; i < 24; i++) {
				int j = 5;
				int l;
				int m;
				if (i < 12) {
					l = MathHelper.floor(60.0F * MathHelper.cos(2.0F * ((float) -Math.PI + (float) (Math.PI / 12) * (float)i)));
					m = MathHelper.floor(60.0F * MathHelper.sin(2.0F * ((float) -Math.PI + (float) (Math.PI / 12) * (float)i)));
				} else if (i < 20) {
					int k = i - 12;
					l = MathHelper.floor(40.0F * MathHelper.cos(2.0F * ((float) -Math.PI + (float) (Math.PI / 8) * (float)k)));
					m = MathHelper.floor(40.0F * MathHelper.sin(2.0F * ((float) -Math.PI + (float) (Math.PI / 8) * (float)k)));
					j += 10;
				} else {
					int var7 = i - 20;
					l = MathHelper.floor(20.0F * MathHelper.cos(2.0F * ((float) -Math.PI + (float) (Math.PI / 4) * (float)var7)));
					m = MathHelper.floor(20.0F * MathHelper.sin(2.0F * ((float) -Math.PI + (float) (Math.PI / 4) * (float)var7)));
				}

				int r = Math.max(this.world.getSeaLevel() + 10, this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(l, 0, m)).getY() + j);
				this.pathNodes[i] = new PathNode(l, r, m);
			}

			this.pathNodeConnections[0] = 6146;
			this.pathNodeConnections[1] = 8197;
			this.pathNodeConnections[2] = 8202;
			this.pathNodeConnections[3] = 16404;
			this.pathNodeConnections[4] = 32808;
			this.pathNodeConnections[5] = 32848;
			this.pathNodeConnections[6] = 65696;
			this.pathNodeConnections[7] = 131392;
			this.pathNodeConnections[8] = 131712;
			this.pathNodeConnections[9] = 263424;
			this.pathNodeConnections[10] = 526848;
			this.pathNodeConnections[11] = 525313;
			this.pathNodeConnections[12] = 1581057;
			this.pathNodeConnections[13] = 3166214;
			this.pathNodeConnections[14] = 2138120;
			this.pathNodeConnections[15] = 6373424;
			this.pathNodeConnections[16] = 4358208;
			this.pathNodeConnections[17] = 12910976;
			this.pathNodeConnections[18] = 9044480;
			this.pathNodeConnections[19] = 9706496;
			this.pathNodeConnections[20] = 15216640;
			this.pathNodeConnections[21] = 13688832;
			this.pathNodeConnections[22] = 11763712;
			this.pathNodeConnections[23] = 8257536;
		}

		return this.getNearestPathNodeIndex(this.getX(), this.getY(), this.getZ());
	}

	public int getNearestPathNodeIndex(double x, double y, double z) {
		float f = 10000.0F;
		int i = 0;
		PathNode pathNode = new PathNode(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
		int j = 0;
		if (this.fight == null || this.fight.getAliveEndCrystals() == 0) {
			j = 12;
		}

		for (int k = j; k < 24; k++) {
			if (this.pathNodes[k] != null) {
				float g = this.pathNodes[k].getSquaredDistance(pathNode);
				if (g < f) {
					f = g;
					i = k;
				}
			}
		}

		return i;
	}

	@Nullable
	public Path findPath(int from, int to, @Nullable PathNode pathNode) {
		for (int i = 0; i < 24; i++) {
			PathNode pathNode2 = this.pathNodes[i];
			pathNode2.visited = false;
			pathNode2.heapWeight = 0.0F;
			pathNode2.penalizedPathLength = 0.0F;
			pathNode2.distanceToNearestTarget = 0.0F;
			pathNode2.previous = null;
			pathNode2.heapIndex = -1;
		}

		PathNode pathNode3 = this.pathNodes[from];
		PathNode pathNode4 = this.pathNodes[to];
		pathNode3.penalizedPathLength = 0.0F;
		pathNode3.distanceToNearestTarget = pathNode3.getDistance(pathNode4);
		pathNode3.heapWeight = pathNode3.distanceToNearestTarget;
		this.pathHeap.clear();
		this.pathHeap.push(pathNode3);
		PathNode pathNode5 = pathNode3;
		int j = 0;
		if (this.fight == null || this.fight.getAliveEndCrystals() == 0) {
			j = 12;
		}

		while (!this.pathHeap.isEmpty()) {
			PathNode pathNode6 = this.pathHeap.pop();
			if (pathNode6.equals(pathNode4)) {
				if (pathNode != null) {
					pathNode.previous = pathNode4;
					pathNode4 = pathNode;
				}

				return this.getPathOfAllPredecessors(pathNode3, pathNode4);
			}

			if (pathNode6.getDistance(pathNode4) < pathNode5.getDistance(pathNode4)) {
				pathNode5 = pathNode6;
			}

			pathNode6.visited = true;
			int k = 0;

			for (int l = 0; l < 24; l++) {
				if (this.pathNodes[l] == pathNode6) {
					k = l;
					break;
				}
			}

			for (int m = j; m < 24; m++) {
				if ((this.pathNodeConnections[k] & 1 << m) > 0) {
					PathNode pathNode7 = this.pathNodes[m];
					if (!pathNode7.visited) {
						float f = pathNode6.penalizedPathLength + pathNode6.getDistance(pathNode7);
						if (!pathNode7.isInHeap() || f < pathNode7.penalizedPathLength) {
							pathNode7.previous = pathNode6;
							pathNode7.penalizedPathLength = f;
							pathNode7.distanceToNearestTarget = pathNode7.getDistance(pathNode4);
							if (pathNode7.isInHeap()) {
								this.pathHeap.setNodeWeight(pathNode7, pathNode7.penalizedPathLength + pathNode7.distanceToNearestTarget);
							} else {
								pathNode7.heapWeight = pathNode7.penalizedPathLength + pathNode7.distanceToNearestTarget;
								this.pathHeap.push(pathNode7);
							}
						}
					}
				}
			}
		}

		if (pathNode5 == pathNode3) {
			return null;
		} else {
			LOGGER.debug("Failed to find path from {} to {}", from, to);
			if (pathNode != null) {
				pathNode.previous = pathNode5;
				pathNode5 = pathNode;
			}

			return this.getPathOfAllPredecessors(pathNode3, pathNode5);
		}
	}

	private Path getPathOfAllPredecessors(PathNode unused, PathNode node) {
		List<PathNode> list = Lists.newArrayList();
		PathNode pathNode = node;
		list.add(0, node);

		while (pathNode.previous != null) {
			pathNode = pathNode.previous;
			list.add(0, pathNode);
		}

		return new Path(list, new BlockPos(node.x, node.y, node.z), true);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("DragonPhase", this.phaseManager.getCurrent().getType().getTypeId());
		nbt.putInt("DragonDeathTime", this.ticksSinceDeath);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("DragonPhase")) {
			this.phaseManager.setPhase(PhaseType.getFromId(nbt.getInt("DragonPhase")));
		}

		if (nbt.contains("DragonDeathTime")) {
			this.ticksSinceDeath = nbt.getInt("DragonDeathTime");
		}
	}

	@Override
	public void checkDespawn() {
	}

	public EnderDragonPart[] getBodyParts() {
		return this.parts;
	}

	@Override
	public boolean collides() {
		return false;
	}

	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.HOSTILE;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_ENDER_DRAGON_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_ENDER_DRAGON_HURT;
	}

	@Override
	protected float getSoundVolume() {
		return 5.0F;
	}

	public float getChangeInNeckPitch(int segmentOffset, double[] segment1, double[] segment2) {
		Phase phase = this.phaseManager.getCurrent();
		PhaseType<? extends Phase> phaseType = phase.getType();
		double e;
		if (phaseType == PhaseType.LANDING || phaseType == PhaseType.TAKEOFF) {
			BlockPos blockPos = this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPortalFeature.ORIGIN);
			double d = Math.max(Math.sqrt(blockPos.getSquaredDistance(this.getPos(), true)) / 4.0, 1.0);
			e = (double)segmentOffset / d;
		} else if (phase.isSittingOrHovering()) {
			e = (double)segmentOffset;
		} else if (segmentOffset == 6) {
			e = 0.0;
		} else {
			e = segment2[1] - segment1[1];
		}

		return (float)e;
	}

	public Vec3d getRotationVectorFromPhase(float tickDelta) {
		Phase phase = this.phaseManager.getCurrent();
		PhaseType<? extends Phase> phaseType = phase.getType();
		Vec3d vec3d;
		if (phaseType == PhaseType.LANDING || phaseType == PhaseType.TAKEOFF) {
			BlockPos blockPos = this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPortalFeature.ORIGIN);
			float f = Math.max((float)Math.sqrt(blockPos.getSquaredDistance(this.getPos(), true)) / 4.0F, 1.0F);
			float g = 6.0F / f;
			float h = this.getPitch();
			float i = 1.5F;
			this.setPitch(-g * 1.5F * 5.0F);
			vec3d = this.getRotationVec(tickDelta);
			this.setPitch(h);
		} else if (phase.isSittingOrHovering()) {
			float j = this.getPitch();
			float k = 1.5F;
			this.setPitch(-45.0F);
			vec3d = this.getRotationVec(tickDelta);
			this.setPitch(j);
		} else {
			vec3d = this.getRotationVec(tickDelta);
		}

		return vec3d;
	}

	public void crystalDestroyed(EndCrystalEntity crystal, BlockPos pos, DamageSource source) {
		PlayerEntity playerEntity;
		if (source.getAttacker() instanceof PlayerEntity) {
			playerEntity = (PlayerEntity)source.getAttacker();
		} else {
			playerEntity = this.world.getClosestPlayer(CLOSE_PLAYER_PREDICATE, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
		}

		if (crystal == this.connectedCrystal) {
			this.damagePart(this.head, DamageSource.explosion(playerEntity), 10.0F);
		}

		this.phaseManager.getCurrent().crystalDestroyed(crystal, pos, source, playerEntity);
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (PHASE_TYPE.equals(data) && this.world.isClient) {
			this.phaseManager.setPhase(PhaseType.getFromId(this.getDataTracker().get(PHASE_TYPE)));
		}

		super.onTrackedDataSet(data);
	}

	public PhaseManager getPhaseManager() {
		return this.phaseManager;
	}

	@Nullable
	public EnderDragonFight getFight() {
		return this.fight;
	}

	@Override
	public boolean addStatusEffect(StatusEffectInstance effect, @Nullable Entity source) {
		return false;
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
	public void readFromPacket(MobSpawnS2CPacket packet) {
		super.readFromPacket(packet);
		EnderDragonPart[] enderDragonParts = this.getBodyParts();

		for (int i = 0; i < enderDragonParts.length; i++) {
			enderDragonParts[i].setId(i + packet.getId());
		}
	}

	@Override
	public boolean canTarget(LivingEntity target) {
		return target.canTakeDamage();
	}
}
