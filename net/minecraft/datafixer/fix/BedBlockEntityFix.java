package net.minecraft.datafixer.fix;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.datafixer.TypeReferences;

public class BedBlockEntityFix extends DataFix {
	public BedBlockEntityFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	public TypeRewriteRule makeRule() {
		Type<?> type = this.getOutputSchema().getType(TypeReferences.CHUNK);
		Type<?> type2 = type.findFieldType("Level");
		if (!(type2.findFieldType("TileEntities") instanceof ListType<?> listType)) {
			throw new IllegalStateException("Tile entity type is not a list type.");
		} else {
			return this.method_15506(type2, listType);
		}
	}

	private <TE> TypeRewriteRule method_15506(Type<?> type, ListType<TE> listType) {
		Type<TE> type2 = listType.getElement();
		OpticFinder<?> opticFinder = DSL.fieldFinder("Level", type);
		OpticFinder<List<TE>> opticFinder2 = DSL.fieldFinder("TileEntities", listType);
		int i = 416;
		return TypeRewriteRule.seq(
			this.fixTypeEverywhere(
				"InjectBedBlockEntityType",
				this.getInputSchema().findChoiceType(TypeReferences.BLOCK_ENTITY),
				this.getOutputSchema().findChoiceType(TypeReferences.BLOCK_ENTITY),
				dynamicOps -> pair -> pair
			),
			this.fixTypeEverywhereTyped(
				"BedBlockEntityInjecter",
				this.getOutputSchema().getType(TypeReferences.CHUNK),
				typed -> {
					Typed<?> typed2 = typed.getTyped(opticFinder);
					Dynamic<?> dynamic = (Dynamic<?>)typed2.get(DSL.remainderFinder());
					int ix = dynamic.get("xPos").asInt(0);
					int j = dynamic.get("zPos").asInt(0);
					List<TE> list = Lists.newArrayList((Iterable)typed2.getOrCreate(opticFinder2));
					List<? extends Dynamic<?>> list2 = dynamic.get("Sections").asList(Function.identity());

					for (int k = 0; k < list2.size(); k++) {
						Dynamic<?> dynamic2 = (Dynamic<?>)list2.get(k);
						int l = dynamic2.get("Y").asInt(0);
						Stream<Integer> stream = dynamic2.get("Blocks").asStream().map(dynamicx -> dynamicx.asInt(0));
						int m = 0;

						for (int n : stream::iterator) {
							if (416 == (n & 0xFF) << 4) {
								int o = m & 15;
								int p = m >> 8 & 15;
								int q = m >> 4 & 15;
								Map<Dynamic<?>, Dynamic<?>> map = Maps.newHashMap();
								map.put(dynamic2.createString("id"), dynamic2.createString("minecraft:bed"));
								map.put(dynamic2.createString("x"), dynamic2.createInt(o + (ix << 4)));
								map.put(dynamic2.createString("y"), dynamic2.createInt(p + (l << 4)));
								map.put(dynamic2.createString("z"), dynamic2.createInt(q + (j << 4)));
								map.put(dynamic2.createString("color"), dynamic2.createShort((short)14));
								list.add(
									((Pair)type2.read(dynamic2.createMap(map)).result().orElseThrow(() -> new IllegalStateException("Could not parse newly created bed block entity.")))
										.getFirst()
								);
							}

							m++;
						}
					}

					return !list.isEmpty() ? typed.set(opticFinder, typed2.set(opticFinder2, list)) : typed;
				}
			)
		);
	}
}
