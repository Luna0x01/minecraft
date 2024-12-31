package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;

public class class_4048 implements ArgumentType<class_4048.class_4049> {
	private static final Collection<String> field_19619 = Arrays.asList("eyes", "feet");
	private static final DynamicCommandExceptionType field_19620 = new DynamicCommandExceptionType(
		object -> new TranslatableText("argument.anchor.invalid", object)
	);

	public static class_4048.class_4049 method_17867(CommandContext<class_3915> commandContext, String string) {
		return (class_4048.class_4049)commandContext.getArgument(string, class_4048.class_4049.class);
	}

	public static class_4048 method_17865() {
		return new class_4048();
	}

	public class_4048.class_4049 parse(StringReader stringReader) throws CommandSyntaxException {
		int i = stringReader.getCursor();
		String string = stringReader.readUnquotedString();
		class_4048.class_4049 lv = class_4048.class_4049.method_17873(string);
		if (lv == null) {
			stringReader.setCursor(i);
			throw field_19620.createWithContext(stringReader, string);
		} else {
			return lv;
		}
	}

	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		return class_3965.method_17571(class_4048.class_4049.field_19623.keySet(), suggestionsBuilder);
	}

	public Collection<String> getExamples() {
		return field_19619;
	}

	public static enum class_4049 {
		FEET("feet", (vec3d, entity) -> vec3d),
		EYES("eyes", (vec3d, entity) -> new Vec3d(vec3d.x, vec3d.y + (double)entity.getEyeHeight(), vec3d.z));

		private static final Map<String, class_4048.class_4049> field_19623 = Util.make(Maps.newHashMap(), hashMap -> {
			for (class_4048.class_4049 lv : values()) {
				hashMap.put(lv.field_19624, lv);
			}
		});
		private final String field_19624;
		private final BiFunction<Vec3d, Entity, Vec3d> field_19625;

		private class_4049(String string2, BiFunction<Vec3d, Entity, Vec3d> biFunction) {
			this.field_19624 = string2;
			this.field_19625 = biFunction;
		}

		@Nullable
		public static class_4048.class_4049 method_17873(String string) {
			return (class_4048.class_4049)field_19623.get(string);
		}

		public Vec3d method_17870(Entity entity) {
			return (Vec3d)this.field_19625.apply(new Vec3d(entity.x, entity.y, entity.z), entity);
		}

		public Vec3d method_17871(class_3915 arg) {
			Entity entity = arg.method_17469();
			return entity == null ? arg.method_17467() : (Vec3d)this.field_19625.apply(arg.method_17467(), entity);
		}
	}
}
