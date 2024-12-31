package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;

public class BedrockBlock extends Block {
	public BedrockBlock(Material material) {
		super(material);
	}

	@Override
	public int getDropCount(Random rand) {
		return 0;
	}

	@Nullable
	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return null;
	}
}
