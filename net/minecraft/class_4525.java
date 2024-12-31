package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Optional;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class class_4525 extends DataFix {
	public class_4525(Schema schema, boolean bl) {
		super(schema, bl);
	}

	private Dynamic<?> method_21764(Dynamic<?> dynamic) {
		Optional<? extends Dynamic<?>> optional = dynamic.get("display");
		if (optional.isPresent()) {
			Dynamic<?> dynamic2 = (Dynamic<?>)optional.get();
			Optional<String> optional2 = dynamic2.get("Name").flatMap(Dynamic::getStringValue);
			if (optional2.isPresent()) {
				dynamic2 = dynamic2.set("Name", dynamic2.createString(Text.Serializer.serialize(new LiteralText((String)optional2.get()))));
			} else {
				Optional<String> optional3 = dynamic2.get("LocName").flatMap(Dynamic::getStringValue);
				if (optional3.isPresent()) {
					dynamic2 = dynamic2.set("Name", dynamic2.createString(Text.Serializer.serialize(new TranslatableText((String)optional3.get()))));
					dynamic2 = dynamic2.remove("LocName");
				}
			}

			return dynamic.set("display", dynamic2);
		} else {
			return dynamic;
		}
	}

	public TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(class_3402.field_16592);
		OpticFinder<?> opticFinder = type.findField("tag");
		return this.fixTypeEverywhereTyped(
			"ItemCustomNameToComponentFix", type, typed -> typed.updateTyped(opticFinder, typedx -> typedx.update(DSL.remainderFinder(), this::method_21764))
		);
	}
}
