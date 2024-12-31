package net.minecraft;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.util.PacketByteBuf;

public class class_4332 implements class_4322<IntegerArgumentType> {
	public void method_19890(IntegerArgumentType integerArgumentType, PacketByteBuf packetByteBuf) {
		boolean bl = integerArgumentType.getMinimum() != Integer.MIN_VALUE;
		boolean bl2 = integerArgumentType.getMaximum() != Integer.MAX_VALUE;
		packetByteBuf.writeByte(class_4329.method_19912(bl, bl2));
		if (bl) {
			packetByteBuf.writeInt(integerArgumentType.getMinimum());
		}

		if (bl2) {
			packetByteBuf.writeInt(integerArgumentType.getMaximum());
		}
	}

	public IntegerArgumentType method_19891(PacketByteBuf packetByteBuf) {
		byte b = packetByteBuf.readByte();
		int i = class_4329.method_19911(b) ? packetByteBuf.readInt() : Integer.MIN_VALUE;
		int j = class_4329.method_19913(b) ? packetByteBuf.readInt() : Integer.MAX_VALUE;
		return IntegerArgumentType.integer(i, j);
	}

	public void method_19889(IntegerArgumentType integerArgumentType, JsonObject jsonObject) {
		if (integerArgumentType.getMinimum() != Integer.MIN_VALUE) {
			jsonObject.addProperty("min", integerArgumentType.getMinimum());
		}

		if (integerArgumentType.getMaximum() != Integer.MAX_VALUE) {
			jsonObject.addProperty("max", integerArgumentType.getMaximum());
		}
	}
}
