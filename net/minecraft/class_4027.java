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

public abstract class class_4027 extends FlowableFluid {
	@Override
	public Fluid method_17768() {
		return Fluids.FLOWING_LAVA;
	}

	@Override
	public Fluid getStill() {
		return Fluids.LAVA;
	}

	@Override
	public RenderLayer getRenderLayer() {
		return RenderLayer.SOLID;
	}

	@Override
	public Item method_17787() {
		return Items.LAVA_BUCKET;
	}

	@Override
	public void method_17777(World world, BlockPos blockPos, FluidState fluidState, Random random) {
		BlockPos blockPos2 = blockPos.up();
		if (world.getBlockState(blockPos2).isAir() && !world.getBlockState(blockPos2).isFullOpaque(world, blockPos2)) {
			if (random.nextInt(100) == 0) {
				double d = (double)((float)blockPos.getX() + random.nextFloat());
				double e = (double)(blockPos.getY() + 1);
				double f = (double)((float)blockPos.getZ() + random.nextFloat());
				world.method_16343(class_4342.field_21357, d, e, f, 0.0, 0.0, 0.0);
				world.playSound(d, e, f, Sounds.BLOCK_LAVA_POP, SoundCategory.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
			}

			if (random.nextInt(200) == 0) {
				world.playSound(
					(double)blockPos.getX(),
					(double)blockPos.getY(),
					(double)blockPos.getZ(),
					Sounds.BLOCK_LAVA_AMBIENT,
					SoundCategory.BLOCKS,
					0.2F + random.nextFloat() * 0.2F,
					0.9F + random.nextFloat() * 0.15F,
					false
				);
			}
		}
	}

	@Override
	public void method_17788(World world, BlockPos blockPos, FluidState fluidState, Random random) {
		if (world.getGameRules().getBoolean("doFireTick")) {
			int i = random.nextInt(3);
			if (i > 0) {
				BlockPos blockPos2 = blockPos;

				for (int j = 0; j < i; j++) {
					blockPos2 = blockPos2.add(random.nextInt(3) - 1, 1, random.nextInt(3) - 1);
					if (!world.method_16338(blockPos2)) {
						return;
					}

					BlockState blockState = world.getBlockState(blockPos2);
					if (blockState.isAir()) {
						if (this.method_17819(world, blockPos2)) {
							world.setBlockState(blockPos2, Blocks.FIRE.getDefaultState());
							return;
						}
					} else if (blockState.getMaterial().blocksMovement()) {
						return;
					}
				}
			} else {
				for (int k = 0; k < 3; k++) {
					BlockPos blockPos3 = blockPos.add(random.nextInt(3) - 1, 0, random.nextInt(3) - 1);
					if (!world.method_16338(blockPos3)) {
						return;
					}

					if (world.method_8579(blockPos3.up()) && this.method_17820(world, blockPos3)) {
						world.setBlockState(blockPos3.up(), Blocks.FIRE.getDefaultState());
					}
				}
			}
		}
	}

	private boolean method_17819(RenderBlockView renderBlockView, BlockPos blockPos) {
		for (Direction direction : Direction.values()) {
			if (this.method_17820(renderBlockView, blockPos.offset(direction))) {
				return true;
			}
		}

		return false;
	}

	private boolean method_17820(RenderBlockView renderBlockView, BlockPos blockPos) {
		return blockPos.getY() >= 0 && blockPos.getY() < 256 && !renderBlockView.method_16359(blockPos)
			? false
			: renderBlockView.getBlockState(blockPos).getMaterial().isBurnable();
	}

	@Nullable
	@Override
	public ParticleEffect getParticle() {
		return class_4342.field_21385;
	}

	@Override
	protected void method_17751(IWorld iWorld, BlockPos blockPos, BlockState blockState) {
		this.method_17818(iWorld, blockPos);
	}

	@Override
	public int method_17764(RenderBlockView renderBlockView) {
		return renderBlockView.method_16393().doesWaterVaporize() ? 4 : 2;
	}

	@Override
	public BlockState method_17789(FluidState fluidState) {
		return Blocks.LAVA.getDefaultState().withProperty(class_3710.field_18402, Integer.valueOf(method_17769(fluidState)));
	}

	@Override
	public boolean method_17781(Fluid fluid) {
		return fluid == Fluids.LAVA || fluid == Fluids.FLOWING_LAVA;
	}

	@Override
	public int method_17767(RenderBlockView renderBlockView) {
		return renderBlockView.method_16393().doesWaterVaporize() ? 1 : 2;
	}

	@Override
	public boolean method_17783(FluidState fluidState, Fluid fluid, Direction direction) {
		return fluidState.method_17810() >= 0.44444445F && fluid.method_17786(FluidTags.WATER);
	}

	@Override
	public int method_17778(RenderBlockView renderBlockView) {
		return renderBlockView.method_16393().hasNoSkylight() ? 10 : 30;
	}

	@Override
	public int method_17750(World world, FluidState fluidState, FluidState fluidState2) {
		int i = this.method_17778(world);
		if (!fluidState.isEmpty()
			&& !fluidState2.isEmpty()
			&& !(Boolean)fluidState.getProperty(FALLING)
			&& !(Boolean)fluidState2.getProperty(FALLING)
			&& fluidState2.method_17810() > fluidState.method_17810()
			&& world.getRandom().nextInt(4) != 0) {
			i *= 4;
		}

		return i;
	}

	protected void method_17818(IWorld iWorld, BlockPos blockPos) {
		double d = (double)blockPos.getX();
		double e = (double)blockPos.getY();
		double f = (double)blockPos.getZ();
		iWorld.playSound(
			null, blockPos, Sounds.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (iWorld.getRandom().nextFloat() - iWorld.getRandom().nextFloat()) * 0.8F
		);

		for (int i = 0; i < 8; i++) {
			iWorld.method_16343(class_4342.field_21356, d + Math.random(), e + 1.2, f + Math.random(), 0.0, 0.0, 0.0);
		}
	}

	@Override
	protected boolean method_17771() {
		return false;
	}

	@Override
	protected void method_17752(IWorld iWorld, BlockPos blockPos, BlockState blockState, Direction direction, FluidState fluidState) {
		if (direction == Direction.DOWN) {
			FluidState fluidState2 = iWorld.getFluidState(blockPos);
			if (this.method_17786(FluidTags.LAVA) && fluidState2.matches(FluidTags.WATER)) {
				if (blockState.getBlock() instanceof class_3710) {
					iWorld.setBlockState(blockPos, Blocks.STONE.getDefaultState(), 3);
				}

				this.method_17818(iWorld, blockPos);
				return;
			}
		}

		super.method_17752(iWorld, blockPos, blockState, direction, fluidState);
	}

	@Override
	protected boolean method_17798() {
		return true;
	}

	@Override
	protected float getBlastResistance() {
		return 100.0F;
	}

	public static class class_4028 extends class_4027 {
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

	public static class class_4029 extends class_4027 {
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
