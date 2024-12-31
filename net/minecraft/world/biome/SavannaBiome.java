package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.entity.DonkeyEntity;
import net.minecraft.entity.LlamaEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.AcaciaTreeFeature;
import net.minecraft.world.gen.feature.FoliageFeature;

public class SavannaBiome extends Biome {
	private static final AcaciaTreeFeature field_7253 = new AcaciaTreeFeature(false);

	protected SavannaBiome(Biome.Settings settings) {
		super(settings);
		this.passiveEntries.add(new Biome.SpawnEntry(HorseBaseEntity.class, 1, 2, 6));
		this.passiveEntries.add(new Biome.SpawnEntry(DonkeyEntity.class, 1, 1, 1));
		if (this.getDepth() > 1.1F) {
			this.passiveEntries.add(new Biome.SpawnEntry(LlamaEntity.class, 8, 4, 4));
		}

		this.biomeDecorator.treesPerChunk = 1;
		this.biomeDecorator.flowersPerChunk = 4;
		this.biomeDecorator.grassPerChunk = 20;
	}

	@Override
	public FoliageFeature method_3822(Random random) {
		return (FoliageFeature)(random.nextInt(5) > 0 ? field_7253 : JUNGLE_TREE_FEATURE);
	}

	@Override
	public void decorate(World world, Random random, BlockPos pos) {
		DOUBLE_PLANT_FEATURE.setType(DoublePlantBlock.DoublePlantType.GRASS);

		for (int i = 0; i < 7; i++) {
			int j = random.nextInt(16) + 8;
			int k = random.nextInt(16) + 8;
			int l = random.nextInt(world.getHighestBlock(pos.add(j, 0, k)).getY() + 32);
			DOUBLE_PLANT_FEATURE.generate(world, random, pos.add(j, l, k));
		}

		super.decorate(world, random, pos);
	}

	@Override
	public Class<? extends Biome> asClass() {
		return SavannaBiome.class;
	}
}
