package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

public class MessageCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralCommandNode<ServerCommandSource> literalCommandNode = dispatcher.register(
			(LiteralArgumentBuilder)CommandManager.literal("msg")
				.then(
					CommandManager.argument("targets", EntityArgumentType.players())
						.then(
							CommandManager.argument("message", MessageArgumentType.message())
								.executes(
									context -> execute(
											(ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers(context, "targets"), MessageArgumentType.getMessage(context, "message")
										)
								)
						)
				)
		);
		dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("tell").redirect(literalCommandNode));
		dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("w").redirect(literalCommandNode));
	}

	private static int execute(ServerCommandSource source, Collection<ServerPlayerEntity> targets, Text message) {
		UUID uUID = source.getEntity() == null ? Util.NIL_UUID : source.getEntity().getUuid();
		Consumer<Text> consumer;
		if (source.getEntity() instanceof ServerPlayerEntity serverPlayerEntity) {
			consumer = playerName -> serverPlayerEntity.sendSystemMessage(
					new TranslatableText("commands.message.display.outgoing", playerName, message).formatted(new Formatting[]{Formatting.GRAY, Formatting.ITALIC}),
					serverPlayerEntity.getUuid()
				);
		} else {
			consumer = playerName -> source.sendFeedback(
					new TranslatableText("commands.message.display.outgoing", playerName, message).formatted(new Formatting[]{Formatting.GRAY, Formatting.ITALIC}), false
				);
		}

		for (ServerPlayerEntity serverPlayerEntity2 : targets) {
			consumer.accept(serverPlayerEntity2.getDisplayName());
			serverPlayerEntity2.sendSystemMessage(
				new TranslatableText("commands.message.display.incoming", source.getDisplayName(), message).formatted(new Formatting[]{Formatting.GRAY, Formatting.ITALIC}),
				uUID
			);
		}

		return targets.size();
	}
}
