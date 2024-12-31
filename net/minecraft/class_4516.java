package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;

public class class_4516 extends class_3395 {
	public class_4516(Schema schema, boolean bl) {
		super(schema, bl, "EntityItemFrameDirectionFix", class_3402.field_16596, "minecraft:item_frame");
	}

	public Dynamic<?> method_21719(Dynamic<?> dynamic) {
		return dynamic.set("Facing", dynamic.createByte(method_21718(dynamic.getByte("Facing"))));
	}

	@Override
	protected Typed<?> method_15200(Typed<?> typed) {
		return typed.update(DSL.remainderFinder(), this::method_21719);
	}

	private static byte method_21718(byte b) {
		switch (b) {
			case 0:
				return 3;
			case 1:
				return 4;
			case 2:
			default:
				return 2;
			case 3:
				return 5;
		}
	}
}
