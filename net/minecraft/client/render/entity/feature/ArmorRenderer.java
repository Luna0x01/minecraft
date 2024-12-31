package net.minecraft.client.render.entity.feature;

import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.BiPedModel;

public class ArmorRenderer extends ArmorFeatureRenderer<BiPedModel> {
	public ArmorRenderer(LivingEntityRenderer<?> livingEntityRenderer) {
		super(livingEntityRenderer);
	}

	@Override
	protected void init() {
		this.secondLayer = new BiPedModel(0.5F);
		this.firstLayer = new BiPedModel(1.0F);
	}

	protected void setVisible(BiPedModel biPedModel, int i) {
		this.setVisible(biPedModel);
		switch (i) {
			case 1:
				biPedModel.rightLeg.visible = true;
				biPedModel.leftLeg.visible = true;
				break;
			case 2:
				biPedModel.body.visible = true;
				biPedModel.rightLeg.visible = true;
				biPedModel.leftLeg.visible = true;
				break;
			case 3:
				biPedModel.body.visible = true;
				biPedModel.rightArm.visible = true;
				biPedModel.leftArm.visible = true;
				break;
			case 4:
				biPedModel.head.visible = true;
				biPedModel.hat.visible = true;
		}
	}

	protected void setVisible(BiPedModel bipedModel) {
		bipedModel.setVisible(false);
	}
}
