package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.DirtBlock;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkBlockStateStorage;
import net.minecraft.world.gen.feature.AcaciaTreeFeature;
import net.minecraft.world.gen.feature.FoliageFeature;

public class SavannaBiome extends Biome {
	private static final AcaciaTreeFeature field_7253 = new AcaciaTreeFeature(false);

	protected SavannaBiome(int i) {
		super(i);
		this.passiveEntries.add(new Biome.SpawnEntry(HorseBaseEntity.class, 1, 2, 6));
		this.biomeDecorator.treesPerChunk = 1;
		this.biomeDecorator.flowersPerChunk = 4;
		this.biomeDecorator.grassPerChunk = 20;
	}

	@Override
	public FoliageFeature method_3822(Random random) {
		return (FoliageFeature)(random.nextInt(5) > 0 ? field_7253 : this.JUNGLE_TREE_FEATURE);
	}

	@Override
	protected Biome getMutatedVariant(int id) {
		Biome biome = new SavannaBiome.ShatteredSavannaBiome(id, this);
		biome.temperature = (this.temperature + 1.0F) * 0.5F;
		biome.depth = this.depth * 0.5F + 0.3F;
		biome.variationModifier = this.variationModifier * 0.5F + 1.2F;
		return biome;
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

	public static class ShatteredSavannaBiome extends MutatedBiome {
		public ShatteredSavannaBiome(int i, Biome biome) {
			super(i, biome);
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
}
