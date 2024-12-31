package net.minecraft.client;

import net.minecraft.class_3558;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.color.world.GrassColors;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Itemable;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.potion.PotionUtil;
import net.minecraft.util.collection.IdList;
import net.minecraft.util.registry.Registry;

public class class_2838 {
	private final IdList<class_2837> field_13300 = new IdList<>(32);

	public static class_2838 method_12161(BlockColors blockColors) {
		class_2838 lv = new class_2838();
		lv.method_12163(
			(itemStack, i) -> i > 0 ? -1 : ((DyeableArmorItem)itemStack.getItem()).method_16050(itemStack),
			Items.LEATHER_HELMET,
			Items.LEATHER_CHESTPLATE,
			Items.LEATHER_LEGGINGS,
			Items.LEATHER_BOOTS
		);
		lv.method_12163((itemStack, i) -> GrassColors.getColor(0.5, 1.0), Blocks.TALL_GRASS, Blocks.LARGE_FERN);
		lv.method_12163((itemStack, i) -> {
			if (i != 1) {
				return -1;
			} else {
				NbtCompound nbtCompound = itemStack.getNbtCompound("Explosion");
				int[] is = nbtCompound != null && nbtCompound.contains("Colors", 11) ? nbtCompound.getIntArray("Colors") : null;
				if (is == null) {
					return 9079434;
				} else if (is.length == 1) {
					return is[0];
				} else {
					int j = 0;
					int k = 0;
					int l = 0;

					for (int m : is) {
						j += (m & 0xFF0000) >> 16;
						k += (m & 0xFF00) >> 8;
						l += (m & 0xFF) >> 0;
					}

					j /= is.length;
					k /= is.length;
					l /= is.length;
					return j << 16 | k << 8 | l;
				}
			}
		}, Items.FIREWORK_STAR);
		lv.method_12163((itemStack, i) -> i > 0 ? -1 : PotionUtil.getColor(itemStack), Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION);

		for (class_3558 lv2 : class_3558.method_16129()) {
			lv.method_12163((itemStack, i) -> lv2.method_16125(i), lv2);
		}

		lv.method_12163(
			(itemStack, i) -> {
				BlockState blockState = ((BlockItem)itemStack.getItem()).getBlock().getDefaultState();
				return blockColors.method_18332(blockState, null, null, i);
			},
			Blocks.GRASS_BLOCK,
			Blocks.GRASS,
			Blocks.FERN,
			Blocks.VINE,
			Blocks.OAK_LEAVES,
			Blocks.SPRUCE_LEAVES,
			Blocks.BIRCH_LEAVES,
			Blocks.JUNGLE_LEAVES,
			Blocks.ACACIA_LEAVES,
			Blocks.DARK_OAK_LEAVES,
			Blocks.LILY_PAD
		);
		lv.method_12163((itemStack, i) -> i == 0 ? PotionUtil.getColor(itemStack) : -1, Items.TIPPED_ARROW);
		lv.method_12163((itemStack, i) -> i == 0 ? -1 : FilledMapItem.method_13665(itemStack), Items.FILLED_MAP);
		return lv;
	}

	public int method_12160(ItemStack itemStack, int i) {
		class_2837 lv = this.field_13300.fromId(Registry.ITEM.getRawId(itemStack.getItem()));
		return lv == null ? -1 : lv.getColor(itemStack, i);
	}

	public void method_12163(class_2837 arg, Itemable... itemables) {
		for (Itemable itemable : itemables) {
			this.field_13300.set(arg, Item.getRawId(itemable.getItem()));
		}
	}
}
