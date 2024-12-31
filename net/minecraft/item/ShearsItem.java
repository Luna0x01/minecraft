package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ShearsItem extends Item {
	public ShearsItem() {
		this.setMaxCount(1);
		this.setMaxDamage(238);
		this.setItemGroup(ItemGroup.TOOLS);
	}

	@Override
	public boolean onBlockBroken(ItemStack stack, World world, Block block, BlockPos pos, LivingEntity entity) {
		if (block.getMaterial() != Material.FOLIAGE
			&& block != Blocks.COBWEB
			&& block != Blocks.TALLGRASS
			&& block != Blocks.VINE
			&& block != Blocks.TRIPWIRE
			&& block != Blocks.WOOL) {
			return super.onBlockBroken(stack, world, block, pos, entity);
		} else {
			stack.damage(1, entity);
			return true;
		}
	}

	@Override
	public boolean isEffectiveOn(Block block) {
		return block == Blocks.COBWEB || block == Blocks.REDSTONE_WIRE || block == Blocks.TRIPWIRE;
	}

	@Override
	public float getMiningSpeedMultiplier(ItemStack stack, Block block) {
		if (block == Blocks.COBWEB || block.getMaterial() == Material.FOLIAGE) {
			return 15.0F;
		} else {
			return block == Blocks.WOOL ? 5.0F : super.getMiningSpeedMultiplier(stack, block);
		}
	}
}
