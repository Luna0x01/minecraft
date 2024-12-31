package net.minecraft.client.sound;

import net.minecraft.util.Identifier;

public interface SoundInstance {
	Identifier getIdentifier();

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
