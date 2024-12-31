package net.minecraft;

import com.google.common.collect.ImmutableSet;
import java.util.BitSet;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;

public abstract class class_3969<C extends class_3845> implements class_3997<C> {
	protected static final BlockState field_19339 = Blocks.AIR.getDefaultState();
	protected static final BlockState field_19340 = Blocks.CAVE_AIR.getDefaultState();
	protected static final FluidState field_19341 = Fluids.WATER.getDefaultState();
	protected static final FluidState field_19342 = Fluids.LAVA.getDefaultState();
	protected Set<Block> field_19343 = ImmutableSet.of(
		Blocks.STONE,
		Blocks.GRANITE,
		Blocks.DIORITE,
		Blocks.ANDESITE,
		Blocks.DIRT,
		Blocks.COARSE_DIRT,
		new Block[]{
			Blocks.PODZOL,
			Blocks.GRASS_BLOCK,
			Blocks.TERRACOTTA,
			Blocks.WHITE_TERRACOTTA,
			Blocks.ORANGE_TERRACOTTA,
			Blocks.MAGENTA_TERRACOTTA,
			Blocks.LIGHT_BLUE_TERRACOTTA,
			Blocks.YELLOW_TERRACOTTA,
			Blocks.LIME_TERRACOTTA,
			Blocks.PINK_TERRACOTTA,
			Blocks.GRAY_TERRACOTTA,
			Blocks.LIGHT_GRAY_TERRACOTTA,
			Blocks.CYAN_TERRACOTTA,
			Blocks.PURPLE_TERRACOTTA,
			Blocks.BLUE_TERRACOTTA,
			Blocks.BROWN_TERRACOTTA,
			Blocks.GREEN_TERRACOTTA,
			Blocks.RED_TERRACOTTA,
			Blocks.BLACK_TERRACOTTA,
			Blocks.SANDSTONE,
			Blocks.RED_SANDSTONE,
			Blocks.MYCELIUM,
			Blocks.SNOW,
			Blocks.PACKED_ICE
		}
	);
	protected Set<Fluid> field_19344 = ImmutableSet.of(Fluids.WATER);

	public int method_17583() {
		return 4;
	}

	protected abstract boolean method_17586(IWorld iWorld, long l, int i, int j, double d, double e, double f, double g, double h, BitSet bitSet);

	protected boolean method_17588(BlockState blockState) {
		return this.field_19343.contains(blockState.getBlock());
	}

	protected boolean method_17589(BlockState blockState, BlockState blockState2) {
		Block block = blockState.getBlock();
		return this.method_17588(blockState) || (block == Blocks.SAND || block == Blocks.GRAVEL) && !blockState2.getFluidState().matches(FluidTags.WATER);
	}

	protected boolean method_17587(RenderBlockView renderBlockView, int i, int j, int k, int l, int m, int n, int o, int p) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int q = k; q < l; q++) {
			for (int r = o; r < p; r++) {
				for (int s = m - 1; s <= n + 1; s++) {
					if (this.field_19344.contains(renderBlockView.getFluidState(mutable.setPosition(q + i * 16, s, r + j * 16)).getFluid())) {
						return true;
					}

					if (s != n + 1 && !this.method_17585(k, l, o, p, q, r)) {
						s = n;
					}
				}
			}
		}

		return false;
	}

	private boolean method_17585(int i, int j, int k, int l, int m, int n) {
		return m == i || m == j - 1 || n == k || n == l - 1;
	}

	protected boolean method_17584(int i, int j, double d, double e, int k, int l, float f) {
		double g = (double)(i * 16 + 8);
		double h = (double)(j * 16 + 8);
		double m = d - g;
		double n = e - h;
		double o = (double)(l - k);
		double p = (double)(f + 2.0F + 16.0F);
		return m * m + n * n - o * o <= p * p;
	}
}
