package net.minecraft.recipe;

import net.minecraft.block.Blocks;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.block.FlowerBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DyeColor;

public class DyeRecipeDispatcher {
	public void register(RecipeDispatcher recipes) {
		for (int i = 0; i < 16; i++) {
			recipes.registerShapelessRecipe(new ItemStack(Blocks.WOOL, 1, i), new ItemStack(Items.DYE, 1, 15 - i), new ItemStack(Item.fromBlock(Blocks.WOOL), 1, 0));
			recipes.registerShapedRecipe(
				new ItemStack(Blocks.STAINED_TERRACOTTA, 8, 15 - i), "###", "#X#", "###", '#', new ItemStack(Blocks.TERRACOTTA), 'X', new ItemStack(Items.DYE, 1, i)
			);
			recipes.registerShapedRecipe(
				new ItemStack(Blocks.STAINED_GLASS, 8, 15 - i), "###", "#X#", "###", '#', new ItemStack(Blocks.GLASS), 'X', new ItemStack(Items.DYE, 1, i)
			);
			recipes.registerShapedRecipe(new ItemStack(Blocks.STAINED_GLASS_PANE, 16, i), "###", "###", '#', new ItemStack(Blocks.STAINED_GLASS, 1, i));
		}

		recipes.registerShapelessRecipe(
			new ItemStack(Items.DYE, 1, DyeColor.YELLOW.getSwappedId()), new ItemStack(Blocks.YELLOW_FLOWER, 1, FlowerBlock.FlowerType.DANDELION.getDataIndex())
		);
		recipes.registerShapelessRecipe(
			new ItemStack(Items.DYE, 1, DyeColor.RED.getSwappedId()), new ItemStack(Blocks.RED_FLOWER, 1, FlowerBlock.FlowerType.POPPY.getDataIndex())
		);
		recipes.registerShapelessRecipe(new ItemStack(Items.DYE, 3, DyeColor.WHITE.getSwappedId()), Items.BONE);
		recipes.registerShapelessRecipe(
			new ItemStack(Items.DYE, 2, DyeColor.PINK.getSwappedId()),
			new ItemStack(Items.DYE, 1, DyeColor.RED.getSwappedId()),
			new ItemStack(Items.DYE, 1, DyeColor.WHITE.getSwappedId())
		);
		recipes.registerShapelessRecipe(
			new ItemStack(Items.DYE, 2, DyeColor.ORANGE.getSwappedId()),
			new ItemStack(Items.DYE, 1, DyeColor.RED.getSwappedId()),
			new ItemStack(Items.DYE, 1, DyeColor.YELLOW.getSwappedId())
		);
		recipes.registerShapelessRecipe(
			new ItemStack(Items.DYE, 2, DyeColor.LIME.getSwappedId()),
			new ItemStack(Items.DYE, 1, DyeColor.GREEN.getSwappedId()),
			new ItemStack(Items.DYE, 1, DyeColor.WHITE.getSwappedId())
		);
		recipes.registerShapelessRecipe(
			new ItemStack(Items.DYE, 2, DyeColor.GRAY.getSwappedId()),
			new ItemStack(Items.DYE, 1, DyeColor.BLACK.getSwappedId()),
			new ItemStack(Items.DYE, 1, DyeColor.WHITE.getSwappedId())
		);
		recipes.registerShapelessRecipe(
			new ItemStack(Items.DYE, 2, DyeColor.SILVER.getSwappedId()),
			new ItemStack(Items.DYE, 1, DyeColor.GRAY.getSwappedId()),
			new ItemStack(Items.DYE, 1, DyeColor.WHITE.getSwappedId())
		);
		recipes.registerShapelessRecipe(
			new ItemStack(Items.DYE, 3, DyeColor.SILVER.getSwappedId()),
			new ItemStack(Items.DYE, 1, DyeColor.BLACK.getSwappedId()),
			new ItemStack(Items.DYE, 1, DyeColor.WHITE.getSwappedId()),
			new ItemStack(Items.DYE, 1, DyeColor.WHITE.getSwappedId())
		);
		recipes.registerShapelessRecipe(
			new ItemStack(Items.DYE, 2, DyeColor.LIGHT_BLUE.getSwappedId()),
			new ItemStack(Items.DYE, 1, DyeColor.BLUE.getSwappedId()),
			new ItemStack(Items.DYE, 1, DyeColor.WHITE.getSwappedId())
		);
		recipes.registerShapelessRecipe(
			new ItemStack(Items.DYE, 2, DyeColor.CYAN.getSwappedId()),
			new ItemStack(Items.DYE, 1, DyeColor.BLUE.getSwappedId()),
			new ItemStack(Items.DYE, 1, DyeColor.GREEN.getSwappedId())
		);
		recipes.registerShapelessRecipe(
			new ItemStack(Items.DYE, 2, DyeColor.PURPLE.getSwappedId()),
			new ItemStack(Items.DYE, 1, DyeColor.BLUE.getSwappedId()),
			new ItemStack(Items.DYE, 1, DyeColor.RED.getSwappedId())
		);
		recipes.registerShapelessRecipe(
			new ItemStack(Items.DYE, 2, DyeColor.MAGENTA.getSwappedId()),
			new ItemStack(Items.DYE, 1, DyeColor.PURPLE.getSwappedId()),
			new ItemStack(Items.DYE, 1, DyeColor.PINK.getSwappedId())
		);
		recipes.registerShapelessRecipe(
			new ItemStack(Items.DYE, 3, DyeColor.MAGENTA.getSwappedId()),
			new ItemStack(Items.DYE, 1, DyeColor.BLUE.getSwappedId()),
			new ItemStack(Items.DYE, 1, DyeColor.RED.getSwappedId()),
			new ItemStack(Items.DYE, 1, DyeColor.PINK.getSwappedId())
		);
		recipes.registerShapelessRecipe(
			new ItemStack(Items.DYE, 4, DyeColor.MAGENTA.getSwappedId()),
			new ItemStack(Items.DYE, 1, DyeColor.BLUE.getSwappedId()),
			new ItemStack(Items.DYE, 1, DyeColor.RED.getSwappedId()),
			new ItemStack(Items.DYE, 1, DyeColor.RED.getSwappedId()),
			new ItemStack(Items.DYE, 1, DyeColor.WHITE.getSwappedId())
		);
		recipes.registerShapelessRecipe(
			new ItemStack(Items.DYE, 1, DyeColor.LIGHT_BLUE.getSwappedId()), new ItemStack(Blocks.RED_FLOWER, 1, FlowerBlock.FlowerType.BLUE_ORCHID.getDataIndex())
		);
		recipes.registerShapelessRecipe(
			new ItemStack(Items.DYE, 1, DyeColor.MAGENTA.getSwappedId()), new ItemStack(Blocks.RED_FLOWER, 1, FlowerBlock.FlowerType.ALLIUM.getDataIndex())
		);
		recipes.registerShapelessRecipe(
			new ItemStack(Items.DYE, 1, DyeColor.SILVER.getSwappedId()), new ItemStack(Blocks.RED_FLOWER, 1, FlowerBlock.FlowerType.HOUSTONIA.getDataIndex())
		);
		recipes.registerShapelessRecipe(
			new ItemStack(Items.DYE, 1, DyeColor.RED.getSwappedId()), new ItemStack(Blocks.RED_FLOWER, 1, FlowerBlock.FlowerType.RED_TULIP.getDataIndex())
		);
		recipes.registerShapelessRecipe(
			new ItemStack(Items.DYE, 1, DyeColor.ORANGE.getSwappedId()), new ItemStack(Blocks.RED_FLOWER, 1, FlowerBlock.FlowerType.ORANGE_TULIP.getDataIndex())
		);
		recipes.registerShapelessRecipe(
			new ItemStack(Items.DYE, 1, DyeColor.SILVER.getSwappedId()), new ItemStack(Blocks.RED_FLOWER, 1, FlowerBlock.FlowerType.WHITE_TULIP.getDataIndex())
		);
		recipes.registerShapelessRecipe(
			new ItemStack(Items.DYE, 1, DyeColor.PINK.getSwappedId()), new ItemStack(Blocks.RED_FLOWER, 1, FlowerBlock.FlowerType.PINK_TULIP.getDataIndex())
		);
		recipes.registerShapelessRecipe(
			new ItemStack(Items.DYE, 1, DyeColor.SILVER.getSwappedId()), new ItemStack(Blocks.RED_FLOWER, 1, FlowerBlock.FlowerType.OXEYE_DAISY.getDataIndex())
		);
		recipes.registerShapelessRecipe(
			new ItemStack(Items.DYE, 2, DyeColor.YELLOW.getSwappedId()), new ItemStack(Blocks.DOUBLE_PLANT, 1, DoublePlantBlock.DoublePlantType.SUNFLOWER.getId())
		);
		recipes.registerShapelessRecipe(
			new ItemStack(Items.DYE, 2, DyeColor.MAGENTA.getSwappedId()), new ItemStack(Blocks.DOUBLE_PLANT, 1, DoublePlantBlock.DoublePlantType.SYRINGA.getId())
		);
		recipes.registerShapelessRecipe(
			new ItemStack(Items.DYE, 2, DyeColor.RED.getSwappedId()), new ItemStack(Blocks.DOUBLE_PLANT, 1, DoublePlantBlock.DoublePlantType.ROSE.getId())
		);
		recipes.registerShapelessRecipe(
			new ItemStack(Items.DYE, 2, DyeColor.PINK.getSwappedId()), new ItemStack(Blocks.DOUBLE_PLANT, 1, DoublePlantBlock.DoublePlantType.PAEONIA.getId())
		);

		for (int j = 0; j < 16; j++) {
			recipes.registerShapedRecipe(new ItemStack(Blocks.CARPET, 3, j), "##", '#', new ItemStack(Blocks.WOOL, 1, j));
		}
	}
}
