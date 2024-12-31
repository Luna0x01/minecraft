package net.minecraft.client.sound;

import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.sound.Sounds;
import net.minecraft.util.math.MathHelper;

public class MinecartMovingSoundInstance extends MovingSoundInstance {
	private final AbstractMinecartEntity minecart;
	private float field_8146 = 0.0F;

	public MinecartMovingSoundInstance(AbstractMinecartEntity abstractMinecartEntity) {
		super(Sounds.ENTITY_MINECART_RIDING, SoundCategory.NEUTRAL);
		this.minecart = abstractMinecartEntity;
		this.repeat = true;
		this.repeatDelay = 0;
	}

	@Override
	public void tick() {
		if (this.minecart.removed) {
			this.done = true;
		} else {
			this.x = (float)this.minecart.x;
			this.y = (float)this.minecart.y;
			this.z = (float)this.minecart.z;
			float f = MathHelper.sqrt(this.minecart.velocityX * this.minecart.velocityX + this.minecart.velocityZ * this.minecart.velocityZ);
			if ((double)f >= 0.01) {
				this.field_8146 = MathHelper.clamp(this.field_8146 + 0.0025F, 0.0F, 1.0F);
				this.volume = 0.0F + MathHelper.clamp(f, 0.0F, 0.5F) * 0.7F;
			} else {
				this.field_8146 = 0.0F;
				this.volume = 0.0F;
			}
		}
	}
}
