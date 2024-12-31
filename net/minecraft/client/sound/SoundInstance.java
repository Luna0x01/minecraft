package net.minecraft.client.sound;

import javax.annotation.Nullable;
import net.minecraft.client.class_2906;
import net.minecraft.util.Identifier;

public interface SoundInstance {
	Identifier getIdentifier();

	@Nullable
	SoundContainerImpl method_12532(SoundManager soundManager);

	class_2906 method_12533();

	SoundCategory getCategory();

	boolean isRepeatable();

	int getRepeatDelay();

	float getVolume();

	float getPitch();

	float getX();

	float getY();

	float getZ();

	SoundInstance.AttenuationType getAttenuationType();

	public static enum AttenuationType {
		NONE(0),
		LINEAR(2);

		private final int integer;

		private AttenuationType(int j) {
			this.integer = j;
		}

		public int getInteger() {
			return this.integer;
		}
	}
}
