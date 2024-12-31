package net.minecraft.client.sound;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.MutableRegistry;

public class SoundRegistry extends MutableRegistry<Identifier, WeightedSoundSet> {
	private Map<Identifier, WeightedSoundSet> sounds;

	@Override
	protected Map<Identifier, WeightedSoundSet> createMap() {
		this.sounds = Maps.newHashMap();
		return this.sounds;
	}

	public void add(WeightedSoundSet set) {
		this.put(set.getIdentifier(), set);
	}

	public void clearRegistry() {
		this.sounds.clear();
	}
}
