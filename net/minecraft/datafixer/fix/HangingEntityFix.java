package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.class_3402;

public class HangingEntityFix extends DataFix {
	private static final int[][] field_22334 = new int[][]{{0, 0, 1}, {-1, 0, 0}, {0, 0, -1}, {1, 0, 0}};

	public HangingEntityFix(Schema schema, boolean bl) {
		super(schema, bl);
	}

	private Dynamic<?> method_21724(Dynamic<?> dynamic, boolean bl, boolean bl2) {
		if ((bl || bl2) && !dynamic.get("Facing").flatMap(Dynamic::getNumberValue).isPresent()) {
			int i;
			if (dynamic.get("Direction").flatMap(Dynamic::getNumberValue).isPresent()) {
				i = dynamic.getByte("Direction") % field_22334.length;
				int[] is = field_22334[i];
				dynamic = dynamic.set("TileX", dynamic.createInt(dynamic.getInt("TileX") + is[0]));
				dynamic = dynamic.set("TileY", dynamic.createInt(dynamic.getInt("TileY") + is[1]));
				dynamic = dynamic.set("TileZ", dynamic.createInt(dynamic.getInt("TileZ") + is[2]));
				dynamic = dynamic.remove("Direction");
				if (bl2 && dynamic.get("ItemRotation").flatMap(Dynamic::getNumberValue).isPresent()) {
					dynamic = dynamic.set("ItemRotation", dynamic.createByte((byte)(dynamic.getByte("ItemRotation") * 2)));
				}
			} else {
				i = dynamic.getByte("Dir") % field_22334.length;
				dynamic = dynamic.remove("Dir");
			}

			dynamic = dynamic.set("Facing", dynamic.createByte((byte)i));
		}

		return dynamic;
	}

	public TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getChoiceType(class_3402.field_16596, "Painting");
		OpticFinder<?> opticFinder = DSL.namedChoice("Painting", type);
		Type<?> type2 = this.getInputSchema().getChoiceType(class_3402.field_16596, "ItemFrame");
		OpticFinder<?> opticFinder2 = DSL.namedChoice("ItemFrame", type2);
		Type<?> type3 = this.getInputSchema().getType(class_3402.field_16596);
		TypeRewriteRule typeRewriteRule = this.fixTypeEverywhereTyped(
			"EntityPaintingFix",
			type3,
			typed -> typed.updateTyped(opticFinder, type, typedx -> typedx.update(DSL.remainderFinder(), dynamic -> this.method_21724(dynamic, true, false)))
		);
		TypeRewriteRule typeRewriteRule2 = this.fixTypeEverywhereTyped(
			"EntityItemFrameFix",
			type3,
			typed -> typed.updateTyped(opticFinder2, type2, typedx -> typedx.update(DSL.remainderFinder(), dynamic -> this.method_21724(dynamic, false, true)))
		);
		return TypeRewriteRule.seq(typeRewriteRule, typeRewriteRule2);
	}
}
