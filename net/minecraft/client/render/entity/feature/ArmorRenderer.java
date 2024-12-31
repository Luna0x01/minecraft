package net.minecraft.client.render.entity.feature;

import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.BiPedModel;
import net.minecraft.entity.EquipmentSlot;

public class ArmorRenderer extends ArmorFeatureRenderer<BiPedModel> {
	public ArmorRenderer(LivingEntityRenderer<?> livingEntityRenderer) {
		super(livingEntityRenderer);
	}

	@Override
	protected void init() {
		this.secondLayer = new BiPedModel(0.5F);
		this.firstLayer = new BiPedModel(1.0F);
	}

	protected void method_10277(BiPedModel biPedModel, EquipmentSlot equipmentSlot) {
		this.setVisible(biPedModel);
		switch (equipmentSlot) {
			case HEAD:
				biPedModel.head.visible = true;
				biPedModel.hat.visible = true;
				break;
			case CHEST:
				biPedModel.body.visible = true;
				biPedModel.rightArm.visible = true;
				biPedModel.leftArm.visible = true;
				break;
			case LEGS:
				biPedModel.body.visible = true;
				biPedModel.rightLeg.visible = true;
				biPedModel.leftLeg.visible = true;
				break;
			case FEET:
				biPedModel.rightLeg.visible = true;
				biPedModel.leftLeg.visible = true;
		}
	}

	protected void setVisible(BiPedModel bipedModel) {
		bipedModel.setVisible(false);
	}
}
