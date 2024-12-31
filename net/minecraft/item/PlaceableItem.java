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
	private final Block block;

	public PlaceableItem(Block block) {
		this.block = block;
	}

	@Override
	public ActionResult use(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction direction, float x, float y, float z) {
		BlockState blockState = world.getBlockState(pos);
		Block block = blockState.getBlock();
		if (block == Blocks.SNOW_LAYER && (Integer)blockState.get(SnowLayerBlock.LAYERS) < 1) {
			direction = Direction.UP;
		} else if (!block.method_8638(world, pos)) {
			pos = pos.offset(direction);
		}

		ItemStack itemStack = player.getStackInHand(hand);
		if (!itemStack.isEmpty() && player.canModify(pos, direction, itemStack) && world.method_8493(this.block, pos, false, direction, null)) {
			BlockState blockState2 = this.block.getStateFromData(world, pos, direction, x, y, z, 0, player);
			if (!world.setBlockState(pos, blockState2, 11)) {
				return ActionResult.FAIL;
			} else {
				blockState2 = world.getBlockState(pos);
				if (blockState2.getBlock() == this.block) {
					BlockItem.setBlockEntityNbt(world, player, pos, itemStack);
					blockState2.getBlock().onPlaced(world, pos, blockState2, player, itemStack);
				}

				BlockSoundGroup blockSoundGroup = this.block.getSoundGroup();
				world.method_11486(
					player, pos, blockSoundGroup.method_4194(), SoundCategory.BLOCKS, (blockSoundGroup.getVolume() + 1.0F) / 2.0F, blockSoundGroup.getPitch() * 0.8F
				);
				itemStack.decrement(1);
				return ActionResult.SUCCESS;
			}
		} else {
			return ActionResult.FAIL;
		}
	}
}
