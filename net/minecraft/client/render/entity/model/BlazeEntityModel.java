package net.minecraft.client.render.entity.model;

import java.util.Arrays;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class BlazeEntityModel<T extends Entity> extends SinglePartEntityModel<T> {
	private final ModelPart root;
	private final ModelPart[] rods;
	private final ModelPart head;

	public BlazeEntityModel(ModelPart root) {
		this.root = root;
		this.head = root.getChild("head");
		this.rods = new ModelPart[12];
		Arrays.setAll(this.rods, index -> root.getChild(getRodName(index)));
	}

	private static String getRodName(int index) {
		return "part" + index;
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), ModelTransform.NONE);
		float f = 0.0F;
		ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(0, 16).cuboid(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F);

		for (int i = 0; i < 4; i++) {
			float g = MathHelper.cos(f) * 9.0F;
			float h = -2.0F + MathHelper.cos((float)(i * 2) * 0.25F);
			float j = MathHelper.sin(f) * 9.0F;
			modelPartData.addChild(getRodName(i), modelPartBuilder, ModelTransform.pivot(g, h, j));
			f++;
		}

		f = (float) (Math.PI / 4);

		for (int k = 4; k < 8; k++) {
			float l = MathHelper.cos(f) * 7.0F;
			float m = 2.0F + MathHelper.cos((float)(k * 2) * 0.25F);
			float n = MathHelper.sin(f) * 7.0F;
			modelPartData.addChild(getRodName(k), modelPartBuilder, ModelTransform.pivot(l, m, n));
			f++;
		}

		f = 0.47123894F;

		for (int o = 8; o < 12; o++) {
			float p = MathHelper.cos(f) * 5.0F;
			float q = 11.0F + MathHelper.cos((float)o * 1.5F * 0.5F);
			float r = MathHelper.sin(f) * 5.0F;
			modelPartData.addChild(getRodName(o), modelPartBuilder, ModelTransform.pivot(p, q, r));
			f++;
		}

		return TexturedModelData.of(modelData, 64, 32);
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		float f = animationProgress * (float) Math.PI * -0.1F;

		for (int i = 0; i < 4; i++) {
			this.rods[i].pivotY = -2.0F + MathHelper.cos(((float)(i * 2) + animationProgress) * 0.25F);
			this.rods[i].pivotX = MathHelper.cos(f) * 9.0F;
			this.rods[i].pivotZ = MathHelper.sin(f) * 9.0F;
			f++;
		}

		f = (float) (Math.PI / 4) + animationProgress * (float) Math.PI * 0.03F;

		for (int j = 4; j < 8; j++) {
			this.rods[j].pivotY = 2.0F + MathHelper.cos(((float)(j * 2) + animationProgress) * 0.25F);
			this.rods[j].pivotX = MathHelper.cos(f) * 7.0F;
			this.rods[j].pivotZ = MathHelper.sin(f) * 7.0F;
			f++;
		}

		f = 0.47123894F + animationProgress * (float) Math.PI * -0.05F;

		for (int k = 8; k < 12; k++) {
			this.rods[k].pivotY = 11.0F + MathHelper.cos(((float)k * 1.5F + animationProgress) * 0.5F);
			this.rods[k].pivotX = MathHelper.cos(f) * 5.0F;
			this.rods[k].pivotZ = MathHelper.sin(f) * 5.0F;
			f++;
		}

		this.head.yaw = headYaw * (float) (Math.PI / 180.0);
		this.head.pitch = headPitch * (float) (Math.PI / 180.0);
	}
}
