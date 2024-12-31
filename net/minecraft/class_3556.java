package net.minecraft;

import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WallHangableItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class class_3556 extends WallHangableItem {
	public class_3556(Item.Settings settings) {
		super(ItemFrameEntity.class, settings);
	}

	@Override
	protected boolean method_16066(PlayerEntity playerEntity, Direction direction, ItemStack itemStack, BlockPos blockPos) {
		return !World.method_11475(blockPos) && playerEntity.canModify(blockPos, direction, itemStack);
	}
}
