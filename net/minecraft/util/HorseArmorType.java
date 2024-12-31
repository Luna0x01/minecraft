package net.minecraft.util;

import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public enum HorseArmorType {
	NONE(0),
	IRON(5, "iron", "meo"),
	GOLD(7, "gold", "goo"),
	DIAMOND(11, "diamond", "dio");

	private final String entityTexture;
	private final String field_14643;
	private final int bonus;

	private HorseArmorType(int j) {
		this.bonus = j;
		this.entityTexture = null;
		this.field_14643 = "";
	}

	private HorseArmorType(int j, String string2, String string3) {
		this.bonus = j;
		this.entityTexture = "textures/entity/horse/armor/horse_armor_" + string2 + ".png";
		this.field_14643 = string3;
	}

	public int method_13134() {
		return this.ordinal();
	}

	public String method_13138() {
		return this.field_14643;
	}

	public int getBonus() {
		return this.bonus;
	}

	@Nullable
	public String getEntityTexture() {
		return this.entityTexture;
	}

	public static HorseArmorType method_13135(int i) {
		return values()[i];
	}

	public static HorseArmorType method_13137(ItemStack itemStack) {
		return itemStack.isEmpty() ? NONE : method_13136(itemStack.getItem());
	}

	public static HorseArmorType method_13136(Item item) {
		if (item == Items.IRON_HORSE_ARMOR) {
			return IRON;
		} else if (item == Items.GOLDEN_HORSE_ARMOR) {
			return GOLD;
		} else {
			return item == Items.DIAMOND_HORSE_ARMOR ? DIAMOND : NONE;
		}
	}

	public static boolean method_13139(Item item) {
		return method_13136(item) != NONE;
	}
}
