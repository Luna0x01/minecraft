package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import net.minecraft.class_3915;
import net.minecraft.class_4062;
import net.minecraft.class_4102;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class MessageCommand {
	public static void method_20866(CommandDispatcher<class_3915> commandDispatcher) {
		LiteralCommandNode<class_3915> literalCommandNode = commandDispatcher.register(
			(LiteralArgumentBuilder)CommandManager.method_17529("msg")
				.then(
					CommandManager.method_17530("targets", class_4062.method_17904())
						.then(
							CommandManager.method_17530("message", class_4102.method_18091())
								.executes(
									commandContext -> method_20865(
											(class_3915)commandContext.getSource(), class_4062.method_17907(commandContext, "targets"), class_4102.method_18093(commandContext, "message")
										)
								)
						)
				)
		);
		commandDispatcher.register((LiteralArgumentBuilder)CommandManager.method_17529("tell").redirect(literalCommandNode));
		commandDispatcher.register((LiteralArgumentBuilder)CommandManager.method_17529("w").redirect(literalCommandNode));
	}

	private static int method_20865(class_3915 arg, Collection<ServerPlayerEntity> collection, Text text) {
		for (ServerPlayerEntity serverPlayerEntity : collection) {
			serverPlayerEntity.method_5505(
				new TranslatableText("commands.message.display.incoming", arg.method_17461(), text.method_20177())
					.formatted(new Formatting[]{Formatting.GRAY, Formatting.ITALIC})
			);
			arg.method_17459(
				new TranslatableText("commands.message.display.outgoing", serverPlayerEntity.getName(), text.method_20177())
					.formatted(new Formatting[]{Formatting.GRAY, Formatting.ITALIC}),
				false
			);
		}

		return collection.size();
	}
}
