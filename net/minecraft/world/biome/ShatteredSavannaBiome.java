package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.DirtBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkBlockStateStorage;

public class ShatteredSavannaBiome extends SavannaBiome {
	public ShatteredSavannaBiome(Biome.Settings settings) {
		super(settings);
		this.biomeDecorator.treesPerChunk = 2;
		this.biomeDecorator.flowersPerChunk = 2;
		this.biomeDecorator.grassPerChunk = 5;
	}

	@Override
	public void method_6420(World world, Random random, ChunkBlockStateStorage chunkStorage, int i, int j, double d) {
		this.topBlock = Blocks.GRASS.getDefaultState();
		this.baseBlock = Blocks.DIRT.getDefaultState();
		if (d > 1.75) {
			this.topBlock = Blocks.STONE.getDefaultState();
			this.baseBlock = Blocks.STONE.getDefaultState();
		} else if (d > -0.5) {
			this.topBlock = Blocks.DIRT.getDefaultState().with(DirtBlock.VARIANT, DirtBlock.DirtType.COARSE_DIRT);
		}

		this.method_8590(world, random, chunkStorage, i, j, d);
	}

	@Override
	public void decorate(World world, Random random, BlockPos pos) {
		this.biomeDecorator.decorate(world, random, this, pos);
	}
}
