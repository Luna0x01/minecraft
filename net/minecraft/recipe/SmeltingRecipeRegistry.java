package net.minecraft.recipe;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.StoneBrickBlock;
import net.minecraft.item.FishItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DyeColor;

public class SmeltingRecipeRegistry {
	private static final SmeltingRecipeRegistry INSTANCE = new SmeltingRecipeRegistry();
	private final Map<ItemStack, ItemStack> ORIGINAL_PRODUCT_MAP = Maps.newHashMap();
	private final Map<ItemStack, Float> PRODUCT_XP_MAP = Maps.newHashMap();

	public static SmeltingRecipeRegistry getInstance() {
		return INSTANCE;
	}

	private SmeltingRecipeRegistry() {
		this.addBlock(Blocks.IRON_ORE, new ItemStack(Items.IRON_INGOT), 0.7F);
		this.addBlock(Blocks.GOLD_ORE, new ItemStack(Items.GOLD_INGOT), 1.0F);
		this.addBlock(Blocks.DIAMOND_ORE, new ItemStack(Items.DIAMOND), 1.0F);
		this.addBlock(Blocks.SAND, new ItemStack(Blocks.GLASS), 0.1F);
		this.addItem(Items.RAW_PORKCHOP, new ItemStack(Items.COOKED_PORKCHOP), 0.35F);
		this.addItem(Items.BEEF, new ItemStack(Items.COOKED_BEEF), 0.35F);
		this.addItem(Items.CHICKEN, new ItemStack(Items.COOKED_CHICKEN), 0.35F);
		this.addItem(Items.RAW_RABBIT, new ItemStack(Items.COOKED_RABBIT), 0.35F);
		this.addItem(Items.MUTTON, new ItemStack(Items.COOKED_MUTTON), 0.35F);
		this.addBlock(Blocks.COBBLESTONE, new ItemStack(Blocks.STONE), 0.1F);
		this.addItemStack(new ItemStack(Blocks.STONE_BRICKS, 1, StoneBrickBlock.DEFAULT_ID), new ItemStack(Blocks.STONE_BRICKS, 1, StoneBrickBlock.CRACKED_ID), 0.1F);
		this.addItem(Items.CLAY_BALL, new ItemStack(Items.BRICK), 0.3F);
		this.addBlock(Blocks.CLAY, new ItemStack(Blocks.TERRACOTTA), 0.35F);
		this.addBlock(Blocks.CACTUS, new ItemStack(Items.DYE, 1, DyeColor.GREEN.getSwappedId()), 0.2F);
		this.addBlock(Blocks.LOG, new ItemStack(Items.COAL, 1, 1), 0.15F);
		this.addBlock(Blocks.LOG2, new ItemStack(Items.COAL, 1, 1), 0.15F);
		this.addBlock(Blocks.EMERALD_ORE, new ItemStack(Items.EMERALD), 1.0F);
		this.addItem(Items.POTATO, new ItemStack(Items.BAKED_POTATO), 0.35F);
		this.addBlock(Blocks.NETHERRACK, new ItemStack(Items.NETHERBRICK), 0.1F);
		this.addItemStack(new ItemStack(Blocks.SPONGE, 1, 1), new ItemStack(Blocks.SPONGE, 1, 0), 0.15F);
		this.addItem(Items.CHORUS_FRUIT, new ItemStack(Items.CHORUS_FRUIT_POPPED), 0.1F);

		for (FishItem.FishType fishType : FishItem.FishType.values()) {
			if (fishType.canBeCooked()) {
				this.addItemStack(new ItemStack(Items.RAW_FISH, 1, fishType.getId()), new ItemStack(Items.COOKED_FISH, 1, fishType.getId()), 0.35F);
			}
		}

		this.addBlock(Blocks.COAL_ORE, new ItemStack(Items.COAL), 0.1F);
		this.addBlock(Blocks.REDSTONE_ORE, new ItemStack(Items.REDSTONE), 0.7F);
		this.addBlock(Blocks.LAPIS_LAZULI_ORE, new ItemStack(Items.DYE, 1, DyeColor.BLUE.getSwappedId()), 0.2F);
		this.addBlock(Blocks.NETHER_QUARTZ_ORE, new ItemStack(Items.QUARTZ), 0.2F);
		this.addItem(Items.CHAINMAIL_HELMET, new ItemStack(Items.IRON_NUGGET), 0.1F);
		this.addItem(Items.CHAINMAIL_CHESTPLATE, new ItemStack(Items.IRON_NUGGET), 0.1F);
		this.addItem(Items.CHAINMAIL_LEGGINGS, new ItemStack(Items.IRON_NUGGET), 0.1F);
		this.addItem(Items.CHAINMAIL_BOOTS, new ItemStack(Items.IRON_NUGGET), 0.1F);
		this.addItem(Items.IRON_PICKAXE, new ItemStack(Items.IRON_NUGGET), 0.1F);
		this.addItem(Items.IRON_SHOVEL, new ItemStack(Items.IRON_NUGGET), 0.1F);
		this.addItem(Items.IRON_AXE, new ItemStack(Items.IRON_NUGGET), 0.1F);
		this.addItem(Items.IRON_HOE, new ItemStack(Items.IRON_NUGGET), 0.1F);
		this.addItem(Items.IRON_SWORD, new ItemStack(Items.IRON_NUGGET), 0.1F);
		this.addItem(Items.IRON_HELMET, new ItemStack(Items.IRON_NUGGET), 0.1F);
		this.addItem(Items.IRON_CHESTPLATE, new ItemStack(Items.IRON_NUGGET), 0.1F);
		this.addItem(Items.IRON_LEGGINGS, new ItemStack(Items.IRON_NUGGET), 0.1F);
		this.addItem(Items.IRON_BOOTS, new ItemStack(Items.IRON_NUGGET), 0.1F);
		this.addItem(Items.IRON_HORSE_ARMOR, new ItemStack(Items.IRON_NUGGET), 0.1F);
		this.addItem(Items.GOLDEN_PICKAXE, new ItemStack(Items.GOLD_NUGGET), 0.1F);
		this.addItem(Items.GOLDEN_SHOVEL, new ItemStack(Items.GOLD_NUGGET), 0.1F);
		this.addItem(Items.GOLDEN_AXE, new ItemStack(Items.GOLD_NUGGET), 0.1F);
		this.addItem(Items.GOLDEN_HOE, new ItemStack(Items.GOLD_NUGGET), 0.1F);
		this.addItem(Items.GOLDEN_SWORD, new ItemStack(Items.GOLD_NUGGET), 0.1F);
		this.addItem(Items.GOLDEN_HELMET, new ItemStack(Items.GOLD_NUGGET), 0.1F);
		this.addItem(Items.GOLDEN_CHESTPLATE, new ItemStack(Items.GOLD_NUGGET), 0.1F);
		this.addItem(Items.GOLDEN_LEGGINGS, new ItemStack(Items.GOLD_NUGGET), 0.1F);
		this.addItem(Items.GOLDEN_BOOTS, new ItemStack(Items.GOLD_NUGGET), 0.1F);
		this.addItem(Items.GOLDEN_HORSE_ARMOR, new ItemStack(Items.GOLD_NUGGET), 0.1F);
		this.addItemStack(new ItemStack(Blocks.STAINED_TERRACOTTA, 1, DyeColor.WHITE.getId()), new ItemStack(Blocks.WHITE_GLAZED_TERRACOTTA), 0.1F);
		this.addItemStack(new ItemStack(Blocks.STAINED_TERRACOTTA, 1, DyeColor.ORANGE.getId()), new ItemStack(Blocks.ORANGE_GLAZED_TERRACOTTA), 0.1F);
		this.addItemStack(new ItemStack(Blocks.STAINED_TERRACOTTA, 1, DyeColor.MAGENTA.getId()), new ItemStack(Blocks.MAGENTA_GLAZED_TERRACOTTA), 0.1F);
		this.addItemStack(new ItemStack(Blocks.STAINED_TERRACOTTA, 1, DyeColor.LIGHT_BLUE.getId()), new ItemStack(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA), 0.1F);
		this.addItemStack(new ItemStack(Blocks.STAINED_TERRACOTTA, 1, DyeColor.YELLOW.getId()), new ItemStack(Blocks.YELLOW_GLAZED_TERRACOTTA), 0.1F);
		this.addItemStack(new ItemStack(Blocks.STAINED_TERRACOTTA, 1, DyeColor.LIME.getId()), new ItemStack(Blocks.LIME_GLAZED_TERRACOTTA), 0.1F);
		this.addItemStack(new ItemStack(Blocks.STAINED_TERRACOTTA, 1, DyeColor.PINK.getId()), new ItemStack(Blocks.PINK_GLAZED_TERRACOTTA), 0.1F);
		this.addItemStack(new ItemStack(Blocks.STAINED_TERRACOTTA, 1, DyeColor.GRAY.getId()), new ItemStack(Blocks.GRAY_GLAZED_TERRACOTTA), 0.1F);
		this.addItemStack(new ItemStack(Blocks.STAINED_TERRACOTTA, 1, DyeColor.SILVER.getId()), new ItemStack(Blocks.SILVER_GLAZED_TERRACOTTA), 0.1F);
		this.addItemStack(new ItemStack(Blocks.STAINED_TERRACOTTA, 1, DyeColor.CYAN.getId()), new ItemStack(Blocks.CYAN_GLAZED_TERRACOTTA), 0.1F);
		this.addItemStack(new ItemStack(Blocks.STAINED_TERRACOTTA, 1, DyeColor.PURPLE.getId()), new ItemStack(Blocks.PURPLE_GLAZED_TERRACOTTA), 0.1F);
		this.addItemStack(new ItemStack(Blocks.STAINED_TERRACOTTA, 1, DyeColor.BLUE.getId()), new ItemStack(Blocks.BLUE_GLAZED_TERRACOTTA), 0.1F);
		this.addItemStack(new ItemStack(Blocks.STAINED_TERRACOTTA, 1, DyeColor.BROWN.getId()), new ItemStack(Blocks.BROWN_GLAZED_TERRACOTTA), 0.1F);
		this.addItemStack(new ItemStack(Blocks.STAINED_TERRACOTTA, 1, DyeColor.GREEN.getId()), new ItemStack(Blocks.GREEN_GLAZED_TERRACOTTA), 0.1F);
		this.addItemStack(new ItemStack(Blocks.STAINED_TERRACOTTA, 1, DyeColor.RED.getId()), new ItemStack(Blocks.RED_GLAZED_TERRACOTTA), 0.1F);
		this.addItemStack(new ItemStack(Blocks.STAINED_TERRACOTTA, 1, DyeColor.BLACK.getId()), new ItemStack(Blocks.BLACK_GLAZED_TERRACOTTA), 0.1F);
	}

	public void addBlock(Block block, ItemStack stack, float xp) {
		this.addItem(Item.fromBlock(block), stack, xp);
	}

	public void addItem(Item item, ItemStack stack, float xp) {
		this.addItemStack(new ItemStack(item, 1, 32767), stack, xp);
	}

	public void addItemStack(ItemStack original, ItemStack product, float xp) {
		this.ORIGINAL_PRODUCT_MAP.put(original, product);
		this.PRODUCT_XP_MAP.put(product, xp);
	}

	public ItemStack getResult(ItemStack stack) {
		for (Entry<ItemStack, ItemStack> entry : this.ORIGINAL_PRODUCT_MAP.entrySet()) {
			if (this.stackEquals(stack, (ItemStack)entry.getKey())) {
				return (ItemStack)entry.getValue();
			}
		}

		return ItemStack.EMPTY;
	}

	private boolean stackEquals(ItemStack stack1, ItemStack stack2) {
		return stack2.getItem() == stack1.getItem() && (stack2.getData() == 32767 || stack2.getData() == stack1.getData());
	}

	public Map<ItemStack, ItemStack> getRecipeMap() {
		return this.ORIGINAL_PRODUCT_MAP;
	}

	public float getXp(ItemStack stack) {
		for (Entry<ItemStack, Float> entry : this.PRODUCT_XP_MAP.entrySet()) {
			if (this.stackEquals(stack, (ItemStack)entry.getKey())) {
				return (Float)entry.getValue();
			}
		}

		return 0.0F;
	}
}
