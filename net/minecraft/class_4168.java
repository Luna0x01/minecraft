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
import net.minecraft.client.particle.ParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class class_4168 implements ArgumentType<ParticleEffect> {
	private static final Collection<String> field_20441 = Arrays.asList("foo", "foo:bar", "particle with options");
	public static final DynamicCommandExceptionType field_20440 = new DynamicCommandExceptionType(object -> new TranslatableText("particle.notFound", object));

	public static class_4168 method_18780() {
		return new class_4168();
	}

	public static ParticleEffect method_18783(CommandContext<class_3915> commandContext, String string) {
		return (ParticleEffect)commandContext.getArgument(string, ParticleEffect.class);
	}

	public ParticleEffect parse(StringReader stringReader) throws CommandSyntaxException {
		return method_18785(stringReader);
	}

	public Collection<String> getExamples() {
		return field_20441;
	}

	public static ParticleEffect method_18785(StringReader stringReader) throws CommandSyntaxException {
		Identifier identifier = Identifier.method_20442(stringReader);
		ParticleType<?> particleType = Registry.PARTICLE_TYPE.getByIdentifier(identifier);
		if (particleType == null) {
			throw field_20440.create(identifier);
		} else {
			return method_18782(stringReader, (ParticleType<ParticleEffect>)particleType);
		}
	}

	private static <T extends ParticleEffect> T method_18782(StringReader stringReader, ParticleType<T> particleType) throws CommandSyntaxException {
		return particleType.method_19987().method_19981(particleType, stringReader);
	}

	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		return class_3965.method_17559(Registry.PARTICLE_TYPE.getKeySet(), suggestionsBuilder);
	}
}
