package net.minecraft.item;

import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.DyeColor;

public class FireworkChargeItem extends Item {
	@Override
	public int getDisplayColor(ItemStack stack, int color) {
		if (color != 1) {
			return super.getDisplayColor(stack, color);
		} else {
			NbtElement nbtElement = getExplosionNbt(stack, "Colors");
			if (!(nbtElement instanceof NbtIntArray)) {
				return 9079434;
			} else {
				NbtIntArray nbtIntArray = (NbtIntArray)nbtElement;
				int[] is = nbtIntArray.getIntArray();
				if (is.length == 1) {
					return is[0];
				} else {
					int i = 0;
					int j = 0;
					int k = 0;

					for (int n : is) {
						i += (n & 0xFF0000) >> 16;
						j += (n & 0xFF00) >> 8;
						k += (n & 0xFF) >> 0;
					}

					i /= is.length;
					j /= is.length;
					k /= is.length;
					return i << 16 | j << 8 | k;
				}
			}
		}
	}

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
	public void appendTooltip(ItemStack stack, PlayerEntity player, List<String> lines, boolean advanced) {
		if (stack.hasNbt()) {
			NbtCompound nbtCompound = stack.getNbt().getCompound("Explosion");
			if (nbtCompound != null) {
				addExplosionInfo(nbtCompound, lines);
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

			for (int k : is) {
				if (!bl) {
					string = string + ", ";
				}

				bl = false;
				boolean bl2 = false;

				for (int l = 0; l < DyeItem.COLORS.length; l++) {
					if (k == DyeItem.COLORS[l]) {
						bl2 = true;
						string = string + CommonI18n.translate("item.fireworksCharge." + DyeColor.getById(l).getTranslationKey());
						break;
					}
				}

				if (!bl2) {
					string = string + CommonI18n.translate("item.fireworksCharge.customColor");
				}
			}

			list.add(string);
		}

		int[] ks = nbt.getIntArray("FadeColors");
		if (ks.length > 0) {
			boolean bl3 = true;
			String string2 = CommonI18n.translate("item.fireworksCharge.fadeTo") + " ";

			for (int o : ks) {
				if (!bl3) {
					string2 = string2 + ", ";
				}

				bl3 = false;
				boolean bl4 = false;

				for (int p = 0; p < 16; p++) {
					if (o == DyeItem.COLORS[p]) {
						bl4 = true;
						string2 = string2 + CommonI18n.translate("item.fireworksCharge." + DyeColor.getById(p).getTranslationKey());
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
