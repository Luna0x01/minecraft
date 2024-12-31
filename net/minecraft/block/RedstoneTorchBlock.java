package net.minecraft.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.class_4338;
import net.minecraft.class_4342;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.sound.Sounds;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public class RedstoneTorchBlock extends TorchBlock {
	public static final BooleanProperty field_18451 = Properties.LIT;
	private static final Map<BlockView, List<RedstoneTorchBlock.TurnOffEntry>> field_18452 = Maps.newHashMap();

	protected RedstoneTorchBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(field_18451, Boolean.valueOf(true)));
	}

	@Override
	public int getTickDelay(RenderBlockView world) {
		return 2;
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState) {
		for (Direction direction : Direction.values()) {
			world.updateNeighborsAlways(pos.offset(direction), this);
		}
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!moved) {
			for (Direction direction : Direction.values()) {
				world.updateNeighborsAlways(pos.offset(direction), this);
			}
		}
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.getProperty(field_18451) && Direction.UP != direction ? 15 : 0;
	}

	protected boolean shouldNotBeLit(World world, BlockPos pos, BlockState state) {
		return world.isEmittingRedstonePower(pos.down(), Direction.DOWN);
	}

	@Override
	public void scheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		method_16729(state, world, pos, random, this.shouldNotBeLit(world, pos, state));
	}

	public static void method_16729(BlockState blockState, World world, BlockPos blockPos, Random random, boolean bl) {
		List<RedstoneTorchBlock.TurnOffEntry> list = (List<RedstoneTorchBlock.TurnOffEntry>)field_18452.get(world);

		while (list != null && !list.isEmpty() && world.getLastUpdateTime() - ((RedstoneTorchBlock.TurnOffEntry)list.get(0)).field_18454 > 60L) {
			list.remove(0);
		}

		if ((Boolean)blockState.getProperty(field_18451)) {
			if (bl) {
				world.setBlockState(blockPos, blockState.withProperty(field_18451, Boolean.valueOf(false)), 3);
				if (isBurnedOut(world, blockPos, true)) {
					world.playSound(
						null, blockPos, Sounds.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F
					);

					for (int i = 0; i < 5; i++) {
						double d = (double)blockPos.getX() + random.nextDouble() * 0.6 + 0.2;
						double e = (double)blockPos.getY() + random.nextDouble() * 0.6 + 0.2;
						double f = (double)blockPos.getZ() + random.nextDouble() * 0.6 + 0.2;
						world.method_16343(class_4342.field_21363, d, e, f, 0.0, 0.0, 0.0);
					}

					world.getBlockTickScheduler().schedule(blockPos, world.getBlockState(blockPos).getBlock(), 160);
				}
			}
		} else if (!bl && !isBurnedOut(world, blockPos, false)) {
			world.setBlockState(blockPos, blockState.withProperty(field_18451, Boolean.valueOf(true)), 3);
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos) {
		if ((Boolean)state.getProperty(field_18451) == this.shouldNotBeLit(world, pos, state) && !world.getBlockTickScheduler().method_16420(pos, this)) {
			world.getBlockTickScheduler().schedule(pos, this, this.getTickDelay(world));
		}
	}

	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return direction == Direction.DOWN ? state.getWeakRedstonePower(world, pos, direction) : 0;
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if ((Boolean)state.getProperty(field_18451)) {
			double d = (double)pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
			double e = (double)pos.getY() + 0.7 + (random.nextDouble() - 0.5) * 0.2;
			double f = (double)pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
			world.method_16343(class_4338.field_21339, d, e, f, 0.0, 0.0, 0.0);
		}
	}

	@Override
	public int getLuminance(BlockState state) {
		return state.getProperty(field_18451) ? super.getLuminance(state) : 0;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(field_18451);
	}

	private static boolean isBurnedOut(World world2, BlockPos world, boolean pos) {
		List<RedstoneTorchBlock.TurnOffEntry> list = (List<RedstoneTorchBlock.TurnOffEntry>)field_18452.get(world2);
		if (list == null) {
			list = Lists.newArrayList();
			field_18452.put(world2, list);
		}

		if (pos) {
			list.add(new RedstoneTorchBlock.TurnOffEntry(world.toImmutable(), world2.getLastUpdateTime()));
		}

		int i = 0;

		for (int j = 0; j < list.size(); j++) {
			RedstoneTorchBlock.TurnOffEntry turnOffEntry = (RedstoneTorchBlock.TurnOffEntry)list.get(j);
			if (turnOffEntry.field_18453.equals(world)) {
				if (++i >= 8) {
					return true;
				}
			}
		}

		return false;
	}

	public static class TurnOffEntry {
		private final BlockPos field_18453;
		private final long field_18454;

		public TurnOffEntry(BlockPos blockPos, long l) {
			this.field_18453 = blockPos;
			this.field_18454 = l;
		}
	}
}
