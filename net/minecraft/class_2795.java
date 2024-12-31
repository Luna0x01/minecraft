package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public abstract class class_2795 {
	private final class_2816[] field_13218;

	protected class_2795(class_2816[] args) {
		this.field_13218 = args;
	}

	public abstract ItemStack method_12029(ItemStack itemStack, Random random, class_2782 arg);

	public class_2816[] method_12028() {
		return this.field_13218;
	}

	public abstract static class class_2796<T extends class_2795> {
		private final Identifier field_13219;
		private final Class<T> field_13220;

		protected class_2796(Identifier identifier, Class<T> class_) {
			this.field_13219 = identifier;
			this.field_13220 = class_;
		}

		public Identifier method_12030() {
			return this.field_13219;
		}

		public Class<T> method_12032() {
			return this.field_13220;
		}

		public abstract void method_12031(JsonObject jsonObject, T arg, JsonSerializationContext jsonSerializationContext);

		public abstract T method_12033(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_2816[] args);
	}
}
