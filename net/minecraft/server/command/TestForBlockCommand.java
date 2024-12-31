package net.minecraft.server.command;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.CommandStats;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.command.InvalidNumberException;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TestForBlockCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "testforblock";
	}

	@Override
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.testforblock.usage";
	}

	@Override
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (args.length < 4) {
			throw new IncorrectUsageException("commands.testforblock.usage");
		} else {
			commandSource.setStat(CommandStats.Type.AFFECTED_BLOCKS, 0);
			BlockPos blockPos = getBlockPos(commandSource, args, 0, false);
			Block block = getBlock(commandSource, args[3]);
			if (block == null) {
				throw new InvalidNumberException("commands.setblock.notFound", args[3]);
			} else {
				World world = commandSource.getWorld();
				if (!world.blockExists(blockPos)) {
					throw new CommandException("commands.testforblock.outOfWorld");
				} else {
					NbtCompound nbtCompound = new NbtCompound();
					boolean bl = false;
					if (args.length >= 6 && block.hasBlockEntity()) {
						String string = method_10706(args, 5);

						try {
							nbtCompound = StringNbtReader.parse(string);
							bl = true;
						} catch (NbtException var14) {
							throw new CommandException("commands.setblock.tagError", var14.getMessage());
						}
					}

					BlockState blockState = world.getBlockState(blockPos);
					Block block2 = blockState.getBlock();
					if (block2 != block) {
						throw new CommandException(
							"commands.testforblock.failed.tile", blockPos.getX(), blockPos.getY(), blockPos.getZ(), block2.getTranslatedName(), block.getTranslatedName()
						);
					} else if (args.length >= 5 && !AbstractCommand.method_13904(block, args[4]).apply(blockState)) {
						try {
							int i = blockState.getBlock().getData(blockState);
							throw new CommandException("commands.testforblock.failed.data", blockPos.getX(), blockPos.getY(), blockPos.getZ(), i, Integer.parseInt(args[4]));
						} catch (NumberFormatException var13) {
							throw new CommandException("commands.testforblock.failed.data", blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockState.toString(), args[4]);
						}
					} else {
						if (bl) {
							BlockEntity blockEntity = world.getBlockEntity(blockPos);
							if (blockEntity == null) {
								throw new CommandException("commands.testforblock.failed.tileEntity", blockPos.getX(), blockPos.getY(), blockPos.getZ());
							}

							NbtCompound nbtCompound2 = blockEntity.toNbt(new NbtCompound());
							if (!NbtHelper.matches(nbtCompound, nbtCompound2, true)) {
								throw new CommandException("commands.testforblock.failed.nbt", blockPos.getX(), blockPos.getY(), blockPos.getZ());
							}
						}

						commandSource.setStat(CommandStats.Type.AFFECTED_BLOCKS, 1);
						run(commandSource, this, "commands.testforblock.success", new Object[]{blockPos.getX(), blockPos.getY(), blockPos.getZ()});
					}
				}
			}
		}
	}

	@Override
	public List<String> method_10738(MinecraftServer server, CommandSource source, String[] strings, @Nullable BlockPos pos) {
		if (strings.length > 0 && strings.length <= 3) {
			return method_10707(strings, 0, pos);
		} else {
			return strings.length == 4 ? method_10708(strings, Block.REGISTRY.getKeySet()) : Collections.emptyList();
		}
	}
}
