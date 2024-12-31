package net.minecraft.sound;

import net.minecraft.util.Identifier;

public class SoundEvent {
	private final Identifier id;

	public SoundEvent(Identifier identifier) {
		this.id = identifier;
	}

	public Identifier getId() {
		return this.id;
	}
}
