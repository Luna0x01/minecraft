package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.block.FlowerBlock;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.BirchTreeFeature;
import net.minecraft.world.gen.feature.DarkOakTreeFeature;
import net.minecraft.world.gen.feature.FoliageFeature;
import net.minecraft.world.gen.feature.HugeMushroomFeature;

public class ForestBiome extends Biome {
	private int field_7237;
	protected static final BirchTreeFeature field_7234 = new BirchTreeFeature(false, true);
	protected static final BirchTreeFeature field_7235 = new BirchTreeFeature(false, false);
	protected static final DarkOakTreeFeature field_7236 = new DarkOakTreeFeature(false);

	public ForestBiome(int i, int j) {
		super(i);
		this.field_7237 = j;
		this.biomeDecorator.treesPerChunk = 10;
		this.biomeDecorator.grassPerChunk = 2;
		if (this.field_7237 == 1) {
			this.biomeDecorator.treesPerChunk = 6;
			this.biomeDecorator.flowersPerChunk = 100;
			this.biomeDecorator.grassPerChunk = 1;
		}

		this.method_3820(5159473);
		this.setTemperatureAndDownfall(0.7F, 0.8F);
		if (this.field_7237 == 2) {
			this.field_7203 = 353825;
			this.field_4661 = 3175492;
			this.setTemperatureAndDownfall(0.6F, 0.6F);
		}

		if (this.field_7237 == 0) {
			this.passiveEntries.add(new Biome.SpawnEntry(WolfEntity.class, 5, 4, 4));
		}

		if (this.field_7237 == 3) {
			this.biomeDecorator.treesPerChunk = -999;
		}
	}

	@Override
	protected Biome seedModifier(int i, boolean bl) {
		if (this.field_7237 == 2) {
			this.field_7203 = 353825;
			this.field_4661 = i;
			if (bl) {
				this.field_7203 = (this.field_7203 & 16711422) >> 1;
			}

			return this;
		} else {
			return super.seedModifier(i, bl);
		}
	}

	@Override
	public FoliageFeature method_3822(Random random) {
		if (this.field_7237 == 3 && random.nextInt(3) > 0) {
			return field_7236;
		} else {
			return (FoliageFeature)(this.field_7237 != 2 && random.nextInt(5) != 0 ? this.JUNGLE_TREE_FEATURE : field_7235);
		}
	}

	@Override
	public FlowerBlock.FlowerType pickFlower(Random random, BlockPos pos) {
		if (this.field_7237 == 1) {
			double d = MathHelper.clamp((1.0 + FOLIAGE_NOISE.noise((double)pos.getX() / 48.0, (double)pos.getZ() / 48.0)) / 2.0, 0.0, 0.9999);
			FlowerBlock.FlowerType flowerType = FlowerBlock.FlowerType.values()[(int)(d * (double)FlowerBlock.FlowerType.values().length)];
			return flowerType == FlowerBlock.FlowerType.BLUE_ORCHID ? FlowerBlock.FlowerType.POPPY : flowerType;
		} else {
			return super.pickFlower(random, pos);
		}
	}

	@Override
	public void decorate(World world, Random random, BlockPos pos) {
		if (this.field_7237 == 3) {
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					int k = i * 4 + 1 + 8 + random.nextInt(3);
					int l = j * 4 + 1 + 8 + random.nextInt(3);
					BlockPos blockPos = world.getHighestBlock(pos.add(k, 0, l));
					if (random.nextInt(20) == 0) {
						HugeMushroomFeature hugeMushroomFeature = new HugeMushroomFeature();
						hugeMushroomFeature.generate(world, random, blockPos);
					} else {
						FoliageFeature foliageFeature = this.method_3822(random);
						foliageFeature.setLeafRadius();
						if (foliageFeature.generate(world, random, blockPos)) {
							foliageFeature.generateSurroundingFeatures(world, random, blockPos);
						}
					}
				}
			}
		}

		int m = random.nextInt(5) - 3;
		if (this.field_7237 == 1) {
			m += 2;
		}

		for (int n = 0; n < m; n++) {
			int o = random.nextInt(3);
			if (o == 0) {
				DOUBLE_PLANT_FEATURE.setType(DoublePlantBlock.DoublePlantType.SYRINGA);
			} else if (o == 1) {
				DOUBLE_PLANT_FEATURE.setType(DoublePlantBlock.DoublePlantType.ROSE);
			} else if (o == 2) {
				DOUBLE_PLANT_FEATURE.setType(DoublePlantBlock.DoublePlantType.PAEONIA);
			}

			for (int p = 0; p < 5; p++) {
				int q = random.nextInt(16) + 8;
				int r = random.nextInt(16) + 8;
				int s = random.nextInt(world.getHighestBlock(pos.add(q, 0, r)).getY() + 32);
				if (DOUBLE_PLANT_FEATURE.generate(world, random, new BlockPos(pos.getX() + q, s, pos.getZ() + r))) {
					break;
				}
			}
		}

		super.decorate(world, random, pos);
	}

	@Override
	public int getGrassColor(BlockPos pos) {
		int i = super.getGrassColor(pos);
		return this.field_7237 == 3 ? (i & 16711422) + 2634762 >> 1 : i;
	}

	@Override
	protected Biome getMutatedVariant(int id) {
		if (this.id == Biome.FOREST.id) {
			ForestBiome forestBiome = new ForestBiome(id, 1);
			forestBiome.setHeight(new Biome.Height(this.depth, this.variationModifier + 0.2F));
			forestBiome.setName("Flower Forest");
			forestBiome.seedModifier(6976549, true);
			forestBiome.method_3820(8233509);
			return forestBiome;
		} else {
			return this.id != Biome.BIRCH_FOREST.id && this.id != Biome.BIRCH_FOREST_HILLS.id ? new MutatedBiome(id, this) {
				@Override
				public void decorate(World world, Random random, BlockPos pos) {
					this.original.decorate(world, random, pos);
				}
			} : new MutatedBiome(id, this) {
				@Override
				public FoliageFeature method_3822(Random random) {
					return random.nextBoolean() ? ForestBiome.field_7234 : ForestBiome.field_7235;
				}
			};
		}
	}
}
