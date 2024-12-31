package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.Sounds;
import net.minecraft.state.StateManager;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public abstract class class_4032 extends FlowableFluid {
	@Override
	public Fluid method_17768() {
		return Fluids.FLOWING_WATER;
	}

	@Override
	public Fluid getStill() {
		return Fluids.WATER;
	}

	@Override
	public RenderLayer getRenderLayer() {
		return RenderLayer.TRANSLUCENT;
	}

	@Override
	public Item method_17787() {
		return Items.WATER_BUCKET;
	}

	@Override
	public void method_17777(World world, BlockPos blockPos, FluidState fluidState, Random random) {
		if (!fluidState.isStill() && !(Boolean)fluidState.getProperty(FALLING)) {
			if (random.nextInt(64) == 0) {
				world.playSound(
					(double)blockPos.getX() + 0.5,
					(double)blockPos.getY() + 0.5,
					(double)blockPos.getZ() + 0.5,
					Sounds.BLOCK_WATER_AMBIENT,
					SoundCategory.BLOCKS,
					random.nextFloat() * 0.25F + 0.75F,
					random.nextFloat() + 0.5F,
					false
				);
			}
		} else if (random.nextInt(10) == 0) {
			world.method_16343(
				class_4342.field_21367,
				(double)((float)blockPos.getX() + random.nextFloat()),
				(double)((float)blockPos.getY() + random.nextFloat()),
				(double)((float)blockPos.getZ() + random.nextFloat()),
				0.0,
				0.0,
				0.0
			);
		}
	}

	@Nullable
	@Override
	public ParticleEffect getParticle() {
		return class_4342.field_21386;
	}

	@Override
	protected boolean method_17771() {
		return true;
	}

	@Override
	protected void method_17751(IWorld iWorld, BlockPos blockPos, BlockState blockState) {
		blockState.method_16867(iWorld.method_16348(), blockPos, 0);
	}

	@Override
	public int method_17764(RenderBlockView renderBlockView) {
		return 4;
	}

	@Override
	public BlockState method_17789(FluidState fluidState) {
		return Blocks.WATER.getDefaultState().withProperty(class_3710.field_18402, Integer.valueOf(method_17769(fluidState)));
	}

	@Override
	public boolean method_17781(Fluid fluid) {
		return fluid == Fluids.WATER || fluid == Fluids.FLOWING_WATER;
	}

	@Override
	public int method_17767(RenderBlockView renderBlockView) {
		return 1;
	}

	@Override
	public int method_17778(RenderBlockView renderBlockView) {
		return 5;
	}

	@Override
	public boolean method_17783(FluidState fluidState, Fluid fluid, Direction direction) {
		return direction == Direction.DOWN && !fluid.method_17786(FluidTags.WATER);
	}

	@Override
	protected float getBlastResistance() {
		return 100.0F;
	}

	public static class class_4033 extends class_4032 {
		@Override
		protected void method_17780(StateManager.Builder<Fluid, FluidState> builder) {
			super.method_17780(builder);
			builder.method_16928(LEVEL);
		}

		@Override
		public int method_17793(FluidState fluidState) {
			return (Integer)fluidState.getProperty(LEVEL);
		}

		@Override
		public boolean isStill(FluidState fluidState) {
			return false;
		}
	}

	public static class class_4034 extends class_4032 {
		@Override
		public int method_17793(FluidState fluidState) {
			return 8;
		}

		@Override
		public boolean isStill(FluidState fluidState) {
			return true;
		}
	}
}
