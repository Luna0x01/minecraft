package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkBlockStateStorage;
import net.minecraft.world.gen.feature.FoliageFeature;

public class MutatedBiome extends Biome {
	protected Biome original;

	public MutatedBiome(int i, Biome biome) {
		super(i);
		this.original = biome;
		this.seedModifier(biome.field_4661, true);
		this.name = biome.name + " M";
		this.topBlock = biome.topBlock;
		this.baseBlock = biome.baseBlock;
		this.field_4619 = biome.field_4619;
		this.depth = biome.depth;
		this.variationModifier = biome.variationModifier;
		this.temperature = biome.temperature;
		this.downfall = biome.downfall;
		this.waterColor = biome.waterColor;
		this.mutated = biome.mutated;
		this.field_4635 = biome.field_4635;
		this.passiveEntries = Lists.newArrayList(biome.passiveEntries);
		this.monsterEntries = Lists.newArrayList(biome.monsterEntries);
		this.flyingEntries = Lists.newArrayList(biome.flyingEntries);
		this.waterEntries = Lists.newArrayList(biome.waterEntries);
		this.temperature = biome.temperature;
		this.downfall = biome.downfall;
		this.depth = biome.depth + 0.1F;
		this.variationModifier = biome.variationModifier + 0.2F;
	}

	@Override
	public void decorate(World world, Random random, BlockPos pos) {
		this.original.biomeDecorator.decorate(world, random, this, pos);
	}

	@Override
	public void method_6420(World world, Random random, ChunkBlockStateStorage chunkStorage, int i, int j, double d) {
		this.original.method_6420(world, random, chunkStorage, i, j, d);
	}

	@Override
	public float getMaxSpawnLimit() {
		return this.original.getMaxSpawnLimit();
	}

	@Override
	public FoliageFeature method_3822(Random random) {
		return this.original.method_3822(random);
	}

	@Override
	public int getFoliageColor(BlockPos pos) {
		return this.original.getFoliageColor(pos);
	}

	@Override
	public int getGrassColor(BlockPos pos) {
		return this.original.getGrassColor(pos);
	}

	@Override
	public Class<? extends Biome> asClass() {
		return this.original.asClass();
	}

	@Override
	public boolean method_6421(Biome biome) {
		return this.original.method_6421(biome);
	}

	@Override
	public Biome.Temperature getBiomeTemperature() {
		return this.original.getBiomeTemperature();
	}
}
