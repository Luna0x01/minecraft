package net.minecraft.client.render.entity.model;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class SkullEntityModel extends SkullBlockEntityModel {
	private final ModelPart root;
	protected final ModelPart head;

	public SkullEntityModel(ModelPart root) {
		this.root = root;
		this.head = root.getChild("head");
	}

	public static ModelData getModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F), ModelTransform.NONE);
		return modelData;
	}

	public static TexturedModelData getHeadTexturedModelData() {
		ModelData modelData = getModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.getChild("head")
			.addChild("hat", ModelPartBuilder.create().uv(32, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.25F)), ModelTransform.NONE);
		return TexturedModelData.of(modelData, 64, 64);
	}

	public static TexturedModelData getSkullTexturedModelData() {
		ModelData modelData = getModelData();
		return TexturedModelData.of(modelData, 64, 32);
	}

	@Override
	public void setHeadRotation(float animationProgress, float yaw, float pitch) {
		this.head.yaw = yaw * (float) (Math.PI / 180.0);
		this.head.pitch = pitch * (float) (Math.PI / 180.0);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
		this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
	}
}
