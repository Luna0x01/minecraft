package net.minecraft.client.render.entity;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.MinecartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public class MinecartEntityRenderer<T extends AbstractMinecartEntity> extends EntityRenderer<T> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/minecart.png");
	protected final EntityModel<T> model;

	public MinecartEntityRenderer(EntityRendererFactory.Context ctx, EntityModelLayer layer) {
		super(ctx);
		this.shadowRadius = 0.7F;
		this.model = new MinecartEntityModel<>(ctx.getPart(layer));
	}

	public void render(T abstractMinecartEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		super.render(abstractMinecartEntity, f, g, matrixStack, vertexConsumerProvider, i);
		matrixStack.push();
		long l = (long)abstractMinecartEntity.getId() * 493286711L;
		l = l * l * 4392167121L + l * 98761L;
		float h = (((float)(l >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		float j = (((float)(l >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		float k = (((float)(l >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		matrixStack.translate((double)h, (double)j, (double)k);
		double d = MathHelper.lerp((double)g, abstractMinecartEntity.lastRenderX, abstractMinecartEntity.getX());
		double e = MathHelper.lerp((double)g, abstractMinecartEntity.lastRenderY, abstractMinecartEntity.getY());
		double m = MathHelper.lerp((double)g, abstractMinecartEntity.lastRenderZ, abstractMinecartEntity.getZ());
		double n = 0.3F;
		Vec3d vec3d = abstractMinecartEntity.snapPositionToRail(d, e, m);
		float o = MathHelper.lerp(g, abstractMinecartEntity.prevPitch, abstractMinecartEntity.getPitch());
		if (vec3d != null) {
			Vec3d vec3d2 = abstractMinecartEntity.snapPositionToRailWithOffset(d, e, m, 0.3F);
			Vec3d vec3d3 = abstractMinecartEntity.snapPositionToRailWithOffset(d, e, m, -0.3F);
			if (vec3d2 == null) {
				vec3d2 = vec3d;
			}

			if (vec3d3 == null) {
				vec3d3 = vec3d;
			}

			matrixStack.translate(vec3d.x - d, (vec3d2.y + vec3d3.y) / 2.0 - e, vec3d.z - m);
			Vec3d vec3d4 = vec3d3.add(-vec3d2.x, -vec3d2.y, -vec3d2.z);
			if (vec3d4.length() != 0.0) {
				vec3d4 = vec3d4.normalize();
				f = (float)(Math.atan2(vec3d4.z, vec3d4.x) * 180.0 / Math.PI);
				o = (float)(Math.atan(vec3d4.y) * 73.0);
			}
		}

		matrixStack.translate(0.0, 0.375, 0.0);
		matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F - f));
		matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-o));
		float p = (float)abstractMinecartEntity.getDamageWobbleTicks() - g;
		float q = abstractMinecartEntity.getDamageWobbleStrength() - g;
		if (q < 0.0F) {
			q = 0.0F;
		}

		if (p > 0.0F) {
			matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(MathHelper.sin(p) * p * q / 10.0F * (float)abstractMinecartEntity.getDamageWobbleSide()));
		}

		int r = abstractMinecartEntity.getBlockOffset();
		BlockState blockState = abstractMinecartEntity.getContainedBlock();
		if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
			matrixStack.push();
			float s = 0.75F;
			matrixStack.scale(0.75F, 0.75F, 0.75F);
			matrixStack.translate(-0.5, (double)((float)(r - 8) / 16.0F), 0.5);
			matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90.0F));
			this.renderBlock(abstractMinecartEntity, g, blockState, matrixStack, vertexConsumerProvider, i);
			matrixStack.pop();
		}

		matrixStack.scale(-1.0F, -1.0F, 1.0F);
		this.model.setAngles(abstractMinecartEntity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F);
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(this.getTexture(abstractMinecartEntity)));
		this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
		matrixStack.pop();
	}

	public Identifier getTexture(T abstractMinecartEntity) {
		return TEXTURE;
	}

	protected void renderBlock(T entity, float delta, BlockState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(state, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
	}
}
