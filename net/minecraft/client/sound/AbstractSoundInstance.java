package net.minecraft.client.sound;

import net.minecraft.util.Identifier;

public abstract class AbstractSoundInstance implements SoundInstance {
	protected final Identifier identifier;
	protected float volume = 1.0F;
	protected float pitch = 1.0F;
	protected float x;
	protected float y;
	protected float z;
	protected boolean repeat = false;
	protected int repeatDelay = 0;
	protected SoundInstance.AttenuationType attenuationType = SoundInstance.AttenuationType.LINEAR;

	protected AbstractSoundInstance(Identifier identifier) {
		this.identifier = identifier;
	}

	@Override
	public Identifier getIdentifier() {
		return this.identifier;
	}

	@Override
	public boolean isRepeatable() {
		return this.repeat;
	}

	@Override
	public int getRepeatDelay() {
		return this.repeatDelay;
	}

	@Override
	public float getVolume() {
		return this.volume;
	}

	@Override
	public float getPitch() {
		return this.pitch;
	}

	@Override
	public float getX() {
		return this.x;
	}

	@Override
	public float getY() {
		return this.y;
	}

	@Override
	public float getZ() {
		return this.z;
	}

	@Override
	public SoundInstance.AttenuationType getAttenuationType() {
		return this.attenuationType;
	}
}
