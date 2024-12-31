package net.minecraft.world.border;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;

public class WorldBorder {
	private final List<WorldBorderListener> listeners = Lists.newArrayList();
	private double centerX = 0.0;
	private double centerZ = 0.0;
	private double oldSize = 6.0E7;
	private double targetSize = this.oldSize;
	private long targetTime;
	private long oldTime;
	private int maxWorldBorderRadius = 29999984;
	private double damagePerBlock = 0.2;
	private double safeZone = 5.0;
	private int warningTime = 15;
	private int warningBlocks = 5;

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
		if (this.targetSize < this.oldSize) {
			return WorldBorderStage.SHRINKING;
		} else {
			return this.targetSize > this.oldSize ? WorldBorderStage.GROWING : WorldBorderStage.STATIONARY;
		}
	}

	public double getBoundWest() {
		double d = this.getCenterX() - this.getOldSize() / 2.0;
		if (d < (double)(-this.maxWorldBorderRadius)) {
			d = (double)(-this.maxWorldBorderRadius);
		}

		return d;
	}

	public double getBoundNorth() {
		double d = this.getCenterZ() - this.getOldSize() / 2.0;
		if (d < (double)(-this.maxWorldBorderRadius)) {
			d = (double)(-this.maxWorldBorderRadius);
		}

		return d;
	}

	public double getBoundEast() {
		double d = this.getCenterX() + this.getOldSize() / 2.0;
		if (d > (double)this.maxWorldBorderRadius) {
			d = (double)this.maxWorldBorderRadius;
		}

		return d;
	}

	public double getBoundSouth() {
		double d = this.getCenterZ() + this.getOldSize() / 2.0;
		if (d > (double)this.maxWorldBorderRadius) {
			d = (double)this.maxWorldBorderRadius;
		}

		return d;
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

		for (WorldBorderListener worldBorderListener : this.getListeners()) {
			worldBorderListener.onCenterChanged(this, x, z);
		}
	}

	public double getOldSize() {
		if (this.getWorldBorderStage() != WorldBorderStage.STATIONARY) {
			double d = (double)((float)(System.currentTimeMillis() - this.oldTime) / (float)(this.targetTime - this.oldTime));
			if (!(d >= 1.0)) {
				return this.oldSize + (this.targetSize - this.oldSize) * d;
			}

			this.setSize(this.targetSize);
		}

		return this.oldSize;
	}

	public long getInterpolationDuration() {
		return this.getWorldBorderStage() != WorldBorderStage.STATIONARY ? this.targetTime - System.currentTimeMillis() : 0L;
	}

	public double getTargetSize() {
		return this.targetSize;
	}

	public void setSize(double size) {
		this.oldSize = size;
		this.targetSize = size;
		this.targetTime = System.currentTimeMillis();
		this.oldTime = this.targetTime;

		for (WorldBorderListener worldBorderListener : this.getListeners()) {
			worldBorderListener.onSizeChange(this, size);
		}
	}

	public void interpolateSize(double oldSize, double targetSize, long time) {
		this.oldSize = oldSize;
		this.targetSize = targetSize;
		this.oldTime = System.currentTimeMillis();
		this.targetTime = this.oldTime + time;

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
		return this.targetTime == this.oldTime ? 0.0 : Math.abs(this.oldSize - this.targetSize) / (double)(this.targetTime - this.oldTime);
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
}
