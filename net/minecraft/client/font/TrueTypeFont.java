package net.minecraft.client.font;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.client.texture.NativeImage;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class TrueTypeFont implements Font {
	private final ByteBuffer buffer;
	final STBTTFontinfo info;
	final float oversample;
	private final IntSet excludedCharacters = new IntArraySet();
	final float shiftX;
	final float shiftY;
	final float scaleFactor;
	final float ascent;

	public TrueTypeFont(ByteBuffer buffer, STBTTFontinfo info, float f, float oversample, float g, float h, String excludedCharacters) {
		this.buffer = buffer;
		this.info = info;
		this.oversample = oversample;
		excludedCharacters.codePoints().forEach(this.excludedCharacters::add);
		this.shiftX = g * oversample;
		this.shiftY = h * oversample;
		this.scaleFactor = STBTruetype.stbtt_ScaleForPixelHeight(info, f * oversample);
		MemoryStack memoryStack = MemoryStack.stackPush();

		try {
			IntBuffer intBuffer = memoryStack.mallocInt(1);
			IntBuffer intBuffer2 = memoryStack.mallocInt(1);
			IntBuffer intBuffer3 = memoryStack.mallocInt(1);
			STBTruetype.stbtt_GetFontVMetrics(info, intBuffer, intBuffer2, intBuffer3);
			this.ascent = (float)intBuffer.get(0) * this.scaleFactor;
		} catch (Throwable var13) {
			if (memoryStack != null) {
				try {
					memoryStack.close();
				} catch (Throwable var12) {
					var13.addSuppressed(var12);
				}
			}

			throw var13;
		}

		if (memoryStack != null) {
			memoryStack.close();
		}
	}

	@Nullable
	public TrueTypeFont.TtfGlyph getGlyph(int i) {
		if (this.excludedCharacters.contains(i)) {
			return null;
		} else {
			MemoryStack memoryStack = MemoryStack.stackPush();

			Object var15;
			label61: {
				TrueTypeFont.TtfGlyph var12;
				label62: {
					Object intBuffer5;
					try {
						IntBuffer intBuffer = memoryStack.mallocInt(1);
						IntBuffer intBuffer2 = memoryStack.mallocInt(1);
						IntBuffer intBuffer3 = memoryStack.mallocInt(1);
						IntBuffer intBuffer4 = memoryStack.mallocInt(1);
						int j = STBTruetype.stbtt_FindGlyphIndex(this.info, i);
						if (j == 0) {
							var15 = null;
							break label61;
						}

						STBTruetype.stbtt_GetGlyphBitmapBoxSubpixel(
							this.info, j, this.scaleFactor, this.scaleFactor, this.shiftX, this.shiftY, intBuffer, intBuffer2, intBuffer3, intBuffer4
						);
						int k = intBuffer3.get(0) - intBuffer.get(0);
						int l = intBuffer4.get(0) - intBuffer2.get(0);
						if (k > 0 && l > 0) {
							IntBuffer intBuffer5x = memoryStack.mallocInt(1);
							IntBuffer intBuffer6 = memoryStack.mallocInt(1);
							STBTruetype.stbtt_GetGlyphHMetrics(this.info, j, intBuffer5x, intBuffer6);
							var12 = new TrueTypeFont.TtfGlyph(
								intBuffer.get(0),
								intBuffer3.get(0),
								-intBuffer2.get(0),
								-intBuffer4.get(0),
								(float)intBuffer5x.get(0) * this.scaleFactor,
								(float)intBuffer6.get(0) * this.scaleFactor,
								j
							);
							break label62;
						}

						intBuffer5 = null;
					} catch (Throwable var14) {
						if (memoryStack != null) {
							try {
								memoryStack.close();
							} catch (Throwable var13) {
								var14.addSuppressed(var13);
							}
						}

						throw var14;
					}

					if (memoryStack != null) {
						memoryStack.close();
					}

					return (TrueTypeFont.TtfGlyph)intBuffer5;
				}

				if (memoryStack != null) {
					memoryStack.close();
				}

				return var12;
			}

			if (memoryStack != null) {
				memoryStack.close();
			}

			return (TrueTypeFont.TtfGlyph)var15;
		}
	}

	@Override
	public void close() {
		this.info.free();
		MemoryUtil.memFree(this.buffer);
	}

	@Override
	public IntSet getProvidedGlyphs() {
		return (IntSet)IntStream.range(0, 65535)
			.filter(codePoint -> !this.excludedCharacters.contains(codePoint))
			.collect(IntOpenHashSet::new, IntCollection::add, IntCollection::addAll);
	}

	class TtfGlyph implements RenderableGlyph {
		private final int width;
		private final int height;
		private final float bearingX;
		private final float ascent;
		private final float advance;
		private final int glyphIndex;

		TtfGlyph(int i, int j, int k, int l, float f, float g, int m) {
			this.width = j - i;
			this.height = k - l;
			this.advance = f / TrueTypeFont.this.oversample;
			this.bearingX = (g + (float)i + TrueTypeFont.this.shiftX) / TrueTypeFont.this.oversample;
			this.ascent = (TrueTypeFont.this.ascent - (float)k + TrueTypeFont.this.shiftY) / TrueTypeFont.this.oversample;
			this.glyphIndex = m;
		}

		@Override
		public int getWidth() {
			return this.width;
		}

		@Override
		public int getHeight() {
			return this.height;
		}

		@Override
		public float getOversample() {
			return TrueTypeFont.this.oversample;
		}

		@Override
		public float getAdvance() {
			return this.advance;
		}

		@Override
		public float getBearingX() {
			return this.bearingX;
		}

		@Override
		public float getAscent() {
			return this.ascent;
		}

		@Override
		public void upload(int x, int y) {
			NativeImage nativeImage = new NativeImage(NativeImage.Format.LUMINANCE, this.width, this.height, false);
			nativeImage.makeGlyphBitmapSubpixel(
				TrueTypeFont.this.info,
				this.glyphIndex,
				this.width,
				this.height,
				TrueTypeFont.this.scaleFactor,
				TrueTypeFont.this.scaleFactor,
				TrueTypeFont.this.shiftX,
				TrueTypeFont.this.shiftY,
				0,
				0
			);
			nativeImage.upload(0, x, y, 0, 0, this.width, this.height, false, true);
		}

		@Override
		public boolean hasColor() {
			return false;
		}
	}
}
