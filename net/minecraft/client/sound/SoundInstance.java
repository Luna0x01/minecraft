package net.minecraft.client.sound;

import javax.annotation.Nullable;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;

public interface SoundInstance {
	Identifier getId();

	@Nullable
	WeightedSoundSet getSoundSet(SoundManager soundManager);

	Sound getSound();

	SoundCategory getCategory();

	boolean isRepeatable();

	boolean isLooping();

	int getRepeatDelay();

	float getVolume();

	float getPitch();

	double getX();

	double getY();

	double getZ();

	SoundInstance.AttenuationType getAttenuationType();

	default boolean shouldAlwaysPlay() {
		return false;
	}

	default boolean canPlay() {
		return true;
	}

	public static enum AttenuationType {
		NONE,
		LINEAR;
	}
}
