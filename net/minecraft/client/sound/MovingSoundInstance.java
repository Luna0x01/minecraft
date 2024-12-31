package net.minecraft.client.sound;

import net.minecraft.util.Identifier;

public abstract class MovingSoundInstance extends AbstractSoundInstance implements TickableSoundInstance {
	protected boolean done = false;

	protected MovingSoundInstance(Identifier identifier) {
		super(identifier);
	}

	@Override
	public boolean isDone() {
		return this.done;
	}
}
