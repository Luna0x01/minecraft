package net.minecraft.server.dedicated;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.regex.Pattern;
import net.minecraft.class_3915;
import net.minecraft.class_4325;
import net.minecraft.class_4336;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.network.NetworkEncryptionUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.Console;
import net.minecraft.server.dedicated.gui.DedicatedServerGui;
import net.minecraft.server.rcon.QueryResponseHandler;
import net.minecraft.server.rcon.RconServer;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.UserCache;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.snooper.Snooper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MinecraftDedicatedServer extends MinecraftServer implements DedicatedServer {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Pattern field_15001 = Pattern.compile("^[a-fA-F0-9]{40}$");
	private final List<PendingServerCommand> commandQueue = Collections.synchronizedList(Lists.newArrayList());
	private QueryResponseHandler queryResponseHandler;
	private final Console field_13846 = new Console(this);
	private RconServer rconServer;
	private AbstractPropertiesHandler abstractPropertiesHandler;
	private EulaReader eulaReader;
	private boolean shouldGenerateStructures;
	private GameMode field_2736;
	private boolean hasGui;

	public MinecraftDedicatedServer(
		File file,
		DataFixer dataFixer,
		YggdrasilAuthenticationService yggdrasilAuthenticationService,
		MinecraftSessionService minecraftSessionService,
		GameProfileRepository gameProfileRepository,
		UserCache userCache
	) {
		super(file, Proxy.NO_PROXY, dataFixer, new CommandManager(true), yggdrasilAuthenticationService, minecraftSessionService, gameProfileRepository, userCache);
		new Thread("Server Infinisleeper") {
			{
				this.setDaemon(true);
				this.setUncaughtExceptionHandler(new class_4325(MinecraftDedicatedServer.LOGGER));
				this.start();
			}

			public void run() {
				while (true) {
					try {
						Thread.sleep(2147483647L);
					} catch (InterruptedException var2) {
					}
				}
			}
		};
	}

	@Override
	protected boolean setupServer() throws IOException {
		Thread thread = new Thread("Server console handler") {
			public void run() {
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

				String string;
				try {
					while (!MinecraftDedicatedServer.this.isStopped() && MinecraftDedicatedServer.this.isRunning() && (string = bufferedReader.readLine()) != null) {
						MinecraftDedicatedServer.this.method_2065(string, MinecraftDedicatedServer.this.method_20330());
					}
				} catch (IOException var4) {
					MinecraftDedicatedServer.LOGGER.error("Exception handling console input", var4);
				}
			}
		};
		thread.setDaemon(true);
		thread.setUncaughtExceptionHandler(new class_4325(LOGGER));
		thread.start();
		LOGGER.info("Starting minecraft server version 1.13.2");
		if (Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L) {
			LOGGER.warn("To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
		}

		LOGGER.info("Loading properties");
		this.abstractPropertiesHandler = new AbstractPropertiesHandler(new File("server.properties"));
		this.eulaReader = new EulaReader(new File("eula.txt"));
		if (!this.eulaReader.isEulaAgreedTo()) {
			LOGGER.info("You need to agree to the EULA in order to run the server. Go to eula.txt for more info.");
			this.eulaReader.createEulaFile();
			return false;
		} else {
			if (this.isSinglePlayer()) {
				this.setServerIp("127.0.0.1");
			} else {
				this.setOnlineMode(this.abstractPropertiesHandler.getBooleanOrDefault("online-mode", true));
				this.method_13913(this.abstractPropertiesHandler.getBooleanOrDefault("prevent-proxy-connections", false));
				this.setServerIp(this.abstractPropertiesHandler.getOrDefault("server-ip", ""));
			}

			this.setSpawnAnimals(this.abstractPropertiesHandler.getBooleanOrDefault("spawn-animals", true));
			this.setSpawnNpcs(this.abstractPropertiesHandler.getBooleanOrDefault("spawn-npcs", true));
			this.setPvpEnabled(this.abstractPropertiesHandler.getBooleanOrDefault("pvp", true));
			this.setFlightEnabled(this.abstractPropertiesHandler.getBooleanOrDefault("allow-flight", false));
			this.setResourcePack(this.abstractPropertiesHandler.getOrDefault("resource-pack", ""), this.method_12762());
			this.setMotd(this.abstractPropertiesHandler.getOrDefault("motd", "A Minecraft Server"));
			this.setForceGameMode(this.abstractPropertiesHandler.getBooleanOrDefault("force-gamemode", false));
			this.setPlayerIdleTimeout(this.abstractPropertiesHandler.getIntOrDefault("player-idle-timeout", 0));
			this.method_20350(this.abstractPropertiesHandler.getBooleanOrDefault("enforce-whitelist", false));
			if (this.abstractPropertiesHandler.getIntOrDefault("difficulty", 1) < 0) {
				this.abstractPropertiesHandler.set("difficulty", 0);
			} else if (this.abstractPropertiesHandler.getIntOrDefault("difficulty", 1) > 3) {
				this.abstractPropertiesHandler.set("difficulty", 3);
			}

			this.shouldGenerateStructures = this.abstractPropertiesHandler.getBooleanOrDefault("generate-structures", true);
			int i = this.abstractPropertiesHandler.getIntOrDefault("gamemode", GameMode.SURVIVAL.getGameModeId());
			this.field_2736 = LevelInfo.method_3754(i);
			LOGGER.info("Default game type: {}", this.field_2736);
			InetAddress inetAddress = null;
			if (!this.getServerIp().isEmpty()) {
				inetAddress = InetAddress.getByName(this.getServerIp());
			}

			if (this.getServerPort() < 0) {
				this.setServerPort(this.abstractPropertiesHandler.getIntOrDefault("server-port", 25565));
			}

			LOGGER.info("Generating keypair");
			this.setKeyPair(NetworkEncryptionUtils.generateServerKeyPair());
			LOGGER.info("Starting Minecraft server on {}:{}", this.getServerIp().isEmpty() ? "*" : this.getServerIp(), this.getServerPort());

			try {
				this.getNetworkIo().bind(inetAddress, this.getServerPort());
			} catch (IOException var18) {
				LOGGER.warn("**** FAILED TO BIND TO PORT!");
				LOGGER.warn("The exception was: {}", var18.toString());
				LOGGER.warn("Perhaps a server is already running on that port?");
				return false;
			}

			if (!this.isOnlineMode()) {
				LOGGER.warn("**** SERVER IS RUNNING IN OFFLINE/INSECURE MODE!");
				LOGGER.warn("The server will make no attempt to authenticate usernames. Beware.");
				LOGGER.warn(
					"While this makes the game possible to play without internet access, it also opens up the ability for hackers to connect with any username they choose."
				);
				LOGGER.warn("To change this, set \"online-mode\" to \"true\" in the server.properties file.");
			}

			if (this.convertData()) {
				this.getUserCache().save();
			}

			if (!ServerConfigHandler.checkSuccess(this.abstractPropertiesHandler)) {
				return false;
			} else {
				this.setPlayerManager(new DedicatedPlayerManager(this));
				long l = Util.method_20230();
				if (this.getLevelName() == null) {
					this.setLevelName(this.abstractPropertiesHandler.getOrDefault("level-name", "world"));
				}

				String string = this.abstractPropertiesHandler.getOrDefault("level-seed", "");
				String string2 = this.abstractPropertiesHandler.getOrDefault("level-type", "DEFAULT");
				String string3 = this.abstractPropertiesHandler.getOrDefault("generator-settings", "");
				long m = new Random().nextLong();
				if (!string.isEmpty()) {
					try {
						long n = Long.parseLong(string);
						if (n != 0L) {
							m = n;
						}
					} catch (NumberFormatException var17) {
						m = (long)string.hashCode();
					}
				}

				LevelGeneratorType levelGeneratorType = LevelGeneratorType.getTypeFromName(string2);
				if (levelGeneratorType == null) {
					levelGeneratorType = LevelGeneratorType.DEFAULT;
				}

				this.areCommandBlocksEnabled();
				this.getOpPermissionLevel();
				this.method_2409();
				this.getNetworkCompressionThreshold();
				this.setWorldHeight(this.abstractPropertiesHandler.getIntOrDefault("max-build-height", 256));
				this.setWorldHeight((this.getWorldHeight() + 8) / 16 * 16);
				this.setWorldHeight(MathHelper.clamp(this.getWorldHeight(), 64, 256));
				this.abstractPropertiesHandler.set("max-build-height", this.getWorldHeight());
				SkullBlockEntity.method_11666(this.getUserCache());
				SkullBlockEntity.method_11665(this.getSessionService());
				UserCache.setUseRemote(this.isOnlineMode());
				LOGGER.info("Preparing level \"{}\"", this.getLevelName());
				JsonObject jsonObject = new JsonObject();
				if (levelGeneratorType == LevelGeneratorType.FLAT) {
					jsonObject.addProperty("flat_world_options", string3);
				} else if (!string3.isEmpty()) {
					jsonObject = JsonHelper.method_21502(string3);
				}

				this.method_20320(this.getLevelName(), this.getLevelName(), m, levelGeneratorType, jsonObject);
				long o = Util.method_20230() - l;
				String string4 = String.format(Locale.ROOT, "%.3fs", (double)o / 1.0E9);
				LOGGER.info("Done ({})! For help, type \"help\"", string4);
				if (this.abstractPropertiesHandler.method_12760("announce-player-achievements")) {
					this.method_20335()
						.method_16297("announceAdvancements", this.abstractPropertiesHandler.getBooleanOrDefault("announce-player-achievements", true) ? "true" : "false", this);
					this.abstractPropertiesHandler.method_12761("announce-player-achievements");
					this.abstractPropertiesHandler.save();
				}

				if (this.abstractPropertiesHandler.getBooleanOrDefault("enable-query", false)) {
					LOGGER.info("Starting GS4 status listener");
					this.queryResponseHandler = new QueryResponseHandler(this);
					this.queryResponseHandler.start();
				}

				if (this.abstractPropertiesHandler.getBooleanOrDefault("enable-rcon", false)) {
					LOGGER.info("Starting remote control listener");
					this.rconServer = new RconServer(this);
					this.rconServer.start();
				}

				if (this.getMaxTickTime() > 0L) {
					Thread thread2 = new Thread(new DedicatedServerWatchdog(this));
					thread2.setUncaughtExceptionHandler(new class_4336(LOGGER));
					thread2.setName("Server Watchdog");
					thread2.setDaemon(true);
					thread2.start();
				}

				Items.AIR.appendToItemGroup(ItemGroup.SEARCH, DefaultedList.of());
				return true;
			}
		}
	}

	public String method_12762() {
		if (this.abstractPropertiesHandler.method_12760("resource-pack-hash")) {
			if (this.abstractPropertiesHandler.method_12760("resource-pack-sha1")) {
				LOGGER.warn("resource-pack-hash is deprecated and found along side resource-pack-sha1. resource-pack-hash will be ignored.");
			} else {
				LOGGER.warn("resource-pack-hash is deprecated. Please use resource-pack-sha1 instead.");
				this.abstractPropertiesHandler.getOrDefault("resource-pack-sha1", this.abstractPropertiesHandler.getOrDefault("resource-pack-hash", ""));
				this.abstractPropertiesHandler.method_12761("resource-pack-hash");
			}
		}

		String string = this.abstractPropertiesHandler.getOrDefault("resource-pack-sha1", "");
		if (!string.isEmpty() && !field_15001.matcher(string).matches()) {
			LOGGER.warn("Invalid sha1 for ressource-pack-sha1");
		}

		if (!this.abstractPropertiesHandler.getOrDefault("resource-pack", "").isEmpty() && string.isEmpty()) {
			LOGGER.warn("You specified a resource pack without providing a sha1 hash. Pack will be updated on the client only if you change the name of the pack.");
		}

		return string;
	}

	@Override
	public void method_2999(GameMode gameMode) {
		super.method_2999(gameMode);
		this.field_2736 = gameMode;
	}

	@Override
	public boolean shouldGenerateStructures() {
		return this.shouldGenerateStructures;
	}

	@Override
	public GameMode method_3026() {
		return this.field_2736;
	}

	@Override
	public Difficulty getDefaultDifficulty() {
		return Difficulty.byOrdinal(this.abstractPropertiesHandler.getIntOrDefault("difficulty", Difficulty.NORMAL.getId()));
	}

	@Override
	public boolean isHardcore() {
		return this.abstractPropertiesHandler.getBooleanOrDefault("hardcore", false);
	}

	@Override
	public CrashReport populateCrashReport(CrashReport report) {
		report = super.populateCrashReport(report);
		report.getSystemDetailsSection().add("Is Modded", (CrashCallable<String>)(() -> {
			String string = this.getServerModName();
			return !"vanilla".equals(string) ? "Definitely; Server brand changed to '" + string + "'" : "Unknown (can't tell)";
		}));
		report.getSystemDetailsSection().add("Type", (CrashCallable<String>)(() -> "Dedicated Server (map_server.txt)"));
		return report;
	}

	@Override
	protected void exit() {
		System.exit(0);
	}

	@Override
	protected void method_20347(BooleanSupplier booleanSupplier) {
		super.method_20347(booleanSupplier);
		this.executeQueuedCommands();
	}

	@Override
	public boolean isNetherAllowed() {
		return this.abstractPropertiesHandler.getBooleanOrDefault("allow-nether", true);
	}

	@Override
	public boolean isMonsterSpawningEnabled() {
		return this.abstractPropertiesHandler.getBooleanOrDefault("spawn-monsters", true);
	}

	@Override
	public void addSnooperInfo(Snooper snooper) {
		snooper.addGameInfo("whitelist_enabled", this.getPlayerManager().isWhitelistEnabled());
		snooper.addGameInfo("whitelist_count", this.getPlayerManager().getWhitelistedNames().length);
		super.addSnooperInfo(snooper);
	}

	@Override
	public boolean method_2409() {
		if (this.abstractPropertiesHandler.getBooleanOrDefault("snooper-enabled", true)) {
		}

		return false;
	}

	public void method_2065(String string, class_3915 arg) {
		this.commandQueue.add(new PendingServerCommand(string, arg));
	}

	public void executeQueuedCommands() {
		while (!this.commandQueue.isEmpty()) {
			PendingServerCommand pendingServerCommand = (PendingServerCommand)this.commandQueue.remove(0);
			this.method_2971().method_17519(pendingServerCommand.field_2697, pendingServerCommand.command);
		}
	}

	@Override
	public boolean isDedicated() {
		return true;
	}

	@Override
	public boolean isUsingNativeTransport() {
		return this.abstractPropertiesHandler.getBooleanOrDefault("use-native-transport", true);
	}

	public DedicatedPlayerManager getPlayerManager() {
		return (DedicatedPlayerManager)super.getPlayerManager();
	}

	@Override
	public boolean shouldBroadcastConsoleToIps() {
		return true;
	}

	@Override
	public int getIntOrDefault(String name, int value) {
		return this.abstractPropertiesHandler.getIntOrDefault(name, value);
	}

	@Override
	public String getOrDefault(String name, String value) {
		return this.abstractPropertiesHandler.getOrDefault(name, value);
	}

	public boolean getBooleanOrDefault(String name, boolean value) {
		return this.abstractPropertiesHandler.getBooleanOrDefault(name, value);
	}

	@Override
	public void setProperty(String property, Object value) {
		this.abstractPropertiesHandler.set(property, value);
	}

	@Override
	public void saveAbstractPropertiesHandler() {
		this.abstractPropertiesHandler.save();
	}

	@Override
	public String getPropertiesFilePath() {
		File file = this.abstractPropertiesHandler.getPropertiesFile();
		return file != null ? file.getAbsolutePath() : "No settings file";
	}

	@Override
	public String getHostname() {
		return this.getServerIp();
	}

	@Override
	public int getPort() {
		return this.getServerPort();
	}

	@Override
	public String getMotd() {
		return this.getServerMotd();
	}

	public void createGui() {
		DedicatedServerGui.create(this);
		this.hasGui = true;
	}

	@Override
	public boolean hasGui() {
		return this.hasGui;
	}

	@Override
	public boolean method_20311(GameMode gameMode, boolean bl, int i) {
		return false;
	}

	@Override
	public boolean areCommandBlocksEnabled() {
		return this.abstractPropertiesHandler.getBooleanOrDefault("enable-command-block", false);
	}

	@Override
	public int getSpawnProtectionRadius() {
		return this.abstractPropertiesHandler.getIntOrDefault("spawn-protection", super.getSpawnProtectionRadius());
	}

	@Override
	public boolean isSpawnProtected(World world, BlockPos pos, PlayerEntity player) {
		if (world.dimension.method_11789() != DimensionType.OVERWORLD) {
			return false;
		} else if (this.getPlayerManager().getOpList().isEmpty()) {
			return false;
		} else if (this.getPlayerManager().isOperator(player.getGameProfile())) {
			return false;
		} else if (this.getSpawnProtectionRadius() <= 0) {
			return false;
		} else {
			BlockPos blockPos = world.method_3585();
			int i = MathHelper.abs(pos.getX() - blockPos.getX());
			int j = MathHelper.abs(pos.getZ() - blockPos.getZ());
			int k = Math.max(i, j);
			return k <= this.getSpawnProtectionRadius();
		}
	}

	@Override
	public int getOpPermissionLevel() {
		return this.abstractPropertiesHandler.getIntOrDefault("op-permission-level", 4);
	}

	@Override
	public void setPlayerIdleTimeout(int playerIdleTimeout) {
		super.setPlayerIdleTimeout(playerIdleTimeout);
		this.abstractPropertiesHandler.set("player-idle-timeout", playerIdleTimeout);
		this.saveAbstractPropertiesHandler();
	}

	@Override
	public boolean shouldBroadcastRconToOps() {
		return this.abstractPropertiesHandler.getBooleanOrDefault("broadcast-rcon-to-ops", true);
	}

	@Override
	public boolean method_17412() {
		return this.abstractPropertiesHandler.getBooleanOrDefault("broadcast-console-to-ops", true);
	}

	@Override
	public int getMaxWorldBorderRadius() {
		int i = this.abstractPropertiesHandler.getIntOrDefault("max-world-size", super.getMaxWorldBorderRadius());
		if (i < 1) {
			i = 1;
		} else if (i > super.getMaxWorldBorderRadius()) {
			i = super.getMaxWorldBorderRadius();
		}

		return i;
	}

	@Override
	public int getNetworkCompressionThreshold() {
		return this.abstractPropertiesHandler.getIntOrDefault("network-compression-threshold", super.getNetworkCompressionThreshold());
	}

	protected boolean convertData() {
		boolean bl = false;

		for (int i = 0; !bl && i <= 2; i++) {
			if (i > 0) {
				LOGGER.warn("Encountered a problem while converting the user banlist, retrying in a few seconds");
				this.sleepFiveSeconds();
			}

			bl = ServerConfigHandler.convertBannedPlayers(this);
		}

		boolean bl2 = false;

		for (int var7 = 0; !bl2 && var7 <= 2; var7++) {
			if (var7 > 0) {
				LOGGER.warn("Encountered a problem while converting the ip banlist, retrying in a few seconds");
				this.sleepFiveSeconds();
			}

			bl2 = ServerConfigHandler.convertBannedIps(this);
		}

		boolean bl3 = false;

		for (int var8 = 0; !bl3 && var8 <= 2; var8++) {
			if (var8 > 0) {
				LOGGER.warn("Encountered a problem while converting the op list, retrying in a few seconds");
				this.sleepFiveSeconds();
			}

			bl3 = ServerConfigHandler.convertOperators(this);
		}

		boolean bl4 = false;

		for (int var9 = 0; !bl4 && var9 <= 2; var9++) {
			if (var9 > 0) {
				LOGGER.warn("Encountered a problem while converting the whitelist, retrying in a few seconds");
				this.sleepFiveSeconds();
			}

			bl4 = ServerConfigHandler.convertWhitelist(this);
		}

		boolean bl5 = false;

		for (int var10 = 0; !bl5 && var10 <= 2; var10++) {
			if (var10 > 0) {
				LOGGER.warn("Encountered a problem while converting the player save files, retrying in a few seconds");
				this.sleepFiveSeconds();
			}

			bl5 = ServerConfigHandler.convertPlayerFiles(this, this.abstractPropertiesHandler);
		}

		return bl || bl2 || bl3 || bl4 || bl5;
	}

	private void sleepFiveSeconds() {
		try {
			Thread.sleep(5000L);
		} catch (InterruptedException var2) {
		}
	}

	public long getMaxTickTime() {
		return this.abstractPropertiesHandler.getLongOrDefault("max-tick-time", TimeUnit.MINUTES.toMillis(1L));
	}

	@Override
	public String getPlugins() {
		return "";
	}

	@Override
	public String executeRconCommand(String name) {
		this.field_13846.destroy();
		this.method_2971().method_17519(this.field_13846.method_21391(), name);
		return this.field_13846.getTextAsString();
	}
}
