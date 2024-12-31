package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.class_3915;
import net.minecraft.class_4062;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameMode;

public class GameModeCommand {
	public static void method_20815(CommandDispatcher<class_3915> commandDispatcher) {
		LiteralArgumentBuilder<class_3915> literalArgumentBuilder = (LiteralArgumentBuilder<class_3915>)CommandManager.method_17529("gamemode")
			.requires(arg -> arg.method_17575(2));

		for (GameMode gameMode : GameMode.gameModes()) {
			if (gameMode != GameMode.NOT_SET) {
				literalArgumentBuilder.then(
					((LiteralArgumentBuilder)CommandManager.method_17529(gameMode.getGameModeName())
							.executes(commandContext -> method_20816(commandContext, Collections.singleton(((class_3915)commandContext.getSource()).method_17471()), gameMode)))
						.then(
							CommandManager.method_17530("target", class_4062.method_17904())
								.executes(commandContext -> method_20816(commandContext, class_4062.method_17907(commandContext, "target"), gameMode))
						)
				);
			}
		}

		commandDispatcher.register(literalArgumentBuilder);
	}

	private static void method_20814(class_3915 arg, ServerPlayerEntity serverPlayerEntity, GameMode gameMode) {
		Text text = new TranslatableText("gameMode." + gameMode.getGameModeName());
		if (arg.method_17469() == serverPlayerEntity) {
			arg.method_17459(new TranslatableText("commands.gamemode.success.self", text), true);
		} else {
			if (arg.method_17468().getGameRules().getBoolean("sendCommandFeedback")) {
				serverPlayerEntity.method_5505(new TranslatableText("gameMode.changed", text));
			}

			arg.method_17459(new TranslatableText("commands.gamemode.success.other", serverPlayerEntity.getName(), text), true);
		}
	}

	private static int method_20816(CommandContext<class_3915> commandContext, Collection<ServerPlayerEntity> collection, GameMode gameMode) {
		int i = 0;

		for (ServerPlayerEntity serverPlayerEntity : collection) {
			if (serverPlayerEntity.interactionManager.getGameMode() != gameMode) {
				serverPlayerEntity.method_3170(gameMode);
				method_20814((class_3915)commandContext.getSource(), serverPlayerEntity, gameMode);
				i++;
			}
		}

		return i;
	}
}
