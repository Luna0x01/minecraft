package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerClothingFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ZombieVillagerEntityModel;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.util.Identifier;

public class ZombieVillagerEntityRenderer extends BipedEntityRenderer<ZombieVillagerEntity, ZombieVillagerEntityModel<ZombieVillagerEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/zombie_villager/zombie_villager.png");

	public ZombieVillagerEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new ZombieVillagerEntityModel<>(context.getPart(EntityModelLayers.ZOMBIE_VILLAGER)), 0.5F);
		this.addFeature(
			new ArmorFeatureRenderer<>(
				this,
				new ZombieVillagerEntityModel(context.getPart(EntityModelLayers.ZOMBIE_VILLAGER_INNER_ARMOR)),
				new ZombieVillagerEntityModel(context.getPart(EntityModelLayers.ZOMBIE_VILLAGER_OUTER_ARMOR))
			)
		);
		this.addFeature(new VillagerClothingFeatureRenderer<>(this, context.getResourceManager(), "zombie_villager"));
	}

	public Identifier getTexture(ZombieVillagerEntity zombieVillagerEntity) {
		return TEXTURE;
	}

	protected boolean isShaking(ZombieVillagerEntity zombieVillagerEntity) {
		return super.isShaking(zombieVillagerEntity) || zombieVillagerEntity.isConverting();
	}
}
