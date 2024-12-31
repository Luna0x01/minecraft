package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.text.TranslatableText;

public class class_4271 implements ArgumentType<class_4261> {
	private static final Collection<String> field_20966 = Arrays.asList("0 0", "~ ~", "~-5 ~5");
	public static final SimpleCommandExceptionType field_20965 = new SimpleCommandExceptionType(new TranslatableText("argument.rotation.incomplete"));

	public static class_4271 method_19435() {
		return new class_4271();
	}

	public static class_4261 method_19437(CommandContext<class_3915> commandContext, String string) {
		return (class_4261)commandContext.getArgument(string, class_4261.class);
	}

	public class_4261 parse(StringReader stringReader) throws CommandSyntaxException {
		int i = stringReader.getCursor();
		if (!stringReader.canRead()) {
			throw field_20965.createWithContext(stringReader);
		} else {
			class_4298 lv = class_4298.method_19609(stringReader, false);
			if (stringReader.canRead() && stringReader.peek() == ' ') {
				stringReader.skip();
				class_4298 lv2 = class_4298.method_19609(stringReader, false);
				return new class_4304(lv2, lv, new class_4298(true, 0.0));
			} else {
				stringReader.setCursor(i);
				throw field_20965.createWithContext(stringReader);
			}
		}
	}

	public Collection<String> getExamples() {
		return field_20966;
	}
}
