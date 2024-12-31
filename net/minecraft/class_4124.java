package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.nbt.AbstractNbtList;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.TranslatableText;

public class class_4124 implements ArgumentType<class_4124.class_4127> {
	private static final Collection<String> field_20090 = Arrays.asList("foo", "foo.bar", "foo[0]", "[0]", ".");
	private static final DynamicCommandExceptionType field_20091 = new DynamicCommandExceptionType(
		object -> new TranslatableText("arguments.nbtpath.child.invalid", object)
	);
	private static final DynamicCommandExceptionType field_20092 = new DynamicCommandExceptionType(
		object -> new TranslatableText("arguments.nbtpath.element.invalid", object)
	);
	private static final SimpleCommandExceptionType field_20093 = new SimpleCommandExceptionType(new TranslatableText("arguments.nbtpath.node.invalid"));

	public static class_4124 method_18432() {
		return new class_4124();
	}

	public static class_4124.class_4127 method_18435(CommandContext<class_3915> commandContext, String string) {
		return (class_4124.class_4127)commandContext.getArgument(string, class_4124.class_4127.class);
	}

	public class_4124.class_4127 parse(StringReader stringReader) throws CommandSyntaxException {
		List<class_4124.class_4128> list = Lists.newArrayList();
		int i = stringReader.getCursor();

		while (stringReader.canRead() && stringReader.peek() != ' ') {
			switch (stringReader.peek()) {
				case '"':
					list.add(new class_4124.class_4125(stringReader.readString()));
					break;
				case '[':
					stringReader.skip();
					list.add(new class_4124.class_4126(stringReader.readInt()));
					stringReader.expect(']');
					break;
				default:
					list.add(new class_4124.class_4125(this.method_18438(stringReader)));
			}

			if (stringReader.canRead()) {
				char c = stringReader.peek();
				if (c != ' ' && c != '[') {
					stringReader.expect('.');
				}
			}
		}

		return new class_4124.class_4127(
			stringReader.getString().substring(i, stringReader.getCursor()), (class_4124.class_4128[])list.toArray(new class_4124.class_4128[0])
		);
	}

	private String method_18438(StringReader stringReader) throws CommandSyntaxException {
		int i = stringReader.getCursor();

		while (stringReader.canRead() && method_18433(stringReader.peek())) {
			stringReader.skip();
		}

		if (stringReader.getCursor() == i) {
			throw field_20093.createWithContext(stringReader);
		} else {
			return stringReader.getString().substring(i, stringReader.getCursor());
		}
	}

	public Collection<String> getExamples() {
		return field_20090;
	}

	private static boolean method_18433(char c) {
		return c != ' ' && c != '"' && c != '[' && c != ']' && c != '.';
	}

	static class class_4125 implements class_4124.class_4128 {
		private final String field_20094;

		public class_4125(String string) {
			this.field_20094 = string;
		}

		@Override
		public NbtElement method_18446(NbtElement nbtElement) throws CommandSyntaxException {
			if (nbtElement instanceof NbtCompound) {
				return ((NbtCompound)nbtElement).get(this.field_20094);
			} else {
				throw class_4124.field_20091.create(this.field_20094);
			}
		}

		@Override
		public NbtElement method_18448(NbtElement nbtElement, Supplier<NbtElement> supplier) throws CommandSyntaxException {
			if (nbtElement instanceof NbtCompound) {
				NbtCompound nbtCompound = (NbtCompound)nbtElement;
				if (nbtCompound.contains(this.field_20094)) {
					return nbtCompound.get(this.field_20094);
				} else {
					NbtElement nbtElement2 = (NbtElement)supplier.get();
					nbtCompound.put(this.field_20094, nbtElement2);
					return nbtElement2;
				}
			} else {
				throw class_4124.field_20091.create(this.field_20094);
			}
		}

		@Override
		public NbtElement method_18445() {
			return new NbtCompound();
		}

		@Override
		public void method_18447(NbtElement nbtElement, NbtElement nbtElement2) throws CommandSyntaxException {
			if (nbtElement instanceof NbtCompound) {
				NbtCompound nbtCompound = (NbtCompound)nbtElement;
				nbtCompound.put(this.field_20094, nbtElement2);
			} else {
				throw class_4124.field_20091.create(this.field_20094);
			}
		}

		@Override
		public void method_18449(NbtElement nbtElement) throws CommandSyntaxException {
			if (nbtElement instanceof NbtCompound) {
				NbtCompound nbtCompound = (NbtCompound)nbtElement;
				if (nbtCompound.contains(this.field_20094)) {
					nbtCompound.remove(this.field_20094);
					return;
				}
			}

			throw class_4124.field_20091.create(this.field_20094);
		}
	}

	static class class_4126 implements class_4124.class_4128 {
		private final int field_20095;

		public class_4126(int i) {
			this.field_20095 = i;
		}

		@Override
		public NbtElement method_18446(NbtElement nbtElement) throws CommandSyntaxException {
			if (nbtElement instanceof AbstractNbtList) {
				AbstractNbtList<?> abstractNbtList = (AbstractNbtList<?>)nbtElement;
				if (abstractNbtList.size() > this.field_20095) {
					return abstractNbtList.getElement(this.field_20095);
				}
			}

			throw class_4124.field_20092.create(this.field_20095);
		}

		@Override
		public NbtElement method_18448(NbtElement nbtElement, Supplier<NbtElement> supplier) throws CommandSyntaxException {
			return this.method_18446(nbtElement);
		}

		@Override
		public NbtElement method_18445() {
			return new NbtList();
		}

		@Override
		public void method_18447(NbtElement nbtElement, NbtElement nbtElement2) throws CommandSyntaxException {
			if (nbtElement instanceof AbstractNbtList) {
				AbstractNbtList<?> abstractNbtList = (AbstractNbtList<?>)nbtElement;
				if (abstractNbtList.size() > this.field_20095) {
					abstractNbtList.setElement(this.field_20095, nbtElement2);
					return;
				}
			}

			throw class_4124.field_20092.create(this.field_20095);
		}

		@Override
		public void method_18449(NbtElement nbtElement) throws CommandSyntaxException {
			if (nbtElement instanceof AbstractNbtList) {
				AbstractNbtList<?> abstractNbtList = (AbstractNbtList<?>)nbtElement;
				if (abstractNbtList.size() > this.field_20095) {
					abstractNbtList.remove(this.field_20095);
					return;
				}
			}

			throw class_4124.field_20092.create(this.field_20095);
		}
	}

	public static class class_4127 {
		private final String field_20096;
		private final class_4124.class_4128[] field_20097;

		public class_4127(String string, class_4124.class_4128[] args) {
			this.field_20096 = string;
			this.field_20097 = args;
		}

		public NbtElement method_18442(NbtElement nbtElement) throws CommandSyntaxException {
			for (class_4124.class_4128 lv : this.field_20097) {
				nbtElement = lv.method_18446(nbtElement);
			}

			return nbtElement;
		}

		public NbtElement method_18443(NbtElement nbtElement, NbtElement nbtElement2) throws CommandSyntaxException {
			for (int i = 0; i < this.field_20097.length; i++) {
				class_4124.class_4128 lv = this.field_20097[i];
				if (i < this.field_20097.length - 1) {
					int j = i + 1;
					nbtElement = lv.method_18448(nbtElement, () -> this.field_20097[j].method_18445());
				} else {
					lv.method_18447(nbtElement, nbtElement2);
				}
			}

			return nbtElement;
		}

		public String toString() {
			return this.field_20096;
		}

		public void method_18444(NbtElement nbtElement) throws CommandSyntaxException {
			for (int i = 0; i < this.field_20097.length; i++) {
				class_4124.class_4128 lv = this.field_20097[i];
				if (i < this.field_20097.length - 1) {
					nbtElement = lv.method_18446(nbtElement);
				} else {
					lv.method_18449(nbtElement);
				}
			}
		}
	}

	interface class_4128 {
		NbtElement method_18446(NbtElement nbtElement) throws CommandSyntaxException;

		NbtElement method_18448(NbtElement nbtElement, Supplier<NbtElement> supplier) throws CommandSyntaxException;

		NbtElement method_18445();

		void method_18447(NbtElement nbtElement, NbtElement nbtElement2) throws CommandSyntaxException;

		void method_18449(NbtElement nbtElement) throws CommandSyntaxException;
	}
}
