package net.minecraft.client.render.entity.model;

import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class DragonHeadEntityModel extends SkullBlockEntityModel {
	private final ModelPart head;
	private final ModelPart jaw;

	public DragonHeadEntityModel(ModelPart root) {
		this.head = root.getChild("head");
		this.jaw = this.head.getChild("jaw");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		float f = -16.0F;
		ModelPartData modelPartData2 = modelPartData.addChild(
			"head",
			ModelPartBuilder.create()
				.cuboid("upper_lip", -6.0F, -1.0F, -24.0F, 12, 5, 16, 176, 44)
				.cuboid("upper_head", -8.0F, -8.0F, -10.0F, 16, 16, 16, 112, 30)
				.mirrored(true)
				.cuboid("scale", -5.0F, -12.0F, -4.0F, 2, 4, 6, 0, 0)
				.cuboid("nostril", -5.0F, -3.0F, -22.0F, 2, 2, 4, 112, 0)
				.mirrored(false)
				.cuboid("scale", 3.0F, -12.0F, -4.0F, 2, 4, 6, 0, 0)
				.cuboid("nostril", 3.0F, -3.0F, -22.0F, 2, 2, 4, 112, 0),
			ModelTransform.NONE
		);
		modelPartData2.addChild(
			"jaw", ModelPartBuilder.create().uv(176, 65).cuboid("jaw", -6.0F, 0.0F, -16.0F, 12.0F, 4.0F, 16.0F), ModelTransform.pivot(0.0F, 4.0F, -8.0F)
		);
		return TexturedModelData.of(modelData, 256, 256);
	}

	@Override
	public void setHeadRotation(float animationProgress, float yaw, float pitch) {
		this.jaw.pitch = (float)(Math.sin((double)(animationProgress * (float) Math.PI * 0.2F)) + 1.0) * 0.2F;
		this.head.yaw = yaw * (float) (Math.PI / 180.0);
		this.head.pitch = pitch * (float) (Math.PI / 180.0);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
		matrices.push();
		matrices.translate(0.0, -0.374375F, 0.0);
		matrices.scale(0.75F, 0.75F, 0.75F);
		this.head.render(matrices, vertices, light, overlay, red, green, blue, alpha);
		matrices.pop();
	}
}
