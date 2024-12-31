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
import net.minecraft.world.World;

public class NetherWartBlock extends PlantBlock {
	public static final IntProperty AGE = IntProperty.of("age", 0, 3);

	protected NetherWartBlock() {
		super(Material.PLANT, MaterialColor.RED);
		this.setDefaultState(this.stateManager.getDefaultState().with(AGE, 0));
		this.setTickRandomly(true);
		float f = 0.5F;
		this.setBoundingBox(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
		this.setItemGroup(null);
	}

	@Override
	protected boolean canPlantOnTop(Block block) {
		return block == Blocks.SOULSAND;
	}

	@Override
	public boolean canPlantAt(World world, BlockPos pos, BlockState state) {
		return this.canPlantOnTop(world.getBlockState(pos.down()).getBlock());
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
		return null;
	}

	@Override
	public int getDropCount(Random rand) {
		return 0;
	}

	@Override
	public Item getPickItem(World world, BlockPos pos) {
		return Items.NETHER_WART;
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
