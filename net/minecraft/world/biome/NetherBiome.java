package net.minecraft.world.biome;

import net.minecraft.class_3007;
import net.minecraft.entity.mob.EndermanEntity;
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
		this.monsterEntries.add(new Biome.SpawnEntry(MagmaCubeEntity.class, 2, 4, 4));
		this.monsterEntries.add(new Biome.SpawnEntry(EndermanEntity.class, 1, 4, 4));
		this.biomeDecorator = new class_3007();
	}
}
