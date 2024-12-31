package net.minecraft;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.client.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

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
	public void appendTooltips(ItemStack stack, @Nullable World world, List<String> tooltip, TooltipContext tooltipContext) {
		super.appendTooltips(stack, world, tooltip, tooltipContext);
		this.field_15107.method_14306(stack, world, tooltip, tooltipContext);
	}
}
