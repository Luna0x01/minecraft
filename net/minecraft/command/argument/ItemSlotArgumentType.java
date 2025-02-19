package net.minecraft.command.argument;

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
import net.minecraft.command.CommandSource;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;

public class ItemSlotArgumentType implements ArgumentType<Integer> {
	private static final Collection<String> EXAMPLES = Arrays.asList("container.5", "12", "weapon");
	private static final DynamicCommandExceptionType UNKNOWN_SLOT_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("slot.unknown", object)
	);
	private static final Map<String, Integer> slotNamesToSlotCommandId = Util.make(Maps.newHashMap(), hashMap -> {
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

		hashMap.put("weapon", EquipmentSlot.MAINHAND.getOffsetEntitySlotId(98));
		hashMap.put("weapon.mainhand", EquipmentSlot.MAINHAND.getOffsetEntitySlotId(98));
		hashMap.put("weapon.offhand", EquipmentSlot.OFFHAND.getOffsetEntitySlotId(98));
		hashMap.put("armor.head", EquipmentSlot.HEAD.getOffsetEntitySlotId(100));
		hashMap.put("armor.chest", EquipmentSlot.CHEST.getOffsetEntitySlotId(100));
		hashMap.put("armor.legs", EquipmentSlot.LEGS.getOffsetEntitySlotId(100));
		hashMap.put("armor.feet", EquipmentSlot.FEET.getOffsetEntitySlotId(100));
		hashMap.put("horse.saddle", 400);
		hashMap.put("horse.armor", 401);
		hashMap.put("horse.chest", 499);
	});

	public static ItemSlotArgumentType itemSlot() {
		return new ItemSlotArgumentType();
	}

	public static int getItemSlot(CommandContext<ServerCommandSource> context, String name) {
		return (Integer)context.getArgument(name, Integer.class);
	}

	public Integer parse(StringReader stringReader) throws CommandSyntaxException {
		String string = stringReader.readUnquotedString();
		if (!slotNamesToSlotCommandId.containsKey(string)) {
			throw UNKNOWN_SLOT_EXCEPTION.create(string);
		} else {
			return (Integer)slotNamesToSlotCommandId.get(string);
		}
	}

	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return CommandSource.suggestMatching(slotNamesToSlotCommandId.keySet(), builder);
	}

	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
