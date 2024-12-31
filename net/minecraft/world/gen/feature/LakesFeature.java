package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class LakesFeature extends Feature {
	private final Block block;

	public LakesFeature(Block block) {
		this.block = block;
	}

	@Override
	public boolean generate(World world, Random random, BlockPos blockPos) {
		blockPos = blockPos.add(-8, 0, -8);

		while (blockPos.getY() > 5 && world.isAir(blockPos)) {
			blockPos = blockPos.down();
		}

		if (blockPos.getY() <= 4) {
			return false;
		} else {
			blockPos = blockPos.down(4);
			boolean[] bls = new boolean[2048];
			int i = random.nextInt(4) + 4;

			for (int j = 0; j < i; j++) {
				double d = random.nextDouble() * 6.0 + 3.0;
				double e = random.nextDouble() * 4.0 + 2.0;
				double f = random.nextDouble() * 6.0 + 3.0;
				double g = random.nextDouble() * (16.0 - d - 2.0) + 1.0 + d / 2.0;
				double h = random.nextDouble() * (8.0 - e - 4.0) + 2.0 + e / 2.0;
				double k = random.nextDouble() * (16.0 - f - 2.0) + 1.0 + f / 2.0;

				for (int l = 1; l < 15; l++) {
					for (int m = 1; m < 15; m++) {
						for (int n = 1; n < 7; n++) {
							double o = ((double)l - g) / (d / 2.0);
							double p = ((double)n - h) / (e / 2.0);
							double q = ((double)m - k) / (f / 2.0);
							double r = o * o + p * p + q * q;
							if (r < 1.0) {
								bls[(l * 16 + m) * 8 + n] = true;
							}
						}
					}
				}
			}

			for (int s = 0; s < 16; s++) {
				for (int t = 0; t < 16; t++) {
					for (int u = 0; u < 8; u++) {
						boolean bl = !bls[(s * 16 + t) * 8 + u]
							&& (
								s < 15 && bls[((s + 1) * 16 + t) * 8 + u]
									|| s > 0 && bls[((s - 1) * 16 + t) * 8 + u]
									|| t < 15 && bls[(s * 16 + t + 1) * 8 + u]
									|| t > 0 && bls[(s * 16 + (t - 1)) * 8 + u]
									|| u < 7 && bls[(s * 16 + t) * 8 + u + 1]
									|| u > 0 && bls[(s * 16 + t) * 8 + (u - 1)]
							);
						if (bl) {
							Material material = world.getBlockState(blockPos.add(s, u, t)).getMaterial();
							if (u >= 4 && material.isFluid()) {
								return false;
							}

							if (u < 4 && !material.isSolid() && world.getBlockState(blockPos.add(s, u, t)).getBlock() != this.block) {
								return false;
							}
						}
					}
				}
			}

			for (int v = 0; v < 16; v++) {
				for (int w = 0; w < 16; w++) {
					for (int x = 0; x < 8; x++) {
						if (bls[(v * 16 + w) * 8 + x]) {
							world.setBlockState(blockPos.add(v, x, w), x >= 4 ? Blocks.AIR.getDefaultState() : this.block.getDefaultState(), 2);
						}
					}
				}
			}

			for (int y = 0; y < 16; y++) {
				for (int z = 0; z < 16; z++) {
					for (int aa = 4; aa < 8; aa++) {
						if (bls[(y * 16 + z) * 8 + aa]) {
							BlockPos blockPos2 = blockPos.add(y, aa - 1, z);
							if (world.getBlockState(blockPos2).getBlock() == Blocks.DIRT && world.getLightAtPos(LightType.SKY, blockPos.add(y, aa, z)) > 0) {
								Biome biome = world.getBiome(blockPos2);
								if (biome.topBlock.getBlock() == Blocks.MYCELIUM) {
									world.setBlockState(blockPos2, Blocks.MYCELIUM.getDefaultState(), 2);
								} else {
									world.setBlockState(blockPos2, Blocks.GRASS.getDefaultState(), 2);
								}
							}
						}
					}
				}
			}

			if (this.block.getDefaultState().getMaterial() == Material.LAVA) {
				for (int ab = 0; ab < 16; ab++) {
					for (int ac = 0; ac < 16; ac++) {
						for (int ad = 0; ad < 8; ad++) {
							boolean bl2 = !bls[(ab * 16 + ac) * 8 + ad]
								&& (
									ab < 15 && bls[((ab + 1) * 16 + ac) * 8 + ad]
										|| ab > 0 && bls[((ab - 1) * 16 + ac) * 8 + ad]
										|| ac < 15 && bls[(ab * 16 + ac + 1) * 8 + ad]
										|| ac > 0 && bls[(ab * 16 + (ac - 1)) * 8 + ad]
										|| ad < 7 && bls[(ab * 16 + ac) * 8 + ad + 1]
										|| ad > 0 && bls[(ab * 16 + ac) * 8 + (ad - 1)]
								);
							if (bl2 && (ad < 4 || random.nextInt(2) != 0) && world.getBlockState(blockPos.add(ab, ad, ac)).getMaterial().isSolid()) {
								world.setBlockState(blockPos.add(ab, ad, ac), Blocks.STONE.getDefaultState(), 2);
							}
						}
					}
				}
			}

			if (this.block.getDefaultState().getMaterial() == Material.WATER) {
				for (int ae = 0; ae < 16; ae++) {
					for (int af = 0; af < 16; af++) {
						int ag = 4;
						if (world.canWaterFreezeAt(blockPos.add(ae, 4, af))) {
							world.setBlockState(blockPos.add(ae, 4, af), Blocks.ICE.getDefaultState(), 2);
						}
					}
				}
			}

			return true;
		}
	}
}
