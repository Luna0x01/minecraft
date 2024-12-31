package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.itemgroup.ItemGroup;

public class MelonBlock extends Block {
	protected MelonBlock() {
		super(Material.PUMPKIN, MaterialColor.LIME);
		this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
	}

	@Nullable
	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Items.MELON;
	}

	@Override
	public int getDropCount(Random rand) {
		return 3 + rand.nextInt(5);
	}

	@Override
	public int getBonusDrops(int id, Random rand) {
		return Math.min(9, this.getDropCount(rand) + rand.nextInt(1 + id));
	}
}
