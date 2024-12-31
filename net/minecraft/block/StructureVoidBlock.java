package net.minecraft.block;

import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class StructureVoidBlock extends Block {
	private static final VoxelShape field_18523 = Block.createCuboidShape(5.0, 5.0, 5.0, 11.0, 11.0, 11.0);

	protected StructureVoidBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.INVISIBLE;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return field_18523;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public float getAmbientOcclusionLightLevel(BlockState state) {
		return 1.0F;
	}

	@Override
	public void method_410(BlockState blockState, World world, BlockPos blockPos, float f, int i) {
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.DESTROY;
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return BlockRenderLayer.UNDEFINED;
	}
}
