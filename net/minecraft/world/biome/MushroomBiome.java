package net.minecraft.world.biome;

import net.minecraft.block.Blocks;
import net.minecraft.entity.passive.MooshroomEntity;

public class MushroomBiome extends Biome {
	public MushroomBiome(Biome.Settings settings) {
		super(settings);
		this.biomeDecorator.treesPerChunk = -100;
		this.biomeDecorator.flowersPerChunk = -100;
		this.biomeDecorator.grassPerChunk = -100;
		this.biomeDecorator.mushroomsPerChunk = 1;
		this.biomeDecorator.hugeMushroomsPerChunk = 1;
		this.topBlock = Blocks.MYCELIUM.getDefaultState();
		this.monsterEntries.clear();
		this.passiveEntries.clear();
		this.waterEntries.clear();
		this.passiveEntries.add(new Biome.SpawnEntry(MooshroomEntity.class, 8, 4, 8));
	}
}
