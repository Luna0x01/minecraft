package net.minecraft;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2801 extends class_2795 {
	private static final Logger field_13224 = LogManager.getLogger();
	private final class_2801.class_2802[] field_13225;

	public class_2801(class_2816[] args, class_2801.class_2802[] args2) {
		super(args);
		this.field_13225 = args2;
	}

	@Override
	public ItemStack method_12029(ItemStack itemStack, Random random, class_2782 arg) {
		for (class_2801.class_2802 lv : this.field_13225) {
			UUID uUID = lv.field_13230;
			if (uUID == null) {
				uUID = UUID.randomUUID();
			}

			EquipmentSlot equipmentSlot = lv.field_13231[random.nextInt(lv.field_13231.length)];
			itemStack.setAttribute(
				lv.field_13227, new AttributeModifier(uUID, lv.field_13226, (double)lv.field_13229.method_12018(random), lv.field_13228), equipmentSlot
			);
		}

		return itemStack;
	}

	static class class_2802 {
		private final String field_13226;
		private final String field_13227;
		private final int field_13228;
		private final class_2789 field_13229;
		@Nullable
		private final UUID field_13230;
		private final EquipmentSlot[] field_13231;

		private class_2802(String string, String string2, int i, class_2789 arg, EquipmentSlot[] equipmentSlots, @Nullable UUID uUID) {
			this.field_13226 = string;
			this.field_13227 = string2;
			this.field_13228 = i;
			this.field_13229 = arg;
			this.field_13230 = uUID;
			this.field_13231 = equipmentSlots;
		}

		public JsonObject method_12046(JsonSerializationContext jsonSerializationContext) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("name", this.field_13226);
			jsonObject.addProperty("attribute", this.field_13227);
			jsonObject.addProperty("operation", method_12043(this.field_13228));
			jsonObject.add("amount", jsonSerializationContext.serialize(this.field_13229));
			if (this.field_13230 != null) {
				jsonObject.addProperty("id", this.field_13230.toString());
			}

			if (this.field_13231.length == 1) {
				jsonObject.addProperty("slot", this.field_13231[0].getName());
			} else {
				JsonArray jsonArray = new JsonArray();

				for (EquipmentSlot equipmentSlot : this.field_13231) {
					jsonArray.add(new JsonPrimitive(equipmentSlot.getName()));
				}

				jsonObject.add("slot", jsonArray);
			}

			return jsonObject;
		}

		public static class_2801.class_2802 method_12045(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			String string = JsonHelper.getString(jsonObject, "name");
			String string2 = JsonHelper.getString(jsonObject, "attribute");
			int i = method_12047(JsonHelper.getString(jsonObject, "operation"));
			class_2789 lv = JsonHelper.deserialize(jsonObject, "amount", jsonDeserializationContext, class_2789.class);
			UUID uUID = null;
			EquipmentSlot[] equipmentSlots;
			if (JsonHelper.hasString(jsonObject, "slot")) {
				equipmentSlots = new EquipmentSlot[]{EquipmentSlot.method_13031(JsonHelper.getString(jsonObject, "slot"))};
			} else {
				if (!JsonHelper.hasArray(jsonObject, "slot")) {
					throw new JsonSyntaxException("Invalid or missing attribute modifier slot; must be either string or array of strings.");
				}

				JsonArray jsonArray = JsonHelper.getArray(jsonObject, "slot");
				equipmentSlots = new EquipmentSlot[jsonArray.size()];
				int j = 0;

				for (JsonElement jsonElement : jsonArray) {
					equipmentSlots[j++] = EquipmentSlot.method_13031(JsonHelper.asString(jsonElement, "slot"));
				}

				if (equipmentSlots.length == 0) {
					throw new JsonSyntaxException("Invalid attribute modifier slot; must contain at least one entry.");
				}
			}

			if (jsonObject.has("id")) {
				String string3 = JsonHelper.getString(jsonObject, "id");

				try {
					uUID = UUID.fromString(string3);
				} catch (IllegalArgumentException var12) {
					throw new JsonSyntaxException("Invalid attribute modifier id '" + string3 + "' (must be UUID format, with dashes)");
				}
			}

			return new class_2801.class_2802(string, string2, i, lv, equipmentSlots, uUID);
		}

		private static String method_12043(int i) {
			switch (i) {
				case 0:
					return "addition";
				case 1:
					return "multiply_base";
				case 2:
					return "multiply_total";
				default:
					throw new IllegalArgumentException("Unknown operation " + i);
			}
		}

		private static int method_12047(String string) {
			if ("addition".equals(string)) {
				return 0;
			} else if ("multiply_base".equals(string)) {
				return 1;
			} else if ("multiply_total".equals(string)) {
				return 2;
			} else {
				throw new JsonSyntaxException("Unknown attribute modifier operation " + string);
			}
		}
	}

	public static class class_2803 extends class_2795.class_2796<class_2801> {
		public class_2803() {
			super(new Identifier("set_attributes"), class_2801.class);
		}

		public void method_12031(JsonObject jsonObject, class_2801 arg, JsonSerializationContext jsonSerializationContext) {
			JsonArray jsonArray = new JsonArray();

			for (class_2801.class_2802 lv : arg.field_13225) {
				jsonArray.add(lv.method_12046(jsonSerializationContext));
			}

			jsonObject.add("modifiers", jsonArray);
		}

		public class_2801 method_12033(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_2816[] args) {
			JsonArray jsonArray = JsonHelper.getArray(jsonObject, "modifiers");
			class_2801.class_2802[] lvs = new class_2801.class_2802[jsonArray.size()];
			int i = 0;

			for (JsonElement jsonElement : jsonArray) {
				lvs[i++] = class_2801.class_2802.method_12045(JsonHelper.asObject(jsonElement, "modifier"), jsonDeserializationContext);
			}

			if (lvs.length == 0) {
				throw new JsonSyntaxException("Invalid attribute modifiers array; cannot be empty");
			} else {
				return new class_2801(args, lvs);
			}
		}
	}
}
