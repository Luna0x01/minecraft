package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;

public class class_4498 extends DataFix {
	public class_4498(Schema schema, boolean bl) {
		super(schema, bl);
	}

	public TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(class_3402.field_16597);
		Type<?> type2 = this.getOutputSchema().getType(class_3402.field_16597);
		Type<Pair<String, Either<Integer, String>>> type3 = DSL.named(class_3402.field_16597.typeName(), DSL.or(DSL.intType(), DSL.namespacedString()));
		Type<Pair<String, String>> type4 = DSL.named(class_3402.field_16597.typeName(), DSL.namespacedString());
		if (Objects.equals(type, type3) && Objects.equals(type2, type4)) {
			return this.fixTypeEverywhere(
				"BlockNameFlatteningFix",
				type3,
				type4,
				dynamicOps -> pair -> pair.mapSecond(
							either -> (String)either.map(class_4500::method_21600, string -> class_4500.method_21604(class_3415.method_15286(string)))
						)
			);
		} else {
			throw new IllegalStateException("Expected and actual types don't match.");
		}
	}
}
