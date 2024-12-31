package net.minecraft.server.command;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.CommandStats;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SetBlockCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "setblock";
	}

	@Override
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.setblock.usage";
	}

	@Override
	public void execute(CommandSource source, String[] args) throws CommandException {
		if (args.length < 4) {
			throw new IncorrectUsageException("commands.setblock.usage");
		} else {
			source.setStat(CommandStats.Type.AFFECTED_BLOCKS, 0);
			BlockPos blockPos = getBlockPos(source, args, 0, false);
			Block block = AbstractCommand.getBlock(source, args[3]);
			int i = 0;
			if (args.length >= 5) {
				i = parseClampedInt(args[4], 0, 15);
			}

			World world = source.getWorld();
			if (!world.blockExists(blockPos)) {
				throw new CommandException("commands.setblock.outOfWorld");
			} else {
				NbtCompound nbtCompound = new NbtCompound();
				boolean bl = false;
				if (args.length >= 7 && block.hasBlockEntity()) {
					String string = method_4635(source, args, 6).asUnformattedString();

					try {
						nbtCompound = StringNbtReader.parse(string);
						bl = true;
					} catch (NbtException var12) {
						throw new CommandException("commands.setblock.tagError", var12.getMessage());
					}
				}

				if (args.length >= 6) {
					if (args[5].equals("destroy")) {
						world.removeBlock(blockPos, true);
						if (block == Blocks.AIR) {
							run(source, this, "commands.setblock.success", new Object[0]);
							return;
						}
					} else if (args[5].equals("keep") && !world.isAir(blockPos)) {
						throw new CommandException("commands.setblock.noChange");
					}
				}

				BlockEntity blockEntity = world.getBlockEntity(blockPos);
				if (blockEntity != null) {
					if (blockEntity instanceof Inventory) {
						((Inventory)blockEntity).clear();
					}

					world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), block == Blocks.AIR ? 2 : 4);
				}

				BlockState blockState = block.stateFromData(i);
				if (!world.setBlockState(blockPos, blockState, 2)) {
					throw new CommandException("commands.setblock.noChange");
				} else {
					if (bl) {
						BlockEntity blockEntity2 = world.getBlockEntity(blockPos);
						if (blockEntity2 != null) {
							nbtCompound.putInt("x", blockPos.getX());
							nbtCompound.putInt("y", blockPos.getY());
							nbtCompound.putInt("z", blockPos.getZ());
							blockEntity2.fromNbt(nbtCompound);
						}
					}

					world.updateNeighbors(blockPos, blockState.getBlock());
					source.setStat(CommandStats.Type.AFFECTED_BLOCKS, 1);
					run(source, this, "commands.setblock.success", new Object[0]);
				}
			}
		}
	}

	@Override
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		if (args.length > 0 && args.length <= 3) {
			return method_10707(args, 0, pos);
		} else if (args.length == 4) {
			return method_10708(args, Block.REGISTRY.keySet());
		} else {
			return args.length == 6 ? method_2894(args, new String[]{"replace", "destroy", "keep"}) : null;
		}
	}
}
