package net.minecraft.client.sound;

import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;

public class RidingMinecartSoundInstance extends MovingSoundInstance {
	private final AbstractMinecartEntity minecart;
	private float distance = 0.0F;

	public RidingMinecartSoundInstance(AbstractMinecartEntity abstractMinecartEntity) {
		super(SoundEvents.field_14784, SoundCategory.field_15254);
		this.minecart = abstractMinecartEntity;
		this.repeat = true;
		this.repeatDelay = 0;
		this.volume = 0.0F;
		this.x = (float)abstractMinecartEntity.getX();
		this.y = (float)abstractMinecartEntity.getY();
		this.z = (float)abstractMinecartEntity.getZ();
	}

	@Override
	public boolean shouldAlwaysPlay() {
		return true;
	}

	@Override
	public void tick() {
		if (this.minecart.removed) {
			this.done = true;
		} else {
			this.x = (float)this.minecart.getX();
			this.y = (float)this.minecart.getY();
			this.z = (float)this.minecart.getZ();
			float f = MathHelper.sqrt(Entity.squaredHorizontalLength(this.minecart.getVelocity()));
			if ((double)f >= 0.01) {
				this.distance = MathHelper.clamp(this.distance + 0.0025F, 0.0F, 1.0F);
				this.volume = MathHelper.lerp(MathHelper.clamp(f, 0.0F, 0.5F), 0.0F, 0.7F);
			} else {
				this.distance = 0.0F;
				this.volume = 0.0F;
			}
		}
	}
}
