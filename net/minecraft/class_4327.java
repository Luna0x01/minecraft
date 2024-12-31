package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.entity.EntityType;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

public class class_4327 {
	private static final Map<Identifier, SuggestionProvider<class_3965>> field_21258 = Maps.newHashMap();
	private static final Identifier field_21259 = new Identifier("minecraft:ask_server");
	public static final SuggestionProvider<class_3965> field_21254 = method_19904(
		field_21259, (commandContext, suggestionsBuilder) -> ((class_3965)commandContext.getSource()).method_17555(commandContext, suggestionsBuilder)
	);
	public static final SuggestionProvider<class_3915> field_21255 = method_19904(
		new Identifier("minecraft:all_recipes"),
		(commandContext, suggestionsBuilder) -> class_3965.method_17559(((class_3965)commandContext.getSource()).method_17579(), suggestionsBuilder)
	);
	public static final SuggestionProvider<class_3915> field_21256 = method_19904(
		new Identifier("minecraft:available_sounds"),
		(commandContext, suggestionsBuilder) -> class_3965.method_17559(((class_3965)commandContext.getSource()).method_17578(), suggestionsBuilder)
	);
	public static final SuggestionProvider<class_3915> field_21257 = method_19904(
		new Identifier("minecraft:summonable_entities"),
		(commandContext, suggestionsBuilder) -> class_3965.method_17567(
				Registry.ENTITY_TYPE.stream().filter(EntityType::method_15626),
				suggestionsBuilder,
				EntityType::getId,
				entityType -> new TranslatableText(Util.createTranslationKey("entity", EntityType.getId(entityType)))
			)
	);

	public static <S extends class_3965> SuggestionProvider<S> method_19904(Identifier identifier, SuggestionProvider<class_3965> suggestionProvider) {
		if (field_21258.containsKey(identifier)) {
			throw new IllegalArgumentException("A command suggestion provider is already registered with the name " + identifier);
		} else {
			field_21258.put(identifier, suggestionProvider);
			return new class_4327.class_4328(identifier, suggestionProvider);
		}
	}

	public static SuggestionProvider<class_3965> method_19903(Identifier identifier) {
		return (SuggestionProvider<class_3965>)field_21258.getOrDefault(identifier, field_21254);
	}

	public static Identifier method_19902(SuggestionProvider<class_3965> suggestionProvider) {
		return suggestionProvider instanceof class_4327.class_4328 ? ((class_4327.class_4328)suggestionProvider).field_21261 : field_21259;
	}

	public static SuggestionProvider<class_3965> method_19906(SuggestionProvider<class_3965> suggestionProvider) {
		return suggestionProvider instanceof class_4327.class_4328 ? suggestionProvider : field_21254;
	}

	public static class class_4328 implements SuggestionProvider<class_3965> {
		private final SuggestionProvider<class_3965> field_21260;
		private final Identifier field_21261;

		public class_4328(Identifier identifier, SuggestionProvider<class_3965> suggestionProvider) {
			this.field_21260 = suggestionProvider;
			this.field_21261 = identifier;
		}

		public CompletableFuture<Suggestions> getSuggestions(CommandContext<class_3965> commandContext, SuggestionsBuilder suggestionsBuilder) throws CommandSyntaxException {
			return this.field_21260.getSuggestions(commandContext, suggestionsBuilder);
		}
	}
}
