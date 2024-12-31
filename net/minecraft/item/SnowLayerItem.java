package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SnowLayerBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class SnowLayerItem extends BlockItem {
	public SnowLayerItem(Block block) {
		super(block);
		this.setMaxDamage(0);
		this.setUnbreakable(true);
	}

	@Override
	public boolean use(ItemStack itemStack, PlayerEntity player, World world, BlockPos pos, Direction direction, float facingX, float facingY, float facingZ) {
		if (itemStack.count == 0) {
			return false;
		} else if (!player.canModify(pos, direction, itemStack)) {
			return false;
		} else {
			BlockState blockState = world.getBlockState(pos);
			Block block = blockState.getBlock();
			BlockPos blockPos = pos;
			if ((direction != Direction.UP || block != this.block) && !block.isReplaceable(world, pos)) {
				blockPos = pos.offset(direction);
				blockState = world.getBlockState(blockPos);
				block = blockState.getBlock();
			}

			if (block == this.block) {
				int i = (Integer)blockState.get(SnowLayerBlock.LAYERS);
				if (i <= 7) {
					BlockState blockState2 = blockState.with(SnowLayerBlock.LAYERS, i + 1);
					Box box = this.block.getCollisionBox(world, blockPos, blockState2);
					if (box != null && world.hasEntityIn(box) && world.setBlockState(blockPos, blockState2, 2)) {
						world.playSound(
							(double)((float)blockPos.getX() + 0.5F),
							(double)((float)blockPos.getY() + 0.5F),
							(double)((float)blockPos.getZ() + 0.5F),
							this.block.sound.getSound(),
							(this.block.sound.getVolume() + 1.0F) / 2.0F,
							this.block.sound.getPitch() * 0.8F
						);
						itemStack.count--;
						return true;
					}
				}
			}

			return super.use(itemStack, player, world, blockPos, direction, facingX, facingY, facingZ);
		}
	}

	@Override
	public int getMeta(int i) {
		return i;
	}
}
