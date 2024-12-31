package net.minecraft;

import net.minecraft.util.math.MathHelper;

public class class_3082 {
	private final class_3082.class_3083 field_15220;
	private byte field_15221;
	private byte field_15222;
	private byte field_15223;

	public class_3082(class_3082.class_3083 arg, byte b, byte c, byte d) {
		this.field_15220 = arg;
		this.field_15221 = b;
		this.field_15222 = c;
		this.field_15223 = d;
	}

	public byte method_13819() {
		return this.field_15220.method_13825();
	}

	public class_3082.class_3083 method_13820() {
		return this.field_15220;
	}

	public byte method_13821() {
		return this.field_15221;
	}

	public byte method_13822() {
		return this.field_15222;
	}

	public byte method_13823() {
		return this.field_15223;
	}

	public boolean method_13824() {
		return this.field_15220.method_13827();
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof class_3082)) {
			return false;
		} else {
			class_3082 lv = (class_3082)object;
			if (this.field_15220 != lv.field_15220) {
				return false;
			} else if (this.field_15223 != lv.field_15223) {
				return false;
			} else {
				return this.field_15221 != lv.field_15221 ? false : this.field_15222 == lv.field_15222;
			}
		}
	}

	public int hashCode() {
		int i = this.field_15220.method_13825();
		i = 31 * i + this.field_15221;
		i = 31 * i + this.field_15222;
		return 31 * i + this.field_15223;
	}

	public static enum class_3083 {
		PLAYER(false),
		FRAME(true),
		RED_MARKER(false),
		BLUE_MARKER(false),
		TARGET_X(true),
		TARGET_POINT(true),
		PLAYER_OFF_MAP(false),
		PLAYER_OFF_LIMITS(false),
		MANSION(true, 5393476),
		MONUMENT(true, 3830373);

		private final byte field_15234 = (byte)this.ordinal();
		private final boolean field_15235;
		private final int field_15236;

		private class_3083(boolean bl) {
			this(bl, -1);
		}

		private class_3083(boolean bl, int j) {
			this.field_15235 = bl;
			this.field_15236 = j;
		}

		public byte method_13825() {
			return this.field_15234;
		}

		public boolean method_13827() {
			return this.field_15235;
		}

		public boolean method_13828() {
			return this.field_15236 >= 0;
		}

		public int method_13829() {
			return this.field_15236;
		}

		public static class_3082.class_3083 method_13826(byte b) {
			return values()[MathHelper.clamp(b, 0, values().length - 1)];
		}
	}
}
