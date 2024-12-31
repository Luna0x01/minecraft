package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.TestableWorld;

public class DarkOakTreeFeature extends AbstractTreeFeature<DefaultFeatureConfig> {
	private static final BlockState LOG = Blocks.field_10010.getDefaultState();
	private static final BlockState LEAVES = Blocks.field_10035.getDefaultState();

	public DarkOakTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function, boolean bl) {
		super(function, bl);
	}

	@Override
	public boolean generate(
		Set<BlockPos> set, ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, MutableIntBoundingBox mutableIntBoundingBox
	) {
		int i = random.nextInt(3) + random.nextInt(2) + 6;
		int j = blockPos.getX();
		int k = blockPos.getY();
		int l = blockPos.getZ();
		if (k >= 1 && k + i + 1 < 256) {
			BlockPos blockPos2 = blockPos.down();
			if (!isNaturalDirtOrGrass(modifiableTestableWorld, blockPos2)) {
				return false;
			} else if (!this.doesTreeFit(modifiableTestableWorld, blockPos, i)) {
				return false;
			} else {
				this.setToDirt(modifiableTestableWorld, blockPos2);
				this.setToDirt(modifiableTestableWorld, blockPos2.east());
				this.setToDirt(modifiableTestableWorld, blockPos2.south());
				this.setToDirt(modifiableTestableWorld, blockPos2.south().east());
				Direction direction = Direction.Type.field_11062.random(random);
				int m = i - random.nextInt(4);
				int n = 2 - random.nextInt(3);
				int o = j;
				int p = l;
				int q = k + i - 1;

				for (int r = 0; r < i; r++) {
					if (r >= m && n > 0) {
						o += direction.getOffsetX();
						p += direction.getOffsetZ();
						n--;
					}

					int s = k + r;
					BlockPos blockPos3 = new BlockPos(o, s, p);
					if (isAirOrLeaves(modifiableTestableWorld, blockPos3)) {
						this.addLog(set, modifiableTestableWorld, blockPos3, mutableIntBoundingBox);
						this.addLog(set, modifiableTestableWorld, blockPos3.east(), mutableIntBoundingBox);
						this.addLog(set, modifiableTestableWorld, blockPos3.south(), mutableIntBoundingBox);
						this.addLog(set, modifiableTestableWorld, blockPos3.east().south(), mutableIntBoundingBox);
					}
				}

				for (int t = -2; t <= 0; t++) {
					for (int u = -2; u <= 0; u++) {
						int v = -1;
						this.addLeaves(modifiableTestableWorld, o + t, q + v, p + u, mutableIntBoundingBox, set);
						this.addLeaves(modifiableTestableWorld, 1 + o - t, q + v, p + u, mutableIntBoundingBox, set);
						this.addLeaves(modifiableTestableWorld, o + t, q + v, 1 + p - u, mutableIntBoundingBox, set);
						this.addLeaves(modifiableTestableWorld, 1 + o - t, q + v, 1 + p - u, mutableIntBoundingBox, set);
						if ((t > -2 || u > -1) && (t != -1 || u != -2)) {
							int var29 = 1;
							this.addLeaves(modifiableTestableWorld, o + t, q + var29, p + u, mutableIntBoundingBox, set);
							this.addLeaves(modifiableTestableWorld, 1 + o - t, q + var29, p + u, mutableIntBoundingBox, set);
							this.addLeaves(modifiableTestableWorld, o + t, q + var29, 1 + p - u, mutableIntBoundingBox, set);
							this.addLeaves(modifiableTestableWorld, 1 + o - t, q + var29, 1 + p - u, mutableIntBoundingBox, set);
						}
					}
				}

				if (random.nextBoolean()) {
					this.addLeaves(modifiableTestableWorld, o, q + 2, p, mutableIntBoundingBox, set);
					this.addLeaves(modifiableTestableWorld, o + 1, q + 2, p, mutableIntBoundingBox, set);
					this.addLeaves(modifiableTestableWorld, o + 1, q + 2, p + 1, mutableIntBoundingBox, set);
					this.addLeaves(modifiableTestableWorld, o, q + 2, p + 1, mutableIntBoundingBox, set);
				}

				for (int w = -3; w <= 4; w++) {
					for (int x = -3; x <= 4; x++) {
						if ((w != -3 || x != -3) && (w != -3 || x != 4) && (w != 4 || x != -3) && (w != 4 || x != 4) && (Math.abs(w) < 3 || Math.abs(x) < 3)) {
							this.addLeaves(modifiableTestableWorld, o + w, q, p + x, mutableIntBoundingBox, set);
						}
					}
				}

				for (int y = -1; y <= 2; y++) {
					for (int z = -1; z <= 2; z++) {
						if ((y < 0 || y > 1 || z < 0 || z > 1) && random.nextInt(3) <= 0) {
							int aa = random.nextInt(3) + 2;

							for (int ab = 0; ab < aa; ab++) {
								this.addLog(set, modifiableTestableWorld, new BlockPos(j + y, q - ab - 1, l + z), mutableIntBoundingBox);
							}

							for (int ac = -1; ac <= 1; ac++) {
								for (int ad = -1; ad <= 1; ad++) {
									this.addLeaves(modifiableTestableWorld, o + y + ac, q, p + z + ad, mutableIntBoundingBox, set);
								}
							}

							for (int ae = -2; ae <= 2; ae++) {
								for (int af = -2; af <= 2; af++) {
									if (Math.abs(ae) != 2 || Math.abs(af) != 2) {
										this.addLeaves(modifiableTestableWorld, o + y + ae, q - 1, p + z + af, mutableIntBoundingBox, set);
									}
								}
							}
						}
					}
				}

				return true;
			}
		} else {
			return false;
		}
	}

	private boolean doesTreeFit(TestableWorld testableWorld, BlockPos blockPos, int i) {
		int j = blockPos.getX();
		int k = blockPos.getY();
		int l = blockPos.getZ();
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int m = 0; m <= i + 1; m++) {
			int n = 1;
			if (m == 0) {
				n = 0;
			}

			if (m >= i - 1) {
				n = 2;
			}

			for (int o = -n; o <= n; o++) {
				for (int p = -n; p <= n; p++) {
					if (!canTreeReplace(testableWorld, mutable.set(j + o, k + m, l + p))) {
						return false;
					}
				}
			}
		}

		return true;
	}

	private void addLog(Set<BlockPos> set, ModifiableTestableWorld modifiableTestableWorld, BlockPos blockPos, MutableIntBoundingBox mutableIntBoundingBox) {
		if (canTreeReplace(modifiableTestableWorld, blockPos)) {
			this.setBlockState(set, modifiableTestableWorld, blockPos, LOG, mutableIntBoundingBox);
		}
	}

	private void addLeaves(ModifiableTestableWorld modifiableTestableWorld, int i, int j, int k, MutableIntBoundingBox mutableIntBoundingBox, Set<BlockPos> set) {
		BlockPos blockPos = new BlockPos(i, j, k);
		if (isAir(modifiableTestableWorld, blockPos)) {
			this.setBlockState(set, modifiableTestableWorld, blockPos, LEAVES, mutableIntBoundingBox);
		}
	}
}
