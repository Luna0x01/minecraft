package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public class PaintingEntityRenderer extends EntityRenderer<PaintingEntity> {
	private static final Identifier field_6511 = new Identifier("textures/painting/paintings_kristoffer_zetterstrand.png");

	public PaintingEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	public void render(PaintingEntity paintingEntity, double d, double e, double f, float g, float h) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(d, e, f);
		GlStateManager.rotate(180.0F - g, 0.0F, 1.0F, 0.0F);
		GlStateManager.enableRescaleNormal();
		this.bindTexture(paintingEntity);
		PaintingEntity.PaintingMotive paintingMotive = paintingEntity.type;
		float i = 0.0625F;
		GlStateManager.scale(i, i, i);
		if (this.field_13631) {
			GlStateManager.enableColorMaterial();
			GlStateManager.method_12309(this.method_12454(paintingEntity));
		}

		this.method_1577(paintingEntity, paintingMotive.width, paintingMotive.height, paintingMotive.textureX, paintingMotive.textureY);
		if (this.field_13631) {
			GlStateManager.method_12315();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		super.render(paintingEntity, d, e, f, g, h);
	}

	protected Identifier getTexture(PaintingEntity paintingEntity) {
		return field_6511;
	}

	private void method_1577(PaintingEntity paintingEntity, int i, int j, int k, int l) {
		float f = (float)(-i) / 2.0F;
		float g = (float)(-j) / 2.0F;
		float h = 0.5F;
		float m = 0.75F;
		float n = 0.8125F;
		float o = 0.0F;
		float p = 0.0625F;
		float q = 0.75F;
		float r = 0.8125F;
		float s = 0.001953125F;
		float t = 0.001953125F;
		float u = 0.7519531F;
		float v = 0.7519531F;
		float w = 0.0F;
		float x = 0.0625F;

		for (int y = 0; y < i / 16; y++) {
			for (int z = 0; z < j / 16; z++) {
				float aa = f + (float)((y + 1) * 16);
				float ab = f + (float)(y * 16);
				float ac = g + (float)((z + 1) * 16);
				float ad = g + (float)(z * 16);
				this.method_1576(paintingEntity, (aa + ab) / 2.0F, (ac + ad) / 2.0F);
				float ae = (float)(k + i - y * 16) / 256.0F;
				float af = (float)(k + i - (y + 1) * 16) / 256.0F;
				float ag = (float)(l + j - z * 16) / 256.0F;
				float ah = (float)(l + j - (z + 1) * 16) / 256.0F;
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferBuilder = tessellator.getBuffer();
				bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_NORMAL);
				bufferBuilder.vertex((double)aa, (double)ad, (double)(-h)).texture((double)af, (double)ag).normal(0.0F, 0.0F, -1.0F).next();
				bufferBuilder.vertex((double)ab, (double)ad, (double)(-h)).texture((double)ae, (double)ag).normal(0.0F, 0.0F, -1.0F).next();
				bufferBuilder.vertex((double)ab, (double)ac, (double)(-h)).texture((double)ae, (double)ah).normal(0.0F, 0.0F, -1.0F).next();
				bufferBuilder.vertex((double)aa, (double)ac, (double)(-h)).texture((double)af, (double)ah).normal(0.0F, 0.0F, -1.0F).next();
				bufferBuilder.vertex((double)aa, (double)ac, (double)h).texture((double)m, (double)o).normal(0.0F, 0.0F, 1.0F).next();
				bufferBuilder.vertex((double)ab, (double)ac, (double)h).texture((double)n, (double)o).normal(0.0F, 0.0F, 1.0F).next();
				bufferBuilder.vertex((double)ab, (double)ad, (double)h).texture((double)n, (double)p).normal(0.0F, 0.0F, 1.0F).next();
				bufferBuilder.vertex((double)aa, (double)ad, (double)h).texture((double)m, (double)p).normal(0.0F, 0.0F, 1.0F).next();
				bufferBuilder.vertex((double)aa, (double)ac, (double)(-h)).texture((double)q, (double)s).normal(0.0F, 1.0F, 0.0F).next();
				bufferBuilder.vertex((double)ab, (double)ac, (double)(-h)).texture((double)r, (double)s).normal(0.0F, 1.0F, 0.0F).next();
				bufferBuilder.vertex((double)ab, (double)ac, (double)h).texture((double)r, (double)t).normal(0.0F, 1.0F, 0.0F).next();
				bufferBuilder.vertex((double)aa, (double)ac, (double)h).texture((double)q, (double)t).normal(0.0F, 1.0F, 0.0F).next();
				bufferBuilder.vertex((double)aa, (double)ad, (double)h).texture((double)q, (double)s).normal(0.0F, -1.0F, 0.0F).next();
				bufferBuilder.vertex((double)ab, (double)ad, (double)h).texture((double)r, (double)s).normal(0.0F, -1.0F, 0.0F).next();
				bufferBuilder.vertex((double)ab, (double)ad, (double)(-h)).texture((double)r, (double)t).normal(0.0F, -1.0F, 0.0F).next();
				bufferBuilder.vertex((double)aa, (double)ad, (double)(-h)).texture((double)q, (double)t).normal(0.0F, -1.0F, 0.0F).next();
				bufferBuilder.vertex((double)aa, (double)ac, (double)h).texture((double)v, (double)w).normal(-1.0F, 0.0F, 0.0F).next();
				bufferBuilder.vertex((double)aa, (double)ad, (double)h).texture((double)v, (double)x).normal(-1.0F, 0.0F, 0.0F).next();
				bufferBuilder.vertex((double)aa, (double)ad, (double)(-h)).texture((double)u, (double)x).normal(-1.0F, 0.0F, 0.0F).next();
				bufferBuilder.vertex((double)aa, (double)ac, (double)(-h)).texture((double)u, (double)w).normal(-1.0F, 0.0F, 0.0F).next();
				bufferBuilder.vertex((double)ab, (double)ac, (double)(-h)).texture((double)v, (double)w).normal(1.0F, 0.0F, 0.0F).next();
				bufferBuilder.vertex((double)ab, (double)ad, (double)(-h)).texture((double)v, (double)x).normal(1.0F, 0.0F, 0.0F).next();
				bufferBuilder.vertex((double)ab, (double)ad, (double)h).texture((double)u, (double)x).normal(1.0F, 0.0F, 0.0F).next();
				bufferBuilder.vertex((double)ab, (double)ac, (double)h).texture((double)u, (double)w).normal(1.0F, 0.0F, 0.0F).next();
				tessellator.draw();
			}
		}
	}

	private void method_1576(PaintingEntity paintingEntity, float f, float g) {
		int i = MathHelper.floor(paintingEntity.x);
		int j = MathHelper.floor(paintingEntity.y + (double)(g / 16.0F));
		int k = MathHelper.floor(paintingEntity.z);
		Direction direction = paintingEntity.direction;
		if (direction == Direction.NORTH) {
			i = MathHelper.floor(paintingEntity.x + (double)(f / 16.0F));
		}

		if (direction == Direction.WEST) {
			k = MathHelper.floor(paintingEntity.z - (double)(f / 16.0F));
		}

		if (direction == Direction.SOUTH) {
			i = MathHelper.floor(paintingEntity.x - (double)(f / 16.0F));
		}

		if (direction == Direction.EAST) {
			k = MathHelper.floor(paintingEntity.z + (double)(f / 16.0F));
		}

		int l = this.dispatcher.world.getLight(new BlockPos(i, j, k), 0);
		int m = l % 65536;
		int n = l / 65536;
		GLX.gl13MultiTexCoord2f(GLX.lightmapTextureUnit, (float)m, (float)n);
		GlStateManager.color(1.0F, 1.0F, 1.0F);
	}
}
