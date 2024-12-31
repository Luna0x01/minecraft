package net.minecraft.block;

import java.util.Random;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BeetrootsBlock extends CropBlock {
	public static final IntProperty field_12569 = IntProperty.of("age", 0, 3);
	private static final Box[] field_12570 = new Box[]{
		new Box(0.0, 0.0, 0.0, 1.0, 0.125, 1.0),
		new Box(0.0, 0.0, 0.0, 1.0, 0.25, 1.0),
		new Box(0.0, 0.0, 0.0, 1.0, 0.375, 1.0),
		new Box(0.0, 0.0, 0.0, 1.0, 0.5, 1.0)
	};

	@Override
	protected IntProperty getAge() {
		return field_12569;
	}

	@Override
	public int getMaxAge() {
		return 3;
	}

	@Override
	protected Item getSeedItem() {
		return Items.BEETROOT_SEED;
	}

	@Override
	protected Item getHarvestItem() {
		return Items.BEETROOT;
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		if (rand.nextInt(3) == 0) {
			this.plantAt(world, pos, state);
		} else {
			super.onScheduledTick(world, pos, state, rand);
		}
	}

	@Override
	protected int getGrowthAmount(World world) {
		return super.getGrowthAmount(world) / 3;
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, field_12569);
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		return field_12570[state.get(this.getAge())];
	}
}
