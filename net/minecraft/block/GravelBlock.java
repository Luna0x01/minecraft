package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class GravelBlock extends FallingBlock {
	@Nullable
	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		if (id > 3) {
			id = 3;
		}

		return random.nextInt(10 - id * 3) == 0 ? Items.FLINT : Item.fromBlock(this);
	}

	@Override
	public MaterialColor getMaterialColor(BlockState state) {
		return MaterialColor.STONE;
	}

	@Override
	public int getColor(BlockState state) {
		return -8356741;
	}
}
