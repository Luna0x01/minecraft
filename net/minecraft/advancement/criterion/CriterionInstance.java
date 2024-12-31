package net.minecraft.advancement.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import net.minecraft.util.Identifier;

public interface CriterionInstance {
	Identifier getCriterion();

	default JsonElement method_21241() {
		return JsonNull.INSTANCE;
	}
}
