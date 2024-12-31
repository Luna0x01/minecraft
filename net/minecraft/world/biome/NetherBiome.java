package net.minecraft.world.biome;

import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.ZombiePigmanEntity;

public class NetherBiome extends Biome {
	public NetherBiome(Biome.Settings settings) {
		super(settings);
		this.monsterEntries.clear();
		this.passiveEntries.clear();
		this.waterEntries.clear();
		this.flyingEntries.clear();
		this.monsterEntries.add(new Biome.SpawnEntry(GhastEntity.class, 50, 4, 4));
		this.monsterEntries.add(new Biome.SpawnEntry(ZombiePigmanEntity.class, 100, 4, 4));
		this.monsterEntries.add(new Biome.SpawnEntry(MagmaCubeEntity.class, 1, 4, 4));
	}
}
