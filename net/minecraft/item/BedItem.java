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
	public ActionResult use(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction direction, float x, float y, float z) {
		if (world.isClient) {
			return ActionResult.SUCCESS;
		} else if (direction != Direction.UP) {
			return ActionResult.FAIL;
		} else {
			BlockState blockState = world.getBlockState(pos);
			Block block = blockState.getBlock();
			boolean bl = block.method_8638(world, pos);
			if (!bl) {
				pos = pos.up();
			}

			int i = MathHelper.floor((double)(player.yaw * 4.0F / 360.0F) + 0.5) & 3;
			Direction direction2 = Direction.fromHorizontal(i);
			BlockPos blockPos = pos.offset(direction2);
			ItemStack itemStack = player.getStackInHand(hand);
			if (player.canModify(pos, direction, itemStack) && player.canModify(blockPos, direction, itemStack)) {
				BlockState blockState2 = world.getBlockState(blockPos);
				boolean bl2 = blockState2.getBlock().method_8638(world, blockPos);
				boolean bl3 = bl || world.isAir(pos);
				boolean bl4 = bl2 || world.isAir(blockPos);
				if (bl3 && bl4 && world.getBlockState(pos.down()).method_11739() && world.getBlockState(blockPos.down()).method_11739()) {
					BlockState blockState3 = Blocks.BED
						.getDefaultState()
						.with(BedBlock.OCCUPIED, false)
						.with(BedBlock.DIRECTION, direction2)
						.with(BedBlock.BED_TYPE, BedBlock.BedBlockType.FOOT);
					world.setBlockState(pos, blockState3, 10);
					world.setBlockState(blockPos, blockState3.with(BedBlock.BED_TYPE, BedBlock.BedBlockType.HEAD), 10);
					world.method_8531(pos, block, false);
					world.method_8531(blockPos, blockState2.getBlock(), false);
					BlockSoundGroup blockSoundGroup = blockState3.getBlock().getSoundGroup();
					world.method_11486(
						null, pos, blockSoundGroup.method_4194(), SoundCategory.BLOCKS, (blockSoundGroup.getVolume() + 1.0F) / 2.0F, blockSoundGroup.getPitch() * 0.8F
					);
					itemStack.decrement(1);
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
