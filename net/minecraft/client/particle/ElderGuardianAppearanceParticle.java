package net.minecraft.client.particle;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ElderGuardianEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.GuardianEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public class ElderGuardianAppearanceParticle extends Particle {
	private final Model model;
	private final RenderLayer LAYER = RenderLayer.getEntityTranslucent(ElderGuardianEntityRenderer.TEXTURE);

	ElderGuardianAppearanceParticle(ClientWorld clientWorld, double d, double e, double f) {
		super(clientWorld, d, e, f);
		this.model = new GuardianEntityModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(EntityModelLayers.ELDER_GUARDIAN));
		this.gravityStrength = 0.0F;
		this.maxAge = 30;
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.CUSTOM;
	}

	@Override
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		float f = ((float)this.age + tickDelta) / (float)this.maxAge;
		float g = 0.05F + 0.5F * MathHelper.sin(f * (float) Math.PI);
		MatrixStack matrixStack = new MatrixStack();
		matrixStack.multiply(camera.getRotation());
		matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(150.0F * f - 60.0F));
		matrixStack.scale(-1.0F, -1.0F, 1.0F);
		matrixStack.translate(0.0, -1.101F, 1.5);
		VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
		VertexConsumer vertexConsumer2 = immediate.getBuffer(this.LAYER);
		this.model.render(matrixStack, vertexConsumer2, 15728880, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, g);
		immediate.draw();
	}

	public static class Factory implements ParticleFactory<DefaultParticleType> {
		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			return new ElderGuardianAppearanceParticle(clientWorld, d, e, f);
		}
	}
}
