package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public enum BannerPattern {
	BASE("base", "b"),
	SQUARE_BOTTOM_LEFT("square_bottom_left", "bl", "   ", "   ", "#  "),
	SQUARE_BOTTOM_RIGHT("square_bottom_right", "br", "   ", "   ", "  #"),
	SQUARE_TOP_LEFT("square_top_left", "tl", "#  ", "   ", "   "),
	SQUARE_TOP_RIGHT("square_top_right", "tr", "  #", "   ", "   "),
	STRIPE_BOTTOM("stripe_bottom", "bs", "   ", "   ", "###"),
	STRIPE_TOP("stripe_top", "ts", "###", "   ", "   "),
	STRIPE_LEFT("stripe_left", "ls", "#  ", "#  ", "#  "),
	STRIPE_RIGHT("stripe_right", "rs", "  #", "  #", "  #"),
	STRIPE_CENTER("stripe_center", "cs", " # ", " # ", " # "),
	STRIPE_MIDDLE("stripe_middle", "ms", "   ", "###", "   "),
	STRIPE_DOWNRIGHT("stripe_downright", "drs", "#  ", " # ", "  #"),
	STRIPE_DOWNLEFT("stripe_downleft", "dls", "  #", " # ", "#  "),
	STRIPE_SMALL("small_stripes", "ss", "# #", "# #", "   "),
	CROSS("cross", "cr", "# #", " # ", "# #"),
	STRAIGHT_CROSS("straight_cross", "sc", " # ", "###", " # "),
	TRIANGLE_BOTTOM("triangle_bottom", "bt", "   ", " # ", "# #"),
	TRIANGLE_TOP("triangle_top", "tt", "# #", " # ", "   "),
	TRIANGLES_BOTTOM("triangles_bottom", "bts", "   ", "# #", " # "),
	TRIANGLES_TOP("triangles_top", "tts", " # ", "# #", "   "),
	DIAGONAL_LEFT("diagonal_left", "ld", "## ", "#  ", "   "),
	DIAGONAL_RIGHT("diagonal_up_right", "rd", "   ", "  #", " ##"),
	DIAGONAL_LEFT_MIRROR("diagonal_up_left", "lud", "   ", "#  ", "## "),
	DIAGONAL_RIGHT_MIRROR("diagonal_right", "rud", " ##", "  #", "   "),
	CIRCLE_MIDDLE("circle", "mc", "   ", " # ", "   "),
	RHOMBUS_MIDDLE("rhombus", "mr", " # ", "# #", " # "),
	HALF_VERTICAL("half_vertical", "vh", "## ", "## ", "## "),
	HALF_HORIZONTAL("half_horizontal", "hh", "###", "###", "   "),
	HALF_VERTICAL_MIRROR("half_vertical_right", "vhr", " ##", " ##", " ##"),
	HALF_HORIZONTAL_MIRROR("half_horizontal_bottom", "hhb", "   ", "###", "###"),
	BORDER("border", "bo", "###", "# #", "###"),
	CURLY_BORDER("curly_border", "cbo", new ItemStack(Blocks.VINE)),
	CREEPER("creeper", "cre", new ItemStack(Items.CREEPER_HEAD)),
	GRADIENT("gradient", "gra", "# #", " # ", " # "),
	GRADIENT_UP("gradient_up", "gru", " # ", " # ", "# #"),
	BRICKS("bricks", "bri", new ItemStack(Blocks.BRICKS)),
	SKULL("skull", "sku", new ItemStack(Items.WITHER_SKELETON_SKULL)),
	FLOWER("flower", "flo", new ItemStack(Blocks.OXEYE_DAISY)),
	MOJANG("mojang", "moj", new ItemStack(Items.ENCHANTED_GOLDEN_APPLE));

	private final String name;
	private final String id;
	private final String[] recipeString = new String[3];
	private ItemStack recipeItem = ItemStack.EMPTY;

	private BannerPattern(String string2, String string3) {
		this.name = string2;
		this.id = string3;
	}

	private BannerPattern(String string2, String string3, ItemStack itemStack) {
		this(string2, string3);
		this.recipeItem = itemStack;
	}

	private BannerPattern(String string2, String string3, String string4, String string5, String string6) {
		this(string2, string3);
		this.recipeString[0] = string4;
		this.recipeString[1] = string5;
		this.recipeString[2] = string6;
	}

	public String getName() {
		return this.name;
	}

	public String getId() {
		return this.id;
	}

	public String[] getRecipeString() {
		return this.recipeString;
	}

	public boolean hasRecipeOrItem() {
		return !this.recipeItem.isEmpty() || this.recipeString[0] != null;
	}

	public boolean hasRecipeItem() {
		return !this.recipeItem.isEmpty();
	}

	public ItemStack getRecipeItem() {
		return this.recipeItem;
	}

	@Nullable
	public static BannerPattern getById(String id) {
		for (BannerPattern bannerPattern : values()) {
			if (bannerPattern.id.equals(id)) {
				return bannerPattern;
			}
		}

		return null;
	}
}
