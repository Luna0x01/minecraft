package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.feature.ArmorRenderer;
import net.minecraft.client.render.entity.model.AbstractZombieModel;
import net.minecraft.client.render.entity.model.BiPedModel;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;

public class ZombieBaseEntityRenderer extends BipedEntityRenderer<ZombieEntity> {
	private static final Identifier ZOMBIE = new Identifier("textures/entity/zombie/zombie.png");

	public ZombieBaseEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, BiPedModel biPedModel) {
		super(entityRenderDispatcher, biPedModel, 0.5F);
		this.addFeature(this.method_19431());
	}

	public ZombieBaseEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		this(entityRenderDispatcher, new AbstractZombieModel());
	}

	protected ArmorRenderer method_19431() {
		return new ArmorRenderer(this) {
			@Override
			protected void init() {
				this.secondLayer = new AbstractZombieModel(0.5F, true);
				this.firstLayer = new AbstractZombieModel(1.0F, true);
			}
		};
	}

	protected Identifier getTexture(ZombieEntity zombieEntity) {
		return ZOMBIE;
	}

	protected void method_5777(ZombieEntity zombieEntity, float f, float g, float h) {
		if (zombieEntity.method_15902()) {
			g += (float)(Math.cos((double)zombieEntity.ticksAlive * 3.25) * Math.PI * 0.25);
		}

		super.method_5777(zombieEntity, f, g, h);
	}
}
