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
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;

public class class_4202 implements ArgumentType<Integer> {
	private static final Collection<String> field_20609 = Arrays.asList("container.5", "12", "weapon");
	private static final DynamicCommandExceptionType field_20610 = new DynamicCommandExceptionType(object -> new TranslatableText("slot.unknown", object));
	private static final Map<String, Integer> field_20611 = Util.make(Maps.newHashMap(), hashMap -> {
		for (int i = 0; i < 54; i++) {
			hashMap.put("container." + i, i);
		}

		for (int j = 0; j < 9; j++) {
			hashMap.put("hotbar." + j, j);
		}

		for (int k = 0; k < 27; k++) {
			hashMap.put("inventory." + k, 9 + k);
		}

		for (int l = 0; l < 27; l++) {
			hashMap.put("enderchest." + l, 200 + l);
		}

		for (int m = 0; m < 8; m++) {
			hashMap.put("villager." + m, 300 + m);
		}

		for (int n = 0; n < 15; n++) {
			hashMap.put("horse." + n, 500 + n);
		}

		hashMap.put("weapon", 98);
		hashMap.put("weapon.mainhand", 98);
		hashMap.put("weapon.offhand", 99);
		hashMap.put("armor.head", 100 + EquipmentSlot.HEAD.method_13032());
		hashMap.put("armor.chest", 100 + EquipmentSlot.CHEST.method_13032());
		hashMap.put("armor.legs", 100 + EquipmentSlot.LEGS.method_13032());
		hashMap.put("armor.feet", 100 + EquipmentSlot.FEET.method_13032());
		hashMap.put("horse.saddle", 400);
		hashMap.put("horse.armor", 401);
		hashMap.put("horse.chest", 499);
	});

	public static class_4202 method_18948() {
		return new class_4202();
	}

	public static int method_18950(CommandContext<class_3915> commandContext, String string) {
		return (Integer)commandContext.getArgument(string, Integer.class);
	}

	public Integer parse(StringReader stringReader) throws CommandSyntaxException {
		String string = stringReader.readUnquotedString();
		if (!field_20611.containsKey(string)) {
			throw field_20610.create(string);
		} else {
			return (Integer)field_20611.get(string);
		}
	}

	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		return class_3965.method_17571(field_20611.keySet(), suggestionsBuilder);
	}

	public Collection<String> getExamples() {
		return field_20609;
	}
}
