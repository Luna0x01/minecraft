package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RedstoneLampBlock extends Block {
	public static final BooleanProperty field_18450 = RedstoneTorchBlock.field_18451;

	public RedstoneLampBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(this.getDefaultState().withProperty(field_18450, Boolean.valueOf(false)));
	}

	@Override
	public int getLuminance(BlockState state) {
		return state.getProperty(field_18450) ? super.getLuminance(state) : 0;
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState) {
		super.onBlockAdded(state, world, pos, oldState);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return this.getDefaultState().withProperty(field_18450, Boolean.valueOf(context.getWorld().isReceivingRedstonePower(context.getBlockPos())));
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos) {
		if (!world.isClient) {
			boolean bl = (Boolean)state.getProperty(field_18450);
			if (bl != world.isReceivingRedstonePower(pos)) {
				if (bl) {
					world.getBlockTickScheduler().schedule(pos, this, 4);
				} else {
					world.setBlockState(pos, state.method_16930(field_18450), 2);
				}
			}
		}
	}

	@Override
	public void scheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		if (!world.isClient) {
			if ((Boolean)state.getProperty(field_18450) && !world.isReceivingRedstonePower(pos)) {
				world.setBlockState(pos, state.method_16930(field_18450), 2);
			}
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(field_18450);
	}
}
