package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.BannerPattern;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.text.Nameable;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;

public class BannerBlockEntity extends BlockEntity implements Nameable {
	private Text field_18590;
	private DyeColor field_15148 = DyeColor.WHITE;
	private NbtList patternsNbt;
	private boolean patternListTagRead;
	private List<BannerPattern> patterns;
	private List<DyeColor> colors;
	private String textureIdentifier;

	public BannerBlockEntity() {
		super(BlockEntityType.BANNER);
	}

	public BannerBlockEntity(DyeColor dyeColor) {
		this();
		this.field_15148 = dyeColor;
	}

	public void method_16774(ItemStack itemStack, DyeColor dyeColor) {
		this.patternsNbt = null;
		NbtCompound nbtCompound = itemStack.getNbtCompound("BlockEntityTag");
		if (nbtCompound != null && nbtCompound.contains("Patterns", 9)) {
			this.patternsNbt = nbtCompound.getList("Patterns", 10).copy();
		}

		this.field_15148 = dyeColor;
		this.patterns = null;
		this.colors = null;
		this.textureIdentifier = "";
		this.patternListTagRead = true;
		this.field_18590 = itemStack.hasCustomName() ? itemStack.getName() : null;
	}

	@Override
	public Text method_15540() {
		return (Text)(this.field_18590 != null ? this.field_18590 : new TranslatableText("block.minecraft.banner"));
	}

	@Override
	public boolean hasCustomName() {
		return this.field_18590 != null;
	}

	@Nullable
	@Override
	public Text method_15541() {
		return this.field_18590;
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		super.toNbt(nbt);
		if (this.patternsNbt != null) {
			nbt.put("Patterns", this.patternsNbt);
		}

		if (this.field_18590 != null) {
			nbt.putString("CustomName", Text.Serializer.serialize(this.field_18590));
		}

		return nbt;
	}

	@Override
	public void fromNbt(NbtCompound nbt) {
		super.fromNbt(nbt);
		if (nbt.contains("CustomName", 8)) {
			this.field_18590 = Text.Serializer.deserializeText(nbt.getString("CustomName"));
		}

		if (this.hasWorld()) {
			this.field_15148 = ((AbstractBannerBlock)this.method_16783().getBlock()).getColor();
		} else {
			this.field_15148 = null;
		}

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
				DyeColor dyeColor = this.method_16776(this::method_16783);
				if (dyeColor == null) {
					this.textureIdentifier = "banner_missing";
				} else {
					this.patterns.add(BannerPattern.BASE);
					this.colors.add(dyeColor);
					this.textureIdentifier = "b" + dyeColor.getId();
					if (this.patternsNbt != null) {
						for (int i = 0; i < this.patternsNbt.size(); i++) {
							NbtCompound nbtCompound = this.patternsNbt.getCompound(i);
							BannerPattern bannerPattern = BannerPattern.getById(nbtCompound.getString("Pattern"));
							if (bannerPattern != null) {
								this.patterns.add(bannerPattern);
								int j = nbtCompound.getInt("Color");
								this.colors.add(DyeColor.byId(j));
								this.textureIdentifier = this.textureIdentifier + bannerPattern.getId() + j;
							}
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
					stack.removeNbt("BlockEntityTag");
				}
			}
		}
	}

	public ItemStack method_16775(BlockState blockState) {
		ItemStack itemStack = new ItemStack(BannerBlock.getForColor(this.method_16776(() -> blockState)));
		if (this.patternsNbt != null && !this.patternsNbt.isEmpty()) {
			itemStack.getOrCreateNbtCompound("BlockEntityTag").put("Patterns", this.patternsNbt.copy());
		}

		if (this.field_18590 != null) {
			itemStack.setCustomName(this.field_18590);
		}

		return itemStack;
	}

	public DyeColor method_16776(Supplier<BlockState> supplier) {
		if (this.field_15148 == null) {
			this.field_15148 = ((AbstractBannerBlock)((BlockState)supplier.get()).getBlock()).getColor();
		}

		return this.field_15148;
	}
}
