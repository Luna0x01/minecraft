package net.minecraft.block;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class PotatoesBlock extends CropBlock {
	private static final Box[] field_12720 = new Box[]{
		new Box(0.0, 0.0, 0.0, 1.0, 0.125, 1.0),
		new Box(0.0, 0.0, 0.0, 1.0, 0.1875, 1.0),
		new Box(0.0, 0.0, 0.0, 1.0, 0.25, 1.0),
		new Box(0.0, 0.0, 0.0, 1.0, 0.3125, 1.0),
		new Box(0.0, 0.0, 0.0, 1.0, 0.375, 1.0),
		new Box(0.0, 0.0, 0.0, 1.0, 0.4375, 1.0),
		new Box(0.0, 0.0, 0.0, 1.0, 0.5, 1.0),
		new Box(0.0, 0.0, 0.0, 1.0, 0.5625, 1.0)
	};

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
			if (this.isMature(state) && world.random.nextInt(50) == 0) {
				onBlockBreak(world, pos, new ItemStack(Items.POISONOUS_POTATO));
			}
		}
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		return field_12720[state.get(this.getAge())];
	}
}
