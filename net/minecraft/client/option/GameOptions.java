package net.minecraft.client.option;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.twitch.TwitchAuth;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientSettingsC2SPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class GameOptions {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = new Gson();
	private static final ParameterizedType STRING_LIST_TYPE = new ParameterizedType() {
		public Type[] getActualTypeArguments() {
			return new Type[]{String.class};
		}

		public Type getRawType() {
			return List.class;
		}

		public Type getOwnerType() {
			return null;
		}
	};
	private static final String[] GUI_SCALE = new String[]{"options.guiScale.auto", "options.guiScale.small", "options.guiScale.normal", "options.guiScale.large"};
	private static final String[] PARTICLES = new String[]{"options.particles.all", "options.particles.decreased", "options.particles.minimal"};
	private static final String[] AMBIENT_OCCLUSION = new String[]{"options.ao.off", "options.ao.min", "options.ao.max"};
	private static final String[] CHAT_VISIBILITY = new String[]{
		"options.stream.compression.low", "options.stream.compression.medium", "options.stream.compression.high"
	};
	private static final String[] STREAM_CHAT_ENABLED = new String[]{
		"options.stream.chat.enabled.streaming", "options.stream.chat.enabled.always", "options.stream.chat.enabled.never"
	};
	private static final String[] STREAM_CHAT_USERFILTER = new String[]{
		"options.stream.chat.userFilter.all", "options.stream.chat.userFilter.subs", "options.stream.chat.userFilter.mods"
	};
	private static final String[] STREAM_MIC_TOGGLE = new String[]{"options.stream.mic_toggle.mute", "options.stream.mic_toggle.talk"};
	private static final String[] GRAPHICS_LEVEL = new String[]{"options.off", "options.graphics.fast", "options.graphics.fancy"};
	public float sensitivity = 0.5F;
	public boolean invertYMouse;
	public int viewDistance = -1;
	public boolean bobView = true;
	public boolean anaglyph3d;
	public boolean fbo = true;
	public int maxFramerate = 120;
	public int cloudMode = 2;
	public boolean fancyGraphics = true;
	public int ao = 2;
	public List<String> resourcePacks = Lists.newArrayList();
	public List<String> incompatibleResourcePacks = Lists.newArrayList();
	public PlayerEntity.ChatVisibilityType chatVisibilityType = PlayerEntity.ChatVisibilityType.FULL;
	public boolean chatColor = true;
	public boolean chatLink = true;
	public boolean chatLinkPrompt = true;
	public float chatOpacity = 1.0F;
	public boolean snopperEnabled = true;
	public boolean fullscreen;
	public boolean vsync = true;
	public boolean vbo = false;
	public boolean alternativeBlocks = true;
	public boolean reducedDebugInfo = false;
	public boolean hideServerAddress;
	public boolean advancedItemTooltips;
	public boolean pauseOnLostFocus = true;
	private final Set<PlayerModelPart> playerModelParts = Sets.newHashSet(PlayerModelPart.values());
	public boolean touchscreen;
	public int overrideWidth;
	public int overrideHeight;
	public boolean heldItemTooltips = true;
	public float chatScale = 1.0F;
	public float chatWidth = 1.0F;
	public float chatHeightUnfocused = 0.44366196F;
	public float chatHeightFocused = 1.0F;
	public boolean showInventoryAchievementHint = true;
	public int mipmapLevels = 4;
	private Map<SoundCategory, Float> soundVolumeLevels = Maps.newEnumMap(SoundCategory.class);
	public float streamBytesPerPixel = 0.5F;
	public float streamMicVolume = 1.0F;
	public float streamSystemVolume = 1.0F;
	public float streamKbps = 0.5412844F;
	public float streamFps = 0.31690142F;
	public int streamCompression = 1;
	public boolean streamSendMetadata = true;
	public String currentTexturePackName = "";
	public int streamChatEnabled = 0;
	public int streamChatUserFilter = 0;
	public int streamMicToggleBehavior = 0;
	public boolean useNativeTransport = true;
	public boolean entityShadows = true;
	public boolean realmsNotifications = true;
	public KeyBinding forwardKey = new KeyBinding("key.forward", 17, "key.categories.movement");
	public KeyBinding leftKey = new KeyBinding("key.left", 30, "key.categories.movement");
	public KeyBinding backKey = new KeyBinding("key.back", 31, "key.categories.movement");
	public KeyBinding rightKey = new KeyBinding("key.right", 32, "key.categories.movement");
	public KeyBinding jumpKey = new KeyBinding("key.jump", 57, "key.categories.movement");
	public KeyBinding sneakKey = new KeyBinding("key.sneak", 42, "key.categories.movement");
	public KeyBinding sprintKey = new KeyBinding("key.sprint", 29, "key.categories.movement");
	public KeyBinding inventoryKey = new KeyBinding("key.inventory", 18, "key.categories.inventory");
	public KeyBinding useKey = new KeyBinding("key.use", -99, "key.categories.gameplay");
	public KeyBinding dropKey = new KeyBinding("key.drop", 16, "key.categories.gameplay");
	public KeyBinding attackKey = new KeyBinding("key.attack", -100, "key.categories.gameplay");
	public KeyBinding pickItemKey = new KeyBinding("key.pickItem", -98, "key.categories.gameplay");
	public KeyBinding chatKey = new KeyBinding("key.chat", 20, "key.categories.multiplayer");
	public KeyBinding playerListKey = new KeyBinding("key.playerlist", 15, "key.categories.multiplayer");
	public KeyBinding commandKey = new KeyBinding("key.command", 53, "key.categories.multiplayer");
	public KeyBinding screenshotKey = new KeyBinding("key.screenshot", 60, "key.categories.misc");
	public KeyBinding togglePerspectiveKey = new KeyBinding("key.togglePerspective", 63, "key.categories.misc");
	public KeyBinding smoothCameraKey = new KeyBinding("key.smoothCamera", 0, "key.categories.misc");
	public KeyBinding fullscreenKey = new KeyBinding("key.fullscreen", 87, "key.categories.misc");
	public KeyBinding spectatorOutlines = new KeyBinding("key.spectatorOutlines", 0, "key.categories.misc");
	public KeyBinding streamStartStopKey = new KeyBinding("key.streamStartStop", 64, "key.categories.stream");
	public KeyBinding streamPauseUnpauseKey = new KeyBinding("key.streamPauseUnpause", 65, "key.categories.stream");
	public KeyBinding streamCommercialKey = new KeyBinding("key.streamCommercial", 0, "key.categories.stream");
	public KeyBinding streamToggleMicKey = new KeyBinding("key.streamToggleMic", 0, "key.categories.stream");
	public KeyBinding[] hotbarKeys = new KeyBinding[]{
		new KeyBinding("key.hotbar.1", 2, "key.categories.inventory"),
		new KeyBinding("key.hotbar.2", 3, "key.categories.inventory"),
		new KeyBinding("key.hotbar.3", 4, "key.categories.inventory"),
		new KeyBinding("key.hotbar.4", 5, "key.categories.inventory"),
		new KeyBinding("key.hotbar.5", 6, "key.categories.inventory"),
		new KeyBinding("key.hotbar.6", 7, "key.categories.inventory"),
		new KeyBinding("key.hotbar.7", 8, "key.categories.inventory"),
		new KeyBinding("key.hotbar.8", 9, "key.categories.inventory"),
		new KeyBinding("key.hotbar.9", 10, "key.categories.inventory")
	};
	public KeyBinding[] allKeys = (KeyBinding[])ArrayUtils.addAll(
		new KeyBinding[]{
			this.attackKey,
			this.useKey,
			this.forwardKey,
			this.leftKey,
			this.backKey,
			this.rightKey,
			this.jumpKey,
			this.sneakKey,
			this.sprintKey,
			this.dropKey,
			this.inventoryKey,
			this.chatKey,
			this.playerListKey,
			this.pickItemKey,
			this.commandKey,
			this.screenshotKey,
			this.togglePerspectiveKey,
			this.smoothCameraKey,
			this.streamStartStopKey,
			this.streamPauseUnpauseKey,
			this.streamCommercialKey,
			this.streamToggleMicKey,
			this.fullscreenKey,
			this.spectatorOutlines
		},
		this.hotbarKeys
	);
	protected MinecraftClient client;
	private File optionsFile;
	public Difficulty difficulty = Difficulty.NORMAL;
	public boolean hudHidden;
	public int perspective;
	public boolean debugEnabled;
	public boolean debugProfilerEnabled;
	public boolean debugFpsEnabled;
	public String lastServer = "";
	public boolean smoothCameraEnabled;
	public boolean field_955;
	public float fov = 70.0F;
	public float gamma;
	public float saturation;
	public int guiScale;
	public int particle;
	public String language = "en_US";
	public boolean forcesUnicodeFont = false;

	public GameOptions(MinecraftClient minecraftClient, File file) {
		this.client = minecraftClient;
		this.optionsFile = new File(file, "options.txt");
		if (minecraftClient.is64Bit() && Runtime.getRuntime().maxMemory() >= 1000000000L) {
			GameOptions.Option.RENDER_DISTANCE.setMaxValue(32.0F);
		} else {
			GameOptions.Option.RENDER_DISTANCE.setMaxValue(16.0F);
		}

		this.viewDistance = minecraftClient.is64Bit() ? 12 : 8;
		this.load();
	}

	public GameOptions() {
	}

	public static String getFormattedNameForKeyCode(int code) {
		if (code < 0) {
			return I18n.translate("key.mouseButton", code + 101);
		} else {
			return code < 256 ? Keyboard.getKeyName(code) : String.format("%c", (char)(code - 256)).toUpperCase();
		}
	}

	public static boolean isPressed(KeyBinding binding) {
		if (binding.getCode() == 0) {
			return false;
		} else {
			return binding.getCode() < 0 ? Mouse.isButtonDown(binding.getCode() + 100) : Keyboard.isKeyDown(binding.getCode());
		}
	}

	public void setKeyBindingCode(KeyBinding binding, int code) {
		binding.setCode(code);
		this.save();
	}

	public void setValue(GameOptions.Option option, float var) {
		if (option == GameOptions.Option.SENSITIVITY) {
			this.sensitivity = var;
		}

		if (option == GameOptions.Option.FIELD_OF_VIEW) {
			this.fov = var;
		}

		if (option == GameOptions.Option.BRIGHTNESS) {
			this.gamma = var;
		}

		if (option == GameOptions.Option.MAX_FPS) {
			this.maxFramerate = (int)var;
		}

		if (option == GameOptions.Option.CHAT_OPACITY) {
			this.chatOpacity = var;
			this.client.inGameHud.getChatHud().reset();
		}

		if (option == GameOptions.Option.CHAT_HEIGHT_FOCUSED) {
			this.chatHeightFocused = var;
			this.client.inGameHud.getChatHud().reset();
		}

		if (option == GameOptions.Option.CHAT_HEIGHT_UNFOCUSED) {
			this.chatHeightUnfocused = var;
			this.client.inGameHud.getChatHud().reset();
		}

		if (option == GameOptions.Option.CHAT_WIDTH) {
			this.chatWidth = var;
			this.client.inGameHud.getChatHud().reset();
		}

		if (option == GameOptions.Option.CHAT_SCALE) {
			this.chatScale = var;
			this.client.inGameHud.getChatHud().reset();
		}

		if (option == GameOptions.Option.MIPMAP_LEVELS) {
			int i = this.mipmapLevels;
			this.mipmapLevels = (int)var;
			if ((float)i != var) {
				this.client.getSpriteAtlasTexture().setMaxTextureSize(this.mipmapLevels);
				this.client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
				this.client.getSpriteAtlasTexture().setFilter(false, this.mipmapLevels > 0);
				this.client.reloadResourcesConcurrently();
			}
		}

		if (option == GameOptions.Option.BLOCK_ALTERNATIVES) {
			this.alternativeBlocks = !this.alternativeBlocks;
			this.client.worldRenderer.reload();
		}

		if (option == GameOptions.Option.RENDER_DISTANCE) {
			this.viewDistance = (int)var;
			this.client.worldRenderer.scheduleTerrainUpdate();
		}

		if (option == GameOptions.Option.STREAM_BYTES_PER_PIXEL) {
			this.streamBytesPerPixel = var;
		}

		if (option == GameOptions.Option.STREAM_VOLUME_MIC) {
			this.streamMicVolume = var;
			this.client.getTwitchStreamProvider().setStreamVolume();
		}

		if (option == GameOptions.Option.STREAM_VOLUME_SYSTEM) {
			this.streamSystemVolume = var;
			this.client.getTwitchStreamProvider().setStreamVolume();
		}

		if (option == GameOptions.Option.STREAM_KBPS) {
			this.streamKbps = var;
		}

		if (option == GameOptions.Option.STREAM_FPS) {
			this.streamFps = var;
		}
	}

	public void getBooleanValue(GameOptions.Option option, int integer) {
		if (option == GameOptions.Option.INVERT_MOUSE) {
			this.invertYMouse = !this.invertYMouse;
		}

		if (option == GameOptions.Option.GUI_SCALE) {
			this.guiScale = this.guiScale + integer & 3;
		}

		if (option == GameOptions.Option.PARTICLES) {
			this.particle = (this.particle + integer) % 3;
		}

		if (option == GameOptions.Option.VIEW_BOBBING) {
			this.bobView = !this.bobView;
		}

		if (option == GameOptions.Option.SHOW_CLOUDS) {
			this.cloudMode = (this.cloudMode + integer) % 3;
		}

		if (option == GameOptions.Option.FORCE_UNICODE) {
			this.forcesUnicodeFont = !this.forcesUnicodeFont;
			this.client.textRenderer.setUnicode(this.client.getLanguageManager().forcesUnicodeFont() || this.forcesUnicodeFont);
		}

		if (option == GameOptions.Option.FBO_ENABLE) {
			this.fbo = !this.fbo;
		}

		if (option == GameOptions.Option.ANAGLYPH) {
			this.anaglyph3d = !this.anaglyph3d;
			this.client.reloadResources();
		}

		if (option == GameOptions.Option.GRAPHICS) {
			this.fancyGraphics = !this.fancyGraphics;
			this.client.worldRenderer.reload();
		}

		if (option == GameOptions.Option.AMBIENT_OCCLUSION) {
			this.ao = (this.ao + integer) % 3;
			this.client.worldRenderer.reload();
		}

		if (option == GameOptions.Option.CHAT_VISIBILITY) {
			this.chatVisibilityType = PlayerEntity.ChatVisibilityType.getById((this.chatVisibilityType.getId() + integer) % 3);
		}

		if (option == GameOptions.Option.STREAM_COMPRESSION) {
			this.streamCompression = (this.streamCompression + integer) % 3;
		}

		if (option == GameOptions.Option.STREAM_SEND_METADATA) {
			this.streamSendMetadata = !this.streamSendMetadata;
		}

		if (option == GameOptions.Option.STREAM_CHAT_ENABLED) {
			this.streamChatEnabled = (this.streamChatEnabled + integer) % 3;
		}

		if (option == GameOptions.Option.STREAM_CHAT_USER_FILTER) {
			this.streamChatUserFilter = (this.streamChatUserFilter + integer) % 3;
		}

		if (option == GameOptions.Option.STREAM_MIC_TOGGLE_BEHAVIOR) {
			this.streamMicToggleBehavior = (this.streamMicToggleBehavior + integer) % 2;
		}

		if (option == GameOptions.Option.CHAT_COLOR) {
			this.chatColor = !this.chatColor;
		}

		if (option == GameOptions.Option.CHAT_LINKS) {
			this.chatLink = !this.chatLink;
		}

		if (option == GameOptions.Option.CHAT_LINKS_PROMPT) {
			this.chatLinkPrompt = !this.chatLinkPrompt;
		}

		if (option == GameOptions.Option.SNOOPER_ENABLED) {
			this.snopperEnabled = !this.snopperEnabled;
		}

		if (option == GameOptions.Option.TOUCHSCREEN) {
			this.touchscreen = !this.touchscreen;
		}

		if (option == GameOptions.Option.USE_FULLSCREEN) {
			this.fullscreen = !this.fullscreen;
			if (this.client.isFullscreen() != this.fullscreen) {
				this.client.toggleFullscreen();
			}
		}

		if (option == GameOptions.Option.ENABLE_VSYNC) {
			this.vsync = !this.vsync;
			Display.setVSyncEnabled(this.vsync);
		}

		if (option == GameOptions.Option.USE_VBO) {
			this.vbo = !this.vbo;
			this.client.worldRenderer.reload();
		}

		if (option == GameOptions.Option.BLOCK_ALTERNATIVES) {
			this.alternativeBlocks = !this.alternativeBlocks;
			this.client.worldRenderer.reload();
		}

		if (option == GameOptions.Option.REDUCED_DEBUG_INFO) {
			this.reducedDebugInfo = !this.reducedDebugInfo;
		}

		if (option == GameOptions.Option.ENTITY_SHADOWS) {
			this.entityShadows = !this.entityShadows;
		}

		if (option == GameOptions.Option.REALMS_NOTIFICATIONS) {
			this.realmsNotifications = !this.realmsNotifications;
		}

		this.save();
	}

	public float getIntValue(GameOptions.Option option) {
		if (option == GameOptions.Option.FIELD_OF_VIEW) {
			return this.fov;
		} else if (option == GameOptions.Option.BRIGHTNESS) {
			return this.gamma;
		} else if (option == GameOptions.Option.SATURATION) {
			return this.saturation;
		} else if (option == GameOptions.Option.SENSITIVITY) {
			return this.sensitivity;
		} else if (option == GameOptions.Option.CHAT_OPACITY) {
			return this.chatOpacity;
		} else if (option == GameOptions.Option.CHAT_HEIGHT_FOCUSED) {
			return this.chatHeightFocused;
		} else if (option == GameOptions.Option.CHAT_HEIGHT_UNFOCUSED) {
			return this.chatHeightUnfocused;
		} else if (option == GameOptions.Option.CHAT_SCALE) {
			return this.chatScale;
		} else if (option == GameOptions.Option.CHAT_WIDTH) {
			return this.chatWidth;
		} else if (option == GameOptions.Option.MAX_FPS) {
			return (float)this.maxFramerate;
		} else if (option == GameOptions.Option.MIPMAP_LEVELS) {
			return (float)this.mipmapLevels;
		} else if (option == GameOptions.Option.RENDER_DISTANCE) {
			return (float)this.viewDistance;
		} else if (option == GameOptions.Option.STREAM_BYTES_PER_PIXEL) {
			return this.streamBytesPerPixel;
		} else if (option == GameOptions.Option.STREAM_VOLUME_MIC) {
			return this.streamMicVolume;
		} else if (option == GameOptions.Option.STREAM_VOLUME_SYSTEM) {
			return this.streamSystemVolume;
		} else if (option == GameOptions.Option.STREAM_KBPS) {
			return this.streamKbps;
		} else {
			return option == GameOptions.Option.STREAM_FPS ? this.streamFps : 0.0F;
		}
	}

	public boolean getIntVideoOptions(GameOptions.Option option) {
		switch (option) {
			case INVERT_MOUSE:
				return this.invertYMouse;
			case VIEW_BOBBING:
				return this.bobView;
			case ANAGLYPH:
				return this.anaglyph3d;
			case FBO_ENABLE:
				return this.fbo;
			case CHAT_COLOR:
				return this.chatColor;
			case CHAT_LINKS:
				return this.chatLink;
			case CHAT_LINKS_PROMPT:
				return this.chatLinkPrompt;
			case SNOOPER_ENABLED:
				return this.snopperEnabled;
			case USE_FULLSCREEN:
				return this.fullscreen;
			case ENABLE_VSYNC:
				return this.vsync;
			case USE_VBO:
				return this.vbo;
			case TOUCHSCREEN:
				return this.touchscreen;
			case STREAM_SEND_METADATA:
				return this.streamSendMetadata;
			case FORCE_UNICODE:
				return this.forcesUnicodeFont;
			case BLOCK_ALTERNATIVES:
				return this.alternativeBlocks;
			case REDUCED_DEBUG_INFO:
				return this.reducedDebugInfo;
			case ENTITY_SHADOWS:
				return this.entityShadows;
			case REALMS_NOTIFICATIONS:
				return this.realmsNotifications;
			default:
				return false;
		}
	}

	private static String translateArrayElement(String[] array, int index) {
		if (index < 0 || index >= array.length) {
			index = 0;
		}

		return I18n.translate(array[index]);
	}

	public String getValueMessage(GameOptions.Option option) {
		String string = I18n.translate(option.getName()) + ": ";
		if (option.isNumeric()) {
			float f = this.getIntValue(option);
			float g = option.getRatio(f);
			if (option == GameOptions.Option.SENSITIVITY) {
				if (g == 0.0F) {
					return string + I18n.translate("options.sensitivity.min");
				} else {
					return g == 1.0F ? string + I18n.translate("options.sensitivity.max") : string + (int)(g * 200.0F) + "%";
				}
			} else if (option == GameOptions.Option.FIELD_OF_VIEW) {
				if (f == 70.0F) {
					return string + I18n.translate("options.fov.min");
				} else {
					return f == 110.0F ? string + I18n.translate("options.fov.max") : string + (int)f;
				}
			} else if (option == GameOptions.Option.MAX_FPS) {
				return f == option.max ? string + I18n.translate("options.framerateLimit.max") : string + (int)f + " fps";
			} else if (option == GameOptions.Option.SHOW_CLOUDS) {
				return f == option.min ? string + I18n.translate("options.cloudHeight.min") : string + ((int)f + 128);
			} else if (option == GameOptions.Option.BRIGHTNESS) {
				if (g == 0.0F) {
					return string + I18n.translate("options.gamma.min");
				} else {
					return g == 1.0F ? string + I18n.translate("options.gamma.max") : string + "+" + (int)(g * 100.0F) + "%";
				}
			} else if (option == GameOptions.Option.SATURATION) {
				return string + (int)(g * 400.0F) + "%";
			} else if (option == GameOptions.Option.CHAT_OPACITY) {
				return string + (int)(g * 90.0F + 10.0F) + "%";
			} else if (option == GameOptions.Option.CHAT_HEIGHT_UNFOCUSED) {
				return string + ChatHud.getHeight(g) + "px";
			} else if (option == GameOptions.Option.CHAT_HEIGHT_FOCUSED) {
				return string + ChatHud.getHeight(g) + "px";
			} else if (option == GameOptions.Option.CHAT_WIDTH) {
				return string + ChatHud.getWidth(g) + "px";
			} else if (option == GameOptions.Option.RENDER_DISTANCE) {
				return string + (int)f + " chunks";
			} else if (option == GameOptions.Option.MIPMAP_LEVELS) {
				return f == 0.0F ? string + I18n.translate("options.off") : string + (int)f;
			} else if (option == GameOptions.Option.STREAM_FPS) {
				return string + TwitchAuth.setStreamFps(g) + " fps";
			} else if (option == GameOptions.Option.STREAM_KBPS) {
				return string + TwitchAuth.setStreamBitrate(g) + " Kbps";
			} else if (option == GameOptions.Option.STREAM_BYTES_PER_PIXEL) {
				return string + String.format("%.3f bpp", TwitchAuth.setStreamBytesPerPixel(g));
			} else {
				return g == 0.0F ? string + I18n.translate("options.off") : string + (int)(g * 100.0F) + "%";
			}
		} else if (option.isBooleanToggle()) {
			boolean bl = this.getIntVideoOptions(option);
			return bl ? string + I18n.translate("options.on") : string + I18n.translate("options.off");
		} else if (option == GameOptions.Option.GUI_SCALE) {
			return string + translateArrayElement(GUI_SCALE, this.guiScale);
		} else if (option == GameOptions.Option.CHAT_VISIBILITY) {
			return string + I18n.translate(this.chatVisibilityType.getName());
		} else if (option == GameOptions.Option.PARTICLES) {
			return string + translateArrayElement(PARTICLES, this.particle);
		} else if (option == GameOptions.Option.AMBIENT_OCCLUSION) {
			return string + translateArrayElement(AMBIENT_OCCLUSION, this.ao);
		} else if (option == GameOptions.Option.STREAM_COMPRESSION) {
			return string + translateArrayElement(CHAT_VISIBILITY, this.streamCompression);
		} else if (option == GameOptions.Option.STREAM_CHAT_ENABLED) {
			return string + translateArrayElement(STREAM_CHAT_ENABLED, this.streamChatEnabled);
		} else if (option == GameOptions.Option.STREAM_CHAT_USER_FILTER) {
			return string + translateArrayElement(STREAM_CHAT_USERFILTER, this.streamChatUserFilter);
		} else if (option == GameOptions.Option.STREAM_MIC_TOGGLE_BEHAVIOR) {
			return string + translateArrayElement(STREAM_MIC_TOGGLE, this.streamMicToggleBehavior);
		} else if (option == GameOptions.Option.SHOW_CLOUDS) {
			return string + translateArrayElement(GRAPHICS_LEVEL, this.cloudMode);
		} else if (option == GameOptions.Option.GRAPHICS) {
			if (this.fancyGraphics) {
				return string + I18n.translate("options.graphics.fancy");
			} else {
				String string2 = "options.graphics.fast";
				return string + I18n.translate("options.graphics.fast");
			}
		} else {
			return string;
		}
	}

	public void load() {
		try {
			if (!this.optionsFile.exists()) {
				return;
			}

			BufferedReader bufferedReader = new BufferedReader(new FileReader(this.optionsFile));
			String string = "";
			this.soundVolumeLevels.clear();

			while ((string = bufferedReader.readLine()) != null) {
				try {
					String[] strings = string.split(":");
					if (strings[0].equals("mouseSensitivity")) {
						this.sensitivity = this.parseFloat(strings[1]);
					}

					if (strings[0].equals("fov")) {
						this.fov = this.parseFloat(strings[1]) * 40.0F + 70.0F;
					}

					if (strings[0].equals("gamma")) {
						this.gamma = this.parseFloat(strings[1]);
					}

					if (strings[0].equals("saturation")) {
						this.saturation = this.parseFloat(strings[1]);
					}

					if (strings[0].equals("invertYMouse")) {
						this.invertYMouse = strings[1].equals("true");
					}

					if (strings[0].equals("renderDistance")) {
						this.viewDistance = Integer.parseInt(strings[1]);
					}

					if (strings[0].equals("guiScale")) {
						this.guiScale = Integer.parseInt(strings[1]);
					}

					if (strings[0].equals("particles")) {
						this.particle = Integer.parseInt(strings[1]);
					}

					if (strings[0].equals("bobView")) {
						this.bobView = strings[1].equals("true");
					}

					if (strings[0].equals("anaglyph3d")) {
						this.anaglyph3d = strings[1].equals("true");
					}

					if (strings[0].equals("maxFps")) {
						this.maxFramerate = Integer.parseInt(strings[1]);
					}

					if (strings[0].equals("fboEnable")) {
						this.fbo = strings[1].equals("true");
					}

					if (strings[0].equals("difficulty")) {
						this.difficulty = Difficulty.byOrdinal(Integer.parseInt(strings[1]));
					}

					if (strings[0].equals("fancyGraphics")) {
						this.fancyGraphics = strings[1].equals("true");
					}

					if (strings[0].equals("ao")) {
						if (strings[1].equals("true")) {
							this.ao = 2;
						} else if (strings[1].equals("false")) {
							this.ao = 0;
						} else {
							this.ao = Integer.parseInt(strings[1]);
						}
					}

					if (strings[0].equals("renderClouds")) {
						if (strings[1].equals("true")) {
							this.cloudMode = 2;
						} else if (strings[1].equals("false")) {
							this.cloudMode = 0;
						} else if (strings[1].equals("fast")) {
							this.cloudMode = 1;
						}
					}

					if (strings[0].equals("resourcePacks")) {
						this.resourcePacks = (List<String>)GSON.fromJson(string.substring(string.indexOf(58) + 1), STRING_LIST_TYPE);
						if (this.resourcePacks == null) {
							this.resourcePacks = Lists.newArrayList();
						}
					}

					if (strings[0].equals("incompatibleResourcePacks")) {
						this.incompatibleResourcePacks = (List<String>)GSON.fromJson(string.substring(string.indexOf(58) + 1), STRING_LIST_TYPE);
						if (this.incompatibleResourcePacks == null) {
							this.incompatibleResourcePacks = Lists.newArrayList();
						}
					}

					if (strings[0].equals("lastServer") && strings.length >= 2) {
						this.lastServer = string.substring(string.indexOf(58) + 1);
					}

					if (strings[0].equals("lang") && strings.length >= 2) {
						this.language = strings[1];
					}

					if (strings[0].equals("chatVisibility")) {
						this.chatVisibilityType = PlayerEntity.ChatVisibilityType.getById(Integer.parseInt(strings[1]));
					}

					if (strings[0].equals("chatColors")) {
						this.chatColor = strings[1].equals("true");
					}

					if (strings[0].equals("chatLinks")) {
						this.chatLink = strings[1].equals("true");
					}

					if (strings[0].equals("chatLinksPrompt")) {
						this.chatLinkPrompt = strings[1].equals("true");
					}

					if (strings[0].equals("chatOpacity")) {
						this.chatOpacity = this.parseFloat(strings[1]);
					}

					if (strings[0].equals("snooperEnabled")) {
						this.snopperEnabled = strings[1].equals("true");
					}

					if (strings[0].equals("fullscreen")) {
						this.fullscreen = strings[1].equals("true");
					}

					if (strings[0].equals("enableVsync")) {
						this.vsync = strings[1].equals("true");
					}

					if (strings[0].equals("useVbo")) {
						this.vbo = strings[1].equals("true");
					}

					if (strings[0].equals("hideServerAddress")) {
						this.hideServerAddress = strings[1].equals("true");
					}

					if (strings[0].equals("advancedItemTooltips")) {
						this.advancedItemTooltips = strings[1].equals("true");
					}

					if (strings[0].equals("pauseOnLostFocus")) {
						this.pauseOnLostFocus = strings[1].equals("true");
					}

					if (strings[0].equals("touchscreen")) {
						this.touchscreen = strings[1].equals("true");
					}

					if (strings[0].equals("overrideHeight")) {
						this.overrideHeight = Integer.parseInt(strings[1]);
					}

					if (strings[0].equals("overrideWidth")) {
						this.overrideWidth = Integer.parseInt(strings[1]);
					}

					if (strings[0].equals("heldItemTooltips")) {
						this.heldItemTooltips = strings[1].equals("true");
					}

					if (strings[0].equals("chatHeightFocused")) {
						this.chatHeightFocused = this.parseFloat(strings[1]);
					}

					if (strings[0].equals("chatHeightUnfocused")) {
						this.chatHeightUnfocused = this.parseFloat(strings[1]);
					}

					if (strings[0].equals("chatScale")) {
						this.chatScale = this.parseFloat(strings[1]);
					}

					if (strings[0].equals("chatWidth")) {
						this.chatWidth = this.parseFloat(strings[1]);
					}

					if (strings[0].equals("showInventoryAchievementHint")) {
						this.showInventoryAchievementHint = strings[1].equals("true");
					}

					if (strings[0].equals("mipmapLevels")) {
						this.mipmapLevels = Integer.parseInt(strings[1]);
					}

					if (strings[0].equals("streamBytesPerPixel")) {
						this.streamBytesPerPixel = this.parseFloat(strings[1]);
					}

					if (strings[0].equals("streamMicVolume")) {
						this.streamMicVolume = this.parseFloat(strings[1]);
					}

					if (strings[0].equals("streamSystemVolume")) {
						this.streamSystemVolume = this.parseFloat(strings[1]);
					}

					if (strings[0].equals("streamKbps")) {
						this.streamKbps = this.parseFloat(strings[1]);
					}

					if (strings[0].equals("streamFps")) {
						this.streamFps = this.parseFloat(strings[1]);
					}

					if (strings[0].equals("streamCompression")) {
						this.streamCompression = Integer.parseInt(strings[1]);
					}

					if (strings[0].equals("streamSendMetadata")) {
						this.streamSendMetadata = strings[1].equals("true");
					}

					if (strings[0].equals("streamPreferredServer") && strings.length >= 2) {
						this.currentTexturePackName = string.substring(string.indexOf(58) + 1);
					}

					if (strings[0].equals("streamChatEnabled")) {
						this.streamChatEnabled = Integer.parseInt(strings[1]);
					}

					if (strings[0].equals("streamChatUserFilter")) {
						this.streamChatUserFilter = Integer.parseInt(strings[1]);
					}

					if (strings[0].equals("streamMicToggleBehavior")) {
						this.streamMicToggleBehavior = Integer.parseInt(strings[1]);
					}

					if (strings[0].equals("forceUnicodeFont")) {
						this.forcesUnicodeFont = strings[1].equals("true");
					}

					if (strings[0].equals("allowBlockAlternatives")) {
						this.alternativeBlocks = strings[1].equals("true");
					}

					if (strings[0].equals("reducedDebugInfo")) {
						this.reducedDebugInfo = strings[1].equals("true");
					}

					if (strings[0].equals("useNativeTransport")) {
						this.useNativeTransport = strings[1].equals("true");
					}

					if (strings[0].equals("entityShadows")) {
						this.entityShadows = strings[1].equals("true");
					}

					if (strings[0].equals("realmsNotifications")) {
						this.realmsNotifications = strings[1].equals("true");
					}

					for (KeyBinding keyBinding : this.allKeys) {
						if (strings[0].equals("key_" + keyBinding.getTranslationKey())) {
							keyBinding.setCode(Integer.parseInt(strings[1]));
						}
					}

					for (SoundCategory soundCategory : SoundCategory.values()) {
						if (strings[0].equals("soundCategory_" + soundCategory.getName())) {
							this.soundVolumeLevels.put(soundCategory, this.parseFloat(strings[1]));
						}
					}

					for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
						if (strings[0].equals("modelPart_" + playerModelPart.getName())) {
							this.setPlayerModelPart(playerModelPart, strings[1].equals("true"));
						}
					}
				} catch (Exception var8) {
					LOGGER.warn("Skipping bad option: " + string);
				}
			}

			KeyBinding.updateKeysByCode();
			bufferedReader.close();
		} catch (Exception var9) {
			LOGGER.error("Failed to load options", var9);
		}
	}

	private float parseFloat(String s) {
		if (s.equals("true")) {
			return 1.0F;
		} else {
			return s.equals("false") ? 0.0F : Float.parseFloat(s);
		}
	}

	public void save() {
		try {
			PrintWriter printWriter = new PrintWriter(new FileWriter(this.optionsFile));
			printWriter.println("invertYMouse:" + this.invertYMouse);
			printWriter.println("mouseSensitivity:" + this.sensitivity);
			printWriter.println("fov:" + (this.fov - 70.0F) / 40.0F);
			printWriter.println("gamma:" + this.gamma);
			printWriter.println("saturation:" + this.saturation);
			printWriter.println("renderDistance:" + this.viewDistance);
			printWriter.println("guiScale:" + this.guiScale);
			printWriter.println("particles:" + this.particle);
			printWriter.println("bobView:" + this.bobView);
			printWriter.println("anaglyph3d:" + this.anaglyph3d);
			printWriter.println("maxFps:" + this.maxFramerate);
			printWriter.println("fboEnable:" + this.fbo);
			printWriter.println("difficulty:" + this.difficulty.getId());
			printWriter.println("fancyGraphics:" + this.fancyGraphics);
			printWriter.println("ao:" + this.ao);
			switch (this.cloudMode) {
				case 0:
					printWriter.println("renderClouds:false");
					break;
				case 1:
					printWriter.println("renderClouds:fast");
					break;
				case 2:
					printWriter.println("renderClouds:true");
			}

			printWriter.println("resourcePacks:" + GSON.toJson(this.resourcePacks));
			printWriter.println("incompatibleResourcePacks:" + GSON.toJson(this.incompatibleResourcePacks));
			printWriter.println("lastServer:" + this.lastServer);
			printWriter.println("lang:" + this.language);
			printWriter.println("chatVisibility:" + this.chatVisibilityType.getId());
			printWriter.println("chatColors:" + this.chatColor);
			printWriter.println("chatLinks:" + this.chatLink);
			printWriter.println("chatLinksPrompt:" + this.chatLinkPrompt);
			printWriter.println("chatOpacity:" + this.chatOpacity);
			printWriter.println("snooperEnabled:" + this.snopperEnabled);
			printWriter.println("fullscreen:" + this.fullscreen);
			printWriter.println("enableVsync:" + this.vsync);
			printWriter.println("useVbo:" + this.vbo);
			printWriter.println("hideServerAddress:" + this.hideServerAddress);
			printWriter.println("advancedItemTooltips:" + this.advancedItemTooltips);
			printWriter.println("pauseOnLostFocus:" + this.pauseOnLostFocus);
			printWriter.println("touchscreen:" + this.touchscreen);
			printWriter.println("overrideWidth:" + this.overrideWidth);
			printWriter.println("overrideHeight:" + this.overrideHeight);
			printWriter.println("heldItemTooltips:" + this.heldItemTooltips);
			printWriter.println("chatHeightFocused:" + this.chatHeightFocused);
			printWriter.println("chatHeightUnfocused:" + this.chatHeightUnfocused);
			printWriter.println("chatScale:" + this.chatScale);
			printWriter.println("chatWidth:" + this.chatWidth);
			printWriter.println("showInventoryAchievementHint:" + this.showInventoryAchievementHint);
			printWriter.println("mipmapLevels:" + this.mipmapLevels);
			printWriter.println("streamBytesPerPixel:" + this.streamBytesPerPixel);
			printWriter.println("streamMicVolume:" + this.streamMicVolume);
			printWriter.println("streamSystemVolume:" + this.streamSystemVolume);
			printWriter.println("streamKbps:" + this.streamKbps);
			printWriter.println("streamFps:" + this.streamFps);
			printWriter.println("streamCompression:" + this.streamCompression);
			printWriter.println("streamSendMetadata:" + this.streamSendMetadata);
			printWriter.println("streamPreferredServer:" + this.currentTexturePackName);
			printWriter.println("streamChatEnabled:" + this.streamChatEnabled);
			printWriter.println("streamChatUserFilter:" + this.streamChatUserFilter);
			printWriter.println("streamMicToggleBehavior:" + this.streamMicToggleBehavior);
			printWriter.println("forceUnicodeFont:" + this.forcesUnicodeFont);
			printWriter.println("allowBlockAlternatives:" + this.alternativeBlocks);
			printWriter.println("reducedDebugInfo:" + this.reducedDebugInfo);
			printWriter.println("useNativeTransport:" + this.useNativeTransport);
			printWriter.println("entityShadows:" + this.entityShadows);
			printWriter.println("realmsNotifications:" + this.realmsNotifications);

			for (KeyBinding keyBinding : this.allKeys) {
				printWriter.println("key_" + keyBinding.getTranslationKey() + ":" + keyBinding.getCode());
			}

			for (SoundCategory soundCategory : SoundCategory.values()) {
				printWriter.println("soundCategory_" + soundCategory.getName() + ":" + this.getSoundVolume(soundCategory));
			}

			for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
				printWriter.println("modelPart_" + playerModelPart.getName() + ":" + this.playerModelParts.contains(playerModelPart));
			}

			printWriter.close();
		} catch (Exception var6) {
			LOGGER.error("Failed to save options", var6);
		}

		this.onPlayerModelPartChange();
	}

	public float getSoundVolume(SoundCategory category) {
		return this.soundVolumeLevels.containsKey(category) ? (Float)this.soundVolumeLevels.get(category) : 1.0F;
	}

	public void setSoundVolume(SoundCategory category, float volume) {
		this.client.getSoundManager().updateSoundVolume(category, volume);
		this.soundVolumeLevels.put(category, volume);
	}

	public void onPlayerModelPartChange() {
		if (this.client.player != null) {
			int i = 0;

			for (PlayerModelPart playerModelPart : this.playerModelParts) {
				i |= playerModelPart.getBitFlag();
			}

			this.client.player.networkHandler.sendPacket(new ClientSettingsC2SPacket(this.language, this.viewDistance, this.chatVisibilityType, this.chatColor, i));
		}
	}

	public Set<PlayerModelPart> getEnabledPlayerModelParts() {
		return ImmutableSet.copyOf(this.playerModelParts);
	}

	public void setPlayerModelPart(PlayerModelPart part, boolean enabled) {
		if (enabled) {
			this.playerModelParts.add(part);
		} else {
			this.playerModelParts.remove(part);
		}

		this.onPlayerModelPartChange();
	}

	public void togglePlayerModelPart(PlayerModelPart part) {
		if (!this.getEnabledPlayerModelParts().contains(part)) {
			this.playerModelParts.add(part);
		} else {
			this.playerModelParts.remove(part);
		}

		this.onPlayerModelPartChange();
	}

	public int getCloudMode() {
		return this.viewDistance >= 4 ? this.cloudMode : 0;
	}

	public boolean shouldUseNativeTransport() {
		return this.useNativeTransport;
	}

	public static enum Option {
		INVERT_MOUSE("options.invertMouse", false, true),
		SENSITIVITY("options.sensitivity", true, false),
		FIELD_OF_VIEW("options.fov", true, false, 30.0F, 110.0F, 1.0F),
		BRIGHTNESS("options.gamma", true, false),
		SATURATION("options.saturation", true, false),
		RENDER_DISTANCE("options.renderDistance", true, false, 2.0F, 16.0F, 1.0F),
		VIEW_BOBBING("options.viewBobbing", false, true),
		ANAGLYPH("options.anaglyph", false, true),
		MAX_FPS("options.framerateLimit", true, false, 10.0F, 260.0F, 10.0F),
		FBO_ENABLE("options.fboEnable", false, true),
		SHOW_CLOUDS("options.renderClouds", false, false),
		GRAPHICS("options.graphics", false, false),
		AMBIENT_OCCLUSION("options.ao", false, false),
		GUI_SCALE("options.guiScale", false, false),
		PARTICLES("options.particles", false, false),
		CHAT_VISIBILITY("options.chat.visibility", false, false),
		CHAT_COLOR("options.chat.color", false, true),
		CHAT_LINKS("options.chat.links", false, true),
		CHAT_OPACITY("options.chat.opacity", true, false),
		CHAT_LINKS_PROMPT("options.chat.links.prompt", false, true),
		SNOOPER_ENABLED("options.snooper", false, true),
		USE_FULLSCREEN("options.fullscreen", false, true),
		ENABLE_VSYNC("options.vsync", false, true),
		USE_VBO("options.vbo", false, true),
		TOUCHSCREEN("options.touchscreen", false, true),
		CHAT_SCALE("options.chat.scale", true, false),
		CHAT_WIDTH("options.chat.width", true, false),
		CHAT_HEIGHT_FOCUSED("options.chat.height.focused", true, false),
		CHAT_HEIGHT_UNFOCUSED("options.chat.height.unfocused", true, false),
		MIPMAP_LEVELS("options.mipmapLevels", true, false, 0.0F, 4.0F, 1.0F),
		FORCE_UNICODE("options.forceUnicodeFont", false, true),
		STREAM_BYTES_PER_PIXEL("options.stream.bytesPerPixel", true, false),
		STREAM_VOLUME_MIC("options.stream.micVolumne", true, false),
		STREAM_VOLUME_SYSTEM("options.stream.systemVolume", true, false),
		STREAM_KBPS("options.stream.kbps", true, false),
		STREAM_FPS("options.stream.fps", true, false),
		STREAM_COMPRESSION("options.stream.compression", false, false),
		STREAM_SEND_METADATA("options.stream.sendMetadata", false, true),
		STREAM_CHAT_ENABLED("options.stream.chat.enabled", false, false),
		STREAM_CHAT_USER_FILTER("options.stream.chat.userFilter", false, false),
		STREAM_MIC_TOGGLE_BEHAVIOR("options.stream.micToggleBehavior", false, false),
		BLOCK_ALTERNATIVES("options.blockAlternatives", false, true),
		REDUCED_DEBUG_INFO("options.reducedDebugInfo", false, true),
		ENTITY_SHADOWS("options.entityShadows", false, true),
		REALMS_NOTIFICATIONS("options.realmsNotifications", false, true);

		private final boolean numeric;
		private final boolean booleanToggle;
		private final String name;
		private final float step;
		private float min;
		private float max;

		public static GameOptions.Option byOrdinal(int id) {
			for (GameOptions.Option option : values()) {
				if (option.getOrdinal() == id) {
					return option;
				}
			}

			return null;
		}

		private Option(String string2, boolean bl, boolean bl2) {
			this(string2, bl, bl2, 0.0F, 1.0F, 0.0F);
		}

		private Option(String string2, boolean bl, boolean bl2, float f, float g, float h) {
			this.name = string2;
			this.numeric = bl;
			this.booleanToggle = bl2;
			this.min = f;
			this.max = g;
			this.step = h;
		}

		public boolean isNumeric() {
			return this.numeric;
		}

		public boolean isBooleanToggle() {
			return this.booleanToggle;
		}

		public int getOrdinal() {
			return this.ordinal();
		}

		public String getName() {
			return this.name;
		}

		public float getMaxValue() {
			return this.max;
		}

		public void setMaxValue(float max) {
			this.max = max;
		}

		public float getRatio(float value) {
			return MathHelper.clamp((this.adjust(value) - this.min) / (this.max - this.min), 0.0F, 1.0F);
		}

		public float getValue(float ratio) {
			return this.adjust(this.min + (this.max - this.min) * MathHelper.clamp(ratio, 0.0F, 1.0F));
		}

		public float adjust(float value) {
			value = this.round(value);
			return MathHelper.clamp(value, this.min, this.max);
		}

		protected float round(float value) {
			if (this.step > 0.0F) {
				value = this.step * (float)Math.round(value / this.step);
			}

			return value;
		}
	}
}
