package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.collection.IntObjectStorage;
import org.lwjgl.glfw.GLFW;

public class class_4107 {
	public static final class_4107.class_4108 field_19910 = class_4107.class_4109.KEYSYM.method_18162(-1);

	public static boolean method_18154(int i) {
		return GLFW.glfwGetKey(MinecraftClient.getInstance().field_19944.method_18315(), i) == 1;
	}

	public static class_4107.class_4108 method_18155(int i, int j) {
		return i == -1 ? class_4107.class_4109.SCANCODE.method_18162(j) : class_4107.class_4109.KEYSYM.method_18162(i);
	}

	public static class_4107.class_4108 method_18156(String string) {
		if (class_4107.class_4108.field_19915.containsKey(string)) {
			return (class_4107.class_4108)class_4107.class_4108.field_19915.get(string);
		} else {
			for (class_4107.class_4109 lv : class_4107.class_4109.values()) {
				if (string.startsWith(lv.field_19921)) {
					String string2 = string.substring(lv.field_19921.length() + 1);
					return lv.method_18162(Integer.parseInt(string2));
				}
			}

			throw new IllegalArgumentException("Unknown key name: " + string);
		}
	}

	public static final class class_4108 {
		private final String field_19912;
		private final class_4107.class_4109 field_19913;
		private final int field_19914;
		private static final Map<String, class_4107.class_4108> field_19915 = Maps.newHashMap();

		private class_4108(String string, class_4107.class_4109 arg, int i) {
			this.field_19912 = string;
			this.field_19913 = arg;
			this.field_19914 = i;
			field_19915.put(string, this);
		}

		public String method_18157() {
			String string = null;
			switch (this.field_19913) {
				case KEYSYM:
					string = GLFW.glfwGetKeyName(this.field_19914, -1);
					break;
				case SCANCODE:
					string = GLFW.glfwGetKeyName(-1, this.field_19914);
					break;
				case MOUSE:
					String string2 = I18n.translate(this.field_19912);
					string = Objects.equals(string2, this.field_19912) ? I18n.translate(class_4107.class_4109.MOUSE.field_19921, this.field_19914 + 1) : string2;
			}

			return string == null ? I18n.translate(this.field_19912) : string;
		}

		public class_4107.class_4109 method_18158() {
			return this.field_19913;
		}

		public int method_18159() {
			return this.field_19914;
		}

		public String method_18160() {
			return this.field_19912;
		}

		public boolean equals(Object object) {
			if (this == object) {
				return true;
			} else if (object != null && this.getClass() == object.getClass()) {
				class_4107.class_4108 lv = (class_4107.class_4108)object;
				return this.field_19914 == lv.field_19914 && this.field_19913 == lv.field_19913;
			} else {
				return false;
			}
		}

		public int hashCode() {
			return Objects.hash(new Object[]{this.field_19913, this.field_19914});
		}

		public String toString() {
			return this.field_19912;
		}
	}

	public static enum class_4109 {
		KEYSYM("key.keyboard"),
		SCANCODE("scancode"),
		MOUSE("key.mouse");

		private static final String[] field_19919 = new String[]{"left", "middle", "right"};
		private final IntObjectStorage<class_4107.class_4108> field_19920 = new IntObjectStorage<>();
		private final String field_19921;

		private static void method_18164(class_4107.class_4109 arg, String string, int i) {
			class_4107.class_4108 lv = new class_4107.class_4108(string, arg, i);
			arg.field_19920.set(i, lv);
		}

		private class_4109(String string2) {
			this.field_19921 = string2;
		}

		public class_4107.class_4108 method_18162(int i) {
			if (this.field_19920.hasEntry(i)) {
				return this.field_19920.get(i);
			} else {
				String string;
				if (this == MOUSE) {
					if (i <= 2) {
						string = "." + field_19919[i];
					} else {
						string = "." + (i + 1);
					}
				} else {
					string = "." + i;
				}

				class_4107.class_4108 lv = new class_4107.class_4108(this.field_19921 + string, this, i);
				this.field_19920.set(i, lv);
				return lv;
			}
		}

		static {
			method_18164(KEYSYM, "key.keyboard.unknown", -1);
			method_18164(MOUSE, "key.mouse.left", 0);
			method_18164(MOUSE, "key.mouse.right", 1);
			method_18164(MOUSE, "key.mouse.middle", 2);
			method_18164(MOUSE, "key.mouse.4", 3);
			method_18164(MOUSE, "key.mouse.5", 4);
			method_18164(MOUSE, "key.mouse.6", 5);
			method_18164(MOUSE, "key.mouse.7", 6);
			method_18164(MOUSE, "key.mouse.8", 7);
			method_18164(KEYSYM, "key.keyboard.0", 48);
			method_18164(KEYSYM, "key.keyboard.1", 49);
			method_18164(KEYSYM, "key.keyboard.2", 50);
			method_18164(KEYSYM, "key.keyboard.3", 51);
			method_18164(KEYSYM, "key.keyboard.4", 52);
			method_18164(KEYSYM, "key.keyboard.5", 53);
			method_18164(KEYSYM, "key.keyboard.6", 54);
			method_18164(KEYSYM, "key.keyboard.7", 55);
			method_18164(KEYSYM, "key.keyboard.8", 56);
			method_18164(KEYSYM, "key.keyboard.9", 57);
			method_18164(KEYSYM, "key.keyboard.a", 65);
			method_18164(KEYSYM, "key.keyboard.b", 66);
			method_18164(KEYSYM, "key.keyboard.c", 67);
			method_18164(KEYSYM, "key.keyboard.d", 68);
			method_18164(KEYSYM, "key.keyboard.e", 69);
			method_18164(KEYSYM, "key.keyboard.f", 70);
			method_18164(KEYSYM, "key.keyboard.g", 71);
			method_18164(KEYSYM, "key.keyboard.h", 72);
			method_18164(KEYSYM, "key.keyboard.i", 73);
			method_18164(KEYSYM, "key.keyboard.j", 74);
			method_18164(KEYSYM, "key.keyboard.k", 75);
			method_18164(KEYSYM, "key.keyboard.l", 76);
			method_18164(KEYSYM, "key.keyboard.m", 77);
			method_18164(KEYSYM, "key.keyboard.n", 78);
			method_18164(KEYSYM, "key.keyboard.o", 79);
			method_18164(KEYSYM, "key.keyboard.p", 80);
			method_18164(KEYSYM, "key.keyboard.q", 81);
			method_18164(KEYSYM, "key.keyboard.r", 82);
			method_18164(KEYSYM, "key.keyboard.s", 83);
			method_18164(KEYSYM, "key.keyboard.t", 84);
			method_18164(KEYSYM, "key.keyboard.u", 85);
			method_18164(KEYSYM, "key.keyboard.v", 86);
			method_18164(KEYSYM, "key.keyboard.w", 87);
			method_18164(KEYSYM, "key.keyboard.x", 88);
			method_18164(KEYSYM, "key.keyboard.y", 89);
			method_18164(KEYSYM, "key.keyboard.z", 90);
			method_18164(KEYSYM, "key.keyboard.f1", 290);
			method_18164(KEYSYM, "key.keyboard.f2", 291);
			method_18164(KEYSYM, "key.keyboard.f3", 292);
			method_18164(KEYSYM, "key.keyboard.f4", 293);
			method_18164(KEYSYM, "key.keyboard.f5", 294);
			method_18164(KEYSYM, "key.keyboard.f6", 295);
			method_18164(KEYSYM, "key.keyboard.f7", 296);
			method_18164(KEYSYM, "key.keyboard.f8", 297);
			method_18164(KEYSYM, "key.keyboard.f9", 298);
			method_18164(KEYSYM, "key.keyboard.f10", 299);
			method_18164(KEYSYM, "key.keyboard.f11", 300);
			method_18164(KEYSYM, "key.keyboard.f12", 301);
			method_18164(KEYSYM, "key.keyboard.f13", 302);
			method_18164(KEYSYM, "key.keyboard.f14", 303);
			method_18164(KEYSYM, "key.keyboard.f15", 304);
			method_18164(KEYSYM, "key.keyboard.f16", 305);
			method_18164(KEYSYM, "key.keyboard.f17", 306);
			method_18164(KEYSYM, "key.keyboard.f18", 307);
			method_18164(KEYSYM, "key.keyboard.f19", 308);
			method_18164(KEYSYM, "key.keyboard.f20", 309);
			method_18164(KEYSYM, "key.keyboard.f21", 310);
			method_18164(KEYSYM, "key.keyboard.f22", 311);
			method_18164(KEYSYM, "key.keyboard.f23", 312);
			method_18164(KEYSYM, "key.keyboard.f24", 313);
			method_18164(KEYSYM, "key.keyboard.f25", 314);
			method_18164(KEYSYM, "key.keyboard.num.lock", 282);
			method_18164(KEYSYM, "key.keyboard.keypad.0", 320);
			method_18164(KEYSYM, "key.keyboard.keypad.1", 321);
			method_18164(KEYSYM, "key.keyboard.keypad.2", 322);
			method_18164(KEYSYM, "key.keyboard.keypad.3", 323);
			method_18164(KEYSYM, "key.keyboard.keypad.4", 324);
			method_18164(KEYSYM, "key.keyboard.keypad.5", 325);
			method_18164(KEYSYM, "key.keyboard.keypad.6", 326);
			method_18164(KEYSYM, "key.keyboard.keypad.7", 327);
			method_18164(KEYSYM, "key.keyboard.keypad.8", 328);
			method_18164(KEYSYM, "key.keyboard.keypad.9", 329);
			method_18164(KEYSYM, "key.keyboard.keypad.add", 334);
			method_18164(KEYSYM, "key.keyboard.keypad.decimal", 330);
			method_18164(KEYSYM, "key.keyboard.keypad.enter", 335);
			method_18164(KEYSYM, "key.keyboard.keypad.equal", 336);
			method_18164(KEYSYM, "key.keyboard.keypad.multiply", 332);
			method_18164(KEYSYM, "key.keyboard.keypad.divide", 331);
			method_18164(KEYSYM, "key.keyboard.keypad.subtract", 333);
			method_18164(KEYSYM, "key.keyboard.down", 264);
			method_18164(KEYSYM, "key.keyboard.left", 263);
			method_18164(KEYSYM, "key.keyboard.right", 262);
			method_18164(KEYSYM, "key.keyboard.up", 265);
			method_18164(KEYSYM, "key.keyboard.apostrophe", 39);
			method_18164(KEYSYM, "key.keyboard.backslash", 92);
			method_18164(KEYSYM, "key.keyboard.comma", 44);
			method_18164(KEYSYM, "key.keyboard.equal", 61);
			method_18164(KEYSYM, "key.keyboard.grave.accent", 96);
			method_18164(KEYSYM, "key.keyboard.left.bracket", 91);
			method_18164(KEYSYM, "key.keyboard.minus", 45);
			method_18164(KEYSYM, "key.keyboard.period", 46);
			method_18164(KEYSYM, "key.keyboard.right.bracket", 93);
			method_18164(KEYSYM, "key.keyboard.semicolon", 59);
			method_18164(KEYSYM, "key.keyboard.slash", 47);
			method_18164(KEYSYM, "key.keyboard.space", 32);
			method_18164(KEYSYM, "key.keyboard.tab", 258);
			method_18164(KEYSYM, "key.keyboard.left.alt", 342);
			method_18164(KEYSYM, "key.keyboard.left.control", 341);
			method_18164(KEYSYM, "key.keyboard.left.shift", 340);
			method_18164(KEYSYM, "key.keyboard.left.win", 343);
			method_18164(KEYSYM, "key.keyboard.right.alt", 346);
			method_18164(KEYSYM, "key.keyboard.right.control", 345);
			method_18164(KEYSYM, "key.keyboard.right.shift", 344);
			method_18164(KEYSYM, "key.keyboard.right.win", 347);
			method_18164(KEYSYM, "key.keyboard.enter", 257);
			method_18164(KEYSYM, "key.keyboard.escape", 256);
			method_18164(KEYSYM, "key.keyboard.backspace", 259);
			method_18164(KEYSYM, "key.keyboard.delete", 261);
			method_18164(KEYSYM, "key.keyboard.end", 269);
			method_18164(KEYSYM, "key.keyboard.home", 268);
			method_18164(KEYSYM, "key.keyboard.insert", 260);
			method_18164(KEYSYM, "key.keyboard.page.down", 267);
			method_18164(KEYSYM, "key.keyboard.page.up", 266);
			method_18164(KEYSYM, "key.keyboard.caps.lock", 280);
			method_18164(KEYSYM, "key.keyboard.pause", 284);
			method_18164(KEYSYM, "key.keyboard.scroll.lock", 281);
			method_18164(KEYSYM, "key.keyboard.menu", 348);
			method_18164(KEYSYM, "key.keyboard.print.screen", 283);
			method_18164(KEYSYM, "key.keyboard.world.1", 161);
			method_18164(KEYSYM, "key.keyboard.world.2", 162);
		}
	}
}
