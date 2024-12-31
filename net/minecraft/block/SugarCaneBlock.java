package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Item;
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

	protected SugarCaneBlock() {
		super(Material.PLANT);
		this.setDefaultState(this.stateManager.getDefaultState().with(AGE, 0));
		float f = 0.375F;
		this.setBoundingBox(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 1.0F, 0.5F + f);
		this.setTickRandomly(true);
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
			for (Direction direction : Direction.DirectionType.HORIZONTAL) {
				if (world.getBlockState(pos.offset(direction).down()).getBlock().getMaterial() == Material.WATER) {
					return true;
				}
			}

			return false;
		}
	}

	@Override
	public void neighborUpdate(World world, BlockPos pos, BlockState state, Block block) {
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

	@Override
	public Box getCollisionBox(World world, BlockPos pos, BlockState state) {
		return null;
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Items.SUGARCANE;
	}

	@Override
	public boolean hasTransparency() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public Item getPickItem(World world, BlockPos pos) {
		return Items.SUGARCANE;
	}

	@Override
	public int getBlockColor(BlockView view, BlockPos pos, int id) {
		return view.getBiome(pos).getGrassColor(pos);
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
}
