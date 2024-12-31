package net.minecraft.block;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.BlockView;

public class CarrotsBlock extends CropBlock {
	private static final Box[] field_12610 = new Box[]{
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
		return Items.CARROT;
	}

	@Override
	protected Item getHarvestItem() {
		return Items.CARROT;
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		return field_12610[state.get(this.getAge())];
	}
}
