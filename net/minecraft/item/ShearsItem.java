package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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
	public boolean method_3356(ItemStack itemStack, World world, BlockState blockState, BlockPos blockPos, LivingEntity livingEntity) {
		if (!world.isClient) {
			itemStack.damage(1, livingEntity);
		}

		Block block = blockState.getBlock();
		return blockState.getMaterial() != Material.FOLIAGE
				&& block != Blocks.COBWEB
				&& block != Blocks.TALLGRASS
				&& block != Blocks.VINE
				&& block != Blocks.TRIPWIRE
				&& block != Blocks.WOOL
			? super.method_3356(itemStack, world, blockState, blockPos, livingEntity)
			: true;
	}

	@Override
	public boolean method_3346(BlockState blockState) {
		Block block = blockState.getBlock();
		return block == Blocks.COBWEB || block == Blocks.REDSTONE_WIRE || block == Blocks.TRIPWIRE;
	}

	@Override
	public float getBlockBreakingSpeed(ItemStack stack, BlockState state) {
		Block block = state.getBlock();
		if (block == Blocks.COBWEB || state.getMaterial() == Material.FOLIAGE) {
			return 15.0F;
		} else {
			return block == Blocks.WOOL ? 5.0F : super.getBlockBreakingSpeed(stack, state);
		}
	}
}
