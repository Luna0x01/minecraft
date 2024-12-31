package net.minecraft;

import java.util.function.Function;

public enum class_3319 {
	MOVEMENT("movement", class_3313::new),
	FIND_TREE("find_tree", class_3312::new),
	PUNCH_TREE("punch_tree", class_3315::new),
	OPEN_INVENTORY("open_inventory", class_3314::new),
	CRAFT_PLANKS("craft_planks", class_3311::new),
	NONE("none", class_3310::new);

	private final String name;
	private final Function<class_3316, ? extends class_3318> field_16240;

	private <T extends class_3318> class_3319(String string2, Function<class_3316, T> function) {
		this.name = string2;
		this.field_16240 = function;
	}

	public class_3318 method_14740(class_3316 arg) {
		return (class_3318)this.field_16240.apply(arg);
	}

	public String getName() {
		return this.name;
	}

	public static class_3319 method_14741(String name) {
		for (class_3319 lv : values()) {
			if (lv.name.equals(name)) {
				return lv;
			}
		}

		return NONE;
	}
}
