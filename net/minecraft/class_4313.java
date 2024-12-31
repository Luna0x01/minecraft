package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.tag.Tag;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class class_4313 implements ArgumentType<class_4313.class_4315> {
	private static final Collection<String> field_21169 = Arrays.asList("stick", "minecraft:stick", "#stick", "#stick{foo=bar}");
	private static final DynamicCommandExceptionType field_21170 = new DynamicCommandExceptionType(
		object -> new TranslatableText("arguments.item.tag.unknown", object)
	);

	public static class_4313 method_19718() {
		return new class_4313();
	}

	public class_4313.class_4315 parse(StringReader stringReader) throws CommandSyntaxException {
		class_4312 lv = new class_4312(stringReader, true).method_19717();
		if (lv.method_19708() != null) {
			class_4313.class_4314 lv2 = new class_4313.class_4314(lv.method_19708(), lv.method_19710());
			return commandContext -> lv2;
		} else {
			Identifier identifier = lv.method_19712();
			return commandContext -> {
				Tag<Item> tag = ((class_3915)commandContext.getSource()).method_17473().method_20332().method_21494().method_21486(identifier);
				if (tag == null) {
					throw field_21170.create(identifier.toString());
				} else {
					return new class_4313.class_4316(tag, lv.method_19710());
				}
			};
		}
	}

	public static Predicate<ItemStack> method_19720(CommandContext<class_3915> commandContext, String string) throws CommandSyntaxException {
		return ((class_4313.class_4315)commandContext.getArgument(string, class_4313.class_4315.class)).create(commandContext);
	}

	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		StringReader stringReader = new StringReader(suggestionsBuilder.getInput());
		stringReader.setCursor(suggestionsBuilder.getStart());
		class_4312 lv = new class_4312(stringReader, true);

		try {
			lv.method_19717();
		} catch (CommandSyntaxException var6) {
		}

		return lv.method_19706(suggestionsBuilder);
	}

	public Collection<String> getExamples() {
		return field_21169;
	}

	static class class_4314 implements Predicate<ItemStack> {
		private final Item field_21171;
		@Nullable
		private final NbtCompound field_21172;

		public class_4314(Item item, @Nullable NbtCompound nbtCompound) {
			this.field_21171 = item;
			this.field_21172 = nbtCompound;
		}

		public boolean test(ItemStack itemStack) {
			return itemStack.getItem() == this.field_21171 && NbtHelper.areEqual(this.field_21172, itemStack.getNbt(), true);
		}
	}

	public interface class_4315 {
		Predicate<ItemStack> create(CommandContext<class_3915> commandContext) throws CommandSyntaxException;
	}

	static class class_4316 implements Predicate<ItemStack> {
		private final Tag<Item> field_21173;
		@Nullable
		private final NbtCompound field_21174;

		public class_4316(Tag<Item> tag, @Nullable NbtCompound nbtCompound) {
			this.field_21173 = tag;
			this.field_21174 = nbtCompound;
		}

		public boolean test(ItemStack itemStack) {
			return this.field_21173.contains(itemStack.getItem()) && NbtHelper.areEqual(this.field_21174, itemStack.getNbt(), true);
		}
	}
}
