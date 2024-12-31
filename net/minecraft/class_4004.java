package net.minecraft;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.PerlinNoiseGenerator;
import net.minecraft.world.biome.Biome;

public class class_4004 implements class_4012<class_4013> {
	protected static final BlockState field_19439 = Blocks.PACKED_ICE.getDefaultState();
	protected static final BlockState field_19440 = Blocks.SNOW_BLOCK.getDefaultState();
	private static final BlockState field_19441 = Blocks.AIR.getDefaultState();
	private static final BlockState field_19442 = Blocks.GRAVEL.getDefaultState();
	private static final BlockState field_19443 = Blocks.ICE.getDefaultState();
	private PerlinNoiseGenerator field_19444;
	private PerlinNoiseGenerator field_19445;
	private long field_19446;

	public void method_17718(
		Random random, class_3781 arg, Biome biome, int i, int j, int k, double d, BlockState blockState, BlockState blockState2, int l, long m, class_4013 arg2
	) {
		double e = 0.0;
		double f = 0.0;
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		float g = biome.getTemperature(mutable.setPosition(i, 63, j));
		double h = Math.min(Math.abs(d), this.field_19444.noise((double)i * 0.1, (double)j * 0.1));
		if (h > 1.8) {
			double n = 0.09765625;
			double o = Math.abs(this.field_19445.noise((double)i * 0.09765625, (double)j * 0.09765625));
			e = h * h * 1.2;
			double p = Math.ceil(o * 40.0) + 14.0;
			if (e > p) {
				e = p;
			}

			if (g > 0.1F) {
				e -= 2.0;
			}

			if (e > 2.0) {
				f = (double)l - e - 7.0;
				e += (double)l;
			} else {
				e = 0.0;
			}
		}

		int q = i & 15;
		int r = j & 15;
		BlockState blockState3 = biome.method_16450().method_17721();
		BlockState blockState4 = biome.method_16450().method_17720();
		int s = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
		int t = -1;
		int u = 0;
		int v = 2 + random.nextInt(4);
		int w = l + 18 + random.nextInt(10);

		for (int x = Math.max(k, (int)e + 1); x >= 0; x--) {
			mutable.setPosition(q, x, r);
			if (arg.getBlockState(mutable).isAir() && x < (int)e && random.nextDouble() > 0.01) {
				arg.method_16994(mutable, field_19439, false);
			} else if (arg.getBlockState(mutable).getMaterial() == Material.WATER && x > (int)f && x < l && f != 0.0 && random.nextDouble() > 0.15) {
				arg.method_16994(mutable, field_19439, false);
			}

			BlockState blockState5 = arg.getBlockState(mutable);
			if (blockState5.isAir()) {
				t = -1;
			} else if (blockState5.getBlock() == blockState.getBlock()) {
				if (t == -1) {
					if (s <= 0) {
						blockState4 = field_19441;
						blockState3 = blockState;
					} else if (x >= l - 4 && x <= l + 1) {
						blockState4 = biome.method_16450().method_17720();
						blockState3 = biome.method_16450().method_17721();
					}

					if (x < l && (blockState4 == null || blockState4.isAir())) {
						if (biome.getTemperature(mutable.setPosition(i, x, j)) < 0.15F) {
							blockState4 = field_19443;
						} else {
							blockState4 = blockState2;
						}
					}

					t = s;
					if (x >= l - 1) {
						arg.method_16994(mutable, blockState4, false);
					} else if (x < l - 7 - s) {
						blockState4 = field_19441;
						blockState3 = blockState;
						arg.method_16994(mutable, field_19442, false);
					} else {
						arg.method_16994(mutable, blockState3, false);
					}
				} else if (t > 0) {
					t--;
					arg.method_16994(mutable, blockState3, false);
					if (t == 0 && blockState3.getBlock() == Blocks.SAND && s > 1) {
						t = random.nextInt(4) + Math.max(0, x - 63);
						blockState3 = blockState3.getBlock() == Blocks.RED_SAND ? Blocks.RED_SANDSTONE.getDefaultState() : Blocks.SANDSTONE.getDefaultState();
					}
				}
			} else if (blockState5.getBlock() == Blocks.PACKED_ICE && u <= v && x > w) {
				arg.method_16994(mutable, field_19440, false);
				u++;
			}
		}
	}

	@Override
	public void method_17717(long l) {
		if (this.field_19446 != l || this.field_19444 == null || this.field_19445 == null) {
			Random random = new class_3812(l);
			this.field_19444 = new PerlinNoiseGenerator(random, 4);
			this.field_19445 = new PerlinNoiseGenerator(random, 1);
		}

		this.field_19446 = l;
	}
}
