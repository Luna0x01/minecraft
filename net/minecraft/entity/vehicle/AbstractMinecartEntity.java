package net.minecraft.entity.vehicle;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.block.enums.RailShape;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.Packet;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public abstract class AbstractMinecartEntity extends Entity {
	private static final TrackedData<Integer> DAMAGE_WOBBLE_TICKS = DataTracker.registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> DAMAGE_WOBBLE_SIDE = DataTracker.registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Float> DAMAGE_WOBBLE_STRENGTH = DataTracker.registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Integer> CUSTOM_BLOCK_ID = DataTracker.registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> CUSTOM_BLOCK_OFFSET = DataTracker.registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> CUSTOM_BLOCK_PRESENT = DataTracker.registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private boolean field_7660;
	private static final Map<RailShape, Pair<Vec3i, Vec3i>> field_7664 = Util.make(Maps.newEnumMap(RailShape.class), enumMap -> {
		Vec3i vec3i = Direction.field_11039.getVector();
		Vec3i vec3i2 = Direction.field_11034.getVector();
		Vec3i vec3i3 = Direction.field_11043.getVector();
		Vec3i vec3i4 = Direction.field_11035.getVector();
		Vec3i vec3i5 = vec3i.down();
		Vec3i vec3i6 = vec3i2.down();
		Vec3i vec3i7 = vec3i3.down();
		Vec3i vec3i8 = vec3i4.down();
		enumMap.put(RailShape.field_12665, Pair.of(vec3i3, vec3i4));
		enumMap.put(RailShape.field_12674, Pair.of(vec3i, vec3i2));
		enumMap.put(RailShape.field_12667, Pair.of(vec3i5, vec3i2));
		enumMap.put(RailShape.field_12666, Pair.of(vec3i, vec3i6));
		enumMap.put(RailShape.field_12670, Pair.of(vec3i3, vec3i8));
		enumMap.put(RailShape.field_12668, Pair.of(vec3i7, vec3i4));
		enumMap.put(RailShape.field_12664, Pair.of(vec3i4, vec3i2));
		enumMap.put(RailShape.field_12671, Pair.of(vec3i4, vec3i));
		enumMap.put(RailShape.field_12672, Pair.of(vec3i3, vec3i));
		enumMap.put(RailShape.field_12663, Pair.of(vec3i3, vec3i2));
	});
	private int clientInterpolationSteps;
	private double clientX;
	private double clientY;
	private double clientZ;
	private double clientYaw;
	private double clientPitch;
	private double clientXVelocity;
	private double clientYVelocity;
	private double clientZVelocity;

	protected AbstractMinecartEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
		this.inanimate = true;
	}

	protected AbstractMinecartEntity(EntityType<?> entityType, World world, double d, double e, double f) {
		this(entityType, world);
		this.updatePosition(d, e, f);
		this.setVelocity(Vec3d.ZERO);
		this.prevX = d;
		this.prevY = e;
		this.prevZ = f;
	}

	public static AbstractMinecartEntity create(World world, double d, double e, double f, AbstractMinecartEntity.Type type) {
		if (type == AbstractMinecartEntity.Type.field_7678) {
			return new ChestMinecartEntity(world, d, e, f);
		} else if (type == AbstractMinecartEntity.Type.field_7679) {
			return new FurnaceMinecartEntity(world, d, e, f);
		} else if (type == AbstractMinecartEntity.Type.field_7675) {
			return new TntMinecartEntity(world, d, e, f);
		} else if (type == AbstractMinecartEntity.Type.field_7680) {
			return new SpawnerMinecartEntity(world, d, e, f);
		} else if (type == AbstractMinecartEntity.Type.field_7677) {
			return new HopperMinecartEntity(world, d, e, f);
		} else {
			return (AbstractMinecartEntity)(type == AbstractMinecartEntity.Type.field_7681
				? new CommandBlockMinecartEntity(world, d, e, f)
				: new MinecartEntity(world, d, e, f));
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
		this.dataTracker.startTracking(CUSTOM_BLOCK_ID, Block.getRawIdFromState(Blocks.field_10124.getDefaultState()));
		this.dataTracker.startTracking(CUSTOM_BLOCK_OFFSET, 6);
		this.dataTracker.startTracking(CUSTOM_BLOCK_PRESENT, false);
	}

	@Nullable
	@Override
	public Box getHardCollisionBox(Entity entity) {
		return entity.isPushable() ? entity.getBoundingBox() : null;
	}

	@Override
	public boolean isPushable() {
		return true;
	}

	@Override
	public double getMountedHeightOffset() {
		return 0.0;
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		if (this.world.isClient || this.removed) {
			return true;
		} else if (this.isInvulnerableTo(damageSource)) {
			return false;
		} else {
			this.setDamageWobbleSide(-this.getDamageWobbleSide());
			this.setDamageWobbleTicks(10);
			this.scheduleVelocityUpdate();
			this.setDamageWobbleStrength(this.getDamageWobbleStrength() + f * 10.0F);
			boolean bl = damageSource.getAttacker() instanceof PlayerEntity && ((PlayerEntity)damageSource.getAttacker()).abilities.creativeMode;
			if (bl || this.getDamageWobbleStrength() > 40.0F) {
				this.removeAllPassengers();
				if (bl && !this.hasCustomName()) {
					this.remove();
				} else {
					this.dropItems(damageSource);
				}
			}

			return true;
		}
	}

	public void dropItems(DamageSource damageSource) {
		this.remove();
		if (this.world.getGameRules().getBoolean(GameRules.field_19393)) {
			ItemStack itemStack = new ItemStack(Items.field_8045);
			if (this.hasCustomName()) {
				itemStack.setCustomName(this.getCustomName());
			}

			this.dropStack(itemStack);
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

	private static Pair<Vec3i, Vec3i> method_22864(RailShape railShape) {
		return (Pair<Vec3i, Vec3i>)field_7664.get(railShape);
	}

	@Override
	public Direction getMovementDirection() {
		return this.field_7660 ? this.getHorizontalFacing().getOpposite().rotateYClockwise() : this.getHorizontalFacing().rotateYClockwise();
	}

	@Override
	public void tick() {
		if (this.getDamageWobbleTicks() > 0) {
			this.setDamageWobbleTicks(this.getDamageWobbleTicks() - 1);
		}

		if (this.getDamageWobbleStrength() > 0.0F) {
			this.setDamageWobbleStrength(this.getDamageWobbleStrength() - 1.0F);
		}

		if (this.getY() < -64.0) {
			this.destroy();
		}

		this.tickNetherPortal();
		if (this.world.isClient) {
			if (this.clientInterpolationSteps > 0) {
				double d = this.getX() + (this.clientX - this.getX()) / (double)this.clientInterpolationSteps;
				double e = this.getY() + (this.clientY - this.getY()) / (double)this.clientInterpolationSteps;
				double f = this.getZ() + (this.clientZ - this.getZ()) / (double)this.clientInterpolationSteps;
				double g = MathHelper.wrapDegrees(this.clientYaw - (double)this.yaw);
				this.yaw = (float)((double)this.yaw + g / (double)this.clientInterpolationSteps);
				this.pitch = (float)((double)this.pitch + (this.clientPitch - (double)this.pitch) / (double)this.clientInterpolationSteps);
				this.clientInterpolationSteps--;
				this.updatePosition(d, e, f);
				this.setRotation(this.yaw, this.pitch);
			} else {
				this.refreshPosition();
				this.setRotation(this.yaw, this.pitch);
			}
		} else {
			if (!this.hasNoGravity()) {
				this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
			}

			int i = MathHelper.floor(this.getX());
			int j = MathHelper.floor(this.getY());
			int k = MathHelper.floor(this.getZ());
			if (this.world.getBlockState(new BlockPos(i, j - 1, k)).matches(BlockTags.field_15463)) {
				j--;
			}

			BlockPos blockPos = new BlockPos(i, j, k);
			BlockState blockState = this.world.getBlockState(blockPos);
			if (blockState.matches(BlockTags.field_15463)) {
				this.moveOnRail(blockPos, blockState);
				if (blockState.getBlock() == Blocks.field_10546) {
					this.onActivatorRail(i, j, k, (Boolean)blockState.get(PoweredRailBlock.POWERED));
				}
			} else {
				this.moveOffRail();
			}

			this.checkBlockCollision();
			this.pitch = 0.0F;
			double h = this.prevX - this.getX();
			double l = this.prevZ - this.getZ();
			if (h * h + l * l > 0.001) {
				this.yaw = (float)(MathHelper.atan2(l, h) * 180.0 / Math.PI);
				if (this.field_7660) {
					this.yaw += 180.0F;
				}
			}

			double m = (double)MathHelper.wrapDegrees(this.yaw - this.prevYaw);
			if (m < -170.0 || m >= 170.0) {
				this.yaw += 180.0F;
				this.field_7660 = !this.field_7660;
			}

			this.setRotation(this.yaw, this.pitch);
			if (this.getMinecartType() == AbstractMinecartEntity.Type.field_7674 && squaredHorizontalLength(this.getVelocity()) > 0.01) {
				List<Entity> list = this.world.getEntities(this, this.getBoundingBox().expand(0.2F, 0.0, 0.2F), EntityPredicates.canBePushedBy(this));
				if (!list.isEmpty()) {
					for (int n = 0; n < list.size(); n++) {
						Entity entity = (Entity)list.get(n);
						if (!(entity instanceof PlayerEntity)
							&& !(entity instanceof IronGolemEntity)
							&& !(entity instanceof AbstractMinecartEntity)
							&& !this.hasPassengers()
							&& !entity.hasVehicle()) {
							entity.startRiding(this);
						} else {
							entity.pushAwayFrom(this);
						}
					}
				}
			} else {
				for (Entity entity2 : this.world.getEntities(this, this.getBoundingBox().expand(0.2F, 0.0, 0.2F))) {
					if (!this.hasPassenger(entity2) && entity2.isPushable() && entity2 instanceof AbstractMinecartEntity) {
						entity2.pushAwayFrom(this);
					}
				}
			}

			this.checkWaterState();
		}
	}

	protected double getMaxOffRailSpeed() {
		return 0.4;
	}

	public void onActivatorRail(int i, int j, int k, boolean bl) {
	}

	protected void moveOffRail() {
		double d = this.getMaxOffRailSpeed();
		Vec3d vec3d = this.getVelocity();
		this.setVelocity(MathHelper.clamp(vec3d.x, -d, d), vec3d.y, MathHelper.clamp(vec3d.z, -d, d));
		if (this.onGround) {
			this.setVelocity(this.getVelocity().multiply(0.5));
		}

		this.move(MovementType.field_6308, this.getVelocity());
		if (!this.onGround) {
			this.setVelocity(this.getVelocity().multiply(0.95));
		}
	}

	protected void moveOnRail(BlockPos blockPos, BlockState blockState) {
		this.fallDistance = 0.0F;
		double d = this.getX();
		double e = this.getY();
		double f = this.getZ();
		Vec3d vec3d = this.method_7508(d, e, f);
		e = (double)blockPos.getY();
		boolean bl = false;
		boolean bl2 = false;
		AbstractRailBlock abstractRailBlock = (AbstractRailBlock)blockState.getBlock();
		if (abstractRailBlock == Blocks.field_10425) {
			bl = (Boolean)blockState.get(PoweredRailBlock.POWERED);
			bl2 = !bl;
		}

		double g = 0.0078125;
		Vec3d vec3d2 = this.getVelocity();
		RailShape railShape = blockState.get(abstractRailBlock.getShapeProperty());
		switch (railShape) {
			case field_12667:
				this.setVelocity(vec3d2.add(-0.0078125, 0.0, 0.0));
				e++;
				break;
			case field_12666:
				this.setVelocity(vec3d2.add(0.0078125, 0.0, 0.0));
				e++;
				break;
			case field_12670:
				this.setVelocity(vec3d2.add(0.0, 0.0, 0.0078125));
				e++;
				break;
			case field_12668:
				this.setVelocity(vec3d2.add(0.0, 0.0, -0.0078125));
				e++;
		}

		vec3d2 = this.getVelocity();
		Pair<Vec3i, Vec3i> pair = method_22864(railShape);
		Vec3i vec3i = (Vec3i)pair.getFirst();
		Vec3i vec3i2 = (Vec3i)pair.getSecond();
		double h = (double)(vec3i2.getX() - vec3i.getX());
		double i = (double)(vec3i2.getZ() - vec3i.getZ());
		double j = Math.sqrt(h * h + i * i);
		double k = vec3d2.x * h + vec3d2.z * i;
		if (k < 0.0) {
			h = -h;
			i = -i;
		}

		double l = Math.min(2.0, Math.sqrt(squaredHorizontalLength(vec3d2)));
		vec3d2 = new Vec3d(l * h / j, vec3d2.y, l * i / j);
		this.setVelocity(vec3d2);
		Entity entity = this.getPassengerList().isEmpty() ? null : (Entity)this.getPassengerList().get(0);
		if (entity instanceof PlayerEntity) {
			Vec3d vec3d3 = entity.getVelocity();
			double m = squaredHorizontalLength(vec3d3);
			double n = squaredHorizontalLength(this.getVelocity());
			if (m > 1.0E-4 && n < 0.01) {
				this.setVelocity(this.getVelocity().add(vec3d3.x * 0.1, 0.0, vec3d3.z * 0.1));
				bl2 = false;
			}
		}

		if (bl2) {
			double o = Math.sqrt(squaredHorizontalLength(this.getVelocity()));
			if (o < 0.03) {
				this.setVelocity(Vec3d.ZERO);
			} else {
				this.setVelocity(this.getVelocity().multiply(0.5, 0.0, 0.5));
			}
		}

		double p = (double)blockPos.getX() + 0.5 + (double)vec3i.getX() * 0.5;
		double q = (double)blockPos.getZ() + 0.5 + (double)vec3i.getZ() * 0.5;
		double r = (double)blockPos.getX() + 0.5 + (double)vec3i2.getX() * 0.5;
		double s = (double)blockPos.getZ() + 0.5 + (double)vec3i2.getZ() * 0.5;
		h = r - p;
		i = s - q;
		double t;
		if (h == 0.0) {
			t = f - (double)blockPos.getZ();
		} else if (i == 0.0) {
			t = d - (double)blockPos.getX();
		} else {
			double v = d - p;
			double w = f - q;
			t = (v * h + w * i) * 2.0;
		}

		d = p + h * t;
		f = q + i * t;
		this.updatePosition(d, e, f);
		double y = this.hasPassengers() ? 0.75 : 1.0;
		double z = this.getMaxOffRailSpeed();
		vec3d2 = this.getVelocity();
		this.move(MovementType.field_6308, new Vec3d(MathHelper.clamp(y * vec3d2.x, -z, z), 0.0, MathHelper.clamp(y * vec3d2.z, -z, z)));
		if (vec3i.getY() != 0 && MathHelper.floor(this.getX()) - blockPos.getX() == vec3i.getX() && MathHelper.floor(this.getZ()) - blockPos.getZ() == vec3i.getZ()) {
			this.updatePosition(this.getX(), this.getY() + (double)vec3i.getY(), this.getZ());
		} else if (vec3i2.getY() != 0
			&& MathHelper.floor(this.getX()) - blockPos.getX() == vec3i2.getX()
			&& MathHelper.floor(this.getZ()) - blockPos.getZ() == vec3i2.getZ()) {
			this.updatePosition(this.getX(), this.getY() + (double)vec3i2.getY(), this.getZ());
		}

		this.applySlowdown();
		Vec3d vec3d4 = this.method_7508(this.getX(), this.getY(), this.getZ());
		if (vec3d4 != null && vec3d != null) {
			double aa = (vec3d.y - vec3d4.y) * 0.05;
			Vec3d vec3d5 = this.getVelocity();
			double ab = Math.sqrt(squaredHorizontalLength(vec3d5));
			if (ab > 0.0) {
				this.setVelocity(vec3d5.multiply((ab + aa) / ab, 1.0, (ab + aa) / ab));
			}

			this.updatePosition(this.getX(), vec3d4.y, this.getZ());
		}

		int ac = MathHelper.floor(this.getX());
		int ad = MathHelper.floor(this.getZ());
		if (ac != blockPos.getX() || ad != blockPos.getZ()) {
			Vec3d vec3d6 = this.getVelocity();
			double ae = Math.sqrt(squaredHorizontalLength(vec3d6));
			this.setVelocity(ae * (double)(ac - blockPos.getX()), vec3d6.y, ae * (double)(ad - blockPos.getZ()));
		}

		if (bl) {
			Vec3d vec3d7 = this.getVelocity();
			double af = Math.sqrt(squaredHorizontalLength(vec3d7));
			if (af > 0.01) {
				double ag = 0.06;
				this.setVelocity(vec3d7.add(vec3d7.x / af * 0.06, 0.0, vec3d7.z / af * 0.06));
			} else {
				Vec3d vec3d8 = this.getVelocity();
				double ah = vec3d8.x;
				double ai = vec3d8.z;
				if (railShape == RailShape.field_12674) {
					if (this.willHitBlockAt(blockPos.west())) {
						ah = 0.02;
					} else if (this.willHitBlockAt(blockPos.east())) {
						ah = -0.02;
					}
				} else {
					if (railShape != RailShape.field_12665) {
						return;
					}

					if (this.willHitBlockAt(blockPos.north())) {
						ai = 0.02;
					} else if (this.willHitBlockAt(blockPos.south())) {
						ai = -0.02;
					}
				}

				this.setVelocity(ah, vec3d8.y, ai);
			}
		}
	}

	private boolean willHitBlockAt(BlockPos blockPos) {
		return this.world.getBlockState(blockPos).isSimpleFullBlock(this.world, blockPos);
	}

	protected void applySlowdown() {
		double d = this.hasPassengers() ? 0.997 : 0.96;
		this.setVelocity(this.getVelocity().multiply(d, 0.0, d));
	}

	@Nullable
	public Vec3d method_7505(double d, double e, double f, double g) {
		int i = MathHelper.floor(d);
		int j = MathHelper.floor(e);
		int k = MathHelper.floor(f);
		if (this.world.getBlockState(new BlockPos(i, j - 1, k)).matches(BlockTags.field_15463)) {
			j--;
		}

		BlockState blockState = this.world.getBlockState(new BlockPos(i, j, k));
		if (blockState.matches(BlockTags.field_15463)) {
			RailShape railShape = blockState.get(((AbstractRailBlock)blockState.getBlock()).getShapeProperty());
			e = (double)j;
			if (railShape.isAscending()) {
				e = (double)(j + 1);
			}

			Pair<Vec3i, Vec3i> pair = method_22864(railShape);
			Vec3i vec3i = (Vec3i)pair.getFirst();
			Vec3i vec3i2 = (Vec3i)pair.getSecond();
			double h = (double)(vec3i2.getX() - vec3i.getX());
			double l = (double)(vec3i2.getZ() - vec3i.getZ());
			double m = Math.sqrt(h * h + l * l);
			h /= m;
			l /= m;
			d += h * g;
			f += l * g;
			if (vec3i.getY() != 0 && MathHelper.floor(d) - i == vec3i.getX() && MathHelper.floor(f) - k == vec3i.getZ()) {
				e += (double)vec3i.getY();
			} else if (vec3i2.getY() != 0 && MathHelper.floor(d) - i == vec3i2.getX() && MathHelper.floor(f) - k == vec3i2.getZ()) {
				e += (double)vec3i2.getY();
			}

			return this.method_7508(d, e, f);
		} else {
			return null;
		}
	}

	@Nullable
	public Vec3d method_7508(double d, double e, double f) {
		int i = MathHelper.floor(d);
		int j = MathHelper.floor(e);
		int k = MathHelper.floor(f);
		if (this.world.getBlockState(new BlockPos(i, j - 1, k)).matches(BlockTags.field_15463)) {
			j--;
		}

		BlockState blockState = this.world.getBlockState(new BlockPos(i, j, k));
		if (blockState.matches(BlockTags.field_15463)) {
			RailShape railShape = blockState.get(((AbstractRailBlock)blockState.getBlock()).getShapeProperty());
			Pair<Vec3i, Vec3i> pair = method_22864(railShape);
			Vec3i vec3i = (Vec3i)pair.getFirst();
			Vec3i vec3i2 = (Vec3i)pair.getSecond();
			double g = (double)i + 0.5 + (double)vec3i.getX() * 0.5;
			double h = (double)j + 0.0625 + (double)vec3i.getY() * 0.5;
			double l = (double)k + 0.5 + (double)vec3i.getZ() * 0.5;
			double m = (double)i + 0.5 + (double)vec3i2.getX() * 0.5;
			double n = (double)j + 0.0625 + (double)vec3i2.getY() * 0.5;
			double o = (double)k + 0.5 + (double)vec3i2.getZ() * 0.5;
			double p = m - g;
			double q = (n - h) * 2.0;
			double r = o - l;
			double s;
			if (p == 0.0) {
				s = f - (double)k;
			} else if (r == 0.0) {
				s = d - (double)i;
			} else {
				double u = d - g;
				double v = f - l;
				s = (u * p + v * r) * 2.0;
			}

			d = g + p * s;
			e = h + q * s;
			f = l + r * s;
			if (q < 0.0) {
				e++;
			} else if (q > 0.0) {
				e += 0.5;
			}

			return new Vec3d(d, e, f);
		} else {
			return null;
		}
	}

	@Override
	public Box getVisibilityBoundingBox() {
		Box box = this.getBoundingBox();
		return this.hasCustomBlock() ? box.expand((double)Math.abs(this.getBlockOffset()) / 16.0) : box;
	}

	@Override
	protected void readCustomDataFromTag(CompoundTag compoundTag) {
		if (compoundTag.getBoolean("CustomDisplayTile")) {
			this.setCustomBlock(NbtHelper.toBlockState(compoundTag.getCompound("DisplayState")));
			this.setCustomBlockOffset(compoundTag.getInt("DisplayOffset"));
		}
	}

	@Override
	protected void writeCustomDataToTag(CompoundTag compoundTag) {
		if (this.hasCustomBlock()) {
			compoundTag.putBoolean("CustomDisplayTile", true);
			compoundTag.put("DisplayState", NbtHelper.fromBlockState(this.getContainedBlock()));
			compoundTag.putInt("DisplayOffset", this.getBlockOffset());
		}
	}

	@Override
	public void pushAwayFrom(Entity entity) {
		if (!this.world.isClient) {
			if (!entity.noClip && !this.noClip) {
				if (!this.hasPassenger(entity)) {
					double d = entity.getX() - this.getX();
					double e = entity.getZ() - this.getZ();
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
							double h = entity.getX() - this.getX();
							double i = entity.getZ() - this.getZ();
							Vec3d vec3d = new Vec3d(h, 0.0, i).normalize();
							Vec3d vec3d2 = new Vec3d((double)MathHelper.cos(this.yaw * (float) (Math.PI / 180.0)), 0.0, (double)MathHelper.sin(this.yaw * (float) (Math.PI / 180.0)))
								.normalize();
							double j = Math.abs(vec3d.dotProduct(vec3d2));
							if (j < 0.8F) {
								return;
							}

							Vec3d vec3d3 = this.getVelocity();
							Vec3d vec3d4 = entity.getVelocity();
							if (((AbstractMinecartEntity)entity).getMinecartType() == AbstractMinecartEntity.Type.field_7679
								&& this.getMinecartType() != AbstractMinecartEntity.Type.field_7679) {
								this.setVelocity(vec3d3.multiply(0.2, 1.0, 0.2));
								this.addVelocity(vec3d4.x - d, 0.0, vec3d4.z - e);
								entity.setVelocity(vec3d4.multiply(0.95, 1.0, 0.95));
							} else if (((AbstractMinecartEntity)entity).getMinecartType() != AbstractMinecartEntity.Type.field_7679
								&& this.getMinecartType() == AbstractMinecartEntity.Type.field_7679) {
								entity.setVelocity(vec3d4.multiply(0.2, 1.0, 0.2));
								entity.addVelocity(vec3d3.x + d, 0.0, vec3d3.z + e);
								this.setVelocity(vec3d3.multiply(0.95, 1.0, 0.95));
							} else {
								double k = (vec3d4.x + vec3d3.x) / 2.0;
								double l = (vec3d4.z + vec3d3.z) / 2.0;
								this.setVelocity(vec3d3.multiply(0.2, 1.0, 0.2));
								this.addVelocity(k - d, 0.0, l - e);
								entity.setVelocity(vec3d4.multiply(0.2, 1.0, 0.2));
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
	public void updateTrackedPositionAndAngles(double d, double e, double f, float g, float h, int i, boolean bl) {
		this.clientX = d;
		this.clientY = e;
		this.clientZ = f;
		this.clientYaw = (double)g;
		this.clientPitch = (double)h;
		this.clientInterpolationSteps = i + 2;
		this.setVelocity(this.clientXVelocity, this.clientYVelocity, this.clientZVelocity);
	}

	@Override
	public void setVelocityClient(double d, double e, double f) {
		this.clientXVelocity = d;
		this.clientYVelocity = e;
		this.clientZVelocity = f;
		this.setVelocity(this.clientXVelocity, this.clientYVelocity, this.clientZVelocity);
	}

	public void setDamageWobbleStrength(float f) {
		this.dataTracker.set(DAMAGE_WOBBLE_STRENGTH, f);
	}

	public float getDamageWobbleStrength() {
		return this.dataTracker.get(DAMAGE_WOBBLE_STRENGTH);
	}

	public void setDamageWobbleTicks(int i) {
		this.dataTracker.set(DAMAGE_WOBBLE_TICKS, i);
	}

	public int getDamageWobbleTicks() {
		return this.dataTracker.get(DAMAGE_WOBBLE_TICKS);
	}

	public void setDamageWobbleSide(int i) {
		this.dataTracker.set(DAMAGE_WOBBLE_SIDE, i);
	}

	public int getDamageWobbleSide() {
		return this.dataTracker.get(DAMAGE_WOBBLE_SIDE);
	}

	public abstract AbstractMinecartEntity.Type getMinecartType();

	public BlockState getContainedBlock() {
		return !this.hasCustomBlock() ? this.getDefaultContainedBlock() : Block.getStateFromRawId(this.getDataTracker().get(CUSTOM_BLOCK_ID));
	}

	public BlockState getDefaultContainedBlock() {
		return Blocks.field_10124.getDefaultState();
	}

	public int getBlockOffset() {
		return !this.hasCustomBlock() ? this.getDefaultBlockOffset() : this.getDataTracker().get(CUSTOM_BLOCK_OFFSET);
	}

	public int getDefaultBlockOffset() {
		return 6;
	}

	public void setCustomBlock(BlockState blockState) {
		this.getDataTracker().set(CUSTOM_BLOCK_ID, Block.getRawIdFromState(blockState));
		this.setCustomBlockPresent(true);
	}

	public void setCustomBlockOffset(int i) {
		this.getDataTracker().set(CUSTOM_BLOCK_OFFSET, i);
		this.setCustomBlockPresent(true);
	}

	public boolean hasCustomBlock() {
		return this.getDataTracker().get(CUSTOM_BLOCK_PRESENT);
	}

	public void setCustomBlockPresent(boolean bl) {
		this.getDataTracker().set(CUSTOM_BLOCK_PRESENT, bl);
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this);
	}

	public static enum Type {
		field_7674,
		field_7678,
		field_7679,
		field_7675,
		field_7680,
		field_7677,
		field_7681;
	}
}
