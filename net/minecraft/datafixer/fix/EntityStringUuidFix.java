package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.UUID;
import net.minecraft.class_3402;

public class EntityStringUuidFix extends DataFix {
	public EntityStringUuidFix(Schema schema, boolean bl) {
		super(schema, bl);
	}

	public TypeRewriteRule makeRule() {
		return this.fixTypeEverywhereTyped(
			"EntityStringUuidFix",
			this.getInputSchema().getType(class_3402.field_16596),
			typed -> typed.update(
					DSL.remainderFinder(),
					dynamic -> {
						if (dynamic.get("UUID").flatMap(Dynamic::getStringValue).isPresent()) {
							UUID uUID = UUID.fromString(dynamic.getString("UUID"));
							return dynamic.remove("UUID")
								.set("UUIDMost", dynamic.createLong(uUID.getMostSignificantBits()))
								.set("UUIDLeast", dynamic.createLong(uUID.getLeastSignificantBits()));
						} else {
							return dynamic;
						}
					}
				)
		);
	}
}
