package net.minecraft.world.biome;

import net.minecraft.block.Blocks;

public class BeachBiome extends Biome {
	public BeachBiome(int i) {
		super(i);
		this.passiveEntries.clear();
		this.topBlock = Blocks.SAND.getDefaultState();
		this.baseBlock = Blocks.SAND.getDefaultState();
		this.biomeDecorator.treesPerChunk = -999;
		this.biomeDecorator.deadBushesPerChunk = 0;
		this.biomeDecorator.sugarcanePerChunk = 0;
		this.biomeDecorator.cactusPerChunk = 0;
	}
}
