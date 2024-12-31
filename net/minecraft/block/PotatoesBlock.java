package net.minecraft.block;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PotatoesBlock extends CropBlock {
	@Override
	protected Item getSeedItem() {
		return Items.POTATO;
	}

	@Override
	protected Item getHarvestItem() {
		return Items.POTATO;
	}

	@Override
	public void randomDropAsItem(World world, BlockPos pos, BlockState state, float chance, int id) {
		super.randomDropAsItem(world, pos, state, chance, id);
		if (!world.isClient) {
			if ((Integer)state.get(AGE) >= 7 && world.random.nextInt(50) == 0) {
				onBlockBreak(world, pos, new ItemStack(Items.POISONOUS_POTATO));
			}
		}
	}
}
