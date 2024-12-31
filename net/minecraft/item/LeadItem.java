package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.FenceBlock;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class LeadItem extends Item {
	public LeadItem() {
		this.setItemGroup(ItemGroup.TOOLS);
	}

	@Override
	public ActionResult method_3355(
		ItemStack itemStack, PlayerEntity playerEntity, World world, BlockPos blockPos, Hand hand, Direction direction, float f, float g, float h
	) {
		Block block = world.getBlockState(blockPos).getBlock();
		if (!(block instanceof FenceBlock)) {
			return ActionResult.PASS;
		} else {
			if (!world.isClient) {
				useLead(playerEntity, world, blockPos);
			}

			return ActionResult.SUCCESS;
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
			MobEntity.class, new Box((double)i - 7.0, (double)j - 7.0, (double)k - 7.0, (double)i + 7.0, (double)j + 7.0, (double)k + 7.0)
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
