package net.minecraft.entity.projectile;

import java.util.List;
import net.minecraft.class_2782;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FishingBobberEntity extends Entity {
	private static final TrackedData<Integer> HOOK_ENTITY_ID = DataTracker.registerData(FishingBobberEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private int posX = -1;
	private int posY = -1;
	private int posZ = -1;
	private Block inBlock;
	private boolean inGround;
	public PlayerEntity thrower;
	private int removalTimer;
	private int field_4070;
	private int hookCountdown;
	private int waitCountdown;
	private int fishTravelCountdown;
	private float fishAngle;
	public Entity caughtEntity;
	private int field_4053;
	private double field_4054;
	private double field_4055;
	private double field_4056;
	private double field_4057;
	private double field_4058;
	private double field_4059;
	private double field_4060;
	private double field_4061;

	public FishingBobberEntity(World world) {
		super(world);
		this.setBounds(0.25F, 0.25F);
		this.ignoreCameraFrustum = true;
	}

	public FishingBobberEntity(World world, double d, double e, double f, PlayerEntity playerEntity) {
		this(world);
		this.updatePosition(d, e, f);
		this.ignoreCameraFrustum = true;
		this.thrower = playerEntity;
		playerEntity.fishHook = this;
	}

	public FishingBobberEntity(World world, PlayerEntity playerEntity) {
		super(world);
		this.ignoreCameraFrustum = true;
		this.thrower = playerEntity;
		this.thrower.fishHook = this;
		this.setBounds(0.25F, 0.25F);
		this.refreshPositionAndAngles(playerEntity.x, playerEntity.y + (double)playerEntity.getEyeHeight(), playerEntity.z, playerEntity.yaw, playerEntity.pitch);
		this.x = this.x - (double)(MathHelper.cos(this.yaw * (float) (Math.PI / 180.0)) * 0.16F);
		this.y -= 0.1F;
		this.z = this.z - (double)(MathHelper.sin(this.yaw * (float) (Math.PI / 180.0)) * 0.16F);
		this.updatePosition(this.x, this.y, this.z);
		float f = 0.4F;
		this.velocityX = (double)(-MathHelper.sin(this.yaw * (float) (Math.PI / 180.0)) * MathHelper.cos(this.pitch * (float) (Math.PI / 180.0)) * f);
		this.velocityZ = (double)(MathHelper.cos(this.yaw * (float) (Math.PI / 180.0)) * MathHelper.cos(this.pitch * (float) (Math.PI / 180.0)) * f);
		this.velocityY = (double)(-MathHelper.sin(this.pitch * (float) (Math.PI / 180.0)) * f);
		this.method_3230(this.velocityX, this.velocityY, this.velocityZ, 1.5F, 1.0F);
	}

	@Override
	protected void initDataTracker() {
		this.getDataTracker().startTracking(HOOK_ENTITY_ID, 0);
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (HOOK_ENTITY_ID.equals(data)) {
			int i = this.getDataTracker().get(HOOK_ENTITY_ID);
			if (i > 0 && this.caughtEntity != null) {
				this.caughtEntity = null;
			}
		}

		super.onTrackedDataSet(data);
	}

	@Override
	public boolean shouldRender(double distance) {
		double d = this.getBoundingBox().getAverage() * 4.0;
		if (Double.isNaN(d)) {
			d = 4.0;
		}

		d *= 64.0;
		return distance < d * d;
	}

	public void method_3230(double d, double e, double f, float g, float h) {
		float i = MathHelper.sqrt(d * d + e * e + f * f);
		d /= (double)i;
		e /= (double)i;
		f /= (double)i;
		d += this.random.nextGaussian() * 0.0075F * (double)h;
		e += this.random.nextGaussian() * 0.0075F * (double)h;
		f += this.random.nextGaussian() * 0.0075F * (double)h;
		d *= (double)g;
		e *= (double)g;
		f *= (double)g;
		this.velocityX = d;
		this.velocityY = e;
		this.velocityZ = f;
		float j = MathHelper.sqrt(d * d + f * f);
		this.prevYaw = this.yaw = (float)(MathHelper.atan2(d, f) * 180.0F / (float)Math.PI);
		this.prevPitch = this.pitch = (float)(MathHelper.atan2(e, (double)j) * 180.0F / (float)Math.PI);
		this.removalTimer = 0;
	}

	@Override
	public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
		this.field_4054 = x;
		this.field_4055 = y;
		this.field_4056 = z;
		this.field_4057 = (double)yaw;
		this.field_4058 = (double)pitch;
		this.field_4053 = interpolationSteps;
		this.velocityX = this.field_4059;
		this.velocityY = this.field_4060;
		this.velocityZ = this.field_4061;
	}

	@Override
	public void setVelocityClient(double x, double y, double z) {
		this.field_4059 = this.velocityX = x;
		this.field_4060 = this.velocityY = y;
		this.field_4061 = this.velocityZ = z;
	}

	@Override
	public void tick() {
		super.tick();
		if (this.world.isClient) {
			int i = this.getDataTracker().get(HOOK_ENTITY_ID);
			if (i > 0 && this.caughtEntity == null) {
				this.caughtEntity = this.world.getEntityById(i - 1);
			}
		} else {
			ItemStack itemStack = this.thrower.getMainHandStack();
			if (this.thrower.removed
				|| !this.thrower.isAlive()
				|| itemStack == null
				|| itemStack.getItem() != Items.FISHING_ROD
				|| this.squaredDistanceTo(this.thrower) > 1024.0) {
				this.remove();
				this.thrower.fishHook = null;
				return;
			}
		}

		if (this.caughtEntity != null) {
			if (!this.caughtEntity.removed) {
				this.x = this.caughtEntity.x;
				double var10002 = (double)this.caughtEntity.height;
				this.y = this.caughtEntity.getBoundingBox().minY + var10002 * 0.8;
				this.z = this.caughtEntity.z;
				return;
			}

			this.caughtEntity = null;
		}

		if (this.field_4053 > 0) {
			double d = this.x + (this.field_4054 - this.x) / (double)this.field_4053;
			double e = this.y + (this.field_4055 - this.y) / (double)this.field_4053;
			double f = this.z + (this.field_4056 - this.z) / (double)this.field_4053;
			double g = MathHelper.wrapDegrees(this.field_4057 - (double)this.yaw);
			this.yaw = (float)((double)this.yaw + g / (double)this.field_4053);
			this.pitch = (float)((double)this.pitch + (this.field_4058 - (double)this.pitch) / (double)this.field_4053);
			this.field_4053--;
			this.updatePosition(d, e, f);
			this.setRotation(this.yaw, this.pitch);
		} else {
			if (this.inGround) {
				if (this.world.getBlockState(new BlockPos(this.posX, this.posY, this.posZ)).getBlock() == this.inBlock) {
					this.removalTimer++;
					if (this.removalTimer == 1200) {
						this.remove();
					}

					return;
				}

				this.inGround = false;
				this.velocityX = this.velocityX * (double)(this.random.nextFloat() * 0.2F);
				this.velocityY = this.velocityY * (double)(this.random.nextFloat() * 0.2F);
				this.velocityZ = this.velocityZ * (double)(this.random.nextFloat() * 0.2F);
				this.removalTimer = 0;
				this.field_4070 = 0;
			} else {
				this.field_4070++;
			}

			if (!this.world.isClient) {
				Vec3d vec3d = new Vec3d(this.x, this.y, this.z);
				Vec3d vec3d2 = new Vec3d(this.x + this.velocityX, this.y + this.velocityY, this.z + this.velocityZ);
				BlockHitResult blockHitResult = this.world.rayTrace(vec3d, vec3d2);
				vec3d = new Vec3d(this.x, this.y, this.z);
				vec3d2 = new Vec3d(this.x + this.velocityX, this.y + this.velocityY, this.z + this.velocityZ);
				if (blockHitResult != null) {
					vec3d2 = new Vec3d(blockHitResult.pos.x, blockHitResult.pos.y, blockHitResult.pos.z);
				}

				Entity entity = null;
				List<Entity> list = this.world.getEntitiesIn(this, this.getBoundingBox().stretch(this.velocityX, this.velocityY, this.velocityZ).expand(1.0));
				double h = 0.0;

				for (int j = 0; j < list.size(); j++) {
					Entity entity2 = (Entity)list.get(j);
					if (entity2.collides() && (entity2 != this.thrower || this.field_4070 >= 5)) {
						Box box = entity2.getBoundingBox().expand(0.3F);
						BlockHitResult blockHitResult2 = box.method_585(vec3d, vec3d2);
						if (blockHitResult2 != null) {
							double k = vec3d.squaredDistanceTo(blockHitResult2.pos);
							if (k < h || h == 0.0) {
								entity = entity2;
								h = k;
							}
						}
					}
				}

				if (entity != null) {
					blockHitResult = new BlockHitResult(entity);
				}

				if (blockHitResult != null) {
					if (blockHitResult.entity != null) {
						this.caughtEntity = blockHitResult.entity;
						this.getDataTracker().set(HOOK_ENTITY_ID, this.caughtEntity.getEntityId() + 1);
					} else {
						this.inGround = true;
					}
				}
			}

			if (!this.inGround) {
				this.move(this.velocityX, this.velocityY, this.velocityZ);
				float l = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
				this.yaw = (float)(MathHelper.atan2(this.velocityX, this.velocityZ) * 180.0F / (float)Math.PI);
				this.pitch = (float)(MathHelper.atan2(this.velocityY, (double)l) * 180.0F / (float)Math.PI);

				while (this.pitch - this.prevPitch < -180.0F) {
					this.prevPitch -= 360.0F;
				}

				while (this.pitch - this.prevPitch >= 180.0F) {
					this.prevPitch += 360.0F;
				}

				while (this.yaw - this.prevYaw < -180.0F) {
					this.prevYaw -= 360.0F;
				}

				while (this.yaw - this.prevYaw >= 180.0F) {
					this.prevYaw += 360.0F;
				}

				this.pitch = this.prevPitch + (this.pitch - this.prevPitch) * 0.2F;
				this.yaw = this.prevYaw + (this.yaw - this.prevYaw) * 0.2F;
				float m = 0.92F;
				if (this.onGround || this.horizontalCollision) {
					m = 0.5F;
				}

				int n = 5;
				double o = 0.0;

				for (int p = 0; p < n; p++) {
					Box box2 = this.getBoundingBox();
					double q = box2.maxY - box2.minY;
					double r = box2.minY + q * (double)p / (double)n;
					double s = box2.minY + q * (double)(p + 1) / (double)n;
					Box box3 = new Box(box2.minX, r, box2.minZ, box2.maxX, s, box2.maxZ);
					if (this.world.containsBlockWithMaterial(box3, Material.WATER)) {
						o += 1.0 / (double)n;
					}
				}

				if (!this.world.isClient && o > 0.0) {
					ServerWorld serverWorld = (ServerWorld)this.world;
					int t = 1;
					BlockPos blockPos = new BlockPos(this).up();
					if (this.random.nextFloat() < 0.25F && this.world.hasRain(blockPos)) {
						t = 2;
					}

					if (this.random.nextFloat() < 0.5F && !this.world.hasDirectSunlight(blockPos)) {
						t--;
					}

					if (this.hookCountdown > 0) {
						this.hookCountdown--;
						if (this.hookCountdown <= 0) {
							this.waitCountdown = 0;
							this.fishTravelCountdown = 0;
						}
					} else if (this.fishTravelCountdown > 0) {
						this.fishTravelCountdown -= t;
						if (this.fishTravelCountdown <= 0) {
							this.velocityY -= 0.2F;
							this.playSound(Sounds.ENTITY_BOBBER_SPLASH, 0.25F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
							float u = (float)MathHelper.floor(this.getBoundingBox().minY);
							serverWorld.addParticle(
								ParticleType.BUBBLE, this.x, (double)(u + 1.0F), this.z, (int)(1.0F + this.width * 20.0F), (double)this.width, 0.0, (double)this.width, 0.2F
							);
							serverWorld.addParticle(
								ParticleType.WATER_WAKE, this.x, (double)(u + 1.0F), this.z, (int)(1.0F + this.width * 20.0F), (double)this.width, 0.0, (double)this.width, 0.2F
							);
							this.hookCountdown = MathHelper.nextInt(this.random, 10, 30);
						} else {
							this.fishAngle = (float)((double)this.fishAngle + this.random.nextGaussian() * 4.0);
							float v = this.fishAngle * (float) (Math.PI / 180.0);
							float w = MathHelper.sin(v);
							float x = MathHelper.cos(v);
							double y = this.x + (double)(w * (float)this.fishTravelCountdown * 0.1F);
							double z = (double)((float)MathHelper.floor(this.getBoundingBox().minY) + 1.0F);
							double aa = this.z + (double)(x * (float)this.fishTravelCountdown * 0.1F);
							Block block = serverWorld.getBlockState(new BlockPos((int)y, (int)z - 1, (int)aa)).getBlock();
							if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
								if (this.random.nextFloat() < 0.15F) {
									serverWorld.addParticle(ParticleType.BUBBLE, y, z - 0.1F, aa, 1, (double)w, 0.1, (double)x, 0.0);
								}

								float ab = w * 0.04F;
								float ac = x * 0.04F;
								serverWorld.addParticle(ParticleType.WATER_WAKE, y, z, aa, 0, (double)ac, 0.01, (double)(-ab), 1.0);
								serverWorld.addParticle(ParticleType.WATER_WAKE, y, z, aa, 0, (double)(-ac), 0.01, (double)ab, 1.0);
							}
						}
					} else if (this.waitCountdown > 0) {
						this.waitCountdown -= t;
						float ad = 0.15F;
						if (this.waitCountdown < 20) {
							ad = (float)((double)ad + (double)(20 - this.waitCountdown) * 0.05);
						} else if (this.waitCountdown < 40) {
							ad = (float)((double)ad + (double)(40 - this.waitCountdown) * 0.02);
						} else if (this.waitCountdown < 60) {
							ad = (float)((double)ad + (double)(60 - this.waitCountdown) * 0.01);
						}

						if (this.random.nextFloat() < ad) {
							float ae = MathHelper.nextFloat(this.random, 0.0F, 360.0F) * (float) (Math.PI / 180.0);
							float af = MathHelper.nextFloat(this.random, 25.0F, 60.0F);
							double ag = this.x + (double)(MathHelper.sin(ae) * af * 0.1F);
							double ah = (double)((float)MathHelper.floor(this.getBoundingBox().minY) + 1.0F);
							double ai = this.z + (double)(MathHelper.cos(ae) * af * 0.1F);
							Block block2 = serverWorld.getBlockState(new BlockPos((int)ag, (int)ah - 1, (int)ai)).getBlock();
							if (block2 == Blocks.WATER || block2 == Blocks.FLOWING_WATER) {
								serverWorld.addParticle(ParticleType.WATER, ag, ah, ai, 2 + this.random.nextInt(2), 0.1F, 0.0, 0.1F, 0.0);
							}
						}

						if (this.waitCountdown <= 0) {
							this.fishAngle = MathHelper.nextFloat(this.random, 0.0F, 360.0F);
							this.fishTravelCountdown = MathHelper.nextInt(this.random, 20, 80);
						}
					} else {
						this.waitCountdown = MathHelper.nextInt(this.random, 100, 900);
						this.waitCountdown = this.waitCountdown - EnchantmentHelper.getLure(this.thrower) * 20 * 5;
					}

					if (this.hookCountdown > 0) {
						this.velocityY = this.velocityY - (double)(this.random.nextFloat() * this.random.nextFloat() * this.random.nextFloat()) * 0.2;
					}
				}

				double aj = o * 2.0 - 1.0;
				this.velocityY += 0.04F * aj;
				if (o > 0.0) {
					m = (float)((double)m * 0.9);
					this.velocityY *= 0.8;
				}

				this.velocityX *= (double)m;
				this.velocityY *= (double)m;
				this.velocityZ *= (double)m;
				this.updatePosition(this.x, this.y, this.z);
			}
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		nbt.putInt("xTile", this.posX);
		nbt.putInt("yTile", this.posY);
		nbt.putInt("zTile", this.posZ);
		Identifier identifier = Block.REGISTRY.getIdentifier(this.inBlock);
		nbt.putString("inTile", identifier == null ? "" : identifier.toString());
		nbt.putByte("inGround", (byte)(this.inGround ? 1 : 0));
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		this.posX = nbt.getInt("xTile");
		this.posY = nbt.getInt("yTile");
		this.posZ = nbt.getInt("zTile");
		if (nbt.contains("inTile", 8)) {
			this.inBlock = Block.get(nbt.getString("inTile"));
		} else {
			this.inBlock = Block.getById(nbt.getByte("inTile") & 255);
		}

		this.inGround = nbt.getByte("inGround") == 1;
	}

	public int retract() {
		if (this.world.isClient) {
			return 0;
		} else {
			int i = 0;
			if (this.caughtEntity != null) {
				this.pullHookedEntity();
				this.world.sendEntityStatus(this, (byte)31);
				i = this.caughtEntity instanceof ItemEntity ? 3 : 5;
			} else if (this.hookCountdown > 0) {
				class_2782.class_2783 lv = new class_2782.class_2783((ServerWorld)this.world);
				lv.method_11995((float)EnchantmentHelper.getLuckOfTheSea(this.thrower) + this.thrower.method_13271());

				for (ItemStack itemStack : this.world.method_11487().method_12006(LootTables.FISHING_GAMEPLAY).method_11981(this.random, lv.method_11994())) {
					ItemEntity itemEntity = new ItemEntity(this.world, this.x, this.y, this.z, itemStack);
					double d = this.thrower.x - this.x;
					double e = this.thrower.y - this.y;
					double f = this.thrower.z - this.z;
					double g = (double)MathHelper.sqrt(d * d + e * e + f * f);
					double h = 0.1;
					itemEntity.velocityX = d * h;
					itemEntity.velocityY = e * h + (double)MathHelper.sqrt(g) * 0.08;
					itemEntity.velocityZ = f * h;
					this.world.spawnEntity(itemEntity);
					this.thrower
						.world
						.spawnEntity(new ExperienceOrbEntity(this.thrower.world, this.thrower.x, this.thrower.y + 0.5, this.thrower.z + 0.5, this.random.nextInt(6) + 1));
				}

				i = 1;
			}

			if (this.inGround) {
				i = 2;
			}

			this.remove();
			this.thrower.fishHook = null;
			return i;
		}
	}

	@Override
	public void handleStatus(byte status) {
		if (status == 31 && this.world.isClient && this.caughtEntity instanceof PlayerEntity && ((PlayerEntity)this.caughtEntity).isMainPlayer()) {
			this.pullHookedEntity();
		}

		super.handleStatus(status);
	}

	protected void pullHookedEntity() {
		double d = this.thrower.x - this.x;
		double e = this.thrower.y - this.y;
		double f = this.thrower.z - this.z;
		double g = (double)MathHelper.sqrt(d * d + e * e + f * f);
		double h = 0.1;
		this.caughtEntity.velocityX += d * h;
		this.caughtEntity.velocityY = this.caughtEntity.velocityY + e * h + (double)MathHelper.sqrt(g) * 0.08;
		this.caughtEntity.velocityZ += f * h;
	}

	@Override
	public void remove() {
		super.remove();
		if (this.thrower != null) {
			this.thrower.fishHook = null;
		}
	}
}
