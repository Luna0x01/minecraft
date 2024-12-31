package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Collection;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.class_2780;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class class_2786 extends class_2778 {
	protected final Identifier field_13206;

	public class_2786(Identifier identifier, int i, int j, class_2816[] args) {
		super(i, j, args);
		this.field_13206 = identifier;
	}

	@Override
	public void method_11976(Collection<ItemStack> collection, Random random, class_2782 arg) {
		class_2780 lv = arg.method_11992().method_12006(this.field_13206);
		Collection<ItemStack> collection2 = lv.method_11981(random, arg);
		collection.addAll(collection2);
	}

	@Override
	protected void method_11975(JsonObject jsonObject, JsonSerializationContext jsonSerializationContext) {
		jsonObject.addProperty("name", this.field_13206.toString());
	}

	public static class_2786 method_12003(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int i, int j, class_2816[] args) {
		Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "name"));
		return new class_2786(identifier, i, j, args);
	}
}
