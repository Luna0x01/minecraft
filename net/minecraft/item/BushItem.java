package net.minecraft.item;

import com.google.common.base.Function;
import net.minecraft.block.Block;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.client.color.world.GrassColors;

public class BushItem extends VariantBlockItem {
	public BushItem(Block block, Block block2, Function<ItemStack, String> function) {
		super(block, block2, function);
	}

	@Override
	public int getDisplayColor(ItemStack stack, int color) {
		DoublePlantBlock.DoublePlantType doublePlantType = DoublePlantBlock.DoublePlantType.getById(stack.getData());
		return doublePlantType != DoublePlantBlock.DoublePlantType.GRASS && doublePlantType != DoublePlantBlock.DoublePlantType.FERN
			? super.getDisplayColor(stack, color)
			: GrassColors.getColor(0.5, 1.0);
	}
}
