package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.feature.DragonEyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.EnderDragonDeathFeature;
import net.minecraft.client.render.entity.model.EnderDragonModel;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class EnderDragonEntityRenderer extends MobEntityRenderer<EnderDragonEntity> {
	public static final Identifier CRYSTAL_BEAM_TEXTURE = new Identifier("textures/entity/endercrystal/endercrystal_beam.png");
	private static final Identifier EXPLOSION_TEXTURE = new Identifier("textures/entity/enderdragon/dragon_exploding.png");
	private static final Identifier DRAGON_TEXTURE = new Identifier("textures/entity/enderdragon/dragon.png");
	protected EnderDragonModel dragonModel = (EnderDragonModel)this.model;

	public EnderDragonEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new EnderDragonModel(0.0F), 0.5F);
		this.addFeature(new DragonEyesFeatureRenderer(this));
		this.addFeature(new EnderDragonDeathFeature());
	}

	protected void method_5777(EnderDragonEntity enderDragonEntity, float f, float g, float h) {
		float i = (float)enderDragonEntity.getSegmentProperties(7, h)[0];
		float j = (float)(enderDragonEntity.getSegmentProperties(5, h)[1] - enderDragonEntity.getSegmentProperties(10, h)[1]);
		GlStateManager.rotate(-i, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(j * 10.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.translate(0.0F, 0.0F, 1.0F);
		if (enderDragonEntity.deathTime > 0) {
			float k = ((float)enderDragonEntity.deathTime + h - 1.0F) / 20.0F * 1.6F;
			k = MathHelper.sqrt(k);
			if (k > 1.0F) {
				k = 1.0F;
			}

			GlStateManager.rotate(k * this.method_5771(enderDragonEntity), 0.0F, 0.0F, 1.0F);
		}
	}

	protected void renderModel(EnderDragonEntity enderDragonEntity, float f, float g, float h, float i, float j, float k) {
		if (enderDragonEntity.field_3746 > 0) {
			float l = (float)enderDragonEntity.field_3746 / 200.0F;
			GlStateManager.depthFunc(515);
			GlStateManager.enableAlphaTest();
			GlStateManager.alphaFunc(516, l);
			this.bindTexture(EXPLOSION_TEXTURE);
			this.model.render(enderDragonEntity, f, g, h, i, j, k);
			GlStateManager.alphaFunc(516, 0.1F);
			GlStateManager.depthFunc(514);
		}

		this.bindTexture(enderDragonEntity);
		this.model.render(enderDragonEntity, f, g, h, i, j, k);
		if (enderDragonEntity.hurtTime > 0) {
			GlStateManager.depthFunc(514);
			GlStateManager.disableTexture();
			GlStateManager.enableBlend();
			GlStateManager.method_12287(GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA);
			GlStateManager.color(1.0F, 0.0F, 0.0F, 0.5F);
			this.model.render(enderDragonEntity, f, g, h, i, j, k);
			GlStateManager.enableTexture();
			GlStateManager.disableBlend();
			GlStateManager.depthFunc(515);
		}
	}

	public void render(EnderDragonEntity enderDragonEntity, double d, double e, double f, float g, float h) {
		super.render(enderDragonEntity, d, e, f, g, h);
		if (enderDragonEntity.connectedCrystal != null) {
			this.bindTexture(CRYSTAL_BEAM_TEXTURE);
			float i = MathHelper.sin(((float)enderDragonEntity.connectedCrystal.ticksAlive + h) * 0.2F) / 2.0F + 0.5F;
			i = (i * i + i) * 0.2F;
			method_12445(
				d,
				e,
				f,
				h,
				enderDragonEntity.x + (enderDragonEntity.prevX - enderDragonEntity.x) * (double)(1.0F - h),
				enderDragonEntity.y + (enderDragonEntity.prevY - enderDragonEntity.y) * (double)(1.0F - h),
				enderDragonEntity.z + (enderDragonEntity.prevZ - enderDragonEntity.z) * (double)(1.0F - h),
				enderDragonEntity.ticksAlive,
				enderDragonEntity.connectedCrystal.x,
				(double)i + enderDragonEntity.connectedCrystal.y,
				enderDragonEntity.connectedCrystal.z
			);
		}
	}

	public static void method_12445(double d, double e, double f, float g, double h, double i, double j, int k, double l, double m, double n) {
		float o = (float)(l - h);
		float p = (float)(m - 1.0 - i);
		float q = (float)(n - j);
		float r = MathHelper.sqrt(o * o + q * q);
		float s = MathHelper.sqrt(o * o + p * p + q * q);
		GlStateManager.pushMatrix();
		GlStateManager.translate((float)d, (float)e + 2.0F, (float)f);
		GlStateManager.rotate((float)(-Math.atan2((double)q, (double)o)) * (180.0F / (float)Math.PI) - 90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate((float)(-Math.atan2((double)r, (double)p)) * (180.0F / (float)Math.PI) - 90.0F, 1.0F, 0.0F, 0.0F);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		DiffuseLighting.disable();
		GlStateManager.disableCull();
		GlStateManager.shadeModel(7425);
		float t = 0.0F - ((float)k + g) * 0.01F;
		float u = MathHelper.sqrt(o * o + p * p + q * q) / 32.0F - ((float)k + g) * 0.01F;
		bufferBuilder.begin(5, VertexFormats.POSITION_TEXTURE_COLOR);
		int v = 8;

		for (int w = 0; w <= 8; w++) {
			float x = MathHelper.sin((float)(w % 8) * (float) (Math.PI * 2) / 8.0F) * 0.75F;
			float y = MathHelper.cos((float)(w % 8) * (float) (Math.PI * 2) / 8.0F) * 0.75F;
			float z = (float)(w % 8) / 8.0F;
			bufferBuilder.vertex((double)(x * 0.2F), (double)(y * 0.2F), 0.0).texture((double)z, (double)t).color(0, 0, 0, 255).next();
			bufferBuilder.vertex((double)x, (double)y, (double)s).texture((double)z, (double)u).color(255, 255, 255, 255).next();
		}

		tessellator.draw();
		GlStateManager.enableCull();
		GlStateManager.shadeModel(7424);
		DiffuseLighting.enableNormally();
		GlStateManager.popMatrix();
	}

	protected Identifier getTexture(EnderDragonEntity enderDragonEntity) {
		return DRAGON_TEXTURE;
	}
}
