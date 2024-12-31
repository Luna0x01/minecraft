package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DeadBushBlock extends PlantBlock {
	protected DeadBushBlock() {
		super(Material.REPLACEABLE_PLANT);
		float f = 0.4F;
		this.setBoundingBox(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.8F, 0.5F + f);
	}

	@Override
	public MaterialColor getMaterialColor(BlockState state) {
		return MaterialColor.WOOD;
	}

	@Override
	protected boolean canPlantOnTop(Block block) {
		return block == Blocks.SAND || block == Blocks.TERRACOTTA || block == Blocks.STAINED_TERRACOTTA || block == Blocks.DIRT;
	}

	@Override
	public boolean isReplaceable(World world, BlockPos pos) {
		return true;
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return null;
	}

	@Override
	public void harvest(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity be) {
		if (!world.isClient && player.getMainHandStack() != null && player.getMainHandStack().getItem() == Items.SHEARS) {
			player.incrementStat(Stats.BLOCK_STATS[Block.getIdByBlock(this)]);
			onBlockBreak(world, pos, new ItemStack(Blocks.DEADBUSH, 1, 0));
		} else {
			super.harvest(world, player, pos, state, be);
		}
	}
}
