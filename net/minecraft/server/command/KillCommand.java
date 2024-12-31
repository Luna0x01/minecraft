package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Collection;
import net.minecraft.class_3915;
import net.minecraft.class_4062;
import net.minecraft.entity.Entity;
import net.minecraft.text.TranslatableText;

public class KillCommand {
	public static void method_20838(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("kill").requires(arg -> arg.method_17575(2)))
				.then(
					CommandManager.method_17530("targets", class_4062.method_17899())
						.executes(commandContext -> method_20837((class_3915)commandContext.getSource(), class_4062.method_17901(commandContext, "targets")))
				)
		);
	}

	private static int method_20837(class_3915 arg, Collection<? extends Entity> collection) {
		for (Entity entity : collection) {
			entity.kill();
		}

		if (collection.size() == 1) {
			arg.method_17459(new TranslatableText("commands.kill.success.single", ((Entity)collection.iterator().next()).getName()), true);
		} else {
			arg.method_17459(new TranslatableText("commands.kill.success.multiple", collection.size()), true);
		}

		return collection.size();
	}
}
