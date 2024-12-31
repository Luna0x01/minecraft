package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.text.TranslatableText;

public class class_4119 implements ArgumentType<NbtCompound> {
	private static final Collection<String> field_20072 = Arrays.asList("{}", "{foo=bar}");
	public static final DynamicCommandExceptionType field_20071 = new DynamicCommandExceptionType(object -> new TranslatableText("argument.nbt.invalid", object));

	private class_4119() {
	}

	public static class_4119 method_18393() {
		return new class_4119();
	}

	public static <S> NbtCompound method_18395(CommandContext<S> commandContext, String string) {
		return (NbtCompound)commandContext.getArgument(string, NbtCompound.class);
	}

	public NbtCompound parse(StringReader stringReader) throws CommandSyntaxException {
		return new StringNbtReader(stringReader).parseCompound();
	}

	public Collection<String> getExamples() {
		return field_20072;
	}
}
