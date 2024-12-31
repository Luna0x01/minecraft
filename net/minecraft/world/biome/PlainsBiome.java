package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.block.FlowerBlock;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PlainsBiome extends Biome {
	protected boolean field_7252;

	protected PlainsBiome(boolean bl, Biome.Settings settings) {
		super(settings);
		this.field_7252 = bl;
		this.passiveEntries.add(new Biome.SpawnEntry(HorseBaseEntity.class, 5, 2, 6));
		this.biomeDecorator.treesPerChunk = -999;
		this.biomeDecorator.flowersPerChunk = 4;
		this.biomeDecorator.grassPerChunk = 10;
	}

	@Override
	public FlowerBlock.FlowerType pickFlower(Random random, BlockPos pos) {
		double d = FOLIAGE_NOISE.noise((double)pos.getX() / 200.0, (double)pos.getZ() / 200.0);
		if (d < -0.8) {
			int i = random.nextInt(4);
			switch (i) {
				case 0:
					return FlowerBlock.FlowerType.ORANGE_TULIP;
				case 1:
					return FlowerBlock.FlowerType.RED_TULIP;
				case 2:
					return FlowerBlock.FlowerType.PINK_TULIP;
				case 3:
				default:
					return FlowerBlock.FlowerType.WHITE_TULIP;
			}
		} else if (random.nextInt(3) > 0) {
			int j = random.nextInt(3);
			if (j == 0) {
				return FlowerBlock.FlowerType.POPPY;
			} else {
				return j == 1 ? FlowerBlock.FlowerType.HOUSTONIA : FlowerBlock.FlowerType.OXEYE_DAISY;
			}
		} else {
			return FlowerBlock.FlowerType.DANDELION;
		}
	}

	@Override
	public void decorate(World world, Random random, BlockPos pos) {
		double d = FOLIAGE_NOISE.noise((double)(pos.getX() + 8) / 200.0, (double)(pos.getZ() + 8) / 200.0);
		if (d < -0.8) {
			this.biomeDecorator.flowersPerChunk = 15;
			this.biomeDecorator.grassPerChunk = 5;
		} else {
			this.biomeDecorator.flowersPerChunk = 4;
			this.biomeDecorator.grassPerChunk = 10;
			DOUBLE_PLANT_FEATURE.setType(DoublePlantBlock.DoublePlantType.GRASS);

			for (int i = 0; i < 7; i++) {
				int j = random.nextInt(16) + 8;
				int k = random.nextInt(16) + 8;
				int l = random.nextInt(world.getHighestBlock(pos.add(j, 0, k)).getY() + 32);
				DOUBLE_PLANT_FEATURE.generate(world, random, pos.add(j, l, k));
			}
		}

		if (this.field_7252) {
			DOUBLE_PLANT_FEATURE.setType(DoublePlantBlock.DoublePlantType.SUNFLOWER);

			for (int m = 0; m < 10; m++) {
				int n = random.nextInt(16) + 8;
				int o = random.nextInt(16) + 8;
				int p = random.nextInt(world.getHighestBlock(pos.add(n, 0, o)).getY() + 32);
				DOUBLE_PLANT_FEATURE.generate(world, random, pos.add(n, p, o));
			}
		}

		super.decorate(world, random, pos);
	}
}
