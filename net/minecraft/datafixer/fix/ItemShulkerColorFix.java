package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.class_3395;
import net.minecraft.class_3402;

public class ItemShulkerColorFix extends class_3395 {
	public ItemShulkerColorFix(Schema schema, boolean bl) {
		super(schema, bl, "BlockEntityShulkerBoxColorFix", class_3402.field_16591, "minecraft:shulker_box");
	}

	@Override
	protected Typed<?> method_15200(Typed<?> typed) {
		return typed.update(DSL.remainderFinder(), dynamic -> dynamic.remove("Color"));
	}
}
