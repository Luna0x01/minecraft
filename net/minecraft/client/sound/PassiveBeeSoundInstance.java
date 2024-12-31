package net.minecraft.client.sound;

import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class PassiveBeeSoundInstance extends AbstractBeeSoundInstance {
	public PassiveBeeSoundInstance(BeeEntity beeEntity) {
		super(beeEntity, SoundEvents.field_20605, SoundCategory.field_15254);
	}

	@Override
	protected MovingSoundInstance getReplacement() {
		return new AggressiveBeeSoundInstance(this.bee);
	}

	@Override
	protected boolean shouldReplace() {
		return this.bee.isAngry();
	}
}
