package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Itemable;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class StemBlock extends PlantBlock implements Growable {
	public static final IntProperty AGE = Properties.AGE_7;
	protected static final VoxelShape[] AGE_TO_SHAPE = new VoxelShape[]{
		Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 2.0, 9.0),
		Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 4.0, 9.0),
		Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 6.0, 9.0),
		Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 8.0, 9.0),
		Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 10.0, 9.0),
		Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 12.0, 9.0),
		Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 14.0, 9.0),
		Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 16.0, 9.0)
	};
	private final GourdBlock gourdBlock;

	protected StemBlock(GourdBlock gourdBlock, Block.Builder builder) {
		super(builder);
		this.gourdBlock = gourdBlock;
		this.setDefaultState(this.stateManager.method_16923().withProperty(AGE, Integer.valueOf(0)));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return AGE_TO_SHAPE[state.getProperty(AGE)];
	}

	@Override
	protected boolean canPlantOnTop(BlockState state, BlockView world, BlockPos pos) {
		return state.getBlock() == Blocks.FARMLAND;
	}

	@Override
	public void scheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		super.scheduledTick(state, world, pos, random);
		if (world.method_16379(pos.up(), 0) >= 9) {
			float f = CropBlock.getAvailableMoisture(this, world, pos);
			if (random.nextInt((int)(25.0F / f) + 1) == 0) {
				int i = (Integer)state.getProperty(AGE);
				if (i < 7) {
					state = state.withProperty(AGE, Integer.valueOf(i + 1));
					world.setBlockState(pos, state, 2);
				} else {
					Direction direction = Direction.DirectionType.HORIZONTAL.getRandomDirection(random);
					BlockPos blockPos = pos.offset(direction);
					Block block = world.getBlockState(blockPos.down()).getBlock();
					if (world.getBlockState(blockPos).isAir()
						&& (block == Blocks.FARMLAND || block == Blocks.DIRT || block == Blocks.COARSE_DIRT || block == Blocks.PODZOL || block == Blocks.GRASS_BLOCK)) {
						world.setBlockState(blockPos, this.gourdBlock.getDefaultState());
						world.setBlockState(pos, this.gourdBlock.getAttachedStem().getDefaultState().withProperty(HorizontalFacingBlock.FACING, direction));
					}
				}
			}
		}
	}

	@Override
	public void method_410(BlockState blockState, World world, BlockPos blockPos, float f, int i) {
		super.method_410(blockState, world, blockPos, f, i);
		if (!world.isClient) {
			Item item = this.getSeeds();
			if (item != null) {
				int j = (Integer)blockState.getProperty(AGE);

				for (int k = 0; k < 3; k++) {
					if (world.random.nextInt(15) <= j) {
						onBlockBreak(world, blockPos, new ItemStack(item));
					}
				}
			}
		}
	}

	@Nullable
	protected Item getSeeds() {
		if (this.gourdBlock == Blocks.PUMPKIN) {
			return Items.PUMPKIN_SEEDS;
		} else {
			return this.gourdBlock == Blocks.MELON_BLOCK ? Items.MELON_SEEDS : null;
		}
	}

	@Override
	public Itemable getDroppedItem(BlockState state, World world, BlockPos pos, int fortuneLevel) {
		return Items.AIR;
	}

	@Override
	public ItemStack getPickBlock(BlockView world, BlockPos pos, BlockState state) {
		Item item = this.getSeeds();
		return item == null ? ItemStack.EMPTY : new ItemStack(item);
	}

	@Override
	public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
		return (Integer)state.getProperty(AGE) != 7;
	}

	@Override
	public boolean canBeFertilized(World world, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(World world, Random random, BlockPos pos, BlockState state) {
		int i = Math.min(7, (Integer)state.getProperty(AGE) + MathHelper.nextInt(world.random, 2, 5));
		BlockState blockState = state.withProperty(AGE, Integer.valueOf(i));
		world.setBlockState(pos, blockState, 2);
		if (i == 7) {
			blockState.scheduledTick(world, pos, world.random);
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(AGE);
	}

	public GourdBlock getGourdBlock() {
		return this.gourdBlock;
	}
}
