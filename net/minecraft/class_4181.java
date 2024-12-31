package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.advancement.SimpleAdvancement;
import net.minecraft.recipe.RecipeType;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class class_4181 implements ArgumentType<Identifier> {
	private static final Collection<String> field_20523 = Arrays.asList("foo", "foo:bar", "012");
	public static final DynamicCommandExceptionType field_20520 = new DynamicCommandExceptionType(object -> new TranslatableText("argument.id.unknown", object));
	public static final DynamicCommandExceptionType field_20521 = new DynamicCommandExceptionType(
		object -> new TranslatableText("advancement.advancementNotFound", object)
	);
	public static final DynamicCommandExceptionType field_20522 = new DynamicCommandExceptionType(object -> new TranslatableText("recipe.notFound", object));

	public static class_4181 method_18904() {
		return new class_4181();
	}

	public static SimpleAdvancement method_18906(CommandContext<class_3915> commandContext, String string) throws CommandSyntaxException {
		Identifier identifier = (Identifier)commandContext.getArgument(string, Identifier.class);
		SimpleAdvancement simpleAdvancement = ((class_3915)commandContext.getSource()).method_17473().method_14910().method_14938(identifier);
		if (simpleAdvancement == null) {
			throw field_20521.create(identifier);
		} else {
			return simpleAdvancement;
		}
	}

	public static RecipeType method_18908(CommandContext<class_3915> commandContext, String string) throws CommandSyntaxException {
		Identifier identifier = (Identifier)commandContext.getArgument(string, Identifier.class);
		RecipeType recipeType = ((class_3915)commandContext.getSource()).method_17473().method_20331().method_16207(identifier);
		if (recipeType == null) {
			throw field_20522.create(identifier);
		} else {
			return recipeType;
		}
	}

	public static Identifier method_18910(CommandContext<class_3915> commandContext, String string) {
		return (Identifier)commandContext.getArgument(string, Identifier.class);
	}

	public Identifier parse(StringReader stringReader) throws CommandSyntaxException {
		return Identifier.method_20442(stringReader);
	}

	public Collection<String> getExamples() {
		return field_20523;
	}
}
