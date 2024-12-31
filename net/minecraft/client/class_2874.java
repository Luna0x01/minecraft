package net.minecraft.client;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;

public class class_2874 {
	private final Identifier field_13562;
	private final Map<Identifier, Float> field_13563;

	public class_2874(Identifier identifier, Map<Identifier, Float> map) {
		this.field_13562 = identifier;
		this.field_13563 = map;
	}

	public Identifier method_12368() {
		return this.field_13562;
	}

	boolean method_12369(ItemStack itemStack, @Nullable World world, @Nullable LivingEntity livingEntity) {
		Item item = itemStack.getItem();

		for (Entry<Identifier, Float> entry : this.field_13563.entrySet()) {
			ItemPropertyGetter itemPropertyGetter = item.getProperty((Identifier)entry.getKey());
			if (itemPropertyGetter == null || itemPropertyGetter.method_11398(itemStack, world, livingEntity) < (Float)entry.getValue()) {
				return false;
			}
		}

		return true;
	}

	static class class_2875 implements JsonDeserializer<class_2874> {
		public class_2874 deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "model"));
			Map<Identifier, Float> map = this.method_12371(jsonObject);
			return new class_2874(identifier, map);
		}

		protected Map<Identifier, Float> method_12371(JsonObject jsonObject) {
			Map<Identifier, Float> map = Maps.newLinkedHashMap();
			JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "predicate");

			for (Entry<String, JsonElement> entry : jsonObject2.entrySet()) {
				map.put(new Identifier((String)entry.getKey()), JsonHelper.asFloat((JsonElement)entry.getValue(), (String)entry.getKey()));
			}

			return map;
		}
	}
}
