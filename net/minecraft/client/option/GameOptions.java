package net.minecraft.client.option;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.mojang.datafixers.DataFixTypes;
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
import javax.annotation.Nullable;
import net.minecraft.class_3253;
import net.minecraft.class_3319;
import net.minecraft.class_4107;
import net.minecraft.class_4115;
import net.minecraft.class_4286;
import net.minecraft.class_4462;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.options.HandOption;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.c2s.play.ClientSettingsC2SPacket;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
	private static final String[] field_19986 = new String[]{"options.particles.all", "options.particles.decreased", "options.particles.minimal"};
	private static final String[] AMBIENT_OCCLUSION = new String[]{"options.ao.off", "options.ao.min", "options.ao.max"};
	private static final String[] GRAPHICS_LEVEL = new String[]{"options.off", "options.clouds.fast", "options.clouds.fancy"};
	private static final String[] field_13293 = new String[]{"options.off", "options.attack.crosshair", "options.attack.hotbar"};
	public static final String[] field_15883 = new String[]{"options.narrator.off", "options.narrator.all", "options.narrator.chat", "options.narrator.system"};
	public double field_19988 = 0.5;
	public boolean invertYMouse;
	public int viewDistance = -1;
	public boolean bobView = true;
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
	public double field_19989 = 1.0;
	public boolean snopperEnabled = true;
	public boolean fullscreen;
	@Nullable
	public String field_19990;
	public boolean field_19991 = true;
	public boolean vbo = true;
	public boolean reducedDebugInfo;
	public boolean hideServerAddress;
	public boolean field_19992;
	public boolean field_19973 = true;
	private final Set<PlayerModelPart> playerModelParts = Sets.newHashSet(PlayerModelPart.values());
	public boolean touchscreen;
	public HandOption field_13289 = HandOption.RIGHT;
	public int overrideWidth;
	public int overrideHeight;
	public boolean heldItemTooltips = true;
	public double field_19974 = 1.0;
	public double field_19975 = 1.0;
	public double field_19976 = 0.44366196F;
	public double field_19977 = 1.0;
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
	public boolean field_19978 = true;
	public int field_19979 = 2;
	public double field_19980 = 1.0;
	public int field_19981 = 1;
	public KeyBinding forwardKey = new KeyBinding("key.forward", 87, "key.categories.movement");
	public KeyBinding leftKey = new KeyBinding("key.left", 65, "key.categories.movement");
	public KeyBinding backKey = new KeyBinding("key.back", 83, "key.categories.movement");
	public KeyBinding rightKey = new KeyBinding("key.right", 68, "key.categories.movement");
	public KeyBinding jumpKey = new KeyBinding("key.jump", 32, "key.categories.movement");
	public KeyBinding sneakKey = new KeyBinding("key.sneak", 340, "key.categories.movement");
	public KeyBinding sprintKey = new KeyBinding("key.sprint", 341, "key.categories.movement");
	public KeyBinding inventoryKey = new KeyBinding("key.inventory", 69, "key.categories.inventory");
	public KeyBinding streamCommercialKey = new KeyBinding("key.swapHands", 70, "key.categories.inventory");
	public KeyBinding dropKey = new KeyBinding("key.drop", 81, "key.categories.inventory");
	public KeyBinding useKey = new KeyBinding("key.use", class_4107.class_4109.MOUSE, 1, "key.categories.gameplay");
	public KeyBinding attackKey = new KeyBinding("key.attack", class_4107.class_4109.MOUSE, 0, "key.categories.gameplay");
	public KeyBinding pickItemKey = new KeyBinding("key.pickItem", class_4107.class_4109.MOUSE, 2, "key.categories.gameplay");
	public KeyBinding chatKey = new KeyBinding("key.chat", 84, "key.categories.multiplayer");
	public KeyBinding playerListKey = new KeyBinding("key.playerlist", 258, "key.categories.multiplayer");
	public KeyBinding commandKey = new KeyBinding("key.command", 47, "key.categories.multiplayer");
	public KeyBinding screenshotKey = new KeyBinding("key.screenshot", 291, "key.categories.misc");
	public KeyBinding togglePerspectiveKey = new KeyBinding("key.togglePerspective", 294, "key.categories.misc");
	public KeyBinding smoothCameraKey = new KeyBinding("key.smoothCamera", -1, "key.categories.misc");
	public KeyBinding fullscreenKey = new KeyBinding("key.fullscreen", 300, "key.categories.misc");
	public KeyBinding spectatorOutlines = new KeyBinding("key.spectatorOutlines", -1, "key.categories.misc");
	public KeyBinding field_15880 = new KeyBinding("key.advancements", 76, "key.categories.misc");
	public KeyBinding[] hotbarKeys = new KeyBinding[]{
		new KeyBinding("key.hotbar.1", 49, "key.categories.inventory"),
		new KeyBinding("key.hotbar.2", 50, "key.categories.inventory"),
		new KeyBinding("key.hotbar.3", 51, "key.categories.inventory"),
		new KeyBinding("key.hotbar.4", 52, "key.categories.inventory"),
		new KeyBinding("key.hotbar.5", 53, "key.categories.inventory"),
		new KeyBinding("key.hotbar.6", 54, "key.categories.inventory"),
		new KeyBinding("key.hotbar.7", 55, "key.categories.inventory"),
		new KeyBinding("key.hotbar.8", 56, "key.categories.inventory"),
		new KeyBinding("key.hotbar.9", 57, "key.categories.inventory")
	};
	public KeyBinding field_15881 = new KeyBinding("key.saveToolbarActivator", 67, "key.categories.creative");
	public KeyBinding field_15882 = new KeyBinding("key.loadToolbarActivator", 88, "key.categories.creative");
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
	public boolean field_19987;
	public int perspective;
	public boolean debugEnabled;
	public boolean field_19982;
	public boolean field_19983;
	public String lastServer = "";
	public boolean smoothCameraEnabled;
	public boolean field_955;
	public double field_19984 = 70.0;
	public double field_19985;
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

	public void method_18255(KeyBinding keyBinding, class_4107.class_4108 arg) {
		keyBinding.method_18170(arg);
		this.save();
	}

	public void method_18257(GameOptions.Option option, double d) {
		if (option == GameOptions.Option.SENSITIVITY) {
			this.field_19988 = d;
		}

		if (option == GameOptions.Option.FIELD_OF_VIEW) {
			this.field_19984 = d;
		}

		if (option == GameOptions.Option.BRIGHTNESS) {
			this.field_19985 = d;
		}

		if (option == GameOptions.Option.MAX_FPS) {
			this.maxFramerate = (int)d;
		}

		if (option == GameOptions.Option.CHAT_OPACITY) {
			this.field_19989 = d;
			this.client.inGameHud.getChatHud().reset();
		}

		if (option == GameOptions.Option.CHAT_HEIGHT_FOCUSED) {
			this.field_19977 = d;
			this.client.inGameHud.getChatHud().reset();
		}

		if (option == GameOptions.Option.CHAT_HEIGHT_UNFOCUSED) {
			this.field_19976 = d;
			this.client.inGameHud.getChatHud().reset();
		}

		if (option == GameOptions.Option.CHAT_WIDTH) {
			this.field_19975 = d;
			this.client.inGameHud.getChatHud().reset();
		}

		if (option == GameOptions.Option.CHAT_SCALE) {
			this.field_19974 = d;
			this.client.inGameHud.getChatHud().reset();
		}

		if (option == GameOptions.Option.MIPMAP_LEVELS) {
			int i = this.mipmapLevels;
			this.mipmapLevels = (int)d;
			if ((double)i != d) {
				this.client.getSpriteAtlasTexture().setMaxTextureSize(this.mipmapLevels);
				this.client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
				this.client.getSpriteAtlasTexture().setFilter(false, this.mipmapLevels > 0);
				this.client.reloadResourcesConcurrently();
			}
		}

		if (option == GameOptions.Option.RENDER_DISTANCE) {
			this.viewDistance = (int)d;
			this.client.worldRenderer.scheduleTerrainUpdate();
		}

		if (option == GameOptions.Option.BIOME_BLEND_RADIUS) {
			this.field_19979 = MathHelper.clamp((int)d, 0, 7);
			this.client.worldRenderer.reload();
		}

		if (option == GameOptions.Option.FULLSCREEN_RESOLUTION) {
			this.client.field_19944.method_18303((int)d);
		}

		if (option == GameOptions.Option.MOUSE_WHEEL_SENSITIVITY) {
			this.field_19980 = d;
		}
	}

	public void method_18258(GameOptions.Option option, int i) {
		if (option == GameOptions.Option.RENDER_DISTANCE) {
			this.method_18257(option, MathHelper.clamp((double)(this.viewDistance + i), option.method_12152(), option.method_18267()));
		}

		if (option == GameOptions.Option.MAIN_HAND) {
			this.field_13289 = this.field_13289.method_13037();
		}

		if (option == GameOptions.Option.INVERT_MOUSE) {
			this.invertYMouse = !this.invertYMouse;
		}

		if (option == GameOptions.Option.GUI_SCALE) {
			this.guiScale = Integer.remainderUnsigned(this.guiScale + i, this.client.field_19944.method_18307(0) + 1);
		}

		if (option == GameOptions.Option.PARTICLES) {
			this.particle = (this.particle + i) % 3;
		}

		if (option == GameOptions.Option.VIEW_BOBBING) {
			this.bobView = !this.bobView;
		}

		if (option == GameOptions.Option.SHOW_CLOUDS) {
			this.cloudMode = (this.cloudMode + i) % 3;
		}

		if (option == GameOptions.Option.FORCE_UNICODE) {
			this.forcesUnicodeFont = !this.forcesUnicodeFont;
			this.client.method_9391().method_18454(this.forcesUnicodeFont);
		}

		if (option == GameOptions.Option.FBO_ENABLE) {
			this.fbo = !this.fbo;
		}

		if (option == GameOptions.Option.GRAPHICS) {
			this.fancyGraphics = !this.fancyGraphics;
			this.client.worldRenderer.reload();
		}

		if (option == GameOptions.Option.AMBIENT_OCCLUSION) {
			this.ao = (this.ao + i) % 3;
			this.client.worldRenderer.reload();
		}

		if (option == GameOptions.Option.CHAT_VISIBILITY) {
			this.chatVisibilityType = PlayerEntity.ChatVisibilityType.getById((this.chatVisibilityType.getId() + i) % 3);
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
			if (this.client.field_19944.method_18316() != this.fullscreen) {
				this.client.field_19944.method_18313();
			}
		}

		if (option == GameOptions.Option.ENABLE_VSYNC) {
			this.field_19991 = !this.field_19991;
			this.client.field_19944.method_18306();
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
			this.field_13290 = (this.field_13290 + i) % 3;
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

		if (option == GameOptions.Option.AUTO_SUGGESTIONS) {
			this.field_19978 = !this.field_19978;
		}

		if (option == GameOptions.Option.NARRATOR) {
			if (class_3253.field_15887.method_14473()) {
				this.field_15879 = (this.field_15879 + i) % field_15883.length;
			} else {
				this.field_15879 = 0;
			}

			class_3253.field_15887.method_14474(this.field_15879);
		}

		this.save();
	}

	public double method_18256(GameOptions.Option option) {
		if (option == GameOptions.Option.BIOME_BLEND_RADIUS) {
			return (double)this.field_19979;
		} else if (option == GameOptions.Option.FIELD_OF_VIEW) {
			return this.field_19984;
		} else if (option == GameOptions.Option.BRIGHTNESS) {
			return this.field_19985;
		} else if (option == GameOptions.Option.SATURATION) {
			return (double)this.saturation;
		} else if (option == GameOptions.Option.SENSITIVITY) {
			return this.field_19988;
		} else if (option == GameOptions.Option.CHAT_OPACITY) {
			return this.field_19989;
		} else if (option == GameOptions.Option.CHAT_HEIGHT_FOCUSED) {
			return this.field_19977;
		} else if (option == GameOptions.Option.CHAT_HEIGHT_UNFOCUSED) {
			return this.field_19976;
		} else if (option == GameOptions.Option.CHAT_SCALE) {
			return this.field_19974;
		} else if (option == GameOptions.Option.CHAT_WIDTH) {
			return this.field_19975;
		} else if (option == GameOptions.Option.MAX_FPS) {
			return (double)this.maxFramerate;
		} else if (option == GameOptions.Option.MIPMAP_LEVELS) {
			return (double)this.mipmapLevels;
		} else if (option == GameOptions.Option.RENDER_DISTANCE) {
			return (double)this.viewDistance;
		} else if (option == GameOptions.Option.FULLSCREEN_RESOLUTION) {
			return (double)this.client.field_19944.method_18311();
		} else {
			return option == GameOptions.Option.MOUSE_WHEEL_SENSITIVITY ? this.field_19980 : 0.0;
		}
	}

	public boolean getIntVideoOptions(GameOptions.Option option) {
		switch (option) {
			case INVERT_MOUSE:
				return this.invertYMouse;
			case VIEW_BOBBING:
				return this.bobView;
			case FBO_ENABLE:
				return this.fbo;
			case CHAT_COLOR:
				return this.chatColor;
			case CHAT_LINKS:
				return this.chatLink;
			case CHAT_LINKS_PROMPT:
				return this.chatLinkPrompt;
			case SNOOPER_ENABLED:
				if (this.snopperEnabled) {
				}

				return false;
			case USE_FULLSCREEN:
				return this.fullscreen;
			case ENABLE_VSYNC:
				return this.field_19991;
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
			case AUTO_SUGGESTIONS:
				return this.field_19978;
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

	public String method_18260(GameOptions.Option option) {
		String string = I18n.translate(option.getName()) + ": ";
		if (option.isNumeric()) {
			double d = this.method_18256(option);
			double e = option.method_18261(d);
			if (option == GameOptions.Option.SENSITIVITY) {
				if (e == 0.0) {
					return string + I18n.translate("options.sensitivity.min");
				} else {
					return e == 1.0 ? string + I18n.translate("options.sensitivity.max") : string + (int)(e * 200.0) + "%";
				}
			} else if (option == GameOptions.Option.BIOME_BLEND_RADIUS) {
				if (e == 0.0) {
					return string + I18n.translate("options.off");
				} else {
					int i = this.field_19979 * 2 + 1;
					return string + i + "x" + i;
				}
			} else if (option == GameOptions.Option.FIELD_OF_VIEW) {
				if (d == 70.0) {
					return string + I18n.translate("options.fov.min");
				} else {
					return d == 110.0 ? string + I18n.translate("options.fov.max") : string + (int)d;
				}
			} else if (option == GameOptions.Option.MAX_FPS) {
				return d == option.field_19998 ? string + I18n.translate("options.framerateLimit.max") : string + I18n.translate("options.framerate", (int)d);
			} else if (option == GameOptions.Option.SHOW_CLOUDS) {
				return d == option.field_19997 ? string + I18n.translate("options.cloudHeight.min") : string + ((int)d + 128);
			} else if (option == GameOptions.Option.BRIGHTNESS) {
				if (e == 0.0) {
					return string + I18n.translate("options.gamma.min");
				} else {
					return e == 1.0 ? string + I18n.translate("options.gamma.max") : string + "+" + (int)(e * 100.0) + "%";
				}
			} else if (option == GameOptions.Option.SATURATION) {
				return string + (int)(e * 400.0) + "%";
			} else if (option == GameOptions.Option.CHAT_OPACITY) {
				return string + (int)(e * 90.0 + 10.0) + "%";
			} else if (option == GameOptions.Option.CHAT_HEIGHT_UNFOCUSED) {
				return string + ChatHud.method_18381(e) + "px";
			} else if (option == GameOptions.Option.CHAT_HEIGHT_FOCUSED) {
				return string + ChatHud.method_18381(e) + "px";
			} else if (option == GameOptions.Option.CHAT_WIDTH) {
				return string + ChatHud.method_18380(e) + "px";
			} else if (option == GameOptions.Option.RENDER_DISTANCE) {
				return string + I18n.translate("options.chunks", (int)d);
			} else if (option == GameOptions.Option.MOUSE_WHEEL_SENSITIVITY) {
				return e == 1.0 ? string + I18n.translate("options.mouseWheelSensitivity.default") : string + "+" + (int)e + "." + (int)(e * 10.0) % 10;
			} else if (option == GameOptions.Option.MIPMAP_LEVELS) {
				return d == 0.0 ? string + I18n.translate("options.off") : string + (int)d;
			} else if (option == GameOptions.Option.FULLSCREEN_RESOLUTION) {
				return d == 0.0 ? string + I18n.translate("options.fullscreen.current") : string + this.client.field_19944.method_18294((int)d - 1);
			} else {
				return e == 0.0 ? string + I18n.translate("options.off") : string + (int)(e * 100.0) + "%";
			}
		} else if (option.isBooleanToggle()) {
			boolean bl = this.getIntVideoOptions(option);
			return bl ? string + I18n.translate("options.on") : string + I18n.translate("options.off");
		} else if (option == GameOptions.Option.MAIN_HAND) {
			return string + this.field_13289;
		} else if (option == GameOptions.Option.GUI_SCALE) {
			return string + (this.guiScale == 0 ? I18n.translate("options.guiScale.auto") : this.guiScale);
		} else if (option == GameOptions.Option.CHAT_VISIBILITY) {
			return string + I18n.translate(this.chatVisibilityType.getName());
		} else if (option == GameOptions.Option.PARTICLES) {
			return string + translateArrayElement(field_19986, this.particle);
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
						this.field_19988 = (double)this.parseFloat(string3);
					}

					if ("fov".equals(string2)) {
						this.field_19984 = (double)(this.parseFloat(string3) * 40.0F + 70.0F);
					}

					if ("gamma".equals(string2)) {
						this.field_19985 = (double)this.parseFloat(string3);
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
						this.field_19989 = (double)this.parseFloat(string3);
					}

					if ("snooperEnabled".equals(string2)) {
						this.snopperEnabled = "true".equals(string3);
					}

					if ("fullscreen".equals(string2)) {
						this.fullscreen = "true".equals(string3);
					}

					if ("fullscreenResolution".equals(string2)) {
						this.field_19990 = string3;
					}

					if ("enableVsync".equals(string2)) {
						this.field_19991 = "true".equals(string3);
					}

					if ("useVbo".equals(string2)) {
						this.vbo = "true".equals(string3);
					}

					if ("hideServerAddress".equals(string2)) {
						this.hideServerAddress = "true".equals(string3);
					}

					if ("advancedItemTooltips".equals(string2)) {
						this.field_19992 = "true".equals(string3);
					}

					if ("pauseOnLostFocus".equals(string2)) {
						this.field_19973 = "true".equals(string3);
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
						this.field_19977 = (double)this.parseFloat(string3);
					}

					if ("chatHeightUnfocused".equals(string2)) {
						this.field_19976 = (double)this.parseFloat(string3);
					}

					if ("chatScale".equals(string2)) {
						this.field_19974 = (double)this.parseFloat(string3);
					}

					if ("chatWidth".equals(string2)) {
						this.field_19975 = (double)this.parseFloat(string3);
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

					if ("autoSuggestions".equals(string2)) {
						this.field_19978 = "true".equals(string3);
					}

					if ("biomeBlendRadius".equals(string2)) {
						this.field_19979 = Integer.parseInt(string3);
					}

					if ("mouseWheelSensitivity".equals(string2)) {
						this.field_19980 = (double)this.parseFloat(string3);
					}

					if ("glDebugVerbosity".equals(string2)) {
						this.field_19981 = Integer.parseInt(string3);
					}

					for (KeyBinding keyBinding : this.allKeys) {
						if (string2.equals("key_" + keyBinding.getTranslationKey())) {
							keyBinding.method_18170(class_4107.method_18156(string3));
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

		return NbtHelper.method_20141(this.client.method_12142(), DataFixTypes.OPTIONS, nbtCompound, i);
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
			printWriter.println("version:1631");
			printWriter.println("invertYMouse:" + this.invertYMouse);
			printWriter.println("mouseSensitivity:" + this.field_19988);
			printWriter.println("fov:" + (this.field_19984 - 70.0) / 40.0);
			printWriter.println("gamma:" + this.field_19985);
			printWriter.println("saturation:" + this.saturation);
			printWriter.println("renderDistance:" + this.viewDistance);
			printWriter.println("guiScale:" + this.guiScale);
			printWriter.println("particles:" + this.particle);
			printWriter.println("bobView:" + this.bobView);
			printWriter.println("maxFps:" + this.maxFramerate);
			printWriter.println("fboEnable:" + this.fbo);
			printWriter.println("difficulty:" + this.difficulty.getId());
			printWriter.println("fancyGraphics:" + this.fancyGraphics);
			printWriter.println("ao:" + this.ao);
			printWriter.println("biomeBlendRadius:" + this.field_19979);
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
			printWriter.println("chatOpacity:" + this.field_19989);
			printWriter.println("snooperEnabled:" + this.snopperEnabled);
			printWriter.println("fullscreen:" + this.fullscreen);
			if (this.client.field_19944.method_18310().isPresent()) {
				printWriter.println("fullscreenResolution:" + ((class_4115)this.client.field_19944.method_18310().get()).method_18287());
			}

			printWriter.println("enableVsync:" + this.field_19991);
			printWriter.println("useVbo:" + this.vbo);
			printWriter.println("hideServerAddress:" + this.hideServerAddress);
			printWriter.println("advancedItemTooltips:" + this.field_19992);
			printWriter.println("pauseOnLostFocus:" + this.field_19973);
			printWriter.println("touchscreen:" + this.touchscreen);
			printWriter.println("overrideWidth:" + this.overrideWidth);
			printWriter.println("overrideHeight:" + this.overrideHeight);
			printWriter.println("heldItemTooltips:" + this.heldItemTooltips);
			printWriter.println("chatHeightFocused:" + this.field_19977);
			printWriter.println("chatHeightUnfocused:" + this.field_19976);
			printWriter.println("chatScale:" + this.field_19974);
			printWriter.println("chatWidth:" + this.field_19975);
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
			printWriter.println("autoSuggestions:" + this.field_19978);
			printWriter.println("mouseWheelSensitivity:" + this.field_19980);
			printWriter.println("glDebugVerbosity:" + this.field_19981);

			for (KeyBinding keyBinding : this.allKeys) {
				printWriter.println("key_" + keyBinding.getTranslationKey() + ":" + keyBinding.method_18176());
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

	public void method_18259(class_4462<class_4286> arg) {
		arg.method_21347();
		Set<class_4286> set = Sets.newLinkedHashSet();
		Iterator<String> iterator = this.resourcePacks.iterator();

		while (iterator.hasNext()) {
			String string = (String)iterator.next();
			class_4286 lv = arg.method_21348(string);
			if (lv == null && !string.startsWith("file/")) {
				lv = arg.method_21348("file/" + string);
			}

			if (lv == null) {
				LOGGER.warn("Removed resource pack {} from options because it doesn't seem to exist anymore", string);
				iterator.remove();
			} else if (!lv.method_21363().method_21343() && !this.incompatibleResourcePacks.contains(string)) {
				LOGGER.warn("Removed resource pack {} from options because it is no longer compatible", string);
				iterator.remove();
			} else if (lv.method_21363().method_21343() && this.incompatibleResourcePacks.contains(string)) {
				LOGGER.info("Removed resource pack {} from incompatibility list because it's now compatible", string);
				this.incompatibleResourcePacks.remove(string);
			} else {
				set.add(lv);
			}
		}

		arg.method_21349(set);
	}

	public static enum Option {
		INVERT_MOUSE("options.invertMouse", false, true),
		SENSITIVITY("options.sensitivity", true, false),
		FIELD_OF_VIEW("options.fov", true, false, 30.0, 110.0, 1.0F),
		BRIGHTNESS("options.gamma", true, false),
		SATURATION("options.saturation", true, false),
		RENDER_DISTANCE("options.renderDistance", true, false, 2.0, 16.0, 1.0F),
		VIEW_BOBBING("options.viewBobbing", false, true),
		MAX_FPS("options.framerateLimit", true, false, 10.0, 260.0, 10.0F),
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
		FULLSCREEN_RESOLUTION("options.fullscreen.resolution", true, false, 0.0, 0.0, 1.0F),
		USE_FULLSCREEN("options.fullscreen", false, true),
		ENABLE_VSYNC("options.vsync", false, true),
		USE_VBO("options.vbo", false, true),
		TOUCHSCREEN("options.touchscreen", false, true),
		CHAT_SCALE("options.chat.scale", true, false),
		CHAT_WIDTH("options.chat.width", true, false),
		CHAT_HEIGHT_FOCUSED("options.chat.height.focused", true, false),
		CHAT_HEIGHT_UNFOCUSED("options.chat.height.unfocused", true, false),
		MIPMAP_LEVELS("options.mipmapLevels", true, false, 0.0, 4.0, 1.0F),
		FORCE_UNICODE("options.forceUnicodeFont", false, true),
		REDUCED_DEBUG_INFO("options.reducedDebugInfo", false, true),
		ENTITY_SHADOWS("options.entityShadows", false, true),
		MAIN_HAND("options.mainHand", false, false),
		ATTACK_INDICATOR("options.attackIndicator", false, false),
		ENABLE_WEAK_ATTACKS("options.enableWeakAttacks", false, true),
		SHOW_SUBTITLES("options.showSubtitles", false, true),
		REALMS_NOTIFICATIONS("options.realmsNotifications", false, true),
		AUTO_JUMP("options.autoJump", false, true),
		NARRATOR("options.narrator", false, false),
		AUTO_SUGGESTIONS("options.autoSuggestCommands", false, true),
		BIOME_BLEND_RADIUS("options.biomeBlendRadius", true, false, 0.0, 7.0, 1.0F),
		MOUSE_WHEEL_SENSITIVITY("options.mouseWheelSensitivity", true, false, 1.0, 10.0, 0.5F);

		private final boolean numeric;
		private final boolean booleanToggle;
		private final String name;
		private final float field_19996;
		private double field_19997;
		private double field_19998;

		public static GameOptions.Option byOrdinal(int id) {
			for (GameOptions.Option option : values()) {
				if (option.getOrdinal() == id) {
					return option;
				}
			}

			return null;
		}

		private Option(String string2, boolean bl, boolean bl2) {
			this(string2, bl, bl2, 0.0, 1.0, 0.0F);
		}

		private Option(String string2, boolean bl, boolean bl2, double d, double e, float f) {
			this.name = string2;
			this.numeric = bl;
			this.booleanToggle = bl2;
			this.field_19997 = d;
			this.field_19998 = e;
			this.field_19996 = f;
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

		public double method_12152() {
			return this.field_19997;
		}

		public double method_18267() {
			return this.field_19998;
		}

		public void setMaxValue(float max) {
			this.field_19998 = (double)max;
		}

		public double method_18261(double d) {
			return MathHelper.clamp((this.method_18265(d) - this.field_19997) / (this.field_19998 - this.field_19997), 0.0, 1.0);
		}

		public double method_18263(double d) {
			return this.method_18265(this.field_19997 + (this.field_19998 - this.field_19997) * MathHelper.clamp(d, 0.0, 1.0));
		}

		public double method_18265(double d) {
			d = this.method_18266(d);
			return MathHelper.clamp(d, this.field_19997, this.field_19998);
		}

		private double method_18266(double d) {
			if (this.field_19996 > 0.0F) {
				d = (double)(this.field_19996 * (float)Math.round(d / (double)this.field_19996));
			}

			return d;
		}
	}
}
