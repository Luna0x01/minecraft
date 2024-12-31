package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.scoreboard.GenericScoreboardCriteria;

public class class_3397 extends DataFix {
	public class_3397(Schema schema, boolean bl) {
		super(schema, bl);
	}

	private static GenericScoreboardCriteria.class_4104 method_15209(String string) {
		return string.equals("health") ? GenericScoreboardCriteria.class_4104.HEARTS : GenericScoreboardCriteria.class_4104.INTEGER;
	}

	protected TypeRewriteRule makeRule() {
		Type<Pair<String, Dynamic<?>>> type = DSL.named(class_3402.field_16601.typeName(), DSL.remainderType());
		if (!Objects.equals(type, this.getInputSchema().getType(class_3402.field_16601))) {
			throw new IllegalStateException("Objective type is not what was expected.");
		} else {
			return this.fixTypeEverywhere("ObjectiveRenderTypeFix", type, dynamicOps -> pair -> pair.mapSecond(dynamic -> {
						Optional<String> optional = dynamic.get("RenderType").flatMap(Dynamic::getStringValue);
						if (!optional.isPresent()) {
							String string = dynamic.getString("CriteriaName");
							GenericScoreboardCriteria.class_4104 lv = method_15209(string);
							return dynamic.set("RenderType", dynamic.createString(lv.method_18132()));
						} else {
							return dynamic;
						}
					}));
		}
	}
}
