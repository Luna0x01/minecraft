package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.FoliageFeature;
import net.minecraft.world.gen.feature.IceDiskFeature;
import net.minecraft.world.gen.feature.IceSpikeFeature;
import net.minecraft.world.gen.feature.SpruceTreeFeature;

public class IceBiome extends Biome {
	private boolean field_7240;
	private IceSpikeFeature iceSpikeFeature = new IceSpikeFeature();
	private IceDiskFeature field_7242 = new IceDiskFeature(4);

	public IceBiome(int i, boolean bl) {
		super(i);
		this.field_7240 = bl;
		if (bl) {
			this.topBlock = Blocks.SNOW.getDefaultState();
		}

		this.passiveEntries.clear();
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

	@Override
	protected Biome getMutatedVariant(int id) {
		Biome biome = new IceBiome(id, true)
			.seedModifier(13828095, true)
			.setName(this.name + " Spikes")
			.setMutated()
			.setTemperatureAndDownfall(0.0F, 0.5F)
			.setHeight(new Biome.Height(this.depth + 0.1F, this.variationModifier + 0.1F));
		biome.depth = this.depth + 0.3F;
		biome.variationModifier = this.variationModifier + 0.4F;
		return biome;
	}
}
