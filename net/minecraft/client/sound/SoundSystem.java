package net.minecraft.client.sound;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import io.netty.util.internal.ThreadLocalRandom;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.class_2906;
import net.minecraft.client.class_2907;
import net.minecraft.client.option.GameOptions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.Sound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.SoundSystemLogger;
import paulscode.sound.Source;
import paulscode.sound.codecs.CodecJOrbis;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;

public class SoundSystem {
	private static final Marker MARKER = MarkerManager.getMarker("SOUNDS");
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Set<Identifier> field_13699 = Sets.newHashSet();
	private final SoundManager manager;
	private final GameOptions options;
	private SoundSystem.ThreadSafeSoundSystem field_8193;
	private boolean started;
	private int ticks;
	private final Map<String, SoundInstance> field_8195 = HashBiMap.create();
	private final Map<SoundInstance, String> field_8196 = ((BiMap)this.field_8195).inverse();
	private final Multimap<SoundCategory, String> field_8198 = HashMultimap.create();
	private final List<TickableSoundInstance> field_8199 = Lists.newArrayList();
	private final Map<SoundInstance, Integer> startTicks = Maps.newHashMap();
	private final Map<String, Integer> field_8201 = Maps.newHashMap();
	private final List<class_2907> field_13700 = Lists.newArrayList();
	private final List<String> field_13701 = Lists.newArrayList();

	public SoundSystem(SoundManager soundManager, GameOptions gameOptions) {
		this.manager = soundManager;
		this.options = gameOptions;

		try {
			SoundSystemConfig.addLibrary(LibraryLWJGLOpenAL.class);
			SoundSystemConfig.setCodec("ogg", CodecJOrbis.class);
		} catch (SoundSystemException var4) {
			LOGGER.error(MARKER, "Error linking with the LibraryJavaSound plug-in", var4);
		}
	}

	public void reloadSounds() {
		field_13699.clear();

		for (Sound sound : Sound.REGISTRY) {
			Identifier identifier = sound.getId();
			if (this.manager.method_12545(identifier) == null) {
				LOGGER.warn("Missing sound for event: {}", new Object[]{Sound.REGISTRY.getIdentifier(sound)});
				field_13699.add(identifier);
			}
		}

		this.stop();
		this.start();
	}

	private synchronized void start() {
		if (!this.started) {
			try {
				new Thread(new Runnable() {
					public void run() {
						SoundSystemConfig.setLogger(new SoundSystemLogger() {
							public void message(String string, int i) {
								if (!string.isEmpty()) {
									SoundSystem.LOGGER.info(string);
								}
							}

							public void importantMessage(String string, int i) {
								if (!string.isEmpty()) {
									SoundSystem.LOGGER.warn(string);
								}
							}

							public void errorMessage(String string, String string2, int i) {
								if (!string2.isEmpty()) {
									SoundSystem.LOGGER.error("Error in class '{}'", new Object[]{string});
									SoundSystem.LOGGER.error(string2);
								}
							}
						});
						SoundSystem.this.field_8193 = SoundSystem.this.new ThreadSafeSoundSystem();
						SoundSystem.this.started = true;
						SoundSystem.this.field_8193.setMasterVolume(SoundSystem.this.options.getSoundVolume(SoundCategory.MASTER));
						SoundSystem.LOGGER.info(SoundSystem.MARKER, "Sound engine started");
					}
				}, "Sound Library Loader").start();
			} catch (RuntimeException var2) {
				LOGGER.error(MARKER, "Error starting SoundSystem. Turning off sounds & music", var2);
				this.options.setSoundVolume(SoundCategory.MASTER, 0.0F);
				this.options.save();
			}
		}
	}

	private float getSoundVolume(SoundCategory soundCategory) {
		return soundCategory != null && soundCategory != SoundCategory.MASTER ? this.options.getSoundVolume(soundCategory) : 1.0F;
	}

	public void updateSoundVolume(SoundCategory soundCategory, float volume) {
		if (this.started) {
			if (soundCategory == SoundCategory.MASTER) {
				this.field_8193.setMasterVolume(volume);
			} else {
				for (String string : this.field_8198.get(soundCategory)) {
					SoundInstance soundInstance = (SoundInstance)this.field_8195.get(string);
					float f = this.method_12540(soundInstance);
					if (f <= 0.0F) {
						this.stop(soundInstance);
					} else {
						this.field_8193.setVolume(string, f);
					}
				}
			}
		}
	}

	public void stop() {
		if (this.started) {
			this.stopAll();
			this.field_8193.cleanup();
			this.started = false;
		}
	}

	public void stopAll() {
		if (this.started) {
			for (String string : this.field_8195.keySet()) {
				this.field_8193.stop(string);
			}

			this.field_8195.clear();
			this.startTicks.clear();
			this.field_8199.clear();
			this.field_8198.clear();
			this.field_8201.clear();
		}
	}

	public void method_12536(class_2907 arg) {
		this.field_13700.add(arg);
	}

	public void method_12538(class_2907 arg) {
		this.field_13700.remove(arg);
	}

	public void tick() {
		this.ticks++;

		for (TickableSoundInstance tickableSoundInstance : this.field_8199) {
			tickableSoundInstance.tick();
			if (tickableSoundInstance.isDone()) {
				this.stop(tickableSoundInstance);
			} else {
				String string = (String)this.field_8196.get(tickableSoundInstance);
				this.field_8193.setVolume(string, this.method_12540(tickableSoundInstance));
				this.field_8193.setPitch(string, this.method_12539(tickableSoundInstance));
				this.field_8193.setPosition(string, tickableSoundInstance.getX(), tickableSoundInstance.getY(), tickableSoundInstance.getZ());
			}
		}

		Iterator<Entry<String, SoundInstance>> iterator2 = this.field_8195.entrySet().iterator();

		while (iterator2.hasNext()) {
			Entry<String, SoundInstance> entry = (Entry<String, SoundInstance>)iterator2.next();
			String string2 = (String)entry.getKey();
			SoundInstance soundInstance = (SoundInstance)entry.getValue();
			if (!this.field_8193.playing(string2)) {
				int i = (Integer)this.field_8201.get(string2);
				if (i <= this.ticks) {
					int j = soundInstance.getRepeatDelay();
					if (soundInstance.isRepeatable() && j > 0) {
						this.startTicks.put(soundInstance, this.ticks + j);
					}

					iterator2.remove();
					LOGGER.debug(MARKER, "Removed channel {} because it's not playing anymore", new Object[]{string2});
					this.field_8193.removeSource(string2);
					this.field_8201.remove(string2);

					try {
						this.field_8198.remove(soundInstance.getCategory(), string2);
					} catch (RuntimeException var8) {
					}

					if (soundInstance instanceof TickableSoundInstance) {
						this.field_8199.remove(soundInstance);
					}
				}
			}
		}

		Iterator<Entry<SoundInstance, Integer>> iterator3 = this.startTicks.entrySet().iterator();

		while (iterator3.hasNext()) {
			Entry<SoundInstance, Integer> entry2 = (Entry<SoundInstance, Integer>)iterator3.next();
			if (this.ticks >= (Integer)entry2.getValue()) {
				SoundInstance soundInstance2 = (SoundInstance)entry2.getKey();
				if (soundInstance2 instanceof TickableSoundInstance) {
					((TickableSoundInstance)soundInstance2).tick();
				}

				this.play(soundInstance2);
				iterator3.remove();
			}
		}
	}

	public boolean isPlaying(SoundInstance soundInstance) {
		if (!this.started) {
			return false;
		} else {
			String string = (String)this.field_8196.get(soundInstance);
			return string == null ? false : this.field_8193.playing(string) || this.field_8201.containsKey(string) && (Integer)this.field_8201.get(string) <= this.ticks;
		}
	}

	public void stop(SoundInstance soundInstance) {
		if (this.started) {
			String string = (String)this.field_8196.get(soundInstance);
			if (string != null) {
				this.field_8193.stop(string);
			}
		}
	}

	public void play(SoundInstance soundInstance) {
		if (this.started) {
			SoundContainerImpl soundContainerImpl = soundInstance.method_12532(this.manager);
			Identifier identifier = soundInstance.getIdentifier();
			if (soundContainerImpl == null) {
				if (field_13699.add(identifier)) {
					LOGGER.warn(MARKER, "Unable to play unknown soundEvent: {}", new Object[]{identifier});
				}
			} else {
				if (!this.field_13700.isEmpty()) {
					for (class_2907 lv : this.field_13700) {
						lv.method_12541(soundInstance, soundContainerImpl);
					}
				}

				if (this.field_8193.getMasterVolume() <= 0.0F) {
					LOGGER.debug(MARKER, "Skipped playing soundEvent: {}, master volume was zero", new Object[]{identifier});
				} else {
					class_2906 lv2 = soundInstance.method_12533();
					if (lv2 == SoundManager.field_13702) {
						if (field_13699.add(identifier)) {
							LOGGER.warn(MARKER, "Unable to play empty soundEvent: {}", new Object[]{identifier});
						}
					} else {
						float f = soundInstance.getVolume();
						float g = 16.0F;
						if (f > 1.0F) {
							g *= f;
						}

						SoundCategory soundCategory = soundInstance.getCategory();
						float h = this.method_12540(soundInstance);
						float i = this.method_12539(soundInstance);
						if (h == 0.0F) {
							LOGGER.debug(MARKER, "Skipped playing sound {}, volume was zero.", new Object[]{lv2.method_12522()});
						} else {
							boolean bl = soundInstance.isRepeatable() && soundInstance.getRepeatDelay() == 0;
							String string = MathHelper.randomUuid(ThreadLocalRandom.current()).toString();
							Identifier identifier2 = lv2.method_12523();
							if (lv2.method_12528()) {
								this.field_8193
									.newStreamingSource(
										false,
										string,
										method_7096(identifier2),
										identifier2.toString(),
										bl,
										soundInstance.getX(),
										soundInstance.getY(),
										soundInstance.getZ(),
										soundInstance.getAttenuationType().getInteger(),
										g
									);
							} else {
								this.field_8193
									.newSource(
										false,
										string,
										method_7096(identifier2),
										identifier2.toString(),
										bl,
										soundInstance.getX(),
										soundInstance.getY(),
										soundInstance.getZ(),
										soundInstance.getAttenuationType().getInteger(),
										g
									);
							}

							LOGGER.debug(MARKER, "Playing sound {} for event {} as channel {}", new Object[]{lv2.method_12522(), identifier2, string});
							this.field_8193.setPitch(string, i);
							this.field_8193.setVolume(string, h);
							this.field_8193.play(string);
							this.field_8201.put(string, this.ticks + 20);
							this.field_8195.put(string, soundInstance);
							if (soundCategory != SoundCategory.MASTER) {
								this.field_8198.put(soundCategory, string);
							}

							if (soundInstance instanceof TickableSoundInstance) {
								this.field_8199.add((TickableSoundInstance)soundInstance);
							}
						}
					}
				}
			}
		}
	}

	private float method_12539(SoundInstance soundInstance) {
		return MathHelper.clamp(soundInstance.getPitch(), 0.5F, 2.0F);
	}

	private float method_12540(SoundInstance soundInstance) {
		return MathHelper.clamp(soundInstance.getVolume() * this.getSoundVolume(soundInstance.getCategory()), 0.0F, 1.0F);
	}

	public void pauseAll() {
		for (Entry<String, SoundInstance> entry : this.field_8195.entrySet()) {
			String string = (String)entry.getKey();
			boolean bl = this.isPlaying((SoundInstance)entry.getValue());
			if (bl) {
				LOGGER.debug(MARKER, "Pausing channel {}", new Object[]{string});
				this.field_8193.pause(string);
				this.field_13701.add(string);
			}
		}
	}

	public void resumeAll() {
		for (String string : this.field_13701) {
			LOGGER.debug(MARKER, "Resuming channel {}", new Object[]{string});
			this.field_8193.play(string);
		}

		this.field_13701.clear();
	}

	public void play(SoundInstance sound, int delay) {
		this.startTicks.put(sound, this.ticks + delay);
	}

	private static URL method_7096(Identifier id) {
		String string = String.format("%s:%s:%s", "mcsounddomain", id.getNamespace(), id.getPath());
		URLStreamHandler uRLStreamHandler = new URLStreamHandler() {
			protected URLConnection openConnection(URL url) {
				return new URLConnection(url) {
					public void connect() throws IOException {
					}

					public InputStream getInputStream() throws IOException {
						return MinecraftClient.getInstance().getResourceManager().getResource(id).getInputStream();
					}
				};
			}
		};

		try {
			return new URL(null, string, uRLStreamHandler);
		} catch (MalformedURLException var4) {
			throw new Error("TODO: Sanely handle url exception! :D");
		}
	}

	public void updateListenerPosition(PlayerEntity player, float tickDelta) {
		if (this.started && player != null) {
			float f = player.prevPitch + (player.pitch - player.prevPitch) * tickDelta;
			float g = player.prevYaw + (player.yaw - player.prevYaw) * tickDelta;
			double d = player.prevX + (player.x - player.prevX) * (double)tickDelta;
			double e = player.prevY + (player.y - player.prevY) * (double)tickDelta + (double)player.getEyeHeight();
			double h = player.prevZ + (player.z - player.prevZ) * (double)tickDelta;
			float i = MathHelper.cos((g + 90.0F) * (float) (Math.PI / 180.0));
			float j = MathHelper.sin((g + 90.0F) * (float) (Math.PI / 180.0));
			float k = MathHelper.cos(-f * (float) (Math.PI / 180.0));
			float l = MathHelper.sin(-f * (float) (Math.PI / 180.0));
			float m = MathHelper.cos((-f + 90.0F) * (float) (Math.PI / 180.0));
			float n = MathHelper.sin((-f + 90.0F) * (float) (Math.PI / 180.0));
			float o = i * k;
			float q = j * k;
			float r = i * m;
			float t = j * m;
			this.field_8193.setListenerPosition((float)d, (float)e, (float)h);
			this.field_8193.setListenerOrientation(o, l, q, r, n, t);
		}
	}

	public void method_12537(String string, SoundCategory soundCategory) {
		if (soundCategory != null) {
			for (String string2 : this.field_8198.get(soundCategory)) {
				SoundInstance soundInstance = (SoundInstance)this.field_8195.get(string2);
				if (!string.isEmpty()) {
					if (soundInstance.getIdentifier().equals(new Identifier(string))) {
						this.stop(soundInstance);
					}
				} else {
					this.stop(soundInstance);
				}
			}
		} else if (!string.isEmpty()) {
			for (SoundInstance soundInstance2 : this.field_8195.values()) {
				if (soundInstance2.getIdentifier().equals(new Identifier(string))) {
					this.stop(soundInstance2);
				}
			}
		} else {
			this.stopAll();
		}
	}

	class ThreadSafeSoundSystem extends paulscode.sound.SoundSystem {
		private ThreadSafeSoundSystem() {
		}

		public boolean playing(String string) {
			synchronized (SoundSystemConfig.THREAD_SYNC) {
				if (this.soundLibrary == null) {
					return false;
				} else {
					Source source = (Source)this.soundLibrary.getSources().get(string);
					return source == null ? false : source.playing() || source.paused() || source.preLoad;
				}
			}
		}
	}
}
