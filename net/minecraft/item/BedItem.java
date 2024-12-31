package net.minecraft.item;

import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class BedItem extends Item {
	public BedItem() {
		this.setItemGroup(ItemGroup.DECORATIONS);
	}

	@Override
	public ActionResult method_3355(
		ItemStack itemStack, PlayerEntity playerEntity, World world, BlockPos blockPos, Hand hand, Direction direction, float f, float g, float h
	) {
		if (world.isClient) {
			return ActionResult.SUCCESS;
		} else if (direction != Direction.UP) {
			return ActionResult.FAIL;
		} else {
			BlockState blockState = world.getBlockState(blockPos);
			Block block = blockState.getBlock();
			boolean bl = block.method_8638(world, blockPos);
			if (!bl) {
				blockPos = blockPos.up();
			}

			int i = MathHelper.floor((double)(playerEntity.yaw * 4.0F / 360.0F) + 0.5) & 3;
			Direction direction2 = Direction.fromHorizontal(i);
			BlockPos blockPos2 = blockPos.offset(direction2);
			if (playerEntity.canModify(blockPos, direction, itemStack) && playerEntity.canModify(blockPos2, direction, itemStack)) {
				boolean bl2 = world.getBlockState(blockPos2).getBlock().method_8638(world, blockPos2);
				boolean bl3 = bl || world.isAir(blockPos);
				boolean bl4 = bl2 || world.isAir(blockPos2);
				if (bl3 && bl4 && world.getBlockState(blockPos.down()).method_11739() && world.getBlockState(blockPos2.down()).method_11739()) {
					BlockState blockState2 = Blocks.BED
						.getDefaultState()
						.with(BedBlock.OCCUPIED, false)
						.with(BedBlock.DIRECTION, direction2)
						.with(BedBlock.BED_TYPE, BedBlock.BedBlockType.FOOT);
					if (world.setBlockState(blockPos, blockState2, 11)) {
						BlockState blockState3 = blockState2.with(BedBlock.BED_TYPE, BedBlock.BedBlockType.HEAD);
						world.setBlockState(blockPos2, blockState3, 11);
					}

					BlockSoundGroup blockSoundGroup = blockState2.getBlock().getSoundGroup();
					world.method_11486(
						null, blockPos, blockSoundGroup.method_4194(), SoundCategory.BLOCKS, (blockSoundGroup.getVolume() + 1.0F) / 2.0F, blockSoundGroup.getPitch() * 0.8F
					);
					itemStack.count--;
					return ActionResult.SUCCESS;
				} else {
					return ActionResult.FAIL;
				}
			} else {
				return ActionResult.FAIL;
			}
		}
	}
}
