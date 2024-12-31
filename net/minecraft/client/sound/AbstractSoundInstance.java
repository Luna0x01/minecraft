package net.minecraft.client.sound;

import javax.annotation.Nullable;
import net.minecraft.client.class_2906;
import net.minecraft.sound.Sound;
import net.minecraft.util.Identifier;

public abstract class AbstractSoundInstance implements SoundInstance {
	protected class_2906 field_13678;
	@Nullable
	private SoundContainerImpl field_13680;
	protected SoundCategory category;
	protected Identifier identifier;
	protected float volume = 1.0F;
	protected float pitch = 1.0F;
	protected float x;
	protected float y;
	protected float z;
	protected boolean repeat;
	protected int repeatDelay;
	protected SoundInstance.AttenuationType attenuationType = SoundInstance.AttenuationType.LINEAR;
	protected boolean field_21096;

	protected AbstractSoundInstance(Sound sound, SoundCategory soundCategory) {
		this(sound.getId(), soundCategory);
	}

	protected AbstractSoundInstance(Identifier identifier, SoundCategory soundCategory) {
		this.identifier = identifier;
		this.category = soundCategory;
	}

	@Override
	public Identifier getIdentifier() {
		return this.identifier;
	}

	@Override
	public SoundContainerImpl method_12532(SoundManager soundManager) {
		this.field_13680 = soundManager.method_12545(this.identifier);
		if (this.field_13680 == null) {
			this.field_13678 = SoundManager.field_13702;
		} else {
			this.field_13678 = this.field_13680.getSound();
		}

		return this.field_13680;
	}

	@Override
	public class_2906 method_12533() {
		return this.field_13678;
	}

	@Override
	public SoundCategory getCategory() {
		return this.category;
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
		return this.volume * this.field_13678.method_12524();
	}

	@Override
	public float getPitch() {
		return this.pitch * this.field_13678.method_12525();
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

	@Override
	public boolean method_19604() {
		return this.field_21096;
	}
}
