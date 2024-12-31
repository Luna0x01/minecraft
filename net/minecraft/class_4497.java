package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;

public class class_4497 extends class_3395 {
	public class_4497(Schema schema, boolean bl) {
		super(schema, bl, "BlockEntityKeepPacked", class_3402.field_16591, "DUMMY");
	}

	private static Dynamic<?> method_21586(Dynamic<?> dynamic) {
		return dynamic.set("keepPacked", dynamic.createBoolean(true));
	}

	@Override
	protected Typed<?> method_15200(Typed<?> typed) {
		return typed.update(DSL.remainderFinder(), class_4497::method_21586);
	}
}
