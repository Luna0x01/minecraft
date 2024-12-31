package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.class_3402;

public class ItemBannerColorFix extends DataFix {
	public ItemBannerColorFix(Schema schema, boolean bl) {
		super(schema, bl);
	}

	public TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(class_3402.field_16592);
		OpticFinder<Pair<String, String>> opticFinder = DSL.fieldFinder("id", DSL.named(class_3402.field_16598.typeName(), DSL.namespacedString()));
		OpticFinder<?> opticFinder2 = type.findField("tag");
		OpticFinder<?> opticFinder3 = opticFinder2.type().findField("BlockEntityTag");
		return this.fixTypeEverywhereTyped(
			"ItemBannerColorFix",
			type,
			typed -> {
				Optional<Pair<String, String>> optional = typed.getOptional(opticFinder);
				if (optional.isPresent() && Objects.equals(((Pair)optional.get()).getSecond(), "minecraft:banner")) {
					Dynamic<?> dynamic = (Dynamic<?>)typed.get(DSL.remainderFinder());
					Optional<? extends Typed<?>> optional2 = typed.getOptionalTyped(opticFinder2);
					if (optional2.isPresent()) {
						Typed<?> typed2 = (Typed<?>)optional2.get();
						Optional<? extends Typed<?>> optional3 = typed2.getOptionalTyped(opticFinder3);
						if (optional3.isPresent()) {
							Typed<?> typed3 = (Typed<?>)optional3.get();
							Dynamic<?> dynamic2 = (Dynamic<?>)typed2.get(DSL.remainderFinder());
							Dynamic<?> dynamic3 = (Dynamic<?>)typed3.getOrCreate(DSL.remainderFinder());
							if (dynamic3.get("Base").flatMap(Dynamic::getNumberValue).isPresent()) {
								dynamic = dynamic.set("Damage", dynamic.createShort((short)(dynamic3.getShort("Base") & 15)));
								Optional<? extends Dynamic<?>> optional4 = dynamic2.get("display");
								if (optional4.isPresent()) {
									Dynamic<?> dynamic4 = (Dynamic<?>)optional4.get();
									if (Objects.equals(dynamic4, dynamic4.emptyMap().merge(dynamic4.createString("Lore"), dynamic4.createList(Stream.of(dynamic4.createString("(+NBT")))))
										)
									 {
										return typed.set(DSL.remainderFinder(), dynamic);
									}
								}

								dynamic3.remove("Base");
								return typed.set(DSL.remainderFinder(), dynamic).set(opticFinder2, typed2.set(opticFinder3, typed3.set(DSL.remainderFinder(), dynamic3)));
							}
						}
					}

					return typed.set(DSL.remainderFinder(), dynamic);
				} else {
					return typed;
				}
			}
		);
	}
}
