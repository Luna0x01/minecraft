package net.minecraft.client.texture;

import net.minecraft.util.Util;

public class MipmapHelper {
	private static final float[] COLOR_FRACTIONS = Util.make(new float[256], fs -> {
		for (int i = 0; i < fs.length; i++) {
			fs[i] = (float)Math.pow((double)((float)i / 255.0F), 2.2);
		}
	});

	public static NativeImage[] getMipmapLevelsImages(NativeImage nativeImage, int i) {
		NativeImage[] nativeImages = new NativeImage[i + 1];
		nativeImages[0] = nativeImage;
		if (i > 0) {
			boolean bl = false;

			label51:
			for (int j = 0; j < nativeImage.getWidth(); j++) {
				for (int k = 0; k < nativeImage.getHeight(); k++) {
					if (nativeImage.getPixelRgba(j, k) >> 24 == 0) {
						bl = true;
						break label51;
					}
				}
			}

			for (int l = 1; l <= i; l++) {
				NativeImage nativeImage2 = nativeImages[l - 1];
				NativeImage nativeImage3 = new NativeImage(nativeImage2.getWidth() >> 1, nativeImage2.getHeight() >> 1, false);
				int m = nativeImage3.getWidth();
				int n = nativeImage3.getHeight();

				for (int o = 0; o < m; o++) {
					for (int p = 0; p < n; p++) {
						nativeImage3.setPixelRgba(
							o,
							p,
							blend(
								nativeImage2.getPixelRgba(o * 2 + 0, p * 2 + 0),
								nativeImage2.getPixelRgba(o * 2 + 1, p * 2 + 0),
								nativeImage2.getPixelRgba(o * 2 + 0, p * 2 + 1),
								nativeImage2.getPixelRgba(o * 2 + 1, p * 2 + 1),
								bl
							)
						);
					}
				}

				nativeImages[l] = nativeImage3;
			}
		}

		return nativeImages;
	}

	private static int blend(int i, int j, int k, int l, boolean bl) {
		if (bl) {
			float f = 0.0F;
			float g = 0.0F;
			float h = 0.0F;
			float m = 0.0F;
			if (i >> 24 != 0) {
				f += getColorFraction(i >> 24);
				g += getColorFraction(i >> 16);
				h += getColorFraction(i >> 8);
				m += getColorFraction(i >> 0);
			}

			if (j >> 24 != 0) {
				f += getColorFraction(j >> 24);
				g += getColorFraction(j >> 16);
				h += getColorFraction(j >> 8);
				m += getColorFraction(j >> 0);
			}

			if (k >> 24 != 0) {
				f += getColorFraction(k >> 24);
				g += getColorFraction(k >> 16);
				h += getColorFraction(k >> 8);
				m += getColorFraction(k >> 0);
			}

			if (l >> 24 != 0) {
				f += getColorFraction(l >> 24);
				g += getColorFraction(l >> 16);
				h += getColorFraction(l >> 8);
				m += getColorFraction(l >> 0);
			}

			f /= 4.0F;
			g /= 4.0F;
			h /= 4.0F;
			m /= 4.0F;
			int n = (int)(Math.pow((double)f, 0.45454545454545453) * 255.0);
			int o = (int)(Math.pow((double)g, 0.45454545454545453) * 255.0);
			int p = (int)(Math.pow((double)h, 0.45454545454545453) * 255.0);
			int q = (int)(Math.pow((double)m, 0.45454545454545453) * 255.0);
			if (n < 96) {
				n = 0;
			}

			return n << 24 | o << 16 | p << 8 | q;
		} else {
			int r = getColorComponent(i, j, k, l, 24);
			int s = getColorComponent(i, j, k, l, 16);
			int t = getColorComponent(i, j, k, l, 8);
			int u = getColorComponent(i, j, k, l, 0);
			return r << 24 | s << 16 | t << 8 | u;
		}
	}

	private static int getColorComponent(int i, int j, int k, int l, int m) {
		float f = getColorFraction(i >> m);
		float g = getColorFraction(j >> m);
		float h = getColorFraction(k >> m);
		float n = getColorFraction(l >> m);
		float o = (float)((double)((float)Math.pow((double)(f + g + h + n) * 0.25, 0.45454545454545453)));
		return (int)((double)o * 255.0);
	}

	private static float getColorFraction(int i) {
		return COLOR_FRACTIONS[i & 0xFF];
	}
}
