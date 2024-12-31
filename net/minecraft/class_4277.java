package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.texture.TextureUtil;
import org.apache.commons.io.IOUtils;
import org.lwjgl.stb.STBIWriteCallback;
import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBImageResize;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public final class class_4277 implements AutoCloseable {
	private static final Set<StandardOpenOption> field_20986 = EnumSet.of(
		StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING
	);
	private final class_4277.class_4278 field_20987;
	private final int field_20988;
	private final int field_20989;
	private final boolean field_20990;
	private long field_20991;
	private final int field_20992;

	public class_4277(int i, int j, boolean bl) {
		this(class_4277.class_4278.RGBA, i, j, bl);
	}

	public class_4277(class_4277.class_4278 arg, int i, int j, boolean bl) {
		this.field_20987 = arg;
		this.field_20988 = i;
		this.field_20989 = j;
		this.field_20992 = i * j * arg.method_19487();
		this.field_20990 = false;
		if (bl) {
			this.field_20991 = MemoryUtil.nmemCalloc(1L, (long)this.field_20992);
		} else {
			this.field_20991 = MemoryUtil.nmemAlloc((long)this.field_20992);
		}
	}

	private class_4277(class_4277.class_4278 arg, int i, int j, boolean bl, long l) {
		this.field_20987 = arg;
		this.field_20988 = i;
		this.field_20989 = j;
		this.field_20990 = bl;
		this.field_20991 = l;
		this.field_20992 = i * j * arg.method_19487();
	}

	public String toString() {
		return "NativeImage[" + this.field_20987 + " " + this.field_20988 + "x" + this.field_20989 + "@" + this.field_20991 + (this.field_20990 ? "S" : "N") + "]";
	}

	public static class_4277 method_19472(InputStream inputStream) throws IOException {
		return method_19468(class_4277.class_4278.RGBA, inputStream);
	}

	public static class_4277 method_19468(@Nullable class_4277.class_4278 arg, InputStream inputStream) throws IOException {
		ByteBuffer byteBuffer = null;

		class_4277 var3;
		try {
			byteBuffer = TextureUtil.method_19533(inputStream);
			byteBuffer.rewind();
			var3 = method_19469(arg, byteBuffer);
		} finally {
			MemoryUtil.memFree(byteBuffer);
			IOUtils.closeQuietly(inputStream);
		}

		return var3;
	}

	public static class_4277 method_19473(ByteBuffer byteBuffer) throws IOException {
		return method_19469(class_4277.class_4278.RGBA, byteBuffer);
	}

	public static class_4277 method_19469(@Nullable class_4277.class_4278 arg, ByteBuffer byteBuffer) throws IOException {
		if (arg != null && !arg.method_19498()) {
			throw new UnsupportedOperationException("Don't know how to read format " + arg);
		} else if (MemoryUtil.memAddress(byteBuffer) == 0L) {
			throw new IllegalArgumentException("Invalid buffer");
		} else {
			MemoryStack memoryStack = MemoryStack.stackPush();
			Throwable var3 = null;

			class_4277 var8;
			try {
				IntBuffer intBuffer = memoryStack.mallocInt(1);
				IntBuffer intBuffer2 = memoryStack.mallocInt(1);
				IntBuffer intBuffer3 = memoryStack.mallocInt(1);
				ByteBuffer byteBuffer2 = STBImage.stbi_load_from_memory(byteBuffer, intBuffer, intBuffer2, intBuffer3, arg == null ? 0 : arg.field_20997);
				if (byteBuffer2 == null) {
					throw new IOException("Could not load image: " + STBImage.stbi_failure_reason());
				}

				var8 = new class_4277(
					arg == null ? class_4277.class_4278.method_19491(intBuffer3.get(0)) : arg, intBuffer.get(0), intBuffer2.get(0), true, MemoryUtil.memAddress(byteBuffer2)
				);
			} catch (Throwable var17) {
				var3 = var17;
				throw var17;
			} finally {
				if (memoryStack != null) {
					if (var3 != null) {
						try {
							memoryStack.close();
						} catch (Throwable var16) {
							var3.addSuppressed(var16);
						}
					} else {
						memoryStack.close();
					}
				}
			}

			return var8;
		}
	}

	private static void method_19480(boolean bl) {
		if (bl) {
			GlStateManager.method_12294(3553, 10242, 10496);
			GlStateManager.method_12294(3553, 10243, 10496);
		} else {
			GlStateManager.method_12294(3553, 10242, 10497);
			GlStateManager.method_12294(3553, 10243, 10497);
		}
	}

	private static void method_19477(boolean bl, boolean bl2) {
		if (bl) {
			GlStateManager.method_12294(3553, 10241, bl2 ? 9987 : 9729);
			GlStateManager.method_12294(3553, 10240, 9729);
		} else {
			GlStateManager.method_12294(3553, 10241, bl2 ? 9986 : 9728);
			GlStateManager.method_12294(3553, 10240, 9728);
		}
	}

	private void method_19486() {
		if (this.field_20991 == 0L) {
			throw new IllegalStateException("Image is not allocated.");
		}
	}

	public void close() {
		if (this.field_20991 != 0L) {
			if (this.field_20990) {
				STBImage.nstbi_image_free(this.field_20991);
			} else {
				MemoryUtil.nmemFree(this.field_20991);
			}
		}

		this.field_20991 = 0L;
	}

	public int method_19458() {
		return this.field_20988;
	}

	public int method_19478() {
		return this.field_20989;
	}

	public class_4277.class_4278 method_19481() {
		return this.field_20987;
	}

	public int method_19459(int i, int j) {
		if (this.field_20987 != class_4277.class_4278.RGBA) {
			throw new IllegalArgumentException(String.format("getPixelRGBA only works on RGBA images; have %s", this.field_20987));
		} else if (i <= this.field_20988 && j <= this.field_20989) {
			this.method_19486();
			return MemoryUtil.memIntBuffer(this.field_20991, this.field_20992).get(i + j * this.field_20988);
		} else {
			throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", i, j, this.field_20988, this.field_20989));
		}
	}

	public void method_19460(int i, int j, int k) {
		if (this.field_20987 != class_4277.class_4278.RGBA) {
			throw new IllegalArgumentException(String.format("getPixelRGBA only works on RGBA images; have %s", this.field_20987));
		} else if (i <= this.field_20988 && j <= this.field_20989) {
			this.method_19486();
			MemoryUtil.memIntBuffer(this.field_20991, this.field_20992).put(i + j * this.field_20988, k);
		} else {
			throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", i, j, this.field_20988, this.field_20989));
		}
	}

	public byte method_19483(int i, int j) {
		if (!this.field_20987.method_19496()) {
			throw new IllegalArgumentException(String.format("no luminance or alpha in %s", this.field_20987));
		} else if (i <= this.field_20988 && j <= this.field_20989) {
			return MemoryUtil.memByteBuffer(this.field_20991, this.field_20992)
				.get((i + j * this.field_20988) * this.field_20987.method_19487() + this.field_20987.method_19497() / 8);
		} else {
			throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", i, j, this.field_20988, this.field_20989));
		}
	}

	public void method_19479(int i, int j, int k) {
		if (this.field_20987 != class_4277.class_4278.RGBA) {
			throw new UnsupportedOperationException("Can only call blendPixel with RGBA format");
		} else {
			int l = this.method_19459(i, j);
			float f = (float)(k >> 24 & 0xFF) / 255.0F;
			float g = (float)(k >> 16 & 0xFF) / 255.0F;
			float h = (float)(k >> 8 & 0xFF) / 255.0F;
			float m = (float)(k >> 0 & 0xFF) / 255.0F;
			float n = (float)(l >> 24 & 0xFF) / 255.0F;
			float o = (float)(l >> 16 & 0xFF) / 255.0F;
			float p = (float)(l >> 8 & 0xFF) / 255.0F;
			float q = (float)(l >> 0 & 0xFF) / 255.0F;
			float s = 1.0F - f;
			float t = f * f + n * s;
			float u = g * f + o * s;
			float v = h * f + p * s;
			float w = m * f + q * s;
			if (t > 1.0F) {
				t = 1.0F;
			}

			if (u > 1.0F) {
				u = 1.0F;
			}

			if (v > 1.0F) {
				v = 1.0F;
			}

			if (w > 1.0F) {
				w = 1.0F;
			}

			int x = (int)(t * 255.0F);
			int y = (int)(u * 255.0F);
			int z = (int)(v * 255.0F);
			int aa = (int)(w * 255.0F);
			this.method_19460(i, j, x << 24 | y << 16 | z << 8 | aa << 0);
		}
	}

	@Deprecated
	public int[] method_19482() {
		if (this.field_20987 != class_4277.class_4278.RGBA) {
			throw new UnsupportedOperationException("can only call makePixelArray for RGBA images.");
		} else {
			this.method_19486();
			int[] is = new int[this.method_19458() * this.method_19478()];

			for (int i = 0; i < this.method_19478(); i++) {
				for (int j = 0; j < this.method_19458(); j++) {
					int k = this.method_19459(j, i);
					int l = k >> 24 & 0xFF;
					int m = k >> 16 & 0xFF;
					int n = k >> 8 & 0xFF;
					int o = k >> 0 & 0xFF;
					int p = l << 24 | o << 16 | n << 8 | m;
					is[j + i * this.method_19458()] = p;
				}
			}

			return is;
		}
	}

	public void method_19466(int i, int j, int k, boolean bl) {
		this.method_19462(i, j, k, 0, 0, this.field_20988, this.field_20989, bl);
	}

	public void method_19462(int i, int j, int k, int l, int m, int n, int o, boolean bl) {
		this.method_19463(i, j, k, l, m, n, o, false, false, bl);
	}

	public void method_19463(int i, int j, int k, int l, int m, int n, int o, boolean bl, boolean bl2, boolean bl3) {
		this.method_19486();
		method_19477(bl, bl3);
		method_19480(bl2);
		if (n == this.method_19458()) {
			GlStateManager.method_12314(3314, 0);
		} else {
			GlStateManager.method_12314(3314, this.method_19458());
		}

		GlStateManager.method_12314(3316, l);
		GlStateManager.method_12314(3315, m);
		this.field_20987.method_19492();
		GlStateManager.method_19118(3553, i, j, k, n, o, this.field_20987.method_19493(), 5121, this.field_20991);
	}

	public void method_19467(int i, boolean bl) {
		this.method_19486();
		this.field_20987.method_19490();
		GlStateManager.method_19120(3553, i, this.field_20987.method_19493(), 5121, this.field_20991);
		if (bl && this.field_20987.method_19494()) {
			for (int j = 0; j < this.method_19478(); j++) {
				for (int k = 0; k < this.method_19458(); k++) {
					this.method_19460(k, j, this.method_19459(k, j) | 255 << this.field_20987.method_19495());
				}
			}
		}
	}

	public void method_19476(boolean bl) {
		this.method_19486();
		this.field_20987.method_19490();
		if (bl) {
			GlStateManager.method_19122(3357, Float.MAX_VALUE);
		}

		GlStateManager.method_19119(0, 0, this.field_20988, this.field_20989, this.field_20987.method_19493(), 5121, this.field_20991);
		if (bl) {
			GlStateManager.method_19122(3357, 0.0F);
		}
	}

	public void method_19471(File file) throws IOException {
		this.method_19474(file.toPath());
	}

	public void method_19475(STBTTFontinfo sTBTTFontinfo, int i, int j, int k, float f, float g, float h, float l, int m, int n) {
		if (m < 0 || m + j > this.method_19458() || n < 0 || n + k > this.method_19478()) {
			throw new IllegalArgumentException(
				String.format("Out of bounds: start: (%s, %s) (size: %sx%s); size: %sx%s", m, n, j, k, this.method_19458(), this.method_19478())
			);
		} else if (this.field_20987.method_19487() != 1) {
			throw new IllegalArgumentException("Can only write fonts into 1-component images.");
		} else {
			STBTruetype.nstbtt_MakeGlyphBitmapSubpixel(
				sTBTTFontinfo.address(), this.field_20991 + (long)m + (long)(n * this.method_19458()), j, k, this.method_19458(), f, g, h, l, i
			);
		}
	}

	public void method_19474(Path path) throws IOException {
		if (!this.field_20987.method_19498()) {
			throw new UnsupportedOperationException("Don't know how to write format " + this.field_20987);
		} else {
			this.method_19486();
			WritableByteChannel writableByteChannel = Files.newByteChannel(path, field_20986);
			Throwable var3 = null;

			try {
				class_4277.class_4280 lv = new class_4277.class_4280(writableByteChannel);

				try {
					if (!STBImageWrite.stbi_write_png_to_func(
						lv, 0L, this.method_19458(), this.method_19478(), this.field_20987.method_19487(), MemoryUtil.memByteBuffer(this.field_20991, this.field_20992), 0
					)) {
						throw new IOException("Could not write image to the PNG file \"" + path.toAbsolutePath() + "\": " + STBImage.stbi_failure_reason());
					}
				} finally {
					lv.free();
				}

				lv.method_19500();
			} catch (Throwable var19) {
				var3 = var19;
				throw var19;
			} finally {
				if (writableByteChannel != null) {
					if (var3 != null) {
						try {
							writableByteChannel.close();
						} catch (Throwable var17) {
							var3.addSuppressed(var17);
						}
					} else {
						writableByteChannel.close();
					}
				}
			}
		}
	}

	public void method_19470(class_4277 arg) {
		if (arg.method_19481() != this.field_20987) {
			throw new UnsupportedOperationException("Image formats don't match.");
		} else {
			int i = this.field_20987.method_19487();
			this.method_19486();
			arg.method_19486();
			if (this.field_20988 == arg.field_20988) {
				MemoryUtil.memCopy(arg.field_20991, this.field_20991, (long)Math.min(this.field_20992, arg.field_20992));
			} else {
				int j = Math.min(this.method_19458(), arg.method_19458());
				int k = Math.min(this.method_19478(), arg.method_19478());

				for (int l = 0; l < k; l++) {
					int m = l * arg.method_19458() * i;
					int n = l * this.method_19458() * i;
					MemoryUtil.memCopy(arg.field_20991 + (long)m, this.field_20991 + (long)n, (long)j);
				}
			}
		}
	}

	public void method_19461(int i, int j, int k, int l, int m) {
		for (int n = j; n < j + l; n++) {
			for (int o = i; o < i + k; o++) {
				this.method_19460(o, n, m);
			}
		}
	}

	public void method_19464(int i, int j, int k, int l, int m, int n, boolean bl, boolean bl2) {
		for (int o = 0; o < n; o++) {
			for (int p = 0; p < m; p++) {
				int q = bl ? m - 1 - p : p;
				int r = bl2 ? n - 1 - o : o;
				int s = this.method_19459(i + p, j + o);
				this.method_19460(i + k + q, j + l + r, s);
			}
		}
	}

	public void method_19484() {
		this.method_19486();
		MemoryStack memoryStack = MemoryStack.stackPush();
		Throwable var2 = null;

		try {
			int i = this.field_20987.method_19487();
			int j = this.method_19458() * i;
			long l = memoryStack.nmalloc(j);

			for (int k = 0; k < this.method_19478() / 2; k++) {
				int m = k * this.method_19458() * i;
				int n = (this.method_19478() - 1 - k) * this.method_19458() * i;
				MemoryUtil.memCopy(this.field_20991 + (long)m, l, (long)j);
				MemoryUtil.memCopy(this.field_20991 + (long)n, this.field_20991 + (long)m, (long)j);
				MemoryUtil.memCopy(l, this.field_20991 + (long)n, (long)j);
			}
		} catch (Throwable var17) {
			var2 = var17;
			throw var17;
		} finally {
			if (memoryStack != null) {
				if (var2 != null) {
					try {
						memoryStack.close();
					} catch (Throwable var16) {
						var2.addSuppressed(var16);
					}
				} else {
					memoryStack.close();
				}
			}
		}
	}

	public void method_19465(int i, int j, int k, int l, class_4277 arg) {
		this.method_19486();
		if (arg.method_19481() != this.field_20987) {
			throw new UnsupportedOperationException("resizeSubRectTo only works for images of the same format.");
		} else {
			int m = this.field_20987.method_19487();
			STBImageResize.nstbir_resize_uint8(
				this.field_20991 + (long)((i + j * this.method_19458()) * m), k, l, this.method_19458() * m, arg.field_20991, arg.method_19458(), arg.method_19478(), 0, m
			);
		}
	}

	public void method_19485() {
		class_4105.method_18147(this.field_20991);
	}

	public static enum class_4278 {
		RGBA(4, 6408, true, true, true, false, true, 0, 8, 16, 255, 24, true),
		RGB(3, 6407, true, true, true, false, false, 0, 8, 16, 255, 255, true),
		LUMINANCE_ALPHA(2, 6410, false, false, false, true, true, 255, 255, 255, 0, 8, true),
		LUMINANCE(1, 6409, false, false, false, true, false, 0, 0, 0, 0, 255, true);

		private final int field_20997;
		private final int field_20998;
		private final boolean field_20999;
		private final boolean field_21000;
		private final boolean field_21001;
		private final boolean field_21002;
		private final boolean field_21003;
		private final int field_21004;
		private final int field_21005;
		private final int field_21006;
		private final int field_21007;
		private final int field_21008;
		private final boolean field_21009;

		private class_4278(int j, int k, boolean bl, boolean bl2, boolean bl3, boolean bl4, boolean bl5, int l, int m, int n, int o, int p, boolean bl6) {
			this.field_20997 = j;
			this.field_20998 = k;
			this.field_20999 = bl;
			this.field_21000 = bl2;
			this.field_21001 = bl3;
			this.field_21002 = bl4;
			this.field_21003 = bl5;
			this.field_21004 = l;
			this.field_21005 = m;
			this.field_21006 = n;
			this.field_21007 = o;
			this.field_21008 = p;
			this.field_21009 = bl6;
		}

		public int method_19487() {
			return this.field_20997;
		}

		public void method_19490() {
			GlStateManager.method_12314(3333, this.method_19487());
		}

		public void method_19492() {
			GlStateManager.method_12314(3317, this.method_19487());
		}

		public int method_19493() {
			return this.field_20998;
		}

		public boolean method_19494() {
			return this.field_21003;
		}

		public int method_19495() {
			return this.field_21008;
		}

		public boolean method_19496() {
			return this.field_21002 || this.field_21003;
		}

		public int method_19497() {
			return this.field_21002 ? this.field_21007 : this.field_21008;
		}

		public boolean method_19498() {
			return this.field_21009;
		}

		private static class_4277.class_4278 method_19491(int i) {
			switch (i) {
				case 1:
					return LUMINANCE;
				case 2:
					return LUMINANCE_ALPHA;
				case 3:
					return RGB;
				case 4:
				default:
					return RGBA;
			}
		}
	}

	public static enum class_4279 {
		RGBA(6408),
		RGB(6407),
		LUMINANCE_ALPHA(6410),
		LUMINANCE(6409),
		INTENSITY(32841);

		private final int field_21016;

		private class_4279(int j) {
			this.field_21016 = j;
		}

		int method_19499() {
			return this.field_21016;
		}
	}

	static class class_4280 extends STBIWriteCallback {
		private final WritableByteChannel field_21018;
		private IOException field_21019;

		private class_4280(WritableByteChannel writableByteChannel) {
			this.field_21018 = writableByteChannel;
		}

		public void invoke(long l, long m, int i) {
			ByteBuffer byteBuffer = getData(m, i);

			try {
				this.field_21018.write(byteBuffer);
			} catch (IOException var8) {
				this.field_21019 = var8;
			}
		}

		public void method_19500() throws IOException {
			if (this.field_21019 != null) {
				throw this.field_21019;
			}
		}
	}
}
