package net.minecraft.server.command;

import com.google.common.collect.Iterables;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import java.util.Map;
import net.minecraft.class_3915;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public class HelpCommand {
	private static final SimpleCommandExceptionType field_21759 = new SimpleCommandExceptionType(new TranslatableText("commands.help.failed"));

	public static void method_20829(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("help").executes(commandContext -> {
					Map<CommandNode<class_3915>, String> map = commandDispatcher.getSmartUsage(commandDispatcher.getRoot(), commandContext.getSource());

					for (String string : map.values()) {
						((class_3915)commandContext.getSource()).method_17459(new LiteralText("/" + string), false);
					}

					return map.size();
				}))
				.then(
					CommandManager.method_17530("command", StringArgumentType.greedyString())
						.executes(
							commandContext -> {
								ParseResults<class_3915> parseResults = commandDispatcher.parse(StringArgumentType.getString(commandContext, "command"), commandContext.getSource());
								if (parseResults.getContext().getNodes().isEmpty()) {
									throw field_21759.create();
								} else {
									Map<CommandNode<class_3915>, String> map = commandDispatcher.getSmartUsage(
										(CommandNode)Iterables.getLast(parseResults.getContext().getNodes().keySet()), commandContext.getSource()
									);

									for (String string : map.values()) {
										((class_3915)commandContext.getSource()).method_17459(new LiteralText("/" + parseResults.getReader().getString() + " " + string), false);
									}

									return map.size();
								}
							}
						)
				)
		);
	}
}
