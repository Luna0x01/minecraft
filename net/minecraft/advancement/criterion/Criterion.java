package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancement.AdvancementFile;
import net.minecraft.advancement.SimpleAdvancement;
import net.minecraft.util.Identifier;

public interface Criterion<T extends CriterionInstance> {
	Identifier getIdentifier();

	void method_14973(AdvancementFile file, Criterion.class_3353<T> arg);

	void method_14974(AdvancementFile file, Criterion.class_3353<T> arg);

	void removeAdvancementFile(AdvancementFile file);

	T fromJson(JsonObject object, JsonDeserializationContext ctx);

	public static class class_3353<T extends CriterionInstance> {
		private final T field_16406;
		private final SimpleAdvancement advancement;
		private final String field_16408;

		public class_3353(T criterionInstance, SimpleAdvancement simpleAdvancement, String string) {
			this.field_16406 = criterionInstance;
			this.advancement = simpleAdvancement;
			this.field_16408 = string;
		}

		public T method_14975() {
			return this.field_16406;
		}

		public void method_14976(AdvancementFile file) {
			file.method_14919(this.advancement, this.field_16408);
		}

		public boolean equals(Object other) {
			if (this == other) {
				return true;
			} else if (other != null && this.getClass() == other.getClass()) {
				Criterion.class_3353<?> lv = (Criterion.class_3353<?>)other;
				if (!this.field_16406.equals(lv.field_16406)) {
					return false;
				} else {
					return !this.advancement.equals(lv.advancement) ? false : this.field_16408.equals(lv.field_16408);
				}
			} else {
				return false;
			}
		}

		public int hashCode() {
			int i = this.field_16406.hashCode();
			i = 31 * i + this.advancement.hashCode();
			return 31 * i + this.field_16408.hashCode();
		}
	}
}
