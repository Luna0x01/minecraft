package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;

public class class_4511 extends class_3395 {
	public class_4511(Schema schema, boolean bl) {
		super(schema, bl, "Colorless shulker entity fix", class_3402.field_16596, "minecraft:shulker");
	}

	@Override
	protected Typed<?> method_15200(Typed<?> typed) {
		return typed.update(DSL.remainderFinder(), dynamic -> dynamic.getByte("Color") == 10 ? dynamic.set("Color", dynamic.createByte((byte)16)) : dynamic);
	}
}
