package net.minecraft;

import java.util.function.Supplier;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Lazy;

public enum class_3543 implements class_3542 {
	LEATHER("leather", 5, new int[]{1, 2, 3, 1}, 15, Sounds.ITEM_ARMOR_EQUIP_LEATHER, 0.0F, () -> Ingredient.ofItems(Items.LEATHER)),
	CHAIN("chainmail", 15, new int[]{1, 4, 5, 2}, 12, Sounds.ITEM_ARMOR_EQUIP_CHAIN, 0.0F, () -> Ingredient.ofItems(Items.IRON_INGOT)),
	IRON("iron", 15, new int[]{2, 5, 6, 2}, 9, Sounds.ITEM_ARMOR_EQUIP_IRON, 0.0F, () -> Ingredient.ofItems(Items.IRON_INGOT)),
	GOLD("gold", 7, new int[]{1, 3, 5, 2}, 25, Sounds.ITEM_ARMOR_EQUIP_GOLD, 0.0F, () -> Ingredient.ofItems(Items.GOLD_INGOT)),
	DIAMOND("diamond", 33, new int[]{3, 6, 8, 3}, 10, Sounds.ITEM_ARMOR_EQUIP_DIAMOND, 2.0F, () -> Ingredient.ofItems(Items.DIAMOND)),
	TURTLE("turtle", 25, new int[]{2, 5, 6, 2}, 9, Sounds.ITEM_ARMOR_EQUIP_TURTLE, 0.0F, () -> Ingredient.ofItems(Items.SCUTE));

	private static final int[] field_17145 = new int[]{13, 15, 16, 11};
	private final String field_17146;
	private final int field_17147;
	private final int[] field_17148;
	private final int field_17149;
	private final Sound field_17150;
	private final float field_17151;
	private final Lazy<Ingredient> field_17152;

	private class_3543(String string2, int j, int[] is, int k, Sound sound, float f, Supplier<Ingredient> supplier) {
		this.field_17146 = string2;
		this.field_17147 = j;
		this.field_17148 = is;
		this.field_17149 = k;
		this.field_17150 = sound;
		this.field_17151 = f;
		this.field_17152 = new Lazy<>(supplier);
	}

	@Override
	public int method_15999(EquipmentSlot equipmentSlot) {
		return field_17145[equipmentSlot.method_13032()] * this.field_17147;
	}

	@Override
	public int method_16001(EquipmentSlot equipmentSlot) {
		return this.field_17148[equipmentSlot.method_13032()];
	}

	@Override
	public int method_15998() {
		return this.field_17149;
	}

	@Override
	public Sound method_16000() {
		return this.field_17150;
	}

	@Override
	public Ingredient method_16002() {
		return this.field_17152.get();
	}

	@Override
	public String method_16003() {
		return this.field_17146;
	}

	@Override
	public float method_16004() {
		return this.field_17151;
	}
}
