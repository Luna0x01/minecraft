package net.minecraft;

import com.google.common.collect.Iterables;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.PacketByteBuf;

public class class_4062 implements ArgumentType<class_4317> {
	private static final Collection<String> field_19710 = Arrays.asList("Player", "0123", "@e", "@e[type=foo]", "dd12be42-52a9-4a91-a8a1-11c01849e498");
	public static final SimpleCommandExceptionType field_19704 = new SimpleCommandExceptionType(new TranslatableText("argument.entity.toomany"));
	public static final SimpleCommandExceptionType field_19705 = new SimpleCommandExceptionType(new TranslatableText("argument.player.toomany"));
	public static final SimpleCommandExceptionType field_19706 = new SimpleCommandExceptionType(new TranslatableText("argument.player.entities"));
	public static final SimpleCommandExceptionType field_19707 = new SimpleCommandExceptionType(new TranslatableText("argument.entity.notfound.entity"));
	public static final SimpleCommandExceptionType field_19708 = new SimpleCommandExceptionType(new TranslatableText("argument.entity.notfound.player"));
	public static final SimpleCommandExceptionType field_19709 = new SimpleCommandExceptionType(new TranslatableText("argument.entity.selector.not_allowed"));
	private final boolean field_19711;
	private final boolean field_19712;

	protected class_4062(boolean bl, boolean bl2) {
		this.field_19711 = bl;
		this.field_19712 = bl2;
	}

	public static class_4062 method_17894() {
		return new class_4062(true, false);
	}

	public static Entity method_17898(CommandContext<class_3915> commandContext, String string) throws CommandSyntaxException {
		return ((class_4317)commandContext.getArgument(string, class_4317.class)).method_19727((class_3915)commandContext.getSource());
	}

	public static class_4062 method_17899() {
		return new class_4062(false, false);
	}

	public static Collection<? extends Entity> method_17901(CommandContext<class_3915> commandContext, String string) throws CommandSyntaxException {
		Collection<? extends Entity> collection = method_17903(commandContext, string);
		if (collection.isEmpty()) {
			throw field_19707.create();
		} else {
			return collection;
		}
	}

	public static Collection<? extends Entity> method_17903(CommandContext<class_3915> commandContext, String string) throws CommandSyntaxException {
		return ((class_4317)commandContext.getArgument(string, class_4317.class)).method_19735((class_3915)commandContext.getSource());
	}

	public static Collection<ServerPlayerEntity> method_17905(CommandContext<class_3915> commandContext, String string) throws CommandSyntaxException {
		return ((class_4317)commandContext.getArgument(string, class_4317.class)).method_19739((class_3915)commandContext.getSource());
	}

	public static class_4062 method_17902() {
		return new class_4062(true, true);
	}

	public static ServerPlayerEntity method_17906(CommandContext<class_3915> commandContext, String string) throws CommandSyntaxException {
		return ((class_4317)commandContext.getArgument(string, class_4317.class)).method_19737((class_3915)commandContext.getSource());
	}

	public static class_4062 method_17904() {
		return new class_4062(false, true);
	}

	public static Collection<ServerPlayerEntity> method_17907(CommandContext<class_3915> commandContext, String string) throws CommandSyntaxException {
		List<ServerPlayerEntity> list = ((class_4317)commandContext.getArgument(string, class_4317.class)).method_19739((class_3915)commandContext.getSource());
		if (list.isEmpty()) {
			throw field_19708.create();
		} else {
			return list;
		}
	}

	public class_4317 parse(StringReader stringReader) throws CommandSyntaxException {
		int i = 0;
		class_4318 lv = new class_4318(stringReader);
		class_4317 lv2 = lv.method_19818();
		if (lv2.method_19726() > 1 && this.field_19711) {
			if (this.field_19712) {
				stringReader.setCursor(0);
				throw field_19705.createWithContext(stringReader);
			} else {
				stringReader.setCursor(0);
				throw field_19704.createWithContext(stringReader);
			}
		} else if (lv2.method_19734() && this.field_19712 && !lv2.method_19736()) {
			stringReader.setCursor(0);
			throw field_19706.createWithContext(stringReader);
		} else {
			return lv2;
		}
	}

	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		if (commandContext.getSource() instanceof class_3965) {
			StringReader stringReader = new StringReader(suggestionsBuilder.getInput());
			stringReader.setCursor(suggestionsBuilder.getStart());
			class_3965 lv = (class_3965)commandContext.getSource();
			class_4318 lv2 = new class_4318(stringReader, lv.method_17575(2));

			try {
				lv2.method_19818();
			} catch (CommandSyntaxException var7) {
			}

			return lv2.method_19761(suggestionsBuilder, suggestionsBuilderx -> {
				Collection<String> collection = lv.method_17576();
				Iterable<String> iterable = (Iterable<String>)(this.field_19712 ? collection : Iterables.concat(collection, lv.method_17580()));
				class_3965.method_17571(iterable, suggestionsBuilderx);
			});
		} else {
			return Suggestions.empty();
		}
	}

	public Collection<String> getExamples() {
		return field_19710;
	}

	public static class class_4063 implements class_4322<class_4062> {
		public void method_19890(class_4062 arg, PacketByteBuf packetByteBuf) {
			byte b = 0;
			if (arg.field_19711) {
				b = (byte)(b | 1);
			}

			if (arg.field_19712) {
				b = (byte)(b | 2);
			}

			packetByteBuf.writeByte(b);
		}

		public class_4062 method_19891(PacketByteBuf packetByteBuf) {
			byte b = packetByteBuf.readByte();
			return new class_4062((b & 1) != 0, (b & 2) != 0);
		}

		public void method_19889(class_4062 arg, JsonObject jsonObject) {
			jsonObject.addProperty("amount", arg.field_19711 ? "single" : "multiple");
			jsonObject.addProperty("type", arg.field_19712 ? "players" : "entities");
		}
	}
}
