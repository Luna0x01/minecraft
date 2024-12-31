package net.minecraft.client.sound;

import net.minecraft.util.Identifier;

public class Sound {
	private final Identifier id;
	private final boolean field_8187;
	private double volume;
	private double pitch;

	public Sound(Identifier identifier, double d, double e, boolean bl) {
		this.id = identifier;
		this.volume = d;
		this.pitch = e;
		this.field_8187 = bl;
	}

	public Sound(Sound sound) {
		this.id = sound.id;
		this.volume = sound.volume;
		this.pitch = sound.pitch;
		this.field_8187 = sound.field_8187;
	}

	public Identifier getIdentifier() {
		return this.id;
	}

	public double getVolume() {
		return this.volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}

	public double getPitch() {
		return this.pitch;
	}

	public void setPitch(double pitch) {
		this.pitch = pitch;
	}

	public boolean method_7094() {
		return this.field_8187;
	}
}
