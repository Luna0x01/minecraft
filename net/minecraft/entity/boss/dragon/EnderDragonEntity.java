package net.minecraft.entity.boss.dragon;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.dragon.class_2987;
import net.minecraft.dragon.class_2993;
import net.minecraft.dragon.class_2994;
import net.minecraft.entity.EndCrystalEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MultipartEntityProvider;
import net.minecraft.entity.ai.class_2769;
import net.minecraft.entity.ai.pathing.PathMinHeap;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.DragonRespawnAnimation;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.dimension.TheEndDimension;
import net.minecraft.world.gen.feature.EndExitPortalFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EnderDragonEntity extends MobEntity implements MultipartEntityProvider, Monster {
	private static final Logger field_14662 = LogManager.getLogger();
	public static final TrackedData<Integer> field_14661 = DataTracker.registerData(EnderDragonEntity.class, TrackedDataHandlerRegistry.INTEGER);
	public double[][] segmentCircularBuffer = new double[64][3];
	public int latestSegment = -1;
	public EnderDragonPart[] parts;
	public EnderDragonPart partHead;
	public EnderDragonPart field_14670;
	public EnderDragonPart partBody;
	public EnderDragonPart partTail1;
	public EnderDragonPart partTail2;
	public EnderDragonPart partTail3;
	public EnderDragonPart partWingRight;
	public EnderDragonPart partWingLeft;
	public float prevWingPosition;
	public float wingPosition;
	public boolean field_3745;
	public int field_3746;
	public EndCrystalEntity connectedCrystal;
	private final DragonRespawnAnimation field_14663;
	private final class_2994 field_14664;
	private int field_14665 = 200;
	private int field_14666;
	private final PathNode[] field_14667 = new PathNode[24];
	private final int[] field_14668 = new int[24];
	private final class_2769 field_14669 = new class_2769();

	public EnderDragonEntity(World world) {
		super(world);
		this.parts = new EnderDragonPart[]{
			this.partHead = new EnderDragonPart(this, "head", 6.0F, 6.0F),
			this.field_14670 = new EnderDragonPart(this, "neck", 6.0F, 6.0F),
			this.partBody = new EnderDragonPart(this, "body", 8.0F, 8.0F),
			this.partTail1 = new EnderDragonPart(this, "tail", 4.0F, 4.0F),
			this.partTail2 = new EnderDragonPart(this, "tail", 4.0F, 4.0F),
			this.partTail3 = new EnderDragonPart(this, "tail", 4.0F, 4.0F),
			this.partWingRight = new EnderDragonPart(this, "wing", 4.0F, 4.0F),
			this.partWingLeft = new EnderDragonPart(this, "wing", 4.0F, 4.0F)
		};
		this.setHealth(this.getMaxHealth());
		this.setBounds(16.0F, 8.0F);
		this.noClip = true;
		this.isFireImmune = true;
		this.field_14665 = 100;
		this.ignoreCameraFrustum = true;
		if (!world.isClient && world.dimension instanceof TheEndDimension) {
			this.field_14663 = ((TheEndDimension)world.dimension).method_11818();
		} else {
			this.field_14663 = null;
		}

		this.field_14664 = new class_2994(this);
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(200.0);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.getDataTracker().startTracking(field_14661, class_2993.HOVER.method_13200());
	}

	public double[] getSegmentProperties(int segmentNumber, float tickDelta) {
		if (this.getHealth() <= 0.0F) {
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
		ds[2] = this.segmentCircularBuffer[i][2] + (this.segmentCircularBuffer[j][2] - this.segmentCircularBuffer[i][2]) * (double)tickDelta;
		return ds;
	}

	@Override
	public void tickMovement() {
		if (this.world.isClient) {
			this.setHealth(this.getHealth());
			if (!this.isSilent()) {
				float f = MathHelper.cos(this.wingPosition * (float) (Math.PI * 2));
				float g = MathHelper.cos(this.prevWingPosition * (float) (Math.PI * 2));
				if (g <= -0.3F && f >= -0.3F) {
					this.world.playSound(this.x, this.y, this.z, Sounds.ENTITY_ENDERDRAGON_FLAP, this.getSoundCategory(), 5.0F, 0.8F + this.random.nextFloat() * 0.3F, false);
				}

				if (!this.field_14664.method_13202().method_13179() && --this.field_14665 < 0) {
					this.world.playSound(this.x, this.y, this.z, Sounds.ENTITY_ENDERDRAGON_GROWL, this.getSoundCategory(), 2.5F, 0.8F + this.random.nextFloat() * 0.3F, false);
					this.field_14665 = 200 + this.random.nextInt(200);
				}
			}
		}

		this.prevWingPosition = this.wingPosition;
		if (this.getHealth() <= 0.0F) {
			float h = (this.random.nextFloat() - 0.5F) * 8.0F;
			float i = (this.random.nextFloat() - 0.5F) * 4.0F;
			float j = (this.random.nextFloat() - 0.5F) * 8.0F;
			this.world.addParticle(ParticleType.LARGE_EXPLOSION, this.x + (double)h, this.y + 2.0 + (double)i, this.z + (double)j, 0.0, 0.0, 0.0);
		} else {
			this.tickWithEndCrystals();
			float k = 0.2F / (MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ) * 10.0F + 1.0F);
			k *= (float)Math.pow(2.0, this.velocityY);
			if (this.field_14664.method_13202().method_13179()) {
				this.wingPosition += 0.1F;
			} else if (this.field_3745) {
				this.wingPosition += k * 0.5F;
			} else {
				this.wingPosition += k;
			}

			this.yaw = MathHelper.wrapDegrees(this.yaw);
			if (this.hasNoAi()) {
				this.wingPosition = 0.5F;
			} else {
				if (this.latestSegment < 0) {
					for (int l = 0; l < this.segmentCircularBuffer.length; l++) {
						this.segmentCircularBuffer[l][0] = (double)this.yaw;
						this.segmentCircularBuffer[l][1] = this.y;
					}
				}

				if (++this.latestSegment == this.segmentCircularBuffer.length) {
					this.latestSegment = 0;
				}

				this.segmentCircularBuffer[this.latestSegment][0] = (double)this.yaw;
				this.segmentCircularBuffer[this.latestSegment][1] = this.y;
				if (this.world.isClient) {
					if (this.bodyTrackingIncrements > 0) {
						double d = this.x + (this.serverPitch - this.x) / (double)this.bodyTrackingIncrements;
						double e = this.y + (this.serverY - this.y) / (double)this.bodyTrackingIncrements;
						double m = this.z + (this.serverZ - this.z) / (double)this.bodyTrackingIncrements;
						double n = MathHelper.wrapDegrees(this.serverYaw - (double)this.yaw);
						this.yaw = (float)((double)this.yaw + n / (double)this.bodyTrackingIncrements);
						this.pitch = (float)((double)this.pitch + (this.serverX - (double)this.pitch) / (double)this.bodyTrackingIncrements);
						this.bodyTrackingIncrements--;
						this.updatePosition(d, e, m);
						this.setRotation(this.yaw, this.pitch);
					}

					this.field_14664.method_13202().method_13182();
				} else {
					class_2987 lv = this.field_14664.method_13202();
					lv.method_13183();
					if (this.field_14664.method_13202() != lv) {
						lv = this.field_14664.method_13202();
						lv.method_13183();
					}

					Vec3d vec3d = lv.method_13187();
					if (vec3d != null) {
						double o = vec3d.x - this.x;
						double p = vec3d.y - this.y;
						double q = vec3d.z - this.z;
						double r = o * o + p * p + q * q;
						float s = lv.method_13186();
						p = MathHelper.clamp(p / (double)MathHelper.sqrt(o * o + q * q), (double)(-s), (double)s);
						this.velocityY += p * 0.1F;
						this.yaw = MathHelper.wrapDegrees(this.yaw);
						double t = MathHelper.clamp(MathHelper.wrapDegrees(180.0 - MathHelper.atan2(o, q) * 180.0F / (float)Math.PI - (double)this.yaw), -50.0, 50.0);
						Vec3d vec3d2 = new Vec3d(vec3d.x - this.x, vec3d.y - this.y, vec3d.z - this.z).normalize();
						Vec3d vec3d3 = new Vec3d(
								(double)MathHelper.sin(this.yaw * (float) (Math.PI / 180.0)), this.velocityY, (double)(-MathHelper.cos(this.yaw * (float) (Math.PI / 180.0)))
							)
							.normalize();
						float u = Math.max(((float)vec3d3.dotProduct(vec3d2) + 0.5F) / 1.5F, 0.0F);
						this.field_6782 *= 0.8F;
						this.field_6782 = (float)((double)this.field_6782 + t * (double)lv.method_13188());
						this.yaw = this.yaw + this.field_6782 * 0.1F;
						float v = (float)(2.0 / (r + 1.0));
						float w = 0.06F;
						this.updateVelocity(0.0F, -1.0F, w * (u * v + (1.0F - v)));
						if (this.field_3745) {
							this.move(this.velocityX * 0.8F, this.velocityY * 0.8F, this.velocityZ * 0.8F);
						} else {
							this.move(this.velocityX, this.velocityY, this.velocityZ);
						}

						Vec3d vec3d4 = new Vec3d(this.velocityX, this.velocityY, this.velocityZ).normalize();
						float x = ((float)vec3d4.dotProduct(vec3d3) + 1.0F) / 2.0F;
						x = 0.8F + 0.15F * x;
						this.velocityX *= (double)x;
						this.velocityZ *= (double)x;
						this.velocityY *= 0.91F;
					}
				}

				this.bodyYaw = this.yaw;
				this.partHead.width = this.partHead.height = 1.0F;
				this.field_14670.width = this.field_14670.height = 3.0F;
				this.partTail1.width = this.partTail1.height = 2.0F;
				this.partTail2.width = this.partTail2.height = 2.0F;
				this.partTail3.width = this.partTail3.height = 2.0F;
				this.partBody.height = 3.0F;
				this.partBody.width = 5.0F;
				this.partWingRight.height = 2.0F;
				this.partWingRight.width = 4.0F;
				this.partWingLeft.height = 3.0F;
				this.partWingLeft.width = 4.0F;
				float y = (float)(this.getSegmentProperties(5, 1.0F)[1] - this.getSegmentProperties(10, 1.0F)[1]) * 10.0F * (float) (Math.PI / 180.0);
				float z = MathHelper.cos(y);
				float aa = MathHelper.sin(y);
				float ab = this.yaw * (float) (Math.PI / 180.0);
				float ac = MathHelper.sin(ab);
				float ad = MathHelper.cos(ab);
				this.partBody.tick();
				this.partBody.refreshPositionAndAngles(this.x + (double)(ac * 0.5F), this.y, this.z - (double)(ad * 0.5F), 0.0F, 0.0F);
				this.partWingRight.tick();
				this.partWingRight.refreshPositionAndAngles(this.x + (double)(ad * 4.5F), this.y + 2.0, this.z + (double)(ac * 4.5F), 0.0F, 0.0F);
				this.partWingLeft.tick();
				this.partWingLeft.refreshPositionAndAngles(this.x - (double)(ad * 4.5F), this.y + 2.0, this.z - (double)(ac * 4.5F), 0.0F, 0.0F);
				if (!this.world.isClient && this.hurtTime == 0) {
					this.launchLivingEntities(this.world.getEntitiesIn(this, this.partWingRight.getBoundingBox().expand(4.0, 2.0, 4.0).offset(0.0, -2.0, 0.0)));
					this.launchLivingEntities(this.world.getEntitiesIn(this, this.partWingLeft.getBoundingBox().expand(4.0, 2.0, 4.0).offset(0.0, -2.0, 0.0)));
					this.damageLivingEntities(this.world.getEntitiesIn(this, this.partHead.getBoundingBox().expand(1.0)));
					this.damageLivingEntities(this.world.getEntitiesIn(this, this.field_14670.getBoundingBox().expand(1.0)));
				}

				double[] ds = this.getSegmentProperties(5, 1.0F);
				float ae = MathHelper.sin(this.yaw * (float) (Math.PI / 180.0) - this.field_6782 * 0.01F);
				float af = MathHelper.cos(this.yaw * (float) (Math.PI / 180.0) - this.field_6782 * 0.01F);
				this.partHead.tick();
				this.field_14670.tick();
				float ag = this.method_13172(1.0F);
				this.partHead
					.refreshPositionAndAngles(this.x + (double)(ae * 6.5F * z), this.y + (double)ag + (double)(aa * 6.5F), this.z - (double)(af * 6.5F * z), 0.0F, 0.0F);
				this.field_14670
					.refreshPositionAndAngles(this.x + (double)(ae * 5.5F * z), this.y + (double)ag + (double)(aa * 5.5F), this.z - (double)(af * 5.5F * z), 0.0F, 0.0F);

				for (int ah = 0; ah < 3; ah++) {
					EnderDragonPart enderDragonPart = null;
					if (ah == 0) {
						enderDragonPart = this.partTail1;
					}

					if (ah == 1) {
						enderDragonPart = this.partTail2;
					}

					if (ah == 2) {
						enderDragonPart = this.partTail3;
					}

					double[] es = this.getSegmentProperties(12 + ah * 2, 1.0F);
					float ai = this.yaw * (float) (Math.PI / 180.0) + this.wrapYawChange(es[0] - ds[0]) * (float) (Math.PI / 180.0);
					float aj = MathHelper.sin(ai);
					float ak = MathHelper.cos(ai);
					float al = 1.5F;
					float am = (float)(ah + 1) * 2.0F;
					enderDragonPart.tick();
					enderDragonPart.refreshPositionAndAngles(
						this.x - (double)((ac * al + aj * am) * z),
						this.y + (es[1] - ds[1]) - (double)((am + al) * aa) + 1.5,
						this.z + (double)((ad * al + ak * am) * z),
						0.0F,
						0.0F
					);
				}

				if (!this.world.isClient) {
					this.field_3745 = this.destroyBlocks(this.partHead.getBoundingBox())
						| this.destroyBlocks(this.field_14670.getBoundingBox())
						| this.destroyBlocks(this.partBody.getBoundingBox());
					if (this.field_14663 != null) {
						this.field_14663.method_11806(this);
					}
				}
			}
		}
	}

	private float method_13172(float f) {
		double d = 0.0;
		if (this.field_14664.method_13202().method_13179()) {
			d = -1.0;
		} else {
			double[] ds = this.getSegmentProperties(5, 1.0F);
			double[] es = this.getSegmentProperties(0, 1.0F);
			d = ds[1] - es[0];
		}

		return (float)d;
	}

	private void tickWithEndCrystals() {
		if (this.connectedCrystal != null) {
			if (this.connectedCrystal.removed) {
				this.connectedCrystal = null;
			} else if (this.ticksAlive % 10 == 0 && this.getHealth() < this.getMaxHealth()) {
				this.setHealth(this.getHealth() + 1.0F);
			}
		}

		if (this.random.nextInt(10) == 0) {
			List<EndCrystalEntity> list = this.world.getEntitiesInBox(EndCrystalEntity.class, this.getBoundingBox().expand(32.0));
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
		double d = (this.partBody.getBoundingBox().minX + this.partBody.getBoundingBox().maxX) / 2.0;
		double e = (this.partBody.getBoundingBox().minZ + this.partBody.getBoundingBox().maxZ) / 2.0;

		for (Entity entity : entities) {
			if (entity instanceof LivingEntity) {
				double f = entity.x - d;
				double g = entity.z - e;
				double h = f * f + g * g;
				entity.addVelocity(f / h * 4.0, 0.2F, g / h * 4.0);
				if (!this.field_14664.method_13202().method_13179() && ((LivingEntity)entity).getLastHurtTimestamp() < entity.ticksAlive - 2) {
					entity.damage(DamageSource.mob(this), 5.0F);
					this.dealDamage(this, entity);
				}
			}
		}
	}

	private void damageLivingEntities(List<Entity> entities) {
		for (int i = 0; i < entities.size(); i++) {
			Entity entity = (Entity)entities.get(i);
			if (entity instanceof LivingEntity) {
				entity.damage(DamageSource.mob(this), 10.0F);
				this.dealDamage(this, entity);
			}
		}
	}

	private float wrapYawChange(double yawDegrees) {
		return (float)MathHelper.wrapDegrees(yawDegrees);
	}

	private boolean destroyBlocks(Box area) {
		int i = MathHelper.floor(area.minX);
		int j = MathHelper.floor(area.minY);
		int k = MathHelper.floor(area.minZ);
		int l = MathHelper.floor(area.maxX);
		int m = MathHelper.floor(area.maxY);
		int n = MathHelper.floor(area.maxZ);
		boolean bl = false;
		boolean bl2 = false;

		for (int o = i; o <= l; o++) {
			for (int p = j; p <= m; p++) {
				for (int q = k; q <= n; q++) {
					BlockPos blockPos = new BlockPos(o, p, q);
					BlockState blockState = this.world.getBlockState(blockPos);
					Block block = blockState.getBlock();
					if (blockState.getMaterial() != Material.AIR && blockState.getMaterial() != Material.FIRE) {
						if (!this.world.getGameRules().getBoolean("mobGriefing")) {
							bl = true;
						} else if (block == Blocks.BARRIER
							|| block == Blocks.OBSIDIAN
							|| block == Blocks.END_STONE
							|| block == Blocks.BEDROCK
							|| block == Blocks.END_PORTAL
							|| block == Blocks.END_PORTAL_FRAME) {
							bl = true;
						} else if (block != Blocks.COMMAND_BLOCK
							&& block != Blocks.REPEATING_COMMAND_BLOCK
							&& block != Blocks.CHAIN_COMMAND_BLOCK
							&& block != Blocks.IRON_BARS
							&& block != Blocks.END_GATEWAY) {
							bl2 = this.world.setAir(blockPos) || bl2;
						} else {
							bl = true;
						}
					}
				}
			}
		}

		if (bl2) {
			double d = area.minX + (area.maxX - area.minX) * (double)this.random.nextFloat();
			double e = area.minY + (area.maxY - area.minY) * (double)this.random.nextFloat();
			double f = area.minZ + (area.maxZ - area.minZ) * (double)this.random.nextFloat();
			this.world.addParticle(ParticleType.LARGE_EXPLOSION, d, e, f, 0.0, 0.0, 0.0);
		}

		return bl;
	}

	@Override
	public boolean setAngry(EnderDragonPart multipart, DamageSource source, float angry) {
		angry = this.field_14664.method_13202().method_13180(multipart, source, angry);
		if (multipart != this.partHead) {
			angry = angry / 4.0F + Math.min(angry, 1.0F);
		}

		if (angry < 0.01F) {
			return false;
		} else {
			if (source.getAttacker() instanceof PlayerEntity || source.isExplosive()) {
				float f = this.getHealth();
				this.method_6302(source, angry);
				if (this.getHealth() <= 0.0F && !this.field_14664.method_13202().method_13179()) {
					this.setHealth(1.0F);
					this.field_14664.method_13203(class_2993.DYING);
				}

				if (this.field_14664.method_13202().method_13179()) {
					this.field_14666 = (int)((float)this.field_14666 + (f - this.getHealth()));
					if ((float)this.field_14666 > 0.25F * this.getMaxHealth()) {
						this.field_14666 = 0;
						this.field_14664.method_13203(class_2993.TAKEOFF);
					}
				}
			}

			return true;
		}
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (source instanceof EntityDamageSource && ((EntityDamageSource)source).isThorns()) {
			this.setAngry(this.partBody, source, amount);
		}

		return false;
	}

	protected boolean method_6302(DamageSource damageSource, float f) {
		return super.damage(damageSource, f);
	}

	@Override
	public void kill() {
		this.remove();
		if (this.field_14663 != null) {
			this.field_14663.method_11806(this);
			this.field_14663.method_11803(this);
		}
	}

	@Override
	protected void dropXp() {
		if (this.field_14663 != null) {
			this.field_14663.method_11806(this);
		}

		this.field_3746++;
		if (this.field_3746 >= 180 && this.field_3746 <= 200) {
			float f = (this.random.nextFloat() - 0.5F) * 8.0F;
			float g = (this.random.nextFloat() - 0.5F) * 4.0F;
			float h = (this.random.nextFloat() - 0.5F) * 8.0F;
			this.world.addParticle(ParticleType.HUGE_EXPLOSION, this.x + (double)f, this.y + 2.0 + (double)g, this.z + (double)h, 0.0, 0.0, 0.0);
		}

		boolean bl = this.world.getGameRules().getBoolean("doMobLoot");
		int i = 500;
		if (this.field_14663 != null && !this.field_14663.wasDragonKilled()) {
			i = 12000;
		}

		if (!this.world.isClient) {
			if (this.field_3746 > 150 && this.field_3746 % 5 == 0 && bl) {
				this.method_13163(MathHelper.floor((float)i * 0.08F));
			}

			if (this.field_3746 == 1) {
				this.world.method_4689(1028, new BlockPos(this), 0);
			}
		}

		this.move(0.0, 0.1F, 0.0);
		this.bodyYaw = this.yaw += 20.0F;
		if (this.field_3746 == 200 && !this.world.isClient) {
			if (bl) {
				this.method_13163(MathHelper.floor((float)i * 0.2F));
			}

			if (this.field_14663 != null) {
				this.field_14663.method_11803(this);
			}

			this.remove();
		}
	}

	private void method_13163(int i) {
		while (i > 0) {
			int j = ExperienceOrbEntity.roundToOrbSize(i);
			i -= j;
			this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.x, this.y, this.z, j));
		}
	}

	public int method_13171() {
		if (this.field_14667[0] == null) {
			int i = 0;
			int j = 0;
			int k = 0;
			int l = 0;

			for (int m = 0; m < 24; m++) {
				int n = 5;
				if (m < 12) {
					i = (int)(60.0F * MathHelper.cos(2.0F * ((float) -Math.PI + (float) (Math.PI / 12) * (float)m)));
					k = (int)(60.0F * MathHelper.sin(2.0F * ((float) -Math.PI + (float) (Math.PI / 12) * (float)m)));
				} else if (m < 20) {
					l = m - 12;
					i = (int)(40.0F * MathHelper.cos(2.0F * ((float) -Math.PI + (float) (Math.PI / 8) * (float)l)));
					k = (int)(40.0F * MathHelper.sin(2.0F * ((float) -Math.PI + (float) (Math.PI / 8) * (float)l)));
					n += 10;
				} else {
					l = m - 20;
					i = (int)(20.0F * MathHelper.cos(2.0F * ((float) -Math.PI + (float) (Math.PI / 4) * (float)l)));
					k = (int)(20.0F * MathHelper.sin(2.0F * ((float) -Math.PI + (float) (Math.PI / 4) * (float)l)));
				}

				j = Math.max(this.world.getSeaLevel() + 10, this.world.getTopPosition(new BlockPos(i, 0, k)).getY() + n);
				this.field_14667[m] = new PathNode(i, j, k);
			}

			this.field_14668[0] = 6146;
			this.field_14668[1] = 8197;
			this.field_14668[2] = 8202;
			this.field_14668[3] = 16404;
			this.field_14668[4] = 32808;
			this.field_14668[5] = 32848;
			this.field_14668[6] = 65696;
			this.field_14668[7] = 131392;
			this.field_14668[8] = 131712;
			this.field_14668[9] = 263424;
			this.field_14668[10] = 526848;
			this.field_14668[11] = 525313;
			this.field_14668[12] = 1581057;
			this.field_14668[13] = 3166214;
			this.field_14668[14] = 2138120;
			this.field_14668[15] = 6373424;
			this.field_14668[16] = 4358208;
			this.field_14668[17] = 12910976;
			this.field_14668[18] = 9044480;
			this.field_14668[19] = 9706496;
			this.field_14668[20] = 15216640;
			this.field_14668[21] = 13688832;
			this.field_14668[22] = 11763712;
			this.field_14668[23] = 8257536;
		}

		return this.method_13170(this.x, this.y, this.z);
	}

	public int method_13170(double d, double e, double f) {
		float g = 10000.0F;
		int i = 0;
		PathNode pathNode = new PathNode(MathHelper.floor(d), MathHelper.floor(e), MathHelper.floor(f));
		int j = 0;
		if (this.field_14663 == null || this.field_14663.getAliveCrystals() == 0) {
			j = 12;
		}

		for (int k = j; k < 24; k++) {
			if (this.field_14667[k] != null) {
				float h = this.field_14667[k].getSquaredDistance(pathNode);
				if (h < g) {
					g = h;
					i = k;
				}
			}
		}

		return i;
	}

	@Nullable
	public PathMinHeap method_13164(int i, int j, @Nullable PathNode pathNode) {
		for (int k = 0; k < 24; k++) {
			PathNode pathNode2 = this.field_14667[k];
			pathNode2.visited = false;
			pathNode2.heapWeight = 0.0F;
			pathNode2.penalizedPathLength = 0.0F;
			pathNode2.distanceToNearestTarget = 0.0F;
			pathNode2.previous = null;
			pathNode2.heapIndex = -1;
		}

		PathNode pathNode3 = this.field_14667[i];
		PathNode pathNode4 = this.field_14667[j];
		pathNode3.penalizedPathLength = 0.0F;
		pathNode3.distanceToNearestTarget = pathNode3.getDistance(pathNode4);
		pathNode3.heapWeight = pathNode3.distanceToNearestTarget;
		this.field_14669.method_11899();
		this.field_14669.method_11901(pathNode3);
		PathNode pathNode5 = pathNode3;
		int l = 0;
		if (this.field_14663 == null || this.field_14663.getAliveCrystals() == 0) {
			l = 12;
		}

		while (!this.field_14669.method_11905()) {
			PathNode pathNode6 = this.field_14669.method_11904();
			if (pathNode6.equals(pathNode4)) {
				if (pathNode != null) {
					pathNode.previous = pathNode4;
					pathNode4 = pathNode;
				}

				return this.method_13166(pathNode3, pathNode4);
			}

			if (pathNode6.getDistance(pathNode4) < pathNode5.getDistance(pathNode4)) {
				pathNode5 = pathNode6;
			}

			pathNode6.visited = true;
			int m = 0;

			for (int n = 0; n < 24; n++) {
				if (this.field_14667[n] == pathNode6) {
					m = n;
					break;
				}
			}

			for (int o = l; o < 24; o++) {
				if ((this.field_14668[m] & 1 << o) > 0) {
					PathNode pathNode7 = this.field_14667[o];
					if (!pathNode7.visited) {
						float f = pathNode6.penalizedPathLength + pathNode6.getDistance(pathNode7);
						if (!pathNode7.isInHeap() || f < pathNode7.penalizedPathLength) {
							pathNode7.previous = pathNode6;
							pathNode7.penalizedPathLength = f;
							pathNode7.distanceToNearestTarget = pathNode7.getDistance(pathNode4);
							if (pathNode7.isInHeap()) {
								this.field_14669.method_11902(pathNode7, pathNode7.penalizedPathLength + pathNode7.distanceToNearestTarget);
							} else {
								pathNode7.heapWeight = pathNode7.penalizedPathLength + pathNode7.distanceToNearestTarget;
								this.field_14669.method_11901(pathNode7);
							}
						}
					}
				}
			}
		}

		if (pathNode5 == pathNode3) {
			return null;
		} else {
			field_14662.debug("Failed to find path from {} to {}", new Object[]{i, j});
			if (pathNode != null) {
				pathNode.previous = pathNode5;
				pathNode5 = pathNode;
			}

			return this.method_13166(pathNode3, pathNode5);
		}
	}

	private PathMinHeap method_13166(PathNode pathNode, PathNode pathNode2) {
		int i = 1;

		for (PathNode pathNode3 = pathNode2; pathNode3.previous != null; pathNode3 = pathNode3.previous) {
			i++;
		}

		PathNode[] pathNodes = new PathNode[i];
		PathNode var7 = pathNode2;
		i--;

		for (pathNodes[i] = pathNode2; var7.previous != null; pathNodes[i] = var7) {
			var7 = var7.previous;
			i--;
		}

		return new PathMinHeap(pathNodes);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("DragonPhase", this.field_14664.method_13202().method_13189().method_13200());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("DragonPhase")) {
			this.field_14664.method_13203(class_2993.method_13197(nbt.getInt("DragonPhase")));
		}
	}

	@Override
	protected void checkDespawn() {
	}

	@Override
	public Entity[] getParts() {
		return this.parts;
	}

	@Override
	public boolean collides() {
		return false;
	}

	@Override
	public World getServerWorld() {
		return this.world;
	}

	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.HOSTILE;
	}

	@Override
	protected Sound ambientSound() {
		return Sounds.ENTITY_ENDERDRAGON_AMBIENT;
	}

	@Override
	protected Sound method_13048() {
		return Sounds.ENTITY_ENDERDRAGON_HURT;
	}

	@Override
	protected float getSoundVolume() {
		return 5.0F;
	}

	public float method_13165(int i, double[] ds, double[] es) {
		class_2987 lv = this.field_14664.method_13202();
		class_2993<? extends class_2987> lv2 = lv.method_13189();
		double d;
		if (lv2 == class_2993.LANDING || lv2 == class_2993.TAKEOFF) {
			BlockPos blockPos = this.world.getTopPosition(EndExitPortalFeature.ORIGIN);
			float f = Math.max(MathHelper.sqrt(this.squaredDistanceToCenter(blockPos)) / 4.0F, 1.0F);
			d = (double)((float)i / f);
		} else if (lv.method_13179()) {
			d = (double)i;
		} else if (i == 6) {
			d = 0.0;
		} else {
			d = es[1] - ds[1];
		}

		return (float)d;
	}

	public Vec3d method_13162(float f) {
		class_2987 lv = this.field_14664.method_13202();
		class_2993<? extends class_2987> lv2 = lv.method_13189();
		Vec3d vec3d;
		if (lv2 == class_2993.LANDING || lv2 == class_2993.TAKEOFF) {
			BlockPos blockPos = this.world.getTopPosition(EndExitPortalFeature.ORIGIN);
			float g = Math.max(MathHelper.sqrt(this.squaredDistanceToCenter(blockPos)) / 4.0F, 1.0F);
			float h = 6.0F / g;
			float i = this.pitch;
			float j = 1.5F;
			this.pitch = -h * j * 5.0F;
			vec3d = this.getRotationVector(f);
			this.pitch = i;
		} else if (lv.method_13179()) {
			float k = this.pitch;
			float l = 1.5F;
			this.pitch = -6.0F * l * 5.0F;
			vec3d = this.getRotationVector(f);
			this.pitch = k;
		} else {
			vec3d = this.getRotationVector(f);
		}

		return vec3d;
	}

	public void method_13167(EndCrystalEntity endCrystalEntity, BlockPos blockPos, DamageSource damageSource) {
		PlayerEntity playerEntity;
		if (damageSource.getAttacker() instanceof PlayerEntity) {
			playerEntity = (PlayerEntity)damageSource.getAttacker();
		} else {
			playerEntity = this.world.method_11480(blockPos, 64.0, 64.0);
		}

		if (endCrystalEntity == this.connectedCrystal) {
			this.setAngry(this.partHead, DamageSource.explosion(playerEntity), 10.0F);
		}

		this.field_14664.method_13202().method_13181(endCrystalEntity, blockPos, damageSource, playerEntity);
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (field_14661.equals(data) && this.world.isClient) {
			this.field_14664.method_13203(class_2993.method_13197(this.getDataTracker().get(field_14661)));
		}

		super.onTrackedDataSet(data);
	}

	public class_2994 method_13168() {
		return this.field_14664;
	}

	@Nullable
	public DragonRespawnAnimation method_13169() {
		return this.field_14663;
	}

	@Override
	public void addStatusEffect(StatusEffectInstance instance) {
	}

	@Override
	protected boolean canStartRiding(Entity entity) {
		return false;
	}

	@Override
	public boolean canUsePortals() {
		return false;
	}
}
