package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.class_3915;
import net.minecraft.text.TranslatableText;

public class ReloadCommand {
	public static void method_20919(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("reload").requires(arg -> arg.method_17575(3))).executes(commandContext -> {
				((class_3915)commandContext.getSource()).method_17459(new TranslatableText("commands.reload.success"), true);
				((class_3915)commandContext.getSource()).method_17473().method_14912();
				return 0;
			})
		);
	}
}
