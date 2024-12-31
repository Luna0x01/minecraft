package net.minecraft;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.util.PacketByteBuf;

public class class_4330 implements class_4322<DoubleArgumentType> {
	public void method_19890(DoubleArgumentType doubleArgumentType, PacketByteBuf packetByteBuf) {
		boolean bl = doubleArgumentType.getMinimum() != -Double.MAX_VALUE;
		boolean bl2 = doubleArgumentType.getMaximum() != Double.MAX_VALUE;
		packetByteBuf.writeByte(class_4329.method_19912(bl, bl2));
		if (bl) {
			packetByteBuf.writeDouble(doubleArgumentType.getMinimum());
		}

		if (bl2) {
			packetByteBuf.writeDouble(doubleArgumentType.getMaximum());
		}
	}

	public DoubleArgumentType method_19891(PacketByteBuf packetByteBuf) {
		byte b = packetByteBuf.readByte();
		double d = class_4329.method_19911(b) ? packetByteBuf.readDouble() : -Double.MAX_VALUE;
		double e = class_4329.method_19913(b) ? packetByteBuf.readDouble() : Double.MAX_VALUE;
		return DoubleArgumentType.doubleArg(d, e);
	}

	public void method_19889(DoubleArgumentType doubleArgumentType, JsonObject jsonObject) {
		if (doubleArgumentType.getMinimum() != -Double.MAX_VALUE) {
			jsonObject.addProperty("min", doubleArgumentType.getMinimum());
		}

		if (doubleArgumentType.getMaximum() != Double.MAX_VALUE) {
			jsonObject.addProperty("max", doubleArgumentType.getMaximum());
		}
	}
}
