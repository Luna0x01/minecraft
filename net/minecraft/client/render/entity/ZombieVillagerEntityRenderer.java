package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.feature.ZombieVillagerArmorRenderer;
import net.minecraft.client.render.entity.model.ZombieVillagerEntityModel;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.util.Identifier;

public class ZombieVillagerEntityRenderer extends BipedEntityRenderer<ZombieVillagerEntity> {
	private static final Identifier UNEMPLOYED = new Identifier("textures/entity/zombie_villager/zombie_villager.png");
	private static final Identifier FARMER = new Identifier("textures/entity/zombie_villager/zombie_farmer.png");
	private static final Identifier LIBRARIAN = new Identifier("textures/entity/zombie_villager/zombie_librarian.png");
	private static final Identifier PRIEST = new Identifier("textures/entity/zombie_villager/zombie_priest.png");
	private static final Identifier BLACKSMITH = new Identifier("textures/entity/zombie_villager/zombie_smith.png");
	private static final Identifier BUTCHER = new Identifier("textures/entity/zombie_villager/zombie_butcher.png");

	public ZombieVillagerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new ZombieVillagerEntityModel(), 0.5F);
		this.addFeature(new ZombieVillagerArmorRenderer(this));
	}

	protected Identifier getTexture(ZombieVillagerEntity zombieVillagerEntity) {
		switch (zombieVillagerEntity.getVillagerData()) {
			case 0:
				return FARMER;
			case 1:
				return LIBRARIAN;
			case 2:
				return PRIEST;
			case 3:
				return BLACKSMITH;
			case 4:
				return BUTCHER;
			case 5:
			default:
				return UNEMPLOYED;
		}
	}

	protected void method_5777(ZombieVillagerEntity zombieVillagerEntity, float f, float g, float h) {
		if (zombieVillagerEntity.isConverting()) {
			g += (float)(Math.cos((double)zombieVillagerEntity.ticksAlive * 3.25) * Math.PI * 0.25);
		}

		super.method_5777(zombieVillagerEntity, f, g, h);
	}
}
