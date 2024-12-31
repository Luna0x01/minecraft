package net.minecraft.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.class_3605;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ComparatorBlockEntity;
import net.minecraft.block.enums.ComparatorMode;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.Sounds;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ComparatorBlock extends AbstractRedstoneGateBlock implements BlockEntityProvider {
	public static final EnumProperty<ComparatorMode> MODE = Properties.COMPARATOR_MODE;

	public ComparatorBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(
			this.stateManager
				.method_16923()
				.withProperty(FACING, Direction.NORTH)
				.withProperty(POWERED, Boolean.valueOf(false))
				.withProperty(MODE, ComparatorMode.COMPARE)
		);
	}

	@Override
	protected int getUpdateDelayInternal(BlockState state) {
		return 2;
	}

	@Override
	protected int getOutputLevel(BlockView world, BlockPos pos, BlockState state) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		return blockEntity instanceof ComparatorBlockEntity ? ((ComparatorBlockEntity)blockEntity).getOutputSignal() : 0;
	}

	private int calculateOutputSignal(World world, BlockPos pos, BlockState state) {
		return state.getProperty(MODE) == ComparatorMode.SUBTRACT
			? Math.max(this.getPower(world, pos, state) - this.getMaxInputLevelSides(world, pos, state), 0)
			: this.getPower(world, pos, state);
	}

	@Override
	protected boolean hasPower(World world, BlockPos pos, BlockState state) {
		int i = this.getPower(world, pos, state);
		if (i >= 15) {
			return true;
		} else {
			return i == 0 ? false : i >= this.getMaxInputLevelSides(world, pos, state);
		}
	}

	@Override
	protected void removeBlockEntity(World world, BlockPos pos) {
		world.removeBlockEntity(pos);
	}

	@Override
	protected int getPower(World world, BlockPos pos, BlockState state) {
		int i = super.getPower(world, pos, state);
		Direction direction = state.getProperty(FACING);
		BlockPos blockPos = pos.offset(direction);
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.method_16910()) {
			i = blockState.getComparatorOutput(world, blockPos);
		} else if (i < 15 && blockState.method_16907()) {
			blockPos = blockPos.offset(direction);
			blockState = world.getBlockState(blockPos);
			if (blockState.method_16910()) {
				i = blockState.getComparatorOutput(world, blockPos);
			} else if (blockState.isAir()) {
				ItemFrameEntity itemFrameEntity = this.getAttachedItemFrame(world, direction, blockPos);
				if (itemFrameEntity != null) {
					i = itemFrameEntity.getComparatorPower();
				}
			}
		}

		return i;
	}

	@Nullable
	private ItemFrameEntity getAttachedItemFrame(World world, Direction facing, BlockPos pos) {
		List<ItemFrameEntity> list = world.method_16325(
			ItemFrameEntity.class,
			new Box((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1)),
			itemFrameEntity -> itemFrameEntity != null && itemFrameEntity.getHorizontalDirection() == facing
		);
		return list.size() == 1 ? (ItemFrameEntity)list.get(0) : null;
	}

	@Override
	public boolean onUse(
		BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction direction, float distanceX, float distanceY, float distanceZ
	) {
		if (!player.abilities.allowModifyWorld) {
			return false;
		} else {
			state = state.method_16930(MODE);
			float f = state.getProperty(MODE) == ComparatorMode.SUBTRACT ? 0.55F : 0.5F;
			world.playSound(player, pos, Sounds.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3F, f);
			world.setBlockState(pos, state, 2);
			this.update(world, pos, state);
			return true;
		}
	}

	@Override
	protected void updatePowered(World world, BlockPos pos, BlockState state) {
		if (!world.getBlockTickScheduler().method_16420(pos, this)) {
			int i = this.calculateOutputSignal(world, pos, state);
			BlockEntity blockEntity = world.getBlockEntity(pos);
			int j = blockEntity instanceof ComparatorBlockEntity ? ((ComparatorBlockEntity)blockEntity).getOutputSignal() : 0;
			if (i != j || (Boolean)state.getProperty(POWERED) != this.hasPower(world, pos, state)) {
				class_3605 lv = this.isTargetNotAligned(world, pos, state) ? class_3605.HIGH : class_3605.NORMAL;
				world.getBlockTickScheduler().method_16419(pos, this, 2, lv);
			}
		}
	}

	private void update(World world, BlockPos pos, BlockState state) {
		int i = this.calculateOutputSignal(world, pos, state);
		BlockEntity blockEntity = world.getBlockEntity(pos);
		int j = 0;
		if (blockEntity instanceof ComparatorBlockEntity) {
			ComparatorBlockEntity comparatorBlockEntity = (ComparatorBlockEntity)blockEntity;
			j = comparatorBlockEntity.getOutputSignal();
			comparatorBlockEntity.setOutputSignal(i);
		}

		if (j != i || state.getProperty(MODE) == ComparatorMode.COMPARE) {
			boolean bl = this.hasPower(world, pos, state);
			boolean bl2 = (Boolean)state.getProperty(POWERED);
			if (bl2 && !bl) {
				world.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(false)), 2);
			} else if (!bl2 && bl) {
				world.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(true)), 2);
			}

			this.updateTarget(world, pos, state);
		}
	}

	@Override
	public void scheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		this.update(world, pos, state);
	}

	@Override
	public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
		super.onSyncedBlockEvent(state, world, pos, type, data);
		BlockEntity blockEntity = world.getBlockEntity(pos);
		return blockEntity != null && blockEntity.onBlockAction(type, data);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new ComparatorBlockEntity();
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(FACING, MODE, POWERED);
	}
}
