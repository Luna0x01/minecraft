package net.minecraft.client.sound;

import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.sound.Sounds;

public class GuardianAttackSoundInstance extends MovingSoundInstance {
	private final GuardianEntity field_11342;

	public GuardianAttackSoundInstance(GuardianEntity guardianEntity) {
		super(Sounds.ENTITY_GUARDIAN_ATTACK, SoundCategory.HOSTILE);
		this.field_11342 = guardianEntity;
		this.attenuationType = SoundInstance.AttenuationType.NONE;
		this.repeat = true;
		this.repeatDelay = 0;
	}

	@Override
	public void tick() {
		if (!this.field_11342.removed && this.field_11342.hasBeamTarget()) {
			this.x = (float)this.field_11342.x;
			this.y = (float)this.field_11342.y;
			this.z = (float)this.field_11342.z;
			float f = this.field_11342.getBeamProgress(0.0F);
			this.volume = 0.0F + 1.0F * f * f;
			this.pitch = 0.7F + 0.5F * f;
		} else {
			this.done = true;
		}
	}
}
