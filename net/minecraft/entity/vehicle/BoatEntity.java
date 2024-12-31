package net.minecraft.entity.vehicle;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.class_4342;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LilyPadBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.predicate.EntityPredicate;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.play.SteerBoatC2SPacket;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.util.shapes.VoxelShapes;
import net.minecraft.world.World;

public class BoatEntity extends Entity {
	private static final TrackedData<Integer> DAMAGE_WOBBLE_TICKS = DataTracker.registerData(BoatEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> DAMAGE_WOBBLE_SIDE = DataTracker.registerData(BoatEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Float> DAMAGE_WOBBLE_STRENGTH = DataTracker.registerData(BoatEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Integer> BOAT_TYPE = DataTracker.registerData(BoatEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> field_17120 = DataTracker.registerData(BoatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> field_17121 = DataTracker.registerData(BoatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Integer> field_17122 = DataTracker.registerData(BoatEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private final float[] paddlePhases = new float[2];
	private float velocityDecay;
	private float ticksUnderwater;
	private float yawVelocity;
	private int field_12223;
	private double boatX;
	private double boatY;
	private double boatZ;
	private double boatYaw;
	private double boatPitch;
	private boolean pressingLeft;
	private boolean pressingRight;
	private boolean pressingForward;
	private boolean pressingBack;
	private double waterLevel;
	private float field_12217;
	private BoatEntity.Location location;
	private BoatEntity.Location lastLocation;
	private double fallVelocity;
	private boolean field_17115;
	private boolean field_17116;
	private float field_17117;
	private float field_17118;
	private float field_17119;

	public BoatEntity(World world) {
		super(EntityType.BOAT, world);
		this.inanimate = true;
		this.setBounds(1.375F, 0.5625F);
	}

	public BoatEntity(World world, double d, double e, double f) {
		this(world);
		this.updatePosition(d, e, f);
		this.velocityX = 0.0;
		this.velocityY = 0.0;
		this.velocityZ = 0.0;
		this.prevX = d;
		this.prevY = e;
		this.prevZ = f;
	}

	@Override
	protected boolean canClimb() {
		return false;
	}

	@Override
	protected void initDataTracker() {
		this.dataTracker.startTracking(DAMAGE_WOBBLE_TICKS, 0);
		this.dataTracker.startTracking(DAMAGE_WOBBLE_SIDE, 1);
		this.dataTracker.startTracking(DAMAGE_WOBBLE_STRENGTH, 0.0F);
		this.dataTracker.startTracking(BOAT_TYPE, BoatEntity.Type.OAK.ordinal());
		this.dataTracker.startTracking(field_17120, false);
		this.dataTracker.startTracking(field_17121, false);
		this.dataTracker.startTracking(field_17122, 0);
	}

	@Nullable
	@Override
	public Box getHardCollisionBox(Entity collidingEntity) {
		return collidingEntity.isPushable() ? collidingEntity.getBoundingBox() : null;
	}

	@Nullable
	@Override
	public Box getBox() {
		return this.getBoundingBox();
	}

	@Override
	public boolean isPushable() {
		return true;
	}

	@Override
	public double getMountedHeightOffset() {
		return -0.1;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else if (this.world.isClient || this.removed) {
			return true;
		} else if (source instanceof ProjectileDamageSource && source.getAttacker() != null && this.hasPassenger(source.getAttacker())) {
			return false;
		} else {
			this.setDamageWobbleSide(-this.getDamageWobbleSide());
			this.setBubbleWobbleTicks(10);
			this.setDamageWobbleStrength(this.getDamageWobbleStrength() + amount * 10.0F);
			this.scheduleVelocityUpdate();
			boolean bl = source.getAttacker() instanceof PlayerEntity && ((PlayerEntity)source.getAttacker()).abilities.creativeMode;
			if (bl || this.getDamageWobbleStrength() > 40.0F) {
				if (!bl && this.world.getGameRules().getBoolean("doEntityDrops")) {
					this.method_15560(this.asItem());
				}

				this.remove();
			}

			return true;
		}
	}

	@Override
	public void method_15593(boolean bl) {
		if (!this.world.isClient) {
			this.field_17115 = true;
			this.field_17116 = bl;
			if (this.method_15963() == 0) {
				this.method_15961(60);
			}
		}

		this.world
			.method_16343(class_4342.field_21368, this.x + (double)this.random.nextFloat(), this.y + 0.7, this.z + (double)this.random.nextFloat(), 0.0, 0.0, 0.0);
		if (this.random.nextInt(20) == 0) {
			this.world.playSound(this.x, this.y, this.z, this.method_12985(), this.getSoundCategory(), 1.0F, 0.8F + 0.4F * this.random.nextFloat(), false);
		}
	}

	@Override
	public void pushAwayFrom(Entity entity) {
		if (entity instanceof BoatEntity) {
			if (entity.getBoundingBox().minY < this.getBoundingBox().maxY) {
				super.pushAwayFrom(entity);
			}
		} else if (entity.getBoundingBox().minY <= this.getBoundingBox().minY) {
			super.pushAwayFrom(entity);
		}
	}

	public Item asItem() {
		switch (this.getBoatType()) {
			case OAK:
			default:
				return Items.BOAT;
			case SPRUCE:
				return Items.SPRUCE_BOAT;
			case BIRCH:
				return Items.BIRCH_BOAT;
			case JUNGLE:
				return Items.JUNGLE_BOAT;
			case ACACIA:
				return Items.ACACIA_BOAT;
			case DARK_OAK:
				return Items.DARK_OAK_BOAT;
		}
	}

	@Override
	public void animateDamage() {
		this.setDamageWobbleSide(-this.getDamageWobbleSide());
		this.setBubbleWobbleTicks(10);
		this.setDamageWobbleStrength(this.getDamageWobbleStrength() * 11.0F);
	}

	@Override
	public boolean collides() {
		return !this.removed;
	}

	@Override
	public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
		this.boatX = x;
		this.boatY = y;
		this.boatZ = z;
		this.boatYaw = (double)yaw;
		this.boatPitch = (double)pitch;
		this.field_12223 = 10;
	}

	@Override
	public Direction getMovementDirection() {
		return this.getHorizontalDirection().rotateYClockwise();
	}

	@Override
	public void tick() {
		this.lastLocation = this.location;
		this.location = this.checkLocation();
		if (this.location != BoatEntity.Location.UNDER_WATER && this.location != BoatEntity.Location.UNDER_FLOWING_WATER) {
			this.ticksUnderwater = 0.0F;
		} else {
			this.ticksUnderwater++;
		}

		if (!this.world.isClient && this.ticksUnderwater >= 60.0F) {
			this.removeAllPassengers();
		}

		if (this.getBubbleWobbleTicks() > 0) {
			this.setBubbleWobbleTicks(this.getBubbleWobbleTicks() - 1);
		}

		if (this.getDamageWobbleStrength() > 0.0F) {
			this.setDamageWobbleStrength(this.getDamageWobbleStrength() - 1.0F);
		}

		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
		super.tick();
		this.method_11335();
		if (this.method_13003()) {
			if (this.getPassengerList().isEmpty() || !(this.getPassengerList().get(0) instanceof PlayerEntity)) {
				this.setPaddleMoving(false, false);
			}

			this.updateVelocity();
			if (this.world.isClient) {
				this.updatePaddles();
				this.world.method_11483(new SteerBoatC2SPacket(this.isPaddleMoving(0), this.isPaddleMoving(1)));
			}

			this.move(MovementType.SELF, this.velocityX, this.velocityY, this.velocityZ);
		} else {
			this.velocityX = 0.0;
			this.velocityY = 0.0;
			this.velocityZ = 0.0;
		}

		this.method_15962();

		for (int i = 0; i <= 1; i++) {
			if (this.isPaddleMoving(i)) {
				if (!this.isSilent()
					&& (double)(this.paddlePhases[i] % (float) (Math.PI * 2)) <= (float) (Math.PI / 4)
					&& ((double)this.paddlePhases[i] + (float) (Math.PI / 8)) % (float) (Math.PI * 2) >= (float) (Math.PI / 4)) {
					Sound sound = this.getPaddleSound();
					if (sound != null) {
						Vec3d vec3d = this.getRotationVector(1.0F);
						double d = i == 1 ? -vec3d.z : vec3d.z;
						double e = i == 1 ? vec3d.x : -vec3d.x;
						this.world.playSound(null, this.x + d, this.y, this.z + e, sound, this.getSoundCategory(), 1.0F, 0.8F + 0.4F * this.random.nextFloat());
					}
				}

				this.paddlePhases[i] = (float)((double)this.paddlePhases[i] + (float) (Math.PI / 8));
			} else {
				this.paddlePhases[i] = 0.0F;
			}
		}

		this.checkBlockCollision();
		List<Entity> list = this.world.method_16288(this, this.getBoundingBox().expand(0.2F, -0.01F, 0.2F), EntityPredicate.method_15605(this));
		if (!list.isEmpty()) {
			boolean bl = !this.world.isClient && !(this.getPrimaryPassenger() instanceof PlayerEntity);

			for (int j = 0; j < list.size(); j++) {
				Entity entity = (Entity)list.get(j);
				if (!entity.hasPassenger(this)) {
					if (bl
						&& this.getPassengerList().size() < 2
						&& !entity.hasMount()
						&& entity.width < this.width
						&& entity instanceof LivingEntity
						&& !(entity instanceof WaterCreatureEntity)
						&& !(entity instanceof PlayerEntity)) {
						entity.ride(this);
					} else {
						this.pushAwayFrom(entity);
					}
				}
			}
		}
	}

	private void method_15962() {
		if (this.world.isClient) {
			int i = this.method_15963();
			if (i > 0) {
				this.field_17117 += 0.05F;
			} else {
				this.field_17117 -= 0.1F;
			}

			this.field_17117 = MathHelper.clamp(this.field_17117, 0.0F, 1.0F);
			this.field_17119 = this.field_17118;
			this.field_17118 = 10.0F * (float)Math.sin((double)(0.5F * (float)this.world.getLastUpdateTime())) * this.field_17117;
		} else {
			if (!this.field_17115) {
				this.method_15961(0);
			}

			int j = this.method_15963();
			if (j > 0) {
				this.method_15961(--j);
				int k = 60 - j - 1;
				if (k > 0 && j == 0) {
					this.method_15961(0);
					if (this.field_17116) {
						this.velocityY -= 0.7;
						this.removeAllPassengers();
					} else {
						this.velocityY = this.method_15566(PlayerEntity.class) ? 2.7 : 0.6;
					}
				}

				this.field_17115 = false;
			}
		}
	}

	@Nullable
	protected Sound getPaddleSound() {
		switch (this.checkLocation()) {
			case IN_WATER:
			case UNDER_WATER:
			case UNDER_FLOWING_WATER:
				return Sounds.ENTITY_BOAT_PADDLE_WATER;
			case ON_LAND:
				return Sounds.ENTITY_BOAT_PADDLE_LAND;
			case IN_AIR:
			default:
				return null;
		}
	}

	private void method_11335() {
		if (this.field_12223 > 0 && !this.method_13003()) {
			double d = this.x + (this.boatX - this.x) / (double)this.field_12223;
			double e = this.y + (this.boatY - this.y) / (double)this.field_12223;
			double f = this.z + (this.boatZ - this.z) / (double)this.field_12223;
			double g = MathHelper.wrapDegrees(this.boatYaw - (double)this.yaw);
			this.yaw = (float)((double)this.yaw + g / (double)this.field_12223);
			this.pitch = (float)((double)this.pitch + (this.boatPitch - (double)this.pitch) / (double)this.field_12223);
			this.field_12223--;
			this.updatePosition(d, e, f);
			this.setRotation(this.yaw, this.pitch);
		}
	}

	public void setPaddleMoving(boolean leftMoving, boolean rightMoving) {
		this.dataTracker.set(field_17120, leftMoving);
		this.dataTracker.set(field_17121, rightMoving);
	}

	public float interpolatePaddlePhase(int paddle, float tickDelta) {
		return this.isPaddleMoving(paddle)
			? (float)MathHelper.clampedLerp((double)this.paddlePhases[paddle] - (float) (Math.PI / 8), (double)this.paddlePhases[paddle], (double)tickDelta)
			: 0.0F;
	}

	private BoatEntity.Location checkLocation() {
		BoatEntity.Location location = this.getUnderWaterLocation();
		if (location != null) {
			this.waterLevel = this.getBoundingBox().maxY;
			return location;
		} else if (this.checkBoatInWater()) {
			return BoatEntity.Location.IN_WATER;
		} else {
			float f = this.method_11333();
			if (f > 0.0F) {
				this.field_12217 = f;
				return BoatEntity.Location.ON_LAND;
			} else {
				return BoatEntity.Location.IN_AIR;
			}
		}
	}

	public float method_11332() {
		Box box = this.getBoundingBox();
		int i = MathHelper.floor(box.minX);
		int j = MathHelper.ceil(box.maxX);
		int k = MathHelper.floor(box.maxY);
		int l = MathHelper.ceil(box.maxY - this.fallVelocity);
		int m = MathHelper.floor(box.minZ);
		int n = MathHelper.ceil(box.maxZ);

		try (BlockPos.Pooled pooled = BlockPos.Pooled.get()) {
			label136:
			for (int o = k; o < l; o++) {
				float f = 0.0F;
				int p = i;

				while (true) {
					if (p < j) {
						for (int q = m; q < n; q++) {
							pooled.setPosition(p, o, q);
							FluidState fluidState = this.world.getFluidState(pooled);
							if (fluidState.matches(FluidTags.WATER)) {
								f = Math.max(f, (float)o + fluidState.method_17810());
							}

							if (f >= 1.0F) {
								continue label136;
							}
						}

						p++;
					} else {
						if (f < 1.0F) {
							return (float)pooled.getY() + f;
						}
						break;
					}
				}
			}

			return (float)(l + 1);
		}
	}

	public float method_11333() {
		Box box = this.getBoundingBox();
		Box box2 = new Box(box.minX, box.minY - 0.001, box.minZ, box.maxX, box.minY, box.maxZ);
		int i = MathHelper.floor(box2.minX) - 1;
		int j = MathHelper.ceil(box2.maxX) + 1;
		int k = MathHelper.floor(box2.minY) - 1;
		int l = MathHelper.ceil(box2.maxY) + 1;
		int m = MathHelper.floor(box2.minZ) - 1;
		int n = MathHelper.ceil(box2.maxZ) + 1;
		VoxelShape voxelShape = VoxelShapes.method_18049(box2);
		float f = 0.0F;
		int o = 0;

		try (BlockPos.Pooled pooled = BlockPos.Pooled.get()) {
			for (int p = i; p < j; p++) {
				for (int q = m; q < n; q++) {
					int r = (p != i && p != j - 1 ? 0 : 1) + (q != m && q != n - 1 ? 0 : 1);
					if (r != 2) {
						for (int s = k; s < l; s++) {
							if (r <= 0 || s != k && s != l - 1) {
								pooled.setPosition(p, s, q);
								BlockState blockState = this.world.getBlockState(pooled);
								if (!(blockState.getBlock() instanceof LilyPadBlock)
									&& VoxelShapes.matchesAnywhere(
										blockState.getCollisionShape(this.world, pooled).offset((double)p, (double)s, (double)q), voxelShape, BooleanBiFunction.AND
									)) {
									f += blockState.getBlock().getSlipperiness();
									o++;
								}
							}
						}
					}
				}
			}
		}

		return f / (float)o;
	}

	private boolean checkBoatInWater() {
		Box box = this.getBoundingBox();
		int i = MathHelper.floor(box.minX);
		int j = MathHelper.ceil(box.maxX);
		int k = MathHelper.floor(box.minY);
		int l = MathHelper.ceil(box.minY + 0.001);
		int m = MathHelper.floor(box.minZ);
		int n = MathHelper.ceil(box.maxZ);
		boolean bl = false;
		this.waterLevel = Double.MIN_VALUE;

		try (BlockPos.Pooled pooled = BlockPos.Pooled.get()) {
			for (int o = i; o < j; o++) {
				for (int p = k; p < l; p++) {
					for (int q = m; q < n; q++) {
						pooled.setPosition(o, p, q);
						FluidState fluidState = this.world.getFluidState(pooled);
						if (fluidState.matches(FluidTags.WATER)) {
							float f = (float)p + fluidState.method_17810();
							this.waterLevel = Math.max((double)f, this.waterLevel);
							bl |= box.minY < (double)f;
						}
					}
				}
			}
		}

		return bl;
	}

	@Nullable
	private BoatEntity.Location getUnderWaterLocation() {
		Box box = this.getBoundingBox();
		double d = box.maxY + 0.001;
		int i = MathHelper.floor(box.minX);
		int j = MathHelper.ceil(box.maxX);
		int k = MathHelper.floor(box.maxY);
		int l = MathHelper.ceil(d);
		int m = MathHelper.floor(box.minZ);
		int n = MathHelper.ceil(box.maxZ);
		boolean bl = false;

		try (BlockPos.Pooled pooled = BlockPos.Pooled.get()) {
			for (int o = i; o < j; o++) {
				for (int p = k; p < l; p++) {
					for (int q = m; q < n; q++) {
						pooled.setPosition(o, p, q);
						FluidState fluidState = this.world.getFluidState(pooled);
						if (fluidState.matches(FluidTags.WATER) && d < (double)((float)pooled.getY() + fluidState.method_17810())) {
							if (!fluidState.isStill()) {
								return BoatEntity.Location.UNDER_FLOWING_WATER;
							}

							bl = true;
						}
					}
				}
			}
		}

		return bl ? BoatEntity.Location.UNDER_WATER : null;
	}

	private void updateVelocity() {
		double d = -0.04F;
		double e = this.hasNoGravity() ? 0.0 : -0.04F;
		double f = 0.0;
		this.velocityDecay = 0.05F;
		if (this.lastLocation == BoatEntity.Location.IN_AIR && this.location != BoatEntity.Location.IN_AIR && this.location != BoatEntity.Location.ON_LAND) {
			this.waterLevel = this.getBoundingBox().minY + (double)this.height;
			this.updatePosition(this.x, (double)(this.method_11332() - this.height) + 0.101, this.z);
			this.velocityY = 0.0;
			this.fallVelocity = 0.0;
			this.location = BoatEntity.Location.IN_WATER;
		} else {
			if (this.location == BoatEntity.Location.IN_WATER) {
				f = (this.waterLevel - this.getBoundingBox().minY) / (double)this.height;
				this.velocityDecay = 0.9F;
			} else if (this.location == BoatEntity.Location.UNDER_FLOWING_WATER) {
				e = -7.0E-4;
				this.velocityDecay = 0.9F;
			} else if (this.location == BoatEntity.Location.UNDER_WATER) {
				f = 0.01F;
				this.velocityDecay = 0.45F;
			} else if (this.location == BoatEntity.Location.IN_AIR) {
				this.velocityDecay = 0.9F;
			} else if (this.location == BoatEntity.Location.ON_LAND) {
				this.velocityDecay = this.field_12217;
				if (this.getPrimaryPassenger() instanceof PlayerEntity) {
					this.field_12217 /= 2.0F;
				}
			}

			this.velocityX = this.velocityX * (double)this.velocityDecay;
			this.velocityZ = this.velocityZ * (double)this.velocityDecay;
			this.yawVelocity = this.yawVelocity * this.velocityDecay;
			this.velocityY += e;
			if (f > 0.0) {
				double g = 0.65;
				this.velocityY += f * 0.06153846016296973;
				double h = 0.75;
				this.velocityY *= 0.75;
			}
		}
	}

	private void updatePaddles() {
		if (this.hasPassengers()) {
			float f = 0.0F;
			if (this.pressingLeft) {
				this.yawVelocity += -1.0F;
			}

			if (this.pressingRight) {
				this.yawVelocity++;
			}

			if (this.pressingRight != this.pressingLeft && !this.pressingForward && !this.pressingBack) {
				f += 0.005F;
			}

			this.yaw = this.yaw + this.yawVelocity;
			if (this.pressingForward) {
				f += 0.04F;
			}

			if (this.pressingBack) {
				f -= 0.005F;
			}

			this.velocityX = this.velocityX + (double)(MathHelper.sin(-this.yaw * (float) (Math.PI / 180.0)) * f);
			this.velocityZ = this.velocityZ + (double)(MathHelper.cos(this.yaw * (float) (Math.PI / 180.0)) * f);
			this.setPaddleMoving(this.pressingRight && !this.pressingLeft || this.pressingForward, this.pressingLeft && !this.pressingRight || this.pressingForward);
		}
	}

	@Override
	public void updatePassengerPosition(Entity passenger) {
		if (this.hasPassenger(passenger)) {
			float f = 0.0F;
			float g = (float)((this.removed ? 0.01F : this.getMountedHeightOffset()) + passenger.getHeightOffset());
			if (this.getPassengerList().size() > 1) {
				int i = this.getPassengerList().indexOf(passenger);
				if (i == 0) {
					f = 0.2F;
				} else {
					f = -0.6F;
				}

				if (passenger instanceof AnimalEntity) {
					f = (float)((double)f + 0.2);
				}
			}

			Vec3d vec3d = new Vec3d((double)f, 0.0, 0.0).rotateY(-this.yaw * (float) (Math.PI / 180.0) - (float) (Math.PI / 2));
			passenger.updatePosition(this.x + vec3d.x, this.y + (double)g, this.z + vec3d.z);
			passenger.yaw = passenger.yaw + this.yawVelocity;
			passenger.setHeadYaw(passenger.getHeadRotation() + this.yawVelocity);
			this.copyEntityData(passenger);
			if (passenger instanceof AnimalEntity && this.getPassengerList().size() > 1) {
				int j = passenger.getEntityId() % 2 == 0 ? 90 : 270;
				passenger.setYaw(((AnimalEntity)passenger).bodyYaw + (float)j);
				passenger.setHeadYaw(passenger.getHeadRotation() + (float)j);
			}
		}
	}

	protected void copyEntityData(Entity entity) {
		entity.setYaw(this.yaw);
		float f = MathHelper.wrapDegrees(entity.yaw - this.yaw);
		float g = MathHelper.clamp(f, -105.0F, 105.0F);
		entity.prevYaw += g - f;
		entity.yaw += g - f;
		entity.setHeadYaw(entity.yaw);
	}

	@Override
	public void onPassengerLookAround(Entity passenger) {
		this.copyEntityData(passenger);
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		nbt.putString("Type", this.getBoatType().getName());
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		if (nbt.contains("Type", 8)) {
			this.setBoatType(BoatEntity.Type.getType(nbt.getString("Type")));
		}
	}

	@Override
	public boolean interact(PlayerEntity player, Hand hand) {
		if (player.isSneaking()) {
			return false;
		} else {
			if (!this.world.isClient && this.ticksUnderwater < 60.0F) {
				player.ride(this);
			}

			return true;
		}
	}

	@Override
	protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPos) {
		this.fallVelocity = this.velocityY;
		if (!this.hasMount()) {
			if (onGround) {
				if (this.fallDistance > 3.0F) {
					if (this.location != BoatEntity.Location.ON_LAND) {
						this.fallDistance = 0.0F;
						return;
					}

					this.handleFallDamage(this.fallDistance, 1.0F);
					if (!this.world.isClient && !this.removed) {
						this.remove();
						if (this.world.getGameRules().getBoolean("doEntityDrops")) {
							for (int i = 0; i < 3; i++) {
								this.method_15560(this.getBoatType().method_11344());
							}

							for (int j = 0; j < 2; j++) {
								this.method_15560(Items.STICK);
							}
						}
					}
				}

				this.fallDistance = 0.0F;
			} else if (!this.world.getFluidState(new BlockPos(this).down()).matches(FluidTags.WATER) && heightDifference < 0.0) {
				this.fallDistance = (float)((double)this.fallDistance - heightDifference);
			}
		}
	}

	public boolean isPaddleMoving(int paddle) {
		return this.dataTracker.get(paddle == 0 ? field_17120 : field_17121) && this.getPrimaryPassenger() != null;
	}

	public void setDamageWobbleStrength(float wobbleStrength) {
		this.dataTracker.set(DAMAGE_WOBBLE_STRENGTH, wobbleStrength);
	}

	public float getDamageWobbleStrength() {
		return this.dataTracker.get(DAMAGE_WOBBLE_STRENGTH);
	}

	public void setBubbleWobbleTicks(int wobbleTicks) {
		this.dataTracker.set(DAMAGE_WOBBLE_TICKS, wobbleTicks);
	}

	public int getBubbleWobbleTicks() {
		return this.dataTracker.get(DAMAGE_WOBBLE_TICKS);
	}

	private void method_15961(int i) {
		this.dataTracker.set(field_17122, i);
	}

	private int method_15963() {
		return this.dataTracker.get(field_17122);
	}

	public float method_15960(float f) {
		return this.field_17119 + (this.field_17118 - this.field_17119) * f;
	}

	public void setDamageWobbleSide(int side) {
		this.dataTracker.set(DAMAGE_WOBBLE_SIDE, side);
	}

	public int getDamageWobbleSide() {
		return this.dataTracker.get(DAMAGE_WOBBLE_SIDE);
	}

	public void setBoatType(BoatEntity.Type type) {
		this.dataTracker.set(BOAT_TYPE, type.ordinal());
	}

	public BoatEntity.Type getBoatType() {
		return BoatEntity.Type.getType(this.dataTracker.get(BOAT_TYPE));
	}

	@Override
	protected boolean canAddPassenger(Entity passenger) {
		return this.getPassengerList().size() < 2 && !this.method_15567(FluidTags.WATER);
	}

	@Nullable
	@Override
	public Entity getPrimaryPassenger() {
		List<Entity> list = this.getPassengerList();
		return list.isEmpty() ? null : (Entity)list.get(0);
	}

	public void setInputs(boolean pressingLeft, boolean pressingRight, boolean pressingForward, boolean pressingBack) {
		this.pressingLeft = pressingLeft;
		this.pressingRight = pressingRight;
		this.pressingForward = pressingForward;
		this.pressingBack = pressingBack;
	}

	public static enum Location {
		IN_WATER,
		UNDER_WATER,
		UNDER_FLOWING_WATER,
		ON_LAND,
		IN_AIR;
	}

	public static enum Type {
		OAK(Blocks.OAK_PLANKS, "oak"),
		SPRUCE(Blocks.SPRUCE_PLANKS, "spruce"),
		BIRCH(Blocks.BIRCH_PLANKS, "birch"),
		JUNGLE(Blocks.JUNGLE_PLANKS, "jungle"),
		ACACIA(Blocks.ACACIA_PLANKS, "acacia"),
		DARK_OAK(Blocks.DARK_OAK_PLANKS, "dark_oak");

		private final String name;
		private final Block field_17123;

		private Type(Block block, String string2) {
			this.name = string2;
			this.field_17123 = block;
		}

		public String getName() {
			return this.name;
		}

		public Block method_11344() {
			return this.field_17123;
		}

		public String toString() {
			return this.name;
		}

		public static BoatEntity.Type getType(int type) {
			BoatEntity.Type[] types = values();
			if (type < 0 || type >= types.length) {
				type = 0;
			}

			return types[type];
		}

		public static BoatEntity.Type getType(String name) {
			BoatEntity.Type[] types = values();

			for (int i = 0; i < types.length; i++) {
				if (types[i].getName().equals(name)) {
					return types[i];
				}
			}

			return types[0];
		}
	}
}
