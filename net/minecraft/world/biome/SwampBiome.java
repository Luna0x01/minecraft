package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkBlockStateStorage;
import net.minecraft.world.gen.feature.FoliageFeature;

public class SwampBiome extends Biome {
	protected static final BlockState field_12547 = Blocks.LILY_PAD.getDefaultState();

	protected SwampBiome(Biome.Settings settings) {
		super(settings);
		this.biomeDecorator.treesPerChunk = 2;
		this.biomeDecorator.flowersPerChunk = 1;
		this.biomeDecorator.deadBushesPerChunk = 1;
		this.biomeDecorator.mushroomsPerChunk = 8;
		this.biomeDecorator.sugarcanePerChunk = 10;
		this.biomeDecorator.clayPerChunk = 1;
		this.biomeDecorator.lilyPadsPerChunk = 4;
		this.biomeDecorator.sandDisksPerChunk = 0;
		this.biomeDecorator.gravelDisksPerChunk = 0;
		this.biomeDecorator.grassPerChunk = 5;
		this.monsterEntries.add(new Biome.SpawnEntry(SlimeEntity.class, 1, 1, 1));
	}

	@Override
	public FoliageFeature method_3822(Random random) {
		return OAK_TREE_FEATURE;
	}

	@Override
	public int getGrassColor(BlockPos pos) {
		double d = FOLIAGE_NOISE.noise((double)pos.getX() * 0.0225, (double)pos.getZ() * 0.0225);
		return d < -0.1 ? 5011004 : 6975545;
	}

	@Override
	public int getFoliageColor(BlockPos pos) {
		return 6975545;
	}

	@Override
	public FlowerBlock.FlowerType pickFlower(Random random, BlockPos pos) {
		return FlowerBlock.FlowerType.BLUE_ORCHID;
	}

	@Override
	public void method_6420(World world, Random random, ChunkBlockStateStorage chunkStorage, int i, int j, double d) {
		double e = FOLIAGE_NOISE.noise((double)i * 0.25, (double)j * 0.25);
		if (e > 0.0) {
			int k = i & 15;
			int l = j & 15;

			for (int m = 255; m >= 0; m--) {
				if (chunkStorage.get(l, m, k).getMaterial() != Material.AIR) {
					if (m == 62 && chunkStorage.get(l, m, k).getBlock() != Blocks.WATER) {
						chunkStorage.set(l, m, k, waterBlockState);
						if (e < 0.12) {
							chunkStorage.set(l, m + 1, k, field_12547);
						}
					}
					break;
				}
			}
		}

		this.method_8590(world, random, chunkStorage, i, j, d);
	}
}
