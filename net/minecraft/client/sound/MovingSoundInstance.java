package net.minecraft.client.sound;

import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

public abstract class MovingSoundInstance extends AbstractSoundInstance implements TickableSoundInstance {
	protected boolean done;

	protected MovingSoundInstance(SoundEvent soundEvent, SoundCategory soundCategory) {
		super(soundEvent, soundCategory);
	}

	@Override
	public boolean isDone() {
		return this.done;
	}
}
