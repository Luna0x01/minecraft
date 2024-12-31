package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public interface class_2829 {
	boolean method_12102(Random random, Entity entity);

	public abstract static class class_2830<T extends class_2829> {
		private final Identifier field_13253;
		private final Class<T> field_13254;

		protected class_2830(Identifier identifier, Class<T> class_) {
			this.field_13253 = identifier;
			this.field_13254 = class_;
		}

		public Identifier method_12103() {
			return this.field_13253;
		}

		public Class<T> method_12106() {
			return this.field_13254;
		}

		public abstract JsonElement method_12104(T arg, JsonSerializationContext jsonSerializationContext);

		public abstract T method_12105(JsonElement jsonElement, JsonDeserializationContext jsonDeserializationContext);
	}
}
