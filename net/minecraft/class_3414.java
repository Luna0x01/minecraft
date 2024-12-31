package net.minecraft;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;

public class class_3414 extends DataFix {
	private final String field_16615;
	private final TypeReference field_16616;

	public class_3414(Schema schema, String string, TypeReference typeReference) {
		super(schema, true);
		this.field_16615 = string;
		this.field_16616 = typeReference;
	}

	protected TypeRewriteRule makeRule() {
		return this.writeAndRead(this.field_16615, this.getInputSchema().getType(this.field_16616), this.getOutputSchema().getType(this.field_16616));
	}
}
