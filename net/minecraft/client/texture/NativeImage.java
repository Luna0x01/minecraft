package net.minecraft.client.texture;

import com.google.common.base.Charsets;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.file.FileSystems;
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
	private static final int field_32031 = 24;
	private static final int field_32032 = 16;
	private static final int field_32033 = 8;
	private static final int field_32034 = 0;
	private static final Set<StandardOpenOption> WRITE_TO_FILE_OPEN_OPTIONS = EnumSet.of(
		StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING
	);
	private final NativeImage.Format format;
	private final int width;
	private final int height;
	private final boolean isStbImage;
	private long pointer;
	private final long sizeBytes;

	public NativeImage(int width, int height, boolean useStb) {
		this(NativeImage.Format.ABGR, width, height, useStb);
	}

	public NativeImage(NativeImage.Format format, int width, int height, boolean useStb) {
		if (width > 0 && height > 0) {
			this.format = format;
			this.width = width;
			this.height = height;
			this.sizeBytes = (long)width * (long)height * (long)format.getChannelCount();
			this.isStbImage = false;
			if (useStb) {
				this.pointer = MemoryUtil.nmemCalloc(1L, this.sizeBytes);
			} else {
				this.pointer = MemoryUtil.nmemAlloc(this.sizeBytes);
			}
		} else {
			throw new IllegalArgumentException("Invalid texture size: " + width + "x" + height);
		}
	}

	private NativeImage(NativeImage.Format format, int width, int height, boolean useStb, long pointer) {
		if (width > 0 && height > 0) {
			this.format = format;
			this.width = width;
			this.height = height;
			this.isStbImage = useStb;
			this.pointer = pointer;
			this.sizeBytes = (long)width * (long)height * (long)format.getChannelCount();
		} else {
			throw new IllegalArgumentException("Invalid texture size: " + width + "x" + height);
		}
	}

	public String toString() {
		return "NativeImage[" + this.format + " " + this.width + "x" + this.height + "@" + this.pointer + (this.isStbImage ? "S" : "N") + "]";
	}

	private boolean isOutOfBounds(int x, int y) {
		return x < 0 || x >= this.width || y < 0 || y >= this.height;
	}

	public static NativeImage read(InputStream inputStream) throws IOException {
		return read(NativeImage.Format.ABGR, inputStream);
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
		return read(NativeImage.Format.ABGR, byteBuffer);
	}

	public static NativeImage read(@Nullable NativeImage.Format format, ByteBuffer byteBuffer) throws IOException {
		if (format != null && !format.isWriteable()) {
			throw new UnsupportedOperationException("Don't know how to read format " + format);
		} else if (MemoryUtil.memAddress(byteBuffer) == 0L) {
			throw new IllegalArgumentException("Invalid buffer");
		} else {
			MemoryStack memoryStack = MemoryStack.stackPush();

			NativeImage var7;
			try {
				IntBuffer intBuffer = memoryStack.mallocInt(1);
				IntBuffer intBuffer2 = memoryStack.mallocInt(1);
				IntBuffer intBuffer3 = memoryStack.mallocInt(1);
				ByteBuffer byteBuffer2 = STBImage.stbi_load_from_memory(byteBuffer, intBuffer, intBuffer2, intBuffer3, format == null ? 0 : format.channelCount);
				if (byteBuffer2 == null) {
					throw new IOException("Could not load image: " + STBImage.stbi_failure_reason());
				}

				var7 = new NativeImage(
					format == null ? NativeImage.Format.getFormat(intBuffer3.get(0)) : format, intBuffer.get(0), intBuffer2.get(0), true, MemoryUtil.memAddress(byteBuffer2)
				);
			} catch (Throwable var9) {
				if (memoryStack != null) {
					try {
						memoryStack.close();
					} catch (Throwable var8) {
						var9.addSuppressed(var8);
					}
				}

				throw var9;
			}

			if (memoryStack != null) {
				memoryStack.close();
			}

			return var7;
		}
	}

	private static void setTextureFilter(boolean blur, boolean mipmap) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		if (blur) {
			GlStateManager._texParameter(3553, 10241, mipmap ? 9987 : 9729);
			GlStateManager._texParameter(3553, 10240, 9729);
		} else {
			GlStateManager._texParameter(3553, 10241, mipmap ? 9986 : 9728);
			GlStateManager._texParameter(3553, 10240, 9728);
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

	public int getPixelColor(int x, int y) {
		if (this.format != NativeImage.Format.ABGR) {
			throw new IllegalArgumentException(String.format("getPixelRGBA only works on RGBA images; have %s", this.format));
		} else if (this.isOutOfBounds(x, y)) {
			throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", x, y, this.width, this.height));
		} else {
			this.checkAllocated();
			long l = ((long)x + (long)y * (long)this.width) * 4L;
			return MemoryUtil.memGetInt(this.pointer + l);
		}
	}

	public void setPixelColor(int x, int y, int color) {
		if (this.format != NativeImage.Format.ABGR) {
			throw new IllegalArgumentException(String.format("getPixelRGBA only works on RGBA images; have %s", this.format));
		} else if (this.isOutOfBounds(x, y)) {
			throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", x, y, this.width, this.height));
		} else {
			this.checkAllocated();
			long l = ((long)x + (long)y * (long)this.width) * 4L;
			MemoryUtil.memPutInt(this.pointer + l, color);
		}
	}

	public void setPixelLuminance(int x, int y, byte luminance) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		if (!this.format.hasLuminance()) {
			throw new IllegalArgumentException(String.format("setPixelLuminance only works on image with luminance; have %s", this.format));
		} else if (this.isOutOfBounds(x, y)) {
			throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", x, y, this.width, this.height));
		} else {
			this.checkAllocated();
			long l = ((long)x + (long)y * (long)this.width) * (long)this.format.getChannelCount() + (long)(this.format.getLuminanceChannelOffset() / 8);
			MemoryUtil.memPutByte(this.pointer + l, luminance);
		}
	}

	public byte getRed(int x, int y) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		if (!this.format.hasRedChannel()) {
			throw new IllegalArgumentException(String.format("no red or luminance in %s", this.format));
		} else if (this.isOutOfBounds(x, y)) {
			throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", x, y, this.width, this.height));
		} else {
			int i = (x + y * this.width) * this.format.getChannelCount() + this.format.getRedOrLuminanceOffset() / 8;
			return MemoryUtil.memGetByte(this.pointer + (long)i);
		}
	}

	public byte getGreen(int x, int y) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		if (!this.format.hasGreenChannel()) {
			throw new IllegalArgumentException(String.format("no green or luminance in %s", this.format));
		} else if (this.isOutOfBounds(x, y)) {
			throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", x, y, this.width, this.height));
		} else {
			int i = (x + y * this.width) * this.format.getChannelCount() + this.format.getGreenOrLuminanceOffset() / 8;
			return MemoryUtil.memGetByte(this.pointer + (long)i);
		}
	}

	public byte getBlue(int x, int y) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		if (!this.format.hasBlueChannel()) {
			throw new IllegalArgumentException(String.format("no blue or luminance in %s", this.format));
		} else if (this.isOutOfBounds(x, y)) {
			throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", x, y, this.width, this.height));
		} else {
			int i = (x + y * this.width) * this.format.getChannelCount() + this.format.getBlueOrLuminanceOffset() / 8;
			return MemoryUtil.memGetByte(this.pointer + (long)i);
		}
	}

	public byte getPixelOpacity(int x, int y) {
		if (!this.format.hasOpacityChannel()) {
			throw new IllegalArgumentException(String.format("no luminance or alpha in %s", this.format));
		} else if (this.isOutOfBounds(x, y)) {
			throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", x, y, this.width, this.height));
		} else {
			int i = (x + y * this.width) * this.format.getChannelCount() + this.format.getOpacityOffset() / 8;
			return MemoryUtil.memGetByte(this.pointer + (long)i);
		}
	}

	public void blendPixel(int x, int y, int color) {
		if (this.format != NativeImage.Format.ABGR) {
			throw new UnsupportedOperationException("Can only call blendPixel with RGBA format");
		} else {
			int i = this.getPixelColor(x, y);
			float f = (float)getAlpha(color) / 255.0F;
			float g = (float)getBlue(color) / 255.0F;
			float h = (float)getGreen(color) / 255.0F;
			float j = (float)getRed(color) / 255.0F;
			float k = (float)getAlpha(i) / 255.0F;
			float l = (float)getBlue(i) / 255.0F;
			float m = (float)getGreen(i) / 255.0F;
			float n = (float)getRed(i) / 255.0F;
			float p = 1.0F - f;
			float q = f * f + k * p;
			float r = g * f + l * p;
			float s = h * f + m * p;
			float t = j * f + n * p;
			if (q > 1.0F) {
				q = 1.0F;
			}

			if (r > 1.0F) {
				r = 1.0F;
			}

			if (s > 1.0F) {
				s = 1.0F;
			}

			if (t > 1.0F) {
				t = 1.0F;
			}

			int u = (int)(q * 255.0F);
			int v = (int)(r * 255.0F);
			int w = (int)(s * 255.0F);
			int z = (int)(t * 255.0F);
			this.setPixelColor(x, y, getAbgrColor(u, v, w, z));
		}
	}

	@Deprecated
	public int[] makePixelArray() {
		if (this.format != NativeImage.Format.ABGR) {
			throw new UnsupportedOperationException("can only call makePixelArray for RGBA images.");
		} else {
			this.checkAllocated();
			int[] is = new int[this.getWidth() * this.getHeight()];

			for (int i = 0; i < this.getHeight(); i++) {
				for (int j = 0; j < this.getWidth(); j++) {
					int k = this.getPixelColor(j, i);
					int l = getAlpha(k);
					int m = getBlue(k);
					int n = getGreen(k);
					int o = getRed(k);
					int p = l << 24 | o << 16 | n << 8 | m;
					is[j + i * this.getWidth()] = p;
				}
			}

			return is;
		}
	}

	public void upload(int level, int offsetX, int offsetY, boolean close) {
		this.upload(level, offsetX, offsetY, 0, 0, this.width, this.height, false, close);
	}

	public void upload(int level, int offsetX, int offsetY, int unpackSkipPixels, int unpackSkipRows, int width, int height, boolean mipmap, boolean close) {
		this.upload(level, offsetX, offsetY, unpackSkipPixels, unpackSkipRows, width, height, false, false, mipmap, close);
	}

	public void upload(
		int level,
		int offsetX,
		int offsetY,
		int unpackSkipPixels,
		int unpackSkipRows,
		int width,
		int height,
		boolean blur,
		boolean clamp,
		boolean mipmap,
		boolean close
	) {
		if (!RenderSystem.isOnRenderThreadOrInit()) {
			RenderSystem.recordRenderCall(
				() -> this.uploadInternal(level, offsetX, offsetY, unpackSkipPixels, unpackSkipRows, width, height, blur, clamp, mipmap, close)
			);
		} else {
			this.uploadInternal(level, offsetX, offsetY, unpackSkipPixels, unpackSkipRows, width, height, blur, clamp, mipmap, close);
		}
	}

	private void uploadInternal(
		int level,
		int offsetX,
		int offsetY,
		int unpackSkipPixels,
		int unpackSkipRows,
		int width,
		int height,
		boolean blur,
		boolean clamp,
		boolean mipmap,
		boolean close
	) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		this.checkAllocated();
		setTextureFilter(blur, mipmap);
		if (width == this.getWidth()) {
			GlStateManager._pixelStore(3314, 0);
		} else {
			GlStateManager._pixelStore(3314, this.getWidth());
		}

		GlStateManager._pixelStore(3316, unpackSkipPixels);
		GlStateManager._pixelStore(3315, unpackSkipRows);
		this.format.setUnpackAlignment();
		GlStateManager._texSubImage2D(3553, level, offsetX, offsetY, width, height, this.format.getPixelDataFormat(), 5121, this.pointer);
		if (clamp) {
			GlStateManager._texParameter(3553, 10242, 33071);
			GlStateManager._texParameter(3553, 10243, 33071);
		}

		if (close) {
			this.close();
		}
	}

	public void loadFromTextureImage(int level, boolean removeAlpha) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		this.checkAllocated();
		this.format.setPackAlignment();
		GlStateManager._getTexImage(3553, level, this.format.getPixelDataFormat(), 5121, this.pointer);
		if (removeAlpha && this.format.hasAlphaChannel()) {
			for (int i = 0; i < this.getHeight(); i++) {
				for (int j = 0; j < this.getWidth(); j++) {
					this.setPixelColor(j, i, this.getPixelColor(j, i) | 255 << this.format.getAlphaChannelOffset());
				}
			}
		}
	}

	public void method_35620(float f) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		if (this.format.getChannelCount() != 1) {
			throw new IllegalStateException("Depth buffer must be stored in NativeImage with 1 component.");
		} else {
			this.checkAllocated();
			this.format.setPackAlignment();
			GlStateManager._readPixels(0, 0, this.width, this.height, 6402, 5121, this.pointer);
		}
	}

	public void method_35627() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		this.format.setUnpackAlignment();
		GlStateManager._glDrawPixels(this.width, this.height, this.format.getPixelDataFormat(), 5121, this.pointer);
	}

	public void method_35622(String string) throws IOException {
		this.writeFile(FileSystems.getDefault().getPath(string));
	}

	public void writeFile(File file) throws IOException {
		this.writeFile(file.toPath());
	}

	public void makeGlyphBitmapSubpixel(
		STBTTFontinfo fontInfo, int glyphIndex, int width, int height, float scaleX, float scaleY, float shiftX, float shiftY, int startX, int startY
	) {
		if (startX < 0 || startX + width > this.getWidth() || startY < 0 || startY + height > this.getHeight()) {
			throw new IllegalArgumentException(
				String.format("Out of bounds: start: (%s, %s) (size: %sx%s); size: %sx%s", startX, startY, width, height, this.getWidth(), this.getHeight())
			);
		} else if (this.format.getChannelCount() != 1) {
			throw new IllegalArgumentException("Can only write fonts into 1-component images.");
		} else {
			STBTruetype.nstbtt_MakeGlyphBitmapSubpixel(
				fontInfo.address(),
				this.pointer + (long)startX + (long)(startY * this.getWidth()),
				width,
				height,
				this.getWidth(),
				scaleX,
				scaleY,
				shiftX,
				shiftY,
				glyphIndex
			);
		}
	}

	public void writeFile(Path path) throws IOException {
		if (!this.format.isWriteable()) {
			throw new UnsupportedOperationException("Don't know how to write format " + this.format);
		} else {
			this.checkAllocated();
			WritableByteChannel writableByteChannel = Files.newByteChannel(path, WRITE_TO_FILE_OPEN_OPTIONS);

			try {
				if (!this.write(writableByteChannel)) {
					throw new IOException("Could not write image to the PNG file \"" + path.toAbsolutePath() + "\": " + STBImage.stbi_failure_reason());
				}
			} catch (Throwable var6) {
				if (writableByteChannel != null) {
					try {
						writableByteChannel.close();
					} catch (Throwable var5) {
						var6.addSuppressed(var5);
					}
				}

				throw var6;
			}

			if (writableByteChannel != null) {
				writableByteChannel.close();
			}
		}
	}

	public byte[] getBytes() throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		byte[] var3;
		try {
			WritableByteChannel writableByteChannel = Channels.newChannel(byteArrayOutputStream);

			try {
				if (!this.write(writableByteChannel)) {
					throw new IOException("Could not write image to byte array: " + STBImage.stbi_failure_reason());
				}

				var3 = byteArrayOutputStream.toByteArray();
			} catch (Throwable var7) {
				if (writableByteChannel != null) {
					try {
						writableByteChannel.close();
					} catch (Throwable var6) {
						var7.addSuppressed(var6);
					}
				}

				throw var7;
			}

			if (writableByteChannel != null) {
				writableByteChannel.close();
			}
		} catch (Throwable var8) {
			try {
				byteArrayOutputStream.close();
			} catch (Throwable var5) {
				var8.addSuppressed(var5);
			}

			throw var8;
		}

		byteArrayOutputStream.close();
		return var3;
	}

	private boolean write(WritableByteChannel writableByteChannel) throws IOException {
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

	public void copyFrom(NativeImage image) {
		if (image.getFormat() != this.format) {
			throw new UnsupportedOperationException("Image formats don't match.");
		} else {
			int i = this.format.getChannelCount();
			this.checkAllocated();
			image.checkAllocated();
			if (this.width == image.width) {
				MemoryUtil.memCopy(image.pointer, this.pointer, Math.min(this.sizeBytes, image.sizeBytes));
			} else {
				int j = Math.min(this.getWidth(), image.getWidth());
				int k = Math.min(this.getHeight(), image.getHeight());

				for (int l = 0; l < k; l++) {
					int m = l * image.getWidth() * i;
					int n = l * this.getWidth() * i;
					MemoryUtil.memCopy(image.pointer + (long)m, this.pointer + (long)n, (long)j);
				}
			}
		}
	}

	public void fillRect(int x, int y, int width, int height, int color) {
		for (int i = y; i < y + height; i++) {
			for (int j = x; j < x + width; j++) {
				this.setPixelColor(j, i, color);
			}
		}
	}

	public void copyRect(int x, int y, int translateX, int translateY, int width, int height, boolean flipX, boolean flipY) {
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int k = flipX ? width - 1 - j : j;
				int l = flipY ? height - 1 - i : i;
				int m = this.getPixelColor(x + j, y + i);
				this.setPixelColor(x + translateX + k, y + translateY + l, m);
			}
		}
	}

	public void mirrorVertically() {
		this.checkAllocated();
		MemoryStack memoryStack = MemoryStack.stackPush();

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
		} catch (Throwable var10) {
			if (memoryStack != null) {
				try {
					memoryStack.close();
				} catch (Throwable var9) {
					var10.addSuppressed(var9);
				}
			}

			throw var10;
		}

		if (memoryStack != null) {
			memoryStack.close();
		}
	}

	public void resizeSubRectTo(int x, int y, int width, int height, NativeImage targetImage) {
		this.checkAllocated();
		if (targetImage.getFormat() != this.format) {
			throw new UnsupportedOperationException("resizeSubRectTo only works for images of the same format.");
		} else {
			int i = this.format.getChannelCount();
			STBImageResize.nstbir_resize_uint8(
				this.pointer + (long)((x + y * this.getWidth()) * i),
				width,
				height,
				this.getWidth() * i,
				targetImage.pointer,
				targetImage.getWidth(),
				targetImage.getHeight(),
				0,
				i
			);
		}
	}

	public void untrack() {
		Untracker.untrack(this.pointer);
	}

	public static NativeImage read(String dataUri) throws IOException {
		byte[] bs = Base64.getDecoder().decode(dataUri.replaceAll("\n", "").getBytes(Charsets.UTF_8));
		MemoryStack memoryStack = MemoryStack.stackPush();

		NativeImage var4;
		try {
			ByteBuffer byteBuffer = memoryStack.malloc(bs.length);
			byteBuffer.put(bs);
			byteBuffer.rewind();
			var4 = read(byteBuffer);
		} catch (Throwable var6) {
			if (memoryStack != null) {
				try {
					memoryStack.close();
				} catch (Throwable var5) {
					var6.addSuppressed(var5);
				}
			}

			throw var6;
		}

		if (memoryStack != null) {
			memoryStack.close();
		}

		return var4;
	}

	public static int getAlpha(int color) {
		return color >> 24 & 0xFF;
	}

	public static int getRed(int color) {
		return color >> 0 & 0xFF;
	}

	public static int getGreen(int color) {
		return color >> 8 & 0xFF;
	}

	public static int getBlue(int color) {
		return color >> 16 & 0xFF;
	}

	public static int getAbgrColor(int alpha, int blue, int green, int red) {
		return (alpha & 0xFF) << 24 | (blue & 0xFF) << 16 | (green & 0xFF) << 8 | (red & 0xFF) << 0;
	}

	public static enum Format {
		ABGR(4, 6408, true, true, true, false, true, 0, 8, 16, 255, 24, true),
		BGR(3, 6407, true, true, true, false, false, 0, 8, 16, 255, 255, true),
		LUMINANCE_ALPHA(2, 33319, false, false, false, true, true, 255, 255, 255, 0, 8, true),
		LUMINANCE(1, 6403, false, false, false, true, false, 0, 0, 0, 0, 255, true);

		final int channelCount;
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

		private Format(
			int channels,
			int glFormat,
			boolean hasRed,
			boolean hasGreen,
			boolean hasBlue,
			boolean hasLuminance,
			boolean hasAlpha,
			int redOffset,
			int greenOffset,
			int blueOffset,
			int luminanceOffset,
			int alphaOffset,
			boolean writeable
		) {
			this.channelCount = channels;
			this.pixelDataFormat = glFormat;
			this.hasRed = hasRed;
			this.hasGreen = hasGreen;
			this.hasBlue = hasBlue;
			this.hasLuminance = hasLuminance;
			this.hasAlpha = hasAlpha;
			this.redOffset = redOffset;
			this.greenOffset = greenOffset;
			this.blueOffset = blueOffset;
			this.luminanceChannelOffset = luminanceOffset;
			this.alphaChannelOffset = alphaOffset;
			this.writeable = writeable;
		}

		public int getChannelCount() {
			return this.channelCount;
		}

		public void setPackAlignment() {
			RenderSystem.assertThread(RenderSystem::isOnRenderThread);
			GlStateManager._pixelStore(3333, this.getChannelCount());
		}

		public void setUnpackAlignment() {
			RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
			GlStateManager._pixelStore(3317, this.getChannelCount());
		}

		public int getPixelDataFormat() {
			return this.pixelDataFormat;
		}

		public boolean hasRed() {
			return this.hasRed;
		}

		public boolean hasGreen() {
			return this.hasGreen;
		}

		public boolean hasBlue() {
			return this.hasBlue;
		}

		public boolean hasLuminance() {
			return this.hasLuminance;
		}

		public boolean hasAlphaChannel() {
			return this.hasAlpha;
		}

		public int getRedOffset() {
			return this.redOffset;
		}

		public int getGreenOffset() {
			return this.greenOffset;
		}

		public int getBlueOffset() {
			return this.blueOffset;
		}

		public int getLuminanceChannelOffset() {
			return this.luminanceChannelOffset;
		}

		public int getAlphaChannelOffset() {
			return this.alphaChannelOffset;
		}

		public boolean hasRedChannel() {
			return this.hasLuminance || this.hasRed;
		}

		public boolean hasGreenChannel() {
			return this.hasLuminance || this.hasGreen;
		}

		public boolean hasBlueChannel() {
			return this.hasLuminance || this.hasBlue;
		}

		public boolean hasOpacityChannel() {
			return this.hasLuminance || this.hasAlpha;
		}

		public int getRedOrLuminanceOffset() {
			return this.hasLuminance ? this.luminanceChannelOffset : this.redOffset;
		}

		public int getGreenOrLuminanceOffset() {
			return this.hasLuminance ? this.luminanceChannelOffset : this.greenOffset;
		}

		public int getBlueOrLuminanceOffset() {
			return this.hasLuminance ? this.luminanceChannelOffset : this.blueOffset;
		}

		public int getOpacityOffset() {
			return this.hasLuminance ? this.luminanceChannelOffset : this.alphaChannelOffset;
		}

		public boolean isWriteable() {
			return this.writeable;
		}

		static NativeImage.Format getFormat(int glFormat) {
			switch (glFormat) {
				case 1:
					return LUMINANCE;
				case 2:
					return LUMINANCE_ALPHA;
				case 3:
					return BGR;
				case 4:
				default:
					return ABGR;
			}
		}
	}

	public static enum GLFormat {
		ABGR(6408),
		BGR(6407),
		RG(33319),
		RED(6403);

		private final int glConstant;

		private GLFormat(int glConstant) {
			this.glConstant = glConstant;
		}

		public int getGlConstant() {
			return this.glConstant;
		}
	}

	static class WriteCallback extends STBIWriteCallback {
		private final WritableByteChannel channel;
		@Nullable
		private IOException exception;

		WriteCallback(WritableByteChannel writableByteChannel) {
			this.channel = writableByteChannel;
		}

		public void invoke(long context, long data, int size) {
			ByteBuffer byteBuffer = getData(data, size);

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
