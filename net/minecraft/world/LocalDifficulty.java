package net.minecraft.world;

import javax.annotation.concurrent.Immutable;
import net.minecraft.util.math.MathHelper;

@Immutable
public class LocalDifficulty {
	private final Difficulty difficulty;
	private final float localDifficulty;

	public LocalDifficulty(Difficulty difficulty, long l, long m, float f) {
		this.difficulty = difficulty;
		this.localDifficulty = this.calculateLocalDifficulty(difficulty, l, m, f);
	}

	public float getLocalDifficulty() {
		return this.localDifficulty;
	}

	public float getClampedLocalDifficulty() {
		if (this.localDifficulty < 2.0F) {
			return 0.0F;
		} else {
			return this.localDifficulty > 4.0F ? 1.0F : (this.localDifficulty - 2.0F) / 2.0F;
		}
	}

	private float calculateLocalDifficulty(Difficulty difficulty, long daytimeTicks, long chunkInhibitedTimeTicks, float moonPhase) {
		if (difficulty == Difficulty.PEACEFUL) {
			return 0.0F;
		} else {
			boolean bl = difficulty == Difficulty.HARD;
			float f = 0.75F;
			float g = MathHelper.clamp(((float)daytimeTicks + -72000.0F) / 1440000.0F, 0.0F, 1.0F) * 0.25F;
			f += g;
			float h = 0.0F;
			h += MathHelper.clamp((float)chunkInhibitedTimeTicks / 3600000.0F, 0.0F, 1.0F) * (bl ? 1.0F : 0.75F);
			h += MathHelper.clamp(moonPhase * 0.25F, 0.0F, g);
			if (difficulty == Difficulty.EASY) {
				h *= 0.5F;
			}

			f += h;
			return (float)difficulty.getId() * f;
		}
	}
}
