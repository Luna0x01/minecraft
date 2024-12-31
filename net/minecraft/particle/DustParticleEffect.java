package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Locale;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

public class DustParticleEffect implements ParticleEffect {
	public static final DustParticleEffect RED = new DustParticleEffect(1.0F, 0.0F, 0.0F, 1.0F);
	public static final ParticleEffect.Factory<DustParticleEffect> PARAMETERS_FACTORY = new ParticleEffect.Factory<DustParticleEffect>() {
		public DustParticleEffect read(ParticleType<DustParticleEffect> particleType, StringReader stringReader) throws CommandSyntaxException {
			stringReader.expect(' ');
			float f = (float)stringReader.readDouble();
			stringReader.expect(' ');
			float g = (float)stringReader.readDouble();
			stringReader.expect(' ');
			float h = (float)stringReader.readDouble();
			stringReader.expect(' ');
			float i = (float)stringReader.readDouble();
			return new DustParticleEffect(f, g, h, i);
		}

		public DustParticleEffect read(ParticleType<DustParticleEffect> particleType, PacketByteBuf packetByteBuf) {
			return new DustParticleEffect(packetByteBuf.readFloat(), packetByteBuf.readFloat(), packetByteBuf.readFloat(), packetByteBuf.readFloat());
		}
	};
	private final float red;
	private final float green;
	private final float blue;
	private final float scale;

	public DustParticleEffect(float f, float g, float h, float i) {
		this.red = f;
		this.green = g;
		this.blue = h;
		this.scale = MathHelper.clamp(i, 0.01F, 4.0F);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) {
		packetByteBuf.writeFloat(this.red);
		packetByteBuf.writeFloat(this.green);
		packetByteBuf.writeFloat(this.blue);
		packetByteBuf.writeFloat(this.scale);
	}

	@Override
	public String asString() {
		return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f", Registry.field_11141.getId(this.getType()), this.red, this.green, this.blue, this.scale);
	}

	@Override
	public ParticleType<DustParticleEffect> getType() {
		return ParticleTypes.field_11212;
	}

	public float getRed() {
		return this.red;
	}

	public float getGreen() {
		return this.green;
	}

	public float getBlue() {
		return this.blue;
	}

	public float getScale() {
		return this.scale;
	}
}
