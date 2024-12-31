package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;

public class class_4513 extends class_3407 {
	public static final Map<String, String> field_22329 = ImmutableMap.builder()
		.put("minecraft:salmon_mob", "minecraft:salmon")
		.put("minecraft:cod_mob", "minecraft:cod")
		.build();
	public static final Map<String, String> field_22330 = ImmutableMap.builder()
		.put("minecraft:salmon_mob_spawn_egg", "minecraft:salmon_spawn_egg")
		.put("minecraft:cod_mob_spawn_egg", "minecraft:cod_spawn_egg")
		.build();

	public class_4513(Schema schema, boolean bl) {
		super("EntityCodSalmonFix", schema, bl);
	}

	@Override
	protected String method_15257(String string) {
		return (String)field_22329.getOrDefault(string, string);
	}
}
