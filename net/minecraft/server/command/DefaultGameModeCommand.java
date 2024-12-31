package net.minecraft.server.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameMode;

public class DefaultGameModeCommand extends GameModeCommand {
	@Override
	public String getCommandName() {
		return "defaultgamemode";
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.defaultgamemode.usage";
	}

	@Override
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (args.length <= 0) {
			throw new IncorrectUsageException("commands.defaultgamemode.usage");
		} else {
			GameMode gameMode = this.method_3540(commandSource, args[0]);
			this.method_13244(gameMode, minecraftServer);
			run(commandSource, this, "commands.defaultgamemode.success", new Object[]{new TranslatableText("gameMode." + gameMode.getGameModeName())});
		}
	}

	protected void method_13244(GameMode gameMode, MinecraftServer minecraftServer) {
		minecraftServer.method_2999(gameMode);
		if (minecraftServer.shouldForceGameMode()) {
			for (ServerPlayerEntity serverPlayerEntity : minecraftServer.getPlayerManager().getPlayers()) {
				serverPlayerEntity.method_3170(gameMode);
			}
		}
	}
}
