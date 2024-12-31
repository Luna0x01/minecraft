package net.minecraft;

import com.google.gson.JsonObject;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public interface class_3578<T extends RecipeType> {
	T method_16215(Identifier identifier, JsonObject jsonObject);

	T method_16216(Identifier identifier, PacketByteBuf packetByteBuf);

	void method_16214(PacketByteBuf packetByteBuf, T recipeType);

	String method_16213();
}
