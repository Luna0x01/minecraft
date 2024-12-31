package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixers.TypeReferences;

public class OptionsAddTextBackgroundFix extends DataFix {
	public OptionsAddTextBackgroundFix(Schema schema, boolean bl) {
		super(schema, bl);
	}

	public TypeRewriteRule makeRule() {
		return this.fixTypeEverywhereTyped(
			"OptionsAddTextBackgroundFix",
			this.getInputSchema().getType(TypeReferences.OPTIONS),
			typed -> typed.update(
					DSL.remainderFinder(),
					dynamic -> (Dynamic)DataFixUtils.orElse(
							dynamic.get("chatOpacity")
								.asString()
								.map(string -> dynamic.set("textBackgroundOpacity", dynamic.createDouble(this.convertToTextBackgroundOpacity(string)))),
							dynamic
						)
				)
		);
	}

	private double convertToTextBackgroundOpacity(String string) {
		try {
			double d = 0.9 * Double.parseDouble(string) + 0.1;
			return d / 2.0;
		} catch (NumberFormatException var4) {
			return 0.5;
		}
	}
}
