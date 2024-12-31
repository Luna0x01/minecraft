package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class SugarCaneBlock extends Block {
	public static final IntProperty AGE = IntProperty.of("age", 0, 15);
	protected static final Box field_12734 = new Box(0.125, 0.0, 0.125, 0.875, 1.0, 0.875);

	protected SugarCaneBlock() {
		super(Material.PLANT);
		this.setDefaultState(this.stateManager.getDefaultState().with(AGE, 0));
		this.setTickRandomly(true);
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		return field_12734;
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		if (world.getBlockState(pos.down()).getBlock() == Blocks.SUGARCANE || this.placeSugarCaneBlock(world, pos, state)) {
			if (world.isAir(pos.up())) {
				int i = 1;

				while (world.getBlockState(pos.down(i)).getBlock() == this) {
					i++;
				}

				if (i < 3) {
					int j = (Integer)state.get(AGE);
					if (j == 15) {
						world.setBlockState(pos.up(), this.getDefaultState());
						world.setBlockState(pos, state.with(AGE, 0), 4);
					} else {
						world.setBlockState(pos, state.with(AGE, j + 1), 4);
					}
				}
			}
		}
	}

	@Override
	public boolean canBePlacedAtPos(World world, BlockPos pos) {
		Block block = world.getBlockState(pos.down()).getBlock();
		if (block == this) {
			return true;
		} else if (block != Blocks.GRASS && block != Blocks.DIRT && block != Blocks.SAND) {
			return false;
		} else {
			BlockPos blockPos = pos.down();

			for (Direction direction : Direction.DirectionType.HORIZONTAL) {
				BlockState blockState = world.getBlockState(blockPos.offset(direction));
				if (blockState.getMaterial() == Material.WATER || blockState.getBlock() == Blocks.FROSTED_ICE) {
					return true;
				}
			}

			return false;
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos) {
		this.placeSugarCaneBlock(world, pos, state);
	}

	protected final boolean placeSugarCaneBlock(World world, BlockPos blockPos, BlockState blockState) {
		if (this.canPlaceAt(world, blockPos)) {
			return true;
		} else {
			this.dropAsItem(world, blockPos, blockState, 0);
			world.setAir(blockPos);
			return false;
		}
	}

	public boolean canPlaceAt(World world, BlockPos blockPos) {
		return this.canBePlacedAtPos(world, blockPos);
	}

	@Nullable
	@Override
	public Box method_8640(BlockState state, BlockView view, BlockPos pos) {
		return EMPTY_BOX;
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Items.SUGARCANE;
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
	public ItemStack getItemStack(World world, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(Items.SUGARCANE);
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(AGE, data);
	}

	@Override
	public int getData(BlockState state) {
		return (Integer)state.get(AGE);
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, AGE);
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return BlockRenderLayer.UNDEFINED;
	}
}
