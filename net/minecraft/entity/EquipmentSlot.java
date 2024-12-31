package net.minecraft.entity;

public enum EquipmentSlot {
	MAINHAND(EquipmentSlot.Type.HAND, 0, 0, "mainhand"),
	OFFHAND(EquipmentSlot.Type.HAND, 1, 5, "offhand"),
	FEET(EquipmentSlot.Type.ARMOR, 0, 1, "feet"),
	LEGS(EquipmentSlot.Type.ARMOR, 1, 2, "legs"),
	CHEST(EquipmentSlot.Type.ARMOR, 2, 3, "chest"),
	HEAD(EquipmentSlot.Type.ARMOR, 3, 4, "head");

	private final EquipmentSlot.Type type;
	private final int field_14531;
	private final int field_14532;
	private final String name;

	private EquipmentSlot(EquipmentSlot.Type type, int j, int k, String string2) {
		this.type = type;
		this.field_14531 = j;
		this.field_14532 = k;
		this.name = string2;
	}

	public EquipmentSlot.Type getType() {
		return this.type;
	}

	public int method_13032() {
		return this.field_14531;
	}

	public int method_13033() {
		return this.field_14532;
	}

	public String getName() {
		return this.name;
	}

	public static EquipmentSlot method_13031(String string) {
		for (EquipmentSlot equipmentSlot : values()) {
			if (equipmentSlot.getName().equals(string)) {
				return equipmentSlot;
			}
		}

		throw new IllegalArgumentException("Invalid slot '" + string + "'");
	}

	public static enum Type {
		HAND,
		ARMOR;
	}
}
