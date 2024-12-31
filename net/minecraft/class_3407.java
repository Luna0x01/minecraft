package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;

public abstract class class_3407 extends DataFix {
	private final String field_16607;

	public class_3407(String string, Schema schema, boolean bl) {
		super(schema, bl);
		this.field_16607 = string;
	}

	public TypeRewriteRule makeRule() {
		TaggedChoiceType<String> taggedChoiceType = this.getInputSchema().findChoiceType(class_3402.field_16596);
		TaggedChoiceType<String> taggedChoiceType2 = this.getOutputSchema().findChoiceType(class_3402.field_16596);
		Type<Pair<String, String>> type = DSL.named(class_3402.field_16594.typeName(), DSL.namespacedString());
		if (!Objects.equals(this.getOutputSchema().getType(class_3402.field_16594), type)) {
			throw new IllegalStateException("Entity name type is not what was expected.");
		} else {
			return TypeRewriteRule.seq(this.fixTypeEverywhere(this.field_16607, taggedChoiceType, taggedChoiceType2, dynamicOps -> pair -> pair.mapFirst(string -> {
						String string2 = this.method_15257(string);
						Type<?> typex = (Type<?>)taggedChoiceType.types().get(string);
						Type<?> type2 = (Type<?>)taggedChoiceType2.types().get(string2);
						if (!type2.equals(typex, true, true)) {
							throw new IllegalStateException(String.format("Dynamic type check failed: %s not equal to %s", type2, typex));
						} else {
							return string2;
						}
					})), this.fixTypeEverywhere(this.field_16607 + " for entity name", type, dynamicOps -> pair -> pair.mapSecond(this::method_15257)));
		}
	}

	protected abstract String method_15257(String string);
}
