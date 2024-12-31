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

public class SpruceTreeFeature extends AbstractTreeFeature<DefaultFeatureConfig> {
	private static final BlockState LOG = Blocks.field_10037.getDefaultState();
	private static final BlockState LEAVES = Blocks.field_9988.getDefaultState();

	public SpruceTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function, boolean bl) {
		super(function, bl);
	}

	@Override
	public boolean generate(
		Set<BlockPos> set, ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, MutableIntBoundingBox mutableIntBoundingBox
	) {
		int i = random.nextInt(4) + 6;
		int j = 1 + random.nextInt(2);
		int k = i - j;
		int l = 2 + random.nextInt(2);
		boolean bl = true;
		if (blockPos.getY() >= 1 && blockPos.getY() + i + 1 <= 256) {
			for (int m = blockPos.getY(); m <= blockPos.getY() + 1 + i && bl; m++) {
				int n;
				if (m - blockPos.getY() < j) {
					n = 0;
				} else {
					n = l;
				}

				BlockPos.Mutable mutable = new BlockPos.Mutable();

				for (int p = blockPos.getX() - n; p <= blockPos.getX() + n && bl; p++) {
					for (int q = blockPos.getZ() - n; q <= blockPos.getZ() + n && bl; q++) {
						if (m >= 0 && m < 256) {
							mutable.set(p, m, q);
							if (!isAirOrLeaves(modifiableTestableWorld, mutable)) {
								bl = false;
							}
						} else {
							bl = false;
						}
					}
				}
			}

			if (!bl) {
				return false;
			} else if (isDirtOrGrass(modifiableTestableWorld, blockPos.down()) && blockPos.getY() < 256 - i - 1) {
				this.setToDirt(modifiableTestableWorld, blockPos.down());
				int r = random.nextInt(2);
				int s = 1;
				int t = 0;

				for (int u = 0; u <= k; u++) {
					int v = blockPos.getY() + i - u;

					for (int w = blockPos.getX() - r; w <= blockPos.getX() + r; w++) {
						int x = w - blockPos.getX();

						for (int y = blockPos.getZ() - r; y <= blockPos.getZ() + r; y++) {
							int z = y - blockPos.getZ();
							if (Math.abs(x) != r || Math.abs(z) != r || r <= 0) {
								BlockPos blockPos2 = new BlockPos(w, v, y);
								if (isAirOrLeaves(modifiableTestableWorld, blockPos2) || isReplaceablePlant(modifiableTestableWorld, blockPos2)) {
									this.setBlockState(set, modifiableTestableWorld, blockPos2, LEAVES, mutableIntBoundingBox);
								}
							}
						}
					}

					if (r >= s) {
						r = t;
						t = 1;
						if (++s > l) {
							s = l;
						}
					} else {
						r++;
					}
				}

				int aa = random.nextInt(3);

				for (int ab = 0; ab < i - aa; ab++) {
					if (isAirOrLeaves(modifiableTestableWorld, blockPos.up(ab))) {
						this.setBlockState(set, modifiableTestableWorld, blockPos.up(ab), LOG, mutableIntBoundingBox);
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
