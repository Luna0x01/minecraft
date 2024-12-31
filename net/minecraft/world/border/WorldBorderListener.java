package net.minecraft.world.border;

public interface WorldBorderListener {
	void onSizeChange(WorldBorder border, double newSize);

	void onInterpolateSize(WorldBorder border, double oldSize, double targetSize, long time);

	void onCenterChanged(WorldBorder border, double centerX, double centerZ);

	void onWarningTimeChanged(WorldBorder border, int newTime);

	void onWarningBlocksChanged(WorldBorder border, int warningBlocks);

	void onDamagePerBlockChanged(WorldBorder border, double damagePerBlock);

	void onSafeZoneChanged(WorldBorder border, double safeZone);
}
