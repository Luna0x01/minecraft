package net.minecraft;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;

public class class_4364 {
	private final class_3579.class_3580<?> field_21473;

	public class_4364(class_3579.class_3580<?> arg) {
		this.field_21473 = arg;
	}

	public static class_4364 method_20065(class_3579.class_3580<?> arg) {
		return new class_4364(arg);
	}

	public void method_20067(Consumer<class_4356> consumer, String string) {
		consumer.accept(new class_4356() {
			@Override
			public JsonObject method_20021() {
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("type", class_4364.this.field_21473.method_16213());
				return jsonObject;
			}

			@Override
			public Identifier method_20022() {
				return new Identifier(string);
			}

			@Nullable
			@Override
			public JsonObject method_20023() {
				return null;
			}

			@Nullable
			@Override
			public Identifier method_20024() {
				return new Identifier("");
			}
		});
	}
}
