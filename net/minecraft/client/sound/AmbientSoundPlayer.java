package net.minecraft.client.sound;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.ClientPlayerTickable;
import net.minecraft.sound.SoundEvents;

public class AmbientSoundPlayer implements ClientPlayerTickable {
	public static final float field_33008 = 0.01F;
	public static final float field_33009 = 0.001F;
	public static final float field_33010 = 1.0E-4F;
	private static final int field_33011 = 0;
	private final ClientPlayerEntity player;
	private final SoundManager soundManager;
	private int ticksUntilPlay = 0;

	public AmbientSoundPlayer(ClientPlayerEntity player, SoundManager soundManager) {
		this.player = player;
		this.soundManager = soundManager;
	}

	@Override
	public void tick() {
		this.ticksUntilPlay--;
		if (this.ticksUntilPlay <= 0 && this.player.isSubmergedInWater()) {
			float f = this.player.world.random.nextFloat();
			if (f < 1.0E-4F) {
				this.ticksUntilPlay = 0;
				this.soundManager.play(new AmbientSoundLoops.MusicLoop(this.player, SoundEvents.AMBIENT_UNDERWATER_LOOP_ADDITIONS_ULTRA_RARE));
			} else if (f < 0.001F) {
				this.ticksUntilPlay = 0;
				this.soundManager.play(new AmbientSoundLoops.MusicLoop(this.player, SoundEvents.AMBIENT_UNDERWATER_LOOP_ADDITIONS_RARE));
			} else if (f < 0.01F) {
				this.ticksUntilPlay = 0;
				this.soundManager.play(new AmbientSoundLoops.MusicLoop(this.player, SoundEvents.AMBIENT_UNDERWATER_LOOP_ADDITIONS));
			}
		}
	}
}
