package net.minecraft.server.dedicated.command;

import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.server.MinecraftServer;

public class SetIdleTimeoutCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "setidletimeout";
	}

	@Override
	public int getPermissionLevel() {
		return 3;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.setidletimeout.usage";
	}

	@Override
	public void execute(CommandSource source, String[] args) throws CommandException {
		if (args.length != 1) {
			throw new IncorrectUsageException("commands.setidletimeout.usage");
		} else {
			int i = parseClampedInt(args[0], 0);
			MinecraftServer.getServer().setPlayerIdleTimeout(i);
			run(source, this, "commands.setidletimeout.success", new Object[]{i});
		}
	}
}
