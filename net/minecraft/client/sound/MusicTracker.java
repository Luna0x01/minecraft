package net.minecraft.client.sound;

import java.util.Random;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
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
			if (!musicType.getSoundIdentifier().equals(this.field_8173.getIdentifier())) {
				this.field_8172.getSoundManager().stop(this.field_8173);
				this.timeUntilNextSong = MathHelper.nextInt(this.random, 0, musicType.getMinDelay() / 2);
			}

			if (!this.field_8172.getSoundManager().isPlaying(this.field_8173)) {
				this.field_8173 = null;
				this.timeUntilNextSong = Math.min(MathHelper.nextInt(this.random, musicType.getMinDelay(), musicType.getMaxDelay()), this.timeUntilNextSong);
			}
		}

		if (this.field_8173 == null && this.timeUntilNextSong-- <= 0) {
			this.play(musicType);
		}
	}

	public void play(MusicTracker.MusicType musicType) {
		this.field_8173 = PositionedSoundInstance.method_7051(musicType.getSoundIdentifier());
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
		MENU(new Identifier("minecraft:music.menu"), 20, 600),
		GAME(new Identifier("minecraft:music.game"), 12000, 24000),
		CREATIVE(new Identifier("minecraft:music.game.creative"), 1200, 3600),
		CREDITS(new Identifier("minecraft:music.game.end.credits"), Integer.MAX_VALUE, Integer.MAX_VALUE),
		NETHER(new Identifier("minecraft:music.game.nether"), 1200, 3600),
		END_BOSS(new Identifier("minecraft:music.game.end.dragon"), 0, 0),
		END(new Identifier("minecraft:music.game.end"), 6000, 24000);

		private final Identifier sound;
		private final int minDelay;
		private final int maxDelay;

		private MusicType(Identifier identifier, int j, int k) {
			this.sound = identifier;
			this.minDelay = j;
			this.maxDelay = k;
		}

		public Identifier getSoundIdentifier() {
			return this.sound;
		}

		public int getMinDelay() {
			return this.minDelay;
		}

		public int getMaxDelay() {
			return this.maxDelay;
		}
	}
}
