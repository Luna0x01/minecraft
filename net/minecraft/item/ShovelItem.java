package net.minecraft.item;

import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ShovelItem extends MiningToolItem {
	protected static final Map<Block, BlockState> PATH_STATES = Maps.newHashMap(
		new Builder()
			.put(Blocks.GRASS_BLOCK, Blocks.DIRT_PATH.getDefaultState())
			.put(Blocks.DIRT, Blocks.DIRT_PATH.getDefaultState())
			.put(Blocks.PODZOL, Blocks.DIRT_PATH.getDefaultState())
			.put(Blocks.COARSE_DIRT, Blocks.DIRT_PATH.getDefaultState())
			.put(Blocks.MYCELIUM, Blocks.DIRT_PATH.getDefaultState())
			.put(Blocks.ROOTED_DIRT, Blocks.DIRT_PATH.getDefaultState())
			.build()
	);

	public ShovelItem(ToolMaterial material, float attackDamage, float attackSpeed, Item.Settings settings) {
		super(attackDamage, attackSpeed, material, BlockTags.SHOVEL_MINEABLE, settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		BlockState blockState = world.getBlockState(blockPos);
		if (context.getSide() == Direction.DOWN) {
			return ActionResult.PASS;
		} else {
			PlayerEntity playerEntity = context.getPlayer();
			BlockState blockState2 = (BlockState)PATH_STATES.get(blockState.getBlock());
			BlockState blockState3 = null;
			if (blockState2 != null && world.getBlockState(blockPos.up()).isAir()) {
				world.playSound(playerEntity, blockPos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
				blockState3 = blockState2;
			} else if (blockState.getBlock() instanceof CampfireBlock && (Boolean)blockState.get(CampfireBlock.LIT)) {
				if (!world.isClient()) {
					world.syncWorldEvent(null, 1009, blockPos, 0);
				}

				CampfireBlock.extinguish(context.getPlayer(), world, blockPos, blockState);
				blockState3 = blockState.with(CampfireBlock.LIT, Boolean.valueOf(false));
			}

			if (blockState3 != null) {
				if (!world.isClient) {
					world.setBlockState(blockPos, blockState3, 11);
					if (playerEntity != null) {
						context.getStack().damage(1, playerEntity, p -> p.sendToolBreakStatus(context.getHand()));
					}
				}

				return ActionResult.success(world.isClient);
			} else {
				return ActionResult.PASS;
			}
		}
	}
}
