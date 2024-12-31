package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.state.property.Property;
import net.minecraft.tag.ItemTags;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class class_4312 {
	public static final SimpleCommandExceptionType field_21158 = new SimpleCommandExceptionType(new TranslatableText("argument.item.tag.disallowed"));
	public static final DynamicCommandExceptionType field_21159 = new DynamicCommandExceptionType(
		object -> new TranslatableText("argument.item.id.invalid", object)
	);
	private static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> field_21160 = SuggestionsBuilder::buildFuture;
	private final StringReader field_21161;
	private final boolean field_21162;
	private final Map<Property<?>, Comparable<?>> field_21163 = Maps.newHashMap();
	private Item field_21164;
	@Nullable
	private NbtCompound field_21165;
	private Identifier field_21166 = new Identifier("");
	private int field_21167;
	private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> field_21168 = field_21160;

	public class_4312(StringReader stringReader, boolean bl) {
		this.field_21161 = stringReader;
		this.field_21162 = bl;
	}

	public Item method_19708() {
		return this.field_21164;
	}

	@Nullable
	public NbtCompound method_19710() {
		return this.field_21165;
	}

	public Identifier method_19712() {
		return this.field_21166;
	}

	public void method_19714() throws CommandSyntaxException {
		int i = this.field_21161.getCursor();
		Identifier identifier = Identifier.method_20442(this.field_21161);
		if (Registry.ITEM.containsId(identifier)) {
			this.field_21164 = Registry.ITEM.getByIdentifier(identifier);
		} else {
			this.field_21161.setCursor(i);
			throw field_21159.createWithContext(this.field_21161, identifier.toString());
		}
	}

	public void method_19715() throws CommandSyntaxException {
		if (!this.field_21162) {
			throw field_21158.create();
		} else {
			this.field_21168 = this::method_19711;
			this.field_21161.expect('#');
			this.field_21167 = this.field_21161.getCursor();
			this.field_21166 = Identifier.method_20442(this.field_21161);
		}
	}

	public void method_19716() throws CommandSyntaxException {
		this.field_21165 = new StringNbtReader(this.field_21161).parseCompound();
	}

	public class_4312 method_19717() throws CommandSyntaxException {
		this.field_21168 = this::method_19713;
		if (this.field_21161.canRead() && this.field_21161.peek() == '#') {
			this.method_19715();
		} else {
			this.method_19714();
			this.field_21168 = this::method_19709;
		}

		if (this.field_21161.canRead() && this.field_21161.peek() == '{') {
			this.field_21168 = field_21160;
			this.method_19716();
		}

		return this;
	}

	private CompletableFuture<Suggestions> method_19709(SuggestionsBuilder suggestionsBuilder) {
		if (suggestionsBuilder.getRemaining().isEmpty()) {
			suggestionsBuilder.suggest(String.valueOf('{'));
		}

		return suggestionsBuilder.buildFuture();
	}

	private CompletableFuture<Suggestions> method_19711(SuggestionsBuilder suggestionsBuilder) {
		return class_3965.method_17559(ItemTags.method_21451().method_21483(), suggestionsBuilder.createOffset(this.field_21167));
	}

	private CompletableFuture<Suggestions> method_19713(SuggestionsBuilder suggestionsBuilder) {
		if (this.field_21162) {
			class_3965.method_17560(ItemTags.method_21451().method_21483(), suggestionsBuilder, String.valueOf('#'));
		}

		return class_3965.method_17559(Registry.ITEM.getKeySet(), suggestionsBuilder);
	}

	public CompletableFuture<Suggestions> method_19706(SuggestionsBuilder suggestionsBuilder) {
		return (CompletableFuture<Suggestions>)this.field_21168.apply(suggestionsBuilder.createOffset(this.field_21161.getCursor()));
	}
}
