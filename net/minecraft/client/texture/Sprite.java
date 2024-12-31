package net.minecraft.client.texture;

import com.google.common.collect.Lists;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.class_2901;
import net.minecraft.client.resource.AnimationMetadata;
import net.minecraft.client.resource.metadata.AnimationFrameResourceMetadata;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;

public class Sprite {
	private final String name;
	protected List<int[][]> frames = Lists.newArrayList();
	protected int[][] field_11198;
	private AnimationMetadata meta;
	protected boolean rotation;
	protected int x;
	protected int y;
	protected int width;
	protected int height;
	private float uMin;
	private float uMax;
	private float vMin;
	private float vMax;
	protected int frameIndex;
	protected int frameTicks;

	protected Sprite(String string) {
		this.name = string;
	}

	protected static Sprite get(Identifier ide) {
		return new Sprite(ide.toString());
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

	public void copyData(Sprite sprite) {
		this.x = sprite.x;
		this.y = sprite.y;
		this.width = sprite.width;
		this.height = sprite.height;
		this.rotation = sprite.rotation;
		this.uMin = sprite.uMin;
		this.uMax = sprite.uMax;
		this.vMin = sprite.vMin;
		this.vMax = sprite.vMax;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
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

	public String getName() {
		return this.name;
	}

	public void update() {
		this.frameTicks++;
		if (this.frameTicks >= this.meta.getTime(this.frameIndex)) {
			int i = this.meta.getIndex(this.frameIndex);
			int j = this.meta.getMetadataListSize() == 0 ? this.frames.size() : this.meta.getMetadataListSize();
			this.frameIndex = (this.frameIndex + 1) % j;
			this.frameTicks = 0;
			int k = this.meta.getIndex(this.frameIndex);
			if (i != k && k >= 0 && k < this.frames.size()) {
				TextureUtil.method_7027((int[][])this.frames.get(k), this.width, this.height, this.x, this.y, false, false);
			}
		} else if (this.meta.shouldInterpolate()) {
			this.method_10321();
		}
	}

	private void method_10321() {
		double d = 1.0 - (double)this.frameTicks / (double)this.meta.getTime(this.frameIndex);
		int i = this.meta.getIndex(this.frameIndex);
		int j = this.meta.getMetadataListSize() == 0 ? this.frames.size() : this.meta.getMetadataListSize();
		int k = this.meta.getIndex((this.frameIndex + 1) % j);
		if (i != k && k >= 0 && k < this.frames.size()) {
			int[][] is = (int[][])this.frames.get(i);
			int[][] js = (int[][])this.frames.get(k);
			if (this.field_11198 == null || this.field_11198.length != is.length) {
				this.field_11198 = new int[is.length][];
			}

			for (int l = 0; l < is.length; l++) {
				if (this.field_11198[l] == null) {
					this.field_11198[l] = new int[is[l].length];
				}

				if (l < js.length && js[l].length == is[l].length) {
					for (int m = 0; m < is[l].length; m++) {
						int n = is[l][m];
						int o = js[l][m];
						int p = this.method_12489(d, n >> 16 & 0xFF, o >> 16 & 0xFF);
						int q = this.method_12489(d, n >> 8 & 0xFF, o >> 8 & 0xFF);
						int r = this.method_12489(d, n & 0xFF, o & 0xFF);
						this.field_11198[l][m] = n & 0xFF000000 | p << 16 | q << 8 | r;
					}
				}
			}

			TextureUtil.method_7027(this.field_11198, this.width, this.height, this.x, this.y, false, false);
		}
	}

	private int method_12489(double d, int i, int j) {
		return (int)(d * (double)i + (1.0 - d) * (double)j);
	}

	public int[][] method_5831(int i) {
		return (int[][])this.frames.get(i);
	}

	public int getSize() {
		return this.frames.size();
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void method_12491(class_2901 arg, boolean bl) throws IOException {
		this.nullify();
		this.width = arg.field_13651;
		this.height = arg.field_13652;
		if (bl) {
			this.height = this.width;
		} else if (arg.field_13652 != arg.field_13651) {
			throw new RuntimeException("broken aspect ratio and not an animation");
		}
	}

	public void method_12492(Resource resource, int i) throws IOException {
		BufferedImage bufferedImage = TextureUtil.create(resource.getInputStream());
		AnimationMetadata animationMetadata = resource.getMetadata("animation");
		int[][] is = new int[i][];
		is[0] = new int[bufferedImage.getWidth() * bufferedImage.getHeight()];
		bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), is[0], 0, bufferedImage.getWidth());
		if (animationMetadata == null) {
			this.frames.add(is);
		} else {
			int j = bufferedImage.getHeight() / this.width;
			if (animationMetadata.getMetadataListSize() > 0) {
				for (int k : animationMetadata.getIndices()) {
					if (k >= j) {
						throw new RuntimeException("invalid frameindex " + k);
					}

					this.method_5839(k);
					this.frames.set(k, method_7012(is, this.width, this.width, k));
				}

				this.meta = animationMetadata;
			} else {
				List<AnimationFrameResourceMetadata> list = Lists.newArrayList();

				for (int l = 0; l < j; l++) {
					this.frames.add(method_7012(is, this.width, this.width, l));
					list.add(new AnimationFrameResourceMetadata(l, -1));
				}

				this.meta = new AnimationMetadata(list, this.width, this.height, animationMetadata.getTime(), animationMetadata.shouldInterpolate());
			}
		}
	}

	public void method_7013(int i) {
		List<int[][]> list = Lists.newArrayList();

		for (int j = 0; j < this.frames.size(); j++) {
			final int[][] is = (int[][])this.frames.get(j);
			if (is != null) {
				try {
					list.add(TextureUtil.method_7021(i, this.width, is));
				} catch (Throwable var8) {
					CrashReport crashReport = CrashReport.create(var8, "Generating mipmaps for frame");
					CrashReportSection crashReportSection = crashReport.addElement("Frame being iterated");
					crashReportSection.add("Frame index", j);
					crashReportSection.add("Frame sizes", new CrashCallable<String>() {
						public String call() throws Exception {
							StringBuilder stringBuilder = new StringBuilder();

							for (int[] is : is) {
								if (stringBuilder.length() > 0) {
									stringBuilder.append(", ");
								}

								stringBuilder.append(is == null ? "null" : is.length);
							}

							return stringBuilder.toString();
						}
					});
					throw new CrashException(crashReport);
				}
			}
		}

		this.setFrames(list);
	}

	private void method_5839(int i) {
		if (this.frames.size() <= i) {
			for (int j = this.frames.size(); j <= i; j++) {
				this.frames.add(null);
			}
		}
	}

	private static int[][] method_7012(int[][] is, int i, int j, int k) {
		int[][] js = new int[is.length][];

		for (int l = 0; l < is.length; l++) {
			int[] ks = is[l];
			if (ks != null) {
				js[l] = new int[(i >> l) * (j >> l)];
				System.arraycopy(ks, k * js[l].length, js[l], 0, js[l].length);
			}
		}

		return js;
	}

	public void clearFrames() {
		this.frames.clear();
	}

	public boolean hasMeta() {
		return this.meta != null;
	}

	public void setFrames(List<int[][]> frames) {
		this.frames = frames;
	}

	private void nullify() {
		this.meta = null;
		this.setFrames(Lists.newArrayList());
		this.frameIndex = 0;
		this.frameTicks = 0;
	}

	public String toString() {
		return "TextureAtlasSprite{name='"
			+ this.name
			+ '\''
			+ ", frameCount="
			+ this.frames.size()
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
}
