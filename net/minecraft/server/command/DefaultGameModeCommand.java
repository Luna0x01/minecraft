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
	public void execute(CommandSource source, String[] args) throws CommandException {
		if (args.length <= 0) {
			throw new IncorrectUsageException("commands.defaultgamemode.usage");
		} else {
			LevelInfo.GameMode gameMode = this.method_3540(source, args[0]);
			this.method_3461(gameMode);
			run(source, this, "commands.defaultgamemode.success", new Object[]{new TranslatableText("gameMode." + gameMode.getName())});
		}
	}

	protected void method_3461(LevelInfo.GameMode gameMode) {
		MinecraftServer minecraftServer = MinecraftServer.getServer();
		minecraftServer.setDefaultGameMode(gameMode);
		if (minecraftServer.shouldForceGameMode()) {
			for (ServerPlayerEntity serverPlayerEntity : MinecraftServer.getServer().getPlayerManager().getPlayers()) {
				serverPlayerEntity.setGameMode(gameMode);
				serverPlayerEntity.fallDistance = 0.0F;
			}
		}
	}
}
