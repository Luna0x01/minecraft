package net.minecraft.client.sound;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.client.option.GameOptions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.sound.SoundEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SoundManager implements ResourceReloadListener, Tickable {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = new GsonBuilder().registerTypeAdapter(SoundEntry.class, new SoundEntryDeserializer()).create();
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
	public static final Sound MISSING_SOUND = new Sound(new Identifier("meta:missing_sound"), 0.0, 0.0, false);
	private final SoundRegistry loadedSounds = new SoundRegistry();
	private final SoundSystem soundSystem;
	private final ResourceManager resourceManager;

	public SoundManager(ResourceManager resourceManager, GameOptions gameOptions) {
		this.resourceManager = resourceManager;
		this.soundSystem = new SoundSystem(this, gameOptions);
	}

	@Override
	public void reload(ResourceManager resourceManager) {
		this.soundSystem.reloadSounds();
		this.loadedSounds.clearRegistry();

		for (String string : resourceManager.getAllNamespaces()) {
			try {
				for (Resource resource : resourceManager.getAllResources(new Identifier(string, "sounds.json"))) {
					try {
						Map<String, SoundEntry> map = this.readSounds(resource.getInputStream());

						for (Entry<String, SoundEntry> entry : map.entrySet()) {
							this.register(new Identifier(string, (String)entry.getKey()), (SoundEntry)entry.getValue());
						}
					} catch (RuntimeException var10) {
						LOGGER.warn("Invalid sounds.json", var10);
					}
				}
			} catch (IOException var11) {
			}
		}
	}

	protected Map<String, SoundEntry> readSounds(InputStream inputStream) {
		Map var2;
		try {
			var2 = (Map)GSON.fromJson(new InputStreamReader(inputStream), TYPE);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}

		return var2;
	}

	private void register(Identifier id, SoundEntry entry) {
		boolean bl = !this.loadedSounds.containsKey(id);
		WeightedSoundSet weightedSoundSet2;
		if (!bl && !entry.canReplace()) {
			weightedSoundSet2 = this.loadedSounds.get(id);
		} else {
			if (!bl) {
				LOGGER.debug("Replaced sound event location {}", new Object[]{id});
			}

			weightedSoundSet2 = new WeightedSoundSet(id, 1.0, 1.0, entry.getCategory());
			this.loadedSounds.add(weightedSoundSet2);
		}

		for (final SoundEntry.Entry entry2 : entry.getSounds()) {
			String string = entry2.method_7059();
			Identifier identifier = new Identifier(string);
			final String string2 = string.contains(":") ? identifier.getNamespace() : id.getNamespace();
			SoundContainer<Sound> soundContainer2;
			switch (entry2.method_7069()) {
				case FILE:
					Identifier identifier2 = new Identifier(string2, "sounds/" + identifier.getPath() + ".ogg");
					InputStream inputStream = null;

					try {
						inputStream = this.resourceManager.getResource(identifier2).getInputStream();
					} catch (FileNotFoundException var18) {
						LOGGER.warn("File {} does not exist, cannot add it to event {}", new Object[]{identifier2, id});
						continue;
					} catch (IOException var19) {
						LOGGER.warn("Could not load sound file " + identifier2 + ", cannot add it to event " + id, var19);
						continue;
					} finally {
						IOUtils.closeQuietly(inputStream);
					}

					soundContainer2 = new SoundContainerImpl(
						new Sound(identifier2, (double)entry2.method_7067(), (double)entry2.method_7065(), entry2.method_7070()), entry2.method_7068()
					);
					break;
				case EVENT:
					soundContainer2 = new SoundContainer<Sound>() {
						final Identifier field_8219 = new Identifier(string2, entry2.method_7059());

						@Override
						public int getWeight() {
							WeightedSoundSet weightedSoundSet = SoundManager.this.loadedSounds.get(this.field_8219);
							return weightedSoundSet == null ? 0 : weightedSoundSet.getWeight();
						}

						public Sound getSound() {
							WeightedSoundSet weightedSoundSet = SoundManager.this.loadedSounds.get(this.field_8219);
							return weightedSoundSet == null ? SoundManager.MISSING_SOUND : weightedSoundSet.getSound();
						}
					};
					break;
				default:
					throw new IllegalStateException("IN YOU FACE");
			}

			weightedSoundSet2.add(soundContainer2);
		}
	}

	public WeightedSoundSet get(Identifier id) {
		return this.loadedSounds.get(id);
	}

	public void play(SoundInstance sound) {
		this.soundSystem.play(sound);
	}

	public void play(SoundInstance sound, int delay) {
		this.soundSystem.play(sound, delay);
	}

	public void updateListenerPosition(PlayerEntity player, float tickDelta) {
		this.soundSystem.updateListenerPosition(player, tickDelta);
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

	@Override
	public void tick() {
		this.soundSystem.tick();
	}

	public void resumeAll() {
		this.soundSystem.resumeAll();
	}

	public void updateSoundVolume(SoundCategory category, float volume) {
		if (category == SoundCategory.MASTER && volume <= 0.0F) {
			this.stopAll();
		}

		this.soundSystem.updateSoundVolume(category, volume);
	}

	public void stop(SoundInstance instance) {
		this.soundSystem.stop(instance);
	}

	public WeightedSoundSet getSoundSet(SoundCategory... categories) {
		List<WeightedSoundSet> list = Lists.newArrayList();

		for (Identifier identifier : this.loadedSounds.keySet()) {
			WeightedSoundSet weightedSoundSet = this.loadedSounds.get(identifier);
			if (ArrayUtils.contains(categories, weightedSoundSet.getSoundCategory())) {
				list.add(weightedSoundSet);
			}
		}

		return list.isEmpty() ? null : (WeightedSoundSet)list.get(new Random().nextInt(list.size()));
	}

	public boolean isPlaying(SoundInstance instance) {
		return this.soundSystem.isPlaying(instance);
	}
}
