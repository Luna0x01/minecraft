package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.util.math.MathHelper;

public class GlowstoneBlock extends Block {
	public GlowstoneBlock(Material material) {
		super(material);
		this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
	}

	@Override
	public int getBonusDrops(int id, Random rand) {
		return MathHelper.clamp(this.getDropCount(rand) + rand.nextInt(id + 1), 1, 4);
	}

	@Override
	public int getDropCount(Random rand) {
		return 2 + rand.nextInt(3);
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Items.GLOWSTONE_DUST;
	}

	@Override
	public MaterialColor getMaterialColor(BlockState state) {
		return MaterialColor.SAND;
	}
}
