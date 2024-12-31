package net.minecraft.client.texture;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.render.SpriteTexturedVertexConsumer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.resource.metadata.AnimationFrameResourceMetadata;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;

public class Sprite implements AutoCloseable {
	private final SpriteAtlasTexture atlas;
	private final Sprite.Info info;
	private final AnimationResourceMetadata animationMetadata;
	protected final NativeImage[] images;
	private final int[] frameXs;
	private final int[] frameYs;
	@Nullable
	private final Sprite.Interpolation interpolation;
	private final int x;
	private final int y;
	private final float uMin;
	private final float uMax;
	private final float vMin;
	private final float vMax;
	private int frameIndex;
	private int frameTicks;

	protected Sprite(SpriteAtlasTexture spriteAtlasTexture, Sprite.Info info, int i, int j, int k, int l, int m, NativeImage nativeImage) {
		this.atlas = spriteAtlasTexture;
		AnimationResourceMetadata animationResourceMetadata = info.animationData;
		int n = info.width;
		int o = info.height;
		this.x = l;
		this.y = m;
		this.uMin = (float)l / (float)j;
		this.uMax = (float)(l + n) / (float)j;
		this.vMin = (float)m / (float)k;
		this.vMax = (float)(m + o) / (float)k;
		int p = nativeImage.getWidth() / animationResourceMetadata.getWidth(n);
		int q = nativeImage.getHeight() / animationResourceMetadata.getHeight(o);
		if (animationResourceMetadata.getFrameCount() > 0) {
			int r = (Integer)animationResourceMetadata.getFrameIndexSet().stream().max(Integer::compareTo).get() + 1;
			this.frameXs = new int[r];
			this.frameYs = new int[r];
			Arrays.fill(this.frameXs, -1);
			Arrays.fill(this.frameYs, -1);

			for (int s : animationResourceMetadata.getFrameIndexSet()) {
				if (s >= p * q) {
					throw new RuntimeException("invalid frameindex " + s);
				}

				int t = s / p;
				int u = s % p;
				this.frameXs[s] = u;
				this.frameYs[s] = t;
			}
		} else {
			List<AnimationFrameResourceMetadata> list = Lists.newArrayList();
			int v = p * q;
			this.frameXs = new int[v];
			this.frameYs = new int[v];

			for (int w = 0; w < q; w++) {
				for (int x = 0; x < p; x++) {
					int y = w * p + x;
					this.frameXs[y] = x;
					this.frameYs[y] = w;
					list.add(new AnimationFrameResourceMetadata(y, -1));
				}
			}

			animationResourceMetadata = new AnimationResourceMetadata(
				list, n, o, animationResourceMetadata.getDefaultFrameTime(), animationResourceMetadata.shouldInterpolate()
			);
		}

		this.info = new Sprite.Info(info.id, n, o, animationResourceMetadata);
		this.animationMetadata = animationResourceMetadata;

		try {
			try {
				this.images = MipmapHelper.getMipmapLevelsImages(nativeImage, i);
			} catch (Throwable var19) {
				CrashReport crashReport = CrashReport.create(var19, "Generating mipmaps for frame");
				CrashReportSection crashReportSection = crashReport.addElement("Frame being iterated");
				crashReportSection.add("First frame", (CrashCallable<String>)(() -> {
					StringBuilder stringBuilder = new StringBuilder();
					if (stringBuilder.length() > 0) {
						stringBuilder.append(", ");
					}

					stringBuilder.append(nativeImage.getWidth()).append("x").append(nativeImage.getHeight());
					return stringBuilder.toString();
				}));
				throw new CrashException(crashReport);
			}
		} catch (Throwable var20) {
			CrashReport crashReport2 = CrashReport.create(var20, "Applying mipmap");
			CrashReportSection crashReportSection2 = crashReport2.addElement("Sprite being mipmapped");
			crashReportSection2.add("Sprite name", (CrashCallable<String>)(() -> this.getId().toString()));
			crashReportSection2.add("Sprite size", (CrashCallable<String>)(() -> this.getWidth() + " x " + this.getHeight()));
			crashReportSection2.add("Sprite frames", (CrashCallable<String>)(() -> this.getFrameCount() + " frames"));
			crashReportSection2.add("Mipmap levels", i);
			throw new CrashException(crashReport2);
		}

		if (animationResourceMetadata.shouldInterpolate()) {
			this.interpolation = new Sprite.Interpolation(info, i);
		} else {
			this.interpolation = null;
		}
	}

	private void upload(int i) {
		int j = this.frameXs[i] * this.info.width;
		int k = this.frameYs[i] * this.info.height;
		this.upload(j, k, this.images);
	}

	private void upload(int i, int j, NativeImage[] nativeImages) {
		for (int k = 0; k < this.images.length; k++) {
			nativeImages[k].upload(k, this.x >> k, this.y >> k, i >> k, j >> k, this.info.width >> k, this.info.height >> k, this.images.length > 1, false);
		}
	}

	public int getWidth() {
		return this.info.width;
	}

	public int getHeight() {
		return this.info.height;
	}

	public float getMinU() {
		return this.uMin;
	}

	public float getMaxU() {
		return this.uMax;
	}

	public float getFrameU(double d) {
		float f = this.uMax - this.uMin;
		return this.uMin + f * (float)d / 16.0F;
	}

	public float getMinV() {
		return this.vMin;
	}

	public float getMaxV() {
		return this.vMax;
	}

	public float getFrameV(double d) {
		float f = this.vMax - this.vMin;
		return this.vMin + f * (float)d / 16.0F;
	}

	public Identifier getId() {
		return this.info.id;
	}

	public SpriteAtlasTexture getAtlas() {
		return this.atlas;
	}

	public int getFrameCount() {
		return this.frameXs.length;
	}

	public void close() {
		for (NativeImage nativeImage : this.images) {
			if (nativeImage != null) {
				nativeImage.close();
			}
		}

		if (this.interpolation != null) {
			this.interpolation.close();
		}
	}

	public String toString() {
		int i = this.frameXs.length;
		return "TextureAtlasSprite{name='"
			+ this.info.id
			+ '\''
			+ ", frameCount="
			+ i
			+ ", x="
			+ this.x
			+ ", y="
			+ this.y
			+ ", height="
			+ this.info.height
			+ ", width="
			+ this.info.width
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

	public boolean isPixelTransparent(int i, int j, int k) {
		return (this.images[0].getPixelRgba(j + this.frameXs[i] * this.info.width, k + this.frameYs[i] * this.info.height) >> 24 & 0xFF) == 0;
	}

	public void upload() {
		this.upload(0);
	}

	private float getFrameDeltaFactor() {
		float f = (float)this.info.width / (this.uMax - this.uMin);
		float g = (float)this.info.height / (this.vMax - this.vMin);
		return Math.max(g, f);
	}

	public float getAnimationFrameDelta() {
		return 4.0F / this.getFrameDeltaFactor();
	}

	public void tickAnimation() {
		this.frameTicks++;
		if (this.frameTicks >= this.animationMetadata.getFrameTime(this.frameIndex)) {
			int i = this.animationMetadata.getFrameIndex(this.frameIndex);
			int j = this.animationMetadata.getFrameCount() == 0 ? this.getFrameCount() : this.animationMetadata.getFrameCount();
			this.frameIndex = (this.frameIndex + 1) % j;
			this.frameTicks = 0;
			int k = this.animationMetadata.getFrameIndex(this.frameIndex);
			if (i != k && k >= 0 && k < this.getFrameCount()) {
				this.upload(k);
			}
		} else if (this.interpolation != null) {
			if (!RenderSystem.isOnRenderThread()) {
				RenderSystem.recordRenderCall(() -> interpolation.method_24128());
			} else {
				this.interpolation.method_24128();
			}
		}
	}

	public boolean isAnimated() {
		return this.animationMetadata.getFrameCount() > 1;
	}

	public VertexConsumer getTextureSpecificVertexConsumer(VertexConsumer vertexConsumer) {
		return new SpriteTexturedVertexConsumer(vertexConsumer, this);
	}

	public static final class Info {
		private final Identifier id;
		private final int width;
		private final int height;
		private final AnimationResourceMetadata animationData;

		public Info(Identifier identifier, int i, int j, AnimationResourceMetadata animationResourceMetadata) {
			this.id = identifier;
			this.width = i;
			this.height = j;
			this.animationData = animationResourceMetadata;
		}

		public Identifier getId() {
			return this.id;
		}

		public int getWidth() {
			return this.width;
		}

		public int getHeight() {
			return this.height;
		}
	}

	final class Interpolation implements AutoCloseable {
		private final NativeImage[] images;

		private Interpolation(Sprite.Info info, int i) {
			this.images = new NativeImage[i + 1];

			for (int j = 0; j < this.images.length; j++) {
				int k = info.width >> j;
				int l = info.height >> j;
				if (this.images[j] == null) {
					this.images[j] = new NativeImage(k, l, false);
				}
			}
		}

		private void method_24128() {
			double d = 1.0 - (double)Sprite.this.frameTicks / (double)Sprite.this.animationMetadata.getFrameTime(Sprite.this.frameIndex);
			int i = Sprite.this.animationMetadata.getFrameIndex(Sprite.this.frameIndex);
			int j = Sprite.this.animationMetadata.getFrameCount() == 0 ? Sprite.this.getFrameCount() : Sprite.this.animationMetadata.getFrameCount();
			int k = Sprite.this.animationMetadata.getFrameIndex((Sprite.this.frameIndex + 1) % j);
			if (i != k && k >= 0 && k < Sprite.this.getFrameCount()) {
				for (int l = 0; l < this.images.length; l++) {
					int m = Sprite.this.info.width >> l;
					int n = Sprite.this.info.height >> l;

					for (int o = 0; o < n; o++) {
						for (int p = 0; p < m; p++) {
							int q = this.method_24130(i, l, p, o);
							int r = this.method_24130(k, l, p, o);
							int s = this.method_24129(d, q >> 16 & 0xFF, r >> 16 & 0xFF);
							int t = this.method_24129(d, q >> 8 & 0xFF, r >> 8 & 0xFF);
							int u = this.method_24129(d, q & 0xFF, r & 0xFF);
							this.images[l].setPixelRgba(p, o, q & 0xFF000000 | s << 16 | t << 8 | u);
						}
					}
				}

				Sprite.this.upload(0, 0, this.images);
			}
		}

		private int method_24130(int i, int j, int k, int l) {
			return Sprite.this.images[j]
				.getPixelRgba(k + (Sprite.this.frameXs[i] * Sprite.this.info.width >> j), l + (Sprite.this.frameYs[i] * Sprite.this.info.height >> j));
		}

		private int method_24129(double d, int i, int j) {
			return (int)(d * (double)i + (1.0 - d) * (double)j);
		}

		public void close() {
			for (NativeImage nativeImage : this.images) {
				if (nativeImage != null) {
					nativeImage.close();
				}
			}
		}
	}
}
