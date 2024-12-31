package net.minecraft.item;

import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.Sounds;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AxeItem extends ToolItem {
	private static final Set<Block> EFFECTIVE_BLOCKS = Sets.newHashSet(
		new Block[]{
			Blocks.OAK_PLANKS,
			Blocks.SPRUCE_PLANKS,
			Blocks.BIRCH_PLANKS,
			Blocks.JUNGLE_PLANKS,
			Blocks.ACACIA_PLANKS,
			Blocks.DARK_OAK_PLANKS,
			Blocks.BOOKSHELF,
			Blocks.OAK_WOOD,
			Blocks.SPRUCE_WOOD,
			Blocks.BIRCH_WOOD,
			Blocks.JUNGLE_WOOD,
			Blocks.ACACIA_WOOD,
			Blocks.DARK_OAK_WOOD,
			Blocks.OAK_LOG,
			Blocks.SPRUCE_LOG,
			Blocks.BIRCH_LOG,
			Blocks.JUNGLE_LOG,
			Blocks.ACACIA_LOG,
			Blocks.DARK_OAK_LOG,
			Blocks.CHEST,
			Blocks.PUMPKIN,
			Blocks.CARVED_PUMPKIN,
			Blocks.JACK_O_LANTERN,
			Blocks.MELON_BLOCK,
			Blocks.LADDER,
			Blocks.OAK_BUTTON,
			Blocks.SPRUCE_BUTTON,
			Blocks.BIRCH_BUTTON,
			Blocks.JUNGLE_BUTTON,
			Blocks.DARK_OAK_BUTTON,
			Blocks.ACACIA_BUTTON,
			Blocks.OAK_PRESSURE_PLATE,
			Blocks.SPRUCE_PRESSURE_PLATE,
			Blocks.BIRCH_PRESSURE_PLATE,
			Blocks.JUNGLE_PRESSURE_PLATE,
			Blocks.DARK_OAK_PRESSURE_PLATE,
			Blocks.ACACIA_PRESSURE_PLATE
		}
	);
	protected static final Map<Block, Block> BLOCK_TRANSFORMATIONS_MAP = new Builder()
		.put(Blocks.OAK_WOOD, Blocks.STRIPPED_OAK_WOOD)
		.put(Blocks.OAK_LOG, Blocks.STRIPPED_OAK_LOG)
		.put(Blocks.DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_WOOD)
		.put(Blocks.DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_LOG)
		.put(Blocks.ACACIA_WOOD, Blocks.STRIPPED_ACACIA_WOOD)
		.put(Blocks.ACACIA_LOG, Blocks.STRIPPED_ACACIA_LOG)
		.put(Blocks.BIRCH_WOOD, Blocks.STRIPPED_BIRCH_WOOD)
		.put(Blocks.BIRCH_LOG, Blocks.STRIPPED_BIRCH_LOG)
		.put(Blocks.JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_WOOD)
		.put(Blocks.JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_LOG)
		.put(Blocks.SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_WOOD)
		.put(Blocks.SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_LOG)
		.build();

	protected AxeItem(IToolMaterial iToolMaterial, float f, float g, Item.Settings settings) {
		super(f, g, iToolMaterial, EFFECTIVE_BLOCKS, settings);
	}

	@Override
	public float getBlockBreakingSpeed(ItemStack stack, BlockState state) {
		Material material = state.getMaterial();
		return material != Material.WOOD && material != Material.PLANT && material != Material.REPLACEABLE_PLANT
			? super.getBlockBreakingSpeed(stack, state)
			: this.miningSpeed;
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		World world = itemUsageContext.getWorld();
		BlockPos blockPos = itemUsageContext.getBlockPos();
		BlockState blockState = world.getBlockState(blockPos);
		Block block = (Block)BLOCK_TRANSFORMATIONS_MAP.get(blockState.getBlock());
		if (block != null) {
			PlayerEntity playerEntity = itemUsageContext.getPlayer();
			world.playSound(playerEntity, blockPos, Sounds.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F);
			if (!world.isClient) {
				world.setBlockState(blockPos, block.getDefaultState().withProperty(PillarBlock.PILLAR_AXIS, blockState.getProperty(PillarBlock.PILLAR_AXIS)), 11);
				if (playerEntity != null) {
					itemUsageContext.getItemStack().damage(1, playerEntity);
				}
			}

			return ActionResult.SUCCESS;
		} else {
			return ActionResult.PASS;
		}
	}
}
