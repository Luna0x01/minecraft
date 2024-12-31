package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;

public class class_4501 extends DataFix {
	public class_4501(Schema schema, boolean bl) {
		super(schema, bl);
	}

	public TypeRewriteRule makeRule() {
		return this.fixTypeEverywhereTyped(
			"BlockStateStructureTemplateFix",
			this.getInputSchema().getType(class_3402.field_16593),
			typed -> typed.update(DSL.remainderFinder(), class_4500::method_21602)
		);
	}
}
