package net.minecraft.item;

import net.minecraft.block.AbstractFluidBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.Sounds;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LilyPadItem extends GrassBlockItem {
	public LilyPadItem(Block block) {
		super(block, false);
	}

	@Override
	public TypedActionResult<ItemStack> method_11373(ItemStack itemStack, World world, PlayerEntity playerEntity, Hand hand) {
		BlockHitResult blockHitResult = this.onHit(world, playerEntity, true);
		if (blockHitResult == null) {
			return new TypedActionResult<>(ActionResult.PASS, itemStack);
		} else {
			if (blockHitResult.type == BlockHitResult.Type.BLOCK) {
				BlockPos blockPos = blockHitResult.getBlockPos();
				if (!world.canPlayerModifyAt(playerEntity, blockPos)
					|| !playerEntity.canModify(blockPos.offset(blockHitResult.direction), blockHitResult.direction, itemStack)) {
					return new TypedActionResult<>(ActionResult.FAIL, itemStack);
				}

				BlockPos blockPos2 = blockPos.up();
				BlockState blockState = world.getBlockState(blockPos);
				if (blockState.getMaterial() == Material.WATER && (Integer)blockState.get(AbstractFluidBlock.LEVEL) == 0 && world.isAir(blockPos2)) {
					world.setBlockState(blockPos2, Blocks.LILY_PAD.getDefaultState(), 11);
					if (!playerEntity.abilities.creativeMode) {
						itemStack.count--;
					}

					playerEntity.incrementStat(Stats.used(this));
					world.method_11486(playerEntity, blockPos, Sounds.BLOCK_WATERLILY_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
					return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
				}
			}

			return new TypedActionResult<>(ActionResult.FAIL, itemStack);
		}
	}
}
