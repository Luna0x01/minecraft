package net.minecraft.server.dedicated.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.class_3915;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.TranslatableText;

public class StopCommand {
	public static void method_21031(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("stop").requires(arg -> arg.method_17575(4))).executes(commandContext -> {
				((class_3915)commandContext.getSource()).method_17459(new TranslatableText("commands.stop.stopping"), true);
				((class_3915)commandContext.getSource()).method_17473().stopRunning();
				return 1;
			})
		);
	}
}
