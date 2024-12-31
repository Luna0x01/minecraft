package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.DyeColor;

public class BannerBlockEntity extends BlockEntity {
	private int base;
	private NbtList patternsNbt;
	private boolean patternListTagRead;
	private List<BannerBlockEntity.BannerPattern> patterns;
	private List<DyeColor> colors;
	private String textureIdentifier;

	public void fromItemStack(ItemStack stack) {
		this.patternsNbt = null;
		if (stack.hasNbt() && stack.getNbt().contains("BlockEntityTag", 10)) {
			NbtCompound nbtCompound = stack.getNbt().getCompound("BlockEntityTag");
			if (nbtCompound.contains("Patterns")) {
				this.patternsNbt = nbtCompound.getList("Patterns", 10).copy();
			}

			if (nbtCompound.contains("Base", 99)) {
				this.base = nbtCompound.getInt("Base");
			} else {
				this.base = stack.getData() & 15;
			}
		} else {
			this.base = stack.getData() & 15;
		}

		this.patterns = null;
		this.colors = null;
		this.textureIdentifier = "";
		this.patternListTagRead = true;
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		super.toNbt(nbt);
		toNbt(nbt, this.base, this.patternsNbt);
		return nbt;
	}

	public static void toNbt(NbtCompound compound, int base, @Nullable NbtList patterns) {
		compound.putInt("Base", base);
		if (patterns != null) {
			compound.put("Patterns", patterns);
		}
	}

	@Override
	public void fromNbt(NbtCompound nbt) {
		super.fromNbt(nbt);
		this.base = nbt.getInt("Base");
		this.patternsNbt = nbt.getList("Patterns", 10);
		this.patterns = null;
		this.colors = null;
		this.textureIdentifier = null;
		this.patternListTagRead = true;
	}

	@Nullable
	@Override
	public BlockEntityUpdateS2CPacket getUpdatePacket() {
		return new BlockEntityUpdateS2CPacket(this.pos, 6, this.getUpdatePacketContent());
	}

	@Override
	public NbtCompound getUpdatePacketContent() {
		return this.toNbt(new NbtCompound());
	}

	public int getBase() {
		return this.base;
	}

	public static int getBase(ItemStack stack) {
		NbtCompound nbtCompound = stack.getSubNbt("BlockEntityTag", false);
		return nbtCompound != null && nbtCompound.contains("Base") ? nbtCompound.getInt("Base") : stack.getData();
	}

	public static int getPatternCount(ItemStack stack) {
		NbtCompound nbtCompound = stack.getSubNbt("BlockEntityTag", false);
		return nbtCompound != null && nbtCompound.contains("Patterns") ? nbtCompound.getList("Patterns", 10).size() : 0;
	}

	public List<BannerBlockEntity.BannerPattern> getPatterns() {
		this.buildTextureIdentifier();
		return this.patterns;
	}

	public NbtList getPatternsNbt() {
		return this.patternsNbt;
	}

	public List<DyeColor> getColors() {
		this.buildTextureIdentifier();
		return this.colors;
	}

	public String getTextureIdentifier() {
		this.buildTextureIdentifier();
		return this.textureIdentifier;
	}

	private void buildTextureIdentifier() {
		if (this.patterns == null || this.colors == null || this.textureIdentifier == null) {
			if (!this.patternListTagRead) {
				this.textureIdentifier = "";
			} else {
				this.patterns = Lists.newArrayList();
				this.colors = Lists.newArrayList();
				this.patterns.add(BannerBlockEntity.BannerPattern.BASE);
				this.colors.add(DyeColor.getById(this.base));
				this.textureIdentifier = "b" + this.base;
				if (this.patternsNbt != null) {
					for (int i = 0; i < this.patternsNbt.size(); i++) {
						NbtCompound nbtCompound = this.patternsNbt.getCompound(i);
						BannerBlockEntity.BannerPattern bannerPattern = BannerBlockEntity.BannerPattern.getById(nbtCompound.getString("Pattern"));
						if (bannerPattern != null) {
							this.patterns.add(bannerPattern);
							int j = nbtCompound.getInt("Color");
							this.colors.add(DyeColor.getById(j));
							this.textureIdentifier = this.textureIdentifier + bannerPattern.getId() + j;
						}
					}
				}
			}
		}
	}

	public static void method_11644(ItemStack itemStack, DyeColor dyeColor) {
		NbtCompound nbtCompound = itemStack.getSubNbt("BlockEntityTag", true);
		nbtCompound.putInt("Base", dyeColor.getSwappedId());
	}

	public static void loadFromItemStack(ItemStack stack) {
		NbtCompound nbtCompound = stack.getSubNbt("BlockEntityTag", false);
		if (nbtCompound != null && nbtCompound.contains("Patterns", 9)) {
			NbtList nbtList = nbtCompound.getList("Patterns", 10);
			if (nbtList.size() > 0) {
				nbtList.remove(nbtList.size() - 1);
				if (nbtList.isEmpty()) {
					stack.getNbt().remove("BlockEntityTag");
					if (stack.getNbt().isEmpty()) {
						stack.setNbt(null);
					}
				}
			}
		}
	}

	public static enum BannerPattern {
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
		CREEPER("creeper", "cre", new ItemStack(Items.SKULL, 1, 4)),
		GRADIENT("gradient", "gra", "# #", " # ", " # "),
		GRADIENT_UP("gradient_up", "gru", " # ", " # ", "# #"),
		BRICKS("bricks", "bri", new ItemStack(Blocks.BRICKS)),
		SKULL("skull", "sku", new ItemStack(Items.SKULL, 1, 1)),
		FLOWER("flower", "flo", new ItemStack(Blocks.RED_FLOWER, 1, FlowerBlock.FlowerType.OXEYE_DAISY.getDataIndex())),
		MOJANG("mojang", "moj", new ItemStack(Items.GOLDEN_APPLE, 1, 1));

		private final String name;
		private final String id;
		private final String[] recipe = new String[3];
		private ItemStack ingredient;

		private BannerPattern(String string2, String string3) {
			this.name = string2;
			this.id = string3;
		}

		private BannerPattern(String string2, String string3, ItemStack itemStack) {
			this(string2, string3);
			this.ingredient = itemStack;
		}

		private BannerPattern(String string2, String string3, String string4, String string5, String string6) {
			this(string2, string3);
			this.recipe[0] = string4;
			this.recipe[1] = string5;
			this.recipe[2] = string6;
		}

		public String getName() {
			return this.name;
		}

		public String getId() {
			return this.id;
		}

		public String[] getRecipe() {
			return this.recipe;
		}

		public boolean isCraftable() {
			return this.ingredient != null || this.recipe[0] != null;
		}

		public boolean hasIngredient() {
			return this.ingredient != null;
		}

		public ItemStack getIngredient() {
			return this.ingredient;
		}

		@Nullable
		public static BannerBlockEntity.BannerPattern getById(String id) {
			for (BannerBlockEntity.BannerPattern bannerPattern : values()) {
				if (bannerPattern.id.equals(id)) {
					return bannerPattern;
				}
			}

			return null;
		}
	}
}
