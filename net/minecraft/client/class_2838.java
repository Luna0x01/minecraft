package net.minecraft.client;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.client.color.world.GrassColors;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BannerItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.FireworkChargeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.potion.PotionUtil;
import net.minecraft.util.collection.IdList;

public class class_2838 {
	private final IdList<class_2837> field_13300 = new IdList<>(32);

	public static class_2838 method_12161(BlockColors blockColors) {
		class_2838 lv = new class_2838();
		lv.method_12162(new class_2837() {
			@Override
			public int method_12159(ItemStack itemStack, int i) {
				return i > 0 ? -1 : ((ArmorItem)itemStack.getItem()).getColor(itemStack);
			}
		}, Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS);
		lv.method_12162(new class_2837() {
			@Override
			public int method_12159(ItemStack itemStack, int i) {
				return i > 0 ? -1 : BannerItem.getDyeColor(itemStack).getMaterialColor().color;
			}
		}, Items.BANNER, Items.SHIELD);
		lv.method_12163(
			new class_2837() {
				@Override
				public int method_12159(ItemStack itemStack, int i) {
					DoublePlantBlock.DoublePlantType doublePlantType = DoublePlantBlock.DoublePlantType.getById(itemStack.getData());
					return doublePlantType != DoublePlantBlock.DoublePlantType.GRASS && doublePlantType != DoublePlantBlock.DoublePlantType.FERN
						? -1
						: GrassColors.getColor(0.5, 1.0);
				}
			},
			Blocks.DOUBLE_PLANT
		);
		lv.method_12162(new class_2837() {
			@Override
			public int method_12159(ItemStack itemStack, int i) {
				if (i != 1) {
					return -1;
				} else {
					NbtElement nbtElement = FireworkChargeItem.getExplosionNbt(itemStack, "Colors");
					if (!(nbtElement instanceof NbtIntArray)) {
						return 9079434;
					} else {
						int[] is = ((NbtIntArray)nbtElement).getIntArray();
						if (is.length == 1) {
							return is[0];
						} else {
							int j = 0;
							int k = 0;
							int l = 0;
							int m = 0;

							for (int n = is.length; m < n; m++) {
								int o = is[m];
								j += (o & 0xFF0000) >> 16;
								k += (o & 0xFF00) >> 8;
								l += (o & 0xFF) >> 0;
							}

							j /= is.length;
							k /= is.length;
							l /= is.length;
							return j << 16 | k << 8 | l;
						}
					}
				}
			}
		}, Items.FIREWORK_CHARGE);
		lv.method_12162(new class_2837() {
			@Override
			public int method_12159(ItemStack itemStack, int i) {
				return i > 0 ? -1 : PotionUtil.getColor(PotionUtil.getPotionEffects(itemStack));
			}
		}, Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION);
		lv.method_12162(new class_2837() {
			@Override
			public int method_12159(ItemStack itemStack, int i) {
				EntityType.SpawnEggData spawnEggData = (EntityType.SpawnEggData)EntityType.SPAWN_EGGS.get(SpawnEggItem.method_11407(itemStack));
				if (spawnEggData == null) {
					return -1;
				} else {
					return i == 0 ? spawnEggData.foreGroundColor : spawnEggData.backGroundColor;
				}
			}
		}, Items.SPAWN_EGG);
		lv.method_12163(new class_2837() {
			@Override
			public int method_12159(ItemStack itemStack, int i) {
				BlockState blockState = ((BlockItem)itemStack.getItem()).getBlock().stateFromData(itemStack.getData());
				return blockColors.method_12157(blockState, null, null, i);
			}
		}, Blocks.GRASS, Blocks.TALLGRASS, Blocks.VINE, Blocks.LEAVES, Blocks.LEAVES2, Blocks.LILY_PAD);
		lv.method_12162(new class_2837() {
			@Override
			public int method_12159(ItemStack itemStack, int i) {
				return i == 0 ? PotionUtil.getColor(PotionUtil.getPotionEffects(itemStack)) : -1;
			}
		}, Items.TIPPED_ARROW);
		return lv;
	}

	public int method_12160(ItemStack itemStack, int i) {
		class_2837 lv = this.field_13300.fromId(Item.REGISTRY.getRawId(itemStack.getItem()));
		return lv == null ? -1 : lv.method_12159(itemStack, i);
	}

	public void method_12163(class_2837 arg, Block... blocks) {
		int i = 0;

		for (int j = blocks.length; i < j; i++) {
			this.field_13300.set(arg, Item.getRawId(Item.fromBlock(blocks[i])));
		}
	}

	public void method_12162(class_2837 arg, Item... items) {
		int i = 0;

		for (int j = items.length; i < j; i++) {
			this.field_13300.set(arg, Item.getRawId(items[i]));
		}
	}
}
