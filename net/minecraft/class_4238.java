package net.minecraft;

import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class class_4238 {
	public static final SimpleCommandExceptionType field_20827 = new SimpleCommandExceptionType(new TranslatableText("argument.block.tag.disallowed"));
	public static final DynamicCommandExceptionType field_20828 = new DynamicCommandExceptionType(
		object -> new TranslatableText("argument.block.id.invalid", object)
	);
	public static final Dynamic2CommandExceptionType field_20829 = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableText("argument.block.property.unknown", object, object2)
	);
	public static final Dynamic2CommandExceptionType field_20830 = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableText("argument.block.property.duplicate", object2, object)
	);
	public static final Dynamic3CommandExceptionType field_20831 = new Dynamic3CommandExceptionType(
		(object, object2, object3) -> new TranslatableText("argument.block.property.invalid", object, object3, object2)
	);
	public static final Dynamic2CommandExceptionType field_20832 = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableText("argument.block.property.novalue", object, object2)
	);
	public static final SimpleCommandExceptionType field_20833 = new SimpleCommandExceptionType(new TranslatableText("argument.block.property.unclosed"));
	private static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> field_20834 = SuggestionsBuilder::buildFuture;
	private final StringReader field_20835;
	private final boolean field_20836;
	private final Map<Property<?>, Comparable<?>> field_20837 = Maps.newHashMap();
	private final Map<String, String> field_20838 = Maps.newHashMap();
	private Identifier field_20839 = new Identifier("");
	private StateManager<Block, BlockState> field_20840;
	private BlockState field_20841;
	@Nullable
	private NbtCompound field_20842;
	private Identifier field_20843 = new Identifier("");
	private int field_20844;
	private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> field_20845 = field_20834;

	public class_4238(StringReader stringReader, boolean bl) {
		this.field_20835 = stringReader;
		this.field_20836 = bl;
	}

	public Map<Property<?>, Comparable<?>> method_19288() {
		return this.field_20837;
	}

	@Nullable
	public BlockState method_19301() {
		return this.field_20841;
	}

	@Nullable
	public NbtCompound method_19304() {
		return this.field_20842;
	}

	@Nullable
	public Identifier method_19307() {
		return this.field_20843;
	}

	public class_4238 method_19300(boolean bl) throws CommandSyntaxException {
		this.field_20845 = this::method_19323;
		if (this.field_20835.canRead() && this.field_20835.peek() == '#') {
			this.method_19311();
			this.field_20845 = this::method_19318;
			if (this.field_20835.canRead() && this.field_20835.peek() == '[') {
				this.method_19315();
				this.field_20845 = this::method_19312;
			}
		} else {
			this.method_19309();
			this.field_20845 = this::method_19320;
			if (this.field_20835.canRead() && this.field_20835.peek() == '[') {
				this.method_19313();
				this.field_20845 = this::method_19312;
			}
		}

		if (bl && this.field_20835.canRead() && this.field_20835.peek() == '{') {
			this.field_20845 = field_20834;
			this.method_19317();
		}

		return this;
	}

	private CompletableFuture<Suggestions> method_19302(SuggestionsBuilder suggestionsBuilder) {
		if (suggestionsBuilder.getRemaining().isEmpty()) {
			suggestionsBuilder.suggest(String.valueOf(']'));
		}

		return this.method_19308(suggestionsBuilder);
	}

	private CompletableFuture<Suggestions> method_19305(SuggestionsBuilder suggestionsBuilder) {
		if (suggestionsBuilder.getRemaining().isEmpty()) {
			suggestionsBuilder.suggest(String.valueOf(']'));
		}

		return this.method_19310(suggestionsBuilder);
	}

	private CompletableFuture<Suggestions> method_19308(SuggestionsBuilder suggestionsBuilder) {
		String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);

		for (Property<?> property : this.field_20841.method_16929()) {
			if (!this.field_20837.containsKey(property) && property.getName().startsWith(string)) {
				suggestionsBuilder.suggest(property.getName() + '=');
			}
		}

		return suggestionsBuilder.buildFuture();
	}

	private CompletableFuture<Suggestions> method_19310(SuggestionsBuilder suggestionsBuilder) {
		String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
		if (this.field_20843 != null && !this.field_20843.getPath().isEmpty()) {
			Tag<Block> tag = BlockTags.getContainer().method_21486(this.field_20843);
			if (tag != null) {
				for (Block block : tag.values()) {
					for (Property<?> property : block.getStateManager().getProperties()) {
						if (!this.field_20838.containsKey(property.getName()) && property.getName().startsWith(string)) {
							suggestionsBuilder.suggest(property.getName() + '=');
						}
					}
				}
			}
		}

		return suggestionsBuilder.buildFuture();
	}

	private CompletableFuture<Suggestions> method_19312(SuggestionsBuilder suggestionsBuilder) {
		if (suggestionsBuilder.getRemaining().isEmpty() && this.method_19321()) {
			suggestionsBuilder.suggest(String.valueOf('{'));
		}

		return suggestionsBuilder.buildFuture();
	}

	private boolean method_19321() {
		if (this.field_20841 != null) {
			return this.field_20841.getBlock().hasBlockEntity();
		} else {
			if (this.field_20843 != null) {
				Tag<Block> tag = BlockTags.getContainer().method_21486(this.field_20843);
				if (tag != null) {
					for (Block block : tag.values()) {
						if (block.hasBlockEntity()) {
							return true;
						}
					}
				}
			}

			return false;
		}
	}

	private CompletableFuture<Suggestions> method_19314(SuggestionsBuilder suggestionsBuilder) {
		if (suggestionsBuilder.getRemaining().isEmpty()) {
			suggestionsBuilder.suggest(String.valueOf('='));
		}

		return suggestionsBuilder.buildFuture();
	}

	private CompletableFuture<Suggestions> method_19316(SuggestionsBuilder suggestionsBuilder) {
		if (suggestionsBuilder.getRemaining().isEmpty()) {
			suggestionsBuilder.suggest(String.valueOf(']'));
		}

		if (suggestionsBuilder.getRemaining().isEmpty() && this.field_20837.size() < this.field_20841.method_16929().size()) {
			suggestionsBuilder.suggest(String.valueOf(','));
		}

		return suggestionsBuilder.buildFuture();
	}

	private static <T extends Comparable<T>> SuggestionsBuilder method_19293(SuggestionsBuilder suggestionsBuilder, Property<T> property) {
		for (T comparable : property.getValues()) {
			if (comparable instanceof Integer) {
				suggestionsBuilder.suggest((Integer)comparable);
			} else {
				suggestionsBuilder.suggest(property.name(comparable));
			}
		}

		return suggestionsBuilder;
	}

	private CompletableFuture<Suggestions> method_19294(SuggestionsBuilder suggestionsBuilder, String string) {
		boolean bl = false;
		if (this.field_20843 != null && !this.field_20843.getPath().isEmpty()) {
			Tag<Block> tag = BlockTags.getContainer().method_21486(this.field_20843);
			if (tag != null) {
				for (Block block : tag.values()) {
					Property<?> property = block.getStateManager().getProperty(string);
					if (property != null) {
						method_19293(suggestionsBuilder, property);
					}

					if (!bl) {
						for (Property<?> property2 : block.getStateManager().getProperties()) {
							if (!this.field_20838.containsKey(property2.getName())) {
								bl = true;
								break;
							}
						}
					}
				}
			}
		}

		if (bl) {
			suggestionsBuilder.suggest(String.valueOf(','));
		}

		suggestionsBuilder.suggest(String.valueOf(']'));
		return suggestionsBuilder.buildFuture();
	}

	private CompletableFuture<Suggestions> method_19318(SuggestionsBuilder suggestionsBuilder) {
		if (suggestionsBuilder.getRemaining().isEmpty()) {
			Tag<Block> tag = BlockTags.getContainer().method_21486(this.field_20843);
			if (tag != null) {
				boolean bl = false;
				boolean bl2 = false;

				for (Block block : tag.values()) {
					bl |= !block.getStateManager().getProperties().isEmpty();
					bl2 |= block.hasBlockEntity();
					if (bl && bl2) {
						break;
					}
				}

				if (bl) {
					suggestionsBuilder.suggest(String.valueOf('['));
				}

				if (bl2) {
					suggestionsBuilder.suggest(String.valueOf('{'));
				}
			}
		}

		return this.method_19322(suggestionsBuilder);
	}

	private CompletableFuture<Suggestions> method_19320(SuggestionsBuilder suggestionsBuilder) {
		if (suggestionsBuilder.getRemaining().isEmpty()) {
			if (!this.field_20841.getBlock().getStateManager().getProperties().isEmpty()) {
				suggestionsBuilder.suggest(String.valueOf('['));
			}

			if (this.field_20841.getBlock().hasBlockEntity()) {
				suggestionsBuilder.suggest(String.valueOf('{'));
			}
		}

		return suggestionsBuilder.buildFuture();
	}

	private CompletableFuture<Suggestions> method_19322(SuggestionsBuilder suggestionsBuilder) {
		return class_3965.method_17559(BlockTags.getContainer().method_21483(), suggestionsBuilder.createOffset(this.field_20844).add(suggestionsBuilder));
	}

	private CompletableFuture<Suggestions> method_19323(SuggestionsBuilder suggestionsBuilder) {
		if (this.field_20836) {
			class_3965.method_17560(BlockTags.getContainer().method_21483(), suggestionsBuilder, String.valueOf('#'));
		}

		class_3965.method_17559(Registry.BLOCK.getKeySet(), suggestionsBuilder);
		return suggestionsBuilder.buildFuture();
	}

	public void method_19309() throws CommandSyntaxException {
		int i = this.field_20835.getCursor();
		this.field_20839 = Identifier.method_20442(this.field_20835);
		if (Registry.BLOCK.containsId(this.field_20839)) {
			Block block = Registry.BLOCK.get(this.field_20839);
			this.field_20840 = block.getStateManager();
			this.field_20841 = block.getDefaultState();
		} else {
			this.field_20835.setCursor(i);
			throw field_20828.createWithContext(this.field_20835, this.field_20839.toString());
		}
	}

	public void method_19311() throws CommandSyntaxException {
		if (!this.field_20836) {
			throw field_20827.create();
		} else {
			this.field_20845 = this::method_19322;
			this.field_20835.expect('#');
			this.field_20844 = this.field_20835.getCursor();
			this.field_20843 = Identifier.method_20442(this.field_20835);
		}
	}

	public void method_19313() throws CommandSyntaxException {
		this.field_20835.skip();
		this.field_20845 = this::method_19302;
		this.field_20835.skipWhitespace();

		while (this.field_20835.canRead() && this.field_20835.peek() != ']') {
			this.field_20835.skipWhitespace();
			int i = this.field_20835.getCursor();
			String string = this.field_20835.readString();
			Property<?> property = this.field_20840.getProperty(string);
			if (property == null) {
				this.field_20835.setCursor(i);
				throw field_20829.createWithContext(this.field_20835, this.field_20839.toString(), string);
			}

			if (this.field_20837.containsKey(property)) {
				this.field_20835.setCursor(i);
				throw field_20830.createWithContext(this.field_20835, this.field_20839.toString(), string);
			}

			this.field_20835.skipWhitespace();
			this.field_20845 = this::method_19314;
			if (!this.field_20835.canRead() || this.field_20835.peek() != '=') {
				throw field_20832.createWithContext(this.field_20835, this.field_20839.toString(), string);
			}

			this.field_20835.skip();
			this.field_20835.skipWhitespace();
			this.field_20845 = suggestionsBuilder -> method_19293(suggestionsBuilder, property).buildFuture();
			int j = this.field_20835.getCursor();
			this.method_19291(property, this.field_20835.readString(), j);
			this.field_20845 = this::method_19316;
			this.field_20835.skipWhitespace();
			if (this.field_20835.canRead()) {
				if (this.field_20835.peek() != ',') {
					if (this.field_20835.peek() != ']') {
						throw field_20833.createWithContext(this.field_20835);
					}
					break;
				}

				this.field_20835.skip();
				this.field_20845 = this::method_19308;
			}
		}

		if (this.field_20835.canRead()) {
			this.field_20835.skip();
		} else {
			throw field_20833.createWithContext(this.field_20835);
		}
	}

	public void method_19315() throws CommandSyntaxException {
		this.field_20835.skip();
		this.field_20845 = this::method_19305;
		int i = -1;
		this.field_20835.skipWhitespace();

		while (this.field_20835.canRead() && this.field_20835.peek() != ']') {
			this.field_20835.skipWhitespace();
			int j = this.field_20835.getCursor();
			String string = this.field_20835.readString();
			if (this.field_20838.containsKey(string)) {
				this.field_20835.setCursor(j);
				throw field_20830.createWithContext(this.field_20835, this.field_20839.toString(), string);
			}

			this.field_20835.skipWhitespace();
			if (!this.field_20835.canRead() || this.field_20835.peek() != '=') {
				this.field_20835.setCursor(j);
				throw field_20832.createWithContext(this.field_20835, this.field_20839.toString(), string);
			}

			this.field_20835.skip();
			this.field_20835.skipWhitespace();
			this.field_20845 = suggestionsBuilder -> this.method_19294(suggestionsBuilder, string);
			i = this.field_20835.getCursor();
			String string2 = this.field_20835.readString();
			this.field_20838.put(string, string2);
			this.field_20835.skipWhitespace();
			if (this.field_20835.canRead()) {
				i = -1;
				if (this.field_20835.peek() != ',') {
					if (this.field_20835.peek() != ']') {
						throw field_20833.createWithContext(this.field_20835);
					}
					break;
				}

				this.field_20835.skip();
				this.field_20845 = this::method_19310;
			}
		}

		if (this.field_20835.canRead()) {
			this.field_20835.skip();
		} else {
			if (i >= 0) {
				this.field_20835.setCursor(i);
			}

			throw field_20833.createWithContext(this.field_20835);
		}
	}

	public void method_19317() throws CommandSyntaxException {
		this.field_20842 = new StringNbtReader(this.field_20835).parseCompound();
	}

	private <T extends Comparable<T>> void method_19291(Property<T> property, String string, int i) throws CommandSyntaxException {
		Optional<T> optional = property.getValueAsString(string);
		if (optional.isPresent()) {
			this.field_20841 = this.field_20841.withProperty(property, (Comparable)optional.get());
			this.field_20837.put(property, optional.get());
		} else {
			this.field_20835.setCursor(i);
			throw field_20831.createWithContext(this.field_20835, this.field_20839.toString(), property.getName(), string);
		}
	}

	public static String method_19289(BlockState blockState, @Nullable NbtCompound nbtCompound) {
		StringBuilder stringBuilder = new StringBuilder(Registry.BLOCK.getId(blockState.getBlock()).toString());
		if (!blockState.method_16929().isEmpty()) {
			stringBuilder.append('[');
			boolean bl = false;

			for (UnmodifiableIterator var4 = blockState.getEntries().entrySet().iterator(); var4.hasNext(); bl = true) {
				Entry<Property<?>, Comparable<?>> entry = (Entry<Property<?>, Comparable<?>>)var4.next();
				if (bl) {
					stringBuilder.append(',');
				}

				method_19299(stringBuilder, (Property)entry.getKey(), (Comparable<?>)entry.getValue());
			}

			stringBuilder.append(']');
		}

		if (nbtCompound != null) {
			stringBuilder.append(nbtCompound);
		}

		return stringBuilder.toString();
	}

	private static <T extends Comparable<T>> void method_19299(StringBuilder stringBuilder, Property<T> property, Comparable<?> comparable) {
		stringBuilder.append(property.getName());
		stringBuilder.append('=');
		stringBuilder.append(property.name((T)comparable));
	}

	public CompletableFuture<Suggestions> method_19292(SuggestionsBuilder suggestionsBuilder) {
		return (CompletableFuture<Suggestions>)this.field_20845.apply(suggestionsBuilder.createOffset(this.field_20835.getCursor()));
	}

	public Map<String, String> method_19319() {
		return this.field_20838;
	}
}
