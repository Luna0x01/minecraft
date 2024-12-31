package net.minecraft.entity;

import com.google.common.collect.ImmutableSet;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.HoneyBlock;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.network.packet.EntityS2CPacket;
import net.minecraft.command.arguments.EntityAnchorArgumentType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.Packet;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Nameable;
import net.minecraft.util.ReusableStream;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.explosion.Explosion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Entity implements Nameable, CommandOutput {
	protected static final Logger LOGGER = LogManager.getLogger();
	private static final AtomicInteger MAX_ENTITY_ID = new AtomicInteger();
	private static final List<ItemStack> EMPTY_STACK_LIST = Collections.emptyList();
	private static final Box NULL_BOX = new Box(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
	private static double renderDistanceMultiplier = 1.0;
	private final EntityType<?> type;
	private int entityId = MAX_ENTITY_ID.incrementAndGet();
	public boolean inanimate;
	private final List<Entity> passengerList = Lists.newArrayList();
	protected int ridingCooldown;
	@Nullable
	private Entity vehicle;
	public boolean teleporting;
	public World world;
	public double prevX;
	public double prevY;
	public double prevZ;
	private double x;
	private double y;
	private double z;
	private Vec3d velocity = Vec3d.ZERO;
	public float yaw;
	public float pitch;
	public float prevYaw;
	public float prevPitch;
	private Box entityBounds = NULL_BOX;
	public boolean onGround;
	public boolean horizontalCollision;
	public boolean verticalCollision;
	public boolean collided;
	public boolean velocityModified;
	protected Vec3d movementMultiplier = Vec3d.ZERO;
	public boolean removed;
	public float prevHorizontalSpeed;
	public float horizontalSpeed;
	public float distanceTraveled;
	public float fallDistance;
	private float nextStepSoundDistance = 1.0F;
	private float nextFlySoundDistance = 1.0F;
	public double lastRenderX;
	public double lastRenderY;
	public double lastRenderZ;
	public float stepHeight;
	public boolean noClip;
	public float pushSpeedReduction;
	protected final Random random = new Random();
	public int age;
	private int fireTicks = -this.getBurningDuration();
	protected boolean touchingWater;
	protected double waterHeight;
	protected boolean submergedInWater;
	protected boolean inLava;
	public int timeUntilRegen;
	protected boolean firstUpdate = true;
	protected final DataTracker dataTracker;
	protected static final TrackedData<Byte> FLAGS = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BYTE);
	private static final TrackedData<Integer> AIR = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Optional<Text>> CUSTOM_NAME = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.OPTIONAL_TEXT_COMPONENT);
	private static final TrackedData<Boolean> NAME_VISIBLE = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> SILENT = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> NO_GRAVITY = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BOOLEAN);
	protected static final TrackedData<EntityPose> POSE = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.ENTITY_POSE);
	public boolean updateNeeded;
	public int chunkX;
	public int chunkY;
	public int chunkZ;
	public long trackedX;
	public long trackedY;
	public long trackedZ;
	public boolean ignoreCameraFrustum;
	public boolean velocityDirty;
	public int netherPortalCooldown;
	protected boolean inNetherPortal;
	protected int netherPortalTime;
	public DimensionType dimension;
	protected BlockPos lastNetherPortalPosition;
	protected Vec3d lastNetherPortalDirectionVector;
	protected Direction lastNetherPortalDirection;
	private boolean invulnerable;
	protected UUID uuid = MathHelper.randomUuid(this.random);
	protected String uuidString = this.uuid.toString();
	protected boolean glowing;
	private final Set<String> scoreboardTags = Sets.newHashSet();
	private boolean teleportRequested;
	private final double[] pistonMovementDelta = new double[]{0.0, 0.0, 0.0};
	private long pistonMovementTick;
	private EntityDimensions dimensions;
	private float standingEyeHeight;

	public Entity(EntityType<?> entityType, World world) {
		this.type = entityType;
		this.world = world;
		this.dimensions = entityType.getDimensions();
		this.updatePosition(0.0, 0.0, 0.0);
		if (world != null) {
			this.dimension = world.dimension.getType();
		}

		this.dataTracker = new DataTracker(this);
		this.dataTracker.startTracking(FLAGS, (byte)0);
		this.dataTracker.startTracking(AIR, this.getMaxAir());
		this.dataTracker.startTracking(NAME_VISIBLE, false);
		this.dataTracker.startTracking(CUSTOM_NAME, Optional.empty());
		this.dataTracker.startTracking(SILENT, false);
		this.dataTracker.startTracking(NO_GRAVITY, false);
		this.dataTracker.startTracking(POSE, EntityPose.field_18076);
		this.initDataTracker();
		this.standingEyeHeight = this.getEyeHeight(EntityPose.field_18076, this.dimensions);
	}

	public int getTeamColorValue() {
		AbstractTeam abstractTeam = this.getScoreboardTeam();
		return abstractTeam != null && abstractTeam.getColor().getColorValue() != null ? abstractTeam.getColor().getColorValue() : 16777215;
	}

	public boolean isSpectator() {
		return false;
	}

	public final void detach() {
		if (this.hasPassengers()) {
			this.removeAllPassengers();
		}

		if (this.hasVehicle()) {
			this.stopRiding();
		}
	}

	public void updateTrackedPosition(double d, double e, double f) {
		this.trackedX = EntityS2CPacket.encodePacketCoordinate(d);
		this.trackedY = EntityS2CPacket.encodePacketCoordinate(e);
		this.trackedZ = EntityS2CPacket.encodePacketCoordinate(f);
	}

	public EntityType<?> getType() {
		return this.type;
	}

	public int getEntityId() {
		return this.entityId;
	}

	public void setEntityId(int i) {
		this.entityId = i;
	}

	public Set<String> getScoreboardTags() {
		return this.scoreboardTags;
	}

	public boolean addScoreboardTag(String string) {
		return this.scoreboardTags.size() >= 1024 ? false : this.scoreboardTags.add(string);
	}

	public boolean removeScoreboardTag(String string) {
		return this.scoreboardTags.remove(string);
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
			for (double d = this.getY(); d > 0.0 && d < 256.0; d++) {
				this.updatePosition(this.getX(), d, this.getZ());
				if (this.world.doesNotCollide(this)) {
					break;
				}
			}

			this.setVelocity(Vec3d.ZERO);
			this.pitch = 0.0F;
		}
	}

	public void remove() {
		this.removed = true;
	}

	protected void setPose(EntityPose entityPose) {
		this.dataTracker.set(POSE, entityPose);
	}

	public EntityPose getPose() {
		return this.dataTracker.get(POSE);
	}

	protected void setRotation(float f, float g) {
		this.yaw = f % 360.0F;
		this.pitch = g % 360.0F;
	}

	public void updatePosition(double d, double e, double f) {
		this.setPos(d, e, f);
		float g = this.dimensions.width / 2.0F;
		float h = this.dimensions.height;
		this.setBoundingBox(new Box(d - (double)g, e, f - (double)g, d + (double)g, e + (double)h, f + (double)g));
	}

	protected void refreshPosition() {
		this.updatePosition(this.x, this.y, this.z);
	}

	public void changeLookDirection(double d, double e) {
		double f = e * 0.15;
		double g = d * 0.15;
		this.pitch = (float)((double)this.pitch + f);
		this.yaw = (float)((double)this.yaw + g);
		this.pitch = MathHelper.clamp(this.pitch, -90.0F, 90.0F);
		this.prevPitch = (float)((double)this.prevPitch + f);
		this.prevYaw = (float)((double)this.prevYaw + g);
		this.prevPitch = MathHelper.clamp(this.prevPitch, -90.0F, 90.0F);
		if (this.vehicle != null) {
			this.vehicle.onPassengerLookAround(this);
		}
	}

	public void tick() {
		if (!this.world.isClient) {
			this.setFlag(6, this.isGlowing());
		}

		this.baseTick();
	}

	public void baseTick() {
		this.world.getProfiler().push("entityBaseTick");
		if (this.hasVehicle() && this.getVehicle().removed) {
			this.stopRiding();
		}

		if (this.ridingCooldown > 0) {
			this.ridingCooldown--;
		}

		this.prevHorizontalSpeed = this.horizontalSpeed;
		this.prevPitch = this.pitch;
		this.prevYaw = this.yaw;
		this.tickNetherPortal();
		this.attemptSprintingParticles();
		this.updateWaterState();
		if (this.world.isClient) {
			this.extinguish();
		} else if (this.fireTicks > 0) {
			if (this.isFireImmune()) {
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

		if (this.isInLava()) {
			this.setOnFireFromLava();
			this.fallDistance *= 0.5F;
		}

		if (this.getY() < -64.0) {
			this.destroy();
		}

		if (!this.world.isClient) {
			this.setFlag(0, this.fireTicks > 0);
		}

		this.firstUpdate = false;
		this.world.getProfiler().pop();
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
		if (!this.isFireImmune()) {
			this.setOnFireFor(15);
			this.damage(DamageSource.LAVA, 4.0F);
		}
	}

	public void setOnFireFor(int i) {
		int j = i * 20;
		if (this instanceof LivingEntity) {
			j = ProtectionEnchantment.transformFireDuration((LivingEntity)this, j);
		}

		if (this.fireTicks < j) {
			this.fireTicks = j;
		}
	}

	public void setFireTicks(int i) {
		this.fireTicks = i;
	}

	public int getFireTicks() {
		return this.fireTicks;
	}

	public void extinguish() {
		this.fireTicks = 0;
	}

	protected void destroy() {
		this.remove();
	}

	public boolean doesNotCollide(double d, double e, double f) {
		return this.doesNotCollide(this.getBoundingBox().offset(d, e, f));
	}

	private boolean doesNotCollide(Box box) {
		return this.world.doesNotCollide(this, box) && !this.world.containsFluid(box);
	}

	public void move(MovementType movementType, Vec3d vec3d) {
		if (this.noClip) {
			this.setBoundingBox(this.getBoundingBox().offset(vec3d));
			this.moveToBoundingBoxCenter();
		} else {
			if (movementType == MovementType.field_6310) {
				vec3d = this.adjustMovementForPiston(vec3d);
				if (vec3d.equals(Vec3d.ZERO)) {
					return;
				}
			}

			this.world.getProfiler().push("move");
			if (this.movementMultiplier.lengthSquared() > 1.0E-7) {
				vec3d = vec3d.multiply(this.movementMultiplier);
				this.movementMultiplier = Vec3d.ZERO;
				this.setVelocity(Vec3d.ZERO);
			}

			vec3d = this.adjustMovementForSneaking(vec3d, movementType);
			Vec3d vec3d2 = this.adjustMovementForCollisions(vec3d);
			if (vec3d2.lengthSquared() > 1.0E-7) {
				this.setBoundingBox(this.getBoundingBox().offset(vec3d2));
				this.moveToBoundingBoxCenter();
			}

			this.world.getProfiler().pop();
			this.world.getProfiler().push("rest");
			this.horizontalCollision = !MathHelper.approximatelyEquals(vec3d.x, vec3d2.x) || !MathHelper.approximatelyEquals(vec3d.z, vec3d2.z);
			this.verticalCollision = vec3d.y != vec3d2.y;
			this.onGround = this.verticalCollision && vec3d.y < 0.0;
			this.collided = this.horizontalCollision || this.verticalCollision;
			BlockPos blockPos = this.getLandingPos();
			BlockState blockState = this.world.getBlockState(blockPos);
			this.fall(vec3d2.y, this.onGround, blockState, blockPos);
			Vec3d vec3d3 = this.getVelocity();
			if (vec3d.x != vec3d2.x) {
				this.setVelocity(0.0, vec3d3.y, vec3d3.z);
			}

			if (vec3d.z != vec3d2.z) {
				this.setVelocity(vec3d3.x, vec3d3.y, 0.0);
			}

			Block block = blockState.getBlock();
			if (vec3d.y != vec3d2.y) {
				block.onEntityLand(this.world, this);
			}

			if (this.onGround && !this.bypassesSteppingEffects()) {
				block.onSteppedOn(this.world, blockPos, this);
			}

			if (this.canClimb() && !this.hasVehicle()) {
				double d = vec3d2.x;
				double e = vec3d2.y;
				double f = vec3d2.z;
				if (block != Blocks.field_9983 && block != Blocks.field_16492) {
					e = 0.0;
				}

				this.horizontalSpeed = (float)((double)this.horizontalSpeed + (double)MathHelper.sqrt(squaredHorizontalLength(vec3d2)) * 0.6);
				this.distanceTraveled = (float)((double)this.distanceTraveled + (double)MathHelper.sqrt(d * d + e * e + f * f) * 0.6);
				if (this.distanceTraveled > this.nextStepSoundDistance && !blockState.isAir()) {
					this.nextStepSoundDistance = this.calculateNextStepSoundDistance();
					if (this.isTouchingWater()) {
						Entity entity = this.hasPassengers() && this.getPrimaryPassenger() != null ? this.getPrimaryPassenger() : this;
						float g = entity == this ? 0.35F : 0.4F;
						Vec3d vec3d4 = entity.getVelocity();
						float h = MathHelper.sqrt(vec3d4.x * vec3d4.x * 0.2F + vec3d4.y * vec3d4.y + vec3d4.z * vec3d4.z * 0.2F) * g;
						if (h > 1.0F) {
							h = 1.0F;
						}

						this.playSwimSound(h);
					} else {
						this.playStepSound(blockPos, blockState);
					}
				} else if (this.distanceTraveled > this.nextFlySoundDistance && this.hasWings() && blockState.isAir()) {
					this.nextFlySoundDistance = this.playFlySound(this.distanceTraveled);
				}
			}

			try {
				this.inLava = false;
				this.checkBlockCollision();
			} catch (Throwable var18) {
				CrashReport crashReport = CrashReport.create(var18, "Checking entity block collision");
				CrashReportSection crashReportSection = crashReport.addElement("Entity being checked for collision");
				this.populateCrashReport(crashReportSection);
				throw new CrashException(crashReport);
			}

			this.setVelocity(this.getVelocity().multiply((double)this.getVelocityMultiplier(), 1.0, (double)this.getVelocityMultiplier()));
			boolean bl = this.isWet();
			if (this.world.doesAreaContainFireSource(this.getBoundingBox().contract(0.001))) {
				if (!bl) {
					this.fireTicks++;
					if (this.fireTicks == 0) {
						this.setOnFireFor(8);
					}
				}

				this.burn(1);
			} else if (this.fireTicks <= 0) {
				this.fireTicks = -this.getBurningDuration();
			}

			if (bl && this.isOnFire()) {
				this.playSound(SoundEvents.field_15222, 0.7F, 1.6F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
				this.fireTicks = -this.getBurningDuration();
			}

			this.world.getProfiler().pop();
		}
	}

	protected BlockPos getLandingPos() {
		int i = MathHelper.floor(this.x);
		int j = MathHelper.floor(this.y - 0.2F);
		int k = MathHelper.floor(this.z);
		BlockPos blockPos = new BlockPos(i, j, k);
		if (this.world.getBlockState(blockPos).isAir()) {
			BlockPos blockPos2 = blockPos.down();
			BlockState blockState = this.world.getBlockState(blockPos2);
			Block block = blockState.getBlock();
			if (block.matches(BlockTags.field_16584) || block.matches(BlockTags.field_15504) || block instanceof FenceGateBlock) {
				return blockPos2;
			}
		}

		return blockPos;
	}

	protected float getJumpVelocityMultiplier() {
		float f = this.world.getBlockState(new BlockPos(this)).getBlock().getJumpVelocityMultiplier();
		float g = this.world.getBlockState(this.getVelocityAffectingPos()).getBlock().getJumpVelocityMultiplier();
		return (double)f == 1.0 ? g : f;
	}

	protected float getVelocityMultiplier() {
		Block block = this.world.getBlockState(new BlockPos(this)).getBlock();
		float f = block.getVelocityMultiplier();
		if (block != Blocks.field_10382 && block != Blocks.field_10422) {
			return (double)f == 1.0 ? this.world.getBlockState(this.getVelocityAffectingPos()).getBlock().getVelocityMultiplier() : f;
		} else {
			return f;
		}
	}

	protected BlockPos getVelocityAffectingPos() {
		return new BlockPos(this.x, this.getBoundingBox().y1 - 0.5000001, this.z);
	}

	protected Vec3d adjustMovementForSneaking(Vec3d vec3d, MovementType movementType) {
		return vec3d;
	}

	protected Vec3d adjustMovementForPiston(Vec3d vec3d) {
		if (vec3d.lengthSquared() <= 1.0E-7) {
			return vec3d;
		} else {
			long l = this.world.getTime();
			if (l != this.pistonMovementTick) {
				Arrays.fill(this.pistonMovementDelta, 0.0);
				this.pistonMovementTick = l;
			}

			if (vec3d.x != 0.0) {
				double d = this.calculatePistonMovementFactor(Direction.Axis.field_11048, vec3d.x);
				return Math.abs(d) <= 1.0E-5F ? Vec3d.ZERO : new Vec3d(d, 0.0, 0.0);
			} else if (vec3d.y != 0.0) {
				double e = this.calculatePistonMovementFactor(Direction.Axis.field_11052, vec3d.y);
				return Math.abs(e) <= 1.0E-5F ? Vec3d.ZERO : new Vec3d(0.0, e, 0.0);
			} else if (vec3d.z != 0.0) {
				double f = this.calculatePistonMovementFactor(Direction.Axis.field_11051, vec3d.z);
				return Math.abs(f) <= 1.0E-5F ? Vec3d.ZERO : new Vec3d(0.0, 0.0, f);
			} else {
				return Vec3d.ZERO;
			}
		}
	}

	private double calculatePistonMovementFactor(Direction.Axis axis, double d) {
		int i = axis.ordinal();
		double e = MathHelper.clamp(d + this.pistonMovementDelta[i], -0.51, 0.51);
		d = e - this.pistonMovementDelta[i];
		this.pistonMovementDelta[i] = e;
		return d;
	}

	private Vec3d adjustMovementForCollisions(Vec3d vec3d) {
		Box box = this.getBoundingBox();
		EntityContext entityContext = EntityContext.of(this);
		VoxelShape voxelShape = this.world.getWorldBorder().asVoxelShape();
		Stream<VoxelShape> stream = VoxelShapes.matchesAnywhere(voxelShape, VoxelShapes.cuboid(box.contract(1.0E-7)), BooleanBiFunction.AND)
			? Stream.empty()
			: Stream.of(voxelShape);
		Stream<VoxelShape> stream2 = this.world.getEntityCollisions(this, box.stretch(vec3d), ImmutableSet.of());
		ReusableStream<VoxelShape> reusableStream = new ReusableStream<>(Stream.concat(stream2, stream));
		Vec3d vec3d2 = vec3d.lengthSquared() == 0.0 ? vec3d : adjustMovementForCollisions(this, vec3d, box, this.world, entityContext, reusableStream);
		boolean bl = vec3d.x != vec3d2.x;
		boolean bl2 = vec3d.y != vec3d2.y;
		boolean bl3 = vec3d.z != vec3d2.z;
		boolean bl4 = this.onGround || bl2 && vec3d.y < 0.0;
		if (this.stepHeight > 0.0F && bl4 && (bl || bl3)) {
			Vec3d vec3d3 = adjustMovementForCollisions(this, new Vec3d(vec3d.x, (double)this.stepHeight, vec3d.z), box, this.world, entityContext, reusableStream);
			Vec3d vec3d4 = adjustMovementForCollisions(
				this, new Vec3d(0.0, (double)this.stepHeight, 0.0), box.stretch(vec3d.x, 0.0, vec3d.z), this.world, entityContext, reusableStream
			);
			if (vec3d4.y < (double)this.stepHeight) {
				Vec3d vec3d5 = adjustMovementForCollisions(this, new Vec3d(vec3d.x, 0.0, vec3d.z), box.offset(vec3d4), this.world, entityContext, reusableStream)
					.add(vec3d4);
				if (squaredHorizontalLength(vec3d5) > squaredHorizontalLength(vec3d3)) {
					vec3d3 = vec3d5;
				}
			}

			if (squaredHorizontalLength(vec3d3) > squaredHorizontalLength(vec3d2)) {
				return vec3d3.add(
					adjustMovementForCollisions(this, new Vec3d(0.0, -vec3d3.y + vec3d.y, 0.0), box.offset(vec3d3), this.world, entityContext, reusableStream)
				);
			}
		}

		return vec3d2;
	}

	public static double squaredHorizontalLength(Vec3d vec3d) {
		return vec3d.x * vec3d.x + vec3d.z * vec3d.z;
	}

	public static Vec3d adjustMovementForCollisions(
		@Nullable Entity entity, Vec3d vec3d, Box box, World world, EntityContext entityContext, ReusableStream<VoxelShape> reusableStream
	) {
		boolean bl = vec3d.x == 0.0;
		boolean bl2 = vec3d.y == 0.0;
		boolean bl3 = vec3d.z == 0.0;
		if ((!bl || !bl2) && (!bl || !bl3) && (!bl2 || !bl3)) {
			ReusableStream<VoxelShape> reusableStream2 = new ReusableStream<>(
				Stream.concat(reusableStream.stream(), world.getBlockCollisions(entity, box.stretch(vec3d)))
			);
			return adjustMovementForCollisions(vec3d, box, reusableStream2);
		} else {
			return adjustSingleAxisMovementForCollisions(vec3d, box, world, entityContext, reusableStream);
		}
	}

	public static Vec3d adjustMovementForCollisions(Vec3d vec3d, Box box, ReusableStream<VoxelShape> reusableStream) {
		double d = vec3d.x;
		double e = vec3d.y;
		double f = vec3d.z;
		if (e != 0.0) {
			e = VoxelShapes.calculateMaxOffset(Direction.Axis.field_11052, box, reusableStream.stream(), e);
			if (e != 0.0) {
				box = box.offset(0.0, e, 0.0);
			}
		}

		boolean bl = Math.abs(d) < Math.abs(f);
		if (bl && f != 0.0) {
			f = VoxelShapes.calculateMaxOffset(Direction.Axis.field_11051, box, reusableStream.stream(), f);
			if (f != 0.0) {
				box = box.offset(0.0, 0.0, f);
			}
		}

		if (d != 0.0) {
			d = VoxelShapes.calculateMaxOffset(Direction.Axis.field_11048, box, reusableStream.stream(), d);
			if (!bl && d != 0.0) {
				box = box.offset(d, 0.0, 0.0);
			}
		}

		if (!bl && f != 0.0) {
			f = VoxelShapes.calculateMaxOffset(Direction.Axis.field_11051, box, reusableStream.stream(), f);
		}

		return new Vec3d(d, e, f);
	}

	public static Vec3d adjustSingleAxisMovementForCollisions(
		Vec3d vec3d, Box box, WorldView worldView, EntityContext entityContext, ReusableStream<VoxelShape> reusableStream
	) {
		double d = vec3d.x;
		double e = vec3d.y;
		double f = vec3d.z;
		if (e != 0.0) {
			e = VoxelShapes.calculatePushVelocity(Direction.Axis.field_11052, box, worldView, e, entityContext, reusableStream.stream());
			if (e != 0.0) {
				box = box.offset(0.0, e, 0.0);
			}
		}

		boolean bl = Math.abs(d) < Math.abs(f);
		if (bl && f != 0.0) {
			f = VoxelShapes.calculatePushVelocity(Direction.Axis.field_11051, box, worldView, f, entityContext, reusableStream.stream());
			if (f != 0.0) {
				box = box.offset(0.0, 0.0, f);
			}
		}

		if (d != 0.0) {
			d = VoxelShapes.calculatePushVelocity(Direction.Axis.field_11048, box, worldView, d, entityContext, reusableStream.stream());
			if (!bl && d != 0.0) {
				box = box.offset(d, 0.0, 0.0);
			}
		}

		if (!bl && f != 0.0) {
			f = VoxelShapes.calculatePushVelocity(Direction.Axis.field_11051, box, worldView, f, entityContext, reusableStream.stream());
		}

		return new Vec3d(d, e, f);
	}

	protected float calculateNextStepSoundDistance() {
		return (float)((int)this.distanceTraveled + 1);
	}

	public void moveToBoundingBoxCenter() {
		Box box = this.getBoundingBox();
		this.setPos((box.x1 + box.x2) / 2.0, box.y1, (box.z1 + box.z2) / 2.0);
	}

	protected SoundEvent getSwimSound() {
		return SoundEvents.field_14818;
	}

	protected SoundEvent getSplashSound() {
		return SoundEvents.field_14737;
	}

	protected SoundEvent getHighSpeedSplashSound() {
		return SoundEvents.field_14737;
	}

	protected void checkBlockCollision() {
		// $VF: Couldn't be decompiled
		// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
		// java.lang.OutOfMemoryError: Java heap space
		//   at java.base/java.util.LinkedHashMap$LinkedKeySet.iterator(LinkedHashMap.java:564)
		//   at java.base/java.util.HashSet.iterator(HashSet.java:174)
		//   at org.jetbrains.java.decompiler.modules.decompiler.decompose.GenericDominatorEngine.calcIDoms(GenericDominatorEngine.java:90)
		//   at org.jetbrains.java.decompiler.modules.decompiler.decompose.GenericDominatorEngine.initialize(GenericDominatorEngine.java:24)
		//   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarVersionsGraph.initDominators(VarVersionsGraph.java:135)
		//   at org.jetbrains.java.decompiler.modules.decompiler.sforms.SSAUConstructorSparseEx.splitVariables(SSAUConstructorSparseEx.java:47)
		//   at org.jetbrains.java.decompiler.modules.decompiler.StackVarsProcessor.simplifyStackVars(StackVarsProcessor.java:86)
		//   at org.jetbrains.java.decompiler.modules.decompiler.StackVarsProcessor.simplifyStackVars(StackVarsProcessor.java:40)
		//   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:224)
		//
		// Bytecode:
		// 000: aload 0
		// 001: invokevirtual net/minecraft/entity/Entity.getBoundingBox ()Lnet/minecraft/util/math/Box;
		// 004: astore 1
		// 005: aload 1
		// 006: getfield net/minecraft/util/math/Box.x1 D
		// 009: ldc2_w 0.001
		// 00c: dadd
		// 00d: aload 1
		// 00e: getfield net/minecraft/util/math/Box.y1 D
		// 011: ldc2_w 0.001
		// 014: dadd
		// 015: aload 1
		// 016: getfield net/minecraft/util/math/Box.z1 D
		// 019: ldc2_w 0.001
		// 01c: dadd
		// 01d: invokestatic net/minecraft/util/math/BlockPos$PooledMutable.get (DDD)Lnet/minecraft/util/math/BlockPos$PooledMutable;
		// 020: astore 2
		// 021: aconst_null
		// 022: astore 3
		// 023: aload 1
		// 024: getfield net/minecraft/util/math/Box.x2 D
		// 027: ldc2_w 0.001
		// 02a: dsub
		// 02b: aload 1
		// 02c: getfield net/minecraft/util/math/Box.y2 D
		// 02f: ldc2_w 0.001
		// 032: dsub
		// 033: aload 1
		// 034: getfield net/minecraft/util/math/Box.z2 D
		// 037: ldc2_w 0.001
		// 03a: dsub
		// 03b: invokestatic net/minecraft/util/math/BlockPos$PooledMutable.get (DDD)Lnet/minecraft/util/math/BlockPos$PooledMutable;
		// 03e: astore 4
		// 040: aconst_null
		// 041: astore 5
		// 043: invokestatic net/minecraft/util/math/BlockPos$PooledMutable.get ()Lnet/minecraft/util/math/BlockPos$PooledMutable;
		// 046: astore 6
		// 048: aconst_null
		// 049: astore 7
		// 04b: aload 0
		// 04c: getfield net/minecraft/entity/Entity.world Lnet/minecraft/world/World;
		// 04f: aload 2
		// 050: aload 4
		// 052: invokevirtual net/minecraft/world/World.isRegionLoaded (Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;)Z
		// 055: ifeq 0ef
		// 058: aload 2
		// 059: invokevirtual net/minecraft/util/math/BlockPos$PooledMutable.getX ()I
		// 05c: istore 8
		// 05e: iload 8
		// 060: aload 4
		// 062: invokevirtual net/minecraft/util/math/BlockPos$PooledMutable.getX ()I
		// 065: if_icmpgt 0ef
		// 068: aload 2
		// 069: invokevirtual net/minecraft/util/math/BlockPos$PooledMutable.getY ()I
		// 06c: istore 9
		// 06e: iload 9
		// 070: aload 4
		// 072: invokevirtual net/minecraft/util/math/BlockPos$PooledMutable.getY ()I
		// 075: if_icmpgt 0e9
		// 078: aload 2
		// 079: invokevirtual net/minecraft/util/math/BlockPos$PooledMutable.getZ ()I
		// 07c: istore 10
		// 07e: iload 10
		// 080: aload 4
		// 082: invokevirtual net/minecraft/util/math/BlockPos$PooledMutable.getZ ()I
		// 085: if_icmpgt 0e3
		// 088: aload 6
		// 08a: iload 8
		// 08c: iload 9
		// 08e: iload 10
		// 090: invokevirtual net/minecraft/util/math/BlockPos$PooledMutable.set (III)Lnet/minecraft/util/math/BlockPos$PooledMutable;
		// 093: pop
		// 094: aload 0
		// 095: getfield net/minecraft/entity/Entity.world Lnet/minecraft/world/World;
		// 098: aload 6
		// 09a: invokevirtual net/minecraft/world/World.getBlockState (Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;
		// 09d: astore 11
		// 09f: aload 11
		// 0a1: aload 0
		// 0a2: getfield net/minecraft/entity/Entity.world Lnet/minecraft/world/World;
		// 0a5: aload 6
		// 0a7: aload 0
		// 0a8: invokevirtual net/minecraft/block/BlockState.onEntityCollision (Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;)V
		// 0ab: aload 0
		// 0ac: aload 11
		// 0ae: invokevirtual net/minecraft/entity/Entity.onBlockCollision (Lnet/minecraft/block/BlockState;)V
		// 0b1: goto 0dd
		// 0b4: astore 12
		// 0b6: aload 12
		// 0b8: ldc_w "Colliding entity with block"
		// 0bb: invokestatic net/minecraft/util/crash/CrashReport.create (Ljava/lang/Throwable;Ljava/lang/String;)Lnet/minecraft/util/crash/CrashReport;
		// 0be: astore 13
		// 0c0: aload 13
		// 0c2: ldc_w "Block being collided with"
		// 0c5: invokevirtual net/minecraft/util/crash/CrashReport.addElement (Ljava/lang/String;)Lnet/minecraft/util/crash/CrashReportSection;
		// 0c8: astore 14
		// 0ca: aload 14
		// 0cc: aload 6
		// 0ce: aload 11
		// 0d0: invokestatic net/minecraft/util/crash/CrashReportSection.addBlockInfo (Lnet/minecraft/util/crash/CrashReportSection;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V
		// 0d3: new net/minecraft/util/crash/CrashException
		// 0d6: dup
		// 0d7: aload 13
		// 0d9: invokespecial net/minecraft/util/crash/CrashException.<init> (Lnet/minecraft/util/crash/CrashReport;)V
		// 0dc: athrow
		// 0dd: iinc 10 1
		// 0e0: goto 07e
		// 0e3: iinc 9 1
		// 0e6: goto 06e
		// 0e9: iinc 8 1
		// 0ec: goto 05e
		// 0ef: aload 6
		// 0f1: ifnull 146
		// 0f4: aload 7
		// 0f6: ifnull 10d
		// 0f9: aload 6
		// 0fb: invokevirtual net/minecraft/util/math/BlockPos$PooledMutable.close ()V
		// 0fe: goto 146
		// 101: astore 8
		// 103: aload 7
		// 105: aload 8
		// 107: invokevirtual java/lang/Throwable.addSuppressed (Ljava/lang/Throwable;)V
		// 10a: goto 146
		// 10d: aload 6
		// 10f: invokevirtual net/minecraft/util/math/BlockPos$PooledMutable.close ()V
		// 112: goto 146
		// 115: astore 8
		// 117: aload 8
		// 119: astore 7
		// 11b: aload 8
		// 11d: athrow
		// 11e: astore 15
		// 120: aload 6
		// 122: ifnull 143
		// 125: aload 7
		// 127: ifnull 13e
		// 12a: aload 6
		// 12c: invokevirtual net/minecraft/util/math/BlockPos$PooledMutable.close ()V
		// 12f: goto 143
		// 132: astore 16
		// 134: aload 7
		// 136: aload 16
		// 138: invokevirtual java/lang/Throwable.addSuppressed (Ljava/lang/Throwable;)V
		// 13b: goto 143
		// 13e: aload 6
		// 140: invokevirtual net/minecraft/util/math/BlockPos$PooledMutable.close ()V
		// 143: aload 15
		// 145: athrow
		// 146: aload 4
		// 148: ifnull 19d
		// 14b: aload 5
		// 14d: ifnull 164
		// 150: aload 4
		// 152: invokevirtual net/minecraft/util/math/BlockPos$PooledMutable.close ()V
		// 155: goto 19d
		// 158: astore 6
		// 15a: aload 5
		// 15c: aload 6
		// 15e: invokevirtual java/lang/Throwable.addSuppressed (Ljava/lang/Throwable;)V
		// 161: goto 19d
		// 164: aload 4
		// 166: invokevirtual net/minecraft/util/math/BlockPos$PooledMutable.close ()V
		// 169: goto 19d
		// 16c: astore 6
		// 16e: aload 6
		// 170: astore 5
		// 172: aload 6
		// 174: athrow
		// 175: astore 17
		// 177: aload 4
		// 179: ifnull 19a
		// 17c: aload 5
		// 17e: ifnull 195
		// 181: aload 4
		// 183: invokevirtual net/minecraft/util/math/BlockPos$PooledMutable.close ()V
		// 186: goto 19a
		// 189: astore 18
		// 18b: aload 5
		// 18d: aload 18
		// 18f: invokevirtual java/lang/Throwable.addSuppressed (Ljava/lang/Throwable;)V
		// 192: goto 19a
		// 195: aload 4
		// 197: invokevirtual net/minecraft/util/math/BlockPos$PooledMutable.close ()V
		// 19a: aload 17
		// 19c: athrow
		// 19d: aload 2
		// 19e: ifnull 1e9
		// 1a1: aload 3
		// 1a2: ifnull 1b7
		// 1a5: aload 2
		// 1a6: invokevirtual net/minecraft/util/math/BlockPos$PooledMutable.close ()V
		// 1a9: goto 1e9
		// 1ac: astore 4
		// 1ae: aload 3
		// 1af: aload 4
		// 1b1: invokevirtual java/lang/Throwable.addSuppressed (Ljava/lang/Throwable;)V
		// 1b4: goto 1e9
		// 1b7: aload 2
		// 1b8: invokevirtual net/minecraft/util/math/BlockPos$PooledMutable.close ()V
		// 1bb: goto 1e9
		// 1be: astore 4
		// 1c0: aload 4
		// 1c2: astore 3
		// 1c3: aload 4
		// 1c5: athrow
		// 1c6: astore 19
		// 1c8: aload 2
		// 1c9: ifnull 1e6
		// 1cc: aload 3
		// 1cd: ifnull 1e2
		// 1d0: aload 2
		// 1d1: invokevirtual net/minecraft/util/math/BlockPos$PooledMutable.close ()V
		// 1d4: goto 1e6
		// 1d7: astore 20
		// 1d9: aload 3
		// 1da: aload 20
		// 1dc: invokevirtual java/lang/Throwable.addSuppressed (Ljava/lang/Throwable;)V
		// 1df: goto 1e6
		// 1e2: aload 2
		// 1e3: invokevirtual net/minecraft/util/math/BlockPos$PooledMutable.close ()V
		// 1e6: aload 19
		// 1e8: athrow
		// 1e9: return
	}

	protected void onBlockCollision(BlockState blockState) {
	}

	protected void playStepSound(BlockPos blockPos, BlockState blockState) {
		if (!blockState.getMaterial().isLiquid()) {
			BlockState blockState2 = this.world.getBlockState(blockPos.up());
			BlockSoundGroup blockSoundGroup = blockState2.getBlock() == Blocks.field_10477 ? blockState2.getSoundGroup() : blockState.getSoundGroup();
			this.playSound(blockSoundGroup.getStepSound(), blockSoundGroup.getVolume() * 0.15F, blockSoundGroup.getPitch());
		}
	}

	protected void playSwimSound(float f) {
		this.playSound(this.getSwimSound(), f, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
	}

	protected float playFlySound(float f) {
		return 0.0F;
	}

	protected boolean hasWings() {
		return false;
	}

	public void playSound(SoundEvent soundEvent, float f, float g) {
		if (!this.isSilent()) {
			this.world.playSound(null, this.getX(), this.getY(), this.getZ(), soundEvent, this.getSoundCategory(), f, g);
		}
	}

	public boolean isSilent() {
		return this.dataTracker.get(SILENT);
	}

	public void setSilent(boolean bl) {
		this.dataTracker.set(SILENT, bl);
	}

	public boolean hasNoGravity() {
		return this.dataTracker.get(NO_GRAVITY);
	}

	public void setNoGravity(boolean bl) {
		this.dataTracker.set(NO_GRAVITY, bl);
	}

	protected boolean canClimb() {
		return true;
	}

	protected void fall(double d, boolean bl, BlockState blockState, BlockPos blockPos) {
		if (bl) {
			if (this.fallDistance > 0.0F) {
				blockState.getBlock().onLandedUpon(this.world, blockPos, this, this.fallDistance);
			}

			this.fallDistance = 0.0F;
		} else if (d < 0.0) {
			this.fallDistance = (float)((double)this.fallDistance - d);
		}
	}

	@Nullable
	public Box getCollisionBox() {
		return null;
	}

	protected void burn(int i) {
		if (!this.isFireImmune()) {
			this.damage(DamageSource.IN_FIRE, (float)i);
		}
	}

	public final boolean isFireImmune() {
		return this.getType().isFireImmune();
	}

	public boolean handleFallDamage(float f, float g) {
		if (this.hasPassengers()) {
			for (Entity entity : this.getPassengerList()) {
				entity.handleFallDamage(f, g);
			}
		}

		return false;
	}

	public boolean isTouchingWater() {
		return this.touchingWater;
	}

	private boolean isBeingRainedOn() {
		boolean var3;
		try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.getEntityPos(this)) {
			var3 = this.world.hasRain(pooledMutable) || this.world.hasRain(pooledMutable.set(this.getX(), this.getY() + (double)this.dimensions.height, this.getZ()));
		}

		return var3;
	}

	private boolean isInsideBubbleColumn() {
		return this.world.getBlockState(new BlockPos(this)).getBlock() == Blocks.field_10422;
	}

	public boolean isTouchingWaterOrRain() {
		return this.isTouchingWater() || this.isBeingRainedOn();
	}

	public boolean isWet() {
		return this.isTouchingWater() || this.isBeingRainedOn() || this.isInsideBubbleColumn();
	}

	public boolean isInsideWaterOrBubbleColumn() {
		return this.isTouchingWater() || this.isInsideBubbleColumn();
	}

	public boolean isSubmergedInWater() {
		return this.submergedInWater && this.isTouchingWater();
	}

	private void updateWaterState() {
		this.checkWaterState();
		this.updateSubmergedInWaterState();
		this.updateSwimming();
	}

	public void updateSwimming() {
		if (this.isSwimming()) {
			this.setSwimming(this.isSprinting() && this.isTouchingWater() && !this.hasVehicle());
		} else {
			this.setSwimming(this.isSprinting() && this.isSubmergedInWater() && !this.hasVehicle());
		}
	}

	public boolean checkWaterState() {
		if (this.getVehicle() instanceof BoatEntity) {
			this.touchingWater = false;
		} else if (this.updateMovementInFluid(FluidTags.field_15517)) {
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

	private void updateSubmergedInWaterState() {
		this.submergedInWater = this.isSubmergedIn(FluidTags.field_15517, true);
	}

	protected void onSwimmingStart() {
		Entity entity = this.hasPassengers() && this.getPrimaryPassenger() != null ? this.getPrimaryPassenger() : this;
		float f = entity == this ? 0.2F : 0.9F;
		Vec3d vec3d = entity.getVelocity();
		float g = MathHelper.sqrt(vec3d.x * vec3d.x * 0.2F + vec3d.y * vec3d.y + vec3d.z * vec3d.z * 0.2F) * f;
		if (g > 1.0F) {
			g = 1.0F;
		}

		if ((double)g < 0.25) {
			this.playSound(this.getSplashSound(), g, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
		} else {
			this.playSound(this.getHighSpeedSplashSound(), g, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
		}

		float h = (float)MathHelper.floor(this.getY());

		for (int i = 0; (float)i < 1.0F + this.dimensions.width * 20.0F; i++) {
			float j = (this.random.nextFloat() * 2.0F - 1.0F) * this.dimensions.width;
			float k = (this.random.nextFloat() * 2.0F - 1.0F) * this.dimensions.width;
			this.world
				.addParticle(
					ParticleTypes.field_11247,
					this.getX() + (double)j,
					(double)(h + 1.0F),
					this.getZ() + (double)k,
					vec3d.x,
					vec3d.y - (double)(this.random.nextFloat() * 0.2F),
					vec3d.z
				);
		}

		for (int l = 0; (float)l < 1.0F + this.dimensions.width * 20.0F; l++) {
			float m = (this.random.nextFloat() * 2.0F - 1.0F) * this.dimensions.width;
			float n = (this.random.nextFloat() * 2.0F - 1.0F) * this.dimensions.width;
			this.world.addParticle(ParticleTypes.field_11202, this.getX() + (double)m, (double)(h + 1.0F), this.getZ() + (double)n, vec3d.x, vec3d.y, vec3d.z);
		}
	}

	public void attemptSprintingParticles() {
		if (this.isSprinting() && !this.isTouchingWater()) {
			this.spawnSprintingParticles();
		}
	}

	protected void spawnSprintingParticles() {
		int i = MathHelper.floor(this.getX());
		int j = MathHelper.floor(this.getY() - 0.2F);
		int k = MathHelper.floor(this.getZ());
		BlockPos blockPos = new BlockPos(i, j, k);
		BlockState blockState = this.world.getBlockState(blockPos);
		if (blockState.getRenderType() != BlockRenderType.field_11455) {
			Vec3d vec3d = this.getVelocity();
			this.world
				.addParticle(
					new BlockStateParticleEffect(ParticleTypes.field_11217, blockState),
					this.getX() + ((double)this.random.nextFloat() - 0.5) * (double)this.dimensions.width,
					this.getY() + 0.1,
					this.getZ() + ((double)this.random.nextFloat() - 0.5) * (double)this.dimensions.width,
					vec3d.x * -4.0,
					1.5,
					vec3d.z * -4.0
				);
		}
	}

	public boolean isInFluid(Tag<Fluid> tag) {
		return this.isSubmergedIn(tag, false);
	}

	public boolean isSubmergedIn(Tag<Fluid> tag, boolean bl) {
		if (this.getVehicle() instanceof BoatEntity) {
			return false;
		} else {
			double d = this.getEyeY();
			BlockPos blockPos = new BlockPos(this.getX(), d, this.getZ());
			if (bl && !this.world.isChunkLoaded(blockPos.getX() >> 4, blockPos.getZ() >> 4)) {
				return false;
			} else {
				FluidState fluidState = this.world.getFluidState(blockPos);
				return fluidState.matches(tag) && d < (double)((float)blockPos.getY() + fluidState.getHeight(this.world, blockPos) + 0.11111111F);
			}
		}
	}

	public void setInLava() {
		this.inLava = true;
	}

	public boolean isInLava() {
		return this.inLava;
	}

	public void updateVelocity(float f, Vec3d vec3d) {
		Vec3d vec3d2 = movementInputToVelocity(vec3d, f, this.yaw);
		this.setVelocity(this.getVelocity().add(vec3d2));
	}

	private static Vec3d movementInputToVelocity(Vec3d vec3d, float f, float g) {
		double d = vec3d.lengthSquared();
		if (d < 1.0E-7) {
			return Vec3d.ZERO;
		} else {
			Vec3d vec3d2 = (d > 1.0 ? vec3d.normalize() : vec3d).multiply((double)f);
			float h = MathHelper.sin(g * (float) (Math.PI / 180.0));
			float i = MathHelper.cos(g * (float) (Math.PI / 180.0));
			return new Vec3d(vec3d2.x * (double)i - vec3d2.z * (double)h, vec3d2.y, vec3d2.z * (double)i + vec3d2.x * (double)h);
		}
	}

	public float getBrightnessAtEyes() {
		BlockPos.Mutable mutable = new BlockPos.Mutable(this.getX(), 0.0, this.getZ());
		if (this.world.isChunkLoaded(mutable)) {
			mutable.setY(MathHelper.floor(this.getEyeY()));
			return this.world.getBrightness(mutable);
		} else {
			return 0.0F;
		}
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public void updatePositionAndAngles(double d, double e, double f, float g, float h) {
		double i = MathHelper.clamp(d, -3.0E7, 3.0E7);
		double j = MathHelper.clamp(f, -3.0E7, 3.0E7);
		this.prevX = i;
		this.prevY = e;
		this.prevZ = j;
		this.updatePosition(i, e, j);
		this.yaw = g % 360.0F;
		this.pitch = MathHelper.clamp(h, -90.0F, 90.0F) % 360.0F;
		this.prevYaw = this.yaw;
		this.prevPitch = this.pitch;
	}

	public void refreshPositionAndAngles(BlockPos blockPos, float f, float g) {
		this.refreshPositionAndAngles((double)blockPos.getX() + 0.5, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5, f, g);
	}

	public void refreshPositionAndAngles(double d, double e, double f, float g, float h) {
		this.resetPosition(d, e, f);
		this.yaw = g;
		this.pitch = h;
		this.refreshPosition();
	}

	public void resetPosition(double d, double e, double f) {
		this.setPos(d, e, f);
		this.prevX = d;
		this.prevY = e;
		this.prevZ = f;
		this.lastRenderX = d;
		this.lastRenderY = e;
		this.lastRenderZ = f;
	}

	public float distanceTo(Entity entity) {
		float f = (float)(this.getX() - entity.getX());
		float g = (float)(this.getY() - entity.getY());
		float h = (float)(this.getZ() - entity.getZ());
		return MathHelper.sqrt(f * f + g * g + h * h);
	}

	public double squaredDistanceTo(double d, double e, double f) {
		double g = this.getX() - d;
		double h = this.getY() - e;
		double i = this.getZ() - f;
		return g * g + h * h + i * i;
	}

	public double squaredDistanceTo(Entity entity) {
		return this.squaredDistanceTo(entity.getPos());
	}

	public double squaredDistanceTo(Vec3d vec3d) {
		double d = this.getX() - vec3d.x;
		double e = this.getY() - vec3d.y;
		double f = this.getZ() - vec3d.z;
		return d * d + e * e + f * f;
	}

	public void onPlayerCollision(PlayerEntity playerEntity) {
	}

	public void pushAwayFrom(Entity entity) {
		if (!this.isConnectedThroughVehicle(entity)) {
			if (!entity.noClip && !this.noClip) {
				double d = entity.getX() - this.getX();
				double e = entity.getZ() - this.getZ();
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

	public void addVelocity(double d, double e, double f) {
		this.setVelocity(this.getVelocity().add(d, e, f));
		this.velocityDirty = true;
	}

	protected void scheduleVelocityUpdate() {
		this.velocityModified = true;
	}

	public boolean damage(DamageSource damageSource, float f) {
		if (this.isInvulnerableTo(damageSource)) {
			return false;
		} else {
			this.scheduleVelocityUpdate();
			return false;
		}
	}

	public final Vec3d getRotationVec(float f) {
		return this.getRotationVector(this.getPitch(f), this.getYaw(f));
	}

	public float getPitch(float f) {
		return f == 1.0F ? this.pitch : MathHelper.lerp(f, this.prevPitch, this.pitch);
	}

	public float getYaw(float f) {
		return f == 1.0F ? this.yaw : MathHelper.lerp(f, this.prevYaw, this.yaw);
	}

	protected final Vec3d getRotationVector(float f, float g) {
		float h = f * (float) (Math.PI / 180.0);
		float i = -g * (float) (Math.PI / 180.0);
		float j = MathHelper.cos(i);
		float k = MathHelper.sin(i);
		float l = MathHelper.cos(h);
		float m = MathHelper.sin(h);
		return new Vec3d((double)(k * l), (double)(-m), (double)(j * l));
	}

	public final Vec3d getOppositeRotationVector(float f) {
		return this.getOppositeRotationVector(this.getPitch(f), this.getYaw(f));
	}

	protected final Vec3d getOppositeRotationVector(float f, float g) {
		return this.getRotationVector(f - 90.0F, g);
	}

	public final Vec3d getCameraPosVec(float f) {
		if (f == 1.0F) {
			return new Vec3d(this.getX(), this.getEyeY(), this.getZ());
		} else {
			double d = MathHelper.lerp((double)f, this.prevX, this.getX());
			double e = MathHelper.lerp((double)f, this.prevY, this.getY()) + (double)this.getStandingEyeHeight();
			double g = MathHelper.lerp((double)f, this.prevZ, this.getZ());
			return new Vec3d(d, e, g);
		}
	}

	public HitResult rayTrace(double d, float f, boolean bl) {
		Vec3d vec3d = this.getCameraPosVec(f);
		Vec3d vec3d2 = this.getRotationVec(f);
		Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
		return this.world
			.rayTrace(
				new RayTraceContext(
					vec3d, vec3d3, RayTraceContext.ShapeType.field_17559, bl ? RayTraceContext.FluidHandling.field_1347 : RayTraceContext.FluidHandling.field_1348, this
				)
			);
	}

	public boolean collides() {
		return false;
	}

	public boolean isPushable() {
		return false;
	}

	public void updateKilledAdvancementCriterion(Entity entity, int i, DamageSource damageSource) {
		if (entity instanceof ServerPlayerEntity) {
			Criterions.ENTITY_KILLED_PLAYER.trigger((ServerPlayerEntity)entity, this, damageSource);
		}
	}

	public boolean shouldRender(double d, double e, double f) {
		double g = this.getX() - d;
		double h = this.getY() - e;
		double i = this.getZ() - f;
		double j = g * g + h * h + i * i;
		return this.shouldRender(j);
	}

	public boolean shouldRender(double d) {
		double e = this.getBoundingBox().getAverageSideLength();
		if (Double.isNaN(e)) {
			e = 1.0;
		}

		e *= 64.0 * renderDistanceMultiplier;
		return d < e * e;
	}

	public boolean saveSelfToTag(CompoundTag compoundTag) {
		String string = this.getSavedEntityId();
		if (!this.removed && string != null) {
			compoundTag.putString("id", string);
			this.toTag(compoundTag);
			return true;
		} else {
			return false;
		}
	}

	public boolean saveToTag(CompoundTag compoundTag) {
		return this.hasVehicle() ? false : this.saveSelfToTag(compoundTag);
	}

	public CompoundTag toTag(CompoundTag compoundTag) {
		try {
			compoundTag.put("Pos", this.toListTag(this.getX(), this.getY(), this.getZ()));
			Vec3d vec3d = this.getVelocity();
			compoundTag.put("Motion", this.toListTag(vec3d.x, vec3d.y, vec3d.z));
			compoundTag.put("Rotation", this.toListTag(this.yaw, this.pitch));
			compoundTag.putFloat("FallDistance", this.fallDistance);
			compoundTag.putShort("Fire", (short)this.fireTicks);
			compoundTag.putShort("Air", (short)this.getAir());
			compoundTag.putBoolean("OnGround", this.onGround);
			compoundTag.putInt("Dimension", this.dimension.getRawId());
			compoundTag.putBoolean("Invulnerable", this.invulnerable);
			compoundTag.putInt("PortalCooldown", this.netherPortalCooldown);
			compoundTag.putUuid("UUID", this.getUuid());
			Text text = this.getCustomName();
			if (text != null) {
				compoundTag.putString("CustomName", Text.Serializer.toJson(text));
			}

			if (this.isCustomNameVisible()) {
				compoundTag.putBoolean("CustomNameVisible", this.isCustomNameVisible());
			}

			if (this.isSilent()) {
				compoundTag.putBoolean("Silent", this.isSilent());
			}

			if (this.hasNoGravity()) {
				compoundTag.putBoolean("NoGravity", this.hasNoGravity());
			}

			if (this.glowing) {
				compoundTag.putBoolean("Glowing", this.glowing);
			}

			if (!this.scoreboardTags.isEmpty()) {
				ListTag listTag = new ListTag();

				for (String string : this.scoreboardTags) {
					listTag.add(StringTag.of(string));
				}

				compoundTag.put("Tags", listTag);
			}

			this.writeCustomDataToTag(compoundTag);
			if (this.hasPassengers()) {
				ListTag listTag2 = new ListTag();

				for (Entity entity : this.getPassengerList()) {
					CompoundTag compoundTag2 = new CompoundTag();
					if (entity.saveSelfToTag(compoundTag2)) {
						listTag2.add(compoundTag2);
					}
				}

				if (!listTag2.isEmpty()) {
					compoundTag.put("Passengers", listTag2);
				}
			}

			return compoundTag;
		} catch (Throwable var8) {
			CrashReport crashReport = CrashReport.create(var8, "Saving entity NBT");
			CrashReportSection crashReportSection = crashReport.addElement("Entity being saved");
			this.populateCrashReport(crashReportSection);
			throw new CrashException(crashReport);
		}
	}

	public void fromTag(CompoundTag compoundTag) {
		try {
			ListTag listTag = compoundTag.getList("Pos", 6);
			ListTag listTag2 = compoundTag.getList("Motion", 6);
			ListTag listTag3 = compoundTag.getList("Rotation", 5);
			double d = listTag2.getDouble(0);
			double e = listTag2.getDouble(1);
			double f = listTag2.getDouble(2);
			this.setVelocity(Math.abs(d) > 10.0 ? 0.0 : d, Math.abs(e) > 10.0 ? 0.0 : e, Math.abs(f) > 10.0 ? 0.0 : f);
			this.resetPosition(listTag.getDouble(0), listTag.getDouble(1), listTag.getDouble(2));
			this.yaw = listTag3.getFloat(0);
			this.pitch = listTag3.getFloat(1);
			this.prevYaw = this.yaw;
			this.prevPitch = this.pitch;
			this.setHeadYaw(this.yaw);
			this.setYaw(this.yaw);
			this.fallDistance = compoundTag.getFloat("FallDistance");
			this.fireTicks = compoundTag.getShort("Fire");
			this.setAir(compoundTag.getShort("Air"));
			this.onGround = compoundTag.getBoolean("OnGround");
			if (compoundTag.contains("Dimension")) {
				this.dimension = DimensionType.byRawId(compoundTag.getInt("Dimension"));
			}

			this.invulnerable = compoundTag.getBoolean("Invulnerable");
			this.netherPortalCooldown = compoundTag.getInt("PortalCooldown");
			if (compoundTag.containsUuid("UUID")) {
				this.uuid = compoundTag.getUuid("UUID");
				this.uuidString = this.uuid.toString();
			}

			if (!Double.isFinite(this.getX()) || !Double.isFinite(this.getY()) || !Double.isFinite(this.getZ())) {
				throw new IllegalStateException("Entity has invalid position");
			} else if (Double.isFinite((double)this.yaw) && Double.isFinite((double)this.pitch)) {
				this.refreshPosition();
				this.setRotation(this.yaw, this.pitch);
				if (compoundTag.contains("CustomName", 8)) {
					this.setCustomName(Text.Serializer.fromJson(compoundTag.getString("CustomName")));
				}

				this.setCustomNameVisible(compoundTag.getBoolean("CustomNameVisible"));
				this.setSilent(compoundTag.getBoolean("Silent"));
				this.setNoGravity(compoundTag.getBoolean("NoGravity"));
				this.setGlowing(compoundTag.getBoolean("Glowing"));
				if (compoundTag.contains("Tags", 9)) {
					this.scoreboardTags.clear();
					ListTag listTag4 = compoundTag.getList("Tags", 8);
					int i = Math.min(listTag4.size(), 1024);

					for (int j = 0; j < i; j++) {
						this.scoreboardTags.add(listTag4.getString(j));
					}
				}

				this.readCustomDataFromTag(compoundTag);
				if (this.shouldSetPositionOnLoad()) {
					this.refreshPosition();
				}
			} else {
				throw new IllegalStateException("Entity has invalid rotation");
			}
		} catch (Throwable var14) {
			CrashReport crashReport = CrashReport.create(var14, "Loading entity NBT");
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
		EntityType<?> entityType = this.getType();
		Identifier identifier = EntityType.getId(entityType);
		return entityType.isSaveable() && identifier != null ? identifier.toString() : null;
	}

	protected abstract void readCustomDataFromTag(CompoundTag compoundTag);

	protected abstract void writeCustomDataToTag(CompoundTag compoundTag);

	protected ListTag toListTag(double... ds) {
		ListTag listTag = new ListTag();

		for (double d : ds) {
			listTag.add(DoubleTag.of(d));
		}

		return listTag;
	}

	protected ListTag toListTag(float... fs) {
		ListTag listTag = new ListTag();

		for (float f : fs) {
			listTag.add(FloatTag.of(f));
		}

		return listTag;
	}

	@Nullable
	public ItemEntity dropItem(ItemConvertible itemConvertible) {
		return this.dropItem(itemConvertible, 0);
	}

	@Nullable
	public ItemEntity dropItem(ItemConvertible itemConvertible, int i) {
		return this.dropStack(new ItemStack(itemConvertible), (float)i);
	}

	@Nullable
	public ItemEntity dropStack(ItemStack itemStack) {
		return this.dropStack(itemStack, 0.0F);
	}

	@Nullable
	public ItemEntity dropStack(ItemStack itemStack, float f) {
		if (itemStack.isEmpty()) {
			return null;
		} else if (this.world.isClient) {
			return null;
		} else {
			ItemEntity itemEntity = new ItemEntity(this.world, this.getX(), this.getY() + (double)f, this.getZ(), itemStack);
			itemEntity.setToDefaultPickupDelay();
			this.world.spawnEntity(itemEntity);
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
			try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
				for (int i = 0; i < 8; i++) {
					int j = MathHelper.floor(this.getY() + (double)(((float)((i >> 0) % 2) - 0.5F) * 0.1F) + (double)this.standingEyeHeight);
					int k = MathHelper.floor(this.getX() + (double)(((float)((i >> 1) % 2) - 0.5F) * this.dimensions.width * 0.8F));
					int l = MathHelper.floor(this.getZ() + (double)(((float)((i >> 2) % 2) - 0.5F) * this.dimensions.width * 0.8F));
					if (pooledMutable.getX() != k || pooledMutable.getY() != j || pooledMutable.getZ() != l) {
						pooledMutable.set(k, j, l);
						if (this.world.getBlockState(pooledMutable).canSuffocate(this.world, pooledMutable)) {
							return true;
						}
					}
				}

				return false;
			}
		}
	}

	public boolean interact(PlayerEntity playerEntity, Hand hand) {
		return false;
	}

	@Nullable
	public Box getHardCollisionBox(Entity entity) {
		return null;
	}

	public void tickRiding() {
		this.setVelocity(Vec3d.ZERO);
		this.tick();
		if (this.hasVehicle()) {
			this.getVehicle().updatePassengerPosition(this);
		}
	}

	public void updatePassengerPosition(Entity entity) {
		this.updatePassengerPosition(entity, Entity::updatePosition);
	}

	public void updatePassengerPosition(Entity entity, Entity.PositionUpdater positionUpdater) {
		if (this.hasPassenger(entity)) {
			positionUpdater.accept(entity, this.getX(), this.getY() + this.getMountedHeightOffset() + entity.getHeightOffset(), this.getZ());
		}
	}

	public void onPassengerLookAround(Entity entity) {
	}

	public double getHeightOffset() {
		return 0.0;
	}

	public double getMountedHeightOffset() {
		return (double)this.dimensions.height * 0.75;
	}

	public boolean startRiding(Entity entity) {
		return this.startRiding(entity, false);
	}

	public boolean isLiving() {
		return this instanceof LivingEntity;
	}

	public boolean startRiding(Entity entity, boolean bl) {
		for (Entity entity2 = entity; entity2.vehicle != null; entity2 = entity2.vehicle) {
			if (entity2.vehicle == this) {
				return false;
			}
		}

		if (bl || this.canStartRiding(entity) && entity.canAddPassenger(this)) {
			if (this.hasVehicle()) {
				this.stopRiding();
			}

			this.vehicle = entity;
			this.vehicle.addPassenger(this);
			return true;
		} else {
			return false;
		}
	}

	protected boolean canStartRiding(Entity entity) {
		return this.ridingCooldown <= 0;
	}

	protected boolean wouldPoseNotCollide(EntityPose entityPose) {
		return this.world.doesNotCollide(this, this.calculateBoundsForPose(entityPose));
	}

	public void removeAllPassengers() {
		for (int i = this.passengerList.size() - 1; i >= 0; i--) {
			((Entity)this.passengerList.get(i)).stopRiding();
		}
	}

	public void stopRiding() {
		if (this.vehicle != null) {
			Entity entity = this.vehicle;
			this.vehicle = null;
			entity.removePassenger(this);
		}
	}

	protected void addPassenger(Entity entity) {
		if (entity.getVehicle() != this) {
			throw new IllegalStateException("Use x.startRiding(y), not y.addPassenger(x)");
		} else {
			if (!this.world.isClient && entity instanceof PlayerEntity && !(this.getPrimaryPassenger() instanceof PlayerEntity)) {
				this.passengerList.add(0, entity);
			} else {
				this.passengerList.add(entity);
			}
		}
	}

	protected void removePassenger(Entity entity) {
		if (entity.getVehicle() == this) {
			throw new IllegalStateException("Use x.stopRiding(y), not y.removePassenger(x)");
		} else {
			this.passengerList.remove(entity);
			entity.ridingCooldown = 60;
		}
	}

	protected boolean canAddPassenger(Entity entity) {
		return this.getPassengerList().size() < 1;
	}

	public void updateTrackedPositionAndAngles(double d, double e, double f, float g, float h, int i, boolean bl) {
		this.updatePosition(d, e, f);
		this.setRotation(g, h);
	}

	public void updateTrackedHeadRotation(float f, int i) {
		this.setHeadYaw(f);
	}

	public float getTargetingMargin() {
		return 0.0F;
	}

	public Vec3d getRotationVector() {
		return this.getRotationVector(this.pitch, this.yaw);
	}

	public Vec2f getRotationClient() {
		return new Vec2f(this.pitch, this.yaw);
	}

	public Vec3d getRotationVecClient() {
		return Vec3d.fromPolar(this.getRotationClient());
	}

	public void setInNetherPortal(BlockPos blockPos) {
		if (this.netherPortalCooldown > 0) {
			this.netherPortalCooldown = this.getDefaultNetherPortalCooldown();
		} else {
			if (!this.world.isClient && !blockPos.equals(this.lastNetherPortalPosition)) {
				this.lastNetherPortalPosition = new BlockPos(blockPos);
				BlockPattern.Result result = NetherPortalBlock.findPortal(this.world, this.lastNetherPortalPosition);
				double d = result.getForwards().getAxis() == Direction.Axis.field_11048 ? (double)result.getFrontTopLeft().getZ() : (double)result.getFrontTopLeft().getX();
				double e = Math.abs(
					MathHelper.minusDiv(
						(result.getForwards().getAxis() == Direction.Axis.field_11048 ? this.getZ() : this.getX())
							- (double)(result.getForwards().rotateYClockwise().getDirection() == Direction.AxisDirection.field_11060 ? 1 : 0),
						d,
						d - (double)result.getWidth()
					)
				);
				double f = MathHelper.minusDiv(this.getY() - 1.0, (double)result.getFrontTopLeft().getY(), (double)(result.getFrontTopLeft().getY() - result.getHeight()));
				this.lastNetherPortalDirectionVector = new Vec3d(e, f, 0.0);
				this.lastNetherPortalDirection = result.getForwards();
			}

			this.inNetherPortal = true;
		}
	}

	protected void tickNetherPortal() {
		if (this.world instanceof ServerWorld) {
			int i = this.getMaxNetherPortalTime();
			if (this.inNetherPortal) {
				if (this.world.getServer().isNetherAllowed() && !this.hasVehicle() && this.netherPortalTime++ >= i) {
					this.world.getProfiler().push("portal");
					this.netherPortalTime = i;
					this.netherPortalCooldown = this.getDefaultNetherPortalCooldown();
					this.changeDimension(this.world.dimension.getType() == DimensionType.field_13076 ? DimensionType.field_13072 : DimensionType.field_13076);
					this.world.getProfiler().pop();
				}

				this.inNetherPortal = false;
			} else {
				if (this.netherPortalTime > 0) {
					this.netherPortalTime -= 4;
				}

				if (this.netherPortalTime < 0) {
					this.netherPortalTime = 0;
				}
			}

			this.tickNetherPortalCooldown();
		}
	}

	public int getDefaultNetherPortalCooldown() {
		return 300;
	}

	public void setVelocityClient(double d, double e, double f) {
		this.setVelocity(d, e, f);
	}

	public void handleStatus(byte b) {
		switch (b) {
			case 53:
				HoneyBlock.addRegularParticles(this);
		}
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

	public void equipStack(EquipmentSlot equipmentSlot, ItemStack itemStack) {
	}

	public boolean isOnFire() {
		boolean bl = this.world != null && this.world.isClient;
		return !this.isFireImmune() && (this.fireTicks > 0 || bl && this.getFlag(0));
	}

	public boolean hasVehicle() {
		return this.getVehicle() != null;
	}

	public boolean hasPassengers() {
		return !this.getPassengerList().isEmpty();
	}

	public boolean canBeRiddenInWater() {
		return true;
	}

	public void setSneaking(boolean bl) {
		this.setFlag(1, bl);
	}

	public boolean isSneaking() {
		return this.getFlag(1);
	}

	public boolean bypassesSteppingEffects() {
		return this.isSneaking();
	}

	public boolean bypassesLandingEffects() {
		return this.isSneaking();
	}

	public boolean isSneaky() {
		return this.isSneaking();
	}

	public boolean isDescending() {
		return this.isSneaking();
	}

	public boolean isInSneakingPose() {
		return this.getPose() == EntityPose.field_18081;
	}

	public boolean isSprinting() {
		return this.getFlag(3);
	}

	public void setSprinting(boolean bl) {
		this.setFlag(3, bl);
	}

	public boolean isSwimming() {
		return this.getFlag(4);
	}

	public boolean isInSwimmingPose() {
		return this.getPose() == EntityPose.field_18079;
	}

	public boolean shouldLeaveSwimmingPose() {
		return this.isInSwimmingPose() && !this.isTouchingWater();
	}

	public void setSwimming(boolean bl) {
		this.setFlag(4, bl);
	}

	public boolean isGlowing() {
		return this.glowing || this.world.isClient && this.getFlag(6);
	}

	public void setGlowing(boolean bl) {
		this.glowing = bl;
		if (!this.world.isClient) {
			this.setFlag(6, this.glowing);
		}
	}

	public boolean isInvisible() {
		return this.getFlag(5);
	}

	public boolean isInvisibleTo(PlayerEntity playerEntity) {
		if (playerEntity.isSpectator()) {
			return false;
		} else {
			AbstractTeam abstractTeam = this.getScoreboardTeam();
			return abstractTeam != null && playerEntity != null && playerEntity.getScoreboardTeam() == abstractTeam && abstractTeam.shouldShowFriendlyInvisibles()
				? false
				: this.isInvisible();
		}
	}

	@Nullable
	public AbstractTeam getScoreboardTeam() {
		return this.world.getScoreboard().getPlayerTeam(this.getEntityName());
	}

	public boolean isTeammate(Entity entity) {
		return this.isTeamPlayer(entity.getScoreboardTeam());
	}

	public boolean isTeamPlayer(AbstractTeam abstractTeam) {
		return this.getScoreboardTeam() != null ? this.getScoreboardTeam().isEqual(abstractTeam) : false;
	}

	public void setInvisible(boolean bl) {
		this.setFlag(5, bl);
	}

	protected boolean getFlag(int i) {
		return (this.dataTracker.get(FLAGS) & 1 << i) != 0;
	}

	protected void setFlag(int i, boolean bl) {
		byte b = this.dataTracker.get(FLAGS);
		if (bl) {
			this.dataTracker.set(FLAGS, (byte)(b | 1 << i));
		} else {
			this.dataTracker.set(FLAGS, (byte)(b & ~(1 << i)));
		}
	}

	public int getMaxAir() {
		return 300;
	}

	public int getAir() {
		return this.dataTracker.get(AIR);
	}

	public void setAir(int i) {
		this.dataTracker.set(AIR, i);
	}

	public void onStruckByLightning(LightningEntity lightningEntity) {
		this.fireTicks++;
		if (this.fireTicks == 0) {
			this.setOnFireFor(8);
		}

		this.damage(DamageSource.LIGHTNING_BOLT, 5.0F);
	}

	public void onBubbleColumnSurfaceCollision(boolean bl) {
		Vec3d vec3d = this.getVelocity();
		double d;
		if (bl) {
			d = Math.max(-0.9, vec3d.y - 0.03);
		} else {
			d = Math.min(1.8, vec3d.y + 0.1);
		}

		this.setVelocity(vec3d.x, d, vec3d.z);
	}

	public void onBubbleColumnCollision(boolean bl) {
		Vec3d vec3d = this.getVelocity();
		double d;
		if (bl) {
			d = Math.max(-0.3, vec3d.y - 0.03);
		} else {
			d = Math.min(0.7, vec3d.y + 0.06);
		}

		this.setVelocity(vec3d.x, d, vec3d.z);
		this.fallDistance = 0.0F;
	}

	public void onKilledOther(LivingEntity livingEntity) {
	}

	protected void pushOutOfBlocks(double d, double e, double f) {
		BlockPos blockPos = new BlockPos(d, e, f);
		Vec3d vec3d = new Vec3d(d - (double)blockPos.getX(), e - (double)blockPos.getY(), f - (double)blockPos.getZ());
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		Direction direction = Direction.field_11036;
		double g = Double.MAX_VALUE;

		for (Direction direction2 : new Direction[]{
			Direction.field_11043, Direction.field_11035, Direction.field_11039, Direction.field_11034, Direction.field_11036
		}) {
			mutable.set(blockPos).setOffset(direction2);
			if (!this.world.getBlockState(mutable).isFullCube(this.world, mutable)) {
				double h = vec3d.getComponentAlongAxis(direction2.getAxis());
				double i = direction2.getDirection() == Direction.AxisDirection.field_11056 ? 1.0 - h : h;
				if (i < g) {
					g = i;
					direction = direction2;
				}
			}
		}

		float j = this.random.nextFloat() * 0.2F + 0.1F;
		float k = (float)direction.getDirection().offset();
		Vec3d vec3d2 = this.getVelocity().multiply(0.75);
		if (direction.getAxis() == Direction.Axis.field_11048) {
			this.setVelocity((double)(k * j), vec3d2.y, vec3d2.z);
		} else if (direction.getAxis() == Direction.Axis.field_11052) {
			this.setVelocity(vec3d2.x, (double)(k * j), vec3d2.z);
		} else if (direction.getAxis() == Direction.Axis.field_11051) {
			this.setVelocity(vec3d2.x, vec3d2.y, (double)(k * j));
		}
	}

	public void slowMovement(BlockState blockState, Vec3d vec3d) {
		this.fallDistance = 0.0F;
		this.movementMultiplier = vec3d;
	}

	private static void removeClickEvents(Text text) {
		text.styled(style -> style.setClickEvent(null)).getSiblings().forEach(Entity::removeClickEvents);
	}

	@Override
	public Text getName() {
		Text text = this.getCustomName();
		if (text != null) {
			Text text2 = text.deepCopy();
			removeClickEvents(text2);
			return text2;
		} else {
			return this.getDefaultName();
		}
	}

	protected Text getDefaultName() {
		return this.type.getName();
	}

	public boolean isPartOf(Entity entity) {
		return this == entity;
	}

	public float getHeadYaw() {
		return 0.0F;
	}

	public void setHeadYaw(float f) {
	}

	public void setYaw(float f) {
	}

	public boolean isAttackable() {
		return true;
	}

	public boolean handleAttack(Entity entity) {
		return false;
	}

	public String toString() {
		return String.format(
			Locale.ROOT,
			"%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f]",
			this.getClass().getSimpleName(),
			this.getName().asString(),
			this.entityId,
			this.world == null ? "~NULL~" : this.world.getLevelProperties().getLevelName(),
			this.getX(),
			this.getY(),
			this.getZ()
		);
	}

	public boolean isInvulnerableTo(DamageSource damageSource) {
		return this.invulnerable && damageSource != DamageSource.OUT_OF_WORLD && !damageSource.isSourceCreativePlayer();
	}

	public boolean isInvulnerable() {
		return this.invulnerable;
	}

	public void setInvulnerable(boolean bl) {
		this.invulnerable = bl;
	}

	public void copyPositionAndRotation(Entity entity) {
		this.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), entity.yaw, entity.pitch);
	}

	public void copyFrom(Entity entity) {
		CompoundTag compoundTag = entity.toTag(new CompoundTag());
		compoundTag.remove("Dimension");
		this.fromTag(compoundTag);
		this.netherPortalCooldown = entity.netherPortalCooldown;
		this.lastNetherPortalPosition = entity.lastNetherPortalPosition;
		this.lastNetherPortalDirectionVector = entity.lastNetherPortalDirectionVector;
		this.lastNetherPortalDirection = entity.lastNetherPortalDirection;
	}

	@Nullable
	public Entity changeDimension(DimensionType dimensionType) {
		if (!this.world.isClient && !this.removed) {
			this.world.getProfiler().push("changeDimension");
			MinecraftServer minecraftServer = this.getServer();
			DimensionType dimensionType2 = this.dimension;
			ServerWorld serverWorld = minecraftServer.getWorld(dimensionType2);
			ServerWorld serverWorld2 = minecraftServer.getWorld(dimensionType);
			this.dimension = dimensionType;
			this.detach();
			this.world.getProfiler().push("reposition");
			Vec3d vec3d = this.getVelocity();
			float f = 0.0F;
			BlockPos blockPos;
			if (dimensionType2 == DimensionType.field_13078 && dimensionType == DimensionType.field_13072) {
				blockPos = serverWorld2.getTopPosition(Heightmap.Type.field_13203, serverWorld2.getSpawnPos());
			} else if (dimensionType == DimensionType.field_13078) {
				blockPos = serverWorld2.getForcedSpawnPoint();
			} else {
				double d = this.getX();
				double e = this.getZ();
				double g = 8.0;
				if (dimensionType2 == DimensionType.field_13072 && dimensionType == DimensionType.field_13076) {
					d /= 8.0;
					e /= 8.0;
				} else if (dimensionType2 == DimensionType.field_13076 && dimensionType == DimensionType.field_13072) {
					d *= 8.0;
					e *= 8.0;
				}

				double h = Math.min(-2.9999872E7, serverWorld2.getWorldBorder().getBoundWest() + 16.0);
				double i = Math.min(-2.9999872E7, serverWorld2.getWorldBorder().getBoundNorth() + 16.0);
				double j = Math.min(2.9999872E7, serverWorld2.getWorldBorder().getBoundEast() - 16.0);
				double k = Math.min(2.9999872E7, serverWorld2.getWorldBorder().getBoundSouth() - 16.0);
				d = MathHelper.clamp(d, h, j);
				e = MathHelper.clamp(e, i, k);
				Vec3d vec3d2 = this.getLastNetherPortalDirectionVector();
				blockPos = new BlockPos(d, this.getY(), e);
				BlockPattern.TeleportTarget teleportTarget = serverWorld2.getPortalForcer()
					.getPortal(blockPos, vec3d, this.getLastNetherPortalDirection(), vec3d2.x, vec3d2.y, this instanceof PlayerEntity);
				if (teleportTarget == null) {
					return null;
				}

				blockPos = new BlockPos(teleportTarget.pos);
				vec3d = teleportTarget.velocity;
				f = (float)teleportTarget.yaw;
			}

			this.world.getProfiler().swap("reloading");
			Entity entity = this.getType().create(serverWorld2);
			if (entity != null) {
				entity.copyFrom(this);
				entity.refreshPositionAndAngles(blockPos, entity.yaw + f, entity.pitch);
				entity.setVelocity(vec3d);
				serverWorld2.onDimensionChanged(entity);
			}

			this.removed = true;
			this.world.getProfiler().pop();
			serverWorld.resetIdleTimeout();
			serverWorld2.resetIdleTimeout();
			this.world.getProfiler().pop();
			return entity;
		} else {
			return null;
		}
	}

	public boolean canUsePortals() {
		return true;
	}

	public float getEffectiveExplosionResistance(
		Explosion explosion, BlockView blockView, BlockPos blockPos, BlockState blockState, FluidState fluidState, float f
	) {
		return f;
	}

	public boolean canExplosionDestroyBlock(Explosion explosion, BlockView blockView, BlockPos blockPos, BlockState blockState, float f) {
		return true;
	}

	public int getSafeFallDistance() {
		return 3;
	}

	public Vec3d getLastNetherPortalDirectionVector() {
		return this.lastNetherPortalDirectionVector;
	}

	public Direction getLastNetherPortalDirection() {
		return this.lastNetherPortalDirection;
	}

	public boolean canAvoidTraps() {
		return false;
	}

	public void populateCrashReport(CrashReportSection crashReportSection) {
		crashReportSection.add("Entity Type", (CrashCallable<String>)(() -> EntityType.getId(this.getType()) + " (" + this.getClass().getCanonicalName() + ")"));
		crashReportSection.add("Entity ID", this.entityId);
		crashReportSection.add("Entity Name", (CrashCallable<String>)(() -> this.getName().getString()));
		crashReportSection.add("Entity's Exact location", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", this.getX(), this.getY(), this.getZ()));
		crashReportSection.add(
			"Entity's Block location",
			CrashReportSection.createPositionString(MathHelper.floor(this.getX()), MathHelper.floor(this.getY()), MathHelper.floor(this.getZ()))
		);
		Vec3d vec3d = this.getVelocity();
		crashReportSection.add("Entity's Momentum", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", vec3d.x, vec3d.y, vec3d.z));
		crashReportSection.add("Entity's Passengers", (CrashCallable<String>)(() -> this.getPassengerList().toString()));
		crashReportSection.add("Entity's Vehicle", (CrashCallable<String>)(() -> this.getVehicle().toString()));
	}

	public boolean doesRenderOnFire() {
		return this.isOnFire() && !this.isSpectator();
	}

	public void setUuid(UUID uUID) {
		this.uuid = uUID;
		this.uuidString = this.uuid.toString();
	}

	public UUID getUuid() {
		return this.uuid;
	}

	public String getUuidAsString() {
		return this.uuidString;
	}

	public String getEntityName() {
		return this.uuidString;
	}

	public boolean canFly() {
		return true;
	}

	public static double getRenderDistanceMultiplier() {
		return renderDistanceMultiplier;
	}

	public static void setRenderDistanceMultiplier(double d) {
		renderDistanceMultiplier = d;
	}

	@Override
	public Text getDisplayName() {
		return Team.modifyText(this.getScoreboardTeam(), this.getName())
			.styled(style -> style.setHoverEvent(this.getHoverEvent()).setInsertion(this.getUuidAsString()));
	}

	public void setCustomName(@Nullable Text text) {
		this.dataTracker.set(CUSTOM_NAME, Optional.ofNullable(text));
	}

	@Nullable
	@Override
	public Text getCustomName() {
		return (Text)this.dataTracker.get(CUSTOM_NAME).orElse(null);
	}

	@Override
	public boolean hasCustomName() {
		return this.dataTracker.get(CUSTOM_NAME).isPresent();
	}

	public void setCustomNameVisible(boolean bl) {
		this.dataTracker.set(NAME_VISIBLE, bl);
	}

	public boolean isCustomNameVisible() {
		return this.dataTracker.get(NAME_VISIBLE);
	}

	public final void teleport(double d, double e, double f) {
		if (this.world instanceof ServerWorld) {
			ChunkPos chunkPos = new ChunkPos(new BlockPos(d, e, f));
			((ServerWorld)this.world).getChunkManager().addTicket(ChunkTicketType.field_19347, chunkPos, 0, this.getEntityId());
			this.world.getChunk(chunkPos.x, chunkPos.z);
			this.requestTeleport(d, e, f);
		}
	}

	public void requestTeleport(double d, double e, double f) {
		if (this.world instanceof ServerWorld) {
			ServerWorld serverWorld = (ServerWorld)this.world;
			this.refreshPositionAndAngles(d, e, f, this.yaw, this.pitch);
			this.streamPassengersRecursively().forEach(entity -> {
				serverWorld.checkChunk(entity);
				entity.teleportRequested = true;
				entity.updatePositionsRecursively(Entity::positAfterTeleport);
			});
		}
	}

	public boolean shouldRenderName() {
		return this.isCustomNameVisible();
	}

	public void onTrackedDataSet(TrackedData<?> trackedData) {
		if (POSE.equals(trackedData)) {
			this.calculateDimensions();
		}
	}

	public void calculateDimensions() {
		EntityDimensions entityDimensions = this.dimensions;
		EntityPose entityPose = this.getPose();
		EntityDimensions entityDimensions2 = this.getDimensions(entityPose);
		this.dimensions = entityDimensions2;
		this.standingEyeHeight = this.getEyeHeight(entityPose, entityDimensions2);
		if (entityDimensions2.width < entityDimensions.width) {
			double d = (double)entityDimensions2.width / 2.0;
			this.setBoundingBox(new Box(this.getX() - d, this.getY(), this.getZ() - d, this.getX() + d, this.getY() + (double)entityDimensions2.height, this.getZ() + d));
		} else {
			Box box = this.getBoundingBox();
			this.setBoundingBox(
				new Box(
					box.x1, box.y1, box.z1, box.x1 + (double)entityDimensions2.width, box.y1 + (double)entityDimensions2.height, box.z1 + (double)entityDimensions2.width
				)
			);
			if (entityDimensions2.width > entityDimensions.width && !this.firstUpdate && !this.world.isClient) {
				float f = entityDimensions.width - entityDimensions2.width;
				this.move(MovementType.field_6308, new Vec3d((double)f, 0.0, (double)f));
			}
		}
	}

	public Direction getHorizontalFacing() {
		return Direction.fromRotation((double)this.yaw);
	}

	public Direction getMovementDirection() {
		return this.getHorizontalFacing();
	}

	protected HoverEvent getHoverEvent() {
		CompoundTag compoundTag = new CompoundTag();
		Identifier identifier = EntityType.getId(this.getType());
		compoundTag.putString("id", this.getUuidAsString());
		if (identifier != null) {
			compoundTag.putString("type", identifier.toString());
		}

		compoundTag.putString("name", Text.Serializer.toJson(this.getName()));
		return new HoverEvent(HoverEvent.Action.field_11761, new LiteralText(compoundTag.toString()));
	}

	public boolean canBeSpectated(ServerPlayerEntity serverPlayerEntity) {
		return true;
	}

	public Box getBoundingBox() {
		return this.entityBounds;
	}

	public Box getVisibilityBoundingBox() {
		return this.getBoundingBox();
	}

	protected Box calculateBoundsForPose(EntityPose entityPose) {
		EntityDimensions entityDimensions = this.getDimensions(entityPose);
		float f = entityDimensions.width / 2.0F;
		Vec3d vec3d = new Vec3d(this.getX() - (double)f, this.getY(), this.getZ() - (double)f);
		Vec3d vec3d2 = new Vec3d(this.getX() + (double)f, this.getY() + (double)entityDimensions.height, this.getZ() + (double)f);
		return new Box(vec3d, vec3d2);
	}

	public void setBoundingBox(Box box) {
		this.entityBounds = box;
	}

	protected float getEyeHeight(EntityPose entityPose, EntityDimensions entityDimensions) {
		return entityDimensions.height * 0.85F;
	}

	public float getEyeHeight(EntityPose entityPose) {
		return this.getEyeHeight(entityPose, this.getDimensions(entityPose));
	}

	public final float getStandingEyeHeight() {
		return this.standingEyeHeight;
	}

	public boolean equip(int i, ItemStack itemStack) {
		return false;
	}

	@Override
	public void sendMessage(Text text) {
	}

	public BlockPos getBlockPos() {
		return new BlockPos(this);
	}

	public Vec3d getPosVector() {
		return this.getPos();
	}

	public World getEntityWorld() {
		return this.world;
	}

	@Nullable
	public MinecraftServer getServer() {
		return this.world.getServer();
	}

	public ActionResult interactAt(PlayerEntity playerEntity, Vec3d vec3d, Hand hand) {
		return ActionResult.field_5811;
	}

	public boolean isImmuneToExplosion() {
		return false;
	}

	protected void dealDamage(LivingEntity livingEntity, Entity entity) {
		if (entity instanceof LivingEntity) {
			EnchantmentHelper.onUserDamaged((LivingEntity)entity, livingEntity);
		}

		EnchantmentHelper.onTargetDamaged(livingEntity, entity);
	}

	public void onStartedTrackingBy(ServerPlayerEntity serverPlayerEntity) {
	}

	public void onStoppedTrackingBy(ServerPlayerEntity serverPlayerEntity) {
	}

	public float applyRotation(BlockRotation blockRotation) {
		float f = MathHelper.wrapDegrees(this.yaw);
		switch (blockRotation) {
			case field_11464:
				return f + 180.0F;
			case field_11465:
				return f + 270.0F;
			case field_11463:
				return f + 90.0F;
			default:
				return f;
		}
	}

	public float applyMirror(BlockMirror blockMirror) {
		float f = MathHelper.wrapDegrees(this.yaw);
		switch (blockMirror) {
			case field_11300:
				return -f;
			case field_11301:
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

	public boolean hasPassenger(Entity entity) {
		for (Entity entity2 : this.getPassengerList()) {
			if (entity2.equals(entity)) {
				return true;
			}
		}

		return false;
	}

	public boolean hasPassengerType(Class<? extends Entity> class_) {
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
			entity.collectPassengers(false, set);
		}

		return set;
	}

	public Stream<Entity> streamPassengersRecursively() {
		return Stream.concat(Stream.of(this), this.passengerList.stream().flatMap(Entity::streamPassengersRecursively));
	}

	public boolean hasPlayerRider() {
		Set<Entity> set = Sets.newHashSet();
		this.collectPassengers(true, set);
		return set.size() == 1;
	}

	private void collectPassengers(boolean bl, Set<Entity> set) {
		for (Entity entity : this.getPassengerList()) {
			if (!bl || ServerPlayerEntity.class.isAssignableFrom(entity.getClass())) {
				set.add(entity);
			}

			entity.collectPassengers(bl, set);
		}
	}

	public Entity getRootVehicle() {
		Entity entity = this;

		while (entity.hasVehicle()) {
			entity = entity.getVehicle();
		}

		return entity;
	}

	public boolean isConnectedThroughVehicle(Entity entity) {
		return this.getRootVehicle() == entity.getRootVehicle();
	}

	public boolean hasPassengerDeep(Entity entity) {
		for (Entity entity2 : this.getPassengerList()) {
			if (entity2.equals(entity)) {
				return true;
			}

			if (entity2.hasPassengerDeep(entity)) {
				return true;
			}
		}

		return false;
	}

	public void updatePositionsRecursively(Entity.PositionUpdater positionUpdater) {
		for (Entity entity : this.passengerList) {
			this.updatePassengerPosition(entity, positionUpdater);
		}
	}

	public boolean isLogicalSideForUpdatingMovement() {
		Entity entity = this.getPrimaryPassenger();
		return entity instanceof PlayerEntity ? ((PlayerEntity)entity).isMainPlayer() : !this.world.isClient;
	}

	@Nullable
	public Entity getVehicle() {
		return this.vehicle;
	}

	public PistonBehavior getPistonBehavior() {
		return PistonBehavior.field_15974;
	}

	public SoundCategory getSoundCategory() {
		return SoundCategory.field_15254;
	}

	protected int getBurningDuration() {
		return 1;
	}

	public ServerCommandSource getCommandSource() {
		return new ServerCommandSource(
			this,
			this.getPos(),
			this.getRotationClient(),
			this.world instanceof ServerWorld ? (ServerWorld)this.world : null,
			this.getPermissionLevel(),
			this.getName().getString(),
			this.getDisplayName(),
			this.world.getServer(),
			this
		);
	}

	protected int getPermissionLevel() {
		return 0;
	}

	public boolean allowsPermissionLevel(int i) {
		return this.getPermissionLevel() >= i;
	}

	@Override
	public boolean sendCommandFeedback() {
		return this.world.getGameRules().getBoolean(GameRules.field_19400);
	}

	@Override
	public boolean shouldTrackOutput() {
		return true;
	}

	@Override
	public boolean shouldBroadcastConsoleToOps() {
		return true;
	}

	public void lookAt(EntityAnchorArgumentType.EntityAnchor entityAnchor, Vec3d vec3d) {
		Vec3d vec3d2 = entityAnchor.positionAt(this);
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

	public boolean updateMovementInFluid(Tag<Fluid> tag) {
		Box box = this.getBoundingBox().contract(0.001);
		int i = MathHelper.floor(box.x1);
		int j = MathHelper.ceil(box.x2);
		int k = MathHelper.floor(box.y1);
		int l = MathHelper.ceil(box.y2);
		int m = MathHelper.floor(box.z1);
		int n = MathHelper.ceil(box.z2);
		if (!this.world.isRegionLoaded(i, k, m, j, l, n)) {
			return false;
		} else {
			double d = 0.0;
			boolean bl = this.canFly();
			boolean bl2 = false;
			Vec3d vec3d = Vec3d.ZERO;
			int o = 0;

			try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
				for (int p = i; p < j; p++) {
					for (int q = k; q < l; q++) {
						for (int r = m; r < n; r++) {
							pooledMutable.set(p, q, r);
							FluidState fluidState = this.world.getFluidState(pooledMutable);
							if (fluidState.matches(tag)) {
								double e = (double)((float)q + fluidState.getHeight(this.world, pooledMutable));
								if (e >= box.y1) {
									bl2 = true;
									d = Math.max(e - box.y1, d);
									if (bl) {
										Vec3d vec3d2 = fluidState.getVelocity(this.world, pooledMutable);
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

				this.setVelocity(this.getVelocity().add(vec3d.multiply(0.014)));
			}

			this.waterHeight = d;
			return bl2;
		}
	}

	public double getWaterHeight() {
		return this.waterHeight;
	}

	public final float getWidth() {
		return this.dimensions.width;
	}

	public final float getHeight() {
		return this.dimensions.height;
	}

	public abstract Packet<?> createSpawnPacket();

	public EntityDimensions getDimensions(EntityPose entityPose) {
		return this.type.getDimensions();
	}

	public Vec3d getPos() {
		return new Vec3d(this.x, this.y, this.z);
	}

	public Vec3d getVelocity() {
		return this.velocity;
	}

	public void setVelocity(Vec3d vec3d) {
		this.velocity = vec3d;
	}

	public void setVelocity(double d, double e, double f) {
		this.setVelocity(new Vec3d(d, e, f));
	}

	public final double getX() {
		return this.x;
	}

	public double offsetX(double d) {
		return this.x + (double)this.getWidth() * d;
	}

	public double getParticleX(double d) {
		return this.offsetX((2.0 * this.random.nextDouble() - 1.0) * d);
	}

	public final double getY() {
		return this.y;
	}

	public double getBodyY(double d) {
		return this.y + (double)this.getHeight() * d;
	}

	public double getRandomBodyY() {
		return this.getBodyY(this.random.nextDouble());
	}

	public double getEyeY() {
		return this.y + (double)this.standingEyeHeight;
	}

	public final double getZ() {
		return this.z;
	}

	public double offsetZ(double d) {
		return this.z + (double)this.getWidth() * d;
	}

	public double getParticleZ(double d) {
		return this.offsetZ((2.0 * this.random.nextDouble() - 1.0) * d);
	}

	public void setPos(double d, double e, double f) {
		this.x = d;
		this.y = e;
		this.z = f;
	}

	public void checkDespawn() {
	}

	public void positAfterTeleport(double d, double e, double f) {
		this.refreshPositionAndAngles(d, e, f, this.yaw, this.pitch);
	}

	@FunctionalInterface
	public interface PositionUpdater {
		void accept(Entity entity, double d, double e, double f);
	}
}
