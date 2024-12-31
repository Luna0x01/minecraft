package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.class_3402;

public class MobSpawnerEntityIdentifiersFix extends DataFix {
	public MobSpawnerEntityIdentifiersFix(Schema schema, boolean bl) {
		super(schema, bl);
	}

	private Dynamic<?> method_15196(Dynamic<?> dynamic) {
		if (!"MobSpawner".equals(dynamic.getString("id"))) {
			return dynamic;
		} else {
			Optional<String> optional = dynamic.get("EntityId").flatMap(Dynamic::getStringValue);
			if (optional.isPresent()) {
				Dynamic<?> dynamic2 = (Dynamic<?>)DataFixUtils.orElse(dynamic.get("SpawnData"), dynamic.emptyMap());
				dynamic2 = dynamic2.set("id", dynamic2.createString(((String)optional.get()).isEmpty() ? "Pig" : (String)optional.get()));
				dynamic = dynamic.set("SpawnData", dynamic2);
				dynamic = dynamic.remove("EntityId");
			}

			Optional<? extends Stream<? extends Dynamic<?>>> optional2 = dynamic.get("SpawnPotentials").flatMap(Dynamic::getStream);
			if (optional2.isPresent()) {
				dynamic = dynamic.set(
					"SpawnPotentials",
					dynamic.createList(
						((Stream)optional2.get())
							.map(
								dynamicx -> {
									Optional<String> optionalx = dynamicx.get("Type").flatMap(Dynamic::getStringValue);
									if (optionalx.isPresent()) {
										Dynamic<?> dynamic2 = ((Dynamic)DataFixUtils.orElse(dynamicx.get("Properties"), dynamicx.emptyMap()))
											.set("id", dynamicx.createString((String)optionalx.get()));
										return dynamicx.set("Entity", dynamic2).remove("Type").remove("Properties");
									} else {
										return dynamicx;
									}
								}
							)
					)
				);
			}

			return dynamic;
		}
	}

	public TypeRewriteRule makeRule() {
		Type<?> type = this.getOutputSchema().getType(class_3402.field_16599);
		return this.fixTypeEverywhereTyped("MobSpawnerEntityIdentifiersFix", this.getInputSchema().getType(class_3402.field_16599), type, typed -> {
			Dynamic<?> dynamic = (Dynamic<?>)typed.get(DSL.remainderFinder());
			dynamic = dynamic.set("id", dynamic.createString("MobSpawner"));
			Pair<?, ? extends Optional<? extends Typed<?>>> pair = type.readTyped(this.method_15196(dynamic));
			return !((Optional)pair.getSecond()).isPresent() ? typed : (Typed)((Optional)pair.getSecond()).get();
		});
	}
}
