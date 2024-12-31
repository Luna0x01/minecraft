package net.minecraft.client.sound;

import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.sound.Sounds;
import net.minecraft.util.math.MathHelper;

public class ElytraSoundInstance extends MovingSoundInstance {
	private final ClientPlayerEntity player;
	private int field_13682;

	public ElytraSoundInstance(ClientPlayerEntity clientPlayerEntity) {
		super(Sounds.ITEM_ELYTRA_FLYING, SoundCategory.PLAYERS);
		this.player = clientPlayerEntity;
		this.repeat = true;
		this.repeatDelay = 0;
		this.volume = 0.1F;
	}

	@Override
	public void tick() {
		this.field_13682++;
		if (!this.player.removed && (this.field_13682 <= 20 || this.player.method_13055())) {
			this.x = (float)this.player.x;
			this.y = (float)this.player.y;
			this.z = (float)this.player.z;
			float f = MathHelper.sqrt(
				this.player.velocityX * this.player.velocityX + this.player.velocityZ * this.player.velocityZ + this.player.velocityY * this.player.velocityY
			);
			float g = f / 2.0F;
			if ((double)f >= 0.01) {
				this.volume = MathHelper.clamp(g * g, 0.0F, 1.0F);
			} else {
				this.volume = 0.0F;
			}

			if (this.field_13682 < 20) {
				this.volume = 0.0F;
			} else if (this.field_13682 < 40) {
				this.volume = (float)((double)this.volume * ((double)(this.field_13682 - 20) / 20.0));
			}

			float h = 0.8F;
			if (this.volume > 0.8F) {
				this.pitch = 1.0F + (this.volume - 0.8F);
			} else {
				this.pitch = 1.0F;
			}
		} else {
			this.done = true;
		}
	}
}
