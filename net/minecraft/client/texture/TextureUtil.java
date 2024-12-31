package net.minecraft.client.texture;

import com.mojang.blaze3d.platform.GlStateManager;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import javax.imageio.ImageIO;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextureUtil {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final IntBuffer BUFFER = GlAllocationUtils.allocateIntBuffer(4194304);
	public static final NativeImageBackedTexture MISSING_TEXTURE = new NativeImageBackedTexture(16, 16);
	public static final int[] field_6583 = MISSING_TEXTURE.getPixels();
	private static final float[] field_13653;
	private static final int[] field_8104;

	private static float method_12494(int i) {
		return field_13653[i & 0xFF];
	}

	public static int getTexLevelParameter() {
		return GlStateManager.getTexLevelParameter();
	}

	public static void deleteTexture(int id) {
		GlStateManager.deleteTexture(id);
	}

	public static int method_5858(int i, BufferedImage bufferedImage) {
		return method_5860(i, bufferedImage, false, false);
	}

	public static void method_5861(int texture, int[] is, int i, int j) {
		bindTexture(texture);
		method_7022(0, is, i, j, 0, 0, false, false, false);
	}

	public static int[][] method_7021(int i, int j, int[][] is) {
		int[][] js = new int[i + 1][];
		js[0] = is[0];
		if (i > 0) {
			boolean bl = false;

			for (int k = 0; k < is.length; k++) {
				if (is[0][k] >> 24 == 0) {
					bl = true;
					break;
				}
			}

			for (int l = 1; l <= i; l++) {
				if (is[l] != null) {
					js[l] = is[l];
				} else {
					int[] ks = js[l - 1];
					int[] ls = new int[ks.length >> 2];
					int m = j >> l;
					int n = ls.length / m;
					int o = m << 1;

					for (int p = 0; p < m; p++) {
						for (int q = 0; q < n; q++) {
							int r = 2 * (p + q * o);
							ls[p + q * m] = method_7020(ks[r + 0], ks[r + 1], ks[r + 0 + o], ks[r + 1 + o], bl);
						}
					}

					js[l] = ls;
				}
			}
		}

		return js;
	}

	private static int method_7020(int i, int j, int k, int l, boolean bl) {
		if (bl) {
			field_8104[0] = i;
			field_8104[1] = j;
			field_8104[2] = k;
			field_8104[3] = l;
			float f = 0.0F;
			float g = 0.0F;
			float h = 0.0F;
			float m = 0.0F;

			for (int n = 0; n < 4; n++) {
				if (field_8104[n] >> 24 != 0) {
					f += method_12494(field_8104[n] >> 24);
					g += method_12494(field_8104[n] >> 16);
					h += method_12494(field_8104[n] >> 8);
					m += method_12494(field_8104[n] >> 0);
				}
			}

			f /= 4.0F;
			g /= 4.0F;
			h /= 4.0F;
			m /= 4.0F;
			int o = (int)(Math.pow((double)f, 0.45454545454545453) * 255.0);
			int p = (int)(Math.pow((double)g, 0.45454545454545453) * 255.0);
			int q = (int)(Math.pow((double)h, 0.45454545454545453) * 255.0);
			int r = (int)(Math.pow((double)m, 0.45454545454545453) * 255.0);
			if (o < 96) {
				o = 0;
			}

			return o << 24 | p << 16 | q << 8 | r;
		} else {
			int s = method_7019(i, j, k, l, 24);
			int t = method_7019(i, j, k, l, 16);
			int u = method_7019(i, j, k, l, 8);
			int v = method_7019(i, j, k, l, 0);
			return s << 24 | t << 16 | u << 8 | v;
		}
	}

	private static int method_7019(int i, int j, int k, int l, int m) {
		float f = method_12494(i >> m);
		float g = method_12494(j >> m);
		float h = method_12494(k >> m);
		float n = method_12494(l >> m);
		float o = (float)((double)((float)Math.pow((double)(f + g + h + n) * 0.25, 0.45454545454545453)));
		return (int)((double)o * 255.0);
	}

	public static void method_7027(int[][] is, int i, int j, int k, int l, boolean bl, boolean bl2) {
		for (int m = 0; m < is.length; m++) {
			int[] js = is[m];
			method_7022(m, js, i >> m, j >> m, k >> m, l >> m, bl, bl2, is.length > 1);
		}
	}

	private static void method_7022(int i, int[] is, int j, int k, int l, int m, boolean bl, boolean bl2, boolean bl3) {
		int n = 4194304 / j;
		setTextureScaling(bl, bl3);
		setTextureWrapping(bl2);
		int o = 0;

		while (o < j * k) {
			int p = o / j;
			int q = Math.min(n, k - p);
			int r = j * q;
			method_5867(is, o, r);
			GlStateManager.method_12295(3553, i, l, m + p, j, q, 32993, 33639, BUFFER);
			o += j * q;
		}
	}

	public static int method_5860(int id, BufferedImage image, boolean linear, boolean clamp) {
		prepareImage(id, image.getWidth(), image.getHeight());
		return method_5859(id, image, 0, 0, linear, clamp);
	}

	public static void prepareImage(int id, int width, int height) {
		prepareImage(id, 0, width, height);
	}

	public static void prepareImage(int id, int maxLevel, int width, int height) {
		deleteTexture(id);
		bindTexture(id);
		if (maxLevel >= 0) {
			GlStateManager.method_12294(3553, 33085, maxLevel);
			GlStateManager.method_12294(3553, 33082, 0);
			GlStateManager.method_12294(3553, 33083, maxLevel);
			GlStateManager.method_12293(3553, 34049, 0.0F);
		}

		for (int i = 0; i <= maxLevel; i++) {
			GlStateManager.method_12276(3553, i, 6408, width >> i, height >> i, 0, 32993, 33639, null);
		}
	}

	public static int method_5859(int id, BufferedImage image, int offsetX, int offsetY, boolean linear, boolean clamp) {
		bindTexture(id);
		method_5863(image, offsetX, offsetY, linear, clamp);
		return id;
	}

	private static void method_5863(BufferedImage image, int offsetX, int offsetY, boolean linear, boolean clamp) {
		int i = image.getWidth();
		int j = image.getHeight();
		int k = 4194304 / i;
		int[] is = new int[k * i];
		setTextureScaling(linear);
		setTextureWrapping(clamp);

		for (int l = 0; l < i * j; l += i * k) {
			int m = l / i;
			int n = Math.min(k, j - m);
			int o = i * n;
			image.getRGB(0, m, i, n, is, 0, i);
			method_5866(is, o);
			GlStateManager.method_12295(3553, 0, offsetX, offsetY + m, i, n, 32993, 33639, BUFFER);
		}
	}

	private static void setTextureWrapping(boolean clamp) {
		if (clamp) {
			GlStateManager.method_12294(3553, 10242, 10496);
			GlStateManager.method_12294(3553, 10243, 10496);
		} else {
			GlStateManager.method_12294(3553, 10242, 10497);
			GlStateManager.method_12294(3553, 10243, 10497);
		}
	}

	private static void setTextureScaling(boolean linear) {
		setTextureScaling(linear, false);
	}

	private static void setTextureScaling(boolean linear, boolean mipmap) {
		if (linear) {
			GlStateManager.method_12294(3553, 10241, mipmap ? 9987 : 9729);
			GlStateManager.method_12294(3553, 10240, 9729);
		} else {
			GlStateManager.method_12294(3553, 10241, mipmap ? 9986 : 9728);
			GlStateManager.method_12294(3553, 10240, 9728);
		}
	}

	private static void method_5866(int[] is, int i) {
		method_5867(is, 0, i);
	}

	private static void method_5867(int[] is, int i, int j) {
		int[] js = is;
		if (MinecraftClient.getInstance().options.anaglyph3d) {
			js = getAnaglyphColors(is);
		}

		BUFFER.clear();
		BUFFER.put(js, i, j);
		BUFFER.position(0).limit(j);
	}

	static void bindTexture(int texture) {
		GlStateManager.bindTexture(texture);
	}

	public static int[] toPixels(ResourceManager resourceManager, Identifier identifier) throws IOException {
		Resource resource = null;

		int[] var7;
		try {
			resource = resourceManager.getResource(identifier);
			BufferedImage bufferedImage = create(resource.getInputStream());
			int i = bufferedImage.getWidth();
			int j = bufferedImage.getHeight();
			int[] is = new int[i * j];
			bufferedImage.getRGB(0, 0, i, j, is, 0, i);
			var7 = is;
		} finally {
			IOUtils.closeQuietly(resource);
		}

		return var7;
	}

	public static BufferedImage create(InputStream input) throws IOException {
		BufferedImage var1;
		try {
			var1 = ImageIO.read(input);
		} finally {
			IOUtils.closeQuietly(input);
		}

		return var1;
	}

	public static int[] getAnaglyphColors(int[] colors) {
		int[] is = new int[colors.length];

		for (int i = 0; i < colors.length; i++) {
			is[i] = getAnaglyphColor(colors[i]);
		}

		return is;
	}

	public static int getAnaglyphColor(int color) {
		int i = color >> 24 & 0xFF;
		int j = color >> 16 & 0xFF;
		int k = color >> 8 & 0xFF;
		int l = color & 0xFF;
		int m = (j * 30 + k * 59 + l * 11) / 100;
		int n = (j * 30 + k * 70) / 100;
		int o = (j * 30 + l * 70) / 100;
		return i << 24 | m << 16 | n << 8 | o;
	}

	public static void flipXY(int[] data, int width, int height) {
		int[] is = new int[width];
		int i = height / 2;

		for (int j = 0; j < i; j++) {
			System.arraycopy(data, j * width, is, 0, width);
			System.arraycopy(data, (height - 1 - j) * width, data, j * width, width);
			System.arraycopy(is, 0, data, (height - 1 - j) * width, width);
		}
	}

	static {
		int i = -16777216;
		int j = -524040;
		int[] is = new int[]{-524040, -524040, -524040, -524040, -524040, -524040, -524040, -524040};
		int[] js = new int[]{-16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216};
		int k = is.length;

		for (int l = 0; l < 16; l++) {
			System.arraycopy(l < k ? is : js, 0, field_6583, 16 * l, k);
			System.arraycopy(l < k ? js : is, 0, field_6583, 16 * l + k, k);
		}

		MISSING_TEXTURE.upload();
		field_13653 = new float[256];

		for (int m = 0; m < field_13653.length; m++) {
			field_13653[m] = (float)Math.pow((double)((float)m / 255.0F), 2.2);
		}

		field_8104 = new int[4];
	}
}
