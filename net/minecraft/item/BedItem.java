package net.minecraft.item;

import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class BedItem extends Item {
	public BedItem() {
		this.setItemGroup(ItemGroup.DECORATIONS);
	}

	@Override
	public boolean use(ItemStack itemStack, PlayerEntity player, World world, BlockPos pos, Direction direction, float facingX, float facingY, float facingZ) {
		if (world.isClient) {
			return true;
		} else if (direction != Direction.UP) {
			return false;
		} else {
			BlockState blockState = world.getBlockState(pos);
			Block block = blockState.getBlock();
			boolean bl = block.isReplaceable(world, pos);
			if (!bl) {
				pos = pos.up();
			}

			int i = MathHelper.floor((double)(player.yaw * 4.0F / 360.0F) + 0.5) & 3;
			Direction direction2 = Direction.fromHorizontal(i);
			BlockPos blockPos = pos.offset(direction2);
			if (player.canModify(pos, direction, itemStack) && player.canModify(blockPos, direction, itemStack)) {
				boolean bl2 = world.getBlockState(blockPos).getBlock().isReplaceable(world, blockPos);
				boolean bl3 = bl || world.isAir(pos);
				boolean bl4 = bl2 || world.isAir(blockPos);
				if (bl3 && bl4 && World.isOpaque(world, pos.down()) && World.isOpaque(world, blockPos.down())) {
					BlockState blockState2 = Blocks.BED
						.getDefaultState()
						.with(BedBlock.OCCUPIED, false)
						.with(BedBlock.FACING, direction2)
						.with(BedBlock.BED_TYPE, BedBlock.BedBlockType.FOOT);
					if (world.setBlockState(pos, blockState2, 3)) {
						BlockState blockState3 = blockState2.with(BedBlock.BED_TYPE, BedBlock.BedBlockType.HEAD);
						world.setBlockState(blockPos, blockState3, 3);
					}

					itemStack.count--;
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
	}
}
