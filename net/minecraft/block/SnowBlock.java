package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class SnowBlock extends Block {
	protected SnowBlock() {
		super(Material.SNOW);
		this.setTickRandomly(true);
		this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Items.SNOWBALL;
	}

	@Override
	public int getDropCount(Random rand) {
		return 4;
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		if (world.getLightAtPos(LightType.BLOCK, pos) > 11) {
			this.dropAsItem(world, pos, world.getBlockState(pos), 0);
			world.setAir(pos);
		}
	}
}
