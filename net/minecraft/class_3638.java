package net.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.JsonHelper;

public abstract class class_3638<T extends Number> {
	public static final SimpleCommandExceptionType field_17691 = new SimpleCommandExceptionType(new TranslatableText("argument.range.empty"));
	public static final SimpleCommandExceptionType field_17692 = new SimpleCommandExceptionType(new TranslatableText("argument.range.swapped"));
	protected final T field_17693;
	protected final T field_17694;

	protected class_3638(@Nullable T number, @Nullable T number2) {
		this.field_17693 = number;
		this.field_17694 = number2;
	}

	@Nullable
	public T method_16505() {
		return this.field_17693;
	}

	@Nullable
	public T method_16511() {
		return this.field_17694;
	}

	public boolean method_16512() {
		return this.field_17693 == null && this.field_17694 == null;
	}

	public JsonElement method_16513() {
		if (this.method_16512()) {
			return JsonNull.INSTANCE;
		} else if (this.field_17693 != null && this.field_17693.equals(this.field_17694)) {
			return new JsonPrimitive(this.field_17693);
		} else {
			JsonObject jsonObject = new JsonObject();
			if (this.field_17693 != null) {
				jsonObject.addProperty("min", this.field_17693);
			}

			if (this.field_17694 != null) {
				jsonObject.addProperty("max", this.field_17693);
			}

			return jsonObject;
		}
	}

	protected static <T extends Number, R extends class_3638<T>> R method_16506(
		@Nullable JsonElement jsonElement, R arg, BiFunction<JsonElement, String, T> biFunction, class_3638.class_3639<T, R> arg2
	) {
		if (jsonElement == null || jsonElement.isJsonNull()) {
			return arg;
		} else if (JsonHelper.isNumber(jsonElement)) {
			T number = (T)biFunction.apply(jsonElement, "value");
			return arg2.create(number, number);
		} else {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "value");
			T number2 = (T)(jsonObject.has("min") ? biFunction.apply(jsonObject.get("min"), "min") : null);
			T number3 = (T)(jsonObject.has("max") ? biFunction.apply(jsonObject.get("max"), "max") : null);
			return arg2.create(number2, number3);
		}
	}

	protected static <T extends Number, R extends class_3638<T>> R method_16508(
		StringReader stringReader,
		class_3638.class_3640<T, R> arg,
		Function<String, T> function,
		Supplier<DynamicCommandExceptionType> supplier,
		Function<T, T> function2
	) throws CommandSyntaxException {
		if (!stringReader.canRead()) {
			throw field_17691.createWithContext(stringReader);
		} else {
			int i = stringReader.getCursor();

			try {
				T number = (T)method_16510(method_16509(stringReader, function, supplier), function2);
				T number2;
				if (stringReader.canRead(2) && stringReader.peek() == '.' && stringReader.peek(1) == '.') {
					stringReader.skip();
					stringReader.skip();
					number2 = (T)method_16510(method_16509(stringReader, function, supplier), function2);
					if (number == null && number2 == null) {
						throw field_17691.createWithContext(stringReader);
					}
				} else {
					number2 = number;
				}

				if (number == null && number2 == null) {
					throw field_17691.createWithContext(stringReader);
				} else {
					return arg.create(stringReader, number, number2);
				}
			} catch (CommandSyntaxException var8) {
				stringReader.setCursor(i);
				throw new CommandSyntaxException(var8.getType(), var8.getRawMessage(), var8.getInput(), i);
			}
		}
	}

	@Nullable
	private static <T extends Number> T method_16509(StringReader stringReader, Function<String, T> function, Supplier<DynamicCommandExceptionType> supplier) throws CommandSyntaxException {
		int i = stringReader.getCursor();

		while (stringReader.canRead() && method_16507(stringReader)) {
			stringReader.skip();
		}

		String string = stringReader.getString().substring(i, stringReader.getCursor());
		if (string.isEmpty()) {
			return null;
		} else {
			try {
				return (T)function.apply(string);
			} catch (NumberFormatException var6) {
				throw ((DynamicCommandExceptionType)supplier.get()).createWithContext(stringReader, string);
			}
		}
	}

	private static boolean method_16507(StringReader stringReader) {
		char c = stringReader.peek();
		if ((c < '0' || c > '9') && c != '-') {
			return c != '.' ? false : !stringReader.canRead(2) || stringReader.peek(1) != '.';
		} else {
			return true;
		}
	}

	@Nullable
	private static <T> T method_16510(@Nullable T object, Function<T, T> function) {
		return (T)(object == null ? null : function.apply(object));
	}

	@FunctionalInterface
	public interface class_3639<T extends Number, R extends class_3638<T>> {
		R create(@Nullable T number, @Nullable T number2);
	}

	@FunctionalInterface
	public interface class_3640<T extends Number, R extends class_3638<T>> {
		R create(StringReader stringReader, @Nullable T number, @Nullable T number2) throws CommandSyntaxException;
	}

	public static class class_3641 extends class_3638<Float> {
		public static final class_3638.class_3641 field_17695 = new class_3638.class_3641(null, null);
		private final Double field_17696;
		private final Double field_17697;

		private static class_3638.class_3641 method_16517(StringReader stringReader, @Nullable Float float_, @Nullable Float float2) throws CommandSyntaxException {
			if (float_ != null && float2 != null && float_ > float2) {
				throw field_17692.createWithContext(stringReader);
			} else {
				return new class_3638.class_3641(float_, float2);
			}
		}

		@Nullable
		private static Double method_16519(@Nullable Float float_) {
			return float_ == null ? null : float_.doubleValue() * float_.doubleValue();
		}

		private class_3641(@Nullable Float float_, @Nullable Float float2) {
			super(float_, float2);
			this.field_17696 = method_16519(float_);
			this.field_17697 = method_16519(float2);
		}

		public static class_3638.class_3641 method_16520(float f) {
			return new class_3638.class_3641(f, null);
		}

		public boolean method_16522(float f) {
			return this.field_17693 != null && this.field_17693 > f ? false : this.field_17694 == null || !(this.field_17694 < f);
		}

		public boolean method_16514(double d) {
			return this.field_17696 != null && this.field_17696 > d ? false : this.field_17697 == null || !(this.field_17697 < d);
		}

		public static class_3638.class_3641 method_16515(@Nullable JsonElement jsonElement) {
			return method_16506(jsonElement, field_17695, JsonHelper::asFloat, class_3638.class_3641::new);
		}

		public static class_3638.class_3641 method_16516(StringReader stringReader) throws CommandSyntaxException {
			return method_16518(stringReader, float_ -> float_);
		}

		public static class_3638.class_3641 method_16518(StringReader stringReader, Function<Float, Float> function) throws CommandSyntaxException {
			return method_16508(
				stringReader, class_3638.class_3641::method_16517, Float::parseFloat, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidFloat, function
			);
		}
	}

	public static class class_3642 extends class_3638<Integer> {
		public static final class_3638.class_3642 field_17698 = new class_3638.class_3642(null, null);
		private final Long field_17699;
		private final Long field_17700;

		private static class_3638.class_3642 method_16526(StringReader stringReader, @Nullable Integer integer, @Nullable Integer integer2) throws CommandSyntaxException {
			if (integer != null && integer2 != null && integer > integer2) {
				throw field_17692.createWithContext(stringReader);
			} else {
				return new class_3638.class_3642(integer, integer2);
			}
		}

		@Nullable
		private static Long method_16528(@Nullable Integer integer) {
			return integer == null ? null : integer.longValue() * integer.longValue();
		}

		private class_3642(@Nullable Integer integer, @Nullable Integer integer2) {
			super(integer, integer2);
			this.field_17699 = method_16528(integer);
			this.field_17700 = method_16528(integer2);
		}

		public static class_3638.class_3642 method_16523(int i) {
			return new class_3638.class_3642(i, i);
		}

		public static class_3638.class_3642 method_16529(int i) {
			return new class_3638.class_3642(i, null);
		}

		public boolean method_16531(int i) {
			return this.field_17693 != null && this.field_17693 > i ? false : this.field_17694 == null || this.field_17694 >= i;
		}

		public static class_3638.class_3642 method_16524(@Nullable JsonElement jsonElement) {
			return method_16506(jsonElement, field_17698, JsonHelper::asInt, class_3638.class_3642::new);
		}

		public static class_3638.class_3642 method_16525(StringReader stringReader) throws CommandSyntaxException {
			return method_16527(stringReader, integer -> integer);
		}

		public static class_3638.class_3642 method_16527(StringReader stringReader, Function<Integer, Integer> function) throws CommandSyntaxException {
			return method_16508(
				stringReader, class_3638.class_3642::method_16526, Integer::parseInt, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidInt, function
			);
		}
	}
}
