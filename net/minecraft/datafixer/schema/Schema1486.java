package net.minecraft.datafixer.schema;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class Schema1486 extends IdentifierNormalizingSchema {
	public Schema1486(int i, Schema schema) {
		super(i, schema);
	}

	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
		map.put("minecraft:cod", (Supplier)map.remove("minecraft:cod_mob"));
		map.put("minecraft:salmon", (Supplier)map.remove("minecraft:salmon_mob"));
		return map;
	}
}
