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
		this.setUnbreakable(true);
	}

	@Override
	public ActionResult method_3355(
		ItemStack itemStack, PlayerEntity playerEntity, World world, BlockPos blockPos, Hand hand, Direction direction, float f, float g, float h
	) {
		if (itemStack.count != 0 && playerEntity.canModify(blockPos, direction, itemStack)) {
			BlockState blockState = world.getBlockState(blockPos);
			Block block = blockState.getBlock();
			BlockPos blockPos2 = blockPos;
			if ((direction != Direction.UP || block != this.block) && !block.method_8638(world, blockPos)) {
				blockPos2 = blockPos.offset(direction);
				blockState = world.getBlockState(blockPos2);
				block = blockState.getBlock();
			}

			if (block == this.block) {
				int i = (Integer)blockState.get(SnowLayerBlock.LAYERS);
				if (i <= 7) {
					BlockState blockState2 = blockState.with(SnowLayerBlock.LAYERS, i + 1);
					Box box = blockState2.getCollisionBox(world, blockPos2);
					if (box != Block.EMPTY_BOX && world.hasEntityIn(box.offset(blockPos2)) && world.setBlockState(blockPos2, blockState2, 10)) {
						BlockSoundGroup blockSoundGroup = this.block.getSoundGroup();
						world.method_11486(
							playerEntity,
							blockPos2,
							blockSoundGroup.method_4194(),
							SoundCategory.BLOCKS,
							(blockSoundGroup.getVolume() + 1.0F) / 2.0F,
							blockSoundGroup.getPitch() * 0.8F
						);
						itemStack.count--;
						return ActionResult.SUCCESS;
					}
				}
			}

			return super.method_3355(itemStack, playerEntity, world, blockPos2, hand, direction, f, g, h);
		} else {
			return ActionResult.FAIL;
		}
	}

	@Override
	public int getMeta(int i) {
		return i;
	}
}
