package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.util.PacketByteBuf;

public interface ParticleEffect {
	ParticleType<?> particleType();

	void method_19979(PacketByteBuf packetByteBuf);

	String method_19978();

	public interface class_4341<T extends ParticleEffect> {
		T method_19981(ParticleType<T> particleType, StringReader stringReader) throws CommandSyntaxException;

		T method_19982(ParticleType<T> particleType, PacketByteBuf packetByteBuf);
	}
}
