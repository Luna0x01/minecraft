package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ModifiableTestableWorld;

public class SwampTreeFeature extends AbstractTreeFeature<DefaultFeatureConfig> {
	private static final BlockState LOG = Blocks.field_10431.getDefaultState();
	private static final BlockState LEAVES = Blocks.field_10503.getDefaultState();

	public SwampTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function, false);
	}

	@Override
	public boolean generate(
		Set<BlockPos> set, ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, MutableIntBoundingBox mutableIntBoundingBox
	) {
		int i = random.nextInt(4) + 5;
		blockPos = modifiableTestableWorld.getTopPosition(Heightmap.Type.field_13200, blockPos);
		boolean bl = true;
		if (blockPos.getY() >= 1 && blockPos.getY() + i + 1 <= 256) {
			for (int j = blockPos.getY(); j <= blockPos.getY() + 1 + i; j++) {
				int k = 1;
				if (j == blockPos.getY()) {
					k = 0;
				}

				if (j >= blockPos.getY() + 1 + i - 2) {
					k = 3;
				}

				BlockPos.Mutable mutable = new BlockPos.Mutable();

				for (int l = blockPos.getX() - k; l <= blockPos.getX() + k && bl; l++) {
					for (int m = blockPos.getZ() - k; m <= blockPos.getZ() + k && bl; m++) {
						if (j >= 0 && j < 256) {
							mutable.set(l, j, m);
							if (!isAirOrLeaves(modifiableTestableWorld, mutable)) {
								if (isWater(modifiableTestableWorld, mutable)) {
									if (j > blockPos.getY()) {
										bl = false;
									}
								} else {
									bl = false;
								}
							}
						} else {
							bl = false;
						}
					}
				}
			}

			if (!bl) {
				return false;
			} else if (isNaturalDirtOrGrass(modifiableTestableWorld, blockPos.down()) && blockPos.getY() < 256 - i - 1) {
				this.setToDirt(modifiableTestableWorld, blockPos.down());

				for (int n = blockPos.getY() - 3 + i; n <= blockPos.getY() + i; n++) {
					int o = n - (blockPos.getY() + i);
					int p = 2 - o / 2;

					for (int q = blockPos.getX() - p; q <= blockPos.getX() + p; q++) {
						int r = q - blockPos.getX();

						for (int s = blockPos.getZ() - p; s <= blockPos.getZ() + p; s++) {
							int t = s - blockPos.getZ();
							if (Math.abs(r) != p || Math.abs(t) != p || random.nextInt(2) != 0 && o != 0) {
								BlockPos blockPos2 = new BlockPos(q, n, s);
								if (isAirOrLeaves(modifiableTestableWorld, blockPos2) || isReplaceablePlant(modifiableTestableWorld, blockPos2)) {
									this.setBlockState(set, modifiableTestableWorld, blockPos2, LEAVES, mutableIntBoundingBox);
								}
							}
						}
					}
				}

				for (int u = 0; u < i; u++) {
					BlockPos blockPos3 = blockPos.up(u);
					if (isAirOrLeaves(modifiableTestableWorld, blockPos3) || isWater(modifiableTestableWorld, blockPos3)) {
						this.setBlockState(set, modifiableTestableWorld, blockPos3, LOG, mutableIntBoundingBox);
					}
				}

				for (int v = blockPos.getY() - 3 + i; v <= blockPos.getY() + i; v++) {
					int w = v - (blockPos.getY() + i);
					int x = 2 - w / 2;
					BlockPos.Mutable mutable2 = new BlockPos.Mutable();

					for (int y = blockPos.getX() - x; y <= blockPos.getX() + x; y++) {
						for (int z = blockPos.getZ() - x; z <= blockPos.getZ() + x; z++) {
							mutable2.set(y, v, z);
							if (isLeaves(modifiableTestableWorld, mutable2)) {
								BlockPos blockPos4 = mutable2.west();
								BlockPos blockPos5 = mutable2.east();
								BlockPos blockPos6 = mutable2.north();
								BlockPos blockPos7 = mutable2.south();
								if (random.nextInt(4) == 0 && isAir(modifiableTestableWorld, blockPos4)) {
									this.makeVines(modifiableTestableWorld, blockPos4, VineBlock.EAST);
								}

								if (random.nextInt(4) == 0 && isAir(modifiableTestableWorld, blockPos5)) {
									this.makeVines(modifiableTestableWorld, blockPos5, VineBlock.WEST);
								}

								if (random.nextInt(4) == 0 && isAir(modifiableTestableWorld, blockPos6)) {
									this.makeVines(modifiableTestableWorld, blockPos6, VineBlock.SOUTH);
								}

								if (random.nextInt(4) == 0 && isAir(modifiableTestableWorld, blockPos7)) {
									this.makeVines(modifiableTestableWorld, blockPos7, VineBlock.NORTH);
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

	private void makeVines(ModifiableTestableWorld modifiableTestableWorld, BlockPos blockPos, BooleanProperty booleanProperty) {
		BlockState blockState = Blocks.field_10597.getDefaultState().with(booleanProperty, Boolean.valueOf(true));
		this.setBlockState(modifiableTestableWorld, blockPos, blockState);
		int i = 4;

		for (BlockPos var6 = blockPos.down(); isAir(modifiableTestableWorld, var6) && i > 0; i--) {
			this.setBlockState(modifiableTestableWorld, var6, blockState);
			var6 = var6.down();
		}
	}
}
