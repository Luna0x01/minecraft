package net.minecraft.client.render.entity.feature;

import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.ZombieVillagerEntityModel;

public class ZombieVillagerArmorRenderer extends ArmorRenderer {
	public ZombieVillagerArmorRenderer(LivingEntityRenderer<?> livingEntityRenderer) {
		super(livingEntityRenderer);
	}

	@Override
	protected void init() {
		this.secondLayer = new ZombieVillagerEntityModel(0.5F, 0.0F, true);
		this.firstLayer = new ZombieVillagerEntityModel(1.0F, 0.0F, true);
	}
}
