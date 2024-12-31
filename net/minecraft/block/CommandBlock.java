package net.minecraft.block;

import java.util.Random;
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
import net.minecraft.world.GameRuleManager;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandBlock extends BlockWithEntity {
	private static final Logger field_15748 = LogManager.getLogger();
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
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos) {
		if (!world.isClient) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof CommandBlockBlockEntity) {
				CommandBlockBlockEntity commandBlockBlockEntity = (CommandBlockBlockEntity)blockEntity;
				boolean bl = world.isReceivingRedstonePower(pos);
				boolean bl2 = commandBlockBlockEntity.method_11653();
				commandBlockBlockEntity.method_11649(bl);
				if (!bl2 && !commandBlockBlockEntity.method_11654() && commandBlockBlockEntity.method_11657() != CommandBlockBlockEntity.class_2736.SEQUENCE) {
					if (bl) {
						commandBlockBlockEntity.method_14368();
						world.createAndScheduleBlockTick(pos, this, this.getTickRate(world));
					}
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
				boolean bl2 = commandBlockBlockEntity.method_11655();
				if (lv == CommandBlockBlockEntity.class_2736.AUTO) {
					commandBlockBlockEntity.method_14368();
					if (bl2) {
						this.execute(state, world, pos, commandBlockExecutor, bl);
					} else if (commandBlockBlockEntity.method_11658()) {
						commandBlockExecutor.setSuccessCount(0);
					}

					if (commandBlockBlockEntity.method_11653() || commandBlockBlockEntity.method_11654()) {
						world.createAndScheduleBlockTick(pos, this, this.getTickRate(world));
					}
				} else if (lv == CommandBlockBlockEntity.class_2736.REDSTONE) {
					if (bl2) {
						this.execute(state, world, pos, commandBlockExecutor, bl);
					} else if (commandBlockBlockEntity.method_11658()) {
						commandBlockExecutor.setSuccessCount(0);
					}
				}

				world.updateHorizontalAdjacent(pos, this);
			}
		}
	}

	private void execute(BlockState state, World world, BlockPos pos, CommandBlockExecutor executor, boolean hasCommand) {
		if (hasCommand) {
			executor.execute(world);
		} else {
			executor.setSuccessCount(0);
		}

		executeCommandChain(world, pos, state.get(FACING));
	}

	@Override
	public int getTickRate(World world) {
		return 1;
	}

	@Override
	public boolean use(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction direction, float f, float g, float h) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof CommandBlockBlockEntity && player.method_13567()) {
			player.method_13260((CommandBlockBlockEntity)blockEntity);
			return true;
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
		return this.getDefaultState().with(FACING, Direction.getLookingDirection(pos, entity)).with(field_12637, false);
	}

	private static void executeCommandChain(World world, BlockPos pos, Direction facing) {
		BlockPos.Mutable mutable = new BlockPos.Mutable(pos);
		GameRuleManager gameRuleManager = world.getGameRules();
		int i = gameRuleManager.getInt("maxCommandChainLength");

		while (i-- > 0) {
			mutable.move(facing);
			BlockState blockState = world.getBlockState(mutable);
			Block block = blockState.getBlock();
			if (block != Blocks.CHAIN_COMMAND_BLOCK) {
				break;
			}

			BlockEntity blockEntity = world.getBlockEntity(mutable);
			if (!(blockEntity instanceof CommandBlockBlockEntity)) {
				break;
			}

			CommandBlockBlockEntity commandBlockBlockEntity = (CommandBlockBlockEntity)blockEntity;
			if (commandBlockBlockEntity.method_11657() != CommandBlockBlockEntity.class_2736.SEQUENCE) {
				break;
			}

			if (commandBlockBlockEntity.method_11653() || commandBlockBlockEntity.method_11654()) {
				CommandBlockExecutor commandBlockExecutor = commandBlockBlockEntity.getCommandExecutor();
				if (commandBlockBlockEntity.method_14368()) {
					if (!commandBlockExecutor.execute(world)) {
						break;
					}

					world.updateHorizontalAdjacent(mutable, block);
				} else if (commandBlockBlockEntity.method_11658()) {
					commandBlockExecutor.setSuccessCount(0);
				}
			}

			facing = blockState.get(FACING);
		}

		if (i <= 0) {
			int j = Math.max(gameRuleManager.getInt("maxCommandChainLength"), 0);
			field_15748.warn("Commandblock chain tried to execure more than " + j + " steps!");
		}
	}
}
