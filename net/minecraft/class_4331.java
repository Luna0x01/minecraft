package net.minecraft;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.util.PacketByteBuf;

public class class_4331 implements class_4322<FloatArgumentType> {
	public void method_19890(FloatArgumentType floatArgumentType, PacketByteBuf packetByteBuf) {
		boolean bl = floatArgumentType.getMinimum() != -Float.MAX_VALUE;
		boolean bl2 = floatArgumentType.getMaximum() != Float.MAX_VALUE;
		packetByteBuf.writeByte(class_4329.method_19912(bl, bl2));
		if (bl) {
			packetByteBuf.writeFloat(floatArgumentType.getMinimum());
		}

		if (bl2) {
			packetByteBuf.writeFloat(floatArgumentType.getMaximum());
		}
	}

	public FloatArgumentType method_19891(PacketByteBuf packetByteBuf) {
		byte b = packetByteBuf.readByte();
		float f = class_4329.method_19911(b) ? packetByteBuf.readFloat() : -Float.MAX_VALUE;
		float g = class_4329.method_19913(b) ? packetByteBuf.readFloat() : Float.MAX_VALUE;
		return FloatArgumentType.floatArg(f, g);
	}

	public void method_19889(FloatArgumentType floatArgumentType, JsonObject jsonObject) {
		if (floatArgumentType.getMinimum() != -Float.MAX_VALUE) {
			jsonObject.addProperty("min", floatArgumentType.getMinimum());
		}

		if (floatArgumentType.getMaximum() != Float.MAX_VALUE) {
			jsonObject.addProperty("max", floatArgumentType.getMaximum());
		}
	}
}
