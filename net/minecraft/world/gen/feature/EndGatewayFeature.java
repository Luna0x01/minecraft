package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.class_3798;
import net.minecraft.class_3843;
import net.minecraft.class_3844;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.TheEndDimension;

public class EndGatewayFeature extends class_3844<class_3843> {
	public boolean method_17343(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3843 arg) {
		for (BlockPos.Mutable mutable : BlockPos.mutableIterate(blockPos.add(-1, -2, -1), blockPos.add(1, 2, 1))) {
			boolean bl = mutable.getX() == blockPos.getX();
			boolean bl2 = mutable.getY() == blockPos.getY();
			boolean bl3 = mutable.getZ() == blockPos.getZ();
			boolean bl4 = Math.abs(mutable.getY() - blockPos.getY()) == 2;
			if (bl && bl2 && bl3) {
				BlockPos blockPos2 = mutable.toImmutable();
				this.method_17344(iWorld, blockPos2, Blocks.END_GATEWAY.getDefaultState());
				if (arg.method_17339()) {
					BlockEntity blockEntity = iWorld.getBlockEntity(blockPos2);
					if (blockEntity instanceof EndGatewayBlockEntity) {
						EndGatewayBlockEntity endGatewayBlockEntity = (EndGatewayBlockEntity)blockEntity;
						endGatewayBlockEntity.setExitPortal(TheEndDimension.field_18968);
					}
				}
			} else if (bl2) {
				this.method_17344(iWorld, mutable, Blocks.AIR.getDefaultState());
			} else if (bl4 && bl && bl3) {
				this.method_17344(iWorld, mutable, Blocks.BEDROCK.getDefaultState());
			} else if ((bl || bl3) && !bl4) {
				this.method_17344(iWorld, mutable, Blocks.BEDROCK.getDefaultState());
			} else {
				this.method_17344(iWorld, mutable, Blocks.AIR.getDefaultState());
			}
		}

		return true;
	}
}
