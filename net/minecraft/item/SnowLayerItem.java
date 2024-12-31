package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SnowLayerBlock;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class SnowLayerItem extends BlockItem {
	public SnowLayerItem(Block block) {
		super(block);
		this.setMaxDamage(0);
	}

	@Override
	public ActionResult use(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction direction, float x, float y, float z) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (!itemStack.isEmpty() && player.canModify(pos, direction, itemStack)) {
			BlockState blockState = world.getBlockState(pos);
			Block block = blockState.getBlock();
			BlockPos blockPos = pos;
			if ((direction != Direction.UP || block != this.block) && !block.method_8638(world, pos)) {
				blockPos = pos.offset(direction);
				blockState = world.getBlockState(blockPos);
				block = blockState.getBlock();
			}

			if (block == this.block) {
				int i = (Integer)blockState.get(SnowLayerBlock.LAYERS);
				if (i < 8) {
					BlockState blockState2 = blockState.with(SnowLayerBlock.LAYERS, i + 1);
					Box box = blockState2.method_11726(world, blockPos);
					if (box != Block.EMPTY_BOX && world.hasEntityIn(box.offset(blockPos)) && world.setBlockState(blockPos, blockState2, 10)) {
						BlockSoundGroup blockSoundGroup = this.block.getSoundGroup();
						world.method_11486(
							player, blockPos, blockSoundGroup.method_4194(), SoundCategory.BLOCKS, (blockSoundGroup.getVolume() + 1.0F) / 2.0F, blockSoundGroup.getPitch() * 0.8F
						);
						itemStack.decrement(1);
						return ActionResult.SUCCESS;
					}
				}
			}

			return super.use(player, world, pos, hand, direction, x, y, z);
		} else {
			return ActionResult.FAIL;
		}
	}

	@Override
	public int getMeta(int i) {
		return i;
	}
}
