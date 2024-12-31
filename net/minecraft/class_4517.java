package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import net.minecraft.util.Identifier;

public class class_4517 extends class_3395 {
	private static final Map<String, String> field_22335 = (Map<String, String>)DataFixUtils.make(Maps.newHashMap(), hashMap -> {
		hashMap.put("donkeykong", "donkey_kong");
		hashMap.put("burningskull", "burning_skull");
		hashMap.put("skullandroses", "skull_and_roses");
	});

	public class_4517(Schema schema, boolean bl) {
		super(schema, bl, "EntityPaintingMotiveFix", class_3402.field_16596, "minecraft:painting");
	}

	public Dynamic<?> method_21730(Dynamic<?> dynamic) {
		Optional<String> optional = dynamic.get("Motive").flatMap(Dynamic::getStringValue);
		if (optional.isPresent()) {
			String string = ((String)optional.get()).toLowerCase(Locale.ROOT);
			return dynamic.set("Motive", dynamic.createString(new Identifier((String)field_22335.getOrDefault(string, string)).toString()));
		} else {
			return dynamic;
		}
	}

	@Override
	protected Typed<?> method_15200(Typed<?> typed) {
		return typed.update(DSL.remainderFinder(), this::method_21730);
	}
}
