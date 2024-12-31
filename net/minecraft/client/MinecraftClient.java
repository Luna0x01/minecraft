package net.minecraft.client;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
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
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import javax.imageio.ImageIO;
import net.minecraft.Bootstrap;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.AchievementNotification;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.OutOfMemoryScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SleepingChatScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.TwitchErrorScreen;
import net.minecraft.client.gui.screen.ingame.SurvivalInventoryScreen;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import net.minecraft.client.gui.widget.IdentifiableBooleanConsumer;
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
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.resource.AnimationMetadata;
import net.minecraft.client.resource.AnimationMetadataSerializer;
import net.minecraft.client.resource.AssetsIndex;
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
import net.minecraft.client.twitch.TwitchAuth;
import net.minecraft.client.util.AnError;
import net.minecraft.client.util.NullTwitchStream;
import net.minecraft.client.util.ScreenshotUtils;
import net.minecraft.client.util.Session;
import net.minecraft.client.util.TwitchStreamProvider;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.BuiltChunk;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.FoliageColorResourceReloadListener;
import net.minecraft.resource.GrassColorResourceReloadListener;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.StatHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.MetadataSerializer;
import net.minecraft.util.MetricsData;
import net.minecraft.util.ThreadExecutor;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
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
import org.lwjgl.opengl.GL11;
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
	public ClientPlayerInteractionManager interactionManager;
	private boolean fullscreen;
	private boolean glErrors = true;
	private boolean crashed;
	private CrashReport crashReport;
	public int width;
	public int height;
	private boolean connectedToRealms = false;
	private ClientTickTracker ticker = new ClientTickTracker(20.0F);
	private Snooper snooper = new Snooper("client", this, MinecraftServer.getTimeMillis());
	public ClientWorld world;
	public WorldRenderer worldRenderer;
	private EntityRenderDispatcher entityRenderDispatcher;
	private ItemRenderer itemRenderer;
	private HeldItemRenderer heldItemRenderer;
	public ClientPlayerEntity player;
	private Entity cameraEntity;
	public Entity targetedEntity;
	public ParticleManager particleManager;
	private final Session session;
	private boolean paused;
	public TextRenderer textRenderer;
	public TextRenderer shadowTextRenderer;
	public Screen currentScreen;
	public LoadingScreenRenderer loadingScreenRenderer;
	public GameRenderer gameRenderer;
	private int attackCooldown;
	private int tempWidth;
	private int tempHeight;
	private IntegratedServer server;
	public AchievementNotification notification;
	public InGameHud inGameHud;
	public boolean skipGameRender;
	public BlockHitResult result;
	public GameOptions options;
	public MouseInput mouse;
	public final File runDirectory;
	private final File assetDirectory;
	private final String gameVersion;
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
	private TwitchStreamProvider twitchStreamProvider;
	private Framebuffer fbo;
	private SpriteAtlasTexture texture;
	private SoundManager soundManager;
	private MusicTracker musicTracker;
	private Identifier mojang;
	private final MinecraftSessionService sessionService;
	private PlayerSkinProvider skinProvider;
	private final Queue<FutureTask<?>> tasks = Queues.newArrayDeque();
	private long frameTime = 0L;
	private final Thread currentThread = Thread.currentThread();
	private BakedModelManager modelManager;
	private BlockRenderManager blockRenderManager;
	volatile boolean running = true;
	public String fpsDebugString = "";
	public boolean wireFrame = false;
	public boolean chunkPath = false;
	public boolean chunkVisibility = false;
	public boolean chunkCullingEnabled = true;
	long time = getTime();
	int fpsCounter;
	long debugTime = -1L;
	private String openProfilerSection = "root";

	public MinecraftClient(RunArgs runArgs) {
		instance = this;
		this.runDirectory = runArgs.directories.runDir;
		this.assetDirectory = runArgs.directories.assetDir;
		this.resourcePackDir = runArgs.directories.resourcePackDir;
		this.gameVersion = runArgs.game.version;
		this.twitchPropertyMap = runArgs.args.userProperties;
		this.sessionPropertyMap = runArgs.args.profileProperties;
		this.defaultResourcePack = new DefaultResourcePack(new AssetsIndex(runArgs.directories.assetDir, runArgs.directories.assetIndex).getIndex());
		this.networkProxy = runArgs.args.netProxy == null ? Proxy.NO_PROXY : runArgs.args.netProxy;
		this.sessionService = new YggdrasilAuthenticationService(runArgs.args.netProxy, UUID.randomUUID().toString()).createMinecraftSessionService();
		this.session = runArgs.args.session;
		LOGGER.info("Setting user: " + this.session.getUsername());
		LOGGER.info("(Session ID is " + this.session.getSessionId() + ")");
		this.isDemo = runArgs.game.demo;
		this.width = runArgs.windowInformation.width > 0 ? runArgs.windowInformation.width : 1;
		this.height = runArgs.windowInformation.height > 0 ? runArgs.windowInformation.height : 1;
		this.tempWidth = runArgs.windowInformation.width;
		this.tempHeight = runArgs.windowInformation.height;
		this.fullscreen = runArgs.windowInformation.fullscreen;
		this.is64Bit = checkIs64Bit();
		this.server = new IntegratedServer(this);
		if (runArgs.autoConnect.serverIp != null) {
			this.serverAddress = runArgs.autoConnect.serverIp;
			this.serverPort = runArgs.autoConnect.serverPort;
		}

		ImageIO.setUseCache(false);
		Bootstrap.initialize();
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

	private void initializeGame() throws LWJGLException, IOException {
		this.options = new GameOptions(this, this.runDirectory);
		this.resourcePacks.add(this.defaultResourcePack);
		this.initializeTimerHackThread();
		if (this.options.overrideHeight > 0 && this.options.overrideWidth > 0) {
			this.width = this.options.overrideWidth;
			this.height = this.options.overrideHeight;
		}

		LOGGER.info("LWJGL Version: " + Sys.getVersion());
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
		this.initializeStream();
		this.skinProvider = new PlayerSkinProvider(this.textureManager, new File(this.assetDirectory, "skins"), this.sessionService);
		this.currentSave = new AnvilLevelStorage(new File(this.runDirectory, "saves"));
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
		AchievementsAndCriterions.TAKING_INVENTORY.setStatFormatter(new StatFormatter() {
			@Override
			public String format(String string) {
				try {
					return String.format(string, GameOptions.getFormattedNameForKeyCode(MinecraftClient.this.options.inventoryKey.getCode()));
				} catch (Exception var3) {
					return "Error: " + var3.getLocalizedMessage();
				}
			}
		});
		this.mouse = new MouseInput();
		this.setGlErrorMessage("Pre startup");
		GlStateManager.enableTexture();
		GlStateManager.shadeModel(7425);
		GlStateManager.clearDepth(1.0);
		GlStateManager.enableDepthTest();
		GlStateManager.depthFunc(515);
		GlStateManager.enableAlphaTest();
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.cullFace(1029);
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
		this.itemRenderer = new ItemRenderer(this.textureManager, this.modelManager);
		this.entityRenderDispatcher = new EntityRenderDispatcher(this.textureManager, this.itemRenderer);
		this.heldItemRenderer = new HeldItemRenderer(this);
		this.resourceManager.registerListener(this.itemRenderer);
		this.gameRenderer = new GameRenderer(this, this.resourceManager);
		this.resourceManager.registerListener(this.gameRenderer);
		this.blockRenderManager = new BlockRenderManager(this.modelManager.getModelShapes(), this.options);
		this.resourceManager.registerListener(this.blockRenderManager);
		this.worldRenderer = new WorldRenderer(this);
		this.resourceManager.registerListener(this.worldRenderer);
		this.notification = new AchievementNotification(this);
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

	private void registerMetadataSerializers() {
		this.metadataSerializer.register(new TextureMetadataSerializer(), TextureResourceMetadata.class);
		this.metadataSerializer.register(new FontMetadataSerializer(), FontMetadata.class);
		this.metadataSerializer.register(new AnimationMetadataSerializer(), AnimationMetadata.class);
		this.metadataSerializer.register(new PackFormatMetadataSerializer(), ResourcePackMetadata.class);
		this.metadataSerializer.register(new LanguageMetadataSerializer(), LanguageResourceMetadata.class);
	}

	private void initializeStream() {
		try {
			this.twitchStreamProvider = new TwitchAuth(this, (Property)Iterables.getFirst(this.twitchPropertyMap.get("twitch_access_token"), null));
		} catch (Throwable var2) {
			this.twitchStreamProvider = new NullTwitchStream(var2);
			LOGGER.error("Couldn't initialize twitch stream");
		}
	}

	private void setPixelFormat() throws LWJGLException {
		Display.setResizable(true);
		Display.setTitle("Minecraft 1.8.9");

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

		for (int k : is) {
			byteBuffer.putInt(k << 8 | k >> 24 & 0xFF);
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

	private void loadLogo(TextureManager textureManager) throws LWJGLException {
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
			LOGGER.error("Unable to load logo: " + MOJANG_LOGO_TEXTURE, var12);
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
		this.drawLogo((window.getWidth() - j) / 2, (window.getHeight() - k) / 2, 0, 0, j, k, 255, 255, 255, 255);
		GlStateManager.disableLighting();
		GlStateManager.disableFog();
		framebuffer.unbind();
		framebuffer.draw(window.getWidth() * i, window.getHeight() * i);
		GlStateManager.enableAlphaTest();
		GlStateManager.alphaFunc(516, 0.1F);
		this.updateDisplay();
	}

	public void drawLogo(int startX, int startY, int endX, int endY, int width, int height, int red, int green, int blue, int alpha) {
		float f = 0.00390625F;
		float g = 0.00390625F;
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex((double)startX, (double)(startY + height), 0.0)
			.texture((double)((float)endX * f), (double)((float)(endY + height) * g))
			.color(red, green, blue, alpha)
			.next();
		bufferBuilder.vertex((double)(startX + width), (double)(startY + height), 0.0)
			.texture((double)((float)(endX + width) * f), (double)((float)(endY + height) * g))
			.color(red, green, blue, alpha)
			.next();
		bufferBuilder.vertex((double)(startX + width), (double)startY, 0.0)
			.texture((double)((float)(endX + width) * f), (double)((float)endY * g))
			.color(red, green, blue, alpha)
			.next();
		bufferBuilder.vertex((double)startX, (double)startY, 0.0).texture((double)((float)endX * f), (double)((float)endY * g)).color(red, green, blue, alpha).next();
		Tessellator.getInstance().draw();
	}

	public LevelStorageAccess getCurrentSave() {
		return this.currentSave;
	}

	public void setScreen(Screen screen) {
		if (this.currentScreen != null) {
			this.currentScreen.removed();
		}

		if (screen == null && this.world == null) {
			screen = new TitleScreen();
		} else if (screen == null && this.player.getHealth() <= 0.0F) {
			screen = new DeathScreen();
		}

		if (screen instanceof TitleScreen) {
			this.options.debugEnabled = false;
			this.inGameHud.getChatHud().clear();
		}

		this.currentScreen = screen;
		if (screen != null) {
			this.grabMouse();
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
		if (this.glErrors) {
			int i = GL11.glGetError();
			if (i != 0) {
				String string = GLU.gluErrorString(i);
				LOGGER.error("########## GL ERROR ##########");
				LOGGER.error("@ " + message);
				LOGGER.error(i + ": " + string);
			}
		}
	}

	public void stop() {
		try {
			this.twitchStreamProvider.stop();
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

		if (this.paused && this.world != null) {
			float f = this.ticker.tickDelta;
			this.ticker.tick();
			this.ticker.tickDelta = f;
		} else {
			this.ticker.tick();
		}

		this.profiler.push("scheduledExecutables");
		synchronized (this.tasks) {
			while (!this.tasks.isEmpty()) {
				Util.executeTask((FutureTask)this.tasks.poll(), LOGGER);
			}
		}

		this.profiler.pop();
		long m = System.nanoTime();
		this.profiler.push("tick");

		for (int i = 0; i < this.ticker.ticksThisFrame; i++) {
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
		if (this.player != null && this.player.isInsideWall()) {
			this.options.perspective = 0;
		}

		this.profiler.pop();
		if (!this.skipGameRender) {
			this.profiler.swap("gameRenderer");
			this.gameRenderer.render(this.ticker.tickDelta, l);
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

		this.notification.tick();
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
		this.profiler.push("stream");
		this.profiler.push("update");
		this.twitchStreamProvider.update();
		this.profiler.swap("submit");
		this.twitchStreamProvider.submit();
		this.profiler.pop();
		this.profiler.pop();
		this.setGlErrorMessage("Post render");
		this.fpsCounter++;
		this.paused = this.isInSingleplayer() && this.currentScreen != null && this.currentScreen.shouldPauseGame() && !this.server.isPublished();
		long o = System.nanoTime();
		this.metricsData.pushSample(o - this.nanoTime);
		this.nanoTime = o;

		while (getTime() >= this.time + 1000L) {
			currentFps = this.fpsCounter;
			this.fpsDebugString = String.format(
				"%d fps (%d chunk update%s) T: %s%s%s%s%s",
				currentFps,
				BuiltChunk.chunkUpdates,
				BuiltChunk.chunkUpdates != 1 ? "s" : "",
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
		if (list != null && !list.isEmpty()) {
			Profiler.Section section = (Profiler.Section)list.remove(0);
			if (digit == 0) {
				if (section.name.length() > 0) {
					int i = this.openProfilerSection.lastIndexOf(".");
					if (i >= 0) {
						this.openProfilerSection = this.openProfilerSection.substring(0, i);
					}
				}
			} else {
				digit--;
				if (digit < list.size() && !((Profiler.Section)list.get(digit)).name.equals("unspecified")) {
					if (this.openProfilerSection.length() > 0) {
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
			GL11.glLineWidth(1.0F);
			GlStateManager.disableTexture();
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			int i = 160;
			int j = this.width - i - 10;
			int k = this.height - i * 2;
			GlStateManager.enableBlend();
			bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
			bufferBuilder.vertex((double)((float)j - (float)i * 1.1F), (double)((float)k - (float)i * 0.6F - 16.0F), 0.0).color(200, 0, 0, 0).next();
			bufferBuilder.vertex((double)((float)j - (float)i * 1.1F), (double)(k + i * 2), 0.0).color(200, 0, 0, 0).next();
			bufferBuilder.vertex((double)((float)j + (float)i * 1.1F), (double)(k + i * 2), 0.0).color(200, 0, 0, 0).next();
			bufferBuilder.vertex((double)((float)j + (float)i * 1.1F), (double)((float)k - (float)i * 0.6F - 16.0F), 0.0).color(200, 0, 0, 0).next();
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
					float f = (float)((d + section2.relativePercentage * (double)r / (double)m) * (float) Math.PI * 2.0 / 100.0);
					float g = MathHelper.sin(f) * (float)i;
					float h = MathHelper.cos(f) * (float)i * 0.5F;
					bufferBuilder.vertex((double)((float)j + g), (double)((float)k - h), 0.0).color(o, p, q, 255).next();
				}

				tessellator.draw();
				bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);

				for (int s = m; s >= 0; s--) {
					float t = (float)((d + section2.relativePercentage * (double)s / (double)m) * (float) Math.PI * 2.0 / 100.0);
					float u = MathHelper.sin(t) * (float)i;
					float v = MathHelper.cos(t) * (float)i * 0.5F;
					bufferBuilder.vertex((double)((float)j + u), (double)((float)k - v), 0.0).color(o >> 1, p >> 1, q >> 1, 255).next();
					bufferBuilder.vertex((double)((float)j + u), (double)((float)k - v + 10.0F), 0.0).color(o >> 1, p >> 1, q >> 1, 255).next();
				}

				tessellator.draw();
				d += section2.relativePercentage;
			}

			DecimalFormat decimalFormat = new DecimalFormat("##0.00");
			GlStateManager.enableTexture();
			String string = "";
			if (!section.name.equals("unspecified")) {
				string = string + "[0] ";
			}

			if (section.name.length() == 0) {
				string = string + "ROOT ";
			} else {
				string = string + section.name + " ";
			}

			int w = 16777215;
			this.textRenderer.drawWithShadow(string, (float)(j - i), (float)(k - i / 2 - 16), w);
			this.textRenderer
				.drawWithShadow(
					string = decimalFormat.format(section.absolutePercentage) + "%", (float)(j + i - this.textRenderer.getStringWidth(string)), (float)(k - i / 2 - 16), w
				);

			for (int x = 0; x < list.size(); x++) {
				Profiler.Section section3 = (Profiler.Section)list.get(x);
				String string2 = "";
				if (section3.name.equals("unspecified")) {
					string2 = string2 + "[?] ";
				} else {
					string2 = string2 + "[" + (x + 1) + "] ";
				}

				string2 = string2 + section3.name;
				this.textRenderer.drawWithShadow(string2, (float)(j - i), (float)(k + i / 2 + x * 8 + 20), section3.getColor());
				this.textRenderer
					.drawWithShadow(
						string2 = decimalFormat.format(section3.relativePercentage) + "%",
						(float)(j + i - 50 - this.textRenderer.getStringWidth(string2)),
						(float)(k + i / 2 + x * 8 + 20),
						section3.getColor()
					);
				this.textRenderer
					.drawWithShadow(
						string2 = decimalFormat.format(section3.absolutePercentage) + "%",
						(float)(j + i - this.textRenderer.getStringWidth(string2)),
						(float)(k + i / 2 + x * 8 + 20),
						section3.getColor()
					);
			}
		}
	}

	public void scheduleStop() {
		this.running = false;
	}

	public void closeScreen() {
		if (Display.isActive()) {
			if (!this.focused) {
				this.focused = true;
				this.mouse.lockMouse();
				this.setScreen(null);
				this.attackCooldown = 10000;
			}
		}
	}

	public void grabMouse() {
		if (this.focused) {
			KeyBinding.releaseAllKeys();
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

		if (this.attackCooldown <= 0 && !this.player.isUsingItem()) {
			if (breaking && this.result != null && this.result.type == BlockHitResult.Type.BLOCK) {
				BlockPos blockPos = this.result.getBlockPos();
				if (this.world.getBlockState(blockPos).getBlock().getMaterial() != Material.AIR
					&& this.interactionManager.updateBlockBreakingProgress(blockPos, this.result.direction)) {
					this.particleManager.addBlockBreakingParticles(blockPos, this.result.direction);
					this.player.swingHand();
				}
			} else {
				this.interactionManager.cancelBlockBreaking();
			}
		}
	}

	private void doAttack() {
		if (this.attackCooldown <= 0) {
			this.player.swingHand();
			if (this.result == null) {
				LOGGER.error("Null returned as 'hitResult', this shouldn't happen!");
				if (this.interactionManager.hasLimitedAttackSpeed()) {
					this.attackCooldown = 10;
				}
			} else {
				switch (this.result.type) {
					case ENTITY:
						this.interactionManager.attackEntity(this.player, this.result.entity);
						break;
					case BLOCK:
						BlockPos blockPos = this.result.getBlockPos();
						if (this.world.getBlockState(blockPos).getBlock().getMaterial() != Material.AIR) {
							this.interactionManager.attackBlock(blockPos, this.result.direction);
							break;
						}
					case MISS:
					default:
						if (this.interactionManager.hasLimitedAttackSpeed()) {
							this.attackCooldown = 10;
						}
				}
			}
		}
	}

	private void doUse() {
		if (!this.interactionManager.isBreakingBlock()) {
			this.blockPlaceDelay = 4;
			boolean bl = true;
			ItemStack itemStack = this.player.inventory.getMainHandStack();
			if (this.result == null) {
				LOGGER.warn("Null returned as 'hitResult', this shouldn't happen!");
			} else {
				switch (this.result.type) {
					case ENTITY:
						if (this.interactionManager.interactEntityAtLocation(this.player, this.result.entity, this.result)) {
							bl = false;
						} else if (this.interactionManager.interactEntity(this.player, this.result.entity)) {
							bl = false;
						}
						break;
					case BLOCK:
						BlockPos blockPos = this.result.getBlockPos();
						if (this.world.getBlockState(blockPos).getBlock().getMaterial() != Material.AIR) {
							int i = itemStack != null ? itemStack.count : 0;
							if (this.interactionManager.onRightClick(this.player, this.world, itemStack, blockPos, this.result.direction, this.result.pos)) {
								bl = false;
								this.player.swingHand();
							}

							if (itemStack == null) {
								return;
							}

							if (itemStack.count == 0) {
								this.player.inventory.main[this.player.inventory.selectedSlot] = null;
							} else if (itemStack.count != i || this.interactionManager.hasCreativeInventory()) {
								this.gameRenderer.firstPersonRenderer.resetEquipProgress();
							}
						}
				}
			}

			if (bl) {
				ItemStack itemStack2 = this.player.inventory.getMainHandStack();
				if (itemStack2 != null && this.interactionManager.interactItem(this.player, this.world, itemStack2)) {
					this.gameRenderer.firstPersonRenderer.resetEquipProgress2();
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
		this.profiler.push("gameMode");
		if (!this.paused && this.world != null) {
			this.interactionManager.tick();
		}

		this.profiler.swap("textures");
		if (!this.paused) {
			this.textureManager.tick();
		}

		if (this.currentScreen == null && this.player != null) {
			if (this.player.getHealth() <= 0.0F) {
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
			} catch (Throwable var7) {
				CrashReport crashReport = CrashReport.create(var7, "Updating screen events");
				CrashReportSection crashReportSection = crashReport.addElement("Affected screen");
				crashReportSection.add("Screen name", new Callable<String>() {
					public String call() throws Exception {
						return MinecraftClient.this.currentScreen.getClass().getCanonicalName();
					}
				});
				throw new CrashException(crashReport);
			}

			if (this.currentScreen != null) {
				try {
					this.currentScreen.tick();
				} catch (Throwable var6) {
					CrashReport crashReport2 = CrashReport.create(var6, "Ticking screen");
					CrashReportSection crashReportSection2 = crashReport2.addElement("Affected screen");
					crashReportSection2.add("Screen name", new Callable<String>() {
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

			if (this.attackCooldown > 0) {
				this.attackCooldown--;
			}

			this.profiler.swap("keyboard");

			while (Keyboard.next()) {
				int k = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();
				KeyBinding.setKeyPressed(k, Keyboard.getEventKeyState());
				if (Keyboard.getEventKeyState()) {
					KeyBinding.onKeyPressed(k);
				}

				if (this.f3CTime > 0L) {
					if (getTime() - this.f3CTime >= 6000L) {
						throw new CrashException(new CrashReport("Manually triggered debug crash", new Throwable()));
					}

					if (!Keyboard.isKeyDown(46) || !Keyboard.isKeyDown(61)) {
						this.f3CTime = -1L;
					}
				} else if (Keyboard.isKeyDown(46) && Keyboard.isKeyDown(61)) {
					this.f3CTime = getTime();
				}

				this.handleKeyInput();
				if (Keyboard.getEventKeyState()) {
					if (k == 62 && this.gameRenderer != null) {
						this.gameRenderer.toggleShadersEnabled();
					}

					if (this.currentScreen != null) {
						this.currentScreen.handleKeyboard();
					} else {
						if (k == 1) {
							this.openGameMenuScreen();
						}

						if (k == 32 && Keyboard.isKeyDown(61) && this.inGameHud != null) {
							this.inGameHud.getChatHud().clear();
						}

						if (k == 31 && Keyboard.isKeyDown(61)) {
							this.reloadResources();
						}

						if (k == 17 && Keyboard.isKeyDown(61)) {
						}

						if (k == 18 && Keyboard.isKeyDown(61)) {
						}

						if (k == 47 && Keyboard.isKeyDown(61)) {
						}

						if (k == 38 && Keyboard.isKeyDown(61)) {
						}

						if (k == 22 && Keyboard.isKeyDown(61)) {
						}

						if (k == 20 && Keyboard.isKeyDown(61)) {
							this.reloadResources();
						}

						if (k == 33 && Keyboard.isKeyDown(61)) {
							this.options.getBooleanValue(GameOptions.Option.RENDER_DISTANCE, Screen.hasShiftDown() ? -1 : 1);
						}

						if (k == 30 && Keyboard.isKeyDown(61)) {
							this.worldRenderer.reload();
						}

						if (k == 35 && Keyboard.isKeyDown(61)) {
							this.options.advancedItemTooltips = !this.options.advancedItemTooltips;
							this.options.save();
						}

						if (k == 48 && Keyboard.isKeyDown(61)) {
							this.entityRenderDispatcher.setRenderHitboxes(!this.entityRenderDispatcher.getRenderHitboxes());
						}

						if (k == 25 && Keyboard.isKeyDown(61)) {
							this.options.pauseOnLostFocus = !this.options.pauseOnLostFocus;
							this.options.save();
						}

						if (k == 59) {
							this.options.hudHidden = !this.options.hudHidden;
						}

						if (k == 61) {
							this.options.debugEnabled = !this.options.debugEnabled;
							this.options.debugProfilerEnabled = Screen.hasShiftDown();
							this.options.debugFpsEnabled = Screen.hasAltDown();
						}

						if (this.options.togglePerspectiveKey.wasPressed()) {
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

						if (this.options.smoothCameraKey.wasPressed()) {
							this.options.smoothCameraEnabled = !this.options.smoothCameraEnabled;
						}
					}

					if (this.options.debugEnabled && this.options.debugProfilerEnabled) {
						if (k == 11) {
							this.handleProfilerKeyPress(0);
						}

						for (int m = 0; m < 9; m++) {
							if (k == 2 + m) {
								this.handleProfilerKeyPress(m + 1);
							}
						}
					}
				}
			}

			for (int n = 0; n < 9; n++) {
				if (this.options.hotbarKeys[n].wasPressed()) {
					if (this.player.isSpectator()) {
						this.inGameHud.getSpectatorHud().selectSlot(n);
					} else {
						this.player.inventory.selectedSlot = n;
					}
				}
			}

			boolean bl = this.options.chatVisibilityType != PlayerEntity.ChatVisibilityType.HIDDEN;

			while (this.options.inventoryKey.wasPressed()) {
				if (this.interactionManager.hasRidingInventory()) {
					this.player.openRidingInventory();
				} else {
					this.getNetworkHandler().sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.OPEN_INVENTORY_ACHIEVEMENT));
					this.setScreen(new SurvivalInventoryScreen(this.player));
				}
			}

			while (this.options.dropKey.wasPressed()) {
				if (!this.player.isSpectator()) {
					this.player.dropSelectedItem(Screen.hasControlDown());
				}
			}

			while (this.options.chatKey.wasPressed() && bl) {
				this.setScreen(new ChatScreen());
			}

			if (this.currentScreen == null && this.options.commandKey.wasPressed() && bl) {
				this.setScreen(new ChatScreen("/"));
			}

			if (this.player.isUsingItem()) {
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

			if (this.options.useKey.isPressed() && this.blockPlaceDelay == 0 && !this.player.isUsingItem()) {
				this.doUse();
			}

			this.handleBlockBreaking(this.currentScreen == null && this.options.attackKey.isPressed() && this.focused);
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

				try {
					this.world.tick();
				} catch (Throwable var8) {
					CrashReport crashReport3 = CrashReport.create(var8, "Exception in world tick");
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

	public void startIntegratedServer(String worldName, String levelName, LevelInfo levelInfo) {
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
			this.server = new IntegratedServer(this, worldName, levelName, levelInfo);
			this.server.startServerThread();
			this.isIntegratedServerRunning = true;
		} catch (Throwable var10) {
			CrashReport crashReport = CrashReport.create(var10, "Starting integrated server");
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
			} catch (InterruptedException var9) {
			}
		}

		this.setScreen(null);
		SocketAddress socketAddress = this.server.getNetworkIo().bindLocal();
		ClientConnection clientConnection = ClientConnection.connectLocal(socketAddress);
		clientConnection.setPacketListener(new ClientLoginNetworkHandler(clientConnection, this, null));
		clientConnection.send(new HandshakeC2SPacket(47, socketAddress.toString(), 0, NetworkState.LOGIN));
		clientConnection.send(new LoginHelloC2SPacket(this.getSession().getProfile()));
		this.clientConnection = clientConnection;
	}

	public void connect(ClientWorld world) {
		this.connect(world, "");
	}

	public void connect(ClientWorld world, String loadingMessage) {
		if (world == null) {
			ClientPlayNetworkHandler clientPlayNetworkHandler = this.getNetworkHandler();
			if (clientPlayNetworkHandler != null) {
				clientPlayNetworkHandler.clearWorld();
			}

			if (this.server != null && this.server.hasGameDir()) {
				this.server.stopRunning();
				this.server.setIntegratedInstance();
			}

			this.server = null;
			this.notification.reset();
			this.gameRenderer.getMapRenderer().clearStateTextures();
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
		if (world != null) {
			if (this.worldRenderer != null) {
				this.worldRenderer.setWorld(world);
			}

			if (this.particleManager != null) {
				this.particleManager.setWorld(world);
			}

			if (this.player == null) {
				this.player = this.interactionManager.createPlayer(world, new StatHandler());
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
		this.player = this.interactionManager.createPlayer(this.world, this.player == null ? new StatHandler() : this.player.getStatHandler());
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

	public ClientPlayNetworkHandler getNetworkHandler() {
		return this.player != null ? this.player.networkHandler : null;
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
		if (this.result != null) {
			boolean bl = this.player.abilities.creativeMode;
			int i = 0;
			boolean bl2 = false;
			BlockEntity blockEntity = null;
			Item item;
			if (this.result.type == BlockHitResult.Type.BLOCK) {
				BlockPos blockPos = this.result.getBlockPos();
				Block block = this.world.getBlockState(blockPos).getBlock();
				if (block.getMaterial() == Material.AIR) {
					return;
				}

				item = block.getPickItem(this.world, blockPos);
				if (item == null) {
					return;
				}

				if (bl && Screen.hasControlDown()) {
					blockEntity = this.world.getBlockEntity(blockPos);
				}

				Block block2 = item instanceof BlockItem && !block.isFlowerPot() ? Block.getBlockFromItem(item) : block;
				i = block2.getMeta(this.world, blockPos);
				bl2 = item.isUnbreakable();
			} else {
				if (this.result.type != BlockHitResult.Type.ENTITY || this.result.entity == null || !bl) {
					return;
				}

				if (this.result.entity instanceof PaintingEntity) {
					item = Items.PAINTING;
				} else if (this.result.entity instanceof LeashKnotEntity) {
					item = Items.LEAD;
				} else if (this.result.entity instanceof ItemFrameEntity) {
					ItemFrameEntity itemFrameEntity = (ItemFrameEntity)this.result.entity;
					ItemStack itemStack = itemFrameEntity.getHeldItemStack();
					if (itemStack == null) {
						item = Items.ITEM_FRAME;
					} else {
						item = itemStack.getItem();
						i = itemStack.getData();
						bl2 = true;
					}
				} else if (this.result.entity instanceof AbstractMinecartEntity) {
					AbstractMinecartEntity abstractMinecartEntity = (AbstractMinecartEntity)this.result.entity;
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
				} else if (this.result.entity instanceof BoatEntity) {
					item = Items.BOAT;
				} else if (this.result.entity instanceof ArmorStandEntity) {
					item = Items.ARMOR_STAND;
				} else {
					item = Items.SPAWN_EGG;
					i = EntityType.getIdByEntity(this.result.entity);
					bl2 = true;
					if (!EntityType.SPAWN_EGGS.containsKey(i)) {
						return;
					}
				}
			}

			PlayerInventory playerInventory = this.player.inventory;
			if (blockEntity == null) {
				playerInventory.addPickBlock(item, i, bl2, bl);
			} else {
				ItemStack itemStack2 = this.addBlockEntityNbt(item, i, blockEntity);
				playerInventory.setInvStack(playerInventory.selectedSlot, itemStack2);
			}

			if (bl) {
				int j = this.player.playerScreenHandler.slots.size() - 9 + playerInventory.selectedSlot;
				this.interactionManager.clickCreativeStack(playerInventory.getInvStack(playerInventory.selectedSlot), j);
			}
		}
	}

	private ItemStack addBlockEntityNbt(Item stack, int meta, BlockEntity blockEntity) {
		ItemStack itemStack = new ItemStack(stack, 1, meta);
		NbtCompound nbtCompound = new NbtCompound();
		blockEntity.toNbt(nbtCompound);
		if (stack == Items.SKULL && nbtCompound.contains("Owner")) {
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
		crashReport.getSystemDetailsSection().add("Launched Version", new Callable<String>() {
			public String call() {
				return MinecraftClient.this.gameVersion;
			}
		});
		crashReport.getSystemDetailsSection().add("LWJGL", new Callable<String>() {
			public String call() {
				return Sys.getVersion();
			}
		});
		crashReport.getSystemDetailsSection().add("OpenGL", new Callable<String>() {
			public String call() {
				return GL11.glGetString(7937) + " GL version " + GL11.glGetString(7938) + ", " + GL11.glGetString(7936);
			}
		});
		crashReport.getSystemDetailsSection().add("GL Caps", new Callable<String>() {
			public String call() {
				return GLX.getContextDescription();
			}
		});
		crashReport.getSystemDetailsSection().add("Using VBOs", new Callable<String>() {
			public String call() {
				return MinecraftClient.this.options.vbo ? "Yes" : "No";
			}
		});
		crashReport.getSystemDetailsSection()
			.add(
				"Is Modded",
				new Callable<String>() {
					public String call() throws Exception {
						String string = ClientBrandRetriever.getClientModName();
						if (!string.equals("vanilla")) {
							return "Definitely; Client brand changed to '" + string + "'";
						} else {
							return MinecraftClient.class.getSigners() == null
								? "Very likely; Jar signature invalidated"
								: "Probably not. Jar signature remains and client brand is untouched.";
						}
					}
				}
			);
		crashReport.getSystemDetailsSection().add("Type", new Callable<String>() {
			public String call() throws Exception {
				return "Client (map_client.txt)";
			}
		});
		crashReport.getSystemDetailsSection().add("Resource Packs", new Callable<String>() {
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
		crashReport.getSystemDetailsSection().add("Current Language", new Callable<String>() {
			public String call() throws Exception {
				return MinecraftClient.this.languageManager.getLanguage().toString();
			}
		});
		crashReport.getSystemDetailsSection().add("Profiler Position", new Callable<String>() {
			public String call() throws Exception {
				return MinecraftClient.this.profiler.enabled ? MinecraftClient.this.profiler.getCurrentLocation() : "N/A (disabled)";
			}
		});
		crashReport.getSystemDetailsSection().add("CPU", new Callable<String>() {
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
		String string = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? "little" : "big";
		snooper.addGameInfo("endianness", string);
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
		snooper.addSystemInfo("opengl_version", GL11.glGetString(7938));
		snooper.addSystemInfo("opengl_vendor", GL11.glGetString(7936));
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
		snooper.addSystemInfo("gl_caps[gl_max_vertex_uniforms]", GL11.glGetInteger(35658));
		GL11.glGetError();
		snooper.addSystemInfo("gl_caps[gl_max_fragment_uniforms]", GL11.glGetInteger(35657));
		GL11.glGetError();
		snooper.addSystemInfo("gl_caps[gl_max_vertex_attribs]", GL11.glGetInteger(34921));
		GL11.glGetError();
		snooper.addSystemInfo("gl_caps[gl_max_vertex_texture_image_units]", GL11.glGetInteger(35660));
		GL11.glGetError();
		snooper.addSystemInfo("gl_caps[gl_max_texture_image_units]", GL11.glGetInteger(34930));
		GL11.glGetError();
		snooper.addSystemInfo("gl_caps[gl_max_texture_image_units]", GL11.glGetInteger(35071));
		GL11.glGetError();
		snooper.addSystemInfo("gl_max_texture_size", getMaxTextureSize());
	}

	public static int getMaxTextureSize() {
		for (int i = 16384; i > 0; i >>= 1) {
			GL11.glTexImage2D(32868, 0, 6408, i, i, 0, 6408, 5121, (ByteBuffer)null);
			int j = GL11.glGetTexLevelParameteri(32868, 0, 4096);
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

	public ServerInfo getCurrentServerEntry() {
		return this.currentServerEntry;
	}

	public boolean isIntegratedServerRunning() {
		return this.isIntegratedServerRunning;
	}

	public boolean isInSingleplayer() {
		return this.isIntegratedServerRunning && this.server != null;
	}

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

	public PropertyMap getTwitchPropertyMap() {
		return this.twitchPropertyMap;
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
		if (this.player != null) {
			if (this.player.world.dimension instanceof TheNetherDimension) {
				return MusicTracker.MusicType.NETHER;
			} else if (this.player.world.dimension instanceof TheEndDimension) {
				return BossBar.name != null && BossBar.framesToLive > 0 ? MusicTracker.MusicType.END_BOSS : MusicTracker.MusicType.END;
			} else {
				return this.player.abilities.creativeMode && this.player.abilities.allowFlying ? MusicTracker.MusicType.CREATIVE : MusicTracker.MusicType.GAME;
			}
		} else {
			return MusicTracker.MusicType.MENU;
		}
	}

	public TwitchStreamProvider getTwitchStreamProvider() {
		return this.twitchStreamProvider;
	}

	public void handleKeyInput() {
		int i = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() : Keyboard.getEventKey();
		if (i != 0 && !Keyboard.isRepeatEvent()) {
			if (!(this.currentScreen instanceof ControlsOptionsScreen) || ((ControlsOptionsScreen)this.currentScreen).time <= getTime() - 20L) {
				if (Keyboard.getEventKeyState()) {
					if (i == this.options.streamStartStopKey.getCode()) {
						if (this.getTwitchStreamProvider().isLive()) {
							this.getTwitchStreamProvider().stopStream();
						} else if (this.getTwitchStreamProvider().isReady()) {
							this.setScreen(new ConfirmScreen(new IdentifiableBooleanConsumer() {
								@Override
								public void confirmResult(boolean confirmed, int id) {
									if (confirmed) {
										MinecraftClient.this.getTwitchStreamProvider().initializeStreamProperties();
									}

									MinecraftClient.this.setScreen(null);
								}
							}, I18n.translate("stream.confirm_start"), "", 0));
						} else if (!this.getTwitchStreamProvider().isRunning() || !this.getTwitchStreamProvider().isLoginSuccessful()) {
							TwitchErrorScreen.openNew(this.currentScreen);
						} else if (this.world != null) {
							this.inGameHud.getChatHud().addMessage(new LiteralText("Not ready to start streaming yet!"));
						}
					} else if (i == this.options.streamPauseUnpauseKey.getCode()) {
						if (this.getTwitchStreamProvider().isLive()) {
							if (this.getTwitchStreamProvider().isPaused()) {
								this.getTwitchStreamProvider().resumeBroadcast();
							} else {
								this.getTwitchStreamProvider().pauseBroadcast();
							}
						}
					} else if (i == this.options.streamCommercialKey.getCode()) {
						if (this.getTwitchStreamProvider().isLive()) {
							this.getTwitchStreamProvider().requestCommercial();
						}
					} else if (i == this.options.streamToggleMicKey.getCode()) {
						this.twitchStreamProvider.toggleMic(true);
					} else if (i == this.options.fullscreenKey.getCode()) {
						this.toggleFullscreen();
					} else if (i == this.options.screenshotKey.getCode()) {
						this.inGameHud.getChatHud().addMessage(ScreenshotUtils.saveScreenshot(this.runDirectory, this.width, this.height, this.fbo));
					}
				} else if (i == this.options.streamToggleMicKey.getCode()) {
					this.twitchStreamProvider.toggleMic(false);
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

	public Entity getCameraEntity() {
		return this.cameraEntity;
	}

	public void setCameraEntity(Entity entity) {
		this.cameraEntity = entity;
		this.gameRenderer.onCameraEntitySet(entity);
	}

	public <V> ListenableFuture<V> execute(Callable<V> task) {
		Validate.notNull(task);
		if (!this.isOnThread()) {
			ListenableFutureTask<V> listenableFutureTask = ListenableFutureTask.create(task);
			synchronized (this.tasks) {
				this.tasks.add(listenableFutureTask);
				return listenableFutureTask;
			}
		} else {
			try {
				return Futures.immediateFuture(task.call());
			} catch (Exception var6) {
				return Futures.immediateFailedCheckedFuture(var6);
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

	public static int getCurrentFps() {
		return currentFps;
	}

	public MetricsData getMetricsData() {
		return this.metricsData;
	}

	public static Map<String, String> getSessionInfoMap() {
		Map<String, String> map = Maps.newHashMap();
		map.put("X-Minecraft-Username", getInstance().getSession().getUsername());
		map.put("X-Minecraft-UUID", getInstance().getSession().getUuid());
		map.put("X-Minecraft-Version", "1.8.9");
		return map;
	}

	public boolean isConnectedToRealms() {
		return this.connectedToRealms;
	}

	public void setConnectedToRealms(boolean connectedToRealms) {
		this.connectedToRealms = connectedToRealms;
	}
}
