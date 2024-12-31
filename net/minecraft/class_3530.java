package net.minecraft;

import com.google.common.base.Joiner;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class class_3530 {
	public static final class_3530 field_17099 = new class_3530();
	private static final Joiner field_17100 = Joiner.on(", ");
	@Nullable
	private final EntityType<?> field_17101;

	public class_3530(EntityType<?> entityType) {
		this.field_17101 = entityType;
	}

	private class_3530() {
		this.field_17101 = null;
	}

	public boolean method_15956(EntityType<?> entityType) {
		return this.field_17101 == null || this.field_17101 == entityType;
	}

	public static class_3530 method_15957(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			String string = JsonHelper.asString(jsonElement, "type");
			Identifier identifier = new Identifier(string);
			EntityType<?> entityType = Registry.ENTITY_TYPE.getByIdentifier(identifier);
			if (entityType == null) {
				throw new JsonSyntaxException("Unknown entity type '" + identifier + "', valid types are: " + field_17100.join(Registry.ENTITY_TYPE.getKeySet()));
			} else {
				return new class_3530(entityType);
			}
		} else {
			return field_17099;
		}
	}

	public JsonElement method_15955() {
		return (JsonElement)(this.field_17101 == null ? JsonNull.INSTANCE : new JsonPrimitive(Registry.ENTITY_TYPE.getId(this.field_17101).toString()));
	}
}
