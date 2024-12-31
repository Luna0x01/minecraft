package net.minecraft;

import java.util.Random;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.EndChunkGenerator;

public class class_3943 extends class_3945<class_3870> {
	public <C extends class_3845> boolean method_17536(
		IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3870 arg, class_3844<C> arg2, C arg3
	) {
		boolean bl = false;
		if (random.nextInt(700) == 0) {
			int i = random.nextInt(16);
			int j = random.nextInt(16);
			int k = iWorld.method_16373(class_3804.class_3805.MOTION_BLOCKING, blockPos.add(i, 0, j)).getY();
			if (k > 0) {
				int l = k + 3 + random.nextInt(7);
				BlockPos blockPos2 = blockPos.add(i, l, j);
				arg2.method_17343(iWorld, chunkGenerator, random, blockPos2, arg3);
				BlockEntity blockEntity = iWorld.getBlockEntity(blockPos);
				if (blockEntity instanceof EndGatewayBlockEntity) {
					EndGatewayBlockEntity endGatewayBlockEntity = (EndGatewayBlockEntity)blockEntity;
					endGatewayBlockEntity.setExitPortal(((EndChunkGenerator)chunkGenerator).method_17283());
				}
			}
		}

		return false;
	}
}
