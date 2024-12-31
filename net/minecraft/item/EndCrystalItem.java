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
	public ActionResult use(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction direction, float x, float y, float z) {
		BlockState blockState = world.getBlockState(pos);
		if (blockState.getBlock() != Blocks.OBSIDIAN && blockState.getBlock() != Blocks.BEDROCK) {
			return ActionResult.FAIL;
		} else {
			BlockPos blockPos = pos.up();
			ItemStack itemStack = player.getStackInHand(hand);
			if (!player.canModify(blockPos, direction, itemStack)) {
				return ActionResult.FAIL;
			} else {
				BlockPos blockPos2 = blockPos.up();
				boolean bl = !world.isAir(blockPos) && !world.getBlockState(blockPos).getBlock().method_8638(world, blockPos);
				bl |= !world.isAir(blockPos2) && !world.getBlockState(blockPos2).getBlock().method_8638(world, blockPos2);
				if (bl) {
					return ActionResult.FAIL;
				} else {
					double d = (double)blockPos.getX();
					double e = (double)blockPos.getY();
					double f = (double)blockPos.getZ();
					List<Entity> list = world.getEntitiesIn(null, new Box(d, e, f, d + 1.0, e + 2.0, f + 1.0));
					if (!list.isEmpty()) {
						return ActionResult.FAIL;
					} else {
						if (!world.isClient) {
							EndCrystalEntity endCrystalEntity = new EndCrystalEntity(
								world, (double)((float)pos.getX() + 0.5F), (double)(pos.getY() + 1), (double)((float)pos.getZ() + 0.5F)
							);
							endCrystalEntity.setShowBottom(false);
							world.spawnEntity(endCrystalEntity);
							if (world.dimension instanceof TheEndDimension) {
								DragonRespawnAnimation dragonRespawnAnimation = ((TheEndDimension)world.dimension).method_11818();
								dragonRespawnAnimation.tryRespawn();
							}
						}

						itemStack.decrement(1);
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
