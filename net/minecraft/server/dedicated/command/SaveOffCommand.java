package net.minecraft.server.dedicated.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.class_3915;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;

public class SaveOffCommand {
	private static final SimpleCommandExceptionType field_21775 = new SimpleCommandExceptionType(new TranslatableText("commands.save.alreadyOff"));

	public static void method_20938(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("save-off").requires(arg -> arg.method_17575(4))).executes(commandContext -> {
				class_3915 lv = (class_3915)commandContext.getSource();
				boolean bl = false;

				for (ServerWorld serverWorld : lv.method_17473().method_20351()) {
					if (serverWorld != null && !serverWorld.savingDisabled) {
						serverWorld.savingDisabled = true;
						bl = true;
					}
				}

				if (!bl) {
					throw field_21775.create();
				} else {
					lv.method_17459(new TranslatableText("commands.save.disabled"), true);
					return 1;
				}
			})
		);
	}
}
