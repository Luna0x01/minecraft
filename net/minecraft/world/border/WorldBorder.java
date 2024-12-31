package net.minecraft.world.border;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;

public class WorldBorder {
	private final List<WorldBorderListener> listeners = Lists.newArrayList();
	private double damagePerBlock = 0.2;
	private double safeZone = 5.0;
	private int warningTime = 15;
	private int warningBlocks = 5;
	private double centerX;
	private double centerZ;
	private int maxWorldBorderRadius = 29999984;
	private WorldBorder.class_3778 field_18825 = new WorldBorder.class_3780(6.0E7);

	public boolean contains(BlockPos pos) {
		return (double)(pos.getX() + 1) > this.getBoundWest()
			&& (double)pos.getX() < this.getBoundEast()
			&& (double)(pos.getZ() + 1) > this.getBoundNorth()
			&& (double)pos.getZ() < this.getBoundSouth();
	}

	public boolean contains(ChunkPos pos) {
		return (double)pos.getOppositeX() > this.getBoundWest()
			&& (double)pos.getActualX() < this.getBoundEast()
			&& (double)pos.getOppositeZ() > this.getBoundNorth()
			&& (double)pos.getActualZ() < this.getBoundSouth();
	}

	public boolean contains(Box box) {
		return box.maxX > this.getBoundWest() && box.minX < this.getBoundEast() && box.maxZ > this.getBoundNorth() && box.minZ < this.getBoundSouth();
	}

	public double getDistanceInsideBorder(Entity entity) {
		return this.getDistanceInsideBorder(entity.x, entity.z);
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

	public WorldBorderStage getWorldBorderStage() {
		return this.field_18825.method_16984();
	}

	public double getBoundWest() {
		return this.field_18825.method_16976();
	}

	public double getBoundNorth() {
		return this.field_18825.method_16978();
	}

	public double getBoundEast() {
		return this.field_18825.method_16977();
	}

	public double getBoundSouth() {
		return this.field_18825.method_16979();
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
		this.field_18825.method_16986();

		for (WorldBorderListener worldBorderListener : this.getListeners()) {
			worldBorderListener.onCenterChanged(this, x, z);
		}
	}

	public double getOldSize() {
		return this.field_18825.method_16980();
	}

	public long getInterpolationDuration() {
		return this.field_18825.method_16982();
	}

	public double getTargetSize() {
		return this.field_18825.method_16983();
	}

	public void setSize(double size) {
		this.field_18825 = new WorldBorder.class_3780(size);

		for (WorldBorderListener worldBorderListener : this.getListeners()) {
			worldBorderListener.onSizeChange(this, size);
		}
	}

	public void interpolateSize(double oldSize, double targetSize, long time) {
		this.field_18825 = (WorldBorder.class_3778)(oldSize != targetSize
			? new WorldBorder.class_3779(oldSize, targetSize, time)
			: new WorldBorder.class_3780(targetSize));

		for (WorldBorderListener worldBorderListener : this.getListeners()) {
			worldBorderListener.onInterpolateSize(this, oldSize, targetSize, time);
		}
	}

	protected List<WorldBorderListener> getListeners() {
		return Lists.newArrayList(this.listeners);
	}

	public void addListener(WorldBorderListener listener) {
		this.listeners.add(listener);
	}

	public void setMaxWorldBorderRadius(int radius) {
		this.maxWorldBorderRadius = radius;
		this.field_18825.method_16985();
	}

	public int getMaxWorldBorderRadius() {
		return this.maxWorldBorderRadius;
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

	public double getBorderDamagePerBlock() {
		return this.damagePerBlock;
	}

	public void setDamagePerBlock(double damagePerBlock) {
		this.damagePerBlock = damagePerBlock;

		for (WorldBorderListener worldBorderListener : this.getListeners()) {
			worldBorderListener.onDamagePerBlockChanged(this, damagePerBlock);
		}
	}

	public double getShrinkingSpeed() {
		return this.field_18825.method_16981();
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

	public void method_16975() {
		this.field_18825 = this.field_18825.method_16987();
	}

	interface class_3778 {
		double method_16976();

		double method_16977();

		double method_16978();

		double method_16979();

		double method_16980();

		double method_16981();

		long method_16982();

		double method_16983();

		WorldBorderStage method_16984();

		void method_16985();

		void method_16986();

		WorldBorder.class_3778 method_16987();
	}

	class class_3779 implements WorldBorder.class_3778 {
		private final double field_18827;
		private final double field_18828;
		private final long field_18829;
		private final long field_18830;
		private final double field_18831;

		private class_3779(double d, double e, long l) {
			this.field_18827 = d;
			this.field_18828 = e;
			this.field_18831 = (double)l;
			this.field_18830 = Util.method_20227();
			this.field_18829 = this.field_18830 + l;
		}

		@Override
		public double method_16976() {
			return Math.max(WorldBorder.this.getCenterX() - this.method_16980() / 2.0, (double)(-WorldBorder.this.maxWorldBorderRadius));
		}

		@Override
		public double method_16978() {
			return Math.max(WorldBorder.this.getCenterZ() - this.method_16980() / 2.0, (double)(-WorldBorder.this.maxWorldBorderRadius));
		}

		@Override
		public double method_16977() {
			return Math.min(WorldBorder.this.getCenterX() + this.method_16980() / 2.0, (double)WorldBorder.this.maxWorldBorderRadius);
		}

		@Override
		public double method_16979() {
			return Math.min(WorldBorder.this.getCenterZ() + this.method_16980() / 2.0, (double)WorldBorder.this.maxWorldBorderRadius);
		}

		@Override
		public double method_16980() {
			double d = (double)(Util.method_20227() - this.field_18830) / this.field_18831;
			return d < 1.0 ? this.field_18827 + (this.field_18828 - this.field_18827) * d : this.field_18828;
		}

		@Override
		public double method_16981() {
			return Math.abs(this.field_18827 - this.field_18828) / (double)(this.field_18829 - this.field_18830);
		}

		@Override
		public long method_16982() {
			return this.field_18829 - Util.method_20227();
		}

		@Override
		public double method_16983() {
			return this.field_18828;
		}

		@Override
		public WorldBorderStage method_16984() {
			return this.field_18828 < this.field_18827 ? WorldBorderStage.SHRINKING : WorldBorderStage.GROWING;
		}

		@Override
		public void method_16986() {
		}

		@Override
		public void method_16985() {
		}

		@Override
		public WorldBorder.class_3778 method_16987() {
			return (WorldBorder.class_3778)(this.method_16982() <= 0L ? WorldBorder.this.new class_3780(this.field_18828) : this);
		}
	}

	class class_3780 implements WorldBorder.class_3778 {
		private final double field_18833;
		private double field_18834;
		private double field_18835;
		private double field_18836;
		private double field_18837;

		public class_3780(double d) {
			this.field_18833 = d;
			this.method_16988();
		}

		@Override
		public double method_16976() {
			return this.field_18834;
		}

		@Override
		public double method_16977() {
			return this.field_18836;
		}

		@Override
		public double method_16978() {
			return this.field_18835;
		}

		@Override
		public double method_16979() {
			return this.field_18837;
		}

		@Override
		public double method_16980() {
			return this.field_18833;
		}

		@Override
		public WorldBorderStage method_16984() {
			return WorldBorderStage.STATIONARY;
		}

		@Override
		public double method_16981() {
			return 0.0;
		}

		@Override
		public long method_16982() {
			return 0L;
		}

		@Override
		public double method_16983() {
			return this.field_18833;
		}

		private void method_16988() {
			this.field_18834 = Math.max(WorldBorder.this.getCenterX() - this.field_18833 / 2.0, (double)(-WorldBorder.this.maxWorldBorderRadius));
			this.field_18835 = Math.max(WorldBorder.this.getCenterZ() - this.field_18833 / 2.0, (double)(-WorldBorder.this.maxWorldBorderRadius));
			this.field_18836 = Math.min(WorldBorder.this.getCenterX() + this.field_18833 / 2.0, (double)WorldBorder.this.maxWorldBorderRadius);
			this.field_18837 = Math.min(WorldBorder.this.getCenterZ() + this.field_18833 / 2.0, (double)WorldBorder.this.maxWorldBorderRadius);
		}

		@Override
		public void method_16985() {
			this.method_16988();
		}

		@Override
		public void method_16986() {
			this.method_16988();
		}

		@Override
		public WorldBorder.class_3778 method_16987() {
			return this;
		}
	}
}
