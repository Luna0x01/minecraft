package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.TranslatableText;

public class class_4429 {
	public static void method_21001(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("setidletimeout").requires(arg -> arg.method_17575(3)))
				.then(
					CommandManager.method_17530("minutes", IntegerArgumentType.integer(0))
						.executes(commandContext -> method_21000((class_3915)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "minutes")))
				)
		);
	}

	private static int method_21000(class_3915 arg, int i) {
		arg.method_17473().setPlayerIdleTimeout(i);
		arg.method_17459(new TranslatableText("commands.setidletimeout.success", i), true);
		return i;
	}
}
