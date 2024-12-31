package net.minecraft.client.texture;

import com.google.common.base.Charsets;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.util.Untracker;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.stb.STBIWriteCallback;
import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBImageResize;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public final class NativeImage implements AutoCloseable {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Set<StandardOpenOption> WRITE_TO_FILE_OPEN_OPTIONS = EnumSet.of(
		StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING
	);
	private final NativeImage.Format format;
	private final int width;
	private final int height;
	private final boolean isStbImage;
	private long pointer;
	private final long sizeBytes;

	public NativeImage(int i, int j, boolean bl) {
		this(NativeImage.Format.field_4997, i, j, bl);
	}

	public NativeImage(NativeImage.Format format, int i, int j, boolean bl) {
		this.format = format;
		this.width = i;
		this.height = j;
		this.sizeBytes = (long)i * (long)j * (long)format.getChannelCount();
		this.isStbImage = false;
		if (bl) {
			this.pointer = MemoryUtil.nmemCalloc(1L, this.sizeBytes);
		} else {
			this.pointer = MemoryUtil.nmemAlloc(this.sizeBytes);
		}
	}

	private NativeImage(NativeImage.Format format, int i, int j, boolean bl, long l) {
		this.format = format;
		this.width = i;
		this.height = j;
		this.isStbImage = bl;
		this.pointer = l;
		this.sizeBytes = (long)(i * j * format.getChannelCount());
	}

	public String toString() {
		return "NativeImage[" + this.format + " " + this.width + "x" + this.height + "@" + this.pointer + (this.isStbImage ? "S" : "N") + "]";
	}

	public static NativeImage read(InputStream inputStream) throws IOException {
		return read(NativeImage.Format.field_4997, inputStream);
	}

	public static NativeImage read(@Nullable NativeImage.Format format, InputStream inputStream) throws IOException {
		ByteBuffer byteBuffer = null;

		NativeImage var3;
		try {
			byteBuffer = TextureUtil.readResource(inputStream);
			byteBuffer.rewind();
			var3 = read(format, byteBuffer);
		} finally {
			MemoryUtil.memFree(byteBuffer);
			IOUtils.closeQuietly(inputStream);
		}

		return var3;
	}

	public static NativeImage read(ByteBuffer byteBuffer) throws IOException {
		return read(NativeImage.Format.field_4997, byteBuffer);
	}

	public static NativeImage read(@Nullable NativeImage.Format format, ByteBuffer byteBuffer) throws IOException {
		if (format != null && !format.isWriteable()) {
			throw new UnsupportedOperationException("Don't know how to read format " + format);
		} else if (MemoryUtil.memAddress(byteBuffer) == 0L) {
			throw new IllegalArgumentException("Invalid buffer");
		} else {
			MemoryStack memoryStack = MemoryStack.stackPush();
			Throwable var3 = null;

			NativeImage var8;
			try {
				IntBuffer intBuffer = memoryStack.mallocInt(1);
				IntBuffer intBuffer2 = memoryStack.mallocInt(1);
				IntBuffer intBuffer3 = memoryStack.mallocInt(1);
				ByteBuffer byteBuffer2 = STBImage.stbi_load_from_memory(byteBuffer, intBuffer, intBuffer2, intBuffer3, format == null ? 0 : format.channelCount);
				if (byteBuffer2 == null) {
					throw new IOException("Could not load image: " + STBImage.stbi_failure_reason());
				}

				var8 = new NativeImage(
					format == null ? NativeImage.Format.getFormat(intBuffer3.get(0)) : format, intBuffer.get(0), intBuffer2.get(0), true, MemoryUtil.memAddress(byteBuffer2)
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

	private static void setTextureClamp(boolean bl) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		if (bl) {
			GlStateManager.texParameter(3553, 10242, 10496);
			GlStateManager.texParameter(3553, 10243, 10496);
		} else {
			GlStateManager.texParameter(3553, 10242, 10497);
			GlStateManager.texParameter(3553, 10243, 10497);
		}
	}

	private static void setTextureFilter(boolean bl, boolean bl2) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		if (bl) {
			GlStateManager.texParameter(3553, 10241, bl2 ? 9987 : 9729);
			GlStateManager.texParameter(3553, 10240, 9729);
		} else {
			GlStateManager.texParameter(3553, 10241, bl2 ? 9986 : 9728);
			GlStateManager.texParameter(3553, 10240, 9728);
		}
	}

	private void checkAllocated() {
		if (this.pointer == 0L) {
			throw new IllegalStateException("Image is not allocated.");
		}
	}

	public void close() {
		if (this.pointer != 0L) {
			if (this.isStbImage) {
				STBImage.nstbi_image_free(this.pointer);
			} else {
				MemoryUtil.nmemFree(this.pointer);
			}
		}

		this.pointer = 0L;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public NativeImage.Format getFormat() {
		return this.format;
	}

	public int getPixelRgba(int i, int j) {
		if (this.format != NativeImage.Format.field_4997) {
			throw new IllegalArgumentException(String.format("getPixelRGBA only works on RGBA images; have %s", this.format));
		} else if (i <= this.width && j <= this.height) {
			this.checkAllocated();
			long l = (long)((i + j * this.width) * 4);
			return MemoryUtil.memGetInt(this.pointer + l);
		} else {
			throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", i, j, this.width, this.height));
		}
	}

	public void setPixelRgba(int i, int j, int k) {
		if (this.format != NativeImage.Format.field_4997) {
			throw new IllegalArgumentException(String.format("getPixelRGBA only works on RGBA images; have %s", this.format));
		} else if (i <= this.width && j <= this.height) {
			this.checkAllocated();
			long l = (long)((i + j * this.width) * 4);
			MemoryUtil.memPutInt(this.pointer + l, k);
		} else {
			throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", i, j, this.width, this.height));
		}
	}

	public byte getPixelOpacity(int i, int j) {
		if (!this.format.hasOpacityChannel()) {
			throw new IllegalArgumentException(String.format("no luminance or alpha in %s", this.format));
		} else if (i <= this.width && j <= this.height) {
			int k = (i + j * this.width) * this.format.getChannelCount() + this.format.getOpacityOffset() / 8;
			return MemoryUtil.memGetByte(this.pointer + (long)k);
		} else {
			throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", i, j, this.width, this.height));
		}
	}

	public void blendPixel(int i, int j, int k) {
		if (this.format != NativeImage.Format.field_4997) {
			throw new UnsupportedOperationException("Can only call blendPixel with RGBA format");
		} else {
			int l = this.getPixelRgba(i, j);
			float f = (float)method_24030(k) / 255.0F;
			float g = (float)method_24035(k) / 255.0F;
			float h = (float)method_24034(k) / 255.0F;
			float m = (float)method_24033(k) / 255.0F;
			float n = (float)method_24030(l) / 255.0F;
			float o = (float)method_24035(l) / 255.0F;
			float p = (float)method_24034(l) / 255.0F;
			float q = (float)method_24033(l) / 255.0F;
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
			this.setPixelRgba(i, j, method_24031(x, y, z, aa));
		}
	}

	@Deprecated
	public int[] makePixelArray() {
		if (this.format != NativeImage.Format.field_4997) {
			throw new UnsupportedOperationException("can only call makePixelArray for RGBA images.");
		} else {
			this.checkAllocated();
			int[] is = new int[this.getWidth() * this.getHeight()];

			for (int i = 0; i < this.getHeight(); i++) {
				for (int j = 0; j < this.getWidth(); j++) {
					int k = this.getPixelRgba(j, i);
					int l = method_24030(k);
					int m = method_24035(k);
					int n = method_24034(k);
					int o = method_24033(k);
					int p = l << 24 | o << 16 | n << 8 | m;
					is[j + i * this.getWidth()] = p;
				}
			}

			return is;
		}
	}

	public void upload(int i, int j, int k, boolean bl) {
		this.upload(i, j, k, 0, 0, this.width, this.height, false, bl);
	}

	public void upload(int i, int j, int k, int l, int m, int n, int o, boolean bl, boolean bl2) {
		this.upload(i, j, k, l, m, n, o, false, false, bl, bl2);
	}

	public void upload(int i, int j, int k, int l, int m, int n, int o, boolean bl, boolean bl2, boolean bl3, boolean bl4) {
		if (!RenderSystem.isOnRenderThreadOrInit()) {
			RenderSystem.recordRenderCall(() -> this.uploadInternal(i, j, k, l, m, n, o, bl, bl2, bl3, bl4));
		} else {
			this.uploadInternal(i, j, k, l, m, n, o, bl, bl2, bl3, bl4);
		}
	}

	private void uploadInternal(int i, int j, int k, int l, int m, int n, int o, boolean bl, boolean bl2, boolean bl3, boolean bl4) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		this.checkAllocated();
		setTextureFilter(bl, bl3);
		setTextureClamp(bl2);
		if (n == this.getWidth()) {
			GlStateManager.pixelStore(3314, 0);
		} else {
			GlStateManager.pixelStore(3314, this.getWidth());
		}

		GlStateManager.pixelStore(3316, l);
		GlStateManager.pixelStore(3315, m);
		this.format.setUnpackAlignment();
		GlStateManager.texSubImage2D(3553, i, j, k, n, o, this.format.getPixelDataFormat(), 5121, this.pointer);
		if (bl4) {
			this.close();
		}
	}

	public void loadFromTextureImage(int i, boolean bl) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		this.checkAllocated();
		this.format.setPackAlignment();
		GlStateManager.getTexImage(3553, i, this.format.getPixelDataFormat(), 5121, this.pointer);
		if (bl && this.format.hasAlphaChannel()) {
			for (int j = 0; j < this.getHeight(); j++) {
				for (int k = 0; k < this.getWidth(); k++) {
					this.setPixelRgba(k, j, this.getPixelRgba(k, j) | 255 << this.format.getAlphaChannelOffset());
				}
			}
		}
	}

	public void writeFile(File file) throws IOException {
		this.writeFile(file.toPath());
	}

	public void makeGlyphBitmapSubpixel(STBTTFontinfo sTBTTFontinfo, int i, int j, int k, float f, float g, float h, float l, int m, int n) {
		if (m < 0 || m + j > this.getWidth() || n < 0 || n + k > this.getHeight()) {
			throw new IllegalArgumentException(String.format("Out of bounds: start: (%s, %s) (size: %sx%s); size: %sx%s", m, n, j, k, this.getWidth(), this.getHeight()));
		} else if (this.format.getChannelCount() != 1) {
			throw new IllegalArgumentException("Can only write fonts into 1-component images.");
		} else {
			STBTruetype.nstbtt_MakeGlyphBitmapSubpixel(
				sTBTTFontinfo.address(), this.pointer + (long)m + (long)(n * this.getWidth()), j, k, this.getWidth(), f, g, h, l, i
			);
		}
	}

	public void writeFile(Path path) throws IOException {
		if (!this.format.isWriteable()) {
			throw new UnsupportedOperationException("Don't know how to write format " + this.format);
		} else {
			this.checkAllocated();
			WritableByteChannel writableByteChannel = Files.newByteChannel(path, WRITE_TO_FILE_OPEN_OPTIONS);
			Throwable var3 = null;

			try {
				if (!this.method_24032(writableByteChannel)) {
					throw new IOException("Could not write image to the PNG file \"" + path.toAbsolutePath() + "\": " + STBImage.stbi_failure_reason());
				}
			} catch (Throwable var12) {
				var3 = var12;
				throw var12;
			} finally {
				if (writableByteChannel != null) {
					if (var3 != null) {
						try {
							writableByteChannel.close();
						} catch (Throwable var11) {
							var3.addSuppressed(var11);
						}
					} else {
						writableByteChannel.close();
					}
				}
			}
		}
	}

	public byte[] getBytes() throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		Throwable var2 = null;

		byte[] var5;
		try {
			WritableByteChannel writableByteChannel = Channels.newChannel(byteArrayOutputStream);
			Throwable var4 = null;

			try {
				if (!this.method_24032(writableByteChannel)) {
					throw new IOException("Could not write image to byte array: " + STBImage.stbi_failure_reason());
				}

				var5 = byteArrayOutputStream.toByteArray();
			} catch (Throwable var28) {
				var4 = var28;
				throw var28;
			} finally {
				if (writableByteChannel != null) {
					if (var4 != null) {
						try {
							writableByteChannel.close();
						} catch (Throwable var27) {
							var4.addSuppressed(var27);
						}
					} else {
						writableByteChannel.close();
					}
				}
			}
		} catch (Throwable var30) {
			var2 = var30;
			throw var30;
		} finally {
			if (byteArrayOutputStream != null) {
				if (var2 != null) {
					try {
						byteArrayOutputStream.close();
					} catch (Throwable var26) {
						var2.addSuppressed(var26);
					}
				} else {
					byteArrayOutputStream.close();
				}
			}
		}

		return var5;
	}

	private boolean method_24032(WritableByteChannel writableByteChannel) throws IOException {
		NativeImage.WriteCallback writeCallback = new NativeImage.WriteCallback(writableByteChannel);

		boolean var4;
		try {
			int i = Math.min(this.getHeight(), Integer.MAX_VALUE / this.getWidth() / this.format.getChannelCount());
			if (i < this.getHeight()) {
				LOGGER.warn("Dropping image height from {} to {} to fit the size into 32-bit signed int", this.getHeight(), i);
			}

			if (STBImageWrite.nstbi_write_png_to_func(writeCallback.address(), 0L, this.getWidth(), i, this.format.getChannelCount(), this.pointer, 0) != 0) {
				writeCallback.throwStoredException();
				return true;
			}

			var4 = false;
		} finally {
			writeCallback.free();
		}

		return var4;
	}

	public void copyFrom(NativeImage nativeImage) {
		if (nativeImage.getFormat() != this.format) {
			throw new UnsupportedOperationException("Image formats don't match.");
		} else {
			int i = this.format.getChannelCount();
			this.checkAllocated();
			nativeImage.checkAllocated();
			if (this.width == nativeImage.width) {
				MemoryUtil.memCopy(nativeImage.pointer, this.pointer, Math.min(this.sizeBytes, nativeImage.sizeBytes));
			} else {
				int j = Math.min(this.getWidth(), nativeImage.getWidth());
				int k = Math.min(this.getHeight(), nativeImage.getHeight());

				for (int l = 0; l < k; l++) {
					int m = l * nativeImage.getWidth() * i;
					int n = l * this.getWidth() * i;
					MemoryUtil.memCopy(nativeImage.pointer + (long)m, this.pointer + (long)n, (long)j);
				}
			}
		}
	}

	public void fillRect(int i, int j, int k, int l, int m) {
		for (int n = j; n < j + l; n++) {
			for (int o = i; o < i + k; o++) {
				this.setPixelRgba(o, n, m);
			}
		}
	}

	public void copyRect(int i, int j, int k, int l, int m, int n, boolean bl, boolean bl2) {
		for (int o = 0; o < n; o++) {
			for (int p = 0; p < m; p++) {
				int q = bl ? m - 1 - p : p;
				int r = bl2 ? n - 1 - o : o;
				int s = this.getPixelRgba(i + p, j + o);
				this.setPixelRgba(i + k + q, j + l + r, s);
			}
		}
	}

	public void mirrorVertically() {
		this.checkAllocated();
		MemoryStack memoryStack = MemoryStack.stackPush();
		Throwable var2 = null;

		try {
			int i = this.format.getChannelCount();
			int j = this.getWidth() * i;
			long l = memoryStack.nmalloc(j);

			for (int k = 0; k < this.getHeight() / 2; k++) {
				int m = k * this.getWidth() * i;
				int n = (this.getHeight() - 1 - k) * this.getWidth() * i;
				MemoryUtil.memCopy(this.pointer + (long)m, l, (long)j);
				MemoryUtil.memCopy(this.pointer + (long)n, this.pointer + (long)m, (long)j);
				MemoryUtil.memCopy(l, this.pointer + (long)n, (long)j);
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

	public void resizeSubRectTo(int i, int j, int k, int l, NativeImage nativeImage) {
		this.checkAllocated();
		if (nativeImage.getFormat() != this.format) {
			throw new UnsupportedOperationException("resizeSubRectTo only works for images of the same format.");
		} else {
			int m = this.format.getChannelCount();
			STBImageResize.nstbir_resize_uint8(
				this.pointer + (long)((i + j * this.getWidth()) * m), k, l, this.getWidth() * m, nativeImage.pointer, nativeImage.getWidth(), nativeImage.getHeight(), 0, m
			);
		}
	}

	public void untrack() {
		Untracker.untrack(this.pointer);
	}

	public static NativeImage read(String string) throws IOException {
		byte[] bs = Base64.getDecoder().decode(string.replaceAll("\n", "").getBytes(Charsets.UTF_8));
		MemoryStack memoryStack = MemoryStack.stackPush();
		Throwable var3 = null;

		NativeImage var5;
		try {
			ByteBuffer byteBuffer = memoryStack.malloc(bs.length);
			byteBuffer.put(bs);
			byteBuffer.rewind();
			var5 = read(byteBuffer);
		} catch (Throwable var14) {
			var3 = var14;
			throw var14;
		} finally {
			if (memoryStack != null) {
				if (var3 != null) {
					try {
						memoryStack.close();
					} catch (Throwable var13) {
						var3.addSuppressed(var13);
					}
				} else {
					memoryStack.close();
				}
			}
		}

		return var5;
	}

	public static int method_24030(int i) {
		return i >> 24 & 0xFF;
	}

	public static int method_24033(int i) {
		return i >> 0 & 0xFF;
	}

	public static int method_24034(int i) {
		return i >> 8 & 0xFF;
	}

	public static int method_24035(int i) {
		return i >> 16 & 0xFF;
	}

	public static int method_24031(int i, int j, int k, int l) {
		return (i & 0xFF) << 24 | (j & 0xFF) << 16 | (k & 0xFF) << 8 | (l & 0xFF) << 0;
	}

	public static enum Format {
		field_4997(4, 6408, true, true, true, false, true, 0, 8, 16, 255, 24, true),
		field_5001(3, 6407, true, true, true, false, false, 0, 8, 16, 255, 255, true),
		field_5002(2, 6410, false, false, false, true, true, 255, 255, 255, 0, 8, true),
		field_4998(1, 6409, false, false, false, true, false, 0, 0, 0, 0, 255, true);

		private final int channelCount;
		private final int pixelDataFormat;
		private final boolean hasRed;
		private final boolean hasGreen;
		private final boolean hasBlue;
		private final boolean hasLuminance;
		private final boolean hasAlpha;
		private final int redOffset;
		private final int greenOffset;
		private final int blueOffset;
		private final int luminanceChannelOffset;
		private final int alphaChannelOffset;
		private final boolean writeable;

		private Format(int j, int k, boolean bl, boolean bl2, boolean bl3, boolean bl4, boolean bl5, int l, int m, int n, int o, int p, boolean bl6) {
			this.channelCount = j;
			this.pixelDataFormat = k;
			this.hasRed = bl;
			this.hasGreen = bl2;
			this.hasBlue = bl3;
			this.hasLuminance = bl4;
			this.hasAlpha = bl5;
			this.redOffset = l;
			this.greenOffset = m;
			this.blueOffset = n;
			this.luminanceChannelOffset = o;
			this.alphaChannelOffset = p;
			this.writeable = bl6;
		}

		public int getChannelCount() {
			return this.channelCount;
		}

		public void setPackAlignment() {
			RenderSystem.assertThread(RenderSystem::isOnRenderThread);
			GlStateManager.pixelStore(3333, this.getChannelCount());
		}

		public void setUnpackAlignment() {
			RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
			GlStateManager.pixelStore(3317, this.getChannelCount());
		}

		public int getPixelDataFormat() {
			return this.pixelDataFormat;
		}

		public boolean hasAlphaChannel() {
			return this.hasAlpha;
		}

		public int getAlphaChannelOffset() {
			return this.alphaChannelOffset;
		}

		public boolean hasOpacityChannel() {
			return this.hasLuminance || this.hasAlpha;
		}

		public int getOpacityOffset() {
			return this.hasLuminance ? this.luminanceChannelOffset : this.alphaChannelOffset;
		}

		public boolean isWriteable() {
			return this.writeable;
		}

		private static NativeImage.Format getFormat(int i) {
			switch (i) {
				case 1:
					return field_4998;
				case 2:
					return field_5002;
				case 3:
					return field_5001;
				case 4:
				default:
					return field_4997;
			}
		}
	}

	public static enum GLFormat {
		field_5012(6408),
		field_5011(6407),
		field_5013(6410),
		field_5017(6409),
		field_5016(32841);

		private final int glConstant;

		private GLFormat(int j) {
			this.glConstant = j;
		}

		int getGlConstant() {
			return this.glConstant;
		}
	}

	static class WriteCallback extends STBIWriteCallback {
		private final WritableByteChannel channel;
		@Nullable
		private IOException exception;

		private WriteCallback(WritableByteChannel writableByteChannel) {
			this.channel = writableByteChannel;
		}

		public void invoke(long l, long m, int i) {
			ByteBuffer byteBuffer = getData(m, i);

			try {
				this.channel.write(byteBuffer);
			} catch (IOException var8) {
				this.exception = var8;
			}
		}

		public void throwStoredException() throws IOException {
			if (this.exception != null) {
				throw this.exception;
			}
		}
	}
}
