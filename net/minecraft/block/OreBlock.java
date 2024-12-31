package net.minecraft.block;

import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Itemable;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class OreBlock extends Block {
	public OreBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	public Itemable getDroppedItem(BlockState state, World world, BlockPos pos, int fortuneLevel) {
		if (this == Blocks.COAL_ORE) {
			return Items.COAL;
		} else if (this == Blocks.DIAMOND_ORE) {
			return Items.DIAMOND;
		} else if (this == Blocks.LAPIS_LAZULI_ORE) {
			return Items.LAPIS_LAZULI;
		} else if (this == Blocks.EMERALD_ORE) {
			return Items.EMERALD;
		} else {
			return (Itemable)(this == Blocks.NETHER_QUARTZ_ORE ? Items.QUARTZ : this);
		}
	}

	@Override
	public int getDropCount(BlockState state, Random random) {
		return this == Blocks.LAPIS_LAZULI_ORE ? 4 + random.nextInt(5) : 1;
	}

	@Override
	public int method_397(BlockState blockState, int i, World world, BlockPos blockPos, Random random) {
		if (i > 0 && this != this.getDroppedItem((BlockState)this.getStateManager().getBlockStates().iterator().next(), world, blockPos, i)) {
			int j = random.nextInt(i + 2) - 1;
			if (j < 0) {
				j = 0;
			}

			return this.getDropCount(blockState, random) * (j + 1);
		} else {
			return this.getDropCount(blockState, random);
		}
	}

	@Override
	public void method_410(BlockState blockState, World world, BlockPos blockPos, float f, int i) {
		super.method_410(blockState, world, blockPos, f, i);
		if (this.getDroppedItem(blockState, world, blockPos, i) != this) {
			int j = 0;
			if (this == Blocks.COAL_ORE) {
				j = MathHelper.nextInt(world.random, 0, 2);
			} else if (this == Blocks.DIAMOND_ORE) {
				j = MathHelper.nextInt(world.random, 3, 7);
			} else if (this == Blocks.EMERALD_ORE) {
				j = MathHelper.nextInt(world.random, 3, 7);
			} else if (this == Blocks.LAPIS_LAZULI_ORE) {
				j = MathHelper.nextInt(world.random, 2, 5);
			} else if (this == Blocks.NETHER_QUARTZ_ORE) {
				j = MathHelper.nextInt(world.random, 2, 5);
			}

			this.dropExperience(world, blockPos, j);
		}
	}

	@Override
	public ItemStack getPickBlock(BlockView world, BlockPos pos, BlockState state) {
		return new ItemStack(this);
	}
}
