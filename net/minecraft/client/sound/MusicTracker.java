package net.minecraft.client.sound;

import java.util.Random;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.MathHelper;

public class MusicTracker implements Tickable {
	private final Random random = new Random();
	private final MinecraftClient field_8172;
	private SoundInstance field_8173;
	private int timeUntilNextSong = 100;

	public MusicTracker(MinecraftClient minecraftClient) {
		this.field_8172 = minecraftClient;
	}

	@Override
	public void tick() {
		MusicTracker.MusicType musicType = this.field_8172.getMusicType();
		if (this.field_8173 != null) {
			if (!musicType.method_7086().getId().equals(this.field_8173.getIdentifier())) {
				this.field_8172.getSoundManager().stop(this.field_8173);
				this.timeUntilNextSong = MathHelper.nextInt(this.random, 0, musicType.getMinDelay() / 2);
			}

			if (!this.field_8172.getSoundManager().isPlaying(this.field_8173)) {
				this.field_8173 = null;
				this.timeUntilNextSong = Math.min(MathHelper.nextInt(this.random, musicType.getMinDelay(), musicType.getMaxDelay()), this.timeUntilNextSong);
			}
		}

		this.timeUntilNextSong = Math.min(this.timeUntilNextSong, musicType.getMaxDelay());
		if (this.field_8173 == null && this.timeUntilNextSong-- <= 0) {
			this.play(musicType);
		}
	}

	public void play(MusicTracker.MusicType musicType) {
		this.field_8173 = PositionedSoundInstance.method_12520(musicType.method_7086());
		this.field_8172.getSoundManager().play(this.field_8173);
		this.timeUntilNextSong = Integer.MAX_VALUE;
	}

	public void stop() {
		if (this.field_8173 != null) {
			this.field_8172.getSoundManager().stop(this.field_8173);
			this.field_8173 = null;
			this.timeUntilNextSong = 0;
		}
	}

	public static enum MusicType {
		MENU(Sounds.MUSIC_MENU, 20, 600),
		GAME(Sounds.MUSIC_GAME, 12000, 24000),
		CREATIVE(Sounds.MUSIC_CREATIVE, 1200, 3600),
		CREDITS(Sounds.MUSIC_CREDITS, Integer.MAX_VALUE, Integer.MAX_VALUE),
		NETHER(Sounds.MUSIC_NETHER, 1200, 3600),
		END_BOSS(Sounds.MUSIC_DRAGON, 0, 0),
		END(Sounds.MUSIC_END, 6000, 24000);

		private final Sound field_13698;
		private final int minDelay;
		private final int maxDelay;

		private MusicType(Sound sound, int j, int k) {
			this.field_13698 = sound;
			this.minDelay = j;
			this.maxDelay = k;
		}

		public Sound method_7086() {
			return this.field_13698;
		}

		public int getMinDelay() {
			return this.minDelay;
		}

		public int getMaxDelay() {
			return this.maxDelay;
		}
	}
}
