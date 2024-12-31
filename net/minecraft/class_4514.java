package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class class_4514 extends DataFix {
	public class_4514(Schema schema, boolean bl) {
		super(schema, bl);
	}

	public TypeRewriteRule makeRule() {
		OpticFinder<String> opticFinder = DSL.fieldFinder("id", DSL.namespacedString());
		return this.fixTypeEverywhereTyped(
			"EntityCustomNameToComponentFix", this.getInputSchema().getType(class_3402.field_16596), typed -> typed.update(DSL.remainderFinder(), dynamic -> {
					Optional<String> optional = typed.getOptional(opticFinder);
					return optional.isPresent() && Objects.equals(optional.get(), "minecraft:commandblock_minecart") ? dynamic : method_21701(dynamic);
				})
		);
	}

	public static Dynamic<?> method_21701(Dynamic<?> dynamic) {
		String string = dynamic.getString("CustomName");
		return string.isEmpty() ? dynamic.remove("CustomName") : dynamic.set("CustomName", dynamic.createString(Text.Serializer.serialize(new LiteralText(string))));
	}
}
