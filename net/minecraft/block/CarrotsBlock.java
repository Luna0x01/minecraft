package net.minecraft.block;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class CarrotsBlock extends CropBlock {
	@Override
	protected Item getSeedItem() {
		return Items.CARROT;
	}

	@Override
	protected Item getHarvestItem() {
		return Items.CARROT;
	}
}
