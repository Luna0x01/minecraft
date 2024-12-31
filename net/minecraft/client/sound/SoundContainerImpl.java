package net.minecraft.client.sound;

public class SoundContainerImpl implements SoundContainer<Sound> {
	private final Sound sound;
	private final int weight;

	SoundContainerImpl(Sound sound, int i) {
		this.sound = sound;
		this.weight = i;
	}

	@Override
	public int getWeight() {
		return this.weight;
	}

	public Sound getSound() {
		return new Sound(this.sound);
	}
}
