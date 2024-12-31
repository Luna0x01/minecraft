package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.itemgroup.ItemGroup;

public class BookshelfBlock extends Block {
	public BookshelfBlock() {
		super(Material.WOOD);
		this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
	}

	@Override
	public int getDropCount(Random rand) {
		return 3;
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Items.BOOK;
	}
}
