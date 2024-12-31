package net.minecraft.client.particle;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ElderGuardianEntityRenderer;
import net.minecraft.client.render.entity.model.GuardianEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ElderGuardianAppearanceParticle extends Particle {
	private final Model field_21793 = new GuardianEntityModel();
	private final RenderLayer field_21792 = RenderLayer.getEntityTranslucent(ElderGuardianEntityRenderer.SKIN);

	private ElderGuardianAppearanceParticle(World world, double d, double e, double f) {
		super(world, d, e, f);
		this.gravityStrength = 0.0F;
		this.maxAge = 30;
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.CUSTOM;
	}

	@Override
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float f) {
		float g = ((float)this.age + f) / (float)this.maxAge;
		float h = 0.05F + 0.5F * MathHelper.sin(g * (float) Math.PI);
		MatrixStack matrixStack = new MatrixStack();
		matrixStack.multiply(camera.getRotation());
		matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(150.0F * g - 60.0F));
		matrixStack.scale(-1.0F, -1.0F, 1.0F);
		matrixStack.translate(0.0, -1.101F, 1.5);
		VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
		VertexConsumer vertexConsumer2 = immediate.getBuffer(this.field_21792);
		this.field_21793.render(matrixStack, vertexConsumer2, 15728880, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, h);
		immediate.draw();
	}

	public static class Factory implements ParticleFactory<DefaultParticleType> {
		public Particle createParticle(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new ElderGuardianAppearanceParticle(world, d, e, f);
		}
	}
}
