package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.TooltipContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

public class FireworkChargeItem extends Item {
	public FireworkChargeItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public void appendTooltips(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext tooltipContext) {
		NbtCompound nbtCompound = stack.getNbtCompound("Explosion");
		if (nbtCompound != null) {
			method_16059(nbtCompound, tooltip);
		}
	}

	public static void method_16059(NbtCompound nbtCompound, List<Text> list) {
		FireworkItem.class_3551 lv = FireworkItem.class_3551.method_16054(nbtCompound.getByte("Type"));
		list.add(new TranslatableText("item.minecraft.firework_star.shape." + lv.method_16056()).formatted(Formatting.GRAY));
		int[] is = nbtCompound.getIntArray("Colors");
		if (is.length > 0) {
			list.add(method_16060(new LiteralText("").formatted(Formatting.GRAY), is));
		}

		int[] js = nbtCompound.getIntArray("FadeColors");
		if (js.length > 0) {
			list.add(method_16060(new TranslatableText("item.minecraft.firework_star.fade_to").append(" ").formatted(Formatting.GRAY), js));
		}

		if (nbtCompound.getBoolean("Trail")) {
			list.add(new TranslatableText("item.minecraft.firework_star.trail").formatted(Formatting.GRAY));
		}

		if (nbtCompound.getBoolean("Flicker")) {
			list.add(new TranslatableText("item.minecraft.firework_star.flicker").formatted(Formatting.GRAY));
		}
	}

	private static Text method_16060(Text text, int[] is) {
		for (int i = 0; i < is.length; i++) {
			if (i > 0) {
				text.append(", ");
			}

			text.append(method_16058(is[i]));
		}

		return text;
	}

	private static Text method_16058(int i) {
		DyeColor dyeColor = DyeColor.getByFireworkColor(i);
		return dyeColor == null
			? new TranslatableText("item.minecraft.firework_star.custom_color")
			: new TranslatableText("item.minecraft.firework_star." + dyeColor.getTranslationKey());
	}
}
