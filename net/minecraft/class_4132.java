package net.minecraft;

import java.io.Closeable;
import javax.annotation.Nullable;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class class_4132 extends AbstractTexture implements Closeable {
	private final Identifier field_20114;
	private final boolean field_20115;
	private final class_4132.class_4133 field_20116;

	public class_4132(Identifier identifier, boolean bl) {
		this.field_20114 = identifier;
		this.field_20115 = bl;
		this.field_20116 = new class_4132.class_4133(0, 0, 256, 256);
		TextureUtil.method_19531(bl ? class_4277.class_4279.RGBA : class_4277.class_4279.INTENSITY, this.getGlId(), 256, 256);
	}

	@Override
	public void load(ResourceManager manager) {
	}

	public void close() {
		this.clearGlId();
	}

	@Nullable
	public class_4136 method_18470(class_4135 arg) {
		if (arg.method_18475() != this.field_20115) {
			return null;
		} else {
			class_4132.class_4133 lv = this.field_20116.method_18471(arg);
			if (lv != null) {
				this.method_19530();
				arg.method_18473(lv.field_20117, lv.field_20118);
				float f = 256.0F;
				float g = 256.0F;
				float h = 0.01F;
				return new class_4136(
					this.field_20114,
					((float)lv.field_20117 + 0.01F) / 256.0F,
					((float)lv.field_20117 - 0.01F + (float)arg.method_18472()) / 256.0F,
					((float)lv.field_20118 + 0.01F) / 256.0F,
					((float)lv.field_20118 - 0.01F + (float)arg.method_18474()) / 256.0F,
					arg.method_18477(),
					arg.method_18478(),
					arg.method_18479(),
					arg.method_18480()
				);
			} else {
				return null;
			}
		}
	}

	public Identifier method_18469() {
		return this.field_20114;
	}

	static class class_4133 {
		final int field_20117;
		final int field_20118;
		final int field_20119;
		final int field_20120;
		class_4132.class_4133 field_20121;
		class_4132.class_4133 field_20122;
		boolean field_20123;

		private class_4133(int i, int j, int k, int l) {
			this.field_20117 = i;
			this.field_20118 = j;
			this.field_20119 = k;
			this.field_20120 = l;
		}

		@Nullable
		class_4132.class_4133 method_18471(class_4135 arg) {
			if (this.field_20121 != null && this.field_20122 != null) {
				class_4132.class_4133 lv = this.field_20121.method_18471(arg);
				if (lv == null) {
					lv = this.field_20122.method_18471(arg);
				}

				return lv;
			} else if (this.field_20123) {
				return null;
			} else {
				int i = arg.method_18472();
				int j = arg.method_18474();
				if (i > this.field_20119 || j > this.field_20120) {
					return null;
				} else if (i == this.field_20119 && j == this.field_20120) {
					this.field_20123 = true;
					return this;
				} else {
					int k = this.field_20119 - i;
					int l = this.field_20120 - j;
					if (k > l) {
						this.field_20121 = new class_4132.class_4133(this.field_20117, this.field_20118, i, this.field_20120);
						this.field_20122 = new class_4132.class_4133(this.field_20117 + i + 1, this.field_20118, this.field_20119 - i - 1, this.field_20120);
					} else {
						this.field_20121 = new class_4132.class_4133(this.field_20117, this.field_20118, this.field_20119, j);
						this.field_20122 = new class_4132.class_4133(this.field_20117, this.field_20118 + j + 1, this.field_20119, this.field_20120 - j - 1);
					}

					return this.field_20121.method_18471(arg);
				}
			}
		}
	}
}
