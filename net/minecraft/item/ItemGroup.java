package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.registry.Registry;

public abstract class ItemGroup {
	public static final ItemGroup[] GROUPS = new ItemGroup[12];
	public static final ItemGroup BUILDING_BLOCKS = (new ItemGroup(0, "buildingBlocks") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Blocks.field_10104);
		}
	}).setName("building_blocks");
	public static final ItemGroup DECORATIONS = new ItemGroup(1, "decorations") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Blocks.field_10003);
		}
	};
	public static final ItemGroup REDSTONE = new ItemGroup(2, "redstone") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Items.field_8725);
		}
	};
	public static final ItemGroup TRANSPORTATION = new ItemGroup(3, "transportation") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Blocks.field_10425);
		}
	};
	public static final ItemGroup MISC = new ItemGroup(6, "misc") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Items.field_8187);
		}
	};
	public static final ItemGroup SEARCH = (new ItemGroup(5, "search") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Items.field_8251);
		}
	}).setTexture("item_search.png");
	public static final ItemGroup FOOD = new ItemGroup(7, "food") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Items.field_8279);
		}
	};
	public static final ItemGroup TOOLS = (new ItemGroup(8, "tools") {
			@Override
			public ItemStack createIcon() {
				return new ItemStack(Items.field_8475);
			}
		})
		.setEnchantments(
			new EnchantmentTarget[]{EnchantmentTarget.field_9075, EnchantmentTarget.field_9069, EnchantmentTarget.field_9072, EnchantmentTarget.field_9082}
		);
	public static final ItemGroup COMBAT = (new ItemGroup(9, "combat") {
			@Override
			public ItemStack createIcon() {
				return new ItemStack(Items.field_8845);
			}
		})
		.setEnchantments(
			new EnchantmentTarget[]{
				EnchantmentTarget.field_9075,
				EnchantmentTarget.field_9068,
				EnchantmentTarget.field_9079,
				EnchantmentTarget.field_9080,
				EnchantmentTarget.field_9076,
				EnchantmentTarget.field_9071,
				EnchantmentTarget.field_9070,
				EnchantmentTarget.field_9074,
				EnchantmentTarget.field_9078,
				EnchantmentTarget.field_9082,
				EnchantmentTarget.field_9073,
				EnchantmentTarget.field_9081
			}
		);
	public static final ItemGroup BREWING = new ItemGroup(10, "brewing") {
		@Override
		public ItemStack createIcon() {
			return PotionUtil.setPotion(new ItemStack(Items.field_8574), Potions.field_8991);
		}
	};
	public static final ItemGroup MATERIALS = MISC;
	public static final ItemGroup HOTBAR = new ItemGroup(4, "hotbar") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Blocks.field_10504);
		}

		@Override
		public void appendStacks(DefaultedList<ItemStack> defaultedList) {
			throw new RuntimeException("Implement exception client-side.");
		}

		@Override
		public boolean isSpecial() {
			return true;
		}
	};
	public static final ItemGroup INVENTORY = (new ItemGroup(11, "inventory") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Blocks.field_10034);
		}
	}).setTexture("inventory.png").setNoScrollbar().setNoTooltip();
	private final int index;
	private final String id;
	private String name;
	private String texture = "items.png";
	private boolean scrollbar = true;
	private boolean tooltip = true;
	private EnchantmentTarget[] enchantments = new EnchantmentTarget[0];
	private ItemStack icon;

	public ItemGroup(int i, String string) {
		this.index = i;
		this.id = string;
		this.icon = ItemStack.EMPTY;
		GROUPS[i] = this;
	}

	public int getIndex() {
		return this.index;
	}

	public String getId() {
		return this.id;
	}

	public String getName() {
		return this.name == null ? this.id : this.name;
	}

	public String getTranslationKey() {
		return "itemGroup." + this.getId();
	}

	public ItemStack getIcon() {
		if (this.icon.isEmpty()) {
			this.icon = this.createIcon();
		}

		return this.icon;
	}

	public abstract ItemStack createIcon();

	public String getTexture() {
		return this.texture;
	}

	public ItemGroup setTexture(String string) {
		this.texture = string;
		return this;
	}

	public ItemGroup setName(String string) {
		this.name = string;
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

	public boolean isSpecial() {
		return this.getColumn() == 5;
	}

	public EnchantmentTarget[] getEnchantments() {
		return this.enchantments;
	}

	public ItemGroup setEnchantments(EnchantmentTarget... enchantmentTargets) {
		this.enchantments = enchantmentTargets;
		return this;
	}

	public boolean containsEnchantments(@Nullable EnchantmentTarget enchantmentTarget) {
		if (enchantmentTarget != null) {
			for (EnchantmentTarget enchantmentTarget2 : this.enchantments) {
				if (enchantmentTarget2 == enchantmentTarget) {
					return true;
				}
			}
		}

		return false;
	}

	public void appendStacks(DefaultedList<ItemStack> defaultedList) {
		for (Item item : Registry.ITEM) {
			item.appendStacks(this, defaultedList);
		}
	}
}
