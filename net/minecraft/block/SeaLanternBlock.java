package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;

public class SeaLanternBlock extends Block {
	public SeaLanternBlock(Material material) {
		super(material);
		this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
	}

	@Override
	public int getDropCount(Random rand) {
		return 2 + rand.nextInt(2);
	}

	@Override
	public int getBonusDrops(int id, Random rand) {
		return MathHelper.clamp(this.getDropCount(rand) + rand.nextInt(id + 1), 1, 5);
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Items.PRISMARINE_CRYSTALS;
	}

	@Override
	public MaterialColor getMaterialColor(BlockState state, BlockView view, BlockPos pos) {
		return MaterialColor.QUARTZ;
	}

	@Override
	protected boolean requiresSilkTouch() {
		return true;
	}
}
