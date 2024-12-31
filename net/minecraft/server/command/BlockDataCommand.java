package net.minecraft.server.command;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.CommandStats;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockDataCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "blockdata";
	}

	@Override
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.blockdata.usage";
	}

	@Override
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (args.length < 4) {
			throw new IncorrectUsageException("commands.blockdata.usage");
		} else {
			commandSource.setStat(CommandStats.Type.AFFECTED_BLOCKS, 0);
			BlockPos blockPos = getBlockPos(commandSource, args, 0, false);
			World world = commandSource.getWorld();
			if (!world.blockExists(blockPos)) {
				throw new CommandException("commands.blockdata.outOfWorld");
			} else {
				BlockState blockState = world.getBlockState(blockPos);
				BlockEntity blockEntity = world.getBlockEntity(blockPos);
				if (blockEntity == null) {
					throw new CommandException("commands.blockdata.notValid");
				} else {
					NbtCompound nbtCompound = blockEntity.toNbt(new NbtCompound());
					NbtCompound nbtCompound2 = (NbtCompound)nbtCompound.copy();

					NbtCompound nbtCompound3;
					try {
						nbtCompound3 = StringNbtReader.parse(method_4635(commandSource, args, 3).asUnformattedString());
					} catch (NbtException var12) {
						throw new CommandException("commands.blockdata.tagError", var12.getMessage());
					}

					nbtCompound.copyFrom(nbtCompound3);
					nbtCompound.putInt("x", blockPos.getX());
					nbtCompound.putInt("y", blockPos.getY());
					nbtCompound.putInt("z", blockPos.getZ());
					if (nbtCompound.equals(nbtCompound2)) {
						throw new CommandException("commands.blockdata.failed", nbtCompound.toString());
					} else {
						blockEntity.fromNbt(nbtCompound);
						blockEntity.markDirty();
						world.method_11481(blockPos, blockState, blockState, 3);
						commandSource.setStat(CommandStats.Type.AFFECTED_BLOCKS, 1);
						run(commandSource, this, "commands.blockdata.success", new Object[]{nbtCompound.toString()});
					}
				}
			}
		}
	}

	@Override
	public List<String> method_10738(MinecraftServer server, CommandSource source, String[] strings, @Nullable BlockPos pos) {
		return strings.length > 0 && strings.length <= 3 ? method_10707(strings, 0, pos) : Collections.emptyList();
	}
}
