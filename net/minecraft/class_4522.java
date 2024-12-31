package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;

public class class_4522 extends class_3395 {
	public class_4522(Schema schema, boolean bl) {
		super(schema, bl, "EntityWolfColorFix", class_3402.field_16596, "minecraft:wolf");
	}

	public Dynamic<?> method_21751(Dynamic<?> dynamic) {
		return dynamic.update("CollarColor", dynamicx -> dynamicx.createByte((byte)(15 - dynamicx.getNumberValue(0).intValue())));
	}

	@Override
	protected Typed<?> method_15200(Typed<?> typed) {
		return typed.update(DSL.remainderFinder(), this::method_21751);
	}
}
