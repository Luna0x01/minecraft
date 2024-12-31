package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class class_4426 {
	public static void method_20944(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("say").requires(arg -> arg.method_17575(2)))
				.then(
					CommandManager.method_17530("message", class_4102.method_18091())
						.executes(
							commandContext -> {
								Text text = class_4102.method_18093(commandContext, "message");
								((class_3915)commandContext.getSource())
									.method_17473()
									.getPlayerManager()
									.sendToAll(new TranslatableText("chat.type.announcement", ((class_3915)commandContext.getSource()).method_17461(), text));
								return 1;
							}
						)
				)
		);
	}
}
