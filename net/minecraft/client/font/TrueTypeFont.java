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
	private final ByteBuffer field_21839;
	private final STBTTFontinfo info;
	private final float oversample;
	private final IntSet excludedCharacters = new IntArraySet();
	private final float shiftX;
	private final float shiftY;
	private final float scaleFactor;
	private final float ascent;

	public TrueTypeFont(ByteBuffer byteBuffer, STBTTFontinfo sTBTTFontinfo, float f, float g, float h, float i, String string) {
		this.field_21839 = byteBuffer;
		this.info = sTBTTFontinfo;
		this.oversample = g;
		string.codePoints().forEach(this.excludedCharacters::add);
		this.shiftX = h * g;
		this.shiftY = i * g;
		this.scaleFactor = STBTruetype.stbtt_ScaleForPixelHeight(sTBTTFontinfo, f * g);
		MemoryStack memoryStack = MemoryStack.stackPush();
		Throwable var9 = null;

		try {
			IntBuffer intBuffer = memoryStack.mallocInt(1);
			IntBuffer intBuffer2 = memoryStack.mallocInt(1);
			IntBuffer intBuffer3 = memoryStack.mallocInt(1);
			STBTruetype.stbtt_GetFontVMetrics(sTBTTFontinfo, intBuffer, intBuffer2, intBuffer3);
			this.ascent = (float)intBuffer.get(0) * this.scaleFactor;
		} catch (Throwable var20) {
			var9 = var20;
			throw var20;
		} finally {
			if (memoryStack != null) {
				if (var9 != null) {
					try {
						memoryStack.close();
					} catch (Throwable var19) {
						var9.addSuppressed(var19);
					}
				} else {
					memoryStack.close();
				}
			}
		}
	}

	@Nullable
	public TrueTypeFont.TtfGlyph getGlyph(int i) {
		if (this.excludedCharacters.contains(i)) {
			return null;
		} else {
			MemoryStack memoryStack = MemoryStack.stackPush();
			Throwable var3 = null;

			TrueTypeFont.TtfGlyph var13;
			try {
				IntBuffer intBuffer = memoryStack.mallocInt(1);
				IntBuffer intBuffer2 = memoryStack.mallocInt(1);
				IntBuffer intBuffer3 = memoryStack.mallocInt(1);
				IntBuffer intBuffer4 = memoryStack.mallocInt(1);
				int j = STBTruetype.stbtt_FindGlyphIndex(this.info, i);
				if (j == 0) {
					return null;
				}

				STBTruetype.stbtt_GetGlyphBitmapBoxSubpixel(
					this.info, j, this.scaleFactor, this.scaleFactor, this.shiftX, this.shiftY, intBuffer, intBuffer2, intBuffer3, intBuffer4
				);
				int k = intBuffer3.get(0) - intBuffer.get(0);
				int l = intBuffer4.get(0) - intBuffer2.get(0);
				if (k == 0 || l == 0) {
					return null;
				}

				IntBuffer intBuffer5 = memoryStack.mallocInt(1);
				IntBuffer intBuffer6 = memoryStack.mallocInt(1);
				STBTruetype.stbtt_GetGlyphHMetrics(this.info, j, intBuffer5, intBuffer6);
				var13 = new TrueTypeFont.TtfGlyph(
					intBuffer.get(0),
					intBuffer3.get(0),
					-intBuffer2.get(0),
					-intBuffer4.get(0),
					(float)intBuffer5.get(0) * this.scaleFactor,
					(float)intBuffer6.get(0) * this.scaleFactor,
					j
				);
			} catch (Throwable var24) {
				var3 = var24;
				throw var24;
			} finally {
				if (memoryStack != null) {
					if (var3 != null) {
						try {
							memoryStack.close();
						} catch (Throwable var23) {
							var3.addSuppressed(var23);
						}
					} else {
						memoryStack.close();
					}
				}
			}

			return var13;
		}
	}

	@Override
	public void close() {
		this.info.free();
		MemoryUtil.memFree(this.field_21839);
	}

	@Override
	public IntSet getProvidedGlyphs() {
		return (IntSet)IntStream.range(0, 65535)
			.filter(i -> !this.excludedCharacters.contains(i))
			.collect(IntOpenHashSet::new, IntCollection::add, IntCollection::addAll);
	}

	class TtfGlyph implements RenderableGlyph {
		private final int width;
		private final int height;
		private final float bearingX;
		private final float ascent;
		private final float advance;
		private final int glyphIndex;

		private TtfGlyph(int xMin, int xMax, int yMax, int yMin, float advance, float bearing, int index) {
			this.width = xMax - xMin;
			this.height = yMax - yMin;
			this.advance = advance / TrueTypeFont.this.oversample;
			this.bearingX = (bearing + (float)xMin + TrueTypeFont.this.shiftX) / TrueTypeFont.this.oversample;
			this.ascent = (TrueTypeFont.this.ascent - (float)yMax + TrueTypeFont.this.shiftY) / TrueTypeFont.this.oversample;
			this.glyphIndex = index;
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
