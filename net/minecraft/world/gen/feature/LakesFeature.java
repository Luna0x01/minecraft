package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.class_3798;
import net.minecraft.class_3844;
import net.minecraft.class_3864;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;

public class LakesFeature extends class_3844<class_3864> {
	private static final BlockState field_19212 = Blocks.CAVE_AIR.getDefaultState();

	public boolean method_17343(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3864 arg) {
		while (blockPos.getY() > 5 && iWorld.method_8579(blockPos)) {
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
							Material material = iWorld.getBlockState(blockPos.add(s, u, t)).getMaterial();
							if (u >= 4 && material.isFluid()) {
								return false;
							}

							if (u < 4 && !material.isSolid() && iWorld.getBlockState(blockPos.add(s, u, t)).getBlock() != arg.field_19211) {
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
							iWorld.setBlockState(blockPos.add(v, x, w), x >= 4 ? field_19212 : arg.field_19211.getDefaultState(), 2);
						}
					}
				}
			}

			for (int y = 0; y < 16; y++) {
				for (int z = 0; z < 16; z++) {
					for (int aa = 4; aa < 8; aa++) {
						if (bls[(y * 16 + z) * 8 + aa]) {
							BlockPos blockPos2 = blockPos.add(y, aa - 1, z);
							if (Block.method_16588(iWorld.getBlockState(blockPos2).getBlock()) && iWorld.method_16370(LightType.SKY, blockPos.add(y, aa, z)) > 0) {
								Biome biome = iWorld.method_8577(blockPos2);
								if (biome.method_16450().method_17720().getBlock() == Blocks.MYCELIUM) {
									iWorld.setBlockState(blockPos2, Blocks.MYCELIUM.getDefaultState(), 2);
								} else {
									iWorld.setBlockState(blockPos2, Blocks.GRASS_BLOCK.getDefaultState(), 2);
								}
							}
						}
					}
				}
			}

			if (arg.field_19211.getDefaultState().getMaterial() == Material.LAVA) {
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
							if (bl2 && (ad < 4 || random.nextInt(2) != 0) && iWorld.getBlockState(blockPos.add(ab, ad, ac)).getMaterial().isSolid()) {
								iWorld.setBlockState(blockPos.add(ab, ad, ac), Blocks.STONE.getDefaultState(), 2);
							}
						}
					}
				}
			}

			if (arg.field_19211.getDefaultState().getMaterial() == Material.WATER) {
				for (int ae = 0; ae < 16; ae++) {
					for (int af = 0; af < 16; af++) {
						int ag = 4;
						BlockPos blockPos3 = blockPos.add(ae, 4, af);
						if (iWorld.method_8577(blockPos3).method_16427(iWorld, blockPos3, false)) {
							iWorld.setBlockState(blockPos3, Blocks.ICE.getDefaultState(), 2);
						}
					}
				}
			}

			return true;
		}
	}
}
