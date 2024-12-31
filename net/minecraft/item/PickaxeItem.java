package net.minecraft.item;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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
			Blocks.POWERED_RAIL,
			Blocks.GOLD_BLOCK,
			Blocks.GOLD_ORE,
			Blocks.ICE,
			Blocks.IRON_BLOCK,
			Blocks.IRON_ORE,
			Blocks.LAPIS_LAZULI_BLOCK,
			Blocks.LAPIS_LAZULI_ORE,
			Blocks.MOSSY_COBBLESTONE,
			Blocks.NETHERRACK,
			Blocks.PACKED_ICE,
			Blocks.BLUE_ICE,
			Blocks.RAIL,
			Blocks.REDSTONE_ORE,
			Blocks.SANDSTONE,
			Blocks.CHISELED_SANDSTONE,
			Blocks.CUT_SANDSTONE,
			Blocks.CHISELED_RED_SANDSTONE,
			Blocks.CUT_RED_SANDSTONE,
			Blocks.RED_SANDSTONE,
			Blocks.STONE,
			Blocks.GRANITE,
			Blocks.POLISHED_GRANITE,
			Blocks.DIORITE,
			Blocks.POLISHED_DIORITE,
			Blocks.ANDESITE,
			Blocks.POLISHED_ANDESITE,
			Blocks.STONE_SLAB,
			Blocks.SANDSTONE_SLAB,
			Blocks.PETRIFIED_OAK_SLAB,
			Blocks.COBBLESTONE_SLAB,
			Blocks.BRICK_SLAB,
			Blocks.STONE_BRICK_SLAB,
			Blocks.NETHER_BRICK_SLAB,
			Blocks.QUARTZ_SLAB,
			Blocks.RED_SANDSTONE_SLAB,
			Blocks.PURPUR_SLAB,
			Blocks.SMOOTH_QUARTZ,
			Blocks.SMOOTH_RED_SANDSTONE,
			Blocks.SMOOTH_SANDSTONE,
			Blocks.SMOOTH_STONE,
			Blocks.STONE_BUTTON,
			Blocks.STONE_PRESSURE_PLATE
		}
	);

	protected PickaxeItem(IToolMaterial iToolMaterial, int i, float f, Item.Settings settings) {
		super((float)i, f, iToolMaterial, EFFECTIVE_BLOCKS, settings);
	}

	@Override
	public boolean method_3346(BlockState blockState) {
		Block block = blockState.getBlock();
		int i = this.method_16137().getMiningLevel();
		if (block == Blocks.OBSIDIAN) {
			return i == 3;
		} else if (block == Blocks.DIAMOND_BLOCK
			|| block == Blocks.DIAMOND_ORE
			|| block == Blocks.EMERALD_ORE
			|| block == Blocks.EMERALD_BLOCK
			|| block == Blocks.GOLD_BLOCK
			|| block == Blocks.GOLD_ORE
			|| block == Blocks.REDSTONE_ORE) {
			return i >= 2;
		} else if (block != Blocks.IRON_BLOCK && block != Blocks.IRON_ORE && block != Blocks.LAPIS_LAZULI_BLOCK && block != Blocks.LAPIS_LAZULI_ORE) {
			Material material = blockState.getMaterial();
			return material == Material.STONE || material == Material.IRON || material == Material.ANVIL;
		} else {
			return i >= 1;
		}
	}

	@Override
	public float getBlockBreakingSpeed(ItemStack stack, BlockState state) {
		Material material = state.getMaterial();
		return material != Material.IRON && material != Material.ANVIL && material != Material.STONE ? super.getBlockBreakingSpeed(stack, state) : this.miningSpeed;
	}
}
