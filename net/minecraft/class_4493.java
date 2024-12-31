package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;

public class class_4493 extends class_3395 {
	public class_4493(Schema schema, boolean bl) {
		super(schema, bl, "BlockEntityBannerColorFix", class_3402.field_16591, "minecraft:banner");
	}

	public Dynamic<?> method_21571(Dynamic<?> dynamic) {
		dynamic = dynamic.update("Base", dynamicx -> dynamicx.createInt(15 - dynamicx.getNumberValue(0).intValue()));
		return dynamic.update(
			"Patterns",
			dynamicx -> (Dynamic)DataFixUtils.orElse(
					dynamicx.getStream()
						.map(stream -> stream.map(dynamicxx -> dynamicxx.update("Color", dynamicxxx -> dynamicxxx.createInt(15 - dynamicxxx.getNumberValue(0).intValue()))))
						.map(dynamicx::createList),
					dynamicx
				)
		);
	}

	@Override
	protected Typed<?> method_15200(Typed<?> typed) {
		return typed.update(DSL.remainderFinder(), this::method_21571);
	}
}
