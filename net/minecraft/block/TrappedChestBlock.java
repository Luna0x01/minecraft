package net.minecraft.block;

import net.minecraft.class_3746;
import net.minecraft.class_4472;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;

public class TrappedChestBlock extends ChestBlock {
	public TrappedChestBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new class_3746();
	}

	@Override
	protected class_4472<Identifier> getOpenStat() {
		return Stats.CUSTOM.method_21429(Stats.TRIGGER_TRAPPED_CHEST);
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return MathHelper.clamp(ChestBlockEntity.method_16792(world, pos), 0, 15);
	}

	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return direction == Direction.UP ? state.getWeakRedstonePower(world, pos, direction) : 0;
	}
}
