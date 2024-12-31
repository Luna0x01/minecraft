package net.minecraft.client.sound;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.util.Identifier;

public class WeightedSoundSet implements SoundContainer<Sound> {
	private final List<SoundContainer<Sound>> sounds = Lists.newArrayList();
	private final Random random = new Random();
	private final Identifier id;
	private final SoundCategory soundCategory;
	private double volumeAdjustment;
	private double pitchAdjustment;

	public WeightedSoundSet(Identifier identifier, double d, double e, SoundCategory soundCategory) {
		this.id = identifier;
		this.pitchAdjustment = e;
		this.volumeAdjustment = d;
		this.soundCategory = soundCategory;
	}

	@Override
	public int getWeight() {
		int i = 0;

		for (SoundContainer<Sound> soundContainer : this.sounds) {
			i += soundContainer.getWeight();
		}

		return i;
	}

	public Sound getSound() {
		int i = this.getWeight();
		if (!this.sounds.isEmpty() && i != 0) {
			int j = this.random.nextInt(i);

			for (SoundContainer<Sound> soundContainer : this.sounds) {
				j -= soundContainer.getWeight();
				if (j < 0) {
					Sound sound = soundContainer.getSound();
					sound.setVolume(sound.getVolume() * this.volumeAdjustment);
					sound.setPitch(sound.getPitch() * this.pitchAdjustment);
					return sound;
				}
			}

			return SoundManager.MISSING_SOUND;
		} else {
			return SoundManager.MISSING_SOUND;
		}
	}

	public void add(SoundContainer<Sound> container) {
		this.sounds.add(container);
	}

	public Identifier getIdentifier() {
		return this.id;
	}

	public SoundCategory getSoundCategory() {
		return this.soundCategory;
	}
}
