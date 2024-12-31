package net.minecraft.world.biome;

import net.minecraft.block.Blocks;

public class StoneBeachBiome extends Biome {
	public StoneBeachBiome(int i) {
		super(i);
		this.passiveEntries.clear();
		this.topBlock = Blocks.STONE.getDefaultState();
		this.baseBlock = Blocks.STONE.getDefaultState();
		this.biomeDecorator.treesPerChunk = -999;
		this.biomeDecorator.deadBushesPerChunk = 0;
		this.biomeDecorator.sugarcanePerChunk = 0;
		this.biomeDecorator.cactusPerChunk = 0;
	}
}
