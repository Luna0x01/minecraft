package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.class_3915;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.text.TranslatableText;

public class PublishCommand {
	private static final SimpleCommandExceptionType field_21767 = new SimpleCommandExceptionType(new TranslatableText("commands.publish.failed"));
	private static final DynamicCommandExceptionType field_21768 = new DynamicCommandExceptionType(
		object -> new TranslatableText("commands.publish.alreadyPublished", object)
	);

	public static void method_20906(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("publish")
						.requires(arg -> arg.method_17473().isSinglePlayer() && arg.method_17575(4)))
					.executes(commandContext -> method_20905((class_3915)commandContext.getSource(), NetworkUtils.getFreePort())))
				.then(
					CommandManager.method_17530("port", IntegerArgumentType.integer(0, 65535))
						.executes(commandContext -> method_20905((class_3915)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "port")))
				)
		);
	}

	private static int method_20905(class_3915 arg, int i) throws CommandSyntaxException {
		if (arg.method_17473().shouldBroadcastConsoleToIps()) {
			throw field_21768.create(arg.method_17473().getServerPort());
		} else if (!arg.method_17473().method_20311(arg.method_17473().method_3026(), false, i)) {
			throw field_21767.create();
		} else {
			arg.method_17459(new TranslatableText("commands.publish.success", i), true);
			return i;
		}
	}
}
