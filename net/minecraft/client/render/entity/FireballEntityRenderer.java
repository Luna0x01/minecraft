package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class FireballEntityRenderer extends EntityRenderer<ExplosiveProjectileEntity> {
	private final float speed;

	public FireballEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, float f) {
		super(entityRenderDispatcher);
		this.speed = f;
	}

	public void render(ExplosiveProjectileEntity explosiveProjectileEntity, double d, double e, double f, float g, float h) {
		GlStateManager.pushMatrix();
		this.bindTexture(explosiveProjectileEntity);
		GlStateManager.translate((float)d, (float)e, (float)f);
		GlStateManager.enableRescaleNormal();
		GlStateManager.scale(this.speed, this.speed, this.speed);
		Sprite sprite = MinecraftClient.getInstance().getHeldItemRenderer().method_19372().method_19155(Items.FIRE_CHARGE);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		float i = sprite.getMinU();
		float j = sprite.getMaxU();
		float k = sprite.getMinV();
		float l = sprite.getMaxV();
		float m = 1.0F;
		float n = 0.5F;
		float o = 0.25F;
		GlStateManager.rotate(180.0F - this.dispatcher.yaw, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate((float)(this.dispatcher.options.perspective == 2 ? -1 : 1) * -this.dispatcher.pitch, 1.0F, 0.0F, 0.0F);
		if (this.field_13631) {
			GlStateManager.enableColorMaterial();
			GlStateManager.method_12309(this.method_12454(explosiveProjectileEntity));
		}

		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_NORMAL);
		bufferBuilder.vertex(-0.5, -0.25, 0.0).texture((double)i, (double)l).normal(0.0F, 1.0F, 0.0F).next();
		bufferBuilder.vertex(0.5, -0.25, 0.0).texture((double)j, (double)l).normal(0.0F, 1.0F, 0.0F).next();
		bufferBuilder.vertex(0.5, 0.75, 0.0).texture((double)j, (double)k).normal(0.0F, 1.0F, 0.0F).next();
		bufferBuilder.vertex(-0.5, 0.75, 0.0).texture((double)i, (double)k).normal(0.0F, 1.0F, 0.0F).next();
		tessellator.draw();
		if (this.field_13631) {
			GlStateManager.method_12315();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		super.render(explosiveProjectileEntity, d, e, f, g, h);
	}

	protected Identifier getTexture(ExplosiveProjectileEntity explosiveProjectileEntity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
	}
}
