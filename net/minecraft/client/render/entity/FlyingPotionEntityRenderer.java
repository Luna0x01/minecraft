package net.minecraft.client.render.entity;

import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.entity.thrown.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class FlyingPotionEntityRenderer extends FlyingItemEntityRenderer<PotionEntity> {
	public FlyingPotionEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, ItemRenderer itemRenderer) {
		super(entityRenderDispatcher, Items.POTION, itemRenderer);
	}

	public ItemStack createStack(PotionEntity potionEntity) {
		return new ItemStack(this.item, 1, potionEntity.method_3237());
	}
}
