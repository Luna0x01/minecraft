package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.function.Function;
import net.minecraft.datafixer.TypeReferences;

public abstract class JigsawBlockNameFix extends BlockNameFix {
	private final String name;

	public JigsawBlockNameFix(Schema schema, String string) {
		super(schema, string);
		this.name = string;
	}

	@Override
	public TypeRewriteRule makeRule() {
		TypeReference typeReference = TypeReferences.BLOCK_ENTITY;
		String string = "minecraft:jigsaw";
		OpticFinder<?> opticFinder = DSL.namedChoice("minecraft:jigsaw", this.getInputSchema().getChoiceType(typeReference, "minecraft:jigsaw"));
		TypeRewriteRule typeRewriteRule = this.fixTypeEverywhereTyped(
			this.name + " for jigsaw state",
			this.getInputSchema().getType(typeReference),
			this.getOutputSchema().getType(typeReference),
			typed -> typed.updateTyped(
					opticFinder,
					this.getOutputSchema().getChoiceType(typeReference, "minecraft:jigsaw"),
					typedx -> typedx.update(
							DSL.remainderFinder(), dynamic -> dynamic.update("final_state", dynamic2 -> (Dynamic)DataFixUtils.orElse(dynamic2.asString().result().map(stringx -> {
										int i = stringx.indexOf(91);
										int j = stringx.indexOf(123);
										int k = stringx.length();
										if (i > 0) {
											k = Math.min(k, i);
										}

										if (j > 0) {
											k = Math.min(k, j);
										}

										String string2 = stringx.substring(0, k);
										String string3 = this.rename(string2);
										return string3 + stringx.substring(k);
									}).map(dynamic::createString), dynamic2))
						)
				)
		);
		return TypeRewriteRule.seq(super.makeRule(), typeRewriteRule);
	}

	public static DataFix create(Schema oldSchema, String name, Function<String, String> rename) {
		return new JigsawBlockNameFix(oldSchema, name) {
			@Override
			protected String rename(String oldName) {
				return (String)rename.apply(oldName);
			}
		};
	}
}
