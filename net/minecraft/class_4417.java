package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.TranslatableText;

public class class_4417 {
	public static void method_20674(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)CommandManager.method_17529("me")
				.then(
					CommandManager.method_17530("action", StringArgumentType.greedyString())
						.executes(
							commandContext -> {
								((class_3915)commandContext.getSource())
									.method_17473()
									.getPlayerManager()
									.sendToAll(
										new TranslatableText(
											"chat.type.emote", ((class_3915)commandContext.getSource()).method_17461(), StringArgumentType.getString(commandContext, "action")
										)
									);
								return 1;
							}
						)
				)
		);
	}
}
