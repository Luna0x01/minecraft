package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.class_3395;
import net.minecraft.class_3402;

public class EntityArmorStandSilentFix extends class_3395 {
	public EntityArmorStandSilentFix(Schema schema, boolean bl) {
		super(schema, bl, "EntityArmorStandSilentFix", class_3402.field_16596, "ArmorStand");
	}

	public Dynamic<?> method_21683(Dynamic<?> dynamic) {
		return dynamic.getBoolean("Silent") && !dynamic.getBoolean("Marker") ? dynamic.remove("Silent") : dynamic;
	}

	@Override
	protected Typed<?> method_15200(Typed<?> typed) {
		return typed.update(DSL.remainderFinder(), this::method_21683);
	}
}
