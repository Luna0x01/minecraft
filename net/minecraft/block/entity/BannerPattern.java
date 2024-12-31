package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.tuple.Pair;

public enum BannerPattern {
	field_11834("base", "b"),
	field_11839("square_bottom_left", "bl", "   ", "   ", "#  "),
	field_11806("square_bottom_right", "br", "   ", "   ", "  #"),
	field_11831("square_top_left", "tl", "#  ", "   ", "   "),
	field_11848("square_top_right", "tr", "  #", "   ", "   "),
	field_11810("stripe_bottom", "bs", "   ", "   ", "###"),
	field_11829("stripe_top", "ts", "###", "   ", "   "),
	field_11837("stripe_left", "ls", "#  ", "#  ", "#  "),
	field_11813("stripe_right", "rs", "  #", "  #", "  #"),
	field_11819("stripe_center", "cs", " # ", " # ", " # "),
	field_11838("stripe_middle", "ms", "   ", "###", "   "),
	field_11807("stripe_downright", "drs", "#  ", " # ", "  #"),
	field_11820("stripe_downleft", "dls", "  #", " # ", "#  "),
	field_11814("small_stripes", "ss", "# #", "# #", "   "),
	field_11844("cross", "cr", "# #", " # ", "# #"),
	field_11830("straight_cross", "sc", " # ", "###", " # "),
	field_11811("triangle_bottom", "bt", "   ", " # ", "# #"),
	field_11849("triangle_top", "tt", "# #", " # ", "   "),
	field_11822("triangles_bottom", "bts", "   ", "# #", " # "),
	field_11815("triangles_top", "tts", " # ", "# #", "   "),
	field_11847("diagonal_left", "ld", "## ", "#  ", "   "),
	field_11835("diagonal_up_right", "rd", "   ", "  #", " ##"),
	field_11817("diagonal_up_left", "lud", "   ", "#  ", "## "),
	field_11842("diagonal_right", "rud", " ##", "  #", "   "),
	field_11826("circle", "mc", "   ", " # ", "   "),
	field_11821("rhombus", "mr", " # ", "# #", " # "),
	field_11828("half_vertical", "vh", "## ", "## ", "## "),
	field_11843("half_horizontal", "hh", "###", "###", "   "),
	field_11818("half_vertical_right", "vhr", " ##", " ##", " ##"),
	field_11836("half_horizontal_bottom", "hhb", "   ", "###", "###"),
	field_11840("border", "bo", "###", "# #", "###"),
	field_11816("curly_border", "cbo", new ItemStack(Blocks.field_10597)),
	field_11827("gradient", "gra", "# #", " # ", " # "),
	field_11850("gradient_up", "gru", " # ", " # ", "# #"),
	field_11809("bricks", "bri", new ItemStack(Blocks.field_10104)),
	field_18689("globe", "glb"),
	field_11823("creeper", "cre", new ItemStack(Items.CREEPER_HEAD)),
	field_11845("skull", "sku", new ItemStack(Items.WITHER_SKELETON_SKULL)),
	field_11812("flower", "flo", new ItemStack(Blocks.field_10554)),
	field_11825("mojang", "moj", new ItemStack(Items.field_8367));

	public static final int COUNT = values().length;
	public static final int LOOM_APPLICABLE_COUNT = COUNT - 5 - 1;
	private final String name;
	private final String id;
	private final String[] recipePattern = new String[3];
	private ItemStack baseStack = ItemStack.EMPTY;

	private BannerPattern(String string2, String string3) {
		this.name = string2;
		this.id = string3;
	}

	private BannerPattern(String string2, String string3, ItemStack itemStack) {
		this(string2, string3);
		this.baseStack = itemStack;
	}

	private BannerPattern(String string2, String string3, String string4, String string5, String string6) {
		this(string2, string3);
		this.recipePattern[0] = string4;
		this.recipePattern[1] = string5;
		this.recipePattern[2] = string6;
	}

	public Identifier getSpriteId(boolean bl) {
		String string = bl ? "banner" : "shield";
		return new Identifier("entity/" + string + "/" + this.getName());
	}

	public String getName() {
		return this.name;
	}

	public String getId() {
		return this.id;
	}

	@Nullable
	public static BannerPattern byId(String string) {
		for (BannerPattern bannerPattern : values()) {
			if (bannerPattern.id.equals(string)) {
				return bannerPattern;
			}
		}

		return null;
	}

	public static class Patterns {
		private final List<Pair<BannerPattern, DyeColor>> entries = Lists.newArrayList();

		public BannerPattern.Patterns add(BannerPattern bannerPattern, DyeColor dyeColor) {
			this.entries.add(Pair.of(bannerPattern, dyeColor));
			return this;
		}

		public ListTag toTag() {
			ListTag listTag = new ListTag();

			for (Pair<BannerPattern, DyeColor> pair : this.entries) {
				CompoundTag compoundTag = new CompoundTag();
				compoundTag.putString("Pattern", ((BannerPattern)pair.getLeft()).id);
				compoundTag.putInt("Color", ((DyeColor)pair.getRight()).getId());
				listTag.add(compoundTag);
			}

			return listTag;
		}
	}
}
