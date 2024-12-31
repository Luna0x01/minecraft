package net.minecraft.server.dedicated.command;

import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

public class SaveOffCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "save-off";
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.save-off.usage";
	}

	@Override
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		boolean bl = false;

		for (int i = 0; i < minecraftServer.worlds.length; i++) {
			if (minecraftServer.worlds[i] != null) {
				ServerWorld serverWorld = minecraftServer.worlds[i];
				if (!serverWorld.savingDisabled) {
					serverWorld.savingDisabled = true;
					bl = true;
				}
			}
		}

		if (bl) {
			run(commandSource, this, "commands.save.disabled", new Object[0]);
		} else {
			throw new CommandException("commands.save-off.alreadyOff");
		}
	}
}
