package net.minecraft.client.sound;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class MinecartSoundInstance extends MovingSoundInstance {
	private final PlayerEntity player;
	private final AbstractMinecartEntity minecart;

	public MinecartSoundInstance(PlayerEntity playerEntity, AbstractMinecartEntity abstractMinecartEntity) {
		super(new Identifier("minecraft:minecart.inside"));
		this.player = playerEntity;
		this.minecart = abstractMinecartEntity;
		this.attenuationType = SoundInstance.AttenuationType.NONE;
		this.repeat = true;
		this.repeatDelay = 0;
	}

	@Override
	public void tick() {
		if (!this.minecart.removed && this.player.hasVehicle() && this.player.vehicle == this.minecart) {
			float f = MathHelper.sqrt(this.minecart.velocityX * this.minecart.velocityX + this.minecart.velocityZ * this.minecart.velocityZ);
			if ((double)f >= 0.01) {
				this.volume = 0.0F + MathHelper.clamp(f, 0.0F, 1.0F) * 0.75F;
			} else {
				this.volume = 0.0F;
			}
		} else {
			this.done = true;
		}
	}
}
