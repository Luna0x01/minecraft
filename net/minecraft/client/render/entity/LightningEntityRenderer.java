package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Random;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.LightningBoltEntity;
import net.minecraft.util.Identifier;

public class LightningEntityRenderer extends EntityRenderer<LightningBoltEntity> {
	public LightningEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	public void render(LightningBoltEntity lightningBoltEntity, double d, double e, double f, float g, float h) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		GlStateManager.disableTexture();
		GlStateManager.disableLighting();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(770, 1);
		double[] ds = new double[8];
		double[] es = new double[8];
		double i = 0.0;
		double j = 0.0;
		Random random = new Random(lightningBoltEntity.seed);

		for (int k = 7; k >= 0; k--) {
			ds[k] = i;
			es[k] = j;
			i += (double)(random.nextInt(11) - 5);
			j += (double)(random.nextInt(11) - 5);
		}

		for (int l = 0; l < 4; l++) {
			Random random2 = new Random(lightningBoltEntity.seed);

			for (int m = 0; m < 3; m++) {
				int n = 7;
				int o = 0;
				if (m > 0) {
					n = 7 - m;
				}

				if (m > 0) {
					o = n - 2;
				}

				double p = ds[n] - i;
				double q = es[n] - j;

				for (int r = n; r >= o; r--) {
					double s = p;
					double t = q;
					if (m == 0) {
						p += (double)(random2.nextInt(11) - 5);
						q += (double)(random2.nextInt(11) - 5);
					} else {
						p += (double)(random2.nextInt(31) - 15);
						q += (double)(random2.nextInt(31) - 15);
					}

					bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);
					float u = 0.5F;
					float v = 0.45F;
					float w = 0.45F;
					float x = 0.5F;
					double y = 0.1 + (double)l * 0.2;
					if (m == 0) {
						y *= (double)r * 0.1 + 1.0;
					}

					double z = 0.1 + (double)l * 0.2;
					if (m == 0) {
						z *= (double)(r - 1) * 0.1 + 1.0;
					}

					for (int aa = 0; aa < 5; aa++) {
						double ab = d + 0.5 - y;
						double ac = f + 0.5 - y;
						if (aa == 1 || aa == 2) {
							ab += y * 2.0;
						}

						if (aa == 2 || aa == 3) {
							ac += y * 2.0;
						}

						double ad = d + 0.5 - z;
						double ae = f + 0.5 - z;
						if (aa == 1 || aa == 2) {
							ad += z * 2.0;
						}

						if (aa == 2 || aa == 3) {
							ae += z * 2.0;
						}

						bufferBuilder.vertex(ad + p, e + (double)(r * 16), ae + q).color(0.45F, 0.45F, 0.5F, 0.3F).next();
						bufferBuilder.vertex(ab + s, e + (double)((r + 1) * 16), ac + t).color(0.45F, 0.45F, 0.5F, 0.3F).next();
					}

					tessellator.draw();
				}
			}
		}

		GlStateManager.disableBlend();
		GlStateManager.enableLighting();
		GlStateManager.enableTexture();
	}

	protected Identifier getTexture(LightningBoltEntity lightningBoltEntity) {
		return null;
	}
}
