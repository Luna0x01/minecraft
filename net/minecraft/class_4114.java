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
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class class_4114 implements ArgumentType<StatusEffect> {
	private static final Collection<String> field_20012 = Arrays.asList("spooky", "effect");
	public static final DynamicCommandExceptionType field_20011 = new DynamicCommandExceptionType(object -> new TranslatableText("effect.effectNotFound", object));

	public static class_4114 method_18275() {
		return new class_4114();
	}

	public static StatusEffect method_18277(CommandContext<class_3915> commandContext, String string) throws CommandSyntaxException {
		return (StatusEffect)commandContext.getArgument(string, StatusEffect.class);
	}

	public StatusEffect parse(StringReader stringReader) throws CommandSyntaxException {
		Identifier identifier = Identifier.method_20442(stringReader);
		StatusEffect statusEffect = Registry.MOB_EFFECT.getByIdentifier(identifier);
		if (statusEffect == null) {
			throw field_20011.create(identifier);
		} else {
			return statusEffect;
		}
	}

	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		return class_3965.method_17559(Registry.MOB_EFFECT.getKeySet(), suggestionsBuilder);
	}

	public Collection<String> getExamples() {
		return field_20012;
	}
}
