package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class LilyPadBlock extends PlantBlock {
	protected static final VoxelShape field_18584 = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 1.5, 15.0);

	protected LilyPadBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		super.onEntityCollision(state, world, pos, entity);
		if (entity instanceof BoatEntity) {
			world.method_8535(new BlockPos(pos), true);
		}
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return field_18584;
	}

	@Override
	protected boolean canPlantOnTop(BlockState state, BlockView world, BlockPos pos) {
		FluidState fluidState = world.getFluidState(pos);
		return fluidState.getFluid() == Fluids.WATER || state.getMaterial() == Material.ICE;
	}
}
