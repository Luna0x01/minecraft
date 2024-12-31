package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.class_3395;
import net.minecraft.class_3402;

public class EntityShulkerColorFix extends class_3395 {
	public EntityShulkerColorFix(Schema schema, boolean bl) {
		super(schema, bl, "EntityShulkerColorFix", class_3402.field_16596, "minecraft:shulker");
	}

	public Dynamic<?> method_21748(Dynamic<?> dynamic) {
		return !dynamic.get("Color").map(Dynamic::getNumberValue).isPresent() ? dynamic.set("Color", dynamic.createByte((byte)10)) : dynamic;
	}

	@Override
	protected Typed<?> method_15200(Typed<?> typed) {
		return typed.update(DSL.remainderFinder(), this::method_21748);
	}
}
