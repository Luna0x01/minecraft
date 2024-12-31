package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class class_4343 extends ParticleType<class_4343> implements ParticleEffect {
	private static final ParticleEffect.class_4341<class_4343> field_21401 = new ParticleEffect.class_4341<class_4343>() {
		public class_4343 method_19981(ParticleType<class_4343> particleType, StringReader stringReader) throws CommandSyntaxException {
			return (class_4343)particleType;
		}

		public class_4343 method_19982(ParticleType<class_4343> particleType, PacketByteBuf packetByteBuf) {
			return (class_4343)particleType;
		}
	};

	protected class_4343(Identifier identifier, boolean bl) {
		super(identifier, bl, field_21401);
	}

	@Override
	public ParticleType<class_4343> particleType() {
		return this;
	}

	@Override
	public void method_19979(PacketByteBuf packetByteBuf) {
	}

	@Override
	public String method_19978() {
		return this.method_19986().toString();
	}
}
