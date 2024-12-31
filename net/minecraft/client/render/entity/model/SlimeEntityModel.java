package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;

public class SlimeEntityModel extends EntityModel {
	private final ModelPart field_1526;
	private final ModelPart field_1527;
	private final ModelPart field_1528;
	private final ModelPart field_1529;

	public SlimeEntityModel(int i) {
		if (i > 0) {
			this.field_1526 = new ModelPart(this, 0, i);
			this.field_1526.addCuboid(-3.0F, 17.0F, -3.0F, 6, 6, 6);
			this.field_1527 = new ModelPart(this, 32, 0);
			this.field_1527.addCuboid(-3.25F, 18.0F, -3.5F, 2, 2, 2);
			this.field_1528 = new ModelPart(this, 32, 4);
			this.field_1528.addCuboid(1.25F, 18.0F, -3.5F, 2, 2, 2);
			this.field_1529 = new ModelPart(this, 32, 8);
			this.field_1529.addCuboid(0.0F, 21.0F, -3.5F, 1, 1, 1);
		} else {
			this.field_1526 = new ModelPart(this, 0, i);
			this.field_1526.addCuboid(-4.0F, 16.0F, -4.0F, 8, 8, 8);
			this.field_1527 = null;
			this.field_1528 = null;
			this.field_1529 = null;
		}
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		this.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		GlStateManager.translate(0.0F, 0.001F, 0.0F);
		this.field_1526.render(scale);
		if (this.field_1527 != null) {
			this.field_1527.render(scale);
			this.field_1528.render(scale);
			this.field_1529.render(scale);
		}
	}
}
