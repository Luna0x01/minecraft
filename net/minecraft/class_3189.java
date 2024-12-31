package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancement.AdvancementFile;
import net.minecraft.advancement.criterion.AbstractCriterionInstance;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.util.Identifier;

public class class_3189 implements Criterion<class_3189.class_3190> {
	private static final Identifier field_15674 = new Identifier("impossible");

	@Override
	public Identifier getIdentifier() {
		return field_15674;
	}

	@Override
	public void method_14973(AdvancementFile file, Criterion.class_3353<class_3189.class_3190> arg) {
	}

	@Override
	public void method_14974(AdvancementFile file, Criterion.class_3353<class_3189.class_3190> arg) {
	}

	@Override
	public void removeAdvancementFile(AdvancementFile file) {
	}

	public class_3189.class_3190 fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		return new class_3189.class_3190();
	}

	public static class class_3190 extends AbstractCriterionInstance {
		public class_3190() {
			super(class_3189.field_15674);
		}
	}
}
