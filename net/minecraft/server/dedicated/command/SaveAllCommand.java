package net.minecraft.server.dedicated.command;

import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.level.storage.WorldSaveException;

public class SaveAllCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "save-all";
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.save.usage";
	}

	@Override
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		commandSource.sendMessage(new TranslatableText("commands.save.start"));
		if (minecraftServer.getPlayerManager() != null) {
			minecraftServer.getPlayerManager().saveAllPlayerData();
		}

		try {
			for (int i = 0; i < minecraftServer.worlds.length; i++) {
				if (minecraftServer.worlds[i] != null) {
					ServerWorld serverWorld = minecraftServer.worlds[i];
					boolean bl = serverWorld.savingDisabled;
					serverWorld.savingDisabled = false;
					serverWorld.save(true, null);
					serverWorld.savingDisabled = bl;
				}
			}

			if (args.length > 0 && "flush".equals(args[0])) {
				commandSource.sendMessage(new TranslatableText("commands.save.flushStart"));

				for (int j = 0; j < minecraftServer.worlds.length; j++) {
					if (minecraftServer.worlds[j] != null) {
						ServerWorld serverWorld2 = minecraftServer.worlds[j];
						boolean bl2 = serverWorld2.savingDisabled;
						serverWorld2.savingDisabled = false;
						serverWorld2.method_5323();
						serverWorld2.savingDisabled = bl2;
					}
				}

				commandSource.sendMessage(new TranslatableText("commands.save.flushEnd"));
			}
		} catch (WorldSaveException var7) {
			run(commandSource, this, "commands.save.failed", new Object[]{var7.getMessage()});
			return;
		}

		run(commandSource, this, "commands.save.success", new Object[0]);
	}
}
