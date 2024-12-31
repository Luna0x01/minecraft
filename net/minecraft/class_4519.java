package net.minecraft;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import com.mojang.datafixers.util.Pair;

public abstract class class_4519 extends DataFix {
	protected final String field_22337;

	public class_4519(String string, Schema schema, boolean bl) {
		super(schema, bl);
		this.field_22337 = string;
	}

	public TypeRewriteRule makeRule() {
		TaggedChoiceType<String> taggedChoiceType = this.getInputSchema().findChoiceType(class_3402.field_16596);
		TaggedChoiceType<String> taggedChoiceType2 = this.getOutputSchema().findChoiceType(class_3402.field_16596);
		return this.fixTypeEverywhere(this.field_22337, taggedChoiceType, taggedChoiceType2, dynamicOps -> pair -> {
				String string = (String)pair.getFirst();
				Type<?> type = (Type<?>)taggedChoiceType.types().get(string);
				Pair<String, Typed<?>> pair2 = this.method_21739(string, this.method_21738(pair.getSecond(), dynamicOps, type));
				Type<?> type2 = (Type<?>)taggedChoiceType2.types().get(pair2.getFirst());
				if (!type2.equals(((Typed)pair2.getSecond()).getType(), true, true)) {
					throw new IllegalStateException(String.format("Dynamic type check failed: %s not equal to %s", type2, ((Typed)pair2.getSecond()).getType()));
				} else {
					return Pair.of(pair2.getFirst(), ((Typed)pair2.getSecond()).getValue());
				}
			});
	}

	private <A> Typed<A> method_21738(Object object, DynamicOps<?> dynamicOps, Type<A> type) {
		return new Typed(type, dynamicOps, object);
	}

	protected abstract Pair<String, Typed<?>> method_21739(String string, Typed<?> typed);
}
