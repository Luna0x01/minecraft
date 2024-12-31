package net.minecraft.item;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;

public class PickaxeItem extends ToolItem {
	private static final Set<Block> EFFECTIVE_BLOCKS = Sets.newHashSet(
		new Block[]{
			Blocks.ACTIVATOR_RAIL,
			Blocks.COAL_ORE,
			Blocks.COBBLESTONE,
			Blocks.DETECTOR_RAIL,
			Blocks.DIAMOND_BLOCK,
			Blocks.DIAMOND_ORE,
			Blocks.DOUBLE_STONE_SLAB,
			Blocks.POWERED_RAIL,
			Blocks.GOLD_BLOCK,
			Blocks.GOLD_ORE,
			Blocks.ICE,
			Blocks.IRON_BLOCK,
			Blocks.IRON_ORE,
			Blocks.LAPIS_LAZULI_BLOCK,
			Blocks.LAPIS_LAZULI_ORE,
			Blocks.LIT_REDSTONE_ORE,
			Blocks.MOSSY_COBBLESTONE,
			Blocks.NETHERRACK,
			Blocks.PACKED_ICE,
			Blocks.RAIL,
			Blocks.REDSTONE_ORE,
			Blocks.SANDSTONE,
			Blocks.RED_SANDSTONE,
			Blocks.STONE,
			Blocks.STONE_SLAB
		}
	);

	protected PickaxeItem(Item.ToolMaterialType toolMaterialType) {
		super(2.0F, toolMaterialType, EFFECTIVE_BLOCKS);
	}

	@Override
	public boolean isEffectiveOn(Block block) {
		if (block == Blocks.OBSIDIAN) {
			return this.material.getMiningLevel() == 3;
		} else if (block == Blocks.DIAMOND_BLOCK || block == Blocks.DIAMOND_ORE) {
			return this.material.getMiningLevel() >= 2;
		} else if (block == Blocks.EMERALD_ORE || block == Blocks.EMERALD_BLOCK) {
			return this.material.getMiningLevel() >= 2;
		} else if (block == Blocks.GOLD_BLOCK || block == Blocks.GOLD_ORE) {
			return this.material.getMiningLevel() >= 2;
		} else if (block == Blocks.IRON_BLOCK || block == Blocks.IRON_ORE) {
			return this.material.getMiningLevel() >= 1;
		} else if (block == Blocks.LAPIS_LAZULI_BLOCK || block == Blocks.LAPIS_LAZULI_ORE) {
			return this.material.getMiningLevel() >= 1;
		} else if (block != Blocks.REDSTONE_ORE && block != Blocks.LIT_REDSTONE_ORE) {
			if (block.getMaterial() == Material.STONE) {
				return true;
			} else {
				return block.getMaterial() == Material.IRON ? true : block.getMaterial() == Material.ANVIL;
			}
		} else {
			return this.material.getMiningLevel() >= 2;
		}
	}

	@Override
	public float getMiningSpeedMultiplier(ItemStack stack, Block block) {
		return block.getMaterial() != Material.IRON && block.getMaterial() != Material.ANVIL && block.getMaterial() != Material.STONE
			? super.getMiningSpeedMultiplier(stack, block)
			: this.miningSpeed;
	}
}
