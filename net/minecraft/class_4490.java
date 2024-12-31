package net.minecraft;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;

public class class_4490 extends DataFix {
	private final String field_22262;
	private final TypeReference field_22263;

	public class_4490(Schema schema, String string, TypeReference typeReference) {
		super(schema, true);
		this.field_22262 = string;
		this.field_22263 = typeReference;
	}

	public TypeRewriteRule makeRule() {
		TaggedChoiceType<?> taggedChoiceType = this.getInputSchema().findChoiceType(this.field_22263);
		TaggedChoiceType<?> taggedChoiceType2 = this.getOutputSchema().findChoiceType(this.field_22263);
		return this.method_21556(this.field_22262, taggedChoiceType, taggedChoiceType2);
	}

	protected final <K> TypeRewriteRule method_21556(String string, TaggedChoiceType<K> taggedChoiceType, TaggedChoiceType<?> taggedChoiceType2) {
		if (taggedChoiceType.getKeyType() != taggedChoiceType2.getKeyType()) {
			throw new IllegalStateException("Could not inject: key type is not the same");
		} else {
			return this.fixTypeEverywhere(string, taggedChoiceType, taggedChoiceType2, dynamicOps -> pair -> {
					if (!taggedChoiceType2.hasType(pair.getFirst())) {
						throw new IllegalArgumentException(String.format("Unknown type %s in %s ", pair.getFirst(), this.field_22263));
					} else {
						return pair;
					}
				});
		}
	}
}
