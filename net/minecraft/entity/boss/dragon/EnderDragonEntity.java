package net.minecraft.entity.boss.dragon;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.TorchBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.EndCrystalEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MultipartEntityProvider;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBarProvider;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EnderDragonEntity extends MobEntity implements BossBarProvider, MultipartEntityProvider, Monster {
	public double field_3742;
	public double field_3751;
	public double field_3752;
	public double[][] segmentCircularBuffer = new double[64][3];
	public int latestSegment = -1;
	public EnderDragonPart[] parts;
	public EnderDragonPart partHead;
	public EnderDragonPart partBody;
	public EnderDragonPart partTail1;
	public EnderDragonPart partTail2;
	public EnderDragonPart partTail3;
	public EnderDragonPart partWingRight;
	public EnderDragonPart partWingLeft;
	public float prevWingPosition;
	public float wingPosition;
	public boolean field_3744;
	public boolean field_3745;
	private Entity target;
	public int field_3746;
	public EndCrystalEntity connectedCrystal;

	public EnderDragonEntity(World world) {
		super(world);
		this.parts = new EnderDragonPart[]{
			this.partHead = new EnderDragonPart(this, "head", 6.0F, 6.0F),
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
		this.field_3751 = 100.0;
		this.ignoreCameraFrustum = true;
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(200.0);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
	}

	public double[] getSegmentProperties(int segmentNumber, float tickDelta) {
		if (this.getHealth() <= 0.0F) {
			tickDelta = 0.0F;
		}

		tickDelta = 1.0F - tickDelta;
		int i = this.latestSegment - segmentNumber * 1 & 63;
		int j = this.latestSegment - segmentNumber * 1 - 1 & 63;
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
			float f = MathHelper.cos(this.wingPosition * (float) Math.PI * 2.0F);
			float g = MathHelper.cos(this.prevWingPosition * (float) Math.PI * 2.0F);
			if (g <= -0.3F && f >= -0.3F && !this.isSilent()) {
				this.world.playSound(this.x, this.y, this.z, "mob.enderdragon.wings", 5.0F, 0.8F + this.random.nextFloat() * 0.3F, false);
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
			if (this.field_3745) {
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
						double d = this.x + (this.serverX - this.x) / (double)this.bodyTrackingIncrements;
						double e = this.y + (this.serverY - this.y) / (double)this.bodyTrackingIncrements;
						double m = this.z + (this.serverZ - this.z) / (double)this.bodyTrackingIncrements;
						double n = MathHelper.wrapDegrees(this.serverYaw - (double)this.yaw);
						this.yaw = (float)((double)this.yaw + n / (double)this.bodyTrackingIncrements);
						this.pitch = (float)((double)this.pitch + (this.serverPitch - (double)this.pitch) / (double)this.bodyTrackingIncrements);
						this.bodyTrackingIncrements--;
						this.updatePosition(d, e, m);
						this.setRotation(this.yaw, this.pitch);
					}
				} else {
					double o = this.field_3742 - this.x;
					double p = this.field_3751 - this.y;
					double q = this.field_3752 - this.z;
					double r = o * o + p * p + q * q;
					if (this.target != null) {
						this.field_3742 = this.target.x;
						this.field_3752 = this.target.z;
						double s = this.field_3742 - this.x;
						double t = this.field_3752 - this.z;
						double u = Math.sqrt(s * s + t * t);
						double v = 0.4F + u / 80.0 - 1.0;
						if (v > 10.0) {
							v = 10.0;
						}

						this.field_3751 = this.target.getBoundingBox().minY + v;
					} else {
						this.field_3742 = this.field_3742 + this.random.nextGaussian() * 2.0;
						this.field_3752 = this.field_3752 + this.random.nextGaussian() * 2.0;
					}

					if (this.field_3744 || r < 100.0 || r > 22500.0 || this.horizontalCollision || this.verticalCollision) {
						this.method_2906();
					}

					p /= (double)MathHelper.sqrt(o * o + q * q);
					float w = 0.6F;
					p = MathHelper.clamp(p, (double)(-w), (double)w);
					this.velocityY += p * 0.1F;
					this.yaw = MathHelper.wrapDegrees(this.yaw);
					double x = 180.0 - MathHelper.atan2(o, q) * 180.0 / (float) Math.PI;
					double y = MathHelper.wrapDegrees(x - (double)this.yaw);
					if (y > 50.0) {
						y = 50.0;
					}

					if (y < -50.0) {
						y = -50.0;
					}

					Vec3d vec3d = new Vec3d(this.field_3742 - this.x, this.field_3751 - this.y, this.field_3752 - this.z).normalize();
					double z = (double)(-MathHelper.cos(this.yaw * (float) Math.PI / 180.0F));
					Vec3d vec3d2 = new Vec3d((double)MathHelper.sin(this.yaw * (float) Math.PI / 180.0F), this.velocityY, z).normalize();
					float aa = ((float)vec3d2.dotProduct(vec3d) + 0.5F) / 1.5F;
					if (aa < 0.0F) {
						aa = 0.0F;
					}

					this.field_6782 *= 0.8F;
					float ab = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ) * 1.0F + 1.0F;
					double ac = Math.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ) * 1.0 + 1.0;
					if (ac > 40.0) {
						ac = 40.0;
					}

					this.field_6782 = (float)((double)this.field_6782 + y * (0.7F / ac / (double)ab));
					this.yaw = this.yaw + this.field_6782 * 0.1F;
					float ad = (float)(2.0 / (ac + 1.0));
					float ae = 0.06F;
					this.updateVelocity(0.0F, -1.0F, ae * (aa * ad + (1.0F - ad)));
					if (this.field_3745) {
						this.move(this.velocityX * 0.8F, this.velocityY * 0.8F, this.velocityZ * 0.8F);
					} else {
						this.move(this.velocityX, this.velocityY, this.velocityZ);
					}

					Vec3d vec3d3 = new Vec3d(this.velocityX, this.velocityY, this.velocityZ).normalize();
					float af = ((float)vec3d3.dotProduct(vec3d2) + 1.0F) / 2.0F;
					af = 0.8F + 0.15F * af;
					this.velocityX *= (double)af;
					this.velocityZ *= (double)af;
					this.velocityY *= 0.91F;
				}

				this.bodyYaw = this.yaw;
				this.partHead.width = this.partHead.height = 3.0F;
				this.partTail1.width = this.partTail1.height = 2.0F;
				this.partTail2.width = this.partTail2.height = 2.0F;
				this.partTail3.width = this.partTail3.height = 2.0F;
				this.partBody.height = 3.0F;
				this.partBody.width = 5.0F;
				this.partWingRight.height = 2.0F;
				this.partWingRight.width = 4.0F;
				this.partWingLeft.height = 3.0F;
				this.partWingLeft.width = 4.0F;
				float ag = (float)(this.getSegmentProperties(5, 1.0F)[1] - this.getSegmentProperties(10, 1.0F)[1]) * 10.0F / 180.0F * (float) Math.PI;
				float ah = MathHelper.cos(ag);
				float ai = -MathHelper.sin(ag);
				float aj = this.yaw * (float) Math.PI / 180.0F;
				float ak = MathHelper.sin(aj);
				float al = MathHelper.cos(aj);
				this.partBody.tick();
				this.partBody.refreshPositionAndAngles(this.x + (double)(ak * 0.5F), this.y, this.z - (double)(al * 0.5F), 0.0F, 0.0F);
				this.partWingRight.tick();
				this.partWingRight.refreshPositionAndAngles(this.x + (double)(al * 4.5F), this.y + 2.0, this.z + (double)(ak * 4.5F), 0.0F, 0.0F);
				this.partWingLeft.tick();
				this.partWingLeft.refreshPositionAndAngles(this.x - (double)(al * 4.5F), this.y + 2.0, this.z - (double)(ak * 4.5F), 0.0F, 0.0F);
				if (!this.world.isClient && this.hurtTime == 0) {
					this.launchLivingEntities(this.world.getEntitiesIn(this, this.partWingRight.getBoundingBox().expand(4.0, 2.0, 4.0).offset(0.0, -2.0, 0.0)));
					this.launchLivingEntities(this.world.getEntitiesIn(this, this.partWingLeft.getBoundingBox().expand(4.0, 2.0, 4.0).offset(0.0, -2.0, 0.0)));
					this.damageLivingEntities(this.world.getEntitiesIn(this, this.partHead.getBoundingBox().expand(1.0, 1.0, 1.0)));
				}

				double[] ds = this.getSegmentProperties(5, 1.0F);
				double[] es = this.getSegmentProperties(0, 1.0F);
				float am = MathHelper.sin(this.yaw * (float) Math.PI / 180.0F - this.field_6782 * 0.01F);
				float an = MathHelper.cos(this.yaw * (float) Math.PI / 180.0F - this.field_6782 * 0.01F);
				this.partHead.tick();
				this.partHead
					.refreshPositionAndAngles(
						this.x + (double)(am * 5.5F * ah), this.y + (es[1] - ds[1]) * 1.0 + (double)(ai * 5.5F), this.z - (double)(an * 5.5F * ah), 0.0F, 0.0F
					);

				for (int ao = 0; ao < 3; ao++) {
					EnderDragonPart enderDragonPart = null;
					if (ao == 0) {
						enderDragonPart = this.partTail1;
					}

					if (ao == 1) {
						enderDragonPart = this.partTail2;
					}

					if (ao == 2) {
						enderDragonPart = this.partTail3;
					}

					double[] fs = this.getSegmentProperties(12 + ao * 2, 1.0F);
					float ap = this.yaw * (float) Math.PI / 180.0F + this.wrapYawChange(fs[0] - ds[0]) * (float) Math.PI / 180.0F * 1.0F;
					float aq = MathHelper.sin(ap);
					float ar = MathHelper.cos(ap);
					float as = 1.5F;
					float at = (float)(ao + 1) * 2.0F;
					enderDragonPart.tick();
					enderDragonPart.refreshPositionAndAngles(
						this.x - (double)((ak * as + aq * at) * ah),
						this.y + (fs[1] - ds[1]) * 1.0 - (double)((at + as) * ai) + 1.5,
						this.z + (double)((al * as + ar * at) * ah),
						0.0F,
						0.0F
					);
				}

				if (!this.world.isClient) {
					this.field_3745 = this.destroyBlocks(this.partHead.getBoundingBox()) | this.destroyBlocks(this.partBody.getBoundingBox());
				}
			}
		}
	}

	private void tickWithEndCrystals() {
		if (this.connectedCrystal != null) {
			if (this.connectedCrystal.removed) {
				if (!this.world.isClient) {
					this.setAngry(this.partHead, DamageSource.explosion(null), 10.0F);
				}

				this.connectedCrystal = null;
			} else if (this.ticksAlive % 10 == 0 && this.getHealth() < this.getMaxHealth()) {
				this.setHealth(this.getHealth() + 1.0F);
			}
		}

		if (this.random.nextInt(10) == 0) {
			float f = 32.0F;
			List<EndCrystalEntity> list = this.world.getEntitiesInBox(EndCrystalEntity.class, this.getBoundingBox().expand((double)f, (double)f, (double)f));
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

	private void method_2906() {
		this.field_3744 = false;
		List<PlayerEntity> list = Lists.newArrayList(this.world.playerEntities);
		Iterator<PlayerEntity> iterator = list.iterator();

		while (iterator.hasNext()) {
			if (((PlayerEntity)iterator.next()).isSpectator()) {
				iterator.remove();
			}
		}

		if (this.random.nextInt(2) == 0 && !list.isEmpty()) {
			this.target = (Entity)list.get(this.random.nextInt(list.size()));
		} else {
			boolean bl;
			do {
				this.field_3742 = 0.0;
				this.field_3751 = (double)(70.0F + this.random.nextFloat() * 50.0F);
				this.field_3752 = 0.0;
				this.field_3742 = this.field_3742 + (double)(this.random.nextFloat() * 120.0F - 60.0F);
				this.field_3752 = this.field_3752 + (double)(this.random.nextFloat() * 120.0F - 60.0F);
				double d = this.x - this.field_3742;
				double e = this.y - this.field_3751;
				double f = this.z - this.field_3752;
				bl = d * d + e * e + f * f > 100.0;
			} while (!bl);

			this.target = null;
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
					Block block = this.world.getBlockState(blockPos).getBlock();
					if (block.getMaterial() != Material.AIR) {
						if (block != Blocks.BARRIER
							&& block != Blocks.OBSIDIAN
							&& block != Blocks.END_STONE
							&& block != Blocks.BEDROCK
							&& block != Blocks.COMMAND_BLOCK
							&& this.world.getGameRules().getBoolean("mobGriefing")) {
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
		if (multipart != this.partHead) {
			angry = angry / 4.0F + 1.0F;
		}

		float f = this.yaw * (float) Math.PI / 180.0F;
		float g = MathHelper.sin(f);
		float h = MathHelper.cos(f);
		this.field_3742 = this.x + (double)(g * 5.0F) + (double)((this.random.nextFloat() - 0.5F) * 2.0F);
		this.field_3751 = this.y + (double)(this.random.nextFloat() * 3.0F) + 1.0;
		this.field_3752 = this.z - (double)(h * 5.0F) + (double)((this.random.nextFloat() - 0.5F) * 2.0F);
		this.target = null;
		if (source.getAttacker() instanceof PlayerEntity || source.isExplosive()) {
			this.method_6302(source, angry);
		}

		return true;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (source instanceof EntityDamageSource && ((EntityDamageSource)source).isThorns()) {
			this.method_6302(source, amount);
		}

		return false;
	}

	protected boolean method_6302(DamageSource damageSource, float f) {
		return super.damage(damageSource, f);
	}

	@Override
	public void kill() {
		this.remove();
	}

	@Override
	protected void dropXp() {
		this.field_3746++;
		if (this.field_3746 >= 180 && this.field_3746 <= 200) {
			float f = (this.random.nextFloat() - 0.5F) * 8.0F;
			float g = (this.random.nextFloat() - 0.5F) * 4.0F;
			float h = (this.random.nextFloat() - 0.5F) * 8.0F;
			this.world.addParticle(ParticleType.HUGE_EXPLOSION, this.x + (double)f, this.y + 2.0 + (double)g, this.z + (double)h, 0.0, 0.0, 0.0);
		}

		boolean bl = this.world.getGameRules().getBoolean("doMobLoot");
		if (!this.world.isClient) {
			if (this.field_3746 > 150 && this.field_3746 % 5 == 0 && bl) {
				int i = 1000;

				while (i > 0) {
					int j = ExperienceOrbEntity.roundToOrbSize(i);
					i -= j;
					this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.x, this.y, this.z, j));
				}
			}

			if (this.field_3746 == 1) {
				this.world.method_4689(1018, new BlockPos(this), 0);
			}
		}

		this.move(0.0, 0.1F, 0.0);
		this.bodyYaw = this.yaw += 20.0F;
		if (this.field_3746 == 200 && !this.world.isClient) {
			if (bl) {
				int k = 2000;

				while (k > 0) {
					int l = ExperienceOrbEntity.roundToOrbSize(k);
					k -= l;
					this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.x, this.y, this.z, l));
				}
			}

			this.method_11113(new BlockPos(this.x, 64.0, this.z));
			this.remove();
		}
	}

	private void method_11113(BlockPos blockPos) {
		int i = 4;
		double d = 12.25;
		double e = 6.25;

		for (int j = -1; j <= 32; j++) {
			for (int k = -4; k <= 4; k++) {
				for (int l = -4; l <= 4; l++) {
					double f = (double)(k * k + l * l);
					if (!(f > 12.25)) {
						BlockPos blockPos2 = blockPos.add(k, j, l);
						if (j < 0) {
							if (f <= 6.25) {
								this.world.setBlockState(blockPos2, Blocks.BEDROCK.getDefaultState());
							}
						} else if (j > 0) {
							this.world.setBlockState(blockPos2, Blocks.AIR.getDefaultState());
						} else if (f > 6.25) {
							this.world.setBlockState(blockPos2, Blocks.BEDROCK.getDefaultState());
						} else {
							this.world.setBlockState(blockPos2, Blocks.END_PORTAL.getDefaultState());
						}
					}
				}
			}
		}

		this.world.setBlockState(blockPos, Blocks.BEDROCK.getDefaultState());
		this.world.setBlockState(blockPos.up(), Blocks.BEDROCK.getDefaultState());
		BlockPos blockPos3 = blockPos.up(2);
		this.world.setBlockState(blockPos3, Blocks.BEDROCK.getDefaultState());
		this.world.setBlockState(blockPos3.west(), Blocks.TORCH.getDefaultState().with(TorchBlock.FACING, Direction.EAST));
		this.world.setBlockState(blockPos3.east(), Blocks.TORCH.getDefaultState().with(TorchBlock.FACING, Direction.WEST));
		this.world.setBlockState(blockPos3.north(), Blocks.TORCH.getDefaultState().with(TorchBlock.FACING, Direction.SOUTH));
		this.world.setBlockState(blockPos3.south(), Blocks.TORCH.getDefaultState().with(TorchBlock.FACING, Direction.NORTH));
		this.world.setBlockState(blockPos.up(3), Blocks.BEDROCK.getDefaultState());
		this.world.setBlockState(blockPos.up(4), Blocks.DRAGON_EGG.getDefaultState());
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
	protected String getAmbientSound() {
		return "mob.enderdragon.growl";
	}

	@Override
	protected String getHurtSound() {
		return "mob.enderdragon.hit";
	}

	@Override
	protected float getSoundVolume() {
		return 5.0F;
	}
}
