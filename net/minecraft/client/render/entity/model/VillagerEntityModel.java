package net.minecraft.client.render.entity.model;

import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class VillagerEntityModel extends EntityModel {
	protected ModelPart field_1557;
	protected ModelPart field_1558;
	protected ModelPart field_1559;
	protected ModelPart field_1560;
	protected ModelPart field_1561;
	protected ModelPart field_5132;

	public VillagerEntityModel(float f) {
		this(f, 0.0F, 64, 64);
	}

	public VillagerEntityModel(float f, float g, int i, int j) {
		this.field_1557 = new ModelPart(this).setTextureSize(i, j);
		this.field_1557.setPivot(0.0F, 0.0F + g, 0.0F);
		this.field_1557.setTextureOffset(0, 0).addCuboid(-4.0F, -10.0F, -4.0F, 8, 10, 8, f);
		this.field_5132 = new ModelPart(this).setTextureSize(i, j);
		this.field_5132.setPivot(0.0F, g - 2.0F, 0.0F);
		this.field_5132.setTextureOffset(24, 0).addCuboid(-1.0F, -1.0F, -6.0F, 2, 4, 2, f);
		this.field_1557.add(this.field_5132);
		this.field_1558 = new ModelPart(this).setTextureSize(i, j);
		this.field_1558.setPivot(0.0F, 0.0F + g, 0.0F);
		this.field_1558.setTextureOffset(16, 20).addCuboid(-4.0F, 0.0F, -3.0F, 8, 12, 6, f);
		this.field_1558.setTextureOffset(0, 38).addCuboid(-4.0F, 0.0F, -3.0F, 8, 18, 6, f + 0.5F);
		this.field_1559 = new ModelPart(this).setTextureSize(i, j);
		this.field_1559.setPivot(0.0F, 0.0F + g + 2.0F, 0.0F);
		this.field_1559.setTextureOffset(44, 22).addCuboid(-8.0F, -2.0F, -2.0F, 4, 8, 4, f);
		this.field_1559.setTextureOffset(44, 22).method_18947(4.0F, -2.0F, -2.0F, 4, 8, 4, f, true);
		this.field_1559.setTextureOffset(40, 38).addCuboid(-4.0F, 2.0F, -2.0F, 8, 4, 4, f);
		this.field_1560 = new ModelPart(this, 0, 22).setTextureSize(i, j);
		this.field_1560.setPivot(-2.0F, 12.0F + g, 0.0F);
		this.field_1560.addCuboid(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
		this.field_1561 = new ModelPart(this, 0, 22).setTextureSize(i, j);
		this.field_1561.mirror = true;
		this.field_1561.setPivot(2.0F, 12.0F + g, 0.0F);
		this.field_1561.addCuboid(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		this.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		this.field_1557.render(scale);
		this.field_1558.render(scale);
		this.field_1560.render(scale);
		this.field_1561.render(scale);
		this.field_1559.render(scale);
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		this.field_1557.posY = age * (float) (Math.PI / 180.0);
		this.field_1557.posX = headPitch * (float) (Math.PI / 180.0);
		this.field_1559.pivotY = 3.0F;
		this.field_1559.pivotZ = -1.0F;
		this.field_1559.posX = -0.75F;
		this.field_1560.posX = MathHelper.cos(handSwing * 0.6662F) * 1.4F * handSwingAmount * 0.5F;
		this.field_1561.posX = MathHelper.cos(handSwing * 0.6662F + (float) Math.PI) * 1.4F * handSwingAmount * 0.5F;
		this.field_1560.posY = 0.0F;
		this.field_1561.posY = 0.0F;
	}

	public ModelPart method_18944() {
		return this.field_1557;
	}
}
