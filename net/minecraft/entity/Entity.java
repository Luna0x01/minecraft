package net.minecraft.entity;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import net.minecraft.block.AbstractFluidBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.CommandStats;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public abstract class Entity implements CommandSource {
	private static final Box HITBOX = new Box(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
	private static int entityCount;
	private int entityId;
	public double renderDistanceMultiplier;
	public boolean inanimate;
	public Entity rider;
	public Entity vehicle;
	public boolean teleporting;
	public World world;
	public double prevX;
	public double prevY;
	public double prevZ;
	public double x;
	public double y;
	public double z;
	public double velocityX;
	public double velocityY;
	public double velocityZ;
	public float yaw;
	public float pitch;
	public float prevYaw;
	public float prevPitch;
	private Box boundingBox;
	public boolean onGround;
	public boolean horizontalCollision;
	public boolean verticalCollision;
	public boolean colliding;
	public boolean velocityModified;
	protected boolean inLava;
	private boolean outsideWorldBorder;
	public boolean removed;
	public float width;
	public float height;
	public float prevHorizontalSpeed;
	public float horizontalSpeed;
	public float distanceTraveled;
	public float fallDistance;
	private int field_3233;
	public double prevTickX;
	public double prevTickY;
	public double prevTickZ;
	public float stepHeight;
	public boolean noClip;
	public float pushSpeedReduction;
	protected Random random;
	public int ticksAlive;
	public int fireResistance;
	private int fireTicks;
	protected boolean touchingWater;
	public int timeUntilRegen;
	protected boolean firstUpdate;
	protected boolean isFireImmune;
	protected DataTracker dataTracker;
	private double vehiclePitch;
	private double vehicleYaw;
	public boolean updateNeeded;
	public int chunkX;
	public int chunkY;
	public int chunkZ;
	public int trackedX;
	public int trackedY;
	public int trackedZ;
	public boolean ignoreCameraFrustum;
	public boolean velocityDirty;
	public int netherPortalCooldown;
	protected boolean changingDimension;
	protected int netherPortalTime;
	public int dimension;
	protected BlockPos lastPortalBlockPos;
	protected Vec3d lastPortalVec3d;
	protected Direction teleportDirection;
	private boolean invulnerable;
	protected UUID playerUuid;
	private final CommandStats commandStats;

	public int getEntityId() {
		return this.entityId;
	}

	public void setEntityId(int id) {
		this.entityId = id;
	}

	public void kill() {
		this.remove();
	}

	public Entity(World world) {
		this.entityId = entityCount++;
		this.renderDistanceMultiplier = 1.0;
		this.boundingBox = HITBOX;
		this.width = 0.6F;
		this.height = 1.8F;
		this.field_3233 = 1;
		this.random = new Random();
		this.fireResistance = 1;
		this.firstUpdate = true;
		this.playerUuid = MathHelper.randomUuid(this.random);
		this.commandStats = new CommandStats();
		this.world = world;
		this.updatePosition(0.0, 0.0, 0.0);
		if (world != null) {
			this.dimension = world.dimension.getType();
		}

		this.dataTracker = new DataTracker(this);
		this.dataTracker.track(0, (byte)0);
		this.dataTracker.track(1, (short)300);
		this.dataTracker.track(3, (byte)0);
		this.dataTracker.track(2, "");
		this.dataTracker.track(4, (byte)0);
		this.initDataTracker();
	}

	protected abstract void initDataTracker();

	public DataTracker getDataTracker() {
		return this.dataTracker;
	}

	public boolean equals(Object object) {
		return object instanceof Entity ? ((Entity)object).entityId == this.entityId : false;
	}

	public int hashCode() {
		return this.entityId;
	}

	protected void afterSpawn() {
		if (this.world != null) {
			while (this.y > 0.0 && this.y < 256.0) {
				this.updatePosition(this.x, this.y, this.z);
				if (this.world.doesBoxCollide(this, this.getBoundingBox()).isEmpty()) {
					break;
				}

				this.y++;
			}

			this.velocityX = this.velocityY = this.velocityZ = 0.0;
			this.pitch = 0.0F;
		}
	}

	public void remove() {
		this.removed = true;
	}

	protected void setBounds(float width, float height) {
		if (width != this.width || height != this.height) {
			float f = this.width;
			this.width = width;
			this.height = height;
			this.setBoundingBox(
				new Box(
					this.getBoundingBox().minX,
					this.getBoundingBox().minY,
					this.getBoundingBox().minZ,
					this.getBoundingBox().minX + (double)this.width,
					this.getBoundingBox().minY + (double)this.height,
					this.getBoundingBox().minZ + (double)this.width
				)
			);
			if (this.width > f && !this.firstUpdate && !this.world.isClient) {
				this.move((double)(f - this.width), 0.0, (double)(f - this.width));
			}
		}
	}

	protected void setRotation(float yaw, float pitch) {
		this.yaw = yaw % 360.0F;
		this.pitch = pitch % 360.0F;
	}

	public void updatePosition(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		float f = this.width / 2.0F;
		float g = this.height;
		this.setBoundingBox(new Box(x - (double)f, y, z - (double)f, x + (double)f, y + (double)g, z + (double)f));
	}

	public void increaseTransforms(float yaw, float pitch) {
		float f = this.pitch;
		float g = this.yaw;
		this.yaw = (float)((double)this.yaw + (double)yaw * 0.15);
		this.pitch = (float)((double)this.pitch - (double)pitch * 0.15);
		this.pitch = MathHelper.clamp(this.pitch, -90.0F, 90.0F);
		this.prevPitch = this.prevPitch + (this.pitch - f);
		this.prevYaw = this.prevYaw + (this.yaw - g);
	}

	public void tick() {
		this.baseTick();
	}

	public void baseTick() {
		this.world.profiler.push("entityBaseTick");
		if (this.vehicle != null && this.vehicle.removed) {
			this.vehicle = null;
		}

		this.prevHorizontalSpeed = this.horizontalSpeed;
		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
		this.prevPitch = this.pitch;
		this.prevYaw = this.yaw;
		if (!this.world.isClient && this.world instanceof ServerWorld) {
			this.world.profiler.push("portal");
			MinecraftServer minecraftServer = ((ServerWorld)this.world).getServer();
			int i = this.getMaxNetherPortalTime();
			if (this.changingDimension) {
				if (minecraftServer.isNetherAllowed()) {
					if (this.vehicle == null && this.netherPortalTime++ >= i) {
						this.netherPortalTime = i;
						this.netherPortalCooldown = this.getDefaultNetherPortalCooldown();
						int j;
						if (this.world.dimension.getType() == -1) {
							j = 0;
						} else {
							j = -1;
						}

						this.teleportToDimension(j);
					}

					this.changingDimension = false;
				}
			} else {
				if (this.netherPortalTime > 0) {
					this.netherPortalTime -= 4;
				}

				if (this.netherPortalTime < 0) {
					this.netherPortalTime = 0;
				}
			}

			if (this.netherPortalCooldown > 0) {
				this.netherPortalCooldown--;
			}

			this.world.profiler.pop();
		}

		this.attemptSprintingParticles();
		this.updateWaterState();
		if (this.world.isClient) {
			this.fireTicks = 0;
		} else if (this.fireTicks > 0) {
			if (this.isFireImmune) {
				this.fireTicks -= 4;
				if (this.fireTicks < 0) {
					this.fireTicks = 0;
				}
			} else {
				if (this.fireTicks % 20 == 0) {
					this.damage(DamageSource.ON_FIRE, 1.0F);
				}

				this.fireTicks--;
			}
		}

		if (this.isTouchingLava()) {
			this.setOnFireFromLava();
			this.fallDistance *= 0.5F;
		}

		if (this.y < -64.0) {
			this.destroy();
		}

		if (!this.world.isClient) {
			this.setFlag(0, this.fireTicks > 0);
		}

		this.firstUpdate = false;
		this.world.profiler.pop();
	}

	public int getMaxNetherPortalTime() {
		return 0;
	}

	protected void setOnFireFromLava() {
		if (!this.isFireImmune) {
			this.damage(DamageSource.LAVA, 4.0F);
			this.setOnFireFor(15);
		}
	}

	public void setOnFireFor(int seconds) {
		int i = seconds * 20;
		i = ProtectionEnchantment.method_4659(this, i);
		if (this.fireTicks < i) {
			this.fireTicks = i;
		}
	}

	public void extinguish() {
		this.fireTicks = 0;
	}

	protected void destroy() {
		this.remove();
	}

	public boolean doesNotCollide(double offsetX, double offsetY, double offsetZ) {
		Box box = this.getBoundingBox().offset(offsetX, offsetY, offsetZ);
		return this.doesNotCollide(box);
	}

	private boolean doesNotCollide(Box box) {
		return this.world.doesBoxCollide(this, box).isEmpty() && !this.world.containsFluid(box);
	}

	public void move(double velocityX, double velocityY, double velocityZ) {
		if (this.noClip) {
			this.setBoundingBox(this.getBoundingBox().offset(velocityX, velocityY, velocityZ));
			this.updateSubmergedInWaterState();
		} else {
			this.world.profiler.push("move");
			double d = this.x;
			double e = this.y;
			double f = this.z;
			if (this.inLava) {
				this.inLava = false;
				velocityX *= 0.25;
				velocityY *= 0.05F;
				velocityZ *= 0.25;
				this.velocityX = 0.0;
				this.velocityY = 0.0;
				this.velocityZ = 0.0;
			}

			double g = velocityX;
			double h = velocityY;
			double i = velocityZ;
			boolean bl = this.onGround && this.isSneaking() && this instanceof PlayerEntity;
			if (bl) {
				double j;
				for (j = 0.05; velocityX != 0.0 && this.world.doesBoxCollide(this, this.getBoundingBox().offset(velocityX, -1.0, 0.0)).isEmpty(); g = velocityX) {
					if (velocityX < j && velocityX >= -j) {
						velocityX = 0.0;
					} else if (velocityX > 0.0) {
						velocityX -= j;
					} else {
						velocityX += j;
					}
				}

				for (; velocityZ != 0.0 && this.world.doesBoxCollide(this, this.getBoundingBox().offset(0.0, -1.0, velocityZ)).isEmpty(); i = velocityZ) {
					if (velocityZ < j && velocityZ >= -j) {
						velocityZ = 0.0;
					} else if (velocityZ > 0.0) {
						velocityZ -= j;
					} else {
						velocityZ += j;
					}
				}

				for (;
					velocityX != 0.0 && velocityZ != 0.0 && this.world.doesBoxCollide(this, this.getBoundingBox().offset(velocityX, -1.0, velocityZ)).isEmpty();
					i = velocityZ
				) {
					if (velocityX < j && velocityX >= -j) {
						velocityX = 0.0;
					} else if (velocityX > 0.0) {
						velocityX -= j;
					} else {
						velocityX += j;
					}

					g = velocityX;
					if (velocityZ < j && velocityZ >= -j) {
						velocityZ = 0.0;
					} else if (velocityZ > 0.0) {
						velocityZ -= j;
					} else {
						velocityZ += j;
					}
				}
			}

			List<Box> list = this.world.doesBoxCollide(this, this.getBoundingBox().stretch(velocityX, velocityY, velocityZ));
			Box box = this.getBoundingBox();

			for (Box box2 : list) {
				velocityY = box2.method_589(this.getBoundingBox(), velocityY);
			}

			this.setBoundingBox(this.getBoundingBox().offset(0.0, velocityY, 0.0));
			boolean bl2 = this.onGround || h != velocityY && h < 0.0;

			for (Box box3 : list) {
				velocityX = box3.method_583(this.getBoundingBox(), velocityX);
			}

			this.setBoundingBox(this.getBoundingBox().offset(velocityX, 0.0, 0.0));

			for (Box box4 : list) {
				velocityZ = box4.method_594(this.getBoundingBox(), velocityZ);
			}

			this.setBoundingBox(this.getBoundingBox().offset(0.0, 0.0, velocityZ));
			if (this.stepHeight > 0.0F && bl2 && (g != velocityX || i != velocityZ)) {
				double k = velocityX;
				double l = velocityY;
				double m = velocityZ;
				Box box5 = this.getBoundingBox();
				this.setBoundingBox(box);
				velocityY = (double)this.stepHeight;
				List<Box> list2 = this.world.doesBoxCollide(this, this.getBoundingBox().stretch(g, velocityY, i));
				Box box6 = this.getBoundingBox();
				Box box7 = box6.stretch(g, 0.0, i);
				double n = velocityY;

				for (Box box8 : list2) {
					n = box8.method_589(box7, n);
				}

				box6 = box6.offset(0.0, n, 0.0);
				double o = g;

				for (Box box9 : list2) {
					o = box9.method_583(box6, o);
				}

				box6 = box6.offset(o, 0.0, 0.0);
				double p = i;

				for (Box box10 : list2) {
					p = box10.method_594(box6, p);
				}

				box6 = box6.offset(0.0, 0.0, p);
				Box box11 = this.getBoundingBox();
				double q = velocityY;

				for (Box box12 : list2) {
					q = box12.method_589(box11, q);
				}

				box11 = box11.offset(0.0, q, 0.0);
				double r = g;

				for (Box box13 : list2) {
					r = box13.method_583(box11, r);
				}

				box11 = box11.offset(r, 0.0, 0.0);
				double s = i;

				for (Box box14 : list2) {
					s = box14.method_594(box11, s);
				}

				box11 = box11.offset(0.0, 0.0, s);
				double t = o * o + p * p;
				double u = r * r + s * s;
				if (t > u) {
					velocityX = o;
					velocityZ = p;
					velocityY = -n;
					this.setBoundingBox(box6);
				} else {
					velocityX = r;
					velocityZ = s;
					velocityY = -q;
					this.setBoundingBox(box11);
				}

				for (Box box15 : list2) {
					velocityY = box15.method_589(this.getBoundingBox(), velocityY);
				}

				this.setBoundingBox(this.getBoundingBox().offset(0.0, velocityY, 0.0));
				if (k * k + m * m >= velocityX * velocityX + velocityZ * velocityZ) {
					velocityX = k;
					velocityY = l;
					velocityZ = m;
					this.setBoundingBox(box5);
				}
			}

			this.world.profiler.pop();
			this.world.profiler.push("rest");
			this.updateSubmergedInWaterState();
			this.horizontalCollision = g != velocityX || i != velocityZ;
			this.verticalCollision = h != velocityY;
			this.onGround = this.verticalCollision && h < 0.0;
			this.colliding = this.horizontalCollision || this.verticalCollision;
			int v = MathHelper.floor(this.x);
			int w = MathHelper.floor(this.y - 0.2F);
			int x = MathHelper.floor(this.z);
			BlockPos blockPos = new BlockPos(v, w, x);
			Block block = this.world.getBlockState(blockPos).getBlock();
			if (block.getMaterial() == Material.AIR) {
				Block block2 = this.world.getBlockState(blockPos.down()).getBlock();
				if (block2 instanceof FenceBlock || block2 instanceof WallBlock || block2 instanceof FenceGateBlock) {
					block = block2;
					blockPos = blockPos.down();
				}
			}

			this.fall(velocityY, this.onGround, block, blockPos);
			if (g != velocityX) {
				this.velocityX = 0.0;
			}

			if (i != velocityZ) {
				this.velocityZ = 0.0;
			}

			if (h != velocityY) {
				block.setEntityVelocity(this.world, this);
			}

			if (this.canClimb() && !bl && this.vehicle == null) {
				double y = this.x - d;
				double z = this.y - e;
				double aa = this.z - f;
				if (block != Blocks.LADDER) {
					z = 0.0;
				}

				if (block != null && this.onGround) {
					block.onSteppedOn(this.world, blockPos, this);
				}

				this.horizontalSpeed = (float)((double)this.horizontalSpeed + (double)MathHelper.sqrt(y * y + aa * aa) * 0.6);
				this.distanceTraveled = (float)((double)this.distanceTraveled + (double)MathHelper.sqrt(y * y + z * z + aa * aa) * 0.6);
				if (this.distanceTraveled > (float)this.field_3233 && block.getMaterial() != Material.AIR) {
					this.field_3233 = (int)this.distanceTraveled + 1;
					if (this.isTouchingWater()) {
						float ab = MathHelper.sqrt(this.velocityX * this.velocityX * 0.2F + this.velocityY * this.velocityY + this.velocityZ * this.velocityZ * 0.2F) * 0.35F;
						if (ab > 1.0F) {
							ab = 1.0F;
						}

						this.playSound(this.getSwimSound(), ab, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
					}

					this.playStepSound(blockPos, block);
				}
			}

			try {
				this.checkBlockCollision();
			} catch (Throwable var52) {
				CrashReport crashReport = CrashReport.create(var52, "Checking entity block collision");
				CrashReportSection crashReportSection = crashReport.addElement("Entity being checked for collision");
				this.populateCrashReport(crashReportSection);
				throw new CrashException(crashReport);
			}

			boolean bl3 = this.tickFire();
			if (this.world.containsFireSource(this.getBoundingBox().increment(0.001, 0.001, 0.001))) {
				this.burn(1);
				if (!bl3) {
					this.fireTicks++;
					if (this.fireTicks == 0) {
						this.setOnFireFor(8);
					}
				}
			} else if (this.fireTicks <= 0) {
				this.fireTicks = -this.fireResistance;
			}

			if (bl3 && this.fireTicks > 0) {
				this.playSound("random.fizz", 0.7F, 1.6F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
				this.fireTicks = -this.fireResistance;
			}

			this.world.profiler.pop();
		}
	}

	private void updateSubmergedInWaterState() {
		this.x = (this.getBoundingBox().minX + this.getBoundingBox().maxX) / 2.0;
		this.y = this.getBoundingBox().minY;
		this.z = (this.getBoundingBox().minZ + this.getBoundingBox().maxZ) / 2.0;
	}

	protected String getSwimSound() {
		return "game.neutral.swim";
	}

	protected void checkBlockCollision() {
		BlockPos blockPos = new BlockPos(this.getBoundingBox().minX + 0.001, this.getBoundingBox().minY + 0.001, this.getBoundingBox().minZ + 0.001);
		BlockPos blockPos2 = new BlockPos(this.getBoundingBox().maxX - 0.001, this.getBoundingBox().maxY - 0.001, this.getBoundingBox().maxZ - 0.001);
		if (this.world.isRegionLoaded(blockPos, blockPos2)) {
			for (int i = blockPos.getX(); i <= blockPos2.getX(); i++) {
				for (int j = blockPos.getY(); j <= blockPos2.getY(); j++) {
					for (int k = blockPos.getZ(); k <= blockPos2.getZ(); k++) {
						BlockPos blockPos3 = new BlockPos(i, j, k);
						BlockState blockState = this.world.getBlockState(blockPos3);

						try {
							blockState.getBlock().onEntityCollision(this.world, blockPos3, blockState, this);
						} catch (Throwable var11) {
							CrashReport crashReport = CrashReport.create(var11, "Colliding entity with block");
							CrashReportSection crashReportSection = crashReport.addElement("Block being collided with");
							CrashReportSection.addBlockInfo(crashReportSection, blockPos3, blockState);
							throw new CrashException(crashReport);
						}
					}
				}
			}
		}
	}

	protected void playStepSound(BlockPos pos, Block block) {
		Block.Sound sound = block.sound;
		if (this.world.getBlockState(pos.up()).getBlock() == Blocks.SNOW_LAYER) {
			sound = Blocks.SNOW_LAYER.sound;
			this.playSound(sound.getStepSound(), sound.getVolume() * 0.15F, sound.getPitch());
		} else if (!block.getMaterial().isFluid()) {
			this.playSound(sound.getStepSound(), sound.getVolume() * 0.15F, sound.getPitch());
		}
	}

	public void playSound(String id, float volume, float pitch) {
		if (!this.isSilent()) {
			this.world.playSound(this, id, volume, pitch);
		}
	}

	public boolean isSilent() {
		return this.dataTracker.getByte(4) == 1;
	}

	public void setSilent(boolean silent) {
		this.dataTracker.setProperty(4, Byte.valueOf((byte)(silent ? 1 : 0)));
	}

	protected boolean canClimb() {
		return true;
	}

	protected void fall(double heightDifference, boolean onGround, Block landedBlock, BlockPos landedPosition) {
		if (onGround) {
			if (this.fallDistance > 0.0F) {
				if (landedBlock != null) {
					landedBlock.onLandedUpon(this.world, landedPosition, this, this.fallDistance);
				} else {
					this.handleFallDamage(this.fallDistance, 1.0F);
				}

				this.fallDistance = 0.0F;
			}
		} else if (heightDifference < 0.0) {
			this.fallDistance = (float)((double)this.fallDistance - heightDifference);
		}
	}

	public Box getBox() {
		return null;
	}

	protected void burn(int time) {
		if (!this.isFireImmune) {
			this.damage(DamageSource.FIRE, (float)time);
		}
	}

	public final boolean isFireImmune() {
		return this.isFireImmune;
	}

	public void handleFallDamage(float fallDistance, float damageMultiplier) {
		if (this.rider != null) {
			this.rider.handleFallDamage(fallDistance, damageMultiplier);
		}
	}

	public boolean tickFire() {
		return this.touchingWater
			|| this.world.hasRain(new BlockPos(this.x, this.y, this.z))
			|| this.world.hasRain(new BlockPos(this.x, this.y + (double)this.height, this.z));
	}

	public boolean isTouchingWater() {
		return this.touchingWater;
	}

	public boolean updateWaterState() {
		if (this.world.method_3610(this.getBoundingBox().expand(0.0, -0.4F, 0.0).increment(0.001, 0.001, 0.001), Material.WATER, this)) {
			if (!this.touchingWater && !this.firstUpdate) {
				this.onSwimmingStart();
			}

			this.fallDistance = 0.0F;
			this.touchingWater = true;
			this.fireTicks = 0;
		} else {
			this.touchingWater = false;
		}

		return this.touchingWater;
	}

	protected void onSwimmingStart() {
		float f = MathHelper.sqrt(this.velocityX * this.velocityX * 0.2F + this.velocityY * this.velocityY + this.velocityZ * this.velocityZ * 0.2F) * 0.2F;
		if (f > 1.0F) {
			f = 1.0F;
		}

		this.playSound(this.getSplashSound(), f, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
		float g = (float)MathHelper.floor(this.getBoundingBox().minY);

		for (int i = 0; (float)i < 1.0F + this.width * 20.0F; i++) {
			float h = (this.random.nextFloat() * 2.0F - 1.0F) * this.width;
			float j = (this.random.nextFloat() * 2.0F - 1.0F) * this.width;
			this.world
				.addParticle(
					ParticleType.BUBBLE,
					this.x + (double)h,
					(double)(g + 1.0F),
					this.z + (double)j,
					this.velocityX,
					this.velocityY - (double)(this.random.nextFloat() * 0.2F),
					this.velocityZ
				);
		}

		for (int k = 0; (float)k < 1.0F + this.width * 20.0F; k++) {
			float l = (this.random.nextFloat() * 2.0F - 1.0F) * this.width;
			float m = (this.random.nextFloat() * 2.0F - 1.0F) * this.width;
			this.world.addParticle(ParticleType.WATER, this.x + (double)l, (double)(g + 1.0F), this.z + (double)m, this.velocityX, this.velocityY, this.velocityZ);
		}
	}

	public void attemptSprintingParticles() {
		if (this.isSprinting() && !this.isTouchingWater()) {
			this.spawnSprintingParticles();
		}
	}

	protected void spawnSprintingParticles() {
		int i = MathHelper.floor(this.x);
		int j = MathHelper.floor(this.y - 0.2F);
		int k = MathHelper.floor(this.z);
		BlockPos blockPos = new BlockPos(i, j, k);
		BlockState blockState = this.world.getBlockState(blockPos);
		Block block = blockState.getBlock();
		if (block.getBlockType() != -1) {
			this.world
				.addParticle(
					ParticleType.BLOCK_CRACK,
					this.x + ((double)this.random.nextFloat() - 0.5) * (double)this.width,
					this.getBoundingBox().minY + 0.1,
					this.z + ((double)this.random.nextFloat() - 0.5) * (double)this.width,
					-this.velocityX * 4.0,
					1.5,
					-this.velocityZ * 4.0,
					Block.getByBlockState(blockState)
				);
		}
	}

	protected String getSplashSound() {
		return "game.neutral.swim.splash";
	}

	public boolean isSubmergedIn(Material material) {
		double d = this.y + (double)this.getEyeHeight();
		BlockPos blockPos = new BlockPos(this.x, d, this.z);
		BlockState blockState = this.world.getBlockState(blockPos);
		Block block = blockState.getBlock();
		if (block.getMaterial() == material) {
			float f = AbstractFluidBlock.getHeightPercent(blockState.getBlock().getData(blockState)) - 0.11111111F;
			float g = (float)(blockPos.getY() + 1) - f;
			boolean bl = d < (double)g;
			return !bl && this instanceof PlayerEntity ? false : bl;
		} else {
			return false;
		}
	}

	public boolean isTouchingLava() {
		return this.world.containsMaterial(this.getBoundingBox().expand(-0.1F, -0.4F, -0.1F), Material.LAVA);
	}

	public void updateVelocity(float f, float g, float h) {
		float i = f * f + g * g;
		if (!(i < 1.0E-4F)) {
			i = MathHelper.sqrt(i);
			if (i < 1.0F) {
				i = 1.0F;
			}

			i = h / i;
			f *= i;
			g *= i;
			float j = MathHelper.sin(this.yaw * (float) Math.PI / 180.0F);
			float k = MathHelper.cos(this.yaw * (float) Math.PI / 180.0F);
			this.velocityX += (double)(f * k - g * j);
			this.velocityZ += (double)(g * k + f * j);
		}
	}

	public int getLightmapCoordinates(float f) {
		BlockPos blockPos = new BlockPos(this.x, this.y + (double)this.getEyeHeight(), this.z);
		return this.world.blockExists(blockPos) ? this.world.getLight(blockPos, 0) : 0;
	}

	public float getBrightnessAtEyes(float f) {
		BlockPos blockPos = new BlockPos(this.x, this.y + (double)this.getEyeHeight(), this.z);
		return this.world.blockExists(blockPos) ? this.world.getBrightness(blockPos) : 0.0F;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public void updatePositionAndAngles(double x, double y, double z, float yaw, float pitch) {
		this.prevX = this.x = x;
		this.prevY = this.y = y;
		this.prevZ = this.z = z;
		this.prevYaw = this.yaw = yaw;
		this.prevPitch = this.pitch = pitch;
		double d = (double)(this.prevYaw - yaw);
		if (d < -180.0) {
			this.prevYaw += 360.0F;
		}

		if (d >= 180.0) {
			this.prevYaw -= 360.0F;
		}

		this.updatePosition(this.x, this.y, this.z);
		this.setRotation(yaw, pitch);
	}

	public void refreshPositionAndAngles(BlockPos pos, float yaw, float pitch) {
		this.refreshPositionAndAngles((double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5, yaw, pitch);
	}

	public void refreshPositionAndAngles(double x, double y, double z, float yaw, float pitch) {
		this.prevTickX = this.prevX = this.x = x;
		this.prevTickY = this.prevY = this.y = y;
		this.prevTickZ = this.prevZ = this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
		this.updatePosition(this.x, this.y, this.z);
	}

	public float distanceTo(Entity entity) {
		float f = (float)(this.x - entity.x);
		float g = (float)(this.y - entity.y);
		float h = (float)(this.z - entity.z);
		return MathHelper.sqrt(f * f + g * g + h * h);
	}

	public double squaredDistanceTo(double x, double y, double z) {
		double d = this.x - x;
		double e = this.y - y;
		double f = this.z - z;
		return d * d + e * e + f * f;
	}

	public double squaredDistanceTo(BlockPos pos) {
		return pos.squaredDistanceTo(this.x, this.y, this.z);
	}

	public double squaredDistanceToCenter(BlockPos pos) {
		return pos.squaredDistanceToCenter(this.x, this.y, this.z);
	}

	public double distanceTo(double x, double y, double z) {
		double d = this.x - x;
		double e = this.y - y;
		double f = this.z - z;
		return (double)MathHelper.sqrt(d * d + e * e + f * f);
	}

	public double squaredDistanceTo(Entity entity) {
		double d = this.x - entity.x;
		double e = this.y - entity.y;
		double f = this.z - entity.z;
		return d * d + e * e + f * f;
	}

	public void onPlayerCollision(PlayerEntity player) {
	}

	public void pushAwayFrom(Entity entity) {
		if (entity.rider != this && entity.vehicle != this) {
			if (!entity.noClip && !this.noClip) {
				double d = entity.x - this.x;
				double e = entity.z - this.z;
				double f = MathHelper.absMax(d, e);
				if (f >= 0.01F) {
					f = (double)MathHelper.sqrt(f);
					d /= f;
					e /= f;
					double g = 1.0 / f;
					if (g > 1.0) {
						g = 1.0;
					}

					d *= g;
					e *= g;
					d *= 0.05F;
					e *= 0.05F;
					d *= (double)(1.0F - this.pushSpeedReduction);
					e *= (double)(1.0F - this.pushSpeedReduction);
					if (this.rider == null) {
						this.addVelocity(-d, 0.0, -e);
					}

					if (entity.rider == null) {
						entity.addVelocity(d, 0.0, e);
					}
				}
			}
		}
	}

	public void addVelocity(double x, double y, double z) {
		this.velocityX += x;
		this.velocityY += y;
		this.velocityZ += z;
		this.velocityDirty = true;
	}

	protected void scheduleVelocityUpdate() {
		this.velocityModified = true;
	}

	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else {
			this.scheduleVelocityUpdate();
			return false;
		}
	}

	public Vec3d getRotationVector(float vector) {
		if (vector == 1.0F) {
			return this.getRotationVector(this.pitch, this.yaw);
		} else {
			float f = this.prevPitch + (this.pitch - this.prevPitch) * vector;
			float g = this.prevYaw + (this.yaw - this.prevYaw) * vector;
			return this.getRotationVector(f, g);
		}
	}

	protected final Vec3d getRotationVector(float pitch, float yaw) {
		float f = MathHelper.cos(-yaw * (float) (Math.PI / 180.0) - (float) Math.PI);
		float g = MathHelper.sin(-yaw * (float) (Math.PI / 180.0) - (float) Math.PI);
		float h = -MathHelper.cos(-pitch * (float) (Math.PI / 180.0));
		float i = MathHelper.sin(-pitch * (float) (Math.PI / 180.0));
		return new Vec3d((double)(g * h), (double)i, (double)(f * h));
	}

	public Vec3d getCameraPosVec(float tickDelta) {
		if (tickDelta == 1.0F) {
			return new Vec3d(this.x, this.y + (double)this.getEyeHeight(), this.z);
		} else {
			double d = this.prevX + (this.x - this.prevX) * (double)tickDelta;
			double e = this.prevY + (this.y - this.prevY) * (double)tickDelta + (double)this.getEyeHeight();
			double f = this.prevZ + (this.z - this.prevZ) * (double)tickDelta;
			return new Vec3d(d, e, f);
		}
	}

	public BlockHitResult rayTrace(double maxDistance, float tickDelta) {
		Vec3d vec3d = this.getCameraPosVec(tickDelta);
		Vec3d vec3d2 = this.getRotationVector(tickDelta);
		Vec3d vec3d3 = vec3d.add(vec3d2.x * maxDistance, vec3d2.y * maxDistance, vec3d2.z * maxDistance);
		return this.world.rayTrace(vec3d, vec3d3, false, false, true);
	}

	public boolean collides() {
		return false;
	}

	public boolean isPushable() {
		return false;
	}

	public void updateKilledAdvancementCriterion(Entity entity, int score) {
	}

	public boolean shouldRender(double x, double y, double z) {
		double d = this.x - x;
		double e = this.y - y;
		double f = this.z - z;
		double g = d * d + e * e + f * f;
		return this.shouldRender(g);
	}

	public boolean shouldRender(double distance) {
		double d = this.getBoundingBox().getAverage();
		if (Double.isNaN(d)) {
			d = 1.0;
		}

		d *= 64.0 * this.renderDistanceMultiplier;
		return distance < d * d;
	}

	public boolean saveSelfToNbt(NbtCompound nbt) {
		String string = this.getSavedEntityId();
		if (!this.removed && string != null) {
			nbt.putString("id", string);
			this.writePlayerData(nbt);
			return true;
		} else {
			return false;
		}
	}

	public boolean saveToNbt(NbtCompound nbt) {
		String string = this.getSavedEntityId();
		if (!this.removed && string != null && this.rider == null) {
			nbt.putString("id", string);
			this.writePlayerData(nbt);
			return true;
		} else {
			return false;
		}
	}

	public void writePlayerData(NbtCompound nbt) {
		try {
			nbt.put("Pos", this.toListNbt(this.x, this.y, this.z));
			nbt.put("Motion", this.toListNbt(this.velocityX, this.velocityY, this.velocityZ));
			nbt.put("Rotation", this.toListNbt(this.yaw, this.pitch));
			nbt.putFloat("FallDistance", this.fallDistance);
			nbt.putShort("Fire", (short)this.fireTicks);
			nbt.putShort("Air", (short)this.getAir());
			nbt.putBoolean("OnGround", this.onGround);
			nbt.putInt("Dimension", this.dimension);
			nbt.putBoolean("Invulnerable", this.invulnerable);
			nbt.putInt("PortalCooldown", this.netherPortalCooldown);
			nbt.putLong("UUIDMost", this.getUuid().getMostSignificantBits());
			nbt.putLong("UUIDLeast", this.getUuid().getLeastSignificantBits());
			if (this.getCustomName() != null && this.getCustomName().length() > 0) {
				nbt.putString("CustomName", this.getCustomName());
				nbt.putBoolean("CustomNameVisible", this.isCustomNameVisible());
			}

			this.commandStats.toNbt(nbt);
			if (this.isSilent()) {
				nbt.putBoolean("Silent", this.isSilent());
			}

			this.writeCustomDataToNbt(nbt);
			if (this.vehicle != null) {
				NbtCompound nbtCompound = new NbtCompound();
				if (this.vehicle.saveSelfToNbt(nbtCompound)) {
					nbt.put("Riding", nbtCompound);
				}
			}
		} catch (Throwable var5) {
			CrashReport crashReport = CrashReport.create(var5, "Saving entity NBT");
			CrashReportSection crashReportSection = crashReport.addElement("Entity being saved");
			this.populateCrashReport(crashReportSection);
			throw new CrashException(crashReport);
		}
	}

	public void fromNbt(NbtCompound nbt) {
		try {
			NbtList nbtList = nbt.getList("Pos", 6);
			NbtList nbtList2 = nbt.getList("Motion", 6);
			NbtList nbtList3 = nbt.getList("Rotation", 5);
			this.velocityX = nbtList2.getDouble(0);
			this.velocityY = nbtList2.getDouble(1);
			this.velocityZ = nbtList2.getDouble(2);
			if (Math.abs(this.velocityX) > 10.0) {
				this.velocityX = 0.0;
			}

			if (Math.abs(this.velocityY) > 10.0) {
				this.velocityY = 0.0;
			}

			if (Math.abs(this.velocityZ) > 10.0) {
				this.velocityZ = 0.0;
			}

			this.prevX = this.prevTickX = this.x = nbtList.getDouble(0);
			this.prevY = this.prevTickY = this.y = nbtList.getDouble(1);
			this.prevZ = this.prevTickZ = this.z = nbtList.getDouble(2);
			this.prevYaw = this.yaw = nbtList3.getFloat(0);
			this.prevPitch = this.pitch = nbtList3.getFloat(1);
			this.setHeadYaw(this.yaw);
			this.setYaw(this.yaw);
			this.fallDistance = nbt.getFloat("FallDistance");
			this.fireTicks = nbt.getShort("Fire");
			this.setAir(nbt.getShort("Air"));
			this.onGround = nbt.getBoolean("OnGround");
			this.dimension = nbt.getInt("Dimension");
			this.invulnerable = nbt.getBoolean("Invulnerable");
			this.netherPortalCooldown = nbt.getInt("PortalCooldown");
			if (nbt.contains("UUIDMost", 4) && nbt.contains("UUIDLeast", 4)) {
				this.playerUuid = new UUID(nbt.getLong("UUIDMost"), nbt.getLong("UUIDLeast"));
			} else if (nbt.contains("UUID", 8)) {
				this.playerUuid = UUID.fromString(nbt.getString("UUID"));
			}

			this.updatePosition(this.x, this.y, this.z);
			this.setRotation(this.yaw, this.pitch);
			if (nbt.contains("CustomName", 8) && nbt.getString("CustomName").length() > 0) {
				this.setCustomName(nbt.getString("CustomName"));
			}

			this.setCustomNameVisible(nbt.getBoolean("CustomNameVisible"));
			this.commandStats.fromNbt(nbt);
			this.setSilent(nbt.getBoolean("Silent"));
			this.readCustomDataFromNbt(nbt);
			if (this.shouldSetPositionOnLoad()) {
				this.updatePosition(this.x, this.y, this.z);
			}
		} catch (Throwable var5) {
			CrashReport crashReport = CrashReport.create(var5, "Loading entity NBT");
			CrashReportSection crashReportSection = crashReport.addElement("Entity being loaded");
			this.populateCrashReport(crashReportSection);
			throw new CrashException(crashReport);
		}
	}

	protected boolean shouldSetPositionOnLoad() {
		return true;
	}

	protected final String getSavedEntityId() {
		return EntityType.getEntityName(this);
	}

	protected abstract void readCustomDataFromNbt(NbtCompound nbt);

	protected abstract void writeCustomDataToNbt(NbtCompound nbt);

	public void method_6097() {
	}

	protected NbtList toListNbt(double... values) {
		NbtList nbtList = new NbtList();

		for (double d : values) {
			nbtList.add(new NbtDouble(d));
		}

		return nbtList;
	}

	protected NbtList toListNbt(float... values) {
		NbtList nbtList = new NbtList();

		for (float f : values) {
			nbtList.add(new NbtFloat(f));
		}

		return nbtList;
	}

	public ItemEntity dropItem(Item item, int yOffset) {
		return this.dropItem(item, yOffset, 0.0F);
	}

	public ItemEntity dropItem(Item item, int count, float yOffset) {
		return this.dropItem(new ItemStack(item, count, 0), yOffset);
	}

	public ItemEntity dropItem(ItemStack stack, float yOffset) {
		if (stack.count != 0 && stack.getItem() != null) {
			ItemEntity itemEntity = new ItemEntity(this.world, this.x, this.y + (double)yOffset, this.z, stack);
			itemEntity.setToDefaultPickupDelay();
			this.world.spawnEntity(itemEntity);
			return itemEntity;
		} else {
			return null;
		}
	}

	public boolean isAlive() {
		return !this.removed;
	}

	public boolean isInsideWall() {
		if (this.noClip) {
			return false;
		} else {
			BlockPos.Mutable mutable = new BlockPos.Mutable(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);

			for (int i = 0; i < 8; i++) {
				int j = MathHelper.floor(this.y + (double)(((float)((i >> 0) % 2) - 0.5F) * 0.1F) + (double)this.getEyeHeight());
				int k = MathHelper.floor(this.x + (double)(((float)((i >> 1) % 2) - 0.5F) * this.width * 0.8F));
				int l = MathHelper.floor(this.z + (double)(((float)((i >> 2) % 2) - 0.5F) * this.width * 0.8F));
				if (mutable.getX() != k || mutable.getY() != j || mutable.getZ() != l) {
					mutable.setPosition(k, j, l);
					if (this.world.getBlockState(mutable).getBlock().isLeafBlock()) {
						return true;
					}
				}
			}

			return false;
		}
	}

	public boolean openInventory(PlayerEntity player) {
		return false;
	}

	public Box getHardCollisionBox(Entity collidingEntity) {
		return null;
	}

	public void tickRiding() {
		if (this.vehicle.removed) {
			this.vehicle = null;
		} else {
			this.velocityX = 0.0;
			this.velocityY = 0.0;
			this.velocityZ = 0.0;
			this.tick();
			if (this.vehicle != null) {
				this.vehicle.updatePassengerPosition();
				this.vehicleYaw = this.vehicleYaw + (double)(this.vehicle.yaw - this.vehicle.prevYaw);
				this.vehiclePitch = this.vehiclePitch + (double)(this.vehicle.pitch - this.vehicle.prevPitch);

				while (this.vehicleYaw >= 180.0) {
					this.vehicleYaw -= 360.0;
				}

				while (this.vehicleYaw < -180.0) {
					this.vehicleYaw += 360.0;
				}

				while (this.vehiclePitch >= 180.0) {
					this.vehiclePitch -= 360.0;
				}

				while (this.vehiclePitch < -180.0) {
					this.vehiclePitch += 360.0;
				}

				double d = this.vehicleYaw * 0.5;
				double e = this.vehiclePitch * 0.5;
				float f = 10.0F;
				if (d > (double)f) {
					d = (double)f;
				}

				if (d < (double)(-f)) {
					d = (double)(-f);
				}

				if (e > (double)f) {
					e = (double)f;
				}

				if (e < (double)(-f)) {
					e = (double)(-f);
				}

				this.vehicleYaw -= d;
				this.vehiclePitch -= e;
			}
		}
	}

	public void updatePassengerPosition() {
		if (this.rider != null) {
			this.rider.updatePosition(this.x, this.y + this.getMountedHeightOffset() + this.rider.getHeightOffset(), this.z);
		}
	}

	public double getHeightOffset() {
		return 0.0;
	}

	public double getMountedHeightOffset() {
		return (double)this.height * 0.75;
	}

	public void startRiding(Entity entity) {
		this.vehiclePitch = 0.0;
		this.vehicleYaw = 0.0;
		if (entity == null) {
			if (this.vehicle != null) {
				this.refreshPositionAndAngles(this.vehicle.x, this.vehicle.getBoundingBox().minY + (double)this.vehicle.height, this.vehicle.z, this.yaw, this.pitch);
				this.vehicle.rider = null;
			}

			this.vehicle = null;
		} else {
			if (this.vehicle != null) {
				this.vehicle.rider = null;
			}

			if (entity != null) {
				for (Entity entity2 = entity.vehicle; entity2 != null; entity2 = entity2.vehicle) {
					if (entity2 == this) {
						return;
					}
				}
			}

			this.vehicle = entity;
			entity.rider = this;
		}
	}

	public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
		this.updatePosition(x, y, z);
		this.setRotation(yaw, pitch);
		List<Box> list = this.world.doesBoxCollide(this, this.getBoundingBox().increment(0.03125, 0.0, 0.03125));
		if (!list.isEmpty()) {
			double d = 0.0;

			for (Box box : list) {
				if (box.maxY > d) {
					d = box.maxY;
				}
			}

			y += d - this.getBoundingBox().minY;
			this.updatePosition(x, y, z);
		}
	}

	public float getTargetingMargin() {
		return 0.1F;
	}

	public Vec3d getRotation() {
		return null;
	}

	public void setInNetherPortal(BlockPos pos) {
		if (this.netherPortalCooldown > 0) {
			this.netherPortalCooldown = this.getDefaultNetherPortalCooldown();
		} else {
			if (!this.world.isClient && !pos.equals(this.lastPortalBlockPos)) {
				this.lastPortalBlockPos = pos;
				BlockPattern.Result result = Blocks.NETHER_PORTAL.findPortal(this.world, pos);
				double d = result.getForwards().getAxis() == Direction.Axis.X ? (double)result.getFrontTopLeft().getZ() : (double)result.getFrontTopLeft().getX();
				double e = result.getForwards().getAxis() == Direction.Axis.X ? this.z : this.x;
				e = Math.abs(
					MathHelper.minusDiv(
						e - (double)(result.getForwards().rotateYClockwise().getAxisDirection() == Direction.AxisDirection.NEGATIVE ? 1 : 0), d, d - (double)result.getWidth()
					)
				);
				double f = MathHelper.minusDiv(this.y - 1.0, (double)result.getFrontTopLeft().getY(), (double)(result.getFrontTopLeft().getY() - result.getHeight()));
				this.lastPortalVec3d = new Vec3d(e, f, 0.0);
				this.teleportDirection = result.getForwards();
			}

			this.changingDimension = true;
		}
	}

	public int getDefaultNetherPortalCooldown() {
		return 300;
	}

	public void setVelocityClient(double x, double y, double z) {
		this.velocityX = x;
		this.velocityY = y;
		this.velocityZ = z;
	}

	public void handleStatus(byte status) {
	}

	public void animateDamage() {
	}

	public ItemStack[] getArmorStacks() {
		return null;
	}

	public void setArmorSlot(int armorSlot, ItemStack item) {
	}

	public boolean isOnFire() {
		boolean bl = this.world != null && this.world.isClient;
		return !this.isFireImmune && (this.fireTicks > 0 || bl && this.getFlag(0));
	}

	public boolean hasVehicle() {
		return this.vehicle != null;
	}

	public boolean isSneaking() {
		return this.getFlag(1);
	}

	public void setSneaking(boolean sneaking) {
		this.setFlag(1, sneaking);
	}

	public boolean isSprinting() {
		return this.getFlag(3);
	}

	public void setSprinting(boolean sprinting) {
		this.setFlag(3, sprinting);
	}

	public boolean isInvisible() {
		return this.getFlag(5);
	}

	public boolean isInvisibleTo(PlayerEntity player) {
		return player.isSpectator() ? false : this.isInvisible();
	}

	public void setInvisible(boolean invisible) {
		this.setFlag(5, invisible);
	}

	public boolean isSwimming() {
		return this.getFlag(4);
	}

	public void setSwimming(boolean swimming) {
		this.setFlag(4, swimming);
	}

	protected boolean getFlag(int index) {
		return (this.dataTracker.getByte(0) & 1 << index) != 0;
	}

	protected void setFlag(int index, boolean value) {
		byte b = this.dataTracker.getByte(0);
		if (value) {
			this.dataTracker.setProperty(0, (byte)(b | 1 << index));
		} else {
			this.dataTracker.setProperty(0, (byte)(b & ~(1 << index)));
		}
	}

	public int getAir() {
		return this.dataTracker.getShort(1);
	}

	public void setAir(int air) {
		this.dataTracker.setProperty(1, (short)air);
	}

	public void onLightningStrike(LightningBoltEntity lightning) {
		this.damage(DamageSource.LIGHTNING_BOLT, 5.0F);
		this.fireTicks++;
		if (this.fireTicks == 0) {
			this.setOnFireFor(8);
		}
	}

	public void onKilledOther(LivingEntity other) {
	}

	protected boolean pushOutOfBlocks(double x, double y, double z) {
		BlockPos blockPos = new BlockPos(x, y, z);
		double d = x - (double)blockPos.getX();
		double e = y - (double)blockPos.getY();
		double f = z - (double)blockPos.getZ();
		List<Box> list = this.world.method_3608(this.getBoundingBox());
		if (list.isEmpty() && !this.world.method_8565(blockPos)) {
			return false;
		} else {
			int i = 3;
			double g = 9999.0;
			if (!this.world.method_8565(blockPos.west()) && d < g) {
				g = d;
				i = 0;
			}

			if (!this.world.method_8565(blockPos.east()) && 1.0 - d < g) {
				g = 1.0 - d;
				i = 1;
			}

			if (!this.world.method_8565(blockPos.up()) && 1.0 - e < g) {
				g = 1.0 - e;
				i = 3;
			}

			if (!this.world.method_8565(blockPos.north()) && f < g) {
				g = f;
				i = 4;
			}

			if (!this.world.method_8565(blockPos.south()) && 1.0 - f < g) {
				g = 1.0 - f;
				i = 5;
			}

			float h = this.random.nextFloat() * 0.2F + 0.1F;
			if (i == 0) {
				this.velocityX = (double)(-h);
			}

			if (i == 1) {
				this.velocityX = (double)h;
			}

			if (i == 3) {
				this.velocityY = (double)h;
			}

			if (i == 4) {
				this.velocityZ = (double)(-h);
			}

			if (i == 5) {
				this.velocityZ = (double)h;
			}

			return true;
		}
	}

	public void setInLava() {
		this.inLava = true;
		this.fallDistance = 0.0F;
	}

	@Override
	public String getTranslationKey() {
		if (this.hasCustomName()) {
			return this.getCustomName();
		} else {
			String string = EntityType.getEntityName(this);
			if (string == null) {
				string = "generic";
			}

			return CommonI18n.translate("entity." + string + ".name");
		}
	}

	public Entity[] getParts() {
		return null;
	}

	public boolean isPartOf(Entity entity) {
		return this == entity;
	}

	public float getHeadRotation() {
		return 0.0F;
	}

	public void setHeadYaw(float headYaw) {
	}

	public void setYaw(float yaw) {
	}

	public boolean isAttackable() {
		return true;
	}

	public boolean handleAttack(Entity attacker) {
		return false;
	}

	public String toString() {
		return String.format(
			"%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f]",
			this.getClass().getSimpleName(),
			this.getTranslationKey(),
			this.entityId,
			this.world == null ? "~NULL~" : this.world.getLevelProperties().getLevelName(),
			this.x,
			this.y,
			this.z
		);
	}

	public boolean isInvulnerableTo(DamageSource damageSource) {
		return this.invulnerable && damageSource != DamageSource.OUT_OF_WORLD && !damageSource.isSourceCreativePlayer();
	}

	public void copyPosition(Entity original) {
		this.refreshPositionAndAngles(original.x, original.y, original.z, original.yaw, original.pitch);
	}

	public void copyPortalInfo(Entity original) {
		NbtCompound nbtCompound = new NbtCompound();
		original.writePlayerData(nbtCompound);
		this.fromNbt(nbtCompound);
		this.netherPortalCooldown = original.netherPortalCooldown;
		this.lastPortalBlockPos = original.lastPortalBlockPos;
		this.lastPortalVec3d = original.lastPortalVec3d;
		this.teleportDirection = original.teleportDirection;
	}

	public void teleportToDimension(int dimensionId) {
		if (!this.world.isClient && !this.removed) {
			this.world.profiler.push("changeDimension");
			MinecraftServer minecraftServer = MinecraftServer.getServer();
			int i = this.dimension;
			ServerWorld serverWorld = minecraftServer.getWorld(i);
			ServerWorld serverWorld2 = minecraftServer.getWorld(dimensionId);
			this.dimension = dimensionId;
			if (i == 1 && dimensionId == 1) {
				serverWorld2 = minecraftServer.getWorld(0);
				this.dimension = 0;
			}

			this.world.removeEntity(this);
			this.removed = false;
			this.world.profiler.push("reposition");
			minecraftServer.getPlayerManager().method_4399(this, i, serverWorld, serverWorld2);
			this.world.profiler.swap("reloading");
			Entity entity = EntityType.createInstanceFromName(EntityType.getEntityName(this), serverWorld2);
			if (entity != null) {
				entity.copyPortalInfo(this);
				if (i == 1 && dimensionId == 1) {
					BlockPos blockPos = this.world.getTopPosition(serverWorld2.getSpawnPos());
					entity.refreshPositionAndAngles(blockPos, entity.yaw, entity.pitch);
				}

				serverWorld2.spawnEntity(entity);
			}

			this.removed = true;
			this.world.profiler.pop();
			serverWorld.resetIdleTimeout();
			serverWorld2.resetIdleTimeout();
			this.world.profiler.pop();
		}
	}

	public float getBlastResistance(Explosion explosion, World world, BlockPos pos, BlockState state) {
		return state.getBlock().getBlastResistance(this);
	}

	public boolean canExplosionDestroyBlock(Explosion explosion, World world, BlockPos pos, BlockState state, float explosionPower) {
		return true;
	}

	public int getSafeFallDistance() {
		return 3;
	}

	public Vec3d getLastNetherPortalDirectionVector() {
		return this.lastPortalVec3d;
	}

	public Direction getLastNetherPortalDirection() {
		return this.teleportDirection;
	}

	public boolean canAvoidTraps() {
		return false;
	}

	public void populateCrashReport(CrashReportSection section) {
		section.add("Entity Type", new Callable<String>() {
			public String call() throws Exception {
				return EntityType.getEntityName(Entity.this) + " (" + Entity.this.getClass().getCanonicalName() + ")";
			}
		});
		section.add("Entity ID", this.entityId);
		section.add("Entity Name", new Callable<String>() {
			public String call() throws Exception {
				return Entity.this.getTranslationKey();
			}
		});
		section.add("Entity's Exact location", String.format("%.2f, %.2f, %.2f", this.x, this.y, this.z));
		section.add(
			"Entity's Block location",
			CrashReportSection.createPositionString((double)MathHelper.floor(this.x), (double)MathHelper.floor(this.y), (double)MathHelper.floor(this.z))
		);
		section.add("Entity's Momentum", String.format("%.2f, %.2f, %.2f", this.velocityX, this.velocityY, this.velocityZ));
		section.add("Entity's Rider", new Callable<String>() {
			public String call() throws Exception {
				return Entity.this.rider.toString();
			}
		});
		section.add("Entity's Vehicle", new Callable<String>() {
			public String call() throws Exception {
				return Entity.this.vehicle.toString();
			}
		});
	}

	public boolean doesRenderOnFire() {
		return this.isOnFire();
	}

	public UUID getUuid() {
		return this.playerUuid;
	}

	public boolean canFly() {
		return true;
	}

	@Override
	public Text getName() {
		LiteralText literalText = new LiteralText(this.getTranslationKey());
		literalText.getStyle().setHoverEvent(this.getHoverEvent());
		literalText.getStyle().setInsertion(this.getUuid().toString());
		return literalText;
	}

	public void setCustomName(String name) {
		this.dataTracker.setProperty(2, name);
	}

	public String getCustomName() {
		return this.dataTracker.getString(2);
	}

	public boolean hasCustomName() {
		return this.dataTracker.getString(2).length() > 0;
	}

	public void setCustomNameVisible(boolean visible) {
		this.dataTracker.setProperty(3, Byte.valueOf((byte)(visible ? 1 : 0)));
	}

	public boolean isCustomNameVisible() {
		return this.dataTracker.getByte(3) == 1;
	}

	public void refreshPositionAfterTeleport(double x, double y, double z) {
		this.refreshPositionAndAngles(x, y, z, this.yaw, this.pitch);
	}

	public boolean shouldRenderName() {
		return this.isCustomNameVisible();
	}

	public void method_8364(int i) {
	}

	public Direction getHorizontalDirection() {
		return Direction.fromHorizontal(MathHelper.floor((double)(this.yaw * 4.0F / 360.0F) + 0.5) & 3);
	}

	protected HoverEvent getHoverEvent() {
		NbtCompound nbtCompound = new NbtCompound();
		String string = EntityType.getEntityName(this);
		nbtCompound.putString("id", this.getUuid().toString());
		if (string != null) {
			nbtCompound.putString("type", string);
		}

		nbtCompound.putString("name", this.getTranslationKey());
		return new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new LiteralText(nbtCompound.toString()));
	}

	public boolean isSpectatedBy(ServerPlayerEntity player) {
		return true;
	}

	public Box getBoundingBox() {
		return this.boundingBox;
	}

	public void setBoundingBox(Box boundingBox) {
		this.boundingBox = boundingBox;
	}

	public float getEyeHeight() {
		return this.height * 0.85F;
	}

	public boolean isOutsideWorldBorder() {
		return this.outsideWorldBorder;
	}

	public void setOutsideWorldBorder(boolean value) {
		this.outsideWorldBorder = value;
	}

	public boolean equip(int slot, ItemStack item) {
		return false;
	}

	@Override
	public void sendMessage(Text text) {
	}

	@Override
	public boolean canUseCommand(int permissionLevel, String commandLiteral) {
		return true;
	}

	@Override
	public BlockPos getBlockPos() {
		return new BlockPos(this.x, this.y + 0.5, this.z);
	}

	@Override
	public Vec3d getPos() {
		return new Vec3d(this.x, this.y, this.z);
	}

	@Override
	public World getWorld() {
		return this.world;
	}

	@Override
	public Entity getEntity() {
		return this;
	}

	@Override
	public boolean sendCommandFeedback() {
		return false;
	}

	@Override
	public void setStat(CommandStats.Type statsType, int value) {
		this.commandStats.execute(this, statsType, value);
	}

	public CommandStats getCommandStats() {
		return this.commandStats;
	}

	public void method_10965(Entity entity) {
		this.commandStats.setAllStats(entity.getCommandStats());
	}

	public NbtCompound method_10948() {
		return null;
	}

	public void fromClientNbt(NbtCompound nbt) {
	}

	public boolean interactAt(PlayerEntity player, Vec3d hitPos) {
		return false;
	}

	public boolean isImmuneToExplosion() {
		return false;
	}

	protected void dealDamage(LivingEntity attacker, Entity target) {
		if (target instanceof LivingEntity) {
			EnchantmentHelper.onUserDamaged((LivingEntity)target, attacker);
		}

		EnchantmentHelper.onTargetDamaged(attacker, target);
	}
}
