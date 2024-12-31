package net.minecraft.item;

import net.minecraft.block.AbstractFluidBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LilyPadItem extends GrassBlockItem {
	public LilyPadItem(Block block) {
		super(block, false);
	}

	@Override
	public ItemStack onStartUse(ItemStack stack, World world, PlayerEntity player) {
		BlockHitResult blockHitResult = this.onHit(world, player, true);
		if (blockHitResult == null) {
			return stack;
		} else {
			if (blockHitResult.type == BlockHitResult.Type.BLOCK) {
				BlockPos blockPos = blockHitResult.getBlockPos();
				if (!world.canPlayerModifyAt(player, blockPos)) {
					return stack;
				}

				if (!player.canModify(blockPos.offset(blockHitResult.direction), blockHitResult.direction, stack)) {
					return stack;
				}

				BlockPos blockPos2 = blockPos.up();
				BlockState blockState = world.getBlockState(blockPos);
				if (blockState.getBlock().getMaterial() == Material.WATER && (Integer)blockState.get(AbstractFluidBlock.LEVEL) == 0 && world.isAir(blockPos2)) {
					world.setBlockState(blockPos2, Blocks.LILY_PAD.getDefaultState());
					if (!player.abilities.creativeMode) {
						stack.count--;
					}

					player.incrementStat(Stats.USED[Item.getRawId(this)]);
				}
			}

			return stack;
		}
	}

	@Override
	public int getDisplayColor(ItemStack stack, int color) {
		return Blocks.LILY_PAD.getColor(Blocks.LILY_PAD.stateFromData(stack.getData()));
	}
}
