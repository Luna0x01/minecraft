package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.class_3559;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.BannerPattern;
import net.minecraft.block.Block;
import net.minecraft.client.TooltipContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;

public class BannerItem extends class_3559 {
	public BannerItem(Block block, Block block2, Item.Settings settings) {
		super(block, block2, settings);
		Validate.isInstanceOf(AbstractBannerBlock.class, block);
		Validate.isInstanceOf(AbstractBannerBlock.class, block2);
	}

	public static void method_11359(ItemStack itemStack, List<Text> list) {
		NbtCompound nbtCompound = itemStack.getNbtCompound("BlockEntityTag");
		if (nbtCompound != null && nbtCompound.contains("Patterns")) {
			NbtList nbtList = nbtCompound.getList("Patterns", 10);

			for (int i = 0; i < nbtList.size() && i < 6; i++) {
				NbtCompound nbtCompound2 = nbtList.getCompound(i);
				DyeColor dyeColor = DyeColor.byId(nbtCompound2.getInt("Color"));
				BannerPattern bannerPattern = BannerPattern.getById(nbtCompound2.getString("Pattern"));
				if (bannerPattern != null) {
					list.add(new TranslatableText("block.minecraft.banner." + bannerPattern.getName() + '.' + dyeColor.getTranslationKey()).formatted(Formatting.GRAY));
				}
			}
		}
	}

	public DyeColor method_16011() {
		return ((AbstractBannerBlock)this.getBlock()).getColor();
	}

	@Override
	public void appendTooltips(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext tooltipContext) {
		method_11359(stack, tooltip);
	}
}
