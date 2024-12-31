package net.minecraft;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.stream.Stream;

public class class_3405 extends DataFix {
	public class_3405(Schema schema, boolean bl) {
		super(schema, bl);
	}

	public TypeRewriteRule makeRule() {
		return this.writeFixAndRead(
			"SavedDataVillageCropFix", this.getInputSchema().getType(class_3402.field_16600), this.getOutputSchema().getType(class_3402.field_16600), this::method_15244
		);
	}

	private <T> Dynamic<T> method_15244(Dynamic<T> dynamic) {
		return dynamic.update("Children", class_3405::method_15247);
	}

	private static <T> Dynamic<T> method_15247(Dynamic<T> dynamic) {
		return (Dynamic<T>)dynamic.getStream().map(class_3405::method_15246).map(dynamic::createList).orElse(dynamic);
	}

	private static Stream<? extends Dynamic<?>> method_15246(Stream<? extends Dynamic<?>> stream) {
		return stream.map(dynamic -> {
			String string = dynamic.getString("id");
			if ("ViF".equals(string)) {
				return method_15248(dynamic);
			} else {
				return "ViDF".equals(string) ? method_15249(dynamic) : dynamic;
			}
		});
	}

	private static <T> Dynamic<T> method_15248(Dynamic<T> dynamic) {
		dynamic = method_15245(dynamic, "CA");
		return method_15245(dynamic, "CB");
	}

	private static <T> Dynamic<T> method_15249(Dynamic<T> dynamic) {
		dynamic = method_15245(dynamic, "CA");
		dynamic = method_15245(dynamic, "CB");
		dynamic = method_15245(dynamic, "CC");
		return method_15245(dynamic, "CD");
	}

	private static <T> Dynamic<T> method_15245(Dynamic<T> dynamic, String string) {
		return dynamic.get(string).flatMap(Dynamic::getNumberValue).isPresent() ? dynamic.set(string, class_4500.method_21605(dynamic.getInt(string) << 4)) : dynamic;
	}
}
