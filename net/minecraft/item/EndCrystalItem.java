package net.minecraft.item;

import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EndCrystalEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.server.DragonRespawnAnimation;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.dimension.TheEndDimension;

public class EndCrystalItem extends Item {
	public EndCrystalItem() {
		this.setTranslationKey("end_crystal");
		this.setItemGroup(ItemGroup.DECORATIONS);
	}

	@Override
	public ActionResult method_3355(
		ItemStack itemStack, PlayerEntity playerEntity, World world, BlockPos blockPos, Hand hand, Direction direction, float f, float g, float h
	) {
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock() != Blocks.OBSIDIAN && blockState.getBlock() != Blocks.BEDROCK) {
			return ActionResult.FAIL;
		} else {
			BlockPos blockPos2 = blockPos.up();
			if (!playerEntity.canModify(blockPos2, direction, itemStack)) {
				return ActionResult.FAIL;
			} else {
				BlockPos blockPos3 = blockPos2.up();
				boolean bl = !world.isAir(blockPos2) && !world.getBlockState(blockPos2).getBlock().method_8638(world, blockPos2);
				bl |= !world.isAir(blockPos3) && !world.getBlockState(blockPos3).getBlock().method_8638(world, blockPos3);
				if (bl) {
					return ActionResult.FAIL;
				} else {
					double d = (double)blockPos2.getX();
					double e = (double)blockPos2.getY();
					double i = (double)blockPos2.getZ();
					List<Entity> list = world.getEntitiesIn(null, new Box(d, e, i, d + 1.0, e + 2.0, i + 1.0));
					if (!list.isEmpty()) {
						return ActionResult.FAIL;
					} else {
						if (!world.isClient) {
							EndCrystalEntity endCrystalEntity = new EndCrystalEntity(
								world, (double)((float)blockPos.getX() + 0.5F), (double)(blockPos.getY() + 1), (double)((float)blockPos.getZ() + 0.5F)
							);
							endCrystalEntity.setShowBottom(false);
							world.spawnEntity(endCrystalEntity);
							if (world.dimension instanceof TheEndDimension) {
								DragonRespawnAnimation dragonRespawnAnimation = ((TheEndDimension)world.dimension).method_11818();
								dragonRespawnAnimation.tryRespawn();
							}
						}

						itemStack.count--;
						return ActionResult.SUCCESS;
					}
				}
			}
		}
	}

	@Override
	public boolean hasEnchantmentGlint(ItemStack stack) {
		return true;
	}
}
