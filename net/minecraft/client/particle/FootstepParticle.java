package net.minecraft.client.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FootstepParticle extends Particle {
	private static final Identifier FOOTPRINT = new Identifier("textures/particle/footprint.png");
	private int footstepAge;
	private int footstepMaxAge;
	private TextureManager manager;

	protected FootstepParticle(TextureManager textureManager, World world, double d, double e, double f) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.manager = textureManager;
		this.velocityX = this.velocityY = this.velocityZ = 0.0;
		this.footstepMaxAge = 200;
	}

	@Override
	public void draw(BufferBuilder builder, Entity entity, float tickDelta, float g, float h, float i, float j, float k) {
		float f = ((float)this.footstepAge + tickDelta) / (float)this.footstepMaxAge;
		f *= f;
		float l = 2.0F - f * 2.0F;
		if (l > 1.0F) {
			l = 1.0F;
		}

		l *= 0.2F;
		GlStateManager.disableLighting();
		float m = 0.125F;
		float n = (float)(this.field_13428 - field_1722);
		float o = (float)(this.field_13429 - field_1723);
		float p = (float)(this.field_13430 - field_1724);
		float q = this.field_13424.getBrightness(new BlockPos(this.field_13428, this.field_13429, this.field_13430));
		this.manager.bindTexture(FOOTPRINT);
		GlStateManager.enableBlend();
		GlStateManager.method_12287(GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA);
		builder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
		builder.vertex((double)(n - 0.125F), (double)o, (double)(p + 0.125F)).texture(0.0, 1.0).color(q, q, q, l).next();
		builder.vertex((double)(n + 0.125F), (double)o, (double)(p + 0.125F)).texture(1.0, 1.0).color(q, q, q, l).next();
		builder.vertex((double)(n + 0.125F), (double)o, (double)(p - 0.125F)).texture(1.0, 0.0).color(q, q, q, l).next();
		builder.vertex((double)(n - 0.125F), (double)o, (double)(p - 0.125F)).texture(0.0, 0.0).color(q, q, q, l).next();
		Tessellator.getInstance().draw();
		GlStateManager.disableBlend();
		GlStateManager.enableLighting();
	}

	@Override
	public void method_12241() {
		this.footstepAge++;
		if (this.footstepAge == this.footstepMaxAge) {
			this.method_12251();
		}
	}

	@Override
	public int getLayer() {
		return 3;
	}

	public static class Factory implements ParticleFactory {
		@Override
		public Particle createParticle(int id, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... arr) {
			return new FootstepParticle(MinecraftClient.getInstance().getTextureManager(), world, x, y, z);
		}
	}
}
