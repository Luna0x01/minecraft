package net.minecraft.fluid;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.state.PropertyContainer;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public interface FluidState extends PropertyContainer<FluidState> {
	Fluid getFluid();

	default boolean isStill() {
		return this.getFluid().isStill(this);
	}

	default boolean isEmpty() {
		return this.getFluid().isEmpty();
	}

	default float method_17810() {
		return this.getFluid().method_17782(this);
	}

	default int method_17811() {
		return this.getFluid().method_17793(this);
	}

	default boolean method_17800(BlockView blockView, BlockPos blockPos) {
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				BlockPos blockPos2 = blockPos.add(i, 0, j);
				FluidState fluidState = blockView.getFluidState(blockPos2);
				if (!fluidState.getFluid().method_17781(this.getFluid()) && !blockView.getBlockState(blockPos2).isFullOpaque(blockView, blockPos2)) {
					return true;
				}
			}
		}

		return false;
	}

	default void method_17801(World world, BlockPos blockPos) {
		this.getFluid().method_17776(world, blockPos, this);
	}

	default void method_17802(World world, BlockPos blockPos, Random random) {
		this.getFluid().method_17777(world, blockPos, this, random);
	}

	default boolean method_17812() {
		return this.getFluid().method_17798();
	}

	default void method_17806(World world, BlockPos blockPos, Random random) {
		this.getFluid().method_17788(world, blockPos, this, random);
	}

	default Vec3d method_17803(RenderBlockView renderBlockView, BlockPos blockPos) {
		return this.getFluid().method_17779(renderBlockView, blockPos, this);
	}

	default BlockState method_17813() {
		return this.getFluid().method_17789(this);
	}

	@Nullable
	default ParticleEffect getParticle() {
		return this.getFluid().getParticle();
	}

	default RenderLayer getRenderLayer() {
		return this.getFluid().getRenderLayer();
	}

	default boolean matches(Tag<Fluid> tag) {
		return this.getFluid().method_17786(tag);
	}

	default float getBlastResistance() {
		return this.getFluid().getBlastResistance();
	}

	default boolean method_17804(Fluid fluid, Direction direction) {
		return this.getFluid().method_17783(this, fluid, direction);
	}
}
