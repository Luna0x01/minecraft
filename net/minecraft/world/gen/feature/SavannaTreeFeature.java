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
import net.minecraft.world.ModifiableWorld;

public class SavannaTreeFeature extends AbstractTreeFeature<DefaultFeatureConfig> {
	private static final BlockState LOG = Blocks.field_10533.getDefaultState();
	private static final BlockState LEAVES = Blocks.field_10098.getDefaultState();

	public SavannaTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function, boolean bl) {
		super(function, bl);
	}

	@Override
	public boolean generate(
		Set<BlockPos> set, ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, MutableIntBoundingBox mutableIntBoundingBox
	) {
		int i = random.nextInt(3) + random.nextInt(3) + 5;
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
			} else if (isNaturalDirtOrGrass(modifiableTestableWorld, blockPos.down()) && blockPos.getY() < 256 - i - 1) {
				this.setToDirt(modifiableTestableWorld, blockPos.down());
				Direction direction = Direction.Type.field_11062.random(random);
				int n = i - random.nextInt(4) - 1;
				int o = 3 - random.nextInt(3);
				int p = blockPos.getX();
				int q = blockPos.getZ();
				int r = 0;

				for (int s = 0; s < i; s++) {
					int t = blockPos.getY() + s;
					if (s >= n && o > 0) {
						p += direction.getOffsetX();
						q += direction.getOffsetZ();
						o--;
					}

					BlockPos blockPos2 = new BlockPos(p, t, q);
					if (isAirOrLeaves(modifiableTestableWorld, blockPos2)) {
						this.addLog(set, modifiableTestableWorld, blockPos2, mutableIntBoundingBox);
						r = t;
					}
				}

				BlockPos blockPos3 = new BlockPos(p, r, q);

				for (int u = -3; u <= 3; u++) {
					for (int v = -3; v <= 3; v++) {
						if (Math.abs(u) != 3 || Math.abs(v) != 3) {
							this.addLeaves(set, modifiableTestableWorld, blockPos3.add(u, 0, v), mutableIntBoundingBox);
						}
					}
				}

				blockPos3 = blockPos3.up();

				for (int w = -1; w <= 1; w++) {
					for (int x = -1; x <= 1; x++) {
						this.addLeaves(set, modifiableTestableWorld, blockPos3.add(w, 0, x), mutableIntBoundingBox);
					}
				}

				this.addLeaves(set, modifiableTestableWorld, blockPos3.east(2), mutableIntBoundingBox);
				this.addLeaves(set, modifiableTestableWorld, blockPos3.west(2), mutableIntBoundingBox);
				this.addLeaves(set, modifiableTestableWorld, blockPos3.south(2), mutableIntBoundingBox);
				this.addLeaves(set, modifiableTestableWorld, blockPos3.north(2), mutableIntBoundingBox);
				p = blockPos.getX();
				q = blockPos.getZ();
				Direction direction2 = Direction.Type.field_11062.random(random);
				if (direction2 != direction) {
					int y = n - random.nextInt(2) - 1;
					int z = 1 + random.nextInt(3);
					r = 0;

					for (int aa = y; aa < i && z > 0; z--) {
						if (aa >= 1) {
							int ab = blockPos.getY() + aa;
							p += direction2.getOffsetX();
							q += direction2.getOffsetZ();
							BlockPos blockPos4 = new BlockPos(p, ab, q);
							if (isAirOrLeaves(modifiableTestableWorld, blockPos4)) {
								this.addLog(set, modifiableTestableWorld, blockPos4, mutableIntBoundingBox);
								r = ab;
							}
						}

						aa++;
					}

					if (r > 0) {
						BlockPos blockPos5 = new BlockPos(p, r, q);

						for (int ac = -2; ac <= 2; ac++) {
							for (int ad = -2; ad <= 2; ad++) {
								if (Math.abs(ac) != 2 || Math.abs(ad) != 2) {
									this.addLeaves(set, modifiableTestableWorld, blockPos5.add(ac, 0, ad), mutableIntBoundingBox);
								}
							}
						}

						blockPos5 = blockPos5.up();

						for (int ae = -1; ae <= 1; ae++) {
							for (int af = -1; af <= 1; af++) {
								this.addLeaves(set, modifiableTestableWorld, blockPos5.add(ae, 0, af), mutableIntBoundingBox);
							}
						}
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

	private void addLog(Set<BlockPos> set, ModifiableWorld modifiableWorld, BlockPos blockPos, MutableIntBoundingBox mutableIntBoundingBox) {
		this.setBlockState(set, modifiableWorld, blockPos, LOG, mutableIntBoundingBox);
	}

	private void addLeaves(Set<BlockPos> set, ModifiableTestableWorld modifiableTestableWorld, BlockPos blockPos, MutableIntBoundingBox mutableIntBoundingBox) {
		if (isAirOrLeaves(modifiableTestableWorld, blockPos)) {
			this.setBlockState(set, modifiableTestableWorld, blockPos, LEAVES, mutableIntBoundingBox);
		}
	}
}
