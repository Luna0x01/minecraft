package net.minecraft.client.sound;

import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

public class EntityTrackingSoundInstance extends MovingSoundInstance {
	private final Entity entity;

	public EntityTrackingSoundInstance(SoundEvent sound, SoundCategory category, float volume, float pitch, Entity entity) {
		super(sound, category);
		this.volume = volume;
		this.pitch = pitch;
		this.entity = entity;
		this.x = (double)((float)this.entity.getX());
		this.y = (double)((float)this.entity.getY());
		this.z = (double)((float)this.entity.getZ());
	}

	@Override
	public boolean canPlay() {
		return !this.entity.isSilent();
	}

	@Override
	public void tick() {
		if (this.entity.isRemoved()) {
			this.setDone();
		} else {
			this.x = (double)((float)this.entity.getX());
			this.y = (double)((float)this.entity.getY());
			this.z = (double)((float)this.entity.getZ());
		}
	}
}
