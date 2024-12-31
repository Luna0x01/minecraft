package net.minecraft.client.sound;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.MutableRegistry;

public class SoundRegistry extends MutableRegistry<Identifier, SoundContainerImpl> {
	private Map<Identifier, SoundContainerImpl> sounds;

	@Override
	protected Map<Identifier, SoundContainerImpl> createMap() {
		this.sounds = Maps.newHashMap();
		return this.sounds;
	}

	public void method_12547(SoundContainerImpl soundContainerImpl) {
		this.put(soundContainerImpl.method_12550(), soundContainerImpl);
	}

	public void clearRegistry() {
		this.sounds.clear();
	}
}
