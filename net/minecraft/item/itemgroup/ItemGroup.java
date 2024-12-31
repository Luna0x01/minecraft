package net.minecraft.item.itemgroup;

import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

public abstract class ItemGroup {
	public static final ItemGroup[] itemGroups = new ItemGroup[12];
	public static final ItemGroup BUILDING_BLOCKS = (new ItemGroup(0, "buildingBlocks") {
		@Override
		public ItemStack method_13647() {
			return new ItemStack(Blocks.BRICKS);
		}
	}).method_16033("building_blocks");
	public static final ItemGroup DECORATIONS = new ItemGroup(1, "decorations") {
		@Override
		public ItemStack method_13647() {
			return new ItemStack(Blocks.PEONY);
		}
	};
	public static final ItemGroup REDSTONE = new ItemGroup(2, "redstone") {
		@Override
		public ItemStack method_13647() {
			return new ItemStack(Items.REDSTONE);
		}
	};
	public static final ItemGroup field_17160 = new ItemGroup(3, "transportation") {
		@Override
		public ItemStack method_13647() {
			return new ItemStack(Blocks.POWERED_RAIL);
		}
	};
	public static final ItemGroup MISC = new ItemGroup(6, "misc") {
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
	public static final ItemGroup FOOD = new ItemGroup(7, "food") {
		@Override
		public ItemStack method_13647() {
			return new ItemStack(Items.APPLE);
		}
	};
	public static final ItemGroup TOOLS = (new ItemGroup(8, "tools") {
		@Override
		public ItemStack method_13647() {
			return new ItemStack(Items.IRON_AXE);
		}
	}).setEnchantments(new EnchantmentTarget[]{EnchantmentTarget.ALL, EnchantmentTarget.DIGGER, EnchantmentTarget.FISHING_ROD, EnchantmentTarget.BREAKABLE});
	public static final ItemGroup COMBAT = (new ItemGroup(9, "combat") {
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
				EnchantmentTarget.BREAKABLE,
				EnchantmentTarget.TRIDENT
			}
		);
	public static final ItemGroup BREWING = new ItemGroup(10, "brewing") {
		@Override
		public ItemStack method_13647() {
			return PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER);
		}
	};
	public static final ItemGroup MATERIALS = MISC;
	public static final ItemGroup field_15657 = new ItemGroup(4, "hotbar") {
		@Override
		public ItemStack method_13647() {
			return new ItemStack(Blocks.BOOKSHELF);
		}

		@Override
		public void method_13646(DefaultedList<ItemStack> defaultedList) {
			throw new RuntimeException("Implement exception client-side.");
		}

		@Override
		public boolean method_14220() {
			return true;
		}
	};
	public static final ItemGroup INVENTORY = (new ItemGroup(11, "inventory") {
		@Override
		public ItemStack method_13647() {
			return new ItemStack(Blocks.CHEST);
		}
	}).setTexture("inventory.png").setNoScrollbar().setNoTooltip();
	private final int index;
	private final String id;
	private String field_17161;
	private String texture = "items.png";
	private boolean scrollbar = true;
	private boolean tooltip = true;
	private EnchantmentTarget[] targets = new EnchantmentTarget[0];
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

	public String method_16034() {
		return this.field_17161 == null ? this.id : this.field_17161;
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

	public ItemGroup method_16033(String string) {
		this.field_17161 = string;
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

	public boolean method_14220() {
		return this.getColumn() == 5;
	}

	public EnchantmentTarget[] getEnchantments() {
		return this.targets;
	}

	public ItemGroup setEnchantments(EnchantmentTarget... targets) {
		this.targets = targets;
		return this;
	}

	public boolean containsEnchantments(@Nullable EnchantmentTarget target) {
		if (target != null) {
			for (EnchantmentTarget enchantmentTarget : this.targets) {
				if (enchantmentTarget == target) {
					return true;
				}
			}
		}

		return false;
	}

	public void method_13646(DefaultedList<ItemStack> defaultedList) {
		for (Item item : Registry.ITEM) {
			item.appendToItemGroup(this, defaultedList);
		}
	}
}
