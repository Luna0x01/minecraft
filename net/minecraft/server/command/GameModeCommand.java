package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;

public class GameModeCommand {
	public static final int field_33393 = 2;

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = (LiteralArgumentBuilder<ServerCommandSource>)CommandManager.literal("gamemode")
			.requires(source -> source.hasPermissionLevel(2));

		for (GameMode gameMode : GameMode.values()) {
			literalArgumentBuilder.then(
				((LiteralArgumentBuilder)CommandManager.literal(gameMode.getName())
						.executes(context -> execute(context, Collections.singleton(((ServerCommandSource)context.getSource()).getPlayer()), gameMode)))
					.then(
						CommandManager.argument("target", EntityArgumentType.players())
							.executes(context -> execute(context, EntityArgumentType.getPlayers(context, "target"), gameMode))
					)
			);
		}

		dispatcher.register(literalArgumentBuilder);
	}

	private static void sendFeedback(ServerCommandSource source, ServerPlayerEntity player, GameMode gameMode) {
		Text text = new TranslatableText("gameMode." + gameMode.getName());
		if (source.getEntity() == player) {
			source.sendFeedback(new TranslatableText("commands.gamemode.success.self", text), true);
		} else {
			if (source.getWorld().getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK)) {
				player.sendSystemMessage(new TranslatableText("gameMode.changed", text), Util.NIL_UUID);
			}

			source.sendFeedback(new TranslatableText("commands.gamemode.success.other", player.getDisplayName(), text), true);
		}
	}

	private static int execute(CommandContext<ServerCommandSource> context, Collection<ServerPlayerEntity> targets, GameMode gameMode) {
		int i = 0;

		for (ServerPlayerEntity serverPlayerEntity : targets) {
			if (serverPlayerEntity.changeGameMode(gameMode)) {
				sendFeedback((ServerCommandSource)context.getSource(), serverPlayerEntity, gameMode);
				i++;
			}
		}

		return i;
	}
}
