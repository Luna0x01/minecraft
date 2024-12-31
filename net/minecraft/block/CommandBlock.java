package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.world.World;

public class CommandBlock extends BlockWithEntity {
	public static final DirectionProperty FACING = FacingBlock.FACING;
	public static final BooleanProperty field_12637 = BooleanProperty.of("conditional");

	public CommandBlock(MaterialColor materialColor) {
		super(Material.IRON, materialColor);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(field_12637, false));
	}

	@Override
	public BlockEntity createBlockEntity(World world, int id) {
		CommandBlockBlockEntity commandBlockBlockEntity = new CommandBlockBlockEntity();
		commandBlockBlockEntity.method_11650(this == Blocks.CHAIN_COMMAND_BLOCK);
		return commandBlockBlockEntity;
	}

	@Override
	public void method_8641(BlockState blockState, World world, BlockPos blockPos, Block block) {
		if (!world.isClient) {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof CommandBlockBlockEntity) {
				CommandBlockBlockEntity commandBlockBlockEntity = (CommandBlockBlockEntity)blockEntity;
				boolean bl = world.isReceivingRedstonePower(blockPos);
				boolean bl2 = commandBlockBlockEntity.method_11653();
				boolean bl3 = commandBlockBlockEntity.method_11654();
				if (bl && !bl2) {
					commandBlockBlockEntity.method_11649(true);
					if (commandBlockBlockEntity.method_11657() != CommandBlockBlockEntity.class_2736.SEQUENCE && !bl3) {
						boolean bl4 = !commandBlockBlockEntity.method_11658() || this.method_11592(world, blockPos, blockState);
						commandBlockBlockEntity.method_11651(bl4);
						world.createAndScheduleBlockTick(blockPos, this, this.getTickRate(world));
						if (bl4) {
							this.method_11591(world, blockPos);
						}
					}
				} else if (!bl && bl2) {
					commandBlockBlockEntity.method_11649(false);
				}
			}
		}
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		if (!world.isClient) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof CommandBlockBlockEntity) {
				CommandBlockBlockEntity commandBlockBlockEntity = (CommandBlockBlockEntity)blockEntity;
				CommandBlockExecutor commandBlockExecutor = commandBlockBlockEntity.getCommandExecutor();
				boolean bl = !ChatUtil.isEmpty(commandBlockExecutor.getCommand());
				CommandBlockBlockEntity.class_2736 lv = commandBlockBlockEntity.method_11657();
				boolean bl2 = !commandBlockBlockEntity.method_11658() || this.method_11592(world, pos, state);
				boolean bl3 = commandBlockBlockEntity.method_11655();
				boolean bl4 = false;
				if (lv != CommandBlockBlockEntity.class_2736.SEQUENCE && bl3 && bl) {
					commandBlockExecutor.execute(world);
					bl4 = true;
				}

				if (commandBlockBlockEntity.method_11653() || commandBlockBlockEntity.method_11654()) {
					if (lv == CommandBlockBlockEntity.class_2736.SEQUENCE && bl2 && bl) {
						commandBlockExecutor.execute(world);
						bl4 = true;
					}

					if (lv == CommandBlockBlockEntity.class_2736.AUTO) {
						world.createAndScheduleBlockTick(pos, this, this.getTickRate(world));
						if (bl2) {
							this.method_11591(world, pos);
						}
					}
				}

				if (!bl4) {
					commandBlockExecutor.setSuccessCount(0);
				}

				commandBlockBlockEntity.method_11651(bl2);
				world.updateHorizontalAdjacent(pos, this);
			}
		}
	}

	public boolean method_11592(World world, BlockPos blockPos, BlockState blockState) {
		Direction direction = blockState.get(FACING);
		BlockEntity blockEntity = world.getBlockEntity(blockPos.offset(direction.getOpposite()));
		return blockEntity instanceof CommandBlockBlockEntity && ((CommandBlockBlockEntity)blockEntity).getCommandExecutor().getSuccessCount() > 0;
	}

	@Override
	public int getTickRate(World world) {
		return 1;
	}

	@Override
	public boolean method_421(
		World world,
		BlockPos blockPos,
		BlockState blockState,
		PlayerEntity playerEntity,
		Hand hand,
		@Nullable ItemStack itemStack,
		Direction direction,
		float f,
		float g,
		float h
	) {
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		if (blockEntity instanceof CommandBlockBlockEntity) {
			if (!playerEntity.abilities.creativeMode) {
				return false;
			} else {
				playerEntity.method_13260((CommandBlockBlockEntity)blockEntity);
				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean method_11577(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		return blockEntity instanceof CommandBlockBlockEntity ? ((CommandBlockBlockEntity)blockEntity).getCommandExecutor().getSuccessCount() : 0;
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof CommandBlockBlockEntity) {
			CommandBlockBlockEntity commandBlockBlockEntity = (CommandBlockBlockEntity)blockEntity;
			CommandBlockExecutor commandBlockExecutor = commandBlockBlockEntity.getCommandExecutor();
			if (itemStack.hasCustomName()) {
				commandBlockExecutor.setName(itemStack.getCustomName());
			}

			if (!world.isClient) {
				NbtCompound nbtCompound = itemStack.getNbt();
				if (nbtCompound == null || !nbtCompound.contains("BlockEntityTag", 10)) {
					commandBlockExecutor.setTrackOutput(world.getGameRules().getBoolean("sendCommandFeedback"));
					commandBlockBlockEntity.method_11650(this == Blocks.CHAIN_COMMAND_BLOCK);
				}

				if (commandBlockBlockEntity.method_11657() == CommandBlockBlockEntity.class_2736.SEQUENCE) {
					boolean bl = world.isReceivingRedstonePower(pos);
					commandBlockBlockEntity.method_11649(bl);
				}
			}
		}
	}

	@Override
	public int getDropCount(Random rand) {
		return 0;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(FACING, Direction.getById(data & 7)).with(field_12637, (data & 8) != 0);
	}

	@Override
	public int getData(BlockState state) {
		return ((Direction)state.get(FACING)).getId() | (state.get(field_12637) ? 8 : 0);
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		return state.withRotation(mirror.getRotation(state.get(FACING)));
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, FACING, field_12637);
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		return this.getDefaultState().with(FACING, PistonBlock.method_9000(pos, entity)).with(field_12637, false);
	}

	public void method_11591(World world, BlockPos blockPos) {
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock() == Blocks.COMMAND_BLOCK || blockState.getBlock() == Blocks.REPEATING_COMMAND_BLOCK) {
			BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos);
			mutable.move(blockState.get(FACING));

			for (BlockEntity blockEntity = world.getBlockEntity(mutable); blockEntity instanceof CommandBlockBlockEntity; blockEntity = world.getBlockEntity(mutable)) {
				CommandBlockBlockEntity commandBlockBlockEntity = (CommandBlockBlockEntity)blockEntity;
				if (commandBlockBlockEntity.method_11657() != CommandBlockBlockEntity.class_2736.SEQUENCE) {
					break;
				}

				BlockState blockState2 = world.getBlockState(mutable);
				Block block = blockState2.getBlock();
				if (block != Blocks.CHAIN_COMMAND_BLOCK || world.method_11489(mutable, block)) {
					break;
				}

				world.createAndScheduleBlockTick(new BlockPos(mutable), block, this.getTickRate(world));
				mutable.move(blockState2.get(FACING));
			}
		}
	}
}
