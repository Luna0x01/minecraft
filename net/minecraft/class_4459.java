package net.minecraft;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.text.Text;
import net.minecraft.util.JsonHelper;

public class class_4459 implements class_4457<class_4458> {
	public class_4458 method_21335(JsonObject jsonObject) {
		Text text = Text.Serializer.method_20179(jsonObject.get("description"));
		if (text == null) {
			throw new JsonParseException("Invalid/missing description!");
		} else {
			int i = JsonHelper.getInt(jsonObject, "pack_format");
			return new class_4458(text, i);
		}
	}

	@Override
	public String method_5956() {
		return "pack";
	}
}
