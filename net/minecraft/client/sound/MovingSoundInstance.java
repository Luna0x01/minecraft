package net.minecraft.client.sound;

import net.minecraft.sound.Sound;

public abstract class MovingSoundInstance extends AbstractSoundInstance implements TickableSoundInstance {
	protected boolean done;

	protected MovingSoundInstance(Sound sound, SoundCategory soundCategory) {
		super(sound, soundCategory);
	}

	@Override
	public boolean isDone() {
		return this.done;
	}
}
