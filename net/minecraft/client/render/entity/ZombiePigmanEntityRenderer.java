package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.feature.ArmorRenderer;
import net.minecraft.client.render.entity.model.AbstractZombieModel;
import net.minecraft.entity.mob.ZombiePigmanEntity;
import net.minecraft.util.Identifier;

public class ZombiePigmanEntityRenderer extends BipedEntityRenderer<ZombiePigmanEntity> {
	private static final Identifier ZOMBIE_PIGMAN = new Identifier("textures/entity/zombie_pigman.png");

	public ZombiePigmanEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new AbstractZombieModel(), 0.5F);
		this.addFeature(new ArmorRenderer(this) {
			@Override
			protected void init() {
				this.secondLayer = new AbstractZombieModel(0.5F, true);
				this.firstLayer = new AbstractZombieModel(1.0F, true);
			}
		});
	}

	protected Identifier getTexture(ZombiePigmanEntity zombiePigmanEntity) {
		return ZOMBIE_PIGMAN;
	}
}
