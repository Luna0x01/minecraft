package net.minecraft.item.itemgroup;

import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.util.collection.DefaultedList;

public abstract class ItemGroup {
	public static final ItemGroup[] itemGroups = new ItemGroup[12];
	public static final ItemGroup BUILDING_BLOCKS = new ItemGroup(0, "buildingBlocks") {
		@Override
		public ItemStack method_13647() {
			return new ItemStack(Item.fromBlock(Blocks.BRICKS));
		}
	};
	public static final ItemGroup DECORATIONS = new ItemGroup(1, "decorations") {
		@Override
		public ItemStack method_13647() {
			return new ItemStack(Item.fromBlock(Blocks.DOUBLE_PLANT), 1, DoublePlantBlock.DoublePlantType.PAEONIA.getId());
		}
	};
	public static final ItemGroup REDSTONE = new ItemGroup(2, "redstone") {
		@Override
		public ItemStack method_13647() {
			return new ItemStack(Items.REDSTONE);
		}
	};
	public static final ItemGroup TRANSPORTATION = new ItemGroup(3, "transportation") {
		@Override
		public ItemStack method_13647() {
			return new ItemStack(Item.fromBlock(Blocks.POWERED_RAIL));
		}
	};
	public static final ItemGroup MISC = new ItemGroup(4, "misc") {
		@Override
		public ItemStack method_13647() {
			return new ItemStack(Items.LAVA_BUCKET);
		}
	};
	public static final ItemGroup SEARCH = (new ItemGroup(5, "search") {
		@Override
		public ItemStack method_13647() {
			return new ItemStack(Items.COMPASS);
		}
	}).setTexture("item_search.png");
	public static final ItemGroup FOOD = new ItemGroup(6, "food") {
		@Override
		public ItemStack method_13647() {
			return new ItemStack(Items.APPLE);
		}
	};
	public static final ItemGroup TOOLS = (new ItemGroup(7, "tools") {
		@Override
		public ItemStack method_13647() {
			return new ItemStack(Items.IRON_AXE);
		}
	}).setEnchantments(new EnchantmentTarget[]{EnchantmentTarget.ALL, EnchantmentTarget.DIGGER, EnchantmentTarget.FISHING_ROD, EnchantmentTarget.BREAKABLE});
	public static final ItemGroup COMBAT = (new ItemGroup(8, "combat") {
			@Override
			public ItemStack method_13647() {
				return new ItemStack(Items.GOLDEN_SWORD);
			}
		})
		.setEnchantments(
			new EnchantmentTarget[]{
				EnchantmentTarget.ALL,
				EnchantmentTarget.ALL_ARMOR,
				EnchantmentTarget.FEET,
				EnchantmentTarget.HEAD,
				EnchantmentTarget.LEGS,
				EnchantmentTarget.ARMOR_CHEST,
				EnchantmentTarget.BOW,
				EnchantmentTarget.WEAPON,
				EnchantmentTarget.WEARABLE,
				EnchantmentTarget.BREAKABLE
			}
		);
	public static final ItemGroup BREWING = new ItemGroup(9, "brewing") {
		@Override
		public ItemStack method_13647() {
			return PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER);
		}
	};
	public static final ItemGroup MATERIALS = new ItemGroup(10, "materials") {
		@Override
		public ItemStack method_13647() {
			return new ItemStack(Items.STICK);
		}
	};
	public static final ItemGroup INVENTORY = (new ItemGroup(11, "inventory") {
		@Override
		public ItemStack method_13647() {
			return new ItemStack(Item.fromBlock(Blocks.CHEST));
		}
	}).setTexture("inventory.png").setNoScrollbar().setNoTooltip();
	private final int index;
	private final String id;
	private String texture = "items.png";
	private boolean scrollbar = true;
	private boolean tooltip = true;
	private EnchantmentTarget[] targets;
	private ItemStack item;

	public ItemGroup(int i, String string) {
		this.index = i;
		this.id = string;
		this.item = ItemStack.EMPTY;
		itemGroups[i] = this;
	}

	public int getIndex() {
		return this.index;
	}

	public String getId() {
		return this.id;
	}

	public String getTranslationKey() {
		return "itemGroup." + this.getId();
	}

	public ItemStack getIcon() {
		if (this.item.isEmpty()) {
			this.item = this.method_13647();
		}

		return this.item;
	}

	public abstract ItemStack method_13647();

	public String getTexture() {
		return this.texture;
	}

	public ItemGroup setTexture(String texture) {
		this.texture = texture;
		return this;
	}

	public boolean hasTooltip() {
		return this.tooltip;
	}

	public ItemGroup setNoTooltip() {
		this.tooltip = false;
		return this;
	}

	public boolean hasScrollbar() {
		return this.scrollbar;
	}

	public ItemGroup setNoScrollbar() {
		this.scrollbar = false;
		return this;
	}

	public int getColumn() {
		return this.index % 6;
	}

	public boolean isTopRow() {
		return this.index < 6;
	}

	public EnchantmentTarget[] getEnchantments() {
		return this.targets;
	}

	public ItemGroup setEnchantments(EnchantmentTarget... targets) {
		this.targets = targets;
		return this;
	}

	public boolean containsEnchantments(EnchantmentTarget target) {
		if (this.targets == null) {
			return false;
		} else {
			for (EnchantmentTarget enchantmentTarget : this.targets) {
				if (enchantmentTarget == target) {
					return true;
				}
			}

			return false;
		}
	}

	public void method_13646(DefaultedList<ItemStack> defaultedList) {
		for (Item item : Item.REGISTRY) {
			if (item != null && item.getItemGroup() == this) {
				item.method_13648(item, this, defaultedList);
			}
		}

		if (this.getEnchantments() != null) {
			this.showBooks(defaultedList, this.getEnchantments());
		}
	}

	public void showBooks(List<ItemStack> stacks, EnchantmentTarget... targets) {
		for (Enchantment enchantment : Enchantment.REGISTRY) {
			if (enchantment != null && enchantment.target != null) {
				boolean bl = false;

				for (int i = 0; i < targets.length && !bl; i++) {
					if (enchantment.target == targets[i]) {
						bl = true;
					}
				}

				if (bl) {
					stacks.add(Items.ENCHANTED_BOOK.getAsItemStack(new EnchantmentLevelEntry(enchantment, enchantment.getMaximumLevel())));
				}
			}
		}
	}
}
