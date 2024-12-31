package net.minecraft;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.util.PacketByteBuf;

public interface class_4322<T extends ArgumentType<?>> {
	void method_19890(T argumentType, PacketByteBuf packetByteBuf);

	T method_19891(PacketByteBuf packetByteBuf);

	void method_19889(T argumentType, JsonObject jsonObject);
}
