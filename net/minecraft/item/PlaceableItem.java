package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowLayerBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PlaceableItem extends Item {
	private Block block;

	public PlaceableItem(Block block) {
		this.block = block;
	}

	@Override
	public boolean use(ItemStack itemStack, PlayerEntity player, World world, BlockPos pos, Direction direction, float facingX, float facingY, float facingZ) {
		BlockState blockState = world.getBlockState(pos);
		Block block = blockState.getBlock();
		if (block == Blocks.SNOW_LAYER && (Integer)blockState.get(SnowLayerBlock.LAYERS) < 1) {
			direction = Direction.UP;
		} else if (!block.isReplaceable(world, pos)) {
			pos = pos.offset(direction);
		}

		if (!player.canModify(pos, direction, itemStack)) {
			return false;
		} else if (itemStack.count == 0) {
			return false;
		} else {
			if (world.canBlockBePlaced(this.block, pos, false, direction, null, itemStack)) {
				BlockState blockState2 = this.block.getStateFromData(world, pos, direction, facingX, facingY, facingZ, 0, player);
				if (world.setBlockState(pos, blockState2, 3)) {
					blockState2 = world.getBlockState(pos);
					if (blockState2.getBlock() == this.block) {
						BlockItem.setBlockEntityNbt(world, player, pos, itemStack);
						blockState2.getBlock().onPlaced(world, pos, blockState2, player, itemStack);
					}

					world.playSound(
						(double)((float)pos.getX() + 0.5F),
						(double)((float)pos.getY() + 0.5F),
						(double)((float)pos.getZ() + 0.5F),
						this.block.sound.getSound(),
						(this.block.sound.getVolume() + 1.0F) / 2.0F,
						this.block.sound.getPitch() * 0.8F
					);
					itemStack.count--;
					return true;
				}
			}

			return false;
		}
	}
}
