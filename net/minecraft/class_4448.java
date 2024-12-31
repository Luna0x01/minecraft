package net.minecraft;

import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkBlockStateStorage;

public class class_4448 extends class_4445 {
	@Override
	protected ChunkBlockStateStorage method_21302(
		class_3786 arg, World world, ChunkGenerator<?> chunkGenerator, ChunkBlockStateStorage[] chunkBlockStateStorages, int i, int j
	) {
		ChunkBlockStateStorage chunkBlockStateStorage = chunkBlockStateStorages[chunkBlockStateStorages.length / 2];
		chunkBlockStateStorage.method_16990(class_3786.FINALIZED);
		chunkBlockStateStorage.method_17000(
			class_3804.class_3805.MOTION_BLOCKING,
			class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES,
			class_3804.class_3805.LIGHT_BLOCKING,
			class_3804.class_3805.OCEAN_FLOOR,
			class_3804.class_3805.WORLD_SURFACE
		);
		return chunkBlockStateStorage;
	}
}
