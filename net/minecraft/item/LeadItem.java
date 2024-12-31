package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.FenceBlock;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class LeadItem extends Item {
	public LeadItem() {
		this.setItemGroup(ItemGroup.TOOLS);
	}

	@Override
	public boolean use(ItemStack itemStack, PlayerEntity player, World world, BlockPos pos, Direction direction, float facingX, float facingY, float facingZ) {
		Block block = world.getBlockState(pos).getBlock();
		if (block instanceof FenceBlock) {
			if (world.isClient) {
				return true;
			} else {
				useLead(player, world, pos);
				return true;
			}
		} else {
			return false;
		}
	}

	public static boolean useLead(PlayerEntity player, World world, BlockPos pos) {
		LeashKnotEntity leashKnotEntity = LeashKnotEntity.getOrCreate(world, pos);
		boolean bl = false;
		double d = 7.0;
		int i = pos.getX();
		int j = pos.getY();
		int k = pos.getZ();

		for (MobEntity mobEntity : world.getEntitiesInBox(
			MobEntity.class, new Box((double)i - d, (double)j - d, (double)k - d, (double)i + d, (double)j + d, (double)k + d)
		)) {
			if (mobEntity.isLeashed() && mobEntity.getLeashOwner() == player) {
				if (leashKnotEntity == null) {
					leashKnotEntity = LeashKnotEntity.create(world, pos);
				}

				mobEntity.attachLeash(leashKnotEntity, true);
				bl = true;
			}
		}

		return bl;
	}
}
