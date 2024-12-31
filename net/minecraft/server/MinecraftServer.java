package net.minecraft.server;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import net.minecraft.Bootstrap;
import net.minecraft.class_3457;
import net.minecraft.class_3595;
import net.minecraft.class_3893;
import net.minecraft.class_3915;
import net.minecraft.class_4070;
import net.minecraft.class_4325;
import net.minecraft.class_4403;
import net.minecraft.class_4454;
import net.minecraft.class_4455;
import net.minecraft.class_4460;
import net.minecraft.class_4462;
import net.minecraft.class_4464;
import net.minecraft.class_4465;
import net.minecraft.class_4468;
import net.minecraft.class_4488;
import net.minecraft.achievement.class_3348;
import net.minecraft.datafixer.DataFixerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.class_2787;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.recipe.RecipeDispatcher;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.function.FunctionTickable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ServerWorldManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.ThreadExecutor;
import net.minecraft.util.Tickable;
import net.minecraft.util.UserCache;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.snooper.Snoopable;
import net.minecraft.util.snooper.Snooper;
import net.minecraft.world.DemoServerWorld;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRuleManager;
import net.minecraft.world.MultiServerWorld;
import net.minecraft.world.SaveHandler;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.AnvilLevelStorage;
import net.minecraft.world.level.storage.LevelStorageAccess;
import net.minecraft.world.level.storage.WorldSaveException;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class MinecraftServer implements ThreadExecutor, Snoopable, class_3893, Runnable {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final File USER_CACHE_FILE = new File("usercache.json");
	private final LevelStorageAccess saveStorage;
	private final Snooper field_13903 = new Snooper("server", this, Util.method_20227());
	private final File gameDir;
	private final List<Tickable> tickables = Lists.newArrayList();
	public final Profiler profiler = new Profiler();
	private final ServerNetworkIo networkIo;
	private final ServerMetadata serverMetadata = new ServerMetadata();
	private final Random random = new Random();
	private final DataFixer field_21612;
	private String serverIp;
	private int serverPort = -1;
	private final Map<DimensionType, ServerWorld> field_21613 = Maps.newIdentityHashMap();
	private PlayerManager playerManager;
	private boolean running = true;
	private boolean stopped;
	private int ticks;
	protected final Proxy proxy;
	private Text field_21614;
	private int progress;
	private boolean onlineMode;
	private boolean field_15357;
	private boolean spawnAnimals;
	private boolean spawnNpcs;
	private boolean pvpEnabled;
	private boolean flightEnabled;
	private String motd;
	private int worldHeight;
	private int playerIdleTimeout;
	public final long[] lastTickLengths = new long[100];
	protected final Map<DimensionType, long[]> field_21611 = Maps.newIdentityHashMap();
	private KeyPair keyPair;
	private String userName;
	private String levelName;
	private String displayName;
	private boolean demo;
	private boolean forceWorldUpgrade;
	private String resourcePackUrl = "";
	private String resourcePackHash = "";
	private boolean loading;
	private long lastWarnTime;
	private Text field_21596;
	private boolean profiling;
	private boolean forceGameMode;
	private final YggdrasilAuthenticationService authService;
	private final MinecraftSessionService sessionService;
	private final GameProfileRepository gameProfileRepo;
	private final UserCache userCache;
	private long lastPlayerSampleUpdate;
	public final Queue<FutureTask<?>> queue = Queues.newConcurrentLinkedQueue();
	private Thread serverThread;
	private long timeReference = Util.method_20227();
	private boolean field_13902;
	private final ReloadableResourceManager field_21597 = new class_4468(class_4455.SERVER_DATA);
	private final class_4462<class_4465> field_21598 = new class_4462<>(class_4465::new);
	private class_4460 field_21599;
	private final CommandManager field_21600;
	private final RecipeDispatcher field_21601 = new RecipeDispatcher();
	private final class_4488 field_21602 = new class_4488();
	private final ServerScoreboard field_21603 = new ServerScoreboard(this);
	private final class_4403 field_21604 = new class_4403(this);
	private final class_2787 field_21605 = new class_2787();
	private final class_3348 field_21606 = new class_3348();
	private final FunctionTickable field_21607 = new FunctionTickable(this);
	private boolean field_21608;
	private boolean field_21609;
	private float field_21610;

	public MinecraftServer(
		@Nullable File file,
		Proxy proxy,
		DataFixer dataFixer,
		CommandManager commandManager,
		YggdrasilAuthenticationService yggdrasilAuthenticationService,
		MinecraftSessionService minecraftSessionService,
		GameProfileRepository gameProfileRepository,
		UserCache userCache
	) {
		this.proxy = proxy;
		this.field_21600 = commandManager;
		this.authService = yggdrasilAuthenticationService;
		this.sessionService = minecraftSessionService;
		this.gameProfileRepo = gameProfileRepository;
		this.userCache = userCache;
		this.gameDir = file;
		this.networkIo = file == null ? null : new ServerNetworkIo(this);
		this.saveStorage = file == null ? null : new AnvilLevelStorage(file.toPath(), file.toPath().resolve("../backups"), dataFixer);
		this.field_21612 = dataFixer;
		this.field_21597.registerListener(this.field_21602);
		this.field_21597.registerListener(this.field_21601);
		this.field_21597.registerListener(this.field_21605);
		this.field_21597.registerListener(this.field_21607);
		this.field_21597.registerListener(this.field_21606);
	}

	public abstract boolean setupServer() throws IOException;

	public void upgradeWorld(String name) {
		if (this.getSaveStorage().needsConversion(name)) {
			LOGGER.info("Converting map!");
			this.method_20346(new TranslatableText("menu.convertingLevel"));
			this.getSaveStorage().convert(name, new ProgressListener() {
				private long lastProgressUpdate = Util.method_20227();

				@Override
				public void method_21524(Text text) {
				}

				@Override
				public void method_21525(Text text) {
				}

				@Override
				public void setProgressPercentage(int percentage) {
					if (Util.method_20227() - this.lastProgressUpdate >= 1000L) {
						this.lastProgressUpdate = Util.method_20227();
						MinecraftServer.LOGGER.info("Converting... {}%", percentage);
					}
				}

				@Override
				public void setDone() {
				}

				@Override
				public void method_21526(Text text) {
				}
			});
		}

		if (this.field_21609) {
			LOGGER.info("Forcing world upgrade!");
			LevelProperties levelProperties = this.getSaveStorage().getLevelProperties(this.getLevelName());
			if (levelProperties != null) {
				class_3457 lv = new class_3457(this.getLevelName(), this.getSaveStorage(), levelProperties);
				Text text = null;

				while (!lv.method_15524()) {
					Text text2 = lv.method_15529();
					if (text != text2) {
						text = text2;
						LOGGER.info(lv.method_15529().getString());
					}

					int i = lv.method_15526();
					if (i > 0) {
						int j = lv.method_15527() + lv.method_15528();
						LOGGER.info("{}% completed ({} / {} chunks)...", MathHelper.floor((float)j / (float)i * 100.0F), j, i);
					}

					if (this.isStopped()) {
						lv.method_15520();
					} else {
						try {
							Thread.sleep(1000L);
						} catch (InterruptedException var8) {
						}
					}
				}
			}
		}
	}

	protected synchronized void method_20346(Text text) {
		this.field_21596 = text;
	}

	@Nullable
	public synchronized Text method_3015() {
		return this.field_21596;
	}

	public void method_20320(String string, String string2, long l, LevelGeneratorType levelGeneratorType, JsonElement jsonElement) {
		this.upgradeWorld(string);
		this.method_20346(new TranslatableText("menu.loadingLevel"));
		SaveHandler saveHandler = this.getSaveStorage().method_250(string, this);
		this.loadResourcePack(this.getLevelName(), saveHandler);
		LevelProperties levelProperties = saveHandler.getLevelProperties();
		LevelInfo levelInfo;
		if (levelProperties == null) {
			if (this.isDemo()) {
				levelInfo = DemoServerWorld.INFO;
			} else {
				levelInfo = new LevelInfo(l, this.method_3026(), this.shouldGenerateStructures(), this.isHardcore(), levelGeneratorType);
				levelInfo.method_16395(jsonElement);
				if (this.forceWorldUpgrade) {
					levelInfo.setBonusChest();
				}
			}

			levelProperties = new LevelProperties(levelInfo, string2);
		} else {
			levelProperties.setLevelName(string2);
			levelInfo = new LevelInfo(levelProperties);
		}

		this.method_20319(saveHandler.getWorldFolder(), levelProperties);
		class_4070 lv = new class_4070(saveHandler);
		this.method_20316(saveHandler, lv, levelProperties, levelInfo);
		this.setDifficulty(this.getDefaultDifficulty());
		this.method_20317(lv);
	}

	public void method_20316(SaveHandler saveHandler, class_4070 arg, LevelProperties levelProperties, LevelInfo levelInfo) {
		if (this.isDemo()) {
			this.field_21613
				.put(DimensionType.OVERWORLD, new DemoServerWorld(this, saveHandler, arg, levelProperties, DimensionType.OVERWORLD, this.profiler).method_21265());
		} else {
			this.field_21613
				.put(DimensionType.OVERWORLD, new ServerWorld(this, saveHandler, arg, levelProperties, DimensionType.OVERWORLD, this.profiler).method_21265());
		}

		ServerWorld serverWorld = this.method_20312(DimensionType.OVERWORLD);
		serverWorld.setPropertiesInitialized(levelInfo);
		serverWorld.addListener(new ServerWorldManager(this, serverWorld));
		if (!this.isSinglePlayer()) {
			serverWorld.method_3588().getGameMode(this.method_3026());
		}

		MultiServerWorld multiServerWorld = new MultiServerWorld(this, saveHandler, DimensionType.THE_NETHER, serverWorld, this.profiler).method_21265();
		this.field_21613.put(DimensionType.THE_NETHER, multiServerWorld);
		multiServerWorld.addListener(new ServerWorldManager(this, multiServerWorld));
		if (!this.isSinglePlayer()) {
			multiServerWorld.method_3588().getGameMode(this.method_3026());
		}

		MultiServerWorld multiServerWorld2 = new MultiServerWorld(this, saveHandler, DimensionType.THE_END, serverWorld, this.profiler).method_21265();
		this.field_21613.put(DimensionType.THE_END, multiServerWorld2);
		multiServerWorld2.addListener(new ServerWorldManager(this, multiServerWorld2));
		if (!this.isSinglePlayer()) {
			multiServerWorld2.method_3588().getGameMode(this.method_3026());
		}

		this.getPlayerManager().method_21387(serverWorld);
		if (levelProperties.method_17953() != null) {
			this.method_20336().method_20478(levelProperties.method_17953());
		}
	}

	public void method_20319(File file, LevelProperties levelProperties) {
		this.field_21598.method_21351(new class_4464());
		this.field_21599 = new class_4460(new File(file, "datapacks"));
		this.field_21598.method_21351(this.field_21599);
		this.field_21598.method_21347();
		List<class_4465> list = Lists.newArrayList();

		for (String string : levelProperties.method_17952()) {
			class_4465 lv = this.field_21598.method_21348(string);
			if (lv != null) {
				list.add(lv);
			} else {
				LOGGER.warn("Missing data pack {}", string);
			}
		}

		this.field_21598.method_21349(list);
		this.method_20314(levelProperties);
	}

	public void method_20317(class_4070 arg) {
		int i = 16;
		int j = 4;
		int k = 12;
		int l = 192;
		int m = 625;
		this.method_20346(new TranslatableText("menu.generatingTerrain"));
		ServerWorld serverWorld = this.method_20312(DimensionType.OVERWORLD);
		LOGGER.info("Preparing start region for dimension " + DimensionType.method_17196(serverWorld.dimension.method_11789()));
		BlockPos blockPos = serverWorld.method_3585();
		List<ChunkPos> list = Lists.newArrayList();
		Set<ChunkPos> set = Sets.newConcurrentHashSet();
		Stopwatch stopwatch = Stopwatch.createStarted();

		for (int n = -192; n <= 192 && this.isRunning(); n += 16) {
			for (int o = -192; o <= 192 && this.isRunning(); o += 16) {
				list.add(new ChunkPos(blockPos.getX() + n >> 4, blockPos.getZ() + o >> 4));
			}

			CompletableFuture<?> completableFuture = serverWorld.method_3586().method_21253(list, chunk -> set.add(chunk.method_3920()));

			while (!completableFuture.isDone()) {
				try {
					completableFuture.get(1L, TimeUnit.SECONDS);
				} catch (InterruptedException var20) {
					throw new RuntimeException(var20);
				} catch (ExecutionException var21) {
					if (var21.getCause() instanceof RuntimeException) {
						throw (RuntimeException)var21.getCause();
					}

					throw new RuntimeException(var21.getCause());
				} catch (TimeoutException var22) {
					this.method_3002(new TranslatableText("menu.preparingSpawn"), set.size() * 100 / 625);
				}
			}

			this.method_3002(new TranslatableText("menu.preparingSpawn"), set.size() * 100 / 625);
		}

		LOGGER.info("Time elapsed: {} ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));

		for (DimensionType dimensionType : DimensionType.method_17200()) {
			class_3595 lv = arg.method_17977(dimensionType, class_3595::new, "chunks");
			if (lv != null) {
				ServerWorld serverWorld2 = this.method_20312(dimensionType);
				LongIterator longIterator = lv.method_16296().iterator();

				while (longIterator.hasNext()) {
					this.method_3002(new TranslatableText("menu.loadingForcedChunks", dimensionType), lv.method_16296().size() * 100 / 625);
					long p = longIterator.nextLong();
					ChunkPos chunkPos = new ChunkPos(p);
					serverWorld2.method_3586().method_17044(chunkPos.x, chunkPos.z, true, true);
				}
			}
		}

		this.save();
	}

	public void loadResourcePack(String levelName, SaveHandler saveHandler) {
		File file = new File(saveHandler.getWorldFolder(), "resources.zip");
		if (file.isFile()) {
			try {
				this.setResourcePack("level://" + URLEncoder.encode(levelName, StandardCharsets.UTF_8.toString()) + "/" + "resources.zip", "");
			} catch (UnsupportedEncodingException var5) {
				LOGGER.warn("Something went wrong url encoding {}", levelName);
			}
		}
	}

	public abstract boolean shouldGenerateStructures();

	public abstract GameMode method_3026();

	public abstract Difficulty getDefaultDifficulty();

	public abstract boolean isHardcore();

	public abstract int getOpPermissionLevel();

	public abstract boolean shouldBroadcastRconToOps();

	protected void method_3002(Text text, int i) {
		this.field_21614 = text;
		this.progress = i;
		LOGGER.info("{}: {}%", text.getString(), i);
	}

	protected void save() {
		this.field_21614 = null;
		this.progress = 0;
	}

	public void saveWorlds(boolean silent) {
		for (ServerWorld serverWorld : this.method_20351()) {
			if (serverWorld != null) {
				if (!silent) {
					LOGGER.info("Saving chunks for level '{}'/{}", serverWorld.method_3588().getLevelName(), DimensionType.method_17196(serverWorld.dimension.method_11789()));
				}

				try {
					serverWorld.save(true, null);
				} catch (WorldSaveException var5) {
					LOGGER.warn(var5.getMessage());
				}
			}
		}
	}

	public void stopServer() {
		LOGGER.info("Stopping server");
		if (this.getNetworkIo() != null) {
			this.getNetworkIo().stop();
		}

		if (this.playerManager != null) {
			LOGGER.info("Saving players");
			this.playerManager.saveAllPlayerData();
			this.playerManager.disconnectAllPlayers();
		}

		LOGGER.info("Saving worlds");

		for (ServerWorld serverWorld : this.method_20351()) {
			if (serverWorld != null) {
				serverWorld.savingDisabled = false;
			}
		}

		this.saveWorlds(false);

		for (ServerWorld serverWorld2 : this.method_20351()) {
			if (serverWorld2 != null) {
				serverWorld2.close();
			}
		}

		if (this.field_13903.isActive()) {
			this.field_13903.cancel();
		}
	}

	public String getServerIp() {
		return this.serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public boolean isRunning() {
		return this.running;
	}

	public void stopRunning() {
		this.running = false;
	}

	private boolean method_20339() {
		return Util.method_20227() < this.timeReference;
	}

	public void run() {
		try {
			if (this.setupServer()) {
				this.timeReference = Util.method_20227();
				this.serverMetadata.setDescription(new LiteralText(this.motd));
				this.serverMetadata.setVersion(new ServerMetadata.Version("1.13.2", 404));
				this.setServerMeta(this.serverMetadata);

				while (this.running) {
					long l = Util.method_20227() - this.timeReference;
					if (l > 2000L && this.timeReference - this.lastWarnTime >= 15000L) {
						long m = l / 50L;
						LOGGER.warn("Can't keep up! Is the server overloaded? Running {}ms or {} ticks behind", l, m);
						this.timeReference += m * 50L;
						this.lastWarnTime = this.timeReference;
					}

					this.method_20324(this::method_20339);
					this.timeReference += 50L;

					while (this.method_20339()) {
						Thread.sleep(1L);
					}

					this.loading = true;
				}
			} else {
				this.setCrashReport(null);
			}
		} catch (Throwable var44) {
			LOGGER.error("Encountered an unexpected exception", var44);
			CrashReport crashReport;
			if (var44 instanceof CrashException) {
				crashReport = this.populateCrashReport(((CrashException)var44).getReport());
			} else {
				crashReport = this.populateCrashReport(new CrashReport("Exception in server tick loop", var44));
			}

			File file = new File(
				new File(this.getRunDirectory(), "crash-reports"), "crash-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-server.txt"
			);
			if (crashReport.writeToFile(file)) {
				LOGGER.error("This crash report has been saved to: {}", file.getAbsolutePath());
			} else {
				LOGGER.error("We were unable to save this crash report to disk.");
			}

			this.setCrashReport(crashReport);
		} finally {
			try {
				this.stopped = true;
				this.stopServer();
			} catch (Throwable var42) {
				LOGGER.error("Exception stopping the server", var42);
			} finally {
				this.exit();
			}
		}
	}

	public void setServerMeta(ServerMetadata metadata) {
		File file = this.getFile("server-icon.png");
		if (!file.exists()) {
			file = this.getSaveStorage().method_11957(this.getLevelName(), "icon.png");
		}

		if (file.isFile()) {
			ByteBuf byteBuf = Unpooled.buffer();

			try {
				BufferedImage bufferedImage = ImageIO.read(file);
				Validate.validState(bufferedImage.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
				Validate.validState(bufferedImage.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
				ImageIO.write(bufferedImage, "PNG", new ByteBufOutputStream(byteBuf));
				ByteBuffer byteBuffer = Base64.getEncoder().encode(byteBuf.nioBuffer());
				metadata.setFavicon("data:image/png;base64," + StandardCharsets.UTF_8.decode(byteBuffer));
			} catch (Exception var9) {
				LOGGER.error("Couldn't load server icon", var9);
			} finally {
				byteBuf.release();
			}
		}
	}

	public boolean method_12837() {
		this.field_13902 = this.field_13902 || this.method_12838().isFile();
		return this.field_13902;
	}

	public File method_12838() {
		return this.getSaveStorage().method_11957(this.getLevelName(), "icon.png");
	}

	public File getRunDirectory() {
		return new File(".");
	}

	public void setCrashReport(CrashReport report) {
	}

	public void exit() {
	}

	public void method_20324(BooleanSupplier booleanSupplier) {
		long l = Util.method_20230();
		this.ticks++;
		if (this.profiling) {
			this.profiling = false;
			this.profiler.method_21520(this.ticks);
		}

		this.profiler.push("root");
		this.method_20347(booleanSupplier);
		if (l - this.lastPlayerSampleUpdate >= 5000000000L) {
			this.lastPlayerSampleUpdate = l;
			this.serverMetadata.setPlayers(new ServerMetadata.Players(this.getMaxPlayerCount(), this.getCurrentPlayerCount()));
			GameProfile[] gameProfiles = new GameProfile[Math.min(this.getCurrentPlayerCount(), 12)];
			int i = MathHelper.nextInt(this.random, 0, this.getCurrentPlayerCount() - gameProfiles.length);

			for (int j = 0; j < gameProfiles.length; j++) {
				gameProfiles[j] = ((ServerPlayerEntity)this.playerManager.getPlayers().get(i + j)).getGameProfile();
			}

			Collections.shuffle(Arrays.asList(gameProfiles));
			this.serverMetadata.getPlayers().setSample(gameProfiles);
		}

		if (this.ticks % 900 == 0) {
			this.profiler.push("save");
			this.playerManager.saveAllPlayerData();
			this.saveWorlds(true);
			this.profiler.pop();
		}

		this.profiler.push("snooper");
		if (!this.field_13903.isActive() && this.ticks > 100) {
			this.field_13903.setActive();
		}

		if (this.ticks % 6000 == 0) {
			this.field_13903.addCpuInfo();
		}

		this.profiler.pop();
		this.profiler.push("tallying");
		long m = this.lastTickLengths[this.ticks % 100] = Util.method_20230() - l;
		this.field_21610 = this.field_21610 * 0.8F + (float)m / 1000000.0F * 0.19999999F;
		this.profiler.pop();
		this.profiler.pop();
	}

	public void method_20347(BooleanSupplier booleanSupplier) {
		this.profiler.push("jobs");

		FutureTask<?> futureTask;
		while ((futureTask = (FutureTask<?>)this.queue.poll()) != null) {
			Util.executeTask(futureTask, LOGGER);
		}

		this.profiler.swap("commandFunctions");
		this.method_14911().tick();
		this.profiler.swap("levels");

		for (ServerWorld serverWorld : this.method_20351()) {
			long l = Util.method_20230();
			if (serverWorld.dimension.method_11789() == DimensionType.OVERWORLD || this.isNetherAllowed()) {
				this.profiler.push((Supplier<String>)(() -> "dim-" + serverWorld.dimension.method_11789().method_17201()));
				if (this.ticks % 20 == 0) {
					this.profiler.push("timeSync");
					this.playerManager
						.method_21385(
							new WorldTimeUpdateS2CPacket(serverWorld.getLastUpdateTime(), serverWorld.getTimeOfDay(), serverWorld.getGameRules().getBoolean("doDaylightCycle")),
							serverWorld.dimension.method_11789()
						);
					this.profiler.pop();
				}

				this.profiler.push("tick");

				try {
					serverWorld.method_16327(booleanSupplier);
				} catch (Throwable var10) {
					CrashReport crashReport = CrashReport.create(var10, "Exception ticking world");
					serverWorld.addToCrashReport(crashReport);
					throw new CrashException(crashReport);
				}

				try {
					serverWorld.tickEntities();
				} catch (Throwable var9) {
					CrashReport crashReport2 = CrashReport.create(var9, "Exception ticking world entities");
					serverWorld.addToCrashReport(crashReport2);
					throw new CrashException(crashReport2);
				}

				this.profiler.pop();
				this.profiler.push("tracker");
				serverWorld.getEntityTracker().method_2095();
				this.profiler.pop();
				this.profiler.pop();
			}

			((long[])this.field_21611.computeIfAbsent(serverWorld.dimension.method_11789(), dimensionType -> new long[100]))[this.ticks % 100] = Util.method_20230() - l;
		}

		this.profiler.swap("connection");
		this.getNetworkIo().tick();
		this.profiler.swap("players");
		this.playerManager.updatePlayerLatency();
		this.profiler.swap("tickables");

		for (int i = 0; i < this.tickables.size(); i++) {
			((Tickable)this.tickables.get(i)).tick();
		}

		this.profiler.pop();
	}

	public boolean isNetherAllowed() {
		return true;
	}

	public void addTickable(Tickable tickable) {
		this.tickables.add(tickable);
	}

	public static void main(String[] args) {
		Bootstrap.initialize();

		try {
			boolean bl = true;
			String string = null;
			String string2 = ".";
			String string3 = null;
			boolean bl2 = false;
			boolean bl3 = false;
			boolean bl4 = false;
			int i = -1;

			for (int j = 0; j < args.length; j++) {
				String string4 = args[j];
				String string5 = j == args.length - 1 ? null : args[j + 1];
				boolean bl5 = false;
				if ("nogui".equals(string4) || "--nogui".equals(string4)) {
					bl = false;
				} else if ("--port".equals(string4) && string5 != null) {
					bl5 = true;

					try {
						i = Integer.parseInt(string5);
					} catch (NumberFormatException var15) {
					}
				} else if ("--singleplayer".equals(string4) && string5 != null) {
					bl5 = true;
					string = string5;
				} else if ("--universe".equals(string4) && string5 != null) {
					bl5 = true;
					string2 = string5;
				} else if ("--world".equals(string4) && string5 != null) {
					bl5 = true;
					string3 = string5;
				} else if ("--demo".equals(string4)) {
					bl2 = true;
				} else if ("--bonusChest".equals(string4)) {
					bl3 = true;
				} else if ("--forceUpgrade".equals(string4)) {
					bl4 = true;
				}

				if (bl5) {
					j++;
				}
			}

			YggdrasilAuthenticationService yggdrasilAuthenticationService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString());
			MinecraftSessionService minecraftSessionService = yggdrasilAuthenticationService.createMinecraftSessionService();
			GameProfileRepository gameProfileRepository = yggdrasilAuthenticationService.createProfileRepository();
			UserCache userCache = new UserCache(gameProfileRepository, new File(string2, USER_CACHE_FILE.getName()));
			final MinecraftDedicatedServer minecraftDedicatedServer = new MinecraftDedicatedServer(
				new File(string2), DataFixerFactory.method_21531(), yggdrasilAuthenticationService, minecraftSessionService, gameProfileRepository, userCache
			);
			if (string != null) {
				minecraftDedicatedServer.setUserName(string);
			}

			if (string3 != null) {
				minecraftDedicatedServer.setLevelName(string3);
			}

			if (i >= 0) {
				minecraftDedicatedServer.setServerPort(i);
			}

			if (bl2) {
				minecraftDedicatedServer.setDemo(true);
			}

			if (bl3) {
				minecraftDedicatedServer.setForceWorldUpgrade(true);
			}

			if (bl && !GraphicsEnvironment.isHeadless()) {
				minecraftDedicatedServer.createGui();
			}

			if (bl4) {
				minecraftDedicatedServer.method_20349(true);
			}

			minecraftDedicatedServer.startServerThread();
			Thread thread = new Thread("Server Shutdown Thread") {
				public void run() {
					minecraftDedicatedServer.stopServer();
				}
			};
			thread.setUncaughtExceptionHandler(new class_4325(LOGGER));
			Runtime.getRuntime().addShutdownHook(thread);
		} catch (Exception var16) {
			LOGGER.fatal("Failed to start the minecraft server", var16);
		}
	}

	protected void method_20349(boolean bl) {
		this.field_21609 = bl;
	}

	public void startServerThread() {
		this.serverThread = new Thread(this, "Server thread");
		this.serverThread.setUncaughtExceptionHandler((thread, throwable) -> LOGGER.error(throwable));
		this.serverThread.start();
	}

	public File getFile(String name) {
		return new File(this.getRunDirectory(), name);
	}

	public void info(String message) {
		LOGGER.info(message);
	}

	public void warn(String message) {
		LOGGER.warn(message);
	}

	public ServerWorld method_20312(DimensionType dimensionType) {
		return (ServerWorld)this.field_21613.get(dimensionType);
	}

	public Iterable<ServerWorld> method_20351() {
		return this.field_21613.values();
	}

	public String getVersion() {
		return "1.13.2";
	}

	public int getCurrentPlayerCount() {
		return this.playerManager.getCurrentPlayerCount();
	}

	public int getMaxPlayerCount() {
		return this.playerManager.getMaxPlayerCount();
	}

	public String[] getPlayerNames() {
		return this.playerManager.getPlayerNames();
	}

	public boolean isDebuggingEnabled() {
		return false;
	}

	public void logError(String message) {
		LOGGER.error(message);
	}

	public void log(String message) {
		if (this.isDebuggingEnabled()) {
			LOGGER.info(message);
		}
	}

	public String getServerModName() {
		return "vanilla";
	}

	public CrashReport populateCrashReport(CrashReport report) {
		report.getSystemDetailsSection()
			.add("Profiler Position", (CrashCallable<String>)(() -> this.profiler.method_21519() ? this.profiler.getCurrentLocation() : "N/A (disabled)"));
		if (this.playerManager != null) {
			report.getSystemDetailsSection()
				.add(
					"Player Count",
					(CrashCallable<String>)(() -> this.playerManager.getCurrentPlayerCount()
							+ " / "
							+ this.playerManager.getMaxPlayerCount()
							+ "; "
							+ this.playerManager.getPlayers())
				);
		}

		report.getSystemDetailsSection().add("Data Packs", (CrashCallable<String>)(() -> {
			StringBuilder stringBuilder = new StringBuilder();

			for (class_4465 lv : this.field_21598.method_21354()) {
				if (stringBuilder.length() > 0) {
					stringBuilder.append(", ");
				}

				stringBuilder.append(lv.method_21365());
				if (!lv.method_21363().method_21343()) {
					stringBuilder.append(" (incompatible)");
				}
			}

			return stringBuilder.toString();
		}));
		return report;
	}

	public boolean hasGameDir() {
		return this.gameDir != null;
	}

	@Override
	public void method_5505(Text text) {
		LOGGER.info(text.getString());
	}

	public KeyPair getKeyPair() {
		return this.keyPair;
	}

	public int getServerPort() {
		return this.serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public boolean isSinglePlayer() {
		return this.userName != null;
	}

	public String getLevelName() {
		return this.levelName;
	}

	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}

	public void setServerName(String serverName) {
		this.displayName = serverName;
	}

	public String getServerName() {
		return this.displayName;
	}

	public void setKeyPair(KeyPair keyPair) {
		this.keyPair = keyPair;
	}

	public void setDifficulty(Difficulty difficulty) {
		for (ServerWorld serverWorld : this.method_20351()) {
			if (serverWorld.method_3588().isHardcore()) {
				serverWorld.method_3588().setDifficulty(Difficulty.HARD);
				serverWorld.setMobSpawning(true, true);
			} else if (this.isSinglePlayer()) {
				serverWorld.method_3588().setDifficulty(difficulty);
				serverWorld.setMobSpawning(serverWorld.method_16346() != Difficulty.PEACEFUL, true);
			} else {
				serverWorld.method_3588().setDifficulty(difficulty);
				serverWorld.setMobSpawning(this.isMonsterSpawningEnabled(), this.spawnAnimals);
			}
		}
	}

	public boolean isMonsterSpawningEnabled() {
		return true;
	}

	public boolean isDemo() {
		return this.demo;
	}

	public void setDemo(boolean demo) {
		this.demo = demo;
	}

	public void setForceWorldUpgrade(boolean forceWorldUpgrade) {
		this.forceWorldUpgrade = forceWorldUpgrade;
	}

	public LevelStorageAccess getSaveStorage() {
		return this.saveStorage;
	}

	public String getResourcePackUrl() {
		return this.resourcePackUrl;
	}

	public String getResourcePackHash() {
		return this.resourcePackHash;
	}

	public void setResourcePack(String url, String hash) {
		this.resourcePackUrl = url;
		this.resourcePackHash = hash;
	}

	@Override
	public void addSnooperInfo(Snooper snooper) {
		snooper.addGameInfo("whitelist_enabled", false);
		snooper.addGameInfo("whitelist_count", 0);
		if (this.playerManager != null) {
			snooper.addGameInfo("players_current", this.getCurrentPlayerCount());
			snooper.addGameInfo("players_max", this.getMaxPlayerCount());
			snooper.addGameInfo("players_seen", this.playerManager.getSavedPlayerIds().length);
		}

		snooper.addGameInfo("uses_auth", this.onlineMode);
		snooper.addGameInfo("gui_state", this.hasGui() ? "enabled" : "disabled");
		snooper.addGameInfo("run_time", (Util.method_20227() - snooper.getStartTime()) / 60L * 1000L);
		snooper.addGameInfo("avg_tick_ms", (int)(MathHelper.average(this.lastTickLengths) * 1.0E-6));
		int i = 0;

		for (ServerWorld serverWorld : this.method_20351()) {
			if (serverWorld != null) {
				LevelProperties levelProperties = serverWorld.method_3588();
				snooper.addGameInfo("world[" + i + "][dimension]", serverWorld.dimension.method_11789());
				snooper.addGameInfo("world[" + i + "][mode]", levelProperties.getGamemode());
				snooper.addGameInfo("world[" + i + "][difficulty]", serverWorld.method_16346());
				snooper.addGameInfo("world[" + i + "][hardcore]", levelProperties.isHardcore());
				snooper.addGameInfo("world[" + i + "][generator_name]", levelProperties.getGeneratorType().getName());
				snooper.addGameInfo("world[" + i + "][generator_version]", levelProperties.getGeneratorType().getVersion());
				snooper.addGameInfo("world[" + i + "][height]", this.worldHeight);
				snooper.addGameInfo("world[" + i + "][chunks_loaded]", serverWorld.method_3586().method_3874());
				i++;
			}
		}

		snooper.addGameInfo("worlds", i);
	}

	public boolean method_2409() {
		return true;
	}

	public abstract boolean isDedicated();

	public boolean isOnlineMode() {
		return this.onlineMode;
	}

	public void setOnlineMode(boolean onlineMode) {
		this.onlineMode = onlineMode;
	}

	public boolean method_13912() {
		return this.field_15357;
	}

	public void method_13913(boolean bl) {
		this.field_15357 = bl;
	}

	public boolean shouldSpawnAnimals() {
		return this.spawnAnimals;
	}

	public void setSpawnAnimals(boolean spawnAnimals) {
		this.spawnAnimals = spawnAnimals;
	}

	public boolean shouldSpawnNpcs() {
		return this.spawnNpcs;
	}

	public abstract boolean isUsingNativeTransport();

	public void setSpawnNpcs(boolean spawnNpcs) {
		this.spawnNpcs = spawnNpcs;
	}

	public boolean isPvpEnabled() {
		return this.pvpEnabled;
	}

	public void setPvpEnabled(boolean pvpEnabled) {
		this.pvpEnabled = pvpEnabled;
	}

	public boolean isFlightEnabled() {
		return this.flightEnabled;
	}

	public void setFlightEnabled(boolean flightEnabled) {
		this.flightEnabled = flightEnabled;
	}

	public abstract boolean areCommandBlocksEnabled();

	public String getServerMotd() {
		return this.motd;
	}

	public void setMotd(String motd) {
		this.motd = motd;
	}

	public int getWorldHeight() {
		return this.worldHeight;
	}

	public void setWorldHeight(int worldHeight) {
		this.worldHeight = worldHeight;
	}

	public boolean isStopped() {
		return this.stopped;
	}

	public PlayerManager getPlayerManager() {
		return this.playerManager;
	}

	public void setPlayerManager(PlayerManager playerManager) {
		this.playerManager = playerManager;
	}

	public abstract boolean shouldBroadcastConsoleToIps();

	public void method_2999(GameMode gameMode) {
		for (ServerWorld serverWorld : this.method_20351()) {
			serverWorld.method_3588().getGameMode(gameMode);
		}
	}

	public ServerNetworkIo getNetworkIo() {
		return this.networkIo;
	}

	public boolean isLoading() {
		return this.loading;
	}

	public boolean hasGui() {
		return false;
	}

	public abstract boolean method_20311(GameMode gameMode, boolean bl, int i);

	public int getTicks() {
		return this.ticks;
	}

	public void enableProfiler() {
		this.profiling = true;
	}

	public Snooper getSnooper() {
		return this.field_13903;
	}

	public int getSpawnProtectionRadius() {
		return 16;
	}

	public boolean isSpawnProtected(World world, BlockPos pos, PlayerEntity player) {
		return false;
	}

	public void setForceGameMode(boolean forceGameMode) {
		this.forceGameMode = forceGameMode;
	}

	public boolean shouldForceGameMode() {
		return this.forceGameMode;
	}

	public int getPlayerIdleTimeout() {
		return this.playerIdleTimeout;
	}

	public void setPlayerIdleTimeout(int playerIdleTimeout) {
		this.playerIdleTimeout = playerIdleTimeout;
	}

	public MinecraftSessionService getSessionService() {
		return this.sessionService;
	}

	public GameProfileRepository getGameProfileRepo() {
		return this.gameProfileRepo;
	}

	public UserCache getUserCache() {
		return this.userCache;
	}

	public ServerMetadata getServerMetadata() {
		return this.serverMetadata;
	}

	public void forcePlayerSampleUpdate() {
		this.lastPlayerSampleUpdate = 0L;
	}

	public int getMaxWorldBorderRadius() {
		return 29999984;
	}

	public <V> ListenableFuture<V> method_10815(Callable<V> callable) {
		Validate.notNull(callable);
		if (!this.isOnThread() && !this.isStopped()) {
			ListenableFutureTask<V> listenableFutureTask = ListenableFutureTask.create(callable);
			this.queue.add(listenableFutureTask);
			return listenableFutureTask;
		} else {
			try {
				return Futures.immediateFuture(callable.call());
			} catch (Exception var3) {
				return Futures.immediateFailedCheckedFuture(var3);
			}
		}
	}

	@Override
	public ListenableFuture<Object> submit(Runnable task) {
		Validate.notNull(task);
		return this.method_10815(Executors.callable(task));
	}

	@Override
	public boolean isOnThread() {
		return Thread.currentThread() == this.serverThread;
	}

	public int getNetworkCompressionThreshold() {
		return 256;
	}

	public long getServerStartTime() {
		return this.timeReference;
	}

	public Thread getThread() {
		return this.serverThread;
	}

	public DataFixer method_20343() {
		return this.field_21612;
	}

	public int method_12834(@Nullable ServerWorld serverWorld) {
		return serverWorld != null ? serverWorld.getGameRules().getInt("spawnRadius") : 10;
	}

	public class_3348 method_14910() {
		return this.field_21606;
	}

	public FunctionTickable method_14911() {
		return this.field_21607;
	}

	public void method_14912() {
		if (!this.isOnThread()) {
			this.submit(this::method_14912);
		} else {
			this.getPlayerManager().saveAllPlayerData();
			this.field_21598.method_21347();
			this.method_20314(this.method_20312(DimensionType.OVERWORLD).method_3588());
			this.getPlayerManager().method_14980();
		}
	}

	private void method_20314(LevelProperties levelProperties) {
		List<class_4465> list = Lists.newArrayList(this.field_21598.method_21354());

		for (class_4465 lv : this.field_21598.method_21352()) {
			if (!levelProperties.method_17951().contains(lv.method_21365()) && !list.contains(lv)) {
				LOGGER.info("Found new data pack {}, loading it automatically", lv.method_21365());
				lv.method_21368().method_21370(list, lv, arg -> arg, false);
			}
		}

		this.field_21598.method_21349(list);
		List<class_4454> list2 = Lists.newArrayList();
		this.field_21598.method_21354().forEach(arg -> list2.add(arg.method_21364()));
		this.field_21597.reload(list2);
		levelProperties.method_17952().clear();
		levelProperties.method_17951().clear();
		this.field_21598.method_21354().forEach(arg -> levelProperties.method_17952().add(arg.method_21365()));
		this.field_21598.method_21352().forEach(arg -> {
			if (!this.field_21598.method_21354().contains(arg)) {
				levelProperties.method_17951().add(arg.method_21365());
			}
		});
	}

	public void method_20313(class_3915 arg) {
		if (this.method_20337()) {
			PlayerManager playerManager = arg.method_17473().getPlayerManager();
			Whitelist whitelist = playerManager.getWhitelist();
			if (whitelist.isEnabled()) {
				for (ServerPlayerEntity serverPlayerEntity : Lists.newArrayList(playerManager.getPlayers())) {
					if (!whitelist.isAllowed(serverPlayerEntity.getGameProfile())) {
						serverPlayerEntity.networkHandler.method_14977(new TranslatableText("multiplayer.disconnect.not_whitelisted"));
					}
				}
			}
		}
	}

	public ReloadableResourceManager method_20326() {
		return this.field_21597;
	}

	public class_4462<class_4465> method_20327() {
		return this.field_21598;
	}

	public Text method_20328() {
		return this.field_21614;
	}

	public int method_20329() {
		return this.progress;
	}

	public CommandManager method_2971() {
		return this.field_21600;
	}

	public class_3915 method_20330() {
		return new class_3915(
			this,
			this.method_20312(DimensionType.OVERWORLD) == null ? Vec3d.ZERO : new Vec3d(this.method_20312(DimensionType.OVERWORLD).method_3585()),
			Vec2f.ZERO,
			this.method_20312(DimensionType.OVERWORLD),
			4,
			"Server",
			new LiteralText("Server"),
			this,
			null
		);
	}

	@Override
	public boolean method_17413() {
		return true;
	}

	@Override
	public boolean method_17414() {
		return true;
	}

	public RecipeDispatcher method_20331() {
		return this.field_21601;
	}

	public class_4488 method_20332() {
		return this.field_21602;
	}

	public ServerScoreboard method_20333() {
		return this.field_21603;
	}

	public class_2787 method_20334() {
		return this.field_21605;
	}

	public GameRuleManager method_20335() {
		return this.method_20312(DimensionType.OVERWORLD).getGameRules();
	}

	public class_4403 method_20336() {
		return this.field_21604;
	}

	public boolean method_20337() {
		return this.field_21608;
	}

	public void method_20350(boolean bl) {
		this.field_21608 = bl;
	}

	public float method_20338() {
		return this.field_21610;
	}

	public int method_20318(GameProfile gameProfile) {
		if (this.getPlayerManager().isOperator(gameProfile)) {
			OperatorEntry operatorEntry = this.getPlayerManager().getOpList().get(gameProfile);
			if (operatorEntry != null) {
				return operatorEntry.getPermissionLevel();
			} else if (this.isSinglePlayer()) {
				if (this.getUserName().equals(gameProfile.getName())) {
					return 4;
				} else {
					return this.getPlayerManager().method_21388() ? 4 : 0;
				}
			} else {
				return this.getOpPermissionLevel();
			}
		} else {
			return 0;
		}
	}
}
