package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.util.math.MathHelper;

public class FoxEntityModel<T extends FoxEntity> extends AnimalModel<T> {
	public final ModelPart head;
	private final ModelPart body;
	private final ModelPart rightHindLeg;
	private final ModelPart leftHindLeg;
	private final ModelPart rightFrontLeg;
	private final ModelPart leftFrontLeg;
	private final ModelPart tail;
	private static final int field_32477 = 6;
	private static final float HEAD_Y_PIVOT = 16.5F;
	private static final float LEG_Y_PIVOT = 17.5F;
	private float legPitchModifier;

	public FoxEntityModel(ModelPart root) {
		super(true, 8.0F, 3.35F);
		this.head = root.getChild("head");
		this.body = root.getChild("body");
		this.rightHindLeg = root.getChild("right_hind_leg");
		this.leftHindLeg = root.getChild("left_hind_leg");
		this.rightFrontLeg = root.getChild("right_front_leg");
		this.leftFrontLeg = root.getChild("left_front_leg");
		this.tail = this.body.getChild("tail");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData modelPartData2 = modelPartData.addChild(
			"head", ModelPartBuilder.create().uv(1, 5).cuboid(-3.0F, -2.0F, -5.0F, 8.0F, 6.0F, 6.0F), ModelTransform.pivot(-1.0F, 16.5F, -3.0F)
		);
		modelPartData2.addChild("right_ear", ModelPartBuilder.create().uv(8, 1).cuboid(-3.0F, -4.0F, -4.0F, 2.0F, 2.0F, 1.0F), ModelTransform.NONE);
		modelPartData2.addChild("left_ear", ModelPartBuilder.create().uv(15, 1).cuboid(3.0F, -4.0F, -4.0F, 2.0F, 2.0F, 1.0F), ModelTransform.NONE);
		modelPartData2.addChild("nose", ModelPartBuilder.create().uv(6, 18).cuboid(-1.0F, 2.01F, -8.0F, 4.0F, 2.0F, 3.0F), ModelTransform.NONE);
		ModelPartData modelPartData3 = modelPartData.addChild(
			"body",
			ModelPartBuilder.create().uv(24, 15).cuboid(-3.0F, 3.999F, -3.5F, 6.0F, 11.0F, 6.0F),
			ModelTransform.of(0.0F, 16.0F, -6.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
		);
		Dilation dilation = new Dilation(0.001F);
		ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(4, 24).cuboid(2.0F, 0.5F, -1.0F, 2.0F, 6.0F, 2.0F, dilation);
		ModelPartBuilder modelPartBuilder2 = ModelPartBuilder.create().uv(13, 24).cuboid(2.0F, 0.5F, -1.0F, 2.0F, 6.0F, 2.0F, dilation);
		modelPartData.addChild("right_hind_leg", modelPartBuilder2, ModelTransform.pivot(-5.0F, 17.5F, 7.0F));
		modelPartData.addChild("left_hind_leg", modelPartBuilder, ModelTransform.pivot(-1.0F, 17.5F, 7.0F));
		modelPartData.addChild("right_front_leg", modelPartBuilder2, ModelTransform.pivot(-5.0F, 17.5F, 0.0F));
		modelPartData.addChild("left_front_leg", modelPartBuilder, ModelTransform.pivot(-1.0F, 17.5F, 0.0F));
		modelPartData3.addChild(
			"tail", ModelPartBuilder.create().uv(30, 0).cuboid(2.0F, 0.0F, -1.0F, 4.0F, 9.0F, 5.0F), ModelTransform.of(-4.0F, 15.0F, -1.0F, -0.05235988F, 0.0F, 0.0F)
		);
		return TexturedModelData.of(modelData, 48, 32);
	}

	public void animateModel(T foxEntity, float f, float g, float h) {
		this.body.pitch = (float) (Math.PI / 2);
		this.tail.pitch = -0.05235988F;
		this.rightHindLeg.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
		this.leftHindLeg.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
		this.rightFrontLeg.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
		this.leftFrontLeg.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
		this.head.setPivot(-1.0F, 16.5F, -3.0F);
		this.head.yaw = 0.0F;
		this.head.roll = foxEntity.getHeadRoll(h);
		this.rightHindLeg.visible = true;
		this.leftHindLeg.visible = true;
		this.rightFrontLeg.visible = true;
		this.leftFrontLeg.visible = true;
		this.body.setPivot(0.0F, 16.0F, -6.0F);
		this.body.roll = 0.0F;
		this.rightHindLeg.setPivot(-5.0F, 17.5F, 7.0F);
		this.leftHindLeg.setPivot(-1.0F, 17.5F, 7.0F);
		if (foxEntity.isInSneakingPose()) {
			this.body.pitch = 1.6755161F;
			float i = foxEntity.getBodyRotationHeightOffset(h);
			this.body.setPivot(0.0F, 16.0F + foxEntity.getBodyRotationHeightOffset(h), -6.0F);
			this.head.setPivot(-1.0F, 16.5F + i, -3.0F);
			this.head.yaw = 0.0F;
		} else if (foxEntity.isSleeping()) {
			this.body.roll = (float) (-Math.PI / 2);
			this.body.setPivot(0.0F, 21.0F, -6.0F);
			this.tail.pitch = (float) (-Math.PI * 5.0 / 6.0);
			if (this.child) {
				this.tail.pitch = -2.1816616F;
				this.body.setPivot(0.0F, 21.0F, -2.0F);
			}

			this.head.setPivot(1.0F, 19.49F, -3.0F);
			this.head.pitch = 0.0F;
			this.head.yaw = (float) (-Math.PI * 2.0 / 3.0);
			this.head.roll = 0.0F;
			this.rightHindLeg.visible = false;
			this.leftHindLeg.visible = false;
			this.rightFrontLeg.visible = false;
			this.leftFrontLeg.visible = false;
		} else if (foxEntity.isSitting()) {
			this.body.pitch = (float) (Math.PI / 6);
			this.body.setPivot(0.0F, 9.0F, -3.0F);
			this.tail.pitch = (float) (Math.PI / 4);
			this.tail.setPivot(-4.0F, 15.0F, -2.0F);
			this.head.setPivot(-1.0F, 10.0F, -0.25F);
			this.head.pitch = 0.0F;
			this.head.yaw = 0.0F;
			if (this.child) {
				this.head.setPivot(-1.0F, 13.0F, -3.75F);
			}

			this.rightHindLeg.pitch = (float) (-Math.PI * 5.0 / 12.0);
			this.rightHindLeg.setPivot(-5.0F, 21.5F, 6.75F);
			this.leftHindLeg.pitch = (float) (-Math.PI * 5.0 / 12.0);
			this.leftHindLeg.setPivot(-1.0F, 21.5F, 6.75F);
			this.rightFrontLeg.pitch = (float) (-Math.PI / 12);
			this.leftFrontLeg.pitch = (float) (-Math.PI / 12);
		}
	}

	@Override
	protected Iterable<ModelPart> getHeadParts() {
		return ImmutableList.of(this.head);
	}

	@Override
	protected Iterable<ModelPart> getBodyParts() {
		return ImmutableList.of(this.body, this.rightHindLeg, this.leftHindLeg, this.rightFrontLeg, this.leftFrontLeg);
	}

	public void setAngles(T foxEntity, float f, float g, float h, float i, float j) {
		if (!foxEntity.isSleeping() && !foxEntity.isWalking() && !foxEntity.isInSneakingPose()) {
			this.head.pitch = j * (float) (Math.PI / 180.0);
			this.head.yaw = i * (float) (Math.PI / 180.0);
		}

		if (foxEntity.isSleeping()) {
			this.head.pitch = 0.0F;
			this.head.yaw = (float) (-Math.PI * 2.0 / 3.0);
			this.head.roll = MathHelper.cos(h * 0.027F) / 22.0F;
		}

		if (foxEntity.isInSneakingPose()) {
			float k = MathHelper.cos(h) * 0.01F;
			this.body.yaw = k;
			this.rightHindLeg.roll = k;
			this.leftHindLeg.roll = k;
			this.rightFrontLeg.roll = k / 2.0F;
			this.leftFrontLeg.roll = k / 2.0F;
		}

		if (foxEntity.isWalking()) {
			float l = 0.1F;
			this.legPitchModifier += 0.67F;
			this.rightHindLeg.pitch = MathHelper.cos(this.legPitchModifier * 0.4662F) * 0.1F;
			this.leftHindLeg.pitch = MathHelper.cos(this.legPitchModifier * 0.4662F + (float) Math.PI) * 0.1F;
			this.rightFrontLeg.pitch = MathHelper.cos(this.legPitchModifier * 0.4662F + (float) Math.PI) * 0.1F;
			this.leftFrontLeg.pitch = MathHelper.cos(this.legPitchModifier * 0.4662F) * 0.1F;
		}
	}
}
