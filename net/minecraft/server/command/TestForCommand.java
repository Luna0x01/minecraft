package net.minecraft.server.command;

import java.util.List;
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
	public void execute(CommandSource source, String[] args) throws CommandException {
		if (args.length < 1) {
			throw new IncorrectUsageException("commands.testfor.usage");
		} else {
			Entity entity = getEntity(source, args[0]);
			NbtCompound nbtCompound = null;
			if (args.length >= 2) {
				try {
					nbtCompound = StringNbtReader.parse(method_10706(args, 1));
				} catch (NbtException var6) {
					throw new CommandException("commands.testfor.tagError", var6.getMessage());
				}
			}

			if (nbtCompound != null) {
				NbtCompound nbtCompound2 = new NbtCompound();
				entity.writePlayerData(nbtCompound2);
				if (!NbtHelper.matches(nbtCompound, nbtCompound2, true)) {
					throw new CommandException("commands.testfor.failure", entity.getTranslationKey());
				}
			}

			run(source, this, "commands.testfor.success", new Object[]{entity.getTranslationKey()});
		}
	}

	@Override
	public boolean isUsernameAtIndex(String[] args, int index) {
		return index == 0;
	}

	@Override
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		return args.length == 1 ? method_2894(args, MinecraftServer.getServer().getPlayerNames()) : null;
	}
}
