package net.minecraft;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class class_3056 extends Item {
	private final Block field_15107;

	public class_3056(Block block) {
		this.field_15107 = block;
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		return this.field_15107.getTranslationKey();
	}

	@Override
	public String getTranslationKey() {
		return this.field_15107.getTranslationKey();
	}

	@Override
	public void appendTooltip(ItemStack stack, PlayerEntity player, List<String> lines, boolean advanced) {
		super.appendTooltip(stack, player, lines, advanced);
		this.field_15107.method_13701(stack, player, lines, advanced);
	}
}
