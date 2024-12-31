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
import net.minecraft.class_3402;

public class BlockEntityShulkerBoxColorFix extends DataFix {
	public static final String[] SHULKERS = new String[]{
		"minecraft:white_shulker_box",
		"minecraft:orange_shulker_box",
		"minecraft:magenta_shulker_box",
		"minecraft:light_blue_shulker_box",
		"minecraft:yellow_shulker_box",
		"minecraft:lime_shulker_box",
		"minecraft:pink_shulker_box",
		"minecraft:gray_shulker_box",
		"minecraft:silver_shulker_box",
		"minecraft:cyan_shulker_box",
		"minecraft:purple_shulker_box",
		"minecraft:blue_shulker_box",
		"minecraft:brown_shulker_box",
		"minecraft:green_shulker_box",
		"minecraft:red_shulker_box",
		"minecraft:black_shulker_box"
	};

	public BlockEntityShulkerBoxColorFix(Schema schema, boolean bl) {
		super(schema, bl);
	}

	public TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(class_3402.field_16592);
		OpticFinder<Pair<String, String>> opticFinder = DSL.fieldFinder("id", DSL.named(class_3402.field_16598.typeName(), DSL.namespacedString()));
		OpticFinder<?> opticFinder2 = type.findField("tag");
		OpticFinder<?> opticFinder3 = opticFinder2.type().findField("BlockEntityTag");
		return this.fixTypeEverywhereTyped(
			"ItemShulkerBoxColorFix",
			type,
			typed -> {
				Optional<Pair<String, String>> optional = typed.getOptional(opticFinder);
				if (optional.isPresent() && Objects.equals(((Pair)optional.get()).getSecond(), "minecraft:shulker_box")) {
					Optional<? extends Typed<?>> optional2 = typed.getOptionalTyped(opticFinder2);
					if (optional2.isPresent()) {
						Typed<?> typed2 = (Typed<?>)optional2.get();
						Optional<? extends Typed<?>> optional3 = typed2.getOptionalTyped(opticFinder3);
						if (optional3.isPresent()) {
							Typed<?> typed3 = (Typed<?>)optional3.get();
							Dynamic<?> dynamic = (Dynamic<?>)typed3.get(DSL.remainderFinder());
							int i = dynamic.getInt("Color");
							dynamic.remove("Color");
							return typed.set(opticFinder2, typed2.set(opticFinder3, typed3.set(DSL.remainderFinder(), dynamic)))
								.set(opticFinder, Pair.of(class_3402.field_16598.typeName(), SHULKERS[i % 16]));
						}
					}
				}

				return typed;
			}
		);
	}
}
