package net.minecraft.client.particle;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ElderGuardianAppearanceParticle extends Particle {
	private LivingEntity entity;

	protected ElderGuardianAppearanceParticle(World world, double d, double e, double f) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.red = 1.0F;
		this.green = 1.0F;
		this.blue = 1.0F;
		this.velocityX = 0.0;
		this.velocityY = 0.0;
		this.velocityZ = 0.0;
		this.gravityStrength = 0.0F;
		this.maxAge = 30;
	}

	@Override
	public int getLayer() {
		return 3;
	}

	@Override
	public void method_12241() {
		super.method_12241();
		if (this.entity == null) {
			GuardianEntity guardianEntity = new GuardianEntity(this.field_13424);
			guardianEntity.method_11201();
			this.entity = guardianEntity;
		}
	}

	@Override
	public void draw(BufferBuilder builder, Entity entity, float tickDelta, float g, float h, float i, float j, float k) {
		if (this.entity != null) {
			EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderManager();
			entityRenderDispatcher.updateCamera(Particle.field_1722, Particle.field_1723, Particle.field_1724);
			float f = 0.42553192F;
			float l = ((float)this.age + tickDelta) / (float)this.maxAge;
			GlStateManager.depthMask(true);
			GlStateManager.enableBlend();
			GlStateManager.enableDepthTest();
			GlStateManager.method_12287(GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA);
			float m = 240.0F;
			GLX.gl13MultiTexCoord2f(GLX.lightmapTextureUnit, 240.0F, 240.0F);
			GlStateManager.pushMatrix();
			float n = 0.05F + 0.5F * MathHelper.sin(l * (float) Math.PI);
			GlStateManager.color(1.0F, 1.0F, 1.0F, n);
			GlStateManager.translate(0.0F, 1.8F, 0.0F);
			GlStateManager.rotate(180.0F - entity.yaw, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(60.0F - 150.0F * l - entity.pitch, 1.0F, 0.0F, 0.0F);
			GlStateManager.translate(0.0F, -0.4F, -1.5F);
			GlStateManager.scale(0.42553192F, 0.42553192F, 0.42553192F);
			this.entity.yaw = 0.0F;
			this.entity.headYaw = 0.0F;
			this.entity.prevYaw = 0.0F;
			this.entity.prevHeadYaw = 0.0F;
			entityRenderDispatcher.method_12446(this.entity, 0.0, 0.0, 0.0, 0.0F, tickDelta, false);
			GlStateManager.popMatrix();
			GlStateManager.enableDepthTest();
		}
	}

	public static class Factory implements ParticleFactory {
		@Override
		public Particle createParticle(int id, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... arr) {
			return new ElderGuardianAppearanceParticle(world, x, y, z);
		}
	}
}
