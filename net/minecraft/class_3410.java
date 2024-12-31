package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class class_3410 extends DataFix {
	public class_3410(Schema schema, boolean bl) {
		super(schema, bl);
	}

	protected TypeRewriteRule makeRule() {
		Type<Pair<String, Dynamic<?>>> type = DSL.named(class_3402.field_16602.typeName(), DSL.remainderType());
		if (!Objects.equals(type, this.getInputSchema().getType(class_3402.field_16602))) {
			throw new IllegalStateException("Team type is not what was expected.");
		} else {
			return this.fixTypeEverywhere(
				"TeamDisplayNameFix",
				type,
				dynamicOps -> pair -> pair.mapSecond(
							dynamic -> dynamic.update(
									"DisplayName",
									dynamic2 -> (Dynamic)DataFixUtils.orElse(
											dynamic2.getStringValue().map(string -> Text.Serializer.serialize(new LiteralText(string))).map(dynamic::createString), dynamic2
										)
								)
						)
			);
		}
	}
}
