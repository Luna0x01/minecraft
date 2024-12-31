package net.minecraft.datafixer.fix;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.class_3402;

public class BedBlockEntityFix extends DataFix {
	public BedBlockEntityFix(Schema schema, boolean bl) {
		super(schema, bl);
	}

	public TypeRewriteRule makeRule() {
		Type<?> type = this.getOutputSchema().getType(class_3402.field_16584);
		Type<?> type2 = type.findFieldType("Level");
		Type<?> type3 = type2.findFieldType("TileEntities");
		if (!(type3 instanceof ListType)) {
			throw new IllegalStateException("Tile entity type is not a list type.");
		} else {
			ListType<?> listType = (ListType<?>)type3;
			return this.method_21565(type2, listType);
		}
	}

	private <TE> TypeRewriteRule method_21565(Type<?> type, ListType<TE> listType) {
		Type<TE> type2 = listType.getElement();
		OpticFinder<?> opticFinder = DSL.fieldFinder("Level", type);
		OpticFinder<List<TE>> opticFinder2 = DSL.fieldFinder("TileEntities", listType);
		int i = 416;
		return TypeRewriteRule.seq(
			this.fixTypeEverywhere(
				"InjectBedBlockEntityType",
				this.getInputSchema().findChoiceType(class_3402.field_16591),
				this.getOutputSchema().findChoiceType(class_3402.field_16591),
				dynamicOps -> pair -> pair
			),
			this.fixTypeEverywhereTyped(
				"BedBlockEntityInjecter",
				this.getOutputSchema().getType(class_3402.field_16584),
				typed -> {
					Typed<?> typed2 = typed.getTyped(opticFinder);
					Dynamic<?> dynamic = (Dynamic<?>)typed2.get(DSL.remainderFinder());
					int ix = dynamic.getInt("xPos");
					int j = dynamic.getInt("zPos");
					List<TE> list = Lists.newArrayList((Iterable)typed2.getOrCreate(opticFinder2));
					List<? extends Dynamic<?>> list2 = (List<? extends Dynamic<?>>)((Stream)dynamic.get("Sections").flatMap(Dynamic::getStream).orElse(Stream.empty()))
						.collect(Collectors.toList());

					for (int k = 0; k < list2.size(); k++) {
						Dynamic<?> dynamic2 = (Dynamic<?>)list2.get(k);
						int l = ((Number)dynamic2.get("Y").flatMap(Dynamic::getNumberValue).orElse(0)).intValue();
						Stream<Integer> stream = ((Stream)dynamic2.get("Blocks").flatMap(Dynamic::getStream).orElse(Stream.empty()))
							.map(dynamicx -> ((Number)dynamicx.getNumberValue().orElse(0)).intValue());
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
									((Optional)type2.read(dynamic2.createMap(map)).getSecond())
										.orElseThrow(() -> new IllegalStateException("Could not parse newly created bed block entity."))
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
