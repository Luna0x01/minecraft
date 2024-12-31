package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class class_4252 implements ArgumentType<class_4261> {
	private static final Collection<String> field_20913 = Arrays.asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "~0.5 ~1 ~-5");
	public static final SimpleCommandExceptionType field_20911 = new SimpleCommandExceptionType(new TranslatableText("argument.pos.unloaded"));
	public static final SimpleCommandExceptionType field_20912 = new SimpleCommandExceptionType(new TranslatableText("argument.pos.outofworld"));

	public static class_4252 method_19358() {
		return new class_4252();
	}

	public static BlockPos method_19360(CommandContext<class_3915> commandContext, String string) throws CommandSyntaxException {
		BlockPos blockPos = ((class_4261)commandContext.getArgument(string, class_4261.class)).method_19415((class_3915)commandContext.getSource());
		if (!((class_3915)commandContext.getSource()).method_17468().method_16359(blockPos)) {
			throw field_20911.create();
		} else {
			((class_3915)commandContext.getSource()).method_17468();
			if (!ServerWorld.method_11479(blockPos)) {
				throw field_20912.create();
			} else {
				return blockPos;
			}
		}
	}

	public static BlockPos method_19361(CommandContext<class_3915> commandContext, String string) throws CommandSyntaxException {
		return ((class_4261)commandContext.getArgument(string, class_4261.class)).method_19415((class_3915)commandContext.getSource());
	}

	public class_4261 parse(StringReader stringReader) throws CommandSyntaxException {
		return (class_4261)(stringReader.canRead() && stringReader.peek() == '^' ? class_4267.method_19428(stringReader) : class_4304.method_19636(stringReader));
	}

	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		if (!(commandContext.getSource() instanceof class_3965)) {
			return Suggestions.empty();
		} else {
			String string = suggestionsBuilder.getRemaining();
			Collection<class_3965.class_3966> collection;
			if (!string.isEmpty() && string.charAt(0) == '^') {
				collection = Collections.singleton(class_3965.class_3966.field_19334);
			} else {
				collection = ((class_3965)commandContext.getSource()).method_17569(false);
			}

			return class_3965.method_17565(string, collection, suggestionsBuilder, CommandManager.method_17520(this::parse));
		}
	}

	public Collection<String> getExamples() {
		return field_20913;
	}
}
