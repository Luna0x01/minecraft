package net.minecraft.client.sound;

public interface SoundContainer<T> {
	int getWeight();

	T getSound();

	void preload(SoundSystem soundSystem);
}
