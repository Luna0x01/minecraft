package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.text.TranslatableText;

public class class_4298 {
	public static final SimpleCommandExceptionType field_21105 = new SimpleCommandExceptionType(new TranslatableText("argument.pos.missing.double"));
	public static final SimpleCommandExceptionType field_21106 = new SimpleCommandExceptionType(new TranslatableText("argument.pos.missing.int"));
	private final boolean field_21107;
	private final double field_21108;

	public class_4298(boolean bl, double d) {
		this.field_21107 = bl;
		this.field_21108 = d;
	}

	public double method_19607(double d) {
		return this.field_21107 ? this.field_21108 + d : this.field_21108;
	}

	public static class_4298 method_19609(StringReader stringReader, boolean bl) throws CommandSyntaxException {
		if (stringReader.canRead() && stringReader.peek() == '^') {
			throw class_4287.field_21052.createWithContext(stringReader);
		} else if (!stringReader.canRead()) {
			throw field_21105.createWithContext(stringReader);
		} else {
			boolean bl2 = method_19610(stringReader);
			int i = stringReader.getCursor();
			double d = stringReader.canRead() && stringReader.peek() != ' ' ? stringReader.readDouble() : 0.0;
			String string = stringReader.getString().substring(i, stringReader.getCursor());
			if (bl2 && string.isEmpty()) {
				return new class_4298(true, 0.0);
			} else {
				if (!string.contains(".") && !bl2 && bl) {
					d += 0.5;
				}

				return new class_4298(bl2, d);
			}
		}
	}

	public static class_4298 method_19608(StringReader stringReader) throws CommandSyntaxException {
		if (stringReader.canRead() && stringReader.peek() == '^') {
			throw class_4287.field_21052.createWithContext(stringReader);
		} else if (!stringReader.canRead()) {
			throw field_21106.createWithContext(stringReader);
		} else {
			boolean bl = method_19610(stringReader);
			double d;
			if (stringReader.canRead() && stringReader.peek() != ' ') {
				d = bl ? stringReader.readDouble() : (double)stringReader.readInt();
			} else {
				d = 0.0;
			}

			return new class_4298(bl, d);
		}
	}

	private static boolean method_19610(StringReader stringReader) {
		boolean bl;
		if (stringReader.peek() == '~') {
			bl = true;
			stringReader.skip();
		} else {
			bl = false;
		}

		return bl;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof class_4298)) {
			return false;
		} else {
			class_4298 lv = (class_4298)object;
			return this.field_21107 != lv.field_21107 ? false : Double.compare(lv.field_21108, this.field_21108) == 0;
		}
	}

	public int hashCode() {
		int i = this.field_21107 ? 1 : 0;
		long l = Double.doubleToLongBits(this.field_21108);
		return 31 * i + (int)(l ^ l >>> 32);
	}

	public boolean method_19606() {
		return this.field_21107;
	}
}
