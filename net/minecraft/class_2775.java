package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Collection;
import java.util.Random;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class class_2775 extends class_2778 {
	protected final Item field_13176;
	protected final class_2795[] field_13177;

	public class_2775(Item item, int i, int j, class_2795[] args, class_2816[] args2) {
		super(i, j, args2);
		this.field_13176 = item;
		this.field_13177 = args;
	}

	@Override
	public void method_11976(Collection<ItemStack> collection, Random random, class_2782 arg) {
		ItemStack itemStack = new ItemStack(this.field_13176);

		for (class_2795 lv : this.field_13177) {
			if (class_2818.method_12082(lv.method_12028(), random, arg)) {
				itemStack = lv.method_12029(itemStack, random, arg);
			}
		}

		if (!itemStack.isEmpty()) {
			if (itemStack.getCount() < this.field_13176.getMaxCount()) {
				collection.add(itemStack);
			} else {
				int i = itemStack.getCount();

				while (i > 0) {
					ItemStack itemStack2 = itemStack.copy();
					itemStack2.setCount(Math.min(itemStack.getMaxCount(), i));
					i -= itemStack2.getCount();
					collection.add(itemStack2);
				}
			}
		}
	}

	@Override
	protected void method_11975(JsonObject jsonObject, JsonSerializationContext jsonSerializationContext) {
		if (this.field_13177 != null && this.field_13177.length > 0) {
			jsonObject.add("functions", jsonSerializationContext.serialize(this.field_13177));
		}

		Identifier identifier = Item.REGISTRY.getIdentifier(this.field_13176);
		if (identifier == null) {
			throw new IllegalArgumentException("Can't serialize unknown item " + this.field_13176);
		} else {
			jsonObject.addProperty("name", identifier.toString());
		}
	}

	public static class_2775 method_11965(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int i, int j, class_2816[] args) {
		Item item = JsonHelper.getItem(jsonObject, "name");
		class_2795[] lvs;
		if (jsonObject.has("functions")) {
			lvs = JsonHelper.deserialize(jsonObject, "functions", jsonDeserializationContext, class_2795[].class);
		} else {
			lvs = new class_2795[0];
		}

		return new class_2775(item, i, j, lvs, args);
	}
}
