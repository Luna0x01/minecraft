package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.util.math.MathHelper;

public class LlamaEntityModel<T extends AbstractDonkeyEntity> extends EntityModel<T> {
	private final ModelPart head;
	private final ModelPart torso;
	private final ModelPart rightBackLeg;
	private final ModelPart leftBackLeg;
	private final ModelPart rightFrontLeg;
	private final ModelPart leftFrontLeg;
	private final ModelPart rightChest;
	private final ModelPart leftChest;

	public LlamaEntityModel(float f) {
		this.textureWidth = 128;
		this.textureHeight = 64;
		this.head = new ModelPart(this, 0, 0);
		this.head.addCuboid(-2.0F, -14.0F, -10.0F, 4.0F, 4.0F, 9.0F, f);
		this.head.setPivot(0.0F, 7.0F, -6.0F);
		this.head.setTextureOffset(0, 14).addCuboid(-4.0F, -16.0F, -6.0F, 8.0F, 18.0F, 6.0F, f);
		this.head.setTextureOffset(17, 0).addCuboid(-4.0F, -19.0F, -4.0F, 3.0F, 3.0F, 2.0F, f);
		this.head.setTextureOffset(17, 0).addCuboid(1.0F, -19.0F, -4.0F, 3.0F, 3.0F, 2.0F, f);
		this.torso = new ModelPart(this, 29, 0);
		this.torso.addCuboid(-6.0F, -10.0F, -7.0F, 12.0F, 18.0F, 10.0F, f);
		this.torso.setPivot(0.0F, 5.0F, 2.0F);
		this.rightChest = new ModelPart(this, 45, 28);
		this.rightChest.addCuboid(-3.0F, 0.0F, 0.0F, 8.0F, 8.0F, 3.0F, f);
		this.rightChest.setPivot(-8.5F, 3.0F, 3.0F);
		this.rightChest.yaw = (float) (Math.PI / 2);
		this.leftChest = new ModelPart(this, 45, 41);
		this.leftChest.addCuboid(-3.0F, 0.0F, 0.0F, 8.0F, 8.0F, 3.0F, f);
		this.leftChest.setPivot(5.5F, 3.0F, 3.0F);
		this.leftChest.yaw = (float) (Math.PI / 2);
		int i = 4;
		int j = 14;
		this.rightBackLeg = new ModelPart(this, 29, 29);
		this.rightBackLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, f);
		this.rightBackLeg.setPivot(-2.5F, 10.0F, 6.0F);
		this.leftBackLeg = new ModelPart(this, 29, 29);
		this.leftBackLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, f);
		this.leftBackLeg.setPivot(2.5F, 10.0F, 6.0F);
		this.rightFrontLeg = new ModelPart(this, 29, 29);
		this.rightFrontLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, f);
		this.rightFrontLeg.setPivot(-2.5F, 10.0F, -4.0F);
		this.leftFrontLeg = new ModelPart(this, 29, 29);
		this.leftFrontLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, f);
		this.leftFrontLeg.setPivot(2.5F, 10.0F, -4.0F);
		this.rightBackLeg.pivotX--;
		this.leftBackLeg.pivotX++;
		this.rightBackLeg.pivotZ += 0.0F;
		this.leftBackLeg.pivotZ += 0.0F;
		this.rightFrontLeg.pivotX--;
		this.leftFrontLeg.pivotX++;
		this.rightFrontLeg.pivotZ--;
		this.leftFrontLeg.pivotZ--;
	}

	public void setAngles(T abstractDonkeyEntity, float f, float g, float h, float i, float j) {
		this.head.pitch = j * (float) (Math.PI / 180.0);
		this.head.yaw = i * (float) (Math.PI / 180.0);
		this.torso.pitch = (float) (Math.PI / 2);
		this.rightBackLeg.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
		this.leftBackLeg.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
		this.rightFrontLeg.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
		this.leftFrontLeg.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
		boolean bl = !abstractDonkeyEntity.isBaby() && abstractDonkeyEntity.hasChest();
		this.rightChest.visible = bl;
		this.leftChest.visible = bl;
	}

	@Override
	public void render(MatrixStack matrixStack, VertexConsumer vertexConsumer, int i, int j, float f, float g, float h, float k) {
		if (this.child) {
			float l = 2.0F;
			matrixStack.push();
			float m = 0.7F;
			matrixStack.scale(0.71428573F, 0.64935064F, 0.7936508F);
			matrixStack.translate(0.0, 1.3125, 0.22F);
			this.head.render(matrixStack, vertexConsumer, i, j, f, g, h, k);
			matrixStack.pop();
			matrixStack.push();
			float n = 1.1F;
			matrixStack.scale(0.625F, 0.45454544F, 0.45454544F);
			matrixStack.translate(0.0, 2.0625, 0.0);
			this.torso.render(matrixStack, vertexConsumer, i, j, f, g, h, k);
			matrixStack.pop();
			matrixStack.push();
			matrixStack.scale(0.45454544F, 0.41322312F, 0.45454544F);
			matrixStack.translate(0.0, 2.0625, 0.0);
			ImmutableList.of(this.rightBackLeg, this.leftBackLeg, this.rightFrontLeg, this.leftFrontLeg, this.rightChest, this.leftChest)
				.forEach(modelPart -> modelPart.render(matrixStack, vertexConsumer, i, j, f, g, h, k));
			matrixStack.pop();
		} else {
			ImmutableList.of(this.head, this.torso, this.rightBackLeg, this.leftBackLeg, this.rightFrontLeg, this.leftFrontLeg, this.rightChest, this.leftChest)
				.forEach(modelPart -> modelPart.render(matrixStack, vertexConsumer, i, j, f, g, h, k));
		}
	}
}
