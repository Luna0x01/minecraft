package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.class_4342;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Itemable;
import net.minecraft.item.Items;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class LeavesBlock extends Block {
	public static final IntProperty field_18388 = Properties.DISTANCE_1_7;
	public static final BooleanProperty field_18389 = Properties.PERSISTENT;
	protected static boolean field_18390;

	public LeavesBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(field_18388, Integer.valueOf(7)).withProperty(field_18389, Boolean.valueOf(false)));
	}

	@Override
	public boolean hasRandomTicks(BlockState state) {
		return (Integer)state.getProperty(field_18388) == 7 && !(Boolean)state.getProperty(field_18389);
	}

	@Override
	public void method_16582(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!(Boolean)blockState.getProperty(field_18389) && (Integer)blockState.getProperty(field_18388) == 7) {
			blockState.method_16867(world, blockPos, 0);
			world.method_8553(blockPos);
		}
	}

	@Override
	public void scheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		world.setBlockState(pos, method_16692(state, world, pos), 3);
	}

	@Override
	public int getLightSubtracted(BlockState state, BlockView world, BlockPos pos) {
		return 1;
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		int i = method_16694(neighborState) + 1;
		if (i != 1 || (Integer)state.getProperty(field_18388) != i) {
			world.getBlockTickScheduler().schedule(pos, this, 1);
		}

		return state;
	}

	private static BlockState method_16692(BlockState blockState, IWorld iWorld, BlockPos blockPos) {
		int i = 7;

		try (BlockPos.Pooled pooled = BlockPos.Pooled.get()) {
			for (Direction direction : Direction.values()) {
				pooled.set(blockPos).move(direction);
				i = Math.min(i, method_16694(iWorld.getBlockState(pooled)) + 1);
				if (i == 1) {
					break;
				}
			}
		}

		return blockState.withProperty(field_18388, Integer.valueOf(i));
	}

	private static int method_16694(BlockState blockState) {
		if (BlockTags.LOGS.contains(blockState.getBlock())) {
			return 0;
		} else {
			return blockState.getBlock() instanceof LeavesBlock ? (Integer)blockState.getProperty(field_18388) : 7;
		}
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if (world.hasRain(pos.up()) && !world.getBlockState(pos.down()).method_16913() && random.nextInt(15) == 1) {
			double d = (double)((float)pos.getX() + random.nextFloat());
			double e = (double)pos.getY() - 0.05;
			double f = (double)((float)pos.getZ() + random.nextFloat());
			world.method_16343(class_4342.field_21386, d, e, f, 0.0, 0.0, 0.0);
		}
	}

	@Override
	public int getDropCount(BlockState state, Random random) {
		return random.nextInt(20) == 0 ? 1 : 0;
	}

	@Override
	public Itemable getDroppedItem(BlockState state, World world, BlockPos pos, int fortuneLevel) {
		Block block = state.getBlock();
		if (block == Blocks.OAK_LEAVES) {
			return Blocks.OAK_SAPLING;
		} else if (block == Blocks.SPRUCE_LEAVES) {
			return Blocks.SPRUCE_SAPLING;
		} else if (block == Blocks.BIRCH_LEAVES) {
			return Blocks.BIRCH_SAPLING;
		} else if (block == Blocks.JUNGLE_LEAVES) {
			return Blocks.JUNGLE_SAPLING;
		} else if (block == Blocks.ACACIA_LEAVES) {
			return Blocks.ACACIA_SAPLING;
		} else {
			return block == Blocks.DARK_OAK_LEAVES ? Blocks.DARK_OAK_SAPLING : Blocks.OAK_SAPLING;
		}
	}

	@Override
	public void method_410(BlockState blockState, World world, BlockPos blockPos, float f, int i) {
		if (!world.isClient) {
			int j = this.method_16693(blockState);
			if (i > 0) {
				j -= 2 << i;
				if (j < 10) {
					j = 10;
				}
			}

			if (world.random.nextInt(j) == 0) {
				onBlockBreak(world, blockPos, new ItemStack(this.getDroppedItem(blockState, world, blockPos, i)));
			}

			j = 200;
			if (i > 0) {
				j -= 10 << i;
				if (j < 40) {
					j = 40;
				}
			}

			this.method_16691(world, blockPos, blockState, j);
		}
	}

	protected void method_16691(World world, BlockPos blockPos, BlockState blockState, int i) {
		if ((blockState.getBlock() == Blocks.OAK_LEAVES || blockState.getBlock() == Blocks.DARK_OAK_LEAVES) && world.random.nextInt(i) == 0) {
			onBlockBreak(world, blockPos, new ItemStack(Items.APPLE));
		}
	}

	protected int method_16693(BlockState blockState) {
		return blockState.getBlock() == Blocks.JUNGLE_LEAVES ? 40 : 20;
	}

	public static void setGraphics(boolean bl) {
		field_18390 = bl;
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return field_18390 ? RenderLayer.CUTOUT_MIPPED : RenderLayer.SOLID;
	}

	@Override
	public boolean method_13703(BlockState state) {
		return false;
	}

	@Override
	public void method_8651(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
		if (!world.isClient && stack.getItem() == Items.SHEARS) {
			player.method_15932(Stats.MINED.method_21429(this));
			player.addExhaustion(0.005F);
			onBlockBreak(world, pos, new ItemStack(this));
		} else {
			super.method_8651(world, player, pos, state, blockEntity, stack);
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(field_18388, field_18389);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return method_16692(this.getDefaultState().withProperty(field_18389, Boolean.valueOf(true)), context.getWorld(), context.getBlockPos());
	}
}
