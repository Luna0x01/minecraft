package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.Collection;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class KickCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("kick").requires(source -> source.hasPermissionLevel(3)))
				.then(
					((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.players())
							.executes(
								context -> execute(
										(ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers(context, "targets"), new TranslatableText("multiplayer.disconnect.kicked")
									)
							))
						.then(
							CommandManager.argument("reason", MessageArgumentType.message())
								.executes(
									context -> execute(
											(ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers(context, "targets"), MessageArgumentType.getMessage(context, "reason")
										)
								)
						)
				)
		);
	}

	private static int execute(ServerCommandSource source, Collection<ServerPlayerEntity> targets, Text reason) {
		for (ServerPlayerEntity serverPlayerEntity : targets) {
			serverPlayerEntity.networkHandler.disconnect(reason);
			source.sendFeedback(new TranslatableText("commands.kick.success", serverPlayerEntity.getDisplayName(), reason), true);
		}

		return targets.size();
	}
}
