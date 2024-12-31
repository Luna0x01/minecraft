package net.minecraft.block;

import net.minecraft.class_4342;
import net.minecraft.block.enums.Instrument;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class NoteBlock extends Block {
	public static final EnumProperty<Instrument> field_18415 = Properties.INSTRUMENT;
	public static final BooleanProperty field_18416 = Properties.POWERED;
	public static final IntProperty field_18417 = Properties.NOTE;

	public NoteBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(
			this.stateManager
				.method_16923()
				.withProperty(field_18415, Instrument.HARP)
				.withProperty(field_18417, Integer.valueOf(0))
				.withProperty(field_18416, Boolean.valueOf(false))
		);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return this.getDefaultState().withProperty(field_18415, Instrument.getByBlockState(context.getWorld().getBlockState(context.getBlockPos().down())));
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		return direction == Direction.DOWN
			? state.withProperty(field_18415, Instrument.getByBlockState(neighborState))
			: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos) {
		boolean bl = world.isReceivingRedstonePower(pos);
		if (bl != (Boolean)state.getProperty(field_18416)) {
			if (bl) {
				this.method_16709(world, pos);
			}

			world.setBlockState(pos, state.withProperty(field_18416, Boolean.valueOf(bl)), 3);
		}
	}

	private void method_16709(World world, BlockPos blockPos) {
		if (world.getBlockState(blockPos.up()).isAir()) {
			world.addBlockAction(blockPos, this, 0, 0);
		}
	}

	@Override
	public boolean onUse(
		BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction direction, float distanceX, float distanceY, float distanceZ
	) {
		if (world.isClient) {
			return true;
		} else {
			state = state.method_16930(field_18417);
			world.setBlockState(pos, state, 3);
			this.method_16709(world, pos);
			player.method_15928(Stats.TUNE_NOTEBLOCK);
			return true;
		}
	}

	@Override
	public void method_420(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity) {
		if (!world.isClient) {
			this.method_16709(world, blockPos);
			playerEntity.method_15928(Stats.PLAY_NOTEBLOCK);
		}
	}

	@Override
	public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
		int i = (Integer)state.getProperty(field_18417);
		float f = (float)Math.pow(2.0, (double)(i - 12) / 12.0);
		world.playSound(null, pos, ((Instrument)state.getProperty(field_18415)).asSound(), SoundCategory.RECORDS, 3.0F, f);
		world.method_16343(class_4342.field_21359, (double)pos.getX() + 0.5, (double)pos.getY() + 1.2, (double)pos.getZ() + 0.5, (double)i / 24.0, 0.0, 0.0);
		return true;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(field_18415, field_18416, field_18417);
	}
}
