package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CocoaBlock;
import net.minecraft.block.VineBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.ModifiableWorld;

public class OakTreeFeature extends AbstractTreeFeature<DefaultFeatureConfig> {
	private static final BlockState LOG = Blocks.field_10431.getDefaultState();
	private static final BlockState LEAVES = Blocks.field_10503.getDefaultState();
	protected final int height;
	private final boolean hasVinesAndCocoa;
	private final BlockState log;
	private final BlockState leaves;

	public OakTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function, boolean bl) {
		this(function, bl, 4, LOG, LEAVES, false);
	}

	public OakTreeFeature(
		Function<Dynamic<?>, ? extends DefaultFeatureConfig> function, boolean bl, int i, BlockState blockState, BlockState blockState2, boolean bl2
	) {
		super(function, bl);
		this.height = i;
		this.log = blockState;
		this.leaves = blockState2;
		this.hasVinesAndCocoa = bl2;
	}

	@Override
	public boolean generate(
		Set<BlockPos> set, ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, MutableIntBoundingBox mutableIntBoundingBox
	) {
		int i = this.getTreeHeight(random);
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
				int n = 3;
				int o = 0;

				for (int p = blockPos.getY() - 3 + i; p <= blockPos.getY() + i; p++) {
					int q = p - (blockPos.getY() + i);
					int r = 1 - q / 2;

					for (int s = blockPos.getX() - r; s <= blockPos.getX() + r; s++) {
						int t = s - blockPos.getX();

						for (int u = blockPos.getZ() - r; u <= blockPos.getZ() + r; u++) {
							int v = u - blockPos.getZ();
							if (Math.abs(t) != r || Math.abs(v) != r || random.nextInt(2) != 0 && q != 0) {
								BlockPos blockPos2 = new BlockPos(s, p, u);
								if (isAirOrLeaves(modifiableTestableWorld, blockPos2) || isReplaceablePlant(modifiableTestableWorld, blockPos2)) {
									this.setBlockState(set, modifiableTestableWorld, blockPos2, this.leaves, mutableIntBoundingBox);
								}
							}
						}
					}
				}

				for (int w = 0; w < i; w++) {
					if (isAirOrLeaves(modifiableTestableWorld, blockPos.up(w)) || isReplaceablePlant(modifiableTestableWorld, blockPos.up(w))) {
						this.setBlockState(set, modifiableTestableWorld, blockPos.up(w), this.log, mutableIntBoundingBox);
						if (this.hasVinesAndCocoa && w > 0) {
							if (random.nextInt(3) > 0 && isAir(modifiableTestableWorld, blockPos.add(-1, w, 0))) {
								this.makeVine(modifiableTestableWorld, blockPos.add(-1, w, 0), VineBlock.EAST);
							}

							if (random.nextInt(3) > 0 && isAir(modifiableTestableWorld, blockPos.add(1, w, 0))) {
								this.makeVine(modifiableTestableWorld, blockPos.add(1, w, 0), VineBlock.WEST);
							}

							if (random.nextInt(3) > 0 && isAir(modifiableTestableWorld, blockPos.add(0, w, -1))) {
								this.makeVine(modifiableTestableWorld, blockPos.add(0, w, -1), VineBlock.SOUTH);
							}

							if (random.nextInt(3) > 0 && isAir(modifiableTestableWorld, blockPos.add(0, w, 1))) {
								this.makeVine(modifiableTestableWorld, blockPos.add(0, w, 1), VineBlock.NORTH);
							}
						}
					}
				}

				if (this.hasVinesAndCocoa) {
					for (int x = blockPos.getY() - 3 + i; x <= blockPos.getY() + i; x++) {
						int y = x - (blockPos.getY() + i);
						int z = 2 - y / 2;
						BlockPos.Mutable mutable2 = new BlockPos.Mutable();

						for (int aa = blockPos.getX() - z; aa <= blockPos.getX() + z; aa++) {
							for (int ab = blockPos.getZ() - z; ab <= blockPos.getZ() + z; ab++) {
								mutable2.set(aa, x, ab);
								if (isLeaves(modifiableTestableWorld, mutable2)) {
									BlockPos blockPos3 = mutable2.west();
									BlockPos blockPos4 = mutable2.east();
									BlockPos blockPos5 = mutable2.north();
									BlockPos blockPos6 = mutable2.south();
									if (random.nextInt(4) == 0 && isAir(modifiableTestableWorld, blockPos3)) {
										this.makeVineColumn(modifiableTestableWorld, blockPos3, VineBlock.EAST);
									}

									if (random.nextInt(4) == 0 && isAir(modifiableTestableWorld, blockPos4)) {
										this.makeVineColumn(modifiableTestableWorld, blockPos4, VineBlock.WEST);
									}

									if (random.nextInt(4) == 0 && isAir(modifiableTestableWorld, blockPos5)) {
										this.makeVineColumn(modifiableTestableWorld, blockPos5, VineBlock.SOUTH);
									}

									if (random.nextInt(4) == 0 && isAir(modifiableTestableWorld, blockPos6)) {
										this.makeVineColumn(modifiableTestableWorld, blockPos6, VineBlock.NORTH);
									}
								}
							}
						}
					}

					if (random.nextInt(5) == 0 && i > 5) {
						for (int ac = 0; ac < 2; ac++) {
							for (Direction direction : Direction.Type.field_11062) {
								if (random.nextInt(4 - ac) == 0) {
									Direction direction2 = direction.getOpposite();
									this.makeCocoa(modifiableTestableWorld, random.nextInt(3), blockPos.add(direction2.getOffsetX(), i - 5 + ac, direction2.getOffsetZ()), direction);
								}
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

	protected int getTreeHeight(Random random) {
		return this.height + random.nextInt(3);
	}

	private void makeCocoa(ModifiableWorld modifiableWorld, int i, BlockPos blockPos, Direction direction) {
		this.setBlockState(
			modifiableWorld, blockPos, Blocks.field_10302.getDefaultState().with(CocoaBlock.AGE, Integer.valueOf(i)).with(CocoaBlock.FACING, direction)
		);
	}

	private void makeVine(ModifiableWorld modifiableWorld, BlockPos blockPos, BooleanProperty booleanProperty) {
		this.setBlockState(modifiableWorld, blockPos, Blocks.field_10597.getDefaultState().with(booleanProperty, Boolean.valueOf(true)));
	}

	private void makeVineColumn(ModifiableTestableWorld modifiableTestableWorld, BlockPos blockPos, BooleanProperty booleanProperty) {
		this.makeVine(modifiableTestableWorld, blockPos, booleanProperty);
		int i = 4;

		for (BlockPos var5 = blockPos.down(); isAir(modifiableTestableWorld, var5) && i > 0; i--) {
			this.makeVine(modifiableTestableWorld, var5, booleanProperty);
			var5 = var5.down();
		}
	}
}
