package net.minecraft.server.integrated;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.FutureTask;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.client.world.IntegratedServerCommandManager;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.NetworkEncryptionUtils;
import net.minecraft.server.LanServerPinger;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ServerWorldManager;
import net.minecraft.util.UserCache;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.snooper.Snooper;
import net.minecraft.world.DemoServerWorld;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.MultiServerWorld;
import net.minecraft.world.SaveHandler;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IntegratedServer extends MinecraftServer {
	private static final Logger LOGGER = LogManager.getLogger();
	private final MinecraftClient client;
	private final LevelInfo levelInfo;
	private boolean paused;
	private boolean published;
	private LanServerPinger pinger;

	public IntegratedServer(
		MinecraftClient minecraftClient,
		String string,
		String string2,
		LevelInfo levelInfo,
		YggdrasilAuthenticationService yggdrasilAuthenticationService,
		MinecraftSessionService minecraftSessionService,
		GameProfileRepository gameProfileRepository,
		UserCache userCache
	) {
		super(
			new File(minecraftClient.runDirectory, "saves"),
			minecraftClient.getNetworkProxy(),
			minecraftClient.method_12142(),
			yggdrasilAuthenticationService,
			minecraftSessionService,
			gameProfileRepository,
			userCache
		);
		this.setUserName(minecraftClient.getSession().getUsername());
		this.setLevelName(string);
		this.setServerName(string2);
		this.setDemo(minecraftClient.isDemo());
		this.setForceWorldUpgrade(levelInfo.hasBonusChest());
		this.setWorldHeight(256);
		this.setPlayerManager(new IntegratedPlayerManager(this));
		this.client = minecraftClient;
		this.levelInfo = this.isDemo() ? DemoServerWorld.INFO : levelInfo;
	}

	@Override
	protected CommandManager createCommandManager() {
		return new IntegratedServerCommandManager(this);
	}

	@Override
	protected void setupWorld(String world, String worldName, long seed, LevelGeneratorType generatorType, String generatorOptions) {
		this.upgradeWorld(world);
		this.worlds = new ServerWorld[3];
		this.field_3858 = new long[this.worlds.length][100];
		SaveHandler saveHandler = this.getSaveStorage().createSaveHandler(world, true);
		this.loadResourcePack(this.getLevelName(), saveHandler);
		LevelProperties levelProperties = saveHandler.getLevelProperties();
		if (levelProperties == null) {
			levelProperties = new LevelProperties(this.levelInfo, worldName);
		} else {
			levelProperties.setLevelName(worldName);
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

				this.worlds[i].setPropertiesInitialized(this.levelInfo);
			} else {
				this.worlds[i] = (ServerWorld)new MultiServerWorld(this, saveHandler, j, this.worlds[0], this.profiler).getWorld();
			}

			this.worlds[i].addListener(new ServerWorldManager(this, this.worlds[i]));
		}

		this.getPlayerManager().setMainWorld(this.worlds);
		if (this.worlds[0].getLevelProperties().getDifficulty() == null) {
			this.setDifficulty(this.client.options.difficulty);
		}

		this.prepareWorlds();
	}

	@Override
	protected boolean setupServer() throws IOException {
		LOGGER.info("Starting integrated minecraft server version 1.11.2");
		this.setOnlineMode(true);
		this.setSpawnAnimals(true);
		this.setSpawnNpcs(true);
		this.setPvpEnabled(true);
		this.setFlightEnabled(true);
		LOGGER.info("Generating keypair");
		this.setKeyPair(NetworkEncryptionUtils.generateServerKeyPair());
		this.setupWorld(this.getLevelName(), this.getServerName(), this.levelInfo.getSeed(), this.levelInfo.getGeneratorType(), this.levelInfo.getGeneratorOptions());
		this.setMotd(this.getUserName() + " - " + this.worlds[0].getLevelProperties().getLevelName());
		return true;
	}

	@Override
	protected void setupWorld() {
		boolean bl = this.paused;
		this.paused = MinecraftClient.getInstance().getNetworkHandler() != null && MinecraftClient.getInstance().isPaused();
		if (!bl && this.paused) {
			LOGGER.info("Saving and pausing game...");
			this.getPlayerManager().saveAllPlayerData();
			this.saveWorlds(false);
		}

		if (this.paused) {
			synchronized (this.queue) {
				while (!this.queue.isEmpty()) {
					Util.executeTask((FutureTask)this.queue.poll(), LOGGER);
				}
			}
		} else {
			super.setupWorld();
			if (this.client.options.viewDistance != this.getPlayerManager().getViewDistance()) {
				LOGGER.info("Changing view distance to {}, from {}", new Object[]{this.client.options.viewDistance, this.getPlayerManager().getViewDistance()});
				this.getPlayerManager().setViewDistance(this.client.options.viewDistance);
			}

			if (this.client.world != null) {
				LevelProperties levelProperties = this.worlds[0].getLevelProperties();
				LevelProperties levelProperties2 = this.client.world.getLevelProperties();
				if (!levelProperties.isDifficultyLocked() && levelProperties2.getDifficulty() != levelProperties.getDifficulty()) {
					LOGGER.info("Changing difficulty to {}, from {}", new Object[]{levelProperties2.getDifficulty(), levelProperties.getDifficulty()});
					this.setDifficulty(levelProperties2.getDifficulty());
				} else if (levelProperties2.isDifficultyLocked() && !levelProperties.isDifficultyLocked()) {
					LOGGER.info("Locking difficulty to {}", new Object[]{levelProperties2.getDifficulty()});

					for (ServerWorld serverWorld : this.worlds) {
						if (serverWorld != null) {
							serverWorld.getLevelProperties().setDifficultyLocked(true);
						}
					}
				}
			}
		}
	}

	@Override
	public boolean shouldGenerateStructures() {
		return false;
	}

	@Override
	public GameMode method_3026() {
		return this.levelInfo.method_3758();
	}

	@Override
	public Difficulty getDefaultDifficulty() {
		return this.client.world.getLevelProperties().getDifficulty();
	}

	@Override
	public boolean isHardcore() {
		return this.levelInfo.isHardcore();
	}

	@Override
	public boolean shouldBroadcastRconToOps() {
		return true;
	}

	@Override
	public boolean shouldBroadcastConsoleToIps() {
		return true;
	}

	@Override
	protected void saveWorlds(boolean silent) {
		super.saveWorlds(silent);
	}

	@Override
	public File getRunDirectory() {
		return this.client.runDirectory;
	}

	@Override
	public boolean isDedicated() {
		return false;
	}

	@Override
	public boolean isUsingNativeTransport() {
		return false;
	}

	@Override
	protected void setCrashReport(CrashReport report) {
		this.client.crash(report);
	}

	@Override
	public CrashReport populateCrashReport(CrashReport report) {
		report = super.populateCrashReport(report);
		report.getSystemDetailsSection().add("Type", new CrashCallable<String>() {
			public String call() throws Exception {
				return "Integrated Server (map_client.txt)";
			}
		});
		report.getSystemDetailsSection()
			.add(
				"Is Modded",
				new CrashCallable<String>() {
					public String call() throws Exception {
						String string = ClientBrandRetriever.getClientModName();
						if (!string.equals("vanilla")) {
							return "Definitely; Client brand changed to '" + string + "'";
						} else {
							string = IntegratedServer.this.getServerModName();
							if (!"vanilla".equals(string)) {
								return "Definitely; Server brand changed to '" + string + "'";
							} else {
								return MinecraftClient.class.getSigners() == null
									? "Very likely; Jar signature invalidated"
									: "Probably not. Jar signature remains and both client + server brands are untouched.";
							}
						}
					}
				}
			);
		return report;
	}

	@Override
	public void setDifficulty(Difficulty difficulty) {
		super.setDifficulty(difficulty);
		if (this.client.world != null) {
			this.client.world.getLevelProperties().setDifficulty(difficulty);
		}
	}

	@Override
	public void addSnooperInfo(Snooper snooper) {
		super.addSnooperInfo(snooper);
		snooper.addGameInfo("snooper_partner", this.client.getSnooper().getSnooperToken());
	}

	@Override
	public boolean isSnooperEnabled() {
		return MinecraftClient.getInstance().isSnooperEnabled();
	}

	@Override
	public String method_3000(GameMode gameMode, boolean bl) {
		try {
			int i = -1;

			try {
				i = NetworkUtils.getFreePort();
			} catch (IOException var5) {
			}

			if (i <= 0) {
				i = 25564;
			}

			this.getNetworkIo().bind(null, i);
			LOGGER.info("Started on {}", new Object[]{i});
			this.published = true;
			this.pinger = new LanServerPinger(this.getServerMotd(), i + "");
			this.pinger.start();
			this.getPlayerManager().setGameMode(gameMode);
			this.getPlayerManager().setCheatsAllowed(bl);
			this.client.player.method_12267(bl ? 4 : 0);
			return i + "";
		} catch (IOException var6) {
			return null;
		}
	}

	@Override
	public void stopServer() {
		super.stopServer();
		if (this.pinger != null) {
			this.pinger.interrupt();
			this.pinger = null;
		}
	}

	@Override
	public void stopRunning() {
		Futures.getUnchecked(this.submit(new Runnable() {
			public void run() {
				for (ServerPlayerEntity serverPlayerEntity : Lists.newArrayList(IntegratedServer.this.getPlayerManager().getPlayers())) {
					if (!serverPlayerEntity.getUuid().equals(IntegratedServer.this.client.player.getUuid())) {
						IntegratedServer.this.getPlayerManager().method_12830(serverPlayerEntity);
					}
				}
			}
		}));
		super.stopRunning();
		if (this.pinger != null) {
			this.pinger.interrupt();
			this.pinger = null;
		}
	}

	public boolean isPublished() {
		return this.published;
	}

	@Override
	public void method_2999(GameMode gameMode) {
		super.method_2999(gameMode);
		this.getPlayerManager().setGameMode(gameMode);
	}

	@Override
	public boolean areCommandBlocksEnabled() {
		return true;
	}

	@Override
	public int getOpPermissionLevel() {
		return 4;
	}

	public void method_12535() {
		if (this.isOnThread()) {
			this.worlds[0].method_11487().method_12004();
		} else {
			this.submit(new Runnable() {
				public void run() {
					IntegratedServer.this.method_12535();
				}
			});
		}
	}
}
