package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.MessageArgumentType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class MessageCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		LiteralCommandNode<ServerCommandSource> literalCommandNode = commandDispatcher.register(
			(LiteralArgumentBuilder)CommandManager.literal("msg")
				.then(
					CommandManager.argument("targets", EntityArgumentType.players())
						.then(
							CommandManager.argument("message", MessageArgumentType.message())
								.executes(
									commandContext -> execute(
											(ServerCommandSource)commandContext.getSource(),
											EntityArgumentType.getPlayers(commandContext, "targets"),
											MessageArgumentType.getMessage(commandContext, "message")
										)
								)
						)
				)
		);
		commandDispatcher.register((LiteralArgumentBuilder)CommandManager.literal("tell").redirect(literalCommandNode));
		commandDispatcher.register((LiteralArgumentBuilder)CommandManager.literal("w").redirect(literalCommandNode));
	}

	private static int execute(ServerCommandSource serverCommandSource, Collection<ServerPlayerEntity> collection, Text text) {
		for (ServerPlayerEntity serverPlayerEntity : collection) {
			serverPlayerEntity.sendMessage(
				new TranslatableText("commands.message.display.incoming", serverCommandSource.getDisplayName(), text.deepCopy())
					.formatted(new Formatting[]{Formatting.field_1080, Formatting.field_1056})
			);
			serverCommandSource.sendFeedback(
				new TranslatableText("commands.message.display.outgoing", serverPlayerEntity.getDisplayName(), text.deepCopy())
					.formatted(new Formatting[]{Formatting.field_1080, Formatting.field_1056}),
				false
			);
		}

		return collection.size();
	}
}
