package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public abstract class class_4499 extends DataFix {
	private final String field_22266;

	public class_4499(Schema schema, String string) {
		super(schema, false);
		this.field_22266 = string;
	}

	public TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(class_3402.field_16597);
		Type<Pair<String, String>> type2 = DSL.named(class_3402.field_16597.typeName(), DSL.namespacedString());
		if (!Objects.equals(type, type2)) {
			throw new IllegalStateException("block type is not what was expected.");
		} else {
			TypeRewriteRule typeRewriteRule = this.fixTypeEverywhere(this.field_22266 + " for block", type2, dynamicOps -> pair -> pair.mapSecond(this::method_21599));
			TypeRewriteRule typeRewriteRule2 = this.fixTypeEverywhereTyped(
				this.field_22266 + " for block_state", this.getInputSchema().getType(class_3402.field_16593), typed -> typed.update(DSL.remainderFinder(), dynamic -> {
						Optional<String> optional = dynamic.get("Name").flatMap(Dynamic::getStringValue);
						return optional.isPresent() ? dynamic.set("Name", dynamic.createString(this.method_21599((String)optional.get()))) : dynamic;
					})
			);
			return TypeRewriteRule.seq(typeRewriteRule, typeRewriteRule2);
		}
	}

	protected abstract String method_21599(String string);

	public static DataFix method_21596(Schema schema, String string, Function<String, String> function) {
		return new class_4499(schema, string) {
			@Override
			protected String method_21599(String string) {
				return (String)function.apply(string);
			}
		};
	}
}
