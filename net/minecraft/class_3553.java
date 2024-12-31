package net.minecraft;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;

public class class_3553 extends BlockItem {
	public class_3553(Block block, Item.Settings settings) {
		super(block, settings);
	}

	@Nullable
	@Override
	protected BlockState method_16016(ItemPlacementContext itemPlacementContext) {
		PlayerEntity playerEntity = itemPlacementContext.getPlayer();
		return playerEntity != null && !playerEntity.method_15936() ? null : super.method_16016(itemPlacementContext);
	}
}
