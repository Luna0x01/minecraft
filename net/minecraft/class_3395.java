package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;

public abstract class class_3395 extends DataFix {
	private final String field_16576;
	private final String field_16577;
	private final TypeReference field_16578;

	public class_3395(Schema schema, boolean bl, String string, TypeReference typeReference, String string2) {
		super(schema, bl);
		this.field_16576 = string;
		this.field_16578 = typeReference;
		this.field_16577 = string2;
	}

	public TypeRewriteRule makeRule() {
		OpticFinder<?> opticFinder = DSL.namedChoice(this.field_16577, this.getInputSchema().getChoiceType(this.field_16578, this.field_16577));
		return this.fixTypeEverywhereTyped(
			this.field_16576,
			this.getInputSchema().getType(this.field_16578),
			this.getOutputSchema().getType(this.field_16578),
			typed -> typed.updateTyped(opticFinder, this.getOutputSchema().getChoiceType(this.field_16578, this.field_16577), this::method_15200)
		);
	}

	protected abstract Typed<?> method_15200(Typed<?> typed);
}
