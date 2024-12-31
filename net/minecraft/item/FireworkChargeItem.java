package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.TooltipContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.DyeColor;
import net.minecraft.world.World;

public class FireworkChargeItem extends Item {
	public static NbtElement getExplosionNbt(ItemStack stack, String name) {
		if (stack.hasNbt()) {
			NbtCompound nbtCompound = stack.getNbt().getCompound("Explosion");
			if (nbtCompound != null) {
				return nbtCompound.get(name);
			}
		}

		return null;
	}

	@Override
	public void appendTooltips(ItemStack stack, @Nullable World world, List<String> tooltip, TooltipContext tooltipContext) {
		if (stack.hasNbt()) {
			NbtCompound nbtCompound = stack.getNbt().getCompound("Explosion");
			if (nbtCompound != null) {
				addExplosionInfo(nbtCompound, tooltip);
			}
		}
	}

	public static void addExplosionInfo(NbtCompound nbt, List<String> list) {
		byte b = nbt.getByte("Type");
		if (b >= 0 && b <= 4) {
			list.add(CommonI18n.translate("item.fireworksCharge.type." + b).trim());
		} else {
			list.add(CommonI18n.translate("item.fireworksCharge.type").trim());
		}

		int[] is = nbt.getIntArray("Colors");
		if (is.length > 0) {
			boolean bl = true;
			String string = "";

			for (int i : is) {
				if (!bl) {
					string = string + ", ";
				}

				bl = false;
				boolean bl2 = false;

				for (int j = 0; j < DyeItem.COLORS.length; j++) {
					if (i == DyeItem.COLORS[j]) {
						bl2 = true;
						string = string + CommonI18n.translate("item.fireworksCharge." + DyeColor.getById(j).getTranslationKey());
						break;
					}
				}

				if (!bl2) {
					string = string + CommonI18n.translate("item.fireworksCharge.customColor");
				}
			}

			list.add(string);
		}

		int[] js = nbt.getIntArray("FadeColors");
		if (js.length > 0) {
			boolean bl3 = true;
			String string2 = CommonI18n.translate("item.fireworksCharge.fadeTo") + " ";

			for (int k : js) {
				if (!bl3) {
					string2 = string2 + ", ";
				}

				bl3 = false;
				boolean bl4 = false;

				for (int l = 0; l < 16; l++) {
					if (k == DyeItem.COLORS[l]) {
						bl4 = true;
						string2 = string2 + CommonI18n.translate("item.fireworksCharge." + DyeColor.getById(l).getTranslationKey());
						break;
					}
				}

				if (!bl4) {
					string2 = string2 + CommonI18n.translate("item.fireworksCharge.customColor");
				}
			}

			list.add(string2);
		}

		boolean bl5 = nbt.getBoolean("Trail");
		if (bl5) {
			list.add(CommonI18n.translate("item.fireworksCharge.trail"));
		}

		boolean bl6 = nbt.getBoolean("Flicker");
		if (bl6) {
			list.add(CommonI18n.translate("item.fireworksCharge.flicker"));
		}
	}
}
