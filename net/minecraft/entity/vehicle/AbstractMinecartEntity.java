package net.minecraft.entity.vehicle;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.predicate.EntityPredicate;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Nameable;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class AbstractMinecartEntity extends Entity implements Nameable {
	private static final TrackedData<Integer> DAMAGE_WOBBLE_TICKS = DataTracker.registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> DAMAGE_WOBBLE_SIDE = DataTracker.registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Float> DAMAGE_WOBBLE_STRENGTH = DataTracker.registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Integer> CUSTOM_BLOCK_ID = DataTracker.registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> CUSTOM_BLOCK_OFFSET = DataTracker.registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> CUSTOM_BLOCK_PRESENT = DataTracker.registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private boolean yawFlipped;
	private static final int[][][] ADJACENT_RAIL_POSITIONS = new int[][][]{
		{{0, 0, -1}, {0, 0, 1}},
		{{-1, 0, 0}, {1, 0, 0}},
		{{-1, -1, 0}, {1, 0, 0}},
		{{-1, 0, 0}, {1, -1, 0}},
		{{0, 0, -1}, {0, -1, 1}},
		{{0, -1, -1}, {0, 0, 1}},
		{{0, 0, 1}, {1, 0, 0}},
		{{0, 0, 1}, {-1, 0, 0}},
		{{0, 0, -1}, {-1, 0, 0}},
		{{0, 0, -1}, {1, 0, 0}}
	};
	private int clientInterpolationSteps;
	private double clientX;
	private double clientY;
	private double clientZ;
	private double clientYaw;
	private double clientPitch;
	private double clientXVelocity;
	private double clientYVelocity;
	private double clientZVelocity;

	public AbstractMinecartEntity(World world) {
		super(world);
		this.inanimate = true;
		this.setBounds(0.98F, 0.7F);
	}

	public static AbstractMinecartEntity createMinecart(World world, double x, double y, double z, AbstractMinecartEntity.Type type) {
		switch (type) {
			case CHEST:
				return new ChestMinecartEntity(world, x, y, z);
			case FURNACE:
				return new FurnaceMinecartEntity(world, x, y, z);
			case TNT:
				return new TntMinecartEntity(world, x, y, z);
			case SPAWNER:
				return new SpawnerMinecartEntity(world, x, y, z);
			case HOPPER:
				return new HopperMinecartEntity(world, x, y, z);
			case COMMAND_BLOCK:
				return new CommandBlockMinecartEntity(world, x, y, z);
			default:
				return new MinecartEntity(world, x, y, z);
		}
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
		this.dataTracker.startTracking(CUSTOM_BLOCK_ID, 0);
		this.dataTracker.startTracking(CUSTOM_BLOCK_OFFSET, 6);
		this.dataTracker.startTracking(CUSTOM_BLOCK_PRESENT, false);
	}

	@Nullable
	@Override
	public Box getHardCollisionBox(Entity collidingEntity) {
		return collidingEntity.isPushable() ? collidingEntity.getBoundingBox() : null;
	}

	@Nullable
	@Override
	public Box getBox() {
		return null;
	}

	@Override
	public boolean isPushable() {
		return true;
	}

	public AbstractMinecartEntity(World world, double d, double e, double f) {
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
	public double getMountedHeightOffset() {
		return 0.0;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.world.isClient || this.removed) {
			return true;
		} else if (this.isInvulnerableTo(source)) {
			return false;
		} else {
			this.setDamageWobbleSide(-this.getDamageWobbleSide());
			this.setDamageWobbleTicks(10);
			this.scheduleVelocityUpdate();
			this.setDamageWobbleStrength(this.getDamageWobbleStrength() + amount * 10.0F);
			boolean bl = source.getAttacker() instanceof PlayerEntity && ((PlayerEntity)source.getAttacker()).abilities.creativeMode;
			if (bl || this.getDamageWobbleStrength() > 40.0F) {
				this.removeAllPassengers();
				if (bl && !this.hasCustomName()) {
					this.remove();
				} else {
					this.dropItems(source);
				}
			}

			return true;
		}
	}

	public void dropItems(DamageSource damageSource) {
		this.remove();
		if (this.world.getGameRules().getBoolean("doEntityDrops")) {
			ItemStack itemStack = new ItemStack(Items.MINECART, 1);
			if (this.hasCustomName()) {
				itemStack.setCustomName(this.getCustomName());
			}

			this.dropItem(itemStack, 0.0F);
		}
	}

	@Override
	public void animateDamage() {
		this.setDamageWobbleSide(-this.getDamageWobbleSide());
		this.setDamageWobbleTicks(10);
		this.setDamageWobbleStrength(this.getDamageWobbleStrength() + this.getDamageWobbleStrength() * 10.0F);
	}

	@Override
	public boolean collides() {
		return !this.removed;
	}

	@Override
	public Direction getMovementDirection() {
		return this.yawFlipped ? this.getHorizontalDirection().getOpposite().rotateYClockwise() : this.getHorizontalDirection().rotateYClockwise();
	}

	@Override
	public void tick() {
		if (this.getDamageWobbleTicks() > 0) {
			this.setDamageWobbleTicks(this.getDamageWobbleTicks() - 1);
		}

		if (this.getDamageWobbleStrength() > 0.0F) {
			this.setDamageWobbleStrength(this.getDamageWobbleStrength() - 1.0F);
		}

		if (this.y < -64.0) {
			this.destroy();
		}

		if (!this.world.isClient && this.world instanceof ServerWorld) {
			this.world.profiler.push("portal");
			MinecraftServer minecraftServer = this.world.getServer();
			int i = this.getMaxNetherPortalTime();
			if (this.changingDimension) {
				if (minecraftServer.isNetherAllowed()) {
					if (!this.hasMount() && this.netherPortalTime++ >= i) {
						this.netherPortalTime = i;
						this.netherPortalCooldown = this.getDefaultNetherPortalCooldown();
						int j;
						if (this.world.dimension.getDimensionType().getId() == -1) {
							j = 0;
						} else {
							j = -1;
						}

						this.changeDimension(j);
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

		if (this.world.isClient) {
			if (this.clientInterpolationSteps > 0) {
				double d = this.x + (this.clientX - this.x) / (double)this.clientInterpolationSteps;
				double e = this.y + (this.clientY - this.y) / (double)this.clientInterpolationSteps;
				double f = this.z + (this.clientZ - this.z) / (double)this.clientInterpolationSteps;
				double g = MathHelper.wrapDegrees(this.clientYaw - (double)this.yaw);
				this.yaw = (float)((double)this.yaw + g / (double)this.clientInterpolationSteps);
				this.pitch = (float)((double)this.pitch + (this.clientPitch - (double)this.pitch) / (double)this.clientInterpolationSteps);
				this.clientInterpolationSteps--;
				this.updatePosition(d, e, f);
				this.setRotation(this.yaw, this.pitch);
			} else {
				this.updatePosition(this.x, this.y, this.z);
				this.setRotation(this.yaw, this.pitch);
			}
		} else {
			this.prevX = this.x;
			this.prevY = this.y;
			this.prevZ = this.z;
			if (!this.hasNoGravity()) {
				this.velocityY -= 0.04F;
			}

			int l = MathHelper.floor(this.x);
			int m = MathHelper.floor(this.y);
			int n = MathHelper.floor(this.z);
			if (AbstractRailBlock.isRail(this.world, new BlockPos(l, m - 1, n))) {
				m--;
			}

			BlockPos blockPos = new BlockPos(l, m, n);
			BlockState blockState = this.world.getBlockState(blockPos);
			if (AbstractRailBlock.isRail(blockState)) {
				this.moveOnRail(blockPos, blockState);
				if (blockState.getBlock() == Blocks.ACTIVATOR_RAIL) {
					this.onActivatorRail(l, m, n, (Boolean)blockState.get(PoweredRailBlock.POWERED));
				}
			} else {
				this.moveOffRail();
			}

			this.checkBlockCollision();
			this.pitch = 0.0F;
			double h = this.prevX - this.x;
			double o = this.prevZ - this.z;
			if (h * h + o * o > 0.001) {
				this.yaw = (float)(MathHelper.atan2(o, h) * 180.0 / Math.PI);
				if (this.yawFlipped) {
					this.yaw += 180.0F;
				}
			}

			double p = (double)MathHelper.wrapDegrees(this.yaw - this.prevYaw);
			if (p < -170.0 || p >= 170.0) {
				this.yaw += 180.0F;
				this.yawFlipped = !this.yawFlipped;
			}

			this.setRotation(this.yaw, this.pitch);
			if (this.getMinecartType() == AbstractMinecartEntity.Type.RIDEABLE && this.velocityX * this.velocityX + this.velocityZ * this.velocityZ > 0.01) {
				List<Entity> list = this.world.getEntitiesIn(this, this.getBoundingBox().expand(0.2F, 0.0, 0.2F), EntityPredicate.method_13025(this));
				if (!list.isEmpty()) {
					for (int q = 0; q < list.size(); q++) {
						Entity entity = (Entity)list.get(q);
						if (!(entity instanceof PlayerEntity)
							&& !(entity instanceof IronGolemEntity)
							&& !(entity instanceof AbstractMinecartEntity)
							&& !this.hasPassengers()
							&& !entity.hasMount()) {
							entity.ride(this);
						} else {
							entity.pushAwayFrom(this);
						}
					}
				}
			} else {
				for (Entity entity2 : this.world.getEntitiesIn(this, this.getBoundingBox().expand(0.2F, 0.0, 0.2F))) {
					if (!this.hasPassenger(entity2) && entity2.isPushable() && entity2 instanceof AbstractMinecartEntity) {
						entity2.pushAwayFrom(this);
					}
				}
			}

			this.updateWaterState();
		}
	}

	protected double getMaxOffRailSpeed() {
		return 0.4;
	}

	public void onActivatorRail(int x, int y, int z, boolean powered) {
	}

	protected void moveOffRail() {
		double d = this.getMaxOffRailSpeed();
		this.velocityX = MathHelper.clamp(this.velocityX, -d, d);
		this.velocityZ = MathHelper.clamp(this.velocityZ, -d, d);
		if (this.onGround) {
			this.velocityX *= 0.5;
			this.velocityY *= 0.5;
			this.velocityZ *= 0.5;
		}

		this.move(MovementType.SELF, this.velocityX, this.velocityY, this.velocityZ);
		if (!this.onGround) {
			this.velocityX *= 0.95F;
			this.velocityY *= 0.95F;
			this.velocityZ *= 0.95F;
		}
	}

	protected void moveOnRail(BlockPos pos, BlockState state) {
		this.fallDistance = 0.0F;
		Vec3d vec3d = this.snapPositionToRail(this.x, this.y, this.z);
		this.y = (double)pos.getY();
		boolean bl = false;
		boolean bl2 = false;
		AbstractRailBlock abstractRailBlock = (AbstractRailBlock)state.getBlock();
		if (abstractRailBlock == Blocks.POWERED_RAIL) {
			bl = (Boolean)state.get(PoweredRailBlock.POWERED);
			bl2 = !bl;
		}

		double d = 0.0078125;
		AbstractRailBlock.RailShapeType railShapeType = state.get(abstractRailBlock.getShapeProperty());
		switch (railShapeType) {
			case ASCENDING_EAST:
				this.velocityX -= 0.0078125;
				this.y++;
				break;
			case ASCENDING_WEST:
				this.velocityX += 0.0078125;
				this.y++;
				break;
			case ASCENDING_NORTH:
				this.velocityZ += 0.0078125;
				this.y++;
				break;
			case ASCENDING_SOUTH:
				this.velocityZ -= 0.0078125;
				this.y++;
		}

		int[][] is = ADJACENT_RAIL_POSITIONS[railShapeType.getData()];
		double e = (double)(is[1][0] - is[0][0]);
		double f = (double)(is[1][2] - is[0][2]);
		double g = Math.sqrt(e * e + f * f);
		double h = this.velocityX * e + this.velocityZ * f;
		if (h < 0.0) {
			e = -e;
			f = -f;
		}

		double i = Math.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
		if (i > 2.0) {
			i = 2.0;
		}

		this.velocityX = i * e / g;
		this.velocityZ = i * f / g;
		Entity entity = this.getPassengerList().isEmpty() ? null : (Entity)this.getPassengerList().get(0);
		if (entity instanceof LivingEntity) {
			double j = (double)((LivingEntity)entity).forwardSpeed;
			if (j > 0.0) {
				double k = -Math.sin((double)(entity.yaw * (float) (Math.PI / 180.0)));
				double l = Math.cos((double)(entity.yaw * (float) (Math.PI / 180.0)));
				double m = this.velocityX * this.velocityX + this.velocityZ * this.velocityZ;
				if (m < 0.01) {
					this.velocityX += k * 0.1;
					this.velocityZ += l * 0.1;
					bl2 = false;
				}
			}
		}

		if (bl2) {
			double n = Math.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
			if (n < 0.03) {
				this.velocityX *= 0.0;
				this.velocityY *= 0.0;
				this.velocityZ *= 0.0;
			} else {
				this.velocityX *= 0.5;
				this.velocityY *= 0.0;
				this.velocityZ *= 0.5;
			}
		}

		double o = (double)pos.getX() + 0.5 + (double)is[0][0] * 0.5;
		double p = (double)pos.getZ() + 0.5 + (double)is[0][2] * 0.5;
		double q = (double)pos.getX() + 0.5 + (double)is[1][0] * 0.5;
		double r = (double)pos.getZ() + 0.5 + (double)is[1][2] * 0.5;
		e = q - o;
		f = r - p;
		double s;
		if (e == 0.0) {
			this.x = (double)pos.getX() + 0.5;
			s = this.z - (double)pos.getZ();
		} else if (f == 0.0) {
			this.z = (double)pos.getZ() + 0.5;
			s = this.x - (double)pos.getX();
		} else {
			double u = this.x - o;
			double v = this.z - p;
			s = (u * e + v * f) * 2.0;
		}

		this.x = o + e * s;
		this.z = p + f * s;
		this.updatePosition(this.x, this.y, this.z);
		double x = this.velocityX;
		double y = this.velocityZ;
		if (this.hasPassengers()) {
			x *= 0.75;
			y *= 0.75;
		}

		double z = this.getMaxOffRailSpeed();
		x = MathHelper.clamp(x, -z, z);
		y = MathHelper.clamp(y, -z, z);
		this.move(MovementType.SELF, x, 0.0, y);
		if (is[0][1] != 0 && MathHelper.floor(this.x) - pos.getX() == is[0][0] && MathHelper.floor(this.z) - pos.getZ() == is[0][2]) {
			this.updatePosition(this.x, this.y + (double)is[0][1], this.z);
		} else if (is[1][1] != 0 && MathHelper.floor(this.x) - pos.getX() == is[1][0] && MathHelper.floor(this.z) - pos.getZ() == is[1][2]) {
			this.updatePosition(this.x, this.y + (double)is[1][1], this.z);
		}

		this.applySlowdown();
		Vec3d vec3d2 = this.snapPositionToRail(this.x, this.y, this.z);
		if (vec3d2 != null && vec3d != null) {
			double aa = (vec3d.y - vec3d2.y) * 0.05;
			i = Math.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
			if (i > 0.0) {
				this.velocityX = this.velocityX / i * (i + aa);
				this.velocityZ = this.velocityZ / i * (i + aa);
			}

			this.updatePosition(this.x, vec3d2.y, this.z);
		}

		int ab = MathHelper.floor(this.x);
		int ac = MathHelper.floor(this.z);
		if (ab != pos.getX() || ac != pos.getZ()) {
			i = Math.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
			this.velocityX = i * (double)(ab - pos.getX());
			this.velocityZ = i * (double)(ac - pos.getZ());
		}

		if (bl) {
			double ad = Math.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
			if (ad > 0.01) {
				double ae = 0.06;
				this.velocityX = this.velocityX + this.velocityX / ad * 0.06;
				this.velocityZ = this.velocityZ + this.velocityZ / ad * 0.06;
			} else if (railShapeType == AbstractRailBlock.RailShapeType.EAST_WEST) {
				if (this.world.getBlockState(pos.west()).method_11734()) {
					this.velocityX = 0.02;
				} else if (this.world.getBlockState(pos.east()).method_11734()) {
					this.velocityX = -0.02;
				}
			} else if (railShapeType == AbstractRailBlock.RailShapeType.NORTH_SOUTH) {
				if (this.world.getBlockState(pos.north()).method_11734()) {
					this.velocityZ = 0.02;
				} else if (this.world.getBlockState(pos.south()).method_11734()) {
					this.velocityZ = -0.02;
				}
			}
		}
	}

	protected void applySlowdown() {
		if (this.hasPassengers()) {
			this.velocityX *= 0.997F;
			this.velocityY *= 0.0;
			this.velocityZ *= 0.997F;
		} else {
			this.velocityX *= 0.96F;
			this.velocityY *= 0.0;
			this.velocityZ *= 0.96F;
		}
	}

	@Override
	public void updatePosition(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		float f = this.width / 2.0F;
		float g = this.height;
		this.setBoundingBox(new Box(x - (double)f, y, z - (double)f, x + (double)f, y + (double)g, z + (double)f));
	}

	@Nullable
	public Vec3d snapPositionToRailWithOffset(double x, double y, double z, double offset) {
		int i = MathHelper.floor(x);
		int j = MathHelper.floor(y);
		int k = MathHelper.floor(z);
		if (AbstractRailBlock.isRail(this.world, new BlockPos(i, j - 1, k))) {
			j--;
		}

		BlockState blockState = this.world.getBlockState(new BlockPos(i, j, k));
		if (AbstractRailBlock.isRail(blockState)) {
			AbstractRailBlock.RailShapeType railShapeType = blockState.get(((AbstractRailBlock)blockState.getBlock()).getShapeProperty());
			y = (double)j;
			if (railShapeType.isAscending()) {
				y = (double)(j + 1);
			}

			int[][] is = ADJACENT_RAIL_POSITIONS[railShapeType.getData()];
			double d = (double)(is[1][0] - is[0][0]);
			double e = (double)(is[1][2] - is[0][2]);
			double f = Math.sqrt(d * d + e * e);
			d /= f;
			e /= f;
			x += d * offset;
			z += e * offset;
			if (is[0][1] != 0 && MathHelper.floor(x) - i == is[0][0] && MathHelper.floor(z) - k == is[0][2]) {
				y += (double)is[0][1];
			} else if (is[1][1] != 0 && MathHelper.floor(x) - i == is[1][0] && MathHelper.floor(z) - k == is[1][2]) {
				y += (double)is[1][1];
			}

			return this.snapPositionToRail(x, y, z);
		} else {
			return null;
		}
	}

	@Nullable
	public Vec3d snapPositionToRail(double x, double y, double z) {
		int i = MathHelper.floor(x);
		int j = MathHelper.floor(y);
		int k = MathHelper.floor(z);
		if (AbstractRailBlock.isRail(this.world, new BlockPos(i, j - 1, k))) {
			j--;
		}

		BlockState blockState = this.world.getBlockState(new BlockPos(i, j, k));
		if (AbstractRailBlock.isRail(blockState)) {
			AbstractRailBlock.RailShapeType railShapeType = blockState.get(((AbstractRailBlock)blockState.getBlock()).getShapeProperty());
			int[][] is = ADJACENT_RAIL_POSITIONS[railShapeType.getData()];
			double d = (double)i + 0.5 + (double)is[0][0] * 0.5;
			double e = (double)j + 0.0625 + (double)is[0][1] * 0.5;
			double f = (double)k + 0.5 + (double)is[0][2] * 0.5;
			double g = (double)i + 0.5 + (double)is[1][0] * 0.5;
			double h = (double)j + 0.0625 + (double)is[1][1] * 0.5;
			double l = (double)k + 0.5 + (double)is[1][2] * 0.5;
			double m = g - d;
			double n = (h - e) * 2.0;
			double o = l - f;
			double p;
			if (m == 0.0) {
				p = z - (double)k;
			} else if (o == 0.0) {
				p = x - (double)i;
			} else {
				double r = x - d;
				double s = z - f;
				p = (r * m + s * o) * 2.0;
			}

			x = d + m * p;
			y = e + n * p;
			z = f + o * p;
			if (n < 0.0) {
				y++;
			}

			if (n > 0.0) {
				y += 0.5;
			}

			return new Vec3d(x, y, z);
		} else {
			return null;
		}
	}

	@Override
	public Box getVisibilityBoundingBox() {
		Box box = this.getBoundingBox();
		return this.hasCustomBlock() ? box.expand((double)Math.abs(this.getBlockOffset()) / 16.0) : box;
	}

	public static void registerDataFixes(DataFixerUpper dataFixer, Class<?> class_) {
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		if (nbt.getBoolean("CustomDisplayTile")) {
			Block block;
			if (nbt.contains("DisplayTile", 8)) {
				block = Block.get(nbt.getString("DisplayTile"));
			} else {
				block = Block.getById(nbt.getInt("DisplayTile"));
			}

			int i = nbt.getInt("DisplayData");
			this.setCustomBlock(block == null ? Blocks.AIR.getDefaultState() : block.stateFromData(i));
			this.setCustomBlockOffset(nbt.getInt("DisplayOffset"));
		}
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		if (this.hasCustomBlock()) {
			nbt.putBoolean("CustomDisplayTile", true);
			BlockState blockState = this.getContainedBlock();
			Identifier identifier = Block.REGISTRY.getIdentifier(blockState.getBlock());
			nbt.putString("DisplayTile", identifier == null ? "" : identifier.toString());
			nbt.putInt("DisplayData", blockState.getBlock().getData(blockState));
			nbt.putInt("DisplayOffset", this.getBlockOffset());
		}
	}

	@Override
	public void pushAwayFrom(Entity entity) {
		if (!this.world.isClient) {
			if (!entity.noClip && !this.noClip) {
				if (!this.hasPassenger(entity)) {
					double d = entity.x - this.x;
					double e = entity.z - this.z;
					double f = d * d + e * e;
					if (f >= 1.0E-4F) {
						f = (double)MathHelper.sqrt(f);
						d /= f;
						e /= f;
						double g = 1.0 / f;
						if (g > 1.0) {
							g = 1.0;
						}

						d *= g;
						e *= g;
						d *= 0.1F;
						e *= 0.1F;
						d *= (double)(1.0F - this.pushSpeedReduction);
						e *= (double)(1.0F - this.pushSpeedReduction);
						d *= 0.5;
						e *= 0.5;
						if (entity instanceof AbstractMinecartEntity) {
							double h = entity.x - this.x;
							double i = entity.z - this.z;
							Vec3d vec3d = new Vec3d(h, 0.0, i).normalize();
							Vec3d vec3d2 = new Vec3d((double)MathHelper.cos(this.yaw * (float) (Math.PI / 180.0)), 0.0, (double)MathHelper.sin(this.yaw * (float) (Math.PI / 180.0)))
								.normalize();
							double j = Math.abs(vec3d.dotProduct(vec3d2));
							if (j < 0.8F) {
								return;
							}

							double k = entity.velocityX + this.velocityX;
							double l = entity.velocityZ + this.velocityZ;
							if (((AbstractMinecartEntity)entity).getMinecartType() == AbstractMinecartEntity.Type.FURNACE
								&& this.getMinecartType() != AbstractMinecartEntity.Type.FURNACE) {
								this.velocityX *= 0.2F;
								this.velocityZ *= 0.2F;
								this.addVelocity(entity.velocityX - d, 0.0, entity.velocityZ - e);
								entity.velocityX *= 0.95F;
								entity.velocityZ *= 0.95F;
							} else if (((AbstractMinecartEntity)entity).getMinecartType() != AbstractMinecartEntity.Type.FURNACE
								&& this.getMinecartType() == AbstractMinecartEntity.Type.FURNACE) {
								entity.velocityX *= 0.2F;
								entity.velocityZ *= 0.2F;
								entity.addVelocity(this.velocityX + d, 0.0, this.velocityZ + e);
								this.velocityX *= 0.95F;
								this.velocityZ *= 0.95F;
							} else {
								k /= 2.0;
								l /= 2.0;
								this.velocityX *= 0.2F;
								this.velocityZ *= 0.2F;
								this.addVelocity(k - d, 0.0, l - e);
								entity.velocityX *= 0.2F;
								entity.velocityZ *= 0.2F;
								entity.addVelocity(k + d, 0.0, l + e);
							}
						} else {
							this.addVelocity(-d, 0.0, -e);
							entity.addVelocity(d / 4.0, 0.0, e / 4.0);
						}
					}
				}
			}
		}
	}

	@Override
	public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
		this.clientX = x;
		this.clientY = y;
		this.clientZ = z;
		this.clientYaw = (double)yaw;
		this.clientPitch = (double)pitch;
		this.clientInterpolationSteps = interpolationSteps + 2;
		this.velocityX = this.clientXVelocity;
		this.velocityY = this.clientYVelocity;
		this.velocityZ = this.clientZVelocity;
	}

	@Override
	public void setVelocityClient(double x, double y, double z) {
		this.velocityX = x;
		this.velocityY = y;
		this.velocityZ = z;
		this.clientXVelocity = this.velocityX;
		this.clientYVelocity = this.velocityY;
		this.clientZVelocity = this.velocityZ;
	}

	public void setDamageWobbleStrength(float damageWobbleStrength) {
		this.dataTracker.set(DAMAGE_WOBBLE_STRENGTH, damageWobbleStrength);
	}

	public float getDamageWobbleStrength() {
		return this.dataTracker.get(DAMAGE_WOBBLE_STRENGTH);
	}

	public void setDamageWobbleTicks(int wobbleTicks) {
		this.dataTracker.set(DAMAGE_WOBBLE_TICKS, wobbleTicks);
	}

	public int getDamageWobbleTicks() {
		return this.dataTracker.get(DAMAGE_WOBBLE_TICKS);
	}

	public void setDamageWobbleSide(int wobbleSide) {
		this.dataTracker.set(DAMAGE_WOBBLE_SIDE, wobbleSide);
	}

	public int getDamageWobbleSide() {
		return this.dataTracker.get(DAMAGE_WOBBLE_SIDE);
	}

	public abstract AbstractMinecartEntity.Type getMinecartType();

	public BlockState getContainedBlock() {
		return !this.hasCustomBlock() ? this.getDefaultContainedBlock() : Block.getStateFromRawId(this.getDataTracker().get(CUSTOM_BLOCK_ID));
	}

	public BlockState getDefaultContainedBlock() {
		return Blocks.AIR.getDefaultState();
	}

	public int getBlockOffset() {
		return !this.hasCustomBlock() ? this.getDefaultBlockOffset() : this.getDataTracker().get(CUSTOM_BLOCK_OFFSET);
	}

	public int getDefaultBlockOffset() {
		return 6;
	}

	public void setCustomBlock(BlockState state) {
		this.getDataTracker().set(CUSTOM_BLOCK_ID, Block.getByBlockState(state));
		this.setCustomBlockPresent(true);
	}

	public void setCustomBlockOffset(int offset) {
		this.getDataTracker().set(CUSTOM_BLOCK_OFFSET, offset);
		this.setCustomBlockPresent(true);
	}

	public boolean hasCustomBlock() {
		return this.getDataTracker().get(CUSTOM_BLOCK_PRESENT);
	}

	public void setCustomBlockPresent(boolean present) {
		this.getDataTracker().set(CUSTOM_BLOCK_PRESENT, present);
	}

	public static enum Type {
		RIDEABLE(0, "MinecartRideable"),
		CHEST(1, "MinecartChest"),
		FURNACE(2, "MinecartFurnace"),
		TNT(3, "MinecartTNT"),
		SPAWNER(4, "MinecartSpawner"),
		HOPPER(5, "MinecartHopper"),
		COMMAND_BLOCK(6, "MinecartCommandBlock");

		private static final Map<Integer, AbstractMinecartEntity.Type> TYPES = Maps.newHashMap();
		private final int id;
		private final String name;

		private Type(int j, String string2) {
			this.id = j;
			this.name = string2;
		}

		public int getId() {
			return this.id;
		}

		public String getName() {
			return this.name;
		}

		public static AbstractMinecartEntity.Type getById(int id) {
			AbstractMinecartEntity.Type type = (AbstractMinecartEntity.Type)TYPES.get(id);
			return type == null ? RIDEABLE : type;
		}

		static {
			for (AbstractMinecartEntity.Type type : values()) {
				TYPES.put(type.getId(), type);
			}
		}
	}
}
