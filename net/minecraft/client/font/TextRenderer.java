package net.minecraft.client.font;

import com.google.common.collect.Lists;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.class_4131;
import net.minecraft.class_4134;
import net.minecraft.class_4136;
import net.minecraft.class_4142;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextRenderer implements AutoCloseable {
	private static final Logger field_20051 = LogManager.getLogger();
	public int fontHeight = 9;
	public Random random = new Random();
	private final TextureManager textureManager;
	private final class_4131 field_20052;
	private boolean rightToLeft;

	public TextRenderer(TextureManager textureManager, class_4131 arg) {
		this.textureManager = textureManager;
		this.field_20052 = arg;
	}

	public void method_18354(List<class_4142> list) {
		this.field_20052.method_18463(list);
	}

	public void close() {
		this.field_20052.close();
	}

	public int drawWithShadow(String text, float x, float y, int color) {
		GlStateManager.enableAlphaTest();
		return this.drawLayer(text, x, y, color, true);
	}

	public int method_18355(String string, float f, float g, int i) {
		GlStateManager.enableAlphaTest();
		return this.drawLayer(string, f, g, i, false);
	}

	private String mirror(String text) {
		try {
			Bidi bidi = new Bidi(new ArabicShaping(8).shape(text), 127);
			bidi.setReorderingMode(0);
			return bidi.writeReordered(2);
		} catch (ArabicShapingException var3) {
			return text;
		}
	}

	private int drawLayer(String text, float x, float y, int color, boolean shadow) {
		if (text == null) {
			return 0;
		} else {
			if (this.rightToLeft) {
				text = this.mirror(text);
			}

			if ((color & -67108864) == 0) {
				color |= -16777216;
			}

			if (shadow) {
				this.method_18356(text, x, y, color, true);
			}

			x = this.method_18356(text, x, y, color, false);
			return (int)x + (shadow ? 1 : 0);
		}
	}

	private float method_18356(String string, float f, float g, int i, boolean bl) {
		float h = bl ? 0.25F : 1.0F;
		float j = (float)(i >> 16 & 0xFF) / 255.0F * h;
		float k = (float)(i >> 8 & 0xFF) / 255.0F * h;
		float l = (float)(i & 0xFF) / 255.0F * h;
		float m = j;
		float n = k;
		float o = l;
		float p = (float)(i >> 24 & 0xFF) / 255.0F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		Identifier identifier = null;
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
		boolean bl2 = false;
		boolean bl3 = false;
		boolean bl4 = false;
		boolean bl5 = false;
		boolean bl6 = false;
		List<TextRenderer.class_4118> list = Lists.newArrayList();

		for (int q = 0; q < string.length(); q++) {
			char c = string.charAt(q);
			if (c == 167 && q + 1 < string.length()) {
				Formatting formatting = Formatting.method_15104(string.charAt(q + 1));
				if (formatting != null) {
					if (formatting.method_15109()) {
						bl2 = false;
						bl3 = false;
						bl6 = false;
						bl5 = false;
						bl4 = false;
						m = j;
						n = k;
						o = l;
					}

					if (formatting.method_15108() != null) {
						int r = formatting.method_15108();
						m = (float)(r >> 16 & 0xFF) / 255.0F * h;
						n = (float)(r >> 8 & 0xFF) / 255.0F * h;
						o = (float)(r & 0xFF) / 255.0F * h;
					} else if (formatting == Formatting.OBFUSCATED) {
						bl2 = true;
					} else if (formatting == Formatting.BOLD) {
						bl3 = true;
					} else if (formatting == Formatting.STRIKETHROUGH) {
						bl6 = true;
					} else if (formatting == Formatting.UNDERLINE) {
						bl5 = true;
					} else if (formatting == Formatting.ITALIC) {
						bl4 = true;
					}
				}

				q++;
			} else {
				class_4134 lv = this.field_20052.method_18459(c);
				class_4136 lv2 = bl2 && c != ' ' ? this.field_20052.method_18461(lv) : this.field_20052.method_18465(c);
				Identifier identifier2 = lv2.method_18481();
				if (identifier2 != null) {
					if (identifier != identifier2) {
						tessellator.draw();
						this.textureManager.bindTexture(identifier2);
						bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
						identifier = identifier2;
					}

					float s = bl3 ? lv.getBoldOffset() : 0.0F;
					float t = bl ? lv.getShadowOffset() : 0.0F;
					this.method_18353(lv2, bl3, bl4, s, f + t, g + t, bufferBuilder, m, n, o, p);
				}

				float u = lv.getAdvance(bl3);
				float v = bl ? 1.0F : 0.0F;
				if (bl6) {
					list.add(
						new TextRenderer.class_4118(f + v - 1.0F, g + v + (float)this.fontHeight / 2.0F, f + v + u, g + v + (float)this.fontHeight / 2.0F - 1.0F, m, n, o, p)
					);
				}

				if (bl5) {
					list.add(new TextRenderer.class_4118(f + v - 1.0F, g + v + (float)this.fontHeight, f + v + u, g + v + (float)this.fontHeight - 1.0F, m, n, o, p));
				}

				f += u;
			}
		}

		tessellator.draw();
		if (!list.isEmpty()) {
			GlStateManager.disableTexture();
			bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);

			for (TextRenderer.class_4118 lv3 : list) {
				lv3.method_18358(bufferBuilder);
			}

			tessellator.draw();
			GlStateManager.enableTexture();
		}

		return f;
	}

	private void method_18353(class_4136 arg, boolean bl, boolean bl2, float f, float g, float h, BufferBuilder bufferBuilder, float i, float j, float k, float l) {
		arg.method_18482(this.textureManager, bl2, g, h, bufferBuilder, i, j, k, l);
		if (bl) {
			arg.method_18482(this.textureManager, bl2, g + f, h, bufferBuilder, i, j, k, l);
		}
	}

	public int getStringWidth(String text) {
		if (text == null) {
			return 0;
		} else {
			float f = 0.0F;
			boolean bl = false;

			for (int i = 0; i < text.length(); i++) {
				char c = text.charAt(i);
				if (c == 167 && i < text.length() - 1) {
					Formatting formatting = Formatting.method_15104(text.charAt(++i));
					if (formatting == Formatting.BOLD) {
						bl = true;
					} else if (formatting != null && formatting.method_15109()) {
						bl = false;
					}
				} else {
					f += this.field_20052.method_18459(c).getAdvance(bl);
				}
			}

			return MathHelper.ceil(f);
		}
	}

	private float method_18352(char c) {
		return c == 167 ? 0.0F : (float)MathHelper.ceil(this.field_20052.method_18459(c).getAdvance(false));
	}

	public String trimToWidth(String text, int width) {
		return this.trimToWidth(text, width, false);
	}

	public String trimToWidth(String text, int width, boolean backwards) {
		StringBuilder stringBuilder = new StringBuilder();
		float f = 0.0F;
		int i = backwards ? text.length() - 1 : 0;
		int j = backwards ? -1 : 1;
		boolean bl = false;
		boolean bl2 = false;

		for (int k = i; k >= 0 && k < text.length() && f < (float)width; k += j) {
			char c = text.charAt(k);
			if (bl) {
				bl = false;
				Formatting formatting = Formatting.method_15104(c);
				if (formatting == Formatting.BOLD) {
					bl2 = true;
				} else if (formatting != null && formatting.method_15109()) {
					bl2 = false;
				}
			} else if (c == 167) {
				bl = true;
			} else {
				f += this.method_18352(c);
				if (bl2) {
					f++;
				}
			}

			if (f > (float)width) {
				break;
			}

			if (backwards) {
				stringBuilder.insert(0, c);
			} else {
				stringBuilder.append(c);
			}
		}

		return stringBuilder.toString();
	}

	private String trimEndNewlines(String text) {
		while (text != null && text.endsWith("\n")) {
			text = text.substring(0, text.length() - 1);
		}

		return text;
	}

	public void drawTrimmed(String text, int x, int y, int maxWidth, int color) {
		text = this.trimEndNewlines(text);
		this.method_18357(text, x, y, maxWidth, color);
	}

	private void method_18357(String string, int i, int j, int k, int l) {
		for (String string2 : this.wrapLines(string, k)) {
			float f = (float)i;
			if (this.rightToLeft) {
				int m = this.getStringWidth(this.mirror(string2));
				f += (float)(k - m);
			}

			this.drawLayer(string2, f, (float)j, l, false);
			j += this.fontHeight;
		}
	}

	public int getHeightSplit(String text, int width) {
		return this.fontHeight * this.wrapLines(text, width).size();
	}

	public void setRightToLeft(boolean rightToLeft) {
		this.rightToLeft = rightToLeft;
	}

	public List<String> wrapLines(String text, int width) {
		return Arrays.asList(this.wrapStringToWidth(text, width).split("\n"));
	}

	public String wrapStringToWidth(String text, int width) {
		String string = "";

		while (!text.isEmpty()) {
			int i = this.getCharacterCountForWidth(text, width);
			if (text.length() <= i) {
				return string + text;
			}

			String string2 = text.substring(0, i);
			char c = text.charAt(i);
			boolean bl = c == ' ' || c == '\n';
			text = Formatting.method_15106(string2) + text.substring(i + (bl ? 1 : 0));
			string = string + string2 + "\n";
		}

		return string;
	}

	private int getCharacterCountForWidth(String text, int offset) {
		int i = Math.max(1, offset);
		int j = text.length();
		float f = 0.0F;
		int k = 0;
		int l = -1;
		boolean bl = false;

		for (boolean bl2 = true; k < j; k++) {
			char c = text.charAt(k);
			switch (c) {
				case '\n':
					k--;
					break;
				case ' ':
					l = k;
				default:
					if (f != 0.0F) {
						bl2 = false;
					}

					f += this.method_18352(c);
					if (bl) {
						f++;
					}
					break;
				case '§':
					if (k < j - 1) {
						Formatting formatting = Formatting.method_15104(text.charAt(++k));
						if (formatting == Formatting.BOLD) {
							bl = true;
						} else if (formatting != null && formatting.method_15109()) {
							bl = false;
						}
					}
			}

			if (c == '\n') {
				l = ++k;
				break;
			}

			if (f > (float)i) {
				if (bl2) {
					k++;
				}
				break;
			}
		}

		return k != j && l != -1 && l < k ? l : k;
	}

	public boolean isRightToLeft() {
		return this.rightToLeft;
	}

	static class class_4118 {
		protected final float field_20053;
		protected final float field_20054;
		protected final float field_20055;
		protected final float field_20056;
		protected final float field_20057;
		protected final float field_20058;
		protected final float field_20059;
		protected final float field_20060;

		private class_4118(float f, float g, float h, float i, float j, float k, float l, float m) {
			this.field_20053 = f;
			this.field_20054 = g;
			this.field_20055 = h;
			this.field_20056 = i;
			this.field_20057 = j;
			this.field_20058 = k;
			this.field_20059 = l;
			this.field_20060 = m;
		}

		public void method_18358(BufferBuilder bufferBuilder) {
			bufferBuilder.vertex((double)this.field_20053, (double)this.field_20054, 0.0)
				.color(this.field_20057, this.field_20058, this.field_20059, this.field_20060)
				.next();
			bufferBuilder.vertex((double)this.field_20055, (double)this.field_20054, 0.0)
				.color(this.field_20057, this.field_20058, this.field_20059, this.field_20060)
				.next();
			bufferBuilder.vertex((double)this.field_20055, (double)this.field_20056, 0.0)
				.color(this.field_20057, this.field_20058, this.field_20059, this.field_20060)
				.next();
			bufferBuilder.vertex((double)this.field_20053, (double)this.field_20056, 0.0)
				.color(this.field_20057, this.field_20058, this.field_20059, this.field_20060)
				.next();
		}
	}
}
