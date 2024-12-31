package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Collection;
import java.util.Random;
import net.minecraft.item.ItemStack;

public class class_2774 extends class_2778 {
	public class_2774(int i, int j, class_2816[] args) {
		super(i, j, args);
	}

	@Override
	public void method_11976(Collection<ItemStack> collection, Random random, class_2782 arg) {
	}

	@Override
	protected void method_11975(JsonObject jsonObject, JsonSerializationContext jsonSerializationContext) {
	}

	public static class_2774 method_11964(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int i, int j, class_2816[] args) {
		return new class_2774(i, j, args);
	}
}
