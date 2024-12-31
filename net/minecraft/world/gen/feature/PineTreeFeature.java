package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.ModifiableTestableWorld;

public class PineTreeFeature extends AbstractTreeFeature<DefaultFeatureConfig> {
	private static final BlockState LOG = Blocks.field_10037.getDefaultState();
	private static final BlockState LEAVES = Blocks.field_9988.getDefaultState();

	public PineTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function, false);
	}

	@Override
	public boolean generate(
		Set<BlockPos> set, ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, MutableIntBoundingBox mutableIntBoundingBox
	) {
		int i = random.nextInt(5) + 7;
		int j = i - random.nextInt(2) - 3;
		int k = i - j;
		int l = 1 + random.nextInt(k + 1);
		if (blockPos.getY() >= 1 && blockPos.getY() + i + 1 <= 256) {
			boolean bl = true;

			for (int m = blockPos.getY(); m <= blockPos.getY() + 1 + i && bl; m++) {
				int n = 1;
				if (m - blockPos.getY() < j) {
					n = 0;
				} else {
					n = l;
				}

				BlockPos.Mutable mutable = new BlockPos.Mutable();

				for (int o = blockPos.getX() - n; o <= blockPos.getX() + n && bl; o++) {
					for (int p = blockPos.getZ() - n; p <= blockPos.getZ() + n && bl; p++) {
						if (m < 0 || m >= 256) {
							bl = false;
						} else if (!canTreeReplace(modifiableTestableWorld, mutable.set(o, m, p))) {
							bl = false;
						}
					}
				}
			}

			if (!bl) {
				return false;
			} else if (isNaturalDirtOrGrass(modifiableTestableWorld, blockPos.down()) && blockPos.getY() < 256 - i - 1) {
				this.setToDirt(modifiableTestableWorld, blockPos.down());
				int q = 0;

				for (int r = blockPos.getY() + i; r >= blockPos.getY() + j; r--) {
					for (int s = blockPos.getX() - q; s <= blockPos.getX() + q; s++) {
						int t = s - blockPos.getX();

						for (int u = blockPos.getZ() - q; u <= blockPos.getZ() + q; u++) {
							int v = u - blockPos.getZ();
							if (Math.abs(t) != q || Math.abs(v) != q || q <= 0) {
								BlockPos blockPos2 = new BlockPos(s, r, u);
								if (isAirOrLeaves(modifiableTestableWorld, blockPos2)) {
									this.setBlockState(set, modifiableTestableWorld, blockPos2, LEAVES, mutableIntBoundingBox);
								}
							}
						}
					}

					if (q >= 1 && r == blockPos.getY() + j + 1) {
						q--;
					} else if (q < l) {
						q++;
					}
				}

				for (int w = 0; w < i - 1; w++) {
					if (isAirOrLeaves(modifiableTestableWorld, blockPos.up(w))) {
						this.setBlockState(set, modifiableTestableWorld, blockPos.up(w), LOG, mutableIntBoundingBox);
					}
				}

				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
