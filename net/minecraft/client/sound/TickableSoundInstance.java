package net.minecraft.client.sound;

import net.minecraft.util.Tickable;

public interface TickableSoundInstance extends SoundInstance, Tickable {
	boolean isDone();
}
