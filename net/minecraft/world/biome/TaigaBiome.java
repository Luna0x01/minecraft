package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.DirtBlock;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkBlockStateStorage;
import net.minecraft.world.gen.feature.BlockFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FoliageFeature;
import net.minecraft.world.gen.feature.GiantSpruceTreeFeature;
import net.minecraft.world.gen.feature.PineTreeFeature;
import net.minecraft.world.gen.feature.SpruceTreeFeature;
import net.minecraft.world.gen.feature.TallGrassFeature;

public class TaigaBiome extends Biome {
	private static final PineTreeFeature field_7254 = new PineTreeFeature();
	private static final SpruceTreeFeature field_7255 = new SpruceTreeFeature(false);
	private static final GiantSpruceTreeFeature field_7256 = new GiantSpruceTreeFeature(false, false);
	private static final GiantSpruceTreeFeature field_7257 = new GiantSpruceTreeFeature(false, true);
	private static final BlockFeature field_7258 = new BlockFeature(Blocks.MOSSY_COBBLESTONE, 0);
	private int field_7259;

	public TaigaBiome(int i, int j) {
		super(i);
		this.field_7259 = j;
		this.passiveEntries.add(new Biome.SpawnEntry(WolfEntity.class, 8, 4, 4));
		this.biomeDecorator.treesPerChunk = 10;
		if (j != 1 && j != 2) {
			this.biomeDecorator.grassPerChunk = 1;
			this.biomeDecorator.mushroomsPerChunk = 1;
		} else {
			this.biomeDecorator.grassPerChunk = 7;
			this.biomeDecorator.deadBushesPerChunk = 1;
			this.biomeDecorator.mushroomsPerChunk = 3;
		}
	}

	@Override
	public FoliageFeature method_3822(Random random) {
		if ((this.field_7259 == 1 || this.field_7259 == 2) && random.nextInt(3) == 0) {
			return this.field_7259 != 2 && random.nextInt(13) != 0 ? field_7256 : field_7257;
		} else {
			return (FoliageFeature)(random.nextInt(3) == 0 ? field_7254 : field_7255);
		}
	}

	@Override
	public Feature method_3828(Random random) {
		return random.nextInt(5) > 0 ? new TallGrassFeature(TallPlantBlock.GrassType.FERN) : new TallGrassFeature(TallPlantBlock.GrassType.GRASS);
	}

	@Override
	public void decorate(World world, Random random, BlockPos pos) {
		if (this.field_7259 == 1 || this.field_7259 == 2) {
			int i = random.nextInt(3);

			for (int j = 0; j < i; j++) {
				int k = random.nextInt(16) + 8;
				int l = random.nextInt(16) + 8;
				BlockPos blockPos = world.getHighestBlock(pos.add(k, 0, l));
				field_7258.generate(world, random, blockPos);
			}
		}

		DOUBLE_PLANT_FEATURE.setType(DoublePlantBlock.DoublePlantType.FERN);

		for (int m = 0; m < 7; m++) {
			int n = random.nextInt(16) + 8;
			int o = random.nextInt(16) + 8;
			int p = random.nextInt(world.getHighestBlock(pos.add(n, 0, o)).getY() + 32);
			DOUBLE_PLANT_FEATURE.generate(world, random, pos.add(n, p, o));
		}

		super.decorate(world, random, pos);
	}

	@Override
	public void method_6420(World world, Random random, ChunkBlockStateStorage chunkStorage, int i, int j, double d) {
		if (this.field_7259 == 1 || this.field_7259 == 2) {
			this.topBlock = Blocks.GRASS.getDefaultState();
			this.baseBlock = Blocks.DIRT.getDefaultState();
			if (d > 1.75) {
				this.topBlock = Blocks.DIRT.getDefaultState().with(DirtBlock.VARIANT, DirtBlock.DirtType.COARSE_DIRT);
			} else if (d > -0.95) {
				this.topBlock = Blocks.DIRT.getDefaultState().with(DirtBlock.VARIANT, DirtBlock.DirtType.PODZOL);
			}
		}

		this.method_8590(world, random, chunkStorage, i, j, d);
	}

	@Override
	protected Biome getMutatedVariant(int id) {
		return this.id == Biome.MEGA_TAIGA.id
			? new TaigaBiome(id, 2)
				.seedModifier(5858897, true)
				.setName("Mega Spruce Taiga")
				.method_3820(5159473)
				.setTemperatureAndDownfall(0.25F, 0.8F)
				.setHeight(new Biome.Height(this.depth, this.variationModifier))
			: super.getMutatedVariant(id);
	}
}
