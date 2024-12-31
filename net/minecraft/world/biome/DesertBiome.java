package net.minecraft.world.biome;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.class_3013;
import net.minecraft.block.Blocks;
import net.minecraft.entity.HuskEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.DesertWellFeature;

public class DesertBiome extends Biome {
	public DesertBiome(Biome.Settings settings) {
		super(settings);
		this.passiveEntries.clear();
		this.topBlock = Blocks.SAND.getDefaultState();
		this.baseBlock = Blocks.SAND.getDefaultState();
		this.biomeDecorator.treesPerChunk = -999;
		this.biomeDecorator.deadBushesPerChunk = 2;
		this.biomeDecorator.sugarcanePerChunk = 50;
		this.biomeDecorator.cactusPerChunk = 10;
		this.passiveEntries.clear();
		this.passiveEntries.add(new Biome.SpawnEntry(RabbitEntity.class, 4, 2, 3));
		Iterator<Biome.SpawnEntry> iterator = this.monsterEntries.iterator();

		while (iterator.hasNext()) {
			Biome.SpawnEntry spawnEntry = (Biome.SpawnEntry)iterator.next();
			if (spawnEntry.entity == ZombieEntity.class || spawnEntry.entity == ZombieVillagerEntity.class) {
				iterator.remove();
			}
		}

		this.monsterEntries.add(new Biome.SpawnEntry(ZombieEntity.class, 19, 4, 4));
		this.monsterEntries.add(new Biome.SpawnEntry(ZombieVillagerEntity.class, 1, 1, 1));
		this.monsterEntries.add(new Biome.SpawnEntry(HuskEntity.class, 80, 4, 4));
	}

	@Override
	public void decorate(World world, Random random, BlockPos pos) {
		super.decorate(world, random, pos);
		if (random.nextInt(1000) == 0) {
			int i = random.nextInt(16) + 8;
			int j = random.nextInt(16) + 8;
			BlockPos blockPos = world.getHighestBlock(pos.add(i, 0, j)).up();
			new DesertWellFeature().generate(world, random, blockPos);
		}

		if (random.nextInt(64) == 0) {
			new class_3013().generate(world, random, pos);
		}
	}
}
