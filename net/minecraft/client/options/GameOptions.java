package net.minecraft.client.options;

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
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.resource.ClientResourcePackContainer;
import net.minecraft.client.tutorial.TutorialStep;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.VideoMode;
import net.minecraft.datafixers.DataFixTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resource.ResourcePackContainerManager;
import net.minecraft.server.network.packet.ClientSettingsC2SPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Arm;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.TagHelper;
import net.minecraft.world.Difficulty;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameOptions {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = new Gson();
	private static final Type STRING_LIST_TYPE = new ParameterizedType() {
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
	public static final Splitter COLON_SPLITTER = Splitter.on(':');
	public double mouseSensitivity = 0.5;
	public int viewDistance = -1;
	public int maxFps = 120;
	public CloudRenderMode cloudRenderMode = CloudRenderMode.FANCY;
	public boolean fancyGraphics = true;
	public AoOption ao = AoOption.MAX;
	public List<String> resourcePacks = Lists.newArrayList();
	public List<String> incompatibleResourcePacks = Lists.newArrayList();
	public ChatVisibility chatVisibility = ChatVisibility.FULL;
	public double chatOpacity = 1.0;
	public double textBackgroundOpacity = 0.5;
	@Nullable
	public String fullscreenResolution;
	public boolean hideServerAddress;
	public boolean advancedItemTooltips;
	public boolean pauseOnLostFocus = true;
	private final Set<PlayerModelPart> enabledPlayerModelParts = Sets.newHashSet(PlayerModelPart.values());
	public Arm mainArm = Arm.field_6183;
	public int overrideWidth;
	public int overrideHeight;
	public boolean heldItemTooltips = true;
	public double chatScale = 1.0;
	public double chatWidth = 1.0;
	public double chatHeightUnfocused = 0.44366196F;
	public double chatHeightFocused = 1.0;
	public int mipmapLevels = 4;
	private final Map<SoundCategory, Float> soundVolumeLevels = Maps.newEnumMap(SoundCategory.class);
	public boolean useNativeTransport = true;
	public AttackIndicator attackIndicator = AttackIndicator.CROSSHAIR;
	public TutorialStep tutorialStep = TutorialStep.field_5650;
	public int biomeBlendRadius = 2;
	public double mouseWheelSensitivity = 1.0;
	public boolean field_20308 = true;
	public int glDebugVerbosity = 1;
	public boolean autoJump = true;
	public boolean autoSuggestions = true;
	public boolean chatColors = true;
	public boolean chatLinks = true;
	public boolean chatLinksPrompt = true;
	public boolean enableVsync = true;
	public boolean entityShadows = true;
	public boolean forceUnicodeFont;
	public boolean invertYMouse;
	public boolean discreteMouseScroll;
	public boolean realmsNotifications = true;
	public boolean reducedDebugInfo;
	public boolean snooperEnabled = true;
	public boolean showSubtitles;
	public boolean backgroundForChatOnly = true;
	public boolean touchscreen;
	public boolean fullscreen;
	public boolean bobView = true;
	public final KeyBinding keyForward = new KeyBinding("key.forward", 87, "key.categories.movement");
	public final KeyBinding keyLeft = new KeyBinding("key.left", 65, "key.categories.movement");
	public final KeyBinding keyBack = new KeyBinding("key.back", 83, "key.categories.movement");
	public final KeyBinding keyRight = new KeyBinding("key.right", 68, "key.categories.movement");
	public final KeyBinding keyJump = new KeyBinding("key.jump", 32, "key.categories.movement");
	public final KeyBinding keySneak = new KeyBinding("key.sneak", 340, "key.categories.movement");
	public final KeyBinding keySprint = new KeyBinding("key.sprint", 341, "key.categories.movement");
	public final KeyBinding keyInventory = new KeyBinding("key.inventory", 69, "key.categories.inventory");
	public final KeyBinding keySwapHands = new KeyBinding("key.swapHands", 70, "key.categories.inventory");
	public final KeyBinding keyDrop = new KeyBinding("key.drop", 81, "key.categories.inventory");
	public final KeyBinding keyUse = new KeyBinding("key.use", InputUtil.Type.MOUSE, 1, "key.categories.gameplay");
	public final KeyBinding keyAttack = new KeyBinding("key.attack", InputUtil.Type.MOUSE, 0, "key.categories.gameplay");
	public final KeyBinding keyPickItem = new KeyBinding("key.pickItem", InputUtil.Type.MOUSE, 2, "key.categories.gameplay");
	public final KeyBinding keyChat = new KeyBinding("key.chat", 84, "key.categories.multiplayer");
	public final KeyBinding keyPlayerList = new KeyBinding("key.playerlist", 258, "key.categories.multiplayer");
	public final KeyBinding keyCommand = new KeyBinding("key.command", 47, "key.categories.multiplayer");
	public final KeyBinding keyScreenshot = new KeyBinding("key.screenshot", 291, "key.categories.misc");
	public final KeyBinding keyTogglePerspective = new KeyBinding("key.togglePerspective", 294, "key.categories.misc");
	public final KeyBinding keySmoothCamera = new KeyBinding("key.smoothCamera", InputUtil.UNKNOWN_KEYCODE.getKeyCode(), "key.categories.misc");
	public final KeyBinding keyFullscreen = new KeyBinding("key.fullscreen", 300, "key.categories.misc");
	public final KeyBinding keySpectatorOutlines = new KeyBinding("key.spectatorOutlines", InputUtil.UNKNOWN_KEYCODE.getKeyCode(), "key.categories.misc");
	public final KeyBinding keyAdvancements = new KeyBinding("key.advancements", 76, "key.categories.misc");
	public final KeyBinding[] keysHotbar = new KeyBinding[]{
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
	public final KeyBinding keySaveToolbarActivator = new KeyBinding("key.saveToolbarActivator", 67, "key.categories.creative");
	public final KeyBinding keyLoadToolbarActivator = new KeyBinding("key.loadToolbarActivator", 88, "key.categories.creative");
	public final KeyBinding[] keysAll = (KeyBinding[])ArrayUtils.addAll(
		new KeyBinding[]{
			this.keyAttack,
			this.keyUse,
			this.keyForward,
			this.keyLeft,
			this.keyBack,
			this.keyRight,
			this.keyJump,
			this.keySneak,
			this.keySprint,
			this.keyDrop,
			this.keyInventory,
			this.keyChat,
			this.keyPlayerList,
			this.keyPickItem,
			this.keyCommand,
			this.keyScreenshot,
			this.keyTogglePerspective,
			this.keySmoothCamera,
			this.keyFullscreen,
			this.keySpectatorOutlines,
			this.keySwapHands,
			this.keySaveToolbarActivator,
			this.keyLoadToolbarActivator,
			this.keyAdvancements
		},
		this.keysHotbar
	);
	protected MinecraftClient client;
	private final File optionsFile;
	public Difficulty difficulty = Difficulty.field_5802;
	public boolean hudHidden;
	public int perspective;
	public boolean debugEnabled;
	public boolean debugProfilerEnabled;
	public boolean debugTpsEnabled;
	public String lastServer = "";
	public boolean smoothCameraEnabled;
	public double fov = 70.0;
	public double gamma;
	public int guiScale;
	public ParticlesOption particles = ParticlesOption.ALL;
	public NarratorOption narrator = NarratorOption.OFF;
	public String language = "en_us";

	public GameOptions(MinecraftClient minecraftClient, File file) {
		this.client = minecraftClient;
		this.optionsFile = new File(file, "options.txt");
		if (minecraftClient.is64Bit() && Runtime.getRuntime().maxMemory() >= 1000000000L) {
			Option.RENDER_DISTANCE.setMax(32.0F);
		} else {
			Option.RENDER_DISTANCE.setMax(16.0F);
		}

		this.viewDistance = minecraftClient.is64Bit() ? 12 : 8;
		this.load();
	}

	public float getTextBackgroundOpacity(float f) {
		return this.backgroundForChatOnly ? f : (float)this.textBackgroundOpacity;
	}

	public int getTextBackgroundColor(float f) {
		return (int)(this.getTextBackgroundOpacity(f) * 255.0F) << 24 & 0xFF000000;
	}

	public int getTextBackgroundColor(int i) {
		return this.backgroundForChatOnly ? i : (int)(this.textBackgroundOpacity * 255.0) << 24 & 0xFF000000;
	}

	public void setKeyCode(KeyBinding keyBinding, InputUtil.KeyCode keyCode) {
		keyBinding.setKeyCode(keyCode);
		this.write();
	}

	public void load() {
		try {
			if (!this.optionsFile.exists()) {
				return;
			}

			this.soundVolumeLevels.clear();
			List<String> list = IOUtils.readLines(new FileInputStream(this.optionsFile));
			CompoundTag compoundTag = new CompoundTag();

			for (String string : list) {
				try {
					Iterator<String> iterator = COLON_SPLITTER.omitEmptyStrings().limit(2).split(string).iterator();
					compoundTag.putString((String)iterator.next(), (String)iterator.next());
				} catch (Exception var10) {
					LOGGER.warn("Skipping bad option: {}", string);
				}
			}

			compoundTag = this.method_1626(compoundTag);

			for (String string2 : compoundTag.getKeys()) {
				String string3 = compoundTag.getString(string2);

				try {
					if ("autoJump".equals(string2)) {
						Option.AUTO_JUMP.set(this, string3);
					}

					if ("autoSuggestions".equals(string2)) {
						Option.AUTO_SUGGESTIONS.set(this, string3);
					}

					if ("chatColors".equals(string2)) {
						Option.CHAT_COLOR.set(this, string3);
					}

					if ("chatLinks".equals(string2)) {
						Option.CHAT_LINKS.set(this, string3);
					}

					if ("chatLinksPrompt".equals(string2)) {
						Option.CHAT_LINKS_PROMPT.set(this, string3);
					}

					if ("enableVsync".equals(string2)) {
						Option.VSYNC.set(this, string3);
					}

					if ("entityShadows".equals(string2)) {
						Option.ENTITY_SHADOWS.set(this, string3);
					}

					if ("forceUnicodeFont".equals(string2)) {
						Option.FORCE_UNICODE_FONT.set(this, string3);
					}

					if ("discrete_mouse_scroll".equals(string2)) {
						Option.DISCRETE_MOUSE_SCROLL.set(this, string3);
					}

					if ("invertYMouse".equals(string2)) {
						Option.INVERT_MOUSE.set(this, string3);
					}

					if ("realmsNotifications".equals(string2)) {
						Option.REALMS_NOTIFICATIONS.set(this, string3);
					}

					if ("reducedDebugInfo".equals(string2)) {
						Option.REDUCED_DEBUG_INFO.set(this, string3);
					}

					if ("showSubtitles".equals(string2)) {
						Option.SUBTITLES.set(this, string3);
					}

					if ("snooperEnabled".equals(string2)) {
						Option.SNOOPER.set(this, string3);
					}

					if ("touchscreen".equals(string2)) {
						Option.TOUCHSCREEN.set(this, string3);
					}

					if ("fullscreen".equals(string2)) {
						Option.FULLSCREEN.set(this, string3);
					}

					if ("bobView".equals(string2)) {
						Option.VIEW_BOBBING.set(this, string3);
					}

					if ("mouseSensitivity".equals(string2)) {
						this.mouseSensitivity = (double)parseFloat(string3);
					}

					if ("fov".equals(string2)) {
						this.fov = (double)(parseFloat(string3) * 40.0F + 70.0F);
					}

					if ("gamma".equals(string2)) {
						this.gamma = (double)parseFloat(string3);
					}

					if ("renderDistance".equals(string2)) {
						this.viewDistance = Integer.parseInt(string3);
					}

					if ("guiScale".equals(string2)) {
						this.guiScale = Integer.parseInt(string3);
					}

					if ("particles".equals(string2)) {
						this.particles = ParticlesOption.byId(Integer.parseInt(string3));
					}

					if ("maxFps".equals(string2)) {
						this.maxFps = Integer.parseInt(string3);
						if (this.client.window != null) {
							this.client.window.setFramerateLimit(this.maxFps);
						}
					}

					if ("difficulty".equals(string2)) {
						this.difficulty = Difficulty.byOrdinal(Integer.parseInt(string3));
					}

					if ("fancyGraphics".equals(string2)) {
						this.fancyGraphics = "true".equals(string3);
					}

					if ("tutorialStep".equals(string2)) {
						this.tutorialStep = TutorialStep.byName(string3);
					}

					if ("ao".equals(string2)) {
						if ("true".equals(string3)) {
							this.ao = AoOption.MAX;
						} else if ("false".equals(string3)) {
							this.ao = AoOption.OFF;
						} else {
							this.ao = AoOption.getOption(Integer.parseInt(string3));
						}
					}

					if ("renderClouds".equals(string2)) {
						if ("true".equals(string3)) {
							this.cloudRenderMode = CloudRenderMode.FANCY;
						} else if ("false".equals(string3)) {
							this.cloudRenderMode = CloudRenderMode.OFF;
						} else if ("fast".equals(string3)) {
							this.cloudRenderMode = CloudRenderMode.FAST;
						}
					}

					if ("attackIndicator".equals(string2)) {
						this.attackIndicator = AttackIndicator.byId(Integer.parseInt(string3));
					}

					if ("resourcePacks".equals(string2)) {
						this.resourcePacks = JsonHelper.deserialize(GSON, string3, STRING_LIST_TYPE);
						if (this.resourcePacks == null) {
							this.resourcePacks = Lists.newArrayList();
						}
					}

					if ("incompatibleResourcePacks".equals(string2)) {
						this.incompatibleResourcePacks = JsonHelper.deserialize(GSON, string3, STRING_LIST_TYPE);
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
						this.chatVisibility = ChatVisibility.byId(Integer.parseInt(string3));
					}

					if ("chatOpacity".equals(string2)) {
						this.chatOpacity = (double)parseFloat(string3);
					}

					if ("textBackgroundOpacity".equals(string2)) {
						this.textBackgroundOpacity = (double)parseFloat(string3);
					}

					if ("backgroundForChatOnly".equals(string2)) {
						this.backgroundForChatOnly = "true".equals(string3);
					}

					if ("fullscreenResolution".equals(string2)) {
						this.fullscreenResolution = string3;
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
						this.chatHeightFocused = (double)parseFloat(string3);
					}

					if ("chatHeightUnfocused".equals(string2)) {
						this.chatHeightUnfocused = (double)parseFloat(string3);
					}

					if ("chatScale".equals(string2)) {
						this.chatScale = (double)parseFloat(string3);
					}

					if ("chatWidth".equals(string2)) {
						this.chatWidth = (double)parseFloat(string3);
					}

					if ("mipmapLevels".equals(string2)) {
						this.mipmapLevels = Integer.parseInt(string3);
					}

					if ("useNativeTransport".equals(string2)) {
						this.useNativeTransport = "true".equals(string3);
					}

					if ("mainHand".equals(string2)) {
						this.mainArm = "left".equals(string3) ? Arm.field_6182 : Arm.field_6183;
					}

					if ("narrator".equals(string2)) {
						this.narrator = NarratorOption.byId(Integer.parseInt(string3));
					}

					if ("biomeBlendRadius".equals(string2)) {
						this.biomeBlendRadius = Integer.parseInt(string3);
					}

					if ("mouseWheelSensitivity".equals(string2)) {
						this.mouseWheelSensitivity = (double)parseFloat(string3);
					}

					if ("rawMouseInput".equals(string2)) {
						this.field_20308 = "true".equals(string3);
					}

					if ("glDebugVerbosity".equals(string2)) {
						this.glDebugVerbosity = Integer.parseInt(string3);
					}

					for (KeyBinding keyBinding : this.keysAll) {
						if (string2.equals("key_" + keyBinding.getId())) {
							keyBinding.setKeyCode(InputUtil.fromName(string3));
						}
					}

					for (SoundCategory soundCategory : SoundCategory.values()) {
						if (string2.equals("soundCategory_" + soundCategory.getName())) {
							this.soundVolumeLevels.put(soundCategory, parseFloat(string3));
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

	private CompoundTag method_1626(CompoundTag compoundTag) {
		int i = 0;

		try {
			i = Integer.parseInt(compoundTag.getString("version"));
		} catch (RuntimeException var4) {
		}

		return TagHelper.update(this.client.getDataFixer(), DataFixTypes.field_19216, compoundTag, i);
	}

	private static float parseFloat(String string) {
		if ("true".equals(string)) {
			return 1.0F;
		} else {
			return "false".equals(string) ? 0.0F : Float.parseFloat(string);
		}
	}

	public void write() {
		try {
			PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.optionsFile), StandardCharsets.UTF_8));
			Throwable var2 = null;

			try {
				printWriter.println("version:" + SharedConstants.getGameVersion().getWorldVersion());
				printWriter.println("autoJump:" + Option.AUTO_JUMP.get(this));
				printWriter.println("autoSuggestions:" + Option.AUTO_SUGGESTIONS.get(this));
				printWriter.println("chatColors:" + Option.CHAT_COLOR.get(this));
				printWriter.println("chatLinks:" + Option.CHAT_LINKS.get(this));
				printWriter.println("chatLinksPrompt:" + Option.CHAT_LINKS_PROMPT.get(this));
				printWriter.println("enableVsync:" + Option.VSYNC.get(this));
				printWriter.println("entityShadows:" + Option.ENTITY_SHADOWS.get(this));
				printWriter.println("forceUnicodeFont:" + Option.FORCE_UNICODE_FONT.get(this));
				printWriter.println("discrete_mouse_scroll:" + Option.DISCRETE_MOUSE_SCROLL.get(this));
				printWriter.println("invertYMouse:" + Option.INVERT_MOUSE.get(this));
				printWriter.println("realmsNotifications:" + Option.REALMS_NOTIFICATIONS.get(this));
				printWriter.println("reducedDebugInfo:" + Option.REDUCED_DEBUG_INFO.get(this));
				printWriter.println("snooperEnabled:" + Option.SNOOPER.get(this));
				printWriter.println("showSubtitles:" + Option.SUBTITLES.get(this));
				printWriter.println("touchscreen:" + Option.TOUCHSCREEN.get(this));
				printWriter.println("fullscreen:" + Option.FULLSCREEN.get(this));
				printWriter.println("bobView:" + Option.VIEW_BOBBING.get(this));
				printWriter.println("mouseSensitivity:" + this.mouseSensitivity);
				printWriter.println("fov:" + (this.fov - 70.0) / 40.0);
				printWriter.println("gamma:" + this.gamma);
				printWriter.println("renderDistance:" + this.viewDistance);
				printWriter.println("guiScale:" + this.guiScale);
				printWriter.println("particles:" + this.particles.getId());
				printWriter.println("maxFps:" + this.maxFps);
				printWriter.println("difficulty:" + this.difficulty.getId());
				printWriter.println("fancyGraphics:" + this.fancyGraphics);
				printWriter.println("ao:" + this.ao.getValue());
				printWriter.println("biomeBlendRadius:" + this.biomeBlendRadius);
				switch (this.cloudRenderMode) {
					case FANCY:
						printWriter.println("renderClouds:true");
						break;
					case FAST:
						printWriter.println("renderClouds:fast");
						break;
					case OFF:
						printWriter.println("renderClouds:false");
				}

				printWriter.println("resourcePacks:" + GSON.toJson(this.resourcePacks));
				printWriter.println("incompatibleResourcePacks:" + GSON.toJson(this.incompatibleResourcePacks));
				printWriter.println("lastServer:" + this.lastServer);
				printWriter.println("lang:" + this.language);
				printWriter.println("chatVisibility:" + this.chatVisibility.getId());
				printWriter.println("chatOpacity:" + this.chatOpacity);
				printWriter.println("textBackgroundOpacity:" + this.textBackgroundOpacity);
				printWriter.println("backgroundForChatOnly:" + this.backgroundForChatOnly);
				if (this.client.window.getVideoMode().isPresent()) {
					printWriter.println("fullscreenResolution:" + ((VideoMode)this.client.window.getVideoMode().get()).asString());
				}

				printWriter.println("hideServerAddress:" + this.hideServerAddress);
				printWriter.println("advancedItemTooltips:" + this.advancedItemTooltips);
				printWriter.println("pauseOnLostFocus:" + this.pauseOnLostFocus);
				printWriter.println("overrideWidth:" + this.overrideWidth);
				printWriter.println("overrideHeight:" + this.overrideHeight);
				printWriter.println("heldItemTooltips:" + this.heldItemTooltips);
				printWriter.println("chatHeightFocused:" + this.chatHeightFocused);
				printWriter.println("chatHeightUnfocused:" + this.chatHeightUnfocused);
				printWriter.println("chatScale:" + this.chatScale);
				printWriter.println("chatWidth:" + this.chatWidth);
				printWriter.println("mipmapLevels:" + this.mipmapLevels);
				printWriter.println("useNativeTransport:" + this.useNativeTransport);
				printWriter.println("mainHand:" + (this.mainArm == Arm.field_6182 ? "left" : "right"));
				printWriter.println("attackIndicator:" + this.attackIndicator.getId());
				printWriter.println("narrator:" + this.narrator.getId());
				printWriter.println("tutorialStep:" + this.tutorialStep.getName());
				printWriter.println("mouseWheelSensitivity:" + this.mouseWheelSensitivity);
				printWriter.println("rawMouseInput:" + Option.RAW_MOUSE_INPUT.get(this));
				printWriter.println("glDebugVerbosity:" + this.glDebugVerbosity);

				for (KeyBinding keyBinding : this.keysAll) {
					printWriter.println("key_" + keyBinding.getId() + ":" + keyBinding.getName());
				}

				for (SoundCategory soundCategory : SoundCategory.values()) {
					printWriter.println("soundCategory_" + soundCategory.getName() + ":" + this.getSoundVolume(soundCategory));
				}

				for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
					printWriter.println("modelPart_" + playerModelPart.getName() + ":" + this.enabledPlayerModelParts.contains(playerModelPart));
				}
			} catch (Throwable var15) {
				var2 = var15;
				throw var15;
			} finally {
				if (printWriter != null) {
					if (var2 != null) {
						try {
							printWriter.close();
						} catch (Throwable var14) {
							var2.addSuppressed(var14);
						}
					} else {
						printWriter.close();
					}
				}
			}
		} catch (Exception var17) {
			LOGGER.error("Failed to save options", var17);
		}

		this.onPlayerModelPartChange();
	}

	public float getSoundVolume(SoundCategory soundCategory) {
		return this.soundVolumeLevels.containsKey(soundCategory) ? (Float)this.soundVolumeLevels.get(soundCategory) : 1.0F;
	}

	public void setSoundVolume(SoundCategory soundCategory, float f) {
		this.soundVolumeLevels.put(soundCategory, f);
		this.client.getSoundManager().updateSoundVolume(soundCategory, f);
	}

	public void onPlayerModelPartChange() {
		if (this.client.player != null) {
			int i = 0;

			for (PlayerModelPart playerModelPart : this.enabledPlayerModelParts) {
				i |= playerModelPart.getBitFlag();
			}

			this.client
				.player
				.networkHandler
				.sendPacket(new ClientSettingsC2SPacket(this.language, this.viewDistance, this.chatVisibility, this.chatColors, i, this.mainArm));
		}
	}

	public Set<PlayerModelPart> getEnabledPlayerModelParts() {
		return ImmutableSet.copyOf(this.enabledPlayerModelParts);
	}

	public void setPlayerModelPart(PlayerModelPart playerModelPart, boolean bl) {
		if (bl) {
			this.enabledPlayerModelParts.add(playerModelPart);
		} else {
			this.enabledPlayerModelParts.remove(playerModelPart);
		}

		this.onPlayerModelPartChange();
	}

	public void togglePlayerModelPart(PlayerModelPart playerModelPart) {
		if (this.getEnabledPlayerModelParts().contains(playerModelPart)) {
			this.enabledPlayerModelParts.remove(playerModelPart);
		} else {
			this.enabledPlayerModelParts.add(playerModelPart);
		}

		this.onPlayerModelPartChange();
	}

	public CloudRenderMode getCloudRenderMode() {
		return this.viewDistance >= 4 ? this.cloudRenderMode : CloudRenderMode.OFF;
	}

	public boolean shouldUseNativeTransport() {
		return this.useNativeTransport;
	}

	public void addResourcePackContainersToManager(ResourcePackContainerManager<ClientResourcePackContainer> resourcePackContainerManager) {
		resourcePackContainerManager.callCreators();
		Set<ClientResourcePackContainer> set = Sets.newLinkedHashSet();
		Iterator<String> iterator = this.resourcePacks.iterator();

		while (iterator.hasNext()) {
			String string = (String)iterator.next();
			ClientResourcePackContainer clientResourcePackContainer = resourcePackContainerManager.getContainer(string);
			if (clientResourcePackContainer == null && !string.startsWith("file/")) {
				clientResourcePackContainer = resourcePackContainerManager.getContainer("file/" + string);
			}

			if (clientResourcePackContainer == null) {
				LOGGER.warn("Removed resource pack {} from options because it doesn't seem to exist anymore", string);
				iterator.remove();
			} else if (!clientResourcePackContainer.getCompatibility().isCompatible() && !this.incompatibleResourcePacks.contains(string)) {
				LOGGER.warn("Removed resource pack {} from options because it is no longer compatible", string);
				iterator.remove();
			} else if (clientResourcePackContainer.getCompatibility().isCompatible() && this.incompatibleResourcePacks.contains(string)) {
				LOGGER.info("Removed resource pack {} from incompatibility list because it's now compatible", string);
				this.incompatibleResourcePacks.remove(string);
			} else {
				set.add(clientResourcePackContainer);
			}
		}

		resourcePackContainerManager.setEnabled(set);
	}
}
