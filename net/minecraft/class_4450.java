package net.minecraft;

import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkBlockStateStorage;

public class class_4450 extends class_4445 {
	@Override
	protected ChunkBlockStateStorage method_21302(
		class_3786 arg, World world, ChunkGenerator<?> chunkGenerator, ChunkBlockStateStorage[] chunkBlockStateStorages, int i, int j
	) {
		chunkGenerator.method_17019(
			new class_4441(chunkBlockStateStorages, arg.method_17053() * 2 + 1, arg.method_17053() * 2 + 1, i, j, world), class_3801.class_3802.LIQUID
		);
		ChunkBlockStateStorage chunkBlockStateStorage = chunkBlockStateStorages[chunkBlockStateStorages.length / 2];
		chunkBlockStateStorage.method_17000(class_3804.class_3805.OCEAN_FLOOR_WG, class_3804.class_3805.WORLD_SURFACE_WG);
		chunkBlockStateStorage.method_16990(class_3786.LIQUID_CARVED);
		return chunkBlockStateStorage;
	}
}
