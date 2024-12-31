package net.minecraft.world.biome;

import net.minecraft.block.Blocks;
import net.minecraft.entity.mob.EndermanEntity;

public class EndBiome extends Biome {
	public EndBiome(Biome.Settings settings) {
		super(settings);
		this.monsterEntries.clear();
		this.passiveEntries.clear();
		this.waterEntries.clear();
		this.flyingEntries.clear();
		this.monsterEntries.add(new Biome.SpawnEntry(EndermanEntity.class, 10, 4, 4));
		this.topBlock = Blocks.DIRT.getDefaultState();
		this.baseBlock = Blocks.DIRT.getDefaultState();
		this.biomeDecorator = new EndBiomeDecorator();
	}

	@Override
	public int getSkyColor(float temperature) {
		return 0;
	}
}
