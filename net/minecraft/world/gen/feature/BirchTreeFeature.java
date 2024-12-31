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

public class BirchTreeFeature extends AbstractTreeFeature<DefaultFeatureConfig> {
	private static final BlockState LOG = Blocks.field_10511.getDefaultState();
	private static final BlockState LEAVES = Blocks.field_10539.getDefaultState();
	private final boolean alwaysTall;

	public BirchTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function, boolean bl, boolean bl2) {
		super(function, bl);
		this.alwaysTall = bl2;
	}

	@Override
	public boolean generate(
		Set<BlockPos> set, ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, MutableIntBoundingBox mutableIntBoundingBox
	) {
		int i = random.nextInt(3) + 5;
		if (this.alwaysTall) {
			i += random.nextInt(7);
		}

		boolean bl = true;
		if (blockPos.getY() >= 1 && blockPos.getY() + i + 1 <= 256) {
			for (int j = blockPos.getY(); j <= blockPos.getY() + 1 + i; j++) {
				int k = 1;
				if (j == blockPos.getY()) {
					k = 0;
				}

				if (j >= blockPos.getY() + 1 + i - 2) {
					k = 2;
				}

				BlockPos.Mutable mutable = new BlockPos.Mutable();

				for (int l = blockPos.getX() - k; l <= blockPos.getX() + k && bl; l++) {
					for (int m = blockPos.getZ() - k; m <= blockPos.getZ() + k && bl; m++) {
						if (j < 0 || j >= 256) {
							bl = false;
						} else if (!canTreeReplace(modifiableTestableWorld, mutable.set(l, j, m))) {
							bl = false;
						}
					}
				}
			}

			if (!bl) {
				return false;
			} else if (isDirtOrGrass(modifiableTestableWorld, blockPos.down()) && blockPos.getY() < 256 - i - 1) {
				this.setToDirt(modifiableTestableWorld, blockPos.down());

				for (int n = blockPos.getY() - 3 + i; n <= blockPos.getY() + i; n++) {
					int o = n - (blockPos.getY() + i);
					int p = 1 - o / 2;

					for (int q = blockPos.getX() - p; q <= blockPos.getX() + p; q++) {
						int r = q - blockPos.getX();

						for (int s = blockPos.getZ() - p; s <= blockPos.getZ() + p; s++) {
							int t = s - blockPos.getZ();
							if (Math.abs(r) != p || Math.abs(t) != p || random.nextInt(2) != 0 && o != 0) {
								BlockPos blockPos2 = new BlockPos(q, n, s);
								if (isAirOrLeaves(modifiableTestableWorld, blockPos2)) {
									this.setBlockState(set, modifiableTestableWorld, blockPos2, LEAVES, mutableIntBoundingBox);
								}
							}
						}
					}
				}

				for (int u = 0; u < i; u++) {
					if (isAirOrLeaves(modifiableTestableWorld, blockPos.up(u))) {
						this.setBlockState(set, modifiableTestableWorld, blockPos.up(u), LOG, mutableIntBoundingBox);
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
