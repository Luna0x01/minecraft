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
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Vec3d;

public class class_4287 implements ArgumentType<class_4261> {
	private static final Collection<String> field_21053 = Arrays.asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "0.1 -0.5 .9", "~0.5 ~1 ~-5");
	public static final SimpleCommandExceptionType field_21051 = new SimpleCommandExceptionType(new TranslatableText("argument.pos3d.incomplete"));
	public static final SimpleCommandExceptionType field_21052 = new SimpleCommandExceptionType(new TranslatableText("argument.pos.mixed"));
	private final boolean field_21054;

	public class_4287(boolean bl) {
		this.field_21054 = bl;
	}

	public static class_4287 method_19562() {
		return new class_4287(true);
	}

	public static class_4287 method_19565(boolean bl) {
		return new class_4287(bl);
	}

	public static Vec3d method_19564(CommandContext<class_3915> commandContext, String string) throws CommandSyntaxException {
		return ((class_4261)commandContext.getArgument(string, class_4261.class)).method_19411((class_3915)commandContext.getSource());
	}

	public static class_4261 method_19566(CommandContext<class_3915> commandContext, String string) {
		return (class_4261)commandContext.getArgument(string, class_4261.class);
	}

	public class_4261 parse(StringReader stringReader) throws CommandSyntaxException {
		return (class_4261)(stringReader.canRead() && stringReader.peek() == '^'
			? class_4267.method_19428(stringReader)
			: class_4304.method_19637(stringReader, this.field_21054));
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
				collection = ((class_3965)commandContext.getSource()).method_17569(true);
			}

			return class_3965.method_17565(string, collection, suggestionsBuilder, CommandManager.method_17520(this::parse));
		}
	}

	public Collection<String> getExamples() {
		return field_21053;
	}
}
