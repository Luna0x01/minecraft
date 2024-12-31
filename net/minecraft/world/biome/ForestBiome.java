package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.block.FlowerBlock;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.BirchTreeFeature;
import net.minecraft.world.gen.feature.DarkOakTreeFeature;
import net.minecraft.world.gen.feature.FoliageFeature;
import net.minecraft.world.gen.feature.HugeMushroomFeature;

public class ForestBiome extends Biome {
	protected static final BirchTreeFeature field_7234 = new BirchTreeFeature(false, true);
	protected static final BirchTreeFeature field_7235 = new BirchTreeFeature(false, false);
	protected static final DarkOakTreeFeature field_7236 = new DarkOakTreeFeature(false);
	private final ForestBiome.Type field_12535;

	public ForestBiome(ForestBiome.Type type, Biome.Settings settings) {
		super(settings);
		this.field_12535 = type;
		this.biomeDecorator.treesPerChunk = 10;
		this.biomeDecorator.grassPerChunk = 2;
		if (this.field_12535 == ForestBiome.Type.FLOWER) {
			this.biomeDecorator.treesPerChunk = 6;
			this.biomeDecorator.flowersPerChunk = 100;
			this.biomeDecorator.grassPerChunk = 1;
			this.passiveEntries.add(new Biome.SpawnEntry(RabbitEntity.class, 4, 2, 3));
		}

		if (this.field_12535 == ForestBiome.Type.NORMAL) {
			this.passiveEntries.add(new Biome.SpawnEntry(WolfEntity.class, 5, 4, 4));
		}

		if (this.field_12535 == ForestBiome.Type.ROOFED) {
			this.biomeDecorator.treesPerChunk = -999;
		}
	}

	@Override
	public FoliageFeature method_3822(Random random) {
		if (this.field_12535 == ForestBiome.Type.ROOFED && random.nextInt(3) > 0) {
			return field_7236;
		} else if (this.field_12535 == ForestBiome.Type.BIRCH || random.nextInt(5) == 0) {
			return field_7235;
		} else {
			return (FoliageFeature)(random.nextInt(10) == 0 ? field_4631 : JUNGLE_TREE_FEATURE);
		}
	}

	@Override
	public FlowerBlock.FlowerType pickFlower(Random random, BlockPos pos) {
		if (this.field_12535 == ForestBiome.Type.FLOWER) {
			double d = MathHelper.clamp((1.0 + FOLIAGE_NOISE.noise((double)pos.getX() / 48.0, (double)pos.getZ() / 48.0)) / 2.0, 0.0, 0.9999);
			FlowerBlock.FlowerType flowerType = FlowerBlock.FlowerType.values()[(int)(d * (double)FlowerBlock.FlowerType.values().length)];
			return flowerType == FlowerBlock.FlowerType.BLUE_ORCHID ? FlowerBlock.FlowerType.POPPY : flowerType;
		} else {
			return super.pickFlower(random, pos);
		}
	}

	@Override
	public void decorate(World world, Random random, BlockPos pos) {
		if (this.field_12535 == ForestBiome.Type.ROOFED) {
			this.method_11543(world, random, pos);
		}

		int i = random.nextInt(5) - 3;
		if (this.field_12535 == ForestBiome.Type.FLOWER) {
			i += 2;
		}

		this.method_11542(world, random, pos, i);
		super.decorate(world, random, pos);
	}

	protected void method_11543(World world, Random random, BlockPos blockPos) {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				int k = i * 4 + 1 + 8 + random.nextInt(3);
				int l = j * 4 + 1 + 8 + random.nextInt(3);
				BlockPos blockPos2 = world.getHighestBlock(blockPos.add(k, 0, l));
				if (random.nextInt(20) == 0) {
					HugeMushroomFeature hugeMushroomFeature = new HugeMushroomFeature();
					hugeMushroomFeature.generate(world, random, blockPos2);
				} else {
					FoliageFeature foliageFeature = this.method_3822(random);
					foliageFeature.setLeafRadius();
					if (foliageFeature.generate(world, random, blockPos2)) {
						foliageFeature.generateSurroundingFeatures(world, random, blockPos2);
					}
				}
			}
		}
	}

	protected void method_11542(World world, Random random, BlockPos blockPos, int i) {
		for (int j = 0; j < i; j++) {
			int k = random.nextInt(3);
			if (k == 0) {
				DOUBLE_PLANT_FEATURE.setType(DoublePlantBlock.DoublePlantType.SYRINGA);
			} else if (k == 1) {
				DOUBLE_PLANT_FEATURE.setType(DoublePlantBlock.DoublePlantType.ROSE);
			} else if (k == 2) {
				DOUBLE_PLANT_FEATURE.setType(DoublePlantBlock.DoublePlantType.PAEONIA);
			}

			for (int l = 0; l < 5; l++) {
				int m = random.nextInt(16) + 8;
				int n = random.nextInt(16) + 8;
				int o = random.nextInt(world.getHighestBlock(blockPos.add(m, 0, n)).getY() + 32);
				if (DOUBLE_PLANT_FEATURE.generate(world, random, new BlockPos(blockPos.getX() + m, o, blockPos.getZ() + n))) {
					break;
				}
			}
		}
	}

	@Override
	public Class<? extends Biome> asClass() {
		return ForestBiome.class;
	}

	@Override
	public int getGrassColor(BlockPos pos) {
		int i = super.getGrassColor(pos);
		return this.field_12535 == ForestBiome.Type.ROOFED ? (i & 16711422) + 2634762 >> 1 : i;
	}

	public static enum Type {
		NORMAL,
		FLOWER,
		BIRCH,
		ROOFED;
	}
}
