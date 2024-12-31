package net.minecraft;

import com.google.common.collect.Streams;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionType;

public class class_4030 implements ArgumentType<DimensionType> {
	private static final Collection<String> field_19493 = (Collection<String>)Stream.of(DimensionType.OVERWORLD, DimensionType.THE_NETHER)
		.map(dimensionType -> DimensionType.method_17196(dimensionType).toString())
		.collect(Collectors.toList());
	public static final DynamicCommandExceptionType field_19492 = new DynamicCommandExceptionType(
		object -> new TranslatableText("argument.dimension.invalid", object)
	);

	public <S> DimensionType parse(StringReader stringReader) throws CommandSyntaxException {
		Identifier identifier = Identifier.method_20442(stringReader);
		DimensionType dimensionType = DimensionType.method_17199(identifier);
		if (dimensionType == null) {
			throw field_19492.create(identifier);
		} else {
			return dimensionType;
		}
	}

	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		return class_3965.method_17566(Streams.stream(DimensionType.method_17200()).map(DimensionType::method_17196), suggestionsBuilder);
	}

	public Collection<String> getExamples() {
		return field_19493;
	}

	public static class_4030 method_17821() {
		return new class_4030();
	}

	public static DimensionType method_17824(CommandContext<class_3915> commandContext, String string) {
		return (DimensionType)commandContext.getArgument(string, DimensionType.class);
	}
}
