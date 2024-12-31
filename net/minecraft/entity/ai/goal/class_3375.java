package net.minecraft.entity.ai.goal;

import net.minecraft.class_3159;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class class_3375 extends Goal {
	private final class_3159 field_16536;
	private PlayerEntity field_16537;
	private boolean field_16538;

	public class_3375(class_3159 arg) {
		this.field_16536 = arg;
	}

	@Override
	public boolean canStart() {
		LivingEntity livingEntity = this.field_16536.getOwner();
		boolean bl = livingEntity != null
			&& !((PlayerEntity)livingEntity).isSpectator()
			&& !((PlayerEntity)livingEntity).abilities.flying
			&& !livingEntity.isTouchingWater();
		return !this.field_16536.isSitting() && bl && this.field_16536.method_14114();
	}

	@Override
	public boolean canStop() {
		return !this.field_16538;
	}

	@Override
	public void start() {
		this.field_16537 = (PlayerEntity)this.field_16536.getOwner();
		this.field_16538 = false;
	}

	@Override
	public void tick() {
		if (!this.field_16538 && !this.field_16536.isSitting() && !this.field_16536.isLeashed()) {
			if (this.field_16536.getBoundingBox().intersects(this.field_16537.getBoundingBox())) {
				this.field_16538 = this.field_16536.method_14115(this.field_16537);
			}
		}
	}
}
