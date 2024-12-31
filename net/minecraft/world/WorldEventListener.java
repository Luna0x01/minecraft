package net.minecraft.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

public interface WorldEventListener {
	void onBlockUpdate(BlockPos pos);

	void onLightUpdate(BlockPos pos);

	void onRenderRegionUpdate(int x1, int y1, int z1, int x2, int y2, int z2);

	void playSound(String name, double x, double y, double z, float volume, float pitch);

	void playSound(PlayerEntity except, String name, double x, double y, double z, float volume, float pitch);

	void addParticle(int id, boolean bl, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... args);

	void onEntitySpawned(Entity entity);

	void onEntityRemoved(Entity entity);

	void playMusicDisc(String id, BlockPos pos);

	void processGlobalEvent(int eventId, BlockPos pos, int j);

	void processWorldEvent(PlayerEntity player, int eventId, BlockPos pos, int data);

	void setBlockBreakInfo(int entityId, BlockPos pos, int progress);
}
