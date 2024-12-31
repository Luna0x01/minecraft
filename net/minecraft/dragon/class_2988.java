package net.minecraft.dragon;

import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.sound.Sounds;

public class class_2988 extends class_2980 {
	private int field_14684;

	public class_2988(EnderDragonEntity enderDragonEntity) {
		super(enderDragonEntity);
	}

	@Override
	public void method_13182() {
		this.dragon
			.world
			.playSound(
				this.dragon.x,
				this.dragon.y,
				this.dragon.z,
				Sounds.ENTITY_ENDER_DRAGON_GROWL,
				this.dragon.getSoundCategory(),
				2.5F,
				0.8F + this.dragon.getRandom().nextFloat() * 0.3F,
				false
			);
	}

	@Override
	public void method_13183() {
		if (this.field_14684++ >= 40) {
			this.dragon.method_13168().method_13203(class_2993.SITTING_FLAMING);
		}
	}

	@Override
	public void method_13184() {
		this.field_14684 = 0;
	}

	@Override
	public class_2993<class_2988> method_13189() {
		return class_2993.SITTING_ATTACKING;
	}
}
