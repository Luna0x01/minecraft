package net.minecraft;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;

public class class_4524 extends DataFix {
	public class_4524(Schema schema, boolean bl) {
		super(schema, bl);
	}

	protected TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(class_3402.field_16600);
		Type<?> type2 = this.getOutputSchema().getType(class_3402.field_16600);
		return this.writeFixAndRead("IglooMetadataRemovalFix", type, type2, class_4524::method_21757);
	}

	private static <T> Dynamic<T> method_21757(Dynamic<T> dynamic) {
		boolean bl = (Boolean)dynamic.get("Children").flatMap(Dynamic::getStream).map(stream -> stream.allMatch(class_4524::method_21761)).orElse(false);
		return bl ? dynamic.set("id", dynamic.createString("Igloo")).remove("Children") : dynamic.update("Children", class_4524::method_21759);
	}

	private static <T> Dynamic<T> method_21759(Dynamic<T> dynamic) {
		return (Dynamic<T>)dynamic.getStream().map(stream -> stream.filter(dynamicx -> !method_21761(dynamicx))).map(dynamic::createList).orElse(dynamic);
	}

	private static boolean method_21761(Dynamic<?> dynamic) {
		return dynamic.getString("id").equals("Iglu");
	}
}
