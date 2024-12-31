package net.minecraft;

import net.minecraft.client.sound.SoundManager;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.sound.Sounds;

public class class_4294 implements class_4293 {
	private final ClientPlayerEntity field_21099;
	private final SoundManager field_21100;
	private int field_21101 = 0;

	public class_4294(ClientPlayerEntity clientPlayerEntity, SoundManager soundManager) {
		this.field_21099 = clientPlayerEntity;
		this.field_21100 = soundManager;
	}

	@Override
	public void method_19601() {
		this.field_21101--;
		if (this.field_21101 <= 0 && this.field_21099.method_15576()) {
			float f = this.field_21099.world.random.nextFloat();
			if (f < 1.0E-4F) {
				this.field_21101 = 0;
				this.field_21100.play(new class_4295.class_4296(this.field_21099, Sounds.AMBIENT_UNDERWATER_LOOP_ADDITIONS_ULTRA_RARE));
			} else if (f < 0.001F) {
				this.field_21101 = 0;
				this.field_21100.play(new class_4295.class_4296(this.field_21099, Sounds.AMBIENT_UNDERWATER_LOOP_ADDITIONS_RARE));
			} else if (f < 0.01F) {
				this.field_21101 = 0;
				this.field_21100.play(new class_4295.class_4296(this.field_21099, Sounds.AMBIENT_UNDERWATER_LOOP_ADDITIONS));
			}
		}
	}
}
