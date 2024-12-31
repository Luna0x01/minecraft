package net.minecraft.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class RedstoneTorchBlock extends TorchBlock {
	private static Map<World, List<RedstoneTorchBlock.TurnOffEntry>> turnOffEntries = Maps.newHashMap();
	private final boolean lit;

	private boolean isBurnedOut(World world, BlockPos pos, boolean turnOff) {
		if (!turnOffEntries.containsKey(world)) {
			turnOffEntries.put(world, Lists.newArrayList());
		}

		List<RedstoneTorchBlock.TurnOffEntry> list = (List<RedstoneTorchBlock.TurnOffEntry>)turnOffEntries.get(world);
		if (turnOff) {
			list.add(new RedstoneTorchBlock.TurnOffEntry(pos, world.getLastUpdateTime()));
		}

		int i = 0;

		for (int j = 0; j < list.size(); j++) {
			RedstoneTorchBlock.TurnOffEntry turnOffEntry = (RedstoneTorchBlock.TurnOffEntry)list.get(j);
			if (turnOffEntry.pos.equals(pos)) {
				if (++i >= 8) {
					return true;
				}
			}
		}

		return false;
	}

	protected RedstoneTorchBlock(boolean bl) {
		this.lit = bl;
		this.setTickRandomly(true);
		this.setItemGroup(null);
	}

	@Override
	public int getTickRate(World world) {
		return 2;
	}

	@Override
	public void onCreation(World world, BlockPos pos, BlockState state) {
		if (this.lit) {
			for (Direction direction : Direction.values()) {
				world.updateNeighborsAlways(pos.offset(direction), this);
			}
		}
	}

	@Override
	public void onBreaking(World world, BlockPos pos, BlockState state) {
		if (this.lit) {
			for (Direction direction : Direction.values()) {
				world.updateNeighborsAlways(pos.offset(direction), this);
			}
		}
	}

	@Override
	public int getWeakRedstonePower(BlockView view, BlockPos pos, BlockState state, Direction facing) {
		return this.lit && state.get(FACING) != facing ? 15 : 0;
	}

	private boolean shouldNotBeLit(World world, BlockPos pos, BlockState state) {
		Direction direction = ((Direction)state.get(FACING)).getOpposite();
		return world.isEmittingRedstonePower(pos.offset(direction), direction);
	}

	@Override
	public void onRandomTick(World world, BlockPos pos, BlockState state, Random rand) {
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		boolean bl = this.shouldNotBeLit(world, pos, state);
		List<RedstoneTorchBlock.TurnOffEntry> list = (List<RedstoneTorchBlock.TurnOffEntry>)turnOffEntries.get(world);

		while (list != null && !list.isEmpty() && world.getLastUpdateTime() - ((RedstoneTorchBlock.TurnOffEntry)list.get(0)).time > 60L) {
			list.remove(0);
		}

		if (this.lit) {
			if (bl) {
				world.setBlockState(pos, Blocks.UNLIT_REDSTONE_TORCH.getDefaultState().with(FACING, state.get(FACING)), 3);
				if (this.isBurnedOut(world, pos, true)) {
					world.playSound(
						(double)((float)pos.getX() + 0.5F),
						(double)((float)pos.getY() + 0.5F),
						(double)((float)pos.getZ() + 0.5F),
						"random.fizz",
						0.5F,
						2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F
					);

					for (int i = 0; i < 5; i++) {
						double d = (double)pos.getX() + rand.nextDouble() * 0.6 + 0.2;
						double e = (double)pos.getY() + rand.nextDouble() * 0.6 + 0.2;
						double f = (double)pos.getZ() + rand.nextDouble() * 0.6 + 0.2;
						world.addParticle(ParticleType.SMOKE, d, e, f, 0.0, 0.0, 0.0);
					}

					world.createAndScheduleBlockTick(pos, world.getBlockState(pos).getBlock(), 160);
				}
			}
		} else if (!bl && !this.isBurnedOut(world, pos, false)) {
			world.setBlockState(pos, Blocks.REDSTONE_TORCH.getDefaultState().with(FACING, state.get(FACING)), 3);
		}
	}

	@Override
	public void neighborUpdate(World world, BlockPos pos, BlockState state, Block block) {
		if (!this.neighborUpdate(world, pos, state)) {
			if (this.lit == this.shouldNotBeLit(world, pos, state)) {
				world.createAndScheduleBlockTick(pos, this, this.getTickRate(world));
			}
		}
	}

	@Override
	public int getStrongRedstonePower(BlockView view, BlockPos pos, BlockState state, Direction facing) {
		return facing == Direction.DOWN ? this.getWeakRedstonePower(view, pos, state, facing) : 0;
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Item.fromBlock(Blocks.REDSTONE_TORCH);
	}

	@Override
	public boolean emitsRedstonePower() {
		return true;
	}

	@Override
	public void randomDisplayTick(World world, BlockPos pos, BlockState state, Random rand) {
		if (this.lit) {
			double d = (double)pos.getX() + 0.5 + (rand.nextDouble() - 0.5) * 0.2;
			double e = (double)pos.getY() + 0.7 + (rand.nextDouble() - 0.5) * 0.2;
			double f = (double)pos.getZ() + 0.5 + (rand.nextDouble() - 0.5) * 0.2;
			Direction direction = state.get(FACING);
			if (direction.getAxis().isHorizontal()) {
				Direction direction2 = direction.getOpposite();
				double g = 0.27;
				d += 0.27 * (double)direction2.getOffsetX();
				e += 0.22;
				f += 0.27 * (double)direction2.getOffsetZ();
			}

			world.addParticle(ParticleType.REDSTONE, d, e, f, 0.0, 0.0, 0.0);
		}
	}

	@Override
	public Item getPickItem(World world, BlockPos pos) {
		return Item.fromBlock(Blocks.REDSTONE_TORCH);
	}

	@Override
	public boolean isEqualTo(Block block) {
		return block == Blocks.UNLIT_REDSTONE_TORCH || block == Blocks.REDSTONE_TORCH;
	}

	static class TurnOffEntry {
		BlockPos pos;
		long time;

		public TurnOffEntry(BlockPos blockPos, long l) {
			this.pos = blockPos;
			this.time = l;
		}
	}
}
