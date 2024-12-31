package net.minecraft.block;

import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Itemable;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public class CropBlock extends PlantBlock implements Growable {
	public static final IntProperty AGE = Properties.AGE_7;
	private static final VoxelShape[] AGE_TO_SHAPE = new VoxelShape[]{
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 10.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 14.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)
	};

	protected CropBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(this.getAge(), Integer.valueOf(0)));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return AGE_TO_SHAPE[state.getProperty(this.getAge())];
	}

	@Override
	protected boolean canPlantOnTop(BlockState state, BlockView world, BlockPos pos) {
		return state.getBlock() == Blocks.FARMLAND;
	}

	public IntProperty getAge() {
		return AGE;
	}

	public int getMaxAge() {
		return 7;
	}

	protected int getAge(BlockState state) {
		return (Integer)state.getProperty(this.getAge());
	}

	public BlockState withAge(int age) {
		return this.getDefaultState().withProperty(this.getAge(), Integer.valueOf(age));
	}

	public boolean isMature(BlockState state) {
		return (Integer)state.getProperty(this.getAge()) >= this.getMaxAge();
	}

	@Override
	public void scheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		super.scheduledTick(state, world, pos, random);
		if (world.method_16379(pos.up(), 0) >= 9) {
			int i = this.getAge(state);
			if (i < this.getMaxAge()) {
				float f = getAvailableMoisture(this, world, pos);
				if (random.nextInt((int)(25.0F / f) + 1) == 0) {
					world.setBlockState(pos, this.withAge(i + 1), 2);
				}
			}
		}
	}

	public void applyGrowth(World world, BlockPos pos, BlockState state) {
		int i = this.getAge(state) + this.getGrowthAmount(world);
		int j = this.getMaxAge();
		if (i > j) {
			i = j;
		}

		world.setBlockState(pos, this.withAge(i), 2);
	}

	protected int getGrowthAmount(World world) {
		return MathHelper.nextInt(world.random, 2, 5);
	}

	protected static float getAvailableMoisture(Block block, BlockView world, BlockPos pos) {
		float f = 1.0F;
		BlockPos blockPos = pos.down();

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				float g = 0.0F;
				BlockState blockState = world.getBlockState(blockPos.add(i, 0, j));
				if (blockState.getBlock() == Blocks.FARMLAND) {
					g = 1.0F;
					if ((Integer)blockState.getProperty(FarmlandBlock.field_18317) > 0) {
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
	public boolean canPlaceAt(BlockState state, RenderBlockView world, BlockPos pos) {
		return (world.method_16379(pos, 0) >= 8 || world.method_8555(pos)) && super.canPlaceAt(state, world, pos);
	}

	protected Itemable getSeedsItem() {
		return Items.WHEAT_SEEDS;
	}

	protected Itemable getHarvestItem() {
		return Items.WHEAT;
	}

	@Override
	public void method_410(BlockState blockState, World world, BlockPos blockPos, float f, int i) {
		super.method_410(blockState, world, blockPos, f, 0);
		if (!world.isClient) {
			int j = this.getAge(blockState);
			if (j >= this.getMaxAge()) {
				int k = 3 + i;

				for (int l = 0; l < k; l++) {
					if (world.random.nextInt(2 * this.getMaxAge()) <= j) {
						onBlockBreak(world, blockPos, new ItemStack(this.getSeedsItem()));
					}
				}
			}
		}
	}

	@Override
	public Itemable getDroppedItem(BlockState state, World world, BlockPos pos, int fortuneLevel) {
		return this.isMature(state) ? this.getHarvestItem() : this.getSeedsItem();
	}

	@Override
	public ItemStack getPickBlock(BlockView world, BlockPos pos, BlockState state) {
		return new ItemStack(this.getSeedsItem());
	}

	@Override
	public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
		return !this.isMature(state);
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
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(AGE);
	}
}
