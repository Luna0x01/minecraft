package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.MinecartEntityModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class MinecartEntityRenderer<T extends AbstractMinecartEntity> extends EntityRenderer<T> {
	private static final Identifier field_6505 = new Identifier("textures/entity/minecart.png");
	protected EntityModel field_2129 = new MinecartEntityModel();

	public MinecartEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
		this.shadowSize = 0.5F;
	}

	public void render(T abstractMinecartEntity, double d, double e, double f, float g, float h) {
		GlStateManager.pushMatrix();
		this.bindTexture(abstractMinecartEntity);
		long l = (long)abstractMinecartEntity.getEntityId() * 493286711L;
		l = l * l * 4392167121L + l * 98761L;
		float i = (((float)(l >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		float j = (((float)(l >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		float k = (((float)(l >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		GlStateManager.translate(i, j, k);
		double m = abstractMinecartEntity.prevTickX + (abstractMinecartEntity.x - abstractMinecartEntity.prevTickX) * (double)h;
		double n = abstractMinecartEntity.prevTickY + (abstractMinecartEntity.y - abstractMinecartEntity.prevTickY) * (double)h;
		double o = abstractMinecartEntity.prevTickZ + (abstractMinecartEntity.z - abstractMinecartEntity.prevTickZ) * (double)h;
		double p = 0.3F;
		Vec3d vec3d = abstractMinecartEntity.snapPositionToRail(m, n, o);
		float q = abstractMinecartEntity.prevPitch + (abstractMinecartEntity.pitch - abstractMinecartEntity.prevPitch) * h;
		if (vec3d != null) {
			Vec3d vec3d2 = abstractMinecartEntity.snapPositionToRailWithOffset(m, n, o, p);
			Vec3d vec3d3 = abstractMinecartEntity.snapPositionToRailWithOffset(m, n, o, -p);
			if (vec3d2 == null) {
				vec3d2 = vec3d;
			}

			if (vec3d3 == null) {
				vec3d3 = vec3d;
			}

			d += vec3d.x - m;
			e += (vec3d2.y + vec3d3.y) / 2.0 - n;
			f += vec3d.z - o;
			Vec3d vec3d4 = vec3d3.add(-vec3d2.x, -vec3d2.y, -vec3d2.z);
			if (vec3d4.length() != 0.0) {
				vec3d4 = vec3d4.normalize();
				g = (float)(Math.atan2(vec3d4.z, vec3d4.x) * 180.0 / Math.PI);
				q = (float)(Math.atan(vec3d4.y) * 73.0);
			}
		}

		GlStateManager.translate((float)d, (float)e + 0.375F, (float)f);
		GlStateManager.rotate(180.0F - g, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-q, 0.0F, 0.0F, 1.0F);
		float r = (float)abstractMinecartEntity.getDamageWobbleTicks() - h;
		float s = abstractMinecartEntity.getDamageWobbleStrength() - h;
		if (s < 0.0F) {
			s = 0.0F;
		}

		if (r > 0.0F) {
			GlStateManager.rotate(MathHelper.sin(r) * r * s / 10.0F * (float)abstractMinecartEntity.getDamageWobbleSide(), 1.0F, 0.0F, 0.0F);
		}

		int t = abstractMinecartEntity.getBlockOffset();
		BlockState blockState = abstractMinecartEntity.getContainedBlock();
		if (blockState.getBlock().getBlockType() != -1) {
			GlStateManager.pushMatrix();
			this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
			float u = 0.75F;
			GlStateManager.scale(u, u, u);
			GlStateManager.translate(-0.5F, (float)(t - 8) / 16.0F, 0.5F);
			this.method_5180(abstractMinecartEntity, h, blockState);
			GlStateManager.popMatrix();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.bindTexture(abstractMinecartEntity);
		}

		GlStateManager.scale(-1.0F, -1.0F, 1.0F);
		this.field_2129.render(abstractMinecartEntity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		GlStateManager.popMatrix();
		super.render(abstractMinecartEntity, d, e, f, g, h);
	}

	protected Identifier getTexture(T abstractMinecartEntity) {
		return field_6505;
	}

	protected void method_5180(T abstractMinecartEntity, float f, BlockState blockState) {
		GlStateManager.pushMatrix();
		MinecraftClient.getInstance().getBlockRenderManager().renderBlockEntity(blockState, abstractMinecartEntity.getBrightnessAtEyes(f));
		GlStateManager.popMatrix();
	}
}
