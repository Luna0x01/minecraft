package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowLayerBlock;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PlaceableItem extends Item {
	private Block block;

	public PlaceableItem(Block block) {
		this.block = block;
	}

	@Override
	public ActionResult method_3355(
		ItemStack itemStack, PlayerEntity playerEntity, World world, BlockPos blockPos, Hand hand, Direction direction, float f, float g, float h
	) {
		BlockState blockState = world.getBlockState(blockPos);
		Block block = blockState.getBlock();
		if (block == Blocks.SNOW_LAYER && (Integer)blockState.get(SnowLayerBlock.LAYERS) < 1) {
			direction = Direction.UP;
		} else if (!block.method_8638(world, blockPos)) {
			blockPos = blockPos.offset(direction);
		}

		if (playerEntity.canModify(blockPos, direction, itemStack)
			&& itemStack.count != 0
			&& world.canBlockBePlaced(this.block, blockPos, false, direction, null, itemStack)) {
			BlockState blockState2 = this.block.getStateFromData(world, blockPos, direction, f, g, h, 0, playerEntity);
			if (!world.setBlockState(blockPos, blockState2, 11)) {
				return ActionResult.FAIL;
			} else {
				blockState2 = world.getBlockState(blockPos);
				if (blockState2.getBlock() == this.block) {
					BlockItem.setBlockEntityNbt(world, playerEntity, blockPos, itemStack);
					blockState2.getBlock().onPlaced(world, blockPos, blockState2, playerEntity, itemStack);
				}

				BlockSoundGroup blockSoundGroup = this.block.getSoundGroup();
				world.method_11486(
					playerEntity,
					blockPos,
					blockSoundGroup.method_4194(),
					SoundCategory.BLOCKS,
					(blockSoundGroup.getVolume() + 1.0F) / 2.0F,
					blockSoundGroup.getPitch() * 0.8F
				);
				itemStack.count--;
				return ActionResult.SUCCESS;
			}
		} else {
			return ActionResult.FAIL;
		}
	}
}
