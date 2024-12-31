package net.minecraft.block;

import java.util.Random;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class CropBlock extends PlantBlock implements Growable {
	public static final IntProperty AGE = IntProperty.of("age", 0, 7);

	protected CropBlock() {
		this.setDefaultState(this.stateManager.getDefaultState().with(AGE, 0));
		this.setTickRandomly(true);
		float f = 0.5F;
		this.setBoundingBox(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
		this.setItemGroup(null);
		this.setStrength(0.0F);
		this.setSound(GRASS);
		this.disableStats();
	}

	@Override
	protected boolean canPlantOnTop(Block block) {
		return block == Blocks.FARMLAND;
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		super.onScheduledTick(world, pos, state, rand);
		if (world.getLightLevelWithNeighbours(pos.up()) >= 9) {
			int i = (Integer)state.get(AGE);
			if (i < 7) {
				float f = getAvailableMoisture(this, world, pos);
				if (rand.nextInt((int)(25.0F / f) + 1) == 0) {
					world.setBlockState(pos, state.with(AGE, i + 1), 2);
				}
			}
		}
	}

	public void applyGrowth(World world, BlockPos pos, BlockState state) {
		int i = (Integer)state.get(AGE) + MathHelper.nextInt(world.random, 2, 5);
		if (i > 7) {
			i = 7;
		}

		world.setBlockState(pos, state.with(AGE, i), 2);
	}

	protected static float getAvailableMoisture(Block block, World world, BlockPos pos) {
		float f = 1.0F;
		BlockPos blockPos = pos.down();

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				float g = 0.0F;
				BlockState blockState = world.getBlockState(blockPos.add(i, 0, j));
				if (blockState.getBlock() == Blocks.FARMLAND) {
					g = 1.0F;
					if ((Integer)blockState.get(FarmlandBlock.MOISTURE) > 0) {
						g = 3.0F;
					}
				}

				if (i != 0 || j != 0) {
					g /= 4.0F;
				}

				f += g;
			}
		}

		BlockPos blockPos2 = pos.north();
		BlockPos blockPos3 = pos.south();
		BlockPos blockPos4 = pos.west();
		BlockPos blockPos5 = pos.east();
		boolean bl = block == world.getBlockState(blockPos4).getBlock() || block == world.getBlockState(blockPos5).getBlock();
		boolean bl2 = block == world.getBlockState(blockPos2).getBlock() || block == world.getBlockState(blockPos3).getBlock();
		if (bl && bl2) {
			f /= 2.0F;
		} else {
			boolean bl3 = block == world.getBlockState(blockPos4.north()).getBlock()
				|| block == world.getBlockState(blockPos5.north()).getBlock()
				|| block == world.getBlockState(blockPos5.south()).getBlock()
				|| block == world.getBlockState(blockPos4.south()).getBlock();
			if (bl3) {
				f /= 2.0F;
			}
		}

		return f;
	}

	@Override
	public boolean canPlantAt(World world, BlockPos pos, BlockState state) {
		return (world.getLightLevel(pos) >= 8 || world.hasDirectSunlight(pos)) && this.canPlantOnTop(world.getBlockState(pos.down()).getBlock());
	}

	protected Item getSeedItem() {
		return Items.WHEAT_SEEDS;
	}

	protected Item getHarvestItem() {
		return Items.WHEAT;
	}

	@Override
	public void randomDropAsItem(World world, BlockPos pos, BlockState state, float chance, int id) {
		super.randomDropAsItem(world, pos, state, chance, 0);
		if (!world.isClient) {
			int i = (Integer)state.get(AGE);
			if (i >= 7) {
				int j = 3 + id;

				for (int k = 0; k < j; k++) {
					if (world.random.nextInt(15) <= i) {
						onBlockBreak(world, pos, new ItemStack(this.getSeedItem(), 1, 0));
					}
				}
			}
		}
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return state.get(AGE) == 7 ? this.getHarvestItem() : this.getSeedItem();
	}

	@Override
	public Item getPickItem(World world, BlockPos pos) {
		return this.getSeedItem();
	}

	@Override
	public boolean canGrow(World world, BlockPos pos, BlockState state, boolean bl) {
		return (Integer)state.get(AGE) < 7;
	}

	@Override
	public boolean canBeFertilized(World world, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(World world, Random random, BlockPos pos, BlockState state) {
		this.applyGrowth(world, pos, state);
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
