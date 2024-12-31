package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.Item;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class ObsidianBlock extends Block {
	public ObsidianBlock() {
		super(Material.STONE);
		this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Item.fromBlock(Blocks.OBSIDIAN);
	}

	@Override
	public MaterialColor getMaterialColor(BlockState state, BlockView view, BlockPos pos) {
		return MaterialColor.BLACK;
	}
}
