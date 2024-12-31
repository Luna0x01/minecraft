package net.minecraft.item;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;

public class AxeItem extends ToolItem {
	private static final Set<Block> EFFECTIVE_BLOCKS = Sets.newHashSet(
		new Block[]{Blocks.PLANKS, Blocks.BOOKSHELF, Blocks.LOG, Blocks.LOG2, Blocks.CHEST, Blocks.PUMPKIN, Blocks.JACK_O_LANTERN, Blocks.MELON_BLOCK, Blocks.LADDER}
	);

	protected AxeItem(Item.ToolMaterialType toolMaterialType) {
		super(3.0F, toolMaterialType, EFFECTIVE_BLOCKS);
	}

	@Override
	public float getMiningSpeedMultiplier(ItemStack stack, Block block) {
		return block.getMaterial() != Material.WOOD && block.getMaterial() != Material.PLANT && block.getMaterial() != Material.REPLACEABLE_PLANT
			? super.getMiningSpeedMultiplier(stack, block)
			: this.miningSpeed;
	}
}
