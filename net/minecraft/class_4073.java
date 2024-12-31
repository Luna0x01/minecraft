package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
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
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

public class class_4073 implements ArgumentType<class_4073.class_4074> {
	private static final Collection<String> field_19806 = Arrays.asList("Player", "0123", "dd12be42-52a9-4a91-a8a1-11c01849e498", "@e");
	public static final SimpleCommandExceptionType field_19805 = new SimpleCommandExceptionType(new TranslatableText("argument.player.unknown"));

	public static Collection<GameProfile> method_17991(CommandContext<class_3915> commandContext, String string) throws CommandSyntaxException {
		return ((class_4073.class_4074)commandContext.getArgument(string, class_4073.class_4074.class)).getNames((class_3915)commandContext.getSource());
	}

	public static class_4073 method_17988() {
		return new class_4073();
	}

	public class_4073.class_4074 parse(StringReader stringReader) throws CommandSyntaxException {
		if (stringReader.canRead() && stringReader.peek() == '@') {
			class_4318 lv = new class_4318(stringReader);
			class_4317 lv2 = lv.method_19818();
			if (lv2.method_19734()) {
				throw class_4062.field_19706.create();
			} else {
				return new class_4073.class_4075(lv2);
			}
		} else {
			int i = stringReader.getCursor();

			while (stringReader.canRead() && stringReader.peek() != ' ') {
				stringReader.skip();
			}

			String string = stringReader.getString().substring(i, stringReader.getCursor());
			return arg -> {
				GameProfile gameProfile = arg.method_17473().getUserCache().findByName(string);
				if (gameProfile == null) {
					throw field_19805.create();
				} else {
					return Collections.singleton(gameProfile);
				}
			};
		}
	}

	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		if (commandContext.getSource() instanceof class_3965) {
			StringReader stringReader = new StringReader(suggestionsBuilder.getInput());
			stringReader.setCursor(suggestionsBuilder.getStart());
			class_4318 lv = new class_4318(stringReader);

			try {
				lv.method_19818();
			} catch (CommandSyntaxException var6) {
			}

			return lv.method_19761(
				suggestionsBuilder, suggestionsBuilderx -> class_3965.method_17571(((class_3965)commandContext.getSource()).method_17576(), suggestionsBuilderx)
			);
		} else {
			return Suggestions.empty();
		}
	}

	public Collection<String> getExamples() {
		return field_19806;
	}

	@FunctionalInterface
	public interface class_4074 {
		Collection<GameProfile> getNames(class_3915 arg) throws CommandSyntaxException;
	}

	public static class class_4075 implements class_4073.class_4074 {
		private final class_4317 field_19807;

		public class_4075(class_4317 arg) {
			this.field_19807 = arg;
		}

		@Override
		public Collection<GameProfile> getNames(class_3915 arg) throws CommandSyntaxException {
			List<ServerPlayerEntity> list = this.field_19807.method_19739(arg);
			if (list.isEmpty()) {
				throw class_4062.field_19708.create();
			} else {
				List<GameProfile> list2 = Lists.newArrayList();

				for (ServerPlayerEntity serverPlayerEntity : list) {
					list2.add(serverPlayerEntity.getGameProfile());
				}

				return list2;
			}
		}
	}
}
