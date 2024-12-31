package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.util.Identifier;

public interface class_2816 {
	boolean method_12074(Random random, class_2782 arg);

	public abstract static class class_2817<T extends class_2816> {
		private final Identifier field_13241;
		private final Class<T> field_13242;

		protected class_2817(Identifier identifier, Class<T> class_) {
			this.field_13241 = identifier;
			this.field_13242 = class_;
		}

		public Identifier method_12075() {
			return this.field_13241;
		}

		public Class<T> method_12077() {
			return this.field_13242;
		}

		public abstract void method_12076(JsonObject jsonObject, T arg, JsonSerializationContext jsonSerializationContext);

		public abstract T method_12078(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext);
	}
}
