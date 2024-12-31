package net.minecraft.loot;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.class_2776;
import net.minecraft.class_2782;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2780 {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final class_2780 field_13185 = new class_2780(new class_2776[0]);
	private final class_2776[] field_13187;

	public class_2780(class_2776[] args) {
		this.field_13187 = args;
	}

	public List<ItemStack> method_11981(Random random, class_2782 arg) {
		List<ItemStack> list = Lists.newArrayList();
		if (arg.method_11987(this)) {
			for (class_2776 lv : this.field_13187) {
				lv.method_11969(list, random, arg);
			}

			arg.method_11990(this);
		} else {
			LOGGER.warn("Detected infinite loop in loot tables");
		}

		return list;
	}

	public void method_11983(Inventory inventory, Random random, class_2782 arg) {
		List<ItemStack> list = this.method_11981(random, arg);
		List<Integer> list2 = this.method_11982(inventory, random);
		this.method_11980(list, list2.size(), random);

		for (ItemStack itemStack : list) {
			if (list2.isEmpty()) {
				LOGGER.warn("Tried to over-fill a container");
				return;
			}

			if (itemStack.isEmpty()) {
				inventory.setInvStack((Integer)list2.remove(list2.size() - 1), ItemStack.EMPTY);
			} else {
				inventory.setInvStack((Integer)list2.remove(list2.size() - 1), itemStack);
			}
		}
	}

	private void method_11980(List<ItemStack> list, int i, Random random) {
		List<ItemStack> list2 = Lists.newArrayList();
		Iterator<ItemStack> iterator = list.iterator();

		while (iterator.hasNext()) {
			ItemStack itemStack = (ItemStack)iterator.next();
			if (itemStack.isEmpty()) {
				iterator.remove();
			} else if (itemStack.getCount() > 1) {
				list2.add(itemStack);
				iterator.remove();
			}
		}

		i -= list.size();

		while (i > 0 && !list2.isEmpty()) {
			ItemStack itemStack2 = (ItemStack)list2.remove(MathHelper.nextInt(random, 0, list2.size() - 1));
			int j = MathHelper.nextInt(random, 1, itemStack2.getCount() / 2);
			ItemStack itemStack3 = itemStack2.split(j);
			if (itemStack2.getCount() > 1 && random.nextBoolean()) {
				list2.add(itemStack2);
			} else {
				list.add(itemStack2);
			}

			if (itemStack3.getCount() > 1 && random.nextBoolean()) {
				list2.add(itemStack3);
			} else {
				list.add(itemStack3);
			}
		}

		list.addAll(list2);
		Collections.shuffle(list, random);
	}

	private List<Integer> method_11982(Inventory inventory, Random random) {
		List<Integer> list = Lists.newArrayList();

		for (int i = 0; i < inventory.getInvSize(); i++) {
			if (inventory.getInvStack(i).isEmpty()) {
				list.add(i);
			}
		}

		Collections.shuffle(list, random);
		return list;
	}

	public static class class_2781 implements JsonDeserializer<class_2780>, JsonSerializer<class_2780> {
		public class_2780 deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "loot table");
			class_2776[] lvs = JsonHelper.deserialize(jsonObject, "pools", new class_2776[0], jsonDeserializationContext, class_2776[].class);
			return new class_2780(lvs);
		}

		public JsonElement serialize(class_2780 arg, Type type, JsonSerializationContext jsonSerializationContext) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("pools", jsonSerializationContext.serialize(arg.field_13187));
			return jsonObject;
		}
	}
}
