package net.minecraft.command.argument;

import com.google.gson.JsonParseException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class TextArgumentType implements ArgumentType<Text> {
	private static final Collection<String> EXAMPLES = Arrays.asList("\"hello world\"", "\"\"", "\"{\"text\":\"hello world\"}", "[\"\"]");
	public static final DynamicCommandExceptionType INVALID_COMPONENT_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("argument.component.invalid", object)
	);

	private TextArgumentType() {
	}

	public static Text getTextArgument(CommandContext<ServerCommandSource> context, String name) {
		return (Text)context.getArgument(name, Text.class);
	}

	public static TextArgumentType text() {
		return new TextArgumentType();
	}

	public Text parse(StringReader stringReader) throws CommandSyntaxException {
		try {
			Text text = Text.Serializer.fromJson(stringReader);
			if (text == null) {
				throw INVALID_COMPONENT_EXCEPTION.createWithContext(stringReader, "empty");
			} else {
				return text;
			}
		} catch (JsonParseException var4) {
			String string = var4.getCause() != null ? var4.getCause().getMessage() : var4.getMessage();
			throw INVALID_COMPONENT_EXCEPTION.createWithContext(stringReader, string);
		}
	}

	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
