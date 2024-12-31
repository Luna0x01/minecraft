package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameMode;

public class class_4415 {
	public static void method_20656(CommandDispatcher<class_3915> commandDispatcher) {
		LiteralArgumentBuilder<class_3915> literalArgumentBuilder = (LiteralArgumentBuilder<class_3915>)CommandManager.method_17529("defaultgamemode")
			.requires(arg -> arg.method_17575(2));

		for (GameMode gameMode : GameMode.gameModes()) {
			if (gameMode != GameMode.NOT_SET) {
				literalArgumentBuilder.then(
					CommandManager.method_17529(gameMode.getGameModeName()).executes(commandContext -> method_20655((class_3915)commandContext.getSource(), gameMode))
				);
			}
		}

		commandDispatcher.register(literalArgumentBuilder);
	}

	private static int method_20655(class_3915 arg, GameMode gameMode) {
		int i = 0;
		MinecraftServer minecraftServer = arg.method_17473();
		minecraftServer.method_2999(gameMode);
		if (minecraftServer.shouldForceGameMode()) {
			for (ServerPlayerEntity serverPlayerEntity : minecraftServer.getPlayerManager().getPlayers()) {
				if (serverPlayerEntity.interactionManager.getGameMode() != gameMode) {
					serverPlayerEntity.method_3170(gameMode);
					i++;
				}
			}
		}

		arg.method_17459(new TranslatableText("commands.defaultgamemode.success", gameMode.method_16311()), true);
		return i;
	}
}
