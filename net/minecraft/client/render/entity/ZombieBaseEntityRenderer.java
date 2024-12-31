package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.feature.ArmorRenderer;
import net.minecraft.client.render.entity.model.AbstractZombieModel;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;

public class ZombieBaseEntityRenderer extends BipedEntityRenderer<ZombieEntity> {
	private static final Identifier ZOMBIE = new Identifier("textures/entity/zombie/zombie.png");

	public ZombieBaseEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new AbstractZombieModel(), 0.5F);
		ArmorRenderer armorRenderer = new ArmorRenderer(this) {
			@Override
			protected void init() {
				this.secondLayer = new AbstractZombieModel(0.5F, true);
				this.firstLayer = new AbstractZombieModel(1.0F, true);
			}
		};
		this.addFeature(armorRenderer);
	}

	protected Identifier getTexture(ZombieEntity zombieEntity) {
		return ZOMBIE;
	}
}
