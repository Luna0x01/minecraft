package net.minecraft.recipe;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.PlanksBlock;
import net.minecraft.block.RedSandstoneSlabBlock;
import net.minecraft.block.StoneBlock;
import net.minecraft.block.StoneSlabBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DyeColor;
import net.minecraft.world.World;

public class RecipeDispatcher {
	private static final RecipeDispatcher INSTANCE = new RecipeDispatcher();
	private final List<RecipeType> recipes = Lists.newArrayList();

	public static RecipeDispatcher getInstance() {
		return INSTANCE;
	}

	private RecipeDispatcher() {
		new HoeRecipeDispatcher().register(this);
		new CombatItemRecipeDispatcher().register(this);
		new NineGoldRecipeDispatcher().register(this);
		new FoodRecipeDispatcher().register(this);
		new BlockRecipeDispatcher().register(this);
		new ArmorRecipeDispatcher().register(this);
		new DyeRecipeDispatcher().register(this);
		this.recipes.add(new ArmorDyeRecipeType());
		this.recipes.add(new BookCloningRecipeType());
		this.recipes.add(new MapCloningRecipeType());
		this.recipes.add(new MapUpscaleRecipeType());
		this.recipes.add(new FireworkRecipeType());
		this.recipes.add(new RepairingRecipeType());
		this.recipes.add(new TippedArrowRecipeType());
		new BannerRecipeDispatcher().registerRecipes(this);
		new ShieldRecipeDispatcher().method_11444(this);
		this.registerShapedRecipe(new ItemStack(Items.PAPER, 3), "###", '#', Items.SUGARCANE);
		this.registerShapelessRecipe(new ItemStack(Items.BOOK, 1), Items.PAPER, Items.PAPER, Items.PAPER, Items.LEATHER);
		this.registerShapelessRecipe(new ItemStack(Items.WRITABLE_BOOK, 1), Items.BOOK, new ItemStack(Items.DYE, 1, DyeColor.BLACK.getSwappedId()), Items.FEATHER);
		this.registerShapedRecipe(
			new ItemStack(Blocks.OAK_FENCE, 3), "W#W", "W#W", '#', Items.STICK, 'W', new ItemStack(Blocks.PLANKS, 1, PlanksBlock.WoodType.OAK.getId())
		);
		this.registerShapedRecipe(
			new ItemStack(Blocks.BIRCH_FENCE, 3), "W#W", "W#W", '#', Items.STICK, 'W', new ItemStack(Blocks.PLANKS, 1, PlanksBlock.WoodType.BIRCH.getId())
		);
		this.registerShapedRecipe(
			new ItemStack(Blocks.SPRUCE_FENCE, 3), "W#W", "W#W", '#', Items.STICK, 'W', new ItemStack(Blocks.PLANKS, 1, PlanksBlock.WoodType.SPRUCE.getId())
		);
		this.registerShapedRecipe(
			new ItemStack(Blocks.JUNGLE_FENCE, 3), "W#W", "W#W", '#', Items.STICK, 'W', new ItemStack(Blocks.PLANKS, 1, PlanksBlock.WoodType.JUNGLE.getId())
		);
		this.registerShapedRecipe(
			new ItemStack(Blocks.ACACIA_FENCE, 3), "W#W", "W#W", '#', Items.STICK, 'W', new ItemStack(Blocks.PLANKS, 1, 4 + PlanksBlock.WoodType.ACACIA.getId() - 4)
		);
		this.registerShapedRecipe(
			new ItemStack(Blocks.DARK_OAK_FENCE, 3), "W#W", "W#W", '#', Items.STICK, 'W', new ItemStack(Blocks.PLANKS, 1, 4 + PlanksBlock.WoodType.DARK_OAK.getId() - 4)
		);
		this.registerShapedRecipe(new ItemStack(Blocks.COBBLESTONE_WALL, 6, WallBlock.WallType.NORMAL.getId()), "###", "###", '#', Blocks.COBBLESTONE);
		this.registerShapedRecipe(new ItemStack(Blocks.COBBLESTONE_WALL, 6, WallBlock.WallType.MOSSY.getId()), "###", "###", '#', Blocks.MOSSY_COBBLESTONE);
		this.registerShapedRecipe(new ItemStack(Blocks.NETHER_BRICK_FENCE, 6), "###", "###", '#', Blocks.NETHER_BRICKS);
		this.registerShapedRecipe(
			new ItemStack(Blocks.OAK_FENCE_GATE, 1), "#W#", "#W#", '#', Items.STICK, 'W', new ItemStack(Blocks.PLANKS, 1, PlanksBlock.WoodType.OAK.getId())
		);
		this.registerShapedRecipe(
			new ItemStack(Blocks.BIRCH_FENCE_GATE, 1), "#W#", "#W#", '#', Items.STICK, 'W', new ItemStack(Blocks.PLANKS, 1, PlanksBlock.WoodType.BIRCH.getId())
		);
		this.registerShapedRecipe(
			new ItemStack(Blocks.SPRUCE_FENCE_GATE, 1), "#W#", "#W#", '#', Items.STICK, 'W', new ItemStack(Blocks.PLANKS, 1, PlanksBlock.WoodType.SPRUCE.getId())
		);
		this.registerShapedRecipe(
			new ItemStack(Blocks.JUNGLE_FENCE_GATE, 1), "#W#", "#W#", '#', Items.STICK, 'W', new ItemStack(Blocks.PLANKS, 1, PlanksBlock.WoodType.JUNGLE.getId())
		);
		this.registerShapedRecipe(
			new ItemStack(Blocks.ACACIA_FENCE_GATE, 1),
			"#W#",
			"#W#",
			'#',
			Items.STICK,
			'W',
			new ItemStack(Blocks.PLANKS, 1, 4 + PlanksBlock.WoodType.ACACIA.getId() - 4)
		);
		this.registerShapedRecipe(
			new ItemStack(Blocks.DARK_OAK_FENCE_GATE, 1),
			"#W#",
			"#W#",
			'#',
			Items.STICK,
			'W',
			new ItemStack(Blocks.PLANKS, 1, 4 + PlanksBlock.WoodType.DARK_OAK.getId() - 4)
		);
		this.registerShapedRecipe(new ItemStack(Blocks.JUKEBOX, 1), "###", "#X#", "###", '#', Blocks.PLANKS, 'X', Items.DIAMOND);
		this.registerShapedRecipe(new ItemStack(Items.LEAD, 2), "~~ ", "~O ", "  ~", '~', Items.STRING, 'O', Items.SLIME_BALL);
		this.registerShapedRecipe(new ItemStack(Blocks.NOTEBLOCK, 1), "###", "#X#", "###", '#', Blocks.PLANKS, 'X', Items.REDSTONE);
		this.registerShapedRecipe(new ItemStack(Blocks.BOOKSHELF, 1), "###", "XXX", "###", '#', Blocks.PLANKS, 'X', Items.BOOK);
		this.registerShapedRecipe(new ItemStack(Blocks.SNOW, 1), "##", "##", '#', Items.SNOWBALL);
		this.registerShapedRecipe(new ItemStack(Blocks.SNOW_LAYER, 6), "###", '#', Blocks.SNOW);
		this.registerShapedRecipe(new ItemStack(Blocks.CLAY, 1), "##", "##", '#', Items.CLAY_BALL);
		this.registerShapedRecipe(new ItemStack(Blocks.BRICKS, 1), "##", "##", '#', Items.BRICK);
		this.registerShapedRecipe(new ItemStack(Blocks.GLOWSTONE, 1), "##", "##", '#', Items.GLOWSTONE_DUST);
		this.registerShapedRecipe(new ItemStack(Blocks.QUARTZ_BLOCK, 1), "##", "##", '#', Items.QUARTZ);
		this.registerShapedRecipe(new ItemStack(Blocks.WOOL, 1), "##", "##", '#', Items.STRING);
		this.registerShapedRecipe(new ItemStack(Blocks.TNT, 1), "X#X", "#X#", "X#X", 'X', Items.GUNPOWDER, '#', Blocks.SAND);
		this.registerShapedRecipe(new ItemStack(Blocks.STONE_SLAB, 6, StoneSlabBlock.SlabType.COBBLESTONE.getId()), "###", '#', Blocks.COBBLESTONE);
		this.registerShapedRecipe(
			new ItemStack(Blocks.STONE_SLAB, 6, StoneSlabBlock.SlabType.STONE.getId()), "###", '#', new ItemStack(Blocks.STONE, 1, StoneBlock.StoneType.STONE.byId())
		);
		this.registerShapedRecipe(new ItemStack(Blocks.STONE_SLAB, 6, StoneSlabBlock.SlabType.SANDSTONE.getId()), "###", '#', Blocks.SANDSTONE);
		this.registerShapedRecipe(new ItemStack(Blocks.STONE_SLAB, 6, StoneSlabBlock.SlabType.BRICK.getId()), "###", '#', Blocks.BRICKS);
		this.registerShapedRecipe(new ItemStack(Blocks.STONE_SLAB, 6, StoneSlabBlock.SlabType.STONE_BRICK.getId()), "###", '#', Blocks.STONE_BRICKS);
		this.registerShapedRecipe(new ItemStack(Blocks.STONE_SLAB, 6, StoneSlabBlock.SlabType.NETHER_BRICK.getId()), "###", '#', Blocks.NETHER_BRICKS);
		this.registerShapedRecipe(new ItemStack(Blocks.STONE_SLAB, 6, StoneSlabBlock.SlabType.QUARTZ.getId()), "###", '#', Blocks.QUARTZ_BLOCK);
		this.registerShapedRecipe(new ItemStack(Blocks.STONE_SLAB2, 6, RedSandstoneSlabBlock.SlabType.RED_SANDSTONE.getId()), "###", '#', Blocks.RED_SANDSTONE);
		this.registerShapedRecipe(new ItemStack(Blocks.PURPUR_SLAB, 6, 0), "###", '#', Blocks.PURPUR_BLOCK);
		this.registerShapedRecipe(new ItemStack(Blocks.WOODEN_SLAB, 6, 0), "###", '#', new ItemStack(Blocks.PLANKS, 1, PlanksBlock.WoodType.OAK.getId()));
		this.registerShapedRecipe(
			new ItemStack(Blocks.WOODEN_SLAB, 6, PlanksBlock.WoodType.BIRCH.getId()), "###", '#', new ItemStack(Blocks.PLANKS, 1, PlanksBlock.WoodType.BIRCH.getId())
		);
		this.registerShapedRecipe(
			new ItemStack(Blocks.WOODEN_SLAB, 6, PlanksBlock.WoodType.SPRUCE.getId()), "###", '#', new ItemStack(Blocks.PLANKS, 1, PlanksBlock.WoodType.SPRUCE.getId())
		);
		this.registerShapedRecipe(
			new ItemStack(Blocks.WOODEN_SLAB, 6, PlanksBlock.WoodType.JUNGLE.getId()), "###", '#', new ItemStack(Blocks.PLANKS, 1, PlanksBlock.WoodType.JUNGLE.getId())
		);
		this.registerShapedRecipe(
			new ItemStack(Blocks.WOODEN_SLAB, 6, 4 + PlanksBlock.WoodType.ACACIA.getId() - 4),
			"###",
			'#',
			new ItemStack(Blocks.PLANKS, 1, 4 + PlanksBlock.WoodType.ACACIA.getId() - 4)
		);
		this.registerShapedRecipe(
			new ItemStack(Blocks.WOODEN_SLAB, 6, 4 + PlanksBlock.WoodType.DARK_OAK.getId() - 4),
			"###",
			'#',
			new ItemStack(Blocks.PLANKS, 1, 4 + PlanksBlock.WoodType.DARK_OAK.getId() - 4)
		);
		this.registerShapedRecipe(new ItemStack(Blocks.LADDER, 3), "# #", "###", "# #", '#', Items.STICK);
		this.registerShapedRecipe(new ItemStack(Items.OAK_DOOR, 3), "##", "##", "##", '#', new ItemStack(Blocks.PLANKS, 1, PlanksBlock.WoodType.OAK.getId()));
		this.registerShapedRecipe(new ItemStack(Items.SPRUCE_DOOR, 3), "##", "##", "##", '#', new ItemStack(Blocks.PLANKS, 1, PlanksBlock.WoodType.SPRUCE.getId()));
		this.registerShapedRecipe(new ItemStack(Items.BIRCH_DOOR, 3), "##", "##", "##", '#', new ItemStack(Blocks.PLANKS, 1, PlanksBlock.WoodType.BIRCH.getId()));
		this.registerShapedRecipe(new ItemStack(Items.JUNGLE_DOOR, 3), "##", "##", "##", '#', new ItemStack(Blocks.PLANKS, 1, PlanksBlock.WoodType.JUNGLE.getId()));
		this.registerShapedRecipe(new ItemStack(Items.ACACIA_DOOR, 3), "##", "##", "##", '#', new ItemStack(Blocks.PLANKS, 1, PlanksBlock.WoodType.ACACIA.getId()));
		this.registerShapedRecipe(
			new ItemStack(Items.DARK_OAK_DOOR, 3), "##", "##", "##", '#', new ItemStack(Blocks.PLANKS, 1, PlanksBlock.WoodType.DARK_OAK.getId())
		);
		this.registerShapedRecipe(new ItemStack(Blocks.TRAPDOOR, 2), "###", "###", '#', Blocks.PLANKS);
		this.registerShapedRecipe(new ItemStack(Items.IRON_DOOR, 3), "##", "##", "##", '#', Items.IRON_INGOT);
		this.registerShapedRecipe(new ItemStack(Blocks.IRON_TRAPDOOR, 1), "##", "##", '#', Items.IRON_INGOT);
		this.registerShapedRecipe(new ItemStack(Items.SIGN, 3), "###", "###", " X ", '#', Blocks.PLANKS, 'X', Items.STICK);
		this.registerShapedRecipe(new ItemStack(Items.CAKE, 1), "AAA", "BEB", "CCC", 'A', Items.MILK_BUCKET, 'B', Items.SUGAR, 'C', Items.WHEAT, 'E', Items.EGG);
		this.registerShapedRecipe(new ItemStack(Items.SUGAR, 1), "#", '#', Items.SUGARCANE);
		this.registerShapedRecipe(
			new ItemStack(Blocks.PLANKS, 4, PlanksBlock.WoodType.OAK.getId()), "#", '#', new ItemStack(Blocks.LOG, 1, PlanksBlock.WoodType.OAK.getId())
		);
		this.registerShapedRecipe(
			new ItemStack(Blocks.PLANKS, 4, PlanksBlock.WoodType.SPRUCE.getId()), "#", '#', new ItemStack(Blocks.LOG, 1, PlanksBlock.WoodType.SPRUCE.getId())
		);
		this.registerShapedRecipe(
			new ItemStack(Blocks.PLANKS, 4, PlanksBlock.WoodType.BIRCH.getId()), "#", '#', new ItemStack(Blocks.LOG, 1, PlanksBlock.WoodType.BIRCH.getId())
		);
		this.registerShapedRecipe(
			new ItemStack(Blocks.PLANKS, 4, PlanksBlock.WoodType.JUNGLE.getId()), "#", '#', new ItemStack(Blocks.LOG, 1, PlanksBlock.WoodType.JUNGLE.getId())
		);
		this.registerShapedRecipe(
			new ItemStack(Blocks.PLANKS, 4, 4 + PlanksBlock.WoodType.ACACIA.getId() - 4),
			"#",
			'#',
			new ItemStack(Blocks.LOG2, 1, PlanksBlock.WoodType.ACACIA.getId() - 4)
		);
		this.registerShapedRecipe(
			new ItemStack(Blocks.PLANKS, 4, 4 + PlanksBlock.WoodType.DARK_OAK.getId() - 4),
			"#",
			'#',
			new ItemStack(Blocks.LOG2, 1, PlanksBlock.WoodType.DARK_OAK.getId() - 4)
		);
		this.registerShapedRecipe(new ItemStack(Items.STICK, 4), "#", "#", '#', Blocks.PLANKS);
		this.registerShapedRecipe(new ItemStack(Blocks.TORCH, 4), "X", "#", 'X', Items.COAL, '#', Items.STICK);
		this.registerShapedRecipe(new ItemStack(Blocks.TORCH, 4), "X", "#", 'X', new ItemStack(Items.COAL, 1, 1), '#', Items.STICK);
		this.registerShapedRecipe(new ItemStack(Items.BOWL, 4), "# #", " # ", '#', Blocks.PLANKS);
		this.registerShapedRecipe(new ItemStack(Items.GLASS_BOTTLE, 3), "# #", " # ", '#', Blocks.GLASS);
		this.registerShapedRecipe(new ItemStack(Blocks.RAIL, 16), "X X", "X#X", "X X", 'X', Items.IRON_INGOT, '#', Items.STICK);
		this.registerShapedRecipe(new ItemStack(Blocks.POWERED_RAIL, 6), "X X", "X#X", "XRX", 'X', Items.GOLD_INGOT, 'R', Items.REDSTONE, '#', Items.STICK);
		this.registerShapedRecipe(new ItemStack(Blocks.ACTIVATOR_RAIL, 6), "XSX", "X#X", "XSX", 'X', Items.IRON_INGOT, '#', Blocks.REDSTONE_TORCH, 'S', Items.STICK);
		this.registerShapedRecipe(
			new ItemStack(Blocks.DETECTOR_RAIL, 6), "X X", "X#X", "XRX", 'X', Items.IRON_INGOT, 'R', Items.REDSTONE, '#', Blocks.STONE_PRESSURE_PLATE
		);
		this.registerShapedRecipe(new ItemStack(Items.MINECART, 1), "# #", "###", '#', Items.IRON_INGOT);
		this.registerShapedRecipe(new ItemStack(Items.CAULDRON, 1), "# #", "# #", "###", '#', Items.IRON_INGOT);
		this.registerShapedRecipe(new ItemStack(Items.BREWING_STAND, 1), " B ", "###", '#', Blocks.COBBLESTONE, 'B', Items.BLAZE_ROD);
		this.registerShapedRecipe(new ItemStack(Blocks.JACK_O_LANTERN, 1), "A", "B", 'A', Blocks.PUMPKIN, 'B', Blocks.TORCH);
		this.registerShapedRecipe(new ItemStack(Items.MINECART_WITH_CHEST, 1), "A", "B", 'A', Blocks.CHEST, 'B', Items.MINECART);
		this.registerShapedRecipe(new ItemStack(Items.MINECART_WITH_FURNACE, 1), "A", "B", 'A', Blocks.FURNACE, 'B', Items.MINECART);
		this.registerShapedRecipe(new ItemStack(Items.MINECART_WITH_TNT, 1), "A", "B", 'A', Blocks.TNT, 'B', Items.MINECART);
		this.registerShapedRecipe(new ItemStack(Items.MINECART_WITH_HOPPER, 1), "A", "B", 'A', Blocks.HOPPER, 'B', Items.MINECART);
		this.registerShapedRecipe(new ItemStack(Items.BOAT, 1), "# #", "###", '#', new ItemStack(Blocks.PLANKS, 1, PlanksBlock.WoodType.OAK.getId()));
		this.registerShapedRecipe(new ItemStack(Items.SPRUCE_BOAT, 1), "# #", "###", '#', new ItemStack(Blocks.PLANKS, 1, PlanksBlock.WoodType.SPRUCE.getId()));
		this.registerShapedRecipe(new ItemStack(Items.BIRCH_BOAT, 1), "# #", "###", '#', new ItemStack(Blocks.PLANKS, 1, PlanksBlock.WoodType.BIRCH.getId()));
		this.registerShapedRecipe(new ItemStack(Items.JUNGLE_BOAT, 1), "# #", "###", '#', new ItemStack(Blocks.PLANKS, 1, PlanksBlock.WoodType.JUNGLE.getId()));
		this.registerShapedRecipe(new ItemStack(Items.ACACIA_BOAT, 1), "# #", "###", '#', new ItemStack(Blocks.PLANKS, 1, PlanksBlock.WoodType.ACACIA.getId()));
		this.registerShapedRecipe(new ItemStack(Items.DARK_OAK_BOAT, 1), "# #", "###", '#', new ItemStack(Blocks.PLANKS, 1, PlanksBlock.WoodType.DARK_OAK.getId()));
		this.registerShapedRecipe(new ItemStack(Items.BUCKET, 1), "# #", " # ", '#', Items.IRON_INGOT);
		this.registerShapedRecipe(new ItemStack(Items.FLOWER_POT, 1), "# #", " # ", '#', Items.BRICK);
		this.registerShapelessRecipe(new ItemStack(Items.FLINT_AND_STEEL, 1), new ItemStack(Items.IRON_INGOT, 1), new ItemStack(Items.FLINT, 1));
		this.registerShapedRecipe(new ItemStack(Items.BREAD, 1), "###", '#', Items.WHEAT);
		this.registerShapedRecipe(new ItemStack(Blocks.WOODEN_STAIRS, 4), "#  ", "## ", "###", '#', new ItemStack(Blocks.PLANKS, 1, PlanksBlock.WoodType.OAK.getId()));
		this.registerShapedRecipe(
			new ItemStack(Blocks.BIRCH_STAIRS, 4), "#  ", "## ", "###", '#', new ItemStack(Blocks.PLANKS, 1, PlanksBlock.WoodType.BIRCH.getId())
		);
		this.registerShapedRecipe(
			new ItemStack(Blocks.SPRUCE_STAIRS, 4), "#  ", "## ", "###", '#', new ItemStack(Blocks.PLANKS, 1, PlanksBlock.WoodType.SPRUCE.getId())
		);
		this.registerShapedRecipe(
			new ItemStack(Blocks.JUNGLE_STAIRS, 4), "#  ", "## ", "###", '#', new ItemStack(Blocks.PLANKS, 1, PlanksBlock.WoodType.JUNGLE.getId())
		);
		this.registerShapedRecipe(
			new ItemStack(Blocks.ACACIA_STAIRS, 4), "#  ", "## ", "###", '#', new ItemStack(Blocks.PLANKS, 1, 4 + PlanksBlock.WoodType.ACACIA.getId() - 4)
		);
		this.registerShapedRecipe(
			new ItemStack(Blocks.DARK_OAK_STAIRS, 4), "#  ", "## ", "###", '#', new ItemStack(Blocks.PLANKS, 1, 4 + PlanksBlock.WoodType.DARK_OAK.getId() - 4)
		);
		this.registerShapedRecipe(new ItemStack(Items.FISHING_ROD, 1), "  #", " #X", "# X", '#', Items.STICK, 'X', Items.STRING);
		this.registerShapedRecipe(new ItemStack(Items.CARROT_ON_A_STICK, 1), "# ", " X", '#', Items.FISHING_ROD, 'X', Items.CARROT);
		this.registerShapedRecipe(new ItemStack(Blocks.STONE_STAIRS, 4), "#  ", "## ", "###", '#', Blocks.COBBLESTONE);
		this.registerShapedRecipe(new ItemStack(Blocks.BRICK_STAIRS, 4), "#  ", "## ", "###", '#', Blocks.BRICKS);
		this.registerShapedRecipe(new ItemStack(Blocks.STONE_BRICK_STAIRS, 4), "#  ", "## ", "###", '#', Blocks.STONE_BRICKS);
		this.registerShapedRecipe(new ItemStack(Blocks.NETHER_BRICK_STAIRS, 4), "#  ", "## ", "###", '#', Blocks.NETHER_BRICKS);
		this.registerShapedRecipe(new ItemStack(Blocks.SANDSTONE_STAIRS, 4), "#  ", "## ", "###", '#', Blocks.SANDSTONE);
		this.registerShapedRecipe(new ItemStack(Blocks.RED_SANDSTONE_STAIRS, 4), "#  ", "## ", "###", '#', Blocks.RED_SANDSTONE);
		this.registerShapedRecipe(new ItemStack(Blocks.QUARTZ_STAIRS, 4), "#  ", "## ", "###", '#', Blocks.QUARTZ_BLOCK);
		this.registerShapedRecipe(new ItemStack(Items.PAINTING, 1), "###", "#X#", "###", '#', Items.STICK, 'X', Blocks.WOOL);
		this.registerShapedRecipe(new ItemStack(Items.ITEM_FRAME, 1), "###", "#X#", "###", '#', Items.STICK, 'X', Items.LEATHER);
		this.registerShapedRecipe(new ItemStack(Items.GOLDEN_APPLE), "###", "#X#", "###", '#', Items.GOLD_INGOT, 'X', Items.APPLE);
		this.registerShapedRecipe(new ItemStack(Items.GOLDEN_CARROT), "###", "#X#", "###", '#', Items.GOLD_NUGGET, 'X', Items.CARROT);
		this.registerShapedRecipe(new ItemStack(Items.GLISTERING_MELON, 1), "###", "#X#", "###", '#', Items.GOLD_NUGGET, 'X', Items.MELON);
		this.registerShapedRecipe(new ItemStack(Blocks.LEVER, 1), "X", "#", '#', Blocks.COBBLESTONE, 'X', Items.STICK);
		this.registerShapedRecipe(new ItemStack(Blocks.TRIPWIRE_HOOK, 2), "I", "S", "#", '#', Blocks.PLANKS, 'S', Items.STICK, 'I', Items.IRON_INGOT);
		this.registerShapedRecipe(new ItemStack(Blocks.REDSTONE_TORCH, 1), "X", "#", '#', Items.STICK, 'X', Items.REDSTONE);
		this.registerShapedRecipe(
			new ItemStack(Items.REPEATER, 1),
			"#X#",
			"III",
			'#',
			Blocks.REDSTONE_TORCH,
			'X',
			Items.REDSTONE,
			'I',
			new ItemStack(Blocks.STONE, 1, StoneBlock.StoneType.STONE.byId())
		);
		this.registerShapedRecipe(
			new ItemStack(Items.COMPARATOR, 1),
			" # ",
			"#X#",
			"III",
			'#',
			Blocks.REDSTONE_TORCH,
			'X',
			Items.QUARTZ,
			'I',
			new ItemStack(Blocks.STONE, 1, StoneBlock.StoneType.STONE.byId())
		);
		this.registerShapedRecipe(new ItemStack(Items.CLOCK, 1), " # ", "#X#", " # ", '#', Items.GOLD_INGOT, 'X', Items.REDSTONE);
		this.registerShapedRecipe(new ItemStack(Items.COMPASS, 1), " # ", "#X#", " # ", '#', Items.IRON_INGOT, 'X', Items.REDSTONE);
		this.registerShapedRecipe(new ItemStack(Items.MAP, 1), "###", "#X#", "###", '#', Items.PAPER, 'X', Items.COMPASS);
		this.registerShapedRecipe(new ItemStack(Blocks.STONE_BUTTON, 1), "#", '#', new ItemStack(Blocks.STONE, 1, StoneBlock.StoneType.STONE.byId()));
		this.registerShapedRecipe(new ItemStack(Blocks.WOODEN_BUTTON, 1), "#", '#', Blocks.PLANKS);
		this.registerShapedRecipe(new ItemStack(Blocks.STONE_PRESSURE_PLATE, 1), "##", '#', new ItemStack(Blocks.STONE, 1, StoneBlock.StoneType.STONE.byId()));
		this.registerShapedRecipe(new ItemStack(Blocks.WOODEN_PRESSURE_PLATE, 1), "##", '#', Blocks.PLANKS);
		this.registerShapedRecipe(new ItemStack(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, 1), "##", '#', Items.IRON_INGOT);
		this.registerShapedRecipe(new ItemStack(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, 1), "##", '#', Items.GOLD_INGOT);
		this.registerShapedRecipe(new ItemStack(Blocks.DISPENSER, 1), "###", "#X#", "#R#", '#', Blocks.COBBLESTONE, 'X', Items.BOW, 'R', Items.REDSTONE);
		this.registerShapedRecipe(new ItemStack(Blocks.DROPPER, 1), "###", "# #", "#R#", '#', Blocks.COBBLESTONE, 'R', Items.REDSTONE);
		this.registerShapedRecipe(
			new ItemStack(Blocks.PISTON, 1), "TTT", "#X#", "#R#", '#', Blocks.COBBLESTONE, 'X', Items.IRON_INGOT, 'R', Items.REDSTONE, 'T', Blocks.PLANKS
		);
		this.registerShapedRecipe(new ItemStack(Blocks.STICKY_PISTON, 1), "S", "P", 'S', Items.SLIME_BALL, 'P', Blocks.PISTON);
		this.registerShapedRecipe(new ItemStack(Items.BED, 1), "###", "XXX", '#', Blocks.WOOL, 'X', Blocks.PLANKS);
		this.registerShapedRecipe(new ItemStack(Blocks.ENCHANTING_TABLE, 1), " B ", "D#D", "###", '#', Blocks.OBSIDIAN, 'B', Items.BOOK, 'D', Items.DIAMOND);
		this.registerShapedRecipe(new ItemStack(Blocks.ANVIL, 1), "III", " i ", "iii", 'I', Blocks.IRON_BLOCK, 'i', Items.IRON_INGOT);
		this.registerShapedRecipe(new ItemStack(Items.LEATHER), "##", "##", '#', Items.RABBIT_HIDE);
		this.registerShapelessRecipe(new ItemStack(Items.EYE_OF_ENDER, 1), Items.ENDER_PEARL, Items.BLAZE_POWDER);
		this.registerShapelessRecipe(new ItemStack(Items.FIRE_CHARGE, 3), Items.GUNPOWDER, Items.BLAZE_POWDER, Items.COAL);
		this.registerShapelessRecipe(new ItemStack(Items.FIRE_CHARGE, 3), Items.GUNPOWDER, Items.BLAZE_POWDER, new ItemStack(Items.COAL, 1, 1));
		this.registerShapedRecipe(new ItemStack(Blocks.DAYLIGHT_DETECTOR), "GGG", "QQQ", "WWW", 'G', Blocks.GLASS, 'Q', Items.QUARTZ, 'W', Blocks.WOODEN_SLAB);
		this.registerShapedRecipe(new ItemStack(Items.END_CRYSTAL), "GGG", "GEG", "GTG", 'G', Blocks.GLASS, 'E', Items.EYE_OF_ENDER, 'T', Items.GHAST_TEAR);
		this.registerShapedRecipe(new ItemStack(Blocks.HOPPER), "I I", "ICI", " I ", 'I', Items.IRON_INGOT, 'C', Blocks.CHEST);
		this.registerShapedRecipe(
			new ItemStack(Items.ARMOR_STAND, 1), "///", " / ", "/_/", '/', Items.STICK, '_', new ItemStack(Blocks.STONE_SLAB, 1, StoneSlabBlock.SlabType.STONE.getId())
		);
		this.registerShapedRecipe(new ItemStack(Blocks.END_ROD, 4), "/", "#", '/', Items.BLAZE_ROD, '#', Items.CHORUS_FRUIT_POPPED);
		Collections.sort(this.recipes, new Comparator<RecipeType>() {
			public int compare(RecipeType recipeType, RecipeType recipeType2) {
				if (recipeType instanceof ShapelessRecipeType && recipeType2 instanceof ShapedRecipeType) {
					return 1;
				} else if (recipeType2 instanceof ShapelessRecipeType && recipeType instanceof ShapedRecipeType) {
					return -1;
				} else if (recipeType2.getSize() < recipeType.getSize()) {
					return -1;
				} else {
					return recipeType2.getSize() > recipeType.getSize() ? 1 : 0;
				}
			}
		});
	}

	public ShapedRecipeType registerShapedRecipe(ItemStack stack, Object... args) {
		String string = "";
		int i = 0;
		int j = 0;
		int k = 0;
		if (args[i] instanceof String[]) {
			String[] strings = (String[])args[i++];

			for (int l = 0; l < strings.length; l++) {
				String string2 = strings[l];
				k++;
				j = string2.length();
				string = string + string2;
			}
		} else {
			while (args[i] instanceof String) {
				String string3 = (String)args[i++];
				k++;
				j = string3.length();
				string = string + string3;
			}
		}

		Map<Character, ItemStack> map;
		for (map = Maps.newHashMap(); i < args.length; i += 2) {
			Character character = (Character)args[i];
			ItemStack itemStack = null;
			if (args[i + 1] instanceof Item) {
				itemStack = new ItemStack((Item)args[i + 1]);
			} else if (args[i + 1] instanceof Block) {
				itemStack = new ItemStack((Block)args[i + 1], 1, 32767);
			} else if (args[i + 1] instanceof ItemStack) {
				itemStack = (ItemStack)args[i + 1];
			}

			map.put(character, itemStack);
		}

		ItemStack[] itemStacks = new ItemStack[j * k];

		for (int m = 0; m < j * k; m++) {
			char c = string.charAt(m);
			if (map.containsKey(c)) {
				itemStacks[m] = ((ItemStack)map.get(c)).copy();
			} else {
				itemStacks[m] = null;
			}
		}

		ShapedRecipeType shapedRecipeType = new ShapedRecipeType(j, k, itemStacks, stack);
		this.recipes.add(shapedRecipeType);
		return shapedRecipeType;
	}

	public void registerShapelessRecipe(ItemStack result, Object... args) {
		List<ItemStack> list = Lists.newArrayList();

		for (Object object : args) {
			if (object instanceof ItemStack) {
				list.add(((ItemStack)object).copy());
			} else if (object instanceof Item) {
				list.add(new ItemStack((Item)object));
			} else {
				if (!(object instanceof Block)) {
					throw new IllegalArgumentException("Invalid shapeless recipe: unknown type " + object.getClass().getName() + "!");
				}

				list.add(new ItemStack((Block)object));
			}
		}

		this.recipes.add(new ShapelessRecipeType(result, list));
	}

	public void addRecipeType(RecipeType recipe) {
		this.recipes.add(recipe);
	}

	@Nullable
	public ItemStack matches(CraftingInventory inventory, World world) {
		for (RecipeType recipeType : this.recipes) {
			if (recipeType.matches(inventory, world)) {
				return recipeType.getResult(inventory);
			}
		}

		return null;
	}

	public ItemStack[] getRemainders(CraftingInventory inventory, World world) {
		for (RecipeType recipeType : this.recipes) {
			if (recipeType.matches(inventory, world)) {
				return recipeType.getRemainders(inventory);
			}
		}

		ItemStack[] itemStacks = new ItemStack[inventory.getInvSize()];

		for (int i = 0; i < itemStacks.length; i++) {
			itemStacks[i] = inventory.getInvStack(i);
		}

		return itemStacks;
	}

	public List<RecipeType> getAllRecipes() {
		return this.recipes;
	}
}
