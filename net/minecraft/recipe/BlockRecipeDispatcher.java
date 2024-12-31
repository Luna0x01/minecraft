package net.minecraft.recipe;

import net.minecraft.block.Blocks;
import net.minecraft.block.DirtBlock;
import net.minecraft.block.PrismarineBlock;
import net.minecraft.block.QuartzBlock;
import net.minecraft.block.RedSandstoneBlock;
import net.minecraft.block.RedSandstoneSlabBlock;
import net.minecraft.block.SandBlock;
import net.minecraft.block.SandstoneBlock;
import net.minecraft.block.StoneBlock;
import net.minecraft.block.StoneBrickBlock;
import net.minecraft.block.StoneSlabBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DyeColor;

public class BlockRecipeDispatcher {
	public void register(RecipeDispatcher recipes) {
		recipes.registerShapedRecipe(new ItemStack(Blocks.CHEST), "###", "# #", "###", '#', Blocks.PLANKS);
		recipes.registerShapedRecipe(new ItemStack(Blocks.TRAPPED_CHEST), "#-", '#', Blocks.CHEST, '-', Blocks.TRIPWIRE_HOOK);
		recipes.registerShapedRecipe(new ItemStack(Blocks.ENDERCHEST), "###", "#E#", "###", '#', Blocks.OBSIDIAN, 'E', Items.EYE_OF_ENDER);
		recipes.registerShapedRecipe(new ItemStack(Blocks.FURNACE), "###", "# #", "###", '#', Blocks.COBBLESTONE);
		recipes.registerShapedRecipe(new ItemStack(Blocks.CRAFTING_TABLE), "##", "##", '#', Blocks.PLANKS);
		recipes.registerShapedRecipe(new ItemStack(Blocks.SANDSTONE), "##", "##", '#', new ItemStack(Blocks.SAND, 1, SandBlock.SandType.SAND.getId()));
		recipes.registerShapedRecipe(new ItemStack(Blocks.RED_SANDSTONE), "##", "##", '#', new ItemStack(Blocks.SAND, 1, SandBlock.SandType.RED_SAND.getId()));
		recipes.registerShapedRecipe(
			new ItemStack(Blocks.SANDSTONE, 4, SandstoneBlock.SandstoneType.SMOOTH.getId()),
			"##",
			"##",
			'#',
			new ItemStack(Blocks.SANDSTONE, 1, SandstoneBlock.SandstoneType.DEFAULT.getId())
		);
		recipes.registerShapedRecipe(
			new ItemStack(Blocks.RED_SANDSTONE, 4, RedSandstoneBlock.RedSandstoneType.SMOOTH.getId()),
			"##",
			"##",
			'#',
			new ItemStack(Blocks.RED_SANDSTONE, 1, RedSandstoneBlock.RedSandstoneType.DEFAULT.getId())
		);
		recipes.registerShapedRecipe(
			new ItemStack(Blocks.SANDSTONE, 1, SandstoneBlock.SandstoneType.CHISELED.getId()),
			"#",
			"#",
			'#',
			new ItemStack(Blocks.STONE_SLAB, 1, StoneSlabBlock.SlabType.SANDSTONE.getId())
		);
		recipes.registerShapedRecipe(
			new ItemStack(Blocks.RED_SANDSTONE, 1, RedSandstoneBlock.RedSandstoneType.CHISELED.getId()),
			"#",
			"#",
			'#',
			new ItemStack(Blocks.STONE_SLAB2, 1, RedSandstoneSlabBlock.SlabType.RED_SANDSTONE.getId())
		);
		recipes.registerShapedRecipe(
			new ItemStack(Blocks.QUARTZ_BLOCK, 1, QuartzBlock.QuartzType.CHISELED.getId()),
			"#",
			"#",
			'#',
			new ItemStack(Blocks.STONE_SLAB, 1, StoneSlabBlock.SlabType.QUARTZ.getId())
		);
		recipes.registerShapedRecipe(
			new ItemStack(Blocks.QUARTZ_BLOCK, 2, QuartzBlock.QuartzType.LINES_X.getId()),
			"#",
			"#",
			'#',
			new ItemStack(Blocks.QUARTZ_BLOCK, 1, QuartzBlock.QuartzType.DEFAULT.getId())
		);
		recipes.registerShapedRecipe(new ItemStack(Blocks.STONE_BRICKS, 4), "##", "##", '#', new ItemStack(Blocks.STONE, 1, StoneBlock.StoneType.STONE.byId()));
		recipes.registerShapedRecipe(
			new ItemStack(Blocks.STONE_BRICKS, 1, StoneBrickBlock.CHISELED_ID),
			"#",
			"#",
			'#',
			new ItemStack(Blocks.STONE_SLAB, 1, StoneSlabBlock.SlabType.STONE_BRICK.getId())
		);
		recipes.registerShapelessRecipe(new ItemStack(Blocks.STONE_BRICKS, 1, StoneBrickBlock.MOSSY_ID), Blocks.STONE_BRICKS, Blocks.VINE);
		recipes.registerShapelessRecipe(new ItemStack(Blocks.MOSSY_COBBLESTONE, 1), Blocks.COBBLESTONE, Blocks.VINE);
		recipes.registerShapedRecipe(new ItemStack(Blocks.IRON_BARS, 16), "###", "###", '#', Items.IRON_INGOT);
		recipes.registerShapedRecipe(new ItemStack(Blocks.GLASS_PANE, 16), "###", "###", '#', Blocks.GLASS);
		recipes.registerShapedRecipe(new ItemStack(Blocks.REDSTONE_LAMP, 1), " R ", "RGR", " R ", 'R', Items.REDSTONE, 'G', Blocks.GLOWSTONE);
		recipes.registerShapedRecipe(new ItemStack(Blocks.BEACON, 1), "GGG", "GSG", "OOO", 'G', Blocks.GLASS, 'S', Items.NETHER_STAR, 'O', Blocks.OBSIDIAN);
		recipes.registerShapedRecipe(new ItemStack(Blocks.NETHER_BRICKS, 1), "NN", "NN", 'N', Items.NETHERBRICK);
		recipes.registerShapedRecipe(new ItemStack(Blocks.STONE, 2, StoneBlock.StoneType.DIORITE.byId()), "CQ", "QC", 'C', Blocks.COBBLESTONE, 'Q', Items.QUARTZ);
		recipes.registerShapelessRecipe(
			new ItemStack(Blocks.STONE, 1, StoneBlock.StoneType.GRANITE.byId()), new ItemStack(Blocks.STONE, 1, StoneBlock.StoneType.DIORITE.byId()), Items.QUARTZ
		);
		recipes.registerShapelessRecipe(
			new ItemStack(Blocks.STONE, 2, StoneBlock.StoneType.ANDESITE.byId()),
			new ItemStack(Blocks.STONE, 1, StoneBlock.StoneType.DIORITE.byId()),
			Blocks.COBBLESTONE
		);
		recipes.registerShapedRecipe(
			new ItemStack(Blocks.DIRT, 4, DirtBlock.DirtType.COARSE_DIRT.getId()),
			"DG",
			"GD",
			'D',
			new ItemStack(Blocks.DIRT, 1, DirtBlock.DirtType.DIRT.getId()),
			'G',
			Blocks.GRAVEL
		);
		recipes.registerShapedRecipe(
			new ItemStack(Blocks.STONE, 4, StoneBlock.StoneType.POLISHED_DIORITE.byId()),
			"SS",
			"SS",
			'S',
			new ItemStack(Blocks.STONE, 1, StoneBlock.StoneType.DIORITE.byId())
		);
		recipes.registerShapedRecipe(
			new ItemStack(Blocks.STONE, 4, StoneBlock.StoneType.POLISHED_GRANITE.byId()),
			"SS",
			"SS",
			'S',
			new ItemStack(Blocks.STONE, 1, StoneBlock.StoneType.GRANITE.byId())
		);
		recipes.registerShapedRecipe(
			new ItemStack(Blocks.STONE, 4, StoneBlock.StoneType.POLISHED_ANDESITE.byId()),
			"SS",
			"SS",
			'S',
			new ItemStack(Blocks.STONE, 1, StoneBlock.StoneType.ANDESITE.byId())
		);
		recipes.registerShapedRecipe(new ItemStack(Blocks.PRISMARINE, 1, PrismarineBlock.ROUGH_ID), "SS", "SS", 'S', Items.PRISMARINE_SHARD);
		recipes.registerShapedRecipe(new ItemStack(Blocks.PRISMARINE, 1, PrismarineBlock.BRICKS_ID), "SSS", "SSS", "SSS", 'S', Items.PRISMARINE_SHARD);
		recipes.registerShapedRecipe(
			new ItemStack(Blocks.PRISMARINE, 1, PrismarineBlock.DARK_ID),
			"SSS",
			"SIS",
			"SSS",
			'S',
			Items.PRISMARINE_SHARD,
			'I',
			new ItemStack(Items.DYE, 1, DyeColor.BLACK.getSwappedId())
		);
		recipes.registerShapedRecipe(new ItemStack(Blocks.SEA_LANTERN, 1, 0), "SCS", "CCC", "SCS", 'S', Items.PRISMARINE_SHARD, 'C', Items.PRISMARINE_CRYSTALS);
	}
}
