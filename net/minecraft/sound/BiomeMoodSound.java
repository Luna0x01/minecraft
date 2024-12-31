package net.minecraft.sound;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class BiomeMoodSound {
	public static final Codec<BiomeMoodSound> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					SoundEvent.CODEC.fieldOf("sound").forGetter(biomeMoodSound -> biomeMoodSound.sound),
					Codec.INT.fieldOf("tick_delay").forGetter(biomeMoodSound -> biomeMoodSound.cultivationTicks),
					Codec.INT.fieldOf("block_search_extent").forGetter(biomeMoodSound -> biomeMoodSound.spawnRange),
					Codec.DOUBLE.fieldOf("offset").forGetter(biomeMoodSound -> biomeMoodSound.extraDistance)
				)
				.apply(instance, BiomeMoodSound::new)
	);
	public static final BiomeMoodSound CAVE = new BiomeMoodSound(SoundEvents.AMBIENT_CAVE, 6000, 8, 2.0);
	private SoundEvent sound;
	private int cultivationTicks;
	private int spawnRange;
	private double extraDistance;

	public BiomeMoodSound(SoundEvent sound, int cultivationTicks, int spawnRange, double extraDistance) {
		this.sound = sound;
		this.cultivationTicks = cultivationTicks;
		this.spawnRange = spawnRange;
		this.extraDistance = extraDistance;
	}

	public SoundEvent getSound() {
		return this.sound;
	}

	public int getCultivationTicks() {
		return this.cultivationTicks;
	}

	public int getSpawnRange() {
		return this.spawnRange;
	}

	public double getExtraDistance() {
		return this.extraDistance;
	}
}
