package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.entity.PolarBearEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.FoliageFeature;
import net.minecraft.world.gen.feature.IceDiskFeature;
import net.minecraft.world.gen.feature.IceSpikeFeature;
import net.minecraft.world.gen.feature.SpruceTreeFeature;

public class IceBiome extends Biome {
	private final boolean field_7240;
	private final IceSpikeFeature iceSpikeFeature = new IceSpikeFeature();
	private final IceDiskFeature field_7242 = new IceDiskFeature(4);

	public IceBiome(boolean bl, Biome.Settings settings) {
		super(settings);
		this.field_7240 = bl;
		if (bl) {
			this.topBlock = Blocks.SNOW.getDefaultState();
		}

		this.passiveEntries.clear();
		this.passiveEntries.add(new Biome.SpawnEntry(RabbitEntity.class, 10, 2, 3));
		this.passiveEntries.add(new Biome.SpawnEntry(PolarBearEntity.class, 1, 1, 2));
	}

	@Override
	public float getMaxSpawnLimit() {
		return 0.07F;
	}

	@Override
	public void decorate(World world, Random random, BlockPos pos) {
		if (this.field_7240) {
			for (int i = 0; i < 3; i++) {
				int j = random.nextInt(16) + 8;
				int k = random.nextInt(16) + 8;
				this.iceSpikeFeature.generate(world, random, world.getHighestBlock(pos.add(j, 0, k)));
			}

			for (int l = 0; l < 2; l++) {
				int m = random.nextInt(16) + 8;
				int n = random.nextInt(16) + 8;
				this.field_7242.generate(world, random, world.getHighestBlock(pos.add(m, 0, n)));
			}
		}

		super.decorate(world, random, pos);
	}

	@Override
	public FoliageFeature method_3822(Random random) {
		return new SpruceTreeFeature(false);
	}
}
