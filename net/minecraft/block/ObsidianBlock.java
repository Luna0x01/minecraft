package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.Item;
import net.minecraft.item.itemgroup.ItemGroup;

public class ObsidianBlock extends Block {
	public ObsidianBlock() {
		super(Material.STONE);
		this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
	}

	@Nullable
	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Item.fromBlock(Blocks.OBSIDIAN);
	}

	@Override
	public MaterialColor getMaterialColor(BlockState state) {
		return MaterialColor.BLACK;
	}
}
