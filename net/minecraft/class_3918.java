package net.minecraft;

import java.util.BitSet;
import java.util.Random;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;

public class class_3918 extends class_3945<class_3933> {
	public <C extends class_3845> boolean method_17536(
		IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3933 arg, class_3844<C> arg2, C arg3
	) {
		class_3781 lv = iWorld.method_16351(blockPos);
		ChunkPos chunkPos = lv.method_3920();
		BitSet bitSet = lv.method_16991(arg.field_19313);

		for (int i = 0; i < bitSet.length(); i++) {
			if (bitSet.get(i) && random.nextFloat() < arg.field_19314) {
				int j = i & 15;
				int k = i >> 4 & 15;
				int l = i >> 8;
				arg2.method_17343(iWorld, chunkGenerator, random, new BlockPos(chunkPos.getActualX() + j, l, chunkPos.getActualZ() + k), arg3);
			}
		}

		return true;
	}
}
