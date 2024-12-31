package net.minecraft.client.texture;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.class_4277;
import net.minecraft.client.class_2901;
import net.minecraft.client.resource.AnimationMetadata;
import net.minecraft.client.resource.metadata.AnimationFrameResourceMetadata;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;

public class Sprite {
	private final Identifier field_21032;
	protected final int width;
	protected final int height;
	protected class_4277[] field_21028;
	@Nullable
	protected int[] field_21029;
	@Nullable
	protected int[] field_21030;
	protected class_4277[] field_21031;
	private AnimationMetadata meta;
	protected boolean rotation;
	protected int x;
	protected int y;
	private float uMin;
	private float uMax;
	private float vMin;
	private float vMax;
	protected int frameIndex;
	protected int frameTicks;
	private static final int[] field_21033 = new int[4];
	private static final float[] field_21034 = Util.make(new float[256], fs -> {
		for (int i = 0; i < fs.length; i++) {
			fs[i] = (float)Math.pow((double)((float)i / 255.0F), 2.2);
		}
	});

	protected Sprite(Identifier identifier, int i, int j) {
		this.field_21032 = identifier;
		this.width = i;
		this.height = j;
	}

	protected Sprite(Identifier identifier, class_2901 arg, @Nullable AnimationMetadata animationMetadata) {
		this.field_21032 = identifier;
		if (animationMetadata != null) {
			int i = Math.min(arg.field_13651, arg.field_13652);
			this.height = this.width = i;
		} else {
			if (arg.field_13652 != arg.field_13651) {
				throw new RuntimeException("broken aspect ratio and not an animation");
			}

			this.width = arg.field_13651;
			this.height = arg.field_13652;
		}

		this.meta = animationMetadata;
	}

	private void method_19524(int i) {
		class_4277[] lvs = new class_4277[i + 1];
		lvs[0] = this.field_21028[0];
		if (i > 0) {
			boolean bl = false;

			label71:
			for (int j = 0; j < this.field_21028[0].method_19458(); j++) {
				for (int k = 0; k < this.field_21028[0].method_19478(); k++) {
					if (this.field_21028[0].method_19459(j, k) >> 24 == 0) {
						bl = true;
						break label71;
					}
				}
			}

			for (int l = 1; l <= i; l++) {
				if (this.field_21028.length > l && this.field_21028[l] != null) {
					lvs[l] = this.field_21028[l];
				} else {
					class_4277 lv = lvs[l - 1];
					class_4277 lv2 = new class_4277(lv.method_19458() >> 1, lv.method_19478() >> 1, false);
					int m = lv2.method_19458();
					int n = lv2.method_19478();

					for (int o = 0; o < m; o++) {
						for (int p = 0; p < n; p++) {
							lv2.method_19460(
								o,
								p,
								method_19525(
									lv.method_19459(o * 2 + 0, p * 2 + 0),
									lv.method_19459(o * 2 + 1, p * 2 + 0),
									lv.method_19459(o * 2 + 0, p * 2 + 1),
									lv.method_19459(o * 2 + 1, p * 2 + 1),
									bl
								)
							);
						}
					}

					lvs[l] = lv2;
				}
			}

			for (int q = i + 1; q < this.field_21028.length; q++) {
				if (this.field_21028[q] != null) {
					this.field_21028[q].close();
				}
			}
		}

		this.field_21028 = lvs;
	}

	private static int method_19525(int i, int j, int k, int l, boolean bl) {
		if (bl) {
			field_21033[0] = i;
			field_21033[1] = j;
			field_21033[2] = k;
			field_21033[3] = l;
			float f = 0.0F;
			float g = 0.0F;
			float h = 0.0F;
			float m = 0.0F;

			for (int n = 0; n < 4; n++) {
				if (field_21033[n] >> 24 != 0) {
					f += method_19526(field_21033[n] >> 24);
					g += method_19526(field_21033[n] >> 16);
					h += method_19526(field_21033[n] >> 8);
					m += method_19526(field_21033[n] >> 0);
				}
			}

			f /= 4.0F;
			g /= 4.0F;
			h /= 4.0F;
			m /= 4.0F;
			int o = (int)(Math.pow((double)f, 0.45454545454545453) * 255.0);
			int p = (int)(Math.pow((double)g, 0.45454545454545453) * 255.0);
			int q = (int)(Math.pow((double)h, 0.45454545454545453) * 255.0);
			int r = (int)(Math.pow((double)m, 0.45454545454545453) * 255.0);
			if (o < 96) {
				o = 0;
			}

			return o << 24 | p << 16 | q << 8 | r;
		} else {
			int s = method_19519(i, j, k, l, 24);
			int t = method_19519(i, j, k, l, 16);
			int u = method_19519(i, j, k, l, 8);
			int v = method_19519(i, j, k, l, 0);
			return s << 24 | t << 16 | u << 8 | v;
		}
	}

	private static int method_19519(int i, int j, int k, int l, int m) {
		float f = method_19526(i >> m);
		float g = method_19526(j >> m);
		float h = method_19526(k >> m);
		float n = method_19526(l >> m);
		float o = (float)((double)((float)Math.pow((double)(f + g + h + n) * 0.25, 0.45454545454545453)));
		return (int)((double)o * 255.0);
	}

	private static float method_19526(int i) {
		return field_21034[i & 0xFF];
	}

	private void method_19527(int i) {
		int j = 0;
		int k = 0;
		if (this.field_21029 != null) {
			j = this.field_21029[i] * this.width;
			k = this.field_21030[i] * this.height;
		}

		this.method_19520(j, k, this.field_21028);
	}

	private void method_19520(int i, int j, class_4277[] args) {
		for (int k = 0; k < this.field_21028.length; k++) {
			args[k].method_19462(k, this.x >> k, this.y >> k, i >> k, j >> k, this.width >> k, this.height >> k, this.field_21028.length > 1);
		}
	}

	public void reInitialize(int u, int v, int x, int y, boolean rotation) {
		this.x = x;
		this.y = y;
		this.rotation = rotation;
		float f = (float)(0.01F / (double)u);
		float g = (float)(0.01F / (double)v);
		this.uMin = (float)x / (float)((double)u) + f;
		this.uMax = (float)(x + this.width) / (float)((double)u) - f;
		this.vMin = (float)y / (float)v + g;
		this.vMax = (float)(y + this.height) / (float)v - g;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public float getMinU() {
		return this.uMin;
	}

	public float getMaxU() {
		return this.uMax;
	}

	public float getFrameU(double frame) {
		float f = this.uMax - this.uMin;
		return this.uMin + f * (float)frame / 16.0F;
	}

	public float method_12490(float f) {
		float g = this.uMax - this.uMin;
		return (f - this.uMin) / g * 16.0F;
	}

	public float getMinV() {
		return this.vMin;
	}

	public float getMaxV() {
		return this.vMax;
	}

	public float getFrameV(double frame) {
		float f = this.vMax - this.vMin;
		return this.vMin + f * (float)frame / 16.0F;
	}

	public float method_12493(float f) {
		float g = this.vMax - this.vMin;
		return (f - this.vMin) / g * 16.0F;
	}

	public Identifier method_5348() {
		return this.field_21032;
	}

	public void update() {
		this.frameTicks++;
		if (this.frameTicks >= this.meta.getTime(this.frameIndex)) {
			int i = this.meta.getIndex(this.frameIndex);
			int j = this.meta.getMetadataListSize() == 0 ? this.getSize() : this.meta.getMetadataListSize();
			this.frameIndex = (this.frameIndex + 1) % j;
			this.frameTicks = 0;
			int k = this.meta.getIndex(this.frameIndex);
			if (i != k && k >= 0 && k < this.getSize()) {
				this.method_19527(k);
			}
		} else if (this.meta.shouldInterpolate()) {
			this.method_10321();
		}
	}

	private void method_10321() {
		double d = 1.0 - (double)this.frameTicks / (double)this.meta.getTime(this.frameIndex);
		int i = this.meta.getIndex(this.frameIndex);
		int j = this.meta.getMetadataListSize() == 0 ? this.getSize() : this.meta.getMetadataListSize();
		int k = this.meta.getIndex((this.frameIndex + 1) % j);
		if (i != k && k >= 0 && k < this.getSize()) {
			if (this.field_21031 == null || this.field_21031.length != this.field_21028.length) {
				if (this.field_21031 != null) {
					for (class_4277 lv : this.field_21031) {
						if (lv != null) {
							lv.close();
						}
					}
				}

				this.field_21031 = new class_4277[this.field_21028.length];
			}

			for (int l = 0; l < this.field_21028.length; l++) {
				int m = this.width >> l;
				int n = this.height >> l;
				if (this.field_21031[l] == null) {
					this.field_21031[l] = new class_4277(m, n, false);
				}

				for (int o = 0; o < n; o++) {
					for (int p = 0; p < m; p++) {
						int q = this.method_19518(i, l, p, o);
						int r = this.method_19518(k, l, p, o);
						int s = this.method_12489(d, q >> 16 & 0xFF, r >> 16 & 0xFF);
						int t = this.method_12489(d, q >> 8 & 0xFF, r >> 8 & 0xFF);
						int u = this.method_12489(d, q & 0xFF, r & 0xFF);
						this.field_21031[l].method_19460(p, o, q & 0xFF000000 | s << 16 | t << 8 | u);
					}
				}
			}

			this.method_19520(0, 0, this.field_21031);
		}
	}

	private int method_12489(double d, int i, int j) {
		return (int)(d * (double)i + (1.0 - d) * (double)j);
	}

	public int getSize() {
		return this.field_21029 == null ? 0 : this.field_21029.length;
	}

	public void method_19521(Resource resource, int i) throws IOException {
		class_4277 lv = class_4277.method_19472(resource.getInputStream());
		this.field_21028 = new class_4277[i];
		this.field_21028[0] = lv;
		int j;
		if (this.meta != null && this.meta.getWidth() != -1) {
			j = lv.method_19458() / this.meta.getWidth();
		} else {
			j = lv.method_19458() / this.width;
		}

		int l;
		if (this.meta != null && this.meta.getHeight() != -1) {
			l = lv.method_19478() / this.meta.getHeight();
		} else {
			l = lv.method_19478() / this.height;
		}

		if (this.meta != null && this.meta.getMetadataListSize() > 0) {
			int n = (Integer)this.meta.getIndices().stream().max(Integer::compareTo).get() + 1;
			this.field_21029 = new int[n];
			this.field_21030 = new int[n];
			Arrays.fill(this.field_21029, -1);
			Arrays.fill(this.field_21030, -1);

			for (int o : this.meta.getIndices()) {
				if (o >= j * l) {
					throw new RuntimeException("invalid frameindex " + o);
				}

				int p = o / j;
				int q = o % j;
				this.field_21029[o] = q;
				this.field_21030[o] = p;
			}
		} else {
			List<AnimationFrameResourceMetadata> list = Lists.newArrayList();
			int r = j * l;
			this.field_21029 = new int[r];
			this.field_21030 = new int[r];

			for (int s = 0; s < l; s++) {
				for (int t = 0; t < j; t++) {
					int u = s * j + t;
					this.field_21029[u] = t;
					this.field_21030[u] = s;
					list.add(new AnimationFrameResourceMetadata(u, -1));
				}
			}

			int v = 1;
			boolean bl = false;
			if (this.meta != null) {
				v = this.meta.getTime();
				bl = this.meta.shouldInterpolate();
			}

			this.meta = new AnimationMetadata(list, this.width, this.height, v, bl);
		}
	}

	public void method_7013(int i) {
		try {
			this.method_19524(i);
		} catch (Throwable var5) {
			CrashReport crashReport = CrashReport.create(var5, "Generating mipmaps for frame");
			CrashReportSection crashReportSection = crashReport.addElement("Frame being iterated");
			crashReportSection.add("Frame sizes", (CrashCallable<String>)(() -> {
				StringBuilder stringBuilder = new StringBuilder();

				for (class_4277 lv : this.field_21028) {
					if (stringBuilder.length() > 0) {
						stringBuilder.append(", ");
					}

					stringBuilder.append(lv == null ? "null" : lv.method_19458() + "x" + lv.method_19478());
				}

				return stringBuilder.toString();
			}));
			throw new CrashException(crashReport);
		}
	}

	public void clearFrames() {
		if (this.field_21028 != null) {
			for (class_4277 lv : this.field_21028) {
				if (lv != null) {
					lv.close();
				}
			}
		}

		this.field_21028 = null;
		if (this.field_21031 != null) {
			for (class_4277 lv2 : this.field_21031) {
				if (lv2 != null) {
					lv2.close();
				}
			}
		}

		this.field_21031 = null;
	}

	public boolean hasMeta() {
		return this.meta != null && this.meta.getMetadataListSize() > 1;
	}

	public String toString() {
		int i = this.field_21029 == null ? 0 : this.field_21029.length;
		return "TextureAtlasSprite{name='"
			+ this.field_21032
			+ '\''
			+ ", frameCount="
			+ i
			+ ", rotated="
			+ this.rotation
			+ ", x="
			+ this.x
			+ ", y="
			+ this.y
			+ ", height="
			+ this.height
			+ ", width="
			+ this.width
			+ ", u0="
			+ this.uMin
			+ ", u1="
			+ this.uMax
			+ ", v0="
			+ this.vMin
			+ ", v1="
			+ this.vMax
			+ '}';
	}

	private int method_19518(int i, int j, int k, int l) {
		return this.field_21028[j].method_19459(k + (this.field_21029[i] * this.width >> j), l + (this.field_21030[i] * this.height >> j));
	}

	public boolean method_19517(int i, int j, int k) {
		return (this.field_21028[0].method_19459(j + this.field_21029[i] * this.width, k + this.field_21030[i] * this.height) >> 24 & 0xFF) == 0;
	}

	public void method_19528() {
		this.method_19527(0);
	}
}
