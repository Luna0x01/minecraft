package net.minecraft.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.Sounds;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ShovelItem extends ToolItem {
	private static final Set<Block> EFFECTIVE_BLOCKS = Sets.newHashSet(
		new Block[]{
			Blocks.CLAY,
			Blocks.DIRT,
			Blocks.COARSE_DIRT,
			Blocks.PODZOL,
			Blocks.FARMLAND,
			Blocks.GRASS_BLOCK,
			Blocks.GRAVEL,
			Blocks.MYCELIUM,
			Blocks.SAND,
			Blocks.RED_SAND,
			Blocks.SNOW_BLOCK,
			Blocks.SNOW,
			Blocks.SOULSAND,
			Blocks.GRASS_PATH,
			Blocks.WHITE_CONCRETE_POWDER,
			Blocks.ORANGE_CONCRETE_POWDER,
			Blocks.MAGENTA_CONCRETE_POWDER,
			Blocks.LIGHT_BLUE_CONCRETE_POWDER,
			Blocks.YELLOW_CONCRETE_POWDER,
			Blocks.LIME_CONCRETE_POWDER,
			Blocks.PINK_CONCRETE_POWDER,
			Blocks.GRAY_CONCRETE_POWDER,
			Blocks.LIGHT_GRAY_CONCRETE_POWDER,
			Blocks.CYAN_CONCRETE_POWDER,
			Blocks.PURPLE_CONCRETE_POWDER,
			Blocks.BLUE_CONCRETE_POWDER,
			Blocks.BROWN_CONCRETE_POWDER,
			Blocks.GREEN_CONCRETE_POWDER,
			Blocks.RED_CONCRETE_POWDER,
			Blocks.BLACK_CONCRETE_POWDER
		}
	);
	protected static final Map<Block, BlockState> field_17376 = Maps.newHashMap(ImmutableMap.of(Blocks.GRASS_BLOCK, Blocks.GRASS_PATH.getDefaultState()));

	public ShovelItem(IToolMaterial iToolMaterial, float f, float g, Item.Settings settings) {
		super(f, g, iToolMaterial, EFFECTIVE_BLOCKS, settings);
	}

	@Override
	public boolean method_3346(BlockState blockState) {
		Block block = blockState.getBlock();
		return block == Blocks.SNOW || block == Blocks.SNOW_BLOCK;
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		World world = itemUsageContext.getWorld();
		BlockPos blockPos = itemUsageContext.getBlockPos();
		if (itemUsageContext.method_16151() != Direction.DOWN && world.getBlockState(blockPos.up()).isAir()) {
			BlockState blockState = (BlockState)field_17376.get(world.getBlockState(blockPos).getBlock());
			if (blockState != null) {
				PlayerEntity playerEntity = itemUsageContext.getPlayer();
				world.playSound(playerEntity, blockPos, Sounds.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
				if (!world.isClient) {
					world.setBlockState(blockPos, blockState, 11);
					if (playerEntity != null) {
						itemUsageContext.getItemStack().damage(1, playerEntity);
					}
				}

				return ActionResult.SUCCESS;
			}
		}

		return ActionResult.PASS;
	}
}
