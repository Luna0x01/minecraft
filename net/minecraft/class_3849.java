package net.minecraft;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;

public class class_3849 extends class_3848 {
	private static final Block[] field_19205 = new Block[]{
		Blocks.DANDELION,
		Blocks.POPPY,
		Blocks.BLUE_ORCHID,
		Blocks.ALLIUM,
		Blocks.AZURE_BLUET,
		Blocks.RED_TULIP,
		Blocks.ORANGE_TULIP,
		Blocks.WHITE_TULIP,
		Blocks.PINK_TULIP,
		Blocks.OXEYE_DAISY
	};

	@Override
	public BlockState method_17350(Random random, BlockPos blockPos) {
		double d = MathHelper.clamp((1.0 + Biome.FOLIAGE_NOISE.noise((double)blockPos.getX() / 48.0, (double)blockPos.getZ() / 48.0)) / 2.0, 0.0, 0.9999);
		Block block = field_19205[(int)(d * (double)field_19205.length)];
		return block == Blocks.BLUE_ORCHID ? Blocks.POPPY.getDefaultState() : block.getDefaultState();
	}
}
