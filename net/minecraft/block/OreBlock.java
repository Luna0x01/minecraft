package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class OreBlock extends Block {
	public OreBlock() {
		this(Material.STONE.getColor());
	}

	public OreBlock(MaterialColor materialColor) {
		super(Material.STONE, materialColor);
		this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		if (this == Blocks.COAL_ORE) {
			return Items.COAL;
		} else if (this == Blocks.DIAMOND_ORE) {
			return Items.DIAMOND;
		} else if (this == Blocks.LAPIS_LAZULI_ORE) {
			return Items.DYE;
		} else if (this == Blocks.EMERALD_ORE) {
			return Items.EMERALD;
		} else {
			return this == Blocks.NETHER_QUARTZ_ORE ? Items.QUARTZ : Item.fromBlock(this);
		}
	}

	@Override
	public int getDropCount(Random rand) {
		return this == Blocks.LAPIS_LAZULI_ORE ? 4 + rand.nextInt(5) : 1;
	}

	@Override
	public int getBonusDrops(int id, Random rand) {
		if (id > 0 && Item.fromBlock(this) != this.getDropItem((BlockState)this.getStateManager().getBlockStates().iterator().next(), rand, id)) {
			int i = rand.nextInt(id + 2) - 1;
			if (i < 0) {
				i = 0;
			}

			return this.getDropCount(rand) * (i + 1);
		} else {
			return this.getDropCount(rand);
		}
	}

	@Override
	public void randomDropAsItem(World world, BlockPos pos, BlockState state, float chance, int id) {
		super.randomDropAsItem(world, pos, state, chance, id);
		if (this.getDropItem(state, world.random, id) != Item.fromBlock(this)) {
			int i = 0;
			if (this == Blocks.COAL_ORE) {
				i = MathHelper.nextInt(world.random, 0, 2);
			} else if (this == Blocks.DIAMOND_ORE) {
				i = MathHelper.nextInt(world.random, 3, 7);
			} else if (this == Blocks.EMERALD_ORE) {
				i = MathHelper.nextInt(world.random, 3, 7);
			} else if (this == Blocks.LAPIS_LAZULI_ORE) {
				i = MathHelper.nextInt(world.random, 2, 5);
			} else if (this == Blocks.NETHER_QUARTZ_ORE) {
				i = MathHelper.nextInt(world.random, 2, 5);
			}

			this.dropExperience(world, pos, i);
		}
	}

	@Override
	public ItemStack getItemStack(World world, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(this);
	}

	@Override
	public int getMeta(BlockState state) {
		return this == Blocks.LAPIS_LAZULI_ORE ? DyeColor.BLUE.getSwappedId() : 0;
	}
}
