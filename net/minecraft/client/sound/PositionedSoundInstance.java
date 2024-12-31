package net.minecraft.client.sound;

import net.minecraft.sound.Sound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class PositionedSoundInstance extends AbstractSoundInstance {
	public PositionedSoundInstance(Sound sound, SoundCategory soundCategory, float f, float g, BlockPos blockPos) {
		this(sound, soundCategory, f, g, (float)blockPos.getX() + 0.5F, (float)blockPos.getY() + 0.5F, (float)blockPos.getZ() + 0.5F);
	}

	public static PositionedSoundInstance method_12521(Sound sound, float f) {
		return method_14699(sound, f, 0.25F);
	}

	public static PositionedSoundInstance method_14699(Sound sound, float f, float g) {
		return new PositionedSoundInstance(sound, SoundCategory.MASTER, g, f, false, 0, SoundInstance.AttenuationType.NONE, 0.0F, 0.0F, 0.0F);
	}

	public static PositionedSoundInstance method_12520(Sound sound) {
		return new PositionedSoundInstance(sound, SoundCategory.MUSIC, 1.0F, 1.0F, false, 0, SoundInstance.AttenuationType.NONE, 0.0F, 0.0F, 0.0F);
	}

	public static PositionedSoundInstance method_7053(Sound sound, float f, float g, float h) {
		return new PositionedSoundInstance(sound, SoundCategory.RECORDS, 4.0F, 1.0F, false, 0, SoundInstance.AttenuationType.LINEAR, f, g, h);
	}

	public PositionedSoundInstance(Sound sound, SoundCategory soundCategory, float f, float g, float h, float i, float j) {
		this(sound, soundCategory, f, g, false, 0, SoundInstance.AttenuationType.LINEAR, h, i, j);
	}

	private PositionedSoundInstance(
		Sound sound, SoundCategory soundCategory, float f, float g, boolean bl, int i, SoundInstance.AttenuationType attenuationType, float h, float j, float k
	) {
		this(sound.getId(), soundCategory, f, g, bl, i, attenuationType, h, j, k);
	}

	public PositionedSoundInstance(
		Identifier identifier,
		SoundCategory soundCategory,
		float f,
		float g,
		boolean bl,
		int i,
		SoundInstance.AttenuationType attenuationType,
		float h,
		float j,
		float k
	) {
		super(identifier, soundCategory);
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
