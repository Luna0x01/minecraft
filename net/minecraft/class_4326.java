package net.minecraft;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import java.util.function.Supplier;
import net.minecraft.util.PacketByteBuf;

public class class_4326<T extends ArgumentType<?>> implements class_4322<T> {
	private final Supplier<T> field_21253;

	public class_4326(Supplier<T> supplier) {
		this.field_21253 = supplier;
	}

	@Override
	public void method_19890(T argumentType, PacketByteBuf packetByteBuf) {
	}

	@Override
	public T method_19891(PacketByteBuf packetByteBuf) {
		return (T)this.field_21253.get();
	}

	@Override
	public void method_19889(T argumentType, JsonObject jsonObject) {
	}
}
