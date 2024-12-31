package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import net.minecraft.class_3915;
import net.minecraft.class_3965;
import net.minecraft.class_4308;
import net.minecraft.server.function.Function;
import net.minecraft.server.function.FunctionTickable;
import net.minecraft.text.TranslatableText;

public class FunctionCommand {
	public static final SuggestionProvider<class_3915> field_21758 = (commandContext, suggestionsBuilder) -> {
		FunctionTickable functionTickable = ((class_3915)commandContext.getSource()).method_17473().method_14911();
		class_3965.method_17560(functionTickable.method_20463().method_21483(), suggestionsBuilder, "#");
		return class_3965.method_17559(functionTickable.getFunctions().keySet(), suggestionsBuilder);
	};

	public static void method_20809(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("function").requires(arg -> arg.method_17575(2)))
				.then(
					CommandManager.method_17530("name", class_4308.method_19691())
						.suggests(field_21758)
						.executes(commandContext -> method_20808((class_3915)commandContext.getSource(), class_4308.method_19693(commandContext, "name")))
				)
		);
	}

	private static int method_20808(class_3915 arg, Collection<Function> collection) {
		int i = 0;

		for (Function function : collection) {
			i += arg.method_17473().method_14911().method_14944(function, arg.method_17448().method_17462(2));
		}

		if (collection.size() == 1) {
			arg.method_17459(new TranslatableText("commands.function.success.single", i, ((Function)collection.iterator().next()).method_17355()), true);
		} else {
			arg.method_17459(new TranslatableText("commands.function.success.multiple", i, collection.size()), true);
		}

		return i;
	}
}
