package net.minecraft.client.sound;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloadListener;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SoundManager extends SinglePreparationResourceReloadListener<SoundManager.SoundList> {
	public static final Sound MISSING_SOUND = new Sound("meta:missing_sound", 1.0F, 1.0F, 1, Sound.RegistrationType.field_5474, false, false, 16);
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = new GsonBuilder()
		.registerTypeHierarchyAdapter(Text.class, new Text.Serializer())
		.registerTypeAdapter(SoundEntry.class, new SoundEntryDeserializer())
		.create();
	private static final ParameterizedType TYPE = new ParameterizedType() {
		public Type[] getActualTypeArguments() {
			return new Type[]{String.class, SoundEntry.class};
		}

		public Type getRawType() {
			return Map.class;
		}

		public Type getOwnerType() {
			return null;
		}
	};
	private final Map<Identifier, WeightedSoundSet> sounds = Maps.newHashMap();
	private final SoundSystem soundSystem;

	public SoundManager(ResourceManager resourceManager, GameOptions gameOptions) {
		this.soundSystem = new SoundSystem(this, gameOptions, resourceManager);
	}

	protected SoundManager.SoundList method_18180(ResourceManager resourceManager, Profiler profiler) {
		SoundManager.SoundList soundList = new SoundManager.SoundList();
		profiler.startTick();

		for (String string : resourceManager.getAllNamespaces()) {
			profiler.push(string);

			try {
				for (Resource resource : resourceManager.getAllResources(new Identifier(string, "sounds.json"))) {
					profiler.push(resource.getResourcePackName());

					try {
						profiler.push("parse");
						Map<String, SoundEntry> map = readSounds(resource.getInputStream());
						profiler.swap("register");

						for (Entry<String, SoundEntry> entry : map.entrySet()) {
							soundList.register(new Identifier(string, (String)entry.getKey()), (SoundEntry)entry.getValue(), resourceManager);
						}

						profiler.pop();
					} catch (RuntimeException var12) {
						LOGGER.warn("Invalid sounds.json in resourcepack: '{}'", resource.getResourcePackName(), var12);
					}

					profiler.pop();
				}
			} catch (IOException var13) {
			}

			profiler.pop();
		}

		profiler.endTick();
		return soundList;
	}

	protected void method_18182(SoundManager.SoundList soundList, ResourceManager resourceManager, Profiler profiler) {
		soundList.addTo(this.sounds, this.soundSystem);

		for (Identifier identifier : this.sounds.keySet()) {
			WeightedSoundSet weightedSoundSet = (WeightedSoundSet)this.sounds.get(identifier);
			if (weightedSoundSet.getSubtitle() instanceof TranslatableText) {
				String string = ((TranslatableText)weightedSoundSet.getSubtitle()).getKey();
				if (!I18n.hasTranslation(string)) {
					LOGGER.debug("Missing subtitle {} for event: {}", string, identifier);
				}
			}
		}

		if (LOGGER.isDebugEnabled()) {
			for (Identifier identifier2 : this.sounds.keySet()) {
				if (!Registry.SOUND_EVENT.containsId(identifier2)) {
					LOGGER.debug("Not having sound event for: {}", identifier2);
				}
			}
		}

		this.soundSystem.reloadSounds();
	}

	@Nullable
	protected static Map<String, SoundEntry> readSounds(InputStream inputStream) {
		Map var1;
		try {
			var1 = JsonHelper.deserialize(GSON, new InputStreamReader(inputStream, StandardCharsets.UTF_8), TYPE);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}

		return var1;
	}

	private static boolean isSoundResourcePresent(Sound sound, Identifier identifier, ResourceManager resourceManager) {
		Identifier identifier2 = sound.getLocation();
		if (!resourceManager.containsResource(identifier2)) {
			LOGGER.warn("File {} does not exist, cannot add it to event {}", identifier2, identifier);
			return false;
		} else {
			return true;
		}
	}

	@Nullable
	public WeightedSoundSet get(Identifier identifier) {
		return (WeightedSoundSet)this.sounds.get(identifier);
	}

	public Collection<Identifier> getKeys() {
		return this.sounds.keySet();
	}

	public void play(SoundInstance soundInstance) {
		this.soundSystem.play(soundInstance);
	}

	public void play(SoundInstance soundInstance, int i) {
		this.soundSystem.play(soundInstance, i);
	}

	public void updateListenerPosition(Camera camera) {
		this.soundSystem.updateListenerPosition(camera);
	}

	public void pauseAll() {
		this.soundSystem.pauseAll();
	}

	public void stopAll() {
		this.soundSystem.stopAll();
	}

	public void close() {
		this.soundSystem.stop();
	}

	public void tick(boolean bl) {
		this.soundSystem.tick(bl);
	}

	public void resumeAll() {
		this.soundSystem.resumeAll();
	}

	public void updateSoundVolume(SoundCategory soundCategory, float f) {
		if (soundCategory == SoundCategory.field_15250 && f <= 0.0F) {
			this.stopAll();
		}

		this.soundSystem.updateSoundVolume(soundCategory, f);
	}

	public void stop(SoundInstance soundInstance) {
		this.soundSystem.stop(soundInstance);
	}

	public boolean isPlaying(SoundInstance soundInstance) {
		return this.soundSystem.isPlaying(soundInstance);
	}

	public void registerListener(ListenerSoundInstance listenerSoundInstance) {
		this.soundSystem.registerListener(listenerSoundInstance);
	}

	public void unregisterListener(ListenerSoundInstance listenerSoundInstance) {
		this.soundSystem.unregisterListener(listenerSoundInstance);
	}

	public void stopSounds(@Nullable Identifier identifier, @Nullable SoundCategory soundCategory) {
		this.soundSystem.stopSounds(identifier, soundCategory);
	}

	public String getDebugString() {
		return this.soundSystem.getDebugString();
	}

	public static class SoundList {
		private final Map<Identifier, WeightedSoundSet> loadedSounds = Maps.newHashMap();

		protected SoundList() {
		}

		private void register(Identifier identifier, SoundEntry soundEntry, ResourceManager resourceManager) {
			WeightedSoundSet weightedSoundSet = (WeightedSoundSet)this.loadedSounds.get(identifier);
			boolean bl = weightedSoundSet == null;
			if (bl || soundEntry.canReplace()) {
				if (!bl) {
					SoundManager.LOGGER.debug("Replaced sound event location {}", identifier);
				}

				weightedSoundSet = new WeightedSoundSet(identifier, soundEntry.getSubtitle());
				this.loadedSounds.put(identifier, weightedSoundSet);
			}

			for (final Sound sound : soundEntry.getSounds()) {
				final Identifier identifier2 = sound.getIdentifier();
				SoundContainer<Sound> soundContainer2;
				switch (sound.getRegistrationType()) {
					case field_5474:
						if (!SoundManager.isSoundResourcePresent(sound, identifier, resourceManager)) {
							continue;
						}

						soundContainer2 = sound;
						break;
					case SOUND_EVENT:
						soundContainer2 = new SoundContainer<Sound>() {
							@Override
							public int getWeight() {
								WeightedSoundSet weightedSoundSet = (WeightedSoundSet)SoundList.this.loadedSounds.get(identifier2);
								return weightedSoundSet == null ? 0 : weightedSoundSet.getWeight();
							}

							public Sound method_4883() {
								WeightedSoundSet weightedSoundSet = (WeightedSoundSet)SoundList.this.loadedSounds.get(identifier2);
								if (weightedSoundSet == null) {
									return SoundManager.MISSING_SOUND;
								} else {
									Sound sound = weightedSoundSet.method_4887();
									return new Sound(
										sound.getIdentifier().toString(),
										sound.getVolume() * sound.getVolume(),
										sound.getPitch() * sound.getPitch(),
										sound.getWeight(),
										Sound.RegistrationType.field_5474,
										sound.isStreamed() || sound.isStreamed(),
										sound.isPreloaded(),
										sound.getAttenuation()
									);
								}
							}

							@Override
							public void preload(SoundSystem soundSystem) {
								WeightedSoundSet weightedSoundSet = (WeightedSoundSet)SoundList.this.loadedSounds.get(identifier2);
								if (weightedSoundSet != null) {
									weightedSoundSet.preload(soundSystem);
								}
							}
						};
						break;
					default:
						throw new IllegalStateException("Unknown SoundEventRegistration type: " + sound.getRegistrationType());
				}

				weightedSoundSet.add(soundContainer2);
			}
		}

		public void addTo(Map<Identifier, WeightedSoundSet> map, SoundSystem soundSystem) {
			map.clear();

			for (Entry<Identifier, WeightedSoundSet> entry : this.loadedSounds.entrySet()) {
				map.put(entry.getKey(), entry.getValue());
				((WeightedSoundSet)entry.getValue()).preload(soundSystem);
			}
		}
	}
}
