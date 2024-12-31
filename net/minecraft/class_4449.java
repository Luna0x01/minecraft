package net.minecraft;

import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkBlockStateStorage;

public class class_4449 extends class_4445 {
	@Override
	protected ChunkBlockStateStorage method_21302(
		class_3786 arg, World world, ChunkGenerator<?> chunkGenerator, ChunkBlockStateStorage[] chunkBlockStateStorages, int i, int j
	) {
		ChunkBlockStateStorage chunkBlockStateStorage = chunkBlockStateStorages[chunkBlockStateStorages.length / 2];
		class_4441 lv = new class_4441(chunkBlockStateStorages, arg.method_17053() * 2 + 1, arg.method_17053() * 2 + 1, i, j, world);
		chunkBlockStateStorage.method_17000(class_3804.class_3805.LIGHT_BLOCKING);
		if (lv.method_16393().isOverworld()) {
			new class_4020().method_17743(lv, chunkBlockStateStorage);
		}

		new class_4018().method_17741(lv, chunkBlockStateStorage);
		chunkBlockStateStorage.method_16990(class_3786.LIGHTED);
		return chunkBlockStateStorage;
	}
}
