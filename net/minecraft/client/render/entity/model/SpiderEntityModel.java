package net.minecraft.client.render.entity.model;

import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class SpiderEntityModel<T extends Entity> extends SinglePartEntityModel<T> {
	private static final String BODY0 = "body0";
	private static final String BODY1 = "body1";
	private static final String RIGHT_MIDDLE_FRONT_LEG = "right_middle_front_leg";
	private static final String LEFT_MIDDLE_FRONT_LEG = "left_middle_front_leg";
	private static final String RIGHT_MIDDLE_HIND_LEG = "right_middle_hind_leg";
	private static final String LEFT_MIDDLE_HIND_LEG = "left_middle_hind_leg";
	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart rightHindLeg;
	private final ModelPart leftHindLeg;
	private final ModelPart rightMiddleLeg;
	private final ModelPart leftMiddleLeg;
	private final ModelPart rightMiddleFrontLeg;
	private final ModelPart leftMiddleFrontLeg;
	private final ModelPart rightFrontLeg;
	private final ModelPart leftFrontLeg;

	public SpiderEntityModel(ModelPart root) {
		this.root = root;
		this.head = root.getChild("head");
		this.rightHindLeg = root.getChild("right_hind_leg");
		this.leftHindLeg = root.getChild("left_hind_leg");
		this.rightMiddleLeg = root.getChild("right_middle_hind_leg");
		this.leftMiddleLeg = root.getChild("left_middle_hind_leg");
		this.rightMiddleFrontLeg = root.getChild("right_middle_front_leg");
		this.leftMiddleFrontLeg = root.getChild("left_middle_front_leg");
		this.rightFrontLeg = root.getChild("right_front_leg");
		this.leftFrontLeg = root.getChild("left_front_leg");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		int i = 15;
		modelPartData.addChild("head", ModelPartBuilder.create().uv(32, 4).cuboid(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F), ModelTransform.pivot(0.0F, 15.0F, -3.0F));
		modelPartData.addChild("body0", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F), ModelTransform.pivot(0.0F, 15.0F, 0.0F));
		modelPartData.addChild("body1", ModelPartBuilder.create().uv(0, 12).cuboid(-5.0F, -4.0F, -6.0F, 10.0F, 8.0F, 12.0F), ModelTransform.pivot(0.0F, 15.0F, 9.0F));
		ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(18, 0).cuboid(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F);
		ModelPartBuilder modelPartBuilder2 = ModelPartBuilder.create().uv(18, 0).cuboid(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F);
		modelPartData.addChild("right_hind_leg", modelPartBuilder, ModelTransform.pivot(-4.0F, 15.0F, 2.0F));
		modelPartData.addChild("left_hind_leg", modelPartBuilder2, ModelTransform.pivot(4.0F, 15.0F, 2.0F));
		modelPartData.addChild("right_middle_hind_leg", modelPartBuilder, ModelTransform.pivot(-4.0F, 15.0F, 1.0F));
		modelPartData.addChild("left_middle_hind_leg", modelPartBuilder2, ModelTransform.pivot(4.0F, 15.0F, 1.0F));
		modelPartData.addChild("right_middle_front_leg", modelPartBuilder, ModelTransform.pivot(-4.0F, 15.0F, 0.0F));
		modelPartData.addChild("left_middle_front_leg", modelPartBuilder2, ModelTransform.pivot(4.0F, 15.0F, 0.0F));
		modelPartData.addChild("right_front_leg", modelPartBuilder, ModelTransform.pivot(-4.0F, 15.0F, -1.0F));
		modelPartData.addChild("left_front_leg", modelPartBuilder2, ModelTransform.pivot(4.0F, 15.0F, -1.0F));
		return TexturedModelData.of(modelData, 64, 32);
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		this.head.yaw = headYaw * (float) (Math.PI / 180.0);
		this.head.pitch = headPitch * (float) (Math.PI / 180.0);
		float f = (float) (Math.PI / 4);
		this.rightHindLeg.roll = (float) (-Math.PI / 4);
		this.leftHindLeg.roll = (float) (Math.PI / 4);
		this.rightMiddleLeg.roll = -0.58119464F;
		this.leftMiddleLeg.roll = 0.58119464F;
		this.rightMiddleFrontLeg.roll = -0.58119464F;
		this.leftMiddleFrontLeg.roll = 0.58119464F;
		this.rightFrontLeg.roll = (float) (-Math.PI / 4);
		this.leftFrontLeg.roll = (float) (Math.PI / 4);
		float g = -0.0F;
		float h = (float) (Math.PI / 8);
		this.rightHindLeg.yaw = (float) (Math.PI / 4);
		this.leftHindLeg.yaw = (float) (-Math.PI / 4);
		this.rightMiddleLeg.yaw = (float) (Math.PI / 8);
		this.leftMiddleLeg.yaw = (float) (-Math.PI / 8);
		this.rightMiddleFrontLeg.yaw = (float) (-Math.PI / 8);
		this.leftMiddleFrontLeg.yaw = (float) (Math.PI / 8);
		this.rightFrontLeg.yaw = (float) (-Math.PI / 4);
		this.leftFrontLeg.yaw = (float) (Math.PI / 4);
		float i = -(MathHelper.cos(limbAngle * 0.6662F * 2.0F + 0.0F) * 0.4F) * limbDistance;
		float j = -(MathHelper.cos(limbAngle * 0.6662F * 2.0F + (float) Math.PI) * 0.4F) * limbDistance;
		float k = -(MathHelper.cos(limbAngle * 0.6662F * 2.0F + (float) (Math.PI / 2)) * 0.4F) * limbDistance;
		float l = -(MathHelper.cos(limbAngle * 0.6662F * 2.0F + (float) (Math.PI * 3.0 / 2.0)) * 0.4F) * limbDistance;
		float m = Math.abs(MathHelper.sin(limbAngle * 0.6662F + 0.0F) * 0.4F) * limbDistance;
		float n = Math.abs(MathHelper.sin(limbAngle * 0.6662F + (float) Math.PI) * 0.4F) * limbDistance;
		float o = Math.abs(MathHelper.sin(limbAngle * 0.6662F + (float) (Math.PI / 2)) * 0.4F) * limbDistance;
		float p = Math.abs(MathHelper.sin(limbAngle * 0.6662F + (float) (Math.PI * 3.0 / 2.0)) * 0.4F) * limbDistance;
		this.rightHindLeg.yaw += i;
		this.leftHindLeg.yaw += -i;
		this.rightMiddleLeg.yaw += j;
		this.leftMiddleLeg.yaw += -j;
		this.rightMiddleFrontLeg.yaw += k;
		this.leftMiddleFrontLeg.yaw += -k;
		this.rightFrontLeg.yaw += l;
		this.leftFrontLeg.yaw += -l;
		this.rightHindLeg.roll += m;
		this.leftHindLeg.roll += -m;
		this.rightMiddleLeg.roll += n;
		this.leftMiddleLeg.roll += -n;
		this.rightMiddleFrontLeg.roll += o;
		this.leftMiddleFrontLeg.roll += -o;
		this.rightFrontLeg.roll += p;
		this.leftFrontLeg.roll += -p;
	}
}
