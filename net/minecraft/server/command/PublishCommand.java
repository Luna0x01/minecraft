package net.minecraft.server.command;

import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.LevelInfo;

public class PublishCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "publish";
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.publish.usage";
	}

	@Override
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		String string = minecraftServer.getPort(LevelInfo.GameMode.SURVIVAL, false);
		if (string != null) {
			run(commandSource, this, "commands.publish.started", new Object[]{string});
		} else {
			run(commandSource, this, "commands.publish.failed", new Object[0]);
		}
	}
}
