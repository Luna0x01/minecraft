package net.minecraft.client.font;

import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import com.mojang.blaze3d.platform.GlStateManager;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;

public class TextRenderer implements ResourceReloadListener {
	private static final Identifier[] PAGES = new Identifier[256];
	private int[] characterWidths = new int[256];
	public int fontHeight = 9;
	public Random random = new Random();
	private byte[] glyphWidths = new byte[65536];
	private int[] colorCodes = new int[32];
	private final Identifier fontTexture;
	private final TextureManager textureManager;
	private float x;
	private float y;
	private boolean unicode;
	private boolean rightToLeft;
	private float red;
	private float green;
	private float blue;
	private float alpha;
	private int color;
	private boolean obfuscated;
	private boolean bold;
	private boolean italic;
	private boolean underline;
	private boolean strikethrough;

	public TextRenderer(GameOptions gameOptions, Identifier identifier, TextureManager textureManager, boolean bl) {
		this.fontTexture = identifier;
		this.textureManager = textureManager;
		this.unicode = bl;
		textureManager.bindTexture(this.fontTexture);

		for (int i = 0; i < 32; i++) {
			int j = (i >> 3 & 1) * 85;
			int k = (i >> 2 & 1) * 170 + j;
			int l = (i >> 1 & 1) * 170 + j;
			int m = (i >> 0 & 1) * 170 + j;
			if (i == 6) {
				k += 85;
			}

			if (gameOptions.anaglyph3d) {
				int n = (k * 30 + l * 59 + m * 11) / 100;
				int o = (k * 30 + l * 70) / 100;
				int p = (k * 30 + m * 70) / 100;
				k = n;
				l = o;
				m = p;
			}

			if (i >= 16) {
				k /= 4;
				l /= 4;
				m /= 4;
			}

			this.colorCodes[i] = (k & 0xFF) << 16 | (l & 0xFF) << 8 | m & 0xFF;
		}

		this.readGlyphSizes();
	}

	@Override
	public void reload(ResourceManager resourceManager) {
		this.init();
	}

	private void init() {
		BufferedImage bufferedImage;
		try {
			bufferedImage = TextureUtil.create(MinecraftClient.getInstance().getResourceManager().getResource(this.fontTexture).getInputStream());
		} catch (IOException var17) {
			throw new RuntimeException(var17);
		}

		int i = bufferedImage.getWidth();
		int j = bufferedImage.getHeight();
		int[] is = new int[i * j];
		bufferedImage.getRGB(0, 0, i, j, is, 0, i);
		int k = j / 16;
		int l = i / 16;
		int m = 1;
		float f = 8.0F / (float)l;

		for (int n = 0; n < 256; n++) {
			int o = n % 16;
			int p = n / 16;
			if (n == 32) {
				this.characterWidths[n] = 3 + m;
			}

			int q;
			for (q = l - 1; q >= 0; q--) {
				int r = o * l + q;
				boolean bl = true;

				for (int s = 0; s < k && bl; s++) {
					int t = (p * l + s) * i;
					if ((is[r + t] >> 24 & 0xFF) != 0) {
						bl = false;
					}
				}

				if (!bl) {
					break;
				}
			}

			this.characterWidths[n] = (int)(0.5 + (double)((float)(++q) * f)) + m;
		}
	}

	private void readGlyphSizes() {
		InputStream inputStream = null;

		try {
			inputStream = MinecraftClient.getInstance().getResourceManager().getResource(new Identifier("font/glyph_sizes.bin")).getInputStream();
			inputStream.read(this.glyphWidths);
		} catch (IOException var6) {
			throw new RuntimeException(var6);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}

	private float drawLayer(char character, boolean italic) {
		if (character == ' ') {
			return 4.0F;
		} else {
			int i = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000"
				.indexOf(character);
			return i != -1 && !this.unicode ? this.drawLayerNormal(i, italic) : this.drawLayerUnicode(character, italic);
		}
	}

	private float drawLayerNormal(int characterIndex, boolean italic) {
		int i = characterIndex % 16 * 8;
		int j = characterIndex / 16 * 8;
		int k = italic ? 1 : 0;
		this.textureManager.bindTexture(this.fontTexture);
		int l = this.characterWidths[characterIndex];
		float f = (float)l - 0.01F;
		GL11.glBegin(5);
		GL11.glTexCoord2f((float)i / 128.0F, (float)j / 128.0F);
		GL11.glVertex3f(this.x + (float)k, this.y, 0.0F);
		GL11.glTexCoord2f((float)i / 128.0F, ((float)j + 7.99F) / 128.0F);
		GL11.glVertex3f(this.x - (float)k, this.y + 7.99F, 0.0F);
		GL11.glTexCoord2f(((float)i + f - 1.0F) / 128.0F, (float)j / 128.0F);
		GL11.glVertex3f(this.x + f - 1.0F + (float)k, this.y, 0.0F);
		GL11.glTexCoord2f(((float)i + f - 1.0F) / 128.0F, ((float)j + 7.99F) / 128.0F);
		GL11.glVertex3f(this.x + f - 1.0F - (float)k, this.y + 7.99F, 0.0F);
		GL11.glEnd();
		return (float)l;
	}

	private Identifier getFontPage(int page) {
		if (PAGES[page] == null) {
			PAGES[page] = new Identifier(String.format("textures/font/unicode_page_%02x.png", page));
		}

		return PAGES[page];
	}

	private void bindPageTexture(int index) {
		this.textureManager.bindTexture(this.getFontPage(index));
	}

	private float drawLayerUnicode(char character, boolean italic) {
		if (this.glyphWidths[character] == 0) {
			return 0.0F;
		} else {
			int i = character / 256;
			this.bindPageTexture(i);
			int j = this.glyphWidths[character] >>> 4;
			int k = this.glyphWidths[character] & 15;
			float f = (float)j;
			float g = (float)(k + 1);
			float h = (float)(character % 16 * 16) + f;
			float l = (float)((character & 255) / 16 * 16);
			float m = g - f - 0.02F;
			float n = italic ? 1.0F : 0.0F;
			GL11.glBegin(5);
			GL11.glTexCoord2f(h / 256.0F, l / 256.0F);
			GL11.glVertex3f(this.x + n, this.y, 0.0F);
			GL11.glTexCoord2f(h / 256.0F, (l + 15.98F) / 256.0F);
			GL11.glVertex3f(this.x - n, this.y + 7.99F, 0.0F);
			GL11.glTexCoord2f((h + m) / 256.0F, l / 256.0F);
			GL11.glVertex3f(this.x + m / 2.0F + n, this.y, 0.0F);
			GL11.glTexCoord2f((h + m) / 256.0F, (l + 15.98F) / 256.0F);
			GL11.glVertex3f(this.x + m / 2.0F - n, this.y + 7.99F, 0.0F);
			GL11.glEnd();
			return (g - f) / 2.0F + 1.0F;
		}
	}

	public int drawWithShadow(String text, float x, float y, int color) {
		return this.draw(text, x, y, color, true);
	}

	public int draw(String text, int x, int y, int color) {
		return this.draw(text, (float)x, (float)y, color, false);
	}

	public int draw(String text, float x, float y, int color, boolean shadow) {
		GlStateManager.enableAlphaTest();
		this.resetState();
		int j;
		if (shadow) {
			j = this.drawLayer(text, x + 1.0F, y + 1.0F, color, true);
			j = Math.max(j, this.drawLayer(text, x, y, color, false));
		} else {
			j = this.drawLayer(text, x, y, color, false);
		}

		return j;
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

	private void resetState() {
		this.obfuscated = false;
		this.bold = false;
		this.italic = false;
		this.underline = false;
		this.strikethrough = false;
	}

	private void draw(String text, boolean shadow) {
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c == 167 && i + 1 < text.length()) {
				int j = "0123456789abcdefklmnor".indexOf(text.toLowerCase(Locale.ENGLISH).charAt(i + 1));
				if (j < 16) {
					this.obfuscated = false;
					this.bold = false;
					this.strikethrough = false;
					this.underline = false;
					this.italic = false;
					if (j < 0 || j > 15) {
						j = 15;
					}

					if (shadow) {
						j += 16;
					}

					int k = this.colorCodes[j];
					this.color = k;
					GlStateManager.color((float)(k >> 16) / 255.0F, (float)(k >> 8 & 0xFF) / 255.0F, (float)(k & 0xFF) / 255.0F, this.alpha);
				} else if (j == 16) {
					this.obfuscated = true;
				} else if (j == 17) {
					this.bold = true;
				} else if (j == 18) {
					this.strikethrough = true;
				} else if (j == 19) {
					this.underline = true;
				} else if (j == 20) {
					this.italic = true;
				} else if (j == 21) {
					this.obfuscated = false;
					this.bold = false;
					this.strikethrough = false;
					this.underline = false;
					this.italic = false;
					GlStateManager.color(this.red, this.green, this.blue, this.alpha);
				}

				i++;
			} else {
				int l = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000"
					.indexOf(c);
				if (this.obfuscated && l != -1) {
					int m = this.getCharWidth(c);

					char d;
					do {
						l = this.random
							.nextInt(
								"ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000"
									.length()
							);
						d = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000"
							.charAt(l);
					} while (m != this.getCharWidth(d));

					c = d;
				}

				float f = this.unicode ? 0.5F : 1.0F;
				boolean bl = (c == 0 || l == -1 || this.unicode) && shadow;
				if (bl) {
					this.x -= f;
					this.y -= f;
				}

				float g = this.drawLayer(c, this.italic);
				if (bl) {
					this.x += f;
					this.y += f;
				}

				if (this.bold) {
					this.x += f;
					if (bl) {
						this.x -= f;
						this.y -= f;
					}

					this.drawLayer(c, this.italic);
					this.x -= f;
					if (bl) {
						this.x += f;
						this.y += f;
					}

					g++;
				}

				if (this.strikethrough) {
					Tessellator tessellator = Tessellator.getInstance();
					BufferBuilder bufferBuilder = tessellator.getBuffer();
					GlStateManager.disableTexture();
					bufferBuilder.begin(7, VertexFormats.POSITION);
					bufferBuilder.vertex((double)this.x, (double)(this.y + (float)(this.fontHeight / 2)), 0.0).next();
					bufferBuilder.vertex((double)(this.x + g), (double)(this.y + (float)(this.fontHeight / 2)), 0.0).next();
					bufferBuilder.vertex((double)(this.x + g), (double)(this.y + (float)(this.fontHeight / 2) - 1.0F), 0.0).next();
					bufferBuilder.vertex((double)this.x, (double)(this.y + (float)(this.fontHeight / 2) - 1.0F), 0.0).next();
					tessellator.draw();
					GlStateManager.enableTexture();
				}

				if (this.underline) {
					Tessellator tessellator2 = Tessellator.getInstance();
					BufferBuilder bufferBuilder2 = tessellator2.getBuffer();
					GlStateManager.disableTexture();
					bufferBuilder2.begin(7, VertexFormats.POSITION);
					int n = this.underline ? -1 : 0;
					bufferBuilder2.vertex((double)(this.x + (float)n), (double)(this.y + (float)this.fontHeight), 0.0).next();
					bufferBuilder2.vertex((double)(this.x + g), (double)(this.y + (float)this.fontHeight), 0.0).next();
					bufferBuilder2.vertex((double)(this.x + g), (double)(this.y + (float)this.fontHeight - 1.0F), 0.0).next();
					bufferBuilder2.vertex((double)(this.x + (float)n), (double)(this.y + (float)this.fontHeight - 1.0F), 0.0).next();
					tessellator2.draw();
					GlStateManager.enableTexture();
				}

				this.x += (float)((int)g);
			}
		}
	}

	private int drawLayer(String text, int x, int y, int width, int color, boolean shadow) {
		if (this.rightToLeft) {
			int i = this.getStringWidth(this.mirror(text));
			x = x + width - i;
		}

		return this.drawLayer(text, (float)x, (float)y, color, shadow);
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
				color = (color & 16579836) >> 2 | color & 0xFF000000;
			}

			this.red = (float)(color >> 16 & 0xFF) / 255.0F;
			this.green = (float)(color >> 8 & 0xFF) / 255.0F;
			this.blue = (float)(color & 0xFF) / 255.0F;
			this.alpha = (float)(color >> 24 & 0xFF) / 255.0F;
			GlStateManager.color(this.red, this.green, this.blue, this.alpha);
			this.x = x;
			this.y = y;
			this.draw(text, shadow);
			return (int)this.x;
		}
	}

	public int getStringWidth(String text) {
		if (text == null) {
			return 0;
		} else {
			int i = 0;
			boolean bl = false;

			for (int j = 0; j < text.length(); j++) {
				char c = text.charAt(j);
				int k = this.getCharWidth(c);
				if (k < 0 && j < text.length() - 1) {
					c = text.charAt(++j);
					if (c == 'l' || c == 'L') {
						bl = true;
					} else if (c == 'r' || c == 'R') {
						bl = false;
					}

					k = 0;
				}

				i += k;
				if (bl && k > 0) {
					i++;
				}
			}

			return i;
		}
	}

	public int getCharWidth(char character) {
		if (character == 167) {
			return -1;
		} else if (character == ' ') {
			return 4;
		} else {
			int i = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000"
				.indexOf(character);
			if (character > 0 && i != -1 && !this.unicode) {
				return this.characterWidths[i];
			} else if (this.glyphWidths[character] != 0) {
				int j = this.glyphWidths[character] >>> 4;
				int k = this.glyphWidths[character] & 15;
				if (k > 7) {
					k = 15;
					j = 0;
				}

				k++;
				return (k - j) / 2 + 1;
			} else {
				return 0;
			}
		}
	}

	public String trimToWidth(String text, int width) {
		return this.trimToWidth(text, width, false);
	}

	public String trimToWidth(String text, int width, boolean backwards) {
		StringBuilder stringBuilder = new StringBuilder();
		int i = 0;
		int j = backwards ? text.length() - 1 : 0;
		int k = backwards ? -1 : 1;
		boolean bl = false;
		boolean bl2 = false;

		for (int l = j; l >= 0 && l < text.length() && i < width; l += k) {
			char c = text.charAt(l);
			int m = this.getCharWidth(c);
			if (bl) {
				bl = false;
				if (c == 'l' || c == 'L') {
					bl2 = true;
				} else if (c == 'r' || c == 'R') {
					bl2 = false;
				}
			} else if (m < 0) {
				bl = true;
			} else {
				i += m;
				if (bl2) {
					i++;
				}
			}

			if (i > width) {
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
		this.resetState();
		this.color = color;
		text = this.trimEndNewlines(text);
		this.drawTrimmed(text, x, y, maxWidth, false);
	}

	private void drawTrimmed(String text, int x, int y, int maxWidth, boolean shadow) {
		for (String string : this.wrapLines(text, maxWidth)) {
			this.drawLayer(string, x, y, maxWidth, this.color, shadow);
			y += this.fontHeight;
		}
	}

	public int getHeightSplit(String text, int width) {
		return this.fontHeight * this.wrapLines(text, width).size();
	}

	public void setUnicode(boolean unicode) {
		this.unicode = unicode;
	}

	public boolean isUnicode() {
		return this.unicode;
	}

	public void setRightToLeft(boolean rightToLeft) {
		this.rightToLeft = rightToLeft;
	}

	public List<String> wrapLines(String text, int width) {
		return Arrays.asList(this.wrapStringToWidth(text, width).split("\n"));
	}

	String wrapStringToWidth(String text, int width) {
		int i = this.getCharacterCountForWidth(text, width);
		if (text.length() <= i) {
			return text;
		} else {
			String string = text.substring(0, i);
			char c = text.charAt(i);
			boolean bl = c == ' ' || c == '\n';
			String string2 = getFormattingOnly(string) + text.substring(i + (bl ? 1 : 0));
			return string + "\n" + this.wrapStringToWidth(string2, width);
		}
	}

	private int getCharacterCountForWidth(String text, int offset) {
		int i = text.length();
		int j = 0;
		int k = 0;
		int l = -1;

		for (boolean bl = false; k < i; k++) {
			char c = text.charAt(k);
			switch (c) {
				case '\n':
					k--;
					break;
				case ' ':
					l = k;
				default:
					j += this.getCharWidth(c);
					if (bl) {
						j++;
					}
					break;
				case '§':
					if (k < i - 1) {
						char d = text.charAt(++k);
						if (d == 'l' || d == 'L') {
							bl = true;
						} else if (d == 'r' || d == 'R' || isColor(d)) {
							bl = false;
						}
					}
			}

			if (c == '\n') {
				l = ++k;
				break;
			}

			if (j > offset) {
				break;
			}
		}

		return k != i && l != -1 && l < k ? l : k;
	}

	private static boolean isColor(char character) {
		return character >= '0' && character <= '9' || character >= 'a' && character <= 'f' || character >= 'A' && character <= 'F';
	}

	private static boolean isSpecial(char character) {
		return character >= 'k' && character <= 'o' || character >= 'K' && character <= 'O' || character == 'r' || character == 'R';
	}

	public static String getFormattingOnly(String text) {
		String string = "";
		int i = -1;
		int j = text.length();

		while ((i = text.indexOf(167, i + 1)) != -1) {
			if (i < j - 1) {
				char c = text.charAt(i + 1);
				if (isColor(c)) {
					string = "§" + c;
				} else if (isSpecial(c)) {
					string = string + "§" + c;
				}
			}
		}

		return string;
	}

	public boolean isRightToLeft() {
		return this.rightToLeft;
	}

	public int getColor(char colorChar) {
		return this.colorCodes["0123456789abcdef".indexOf(colorChar)];
	}
}
