package net.minecraft;

import com.google.gson.JsonParseException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class class_4009 implements ArgumentType<Text> {
	private static final Collection<String> field_19472 = Arrays.asList("\"hello world\"", "\"\"", "\"{\"text\":\"hello world\"}", "[\"\"]");
	public static final DynamicCommandExceptionType field_19471 = new DynamicCommandExceptionType(
		object -> new TranslatableText("argument.component.invalid", object)
	);

	private class_4009() {
	}

	public static Text method_17713(CommandContext<class_3915> commandContext, String string) {
		return (Text)commandContext.getArgument(string, Text.class);
	}

	public static class_4009 method_17711() {
		return new class_4009();
	}

	public Text parse(StringReader stringReader) throws CommandSyntaxException {
		try {
			Text text = Text.Serializer.method_20181(stringReader);
			if (text == null) {
				throw field_19471.createWithContext(stringReader, "empty");
			} else {
				return text;
			}
		} catch (JsonParseException var4) {
			String string = var4.getCause() != null ? var4.getCause().getMessage() : var4.getMessage();
			throw field_19471.createWithContext(stringReader, string);
		}
	}

	public Collection<String> getExamples() {
		return field_19472;
	}
}
