package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import net.minecraft.achievement.class_3348;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandSource;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandRegistryProvider;
import net.minecraft.server.function.FunctionTickable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ServerWorldManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.ThreadExecutor;
import net.minecraft.util.Tickable;
import net.minecraft.util.UserCache;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.snooper.Snoopable;
import net.minecraft.util.snooper.Snooper;
import net.minecraft.world.DemoServerWorld;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.MultiServerWorld;
import net.minecraft.world.SaveHandler;
import net.minecraft.world.World;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.AnvilLevelStorage;
import net.minecraft.world.level.storage.LevelStorageAccess;
import net.minecraft.world.level.storage.WorldSaveException;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class MinecraftServer implements CommandSource, Runnable, ThreadExecutor, Snoopable {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final File USER_CACHE_FILE = new File("usercache.json");
	private final LevelStorageAccess saveStorage;
	private final Snooper field_13903 = new Snooper("server", this, getTimeMillis());
	private final File gameDir;
	private final List<Tickable> tickables = Lists.newArrayList();
	public final CommandRegistryProvider provider;
	public final Profiler profiler = new Profiler();
	private final ServerNetworkIo networkIo;
	private final ServerMetadata serverMetadata = new ServerMetadata();
	private final Random random = new Random();
	private final DataFixerUpper dataFixer;
	private int serverPort = -1;
	public ServerWorld[] worlds;
	private PlayerManager playerManager;
	private boolean running = true;
	private boolean stopped;
	private int ticks;
	protected final Proxy proxy;
	public String progressType;
	public int progress;
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
	public long[][] field_3858;
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
	private String serverOperation;
	private boolean profiling;
	private boolean forceGameMode;
	private final YggdrasilAuthenticationService authService;
	private final MinecraftSessionService sessionService;
	private final GameProfileRepository gameProfileRepo;
	private final UserCache userCache;
	private long lastPlayerSampleUpdate;
	public final Queue<FutureTask<?>> queue = Queues.newArrayDeque();
	private Thread serverThread;
	private long timeReference = getTimeMillis();
	private boolean field_13902;

	public MinecraftServer(
		File file,
		Proxy proxy,
		DataFixerUpper dataFixerUpper,
		YggdrasilAuthenticationService yggdrasilAuthenticationService,
		MinecraftSessionService minecraftSessionService,
		GameProfileRepository gameProfileRepository,
		UserCache userCache
	) {
		this.proxy = proxy;
		this.authService = yggdrasilAuthenticationService;
		this.sessionService = minecraftSessionService;
		this.gameProfileRepo = gameProfileRepository;
		this.userCache = userCache;
		this.gameDir = file;
		this.networkIo = new ServerNetworkIo(this);
		this.provider = this.createCommandManager();
		this.saveStorage = new AnvilLevelStorage(file, dataFixerUpper);
		this.dataFixer = dataFixerUpper;
	}

	public CommandManager createCommandManager() {
		return new CommandManager(this);
	}

	public abstract boolean setupServer() throws IOException;

	public void upgradeWorld(String name) {
		if (this.getSaveStorage().needsConversion(name)) {
			LOGGER.info("Converting map!");
			this.setServerOperation("menu.convertingLevel");
			this.getSaveStorage().convert(name, new ProgressListener() {
				private long lastProgressUpdate = MinecraftServer.getTimeMillis();

				@Override
				public void setTitle(String title) {
				}

				@Override
				public void setTitleAndTask(String title) {
				}

				@Override
				public void setProgressPercentage(int percentage) {
					if (MinecraftServer.getTimeMillis() - this.lastProgressUpdate >= 1000L) {
						this.lastProgressUpdate = MinecraftServer.getTimeMillis();
						MinecraftServer.LOGGER.info("Converting... {}%", percentage);
					}
				}

				@Override
				public void setDone() {
				}

				@Override
				public void setTask(String task) {
				}
			});
		}
	}

	protected synchronized void setServerOperation(String operation) {
		this.serverOperation = operation;
	}

	@Nullable
	public synchronized String getServerOperation() {
		return this.serverOperation;
	}

	public void setupWorld(String world, String worldName, long seed, LevelGeneratorType generatorType, String generatorOptions) {
		this.upgradeWorld(world);
		this.setServerOperation("menu.loadingLevel");
		this.worlds = new ServerWorld[3];
		this.field_3858 = new long[this.worlds.length][100];
		SaveHandler saveHandler = this.saveStorage.createSaveHandler(world, true);
		this.loadResourcePack(this.getLevelName(), saveHandler);
		LevelProperties levelProperties = saveHandler.getLevelProperties();
		LevelInfo levelInfo;
		if (levelProperties == null) {
			if (this.isDemo()) {
				levelInfo = DemoServerWorld.INFO;
			} else {
				levelInfo = new LevelInfo(seed, this.method_3026(), this.shouldGenerateStructures(), this.isHardcore(), generatorType);
				levelInfo.setGeneratorOptions(generatorOptions);
				if (this.forceWorldUpgrade) {
					levelInfo.setBonusChest();
				}
			}

			levelProperties = new LevelProperties(levelInfo, worldName);
		} else {
			levelProperties.setLevelName(worldName);
			levelInfo = new LevelInfo(levelProperties);
		}

		for (int i = 0; i < this.worlds.length; i++) {
			int j = 0;
			if (i == 1) {
				j = -1;
			}

			if (i == 2) {
				j = 1;
			}

			if (i == 0) {
				if (this.isDemo()) {
					this.worlds[i] = (ServerWorld)new DemoServerWorld(this, saveHandler, levelProperties, j, this.profiler).getWorld();
				} else {
					this.worlds[i] = (ServerWorld)new ServerWorld(this, saveHandler, levelProperties, j, this.profiler).getWorld();
				}

				this.worlds[i].setPropertiesInitialized(levelInfo);
			} else {
				this.worlds[i] = (ServerWorld)new MultiServerWorld(this, saveHandler, j, this.worlds[0], this.profiler).getWorld();
			}

			this.worlds[i].addListener(new ServerWorldManager(this, this.worlds[i]));
			if (!this.isSinglePlayer()) {
				this.worlds[i].getLevelProperties().getGameMode(this.method_3026());
			}
		}

		this.playerManager.setMainWorld(this.worlds);
		this.setDifficulty(this.getDefaultDifficulty());
		this.prepareWorlds();
	}

	public void prepareWorlds() {
		int i = 16;
		int j = 4;
		int k = 192;
		int l = 625;
		int m = 0;
		this.setServerOperation("menu.generatingTerrain");
		int n = 0;
		LOGGER.info("Preparing start region for level 0");
		ServerWorld serverWorld = this.worlds[0];
		BlockPos blockPos = serverWorld.getSpawnPos();
		long o = getTimeMillis();

		for (int p = -192; p <= 192 && this.isRunning(); p += 16) {
			for (int q = -192; q <= 192 && this.isRunning(); q += 16) {
				long r = getTimeMillis();
				if (r - o > 1000L) {
					this.logProgress("Preparing spawn area", m * 100 / 625);
					o = r;
				}

				m++;
				serverWorld.getChunkProvider().getOrGenerateChunks(blockPos.getX() + p >> 4, blockPos.getZ() + q >> 4);
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

	public abstract boolean shouldBroadcastConsoleToIps();

	protected void logProgress(String progressType, int worldProgress) {
		this.progressType = progressType;
		this.progress = worldProgress;
		LOGGER.info("{}: {}%", progressType, worldProgress);
	}

	protected void save() {
		this.progressType = null;
		this.progress = 0;
	}

	public void saveWorlds(boolean silent) {
		for (ServerWorld serverWorld : this.worlds) {
			if (serverWorld != null) {
				if (!silent) {
					LOGGER.info("Saving chunks for level '{}'/{}", serverWorld.getLevelProperties().getLevelName(), serverWorld.dimension.getDimensionType().getName());
				}

				try {
					serverWorld.save(true, null);
				} catch (WorldSaveException var7) {
					LOGGER.warn(var7.getMessage());
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

		if (this.worlds != null) {
			LOGGER.info("Saving worlds");

			for (ServerWorld serverWorld : this.worlds) {
				if (serverWorld != null) {
					serverWorld.savingDisabled = false;
				}
			}

			this.saveWorlds(false);

			for (ServerWorld serverWorld2 : this.worlds) {
				if (serverWorld2 != null) {
					serverWorld2.close();
				}
			}
		}

		if (this.field_13903.isActive()) {
			this.field_13903.cancel();
		}
	}

	public boolean isRunning() {
		return this.running;
	}

	public void stopRunning() {
		this.running = false;
	}

	public void run() {
		try {
			if (this.setupServer()) {
				this.timeReference = getTimeMillis();
				long l = 0L;
				this.serverMetadata.setDescription(new LiteralText(this.motd));
				this.serverMetadata.setVersion(new ServerMetadata.Version("1.12.2", 340));
				this.setServerMeta(this.serverMetadata);

				while (this.running) {
					long m = getTimeMillis();
					long n = m - this.timeReference;
					if (n > 2000L && this.timeReference - this.lastWarnTime >= 15000L) {
						LOGGER.warn("Can't keep up! Did the system time change, or is the server overloaded? Running {}ms behind, skipping {} tick(s)", n, n / 50L);
						n = 2000L;
						this.lastWarnTime = this.timeReference;
					}

					if (n < 0L) {
						LOGGER.warn("Time ran backwards! Did the system time change?");
						n = 0L;
					}

					l += n;
					this.timeReference = m;
					if (this.worlds[0].isReady()) {
						this.setupWorld();
						l = 0L;
					} else {
						while (l > 50L) {
							l -= 50L;
							this.setupWorld();
						}
					}

					Thread.sleep(Math.max(1L, 50L - l));
					this.loading = true;
				}
			} else {
				this.setCrashReport(null);
			}
		} catch (Throwable var46) {
			LOGGER.error("Encountered an unexpected exception", var46);
			CrashReport crashReport = null;
			if (var46 instanceof CrashException) {
				crashReport = this.populateCrashReport(((CrashException)var46).getReport());
			} else {
				crashReport = this.populateCrashReport(new CrashReport("Exception in server tick loop", var46));
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
			} catch (Throwable var44) {
				LOGGER.error("Exception stopping the server", var44);
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
				ByteBuf byteBuf2 = Base64.encode(byteBuf);
				metadata.setFavicon("data:image/png;base64," + byteBuf2.toString(StandardCharsets.UTF_8));
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

	public void setupWorld() {
		long l = System.nanoTime();
		this.ticks++;
		if (this.profiling) {
			this.profiling = false;
			this.profiler.enabled = true;
			this.profiler.reset();
		}

		this.profiler.push("root");
		this.tick();
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

		this.profiler.push("tallying");
		this.lastTickLengths[this.ticks % 100] = System.nanoTime() - l;
		this.profiler.pop();
		this.profiler.push("snooper");
		if (!this.field_13903.isActive() && this.ticks > 100) {
			this.field_13903.setActive();
		}

		if (this.ticks % 6000 == 0) {
			this.field_13903.addCpuInfo();
		}

		this.profiler.pop();
		this.profiler.pop();
	}

	public void tick() {
		this.profiler.push("jobs");
		synchronized (this.queue) {
			while (!this.queue.isEmpty()) {
				Util.executeTask((FutureTask)this.queue.poll(), LOGGER);
			}
		}

		this.profiler.swap("levels");

		for (int i = 0; i < this.worlds.length; i++) {
			long l = System.nanoTime();
			if (i == 0 || this.isNetherAllowed()) {
				ServerWorld serverWorld = this.worlds[i];
				this.profiler.push((Supplier<String>)(() -> serverWorld.getLevelProperties().getLevelName()));
				if (this.ticks % 20 == 0) {
					this.profiler.push("timeSync");
					this.playerManager
						.sendToDimension(
							new WorldTimeUpdateS2CPacket(serverWorld.getLastUpdateTime(), serverWorld.getTimeOfDay(), serverWorld.getGameRules().getBoolean("doDaylightCycle")),
							serverWorld.dimension.getDimensionType().getId()
						);
					this.profiler.pop();
				}

				this.profiler.push("tick");

				try {
					serverWorld.tick();
				} catch (Throwable var8) {
					CrashReport crashReport = CrashReport.create(var8, "Exception ticking world");
					serverWorld.addToCrashReport(crashReport);
					throw new CrashException(crashReport);
				}

				try {
					serverWorld.tickEntities();
				} catch (Throwable var7) {
					CrashReport crashReport2 = CrashReport.create(var7, "Exception ticking world entities");
					serverWorld.addToCrashReport(crashReport2);
					throw new CrashException(crashReport2);
				}

				this.profiler.pop();
				this.profiler.push("tracker");
				serverWorld.getEntityTracker().method_2095();
				this.profiler.pop();
				this.profiler.pop();
			}

			this.field_3858[i][this.ticks % 100] = System.nanoTime() - l;
		}

		this.profiler.swap("connection");
		this.getNetworkIo().tick();
		this.profiler.swap("players");
		this.playerManager.updatePlayerLatency();
		this.profiler.swap("commandFunctions");
		this.method_14911().tick();
		this.profiler.swap("tickables");

		for (int j = 0; j < this.tickables.size(); j++) {
			((Tickable)this.tickables.get(j)).tick();
		}

		this.profiler.pop();
	}

	public boolean isNetherAllowed() {
		return true;
	}

	public void startServerThread() {
		this.serverThread = new Thread(this, "Server thread");
		this.serverThread.start();
	}

	public File getFile(String name) {
		return new File(this.getRunDirectory(), name);
	}

	public void warn(String message) {
		LOGGER.warn(message);
	}

	public ServerWorld getWorld(int id) {
		if (id == -1) {
			return this.worlds[1];
		} else {
			return id == 1 ? this.worlds[2] : this.worlds[0];
		}
	}

	public String getVersion() {
		return "1.12.2";
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

	public GameProfile[] getProfiles() {
		return this.playerManager.getProfiles();
	}

	public String getServerModName() {
		return "vanilla";
	}

	public CrashReport populateCrashReport(CrashReport report) {
		report.getSystemDetailsSection().add("Profiler Position", new CrashCallable<String>() {
			public String call() throws Exception {
				return MinecraftServer.this.profiler.enabled ? MinecraftServer.this.profiler.getCurrentLocation() : "N/A (disabled)";
			}
		});
		if (this.playerManager != null) {
			report.getSystemDetailsSection()
				.add(
					"Player Count",
					new CrashCallable<String>() {
						public String call() {
							return MinecraftServer.this.playerManager.getCurrentPlayerCount()
								+ " / "
								+ MinecraftServer.this.playerManager.getMaxPlayerCount()
								+ "; "
								+ MinecraftServer.this.playerManager.getPlayers();
						}
					}
				);
		}

		return report;
	}

	public List<String> method_12835(CommandSource commandSource, String string, @Nullable BlockPos blockPos, boolean bl) {
		List<String> list = Lists.newArrayList();
		boolean bl2 = string.startsWith("/");
		if (bl2) {
			string = string.substring(1);
		}

		if (!bl2 && !bl) {
			String[] strings = string.split(" ", -1);
			String string3 = strings[strings.length - 1];

			for (String string4 : this.playerManager.getPlayerNames()) {
				if (AbstractCommand.method_2883(string3, string4)) {
					list.add(string4);
				}
			}

			return list;
		} else {
			boolean bl3 = !string.contains(" ");
			List<String> list2 = this.provider.getCompletions(commandSource, string, blockPos);
			if (!list2.isEmpty()) {
				for (String string2 : list2) {
					if (bl3 && !bl) {
						list.add("/" + string2);
					} else {
						list.add(string2);
					}
				}
			}

			return list;
		}
	}

	public boolean hasGameDir() {
		return this.gameDir != null;
	}

	@Override
	public String getTranslationKey() {
		return "Server";
	}

	@Override
	public void sendMessage(Text text) {
		LOGGER.info(text.asUnformattedString());
	}

	@Override
	public boolean canUseCommand(int permissionLevel, String commandLiteral) {
		return true;
	}

	public CommandRegistryProvider getCommandManager() {
		return this.provider;
	}

	public KeyPair getKeyPair() {
		return this.keyPair;
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
		for (ServerWorld serverWorld : this.worlds) {
			if (serverWorld != null) {
				if (serverWorld.getLevelProperties().isHardcore()) {
					serverWorld.getLevelProperties().setDifficulty(Difficulty.HARD);
					serverWorld.setMobSpawning(true, true);
				} else if (this.isSinglePlayer()) {
					serverWorld.getLevelProperties().setDifficulty(difficulty);
					serverWorld.setMobSpawning(serverWorld.getGlobalDifficulty() != Difficulty.PEACEFUL, true);
				} else {
					serverWorld.getLevelProperties().setDifficulty(difficulty);
					serverWorld.setMobSpawning(this.isMonsterSpawningEnabled(), this.spawnAnimals);
				}
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
		snooper.addGameInfo("run_time", (getTimeMillis() - snooper.getStartTime()) / 60L * 1000L);
		snooper.addGameInfo("avg_tick_ms", (int)(MathHelper.average(this.lastTickLengths) * 1.0E-6));
		int i = 0;
		if (this.worlds != null) {
			for (ServerWorld serverWorld : this.worlds) {
				if (serverWorld != null) {
					LevelProperties levelProperties = serverWorld.getLevelProperties();
					snooper.addGameInfo("world[" + i + "][dimension]", serverWorld.dimension.getDimensionType().getId());
					snooper.addGameInfo("world[" + i + "][mode]", levelProperties.getGamemode());
					snooper.addGameInfo("world[" + i + "][difficulty]", serverWorld.getGlobalDifficulty());
					snooper.addGameInfo("world[" + i + "][hardcore]", levelProperties.isHardcore());
					snooper.addGameInfo("world[" + i + "][generator_name]", levelProperties.getGeneratorType().getName());
					snooper.addGameInfo("world[" + i + "][generator_version]", levelProperties.getGeneratorType().getVersion());
					snooper.addGameInfo("world[" + i + "][height]", this.worldHeight);
					snooper.addGameInfo("world[" + i + "][chunks_loaded]", serverWorld.getChunkProvider().method_3874());
					i++;
				}
			}
		}

		snooper.addGameInfo("worlds", i);
	}

	@Override
	public void addSnooper(Snooper snooper) {
		snooper.addSystemInfo("singleplayer", this.isSinglePlayer());
		snooper.addSystemInfo("server_brand", this.getServerModName());
		snooper.addSystemInfo("gui_supported", GraphicsEnvironment.isHeadless() ? "headless" : "supported");
		snooper.addSystemInfo("dedicated", this.isDedicated());
	}

	@Override
	public boolean isSnooperEnabled() {
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

	public void method_2999(GameMode gameMode) {
		for (ServerWorld serverWorld : this.worlds) {
			serverWorld.getLevelProperties().getGameMode(gameMode);
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

	public abstract String method_3000(GameMode gameMode, boolean bl);

	public int getTicks() {
		return this.ticks;
	}

	public void enableProfiler() {
		this.profiling = true;
	}

	public Snooper getSnooper() {
		return this.field_13903;
	}

	@Override
	public World getWorld() {
		return this.worlds[0];
	}

	public boolean isSpawnProtected(World world, BlockPos pos, PlayerEntity player) {
		return false;
	}

	public boolean shouldForceGameMode() {
		return this.forceGameMode;
	}

	public Proxy getProxy() {
		return this.proxy;
	}

	public static long getTimeMillis() {
		return System.currentTimeMillis();
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

	@Nullable
	public Entity getEntity(UUID uUID) {
		for (ServerWorld serverWorld : this.worlds) {
			if (serverWorld != null) {
				Entity entity = serverWorld.getEntity(uUID);
				if (entity != null) {
					return entity;
				}
			}
		}

		return null;
	}

	@Override
	public boolean sendCommandFeedback() {
		return this.worlds[0].getGameRules().getBoolean("sendCommandFeedback");
	}

	@Override
	public MinecraftServer getMinecraftServer() {
		return this;
	}

	public int getMaxWorldBorderRadius() {
		return 29999984;
	}

	public <V> ListenableFuture<V> method_10815(Callable<V> callable) {
		Validate.notNull(callable);
		if (!this.isOnThread() && !this.isStopped()) {
			ListenableFutureTask<V> listenableFutureTask = ListenableFutureTask.create(callable);
			synchronized (this.queue) {
				this.queue.add(listenableFutureTask);
				return listenableFutureTask;
			}
		} else {
			try {
				return Futures.immediateFuture(callable.call());
			} catch (Exception var6) {
				return Futures.immediateFailedCheckedFuture(var6);
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

	public int method_12834(@Nullable ServerWorld serverWorld) {
		return serverWorld != null ? serverWorld.getGameRules().getInt("spawnRadius") : 10;
	}

	public class_3348 method_14910() {
		return this.worlds[0].method_14963();
	}

	public FunctionTickable method_14911() {
		return this.worlds[0].method_14962();
	}

	public void method_14912() {
		if (this.isOnThread()) {
			this.getPlayerManager().saveAllPlayerData();
			this.worlds[0].method_11487().method_12004();
			this.method_14910().method_14936();
			this.method_14911().reset();
			this.getPlayerManager().method_14980();
		} else {
			this.submit(this::method_14912);
		}
	}
}
