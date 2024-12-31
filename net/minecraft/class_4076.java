package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class class_4076 extends class_2795 {
	private final Text field_19808;

	public class_4076(class_2816[] args, @Nullable Text text) {
		super(args);
		this.field_19808 = text;
	}

	@Override
	public ItemStack method_12029(ItemStack itemStack, Random random, class_2782 arg) {
		if (this.field_19808 != null) {
			itemStack.setCustomName(this.field_19808);
		}

		return itemStack;
	}

	public static class class_4077 extends class_2795.class_2796<class_4076> {
		public class_4077() {
			super(new Identifier("set_name"), class_4076.class);
		}

		public void method_12031(JsonObject jsonObject, class_4076 arg, JsonSerializationContext jsonSerializationContext) {
			if (arg.field_19808 != null) {
				jsonObject.add("name", Text.Serializer.method_20183(arg.field_19808));
			}
		}

		public class_4076 method_12033(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_2816[] args) {
			Text text = Text.Serializer.method_20179(jsonObject.get("name"));
			return new class_4076(args, text);
		}
	}
}
