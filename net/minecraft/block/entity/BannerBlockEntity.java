package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BannerPattern;
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Nameable;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;

public class BannerBlockEntity extends BlockEntity implements Nameable {
	private String field_15147;
	private DyeColor field_15148 = DyeColor.BLACK;
	private NbtList patternsNbt;
	private boolean patternListTagRead;
	private List<BannerPattern> patterns;
	private List<DyeColor> colors;
	private String textureIdentifier;

	public void method_13720(ItemStack stack, boolean bl) {
		this.patternsNbt = null;
		NbtCompound nbtCompound = stack.getNbtCompound("BlockEntityTag");
		if (nbtCompound != null && nbtCompound.contains("Patterns", 9)) {
			this.patternsNbt = nbtCompound.getList("Patterns", 10).copy();
		}

		this.field_15148 = bl ? method_13721(stack) : BannerItem.getDyeColor(stack);
		this.patterns = null;
		this.colors = null;
		this.textureIdentifier = "";
		this.patternListTagRead = true;
		this.field_15147 = stack.hasCustomName() ? stack.getCustomName() : null;
	}

	@Override
	public String getTranslationKey() {
		return this.hasCustomName() ? this.field_15147 : "banner";
	}

	@Override
	public boolean hasCustomName() {
		return this.field_15147 != null && !this.field_15147.isEmpty();
	}

	@Override
	public Text getName() {
		return (Text)(this.hasCustomName() ? new LiteralText(this.getTranslationKey()) : new TranslatableText(this.getTranslationKey()));
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		super.toNbt(nbt);
		nbt.putInt("Base", this.field_15148.getSwappedId());
		if (this.patternsNbt != null) {
			nbt.put("Patterns", this.patternsNbt);
		}

		if (this.hasCustomName()) {
			nbt.putString("CustomName", this.field_15147);
		}

		return nbt;
	}

	@Override
	public void fromNbt(NbtCompound nbt) {
		super.fromNbt(nbt);
		if (nbt.contains("CustomName", 8)) {
			this.field_15147 = nbt.getString("CustomName");
		}

		this.field_15148 = DyeColor.getById(nbt.getInt("Base"));
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

	public static int getPatternCount(ItemStack stack) {
		NbtCompound nbtCompound = stack.getNbtCompound("BlockEntityTag");
		return nbtCompound != null && nbtCompound.contains("Patterns") ? nbtCompound.getList("Patterns", 10).size() : 0;
	}

	public List<BannerPattern> getPatterns() {
		this.buildTextureIdentifier();
		return this.patterns;
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
				this.patterns.add(BannerPattern.BASE);
				this.colors.add(this.field_15148);
				this.textureIdentifier = "b" + this.field_15148.getSwappedId();
				if (this.patternsNbt != null) {
					for (int i = 0; i < this.patternsNbt.size(); i++) {
						NbtCompound nbtCompound = this.patternsNbt.getCompound(i);
						BannerPattern bannerPattern = BannerPattern.getById(nbtCompound.getString("Pattern"));
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

	public static void loadFromItemStack(ItemStack stack) {
		NbtCompound nbtCompound = stack.getNbtCompound("BlockEntityTag");
		if (nbtCompound != null && nbtCompound.contains("Patterns", 9)) {
			NbtList nbtList = nbtCompound.getList("Patterns", 10);
			if (!nbtList.isEmpty()) {
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

	public ItemStack method_13722() {
		ItemStack itemStack = BannerItem.method_13645(this.field_15148, this.patternsNbt);
		if (this.hasCustomName()) {
			itemStack.setCustomName(this.getTranslationKey());
		}

		return itemStack;
	}

	public static DyeColor method_13721(ItemStack itemStack) {
		NbtCompound nbtCompound = itemStack.getNbtCompound("BlockEntityTag");
		return nbtCompound != null && nbtCompound.contains("Base") ? DyeColor.getById(nbtCompound.getInt("Base")) : DyeColor.BLACK;
	}
}
