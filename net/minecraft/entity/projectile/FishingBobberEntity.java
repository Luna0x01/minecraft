package net.minecraft.entity.projectile;

import java.util.List;
import net.minecraft.class_2782;
import net.minecraft.block.AbstractFluidBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.Sounds;
import net.minecraft.stat.Stats;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FishingBobberEntity extends Entity {
	private static final TrackedData<Integer> HOOK_ENTITY_ID = DataTracker.registerData(FishingBobberEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private boolean inGround;
	private int removalTimer = 0;
	private PlayerEntity thrower;
	private int field_4070;
	private int hookCountdown;
	private int waitCountdown;
	private int fishTravelCountdown;
	private float fishAngle;
	public Entity caughtEntity;
	private FishingBobberEntity.State state = FishingBobberEntity.State.FLYING;
	private int luckOfTheSeaLevel;
	private int lureLevel;

	public FishingBobberEntity(World world, PlayerEntity playerEntity, double d, double e, double f) {
		super(world);
		this.setThrower(playerEntity);
		this.updatePosition(d, e, f);
		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
	}

	public FishingBobberEntity(World world, PlayerEntity playerEntity) {
		super(world);
		this.setThrower(playerEntity);
		this.initVelocity();
	}

	private void setThrower(PlayerEntity thrower) {
		this.setBounds(0.25F, 0.25F);
		this.ignoreCameraFrustum = true;
		this.thrower = thrower;
		this.thrower.fishHook = this;
	}

	public void setLure(int level) {
		this.lureLevel = level;
	}

	public void setLuckOfTheSea(int level) {
		this.luckOfTheSeaLevel = level;
	}

	private void initVelocity() {
		float f = this.thrower.prevPitch + (this.thrower.pitch - this.thrower.prevPitch);
		float g = this.thrower.prevYaw + (this.thrower.yaw - this.thrower.prevYaw);
		float h = MathHelper.cos(-g * (float) (Math.PI / 180.0) - (float) Math.PI);
		float i = MathHelper.sin(-g * (float) (Math.PI / 180.0) - (float) Math.PI);
		float j = -MathHelper.cos(-f * (float) (Math.PI / 180.0));
		float k = MathHelper.sin(-f * (float) (Math.PI / 180.0));
		double d = this.thrower.prevX + (this.thrower.x - this.thrower.prevX) - (double)i * 0.3;
		double e = this.thrower.prevY + (this.thrower.y - this.thrower.prevY) + (double)this.thrower.getEyeHeight();
		double l = this.thrower.prevZ + (this.thrower.z - this.thrower.prevZ) - (double)h * 0.3;
		this.refreshPositionAndAngles(d, e, l, g, f);
		this.velocityX = (double)(-i);
		this.velocityY = (double)MathHelper.clamp(-(k / j), -5.0F, 5.0F);
		this.velocityZ = (double)(-h);
		float m = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityY * this.velocityY + this.velocityZ * this.velocityZ);
		this.velocityX = this.velocityX * (0.6 / (double)m + 0.5 + this.random.nextGaussian() * 0.0045);
		this.velocityY = this.velocityY * (0.6 / (double)m + 0.5 + this.random.nextGaussian() * 0.0045);
		this.velocityZ = this.velocityZ * (0.6 / (double)m + 0.5 + this.random.nextGaussian() * 0.0045);
		float n = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
		this.yaw = (float)(MathHelper.atan2(this.velocityX, this.velocityZ) * 180.0F / (float)Math.PI);
		this.pitch = (float)(MathHelper.atan2(this.velocityY, (double)n) * 180.0F / (float)Math.PI);
		this.prevYaw = this.yaw;
		this.prevPitch = this.pitch;
	}

	@Override
	protected void initDataTracker() {
		this.getDataTracker().startTracking(HOOK_ENTITY_ID, 0);
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (HOOK_ENTITY_ID.equals(data)) {
			int i = this.getDataTracker().get(HOOK_ENTITY_ID);
			this.caughtEntity = i > 0 ? this.world.getEntityById(i - 1) : null;
		}

		super.onTrackedDataSet(data);
	}

	@Override
	public boolean shouldRender(double distance) {
		double d = 64.0;
		return distance < 4096.0;
	}

	@Override
	public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
	}

	@Override
	public void tick() {
		super.tick();
		if (this.thrower == null) {
			this.remove();
		} else if (this.world.isClient || !this.removeIfInvalid()) {
			if (this.inGround) {
				this.removalTimer++;
				if (this.removalTimer >= 1200) {
					this.remove();
					return;
				}
			}

			float f = 0.0F;
			BlockPos blockPos = new BlockPos(this);
			BlockState blockState = this.world.getBlockState(blockPos);
			if (blockState.getMaterial() == Material.WATER) {
				f = AbstractFluidBlock.method_13709(blockState, this.world, blockPos);
			}

			if (this.state == FishingBobberEntity.State.FLYING) {
				if (this.caughtEntity != null) {
					this.velocityX = 0.0;
					this.velocityY = 0.0;
					this.velocityZ = 0.0;
					this.state = FishingBobberEntity.State.HOOKED_IN_ENTITY;
					return;
				}

				if (f > 0.0F) {
					this.velocityX *= 0.3;
					this.velocityY *= 0.2;
					this.velocityZ *= 0.3;
					this.state = FishingBobberEntity.State.BOBBING;
					return;
				}

				if (!this.world.isClient) {
					this.checkForCollision();
				}

				if (!this.inGround && !this.onGround && !this.horizontalCollision) {
					this.field_4070++;
				} else {
					this.field_4070 = 0;
					this.velocityX = 0.0;
					this.velocityY = 0.0;
					this.velocityZ = 0.0;
				}
			} else {
				if (this.state == FishingBobberEntity.State.HOOKED_IN_ENTITY) {
					if (this.caughtEntity != null) {
						if (this.caughtEntity.removed) {
							this.caughtEntity = null;
							this.state = FishingBobberEntity.State.FLYING;
						} else {
							this.x = this.caughtEntity.x;
							double var10002 = (double)this.caughtEntity.height;
							this.y = this.caughtEntity.getBoundingBox().minY + var10002 * 0.8;
							this.z = this.caughtEntity.z;
							this.updatePosition(this.x, this.y, this.z);
						}
					}

					return;
				}

				if (this.state == FishingBobberEntity.State.BOBBING) {
					this.velocityX *= 0.9;
					this.velocityZ *= 0.9;
					double d = this.y + this.velocityY - (double)blockPos.getY() - (double)f;
					if (Math.abs(d) < 0.01) {
						d += Math.signum(d) * 0.1;
					}

					this.velocityY = this.velocityY - d * (double)this.random.nextFloat() * 0.2;
					if (!this.world.isClient && f > 0.0F) {
						this.tickFishingLogic(blockPos);
					}
				}
			}

			if (blockState.getMaterial() != Material.WATER) {
				this.velocityY -= 0.03;
			}

			this.move(MovementType.SELF, this.velocityX, this.velocityY, this.velocityZ);
			this.method_14053();
			double e = 0.92;
			this.velocityX *= 0.92;
			this.velocityY *= 0.92;
			this.velocityZ *= 0.92;
			this.updatePosition(this.x, this.y, this.z);
		}
	}

	private boolean removeIfInvalid() {
		ItemStack itemStack = this.thrower.getMainHandStack();
		ItemStack itemStack2 = this.thrower.getOffHandStack();
		boolean bl = itemStack.getItem() == Items.FISHING_ROD;
		boolean bl2 = itemStack2.getItem() == Items.FISHING_ROD;
		if (!this.thrower.removed && this.thrower.isAlive() && (bl || bl2) && !(this.squaredDistanceTo(this.thrower) > 1024.0)) {
			return false;
		} else {
			this.remove();
			return true;
		}
	}

	private void method_14053() {
		float f = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
		this.yaw = (float)(MathHelper.atan2(this.velocityX, this.velocityZ) * 180.0F / (float)Math.PI);
		this.pitch = (float)(MathHelper.atan2(this.velocityY, (double)f) * 180.0F / (float)Math.PI);

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
	}

	private void checkForCollision() {
		Vec3d vec3d = new Vec3d(this.x, this.y, this.z);
		Vec3d vec3d2 = new Vec3d(this.x + this.velocityX, this.y + this.velocityY, this.z + this.velocityZ);
		BlockHitResult blockHitResult = this.world.rayTrace(vec3d, vec3d2, false, true, false);
		vec3d = new Vec3d(this.x, this.y, this.z);
		vec3d2 = new Vec3d(this.x + this.velocityX, this.y + this.velocityY, this.z + this.velocityZ);
		if (blockHitResult != null) {
			vec3d2 = new Vec3d(blockHitResult.pos.x, blockHitResult.pos.y, blockHitResult.pos.z);
		}

		Entity entity = null;
		List<Entity> list = this.world.getEntitiesIn(this, this.getBoundingBox().stretch(this.velocityX, this.velocityY, this.velocityZ).expand(1.0));
		double d = 0.0;

		for (Entity entity2 : list) {
			if (this.canHit(entity2) && (entity2 != this.thrower || this.field_4070 >= 5)) {
				Box box = entity2.getBoundingBox().expand(0.3F);
				BlockHitResult blockHitResult2 = box.method_585(vec3d, vec3d2);
				if (blockHitResult2 != null) {
					double e = vec3d.squaredDistanceTo(blockHitResult2.pos);
					if (e < d || d == 0.0) {
						entity = entity2;
						d = e;
					}
				}
			}
		}

		if (entity != null) {
			blockHitResult = new BlockHitResult(entity);
		}

		if (blockHitResult != null && blockHitResult.type != BlockHitResult.Type.MISS) {
			if (blockHitResult.type == BlockHitResult.Type.ENTITY) {
				this.caughtEntity = blockHitResult.entity;
				this.updateHookedEntityId();
			} else {
				this.inGround = true;
			}
		}
	}

	private void updateHookedEntityId() {
		this.getDataTracker().set(HOOK_ENTITY_ID, this.caughtEntity.getEntityId() + 1);
	}

	private void tickFishingLogic(BlockPos pos) {
		ServerWorld serverWorld = (ServerWorld)this.world;
		int i = 1;
		BlockPos blockPos = pos.up();
		if (this.random.nextFloat() < 0.25F && this.world.hasRain(blockPos)) {
			i++;
		}

		if (this.random.nextFloat() < 0.5F && !this.world.hasDirectSunlight(blockPos)) {
			i--;
		}

		if (this.hookCountdown > 0) {
			this.hookCountdown--;
			if (this.hookCountdown <= 0) {
				this.waitCountdown = 0;
				this.fishTravelCountdown = 0;
			} else {
				this.velocityY = this.velocityY - 0.2 * (double)this.random.nextFloat() * (double)this.random.nextFloat();
			}
		} else if (this.fishTravelCountdown > 0) {
			this.fishTravelCountdown -= i;
			if (this.fishTravelCountdown > 0) {
				this.fishAngle = (float)((double)this.fishAngle + this.random.nextGaussian() * 4.0);
				float f = this.fishAngle * (float) (Math.PI / 180.0);
				float g = MathHelper.sin(f);
				float h = MathHelper.cos(f);
				double d = this.x + (double)(g * (float)this.fishTravelCountdown * 0.1F);
				double e = (double)((float)MathHelper.floor(this.getBoundingBox().minY) + 1.0F);
				double j = this.z + (double)(h * (float)this.fishTravelCountdown * 0.1F);
				Block block = serverWorld.getBlockState(new BlockPos(d, e - 1.0, j)).getBlock();
				if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
					if (this.random.nextFloat() < 0.15F) {
						serverWorld.addParticle(ParticleType.BUBBLE, d, e - 0.1F, j, 1, (double)g, 0.1, (double)h, 0.0);
					}

					float k = g * 0.04F;
					float l = h * 0.04F;
					serverWorld.addParticle(ParticleType.WATER_WAKE, d, e, j, 0, (double)l, 0.01, (double)(-k), 1.0);
					serverWorld.addParticle(ParticleType.WATER_WAKE, d, e, j, 0, (double)(-l), 0.01, (double)k, 1.0);
				}
			} else {
				this.velocityY = (double)(-0.4F * MathHelper.nextFloat(this.random, 0.6F, 1.0F));
				this.playSound(Sounds.ENTITY_BOBBER_SPLASH, 0.25F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
				double m = this.getBoundingBox().minY + 0.5;
				serverWorld.addParticle(ParticleType.BUBBLE, this.x, m, this.z, (int)(1.0F + this.width * 20.0F), (double)this.width, 0.0, (double)this.width, 0.2F);
				serverWorld.addParticle(ParticleType.WATER_WAKE, this.x, m, this.z, (int)(1.0F + this.width * 20.0F), (double)this.width, 0.0, (double)this.width, 0.2F);
				this.hookCountdown = MathHelper.nextInt(this.random, 20, 40);
			}
		} else if (this.waitCountdown > 0) {
			this.waitCountdown -= i;
			float n = 0.15F;
			if (this.waitCountdown < 20) {
				n = (float)((double)n + (double)(20 - this.waitCountdown) * 0.05);
			} else if (this.waitCountdown < 40) {
				n = (float)((double)n + (double)(40 - this.waitCountdown) * 0.02);
			} else if (this.waitCountdown < 60) {
				n = (float)((double)n + (double)(60 - this.waitCountdown) * 0.01);
			}

			if (this.random.nextFloat() < n) {
				float o = MathHelper.nextFloat(this.random, 0.0F, 360.0F) * (float) (Math.PI / 180.0);
				float p = MathHelper.nextFloat(this.random, 25.0F, 60.0F);
				double q = this.x + (double)(MathHelper.sin(o) * p * 0.1F);
				double r = (double)((float)MathHelper.floor(this.getBoundingBox().minY) + 1.0F);
				double s = this.z + (double)(MathHelper.cos(o) * p * 0.1F);
				Block block2 = serverWorld.getBlockState(new BlockPos((int)q, (int)r - 1, (int)s)).getBlock();
				if (block2 == Blocks.WATER || block2 == Blocks.FLOWING_WATER) {
					serverWorld.addParticle(ParticleType.WATER, q, r, s, 2 + this.random.nextInt(2), 0.1F, 0.0, 0.1F, 0.0);
				}
			}

			if (this.waitCountdown <= 0) {
				this.fishAngle = MathHelper.nextFloat(this.random, 0.0F, 360.0F);
				this.fishTravelCountdown = MathHelper.nextInt(this.random, 20, 80);
			}
		} else {
			this.waitCountdown = MathHelper.nextInt(this.random, 100, 600);
			this.waitCountdown = this.waitCountdown - this.lureLevel * 20 * 5;
		}
	}

	protected boolean canHit(Entity entity) {
		return entity.collides() || entity instanceof ItemEntity;
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
	}

	public int retract() {
		if (!this.world.isClient && this.thrower != null) {
			int i = 0;
			if (this.caughtEntity != null) {
				this.pullHookedEntity();
				this.world.sendEntityStatus(this, (byte)31);
				i = this.caughtEntity instanceof ItemEntity ? 3 : 5;
			} else if (this.hookCountdown > 0) {
				class_2782.class_2783 lv = new class_2782.class_2783((ServerWorld)this.world);
				lv.method_11995((float)this.luckOfTheSeaLevel + this.thrower.method_13271());

				for (ItemStack itemStack : this.world.method_11487().method_12006(LootTables.FISHING_GAMEPLAY).method_11981(this.random, lv.method_11994())) {
					ItemEntity itemEntity = new ItemEntity(this.world, this.x, this.y, this.z, itemStack);
					double d = this.thrower.x - this.x;
					double e = this.thrower.y - this.y;
					double f = this.thrower.z - this.z;
					double g = (double)MathHelper.sqrt(d * d + e * e + f * f);
					double h = 0.1;
					itemEntity.velocityX = d * 0.1;
					itemEntity.velocityY = e * 0.1 + (double)MathHelper.sqrt(g) * 0.08;
					itemEntity.velocityZ = f * 0.1;
					this.world.spawnEntity(itemEntity);
					this.thrower
						.world
						.spawnEntity(new ExperienceOrbEntity(this.thrower.world, this.thrower.x, this.thrower.y + 0.5, this.thrower.z + 0.5, this.random.nextInt(6) + 1));
					Item item = itemStack.getItem();
					if (item == Items.RAW_FISH || item == Items.COOKED_FISH) {
						this.thrower.incrementStat(Stats.field_14357, 1);
					}
				}

				i = 1;
			}

			if (this.inGround) {
				i = 2;
			}

			this.remove();
			return i;
		} else {
			return 0;
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
		if (this.thrower != null) {
			double d = this.thrower.x - this.x;
			double e = this.thrower.y - this.y;
			double f = this.thrower.z - this.z;
			double g = 0.1;
			this.caughtEntity.velocityX += d * 0.1;
			this.caughtEntity.velocityY += e * 0.1;
			this.caughtEntity.velocityZ += f * 0.1;
		}
	}

	@Override
	protected boolean canClimb() {
		return false;
	}

	@Override
	public void remove() {
		super.remove();
		if (this.thrower != null) {
			this.thrower.fishHook = null;
		}
	}

	public PlayerEntity getThrower() {
		return this.thrower;
	}

	static enum State {
		FLYING,
		HOOKED_IN_ENTITY,
		BOBBING;
	}
}
