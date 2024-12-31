package net.minecraft.client.sound;

import net.minecraft.util.Identifier;

public class PositionedSoundInstance extends AbstractSoundInstance {
	public static PositionedSoundInstance master(Identifier identifier, float f) {
		return new PositionedSoundInstance(identifier, 0.25F, f, false, 0, SoundInstance.AttenuationType.NONE, 0.0F, 0.0F, 0.0F);
	}

	public static PositionedSoundInstance method_7051(Identifier identifier) {
		return new PositionedSoundInstance(identifier, 1.0F, 1.0F, false, 0, SoundInstance.AttenuationType.NONE, 0.0F, 0.0F, 0.0F);
	}

	public static PositionedSoundInstance method_7053(Identifier identifier, float f, float g, float h) {
		return new PositionedSoundInstance(identifier, 4.0F, 1.0F, false, 0, SoundInstance.AttenuationType.LINEAR, f, g, h);
	}

	public PositionedSoundInstance(Identifier identifier, float f, float g, float h, float i, float j) {
		this(identifier, f, g, false, 0, SoundInstance.AttenuationType.LINEAR, h, i, j);
	}

	private PositionedSoundInstance(
		Identifier identifier, float f, float g, boolean bl, int i, SoundInstance.AttenuationType attenuationType, float h, float j, float k
	) {
		super(identifier);
		this.volume = f;
		this.pitch = g;
		this.x = h;
		this.y = j;
		this.z = k;
		this.repeat = bl;
		this.repeatDelay = i;
		this.attenuationType = attenuationType;
	}
}
