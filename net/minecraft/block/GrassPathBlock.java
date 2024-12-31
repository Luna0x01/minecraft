package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class GrassPathBlock extends Block {
	protected static final Box field_12681 = new Box(0.0, 0.0, 0.0, 1.0, 0.9375, 1.0);

	protected GrassPathBlock() {
		super(Material.DIRT);
		this.setOpacity(255);
	}

	@Override
	public boolean method_8654(BlockState state, BlockView view, BlockPos pos, Direction direction) {
		switch (direction) {
			case UP:
				return true;
			case NORTH:
			case SOUTH:
			case WEST:
			case EAST:
				BlockState blockState = view.getBlockState(pos.offset(direction));
				Block block = blockState.getBlock();
				return !blockState.isFullBoundsCubeForCulling() && block != Blocks.FARMLAND && block != Blocks.GRASS_PATH;
			default:
				return super.method_8654(state, view, pos, direction);
		}
	}

	@Override
	public void onCreation(World world, BlockPos pos, BlockState state) {
		super.onCreation(world, pos, state);
		this.method_13708(world, pos);
	}

	private void method_13708(World world, BlockPos blockPos) {
		if (world.getBlockState(blockPos.up()).getMaterial().isSolid()) {
			FarmlandBlock.method_13706(world, blockPos);
		}
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		return field_12681;
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return false;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Blocks.DIRT.getDropItem(Blocks.DIRT.getDefaultState().with(DirtBlock.VARIANT, DirtBlock.DirtType.DIRT), random, id);
	}

	@Override
	public ItemStack getItemStack(World world, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(this);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos) {
		super.neighborUpdate(state, world, pos, block, neighborPos);
		this.method_13708(world, pos);
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return direction == Direction.DOWN ? BlockRenderLayer.SOLID : BlockRenderLayer.UNDEFINED;
	}
}
