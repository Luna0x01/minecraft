package net.minecraft.client.render.entity;

import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.entity.thrown.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class FlyingPotionEntityRenderer extends FlyingItemEntityRenderer<PotionEntity> {
	public FlyingPotionEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, HeldItemRenderer heldItemRenderer) {
		super(entityRenderDispatcher, Items.POTION, heldItemRenderer);
	}

	public ItemStack createStack(PotionEntity potionEntity) {
		return potionEntity.getItem();
	}
}
