package net.minecraft;

import java.util.Map;

public class class_4464 implements class_4463 {
	private final class_4456 field_21909 = new class_4456("minecraft");

	@Override
	public <T extends class_4465> void method_21356(Map<String, T> map, class_4465.class_4467<T> arg) {
		T lv = class_4465.method_21359("vanilla", false, () -> this.field_21909, arg, class_4465.class_4466.BOTTOM);
		if (lv != null) {
			map.put("vanilla", lv);
		}
	}
}
