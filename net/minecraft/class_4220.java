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
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.state.property.Property;
import net.minecraft.tag.Tag;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class class_4220 implements ArgumentType<class_4220.class_4222> {
	private static final Collection<String> field_20712 = Arrays.asList("stone", "minecraft:stone", "stone[foo=bar]", "#stone", "#stone[foo=bar]{baz=nbt}");
	private static final DynamicCommandExceptionType field_20713 = new DynamicCommandExceptionType(
		object -> new TranslatableText("arguments.block.tag.unknown", object)
	);

	public static class_4220 method_19107() {
		return new class_4220();
	}

	public class_4220.class_4222 parse(StringReader stringReader) throws CommandSyntaxException {
		class_4238 lv = new class_4238(stringReader, true).method_19300(true);
		if (lv.method_19301() != null) {
			class_4220.class_4221 lv2 = new class_4220.class_4221(lv.method_19301(), lv.method_19288().keySet(), lv.method_19304());
			return arg2 -> lv2;
		} else {
			Identifier identifier = lv.method_19307();
			return arg2 -> {
				Tag<Block> tag = arg2.method_21492().method_21486(identifier);
				if (tag == null) {
					throw field_20713.create(identifier.toString());
				} else {
					return new class_4220.class_4223(tag, lv.method_19319(), lv.method_19304());
				}
			};
		}
	}

	public static Predicate<CachedBlockPosition> method_19109(CommandContext<class_3915> commandContext, String string) throws CommandSyntaxException {
		return ((class_4220.class_4222)commandContext.getArgument(string, class_4220.class_4222.class))
			.create(((class_3915)commandContext.getSource()).method_17473().method_20332());
	}

	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		StringReader stringReader = new StringReader(suggestionsBuilder.getInput());
		stringReader.setCursor(suggestionsBuilder.getStart());
		class_4238 lv = new class_4238(stringReader, true);

		try {
			lv.method_19300(true);
		} catch (CommandSyntaxException var6) {
		}

		return lv.method_19292(suggestionsBuilder);
	}

	public Collection<String> getExamples() {
		return field_20712;
	}

	static class class_4221 implements Predicate<CachedBlockPosition> {
		private final BlockState field_20714;
		private final Set<Property<?>> field_20715;
		@Nullable
		private final NbtCompound field_20716;

		public class_4221(BlockState blockState, Set<Property<?>> set, @Nullable NbtCompound nbtCompound) {
			this.field_20714 = blockState;
			this.field_20715 = set;
			this.field_20716 = nbtCompound;
		}

		public boolean test(CachedBlockPosition cachedBlockPosition) {
			BlockState blockState = cachedBlockPosition.getBlockState();
			if (blockState.getBlock() != this.field_20714.getBlock()) {
				return false;
			} else {
				for (Property<?> property : this.field_20715) {
					if (blockState.getProperty(property) != this.field_20714.getProperty(property)) {
						return false;
					}
				}

				if (this.field_20716 == null) {
					return true;
				} else {
					BlockEntity blockEntity = cachedBlockPosition.getBlockEntity();
					return blockEntity != null && NbtHelper.areEqual(this.field_20716, blockEntity.toNbt(new NbtCompound()), true);
				}
			}
		}
	}

	public interface class_4222 {
		Predicate<CachedBlockPosition> create(class_4488 arg) throws CommandSyntaxException;
	}

	static class class_4223 implements Predicate<CachedBlockPosition> {
		private final Tag<Block> field_20717;
		@Nullable
		private final NbtCompound field_20718;
		private final Map<String, String> field_20719;

		private class_4223(Tag<Block> tag, Map<String, String> map, @Nullable NbtCompound nbtCompound) {
			this.field_20717 = tag;
			this.field_20719 = map;
			this.field_20718 = nbtCompound;
		}

		public boolean test(CachedBlockPosition cachedBlockPosition) {
			BlockState blockState = cachedBlockPosition.getBlockState();
			if (!blockState.isIn(this.field_20717)) {
				return false;
			} else {
				for (Entry<String, String> entry : this.field_20719.entrySet()) {
					Property<?> property = blockState.getBlock().getStateManager().getProperty((String)entry.getKey());
					if (property == null) {
						return false;
					}

					Comparable<?> comparable = (Comparable<?>)property.getValueAsString((String)entry.getValue()).orElse(null);
					if (comparable == null) {
						return false;
					}

					if (blockState.getProperty(property) != comparable) {
						return false;
					}
				}

				if (this.field_20718 == null) {
					return true;
				} else {
					BlockEntity blockEntity = cachedBlockPosition.getBlockEntity();
					return blockEntity != null && NbtHelper.areEqual(this.field_20718, blockEntity.toNbt(new NbtCompound()), true);
				}
			}
		}
	}
}
