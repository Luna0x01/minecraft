package net.minecraft.client.toast;

import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public interface Toast {
	Identifier TEXTURE = new Identifier("textures/gui/toasts.png");
	Object TYPE = new Object();

	Toast.Visibility draw(MatrixStack matrices, ToastManager manager, long startTime);

	default Object getType() {
		return TYPE;
	}

	default int getWidth() {
		return 160;
	}

	default int getHeight() {
		return 32;
	}

	public static enum Visibility {
		SHOW(SoundEvents.UI_TOAST_IN),
		HIDE(SoundEvents.UI_TOAST_OUT);

		private final SoundEvent sound;

		private Visibility(SoundEvent sound) {
			this.sound = sound;
		}

		public void playSound(SoundManager soundManager) {
			soundManager.play(PositionedSoundInstance.master(this.sound, 1.0F, 1.0F));
		}
	}
}
