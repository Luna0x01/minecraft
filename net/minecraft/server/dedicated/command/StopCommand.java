package net.minecraft.server.dedicated.command;

import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;

public class StopCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "stop";
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.stop.usage";
	}

	@Override
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (minecraftServer.worlds != null) {
			run(commandSource, this, "commands.stop.start", new Object[0]);
		}

		minecraftServer.stopRunning();
	}
}
