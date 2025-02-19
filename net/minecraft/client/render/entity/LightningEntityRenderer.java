package net.minecraft.client.render.entity;

import java.util.Random;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LightningEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;

public class LightningEntityRenderer extends EntityRenderer<LightningEntity> {
	public LightningEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
	}

	public void render(LightningEntity lightningEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		float[] fs = new float[8];
		float[] gs = new float[8];
		float h = 0.0F;
		float j = 0.0F;
		Random random = new Random(lightningEntity.seed);

		for (int k = 7; k >= 0; k--) {
			fs[k] = h;
			gs[k] = j;
			h += (float)(random.nextInt(11) - 5);
			j += (float)(random.nextInt(11) - 5);
		}

		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getLightning());
		Matrix4f matrix4f = matrixStack.peek().getModel();

		for (int l = 0; l < 4; l++) {
			Random random2 = new Random(lightningEntity.seed);

			for (int m = 0; m < 3; m++) {
				int n = 7;
				int o = 0;
				if (m > 0) {
					n = 7 - m;
				}

				if (m > 0) {
					o = n - 2;
				}

				float p = fs[n] - h;
				float q = gs[n] - j;

				for (int r = n; r >= o; r--) {
					float s = p;
					float t = q;
					if (m == 0) {
						p += (float)(random2.nextInt(11) - 5);
						q += (float)(random2.nextInt(11) - 5);
					} else {
						p += (float)(random2.nextInt(31) - 15);
						q += (float)(random2.nextInt(31) - 15);
					}

					float u = 0.5F;
					float v = 0.45F;
					float w = 0.45F;
					float x = 0.5F;
					float y = 0.1F + (float)l * 0.2F;
					if (m == 0) {
						y = (float)((double)y * ((double)r * 0.1 + 1.0));
					}

					float z = 0.1F + (float)l * 0.2F;
					if (m == 0) {
						z *= (float)(r - 1) * 0.1F + 1.0F;
					}

					drawBranch(matrix4f, vertexConsumer, p, q, r, s, t, 0.45F, 0.45F, 0.5F, y, z, false, false, true, false);
					drawBranch(matrix4f, vertexConsumer, p, q, r, s, t, 0.45F, 0.45F, 0.5F, y, z, true, false, true, true);
					drawBranch(matrix4f, vertexConsumer, p, q, r, s, t, 0.45F, 0.45F, 0.5F, y, z, true, true, false, true);
					drawBranch(matrix4f, vertexConsumer, p, q, r, s, t, 0.45F, 0.45F, 0.5F, y, z, false, true, false, false);
				}
			}
		}
	}

	private static void drawBranch(
		Matrix4f matrix,
		VertexConsumer buffer,
		float f,
		float g,
		int i,
		float h,
		float j,
		float red,
		float green,
		float blue,
		float k,
		float l,
		boolean bl,
		boolean bl2,
		boolean bl3,
		boolean bl4
	) {
		buffer.vertex(matrix, f + (bl ? l : -l), (float)(i * 16), g + (bl2 ? l : -l)).color(red, green, blue, 0.3F).next();
		buffer.vertex(matrix, h + (bl ? k : -k), (float)((i + 1) * 16), j + (bl2 ? k : -k)).color(red, green, blue, 0.3F).next();
		buffer.vertex(matrix, h + (bl3 ? k : -k), (float)((i + 1) * 16), j + (bl4 ? k : -k)).color(red, green, blue, 0.3F).next();
		buffer.vertex(matrix, f + (bl3 ? l : -l), (float)(i * 16), g + (bl4 ? l : -l)).color(red, green, blue, 0.3F).next();
	}

	public Identifier getTexture(LightningEntity lightningEntity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
	}
}
