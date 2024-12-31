package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Optional;
import net.minecraft.datafixer.fix.ItemIdFix;

public class class_4496 extends class_3395 {
	public class_4496(Schema schema, boolean bl) {
		super(schema, bl, "BlockEntityJukeboxFix", class_3402.field_16591, "minecraft:jukebox");
	}

	@Override
	protected Typed<?> method_15200(Typed<?> typed) {
		Type<?> type = this.getInputSchema().getChoiceType(class_3402.field_16591, "minecraft:jukebox");
		Type<?> type2 = type.findFieldType("RecordItem");
		OpticFinder<?> opticFinder = DSL.fieldFinder("RecordItem", type2);
		Dynamic<?> dynamic = (Dynamic<?>)typed.get(DSL.remainderFinder());
		int i = dynamic.getInt("Record");
		if (i > 0) {
			dynamic.remove("Record");
			String string = class_3389.method_15138(ItemIdFix.method_21767(i), 0);
			if (string != null) {
				Dynamic<?> dynamic2 = dynamic.emptyMap();
				dynamic2 = dynamic2.set("id", dynamic2.createString(string));
				dynamic2 = dynamic2.set("Count", dynamic2.createByte((byte)1));
				return typed.set(
						opticFinder, (Typed)((Optional)type2.readTyped(dynamic2).getSecond()).orElseThrow(() -> new IllegalStateException("Could not create record item stack."))
					)
					.set(DSL.remainderFinder(), dynamic);
			}
		}

		return typed;
	}
}
