package net.minecraft.item;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

public class ShovelItem extends ToolItem {
	private static final Set<Block> EFFECTIVE_BLOCKS = Sets.newHashSet(
		new Block[]{
			Blocks.CLAY, Blocks.DIRT, Blocks.FARMLAND, Blocks.GRASS, Blocks.GRAVEL, Blocks.MYCELIUM, Blocks.SAND, Blocks.SNOW, Blocks.SNOW_LAYER, Blocks.SOULSAND
		}
	);

	public ShovelItem(Item.ToolMaterialType toolMaterialType) {
		super(1.0F, toolMaterialType, EFFECTIVE_BLOCKS);
	}

	@Override
	public boolean isEffectiveOn(Block block) {
		return block == Blocks.SNOW_LAYER ? true : block == Blocks.SNOW;
	}
}
