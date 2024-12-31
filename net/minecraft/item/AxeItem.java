package net.minecraft.item;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;

public class AxeItem extends ToolItem {
	private static final Set<Block> EFFECTIVE_BLOCKS = Sets.newHashSet(
		new Block[]{
			Blocks.PLANKS,
			Blocks.BOOKSHELF,
			Blocks.LOG,
			Blocks.LOG2,
			Blocks.CHEST,
			Blocks.PUMPKIN,
			Blocks.JACK_O_LANTERN,
			Blocks.MELON_BLOCK,
			Blocks.LADDER,
			Blocks.WOODEN_BUTTON,
			Blocks.WOODEN_PRESSURE_PLATE
		}
	);
	private static final float[] field_12280 = new float[]{6.0F, 8.0F, 8.0F, 8.0F, 6.0F};
	private static final float[] field_12281 = new float[]{-3.2F, -3.2F, -3.1F, -3.0F, -3.0F};

	protected AxeItem(Item.ToolMaterialType toolMaterialType) {
		super(toolMaterialType, EFFECTIVE_BLOCKS);
		this.attackDamage = field_12280[toolMaterialType.ordinal()];
		this.field_12294 = field_12281[toolMaterialType.ordinal()];
	}

	@Override
	public float getBlockBreakingSpeed(ItemStack stack, BlockState state) {
		Material material = state.getMaterial();
		return material != Material.WOOD && material != Material.PLANT && material != Material.REPLACEABLE_PLANT
			? super.getBlockBreakingSpeed(stack, state)
			: this.miningSpeed;
	}
}
