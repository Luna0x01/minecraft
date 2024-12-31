package net.minecraft.client.render.entity.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

public class SkullEntityModel extends Model {
	protected final ModelPart skull;

	public SkullEntityModel() {
		this(0, 35, 64, 64);
	}

	public SkullEntityModel(int textureU, int textureV, int textureWidth, int textureHeight) {
		super(RenderLayer::getEntityTranslucent);
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		this.skull = new ModelPart(this, textureU, textureV);
		this.skull.addCuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F);
		this.skull.setPivot(0.0F, 0.0F, 0.0F);
	}

	public void method_2821(float f, float g, float h) {
		this.skull.yaw = g * (float) (Math.PI / 180.0);
		this.skull.pitch = h * (float) (Math.PI / 180.0);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
		this.skull.render(matrices, vertices, light, overlay, red, green, blue, alpha);
	}
}
