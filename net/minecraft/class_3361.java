package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancement.SimpleAdvancement;

public class class_3361 {
	private final SimpleAdvancement advancement;
	private final class_3361 field_16493;
	private final class_3361 field_16494;
	private final int field_16495;
	private final List<class_3361> field_16496 = Lists.newArrayList();
	private class_3361 field_16497;
	private class_3361 field_16498;
	private int field_16499;
	private float field_16500;
	private float field_16501;
	private float field_16502;
	private float field_16503;

	public class_3361(SimpleAdvancement simpleAdvancement, @Nullable class_3361 arg, @Nullable class_3361 arg2, int i, int j) {
		if (simpleAdvancement.getDisplay() == null) {
			throw new IllegalArgumentException("Can't position an invisible advancement!");
		} else {
			this.advancement = simpleAdvancement;
			this.field_16493 = arg;
			this.field_16494 = arg2;
			this.field_16495 = i;
			this.field_16497 = this;
			this.field_16499 = j;
			this.field_16500 = -1.0F;
			class_3361 lv = null;

			for (SimpleAdvancement simpleAdvancement2 : simpleAdvancement.getChildren()) {
				lv = this.method_15032(simpleAdvancement2, lv);
			}
		}
	}

	@Nullable
	private class_3361 method_15032(SimpleAdvancement simpleAdvancement, @Nullable class_3361 arg) {
		if (simpleAdvancement.getDisplay() != null) {
			arg = new class_3361(simpleAdvancement, this, arg, this.field_16496.size() + 1, this.field_16499 + 1);
			this.field_16496.add(arg);
		} else {
			for (SimpleAdvancement simpleAdvancement2 : simpleAdvancement.getChildren()) {
				arg = this.method_15032(simpleAdvancement2, arg);
			}
		}

		return arg;
	}

	private void method_15028() {
		if (this.field_16496.isEmpty()) {
			if (this.field_16494 != null) {
				this.field_16500 = this.field_16494.field_16500 + 1.0F;
			} else {
				this.field_16500 = 0.0F;
			}
		} else {
			class_3361 lv = null;

			for (class_3361 lv2 : this.field_16496) {
				lv2.method_15028();
				lv = lv2.method_15033(lv == null ? lv2 : lv);
			}

			this.method_15036();
			float f = (((class_3361)this.field_16496.get(0)).field_16500 + ((class_3361)this.field_16496.get(this.field_16496.size() - 1)).field_16500) / 2.0F;
			if (this.field_16494 != null) {
				this.field_16500 = this.field_16494.field_16500 + 1.0F;
				this.field_16501 = this.field_16500 - f;
			} else {
				this.field_16500 = f;
			}
		}
	}

	private float method_15030(float f, int i, float g) {
		this.field_16500 += f;
		this.field_16499 = i;
		if (this.field_16500 < g) {
			g = this.field_16500;
		}

		for (class_3361 lv : this.field_16496) {
			g = lv.method_15030(f + this.field_16501, i + 1, g);
		}

		return g;
	}

	private void method_15029(float f) {
		this.field_16500 += f;

		for (class_3361 lv : this.field_16496) {
			lv.method_15029(f);
		}
	}

	private void method_15036() {
		float f = 0.0F;
		float g = 0.0F;

		for (int i = this.field_16496.size() - 1; i >= 0; i--) {
			class_3361 lv = (class_3361)this.field_16496.get(i);
			lv.field_16500 += f;
			lv.field_16501 += f;
			g += lv.field_16502;
			f += lv.field_16503 + g;
		}
	}

	@Nullable
	private class_3361 method_15037() {
		if (this.field_16498 != null) {
			return this.field_16498;
		} else {
			return !this.field_16496.isEmpty() ? (class_3361)this.field_16496.get(0) : null;
		}
	}

	@Nullable
	private class_3361 method_15038() {
		if (this.field_16498 != null) {
			return this.field_16498;
		} else {
			return !this.field_16496.isEmpty() ? (class_3361)this.field_16496.get(this.field_16496.size() - 1) : null;
		}
	}

	private class_3361 method_15033(class_3361 arg) {
		if (this.field_16494 == null) {
			return arg;
		} else {
			class_3361 lv = this;
			class_3361 lv2 = this;
			class_3361 lv3 = this.field_16494;
			class_3361 lv4 = (class_3361)this.field_16493.field_16496.get(0);
			float f = this.field_16501;
			float g = this.field_16501;
			float h = lv3.field_16501;

			float i;
			for (i = lv4.field_16501; lv3.method_15038() != null && lv.method_15037() != null; g += lv2.field_16501) {
				lv3 = lv3.method_15038();
				lv = lv.method_15037();
				lv4 = lv4.method_15037();
				lv2 = lv2.method_15038();
				lv2.field_16497 = this;
				float j = lv3.field_16500 + h - (lv.field_16500 + f) + 1.0F;
				if (j > 0.0F) {
					lv3.method_15035(this, arg).method_15034(this, j);
					f += j;
					g += j;
				}

				h += lv3.field_16501;
				f += lv.field_16501;
				i += lv4.field_16501;
			}

			if (lv3.method_15038() != null && lv2.method_15038() == null) {
				lv2.field_16498 = lv3.method_15038();
				lv2.field_16501 += h - g;
			} else {
				if (lv.method_15037() != null && lv4.method_15037() == null) {
					lv4.field_16498 = lv.method_15037();
					lv4.field_16501 += f - i;
				}

				arg = this;
			}

			return arg;
		}
	}

	private void method_15034(class_3361 arg, float f) {
		float g = (float)(arg.field_16495 - this.field_16495);
		if (g != 0.0F) {
			arg.field_16502 -= f / g;
			this.field_16502 += f / g;
		}

		arg.field_16503 += f;
		arg.field_16500 += f;
		arg.field_16501 += f;
	}

	private class_3361 method_15035(class_3361 arg, class_3361 arg2) {
		return this.field_16497 != null && arg.field_16493.field_16496.contains(this.field_16497) ? this.field_16497 : arg2;
	}

	private void method_15039() {
		if (this.advancement.getDisplay() != null) {
			this.advancement.getDisplay().method_15003((float)this.field_16499, this.field_16500);
		}

		if (!this.field_16496.isEmpty()) {
			for (class_3361 lv : this.field_16496) {
				lv.method_15039();
			}
		}
	}

	public static void positionChildren(SimpleAdvancement advancement) {
		if (advancement.getDisplay() == null) {
			throw new IllegalArgumentException("Can't position children of an invisible root!");
		} else {
			class_3361 lv = new class_3361(advancement, null, null, 1, 0);
			lv.method_15028();
			float f = lv.method_15030(0.0F, 0, lv.field_16500);
			if (f < 0.0F) {
				lv.method_15029(-f);
			}

			lv.method_15039();
		}
	}
}
