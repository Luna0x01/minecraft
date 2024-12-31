package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class NetherWartBlock extends PlantBlock {
	public static final IntProperty AGE = IntProperty.of("age", 0, 3);
	private static final Box[] field_12714 = new Box[]{
		new Box(0.0, 0.0, 0.0, 1.0, 0.3125, 1.0),
		new Box(0.0, 0.0, 0.0, 1.0, 0.5, 1.0),
		new Box(0.0, 0.0, 0.0, 1.0, 0.6875, 1.0),
		new Box(0.0, 0.0, 0.0, 1.0, 0.875, 1.0)
	};

	protected NetherWartBlock() {
		super(Material.PLANT, MaterialColor.RED);
		this.setDefaultState(this.stateManager.getDefaultState().with(AGE, 0));
		this.setTickRandomly(true);
		this.setItemGroup(null);
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		return field_12714[state.get(AGE)];
	}

	@Override
	protected boolean method_11579(BlockState blockState) {
		return blockState.getBlock() == Blocks.SOULSAND;
	}

	@Override
	public boolean canPlantAt(World world, BlockPos pos, BlockState state) {
		return this.method_11579(world.getBlockState(pos.down()));
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		int i = (Integer)state.get(AGE);
		if (i < 3 && rand.nextInt(10) == 0) {
			state = state.with(AGE, i + 1);
			world.setBlockState(pos, state, 2);
		}

		super.onScheduledTick(world, pos, state, rand);
	}

	@Override
	public void randomDropAsItem(World world, BlockPos pos, BlockState state, float chance, int id) {
		if (!world.isClient) {
			int i = 1;
			if ((Integer)state.get(AGE) >= 3) {
				i = 2 + world.random.nextInt(3);
				if (id > 0) {
					i += world.random.nextInt(id + 1);
				}
			}

			for (int j = 0; j < i; j++) {
				onBlockBreak(world, pos, new ItemStack(Items.NETHER_WART));
			}
		}
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Items.AIR;
	}

	@Override
	public int getDropCount(Random rand) {
		return 0;
	}

	@Override
	public ItemStack getItemStack(World world, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(Items.NETHER_WART);
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
