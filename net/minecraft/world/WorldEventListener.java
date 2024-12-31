package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.Sound;
import net.minecraft.util.math.BlockPos;

public interface WorldEventListener {
	void method_11493(World world, BlockPos position, BlockState blockState, BlockState blockState2, int i);

	void onLightUpdate(BlockPos pos);

	void onRenderRegionUpdate(int x1, int y1, int z1, int x2, int y2, int z2);

	void method_3747(@Nullable PlayerEntity playerEntity, Sound sound, SoundCategory soundCategory, double d, double e, double f, float g, float h);

	void method_8572(Sound sound, BlockPos blockPos);

	void addParticle(int id, boolean bl, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... args);

	void method_13696(int i, boolean bl, boolean bl2, double d, double e, double f, double g, double h, double j, int... is);

	void onEntitySpawned(Entity entity);

	void onEntityRemoved(Entity entity);

	void processGlobalEvent(int eventId, BlockPos pos, int j);

	void processWorldEvent(PlayerEntity player, int eventId, BlockPos pos, int data);

	void setBlockBreakInfo(int entityId, BlockPos pos, int progress);
}
