package net.minecraft.client.sound;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.class_2906;
import net.minecraft.client.class_2907;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.sound.Sound;
import net.minecraft.sound.SoundEntry;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SoundManager implements ResourceReloadListener, Tickable {
	public static final class_2906 field_13702 = new class_2906("meta:missing_sound", 1.0F, 1.0F, 1, class_2906.class_1898.FILE, false);
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
	private final SoundRegistry loadedSounds = new SoundRegistry();
	private final SoundSystem soundSystem;
	private final ResourceManager resourceManager;

	public SoundManager(ResourceManager resourceManager, GameOptions gameOptions) {
		this.resourceManager = resourceManager;
		this.soundSystem = new SoundSystem(this, gameOptions);
	}

	@Override
	public void reload(ResourceManager resourceManager) {
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

		for (Identifier identifier : this.loadedSounds.getKeySet()) {
			SoundContainerImpl soundContainerImpl = this.loadedSounds.get(identifier);
			if (soundContainerImpl.method_12551() instanceof TranslatableText) {
				String string2 = ((TranslatableText)soundContainerImpl.method_12551()).getKey();
				if (!I18n.method_12500(string2)) {
					LOGGER.debug("Missing subtitle {} for event: {}", new Object[]{string2, identifier});
				}
			}
		}

		for (Identifier identifier2 : this.loadedSounds.getKeySet()) {
			if (Sound.REGISTRY.get(identifier2) == null) {
				LOGGER.debug("Not having sound event for: {}", new Object[]{identifier2});
			}
		}

		this.soundSystem.reloadSounds();
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
		SoundContainerImpl soundContainerImpl = this.loadedSounds.get(id);
		boolean bl = soundContainerImpl == null;
		if (bl || entry.canReplace()) {
			if (!bl) {
				LOGGER.debug("Replaced sound event location {}", new Object[]{id});
			}

			soundContainerImpl = new SoundContainerImpl(id, entry.method_7058());
			this.loadedSounds.method_12547(soundContainerImpl);
		}

		for (class_2906 lv : entry.getSounds()) {
			final Identifier identifier = lv.method_12522();
			SoundContainer<class_2906> soundContainer;
			switch (lv.method_12527()) {
				case FILE:
					if (!this.method_12542(lv, id)) {
						continue;
					}

					soundContainer = lv;
					break;
				case SOUND_EVENT:
					soundContainer = new SoundContainer<class_2906>() {
						@Override
						public int getWeight() {
							SoundContainerImpl soundContainerImpl = SoundManager.this.loadedSounds.get(identifier);
							return soundContainerImpl == null ? 0 : soundContainerImpl.getWeight();
						}

						public class_2906 getSound() {
							SoundContainerImpl soundContainerImpl = SoundManager.this.loadedSounds.get(identifier);
							return soundContainerImpl == null ? SoundManager.field_13702 : soundContainerImpl.getSound();
						}
					};
					break;
				default:
					throw new IllegalStateException("Unknown SoundEventRegistration type: " + lv.method_12527());
			}

			soundContainerImpl.method_12549(soundContainer);
		}
	}

	private boolean method_12542(class_2906 arg, Identifier identifier) {
		Identifier identifier2 = arg.method_12523();
		Resource resource = null;

		boolean var6;
		try {
			resource = this.resourceManager.getResource(identifier2);
			resource.getInputStream();
			return true;
		} catch (FileNotFoundException var11) {
			LOGGER.warn("File {} does not exist, cannot add it to event {}", new Object[]{identifier2, identifier});
			return false;
		} catch (IOException var12) {
			LOGGER.warn("Could not load sound file " + identifier2 + ", cannot add it to event " + identifier, var12);
			var6 = false;
		} finally {
			IOUtils.closeQuietly(resource);
		}

		return var6;
	}

	@Nullable
	public SoundContainerImpl method_12545(Identifier identifier) {
		return this.loadedSounds.get(identifier);
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

	public boolean isPlaying(SoundInstance instance) {
		return this.soundSystem.isPlaying(instance);
	}

	public void method_12543(class_2907 arg) {
		this.soundSystem.method_12536(arg);
	}

	public void method_12546(class_2907 arg) {
		this.soundSystem.method_12538(arg);
	}

	public void stopSound(String string, SoundCategory soundCategory) {
		this.soundSystem.method_12537(string, soundCategory);
	}
}
