package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.function.Function;

public abstract class class_3385 extends DataFix {
	private final String field_16552;

	public class_3385(Schema schema, String string) {
		super(schema, false);
		this.field_16552 = string;
	}

	public TypeRewriteRule makeRule() {
		Type<Pair<String, String>> type = DSL.named(class_3402.field_16598.typeName(), DSL.namespacedString());
		if (!Objects.equals(this.getInputSchema().getType(class_3402.field_16598), type)) {
			throw new IllegalStateException("item name type is not what was expected.");
		} else {
			return this.fixTypeEverywhere(this.field_16552, type, dynamicOps -> pair -> pair.mapSecond(this::method_15116));
		}
	}

	protected abstract String method_15116(String string);

	public static DataFix method_15113(Schema schema, String string, Function<String, String> function) {
		return new class_3385(schema, string) {
			@Override
			protected String method_15116(String string) {
				return (String)function.apply(string);
			}
		};
	}
}
