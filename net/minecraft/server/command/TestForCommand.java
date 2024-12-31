package net.minecraft.server.command;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class TestForCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "testfor";
	}

	@Override
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.testfor.usage";
	}

	@Override
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (args.length < 1) {
			throw new IncorrectUsageException("commands.testfor.usage");
		} else {
			Entity entity = method_10711(minecraftServer, commandSource, args[0]);
			NbtCompound nbtCompound = null;
			if (args.length >= 2) {
				try {
					nbtCompound = StringNbtReader.parse(method_10706(args, 1));
				} catch (NbtException var7) {
					throw new CommandException("commands.testfor.tagError", var7.getMessage());
				}
			}

			if (nbtCompound != null) {
				NbtCompound nbtCompound2 = getEntityNbt(entity);
				if (!NbtHelper.matches(nbtCompound, nbtCompound2, true)) {
					throw new CommandException("commands.testfor.failure", entity.getTranslationKey());
				}
			}

			run(commandSource, this, "commands.testfor.success", new Object[]{entity.getTranslationKey()});
		}
	}

	@Override
	public boolean isUsernameAtIndex(String[] args, int index) {
		return index == 0;
	}

	@Override
	public List<String> method_10738(MinecraftServer server, CommandSource source, String[] strings, @Nullable BlockPos pos) {
		return strings.length == 1 ? method_2894(strings, server.getPlayerNames()) : Collections.emptyList();
	}
}
