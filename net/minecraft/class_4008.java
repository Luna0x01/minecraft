package net.minecraft;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.NoiseGenerator;

public class class_4008 implements class_4012<class_4013> {
	private static final BlockState field_19467 = Blocks.CAVE_AIR.getDefaultState();
	private static final BlockState field_19468 = Blocks.NETHERRACK.getDefaultState();
	private static final BlockState field_19469 = Blocks.GRAVEL.getDefaultState();
	private static final BlockState field_19470 = Blocks.SOULSAND.getDefaultState();
	protected long field_19465;
	protected NoiseGenerator field_19466;

	public void method_17718(
		Random random, class_3781 arg, Biome biome, int i, int j, int k, double d, BlockState blockState, BlockState blockState2, int l, long m, class_4013 arg2
	) {
		int n = l + 1;
		int o = i & 15;
		int p = j & 15;
		double e = 0.03125;
		boolean bl = this.field_19466.method_17727((double)i * 0.03125, (double)j * 0.03125, 0.0) + random.nextDouble() * 0.2 > 0.0;
		boolean bl2 = this.field_19466.method_17727((double)i * 0.03125, 109.0, (double)j * 0.03125) + random.nextDouble() * 0.2 > 0.0;
		int q = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		int r = -1;
		BlockState blockState3 = field_19468;
		BlockState blockState4 = field_19468;

		for (int s = 127; s >= 0; s--) {
			mutable.setPosition(o, s, p);
			BlockState blockState5 = arg.getBlockState(mutable);
			if (blockState5.getBlock() != null && !blockState5.isAir()) {
				if (blockState5.getBlock() == blockState.getBlock()) {
					if (r == -1) {
						if (q <= 0) {
							blockState3 = field_19467;
							blockState4 = field_19468;
						} else if (s >= n - 4 && s <= n + 1) {
							blockState3 = field_19468;
							blockState4 = field_19468;
							if (bl2) {
								blockState3 = field_19469;
								blockState4 = field_19468;
							}

							if (bl) {
								blockState3 = field_19470;
								blockState4 = field_19470;
							}
						}

						if (s < n && (blockState3 == null || blockState3.isAir())) {
							blockState3 = blockState2;
						}

						r = q;
						if (s >= n - 1) {
							arg.method_16994(mutable, blockState3, false);
						} else {
							arg.method_16994(mutable, blockState4, false);
						}
					} else if (r > 0) {
						r--;
						arg.method_16994(mutable, blockState4, false);
					}
				}
			} else {
				r = -1;
			}
		}
	}

	@Override
	public void method_17717(long l) {
		if (this.field_19465 != l || this.field_19466 == null) {
			this.field_19466 = new NoiseGenerator(new class_3812(l), 4);
		}

		this.field_19465 = l;
	}
}
