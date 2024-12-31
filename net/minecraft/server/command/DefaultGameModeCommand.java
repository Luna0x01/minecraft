package net.minecraft.server.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.level.LevelInfo;

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
			LevelInfo.GameMode gameMode = this.method_3540(commandSource, args[0]);
			this.method_13244(gameMode, minecraftServer);
			run(commandSource, this, "commands.defaultgamemode.success", new Object[]{new TranslatableText("gameMode." + gameMode.getName())});
		}
	}

	protected void method_13244(LevelInfo.GameMode gameMode, MinecraftServer minecraftServer) {
		minecraftServer.setDefaultGameMode(gameMode);
		if (minecraftServer.shouldForceGameMode()) {
			for (ServerPlayerEntity serverPlayerEntity : minecraftServer.getPlayerManager().getPlayers()) {
				serverPlayerEntity.setGameMode(gameMode);
			}
		}
	}
}
