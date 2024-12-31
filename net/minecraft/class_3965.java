package net.minecraft;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.util.Identifier;

public interface class_3965 {
	Collection<String> method_17576();

	default Collection<String> method_17580() {
		return Collections.emptyList();
	}

	Collection<String> method_17577();

	Collection<Identifier> method_17578();

	Collection<Identifier> method_17579();

	CompletableFuture<Suggestions> method_17555(CommandContext<class_3965> commandContext, SuggestionsBuilder suggestionsBuilder);

	Collection<class_3965.class_3966> method_17569(boolean bl);

	boolean method_17575(int i);

	static <T> void method_17563(Iterable<T> iterable, String string, Function<T, Identifier> function, Consumer<T> consumer) {
		boolean bl = string.indexOf(58) > -1;

		for (T object : iterable) {
			Identifier identifier = (Identifier)function.apply(object);
			if (bl) {
				String string2 = identifier.toString();
				if (string2.startsWith(string)) {
					consumer.accept(object);
				}
			} else if (identifier.getNamespace().startsWith(string) || identifier.getNamespace().equals("minecraft") && identifier.getPath().startsWith(string)) {
				consumer.accept(object);
			}
		}
	}

	static <T> void method_17562(Iterable<T> iterable, String string, String string2, Function<T, Identifier> function, Consumer<T> consumer) {
		if (string.isEmpty()) {
			iterable.forEach(consumer);
		} else {
			String string3 = Strings.commonPrefix(string, string2);
			if (!string3.isEmpty()) {
				String string4 = string.substring(string3.length());
				method_17563(iterable, string4, function, consumer);
			}
		}
	}

	static CompletableFuture<Suggestions> method_17560(Iterable<Identifier> iterable, SuggestionsBuilder suggestionsBuilder, String string) {
		String string2 = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
		method_17562(iterable, string2, string, identifier -> identifier, identifier -> suggestionsBuilder.suggest(string + identifier));
		return suggestionsBuilder.buildFuture();
	}

	static CompletableFuture<Suggestions> method_17559(Iterable<Identifier> iterable, SuggestionsBuilder suggestionsBuilder) {
		String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
		method_17563(iterable, string, identifier -> identifier, identifier -> suggestionsBuilder.suggest(identifier.toString()));
		return suggestionsBuilder.buildFuture();
	}

	static <T> CompletableFuture<Suggestions> method_17561(
		Iterable<T> iterable, SuggestionsBuilder suggestionsBuilder, Function<T, Identifier> function, Function<T, Message> function2
	) {
		String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
		method_17563(
			iterable, string, function, object -> suggestionsBuilder.suggest(((Identifier)function.apply(object)).toString(), (Message)function2.apply(object))
		);
		return suggestionsBuilder.buildFuture();
	}

	static CompletableFuture<Suggestions> method_17566(Stream<Identifier> stream, SuggestionsBuilder suggestionsBuilder) {
		return method_17559(stream::iterator, suggestionsBuilder);
	}

	static <T> CompletableFuture<Suggestions> method_17567(
		Stream<T> stream, SuggestionsBuilder suggestionsBuilder, Function<T, Identifier> function, Function<T, Message> function2
	) {
		return method_17561(stream::iterator, suggestionsBuilder, function, function2);
	}

	static CompletableFuture<Suggestions> method_17565(
		String string, Collection<class_3965.class_3966> collection, SuggestionsBuilder suggestionsBuilder, Predicate<String> predicate
	) {
		List<String> list = Lists.newArrayList();
		if (Strings.isNullOrEmpty(string)) {
			for (class_3965.class_3966 lv : collection) {
				String string2 = lv.field_19336 + " " + lv.field_19337 + " " + lv.field_19338;
				if (predicate.test(string2)) {
					list.add(lv.field_19336);
					list.add(lv.field_19336 + " " + lv.field_19337);
					list.add(string2);
				}
			}
		} else {
			String[] strings = string.split(" ");
			if (strings.length == 1) {
				for (class_3965.class_3966 lv2 : collection) {
					String string3 = strings[0] + " " + lv2.field_19337 + " " + lv2.field_19338;
					if (predicate.test(string3)) {
						list.add(strings[0] + " " + lv2.field_19337);
						list.add(string3);
					}
				}
			} else if (strings.length == 2) {
				for (class_3965.class_3966 lv3 : collection) {
					String string4 = strings[0] + " " + strings[1] + " " + lv3.field_19338;
					if (predicate.test(string4)) {
						list.add(string4);
					}
				}
			}
		}

		return method_17571(list, suggestionsBuilder);
	}

	static CompletableFuture<Suggestions> method_17572(
		String string, Collection<class_3965.class_3966> collection, SuggestionsBuilder suggestionsBuilder, Predicate<String> predicate
	) {
		List<String> list = Lists.newArrayList();
		if (Strings.isNullOrEmpty(string)) {
			for (class_3965.class_3966 lv : collection) {
				String string2 = lv.field_19336 + " " + lv.field_19338;
				if (predicate.test(string2)) {
					list.add(lv.field_19336);
					list.add(string2);
				}
			}
		} else {
			String[] strings = string.split(" ");
			if (strings.length == 1) {
				for (class_3965.class_3966 lv2 : collection) {
					String string3 = strings[0] + " " + lv2.field_19338;
					if (predicate.test(string3)) {
						list.add(string3);
					}
				}
			}
		}

		return method_17571(list, suggestionsBuilder);
	}

	static CompletableFuture<Suggestions> method_17571(Iterable<String> iterable, SuggestionsBuilder suggestionsBuilder) {
		String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);

		for (String string2 : iterable) {
			if (string2.toLowerCase(Locale.ROOT).startsWith(string)) {
				suggestionsBuilder.suggest(string2);
			}
		}

		return suggestionsBuilder.buildFuture();
	}

	static CompletableFuture<Suggestions> method_17573(Stream<String> stream, SuggestionsBuilder suggestionsBuilder) {
		String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
		stream.filter(string2 -> string2.toLowerCase(Locale.ROOT).startsWith(string)).forEach(suggestionsBuilder::suggest);
		return suggestionsBuilder.buildFuture();
	}

	static CompletableFuture<Suggestions> method_17570(String[] strings, SuggestionsBuilder suggestionsBuilder) {
		String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);

		for (String string2 : strings) {
			if (string2.toLowerCase(Locale.ROOT).startsWith(string)) {
				suggestionsBuilder.suggest(string2);
			}
		}

		return suggestionsBuilder.buildFuture();
	}

	public static class class_3966 {
		public static final class_3965.class_3966 field_19334 = new class_3965.class_3966("^", "^", "^");
		public static final class_3965.class_3966 field_19335 = new class_3965.class_3966("~", "~", "~");
		public final String field_19336;
		public final String field_19337;
		public final String field_19338;

		public class_3966(String string, String string2, String string3) {
			this.field_19336 = string;
			this.field_19337 = string2;
			this.field_19338 = string3;
		}
	}
}
