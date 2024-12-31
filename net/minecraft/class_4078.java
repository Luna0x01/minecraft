package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class class_4078 implements ArgumentType<Enchantment> {
	private static final Collection<String> field_19811 = Arrays.asList("unbreaking", "silk_touch");
	public static final DynamicCommandExceptionType field_19810 = new DynamicCommandExceptionType(object -> new TranslatableText("enchantment.unknown", object));

	public static class_4078 method_17997() {
		return new class_4078();
	}

	public static Enchantment method_17999(CommandContext<class_3915> commandContext, String string) {
		return (Enchantment)commandContext.getArgument(string, Enchantment.class);
	}

	public Enchantment parse(StringReader stringReader) throws CommandSyntaxException {
		Identifier identifier = Identifier.method_20442(stringReader);
		Enchantment enchantment = Registry.ENCHANTMENT.getByIdentifier(identifier);
		if (enchantment == null) {
			throw field_19810.create(identifier);
		} else {
			return enchantment;
		}
	}

	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		return class_3965.method_17559(Registry.ENCHANTMENT.getKeySet(), suggestionsBuilder);
	}

	public Collection<String> getExamples() {
		return field_19811;
	}
}
