package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.class_3402;

public class BedItemColorFix extends DataFix {
	public BedItemColorFix(Schema schema, boolean bl) {
		super(schema, bl);
	}

	public TypeRewriteRule makeRule() {
		OpticFinder<Pair<String, String>> opticFinder = DSL.fieldFinder("id", DSL.named(class_3402.field_16598.typeName(), DSL.namespacedString()));
		return this.fixTypeEverywhereTyped("BedItemColorFix", this.getInputSchema().getType(class_3402.field_16592), typed -> {
			Optional<Pair<String, String>> optional = typed.getOptional(opticFinder);
			if (optional.isPresent() && Objects.equals(((Pair)optional.get()).getSecond(), "minecraft:bed")) {
				Dynamic<?> dynamic = (Dynamic<?>)typed.get(DSL.remainderFinder());
				if (dynamic.getShort("Damage") == 0) {
					return typed.set(DSL.remainderFinder(), dynamic.set("Damage", dynamic.createShort((short)14)));
				}
			}

			return typed;
		});
	}
}
