package net.minecraft;

import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;

public class class_4295 {
	public static class class_4296 extends MovingSoundInstance {
		private final ClientPlayerEntity field_21102;

		protected class_4296(ClientPlayerEntity clientPlayerEntity, Sound sound) {
			super(sound, SoundCategory.AMBIENT);
			this.field_21102 = clientPlayerEntity;
			this.repeat = false;
			this.repeatDelay = 0;
			this.volume = 1.0F;
			this.field_21096 = true;
		}

		@Override
		public void tick() {
			if (!this.field_21102.removed && this.field_21102.method_15576()) {
				this.x = (float)this.field_21102.x;
				this.y = (float)this.field_21102.y;
				this.z = (float)this.field_21102.z;
			} else {
				this.done = true;
			}
		}
	}

	public static class class_4297 extends MovingSoundInstance {
		private final ClientPlayerEntity field_21103;
		private int field_21104;

		public class_4297(ClientPlayerEntity clientPlayerEntity) {
			super(Sounds.AMBIENT_UNDERWATER_LOOP, SoundCategory.AMBIENT);
			this.field_21103 = clientPlayerEntity;
			this.repeat = true;
			this.repeatDelay = 0;
			this.volume = 1.0F;
			this.field_21096 = true;
		}

		@Override
		public void tick() {
			if (!this.field_21103.removed && this.field_21104 >= 0) {
				this.x = (float)this.field_21103.x;
				this.y = (float)this.field_21103.y;
				this.z = (float)this.field_21103.z;
				if (this.field_21103.method_15576()) {
					this.field_21104++;
				} else {
					this.field_21104 -= 2;
				}

				this.field_21104 = Math.min(this.field_21104, 40);
				this.volume = Math.max(0.0F, Math.min((float)this.field_21104 / 40.0F, 1.0F));
			} else {
				this.done = true;
			}
		}
	}
}
