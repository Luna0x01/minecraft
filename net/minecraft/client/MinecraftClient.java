package net.minecraft.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.hash.Hashing;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.mojang.authlib.AuthenticationService;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import net.minecraft.Bootstrap;
import net.minecraft.class_3251;
import net.minecraft.class_3253;
import net.minecraft.class_3264;
import net.minecraft.class_3286;
import net.minecraft.class_3304;
import net.minecraft.class_3306;
import net.minecraft.class_3308;
import net.minecraft.class_3316;
import net.minecraft.class_3320;
import net.minecraft.class_3355;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.AdvancementsScreen;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.OutOfMemoryScreen;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SleepingChatScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.SurvivalInventoryScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.options.ChatOptionsScreen;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.ClientTickTracker;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LoadingScreenRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.resource.AnimationMetadata;
import net.minecraft.client.resource.AnimationMetadataSerializer;
import net.minecraft.client.resource.FontMetadata;
import net.minecraft.client.resource.FontMetadataSerializer;
import net.minecraft.client.resource.LanguageMetadataSerializer;
import net.minecraft.client.resource.PackFormatMetadataSerializer;
import net.minecraft.client.resource.ResourcePackLoader;
import net.minecraft.client.resource.ResourcePackMetadata;
import net.minecraft.client.resource.TextureMetadataSerializer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.resource.metadata.LanguageResourceMetadata;
import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.AnError;
import net.minecraft.client.util.ScreenshotUtils;
import net.minecraft.client.util.Session;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.BuiltChunk;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.datafixer.DataFixerFactory;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.entity.EndCrystalEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.FoliageColorResourceReloadListener;
import net.minecraft.resource.GrassColorResourceReloadListener;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.stat.StatHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.KeyBindComponent;
import net.minecraft.util.MetadataSerializer;
import net.minecraft.util.MetricsData;
import net.minecraft.util.ThreadExecutor;
import net.minecraft.util.UserCache;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.snooper.Snoopable;
import net.minecraft.util.snooper.Snooper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.SaveHandler;
import net.minecraft.world.dimension.TheEndDimension;
import net.minecraft.world.dimension.TheNetherDimension;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.AnvilLevelStorage;
import net.minecraft.world.level.storage.LevelStorageAccess;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.OpenGLException;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;

public class MinecraftClient implements ThreadExecutor, Snoopable {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Identifier MOJANG_LOGO_TEXTURE = new Identifier("textures/gui/title/mojang.png");
	public static final boolean IS_MAC = Util.getOperatingSystem() == Util.OperatingSystem.MACOS;
	public static byte[] memoryReservedForCrash = new byte[10485760];
	private static final List<DisplayMode> DISPLAY_MODES = Lists.newArrayList(new DisplayMode[]{new DisplayMode(2560, 1600), new DisplayMode(2880, 1800)});
	private final File resourcePackDir;
	private final PropertyMap twitchPropertyMap;
	private final PropertyMap sessionPropertyMap;
	private ServerInfo currentServerEntry;
	private TextureManager textureManager;
	private static MinecraftClient instance;
	private final DataFixerUpper field_13277;
	public ClientPlayerInteractionManager interactionManager;
	private boolean fullscreen;
	private final boolean glErrors = true;
	private boolean crashed;
	private CrashReport crashReport;
	public int width;
	public int height;
	private boolean connectedToRealms;
	private final ClientTickTracker ticker = new ClientTickTracker(20.0F);
	private final Snooper snooper = new Snooper("client", this, MinecraftServer.getTimeMillis());
	public ClientWorld world;
	public WorldRenderer worldRenderer;
	private EntityRenderDispatcher entityRenderDispatcher;
	private ItemRenderer itemRenderer;
	private HeldItemRenderer heldItemRenderer;
	public ClientPlayerEntity player;
	@Nullable
	private Entity cameraEntity;
	public Entity targetedEntity;
	public ParticleManager particleManager;
	private class_3306 field_15870 = new class_3306();
	private final Session session;
	private boolean paused;
	private float field_15871;
	public TextRenderer textRenderer;
	public TextRenderer shadowTextRenderer;
	@Nullable
	public Screen currentScreen;
	public LoadingScreenRenderer loadingScreenRenderer;
	public GameRenderer gameRenderer;
	public DebugRenderer debugRenderer;
	private int attackCooldown;
	private final int tempWidth;
	private final int tempHeight;
	@Nullable
	private IntegratedServer server;
	public InGameHud inGameHud;
	public boolean skipGameRender;
	public BlockHitResult result;
	public GameOptions options;
	public class_3251 field_15872;
	public MouseInput mouse;
	public final File runDirectory;
	private final File assetDirectory;
	private final String gameVersion;
	private final String versionType;
	private final Proxy networkProxy;
	private LevelStorageAccess currentSave;
	private static int currentFps;
	private int blockPlaceDelay;
	private String serverAddress;
	private int serverPort;
	public boolean focused;
	long sysTime = getTime();
	private int joinPlayerCounter;
	public final MetricsData metricsData = new MetricsData();
	long nanoTime = System.nanoTime();
	private final boolean is64Bit;
	private final boolean isDemo;
	@Nullable
	private ClientConnection clientConnection;
	private boolean isIntegratedServerRunning;
	public final Profiler profiler = new Profiler();
	private long f3CTime = -1L;
	private ReloadableResourceManager resourceManager;
	private final MetadataSerializer metadataSerializer = new MetadataSerializer();
	private final List<ResourcePack> resourcePacks = Lists.newArrayList();
	private final DefaultResourcePack defaultResourcePack;
	private ResourcePackLoader loader;
	private LanguageManager languageManager;
	private BlockColors field_13278;
	private class_2838 field_13279;
	private Framebuffer fbo;
	private SpriteAtlasTexture texture;
	private SoundManager soundManager;
	private MusicTracker musicTracker;
	private Identifier mojang;
	private final MinecraftSessionService sessionService;
	private PlayerSkinProvider skinProvider;
	private final Queue<FutureTask<?>> tasks = Queues.newArrayDeque();
	private final Thread currentThread = Thread.currentThread();
	private BakedModelManager modelManager;
	private BlockRenderManager blockRenderManager;
	private final class_3264 field_15868;
	volatile boolean running = true;
	public String fpsDebugString = "";
	public boolean chunkCullingEnabled = true;
	private long time = getTime();
	private int fpsCounter;
	private boolean field_13280;
	private final class_3316 field_15869;
	long debugTime = -1L;
	private String openProfilerSection = "root";

	public MinecraftClient(RunArgs runArgs) {
		instance = this;
		this.runDirectory = runArgs.directories.runDir;
		this.assetDirectory = runArgs.directories.assetDir;
		this.resourcePackDir = runArgs.directories.resourcePackDir;
		this.gameVersion = runArgs.game.version;
		this.versionType = runArgs.game.versionType;
		this.twitchPropertyMap = runArgs.args.userProperties;
		this.sessionPropertyMap = runArgs.args.profileProperties;
		this.defaultResourcePack = new DefaultResourcePack(runArgs.directories.getAssetsIndex());
		this.networkProxy = runArgs.args.netProxy == null ? Proxy.NO_PROXY : runArgs.args.netProxy;
		this.sessionService = new YggdrasilAuthenticationService(this.networkProxy, UUID.randomUUID().toString()).createMinecraftSessionService();
		this.session = runArgs.args.session;
		LOGGER.info("Setting user: {}", this.session.getUsername());
		LOGGER.debug("(Session ID is {})", this.session.getSessionId());
		this.isDemo = runArgs.game.demo;
		this.width = runArgs.windowInformation.width > 0 ? runArgs.windowInformation.width : 1;
		this.height = runArgs.windowInformation.height > 0 ? runArgs.windowInformation.height : 1;
		this.tempWidth = runArgs.windowInformation.width;
		this.tempHeight = runArgs.windowInformation.height;
		this.fullscreen = runArgs.windowInformation.fullscreen;
		this.is64Bit = checkIs64Bit();
		this.server = null;
		if (runArgs.autoConnect.serverIp != null) {
			this.serverAddress = runArgs.autoConnect.serverIp;
			this.serverPort = runArgs.autoConnect.serverPort;
		}

		ImageIO.setUseCache(false);
		Locale.setDefault(Locale.ROOT);
		Bootstrap.initialize();
		KeyBindComponent.field_16261 = KeyBinding::method_14453;
		this.field_13277 = DataFixerFactory.createDataFixer();
		this.field_15868 = new class_3264(this);
		this.field_15869 = new class_3316(this);
	}

	public void run() {
		this.running = true;

		try {
			this.initializeGame();
		} catch (Throwable var11) {
			CrashReport crashReport = CrashReport.create(var11, "Initializing game");
			crashReport.addElement("Initialization");
			this.printCrashReport(this.addSystemDetailsToCrashReport(crashReport));
			return;
		}

		try {
			try {
				while (this.running) {
					if (this.crashed && this.crashReport != null) {
						this.printCrashReport(this.crashReport);
						return;
					} else {
						try {
							this.runGameLoop();
						} catch (OutOfMemoryError var10) {
							this.cleanUpAfterCrash();
							this.setScreen(new OutOfMemoryScreen());
							System.gc();
						}
					}
				}

				return;
			} catch (AnError var12) {
			} catch (CrashException var13) {
				this.addSystemDetailsToCrashReport(var13.getReport());
				this.cleanUpAfterCrash();
				LOGGER.fatal("Reported exception thrown!", var13);
				this.printCrashReport(var13.getReport());
			} catch (Throwable var14) {
				CrashReport crashReport2 = this.addSystemDetailsToCrashReport(new CrashReport("Unexpected error", var14));
				this.cleanUpAfterCrash();
				LOGGER.fatal("Unreported exception thrown!", var14);
				this.printCrashReport(crashReport2);
			}
		} finally {
			this.stop();
		}
	}

	private void initializeGame() throws LWJGLException {
		this.options = new GameOptions(this, this.runDirectory);
		this.field_15872 = new class_3251(this, this.runDirectory);
		this.resourcePacks.add(this.defaultResourcePack);
		this.initializeTimerHackThread();
		if (this.options.overrideHeight > 0 && this.options.overrideWidth > 0) {
			this.width = this.options.overrideWidth;
			this.height = this.options.overrideHeight;
		}

		LOGGER.info("LWJGL Version: {}", Sys.getVersion());
		this.setDefaultIcon();
		this.setDisplayBounds();
		this.setPixelFormat();
		GLX.createContext();
		this.fbo = new Framebuffer(this.width, this.height, true);
		this.fbo.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
		this.registerMetadataSerializers();
		this.loader = new ResourcePackLoader(
			this.resourcePackDir, new File(this.runDirectory, "server-resource-packs"), this.defaultResourcePack, this.metadataSerializer, this.options
		);
		this.resourceManager = new ReloadableResourceManagerImpl(this.metadataSerializer);
		this.languageManager = new LanguageManager(this.metadataSerializer, this.options.language);
		this.resourceManager.registerListener(this.languageManager);
		this.reloadResources();
		this.textureManager = new TextureManager(this.resourceManager);
		this.resourceManager.registerListener(this.textureManager);
		this.loadLogo(this.textureManager);
		this.skinProvider = new PlayerSkinProvider(this.textureManager, new File(this.assetDirectory, "skins"), this.sessionService);
		this.currentSave = new AnvilLevelStorage(new File(this.runDirectory, "saves"), this.field_13277);
		this.soundManager = new SoundManager(this.resourceManager, this.options);
		this.resourceManager.registerListener(this.soundManager);
		this.musicTracker = new MusicTracker(this);
		this.textRenderer = new TextRenderer(this.options, new Identifier("textures/font/ascii.png"), this.textureManager, false);
		if (this.options.language != null) {
			this.textRenderer.setUnicode(this.forcesUnicodeFont());
			this.textRenderer.setRightToLeft(this.languageManager.isRightToLeft());
		}

		this.shadowTextRenderer = new TextRenderer(this.options, new Identifier("textures/font/ascii_sga.png"), this.textureManager, false);
		this.resourceManager.registerListener(this.textRenderer);
		this.resourceManager.registerListener(this.shadowTextRenderer);
		this.resourceManager.registerListener(new GrassColorResourceReloadListener());
		this.resourceManager.registerListener(new FoliageColorResourceReloadListener());
		this.mouse = new MouseInput();
		this.setGlErrorMessage("Pre startup");
		GlStateManager.enableTexture();
		GlStateManager.shadeModel(7425);
		GlStateManager.clearDepth(1.0);
		GlStateManager.enableDepthTest();
		GlStateManager.depthFunc(515);
		GlStateManager.enableAlphaTest();
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.method_12284(GlStateManager.class_2865.BACK);
		GlStateManager.matrixMode(5889);
		GlStateManager.loadIdentity();
		GlStateManager.matrixMode(5888);
		this.setGlErrorMessage("Startup");
		this.texture = new SpriteAtlasTexture("textures");
		this.texture.setMaxTextureSize(this.options.mipmapLevels);
		this.textureManager.loadTickableTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX, this.texture);
		this.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		this.texture.setFilter(false, this.options.mipmapLevels > 0);
		this.modelManager = new BakedModelManager(this.texture);
		this.resourceManager.registerListener(this.modelManager);
		this.field_13278 = BlockColors.create();
		this.field_13279 = class_2838.method_12161(this.field_13278);
		this.itemRenderer = new ItemRenderer(this.textureManager, this.modelManager, this.field_13279);
		this.entityRenderDispatcher = new EntityRenderDispatcher(this.textureManager, this.itemRenderer);
		this.heldItemRenderer = new HeldItemRenderer(this);
		this.resourceManager.registerListener(this.itemRenderer);
		this.gameRenderer = new GameRenderer(this, this.resourceManager);
		this.resourceManager.registerListener(this.gameRenderer);
		this.blockRenderManager = new BlockRenderManager(this.modelManager.getModelShapes(), this.field_13278);
		this.resourceManager.registerListener(this.blockRenderManager);
		this.worldRenderer = new WorldRenderer(this);
		this.resourceManager.registerListener(this.worldRenderer);
		this.method_14464();
		this.resourceManager.registerListener(this.field_15870);
		GlStateManager.viewport(0, 0, this.width, this.height);
		this.particleManager = new ParticleManager(this.world, this.textureManager);
		this.setGlErrorMessage("Post startup");
		this.inGameHud = new InGameHud(this);
		if (this.serverAddress != null) {
			this.setScreen(new ConnectScreen(new TitleScreen(), this, this.serverAddress, this.serverPort));
		} else {
			this.setScreen(new TitleScreen());
		}

		this.textureManager.close(this.mojang);
		this.mojang = null;
		this.loadingScreenRenderer = new LoadingScreenRenderer(this);
		this.debugRenderer = new DebugRenderer(this);
		if (this.options.fullscreen && !this.fullscreen) {
			this.toggleFullscreen();
		}

		try {
			Display.setVSyncEnabled(this.options.vsync);
		} catch (OpenGLException var2) {
			this.options.vsync = false;
			this.options.save();
		}

		this.worldRenderer.setupEntityOutlineShader();
	}

	private void method_14464() {
		class_3304<ItemStack> lv = new class_3304<>(
			itemStack -> (List)itemStack.getTooltip(null, TooltipContext.TooltipType.NORMAL)
					.stream()
					.map(Formatting::strip)
					.map(String::trim)
					.filter(string -> !string.isEmpty())
					.collect(Collectors.toList()),
			itemStack -> Collections.singleton(Item.REGISTRY.getIdentifier(itemStack.getItem()))
		);
		DefaultedList<ItemStack> defaultedList = DefaultedList.of();

		for (Item item : Item.REGISTRY) {
			item.appendToItemGroup(ItemGroup.SEARCH, defaultedList);
		}

		defaultedList.forEach(lv::method_14701);
		class_3304<class_3286> lv2 = new class_3304<>(
			arg -> (List)arg.method_14634()
					.stream()
					.flatMap(recipeType -> recipeType.getOutput().getTooltip(null, TooltipContext.TooltipType.NORMAL).stream())
					.map(Formatting::strip)
					.map(String::trim)
					.filter(string -> !string.isEmpty())
					.collect(Collectors.toList()),
			arg -> (List)arg.method_14634().stream().map(recipeType -> Item.REGISTRY.getIdentifier(recipeType.getOutput().getItem())).collect(Collectors.toList())
		);
		class_3320.field_16243.forEach(lv2::method_14701);
		this.field_15870.method_14706(class_3306.field_16177, lv);
		this.field_15870.method_14706(class_3306.field_16178, lv2);
	}

	private void registerMetadataSerializers() {
		this.metadataSerializer.register(new TextureMetadataSerializer(), TextureResourceMetadata.class);
		this.metadataSerializer.register(new FontMetadataSerializer(), FontMetadata.class);
		this.metadataSerializer.register(new AnimationMetadataSerializer(), AnimationMetadata.class);
		this.metadataSerializer.register(new PackFormatMetadataSerializer(), ResourcePackMetadata.class);
		this.metadataSerializer.register(new LanguageMetadataSerializer(), LanguageResourceMetadata.class);
	}

	private void setPixelFormat() throws LWJGLException {
		Display.setResizable(true);
		Display.setTitle("Minecraft 1.12.2");

		try {
			Display.create(new PixelFormat().withDepthBits(24));
		} catch (LWJGLException var4) {
			LOGGER.error("Couldn't set pixel format", var4);

			try {
				Thread.sleep(1000L);
			} catch (InterruptedException var3) {
			}

			if (this.fullscreen) {
				this.updateDisplayMode();
			}

			Display.create();
		}
	}

	private void setDisplayBounds() throws LWJGLException {
		if (this.fullscreen) {
			Display.setFullscreen(true);
			DisplayMode displayMode = Display.getDisplayMode();
			this.width = Math.max(1, displayMode.getWidth());
			this.height = Math.max(1, displayMode.getHeight());
		} else {
			Display.setDisplayMode(new DisplayMode(this.width, this.height));
		}
	}

	private void setDefaultIcon() {
		Util.OperatingSystem operatingSystem = Util.getOperatingSystem();
		if (operatingSystem != Util.OperatingSystem.MACOS) {
			InputStream inputStream = null;
			InputStream inputStream2 = null;

			try {
				inputStream = this.defaultResourcePack.openFile(new Identifier("icons/icon_16x16.png"));
				inputStream2 = this.defaultResourcePack.openFile(new Identifier("icons/icon_32x32.png"));
				if (inputStream != null && inputStream2 != null) {
					Display.setIcon(new ByteBuffer[]{this.readInputStreamAsImage(inputStream), this.readInputStreamAsImage(inputStream2)});
				}
			} catch (IOException var8) {
				LOGGER.error("Couldn't set icon", var8);
			} finally {
				IOUtils.closeQuietly(inputStream);
				IOUtils.closeQuietly(inputStream2);
			}
		}
	}

	private static boolean checkIs64Bit() {
		String[] strings = new String[]{"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};

		for (String string : strings) {
			String string2 = System.getProperty(string);
			if (string2 != null && string2.contains("64")) {
				return true;
			}
		}

		return false;
	}

	public Framebuffer getFramebuffer() {
		return this.fbo;
	}

	public String getGameVersion() {
		return this.gameVersion;
	}

	public String getVersionType() {
		return this.versionType;
	}

	private void initializeTimerHackThread() {
		Thread thread = new Thread("Timer hack thread") {
			public void run() {
				while (MinecraftClient.this.running) {
					try {
						Thread.sleep(2147483647L);
					} catch (InterruptedException var2) {
					}
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
	}

	public void crash(CrashReport crashReport) {
		this.crashed = true;
		this.crashReport = crashReport;
	}

	public void printCrashReport(CrashReport crashReport) {
		File file = new File(getInstance().runDirectory, "crash-reports");
		File file2 = new File(file, "crash-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-client.txt");
		Bootstrap.println(crashReport.asString());
		if (crashReport.getFile() != null) {
			Bootstrap.println("#@!@# Game crashed! Crash report saved to: #@!@# " + crashReport.getFile());
			System.exit(-1);
		} else if (crashReport.writeToFile(file2)) {
			Bootstrap.println("#@!@# Game crashed! Crash report saved to: #@!@# " + file2.getAbsolutePath());
			System.exit(-1);
		} else {
			Bootstrap.println("#@?@# Game crashed! Crash report could not be saved. #@?@#");
			System.exit(-2);
		}
	}

	public boolean forcesUnicodeFont() {
		return this.languageManager.forcesUnicodeFont() || this.options.forcesUnicodeFont;
	}

	public void reloadResources() {
		List<ResourcePack> list = Lists.newArrayList(this.resourcePacks);
		if (this.server != null) {
			this.server.method_14912();
		}

		for (ResourcePackLoader.Entry entry : this.loader.getSelectedResourcePacks()) {
			list.add(entry.getResourcePack());
		}

		if (this.loader.getServerContainer() != null) {
			list.add(this.loader.getServerContainer());
		}

		try {
			this.resourceManager.reload(list);
		} catch (RuntimeException var4) {
			LOGGER.info("Caught error stitching, removing all assigned resourcepacks", var4);
			list.clear();
			list.addAll(this.resourcePacks);
			this.loader.setSelectedResourcePacks(Collections.emptyList());
			this.resourceManager.reload(list);
			this.options.resourcePacks.clear();
			this.options.incompatibleResourcePacks.clear();
			this.options.save();
		}

		this.languageManager.reloadResourceLanguages(list);
		if (this.worldRenderer != null) {
			this.worldRenderer.reload();
		}
	}

	private ByteBuffer readInputStreamAsImage(InputStream stream) throws IOException {
		BufferedImage bufferedImage = ImageIO.read(stream);
		int[] is = bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null, 0, bufferedImage.getWidth());
		ByteBuffer byteBuffer = ByteBuffer.allocate(4 * is.length);

		for (int i : is) {
			byteBuffer.putInt(i << 8 | i >> 24 & 0xFF);
		}

		byteBuffer.flip();
		return byteBuffer;
	}

	private void updateDisplayMode() throws LWJGLException {
		Set<DisplayMode> set = Sets.newHashSet();
		Collections.addAll(set, Display.getAvailableDisplayModes());
		DisplayMode displayMode = Display.getDesktopDisplayMode();
		if (!set.contains(displayMode) && Util.getOperatingSystem() == Util.OperatingSystem.MACOS) {
			for (DisplayMode displayMode2 : DISPLAY_MODES) {
				boolean bl = true;

				for (DisplayMode displayMode3 : set) {
					if (displayMode3.getBitsPerPixel() == 32 && displayMode3.getWidth() == displayMode2.getWidth() && displayMode3.getHeight() == displayMode2.getHeight()) {
						bl = false;
						break;
					}
				}

				if (!bl) {
					for (DisplayMode displayMode4 : set) {
						if (displayMode4.getBitsPerPixel() == 32
							&& displayMode4.getWidth() == displayMode2.getWidth() / 2
							&& displayMode4.getHeight() == displayMode2.getHeight() / 2) {
							displayMode = displayMode4;
							break;
						}
					}
				}
			}
		}

		Display.setDisplayMode(displayMode);
		this.width = displayMode.getWidth();
		this.height = displayMode.getHeight();
	}

	private void loadLogo(TextureManager textureManager) {
		Window window = new Window(this);
		int i = window.getScaleFactor();
		Framebuffer framebuffer = new Framebuffer(window.getWidth() * i, window.getHeight() * i, true);
		framebuffer.bind(false);
		GlStateManager.matrixMode(5889);
		GlStateManager.loadIdentity();
		GlStateManager.ortho(0.0, (double)window.getWidth(), (double)window.getHeight(), 0.0, 1000.0, 3000.0);
		GlStateManager.matrixMode(5888);
		GlStateManager.loadIdentity();
		GlStateManager.translate(0.0F, 0.0F, -2000.0F);
		GlStateManager.disableLighting();
		GlStateManager.disableFog();
		GlStateManager.disableDepthTest();
		GlStateManager.enableTexture();
		InputStream inputStream = null;

		try {
			inputStream = this.defaultResourcePack.open(MOJANG_LOGO_TEXTURE);
			this.mojang = textureManager.registerDynamicTexture("logo", new NativeImageBackedTexture(ImageIO.read(inputStream)));
			textureManager.bindTexture(this.mojang);
		} catch (IOException var12) {
			LOGGER.error("Unable to load logo: {}", MOJANG_LOGO_TEXTURE, var12);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex(0.0, (double)this.height, 0.0).texture(0.0, 0.0).color(255, 255, 255, 255).next();
		bufferBuilder.vertex((double)this.width, (double)this.height, 0.0).texture(0.0, 0.0).color(255, 255, 255, 255).next();
		bufferBuilder.vertex((double)this.width, 0.0, 0.0).texture(0.0, 0.0).color(255, 255, 255, 255).next();
		bufferBuilder.vertex(0.0, 0.0, 0.0).texture(0.0, 0.0).color(255, 255, 255, 255).next();
		tessellator.draw();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		int j = 256;
		int k = 256;
		this.drawLogo((window.getWidth() - 256) / 2, (window.getHeight() - 256) / 2, 0, 0, 256, 256, 255, 255, 255, 255);
		GlStateManager.disableLighting();
		GlStateManager.disableFog();
		framebuffer.unbind();
		framebuffer.draw(window.getWidth() * i, window.getHeight() * i);
		GlStateManager.enableAlphaTest();
		GlStateManager.alphaFunc(516, 0.1F);
		this.updateDisplay();
	}

	public void drawLogo(int startX, int startY, int endX, int endY, int width, int height, int red, int green, int blue, int alpha) {
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
		float f = 0.00390625F;
		float g = 0.00390625F;
		bufferBuilder.vertex((double)startX, (double)(startY + height), 0.0)
			.texture((double)((float)endX * 0.00390625F), (double)((float)(endY + height) * 0.00390625F))
			.color(red, green, blue, alpha)
			.next();
		bufferBuilder.vertex((double)(startX + width), (double)(startY + height), 0.0)
			.texture((double)((float)(endX + width) * 0.00390625F), (double)((float)(endY + height) * 0.00390625F))
			.color(red, green, blue, alpha)
			.next();
		bufferBuilder.vertex((double)(startX + width), (double)startY, 0.0)
			.texture((double)((float)(endX + width) * 0.00390625F), (double)((float)endY * 0.00390625F))
			.color(red, green, blue, alpha)
			.next();
		bufferBuilder.vertex((double)startX, (double)startY, 0.0)
			.texture((double)((float)endX * 0.00390625F), (double)((float)endY * 0.00390625F))
			.color(red, green, blue, alpha)
			.next();
		Tessellator.getInstance().draw();
	}

	public LevelStorageAccess getCurrentSave() {
		return this.currentSave;
	}

	public void setScreen(@Nullable Screen screen) {
		if (this.currentScreen != null) {
			this.currentScreen.removed();
		}

		if (screen == null && this.world == null) {
			screen = new TitleScreen();
		} else if (screen == null && this.player.getHealth() <= 0.0F) {
			screen = new DeathScreen(null);
		}

		if (screen instanceof TitleScreen || screen instanceof MultiplayerScreen) {
			this.options.debugEnabled = false;
			this.inGameHud.getChatHud().clear(true);
		}

		this.currentScreen = screen;
		if (screen != null) {
			this.grabMouse();
			KeyBinding.releaseAllKeys();

			while (Mouse.next()) {
			}

			while (Keyboard.next()) {
			}

			Window window = new Window(this);
			int i = window.getWidth();
			int j = window.getHeight();
			screen.init(this, i, j);
			this.skipGameRender = false;
		} else {
			this.soundManager.resumeAll();
			this.closeScreen();
		}
	}

	private void setGlErrorMessage(String message) {
		int i = GlStateManager.method_12271();
		if (i != 0) {
			String string = GLU.gluErrorString(i);
			LOGGER.error("########## GL ERROR ##########");
			LOGGER.error("@ {}", message);
			LOGGER.error("{}: {}", i, string);
		}
	}

	public void stop() {
		try {
			LOGGER.info("Stopping!");

			try {
				this.connect(null);
			} catch (Throwable var5) {
			}

			this.soundManager.close();
		} finally {
			Display.destroy();
			if (!this.crashed) {
				System.exit(0);
			}
		}

		System.gc();
	}

	private void runGameLoop() {
		long l = System.nanoTime();
		this.profiler.push("root");
		if (Display.isCreated() && Display.isCloseRequested()) {
			this.scheduleStop();
		}

		this.ticker.tick();
		this.profiler.push("scheduledExecutables");
		synchronized (this.tasks) {
			while (!this.tasks.isEmpty()) {
				Util.executeTask((FutureTask)this.tasks.poll(), LOGGER);
			}
		}

		this.profiler.pop();
		long m = System.nanoTime();
		this.profiler.push("tick");

		for (int i = 0; i < Math.min(10, this.ticker.ticksThisFrame); i++) {
			this.tick();
		}

		this.profiler.swap("preRenderErrors");
		long n = System.nanoTime() - m;
		this.setGlErrorMessage("Pre render");
		this.profiler.swap("sound");
		this.soundManager.updateListenerPosition(this.player, this.ticker.tickDelta);
		this.profiler.pop();
		this.profiler.push("render");
		GlStateManager.pushMatrix();
		GlStateManager.clear(16640);
		this.fbo.bind(true);
		this.profiler.push("display");
		GlStateManager.enableTexture();
		this.profiler.pop();
		if (!this.skipGameRender) {
			this.profiler.swap("gameRenderer");
			this.gameRenderer.render(this.paused ? this.field_15871 : this.ticker.tickDelta, l);
			this.profiler.swap("toasts");
			this.field_15868.method_14490(new Window(this));
			this.profiler.pop();
		}

		this.profiler.pop();
		if (this.options.debugEnabled && this.options.debugProfilerEnabled && !this.options.hudHidden) {
			if (!this.profiler.enabled) {
				this.profiler.reset();
			}

			this.profiler.enabled = true;
			this.drawProfilerResults(n);
		} else {
			this.profiler.enabled = false;
			this.debugTime = System.nanoTime();
		}

		this.fbo.unbind();
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		this.fbo.draw(this.width, this.height);
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		this.gameRenderer.renderStreamIndicator(this.ticker.tickDelta);
		GlStateManager.popMatrix();
		this.profiler.push("root");
		this.updateDisplay();
		Thread.yield();
		this.setGlErrorMessage("Post render");
		this.fpsCounter++;
		boolean bl = this.isInSingleplayer() && this.currentScreen != null && this.currentScreen.shouldPauseGame() && !this.server.isPublished();
		if (this.paused != bl) {
			if (this.paused) {
				this.field_15871 = this.ticker.tickDelta;
			} else {
				this.ticker.tickDelta = this.field_15871;
			}

			this.paused = bl;
		}

		long o = System.nanoTime();
		this.metricsData.pushSample(o - this.nanoTime);
		this.nanoTime = o;

		while (getTime() >= this.time + 1000L) {
			currentFps = this.fpsCounter;
			this.fpsDebugString = String.format(
				"%d fps (%d chunk update%s) T: %s%s%s%s%s",
				currentFps,
				BuiltChunk.chunkUpdates,
				BuiltChunk.chunkUpdates == 1 ? "" : "s",
				(float)this.options.maxFramerate == GameOptions.Option.MAX_FPS.getMaxValue() ? "inf" : this.options.maxFramerate,
				this.options.vsync ? " vsync" : "",
				this.options.fancyGraphics ? "" : " fast",
				this.options.cloudMode == 0 ? "" : (this.options.cloudMode == 1 ? " fast-clouds" : " fancy-clouds"),
				GLX.supportsVbo() ? " vbo" : ""
			);
			BuiltChunk.chunkUpdates = 0;
			this.time += 1000L;
			this.fpsCounter = 0;
			this.snooper.addCpuInfo();
			if (!this.snooper.isActive()) {
				this.snooper.setActive();
			}
		}

		if (this.isFramerateValid()) {
			this.profiler.push("fpslimit_wait");
			Display.sync(this.getMaxFramerate());
			this.profiler.pop();
		}

		this.profiler.pop();
	}

	public void updateDisplay() {
		this.profiler.push("display_update");
		Display.update();
		this.profiler.pop();
		this.updateWindow();
	}

	protected void updateWindow() {
		if (!this.fullscreen && Display.wasResized()) {
			int i = this.width;
			int j = this.height;
			this.width = Display.getWidth();
			this.height = Display.getHeight();
			if (this.width != i || this.height != j) {
				if (this.width <= 0) {
					this.width = 1;
				}

				if (this.height <= 0) {
					this.height = 1;
				}

				this.onResolutionChanged(this.width, this.height);
			}
		}
	}

	public int getMaxFramerate() {
		return this.world == null && this.currentScreen != null ? 30 : this.options.maxFramerate;
	}

	public boolean isFramerateValid() {
		return (float)this.getMaxFramerate() < GameOptions.Option.MAX_FPS.getMaxValue();
	}

	public void cleanUpAfterCrash() {
		try {
			memoryReservedForCrash = new byte[0];
			this.worldRenderer.cleanUp();
		} catch (Throwable var3) {
		}

		try {
			System.gc();
			this.connect(null);
		} catch (Throwable var2) {
		}

		System.gc();
	}

	private void handleProfilerKeyPress(int digit) {
		List<Profiler.Section> list = this.profiler.getData(this.openProfilerSection);
		if (!list.isEmpty()) {
			Profiler.Section section = (Profiler.Section)list.remove(0);
			if (digit == 0) {
				if (!section.name.isEmpty()) {
					int i = this.openProfilerSection.lastIndexOf(46);
					if (i >= 0) {
						this.openProfilerSection = this.openProfilerSection.substring(0, i);
					}
				}
			} else {
				digit--;
				if (digit < list.size() && !"unspecified".equals(((Profiler.Section)list.get(digit)).name)) {
					if (!this.openProfilerSection.isEmpty()) {
						this.openProfilerSection = this.openProfilerSection + ".";
					}

					this.openProfilerSection = this.openProfilerSection + ((Profiler.Section)list.get(digit)).name;
				}
			}
		}
	}

	private void drawProfilerResults(long tickTimeNanos) {
		if (this.profiler.enabled) {
			List<Profiler.Section> list = this.profiler.getData(this.openProfilerSection);
			Profiler.Section section = (Profiler.Section)list.remove(0);
			GlStateManager.clear(256);
			GlStateManager.matrixMode(5889);
			GlStateManager.enableColorMaterial();
			GlStateManager.loadIdentity();
			GlStateManager.ortho(0.0, (double)this.width, (double)this.height, 0.0, 1000.0, 3000.0);
			GlStateManager.matrixMode(5888);
			GlStateManager.loadIdentity();
			GlStateManager.translate(0.0F, 0.0F, -2000.0F);
			GlStateManager.method_12304(1.0F);
			GlStateManager.disableTexture();
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			int i = 160;
			int j = this.width - 160 - 10;
			int k = this.height - 320;
			GlStateManager.enableBlend();
			bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
			bufferBuilder.vertex((double)((float)j - 176.0F), (double)((float)k - 96.0F - 16.0F), 0.0).color(200, 0, 0, 0).next();
			bufferBuilder.vertex((double)((float)j - 176.0F), (double)(k + 320), 0.0).color(200, 0, 0, 0).next();
			bufferBuilder.vertex((double)((float)j + 176.0F), (double)(k + 320), 0.0).color(200, 0, 0, 0).next();
			bufferBuilder.vertex((double)((float)j + 176.0F), (double)((float)k - 96.0F - 16.0F), 0.0).color(200, 0, 0, 0).next();
			tessellator.draw();
			GlStateManager.disableBlend();
			double d = 0.0;

			for (int l = 0; l < list.size(); l++) {
				Profiler.Section section2 = (Profiler.Section)list.get(l);
				int m = MathHelper.floor(section2.relativePercentage / 4.0) + 1;
				bufferBuilder.begin(6, VertexFormats.POSITION_COLOR);
				int n = section2.getColor();
				int o = n >> 16 & 0xFF;
				int p = n >> 8 & 0xFF;
				int q = n & 0xFF;
				bufferBuilder.vertex((double)j, (double)k, 0.0).color(o, p, q, 255).next();

				for (int r = m; r >= 0; r--) {
					float f = (float)((d + section2.relativePercentage * (double)r / (double)m) * (float) (Math.PI * 2) / 100.0);
					float g = MathHelper.sin(f) * 160.0F;
					float h = MathHelper.cos(f) * 160.0F * 0.5F;
					bufferBuilder.vertex((double)((float)j + g), (double)((float)k - h), 0.0).color(o, p, q, 255).next();
				}

				tessellator.draw();
				bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);

				for (int s = m; s >= 0; s--) {
					float t = (float)((d + section2.relativePercentage * (double)s / (double)m) * (float) (Math.PI * 2) / 100.0);
					float u = MathHelper.sin(t) * 160.0F;
					float v = MathHelper.cos(t) * 160.0F * 0.5F;
					bufferBuilder.vertex((double)((float)j + u), (double)((float)k - v), 0.0).color(o >> 1, p >> 1, q >> 1, 255).next();
					bufferBuilder.vertex((double)((float)j + u), (double)((float)k - v + 10.0F), 0.0).color(o >> 1, p >> 1, q >> 1, 255).next();
				}

				tessellator.draw();
				d += section2.relativePercentage;
			}

			DecimalFormat decimalFormat = new DecimalFormat("##0.00");
			GlStateManager.enableTexture();
			String string = "";
			if (!"unspecified".equals(section.name)) {
				string = string + "[0] ";
			}

			if (section.name.isEmpty()) {
				string = string + "ROOT ";
			} else {
				string = string + section.name + ' ';
			}

			int w = 16777215;
			this.textRenderer.drawWithShadow(string, (float)(j - 160), (float)(k - 80 - 16), 16777215);
			string = decimalFormat.format(section.absolutePercentage) + "%";
			this.textRenderer.drawWithShadow(string, (float)(j + 160 - this.textRenderer.getStringWidth(string)), (float)(k - 80 - 16), 16777215);

			for (int x = 0; x < list.size(); x++) {
				Profiler.Section section3 = (Profiler.Section)list.get(x);
				StringBuilder stringBuilder = new StringBuilder();
				if ("unspecified".equals(section3.name)) {
					stringBuilder.append("[?] ");
				} else {
					stringBuilder.append("[").append(x + 1).append("] ");
				}

				String string2 = stringBuilder.append(section3.name).toString();
				this.textRenderer.drawWithShadow(string2, (float)(j - 160), (float)(k + 80 + x * 8 + 20), section3.getColor());
				string2 = decimalFormat.format(section3.relativePercentage) + "%";
				this.textRenderer
					.drawWithShadow(string2, (float)(j + 160 - 50 - this.textRenderer.getStringWidth(string2)), (float)(k + 80 + x * 8 + 20), section3.getColor());
				string2 = decimalFormat.format(section3.absolutePercentage) + "%";
				this.textRenderer.drawWithShadow(string2, (float)(j + 160 - this.textRenderer.getStringWidth(string2)), (float)(k + 80 + x * 8 + 20), section3.getColor());
			}
		}
	}

	public void scheduleStop() {
		this.running = false;
	}

	public void closeScreen() {
		if (Display.isActive()) {
			if (!this.focused) {
				if (!IS_MAC) {
					KeyBinding.method_12137();
				}

				this.focused = true;
				this.mouse.lockMouse();
				this.setScreen(null);
				this.attackCooldown = 10000;
			}
		}
	}

	public void grabMouse() {
		if (this.focused) {
			this.focused = false;
			this.mouse.grabMouse();
		}
	}

	public void openGameMenuScreen() {
		if (this.currentScreen == null) {
			this.setScreen(new GameMenuScreen());
			if (this.isInSingleplayer() && !this.server.isPublished()) {
				this.soundManager.pauseAll();
			}
		}
	}

	private void handleBlockBreaking(boolean breaking) {
		if (!breaking) {
			this.attackCooldown = 0;
		}

		if (this.attackCooldown <= 0 && !this.player.method_13061()) {
			if (breaking && this.result != null && this.result.type == BlockHitResult.Type.BLOCK) {
				BlockPos blockPos = this.result.getBlockPos();
				if (this.world.getBlockState(blockPos).getMaterial() != Material.AIR
					&& this.interactionManager.updateBlockBreakingProgress(blockPos, this.result.direction)) {
					this.particleManager.addBlockBreakingParticles(blockPos, this.result.direction);
					this.player.swingHand(Hand.MAIN_HAND);
				}
			} else {
				this.interactionManager.cancelBlockBreaking();
			}
		}
	}

	private void doAttack() {
		if (this.attackCooldown <= 0) {
			if (this.result == null) {
				LOGGER.error("Null returned as 'hitResult', this shouldn't happen!");
				if (this.interactionManager.hasLimitedAttackSpeed()) {
					this.attackCooldown = 10;
				}
			} else if (!this.player.method_12266()) {
				switch (this.result.type) {
					case ENTITY:
						this.interactionManager.attackEntity(this.player, this.result.entity);
						break;
					case BLOCK:
						BlockPos blockPos = this.result.getBlockPos();
						if (this.world.getBlockState(blockPos).getMaterial() != Material.AIR) {
							this.interactionManager.attackBlock(blockPos, this.result.direction);
							break;
						}
					case MISS:
						if (this.interactionManager.hasLimitedAttackSpeed()) {
							this.attackCooldown = 10;
						}

						this.player.method_13269();
				}

				this.player.swingHand(Hand.MAIN_HAND);
			}
		}
	}

	private void doUse() {
		if (!this.interactionManager.isBreakingBlock()) {
			this.blockPlaceDelay = 4;
			if (!this.player.method_12266()) {
				if (this.result == null) {
					LOGGER.warn("Null returned as 'hitResult', this shouldn't happen!");
				}

				for (Hand hand : Hand.values()) {
					ItemStack itemStack = this.player.getStackInHand(hand);
					if (this.result != null) {
						switch (this.result.type) {
							case ENTITY:
								if (this.interactionManager.method_12236(this.player, this.result.entity, this.result, hand) == ActionResult.SUCCESS) {
									return;
								}

								if (this.interactionManager.method_12235(this.player, this.result.entity, hand) == ActionResult.SUCCESS) {
									return;
								}
								break;
							case BLOCK:
								BlockPos blockPos = this.result.getBlockPos();
								if (this.world.getBlockState(blockPos).getMaterial() != Material.AIR) {
									int i = itemStack.getCount();
									ActionResult actionResult = this.interactionManager.method_13842(this.player, this.world, blockPos, this.result.direction, this.result.pos, hand);
									if (actionResult == ActionResult.SUCCESS) {
										this.player.swingHand(hand);
										if (!itemStack.isEmpty() && (itemStack.getCount() != i || this.interactionManager.hasCreativeInventory())) {
											this.gameRenderer.firstPersonRenderer.method_12330(hand);
										}

										return;
									}
								}
						}
					}

					if (!itemStack.isEmpty() && this.interactionManager.method_12234(this.player, this.world, hand) == ActionResult.SUCCESS) {
						this.gameRenderer.firstPersonRenderer.method_12330(hand);
						return;
					}
				}
			}
		}
	}

	public void toggleFullscreen() {
		try {
			this.fullscreen = !this.fullscreen;
			this.options.fullscreen = this.fullscreen;
			if (this.fullscreen) {
				this.updateDisplayMode();
				this.width = Display.getDisplayMode().getWidth();
				this.height = Display.getDisplayMode().getHeight();
				if (this.width <= 0) {
					this.width = 1;
				}

				if (this.height <= 0) {
					this.height = 1;
				}
			} else {
				Display.setDisplayMode(new DisplayMode(this.tempWidth, this.tempHeight));
				this.width = this.tempWidth;
				this.height = this.tempHeight;
				if (this.width <= 0) {
					this.width = 1;
				}

				if (this.height <= 0) {
					this.height = 1;
				}
			}

			if (this.currentScreen != null) {
				this.onResolutionChanged(this.width, this.height);
			} else {
				this.resizeFramebuffer();
			}

			Display.setFullscreen(this.fullscreen);
			Display.setVSyncEnabled(this.options.vsync);
			this.updateDisplay();
		} catch (Exception var2) {
			LOGGER.error("Couldn't toggle fullscreen", var2);
		}
	}

	private void onResolutionChanged(int width, int height) {
		this.width = Math.max(1, width);
		this.height = Math.max(1, height);
		if (this.currentScreen != null) {
			Window window = new Window(this);
			this.currentScreen.resize(this, window.getWidth(), window.getHeight());
		}

		this.loadingScreenRenderer = new LoadingScreenRenderer(this);
		this.resizeFramebuffer();
	}

	private void resizeFramebuffer() {
		this.fbo.resize(this.width, this.height);
		if (this.gameRenderer != null) {
			this.gameRenderer.onResized(this.width, this.height);
		}
	}

	public MusicTracker getMusicTracker() {
		return this.musicTracker;
	}

	public void tick() {
		if (this.blockPlaceDelay > 0) {
			this.blockPlaceDelay--;
		}

		this.profiler.push("gui");
		if (!this.paused) {
			this.inGameHud.tick();
		}

		this.profiler.pop();
		this.gameRenderer.updateTargetedEntity(1.0F);
		this.field_15869.method_14721(this.world, this.result);
		this.profiler.push("gameMode");
		if (!this.paused && this.world != null) {
			this.interactionManager.tick();
		}

		this.profiler.swap("textures");
		if (this.world != null) {
			this.textureManager.tick();
		}

		if (this.currentScreen == null && this.player != null) {
			if (this.player.getHealth() <= 0.0F && !(this.currentScreen instanceof DeathScreen)) {
				this.setScreen(null);
			} else if (this.player.isSleeping() && this.world != null) {
				this.setScreen(new SleepingChatScreen());
			}
		} else if (this.currentScreen != null && this.currentScreen instanceof SleepingChatScreen && !this.player.isSleeping()) {
			this.setScreen(null);
		}

		if (this.currentScreen != null) {
			this.attackCooldown = 10000;
		}

		if (this.currentScreen != null) {
			try {
				this.currentScreen.handleInput();
			} catch (Throwable var5) {
				CrashReport crashReport = CrashReport.create(var5, "Updating screen events");
				CrashReportSection crashReportSection = crashReport.addElement("Affected screen");
				crashReportSection.add("Screen name", new CrashCallable<String>() {
					public String call() throws Exception {
						return MinecraftClient.this.currentScreen.getClass().getCanonicalName();
					}
				});
				throw new CrashException(crashReport);
			}

			if (this.currentScreen != null) {
				try {
					this.currentScreen.tick();
				} catch (Throwable var4) {
					CrashReport crashReport2 = CrashReport.create(var4, "Ticking screen");
					CrashReportSection crashReportSection2 = crashReport2.addElement("Affected screen");
					crashReportSection2.add("Screen name", new CrashCallable<String>() {
						public String call() throws Exception {
							return MinecraftClient.this.currentScreen.getClass().getCanonicalName();
						}
					});
					throw new CrashException(crashReport2);
				}
			}
		}

		if (this.currentScreen == null || this.currentScreen.passEvents) {
			this.profiler.swap("mouse");
			this.tickMouse();
			if (this.attackCooldown > 0) {
				this.attackCooldown--;
			}

			this.profiler.swap("keyboard");
			this.tickKeyboard();
		}

		if (this.world != null) {
			if (this.player != null) {
				this.joinPlayerCounter++;
				if (this.joinPlayerCounter == 30) {
					this.joinPlayerCounter = 0;
					this.world.loadEntity(this.player);
				}
			}

			this.profiler.swap("gameRenderer");
			if (!this.paused) {
				this.gameRenderer.tick();
			}

			this.profiler.swap("levelRenderer");
			if (!this.paused) {
				this.worldRenderer.tick();
			}

			this.profiler.swap("level");
			if (!this.paused) {
				if (this.world.getLightningTicksLeft() > 0) {
					this.world.setLightningTicksLeft(this.world.getLightningTicksLeft() - 1);
				}

				this.world.tickEntities();
			}
		} else if (this.gameRenderer.areShadersSupported()) {
			this.gameRenderer.disableShader();
		}

		if (!this.paused) {
			this.musicTracker.tick();
			this.soundManager.tick();
		}

		if (this.world != null) {
			if (!this.paused) {
				this.world.setMobSpawning(this.world.getGlobalDifficulty() != Difficulty.PEACEFUL, true);
				this.field_15869.method_14728();

				try {
					this.world.tick();
				} catch (Throwable var6) {
					CrashReport crashReport3 = CrashReport.create(var6, "Exception in world tick");
					if (this.world == null) {
						CrashReportSection crashReportSection3 = crashReport3.addElement("Affected level");
						crashReportSection3.add("Problem", "Level is null!");
					} else {
						this.world.addToCrashReport(crashReport3);
					}

					throw new CrashException(crashReport3);
				}
			}

			this.profiler.swap("animateTick");
			if (!this.paused && this.world != null) {
				this.world.spawnRandomParticles(MathHelper.floor(this.player.x), MathHelper.floor(this.player.y), MathHelper.floor(this.player.z));
			}

			this.profiler.swap("particles");
			if (!this.paused) {
				this.particleManager.tick();
			}
		} else if (this.clientConnection != null) {
			this.profiler.swap("pendingConnection");
			this.clientConnection.tick();
		}

		this.profiler.pop();
		this.sysTime = getTime();
	}

	private void tickKeyboard() {
		while (Keyboard.next()) {
			int i = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();
			if (this.f3CTime > 0L) {
				if (getTime() - this.f3CTime >= 6000L) {
					throw new CrashException(new CrashReport("Manually triggered debug crash", new Throwable()));
				}

				if (!Keyboard.isKeyDown(46) || !Keyboard.isKeyDown(61)) {
					this.f3CTime = -1L;
				}
			} else if (Keyboard.isKeyDown(46) && Keyboard.isKeyDown(61)) {
				this.field_13280 = true;
				this.f3CTime = getTime();
			}

			this.handleKeyInput();
			if (this.currentScreen != null) {
				this.currentScreen.handleKeyboard();
			}

			boolean bl = Keyboard.getEventKeyState();
			if (bl) {
				if (i == 62 && this.gameRenderer != null) {
					this.gameRenderer.toggleShadersEnabled();
				}

				boolean bl2 = false;
				if (this.currentScreen == null) {
					if (i == 1) {
						this.openGameMenuScreen();
					}

					bl2 = Keyboard.isKeyDown(61) && this.method_12146(i);
					this.field_13280 |= bl2;
					if (i == 59) {
						this.options.hudHidden = !this.options.hudHidden;
					}
				}

				if (bl2) {
					KeyBinding.setKeyPressed(i, false);
				} else {
					KeyBinding.setKeyPressed(i, true);
					KeyBinding.onKeyPressed(i);
				}

				if (this.options.debugProfilerEnabled) {
					if (i == 11) {
						this.handleProfilerKeyPress(0);
					}

					for (int j = 0; j < 9; j++) {
						if (i == 2 + j) {
							this.handleProfilerKeyPress(j + 1);
						}
					}
				}
			} else {
				KeyBinding.setKeyPressed(i, false);
				if (i == 61) {
					if (this.field_13280) {
						this.field_13280 = false;
					} else {
						this.options.debugEnabled = !this.options.debugEnabled;
						this.options.debugProfilerEnabled = this.options.debugEnabled && Screen.hasShiftDown();
						this.options.debugFpsEnabled = this.options.debugEnabled && Screen.hasAltDown();
					}
				}
			}
		}

		this.method_12140();
	}

	private boolean method_12146(int key) {
		if (key == 30) {
			this.worldRenderer.reload();
			this.addDebugMessage("debug.reload_chunks.message");
			return true;
		} else if (key == 48) {
			boolean bl = !this.entityRenderDispatcher.getRenderHitboxes();
			this.entityRenderDispatcher.setRenderHitboxes(bl);
			this.addDebugMessage(bl ? "debug.show_hitboxes.on" : "debug.show_hitboxes.off");
			return true;
		} else if (key == 32) {
			if (this.inGameHud != null) {
				this.inGameHud.getChatHud().clear(false);
			}

			return true;
		} else if (key == 33) {
			this.options.getBooleanValue(GameOptions.Option.RENDER_DISTANCE, Screen.hasShiftDown() ? -1 : 1);
			this.addDebugMessage("debug.cycle_renderdistance.message", this.options.viewDistance);
			return true;
		} else if (key == 34) {
			boolean bl2 = this.debugRenderer.toggleChunkBorders();
			this.addDebugMessage(bl2 ? "debug.chunk_boundaries.on" : "debug.chunk_boundaries.off");
			return true;
		} else if (key == 35) {
			this.options.advancedItemTooltips = !this.options.advancedItemTooltips;
			this.addDebugMessage(this.options.advancedItemTooltips ? "debug.advanced_tooltips.on" : "debug.advanced_tooltips.off");
			this.options.save();
			return true;
		} else if (key == 49) {
			if (!this.player.canUseCommand(2, "")) {
				this.addDebugMessage("debug.creative_spectator.error");
			} else if (this.player.isCreative()) {
				this.player.sendChatMessage("/gamemode spectator");
			} else if (this.player.isSpectator()) {
				this.player.sendChatMessage("/gamemode creative");
			}

			return true;
		} else if (key == 25) {
			this.options.pauseOnLostFocus = !this.options.pauseOnLostFocus;
			this.options.save();
			this.addDebugMessage(this.options.pauseOnLostFocus ? "debug.pause_focus.on" : "debug.pause_focus.off");
			return true;
		} else if (key == 16) {
			this.addDebugMessage("debug.help.message");
			ChatHud chatHud = this.inGameHud.getChatHud();
			chatHud.addMessage(new TranslatableText("debug.reload_chunks.help"));
			chatHud.addMessage(new TranslatableText("debug.show_hitboxes.help"));
			chatHud.addMessage(new TranslatableText("debug.clear_chat.help"));
			chatHud.addMessage(new TranslatableText("debug.cycle_renderdistance.help"));
			chatHud.addMessage(new TranslatableText("debug.chunk_boundaries.help"));
			chatHud.addMessage(new TranslatableText("debug.advanced_tooltips.help"));
			chatHud.addMessage(new TranslatableText("debug.creative_spectator.help"));
			chatHud.addMessage(new TranslatableText("debug.pause_focus.help"));
			chatHud.addMessage(new TranslatableText("debug.help.help"));
			chatHud.addMessage(new TranslatableText("debug.reload_resourcepacks.help"));
			return true;
		} else if (key == 20) {
			this.addDebugMessage("debug.reload_resourcepacks.message");
			this.reloadResources();
			return true;
		} else {
			return false;
		}
	}

	private void method_12140() {
		while (this.options.togglePerspectiveKey.wasPressed()) {
			this.options.perspective++;
			if (this.options.perspective > 2) {
				this.options.perspective = 0;
			}

			if (this.options.perspective == 0) {
				this.gameRenderer.onCameraEntitySet(this.getCameraEntity());
			} else if (this.options.perspective == 1) {
				this.gameRenderer.onCameraEntitySet(null);
			}

			this.worldRenderer.scheduleTerrainUpdate();
		}

		while (this.options.smoothCameraKey.wasPressed()) {
			this.options.smoothCameraEnabled = !this.options.smoothCameraEnabled;
		}

		for (int i = 0; i < 9; i++) {
			boolean bl = this.options.field_15881.isPressed();
			boolean bl2 = this.options.field_15882.isPressed();
			if (this.options.hotbarKeys[i].wasPressed()) {
				if (this.player.isSpectator()) {
					this.inGameHud.getSpectatorHud().selectSlot(i);
				} else if (!this.player.isCreative() || this.currentScreen != null || !bl2 && !bl) {
					this.player.inventory.selectedSlot = i;
				} else {
					CreativeInventoryScreen.method_14550(this, i, bl2, bl);
				}
			}
		}

		while (this.options.inventoryKey.wasPressed()) {
			if (this.interactionManager.hasRidingInventory()) {
				this.player.openRidingInventory();
			} else {
				this.field_15869.method_14718();
				this.setScreen(new SurvivalInventoryScreen(this.player));
			}
		}

		while (this.options.field_15880.wasPressed()) {
			this.setScreen(new AdvancementsScreen(this.player.networkHandler.method_14672()));
		}

		while (this.options.streamCommercialKey.wasPressed()) {
			if (!this.player.isSpectator()) {
				this.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_HELD_ITEMS, BlockPos.ORIGIN, Direction.DOWN));
			}
		}

		while (this.options.dropKey.wasPressed()) {
			if (!this.player.isSpectator()) {
				this.player.dropSelectedItem(Screen.hasControlDown());
			}
		}

		boolean bl3 = this.options.chatVisibilityType != PlayerEntity.ChatVisibilityType.HIDDEN;
		if (bl3) {
			while (this.options.chatKey.wasPressed()) {
				this.setScreen(new ChatScreen());
			}

			if (this.currentScreen == null && this.options.commandKey.wasPressed()) {
				this.setScreen(new ChatScreen("/"));
			}
		}

		if (this.player.method_13061()) {
			if (!this.options.useKey.isPressed()) {
				this.interactionManager.stopUsingItem(this.player);
			}

			while (this.options.attackKey.wasPressed()) {
			}

			while (this.options.useKey.wasPressed()) {
			}

			while (this.options.pickItemKey.wasPressed()) {
			}
		} else {
			while (this.options.attackKey.wasPressed()) {
				this.doAttack();
			}

			while (this.options.useKey.wasPressed()) {
				this.doUse();
			}

			while (this.options.pickItemKey.wasPressed()) {
				this.doPick();
			}
		}

		if (this.options.useKey.isPressed() && this.blockPlaceDelay == 0 && !this.player.method_13061()) {
			this.doUse();
		}

		this.handleBlockBreaking(this.currentScreen == null && this.options.attackKey.isPressed() && this.focused);
	}

	private void tickMouse() {
		while (Mouse.next()) {
			int i = Mouse.getEventButton();
			KeyBinding.setKeyPressed(i - 100, Mouse.getEventButtonState());
			if (Mouse.getEventButtonState()) {
				if (this.player.isSpectator() && i == 2) {
					this.inGameHud.getSpectatorHud().useSelectedCommand();
				} else {
					KeyBinding.onKeyPressed(i - 100);
				}
			}

			long l = getTime() - this.sysTime;
			if (l <= 200L) {
				int j = Mouse.getEventDWheel();
				if (j != 0) {
					if (this.player.isSpectator()) {
						j = j < 0 ? -1 : 1;
						if (this.inGameHud.getSpectatorHud().isOpen()) {
							this.inGameHud.getSpectatorHud().cycleSlot(-j);
						} else {
							float f = MathHelper.clamp(this.player.abilities.getFlySpeed() + (float)j * 0.005F, 0.0F, 0.2F);
							this.player.abilities.setFlySpeed(f);
						}
					} else {
						this.player.inventory.scrollInHotbar(j);
					}
				}

				if (this.currentScreen == null) {
					if (!this.focused && Mouse.getEventButtonState()) {
						this.closeScreen();
					}
				} else if (this.currentScreen != null) {
					this.currentScreen.handleMouse();
				}
			}
		}
	}

	private void addDebugMessage(String key, Object... args) {
		this.inGameHud
			.getChatHud()
			.addMessage(
				new LiteralText("")
					.append(new TranslatableText("debug.prefix").setStyle(new Style().setFormatting(Formatting.YELLOW).setBold(true)))
					.append(" ")
					.append(new TranslatableText(key, args))
			);
	}

	public void startIntegratedServer(String worldName, String levelName, @Nullable LevelInfo levelInfo) {
		this.connect(null);
		System.gc();
		SaveHandler saveHandler = this.currentSave.createSaveHandler(worldName, false);
		LevelProperties levelProperties = saveHandler.getLevelProperties();
		if (levelProperties == null && levelInfo != null) {
			levelProperties = new LevelProperties(levelInfo, worldName);
			saveHandler.saveWorld(levelProperties);
		}

		if (levelInfo == null) {
			levelInfo = new LevelInfo(levelProperties);
		}

		try {
			YggdrasilAuthenticationService yggdrasilAuthenticationService = new YggdrasilAuthenticationService(this.networkProxy, UUID.randomUUID().toString());
			MinecraftSessionService minecraftSessionService = yggdrasilAuthenticationService.createMinecraftSessionService();
			GameProfileRepository gameProfileRepository = yggdrasilAuthenticationService.createProfileRepository();
			UserCache userCache = new UserCache(gameProfileRepository, new File(this.runDirectory, MinecraftServer.USER_CACHE_FILE.getName()));
			SkullBlockEntity.method_11666(userCache);
			SkullBlockEntity.method_11665(minecraftSessionService);
			UserCache.setUseRemote(false);
			this.server = new IntegratedServer(
				this, worldName, levelName, levelInfo, yggdrasilAuthenticationService, minecraftSessionService, gameProfileRepository, userCache
			);
			this.server.startServerThread();
			this.isIntegratedServerRunning = true;
		} catch (Throwable var11) {
			CrashReport crashReport = CrashReport.create(var11, "Starting integrated server");
			CrashReportSection crashReportSection = crashReport.addElement("Starting integrated server");
			crashReportSection.add("Level ID", worldName);
			crashReportSection.add("Level Name", levelName);
			throw new CrashException(crashReport);
		}

		this.loadingScreenRenderer.setTitle(I18n.translate("menu.loadingLevel"));

		while (!this.server.isLoading()) {
			String string = this.server.getServerOperation();
			if (string != null) {
				this.loadingScreenRenderer.setTask(I18n.translate(string));
			} else {
				this.loadingScreenRenderer.setTask("");
			}

			try {
				Thread.sleep(200L);
			} catch (InterruptedException var10) {
			}
		}

		this.setScreen(new ProgressScreen());
		SocketAddress socketAddress = this.server.getNetworkIo().bindLocal();
		ClientConnection clientConnection = ClientConnection.connectLocal(socketAddress);
		clientConnection.setPacketListener(new ClientLoginNetworkHandler(clientConnection, this, null));
		clientConnection.send(new HandshakeC2SPacket(socketAddress.toString(), 0, NetworkState.LOGIN));
		clientConnection.send(new LoginHelloC2SPacket(this.getSession().getProfile()));
		this.clientConnection = clientConnection;
	}

	public void connect(@Nullable ClientWorld world) {
		this.connect(world, "");
	}

	public void connect(@Nullable ClientWorld world, String loadingMessage) {
		if (world == null) {
			ClientPlayNetworkHandler clientPlayNetworkHandler = this.getNetworkHandler();
			if (clientPlayNetworkHandler != null) {
				clientPlayNetworkHandler.clearWorld();
			}

			if (this.server != null && this.server.hasGameDir()) {
				this.server.stopRunning();
			}

			this.server = null;
			this.gameRenderer.method_13848();
			this.interactionManager = null;
			class_3253.field_15887.method_14475();
		}

		this.cameraEntity = null;
		this.clientConnection = null;
		if (this.loadingScreenRenderer != null) {
			this.loadingScreenRenderer.setTitleAndTask(loadingMessage);
			this.loadingScreenRenderer.setTask("");
		}

		if (world == null && this.world != null) {
			this.loader.clear();
			this.inGameHud.resetDebugHudChunk();
			this.setCurrentServerEntry(null);
			this.isIntegratedServerRunning = false;
		}

		this.soundManager.stopAll();
		this.world = world;
		if (this.worldRenderer != null) {
			this.worldRenderer.setWorld(world);
		}

		if (this.particleManager != null) {
			this.particleManager.setWorld(world);
		}

		BlockEntityRenderDispatcher.INSTANCE.setWorld(world);
		if (world != null) {
			if (!this.isIntegratedServerRunning) {
				AuthenticationService authenticationService = new YggdrasilAuthenticationService(this.networkProxy, UUID.randomUUID().toString());
				MinecraftSessionService minecraftSessionService = authenticationService.createMinecraftSessionService();
				GameProfileRepository gameProfileRepository = authenticationService.createProfileRepository();
				UserCache userCache = new UserCache(gameProfileRepository, new File(this.runDirectory, MinecraftServer.USER_CACHE_FILE.getName()));
				SkullBlockEntity.method_11666(userCache);
				SkullBlockEntity.method_11665(minecraftSessionService);
				UserCache.setUseRemote(false);
			}

			if (this.player == null) {
				this.player = this.interactionManager.method_9658(world, new StatHandler(), new class_3320());
				this.interactionManager.flipPlayer(this.player);
			}

			this.player.afterSpawn();
			world.spawnEntity(this.player);
			this.player.input = new KeyboardInput(this.options);
			this.interactionManager.copyAbilities(this.player);
			this.cameraEntity = this.player;
		} else {
			this.currentSave.clearAll();
			this.player = null;
		}

		System.gc();
		this.sysTime = 0L;
	}

	public void setDimensionAndSpawn(int dimension) {
		this.world.setDefaultSpawnClient();
		this.world.clearEntities();
		int i = 0;
		String string = null;
		if (this.player != null) {
			i = this.player.getEntityId();
			this.world.removeEntity(this.player);
			string = this.player.getServerBrand();
		}

		this.cameraEntity = null;
		ClientPlayerEntity clientPlayerEntity = this.player;
		this.player = this.interactionManager
			.method_9658(
				this.world, this.player == null ? new StatHandler() : this.player.getStatHandler(), this.player == null ? new class_3355() : this.player.method_14675()
			);
		this.player.getDataTracker().writeUpdatedEntries(clientPlayerEntity.getDataTracker().getEntries());
		this.player.dimension = dimension;
		this.cameraEntity = this.player;
		this.player.afterSpawn();
		this.player.setServerBrand(string);
		this.world.spawnEntity(this.player);
		this.interactionManager.flipPlayer(this.player);
		this.player.input = new KeyboardInput(this.options);
		this.player.setEntityId(i);
		this.interactionManager.copyAbilities(this.player);
		this.player.setReducedDebugInfo(clientPlayerEntity.getReducedDebugInfo());
		if (this.currentScreen instanceof DeathScreen) {
			this.setScreen(null);
		}
	}

	public final boolean isDemo() {
		return this.isDemo;
	}

	@Nullable
	public ClientPlayNetworkHandler getNetworkHandler() {
		return this.player == null ? null : this.player.networkHandler;
	}

	public static boolean isHudEnabled() {
		return instance == null || !instance.options.hudHidden;
	}

	public static boolean isFancyGraphicsEnabled() {
		return instance != null && instance.options.fancyGraphics;
	}

	public static boolean isAmbientOcclusionEnabled() {
		return instance != null && instance.options.ao != 0;
	}

	private void doPick() {
		if (this.result != null && this.result.type != BlockHitResult.Type.MISS) {
			boolean bl = this.player.abilities.creativeMode;
			BlockEntity blockEntity = null;
			ItemStack itemStack;
			if (this.result.type == BlockHitResult.Type.BLOCK) {
				BlockPos blockPos = this.result.getBlockPos();
				BlockState blockState = this.world.getBlockState(blockPos);
				Block block = blockState.getBlock();
				if (blockState.getMaterial() == Material.AIR) {
					return;
				}

				itemStack = block.getItemStack(this.world, blockPos, blockState);
				if (itemStack.isEmpty()) {
					return;
				}

				if (bl && Screen.hasControlDown() && block.hasBlockEntity()) {
					blockEntity = this.world.getBlockEntity(blockPos);
				}
			} else {
				if (this.result.type != BlockHitResult.Type.ENTITY || this.result.entity == null || !bl) {
					return;
				}

				if (this.result.entity instanceof PaintingEntity) {
					itemStack = new ItemStack(Items.PAINTING);
				} else if (this.result.entity instanceof LeashKnotEntity) {
					itemStack = new ItemStack(Items.LEAD);
				} else if (this.result.entity instanceof ItemFrameEntity) {
					ItemFrameEntity itemFrameEntity = (ItemFrameEntity)this.result.entity;
					ItemStack itemStack4 = itemFrameEntity.getHeldItemStack();
					if (itemStack4.isEmpty()) {
						itemStack = new ItemStack(Items.ITEM_FRAME);
					} else {
						itemStack = itemStack4.copy();
					}
				} else if (this.result.entity instanceof AbstractMinecartEntity) {
					AbstractMinecartEntity abstractMinecartEntity = (AbstractMinecartEntity)this.result.entity;
					Item item;
					switch (abstractMinecartEntity.getMinecartType()) {
						case FURNACE:
							item = Items.MINECART_WITH_FURNACE;
							break;
						case CHEST:
							item = Items.MINECART_WITH_CHEST;
							break;
						case TNT:
							item = Items.MINECART_WITH_TNT;
							break;
						case HOPPER:
							item = Items.MINECART_WITH_HOPPER;
							break;
						case COMMAND_BLOCK:
							item = Items.MINECART_WITH_COMMAND_BLOCK;
							break;
						default:
							item = Items.MINECART;
					}

					itemStack = new ItemStack(item);
				} else if (this.result.entity instanceof BoatEntity) {
					itemStack = new ItemStack(((BoatEntity)this.result.entity).asItem());
				} else if (this.result.entity instanceof ArmorStandEntity) {
					itemStack = new ItemStack(Items.ARMOR_STAND);
				} else if (this.result.entity instanceof EndCrystalEntity) {
					itemStack = new ItemStack(Items.END_CRYSTAL);
				} else {
					Identifier identifier = EntityType.getId(this.result.entity);
					if (identifier == null || !EntityType.SPAWN_EGGS.containsKey(identifier)) {
						return;
					}

					itemStack = new ItemStack(Items.SPAWN_EGG);
					SpawnEggItem.setEggEntity(itemStack, identifier);
				}
			}

			if (itemStack.isEmpty()) {
				String string = "";
				if (this.result.type == BlockHitResult.Type.BLOCK) {
					string = Block.REGISTRY.getIdentifier(this.world.getBlockState(this.result.getBlockPos()).getBlock()).toString();
				} else if (this.result.type == BlockHitResult.Type.ENTITY) {
					string = EntityType.getId(this.result.entity).toString();
				}

				LOGGER.warn("Picking on: [{}] {} gave null item", this.result.type, string);
			} else {
				PlayerInventory playerInventory = this.player.inventory;
				if (blockEntity != null) {
					this.method_12138(itemStack, blockEntity);
				}

				int i = playerInventory.method_13253(itemStack);
				if (bl) {
					playerInventory.method_13250(itemStack);
					this.interactionManager.clickCreativeStack(this.player.getStackInHand(Hand.MAIN_HAND), 36 + playerInventory.selectedSlot);
				} else if (i != -1) {
					if (PlayerInventory.method_13258(i)) {
						playerInventory.selectedSlot = i;
					} else {
						this.interactionManager.method_12231(i);
					}
				}
			}
		}
	}

	private ItemStack method_12138(ItemStack itemStack, BlockEntity blockEntity) {
		NbtCompound nbtCompound = blockEntity.toNbt(new NbtCompound());
		if (itemStack.getItem() == Items.SKULL && nbtCompound.contains("Owner")) {
			NbtCompound nbtCompound2 = nbtCompound.getCompound("Owner");
			NbtCompound nbtCompound3 = new NbtCompound();
			nbtCompound3.put("SkullOwner", nbtCompound2);
			itemStack.setNbt(nbtCompound3);
			return itemStack;
		} else {
			itemStack.putSubNbt("BlockEntityTag", nbtCompound);
			NbtCompound nbtCompound4 = new NbtCompound();
			NbtList nbtList = new NbtList();
			nbtList.add(new NbtString("(+NBT)"));
			nbtCompound4.put("Lore", nbtList);
			itemStack.putSubNbt("display", nbtCompound4);
			return itemStack;
		}
	}

	public CrashReport addSystemDetailsToCrashReport(CrashReport crashReport) {
		crashReport.getSystemDetailsSection().add("Launched Version", new CrashCallable<String>() {
			public String call() {
				return MinecraftClient.this.gameVersion;
			}
		});
		crashReport.getSystemDetailsSection().add("LWJGL", new CrashCallable<String>() {
			public String call() {
				return Sys.getVersion();
			}
		});
		crashReport.getSystemDetailsSection().add("OpenGL", new CrashCallable<String>() {
			public String call() {
				return GlStateManager.method_12320(7937) + " GL version " + GlStateManager.method_12320(7938) + ", " + GlStateManager.method_12320(7936);
			}
		});
		crashReport.getSystemDetailsSection().add("GL Caps", new CrashCallable<String>() {
			public String call() {
				return GLX.getContextDescription();
			}
		});
		crashReport.getSystemDetailsSection().add("Using VBOs", new CrashCallable<String>() {
			public String call() {
				return MinecraftClient.this.options.vbo ? "Yes" : "No";
			}
		});
		crashReport.getSystemDetailsSection()
			.add(
				"Is Modded",
				new CrashCallable<String>() {
					public String call() throws Exception {
						String string = ClientBrandRetriever.getClientModName();
						if (!"vanilla".equals(string)) {
							return "Definitely; Client brand changed to '" + string + "'";
						} else {
							return MinecraftClient.class.getSigners() == null
								? "Very likely; Jar signature invalidated"
								: "Probably not. Jar signature remains and client brand is untouched.";
						}
					}
				}
			);
		crashReport.getSystemDetailsSection().add("Type", new CrashCallable<String>() {
			public String call() throws Exception {
				return "Client (map_client.txt)";
			}
		});
		crashReport.getSystemDetailsSection().add("Resource Packs", new CrashCallable<String>() {
			public String call() throws Exception {
				StringBuilder stringBuilder = new StringBuilder();

				for (String string : MinecraftClient.this.options.resourcePacks) {
					if (stringBuilder.length() > 0) {
						stringBuilder.append(", ");
					}

					stringBuilder.append(string);
					if (MinecraftClient.this.options.incompatibleResourcePacks.contains(string)) {
						stringBuilder.append(" (incompatible)");
					}
				}

				return stringBuilder.toString();
			}
		});
		crashReport.getSystemDetailsSection().add("Current Language", new CrashCallable<String>() {
			public String call() throws Exception {
				return MinecraftClient.this.languageManager.getLanguage().toString();
			}
		});
		crashReport.getSystemDetailsSection().add("Profiler Position", new CrashCallable<String>() {
			public String call() throws Exception {
				return MinecraftClient.this.profiler.enabled ? MinecraftClient.this.profiler.getCurrentLocation() : "N/A (disabled)";
			}
		});
		crashReport.getSystemDetailsSection().add("CPU", new CrashCallable<String>() {
			public String call() {
				return GLX.getProcessor();
			}
		});
		if (this.world != null) {
			this.world.addToCrashReport(crashReport);
		}

		return crashReport;
	}

	public static MinecraftClient getInstance() {
		return instance;
	}

	public ListenableFuture<Object> reloadResourcesConcurrently() {
		return this.submit(new Runnable() {
			public void run() {
				MinecraftClient.this.reloadResources();
			}
		});
	}

	@Override
	public void addSnooperInfo(Snooper snooper) {
		snooper.addGameInfo("fps", currentFps);
		snooper.addGameInfo("vsync_enabled", this.options.vsync);
		snooper.addGameInfo("display_frequency", Display.getDisplayMode().getFrequency());
		snooper.addGameInfo("display_type", this.fullscreen ? "fullscreen" : "windowed");
		snooper.addGameInfo("run_time", (MinecraftServer.getTimeMillis() - snooper.getStartTime()) / 60L * 1000L);
		snooper.addGameInfo("current_action", this.getCurrentAction());
		snooper.addGameInfo("language", this.options.language == null ? "en_us" : this.options.language);
		String string = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? "little" : "big";
		snooper.addGameInfo("endianness", string);
		snooper.addGameInfo("subtitles", this.options.field_13292);
		snooper.addGameInfo("touch", this.options.touchscreen ? "touch" : "mouse");
		snooper.addGameInfo("resource_packs", this.loader.getSelectedResourcePacks().size());
		int i = 0;

		for (ResourcePackLoader.Entry entry : this.loader.getSelectedResourcePacks()) {
			snooper.addGameInfo("resource_pack[" + i++ + "]", entry.getName());
		}

		if (this.server != null && this.server.getSnooper() != null) {
			snooper.addGameInfo("snooper_partner", this.server.getSnooper().getSnooperToken());
		}
	}

	private String getCurrentAction() {
		if (this.server != null) {
			return this.server.isPublished() ? "hosting_lan" : "singleplayer";
		} else if (this.currentServerEntry != null) {
			return this.currentServerEntry.isLocal() ? "playing_lan" : "multiplayer";
		} else {
			return "out_of_game";
		}
	}

	@Override
	public void addSnooper(Snooper snooper) {
		snooper.addSystemInfo("opengl_version", GlStateManager.method_12320(7938));
		snooper.addSystemInfo("opengl_vendor", GlStateManager.method_12320(7936));
		snooper.addSystemInfo("client_brand", ClientBrandRetriever.getClientModName());
		snooper.addSystemInfo("launched_version", this.gameVersion);
		ContextCapabilities contextCapabilities = GLContext.getCapabilities();
		snooper.addSystemInfo("gl_caps[ARB_arrays_of_arrays]", contextCapabilities.GL_ARB_arrays_of_arrays);
		snooper.addSystemInfo("gl_caps[ARB_base_instance]", contextCapabilities.GL_ARB_base_instance);
		snooper.addSystemInfo("gl_caps[ARB_blend_func_extended]", contextCapabilities.GL_ARB_blend_func_extended);
		snooper.addSystemInfo("gl_caps[ARB_clear_buffer_object]", contextCapabilities.GL_ARB_clear_buffer_object);
		snooper.addSystemInfo("gl_caps[ARB_color_buffer_float]", contextCapabilities.GL_ARB_color_buffer_float);
		snooper.addSystemInfo("gl_caps[ARB_compatibility]", contextCapabilities.GL_ARB_compatibility);
		snooper.addSystemInfo("gl_caps[ARB_compressed_texture_pixel_storage]", contextCapabilities.GL_ARB_compressed_texture_pixel_storage);
		snooper.addSystemInfo("gl_caps[ARB_compute_shader]", contextCapabilities.GL_ARB_compute_shader);
		snooper.addSystemInfo("gl_caps[ARB_copy_buffer]", contextCapabilities.GL_ARB_copy_buffer);
		snooper.addSystemInfo("gl_caps[ARB_copy_image]", contextCapabilities.GL_ARB_copy_image);
		snooper.addSystemInfo("gl_caps[ARB_depth_buffer_float]", contextCapabilities.GL_ARB_depth_buffer_float);
		snooper.addSystemInfo("gl_caps[ARB_compute_shader]", contextCapabilities.GL_ARB_compute_shader);
		snooper.addSystemInfo("gl_caps[ARB_copy_buffer]", contextCapabilities.GL_ARB_copy_buffer);
		snooper.addSystemInfo("gl_caps[ARB_copy_image]", contextCapabilities.GL_ARB_copy_image);
		snooper.addSystemInfo("gl_caps[ARB_depth_buffer_float]", contextCapabilities.GL_ARB_depth_buffer_float);
		snooper.addSystemInfo("gl_caps[ARB_depth_clamp]", contextCapabilities.GL_ARB_depth_clamp);
		snooper.addSystemInfo("gl_caps[ARB_depth_texture]", contextCapabilities.GL_ARB_depth_texture);
		snooper.addSystemInfo("gl_caps[ARB_draw_buffers]", contextCapabilities.GL_ARB_draw_buffers);
		snooper.addSystemInfo("gl_caps[ARB_draw_buffers_blend]", contextCapabilities.GL_ARB_draw_buffers_blend);
		snooper.addSystemInfo("gl_caps[ARB_draw_elements_base_vertex]", contextCapabilities.GL_ARB_draw_elements_base_vertex);
		snooper.addSystemInfo("gl_caps[ARB_draw_indirect]", contextCapabilities.GL_ARB_draw_indirect);
		snooper.addSystemInfo("gl_caps[ARB_draw_instanced]", contextCapabilities.GL_ARB_draw_instanced);
		snooper.addSystemInfo("gl_caps[ARB_explicit_attrib_location]", contextCapabilities.GL_ARB_explicit_attrib_location);
		snooper.addSystemInfo("gl_caps[ARB_explicit_uniform_location]", contextCapabilities.GL_ARB_explicit_uniform_location);
		snooper.addSystemInfo("gl_caps[ARB_fragment_layer_viewport]", contextCapabilities.GL_ARB_fragment_layer_viewport);
		snooper.addSystemInfo("gl_caps[ARB_fragment_program]", contextCapabilities.GL_ARB_fragment_program);
		snooper.addSystemInfo("gl_caps[ARB_fragment_shader]", contextCapabilities.GL_ARB_fragment_shader);
		snooper.addSystemInfo("gl_caps[ARB_fragment_program_shadow]", contextCapabilities.GL_ARB_fragment_program_shadow);
		snooper.addSystemInfo("gl_caps[ARB_framebuffer_object]", contextCapabilities.GL_ARB_framebuffer_object);
		snooper.addSystemInfo("gl_caps[ARB_framebuffer_sRGB]", contextCapabilities.GL_ARB_framebuffer_sRGB);
		snooper.addSystemInfo("gl_caps[ARB_geometry_shader4]", contextCapabilities.GL_ARB_geometry_shader4);
		snooper.addSystemInfo("gl_caps[ARB_gpu_shader5]", contextCapabilities.GL_ARB_gpu_shader5);
		snooper.addSystemInfo("gl_caps[ARB_half_float_pixel]", contextCapabilities.GL_ARB_half_float_pixel);
		snooper.addSystemInfo("gl_caps[ARB_half_float_vertex]", contextCapabilities.GL_ARB_half_float_vertex);
		snooper.addSystemInfo("gl_caps[ARB_instanced_arrays]", contextCapabilities.GL_ARB_instanced_arrays);
		snooper.addSystemInfo("gl_caps[ARB_map_buffer_alignment]", contextCapabilities.GL_ARB_map_buffer_alignment);
		snooper.addSystemInfo("gl_caps[ARB_map_buffer_range]", contextCapabilities.GL_ARB_map_buffer_range);
		snooper.addSystemInfo("gl_caps[ARB_multisample]", contextCapabilities.GL_ARB_multisample);
		snooper.addSystemInfo("gl_caps[ARB_multitexture]", contextCapabilities.GL_ARB_multitexture);
		snooper.addSystemInfo("gl_caps[ARB_occlusion_query2]", contextCapabilities.GL_ARB_occlusion_query2);
		snooper.addSystemInfo("gl_caps[ARB_pixel_buffer_object]", contextCapabilities.GL_ARB_pixel_buffer_object);
		snooper.addSystemInfo("gl_caps[ARB_seamless_cube_map]", contextCapabilities.GL_ARB_seamless_cube_map);
		snooper.addSystemInfo("gl_caps[ARB_shader_objects]", contextCapabilities.GL_ARB_shader_objects);
		snooper.addSystemInfo("gl_caps[ARB_shader_stencil_export]", contextCapabilities.GL_ARB_shader_stencil_export);
		snooper.addSystemInfo("gl_caps[ARB_shader_texture_lod]", contextCapabilities.GL_ARB_shader_texture_lod);
		snooper.addSystemInfo("gl_caps[ARB_shadow]", contextCapabilities.GL_ARB_shadow);
		snooper.addSystemInfo("gl_caps[ARB_shadow_ambient]", contextCapabilities.GL_ARB_shadow_ambient);
		snooper.addSystemInfo("gl_caps[ARB_stencil_texturing]", contextCapabilities.GL_ARB_stencil_texturing);
		snooper.addSystemInfo("gl_caps[ARB_sync]", contextCapabilities.GL_ARB_sync);
		snooper.addSystemInfo("gl_caps[ARB_tessellation_shader]", contextCapabilities.GL_ARB_tessellation_shader);
		snooper.addSystemInfo("gl_caps[ARB_texture_border_clamp]", contextCapabilities.GL_ARB_texture_border_clamp);
		snooper.addSystemInfo("gl_caps[ARB_texture_buffer_object]", contextCapabilities.GL_ARB_texture_buffer_object);
		snooper.addSystemInfo("gl_caps[ARB_texture_cube_map]", contextCapabilities.GL_ARB_texture_cube_map);
		snooper.addSystemInfo("gl_caps[ARB_texture_cube_map_array]", contextCapabilities.GL_ARB_texture_cube_map_array);
		snooper.addSystemInfo("gl_caps[ARB_texture_non_power_of_two]", contextCapabilities.GL_ARB_texture_non_power_of_two);
		snooper.addSystemInfo("gl_caps[ARB_uniform_buffer_object]", contextCapabilities.GL_ARB_uniform_buffer_object);
		snooper.addSystemInfo("gl_caps[ARB_vertex_blend]", contextCapabilities.GL_ARB_vertex_blend);
		snooper.addSystemInfo("gl_caps[ARB_vertex_buffer_object]", contextCapabilities.GL_ARB_vertex_buffer_object);
		snooper.addSystemInfo("gl_caps[ARB_vertex_program]", contextCapabilities.GL_ARB_vertex_program);
		snooper.addSystemInfo("gl_caps[ARB_vertex_shader]", contextCapabilities.GL_ARB_vertex_shader);
		snooper.addSystemInfo("gl_caps[EXT_bindable_uniform]", contextCapabilities.GL_EXT_bindable_uniform);
		snooper.addSystemInfo("gl_caps[EXT_blend_equation_separate]", contextCapabilities.GL_EXT_blend_equation_separate);
		snooper.addSystemInfo("gl_caps[EXT_blend_func_separate]", contextCapabilities.GL_EXT_blend_func_separate);
		snooper.addSystemInfo("gl_caps[EXT_blend_minmax]", contextCapabilities.GL_EXT_blend_minmax);
		snooper.addSystemInfo("gl_caps[EXT_blend_subtract]", contextCapabilities.GL_EXT_blend_subtract);
		snooper.addSystemInfo("gl_caps[EXT_draw_instanced]", contextCapabilities.GL_EXT_draw_instanced);
		snooper.addSystemInfo("gl_caps[EXT_framebuffer_multisample]", contextCapabilities.GL_EXT_framebuffer_multisample);
		snooper.addSystemInfo("gl_caps[EXT_framebuffer_object]", contextCapabilities.GL_EXT_framebuffer_object);
		snooper.addSystemInfo("gl_caps[EXT_framebuffer_sRGB]", contextCapabilities.GL_EXT_framebuffer_sRGB);
		snooper.addSystemInfo("gl_caps[EXT_geometry_shader4]", contextCapabilities.GL_EXT_geometry_shader4);
		snooper.addSystemInfo("gl_caps[EXT_gpu_program_parameters]", contextCapabilities.GL_EXT_gpu_program_parameters);
		snooper.addSystemInfo("gl_caps[EXT_gpu_shader4]", contextCapabilities.GL_EXT_gpu_shader4);
		snooper.addSystemInfo("gl_caps[EXT_multi_draw_arrays]", contextCapabilities.GL_EXT_multi_draw_arrays);
		snooper.addSystemInfo("gl_caps[EXT_packed_depth_stencil]", contextCapabilities.GL_EXT_packed_depth_stencil);
		snooper.addSystemInfo("gl_caps[EXT_paletted_texture]", contextCapabilities.GL_EXT_paletted_texture);
		snooper.addSystemInfo("gl_caps[EXT_rescale_normal]", contextCapabilities.GL_EXT_rescale_normal);
		snooper.addSystemInfo("gl_caps[EXT_separate_shader_objects]", contextCapabilities.GL_EXT_separate_shader_objects);
		snooper.addSystemInfo("gl_caps[EXT_shader_image_load_store]", contextCapabilities.GL_EXT_shader_image_load_store);
		snooper.addSystemInfo("gl_caps[EXT_shadow_funcs]", contextCapabilities.GL_EXT_shadow_funcs);
		snooper.addSystemInfo("gl_caps[EXT_shared_texture_palette]", contextCapabilities.GL_EXT_shared_texture_palette);
		snooper.addSystemInfo("gl_caps[EXT_stencil_clear_tag]", contextCapabilities.GL_EXT_stencil_clear_tag);
		snooper.addSystemInfo("gl_caps[EXT_stencil_two_side]", contextCapabilities.GL_EXT_stencil_two_side);
		snooper.addSystemInfo("gl_caps[EXT_stencil_wrap]", contextCapabilities.GL_EXT_stencil_wrap);
		snooper.addSystemInfo("gl_caps[EXT_texture_3d]", contextCapabilities.GL_EXT_texture_3d);
		snooper.addSystemInfo("gl_caps[EXT_texture_array]", contextCapabilities.GL_EXT_texture_array);
		snooper.addSystemInfo("gl_caps[EXT_texture_buffer_object]", contextCapabilities.GL_EXT_texture_buffer_object);
		snooper.addSystemInfo("gl_caps[EXT_texture_integer]", contextCapabilities.GL_EXT_texture_integer);
		snooper.addSystemInfo("gl_caps[EXT_texture_lod_bias]", contextCapabilities.GL_EXT_texture_lod_bias);
		snooper.addSystemInfo("gl_caps[EXT_texture_sRGB]", contextCapabilities.GL_EXT_texture_sRGB);
		snooper.addSystemInfo("gl_caps[EXT_vertex_shader]", contextCapabilities.GL_EXT_vertex_shader);
		snooper.addSystemInfo("gl_caps[EXT_vertex_weighting]", contextCapabilities.GL_EXT_vertex_weighting);
		snooper.addSystemInfo("gl_caps[gl_max_vertex_uniforms]", GlStateManager.method_12321(35658));
		GlStateManager.method_12271();
		snooper.addSystemInfo("gl_caps[gl_max_fragment_uniforms]", GlStateManager.method_12321(35657));
		GlStateManager.method_12271();
		snooper.addSystemInfo("gl_caps[gl_max_vertex_attribs]", GlStateManager.method_12321(34921));
		GlStateManager.method_12271();
		snooper.addSystemInfo("gl_caps[gl_max_vertex_texture_image_units]", GlStateManager.method_12321(35660));
		GlStateManager.method_12271();
		snooper.addSystemInfo("gl_caps[gl_max_texture_image_units]", GlStateManager.method_12321(34930));
		GlStateManager.method_12271();
		snooper.addSystemInfo("gl_caps[gl_max_array_texture_layers]", GlStateManager.method_12321(35071));
		GlStateManager.method_12271();
		snooper.addSystemInfo("gl_max_texture_size", getMaxTextureSize());
		GameProfile gameProfile = this.session.getProfile();
		if (gameProfile != null && gameProfile.getId() != null) {
			snooper.addSystemInfo("uuid", Hashing.sha1().hashBytes(gameProfile.getId().toString().getBytes(Charsets.ISO_8859_1)).toString());
		}
	}

	public static int getMaxTextureSize() {
		for (int i = 16384; i > 0; i >>= 1) {
			GlStateManager.method_12276(32868, 0, 6408, i, i, 0, 6408, 5121, null);
			int j = GlStateManager.method_12301(32868, 0, 4096);
			if (j != 0) {
				return i;
			}
		}

		return -1;
	}

	@Override
	public boolean isSnooperEnabled() {
		return this.options.snopperEnabled;
	}

	public void setCurrentServerEntry(ServerInfo info) {
		this.currentServerEntry = info;
	}

	@Nullable
	public ServerInfo getCurrentServerEntry() {
		return this.currentServerEntry;
	}

	public boolean isIntegratedServerRunning() {
		return this.isIntegratedServerRunning;
	}

	public boolean isInSingleplayer() {
		return this.isIntegratedServerRunning && this.server != null;
	}

	@Nullable
	public IntegratedServer getServer() {
		return this.server;
	}

	public static void stopServer() {
		if (instance != null) {
			IntegratedServer integratedServer = instance.getServer();
			if (integratedServer != null) {
				integratedServer.stopServer();
			}
		}
	}

	public Snooper getSnooper() {
		return this.snooper;
	}

	public static long getTime() {
		return Sys.getTime() * 1000L / Sys.getTimerResolution();
	}

	public boolean isFullscreen() {
		return this.fullscreen;
	}

	public Session getSession() {
		return this.session;
	}

	public PropertyMap getSessionProperties() {
		if (this.sessionPropertyMap.isEmpty()) {
			GameProfile gameProfile = this.getSessionService().fillProfileProperties(this.session.getProfile(), false);
			this.sessionPropertyMap.putAll(gameProfile.getProperties());
		}

		return this.sessionPropertyMap;
	}

	public Proxy getNetworkProxy() {
		return this.networkProxy;
	}

	public TextureManager getTextureManager() {
		return this.textureManager;
	}

	public ResourceManager getResourceManager() {
		return this.resourceManager;
	}

	public ResourcePackLoader getResourcePackLoader() {
		return this.loader;
	}

	public LanguageManager getLanguageManager() {
		return this.languageManager;
	}

	public SpriteAtlasTexture getSpriteAtlasTexture() {
		return this.texture;
	}

	public boolean is64Bit() {
		return this.is64Bit;
	}

	public boolean isPaused() {
		return this.paused;
	}

	public SoundManager getSoundManager() {
		return this.soundManager;
	}

	public MusicTracker.MusicType getMusicType() {
		if (this.currentScreen instanceof CreditsScreen) {
			return MusicTracker.MusicType.CREDITS;
		} else if (this.player != null) {
			if (this.player.world.dimension instanceof TheNetherDimension) {
				return MusicTracker.MusicType.NETHER;
			} else if (this.player.world.dimension instanceof TheEndDimension) {
				return this.inGameHud.method_12167().method_12172() ? MusicTracker.MusicType.END_BOSS : MusicTracker.MusicType.END;
			} else {
				return this.player.abilities.creativeMode && this.player.abilities.allowFlying ? MusicTracker.MusicType.CREATIVE : MusicTracker.MusicType.GAME;
			}
		} else {
			return MusicTracker.MusicType.MENU;
		}
	}

	public void handleKeyInput() {
		int i = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();
		if (i != 0 && !Keyboard.isRepeatEvent()) {
			if (!(this.currentScreen instanceof ControlsOptionsScreen) || ((ControlsOptionsScreen)this.currentScreen).time <= getTime() - 20L) {
				if (Keyboard.getEventKeyState()) {
					if (i == this.options.fullscreenKey.getCode()) {
						this.toggleFullscreen();
					} else if (i == this.options.screenshotKey.getCode()) {
						this.inGameHud.getChatHud().addMessage(ScreenshotUtils.saveScreenshot(this.runDirectory, this.width, this.height, this.fbo));
					} else if (i == 48 && Screen.hasControlDown() && (this.currentScreen == null || this.currentScreen != null && !this.currentScreen.method_14504())) {
						this.options.getBooleanValue(GameOptions.Option.NARRATOR, 1);
						if (this.currentScreen instanceof ChatOptionsScreen) {
							((ChatOptionsScreen)this.currentScreen).method_14501();
						}
					}
				}
			}
		}
	}

	public MinecraftSessionService getSessionService() {
		return this.sessionService;
	}

	public PlayerSkinProvider getSkinProvider() {
		return this.skinProvider;
	}

	@Nullable
	public Entity getCameraEntity() {
		return this.cameraEntity;
	}

	public void setCameraEntity(Entity entity) {
		this.cameraEntity = entity;
		this.gameRenderer.onCameraEntitySet(entity);
	}

	public <V> ListenableFuture<V> execute(Callable<V> task) {
		Validate.notNull(task);
		if (this.isOnThread()) {
			try {
				return Futures.immediateFuture(task.call());
			} catch (Exception var5) {
				return Futures.immediateFailedCheckedFuture(var5);
			}
		} else {
			ListenableFutureTask<V> listenableFutureTask = ListenableFutureTask.create(task);
			synchronized (this.tasks) {
				this.tasks.add(listenableFutureTask);
				return listenableFutureTask;
			}
		}
	}

	@Override
	public ListenableFuture<Object> submit(Runnable task) {
		Validate.notNull(task);
		return this.execute(Executors.callable(task));
	}

	@Override
	public boolean isOnThread() {
		return Thread.currentThread() == this.currentThread;
	}

	public BlockRenderManager getBlockRenderManager() {
		return this.blockRenderManager;
	}

	public EntityRenderDispatcher getEntityRenderManager() {
		return this.entityRenderDispatcher;
	}

	public ItemRenderer getItemRenderer() {
		return this.itemRenderer;
	}

	public HeldItemRenderer getHeldItemRenderer() {
		return this.heldItemRenderer;
	}

	public <T> class_3308<T> method_14460(class_3306.class_3307<T> arg) {
		return this.field_15870.method_14705(arg);
	}

	public static int getCurrentFps() {
		return currentFps;
	}

	public MetricsData getMetricsData() {
		return this.metricsData;
	}

	public boolean isConnectedToRealms() {
		return this.connectedToRealms;
	}

	public void setConnectedToRealms(boolean connectedToRealms) {
		this.connectedToRealms = connectedToRealms;
	}

	public DataFixerUpper method_12142() {
		return this.field_13277;
	}

	public float method_12143() {
		return this.ticker.tickDelta;
	}

	public float method_14461() {
		return this.ticker.lastFrameDuration;
	}

	public BlockColors method_12144() {
		return this.field_13278;
	}

	public boolean hasReducedDebugInfo() {
		return this.player != null && this.player.getReducedDebugInfo() || this.options.reducedDebugInfo;
	}

	public class_3264 method_14462() {
		return this.field_15868;
	}

	public class_3316 method_14463() {
		return this.field_15869;
	}
}
