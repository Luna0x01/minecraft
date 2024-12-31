package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.entity.mob.ZombieEntity;

public class ZombieEntityRenderer extends ZombieBaseEntityRenderer<ZombieEntity, ZombieEntityModel<ZombieEntity>> {
	public ZombieEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new ZombieEntityModel<>(), new ZombieEntityModel<>(0.5F, true), new ZombieEntityModel<>(1.0F, true));
	}
}
