package net.minecraft.client.particle;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class ItemPickupParticle extends Particle {
	private final Entity itemEntity;
	private final Entity interactingEntity;
	private int pickupAge;
	private final int pickupMaxAge;
	private final float field_1751;
	private final EntityRenderDispatcher entityRenderManager = MinecraftClient.getInstance().getEntityRenderManager();

	public ItemPickupParticle(World world, Entity entity, Entity entity2, float f) {
		super(world, entity.x, entity.y, entity.z, entity.velocityX, entity.velocityY, entity.velocityZ);
		this.itemEntity = entity;
		this.interactingEntity = entity2;
		this.pickupMaxAge = 3;
		this.field_1751 = f;
	}

	@Override
	public void draw(BufferBuilder builder, Entity entity, float tickDelta, float g, float h, float i, float j, float k) {
		float f = ((float)this.pickupAge + tickDelta) / (float)this.pickupMaxAge;
		f *= f;
		double d = this.itemEntity.x;
		double e = this.itemEntity.y;
		double l = this.itemEntity.z;
		double m = this.interactingEntity.prevTickX + (this.interactingEntity.x - this.interactingEntity.prevTickX) * (double)tickDelta;
		double n = this.interactingEntity.prevTickY + (this.interactingEntity.y - this.interactingEntity.prevTickY) * (double)tickDelta + (double)this.field_1751;
		double o = this.interactingEntity.prevTickZ + (this.interactingEntity.z - this.interactingEntity.prevTickZ) * (double)tickDelta;
		double p = d + (m - d) * (double)f;
		double q = e + (n - e) * (double)f;
		double r = l + (o - l) * (double)f;
		int s = this.method_12243(tickDelta);
		int t = s % 65536;
		int u = s / 65536;
		GLX.gl13MultiTexCoord2f(GLX.lightmapTextureUnit, (float)t, (float)u);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		p -= field_1722;
		q -= field_1723;
		r -= field_1724;
		GlStateManager.enableLighting();
		this.entityRenderManager.method_12446(this.itemEntity, p, q, r, this.itemEntity.yaw, tickDelta, false);
	}

	@Override
	public void method_12241() {
		this.pickupAge++;
		if (this.pickupAge == this.pickupMaxAge) {
			this.method_12251();
		}
	}

	@Override
	public int getLayer() {
		return 3;
	}
}
