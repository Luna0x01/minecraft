package net.minecraft.client.sound;

import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class PositionedSoundInstance extends AbstractSoundInstance {
	public PositionedSoundInstance(SoundEvent sound, SoundCategory category, float volume, float pitch, BlockPos pos) {
		this(sound, category, volume, pitch, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5);
	}

	public static PositionedSoundInstance master(SoundEvent sound, float pitch) {
		return master(sound, pitch, 0.25F);
	}

	public static PositionedSoundInstance master(SoundEvent sound, float pitch, float volume) {
		return new PositionedSoundInstance(sound.getId(), SoundCategory.MASTER, volume, pitch, false, 0, SoundInstance.AttenuationType.NONE, 0.0, 0.0, 0.0, true);
	}

	public static PositionedSoundInstance music(SoundEvent sound) {
		return new PositionedSoundInstance(sound.getId(), SoundCategory.MUSIC, 1.0F, 1.0F, false, 0, SoundInstance.AttenuationType.NONE, 0.0, 0.0, 0.0, true);
	}

	public static PositionedSoundInstance record(SoundEvent sound, double x, double y, double z) {
		return new PositionedSoundInstance(sound, SoundCategory.RECORDS, 4.0F, 1.0F, false, 0, SoundInstance.AttenuationType.LINEAR, x, y, z);
	}

	public static PositionedSoundInstance ambient(SoundEvent sound, float pitch, float volume) {
		return new PositionedSoundInstance(sound.getId(), SoundCategory.AMBIENT, volume, pitch, false, 0, SoundInstance.AttenuationType.NONE, 0.0, 0.0, 0.0, true);
	}

	public static PositionedSoundInstance ambient(SoundEvent sound) {
		return ambient(sound, 1.0F, 1.0F);
	}

	public static PositionedSoundInstance ambient(SoundEvent sound, double x, double y, double z) {
		return new PositionedSoundInstance(sound, SoundCategory.AMBIENT, 1.0F, 1.0F, false, 0, SoundInstance.AttenuationType.LINEAR, x, y, z);
	}

	public PositionedSoundInstance(SoundEvent sound, SoundCategory category, float volume, float pitch, double x, double y, double z) {
		this(sound, category, volume, pitch, false, 0, SoundInstance.AttenuationType.LINEAR, x, y, z);
	}

	private PositionedSoundInstance(
		SoundEvent sound,
		SoundCategory category,
		float volume,
		float pitch,
		boolean repeat,
		int repeatDelay,
		SoundInstance.AttenuationType attenuationType,
		double x,
		double y,
		double z
	) {
		this(sound.getId(), category, volume, pitch, repeat, repeatDelay, attenuationType, x, y, z, false);
	}

	public PositionedSoundInstance(
		Identifier id,
		SoundCategory category,
		float volume,
		float pitch,
		boolean repeat,
		int repeatDelay,
		SoundInstance.AttenuationType attenuationType,
		double x,
		double y,
		double z,
		boolean looping
	) {
		super(id, category);
		this.volume = volume;
		this.pitch = pitch;
		this.x = x;
		this.y = y;
		this.z = z;
		this.repeat = repeat;
		this.repeatDelay = repeatDelay;
		this.attenuationType = attenuationType;
		this.looping = looping;
	}
}
