package net.minecraft.client.option;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.class_3253;
import net.minecraft.class_3319;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.options.HandOption;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.play.ClientSettingsC2SPacket;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.storage.LevelDataType;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class GameOptions {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = new Gson();
	private static final Type field_14903 = new ParameterizedType() {
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
	public static final Splitter field_14904 = Splitter.on(':');
	private static final String[] GUI_SCALE = new String[]{"options.guiScale.auto", "options.guiScale.small", "options.guiScale.normal", "options.guiScale.large"};
	private static final String[] PARTICLES = new String[]{"options.particles.all", "options.particles.decreased", "options.particles.minimal"};
	private static final String[] AMBIENT_OCCLUSION = new String[]{"options.ao.off", "options.ao.min", "options.ao.max"};
	private static final String[] GRAPHICS_LEVEL = new String[]{"options.off", "options.clouds.fast", "options.clouds.fancy"};
	private static final String[] field_13293 = new String[]{"options.off", "options.attack.crosshair", "options.attack.hotbar"};
	public static final String[] field_15883 = new String[]{"options.narrator.off", "options.narrator.all", "options.narrator.chat", "options.narrator.system"};
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
	public boolean vbo = true;
	public boolean reducedDebugInfo;
	public boolean hideServerAddress;
	public boolean advancedItemTooltips;
	public boolean pauseOnLostFocus = true;
	private final Set<PlayerModelPart> playerModelParts = Sets.newHashSet(PlayerModelPart.values());
	public boolean touchscreen;
	public HandOption field_13289 = HandOption.RIGHT;
	public int overrideWidth;
	public int overrideHeight;
	public boolean heldItemTooltips = true;
	public float chatScale = 1.0F;
	public float chatWidth = 1.0F;
	public float chatHeightUnfocused = 0.44366196F;
	public float chatHeightFocused = 1.0F;
	public int mipmapLevels = 4;
	private final Map<SoundCategory, Float> soundVolumeLevels = Maps.newEnumMap(SoundCategory.class);
	public boolean useNativeTransport = true;
	public boolean entityShadows = true;
	public int field_13290 = 1;
	public boolean field_13291;
	public boolean field_13292;
	public boolean realmsNotifications = true;
	public boolean field_14902 = true;
	public class_3319 field_15878 = class_3319.MOVEMENT;
	public KeyBinding forwardKey = new KeyBinding("key.forward", 17, "key.categories.movement");
	public KeyBinding leftKey = new KeyBinding("key.left", 30, "key.categories.movement");
	public KeyBinding backKey = new KeyBinding("key.back", 31, "key.categories.movement");
	public KeyBinding rightKey = new KeyBinding("key.right", 32, "key.categories.movement");
	public KeyBinding jumpKey = new KeyBinding("key.jump", 57, "key.categories.movement");
	public KeyBinding sneakKey = new KeyBinding("key.sneak", 42, "key.categories.movement");
	public KeyBinding sprintKey = new KeyBinding("key.sprint", 29, "key.categories.movement");
	public KeyBinding inventoryKey = new KeyBinding("key.inventory", 18, "key.categories.inventory");
	public KeyBinding streamCommercialKey = new KeyBinding("key.swapHands", 33, "key.categories.inventory");
	public KeyBinding dropKey = new KeyBinding("key.drop", 16, "key.categories.inventory");
	public KeyBinding useKey = new KeyBinding("key.use", -99, "key.categories.gameplay");
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
	public KeyBinding field_15880 = new KeyBinding("key.advancements", 38, "key.categories.misc");
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
	public KeyBinding field_15881 = new KeyBinding("key.saveToolbarActivator", 46, "key.categories.creative");
	public KeyBinding field_15882 = new KeyBinding("key.loadToolbarActivator", 45, "key.categories.creative");
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
			this.fullscreenKey,
			this.spectatorOutlines,
			this.streamCommercialKey,
			this.field_15881,
			this.field_15882,
			this.field_15880
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
	public int field_15879;
	public String language = "en_us";
	public boolean forcesUnicodeFont;

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
			switch (code) {
				case -100:
					return I18n.translate("key.mouse.left");
				case -99:
					return I18n.translate("key.mouse.right");
				case -98:
					return I18n.translate("key.mouse.middle");
				default:
					return I18n.translate("key.mouseButton", code + 101);
			}
		} else {
			return code < 256 ? Keyboard.getKeyName(code) : String.format("%c", (char)(code - 256)).toUpperCase();
		}
	}

	public static boolean isPressed(KeyBinding binding) {
		int i = binding.getCode();
		if (i == 0 || i >= 256) {
			return false;
		} else {
			return i < 0 ? Mouse.isButtonDown(i + 100) : Keyboard.isKeyDown(i);
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

		if (option == GameOptions.Option.RENDER_DISTANCE) {
			this.viewDistance = (int)var;
			this.client.worldRenderer.scheduleTerrainUpdate();
		}
	}

	public void getBooleanValue(GameOptions.Option option, int integer) {
		if (option == GameOptions.Option.RENDER_DISTANCE) {
			this.setValue(option, MathHelper.clamp((float)(this.viewDistance + integer), option.getMinValue(), option.getMaxValue()));
		}

		if (option == GameOptions.Option.MAIN_HAND) {
			this.field_13289 = this.field_13289.method_13037();
		}

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

		if (option == GameOptions.Option.REDUCED_DEBUG_INFO) {
			this.reducedDebugInfo = !this.reducedDebugInfo;
		}

		if (option == GameOptions.Option.ENTITY_SHADOWS) {
			this.entityShadows = !this.entityShadows;
		}

		if (option == GameOptions.Option.ATTACK_INDICATOR) {
			this.field_13290 = (this.field_13290 + integer) % 3;
		}

		if (option == GameOptions.Option.SHOW_SUBTITLES) {
			this.field_13292 = !this.field_13292;
		}

		if (option == GameOptions.Option.REALMS_NOTIFICATIONS) {
			this.realmsNotifications = !this.realmsNotifications;
		}

		if (option == GameOptions.Option.AUTO_JUMP) {
			this.field_14902 = !this.field_14902;
		}

		if (option == GameOptions.Option.NARRATOR) {
			if (class_3253.field_15887.method_14473()) {
				this.field_15879 = (this.field_15879 + integer) % field_15883.length;
			} else {
				this.field_15879 = 0;
			}

			class_3253.field_15887.method_14474(this.field_15879);
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
		} else {
			return option == GameOptions.Option.RENDER_DISTANCE ? (float)this.viewDistance : 0.0F;
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
			case FORCE_UNICODE:
				return this.forcesUnicodeFont;
			case REDUCED_DEBUG_INFO:
				return this.reducedDebugInfo;
			case ENTITY_SHADOWS:
				return this.entityShadows;
			case SHOW_SUBTITLES:
				return this.field_13292;
			case REALMS_NOTIFICATIONS:
				return this.realmsNotifications;
			case ENABLE_WEAK_ATTACKS:
				return this.field_13291;
			case AUTO_JUMP:
				return this.field_14902;
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
				return f == option.max ? string + I18n.translate("options.framerateLimit.max") : string + I18n.translate("options.framerate", (int)f);
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
				return string + I18n.translate("options.chunks", (int)f);
			} else if (option == GameOptions.Option.MIPMAP_LEVELS) {
				return f == 0.0F ? string + I18n.translate("options.off") : string + (int)f;
			} else {
				return g == 0.0F ? string + I18n.translate("options.off") : string + (int)(g * 100.0F) + "%";
			}
		} else if (option.isBooleanToggle()) {
			boolean bl = this.getIntVideoOptions(option);
			return bl ? string + I18n.translate("options.on") : string + I18n.translate("options.off");
		} else if (option == GameOptions.Option.MAIN_HAND) {
			return string + this.field_13289;
		} else if (option == GameOptions.Option.GUI_SCALE) {
			return string + translateArrayElement(GUI_SCALE, this.guiScale);
		} else if (option == GameOptions.Option.CHAT_VISIBILITY) {
			return string + I18n.translate(this.chatVisibilityType.getName());
		} else if (option == GameOptions.Option.PARTICLES) {
			return string + translateArrayElement(PARTICLES, this.particle);
		} else if (option == GameOptions.Option.AMBIENT_OCCLUSION) {
			return string + translateArrayElement(AMBIENT_OCCLUSION, this.ao);
		} else if (option == GameOptions.Option.SHOW_CLOUDS) {
			return string + translateArrayElement(GRAPHICS_LEVEL, this.cloudMode);
		} else if (option == GameOptions.Option.GRAPHICS) {
			if (this.fancyGraphics) {
				return string + I18n.translate("options.graphics.fancy");
			} else {
				String string2 = "options.graphics.fast";
				return string + I18n.translate("options.graphics.fast");
			}
		} else if (option == GameOptions.Option.ATTACK_INDICATOR) {
			return string + translateArrayElement(field_13293, this.field_13290);
		} else if (option == GameOptions.Option.NARRATOR) {
			return class_3253.field_15887.method_14473()
				? string + translateArrayElement(field_15883, this.field_15879)
				: string + I18n.translate("options.narrator.notavailable");
		} else {
			return string;
		}
	}

	public void load() {
		try {
			if (!this.optionsFile.exists()) {
				return;
			}

			this.soundVolumeLevels.clear();
			List<String> list = IOUtils.readLines(new FileInputStream(this.optionsFile));
			NbtCompound nbtCompound = new NbtCompound();

			for (String string : list) {
				try {
					Iterator<String> iterator = field_14904.omitEmptyStrings().limit(2).split(string).iterator();
					nbtCompound.putString((String)iterator.next(), (String)iterator.next());
				} catch (Exception var10) {
					LOGGER.warn("Skipping bad option: {}", string);
				}
			}

			nbtCompound = this.method_13409(nbtCompound);

			for (String string2 : nbtCompound.getKeys()) {
				String string3 = nbtCompound.getString(string2);

				try {
					if ("mouseSensitivity".equals(string2)) {
						this.sensitivity = this.parseFloat(string3);
					}

					if ("fov".equals(string2)) {
						this.fov = this.parseFloat(string3) * 40.0F + 70.0F;
					}

					if ("gamma".equals(string2)) {
						this.gamma = this.parseFloat(string3);
					}

					if ("saturation".equals(string2)) {
						this.saturation = this.parseFloat(string3);
					}

					if ("invertYMouse".equals(string2)) {
						this.invertYMouse = "true".equals(string3);
					}

					if ("renderDistance".equals(string2)) {
						this.viewDistance = Integer.parseInt(string3);
					}

					if ("guiScale".equals(string2)) {
						this.guiScale = Integer.parseInt(string3);
					}

					if ("particles".equals(string2)) {
						this.particle = Integer.parseInt(string3);
					}

					if ("bobView".equals(string2)) {
						this.bobView = "true".equals(string3);
					}

					if ("anaglyph3d".equals(string2)) {
						this.anaglyph3d = "true".equals(string3);
					}

					if ("maxFps".equals(string2)) {
						this.maxFramerate = Integer.parseInt(string3);
					}

					if ("fboEnable".equals(string2)) {
						this.fbo = "true".equals(string3);
					}

					if ("difficulty".equals(string2)) {
						this.difficulty = Difficulty.byOrdinal(Integer.parseInt(string3));
					}

					if ("fancyGraphics".equals(string2)) {
						this.fancyGraphics = "true".equals(string3);
					}

					if ("tutorialStep".equals(string2)) {
						this.field_15878 = class_3319.method_14741(string3);
					}

					if ("ao".equals(string2)) {
						if ("true".equals(string3)) {
							this.ao = 2;
						} else if ("false".equals(string3)) {
							this.ao = 0;
						} else {
							this.ao = Integer.parseInt(string3);
						}
					}

					if ("renderClouds".equals(string2)) {
						if ("true".equals(string3)) {
							this.cloudMode = 2;
						} else if ("false".equals(string3)) {
							this.cloudMode = 0;
						} else if ("fast".equals(string3)) {
							this.cloudMode = 1;
						}
					}

					if ("attackIndicator".equals(string2)) {
						if ("0".equals(string3)) {
							this.field_13290 = 0;
						} else if ("1".equals(string3)) {
							this.field_13290 = 1;
						} else if ("2".equals(string3)) {
							this.field_13290 = 2;
						}
					}

					if ("resourcePacks".equals(string2)) {
						this.resourcePacks = JsonHelper.deserialize(GSON, string3, field_14903);
						if (this.resourcePacks == null) {
							this.resourcePacks = Lists.newArrayList();
						}
					}

					if ("incompatibleResourcePacks".equals(string2)) {
						this.incompatibleResourcePacks = JsonHelper.deserialize(GSON, string3, field_14903);
						if (this.incompatibleResourcePacks == null) {
							this.incompatibleResourcePacks = Lists.newArrayList();
						}
					}

					if ("lastServer".equals(string2)) {
						this.lastServer = string3;
					}

					if ("lang".equals(string2)) {
						this.language = string3;
					}

					if ("chatVisibility".equals(string2)) {
						this.chatVisibilityType = PlayerEntity.ChatVisibilityType.getById(Integer.parseInt(string3));
					}

					if ("chatColors".equals(string2)) {
						this.chatColor = "true".equals(string3);
					}

					if ("chatLinks".equals(string2)) {
						this.chatLink = "true".equals(string3);
					}

					if ("chatLinksPrompt".equals(string2)) {
						this.chatLinkPrompt = "true".equals(string3);
					}

					if ("chatOpacity".equals(string2)) {
						this.chatOpacity = this.parseFloat(string3);
					}

					if ("snooperEnabled".equals(string2)) {
						this.snopperEnabled = "true".equals(string3);
					}

					if ("fullscreen".equals(string2)) {
						this.fullscreen = "true".equals(string3);
					}

					if ("enableVsync".equals(string2)) {
						this.vsync = "true".equals(string3);
					}

					if ("useVbo".equals(string2)) {
						this.vbo = "true".equals(string3);
					}

					if ("hideServerAddress".equals(string2)) {
						this.hideServerAddress = "true".equals(string3);
					}

					if ("advancedItemTooltips".equals(string2)) {
						this.advancedItemTooltips = "true".equals(string3);
					}

					if ("pauseOnLostFocus".equals(string2)) {
						this.pauseOnLostFocus = "true".equals(string3);
					}

					if ("touchscreen".equals(string2)) {
						this.touchscreen = "true".equals(string3);
					}

					if ("overrideHeight".equals(string2)) {
						this.overrideHeight = Integer.parseInt(string3);
					}

					if ("overrideWidth".equals(string2)) {
						this.overrideWidth = Integer.parseInt(string3);
					}

					if ("heldItemTooltips".equals(string2)) {
						this.heldItemTooltips = "true".equals(string3);
					}

					if ("chatHeightFocused".equals(string2)) {
						this.chatHeightFocused = this.parseFloat(string3);
					}

					if ("chatHeightUnfocused".equals(string2)) {
						this.chatHeightUnfocused = this.parseFloat(string3);
					}

					if ("chatScale".equals(string2)) {
						this.chatScale = this.parseFloat(string3);
					}

					if ("chatWidth".equals(string2)) {
						this.chatWidth = this.parseFloat(string3);
					}

					if ("mipmapLevels".equals(string2)) {
						this.mipmapLevels = Integer.parseInt(string3);
					}

					if ("forceUnicodeFont".equals(string2)) {
						this.forcesUnicodeFont = "true".equals(string3);
					}

					if ("reducedDebugInfo".equals(string2)) {
						this.reducedDebugInfo = "true".equals(string3);
					}

					if ("useNativeTransport".equals(string2)) {
						this.useNativeTransport = "true".equals(string3);
					}

					if ("entityShadows".equals(string2)) {
						this.entityShadows = "true".equals(string3);
					}

					if ("mainHand".equals(string2)) {
						this.field_13289 = "left".equals(string3) ? HandOption.LEFT : HandOption.RIGHT;
					}

					if ("showSubtitles".equals(string2)) {
						this.field_13292 = "true".equals(string3);
					}

					if ("realmsNotifications".equals(string2)) {
						this.realmsNotifications = "true".equals(string3);
					}

					if ("enableWeakAttacks".equals(string2)) {
						this.field_13291 = "true".equals(string3);
					}

					if ("autoJump".equals(string2)) {
						this.field_14902 = "true".equals(string3);
					}

					if ("narrator".equals(string2)) {
						this.field_15879 = Integer.parseInt(string3);
					}

					for (KeyBinding keyBinding : this.allKeys) {
						if (string2.equals("key_" + keyBinding.getTranslationKey())) {
							keyBinding.setCode(Integer.parseInt(string3));
						}
					}

					for (SoundCategory soundCategory : SoundCategory.values()) {
						if (string2.equals("soundCategory_" + soundCategory.getName())) {
							this.soundVolumeLevels.put(soundCategory, this.parseFloat(string3));
						}
					}

					for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
						if (string2.equals("modelPart_" + playerModelPart.getName())) {
							this.setPlayerModelPart(playerModelPart, "true".equals(string3));
						}
					}
				} catch (Exception var11) {
					LOGGER.warn("Skipping bad option: {}:{}", string2, string3);
				}
			}

			KeyBinding.updateKeysByCode();
		} catch (Exception var12) {
			LOGGER.error("Failed to load options", var12);
		}
	}

	private NbtCompound method_13409(NbtCompound nbtCompound) {
		int i = 0;

		try {
			i = Integer.parseInt(nbtCompound.getString("version"));
		} catch (RuntimeException var4) {
		}

		return this.client.method_12142().update(LevelDataType.OPTIONS, nbtCompound, i);
	}

	private float parseFloat(String s) {
		if ("true".equals(s)) {
			return 1.0F;
		} else {
			return "false".equals(s) ? 0.0F : Float.parseFloat(s);
		}
	}

	public void save() {
		PrintWriter printWriter = null;

		try {
			printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.optionsFile), StandardCharsets.UTF_8));
			printWriter.println("version:1343");
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
			printWriter.println("mipmapLevels:" + this.mipmapLevels);
			printWriter.println("forceUnicodeFont:" + this.forcesUnicodeFont);
			printWriter.println("reducedDebugInfo:" + this.reducedDebugInfo);
			printWriter.println("useNativeTransport:" + this.useNativeTransport);
			printWriter.println("entityShadows:" + this.entityShadows);
			printWriter.println("mainHand:" + (this.field_13289 == HandOption.LEFT ? "left" : "right"));
			printWriter.println("attackIndicator:" + this.field_13290);
			printWriter.println("showSubtitles:" + this.field_13292);
			printWriter.println("realmsNotifications:" + this.realmsNotifications);
			printWriter.println("enableWeakAttacks:" + this.field_13291);
			printWriter.println("autoJump:" + this.field_14902);
			printWriter.println("narrator:" + this.field_15879);
			printWriter.println("tutorialStep:" + this.field_15878.getName());

			for (KeyBinding keyBinding : this.allKeys) {
				printWriter.println("key_" + keyBinding.getTranslationKey() + ":" + keyBinding.getCode());
			}

			for (SoundCategory soundCategory : SoundCategory.values()) {
				printWriter.println("soundCategory_" + soundCategory.getName() + ":" + this.getSoundVolume(soundCategory));
			}

			for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
				printWriter.println("modelPart_" + playerModelPart.getName() + ":" + this.playerModelParts.contains(playerModelPart));
			}
		} catch (Exception var9) {
			LOGGER.error("Failed to save options", var9);
		} finally {
			IOUtils.closeQuietly(printWriter);
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

			this.client
				.player
				.networkHandler
				.sendPacket(new ClientSettingsC2SPacket(this.language, this.viewDistance, this.chatVisibilityType, this.chatColor, i, this.field_13289));
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
		if (this.getEnabledPlayerModelParts().contains(part)) {
			this.playerModelParts.remove(part);
		} else {
			this.playerModelParts.add(part);
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
		REDUCED_DEBUG_INFO("options.reducedDebugInfo", false, true),
		ENTITY_SHADOWS("options.entityShadows", false, true),
		MAIN_HAND("options.mainHand", false, false),
		ATTACK_INDICATOR("options.attackIndicator", false, false),
		ENABLE_WEAK_ATTACKS("options.enableWeakAttacks", false, true),
		SHOW_SUBTITLES("options.showSubtitles", false, true),
		REALMS_NOTIFICATIONS("options.realmsNotifications", false, true),
		AUTO_JUMP("options.autoJump", false, true),
		NARRATOR("options.narrator", false, false);

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

		public float getMinValue() {
			return this.min;
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

		private float round(float value) {
			if (this.step > 0.0F) {
				value = this.step * (float)Math.round(value / this.step);
			}

			return value;
		}
	}
}
