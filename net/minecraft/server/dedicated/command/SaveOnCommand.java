package net.minecraft.server.dedicated.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.class_3915;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;

public class SaveOnCommand {
	private static final SimpleCommandExceptionType field_21776 = new SimpleCommandExceptionType(new TranslatableText("commands.save.alreadyOn"));

	public static void method_20941(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("save-on").requires(arg -> arg.method_17575(4))).executes(commandContext -> {
				class_3915 lv = (class_3915)commandContext.getSource();
				boolean bl = false;

				for (ServerWorld serverWorld : lv.method_17473().method_20351()) {
					if (serverWorld != null && serverWorld.savingDisabled) {
						serverWorld.savingDisabled = false;
						bl = true;
					}
				}

				if (!bl) {
					throw field_21776.create();
				} else {
					lv.method_17459(new TranslatableText("commands.save.enabled"), true);
					return 1;
				}
			})
		);
	}
}
