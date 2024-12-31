package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.text.TranslatableText;

public class class_3783 {
	public static final class_3783 field_18843 = new class_3783(null, null);
	public static final SimpleCommandExceptionType field_18844 = new SimpleCommandExceptionType(new TranslatableText("argument.range.ints"));
	private final Float field_18845;
	private final Float field_18846;

	public class_3783(@Nullable Float float_, @Nullable Float float2) {
		this.field_18845 = float_;
		this.field_18846 = float2;
	}

	@Nullable
	public Float method_17032() {
		return this.field_18845;
	}

	@Nullable
	public Float method_17035() {
		return this.field_18846;
	}

	public static class_3783 method_17033(StringReader stringReader, boolean bl, Function<Float, Float> function) throws CommandSyntaxException {
		if (!stringReader.canRead()) {
			throw class_3638.field_17691.createWithContext(stringReader);
		} else {
			int i = stringReader.getCursor();
			Float float_ = method_17034(method_17036(stringReader, bl), function);
			Float float2;
			if (stringReader.canRead(2) && stringReader.peek() == '.' && stringReader.peek(1) == '.') {
				stringReader.skip();
				stringReader.skip();
				float2 = method_17034(method_17036(stringReader, bl), function);
				if (float_ == null && float2 == null) {
					stringReader.setCursor(i);
					throw class_3638.field_17691.createWithContext(stringReader);
				}
			} else {
				if (!bl && stringReader.canRead() && stringReader.peek() == '.') {
					stringReader.setCursor(i);
					throw field_18844.createWithContext(stringReader);
				}

				float2 = float_;
			}

			if (float_ == null && float2 == null) {
				stringReader.setCursor(i);
				throw class_3638.field_17691.createWithContext(stringReader);
			} else {
				return new class_3783(float_, float2);
			}
		}
	}

	@Nullable
	private static Float method_17036(StringReader stringReader, boolean bl) throws CommandSyntaxException {
		int i = stringReader.getCursor();

		while (stringReader.canRead() && method_17037(stringReader, bl)) {
			stringReader.skip();
		}

		String string = stringReader.getString().substring(i, stringReader.getCursor());
		if (string.isEmpty()) {
			return null;
		} else {
			try {
				return Float.parseFloat(string);
			} catch (NumberFormatException var5) {
				if (bl) {
					throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidDouble().createWithContext(stringReader, string);
				} else {
					throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt().createWithContext(stringReader, string);
				}
			}
		}
	}

	private static boolean method_17037(StringReader stringReader, boolean bl) {
		char c = stringReader.peek();
		if ((c < '0' || c > '9') && c != '-') {
			return bl && c == '.' ? !stringReader.canRead(2) || stringReader.peek(1) != '.' : false;
		} else {
			return true;
		}
	}

	@Nullable
	private static Float method_17034(@Nullable Float float_, Function<Float, Float> function) {
		return float_ == null ? null : (Float)function.apply(float_);
	}
}
