package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import java.util.Objects;

public class class_4518 extends class_3407 {
	public static final Map<String, String> field_22336 = ImmutableMap.builder().put("minecraft:puffer_fish_spawn_egg", "minecraft:pufferfish_spawn_egg").build();

	public class_4518(Schema schema, boolean bl) {
		super("EntityPufferfishRenameFix", schema, bl);
	}

	@Override
	protected String method_15257(String string) {
		return Objects.equals("minecraft:puffer_fish", string) ? "minecraft:pufferfish" : string;
	}
}
