package net.minecraft;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RenderBlockView;

public class class_4021 extends Fluid {
	@Override
	public RenderLayer getRenderLayer() {
		return RenderLayer.SOLID;
	}

	@Override
	public Item method_17787() {
		return Items.AIR;
	}

	@Override
	public boolean method_17783(FluidState fluidState, Fluid fluid, Direction direction) {
		return true;
	}

	@Override
	public Vec3d method_17779(RenderBlockView renderBlockView, BlockPos blockPos, FluidState fluidState) {
		return Vec3d.ZERO;
	}

	@Override
	public int method_17778(RenderBlockView renderBlockView) {
		return 0;
	}

	@Override
	protected boolean isEmpty() {
		return true;
	}

	@Override
	protected float getBlastResistance() {
		return 0.0F;
	}

	@Override
	public float method_17782(FluidState fluidState) {
		return 0.0F;
	}

	@Override
	protected BlockState method_17789(FluidState fluidState) {
		return Blocks.AIR.getDefaultState();
	}

	@Override
	public boolean isStill(FluidState fluidState) {
		return false;
	}

	@Override
	public int method_17793(FluidState fluidState) {
		return 0;
	}
}
