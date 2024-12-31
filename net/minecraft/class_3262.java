package net.minecraft;

import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Identifier;

public interface class_3262 {
	Identifier field_15914 = new Identifier("textures/gui/toasts.png");
	Object field_15915 = new Object();

	class_3262.class_3263 method_14486(class_3264 arg, long l);

	default Object method_14487() {
		return field_15915;
	}

	public static enum class_3263 {
		SHOW(Sounds.UI_TOAST_IN),
		HIDE(Sounds.UI_TOAST_OUT);

		private final Sound field_15918;

		private class_3263(Sound sound) {
			this.field_15918 = sound;
		}

		public void method_14488(SoundManager soundManager) {
			soundManager.play(PositionedSoundInstance.method_14699(this.field_15918, 1.0F, 1.0F));
		}
	}
}
