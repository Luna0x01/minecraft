package net.minecraft.server.command;

import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.server.MinecraftServer;

public class ReloadCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "reload";
	}

	@Override
	public int getPermissionLevel() {
		return 3;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.reload.usage";
	}

	@Override
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (args.length > 0) {
			throw new IncorrectUsageException("commands.reload.usage");
		} else {
			minecraftServer.method_14912();
			run(commandSource, this, "commands.reload.success", new Object[0]);
		}
	}
}
