package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class class_4284 implements ArgumentType<class_4261> {
	private static final Collection<String> field_21036 = Arrays.asList("0 0", "~ ~", "0.1 -0.5", "~1 ~-2");
	public static final SimpleCommandExceptionType field_21035 = new SimpleCommandExceptionType(new TranslatableText("argument.pos2d.incomplete"));
	private final boolean field_21037;

	public class_4284(boolean bl) {
		this.field_21037 = bl;
	}

	public static class_4284 method_19539() {
		return new class_4284(true);
	}

	public static Vec2f method_19541(CommandContext<class_3915> commandContext, String string) throws CommandSyntaxException {
		Vec3d vec3d = ((class_4261)commandContext.getArgument(string, class_4261.class)).method_19411((class_3915)commandContext.getSource());
		return new Vec2f((float)vec3d.x, (float)vec3d.z);
	}

	public class_4261 parse(StringReader stringReader) throws CommandSyntaxException {
		int i = stringReader.getCursor();
		if (!stringReader.canRead()) {
			throw field_21035.createWithContext(stringReader);
		} else {
			class_4298 lv = class_4298.method_19609(stringReader, this.field_21037);
			if (stringReader.canRead() && stringReader.peek() == ' ') {
				stringReader.skip();
				class_4298 lv2 = class_4298.method_19609(stringReader, this.field_21037);
				return new class_4304(lv, new class_4298(true, 0.0), lv2);
			} else {
				stringReader.setCursor(i);
				throw field_21035.createWithContext(stringReader);
			}
		}
	}

	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		if (!(commandContext.getSource() instanceof class_3965)) {
			return Suggestions.empty();
		} else {
			String string = suggestionsBuilder.getRemaining();
			Collection<class_3965.class_3966> collection;
			if (!string.isEmpty() && string.charAt(0) == '^') {
				collection = Collections.singleton(class_3965.class_3966.field_19334);
			} else {
				collection = ((class_3965)commandContext.getSource()).method_17569(true);
			}

			return class_3965.method_17572(string, collection, suggestionsBuilder, CommandManager.method_17520(this::parse));
		}
	}

	public Collection<String> getExamples() {
		return field_21036;
	}
}
