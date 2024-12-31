package net.minecraft;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.ItemStack;

enum class_3269 {
	ABOVE(0, 0, 28, 32, 8),
	BELOW(84, 0, 28, 32, 8),
	LEFT(0, 64, 32, 28, 5),
	RIGHT(96, 64, 32, 28, 5);

	private final int field_15977;
	private final int field_15978;
	private final int field_15979;
	private final int field_15980;
	private final int field_15981;

	private class_3269(int j, int k, int l, int m, int n) {
		this.field_15977 = j;
		this.field_15978 = k;
		this.field_15979 = l;
		this.field_15980 = m;
		this.field_15981 = n;
	}

	public int method_14519() {
		return this.field_15981;
	}

	public void method_14523(DrawableHelper drawableHelper, int i, int j, boolean bl, int k) {
		int l = this.field_15977;
		if (k > 0) {
			l += this.field_15979;
		}

		if (k == this.field_15981 - 1) {
			l += this.field_15979;
		}

		int m = bl ? this.field_15978 + this.field_15980 : this.field_15978;
		drawableHelper.drawTexture(i + this.method_14520(k), j + this.method_14524(k), l, m, this.field_15979, this.field_15980);
	}

	public void method_14522(int i, int j, int k, HeldItemRenderer heldItemRenderer, ItemStack itemStack) {
		int l = i + this.method_14520(k);
		int m = j + this.method_14524(k);
		switch (this) {
			case ABOVE:
				l += 6;
				m += 9;
				break;
			case BELOW:
				l += 6;
				m += 6;
				break;
			case LEFT:
				l += 10;
				m += 5;
				break;
			case RIGHT:
				l += 6;
				m += 5;
		}

		heldItemRenderer.method_19374(null, itemStack, l, m);
	}

	public int method_14520(int i) {
		switch (this) {
			case ABOVE:
				return (this.field_15979 + 4) * i;
			case BELOW:
				return (this.field_15979 + 4) * i;
			case LEFT:
				return -this.field_15979 + 4;
			case RIGHT:
				return 248;
			default:
				throw new UnsupportedOperationException("Don't know what this tab type is!" + this);
		}
	}

	public int method_14524(int i) {
		switch (this) {
			case ABOVE:
				return -this.field_15980 + 4;
			case BELOW:
				return 136;
			case LEFT:
				return this.field_15980 * i;
			case RIGHT:
				return this.field_15980 * i;
			default:
				throw new UnsupportedOperationException("Don't know what this tab type is!" + this);
		}
	}

	public boolean method_14521(int i, int j, int k, double d, double e) {
		int l = i + this.method_14520(k);
		int m = j + this.method_14524(k);
		return d > (double)l && d < (double)(l + this.field_15979) && e > (double)m && e < (double)(m + this.field_15980);
	}
}
