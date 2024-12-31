package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.feature.DragonEyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.EnderDragonDeathFeature;
import net.minecraft.client.render.entity.model.EnderDragonModel;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class EnderDragonEntityRenderer extends MobEntityRenderer<EnderDragonEntity> {
	private static final Identifier CRYSTAL_BEAM_TEXTURE = new Identifier("textures/entity/endercrystal/endercrystal_beam.png");
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
			GlStateManager.blendFunc(770, 771);
			GlStateManager.color(1.0F, 0.0F, 0.0F, 0.5F);
			this.model.render(enderDragonEntity, f, g, h, i, j, k);
			GlStateManager.enableTexture();
			GlStateManager.disableBlend();
			GlStateManager.depthFunc(515);
		}
	}

	public void render(EnderDragonEntity enderDragonEntity, double d, double e, double f, float g, float h) {
		BossBar.update(enderDragonEntity, false);
		super.render(enderDragonEntity, d, e, f, g, h);
		if (enderDragonEntity.connectedCrystal != null) {
			this.method_10194(enderDragonEntity, d, e, f, h);
		}
	}

	protected void method_10194(EnderDragonEntity dragon, double d, double e, double f, float g) {
		float h = (float)dragon.connectedCrystal.endCrystalAge + g;
		float i = MathHelper.sin(h * 0.2F) / 2.0F + 0.5F;
		i = (i * i + i) * 0.2F;
		float j = (float)(dragon.connectedCrystal.x - dragon.x - (dragon.prevX - dragon.x) * (double)(1.0F - g));
		float k = (float)((double)i + dragon.connectedCrystal.y - 1.0 - dragon.y - (dragon.prevY - dragon.y) * (double)(1.0F - g));
		float l = (float)(dragon.connectedCrystal.z - dragon.z - (dragon.prevZ - dragon.z) * (double)(1.0F - g));
		float m = MathHelper.sqrt(j * j + l * l);
		float n = MathHelper.sqrt(j * j + k * k + l * l);
		GlStateManager.pushMatrix();
		GlStateManager.translate((float)d, (float)e + 2.0F, (float)f);
		GlStateManager.rotate((float)(-Math.atan2((double)l, (double)j)) * 180.0F / (float) Math.PI - 90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate((float)(-Math.atan2((double)m, (double)k)) * 180.0F / (float) Math.PI - 90.0F, 1.0F, 0.0F, 0.0F);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		DiffuseLighting.disable();
		GlStateManager.disableCull();
		this.bindTexture(CRYSTAL_BEAM_TEXTURE);
		GlStateManager.shadeModel(7425);
		float o = 0.0F - ((float)dragon.ticksAlive + g) * 0.01F;
		float p = MathHelper.sqrt(j * j + k * k + l * l) / 32.0F - ((float)dragon.ticksAlive + g) * 0.01F;
		bufferBuilder.begin(5, VertexFormats.POSITION_TEXTURE_COLOR);
		int q = 8;

		for (int r = 0; r <= 8; r++) {
			float s = MathHelper.sin((float)(r % 8) * (float) Math.PI * 2.0F / 8.0F) * 0.75F;
			float t = MathHelper.cos((float)(r % 8) * (float) Math.PI * 2.0F / 8.0F) * 0.75F;
			float u = (float)(r % 8) * 1.0F / 8.0F;
			bufferBuilder.vertex((double)(s * 0.2F), (double)(t * 0.2F), 0.0).texture((double)u, (double)p).color(0, 0, 0, 255).next();
			bufferBuilder.vertex((double)s, (double)t, (double)n).texture((double)u, (double)o).color(255, 255, 255, 255).next();
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
