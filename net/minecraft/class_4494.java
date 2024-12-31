package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Optional;

public class class_4494 extends class_3395 {
	public class_4494(Schema schema, boolean bl) {
		super(schema, bl, "BlockEntityBlockStateFix", class_3402.field_16591, "minecraft:piston");
	}

	@Override
	protected Typed<?> method_15200(Typed<?> typed) {
		Type<?> type = this.getOutputSchema().getChoiceType(class_3402.field_16591, "minecraft:piston");
		Type<?> type2 = type.findFieldType("blockState");
		OpticFinder<?> opticFinder = DSL.fieldFinder("blockState", type2);
		Dynamic<?> dynamic = (Dynamic<?>)typed.get(DSL.remainderFinder());
		int i = dynamic.getInt("blockId");
		dynamic = dynamic.remove("blockId");
		int j = dynamic.getInt("blockData") & 15;
		dynamic = dynamic.remove("blockData");
		Dynamic<?> dynamic2 = class_4500.method_21605(i << 4 | j);
		Typed<?> typed2 = (Typed<?>)type.pointTyped(typed.getOps()).orElseThrow(() -> new IllegalStateException("Could not create new piston block entity."));
		return typed2.set(DSL.remainderFinder(), dynamic)
			.set(
				opticFinder,
				(Typed)((Optional)type2.readTyped(dynamic2).getSecond()).orElseThrow(() -> new IllegalStateException("Could not parse newly created block state tag."))
			);
	}
}
