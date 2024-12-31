package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.Collection;
import net.minecraft.class_3915;
import net.minecraft.class_4062;
import net.minecraft.class_4102;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class KickCommand {
	public static void method_20833(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("kick").requires(arg -> arg.method_17575(3)))
				.then(
					((RequiredArgumentBuilder)CommandManager.method_17530("targets", class_4062.method_17904())
							.executes(
								commandContext -> method_3279(
										(class_3915)commandContext.getSource(), class_4062.method_17907(commandContext, "targets"), new TranslatableText("multiplayer.disconnect.kicked")
									)
							))
						.then(
							CommandManager.method_17530("reason", class_4102.method_18091())
								.executes(
									commandContext -> method_3279(
											(class_3915)commandContext.getSource(), class_4062.method_17907(commandContext, "targets"), class_4102.method_18093(commandContext, "reason")
										)
								)
						)
				)
		);
	}

	private static int method_3279(class_3915 arg, Collection<ServerPlayerEntity> collection, Text text) {
		for (ServerPlayerEntity serverPlayerEntity : collection) {
			serverPlayerEntity.networkHandler.method_14977(text);
			arg.method_17459(new TranslatableText("commands.kick.success", serverPlayerEntity.getName(), text), true);
		}

		return collection.size();
	}
}
