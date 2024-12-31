package net.minecraft;

import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.util.Identifier;

public class class_3415 extends Schema {
	public class_3415(int i, Schema schema) {
		super(i, schema);
	}

	public static String method_15286(String string) {
		Identifier identifier = Identifier.fromString(string);
		return identifier != null ? identifier.toString() : string;
	}

	public Type<?> getChoiceType(TypeReference typeReference, String string) {
		return super.getChoiceType(typeReference, method_15286(string));
	}
}
