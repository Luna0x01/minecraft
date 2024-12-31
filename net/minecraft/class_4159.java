package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.scoreboard.GenericScoreboardCriteria;
import net.minecraft.stat.StatType;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.Registry;

public class class_4159 implements ArgumentType<GenericScoreboardCriteria> {
	private static final Collection<String> field_20304 = Arrays.asList("foo", "foo.bar.baz", "minecraft:foo");
	public static final DynamicCommandExceptionType field_20303 = new DynamicCommandExceptionType(
		object -> new TranslatableText("argument.criteria.invalid", object)
	);

	private class_4159() {
	}

	public static class_4159 method_18598() {
		return new class_4159();
	}

	public static GenericScoreboardCriteria method_18600(CommandContext<class_3915> commandContext, String string) {
		return (GenericScoreboardCriteria)commandContext.getArgument(string, GenericScoreboardCriteria.class);
	}

	public GenericScoreboardCriteria parse(StringReader stringReader) throws CommandSyntaxException {
		int i = stringReader.getCursor();

		while (stringReader.canRead() && stringReader.peek() != ' ') {
			stringReader.skip();
		}

		String string = stringReader.getString().substring(i, stringReader.getCursor());
		GenericScoreboardCriteria genericScoreboardCriteria = GenericScoreboardCriteria.method_18129(string);
		if (genericScoreboardCriteria == null) {
			stringReader.setCursor(i);
			throw field_20303.create(string);
		} else {
			return genericScoreboardCriteria;
		}
	}

	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		List<String> list = Lists.newArrayList(GenericScoreboardCriteria.field_19879.keySet());

		for (StatType<?> statType : Registry.STATS) {
			for (Object object : statType.method_21424()) {
				String string = this.method_18602(statType, object);
				list.add(string);
			}
		}

		return class_3965.method_17571(list, suggestionsBuilder);
	}

	public <T> String method_18602(StatType<T> statType, Object object) {
		return class_4472.method_21422(statType, (T)object);
	}

	public Collection<String> getExamples() {
		return field_20304;
	}
}
