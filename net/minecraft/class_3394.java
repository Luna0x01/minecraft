package net.minecraft;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.math.NumberUtils;

public class class_3394 extends DataFix {
	private static final Splitter field_16571 = Splitter.on(';').limit(5);
	private static final Splitter field_16572 = Splitter.on(',');
	private static final Splitter field_16573 = Splitter.on('x').limit(2);
	private static final Splitter field_16574 = Splitter.on('*').limit(2);
	private static final Splitter field_16575 = Splitter.on(':').limit(3);

	public class_3394(Schema schema, boolean bl) {
		super(schema, bl);
	}

	public TypeRewriteRule makeRule() {
		return this.fixTypeEverywhereTyped(
			"LevelFlatGeneratorInfoFix", this.getInputSchema().getType(class_3402.field_16582), typed -> typed.update(DSL.remainderFinder(), this::method_15192)
		);
	}

	private Dynamic<?> method_15192(Dynamic<?> dynamic) {
		return dynamic.getString("generatorName").equalsIgnoreCase("flat")
			? dynamic.update(
				"generatorOptions", dynamicx -> (Dynamic)DataFixUtils.orElse(dynamicx.getStringValue().map(this::method_15194).map(dynamicx::createString), dynamicx)
			)
			: dynamic;
	}

	@VisibleForTesting
	String method_15194(String string) {
		if (string.isEmpty()) {
			return "minecraft:bedrock,2*minecraft:dirt,minecraft:grass_block;1;village";
		} else {
			Iterator<String> iterator = field_16571.split(string).iterator();
			String string2 = (String)iterator.next();
			int i;
			String string3;
			if (iterator.hasNext()) {
				i = NumberUtils.toInt(string2, 0);
				string3 = (String)iterator.next();
			} else {
				i = 0;
				string3 = string2;
			}

			if (i >= 0 && i <= 3) {
				StringBuilder stringBuilder = new StringBuilder();
				Splitter splitter = i < 3 ? field_16573 : field_16574;
				stringBuilder.append((String)StreamSupport.stream(field_16572.split(string3).spliterator(), false).map(stringx -> {
					List<String> list = splitter.splitToList(stringx);
					int jx;
					String string2x;
					if (list.size() == 2) {
						jx = NumberUtils.toInt((String)list.get(0));
						string2x = (String)list.get(1);
					} else {
						jx = 1;
						string2x = (String)list.get(0);
					}

					List<String> list2 = field_16575.splitToList(string2x);
					int l = ((String)list2.get(0)).equals("minecraft") ? 1 : 0;
					String string4x = (String)list2.get(l);
					int m = i == 3 ? class_4512.method_21694("minecraft:" + string4x) : NumberUtils.toInt(string4x, 0);
					int n = l + 1;
					int o = list2.size() > n ? NumberUtils.toInt((String)list2.get(n), 0) : 0;
					return (jx == 1 ? "" : jx + "*") + class_4500.method_21605(m << 4 | o).getString("Name");
				}).collect(Collectors.joining(",")));

				while (iterator.hasNext()) {
					stringBuilder.append(';').append((String)iterator.next());
				}

				return stringBuilder.toString();
			} else {
				return "minecraft:bedrock,2*minecraft:dirt,minecraft:grass_block;1;village";
			}
		}
	}
}
