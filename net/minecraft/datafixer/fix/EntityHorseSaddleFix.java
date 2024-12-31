package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import net.minecraft.class_3395;
import net.minecraft.class_3402;

public class EntityHorseSaddleFix extends class_3395 {
	public EntityHorseSaddleFix(Schema schema, boolean bl) {
		super(schema, bl, "EntityHorseSaddleFix", class_3402.field_16596, "EntityHorse");
	}

	@Override
	protected Typed<?> method_15200(Typed<?> typed) {
		OpticFinder<Pair<String, String>> opticFinder = DSL.fieldFinder("id", DSL.named(class_3402.field_16598.typeName(), DSL.namespacedString()));
		Type<?> type = this.getInputSchema().getTypeRaw(class_3402.field_16592);
		OpticFinder<?> opticFinder2 = DSL.fieldFinder("SaddleItem", type);
		Optional<? extends Typed<?>> optional = typed.getOptionalTyped(opticFinder2);
		Dynamic<?> dynamic = (Dynamic<?>)typed.get(DSL.remainderFinder());
		if (!optional.isPresent() && dynamic.getBoolean("Saddle")) {
			Typed<?> typed2 = (Typed<?>)type.pointTyped(typed.getOps()).orElseThrow(IllegalStateException::new);
			typed2 = typed2.set(opticFinder, Pair.of(class_3402.field_16598.typeName(), "minecraft:saddle"));
			Dynamic<?> dynamic2 = dynamic.emptyMap();
			dynamic2 = dynamic2.set("Count", dynamic2.createByte((byte)1));
			dynamic2 = dynamic2.set("Damage", dynamic2.createShort((short)0));
			typed2 = typed2.set(DSL.remainderFinder(), dynamic2);
			dynamic.remove("Saddle");
			return typed.set(opticFinder2, typed2).set(DSL.remainderFinder(), dynamic);
		} else {
			return typed;
		}
	}
}
