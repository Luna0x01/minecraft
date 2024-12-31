package net.minecraft.block;

import java.util.Random;
import net.minecraft.class_4342;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.util.shapes.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class EndPortalBlock extends BlockWithEntity {
	protected static final VoxelShape field_18305 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);

	protected EndPortalBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new EndPortalBlockEntity();
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return field_18305;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public int getDropCount(BlockState state, Random random) {
		return 0;
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (!world.isClient
			&& !entity.hasMount()
			&& !entity.hasPassengers()
			&& entity.canUsePortals()
			&& VoxelShapes.matchesAnywhere(
				VoxelShapes.method_18049(entity.getBoundingBox().offset((double)(-pos.getX()), (double)(-pos.getY()), (double)(-pos.getZ()))),
				state.getOutlineShape(world, pos),
				BooleanBiFunction.AND
			)) {
			entity.method_15562(DimensionType.THE_END);
		}
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		double d = (double)((float)pos.getX() + random.nextFloat());
		double e = (double)((float)pos.getY() + 0.8F);
		double f = (double)((float)pos.getZ() + random.nextFloat());
		double g = 0.0;
		double h = 0.0;
		double i = 0.0;
		world.method_16343(class_4342.field_21363, d, e, f, 0.0, 0.0, 0.0);
	}

	@Override
	public ItemStack getPickBlock(BlockView world, BlockPos pos, BlockState state) {
		return ItemStack.EMPTY;
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return BlockRenderLayer.UNDEFINED;
	}
}
