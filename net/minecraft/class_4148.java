package net.minecraft;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.chars.CharArraySet;
import it.unimi.dsi.fastutil.chars.CharSet;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import javax.annotation.Nullable;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;

public class class_4148 implements class_4142 {
	private static final Logger field_20170 = LogManager.getLogger();
	private final STBTTFontinfo field_20171;
	private final float field_20172;
	private final CharSet field_20173 = new CharArraySet();
	private final float field_20174;
	private final float field_20175;
	private final float field_20176;
	private final float field_20177;

	protected class_4148(STBTTFontinfo sTBTTFontinfo, float f, float g, float h, float i, String string) {
		this.field_20171 = sTBTTFontinfo;
		this.field_20172 = g;
		string.chars().forEach(ix -> this.field_20173.add((char)(ix & 65535)));
		this.field_20174 = h * g;
		this.field_20175 = i * g;
		this.field_20176 = STBTruetype.stbtt_ScaleForPixelHeight(sTBTTFontinfo, f * g);
		MemoryStack memoryStack = MemoryStack.stackPush();
		Throwable var8 = null;

		try {
			IntBuffer intBuffer = memoryStack.mallocInt(1);
			IntBuffer intBuffer2 = memoryStack.mallocInt(1);
			IntBuffer intBuffer3 = memoryStack.mallocInt(1);
			STBTruetype.stbtt_GetFontVMetrics(sTBTTFontinfo, intBuffer, intBuffer2, intBuffer3);
			this.field_20177 = (float)intBuffer.get(0) * this.field_20176;
		} catch (Throwable var19) {
			var8 = var19;
			throw var19;
		} finally {
			if (memoryStack != null) {
				if (var8 != null) {
					try {
						memoryStack.close();
					} catch (Throwable var18) {
						var8.addSuppressed(var18);
					}
				} else {
					memoryStack.close();
				}
			}
		}
	}

	@Nullable
	public class_4148.class_4150 method_18486(char c) {
		if (this.field_20173.contains(c)) {
			return null;
		} else {
			MemoryStack memoryStack = MemoryStack.stackPush();
			Throwable var3 = null;

			class_4148.class_4150 var13;
			try {
				IntBuffer intBuffer = memoryStack.mallocInt(1);
				IntBuffer intBuffer2 = memoryStack.mallocInt(1);
				IntBuffer intBuffer3 = memoryStack.mallocInt(1);
				IntBuffer intBuffer4 = memoryStack.mallocInt(1);
				int i = STBTruetype.stbtt_FindGlyphIndex(this.field_20171, c);
				if (i == 0) {
					return null;
				}

				STBTruetype.stbtt_GetGlyphBitmapBoxSubpixel(
					this.field_20171, i, this.field_20176, this.field_20176, this.field_20174, this.field_20175, intBuffer, intBuffer2, intBuffer3, intBuffer4
				);
				int j = intBuffer3.get(0) - intBuffer.get(0);
				int k = intBuffer4.get(0) - intBuffer2.get(0);
				if (j == 0 || k == 0) {
					return null;
				}

				IntBuffer intBuffer5 = memoryStack.mallocInt(1);
				IntBuffer intBuffer6 = memoryStack.mallocInt(1);
				STBTruetype.stbtt_GetGlyphHMetrics(this.field_20171, i, intBuffer5, intBuffer6);
				var13 = new class_4148.class_4150(
					intBuffer.get(0),
					intBuffer3.get(0),
					-intBuffer2.get(0),
					-intBuffer4.get(0),
					(float)intBuffer5.get(0) * this.field_20176,
					(float)intBuffer6.get(0) * this.field_20176,
					i
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

	public static class class_4149 implements class_4143 {
		private final Identifier field_20178;
		private final float field_20179;
		private final float field_20180;
		private final float field_20181;
		private final float field_20182;
		private final String field_20183;

		public class_4149(Identifier identifier, float f, float g, float h, float i, String string) {
			this.field_20178 = identifier;
			this.field_20179 = f;
			this.field_20180 = g;
			this.field_20181 = h;
			this.field_20182 = i;
			this.field_20183 = string;
		}

		public static class_4143 method_18506(JsonObject jsonObject) {
			float f = 0.0F;
			float g = 0.0F;
			if (jsonObject.has("shift")) {
				JsonArray jsonArray = jsonObject.getAsJsonArray("shift");
				if (jsonArray.size() != 2) {
					throw new JsonParseException("Expected 2 elements in 'shift', found " + jsonArray.size());
				}

				f = JsonHelper.asFloat(jsonArray.get(0), "shift[0]");
				g = JsonHelper.asFloat(jsonArray.get(1), "shift[1]");
			}

			StringBuilder stringBuilder = new StringBuilder();
			if (jsonObject.has("skip")) {
				JsonElement jsonElement = jsonObject.get("skip");
				if (jsonElement.isJsonArray()) {
					JsonArray jsonArray2 = JsonHelper.asArray(jsonElement, "skip");

					for (int i = 0; i < jsonArray2.size(); i++) {
						stringBuilder.append(JsonHelper.asString(jsonArray2.get(i), "skip[" + i + "]"));
					}
				} else {
					stringBuilder.append(JsonHelper.asString(jsonElement, "skip"));
				}
			}

			return new class_4148.class_4149(
				new Identifier(JsonHelper.getString(jsonObject, "file")),
				JsonHelper.getFloat(jsonObject, "size", 11.0F),
				JsonHelper.getFloat(jsonObject, "oversample", 1.0F),
				f,
				g,
				stringBuilder.toString()
			);
		}

		@Nullable
		@Override
		public class_4142 method_18487(ResourceManager resourceManager) {
			try {
				Resource resource = resourceManager.getResource(new Identifier(this.field_20178.getNamespace(), "font/" + this.field_20178.getPath()));
				Throwable var3 = null;

				class_4148 var6;
				try {
					class_4148.field_20170.info("Loading font");
					ByteBuffer byteBuffer = TextureUtil.method_19533(resource.getInputStream());
					byteBuffer.flip();
					STBTTFontinfo sTBTTFontinfo = STBTTFontinfo.create();
					class_4148.field_20170.info("Reading font");
					if (!STBTruetype.stbtt_InitFont(sTBTTFontinfo, byteBuffer)) {
						throw new IOException("Invalid ttf");
					}

					var6 = new class_4148(sTBTTFontinfo, this.field_20179, this.field_20180, this.field_20181, this.field_20182, this.field_20183);
				} catch (Throwable var16) {
					var3 = var16;
					throw var16;
				} finally {
					if (resource != null) {
						if (var3 != null) {
							try {
								resource.close();
							} catch (Throwable var15) {
								var3.addSuppressed(var15);
							}
						} else {
							resource.close();
						}
					}
				}

				return var6;
			} catch (IOException var18) {
				class_4148.field_20170.error("Couldn't load truetype font {}", this.field_20178, var18);
				return null;
			}
		}
	}

	class class_4150 implements class_4135 {
		private final int field_20185;
		private final int field_20186;
		private final float field_20187;
		private final float field_20188;
		private final float field_20189;
		private final int field_20190;

		private class_4150(int i, int j, int k, int l, float f, float g, int m) {
			this.field_20185 = j - i;
			this.field_20186 = k - l;
			this.field_20189 = f / class_4148.this.field_20172;
			this.field_20187 = (g + (float)i + class_4148.this.field_20174) / class_4148.this.field_20172;
			this.field_20188 = (class_4148.this.field_20177 - (float)k + class_4148.this.field_20175) / class_4148.this.field_20172;
			this.field_20190 = m;
		}

		@Override
		public int method_18472() {
			return this.field_20185;
		}

		@Override
		public int method_18474() {
			return this.field_20186;
		}

		@Override
		public float method_18476() {
			return class_4148.this.field_20172;
		}

		@Override
		public float getAdvance() {
			return this.field_20189;
		}

		@Override
		public float getBearingX() {
			return this.field_20187;
		}

		@Override
		public float getBearingY() {
			return this.field_20188;
		}

		@Override
		public void method_18473(int i, int j) {
			try (class_4277 lv = new class_4277(class_4277.class_4278.LUMINANCE, this.field_20185, this.field_20186, false)) {
				lv.method_19475(
					class_4148.this.field_20171,
					this.field_20190,
					this.field_20185,
					this.field_20186,
					class_4148.this.field_20176,
					class_4148.this.field_20176,
					class_4148.this.field_20174,
					class_4148.this.field_20175,
					0,
					0
				);
				lv.method_19462(0, i, j, 0, 0, this.field_20185, this.field_20186, false);
			}
		}

		@Override
		public boolean method_18475() {
			return false;
		}
	}
}
