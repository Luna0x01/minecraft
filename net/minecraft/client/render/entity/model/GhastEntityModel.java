package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Random;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class GhastEntityModel extends EntityModel {
	ModelPart body;
	ModelPart[] tentacles = new ModelPart[9];

	public GhastEntityModel() {
		int i = -16;
		this.body = new ModelPart(this, 0, 0);
		this.body.addCuboid(-8.0F, -8.0F, -8.0F, 16, 16, 16);
		this.body.pivotY += (float)(24 + i);
		Random random = new Random(1660L);

		for (int j = 0; j < this.tentacles.length; j++) {
			this.tentacles[j] = new ModelPart(this, 0, 0);
			float f = (((float)(j % 3) - (float)(j / 3 % 2) * 0.5F + 0.25F) / 2.0F * 2.0F - 1.0F) * 5.0F;
			float g = ((float)(j / 3) / 2.0F * 2.0F - 1.0F) * 5.0F;
			int k = random.nextInt(7) + 8;
			this.tentacles[j].addCuboid(-1.0F, 0.0F, -1.0F, 2, k, 2);
			this.tentacles[j].pivotX = f;
			this.tentacles[j].pivotZ = g;
			this.tentacles[j].pivotY = (float)(31 + i);
		}
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		for (int i = 0; i < this.tentacles.length; i++) {
			this.tentacles[i].posX = 0.2F * MathHelper.sin(tickDelta * 0.3F + (float)i) + 0.4F;
		}
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		this.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.0F, 0.6F, 0.0F);
		this.body.render(scale);

		for (ModelPart modelPart : this.tentacles) {
			modelPart.render(scale);
		}

		GlStateManager.popMatrix();
	}
}
