package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.world.World;

public class CommandBlock extends BlockWithEntity {
	public static final BooleanProperty TRIGGERED = BooleanProperty.of("triggered");

	public CommandBlock() {
		super(Material.IRON, MaterialColor.ORANGE);
		this.setDefaultState(this.stateManager.getDefaultState().with(TRIGGERED, false));
	}

	@Override
	public BlockEntity createBlockEntity(World world, int id) {
		return new CommandBlockBlockEntity();
	}

	@Override
	public void neighborUpdate(World world, BlockPos pos, BlockState state, Block block) {
		if (!world.isClient) {
			boolean bl = world.isReceivingRedstonePower(pos);
			boolean bl2 = (Boolean)state.get(TRIGGERED);
			if (bl && !bl2) {
				world.setBlockState(pos, state.with(TRIGGERED, true), 4);
				world.createAndScheduleBlockTick(pos, this, this.getTickRate(world));
			} else if (!bl && bl2) {
				world.setBlockState(pos, state.with(TRIGGERED, false), 4);
			}
		}
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof CommandBlockBlockEntity) {
			((CommandBlockBlockEntity)blockEntity).getCommandExecutor().execute(world);
			world.updateHorizontalAdjacent(pos, this);
		}
	}

	@Override
	public int getTickRate(World world) {
		return 1;
	}

	@Override
	public boolean onUse(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction direction, float posX, float posY, float posZ) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		return blockEntity instanceof CommandBlockBlockEntity ? ((CommandBlockBlockEntity)blockEntity).getCommandExecutor().interact(player) : false;
	}

	@Override
	public boolean hasComparatorOutput() {
		return true;
	}

	@Override
	public int getComparatorOutput(World world, BlockPos pos) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		return blockEntity instanceof CommandBlockBlockEntity ? ((CommandBlockBlockEntity)blockEntity).getCommandExecutor().getSuccessCount() : 0;
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof CommandBlockBlockEntity) {
			CommandBlockExecutor commandBlockExecutor = ((CommandBlockBlockEntity)blockEntity).getCommandExecutor();
			if (itemStack.hasCustomName()) {
				commandBlockExecutor.setName(itemStack.getCustomName());
			}

			if (!world.isClient) {
				commandBlockExecutor.setTrackOutput(world.getGameRules().getBoolean("sendCommandFeedback"));
			}
		}
	}

	@Override
	public int getDropCount(Random rand) {
		return 0;
	}

	@Override
	public int getBlockType() {
		return 3;
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(TRIGGERED, (data & 1) > 0);
	}

	@Override
	public int getData(BlockState state) {
		int i = 0;
		if ((Boolean)state.get(TRIGGERED)) {
			i |= 1;
		}

		return i;
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, TRIGGERED);
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		return this.getDefaultState().with(TRIGGERED, false);
	}
}
