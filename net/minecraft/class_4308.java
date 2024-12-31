package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.server.function.Function;
import net.minecraft.tag.Tag;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class class_4308 implements ArgumentType<class_4308.class_4309> {
	private static final Collection<String> field_21151 = Arrays.asList("foo", "foo:bar", "#foo");
	private static final DynamicCommandExceptionType field_21152 = new DynamicCommandExceptionType(
		object -> new TranslatableText("arguments.function.tag.unknown", object)
	);
	private static final DynamicCommandExceptionType field_21153 = new DynamicCommandExceptionType(
		object -> new TranslatableText("arguments.function.unknown", object)
	);

	public static class_4308 method_19691() {
		return new class_4308();
	}

	public class_4308.class_4309 parse(StringReader stringReader) throws CommandSyntaxException {
		if (stringReader.canRead() && stringReader.peek() == '#') {
			stringReader.skip();
			Identifier identifier = Identifier.method_20442(stringReader);
			return commandContext -> {
				Tag<Function> tag = ((class_3915)commandContext.getSource()).method_17473().method_14911().method_20463().method_21486(identifier);
				if (tag == null) {
					throw field_21152.create(identifier.toString());
				} else {
					return tag.values();
				}
			};
		} else {
			Identifier identifier2 = Identifier.method_20442(stringReader);
			return commandContext -> {
				Function function = ((class_3915)commandContext.getSource()).method_17473().method_14911().getFunction(identifier2);
				if (function == null) {
					throw field_21153.create(identifier2.toString());
				} else {
					return Collections.singleton(function);
				}
			};
		}
	}

	public static Collection<Function> method_19693(CommandContext<class_3915> commandContext, String string) throws CommandSyntaxException {
		return ((class_4308.class_4309)commandContext.getArgument(string, class_4308.class_4309.class)).create(commandContext);
	}

	public Collection<String> getExamples() {
		return field_21151;
	}

	public interface class_4309 {
		Collection<Function> create(CommandContext<class_3915> commandContext) throws CommandSyntaxException;
	}
}
