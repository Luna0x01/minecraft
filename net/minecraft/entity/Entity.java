package net.minecraft.entity;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.class_3804;
import net.minecraft.class_3893;
import net.minecraft.class_3915;
import net.minecraft.class_4048;
import net.minecraft.class_4079;
import net.minecraft.class_4337;
import net.minecraft.class_4342;
import net.minecraft.class_4489;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Itemable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Nameable;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.util.shapes.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.explosion.Explosion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Entity implements Nameable, class_3893 {
	protected static final Logger LOGGER_ = LogManager.getLogger();
	private static final List<ItemStack> EMPTY_STACK_LIST = Collections.emptyList();
	private static final Box HITBOX = new Box(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
	private static double renderDistanceMultiplier = 1.0;
	private static int entityCount;
	private final EntityType<?> field_16697;
	private int entityId;
	public boolean inanimate;
	private final List<Entity> passengerList;
	protected int ridingCooldown;
	private Entity mount;
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
	private float field_16695;
	private float nextSoundDistance;
	public double prevTickX;
	public double prevTickY;
	public double prevTickZ;
	public float stepHeight;
	public boolean noClip;
	public float pushSpeedReduction;
	protected Random random;
	public int ticksAlive;
	private int fireTicks;
	protected boolean touchingWater;
	protected double field_16693;
	protected boolean field_16694;
	public int timeUntilRegen;
	protected boolean firstUpdate;
	protected boolean isFireImmune;
	protected DataTracker dataTracker;
	protected static final TrackedData<Byte> FLAGS = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BYTE);
	private static final TrackedData<Integer> AIR = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Optional<Text>> CUSTOM_NAME = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.field_21650);
	private static final TrackedData<Boolean> NAME_VISIBLE = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> SILENT = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> NO_GRAVITY = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BOOLEAN);
	public boolean updateNeeded;
	public int chunkX;
	public int chunkY;
	public int chunkZ;
	public long tracedX;
	public long tracedY;
	public long tracedZ;
	public boolean ignoreCameraFrustum;
	public boolean velocityDirty;
	public int netherPortalCooldown;
	protected boolean changingDimension;
	protected int netherPortalTime;
	public DimensionType field_16696;
	protected BlockPos lastPortalBlockPos;
	protected Vec3d lastPortalVec3d;
	protected Direction teleportDirection;
	private boolean invulnerable;
	protected UUID playerUuid;
	protected String uuidString;
	protected boolean isGlowing;
	private final Set<String> scoreboardTags;
	private boolean teleportRequested;
	private final double[] pistonMovementDelta;
	private long field_15444;

	public Entity(EntityType<?> entityType, World world) {
		this.entityId = entityCount++;
		this.passengerList = Lists.newArrayList();
		this.boundingBox = HITBOX;
		this.width = 0.6F;
		this.height = 1.8F;
		this.field_16695 = 1.0F;
		this.nextSoundDistance = 1.0F;
		this.random = new Random();
		this.fireTicks = -this.getBurningDuration();
		this.firstUpdate = true;
		this.playerUuid = MathHelper.randomUuid(this.random);
		this.uuidString = this.playerUuid.toString();
		this.scoreboardTags = Sets.newHashSet();
		this.pistonMovementDelta = new double[]{0.0, 0.0, 0.0};
		this.field_16697 = entityType;
		this.world = world;
		this.updatePosition(0.0, 0.0, 0.0);
		if (world != null) {
			this.field_16696 = world.dimension.method_11789();
		}

		this.dataTracker = new DataTracker(this);
		this.dataTracker.startTracking(FLAGS, (byte)0);
		this.dataTracker.startTracking(AIR, this.method_15585());
		this.dataTracker.startTracking(NAME_VISIBLE, false);
		this.dataTracker.startTracking(CUSTOM_NAME, Optional.empty());
		this.dataTracker.startTracking(SILENT, false);
		this.dataTracker.startTracking(NO_GRAVITY, false);
		this.initDataTracker();
	}

	public EntityType<?> method_15557() {
		return this.field_16697;
	}

	public int getEntityId() {
		return this.entityId;
	}

	public void setEntityId(int id) {
		this.entityId = id;
	}

	public Set<String> getScoreboardTags() {
		return this.scoreboardTags;
	}

	public boolean addScoreboardTag(String tag) {
		return this.scoreboardTags.size() >= 1024 ? false : this.scoreboardTags.add(tag);
	}

	public boolean removeScoreboardTag(String tag) {
		return this.scoreboardTags.remove(tag);
	}

	public void kill() {
		this.remove();
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
				if (this.world.method_16387(this, this.getBoundingBox())) {
					break;
				}

				this.y++;
			}

			this.velocityX = 0.0;
			this.velocityY = 0.0;
			this.velocityZ = 0.0;
			this.pitch = 0.0F;
		}
	}

	public void remove() {
		this.removed = true;
	}

	public void method_12991(boolean bl) {
	}

	protected void setBounds(float width, float height) {
		if (width != this.width || height != this.height) {
			float f = this.width;
			this.width = width;
			this.height = height;
			if (this.width < f) {
				double d = (double)width / 2.0;
				this.setBoundingBox(new Box(this.x - d, this.y, this.z - d, this.x + d, this.y + (double)this.height, this.z + d));
				return;
			}

			Box box = this.getBoundingBox();
			this.setBoundingBox(new Box(box.minX, box.minY, box.minZ, box.minX + (double)this.width, box.minY + (double)this.height, box.minZ + (double)this.width));
			if (this.width > f && !this.firstUpdate && !this.world.isClient) {
				this.move(MovementType.SELF, (double)(f - this.width), 0.0, (double)(f - this.width));
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

	public void method_15558(double d, double e) {
		double f = e * 0.15;
		double g = d * 0.15;
		this.pitch = (float)((double)this.pitch + f);
		this.yaw = (float)((double)this.yaw + g);
		this.pitch = MathHelper.clamp(this.pitch, -90.0F, 90.0F);
		this.prevPitch = (float)((double)this.prevPitch + f);
		this.prevYaw = (float)((double)this.prevYaw + g);
		this.prevPitch = MathHelper.clamp(this.prevPitch, -90.0F, 90.0F);
		if (this.mount != null) {
			this.mount.onPassengerLookAround(this);
		}
	}

	public void tick() {
		if (!this.world.isClient) {
			this.setFlag(6, this.isGlowing());
		}

		this.baseTick();
	}

	public void baseTick() {
		this.world.profiler.push("entityBaseTick");
		if (this.hasMount() && this.getVehicle().removed) {
			this.stopRiding();
		}

		if (this.ridingCooldown > 0) {
			this.ridingCooldown--;
		}

		this.prevHorizontalSpeed = this.horizontalSpeed;
		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
		this.prevPitch = this.pitch;
		this.prevYaw = this.yaw;
		if (!this.world.isClient && this.world instanceof ServerWorld) {
			this.world.profiler.push("portal");
			if (this.changingDimension) {
				MinecraftServer minecraftServer = this.world.getServer();
				if (minecraftServer.isNetherAllowed()) {
					if (!this.hasMount()) {
						int i = this.getMaxNetherPortalTime();
						if (this.netherPortalTime++ >= i) {
							this.netherPortalTime = i;
							this.netherPortalCooldown = this.getDefaultNetherPortalCooldown();
							DimensionType dimensionType;
							if (this.world.dimension.method_11789() == DimensionType.THE_NETHER) {
								dimensionType = DimensionType.OVERWORLD;
							} else {
								dimensionType = DimensionType.THE_NETHER;
							}

							this.method_15562(dimensionType);
						}
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

			this.tickNetherPortalCooldown();
			this.world.profiler.pop();
		}

		this.attemptSprintingParticles();
		this.method_15597();
		if (this.world.isClient) {
			this.extinguish();
		} else if (this.fireTicks > 0) {
			if (this.isFireImmune) {
				this.fireTicks -= 4;
				if (this.fireTicks < 0) {
					this.extinguish();
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

	protected void tickNetherPortalCooldown() {
		if (this.netherPortalCooldown > 0) {
			this.netherPortalCooldown--;
		}
	}

	public int getMaxNetherPortalTime() {
		return 1;
	}

	protected void setOnFireFromLava() {
		if (!this.isFireImmune) {
			this.setOnFireFor(15);
			this.damage(DamageSource.LAVA, 4.0F);
		}
	}

	public void setOnFireFor(int seconds) {
		int i = seconds * 20;
		if (this instanceof LivingEntity) {
			i = ProtectionEnchantment.method_11466((LivingEntity)this, i);
		}

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
		return this.doesNotCollide(this.getBoundingBox().offset(offsetX, offsetY, offsetZ));
	}

	private boolean doesNotCollide(Box box) {
		return this.world.method_16387(this, box) && !this.world.method_16388(box);
	}

	public void move(MovementType type, double movementX, double movementY, double movementZ) {
		if (this.noClip) {
			this.setBoundingBox(this.getBoundingBox().offset(movementX, movementY, movementZ));
			this.updateSubmergedInWaterState();
		} else {
			if (type == MovementType.PISTON) {
				long l = this.world.getLastUpdateTime();
				if (l != this.field_15444) {
					Arrays.fill(this.pistonMovementDelta, 0.0);
					this.field_15444 = l;
				}

				if (movementX != 0.0) {
					int i = Direction.Axis.X.ordinal();
					double d = MathHelper.clamp(movementX + this.pistonMovementDelta[i], -0.51, 0.51);
					movementX = d - this.pistonMovementDelta[i];
					this.pistonMovementDelta[i] = d;
					if (Math.abs(movementX) <= 1.0E-5F) {
						return;
					}
				} else if (movementY != 0.0) {
					int j = Direction.Axis.Y.ordinal();
					double e = MathHelper.clamp(movementY + this.pistonMovementDelta[j], -0.51, 0.51);
					movementY = e - this.pistonMovementDelta[j];
					this.pistonMovementDelta[j] = e;
					if (Math.abs(movementY) <= 1.0E-5F) {
						return;
					}
				} else {
					if (movementZ == 0.0) {
						return;
					}

					int k = Direction.Axis.Z.ordinal();
					double f = MathHelper.clamp(movementZ + this.pistonMovementDelta[k], -0.51, 0.51);
					movementZ = f - this.pistonMovementDelta[k];
					this.pistonMovementDelta[k] = f;
					if (Math.abs(movementZ) <= 1.0E-5F) {
						return;
					}
				}
			}

			this.world.profiler.push("move");
			double g = this.x;
			double h = this.y;
			double m = this.z;
			if (this.inLava) {
				this.inLava = false;
				movementX *= 0.25;
				movementY *= 0.05F;
				movementZ *= 0.25;
				this.velocityX = 0.0;
				this.velocityY = 0.0;
				this.velocityZ = 0.0;
			}

			double n = movementX;
			double o = movementY;
			double p = movementZ;
			if ((type == MovementType.SELF || type == MovementType.PLAYER) && this.onGround && this.isSneaking() && this instanceof PlayerEntity) {
				for (double q = 0.05;
					movementX != 0.0 && this.world.method_16387(this, this.getBoundingBox().offset(movementX, (double)(-this.stepHeight), 0.0));
					n = movementX
				) {
					if (movementX < 0.05 && movementX >= -0.05) {
						movementX = 0.0;
					} else if (movementX > 0.0) {
						movementX -= 0.05;
					} else {
						movementX += 0.05;
					}
				}

				for (; movementZ != 0.0 && this.world.method_16387(this, this.getBoundingBox().offset(0.0, (double)(-this.stepHeight), movementZ)); p = movementZ) {
					if (movementZ < 0.05 && movementZ >= -0.05) {
						movementZ = 0.0;
					} else if (movementZ > 0.0) {
						movementZ -= 0.05;
					} else {
						movementZ += 0.05;
					}
				}

				for (;
					movementX != 0.0 && movementZ != 0.0 && this.world.method_16387(this, this.getBoundingBox().offset(movementX, (double)(-this.stepHeight), movementZ));
					p = movementZ
				) {
					if (movementX < 0.05 && movementX >= -0.05) {
						movementX = 0.0;
					} else if (movementX > 0.0) {
						movementX -= 0.05;
					} else {
						movementX += 0.05;
					}

					n = movementX;
					if (movementZ < 0.05 && movementZ >= -0.05) {
						movementZ = 0.0;
					} else if (movementZ > 0.0) {
						movementZ -= 0.05;
					} else {
						movementZ += 0.05;
					}
				}
			}

			Box box = this.getBoundingBox();
			if (movementX != 0.0 || movementY != 0.0 || movementZ != 0.0) {
				class_4489<VoxelShape> lv = new class_4489<>(this.world.method_16365(this, this.getBoundingBox(), movementX, movementY, movementZ));
				if (movementY != 0.0) {
					movementY = VoxelShapes.calculateMaxOffset(Direction.Axis.Y, this.getBoundingBox(), lv.method_21527(), movementY);
					this.setBoundingBox(this.getBoundingBox().offset(0.0, movementY, 0.0));
				}

				if (movementX != 0.0) {
					movementX = VoxelShapes.calculateMaxOffset(Direction.Axis.X, this.getBoundingBox(), lv.method_21527(), movementX);
					if (movementX != 0.0) {
						this.setBoundingBox(this.getBoundingBox().offset(movementX, 0.0, 0.0));
					}
				}

				if (movementZ != 0.0) {
					movementZ = VoxelShapes.calculateMaxOffset(Direction.Axis.Z, this.getBoundingBox(), lv.method_21527(), movementZ);
					if (movementZ != 0.0) {
						this.setBoundingBox(this.getBoundingBox().offset(0.0, 0.0, movementZ));
					}
				}
			}

			boolean bl = this.onGround || o != movementY && o < 0.0;
			if (this.stepHeight > 0.0F && bl && (n != movementX || p != movementZ)) {
				double r = movementX;
				double s = movementY;
				double t = movementZ;
				Box box2 = this.getBoundingBox();
				this.setBoundingBox(box);
				movementX = n;
				movementY = (double)this.stepHeight;
				movementZ = p;
				if (n != 0.0 || movementY != 0.0 || p != 0.0) {
					class_4489<VoxelShape> lv2 = new class_4489<>(this.world.method_16365(this, this.getBoundingBox(), n, movementY, p));
					Box box3 = this.getBoundingBox();
					Box box4 = box3.stretch(n, 0.0, p);
					double u = VoxelShapes.calculateMaxOffset(Direction.Axis.Y, box4, lv2.method_21527(), movementY);
					if (u != 0.0) {
						box3 = box3.offset(0.0, u, 0.0);
					}

					double v = VoxelShapes.calculateMaxOffset(Direction.Axis.X, box3, lv2.method_21527(), n);
					if (v != 0.0) {
						box3 = box3.offset(v, 0.0, 0.0);
					}

					double w = VoxelShapes.calculateMaxOffset(Direction.Axis.Z, box3, lv2.method_21527(), p);
					if (w != 0.0) {
						box3 = box3.offset(0.0, 0.0, w);
					}

					Box box5 = this.getBoundingBox();
					double x = VoxelShapes.calculateMaxOffset(Direction.Axis.Y, box5, lv2.method_21527(), movementY);
					if (x != 0.0) {
						box5 = box5.offset(0.0, x, 0.0);
					}

					double y = VoxelShapes.calculateMaxOffset(Direction.Axis.X, box5, lv2.method_21527(), n);
					if (y != 0.0) {
						box5 = box5.offset(y, 0.0, 0.0);
					}

					double z = VoxelShapes.calculateMaxOffset(Direction.Axis.Z, box5, lv2.method_21527(), p);
					if (z != 0.0) {
						box5 = box5.offset(0.0, 0.0, z);
					}

					double aa = v * v + w * w;
					double ab = y * y + z * z;
					if (aa > ab) {
						movementX = v;
						movementZ = w;
						movementY = -u;
						this.setBoundingBox(box3);
					} else {
						movementX = y;
						movementZ = z;
						movementY = -x;
						this.setBoundingBox(box5);
					}

					movementY = VoxelShapes.calculateMaxOffset(Direction.Axis.Y, this.getBoundingBox(), lv2.method_21527(), movementY);
					if (movementY != 0.0) {
						this.setBoundingBox(this.getBoundingBox().offset(0.0, movementY, 0.0));
					}
				}

				if (r * r + t * t >= movementX * movementX + movementZ * movementZ) {
					movementX = r;
					movementY = s;
					movementZ = t;
					this.setBoundingBox(box2);
				}
			}

			this.world.profiler.pop();
			this.world.profiler.push("rest");
			this.updateSubmergedInWaterState();
			this.horizontalCollision = n != movementX || p != movementZ;
			this.verticalCollision = o != movementY;
			this.onGround = this.verticalCollision && o < 0.0;
			this.colliding = this.horizontalCollision || this.verticalCollision;
			int ac = MathHelper.floor(this.x);
			int ad = MathHelper.floor(this.y - 0.2F);
			int ae = MathHelper.floor(this.z);
			BlockPos blockPos = new BlockPos(ac, ad, ae);
			BlockState blockState = this.world.getBlockState(blockPos);
			if (blockState.isAir()) {
				BlockPos blockPos2 = blockPos.down();
				BlockState blockState2 = this.world.getBlockState(blockPos2);
				Block block = blockState2.getBlock();
				if (block instanceof FenceBlock || block instanceof WallBlock || block instanceof FenceGateBlock) {
					blockState = blockState2;
					blockPos = blockPos2;
				}
			}

			this.fall(movementY, this.onGround, blockState, blockPos);
			if (n != movementX) {
				this.velocityX = 0.0;
			}

			if (p != movementZ) {
				this.velocityZ = 0.0;
			}

			Block block2 = blockState.getBlock();
			if (o != movementY) {
				block2.onEntityLand(this.world, this);
			}

			if (this.canClimb() && (!this.onGround || !this.isSneaking() || !(this instanceof PlayerEntity)) && !this.hasMount()) {
				double af = this.x - g;
				double ag = this.y - h;
				double ah = this.z - m;
				if (block2 != Blocks.LADDER) {
					ag = 0.0;
				}

				if (block2 != null && this.onGround) {
					block2.onSteppedOn(this.world, blockPos, this);
				}

				this.horizontalSpeed = (float)((double)this.horizontalSpeed + (double)MathHelper.sqrt(af * af + ah * ah) * 0.6);
				this.distanceTraveled = (float)((double)this.distanceTraveled + (double)MathHelper.sqrt(af * af + ag * ag + ah * ah) * 0.6);
				if (this.distanceTraveled > this.field_16695 && !blockState.isAir()) {
					this.field_16695 = this.method_15572();
					if (this.isTouchingWater()) {
						Entity entity = this.hasPassengers() && this.getPrimaryPassenger() != null ? this.getPrimaryPassenger() : this;
						float ai = entity == this ? 0.35F : 0.4F;
						float aj = MathHelper.sqrt(entity.velocityX * entity.velocityX * 0.2F + entity.velocityY * entity.velocityY + entity.velocityZ * entity.velocityZ * 0.2F)
							* ai;
						if (aj > 1.0F) {
							aj = 1.0F;
						}

						this.method_15588(aj);
					} else {
						this.method_10936(blockPos, blockState);
					}
				} else if (this.distanceTraveled > this.nextSoundDistance && this.method_15051() && blockState.isAir()) {
					this.nextSoundDistance = this.method_15055(this.distanceTraveled);
				}
			}

			try {
				this.checkBlockCollision();
			} catch (Throwable var49) {
				CrashReport crashReport = CrashReport.create(var49, "Checking entity block collision");
				CrashReportSection crashReportSection = crashReport.addElement("Entity being checked for collision");
				this.populateCrashReport(crashReportSection);
				throw new CrashException(crashReport);
			}

			boolean bl2 = this.method_15574();
			if (this.world.containsFireSource(this.getBoundingBox().contract(0.001))) {
				if (!bl2) {
					this.fireTicks++;
					if (this.fireTicks == 0) {
						this.setOnFireFor(8);
					}
				}

				this.burn(1);
			} else if (this.fireTicks <= 0) {
				this.fireTicks = -this.getBurningDuration();
			}

			if (bl2 && this.isOnFire()) {
				this.playSound(Sounds.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.7F, 1.6F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
				this.fireTicks = -this.getBurningDuration();
			}

			this.world.profiler.pop();
		}
	}

	protected float method_15572() {
		return (float)((int)this.distanceTraveled + 1);
	}

	public void updateSubmergedInWaterState() {
		Box box = this.getBoundingBox();
		this.x = (box.minX + box.maxX) / 2.0;
		this.y = box.minY;
		this.z = (box.minZ + box.maxZ) / 2.0;
	}

	protected Sound method_12984() {
		return Sounds.ENTITY_GENERIC_SWIM;
	}

	protected Sound method_12985() {
		return Sounds.ENTITY_GENERIC_SPLASH;
	}

	protected Sound method_15573() {
		return Sounds.ENTITY_GENERIC_SPLASH;
	}

	protected void checkBlockCollision() {
		Box box = this.getBoundingBox();

		try (
			BlockPos.Pooled pooled = BlockPos.Pooled.method_12567(box.minX + 0.001, box.minY + 0.001, box.minZ + 0.001);
			BlockPos.Pooled pooled2 = BlockPos.Pooled.method_12567(box.maxX - 0.001, box.maxY - 0.001, box.maxZ - 0.001);
			BlockPos.Pooled pooled3 = BlockPos.Pooled.get();
		) {
			if (this.world.method_16385(pooled, pooled2)) {
				for (int i = pooled.getX(); i <= pooled2.getX(); i++) {
					for (int j = pooled.getY(); j <= pooled2.getY(); j++) {
						for (int k = pooled.getZ(); k <= pooled2.getZ(); k++) {
							pooled3.setPosition(i, j, k);
							BlockState blockState = this.world.getBlockState(pooled3);

							try {
								blockState.onEntityCollision(this.world, pooled3, this);
								this.onBlockCollision(blockState);
							} catch (Throwable var60) {
								CrashReport crashReport = CrashReport.create(var60, "Colliding entity with block");
								CrashReportSection crashReportSection = crashReport.addElement("Block being collided with");
								CrashReportSection.addBlockInfo(crashReportSection, pooled3, blockState);
								throw new CrashException(crashReport);
							}
						}
					}
				}
			}
		}
	}

	protected void onBlockCollision(BlockState state) {
	}

	protected void method_10936(BlockPos blockPos, BlockState blockState) {
		if (!blockState.getMaterial().isFluid()) {
			BlockSoundGroup blockSoundGroup = this.world.getBlockState(blockPos.up()).getBlock() == Blocks.SNOW
				? Blocks.SNOW.getSoundGroup()
				: blockState.getBlock().getSoundGroup();
			this.playSound(blockSoundGroup.getStepSound(), blockSoundGroup.getVolume() * 0.15F, blockSoundGroup.getPitch());
		}
	}

	protected void method_15588(float f) {
		this.playSound(this.method_12984(), f, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
	}

	protected float method_15055(float f) {
		return 0.0F;
	}

	protected boolean method_15051() {
		return false;
	}

	public void playSound(Sound event, float volume, float pitch) {
		if (!this.isSilent()) {
			this.world.playSound(null, this.x, this.y, this.z, event, this.getSoundCategory(), volume, pitch);
		}
	}

	public boolean isSilent() {
		return this.dataTracker.get(SILENT);
	}

	public void setSilent(boolean silent) {
		this.dataTracker.set(SILENT, silent);
	}

	public boolean hasNoGravity() {
		return this.dataTracker.get(NO_GRAVITY);
	}

	public void setNoGravity(boolean noGravity) {
		this.dataTracker.set(NO_GRAVITY, noGravity);
	}

	protected boolean canClimb() {
		return true;
	}

	protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPos) {
		if (onGround) {
			if (this.fallDistance > 0.0F) {
				landedState.getBlock().onLandedUpon(this.world, landedPos, this, this.fallDistance);
			}

			this.fallDistance = 0.0F;
		} else if (heightDifference < 0.0) {
			this.fallDistance = (float)((double)this.fallDistance - heightDifference);
		}
	}

	@Nullable
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
		if (this.hasPassengers()) {
			for (Entity entity : this.getPassengerList()) {
				entity.handleFallDamage(fallDistance, damageMultiplier);
			}
		}
	}

	public boolean isTouchingWater() {
		return this.touchingWater;
	}

	private boolean method_15595() {
		boolean var3;
		try (BlockPos.Pooled pooled = BlockPos.Pooled.method_19935(this)) {
			var3 = this.world.hasRain(pooled) || this.world.hasRain(pooled.set(this.x, this.y + (double)this.height, this.z));
		}

		return var3;
	}

	private boolean method_15596() {
		return this.world.getBlockState(new BlockPos(this)).getBlock() == Blocks.BUBBLE_COLUMN;
	}

	public boolean tickFire() {
		return this.isTouchingWater() || this.method_15595();
	}

	public boolean method_15574() {
		return this.isTouchingWater() || this.method_15595() || this.method_15596();
	}

	public boolean method_15575() {
		return this.isTouchingWater() || this.method_15596();
	}

	public boolean method_15576() {
		return this.field_16694 && this.isTouchingWater();
	}

	private void method_15597() {
		this.updateWaterState();
		this.method_15598();
		this.method_15577();
	}

	public void method_15577() {
		if (this.method_15584()) {
			this.method_15590(this.isSprinting() && this.isTouchingWater() && !this.hasMount());
		} else {
			this.method_15590(this.isSprinting() && this.method_15576() && !this.hasMount());
		}
	}

	public boolean updateWaterState() {
		if (this.getVehicle() instanceof BoatEntity) {
			this.touchingWater = false;
		} else if (this.method_15580(FluidTags.WATER)) {
			if (!this.touchingWater && !this.firstUpdate) {
				this.onSwimmingStart();
			}

			this.fallDistance = 0.0F;
			this.touchingWater = true;
			this.extinguish();
		} else {
			this.touchingWater = false;
		}

		return this.touchingWater;
	}

	private void method_15598() {
		this.field_16694 = this.method_15567(FluidTags.WATER);
	}

	protected void onSwimmingStart() {
		Entity entity = this.hasPassengers() && this.getPrimaryPassenger() != null ? this.getPrimaryPassenger() : this;
		float f = entity == this ? 0.2F : 0.9F;
		float g = MathHelper.sqrt(entity.velocityX * entity.velocityX * 0.2F + entity.velocityY * entity.velocityY + entity.velocityZ * entity.velocityZ * 0.2F) * f;
		if (g > 1.0F) {
			g = 1.0F;
		}

		if ((double)g < 0.25) {
			this.playSound(this.method_12985(), g, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
		} else {
			this.playSound(this.method_15573(), g, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
		}

		float h = (float)MathHelper.floor(this.getBoundingBox().minY);

		for (int i = 0; (float)i < 1.0F + this.width * 20.0F; i++) {
			float j = (this.random.nextFloat() * 2.0F - 1.0F) * this.width;
			float k = (this.random.nextFloat() * 2.0F - 1.0F) * this.width;
			this.world
				.method_16343(
					class_4342.field_21379,
					this.x + (double)j,
					(double)(h + 1.0F),
					this.z + (double)k,
					this.velocityX,
					this.velocityY - (double)(this.random.nextFloat() * 0.2F),
					this.velocityZ
				);
		}

		for (int l = 0; (float)l < 1.0F + this.width * 20.0F; l++) {
			float m = (this.random.nextFloat() * 2.0F - 1.0F) * this.width;
			float n = (this.random.nextFloat() * 2.0F - 1.0F) * this.width;
			this.world.method_16343(class_4342.field_21368, this.x + (double)m, (double)(h + 1.0F), this.z + (double)n, this.velocityX, this.velocityY, this.velocityZ);
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
		if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
			this.world
				.method_16343(
					new class_4337(class_4342.BLOCK, blockState),
					this.x + ((double)this.random.nextFloat() - 0.5) * (double)this.width,
					this.getBoundingBox().minY + 0.1,
					this.z + ((double)this.random.nextFloat() - 0.5) * (double)this.width,
					-this.velocityX * 4.0,
					1.5,
					-this.velocityZ * 4.0
				);
		}
	}

	public boolean method_15567(Tag<Fluid> tag) {
		if (this.getVehicle() instanceof BoatEntity) {
			return false;
		} else {
			double d = this.y + (double)this.getEyeHeight();
			BlockPos blockPos = new BlockPos(this.x, d, this.z);
			FluidState fluidState = this.world.getFluidState(blockPos);
			return fluidState.matches(tag) && d < (double)((float)blockPos.getY() + fluidState.method_17810() + 0.11111111F);
		}
	}

	public boolean isTouchingLava() {
		return this.world.containsMaterial(this.getBoundingBox().method_18008(0.1F, 0.4F, 0.1F), Material.LAVA);
	}

	public void method_2492(float f, float g, float h, float i) {
		float j = f * f + g * g + h * h;
		if (!(j < 1.0E-4F)) {
			j = MathHelper.sqrt(j);
			if (j < 1.0F) {
				j = 1.0F;
			}

			j = i / j;
			f *= j;
			g *= j;
			h *= j;
			float k = MathHelper.sin(this.yaw * (float) (Math.PI / 180.0));
			float l = MathHelper.cos(this.yaw * (float) (Math.PI / 180.0));
			this.velocityX += (double)(f * l - h * k);
			this.velocityY += (double)g;
			this.velocityZ += (double)(h * l + f * k);
		}
	}

	public int getLightmapCoordinates() {
		BlockPos blockPos = new BlockPos(this.x, this.y + (double)this.getEyeHeight(), this.z);
		return this.world.method_16359(blockPos) ? this.world.method_8578(blockPos, 0) : 0;
	}

	public float getBrightnessAtEyes() {
		BlockPos.Mutable mutable = new BlockPos.Mutable(MathHelper.floor(this.x), 0, MathHelper.floor(this.z));
		if (this.world.method_16359(mutable)) {
			mutable.setY(MathHelper.floor(this.y + (double)this.getEyeHeight()));
			return this.world.method_16356(mutable);
		} else {
			return 0.0F;
		}
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public void updatePositionAndAngles(double x, double y, double z, float yaw, float pitch) {
		this.x = MathHelper.clamp(x, -3.0E7, 3.0E7);
		this.y = y;
		this.z = MathHelper.clamp(z, -3.0E7, 3.0E7);
		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
		pitch = MathHelper.clamp(pitch, -90.0F, 90.0F);
		this.yaw = yaw;
		this.pitch = pitch;
		this.prevYaw = this.yaw;
		this.prevPitch = this.pitch;
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
		this.x = x;
		this.y = y;
		this.z = z;
		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
		this.prevTickX = this.x;
		this.prevTickY = this.y;
		this.prevTickZ = this.z;
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

	public double method_15564(Vec3d vec3d) {
		double d = this.x - vec3d.x;
		double e = this.y - vec3d.y;
		double f = this.z - vec3d.z;
		return d * d + e * e + f * f;
	}

	public void onPlayerCollision(PlayerEntity player) {
	}

	public void pushAwayFrom(Entity entity) {
		if (!this.isConnectedThroughVehicle(entity)) {
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
					if (!this.hasPassengers()) {
						this.addVelocity(-d, 0.0, -e);
					}

					if (!entity.hasPassengers()) {
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

	public final Vec3d getRotationVector(float vector) {
		return this.getRotationVector(this.method_15589(vector), this.method_15591(vector));
	}

	public float method_15589(float f) {
		return f == 1.0F ? this.pitch : this.prevPitch + (this.pitch - this.prevPitch) * f;
	}

	public float method_15591(float f) {
		return f == 1.0F ? this.yaw : this.prevYaw + (this.yaw - this.prevYaw) * f;
	}

	protected final Vec3d getRotationVector(float pitch, float yaw) {
		float f = pitch * (float) (Math.PI / 180.0);
		float g = -yaw * (float) (Math.PI / 180.0);
		float h = MathHelper.cos(g);
		float i = MathHelper.sin(g);
		float j = MathHelper.cos(f);
		float k = MathHelper.sin(f);
		return new Vec3d((double)(i * j), (double)(-k), (double)(h * j));
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

	@Nullable
	public BlockHitResult method_10931(double d, float f, class_4079 arg) {
		Vec3d vec3d = this.getCameraPosVec(f);
		Vec3d vec3d2 = this.getRotationVector(f);
		Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
		return this.world.method_3615(vec3d, vec3d3, arg, false, true);
	}

	public boolean collides() {
		return false;
	}

	public boolean isPushable() {
		return false;
	}

	public void updateKilledAchievement(Entity killer, int score, DamageSource source) {
		if (killer instanceof ServerPlayerEntity) {
			AchievementsAndCriterions.ENTITY_KILLED_PLAYER.trigger((ServerPlayerEntity)killer, this, source);
		}
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

		d *= 64.0 * renderDistanceMultiplier;
		return distance < d * d;
	}

	public boolean saveSelfToNbt(NbtCompound nbt) {
		String string = this.getSavedEntityId();
		if (!this.removed && string != null) {
			nbt.putString("id", string);
			this.toNbt(nbt);
			return true;
		} else {
			return false;
		}
	}

	public boolean saveToNbt(NbtCompound nbt) {
		return this.hasMount() ? false : this.saveSelfToNbt(nbt);
	}

	public NbtCompound toNbt(NbtCompound nbt) {
		try {
			nbt.put("Pos", this.toListNbt(this.x, this.y, this.z));
			nbt.put("Motion", this.toListNbt(this.velocityX, this.velocityY, this.velocityZ));
			nbt.put("Rotation", this.toListNbt(this.yaw, this.pitch));
			nbt.putFloat("FallDistance", this.fallDistance);
			nbt.putShort("Fire", (short)this.fireTicks);
			nbt.putShort("Air", (short)this.getAir());
			nbt.putBoolean("OnGround", this.onGround);
			nbt.putInt("Dimension", this.field_16696.method_17201());
			nbt.putBoolean("Invulnerable", this.invulnerable);
			nbt.putInt("PortalCooldown", this.netherPortalCooldown);
			nbt.putUuid("UUID", this.getUuid());
			Text text = this.method_15541();
			if (text != null) {
				nbt.putString("CustomName", Text.Serializer.serialize(text));
			}

			if (this.isCustomNameVisible()) {
				nbt.putBoolean("CustomNameVisible", this.isCustomNameVisible());
			}

			if (this.isSilent()) {
				nbt.putBoolean("Silent", this.isSilent());
			}

			if (this.hasNoGravity()) {
				nbt.putBoolean("NoGravity", this.hasNoGravity());
			}

			if (this.isGlowing) {
				nbt.putBoolean("Glowing", this.isGlowing);
			}

			if (!this.scoreboardTags.isEmpty()) {
				NbtList nbtList = new NbtList();

				for (String string : this.scoreboardTags) {
					nbtList.add((NbtElement)(new NbtString(string)));
				}

				nbt.put("Tags", nbtList);
			}

			this.writeCustomDataToNbt(nbt);
			if (this.hasPassengers()) {
				NbtList nbtList2 = new NbtList();

				for (Entity entity : this.getPassengerList()) {
					NbtCompound nbtCompound = new NbtCompound();
					if (entity.saveSelfToNbt(nbtCompound)) {
						nbtList2.add((NbtElement)nbtCompound);
					}
				}

				if (!nbtList2.isEmpty()) {
					nbt.put("Passengers", nbtList2);
				}
			}

			return nbt;
		} catch (Throwable var7) {
			CrashReport crashReport = CrashReport.create(var7, "Saving entity NBT");
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

			this.x = nbtList.getDouble(0);
			this.y = nbtList.getDouble(1);
			this.z = nbtList.getDouble(2);
			this.prevTickX = this.x;
			this.prevTickY = this.y;
			this.prevTickZ = this.z;
			this.prevX = this.x;
			this.prevY = this.y;
			this.prevZ = this.z;
			this.yaw = nbtList3.getFloat(0);
			this.pitch = nbtList3.getFloat(1);
			this.prevYaw = this.yaw;
			this.prevPitch = this.pitch;
			this.setHeadYaw(this.yaw);
			this.setYaw(this.yaw);
			this.fallDistance = nbt.getFloat("FallDistance");
			this.fireTicks = nbt.getShort("Fire");
			this.setAir(nbt.getShort("Air"));
			this.onGround = nbt.getBoolean("OnGround");
			if (nbt.contains("Dimension")) {
				this.field_16696 = DimensionType.method_17195(nbt.getInt("Dimension"));
			}

			this.invulnerable = nbt.getBoolean("Invulnerable");
			this.netherPortalCooldown = nbt.getInt("PortalCooldown");
			if (nbt.containsUuid("UUID")) {
				this.playerUuid = nbt.getUuid("UUID");
				this.uuidString = this.playerUuid.toString();
			}

			this.updatePosition(this.x, this.y, this.z);
			this.setRotation(this.yaw, this.pitch);
			if (nbt.contains("CustomName", 8)) {
				this.method_15578(Text.Serializer.deserializeText(nbt.getString("CustomName")));
			}

			this.setCustomNameVisible(nbt.getBoolean("CustomNameVisible"));
			this.setSilent(nbt.getBoolean("Silent"));
			this.setNoGravity(nbt.getBoolean("NoGravity"));
			this.setGlowing(nbt.getBoolean("Glowing"));
			if (nbt.contains("Tags", 9)) {
				this.scoreboardTags.clear();
				NbtList nbtList4 = nbt.getList("Tags", 8);
				int i = Math.min(nbtList4.size(), 1024);

				for (int j = 0; j < i; j++) {
					this.scoreboardTags.add(nbtList4.getString(j));
				}
			}

			this.readCustomDataFromNbt(nbt);
			if (this.shouldSetPositionOnLoad()) {
				this.updatePosition(this.x, this.y, this.z);
			}
		} catch (Throwable var8) {
			CrashReport crashReport = CrashReport.create(var8, "Loading entity NBT");
			CrashReportSection crashReportSection = crashReport.addElement("Entity being loaded");
			this.populateCrashReport(crashReportSection);
			throw new CrashException(crashReport);
		}
	}

	protected boolean shouldSetPositionOnLoad() {
		return true;
	}

	@Nullable
	protected final String getSavedEntityId() {
		EntityType<?> entityType = this.method_15557();
		Identifier identifier = EntityType.getId(entityType);
		return entityType.method_15613() && identifier != null ? identifier.toString() : null;
	}

	protected abstract void readCustomDataFromNbt(NbtCompound nbt);

	protected abstract void writeCustomDataToNbt(NbtCompound nbt);

	protected NbtList toListNbt(double... values) {
		NbtList nbtList = new NbtList();

		for (double d : values) {
			nbtList.add((NbtElement)(new NbtDouble(d)));
		}

		return nbtList;
	}

	protected NbtList toListNbt(float... values) {
		NbtList nbtList = new NbtList();

		for (float f : values) {
			nbtList.add((NbtElement)(new NbtFloat(f)));
		}

		return nbtList;
	}

	@Nullable
	public ItemEntity method_15560(Itemable itemable) {
		return this.method_15561(itemable, 0);
	}

	@Nullable
	public ItemEntity method_15561(Itemable itemable, int i) {
		return this.dropItem(new ItemStack(itemable), (float)i);
	}

	@Nullable
	public ItemEntity method_15571(ItemStack itemStack) {
		return this.dropItem(itemStack, 0.0F);
	}

	@Nullable
	public ItemEntity dropItem(ItemStack stack, float yOffset) {
		if (stack.isEmpty()) {
			return null;
		} else {
			ItemEntity itemEntity = new ItemEntity(this.world, this.x, this.y + (double)yOffset, this.z, stack);
			itemEntity.setToDefaultPickupDelay();
			this.world.method_3686(itemEntity);
			return itemEntity;
		}
	}

	public boolean isAlive() {
		return !this.removed;
	}

	public boolean isInsideWall() {
		if (this.noClip) {
			return false;
		} else {
			try (BlockPos.Pooled pooled = BlockPos.Pooled.get()) {
				for (int i = 0; i < 8; i++) {
					int j = MathHelper.floor(this.y + (double)(((float)((i >> 0) % 2) - 0.5F) * 0.1F) + (double)this.getEyeHeight());
					int k = MathHelper.floor(this.x + (double)(((float)((i >> 1) % 2) - 0.5F) * this.width * 0.8F));
					int l = MathHelper.floor(this.z + (double)(((float)((i >> 2) % 2) - 0.5F) * this.width * 0.8F));
					if (pooled.getX() != k || pooled.getY() != j || pooled.getZ() != l) {
						pooled.setPosition(k, j, l);
						if (this.world.getBlockState(pooled).method_16914()) {
							return true;
						}
					}
				}

				return false;
			}
		}
	}

	public boolean interact(PlayerEntity player, Hand hand) {
		return false;
	}

	@Nullable
	public Box getHardCollisionBox(Entity collidingEntity) {
		return null;
	}

	public void tickRiding() {
		Entity entity = this.getVehicle();
		if (this.hasMount() && entity.removed) {
			this.stopRiding();
		} else {
			this.velocityX = 0.0;
			this.velocityY = 0.0;
			this.velocityZ = 0.0;
			this.tick();
			if (this.hasMount()) {
				entity.updatePassengerPosition(this);
			}
		}
	}

	public void updatePassengerPosition(Entity passenger) {
		if (this.hasPassenger(passenger)) {
			passenger.updatePosition(this.x, this.y + this.getMountedHeightOffset() + passenger.getHeightOffset(), this.z);
		}
	}

	public void onPassengerLookAround(Entity passenger) {
	}

	public double getHeightOffset() {
		return 0.0;
	}

	public double getMountedHeightOffset() {
		return (double)this.height * 0.75;
	}

	public boolean ride(Entity entity) {
		return this.startRiding(entity, false);
	}

	public boolean method_15569() {
		return this instanceof LivingEntity;
	}

	public boolean startRiding(Entity entity, boolean force) {
		for (Entity entity2 = entity; entity2.mount != null; entity2 = entity2.mount) {
			if (entity2.mount == this) {
				return false;
			}
		}

		if (force || this.canStartRiding(entity) && entity.canAddPassenger(this)) {
			if (this.hasMount()) {
				this.stopRiding();
			}

			this.mount = entity;
			this.mount.addPassenger(this);
			return true;
		} else {
			return false;
		}
	}

	protected boolean canStartRiding(Entity entity) {
		return this.ridingCooldown <= 0;
	}

	public void removeAllPassengers() {
		for (int i = this.passengerList.size() - 1; i >= 0; i--) {
			((Entity)this.passengerList.get(i)).stopRiding();
		}
	}

	public void stopRiding() {
		if (this.mount != null) {
			Entity entity = this.mount;
			this.mount = null;
			entity.removePassenger(this);
		}
	}

	protected void addPassenger(Entity passenger) {
		if (passenger.getVehicle() != this) {
			throw new IllegalStateException("Use x.startRiding(y), not y.addPassenger(x)");
		} else {
			if (!this.world.isClient && passenger instanceof PlayerEntity && !(this.getPrimaryPassenger() instanceof PlayerEntity)) {
				this.passengerList.add(0, passenger);
			} else {
				this.passengerList.add(passenger);
			}
		}
	}

	protected void removePassenger(Entity passenger) {
		if (passenger.getVehicle() == this) {
			throw new IllegalStateException("Use x.stopRiding(y), not y.removePassenger(x)");
		} else {
			this.passengerList.remove(passenger);
			passenger.ridingCooldown = 60;
		}
	}

	protected boolean canAddPassenger(Entity passenger) {
		return this.getPassengerList().size() < 1;
	}

	public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
		this.updatePosition(x, y, z);
		this.setRotation(yaw, pitch);
	}

	public void method_15559(float f, int i) {
		this.setHeadYaw(f);
	}

	public float getTargetingMargin() {
		return 0.0F;
	}

	public Vec3d getRotation() {
		return this.getRotationVector(this.pitch, this.yaw);
	}

	public Vec2f getRotationClient() {
		return new Vec2f(this.pitch, this.yaw);
	}

	public Vec3d getRotationVecClient() {
		return Vec3d.fromPolar(this.getRotationClient());
	}

	public void setInNetherPortal(BlockPos pos) {
		if (this.netherPortalCooldown > 0) {
			this.netherPortalCooldown = this.getDefaultNetherPortalCooldown();
		} else {
			if (!this.world.isClient && !pos.equals(this.lastPortalBlockPos)) {
				this.lastPortalBlockPos = new BlockPos(pos);
				BlockPattern.Result result = ((NetherPortalBlock)Blocks.NETHER_PORTAL).method_8848(this.world, this.lastPortalBlockPos);
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

	public Iterable<ItemStack> getItemsHand() {
		return EMPTY_STACK_LIST;
	}

	public Iterable<ItemStack> getArmorItems() {
		return EMPTY_STACK_LIST;
	}

	public Iterable<ItemStack> getItemsEquipped() {
		return Iterables.concat(this.getItemsHand(), this.getArmorItems());
	}

	public void equipStack(EquipmentSlot slot, ItemStack stack) {
	}

	public boolean isOnFire() {
		boolean bl = this.world != null && this.world.isClient;
		return !this.isFireImmune && (this.fireTicks > 0 || bl && this.getFlag(0));
	}

	public boolean hasMount() {
		return this.getVehicle() != null;
	}

	public boolean hasPassengers() {
		return !this.getPassengerList().isEmpty();
	}

	public boolean method_15570() {
		return true;
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

	public boolean method_15584() {
		return this.getFlag(4);
	}

	public void method_15590(boolean bl) {
		this.setFlag(4, bl);
	}

	public boolean isGlowing() {
		return this.isGlowing || this.world.isClient && this.getFlag(6);
	}

	public void setGlowing(boolean glowing) {
		this.isGlowing = glowing;
		if (!this.world.isClient) {
			this.setFlag(6, this.isGlowing);
		}
	}

	public boolean isInvisible() {
		return this.getFlag(5);
	}

	public boolean isInvisibleTo(PlayerEntity player) {
		if (player.isSpectator()) {
			return false;
		} else {
			AbstractTeam abstractTeam = this.getScoreboardTeam();
			return abstractTeam != null && player != null && player.getScoreboardTeam() == abstractTeam && abstractTeam.shouldShowFriendlyInvisibles()
				? false
				: this.isInvisible();
		}
	}

	@Nullable
	public AbstractTeam getScoreboardTeam() {
		return this.world.getScoreboard().getPlayerTeam(this.method_15586());
	}

	public boolean isTeammate(Entity other) {
		return this.isTeamPlayer(other.getScoreboardTeam());
	}

	public boolean isTeamPlayer(AbstractTeam team) {
		return this.getScoreboardTeam() != null ? this.getScoreboardTeam().isEqual(team) : false;
	}

	public void setInvisible(boolean invisible) {
		this.setFlag(5, invisible);
	}

	protected boolean getFlag(int index) {
		return (this.dataTracker.get(FLAGS) & 1 << index) != 0;
	}

	protected void setFlag(int index, boolean value) {
		byte b = this.dataTracker.get(FLAGS);
		if (value) {
			this.dataTracker.set(FLAGS, (byte)(b | 1 << index));
		} else {
			this.dataTracker.set(FLAGS, (byte)(b & ~(1 << index)));
		}
	}

	public int method_15585() {
		return 300;
	}

	public int getAir() {
		return this.dataTracker.get(AIR);
	}

	public void setAir(int air) {
		this.dataTracker.set(AIR, air);
	}

	public void onLightningStrike(LightningBoltEntity lightning) {
		this.fireTicks++;
		if (this.fireTicks == 0) {
			this.setOnFireFor(8);
		}

		this.damage(DamageSource.LIGHTNING_BOLT, 5.0F);
	}

	public void method_15593(boolean bl) {
		if (bl) {
			this.velocityY = Math.max(-0.9, this.velocityY - 0.03);
		} else {
			this.velocityY = Math.min(1.8, this.velocityY + 0.1);
		}
	}

	public void method_15594(boolean bl) {
		if (bl) {
			this.velocityY = Math.max(-0.3, this.velocityY - 0.03);
		} else {
			this.velocityY = Math.min(0.7, this.velocityY + 0.06);
		}

		this.fallDistance = 0.0F;
	}

	public void onKilledOther(LivingEntity other) {
	}

	protected boolean pushOutOfBlocks(double x, double y, double z) {
		BlockPos blockPos = new BlockPos(x, y, z);
		double d = x - (double)blockPos.getX();
		double e = y - (double)blockPos.getY();
		double f = z - (double)blockPos.getZ();
		if (this.world.method_16387(null, this.getBoundingBox())) {
			return false;
		} else {
			Direction direction = Direction.UP;
			double g = Double.MAX_VALUE;
			if (!this.world.method_11492(blockPos.west()) && d < g) {
				g = d;
				direction = Direction.WEST;
			}

			if (!this.world.method_11492(blockPos.east()) && 1.0 - d < g) {
				g = 1.0 - d;
				direction = Direction.EAST;
			}

			if (!this.world.method_11492(blockPos.north()) && f < g) {
				g = f;
				direction = Direction.NORTH;
			}

			if (!this.world.method_11492(blockPos.south()) && 1.0 - f < g) {
				g = 1.0 - f;
				direction = Direction.SOUTH;
			}

			if (!this.world.method_11492(blockPos.up()) && 1.0 - e < g) {
				g = 1.0 - e;
				direction = Direction.UP;
			}

			float h = this.random.nextFloat() * 0.2F + 0.1F;
			float i = (float)direction.getAxisDirection().offset();
			if (direction.getAxis() == Direction.Axis.X) {
				this.velocityX = (double)(i * h);
				this.velocityY *= 0.75;
				this.velocityZ *= 0.75;
			} else if (direction.getAxis() == Direction.Axis.Y) {
				this.velocityX *= 0.75;
				this.velocityY = (double)(i * h);
				this.velocityZ *= 0.75;
			} else if (direction.getAxis() == Direction.Axis.Z) {
				this.velocityX *= 0.75;
				this.velocityY *= 0.75;
				this.velocityZ = (double)(i * h);
			}

			return true;
		}
	}

	public void setInLava() {
		this.inLava = true;
		this.fallDistance = 0.0F;
	}

	private static void method_15587(Text text) {
		text.styled(style -> style.setClickEvent(null)).getSiblings().forEach(Entity::method_15587);
	}

	@Override
	public Text method_15540() {
		Text text = this.method_15541();
		if (text != null) {
			Text text2 = text.method_20177();
			method_15587(text2);
			return text2;
		} else {
			return this.field_16697.method_15630();
		}
	}

	@Nullable
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
			Locale.ROOT,
			"%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f]",
			this.getClass().getSimpleName(),
			this.method_15540().computeValue(),
			this.entityId,
			this.world == null ? "~NULL~" : this.world.method_3588().getLevelName(),
			this.x,
			this.y,
			this.z
		);
	}

	public boolean isInvulnerableTo(DamageSource damageSource) {
		return this.invulnerable && damageSource != DamageSource.OUT_OF_WORLD && !damageSource.isSourceCreativePlayer();
	}

	public boolean isInvulnerable() {
		return this.invulnerable;
	}

	public void setInvulnerable(boolean invulnerable) {
		this.invulnerable = invulnerable;
	}

	public void copyPosition(Entity original) {
		this.refreshPositionAndAngles(original.x, original.y, original.z, original.yaw, original.pitch);
	}

	public void copyPortalInfo(Entity original) {
		NbtCompound nbtCompound = original.toNbt(new NbtCompound());
		nbtCompound.remove("Dimension");
		this.fromNbt(nbtCompound);
		this.netherPortalCooldown = original.netherPortalCooldown;
		this.lastPortalBlockPos = original.lastPortalBlockPos;
		this.lastPortalVec3d = original.lastPortalVec3d;
		this.teleportDirection = original.teleportDirection;
	}

	@Nullable
	public Entity method_15562(DimensionType dimensionType) {
		if (!this.world.isClient && !this.removed) {
			this.world.profiler.push("changeDimension");
			MinecraftServer minecraftServer = this.method_12833();
			DimensionType dimensionType2 = this.field_16696;
			ServerWorld serverWorld = minecraftServer.method_20312(dimensionType2);
			ServerWorld serverWorld2 = minecraftServer.method_20312(dimensionType);
			this.field_16696 = dimensionType;
			if (dimensionType2 == DimensionType.THE_END && dimensionType == DimensionType.THE_END) {
				serverWorld2 = minecraftServer.method_20312(DimensionType.OVERWORLD);
				this.field_16696 = DimensionType.OVERWORLD;
			}

			this.world.removeEntity(this);
			this.removed = false;
			this.world.profiler.push("reposition");
			BlockPos blockPos;
			if (dimensionType == DimensionType.THE_END) {
				blockPos = serverWorld2.getForcedSpawnPoint();
			} else {
				double d = this.x;
				double e = this.z;
				double f = 8.0;
				if (dimensionType == DimensionType.THE_NETHER) {
					d = MathHelper.clamp(d / 8.0, serverWorld2.method_8524().getBoundWest() + 16.0, serverWorld2.method_8524().getBoundEast() - 16.0);
					e = MathHelper.clamp(e / 8.0, serverWorld2.method_8524().getBoundNorth() + 16.0, serverWorld2.method_8524().getBoundSouth() - 16.0);
				} else if (dimensionType == DimensionType.OVERWORLD) {
					d = MathHelper.clamp(d * 8.0, serverWorld2.method_8524().getBoundWest() + 16.0, serverWorld2.method_8524().getBoundEast() - 16.0);
					e = MathHelper.clamp(e * 8.0, serverWorld2.method_8524().getBoundNorth() + 16.0, serverWorld2.method_8524().getBoundSouth() - 16.0);
				}

				d = (double)MathHelper.clamp((int)d, -29999872, 29999872);
				e = (double)MathHelper.clamp((int)e, -29999872, 29999872);
				float g = this.yaw;
				this.refreshPositionAndAngles(d, this.y, e, 90.0F, 0.0F);
				PortalTeleporter portalTeleporter = serverWorld2.getPortalTeleporter();
				portalTeleporter.method_8584(this, g);
				blockPos = new BlockPos(this);
			}

			serverWorld.checkChunk(this, false);
			this.world.profiler.swap("reloading");
			Entity entity = this.method_15557().spawn(serverWorld2);
			if (entity != null) {
				entity.copyPortalInfo(this);
				if (dimensionType2 == DimensionType.THE_END && dimensionType == DimensionType.THE_END) {
					BlockPos blockPos3 = serverWorld2.method_16373(class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES, serverWorld2.method_3585());
					entity.refreshPositionAndAngles(blockPos3, entity.yaw, entity.pitch);
				} else {
					entity.refreshPositionAndAngles(blockPos, entity.yaw, entity.pitch);
				}

				boolean bl = entity.teleporting;
				entity.teleporting = true;
				serverWorld2.method_3686(entity);
				entity.teleporting = bl;
				serverWorld2.checkChunk(entity, false);
			}

			this.removed = true;
			this.world.profiler.pop();
			serverWorld.resetIdleTimeout();
			serverWorld2.resetIdleTimeout();
			this.world.profiler.pop();
			return entity;
		} else {
			return null;
		}
	}

	public boolean canUsePortals() {
		return true;
	}

	public float method_10932(Explosion explosion, BlockView blockView, BlockPos blockPos, BlockState blockState, FluidState fluidState, float f) {
		return f;
	}

	public boolean method_10933(Explosion explosion, BlockView blockView, BlockPos blockPos, BlockState blockState, float f) {
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
		section.add("Entity Type", (CrashCallable<String>)(() -> EntityType.getId(this.method_15557()) + " (" + this.getClass().getCanonicalName() + ")"));
		section.add("Entity ID", this.entityId);
		section.add("Entity Name", (CrashCallable<String>)(() -> this.method_15540().getString()));
		section.add("Entity's Exact location", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", this.x, this.y, this.z));
		section.add("Entity's Block location", CrashReportSection.createPositionString(MathHelper.floor(this.x), MathHelper.floor(this.y), MathHelper.floor(this.z)));
		section.add("Entity's Momentum", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", this.velocityX, this.velocityY, this.velocityZ));
		section.add("Entity's Passengers", (CrashCallable<String>)(() -> this.getPassengerList().toString()));
		section.add("Entity's Vehicle", (CrashCallable<String>)(() -> this.getVehicle().toString()));
	}

	public boolean doesRenderOnFire() {
		return this.isOnFire();
	}

	public void setUuid(UUID uuid) {
		this.playerUuid = uuid;
		this.uuidString = this.playerUuid.toString();
	}

	public UUID getUuid() {
		return this.playerUuid;
	}

	public String getEntityName() {
		return this.uuidString;
	}

	public String method_15586() {
		return this.uuidString;
	}

	public boolean canFly() {
		return true;
	}

	public static double getRenderDistanceMultiplier() {
		return renderDistanceMultiplier;
	}

	public static void setRenderDistanceMultiplier(double value) {
		renderDistanceMultiplier = value;
	}

	@Override
	public Text getName() {
		return Team.method_18097(this.getScoreboardTeam(), this.method_15540())
			.styled(style -> style.setHoverEvent(this.getHoverEvent()).setInsertion(this.getEntityName()));
	}

	public void method_15578(@Nullable Text text) {
		this.dataTracker.set(CUSTOM_NAME, Optional.ofNullable(text));
	}

	@Nullable
	@Override
	public Text method_15541() {
		return (Text)this.dataTracker.get(CUSTOM_NAME).orElse(null);
	}

	@Override
	public boolean hasCustomName() {
		return this.dataTracker.get(CUSTOM_NAME).isPresent();
	}

	public void setCustomNameVisible(boolean visible) {
		this.dataTracker.set(NAME_VISIBLE, visible);
	}

	public boolean isCustomNameVisible() {
		return this.dataTracker.get(NAME_VISIBLE);
	}

	public void refreshPositionAfterTeleport(double x, double y, double z) {
		this.teleportRequested = true;
		this.refreshPositionAndAngles(x, y, z, this.yaw, this.pitch);
		this.world.checkChunk(this, false);
	}

	public boolean shouldRenderName() {
		return this.isCustomNameVisible();
	}

	public void onTrackedDataSet(TrackedData<?> data) {
	}

	public Direction getHorizontalDirection() {
		return Direction.fromRotation((double)this.yaw);
	}

	public Direction getMovementDirection() {
		return this.getHorizontalDirection();
	}

	protected HoverEvent getHoverEvent() {
		NbtCompound nbtCompound = new NbtCompound();
		Identifier identifier = EntityType.getId(this.method_15557());
		nbtCompound.putString("id", this.getEntityName());
		if (identifier != null) {
			nbtCompound.putString("type", identifier.toString());
		}

		nbtCompound.putString("name", Text.Serializer.serialize(this.method_15540()));
		return new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new LiteralText(nbtCompound.toString()));
	}

	public boolean isSpectatedBy(ServerPlayerEntity player) {
		return true;
	}

	public Box getBoundingBox() {
		return this.boundingBox;
	}

	public Box getVisibilityBoundingBox() {
		return this.getBoundingBox();
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
	public void method_5505(Text text) {
	}

	public BlockPos method_4086() {
		return new BlockPos(this);
	}

	public Vec3d method_10787() {
		return new Vec3d(this.x, this.y, this.z);
	}

	public World method_5506() {
		return this.world;
	}

	@Nullable
	public MinecraftServer method_12833() {
		return this.world.getServer();
	}

	public ActionResult interactAt(PlayerEntity player, Vec3d hitPos, Hand hand) {
		return ActionResult.PASS;
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

	public void onStartedTrackingBy(ServerPlayerEntity player) {
	}

	public void onStoppedTrackingBy(ServerPlayerEntity player) {
	}

	public float applyRotation(BlockRotation rotation) {
		float f = MathHelper.wrapDegrees(this.yaw);
		switch (rotation) {
			case CLOCKWISE_180:
				return f + 180.0F;
			case COUNTERCLOCKWISE_90:
				return f + 270.0F;
			case CLOCKWISE_90:
				return f + 90.0F;
			default:
				return f;
		}
	}

	public float applyMirror(BlockMirror mirror) {
		float f = MathHelper.wrapDegrees(this.yaw);
		switch (mirror) {
			case LEFT_RIGHT:
				return -f;
			case FRONT_BACK:
				return 180.0F - f;
			default:
				return f;
		}
	}

	public boolean entityDataRequiresOperator() {
		return false;
	}

	public boolean teleportRequested() {
		boolean bl = this.teleportRequested;
		this.teleportRequested = false;
		return bl;
	}

	@Nullable
	public Entity getPrimaryPassenger() {
		return null;
	}

	public List<Entity> getPassengerList() {
		return (List<Entity>)(this.passengerList.isEmpty() ? Collections.emptyList() : Lists.newArrayList(this.passengerList));
	}

	public boolean hasPassenger(Entity passenger) {
		for (Entity entity : this.getPassengerList()) {
			if (entity.equals(passenger)) {
				return true;
			}
		}

		return false;
	}

	public boolean method_15566(Class<? extends Entity> class_) {
		for (Entity entity : this.getPassengerList()) {
			if (class_.isAssignableFrom(entity.getClass())) {
				return true;
			}
		}

		return false;
	}

	public Collection<Entity> getPassengersDeep() {
		Set<Entity> set = Sets.newHashSet();

		for (Entity entity : this.getPassengerList()) {
			set.add(entity);
			entity.method_15568(false, set);
		}

		return set;
	}

	public boolean method_15581() {
		Set<Entity> set = Sets.newHashSet();
		this.method_15568(true, set);
		return set.size() == 1;
	}

	private void method_15568(boolean bl, Set<Entity> set) {
		for (Entity entity : this.getPassengerList()) {
			if (!bl || ServerPlayerEntity.class.isAssignableFrom(entity.getClass())) {
				set.add(entity);
			}

			entity.method_15568(bl, set);
		}
	}

	public Entity getRootVehicle() {
		Entity entity = this;

		while (entity.hasMount()) {
			entity = entity.getVehicle();
		}

		return entity;
	}

	public boolean isConnectedThroughVehicle(Entity entity) {
		return this.getRootVehicle() == entity.getRootVehicle();
	}

	public boolean hasPassengerDeep(Entity passenger) {
		for (Entity entity : this.getPassengerList()) {
			if (entity.equals(passenger)) {
				return true;
			}

			if (entity.hasPassengerDeep(passenger)) {
				return true;
			}
		}

		return false;
	}

	public boolean method_13003() {
		Entity entity = this.getPrimaryPassenger();
		return entity instanceof PlayerEntity ? ((PlayerEntity)entity).isMainPlayer() : !this.world.isClient;
	}

	@Nullable
	public Entity getVehicle() {
		return this.mount;
	}

	public PistonBehavior getPistonBehavior() {
		return PistonBehavior.NORMAL;
	}

	public SoundCategory getSoundCategory() {
		return SoundCategory.NEUTRAL;
	}

	protected int getBurningDuration() {
		return 1;
	}

	public class_3915 method_15582() {
		return new class_3915(
			this,
			new Vec3d(this.x, this.y, this.z),
			this.getRotationClient(),
			this.world instanceof ServerWorld ? (ServerWorld)this.world : null,
			this.method_12265(),
			this.method_15540().getString(),
			this.getName(),
			this.world.getServer(),
			this
		);
	}

	protected int method_12265() {
		return 0;
	}

	public boolean method_15592(int i) {
		return this.method_12265() >= i;
	}

	@Override
	public boolean method_17413() {
		return this.world.getGameRules().getBoolean("sendCommandFeedback");
	}

	@Override
	public boolean method_17414() {
		return true;
	}

	@Override
	public boolean method_17412() {
		return true;
	}

	public void method_15563(class_4048.class_4049 arg, Vec3d vec3d) {
		Vec3d vec3d2 = arg.method_17870(this);
		double d = vec3d.x - vec3d2.x;
		double e = vec3d.y - vec3d2.y;
		double f = vec3d.z - vec3d2.z;
		double g = (double)MathHelper.sqrt(d * d + f * f);
		this.pitch = MathHelper.wrapDegrees((float)(-(MathHelper.atan2(e, g) * 180.0F / (float)Math.PI)));
		this.yaw = MathHelper.wrapDegrees((float)(MathHelper.atan2(f, d) * 180.0F / (float)Math.PI) - 90.0F);
		this.setHeadYaw(this.yaw);
		this.prevPitch = this.pitch;
		this.prevYaw = this.yaw;
	}

	public boolean method_15580(Tag<Fluid> tag) {
		Box box = this.getBoundingBox().contract(0.001);
		int i = MathHelper.floor(box.minX);
		int j = MathHelper.ceil(box.maxX);
		int k = MathHelper.floor(box.minY);
		int l = MathHelper.ceil(box.maxY);
		int m = MathHelper.floor(box.minZ);
		int n = MathHelper.ceil(box.maxZ);
		if (!this.world.method_16362(i, k, m, j, l, n, true)) {
			return false;
		} else {
			double d = 0.0;
			boolean bl = this.canFly();
			boolean bl2 = false;
			Vec3d vec3d = Vec3d.ZERO;
			int o = 0;

			try (BlockPos.Pooled pooled = BlockPos.Pooled.get()) {
				for (int p = i; p < j; p++) {
					for (int q = k; q < l; q++) {
						for (int r = m; r < n; r++) {
							pooled.setPosition(p, q, r);
							FluidState fluidState = this.world.getFluidState(pooled);
							if (fluidState.matches(tag)) {
								double e = (double)((float)q + fluidState.method_17810());
								if (e >= box.minY) {
									bl2 = true;
									d = Math.max(e - box.minY, d);
									if (bl) {
										Vec3d vec3d2 = fluidState.method_17803(this.world, pooled);
										if (d < 0.4) {
											vec3d2 = vec3d2.multiply(d);
										}

										vec3d = vec3d.add(vec3d2);
										o++;
									}
								}
							}
						}
					}
				}
			}

			if (vec3d.length() > 0.0) {
				if (o > 0) {
					vec3d = vec3d.multiply(1.0 / (double)o);
				}

				if (!(this instanceof PlayerEntity)) {
					vec3d = vec3d.normalize();
				}

				double f = 0.014;
				this.velocityX = this.velocityX + vec3d.x * 0.014;
				this.velocityY = this.velocityY + vec3d.y * 0.014;
				this.velocityZ = this.velocityZ + vec3d.z * 0.014;
			}

			this.field_16693 = d;
			return bl2;
		}
	}

	public double method_15583() {
		return this.field_16693;
	}
}
