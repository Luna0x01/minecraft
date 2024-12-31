package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Locale;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.MathHelper;

public class class_4338 implements ParticleEffect {
	public static final class_4338 field_21339 = new class_4338(1.0F, 0.0F, 0.0F, 1.0F);
	public static final ParticleEffect.class_4341<class_4338> field_21340 = new ParticleEffect.class_4341<class_4338>() {
		public class_4338 method_19981(ParticleType<class_4338> particleType, StringReader stringReader) throws CommandSyntaxException {
			stringReader.expect(' ');
			float f = (float)stringReader.readDouble();
			stringReader.expect(' ');
			float g = (float)stringReader.readDouble();
			stringReader.expect(' ');
			float h = (float)stringReader.readDouble();
			stringReader.expect(' ');
			float i = (float)stringReader.readDouble();
			return new class_4338(f, g, h, i);
		}

		public class_4338 method_19982(ParticleType<class_4338> particleType, PacketByteBuf packetByteBuf) {
			return new class_4338(packetByteBuf.readFloat(), packetByteBuf.readFloat(), packetByteBuf.readFloat(), packetByteBuf.readFloat());
		}
	};
	private final float field_21341;
	private final float field_21342;
	private final float field_21343;
	private final float field_21344;

	public class_4338(float f, float g, float h, float i) {
		this.field_21341 = f;
		this.field_21342 = g;
		this.field_21343 = h;
		this.field_21344 = MathHelper.clamp(i, 0.01F, 4.0F);
	}

	@Override
	public void method_19979(PacketByteBuf packetByteBuf) {
		packetByteBuf.writeFloat(this.field_21341);
		packetByteBuf.writeFloat(this.field_21342);
		packetByteBuf.writeFloat(this.field_21343);
		packetByteBuf.writeFloat(this.field_21344);
	}

	@Override
	public String method_19978() {
		return String.format(
			Locale.ROOT, "%s %.2f %.2f %.2f %.2f", this.particleType().method_19986(), this.field_21341, this.field_21342, this.field_21343, this.field_21344
		);
	}

	@Override
	public ParticleType<class_4338> particleType() {
		return class_4342.DUST;
	}

	public float method_19969() {
		return this.field_21341;
	}

	public float method_19970() {
		return this.field_21342;
	}

	public float method_19971() {
		return this.field_21343;
	}

	public float method_19972() {
		return this.field_21344;
	}
}
