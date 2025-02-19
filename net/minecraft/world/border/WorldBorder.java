package net.minecraft.world.border;

import com.google.common.collect.Lists;
import com.mojang.serialization.DynamicLike;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Util;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class WorldBorder {
	public static final double field_33643 = 5.999997E7F;
	private final List<WorldBorderListener> listeners = Lists.newArrayList();
	private double damagePerBlock = 0.2;
	private double safeZone = 5.0;
	private int warningTime = 15;
	private int warningBlocks = 5;
	private double centerX;
	private double centerZ;
	int maxRadius = 29999984;
	private WorldBorder.Area area = new WorldBorder.StaticArea(5.999997E7F);
	public static final WorldBorder.Properties DEFAULT_BORDER = new WorldBorder.Properties(0.0, 0.0, 0.2, 5.0, 5, 15, 5.999997E7F, 0L, 0.0);

	public boolean contains(BlockPos pos) {
		return (double)(pos.getX() + 1) > this.getBoundWest()
			&& (double)pos.getX() < this.getBoundEast()
			&& (double)(pos.getZ() + 1) > this.getBoundNorth()
			&& (double)pos.getZ() < this.getBoundSouth();
	}

	public boolean contains(ChunkPos pos) {
		return (double)pos.getEndX() > this.getBoundWest()
			&& (double)pos.getStartX() < this.getBoundEast()
			&& (double)pos.getEndZ() > this.getBoundNorth()
			&& (double)pos.getStartZ() < this.getBoundSouth();
	}

	public boolean contains(double x, double z) {
		return x > this.getBoundWest() && x < this.getBoundEast() && z > this.getBoundNorth() && z < this.getBoundSouth();
	}

	public boolean contains(Box box) {
		return box.maxX > this.getBoundWest() && box.minX < this.getBoundEast() && box.maxZ > this.getBoundNorth() && box.minZ < this.getBoundSouth();
	}

	public double getDistanceInsideBorder(Entity entity) {
		return this.getDistanceInsideBorder(entity.getX(), entity.getZ());
	}

	public VoxelShape asVoxelShape() {
		return this.area.asVoxelShape();
	}

	public double getDistanceInsideBorder(double x, double z) {
		double d = z - this.getBoundNorth();
		double e = this.getBoundSouth() - z;
		double f = x - this.getBoundWest();
		double g = this.getBoundEast() - x;
		double h = Math.min(f, g);
		h = Math.min(h, d);
		return Math.min(h, e);
	}

	public WorldBorderStage getStage() {
		return this.area.getStage();
	}

	public double getBoundWest() {
		return this.area.getBoundWest();
	}

	public double getBoundNorth() {
		return this.area.getBoundNorth();
	}

	public double getBoundEast() {
		return this.area.getBoundEast();
	}

	public double getBoundSouth() {
		return this.area.getBoundSouth();
	}

	public double getCenterX() {
		return this.centerX;
	}

	public double getCenterZ() {
		return this.centerZ;
	}

	public void setCenter(double x, double z) {
		this.centerX = x;
		this.centerZ = z;
		this.area.onCenterChanged();

		for (WorldBorderListener worldBorderListener : this.getListeners()) {
			worldBorderListener.onCenterChanged(this, x, z);
		}
	}

	public double getSize() {
		return this.area.getSize();
	}

	public long getSizeLerpTime() {
		return this.area.getSizeLerpTime();
	}

	public double getSizeLerpTarget() {
		return this.area.getSizeLerpTarget();
	}

	public void setSize(double size) {
		this.area = new WorldBorder.StaticArea(size);

		for (WorldBorderListener worldBorderListener : this.getListeners()) {
			worldBorderListener.onSizeChange(this, size);
		}
	}

	public void interpolateSize(double fromSize, double toSize, long time) {
		this.area = (WorldBorder.Area)(fromSize == toSize ? new WorldBorder.StaticArea(toSize) : new WorldBorder.MovingArea(fromSize, toSize, time));

		for (WorldBorderListener worldBorderListener : this.getListeners()) {
			worldBorderListener.onInterpolateSize(this, fromSize, toSize, time);
		}
	}

	protected List<WorldBorderListener> getListeners() {
		return Lists.newArrayList(this.listeners);
	}

	public void addListener(WorldBorderListener listener) {
		this.listeners.add(listener);
	}

	public void removeListener(WorldBorderListener listener) {
		this.listeners.remove(listener);
	}

	public void setMaxRadius(int maxRadius) {
		this.maxRadius = maxRadius;
		this.area.onMaxRadiusChanged();
	}

	public int getMaxRadius() {
		return this.maxRadius;
	}

	public double getSafeZone() {
		return this.safeZone;
	}

	public void setSafeZone(double safeZone) {
		this.safeZone = safeZone;

		for (WorldBorderListener worldBorderListener : this.getListeners()) {
			worldBorderListener.onSafeZoneChanged(this, safeZone);
		}
	}

	public double getDamagePerBlock() {
		return this.damagePerBlock;
	}

	public void setDamagePerBlock(double damagePerBlock) {
		this.damagePerBlock = damagePerBlock;

		for (WorldBorderListener worldBorderListener : this.getListeners()) {
			worldBorderListener.onDamagePerBlockChanged(this, damagePerBlock);
		}
	}

	public double getShrinkingSpeed() {
		return this.area.getShrinkingSpeed();
	}

	public int getWarningTime() {
		return this.warningTime;
	}

	public void setWarningTime(int warningTime) {
		this.warningTime = warningTime;

		for (WorldBorderListener worldBorderListener : this.getListeners()) {
			worldBorderListener.onWarningTimeChanged(this, warningTime);
		}
	}

	public int getWarningBlocks() {
		return this.warningBlocks;
	}

	public void setWarningBlocks(int warningBlocks) {
		this.warningBlocks = warningBlocks;

		for (WorldBorderListener worldBorderListener : this.getListeners()) {
			worldBorderListener.onWarningBlocksChanged(this, warningBlocks);
		}
	}

	public void tick() {
		this.area = this.area.getAreaInstance();
	}

	public WorldBorder.Properties write() {
		return new WorldBorder.Properties(this);
	}

	public void load(WorldBorder.Properties properties) {
		this.setCenter(properties.getCenterX(), properties.getCenterZ());
		this.setDamagePerBlock(properties.getDamagePerBlock());
		this.setSafeZone(properties.getSafeZone());
		this.setWarningBlocks(properties.getWarningBlocks());
		this.setWarningTime(properties.getWarningTime());
		if (properties.getSizeLerpTime() > 0L) {
			this.interpolateSize(properties.getSize(), properties.getSizeLerpTarget(), properties.getSizeLerpTime());
		} else {
			this.setSize(properties.getSize());
		}
	}

	interface Area {
		double getBoundWest();

		double getBoundEast();

		double getBoundNorth();

		double getBoundSouth();

		double getSize();

		double getShrinkingSpeed();

		long getSizeLerpTime();

		double getSizeLerpTarget();

		WorldBorderStage getStage();

		void onMaxRadiusChanged();

		void onCenterChanged();

		WorldBorder.Area getAreaInstance();

		VoxelShape asVoxelShape();
	}

	class MovingArea implements WorldBorder.Area {
		private final double oldSize;
		private final double newSize;
		private final long timeEnd;
		private final long timeStart;
		private final double timeDuration;

		MovingArea(double oldSize, double newSize, long timeDuration) {
			this.oldSize = oldSize;
			this.newSize = newSize;
			this.timeDuration = (double)timeDuration;
			this.timeStart = Util.getMeasuringTimeMs();
			this.timeEnd = this.timeStart + timeDuration;
		}

		@Override
		public double getBoundWest() {
			return MathHelper.clamp(WorldBorder.this.getCenterX() - this.getSize() / 2.0, (double)(-WorldBorder.this.maxRadius), (double)WorldBorder.this.maxRadius);
		}

		@Override
		public double getBoundNorth() {
			return MathHelper.clamp(WorldBorder.this.getCenterZ() - this.getSize() / 2.0, (double)(-WorldBorder.this.maxRadius), (double)WorldBorder.this.maxRadius);
		}

		@Override
		public double getBoundEast() {
			return MathHelper.clamp(WorldBorder.this.getCenterX() + this.getSize() / 2.0, (double)(-WorldBorder.this.maxRadius), (double)WorldBorder.this.maxRadius);
		}

		@Override
		public double getBoundSouth() {
			return MathHelper.clamp(WorldBorder.this.getCenterZ() + this.getSize() / 2.0, (double)(-WorldBorder.this.maxRadius), (double)WorldBorder.this.maxRadius);
		}

		@Override
		public double getSize() {
			double d = (double)(Util.getMeasuringTimeMs() - this.timeStart) / this.timeDuration;
			return d < 1.0 ? MathHelper.lerp(d, this.oldSize, this.newSize) : this.newSize;
		}

		@Override
		public double getShrinkingSpeed() {
			return Math.abs(this.oldSize - this.newSize) / (double)(this.timeEnd - this.timeStart);
		}

		@Override
		public long getSizeLerpTime() {
			return this.timeEnd - Util.getMeasuringTimeMs();
		}

		@Override
		public double getSizeLerpTarget() {
			return this.newSize;
		}

		@Override
		public WorldBorderStage getStage() {
			return this.newSize < this.oldSize ? WorldBorderStage.SHRINKING : WorldBorderStage.GROWING;
		}

		@Override
		public void onCenterChanged() {
		}

		@Override
		public void onMaxRadiusChanged() {
		}

		@Override
		public WorldBorder.Area getAreaInstance() {
			return (WorldBorder.Area)(this.getSizeLerpTime() <= 0L ? WorldBorder.this.new StaticArea(this.newSize) : this);
		}

		@Override
		public VoxelShape asVoxelShape() {
			return VoxelShapes.combineAndSimplify(
				VoxelShapes.UNBOUNDED,
				VoxelShapes.cuboid(
					Math.floor(this.getBoundWest()),
					Double.NEGATIVE_INFINITY,
					Math.floor(this.getBoundNorth()),
					Math.ceil(this.getBoundEast()),
					Double.POSITIVE_INFINITY,
					Math.ceil(this.getBoundSouth())
				),
				BooleanBiFunction.ONLY_FIRST
			);
		}
	}

	public static class Properties {
		private final double centerX;
		private final double centerZ;
		private final double damagePerBlock;
		private final double safeZone;
		private final int warningBlocks;
		private final int warningTime;
		private final double size;
		private final long sizeLerpTime;
		private final double sizeLerpTarget;

		Properties(
			double centerX,
			double centerZ,
			double damagePerBlock,
			double safeZone,
			int warningBlocks,
			int warningTime,
			double size,
			long sizeLerpTime,
			double sizeLerpTarget
		) {
			this.centerX = centerX;
			this.centerZ = centerZ;
			this.damagePerBlock = damagePerBlock;
			this.safeZone = safeZone;
			this.warningBlocks = warningBlocks;
			this.warningTime = warningTime;
			this.size = size;
			this.sizeLerpTime = sizeLerpTime;
			this.sizeLerpTarget = sizeLerpTarget;
		}

		Properties(WorldBorder worldBorder) {
			this.centerX = worldBorder.getCenterX();
			this.centerZ = worldBorder.getCenterZ();
			this.damagePerBlock = worldBorder.getDamagePerBlock();
			this.safeZone = worldBorder.getSafeZone();
			this.warningBlocks = worldBorder.getWarningBlocks();
			this.warningTime = worldBorder.getWarningTime();
			this.size = worldBorder.getSize();
			this.sizeLerpTime = worldBorder.getSizeLerpTime();
			this.sizeLerpTarget = worldBorder.getSizeLerpTarget();
		}

		public double getCenterX() {
			return this.centerX;
		}

		public double getCenterZ() {
			return this.centerZ;
		}

		public double getDamagePerBlock() {
			return this.damagePerBlock;
		}

		public double getSafeZone() {
			return this.safeZone;
		}

		public int getWarningBlocks() {
			return this.warningBlocks;
		}

		public int getWarningTime() {
			return this.warningTime;
		}

		public double getSize() {
			return this.size;
		}

		public long getSizeLerpTime() {
			return this.sizeLerpTime;
		}

		public double getSizeLerpTarget() {
			return this.sizeLerpTarget;
		}

		public static WorldBorder.Properties fromDynamic(DynamicLike<?> dynamicLike, WorldBorder.Properties properties) {
			double d = dynamicLike.get("BorderCenterX").asDouble(properties.centerX);
			double e = dynamicLike.get("BorderCenterZ").asDouble(properties.centerZ);
			double f = dynamicLike.get("BorderSize").asDouble(properties.size);
			long l = dynamicLike.get("BorderSizeLerpTime").asLong(properties.sizeLerpTime);
			double g = dynamicLike.get("BorderSizeLerpTarget").asDouble(properties.sizeLerpTarget);
			double h = dynamicLike.get("BorderSafeZone").asDouble(properties.safeZone);
			double i = dynamicLike.get("BorderDamagePerBlock").asDouble(properties.damagePerBlock);
			int j = dynamicLike.get("BorderWarningBlocks").asInt(properties.warningBlocks);
			int k = dynamicLike.get("BorderWarningTime").asInt(properties.warningTime);
			return new WorldBorder.Properties(d, e, i, h, j, k, f, l, g);
		}

		public void writeNbt(NbtCompound nbt) {
			nbt.putDouble("BorderCenterX", this.centerX);
			nbt.putDouble("BorderCenterZ", this.centerZ);
			nbt.putDouble("BorderSize", this.size);
			nbt.putLong("BorderSizeLerpTime", this.sizeLerpTime);
			nbt.putDouble("BorderSafeZone", this.safeZone);
			nbt.putDouble("BorderDamagePerBlock", this.damagePerBlock);
			nbt.putDouble("BorderSizeLerpTarget", this.sizeLerpTarget);
			nbt.putDouble("BorderWarningBlocks", (double)this.warningBlocks);
			nbt.putDouble("BorderWarningTime", (double)this.warningTime);
		}
	}

	class StaticArea implements WorldBorder.Area {
		private final double size;
		private double boundWest;
		private double boundNorth;
		private double boundEast;
		private double boundSouth;
		private VoxelShape shape;

		public StaticArea(double size) {
			this.size = size;
			this.recalculateBounds();
		}

		@Override
		public double getBoundWest() {
			return this.boundWest;
		}

		@Override
		public double getBoundEast() {
			return this.boundEast;
		}

		@Override
		public double getBoundNorth() {
			return this.boundNorth;
		}

		@Override
		public double getBoundSouth() {
			return this.boundSouth;
		}

		@Override
		public double getSize() {
			return this.size;
		}

		@Override
		public WorldBorderStage getStage() {
			return WorldBorderStage.STATIONARY;
		}

		@Override
		public double getShrinkingSpeed() {
			return 0.0;
		}

		@Override
		public long getSizeLerpTime() {
			return 0L;
		}

		@Override
		public double getSizeLerpTarget() {
			return this.size;
		}

		private void recalculateBounds() {
			this.boundWest = MathHelper.clamp(WorldBorder.this.getCenterX() - this.size / 2.0, (double)(-WorldBorder.this.maxRadius), (double)WorldBorder.this.maxRadius);
			this.boundNorth = MathHelper.clamp(
				WorldBorder.this.getCenterZ() - this.size / 2.0, (double)(-WorldBorder.this.maxRadius), (double)WorldBorder.this.maxRadius
			);
			this.boundEast = MathHelper.clamp(WorldBorder.this.getCenterX() + this.size / 2.0, (double)(-WorldBorder.this.maxRadius), (double)WorldBorder.this.maxRadius);
			this.boundSouth = MathHelper.clamp(
				WorldBorder.this.getCenterZ() + this.size / 2.0, (double)(-WorldBorder.this.maxRadius), (double)WorldBorder.this.maxRadius
			);
			this.shape = VoxelShapes.combineAndSimplify(
				VoxelShapes.UNBOUNDED,
				VoxelShapes.cuboid(
					Math.floor(this.getBoundWest()),
					Double.NEGATIVE_INFINITY,
					Math.floor(this.getBoundNorth()),
					Math.ceil(this.getBoundEast()),
					Double.POSITIVE_INFINITY,
					Math.ceil(this.getBoundSouth())
				),
				BooleanBiFunction.ONLY_FIRST
			);
		}

		@Override
		public void onMaxRadiusChanged() {
			this.recalculateBounds();
		}

		@Override
		public void onCenterChanged() {
			this.recalculateBounds();
		}

		@Override
		public WorldBorder.Area getAreaInstance() {
			return this;
		}

		@Override
		public VoxelShape asVoxelShape() {
			return this.shape;
		}
	}
}
