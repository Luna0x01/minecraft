package net.minecraft.client;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
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
import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.ByteOrder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
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
import net.minecraft.class_3558;
import net.minecraft.class_4110;
import net.minecraft.class_4112;
import net.minecraft.class_4116;
import net.minecraft.class_4117;
import net.minecraft.class_4122;
import net.minecraft.class_4123;
import net.minecraft.class_4130;
import net.minecraft.class_4157;
import net.minecraft.class_4158;
import net.minecraft.class_4218;
import net.minecraft.class_4219;
import net.minecraft.class_4225;
import net.minecraft.class_4286;
import net.minecraft.class_4325;
import net.minecraft.class_4454;
import net.minecraft.class_4455;
import net.minecraft.class_4460;
import net.minecraft.class_4462;
import net.minecraft.class_4465;
import net.minecraft.class_4468;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.Framebuffer;
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
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.resource.ResourcePackLoader;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.Session;
import net.minecraft.client.world.BuiltChunk;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.datafixer.DataFixerFactory;
import net.minecraft.entity.EndCrystalEntity;
import net.minecraft.entity.Entity;
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
import net.minecraft.item.SkullItem;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.recipe.RecipeDispatcher;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.FoliageColorResourceReloadListener;
import net.minecraft.resource.GrassColorResourceReloadListener;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.stat.StatHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.KeyBindComponent;
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
import net.minecraft.util.registry.Registry;
import net.minecraft.util.snooper.Snoopable;
import net.minecraft.util.snooper.Snooper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.SaveHandler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.TheEndDimension;
import net.minecraft.world.dimension.TheNetherDimension;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.AnvilLevelStorage;
import net.minecraft.world.level.storage.LevelStorageAccess;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

public class MinecraftClient implements ThreadExecutor, Snoopable, class_4123 {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final boolean IS_MAC = Util.getOperatingSystem() == Util.OperatingSystem.MACOS;
	public static final Identifier field_19942 = new Identifier("default");
	public static final Identifier field_19943 = new Identifier("alt");
	public static byte[] memoryReservedForCrash = new byte[10485760];
	private static int field_19935 = -1;
	private final File resourcePackDir;
	private final PropertyMap sessionPropertyMap;
	private final RunArgs.WindowInformation field_19936;
	private ServerInfo currentServerEntry;
	private TextureManager textureManager;
	private static MinecraftClient instance;
	private final DataFixer field_19937;
	public ClientPlayerInteractionManager interactionManager;
	private class_4116 field_19938;
	public class_4117 field_19944;
	private boolean crashed;
	private CrashReport crashReport;
	private boolean connectedToRealms;
	private final ClientTickTracker ticker = new ClientTickTracker(20.0F, 0L);
	private final Snooper snooper = new Snooper("client", this, Util.method_20227());
	public ClientWorld world;
	public WorldRenderer worldRenderer;
	private EntityRenderDispatcher entityRenderDispatcher;
	private HeldItemRenderer field_19939;
	private class_4225 field_3760;
	public ClientPlayerEntity player;
	@Nullable
	private Entity cameraEntity;
	@Nullable
	public Entity targetedEntity;
	public ParticleManager particleManager;
	private final class_3306 field_15870 = new class_3306();
	private final Session session;
	private boolean paused;
	private float field_15871;
	public TextRenderer textRenderer;
	@Nullable
	public Screen currentScreen;
	public class_4218 field_3818;
	public DebugRenderer debugRenderer;
	int attackCooldown;
	@Nullable
	private IntegratedServer server;
	public InGameHud inGameHud;
	public boolean skipGameRender;
	public BlockHitResult result;
	public GameOptions options;
	private class_3251 field_15872;
	public class_4112 field_19945;
	public class_4110 field_19946;
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
	private int joinPlayerCounter;
	public final MetricsData metricsData = new MetricsData();
	private long nanoTime = Util.method_20230();
	private final boolean is64Bit;
	private final boolean isDemo;
	@Nullable
	private ClientConnection clientConnection;
	private boolean isIntegratedServerRunning;
	public final Profiler profiler = new Profiler();
	private ReloadableResourceManager resourceManager;
	private final ResourcePackLoader field_19940;
	private final class_4462<class_4286> field_19941;
	private LanguageManager languageManager;
	private BlockColors field_13278;
	private class_2838 field_13279;
	private Framebuffer fbo;
	private SpriteAtlasTexture texture;
	private SoundManager soundManager;
	private MusicTracker musicTracker;
	private class_4130 field_10307;
	private final MinecraftSessionService sessionService;
	private PlayerSkinProvider skinProvider;
	private final Queue<FutureTask<?>> tasks = Queues.newConcurrentLinkedQueue();
	private final Thread currentThread = Thread.currentThread();
	private BakedModelManager modelManager;
	private BlockRenderManager blockRenderManager;
	private final class_3264 field_15868;
	private volatile boolean running = true;
	public String fpsDebugString = "";
	public boolean chunkCullingEnabled = true;
	private long time;
	private int fpsCounter;
	private final class_3316 field_15869;
	boolean field_19934;
	private String openProfilerSection = "root";

	public MinecraftClient(RunArgs runArgs) {
		this.field_19936 = runArgs.windowInformation;
		instance = this;
		this.runDirectory = runArgs.directories.runDir;
		this.assetDirectory = runArgs.directories.assetDir;
		this.resourcePackDir = runArgs.directories.resourcePackDir;
		this.gameVersion = runArgs.game.version;
		this.versionType = runArgs.game.versionType;
		this.sessionPropertyMap = runArgs.args.profileProperties;
		this.field_19940 = new ResourcePackLoader(new File(this.runDirectory, "server-resource-packs"), runArgs.directories.getAssetsIndex());
		this.field_19941 = new class_4462<>((string, bl, supplier, arg, arg2, arg3) -> {
			Supplier<class_4454> supplier2;
			if (arg2.method_21337() < 4) {
				supplier2 = () -> new DefaultResourcePack((class_4454)supplier.get(), DefaultResourcePack.field_21041);
			} else {
				supplier2 = supplier;
			}

			return new class_4286(string, bl, supplier2, arg, arg2, arg3);
		});
		this.field_19941.method_21351(this.field_19940);
		this.field_19941.method_21351(new class_4460(this.resourcePackDir));
		this.networkProxy = runArgs.args.netProxy == null ? Proxy.NO_PROXY : runArgs.args.netProxy;
		this.sessionService = new YggdrasilAuthenticationService(this.networkProxy, UUID.randomUUID().toString()).createMinecraftSessionService();
		this.session = runArgs.args.session;
		LOGGER.info("Setting user: {}", this.session.getUsername());
		LOGGER.debug("(Session ID is {})", this.session.getSessionId());
		this.isDemo = runArgs.game.demo;
		this.is64Bit = checkIs64Bit();
		this.server = null;
		if (runArgs.autoConnect.serverIp != null) {
			this.serverAddress = runArgs.autoConnect.serverIp;
			this.serverPort = runArgs.autoConnect.serverPort;
		}

		Bootstrap.initialize();
		KeyBindComponent.field_16261 = KeyBinding::method_14453;
		this.field_19937 = DataFixerFactory.method_21531();
		this.field_15868 = new class_3264(this);
		this.field_15869 = new class_3316(this);
	}

	public void run() {
		this.running = true;

		try {
			this.initializeGame();
		} catch (Throwable var10) {
			CrashReport crashReport = CrashReport.create(var10, "Initializing game");
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
							this.method_18228(true);
						} catch (OutOfMemoryError var9) {
							this.cleanUpAfterCrash();
							this.setScreen(new OutOfMemoryScreen());
							System.gc();
						}
					}
				}

				return;
			} catch (CrashException var11) {
				this.addSystemDetailsToCrashReport(var11.getReport());
				this.cleanUpAfterCrash();
				LOGGER.fatal("Reported exception thrown!", var11);
				this.printCrashReport(var11.getReport());
			} catch (Throwable var12) {
				CrashReport crashReport2 = this.addSystemDetailsToCrashReport(new CrashReport("Unexpected error", var12));
				this.cleanUpAfterCrash();
				LOGGER.fatal("Unreported exception thrown!", var12);
				this.printCrashReport(crashReport2);
			}
		} finally {
			this.stop();
		}
	}

	private void initializeGame() {
		this.options = new GameOptions(this, this.runDirectory);
		this.field_15872 = new class_3251(this.runDirectory, this.field_19937);
		this.initializeTimerHackThread();
		LOGGER.info("LWJGL Version: {}", Version.getVersion());
		RunArgs.WindowInformation windowInformation = this.field_19936;
		if (this.options.overrideHeight > 0 && this.options.overrideWidth > 0) {
			windowInformation = new RunArgs.WindowInformation(
				this.options.overrideWidth, this.options.overrideHeight, windowInformation.field_20508, windowInformation.field_20509, windowInformation.checkGlErrors
			);
		}

		this.method_18223();
		this.field_19938 = new class_4116(this);
		this.field_19944 = this.field_19938.method_18291(windowInformation, this.options.field_19990);
		GLX.createContext();
		class_4219.method_19101(this.options.field_19981);
		this.fbo = new Framebuffer(this.field_19944.method_18317(), this.field_19944.method_18318(), true);
		this.fbo.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
		this.resourceManager = new class_4468(class_4455.CLIENT_RESOURCES);
		this.languageManager = new LanguageManager(this.options.language);
		this.resourceManager.registerListener(this.languageManager);
		this.options.method_18259(this.field_19941);
		this.reloadResources();
		this.textureManager = new TextureManager(this.resourceManager);
		this.resourceManager.registerListener(this.textureManager);
		this.field_19944.method_18314();
		this.setScreen(new class_4158());
		this.registerMetadataSerializers();
		this.skinProvider = new PlayerSkinProvider(this.textureManager, new File(this.assetDirectory, "skins"), this.sessionService);
		this.currentSave = new AnvilLevelStorage(this.runDirectory.toPath().resolve("saves"), this.runDirectory.toPath().resolve("backups"), this.field_19937);
		this.soundManager = new SoundManager(this.resourceManager, this.options);
		this.resourceManager.registerListener(this.soundManager);
		this.musicTracker = new MusicTracker(this);
		this.field_10307 = new class_4130(this.textureManager, this.method_18229());
		this.resourceManager.registerListener(this.field_10307);
		this.textRenderer = this.field_10307.method_18453(field_19942);
		if (this.options.language != null) {
			this.textRenderer.setRightToLeft(this.languageManager.isRightToLeft());
		}

		this.resourceManager.registerListener(new GrassColorResourceReloadListener());
		this.resourceManager.registerListener(new FoliageColorResourceReloadListener());
		this.field_19944.method_18299("Startup");
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
		this.field_19944.method_18299("Post startup");
		this.texture = new SpriteAtlasTexture("textures");
		this.texture.setMaxTextureSize(this.options.mipmapLevels);
		this.textureManager.loadTickableTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX, this.texture);
		this.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		this.texture.setFilter(false, this.options.mipmapLevels > 0);
		this.modelManager = new BakedModelManager(this.texture);
		this.resourceManager.registerListener(this.modelManager);
		this.field_13278 = BlockColors.create();
		this.field_13279 = class_2838.method_12161(this.field_13278);
		this.field_19939 = new HeldItemRenderer(this.textureManager, this.modelManager, this.field_13279);
		this.entityRenderDispatcher = new EntityRenderDispatcher(this.textureManager, this.field_19939);
		this.field_3760 = new class_4225(this);
		this.resourceManager.registerListener(this.field_19939);
		this.field_3818 = new class_4218(this, this.resourceManager);
		this.resourceManager.registerListener(this.field_3818);
		this.blockRenderManager = new BlockRenderManager(this.modelManager.getModelShapes(), this.field_13278);
		this.resourceManager.registerListener(this.blockRenderManager);
		this.worldRenderer = new WorldRenderer(this);
		this.resourceManager.registerListener(this.worldRenderer);
		this.method_14464();
		this.resourceManager.registerListener(this.field_15870);
		GlStateManager.viewport(0, 0, this.field_19944.method_18317(), this.field_19944.method_18318());
		this.particleManager = new ParticleManager(this.world, this.textureManager);
		this.inGameHud = new InGameHud(this);
		if (this.serverAddress != null) {
			this.setScreen(new ConnectScreen(new TitleScreen(), this, this.serverAddress, this.serverPort));
		} else {
			this.setScreen(new TitleScreen());
		}

		this.debugRenderer = new DebugRenderer(this);
		GLFW.glfwSetErrorCallback(this::method_18202).free();
		if (this.options.fullscreen && !this.field_19944.method_18316()) {
			this.field_19944.method_18313();
		}

		this.field_19944.method_18306();
		this.field_19944.method_18302();
		this.worldRenderer.setupEntityOutlineShader();
	}

	private void method_18223() {
		class_4117.method_18300((integer, stringx) -> {
			throw new IllegalStateException(String.format("GLFW error before init: [0x%X]%s", integer, stringx));
		});
		List<String> list = Lists.newArrayList();
		GLFWErrorCallback gLFWErrorCallback = GLFW.glfwSetErrorCallback((i, l) -> list.add(String.format("GLFW error during init: [0x%X]%s", i, l)));
		if (!GLFW.glfwInit()) {
			throw new IllegalStateException("Failed to initialize GLFW, errors: " + Joiner.on(",").join(list));
		} else {
			Util.field_21541 = () -> (long)(GLFW.glfwGetTime() * 1.0E9);

			for (String string : list) {
				LOGGER.error("GLFW error collected during initialization: {}", string);
			}

			GLFW.glfwSetErrorCallback(gLFWErrorCallback).free();
		}
	}

	private void method_14464() {
		class_3304<ItemStack> lv = new class_3304<>(
			itemStack -> (List)itemStack.getTooltip(null, TooltipContext.TooltipType.NORMAL)
					.stream()
					.map(text -> Formatting.strip(text.getString()).trim())
					.filter(string -> !string.isEmpty())
					.collect(Collectors.toList()),
			itemStack -> Collections.singleton(Registry.ITEM.getId(itemStack.getItem()))
		);
		DefaultedList<ItemStack> defaultedList = DefaultedList.of();

		for (Item item : Registry.ITEM) {
			item.appendToItemGroup(ItemGroup.SEARCH, defaultedList);
		}

		defaultedList.forEach(lv::method_14701);
		class_3304<class_3286> lv2 = new class_3304<>(
			arg -> (List)arg.method_14634()
					.stream()
					.flatMap(recipeType -> recipeType.getOutput().getTooltip(null, TooltipContext.TooltipType.NORMAL).stream())
					.map(text -> Formatting.strip(text.getString()).trim())
					.filter(string -> !string.isEmpty())
					.collect(Collectors.toList()),
			arg -> (List)arg.method_14634().stream().map(recipeType -> Registry.ITEM.getId(recipeType.getOutput().getItem())).collect(Collectors.toList())
		);
		this.field_15870.method_14706(class_3306.field_16177, lv);
		this.field_15870.method_14706(class_3306.field_16178, lv2);
	}

	private void method_18202(int i, long l) {
		this.options.field_19991 = false;
		this.options.save();
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
		thread.setUncaughtExceptionHandler(new class_4325(LOGGER));
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

	public boolean method_18229() {
		return this.options.forcesUnicodeFont;
	}

	public void reloadResources() {
		this.field_19941.method_21347();
		List<class_4454> list = (List<class_4454>)this.field_19941.method_21354().stream().map(class_4465::method_21364).collect(Collectors.toList());
		if (this.server != null) {
			this.server.method_14912();
		}

		try {
			this.resourceManager.reload(list);
		} catch (RuntimeException var4) {
			LOGGER.info("Caught error stitching, removing all assigned resourcepacks", var4);
			this.field_19941.method_21349(Collections.emptyList());
			List<class_4454> list2 = (List<class_4454>)this.field_19941.method_21354().stream().map(class_4465::method_21364).collect(Collectors.toList());
			this.resourceManager.reload(list2);
			this.options.resourcePacks.clear();
			this.options.incompatibleResourcePacks.clear();
			this.options.save();
		}

		this.languageManager.reloadResourceLanguages(list);
		if (this.worldRenderer != null) {
			this.worldRenderer.reload();
		}
	}

	private void registerMetadataSerializers() {
		this.field_19944.method_18293();
		this.currentScreen.render(0, 0, 0.0F);
		this.field_19944.method_18301(false);
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

	@Nullable
	@Override
	public class_4122 getFocused() {
		return this.currentScreen;
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
			this.field_19945.method_18254();
			KeyBinding.releaseAllKeys();
			screen.init(this, this.field_19944.method_18321(), this.field_19944.method_18322());
			this.skipGameRender = false;
		} else {
			this.soundManager.resumeAll();
			this.field_19945.method_18253();
		}
	}

	public void stop() {
		try {
			LOGGER.info("Stopping!");

			try {
				this.connect(null);
			} catch (Throwable var5) {
			}

			if (this.currentScreen != null) {
				this.currentScreen.removed();
			}

			this.texture.method_19516();
			this.textRenderer.close();
			this.field_3818.close();
			this.worldRenderer.close();
			this.soundManager.close();
		} finally {
			this.field_19938.close();
			this.field_19944.close();
			if (!this.crashed) {
				System.exit(0);
			}
		}

		System.gc();
	}

	private void method_18228(boolean bl) {
		this.field_19944.method_18299("Pre render");
		long l = Util.method_20230();
		this.profiler.push("root");
		if (GLFW.glfwWindowShouldClose(this.field_19944.method_18315())) {
			this.scheduleStop();
		}

		if (bl) {
			this.ticker.method_18274(Util.method_20227());
			this.profiler.push("scheduledExecutables");

			FutureTask<?> futureTask;
			while ((futureTask = (FutureTask<?>)this.tasks.poll()) != null) {
				Util.executeTask(futureTask, LOGGER);
			}

			this.profiler.pop();
		}

		long m = Util.method_20230();
		if (bl) {
			this.profiler.push("tick");

			for (int i = 0; i < Math.min(10, this.ticker.ticksThisFrame); i++) {
				this.tick();
			}
		}

		this.field_19945.method_18239();
		this.field_19944.method_18299("Render");
		GLFW.glfwPollEvents();
		long n = Util.method_20230() - m;
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
			this.field_3818.method_19061(this.paused ? this.field_15871 : this.ticker.tickDelta, l, bl);
			this.profiler.swap("toasts");
			this.field_15868.method_18450();
			this.profiler.pop();
		}

		this.profiler.pop();
		if (this.options.debugEnabled && this.options.field_19982 && !this.options.field_19987) {
			this.profiler.method_21520(this.ticker.ticksThisFrame);
			this.method_18224();
		} else {
			this.profiler.method_21521();
		}

		this.fbo.unbind();
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		this.fbo.draw(this.field_19944.method_18317(), this.field_19944.method_18318());
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		this.field_3818.method_19073(this.ticker.tickDelta);
		GlStateManager.popMatrix();
		this.profiler.push("root");
		this.field_19944.method_18301(true);
		Thread.yield();
		this.field_19944.method_18299("Post render");
		this.fpsCounter++;
		boolean bl2 = this.isInSingleplayer() && this.currentScreen != null && this.currentScreen.shouldPauseGame() && !this.server.shouldBroadcastConsoleToIps();
		if (this.paused != bl2) {
			if (this.paused) {
				this.field_15871 = this.ticker.tickDelta;
			} else {
				this.ticker.tickDelta = this.field_15871;
			}

			this.paused = bl2;
		}

		long o = Util.method_20230();
		this.metricsData.pushSample(o - this.nanoTime);
		this.nanoTime = o;

		while (Util.method_20227() >= this.time + 1000L) {
			currentFps = this.fpsCounter;
			this.fpsDebugString = String.format(
				"%d fps (%d chunk update%s) T: %s%s%s%s%s",
				currentFps,
				BuiltChunk.chunkUpdates,
				BuiltChunk.chunkUpdates == 1 ? "" : "s",
				(double)this.options.maxFramerate == GameOptions.Option.MAX_FPS.method_18267() ? "inf" : this.options.maxFramerate,
				this.options.field_19991 ? " vsync" : "",
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

		this.profiler.pop();
	}

	public void cleanUpAfterCrash() {
		try {
			memoryReservedForCrash = new byte[0];
			this.worldRenderer.cleanUp();
		} catch (Throwable var3) {
		}

		try {
			System.gc();
			this.method_18206(null, new class_4157(I18n.translate("menu.savingLevel")));
		} catch (Throwable var2) {
		}

		System.gc();
	}

	void handleProfilerKeyPress(int digit) {
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

	private void method_18224() {
		if (this.profiler.method_21519()) {
			List<Profiler.Section> list = this.profiler.getData(this.openProfilerSection);
			Profiler.Section section = (Profiler.Section)list.remove(0);
			GlStateManager.clear(256);
			GlStateManager.matrixMode(5889);
			GlStateManager.enableColorMaterial();
			GlStateManager.loadIdentity();
			GlStateManager.ortho(0.0, (double)this.field_19944.method_18317(), (double)this.field_19944.method_18318(), 0.0, 1000.0, 3000.0);
			GlStateManager.matrixMode(5888);
			GlStateManager.loadIdentity();
			GlStateManager.translate(0.0F, 0.0F, -2000.0F);
			GlStateManager.method_12304(1.0F);
			GlStateManager.disableTexture();
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			int i = 160;
			int j = this.field_19944.method_18317() - 160 - 10;
			int k = this.field_19944.method_18318() - 320;
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
			decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
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

	public void openGameMenuScreen() {
		if (this.currentScreen == null) {
			this.setScreen(new GameMenuScreen());
			if (this.isInSingleplayer() && !this.server.shouldBroadcastConsoleToIps()) {
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
				if (!this.world.getBlockState(blockPos).isAir() && this.interactionManager.updateBlockBreakingProgress(blockPos, this.result.direction)) {
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
						if (!this.world.getBlockState(blockPos).isAir()) {
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
								if (!this.world.getBlockState(blockPos).isAir()) {
									int i = itemStack.getCount();
									ActionResult actionResult = this.interactionManager.method_13842(this.player, this.world, blockPos, this.result.direction, this.result.pos, hand);
									if (actionResult == ActionResult.SUCCESS) {
										this.player.swingHand(hand);
										if (!itemStack.isEmpty() && (itemStack.getCount() != i || this.interactionManager.hasCreativeInventory())) {
											this.field_3818.field_20676.method_19136(hand);
										}

										return;
									}

									if (actionResult == ActionResult.FAIL) {
										return;
									}
								}
						}
					}

					if (!itemStack.isEmpty() && this.interactionManager.method_12234(this.player, this.world, hand) == ActionResult.SUCCESS) {
						this.field_3818.field_20676.method_19136(hand);
						return;
					}
				}
			}
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
		this.field_3818.method_19059(1.0F);
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
			Screen.method_18605(() -> this.currentScreen.tick(), "Ticking screen", this.currentScreen.getClass().getCanonicalName());
		}

		if (this.currentScreen == null || this.currentScreen.passEvents) {
			this.profiler.swap("GLFW events");
			GLFW.glfwPollEvents();
			this.method_12140();
			if (this.attackCooldown > 0) {
				this.attackCooldown--;
			}
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
				this.field_3818.method_19080();
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
		} else if (this.field_3818.method_19058()) {
			this.field_3818.method_19072();
		}

		if (!this.paused) {
			this.musicTracker.tick();
			this.soundManager.tick();
		}

		if (this.world != null) {
			if (!this.paused) {
				this.world.setMobSpawning(this.world.method_16346() != Difficulty.PEACEFUL, true);
				this.field_15869.method_14728();

				try {
					this.world.method_16327(() -> true);
				} catch (Throwable var4) {
					CrashReport crashReport = CrashReport.create(var4, "Exception in world tick");
					if (this.world == null) {
						CrashReportSection crashReportSection = crashReport.addElement("Affected level");
						crashReportSection.add("Problem", "Level is null!");
					} else {
						this.world.addToCrashReport(crashReport);
					}

					throw new CrashException(crashReport);
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

		this.profiler.swap("keyboard");
		this.field_19946.method_18193();
		this.profiler.pop();
	}

	private void method_12140() {
		while (this.options.togglePerspectiveKey.wasPressed()) {
			this.options.perspective++;
			if (this.options.perspective > 2) {
				this.options.perspective = 0;
			}

			if (this.options.perspective == 0) {
				this.field_3818.method_19065(this.getCameraEntity());
			} else if (this.options.perspective == 1) {
				this.field_3818.method_19065(null);
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

		this.handleBlockBreaking(this.currentScreen == null && this.options.attackKey.isPressed() && this.field_19945.method_18252());
	}

	public void startIntegratedServer(String worldName, String levelName, @Nullable LevelInfo levelInfo) {
		this.connect(null);
		System.gc();
		SaveHandler saveHandler = this.currentSave.method_250(worldName, null);
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

		ProgressScreen progressScreen = new ProgressScreen();
		this.setScreen(progressScreen);
		progressScreen.method_21524(new TranslatableText("menu.loadingLevel"));

		while (!this.server.isLoading()) {
			Text text = this.server.method_3015();
			if (text != null) {
				Text text2 = this.server.method_20328();
				if (text2 != null) {
					progressScreen.method_21526(text2);
					progressScreen.setProgressPercentage(this.server.method_20329());
				} else {
					progressScreen.method_21526(text);
				}
			} else {
				progressScreen.method_21526(new LiteralText(""));
			}

			this.method_18228(false);

			try {
				Thread.sleep(200L);
			} catch (InterruptedException var10) {
			}

			if (this.crashed && this.crashReport != null) {
				this.printCrashReport(this.crashReport);
				return;
			}
		}

		SocketAddress socketAddress = this.server.getNetworkIo().bindLocal();
		ClientConnection clientConnection = ClientConnection.connectLocal(socketAddress);
		clientConnection.setPacketListener(new ClientLoginNetworkHandler(clientConnection, this, null, textx -> {
		}));
		clientConnection.send(new HandshakeC2SPacket(socketAddress.toString(), 0, NetworkState.LOGIN));
		clientConnection.send(new LoginHelloC2SPacket(this.getSession().getProfile()));
		this.clientConnection = clientConnection;
	}

	public void connect(@Nullable ClientWorld world) {
		ProgressScreen progressScreen = new ProgressScreen();
		if (world != null) {
			progressScreen.method_21524(new TranslatableText("connect.joining"));
		}

		this.method_18206(world, progressScreen);
	}

	public void method_18206(@Nullable ClientWorld clientWorld, Screen screen) {
		if (clientWorld == null) {
			ClientPlayNetworkHandler clientPlayNetworkHandler = this.getNetworkHandler();
			if (clientPlayNetworkHandler != null) {
				this.tasks.clear();
				clientPlayNetworkHandler.clearWorld();
			}

			this.server = null;
			this.field_3818.method_19089();
			this.interactionManager = null;
			class_3253.field_15887.method_14475();
		}

		this.musicTracker.method_19622();
		this.soundManager.stopAll();
		this.cameraEntity = null;
		this.clientConnection = null;
		this.setScreen(screen);
		this.method_18228(false);
		if (clientWorld == null && this.world != null) {
			this.field_19940.clear();
			this.inGameHud.resetDebugHudChunk();
			this.setCurrentServerEntry(null);
			this.isIntegratedServerRunning = false;
		}

		this.world = clientWorld;
		if (this.worldRenderer != null) {
			this.worldRenderer.setWorld(clientWorld);
		}

		if (this.particleManager != null) {
			this.particleManager.setWorld(clientWorld);
		}

		BlockEntityRenderDispatcher.INSTANCE.setWorld(clientWorld);
		if (clientWorld != null) {
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
				this.player = this.interactionManager.method_9658(clientWorld, new StatHandler(), new class_3320(clientWorld.method_16313()));
				this.interactionManager.flipPlayer(this.player);
				if (this.server != null) {
					this.server.method_19612(this.player.getUuid());
				}
			}

			this.player.afterSpawn();
			clientWorld.method_3686(this.player);
			this.player.input = new KeyboardInput(this.options);
			this.interactionManager.copyAbilities(this.player);
			this.cameraEntity = this.player;
		} else {
			this.player = null;
		}

		System.gc();
	}

	public void method_18204(DimensionType dimensionType) {
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
				this.world,
				this.player == null ? new StatHandler() : this.player.getStatHandler(),
				this.player == null ? new class_3320(new RecipeDispatcher()) : this.player.method_14675()
			);
		this.player.getDataTracker().writeUpdatedEntries(clientPlayerEntity.getDataTracker().getEntries());
		this.player.field_16696 = dimensionType;
		this.cameraEntity = this.player;
		this.player.afterSpawn();
		this.player.setServerBrand(string);
		this.world.method_3686(this.player);
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
		return instance == null || !instance.options.field_19987;
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
				if (blockState.isAir()) {
					return;
				}

				itemStack = block.getPickBlock(this.world, blockPos, blockState);
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
					class_3558 lv = class_3558.method_16126(this.result.entity.method_15557());
					if (lv == null) {
						return;
					}

					itemStack = new ItemStack(lv);
				}
			}

			if (itemStack.isEmpty()) {
				String string = "";
				if (this.result.type == BlockHitResult.Type.BLOCK) {
					string = Registry.BLOCK.getId(this.world.getBlockState(this.result.getBlockPos()).getBlock()).toString();
				} else if (this.result.type == BlockHitResult.Type.ENTITY) {
					string = Registry.ENTITY_TYPE.getId(this.result.entity.method_15557()).toString();
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
		if (itemStack.getItem() instanceof SkullItem && nbtCompound.contains("Owner")) {
			NbtCompound nbtCompound2 = nbtCompound.getCompound("Owner");
			itemStack.getOrCreateNbt().put("SkullOwner", nbtCompound2);
			return itemStack;
		} else {
			itemStack.addNbt("BlockEntityTag", nbtCompound);
			NbtCompound nbtCompound3 = new NbtCompound();
			NbtList nbtList = new NbtList();
			nbtList.add((NbtElement)(new NbtString("(+NBT)")));
			nbtCompound3.put("Lore", nbtList);
			itemStack.addNbt("display", nbtCompound3);
			return itemStack;
		}
	}

	public CrashReport addSystemDetailsToCrashReport(CrashReport crashReport) {
		CrashReportSection crashReportSection = crashReport.getSystemDetailsSection();
		crashReportSection.add("Launched Version", (CrashCallable<String>)(() -> this.gameVersion));
		crashReportSection.add("LWJGL", Version::getVersion);
		crashReportSection.add(
			"OpenGL",
			(CrashCallable<String>)(() -> GLFW.glfwGetCurrentContext() == 0L
					? "NO CONTEXT"
					: GlStateManager.method_12320(7937) + " GL version " + GlStateManager.method_12320(7938) + ", " + GlStateManager.method_12320(7936))
		);
		crashReportSection.add("GL Caps", GLX::getContextDescription);
		crashReportSection.add("Using VBOs", (CrashCallable<String>)(() -> this.options.vbo ? "Yes" : "No"));
		crashReportSection.add(
			"Is Modded",
			(CrashCallable<String>)(() -> {
				String string = ClientBrandRetriever.getClientModName();
				if (!"vanilla".equals(string)) {
					return "Definitely; Client brand changed to '" + string + "'";
				} else {
					return MinecraftClient.class.getSigners() == null
						? "Very likely; Jar signature invalidated"
						: "Probably not. Jar signature remains and client brand is untouched.";
				}
			})
		);
		crashReportSection.add("Type", "Client (map_client.txt)");
		crashReportSection.add("Resource Packs", (CrashCallable<String>)(() -> {
			StringBuilder stringBuilder = new StringBuilder();

			for (String string : this.options.resourcePacks) {
				if (stringBuilder.length() > 0) {
					stringBuilder.append(", ");
				}

				stringBuilder.append(string);
				if (this.options.incompatibleResourcePacks.contains(string)) {
					stringBuilder.append(" (incompatible)");
				}
			}

			return stringBuilder.toString();
		}));
		crashReportSection.add("Current Language", (CrashCallable<String>)(() -> this.languageManager.getLanguage().toString()));
		crashReportSection.add(
			"Profiler Position", (CrashCallable<String>)(() -> this.profiler.method_21519() ? this.profiler.getCurrentLocation() : "N/A (disabled)")
		);
		crashReportSection.add("CPU", GLX::getProcessor);
		if (this.world != null) {
			this.world.addToCrashReport(crashReport);
		}

		return crashReport;
	}

	public static MinecraftClient getInstance() {
		return instance;
	}

	public ListenableFuture<Object> reloadResourcesConcurrently() {
		return this.submit(this::reloadResources);
	}

	@Override
	public void addSnooperInfo(Snooper snooper) {
		snooper.addGameInfo("fps", currentFps);
		snooper.addGameInfo("vsync_enabled", this.options.field_19991);
		long l = GLFW.glfwGetWindowMonitor(this.field_19944.method_18315());
		if (l == 0L) {
			l = GLFW.glfwGetPrimaryMonitor();
		}

		snooper.addGameInfo("display_frequency", GLFW.glfwGetVideoMode(l).refreshRate());
		snooper.addGameInfo("display_type", this.field_19944.method_18316() ? "fullscreen" : "windowed");
		snooper.addGameInfo("run_time", (Util.method_20227() - snooper.getStartTime()) / 60L * 1000L);
		snooper.addGameInfo("current_action", this.getCurrentAction());
		snooper.addGameInfo("language", this.options.language == null ? "en_us" : this.options.language);
		String string = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? "little" : "big";
		snooper.addGameInfo("endianness", string);
		snooper.addGameInfo("subtitles", this.options.field_13292);
		snooper.addGameInfo("touch", this.options.touchscreen ? "touch" : "mouse");
		int i = 0;

		for (class_4286 lv : this.field_19941.method_21354()) {
			if (!lv.method_21366() && !lv.method_21367()) {
				snooper.addGameInfo("resource_pack[" + i++ + "]", lv.method_21365());
			}
		}

		snooper.addGameInfo("resource_packs", i);
		if (this.server != null && this.server.getSnooper() != null) {
			snooper.addGameInfo("snooper_partner", this.server.getSnooper().getSnooperToken());
		}
	}

	private String getCurrentAction() {
		if (this.server != null) {
			return this.server.shouldBroadcastConsoleToIps() ? "hosting_lan" : "singleplayer";
		} else if (this.currentServerEntry != null) {
			return this.currentServerEntry.isLocal() ? "playing_lan" : "multiplayer";
		} else {
			return "out_of_game";
		}
	}

	public static int getMaxTextureSize() {
		if (field_19935 == -1) {
			for (int i = 16384; i > 0; i >>= 1) {
				GlStateManager.method_12276(32868, 0, 6408, i, i, 0, 6408, 5121, null);
				int j = GlStateManager.method_12301(32868, 0, 4096);
				if (j != 0) {
					field_19935 = i;
					return i;
				}
			}
		}

		return field_19935;
	}

	public boolean method_2409() {
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

	public class_4462<class_4286> method_18199() {
		return this.field_19941;
	}

	public ResourcePackLoader getResourcePackLoader() {
		return this.field_19940;
	}

	public File method_18200() {
		return this.resourcePackDir;
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
		} else if (this.player == null) {
			return MusicTracker.MusicType.MENU;
		} else if (this.player.world.dimension instanceof TheNetherDimension) {
			return MusicTracker.MusicType.NETHER;
		} else if (this.player.world.dimension instanceof TheEndDimension) {
			return this.inGameHud.method_12167().method_12172() ? MusicTracker.MusicType.END_BOSS : MusicTracker.MusicType.END;
		} else {
			Biome.Category category = this.player.world.method_8577(new BlockPos(this.player.x, this.player.y, this.player.z)).getCategory();
			if (!this.musicTracker.method_19623(MusicTracker.MusicType.UNDER_WATER)
				&& (
					!this.player.method_15576()
						|| this.musicTracker.method_19623(MusicTracker.MusicType.GAME)
						|| category != Biome.Category.OCEAN && category != Biome.Category.RIVER
				)) {
				return this.player.abilities.creativeMode && this.player.abilities.allowFlying ? MusicTracker.MusicType.CREATIVE : MusicTracker.MusicType.GAME;
			} else {
				return MusicTracker.MusicType.UNDER_WATER;
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
		this.field_3818.method_19065(entity);
	}

	public <V> ListenableFuture<V> execute(Callable<V> task) {
		Validate.notNull(task);
		if (this.isOnThread()) {
			try {
				return Futures.immediateFuture(task.call());
			} catch (Exception var3) {
				return Futures.immediateFailedCheckedFuture(var3);
			}
		} else {
			ListenableFutureTask<V> listenableFutureTask = ListenableFutureTask.create(task);
			this.tasks.add(listenableFutureTask);
			return listenableFutureTask;
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

	public HeldItemRenderer getHeldItemRenderer() {
		return this.field_19939;
	}

	public class_4225 method_18201() {
		return this.field_3760;
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

	public DataFixer method_12142() {
		return this.field_19937;
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

	public boolean isFullscreen() {
		return this.field_19934;
	}

	public class_3251 method_18221() {
		return this.field_15872;
	}

	public BakedModelManager method_18222() {
		return this.modelManager;
	}

	public class_4130 method_9391() {
		return this.field_10307;
	}
}
