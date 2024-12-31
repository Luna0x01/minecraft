package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.InfestedBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkBlockStateStorage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FoliageFeature;
import net.minecraft.world.gen.feature.OreFeature;
import net.minecraft.world.gen.feature.SpruceTreeFeature;

public class ExtremeHillsBiome extends Biome {
	private Feature field_5478 = new OreFeature(Blocks.MONSTER_EGG.getDefaultState().with(InfestedBlock.VARIANT, InfestedBlock.Variants.STONE), 9);
	private SpruceTreeFeature field_7229 = new SpruceTreeFeature(false);
	private int field_7230 = 0;
	private int field_7231 = 1;
	private int field_7232 = 2;
	private int field_7233 = this.field_7230;

	protected ExtremeHillsBiome(int i, boolean bl) {
		super(i);
		if (bl) {
			this.biomeDecorator.treesPerChunk = 3;
			this.field_7233 = this.field_7231;
		}
	}

	@Override
	public FoliageFeature method_3822(Random random) {
		return (FoliageFeature)(random.nextInt(3) > 0 ? this.field_7229 : super.method_3822(random));
	}

	@Override
	public void decorate(World world, Random random, BlockPos pos) {
		super.decorate(world, random, pos);
		int i = 3 + random.nextInt(6);

		for (int j = 0; j < i; j++) {
			int k = random.nextInt(16);
			int l = random.nextInt(28) + 4;
			int m = random.nextInt(16);
			BlockPos blockPos = pos.add(k, l, m);
			if (world.getBlockState(blockPos).getBlock() == Blocks.STONE) {
				world.setBlockState(blockPos, Blocks.EMERALD_ORE.getDefaultState(), 2);
			}
		}

		for (int n = 0; n < 7; n++) {
			int o = random.nextInt(16);
			int p = random.nextInt(64);
			int q = random.nextInt(16);
			this.field_5478.generate(world, random, pos.add(o, p, q));
		}
	}

	@Override
	public void method_6420(World world, Random random, ChunkBlockStateStorage chunkStorage, int i, int j, double d) {
		this.topBlock = Blocks.GRASS.getDefaultState();
		this.baseBlock = Blocks.DIRT.getDefaultState();
		if ((d < -1.0 || d > 2.0) && this.field_7233 == this.field_7232) {
			this.topBlock = Blocks.GRAVEL.getDefaultState();
			this.baseBlock = Blocks.GRAVEL.getDefaultState();
		} else if (d > 1.0 && this.field_7233 != this.field_7231) {
			this.topBlock = Blocks.STONE.getDefaultState();
			this.baseBlock = Blocks.STONE.getDefaultState();
		}

		this.method_8590(world, random, chunkStorage, i, j, d);
	}

	private ExtremeHillsBiome method_6436(Biome biome) {
		this.field_7233 = this.field_7232;
		this.seedModifier(biome.field_4661, true);
		this.setName(biome.name + " M");
		this.setHeight(new Biome.Height(biome.depth, biome.variationModifier));
		this.setTemperatureAndDownfall(biome.temperature, biome.downfall);
		return this;
	}

	@Override
	protected Biome getMutatedVariant(int id) {
		return new ExtremeHillsBiome(id, false).method_6436(this);
	}
}
