package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Map.Entry;
import net.minecraft.class_3915;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameRuleManager;

public class GameRuleCommand {
	public static void method_20821(CommandDispatcher<class_3915> commandDispatcher) {
		LiteralArgumentBuilder<class_3915> literalArgumentBuilder = (LiteralArgumentBuilder<class_3915>)CommandManager.method_17529("gamerule")
			.requires(arg -> arg.method_17575(2));

		for (Entry<String, GameRuleManager.class_3596> entry : GameRuleManager.method_16300().entrySet()) {
			literalArgumentBuilder.then(
				((LiteralArgumentBuilder)CommandManager.method_17529((String)entry.getKey())
						.executes(commandContext -> method_20819((class_3915)commandContext.getSource(), (String)entry.getKey())))
					.then(
						((GameRuleManager.class_3596)entry.getValue())
							.method_16305()
							.method_16308("value")
							.executes(commandContext -> method_20820((class_3915)commandContext.getSource(), (String)entry.getKey(), commandContext))
					)
			);
		}

		commandDispatcher.register(literalArgumentBuilder);
	}

	private static int method_20820(class_3915 arg, String string, CommandContext<class_3915> commandContext) {
		GameRuleManager.Value value = arg.method_17473().method_20335().method_16301(string);
		value.getVariableType().method_16307(commandContext, "value", value);
		arg.method_17459(new TranslatableText("commands.gamerule.set", string, value.getStringDefaultValue()), true);
		return value.getIntDefaultValue();
	}

	private static int method_20819(class_3915 arg, String string) {
		GameRuleManager.Value value = arg.method_17473().method_20335().method_16301(string);
		arg.method_17459(new TranslatableText("commands.gamerule.query", string, value.getStringDefaultValue()), false);
		return value.getIntDefaultValue();
	}
}
