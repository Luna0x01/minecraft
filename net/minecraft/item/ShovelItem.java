package net.minecraft.item;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.Sounds;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ShovelItem extends ToolItem {
	private static final Set<Block> EFFECTIVE_BLOCKS = Sets.newHashSet(
		new Block[]{
			Blocks.CLAY,
			Blocks.DIRT,
			Blocks.FARMLAND,
			Blocks.GRASS,
			Blocks.GRAVEL,
			Blocks.MYCELIUM,
			Blocks.SAND,
			Blocks.SNOW,
			Blocks.SNOW_LAYER,
			Blocks.SOULSAND,
			Blocks.GRASS_PATH
		}
	);

	public ShovelItem(Item.ToolMaterialType toolMaterialType) {
		super(1.5F, -3.0F, toolMaterialType, EFFECTIVE_BLOCKS);
	}

	@Override
	public boolean method_3346(BlockState blockState) {
		Block block = blockState.getBlock();
		return block == Blocks.SNOW_LAYER ? true : block == Blocks.SNOW;
	}

	@Override
	public ActionResult method_3355(
		ItemStack itemStack, PlayerEntity playerEntity, World world, BlockPos blockPos, Hand hand, Direction direction, float f, float g, float h
	) {
		if (!playerEntity.canModify(blockPos.offset(direction), direction, itemStack)) {
			return ActionResult.FAIL;
		} else {
			BlockState blockState = world.getBlockState(blockPos);
			Block block = blockState.getBlock();
			if (direction != Direction.DOWN && world.getBlockState(blockPos.up()).getMaterial() == Material.AIR && block == Blocks.GRASS) {
				BlockState blockState2 = Blocks.GRASS_PATH.getDefaultState();
				world.method_11486(playerEntity, blockPos, Sounds.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
				if (!world.isClient) {
					world.setBlockState(blockPos, blockState2, 11);
					itemStack.damage(1, playerEntity);
				}

				return ActionResult.SUCCESS;
			} else {
				return ActionResult.PASS;
			}
		}
	}
}
