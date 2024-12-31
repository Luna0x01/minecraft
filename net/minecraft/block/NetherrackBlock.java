package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.itemgroup.ItemGroup;

public class NetherrackBlock extends Block {
	public NetherrackBlock() {
		super(Material.STONE);
		this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
	}

	@Override
	public MaterialColor getMaterialColor(BlockState state) {
		return MaterialColor.NETHER;
	}
}
