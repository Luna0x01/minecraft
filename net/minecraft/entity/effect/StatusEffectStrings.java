package net.minecraft.entity.effect;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.item.FishItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;

public class StatusEffectStrings {
	private static final List<StatusEffectStrings.class_2697<Potion>> field_12339 = Lists.newArrayList();
	private static final List<StatusEffectStrings.class_2697<Item>> field_12340 = Lists.newArrayList();
	private static final List<StatusEffectStrings.class_2696> field_12341 = Lists.newArrayList();
	private static final Predicate<ItemStack> field_12342 = new Predicate<ItemStack>() {
		public boolean apply(@Nullable ItemStack itemStack) {
			for (StatusEffectStrings.class_2696 lv : StatusEffectStrings.field_12341) {
				if (lv.apply(itemStack)) {
					return true;
				}
			}

			return false;
		}
	};

	public static boolean method_11417(ItemStack itemStack) {
		return method_11423(itemStack) || method_11425(itemStack);
	}

	protected static boolean method_11423(ItemStack itemStack) {
		int i = 0;

		for (int j = field_12340.size(); i < j; i++) {
			if (((StatusEffectStrings.class_2697)field_12340.get(i)).field_12346.apply(itemStack)) {
				return true;
			}
		}

		return false;
	}

	protected static boolean method_11425(ItemStack itemStack) {
		int i = 0;

		for (int j = field_12339.size(); i < j; i++) {
			if (((StatusEffectStrings.class_2697)field_12339.get(i)).field_12346.apply(itemStack)) {
				return true;
			}
		}

		return false;
	}

	public static boolean method_11418(ItemStack itemStack, ItemStack itemStack2) {
		return !field_12342.apply(itemStack) ? false : method_11424(itemStack, itemStack2) || method_11426(itemStack, itemStack2);
	}

	protected static boolean method_11424(ItemStack itemStack, ItemStack itemStack2) {
		Item item = itemStack.getItem();
		int i = 0;

		for (int j = field_12340.size(); i < j; i++) {
			StatusEffectStrings.class_2697<Item> lv = (StatusEffectStrings.class_2697<Item>)field_12340.get(i);
			if (lv.field_12345 == item && lv.field_12346.apply(itemStack2)) {
				return true;
			}
		}

		return false;
	}

	protected static boolean method_11426(ItemStack itemStack, ItemStack itemStack2) {
		Potion potion = PotionUtil.getPotion(itemStack);
		int i = 0;

		for (int j = field_12339.size(); i < j; i++) {
			StatusEffectStrings.class_2697<Potion> lv = (StatusEffectStrings.class_2697<Potion>)field_12339.get(i);
			if (lv.field_12345 == potion && lv.field_12346.apply(itemStack2)) {
				return true;
			}
		}

		return false;
	}

	@Nullable
	public static ItemStack method_11427(ItemStack itemStack, @Nullable ItemStack itemStack2) {
		if (itemStack2 != null) {
			Potion potion = PotionUtil.getPotion(itemStack2);
			Item item = itemStack2.getItem();
			int i = 0;

			for (int j = field_12340.size(); i < j; i++) {
				StatusEffectStrings.class_2697<Item> lv = (StatusEffectStrings.class_2697<Item>)field_12340.get(i);
				if (lv.field_12345 == item && lv.field_12346.apply(itemStack)) {
					return PotionUtil.setPotion(new ItemStack(lv.field_12347), potion);
				}
			}

			i = 0;

			for (int l = field_12339.size(); i < l; i++) {
				StatusEffectStrings.class_2697<Potion> lv2 = (StatusEffectStrings.class_2697<Potion>)field_12339.get(i);
				if (lv2.field_12345 == potion && lv2.field_12346.apply(itemStack)) {
					return PotionUtil.setPotion(new ItemStack(item), lv2.field_12347);
				}
			}
		}

		return itemStack2;
	}

	public static void method_11416() {
		Predicate<ItemStack> predicate = new StatusEffectStrings.class_2696(Items.NETHER_WART);
		Predicate<ItemStack> predicate2 = new StatusEffectStrings.class_2696(Items.GOLDEN_CARROT);
		Predicate<ItemStack> predicate3 = new StatusEffectStrings.class_2696(Items.REDSTONE);
		Predicate<ItemStack> predicate4 = new StatusEffectStrings.class_2696(Items.FERMENTED_SPIDER_EYE);
		Predicate<ItemStack> predicate5 = new StatusEffectStrings.class_2696(Items.RABBIT_FOOT);
		Predicate<ItemStack> predicate6 = new StatusEffectStrings.class_2696(Items.GLOWSTONE_DUST);
		Predicate<ItemStack> predicate7 = new StatusEffectStrings.class_2696(Items.MAGMA_CREAM);
		Predicate<ItemStack> predicate8 = new StatusEffectStrings.class_2696(Items.SUGAR);
		Predicate<ItemStack> predicate9 = new StatusEffectStrings.class_2696(Items.RAW_FISH, FishItem.FishType.PUFFERFISH.getId());
		Predicate<ItemStack> predicate10 = new StatusEffectStrings.class_2696(Items.GLISTERING_MELON);
		Predicate<ItemStack> predicate11 = new StatusEffectStrings.class_2696(Items.SPIDER_EYE);
		Predicate<ItemStack> predicate12 = new StatusEffectStrings.class_2696(Items.GHAST_TEAR);
		Predicate<ItemStack> predicate13 = new StatusEffectStrings.class_2696(Items.BLAZE_POWDER);
		method_11421(new StatusEffectStrings.class_2696(Items.POTION));
		method_11421(new StatusEffectStrings.class_2696(Items.SPLASH_POTION));
		method_11421(new StatusEffectStrings.class_2696(Items.LINGERING_POTION));
		method_11419(Items.POTION, new StatusEffectStrings.class_2696(Items.GUNPOWDER), Items.SPLASH_POTION);
		method_11419(Items.SPLASH_POTION, new StatusEffectStrings.class_2696(Items.DRAGON_BREATH), Items.LINGERING_POTION);
		method_11420(Potions.WATER, predicate10, Potions.MUNDANE);
		method_11420(Potions.WATER, predicate12, Potions.MUNDANE);
		method_11420(Potions.WATER, predicate5, Potions.MUNDANE);
		method_11420(Potions.WATER, predicate13, Potions.MUNDANE);
		method_11420(Potions.WATER, predicate11, Potions.MUNDANE);
		method_11420(Potions.WATER, predicate8, Potions.MUNDANE);
		method_11420(Potions.WATER, predicate7, Potions.MUNDANE);
		method_11420(Potions.WATER, predicate6, Potions.THICK);
		method_11420(Potions.WATER, predicate3, Potions.MUNDANE);
		method_11420(Potions.WATER, predicate, Potions.AWKWARD);
		method_11420(Potions.AWKWARD, predicate2, Potions.NIGHT_VISION);
		method_11420(Potions.NIGHT_VISION, predicate3, Potions.LONG_NIGHT_VISION);
		method_11420(Potions.NIGHT_VISION, predicate4, Potions.INVISIBILITY);
		method_11420(Potions.LONG_NIGHT_VISION, predicate4, Potions.LONG_INVISIBILITY);
		method_11420(Potions.INVISIBILITY, predicate3, Potions.LONG_INVISIBILITY);
		method_11420(Potions.AWKWARD, predicate7, Potions.FIRE_RESISTANCE);
		method_11420(Potions.FIRE_RESISTANCE, predicate3, Potions.LONG_FIRE_RESISTANCE);
		method_11420(Potions.AWKWARD, predicate5, Potions.LEAPING);
		method_11420(Potions.LEAPING, predicate3, Potions.LONG_LEAPING);
		method_11420(Potions.LEAPING, predicate6, Potions.STRONG_LEAPING);
		method_11420(Potions.LEAPING, predicate4, Potions.SLOWNESS);
		method_11420(Potions.LONG_LEAPING, predicate4, Potions.LONG_SLOWNESS);
		method_11420(Potions.SLOWNESS, predicate3, Potions.LONG_SLOWNESS);
		method_11420(Potions.SWIFTNESS, predicate4, Potions.SLOWNESS);
		method_11420(Potions.LONG_SWIFTNESS, predicate4, Potions.LONG_SLOWNESS);
		method_11420(Potions.AWKWARD, predicate8, Potions.SWIFTNESS);
		method_11420(Potions.SWIFTNESS, predicate3, Potions.LONG_SWIFTNESS);
		method_11420(Potions.SWIFTNESS, predicate6, Potions.STRONG_SWIFTNESS);
		method_11420(Potions.AWKWARD, predicate9, Potions.WATER_BREATHING);
		method_11420(Potions.WATER_BREATHING, predicate3, Potions.LONG_WATER_BREATHING);
		method_11420(Potions.AWKWARD, predicate10, Potions.HEALING);
		method_11420(Potions.HEALING, predicate6, Potions.STRONG_HEALING);
		method_11420(Potions.HEALING, predicate4, Potions.HARMING);
		method_11420(Potions.STRONG_HEALING, predicate4, Potions.STRONG_HARMING);
		method_11420(Potions.HARMING, predicate6, Potions.STRONG_HARMING);
		method_11420(Potions.POISON, predicate4, Potions.HARMING);
		method_11420(Potions.LONG_POISON, predicate4, Potions.HARMING);
		method_11420(Potions.STRONG_POISON, predicate4, Potions.STRONG_HARMING);
		method_11420(Potions.AWKWARD, predicate11, Potions.POISON);
		method_11420(Potions.POISON, predicate3, Potions.LONG_POISON);
		method_11420(Potions.POISON, predicate6, Potions.STRONG_POISON);
		method_11420(Potions.AWKWARD, predicate12, Potions.REGENERATION);
		method_11420(Potions.REGENERATION, predicate3, Potions.LONG_REGENERATION);
		method_11420(Potions.REGENERATION, predicate6, Potions.STRONG_REGENERATION);
		method_11420(Potions.AWKWARD, predicate13, Potions.STRENGTH);
		method_11420(Potions.STRENGTH, predicate3, Potions.LONG_STRENGTH);
		method_11420(Potions.STRENGTH, predicate6, Potions.STRONG_STRENGTH);
		method_11420(Potions.WATER, predicate4, Potions.WEAKNESS);
		method_11420(Potions.WEAKNESS, predicate3, Potions.LONG_WEAKNESS);
	}

	private static void method_11419(PotionItem potionItem, StatusEffectStrings.class_2696 arg, PotionItem potionItem2) {
		field_12340.add(new StatusEffectStrings.class_2697<>(potionItem, arg, potionItem2));
	}

	private static void method_11421(StatusEffectStrings.class_2696 arg) {
		field_12341.add(arg);
	}

	private static void method_11420(Potion potion, Predicate<ItemStack> predicate, Potion potion2) {
		field_12339.add(new StatusEffectStrings.class_2697<>(potion, predicate, potion2));
	}

	static class class_2696 implements Predicate<ItemStack> {
		private final Item field_12343;
		private final int field_12344;

		public class_2696(Item item) {
			this(item, -1);
		}

		public class_2696(Item item, int i) {
			this.field_12343 = item;
			this.field_12344 = i;
		}

		public boolean apply(@Nullable ItemStack itemStack) {
			return itemStack != null && itemStack.getItem() == this.field_12343 && (this.field_12344 == -1 || this.field_12344 == itemStack.getData());
		}
	}

	static class class_2697<T> {
		final T field_12345;
		final Predicate<ItemStack> field_12346;
		final T field_12347;

		public class_2697(T object, Predicate<ItemStack> predicate, T object2) {
			this.field_12345 = object;
			this.field_12346 = predicate;
			this.field_12347 = object2;
		}
	}
}
