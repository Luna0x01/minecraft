package net.minecraft.server.integrated;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.FutureTask;
import java.util.function.BooleanSupplier;
import net.minecraft.class_4070;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.NetworkEncryptionUtils;
import net.minecraft.server.LanServerPinger;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.UserCache;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.snooper.Snooper;
import net.minecraft.world.DemoServerWorld;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.SaveHandler;
import net.minecraft.world.dimension.DimensionType;
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
	private int field_21115 = -1;
	private LanServerPinger pinger;
	private UUID field_21116;

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
			new CommandManager(false),
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
	protected void method_20320(String string, String string2, long l, LevelGeneratorType levelGeneratorType, JsonElement jsonElement) {
		this.upgradeWorld(string);
		SaveHandler saveHandler = this.getSaveStorage().method_250(string, this);
		this.loadResourcePack(this.getLevelName(), saveHandler);
		LevelProperties levelProperties = saveHandler.getLevelProperties();
		if (levelProperties == null) {
			levelProperties = new LevelProperties(this.levelInfo, string2);
		} else {
			levelProperties.setLevelName(string2);
		}

		this.method_20319(saveHandler.getWorldFolder(), levelProperties);
		class_4070 lv = new class_4070(saveHandler);
		this.method_20316(saveHandler, lv, levelProperties, this.levelInfo);
		if (this.method_20312(DimensionType.OVERWORLD).method_3588().getDifficulty() == null) {
			this.setDifficulty(this.client.options.difficulty);
		}

		this.method_20317(lv);
	}

	@Override
	protected boolean setupServer() throws IOException {
		LOGGER.info("Starting integrated minecraft server version 1.13.2");
		this.setOnlineMode(true);
		this.setSpawnAnimals(true);
		this.setSpawnNpcs(true);
		this.setPvpEnabled(true);
		this.setFlightEnabled(true);
		LOGGER.info("Generating keypair");
		this.setKeyPair(NetworkEncryptionUtils.generateServerKeyPair());
		this.method_20320(this.getLevelName(), this.getServerName(), this.levelInfo.getSeed(), this.levelInfo.getGeneratorType(), this.levelInfo.method_4695());
		this.setMotd(this.getUserName() + " - " + this.method_20312(DimensionType.OVERWORLD).method_3588().getLevelName());
		return true;
	}

	@Override
	protected void method_20324(BooleanSupplier booleanSupplier) {
		boolean bl = this.paused;
		this.paused = MinecraftClient.getInstance().getNetworkHandler() != null && MinecraftClient.getInstance().isPaused();
		if (!bl && this.paused) {
			LOGGER.info("Saving and pausing game...");
			this.getPlayerManager().saveAllPlayerData();
			this.saveWorlds(false);
		}

		FutureTask<?> futureTask;
		if (this.paused) {
			while ((futureTask = (FutureTask<?>)this.queue.poll()) != null) {
				Util.executeTask(futureTask, LOGGER);
			}
		} else {
			super.method_20324(booleanSupplier);
			if (this.client.options.viewDistance != this.getPlayerManager().getViewDistance()) {
				LOGGER.info("Changing view distance to {}, from {}", this.client.options.viewDistance, this.getPlayerManager().getViewDistance());
				this.getPlayerManager().setViewDistance(this.client.options.viewDistance);
			}

			if (this.client.world != null) {
				LevelProperties levelProperties = this.method_20312(DimensionType.OVERWORLD).method_3588();
				LevelProperties levelProperties2 = this.client.world.method_3588();
				if (!levelProperties.isDifficultyLocked() && levelProperties2.getDifficulty() != levelProperties.getDifficulty()) {
					LOGGER.info("Changing difficulty to {}, from {}", levelProperties2.getDifficulty(), levelProperties.getDifficulty());
					this.setDifficulty(levelProperties2.getDifficulty());
				} else if (levelProperties2.isDifficultyLocked() && !levelProperties.isDifficultyLocked()) {
					LOGGER.info("Locking difficulty to {}", levelProperties2.getDifficulty());

					for (ServerWorld serverWorld : this.method_20351()) {
						if (serverWorld != null) {
							serverWorld.method_3588().setDifficultyLocked(true);
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
		return this.client.world.method_3588().getDifficulty();
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
	public boolean method_17412() {
		return true;
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
		report.getSystemDetailsSection().add("Type", "Integrated Server (map_client.txt)");
		report.getSystemDetailsSection()
			.add(
				"Is Modded",
				(CrashCallable<String>)(() -> {
					String string = ClientBrandRetriever.getClientModName();
					if (!string.equals("vanilla")) {
						return "Definitely; Client brand changed to '" + string + "'";
					} else {
						string = this.getServerModName();
						if (!"vanilla".equals(string)) {
							return "Definitely; Server brand changed to '" + string + "'";
						} else {
							return MinecraftClient.class.getSigners() == null
								? "Very likely; Jar signature invalidated"
								: "Probably not. Jar signature remains and both client + server brands are untouched.";
						}
					}
				})
			);
		return report;
	}

	@Override
	public void setDifficulty(Difficulty difficulty) {
		super.setDifficulty(difficulty);
		if (this.client.world != null) {
			this.client.world.method_3588().setDifficulty(difficulty);
		}
	}

	@Override
	public void addSnooperInfo(Snooper snooper) {
		super.addSnooperInfo(snooper);
		snooper.addGameInfo("snooper_partner", this.client.getSnooper().getSnooperToken());
	}

	@Override
	public boolean method_2409() {
		return MinecraftClient.getInstance().method_2409();
	}

	@Override
	public boolean method_20311(GameMode gameMode, boolean bl, int i) {
		try {
			this.getNetworkIo().bind(null, i);
			LOGGER.info("Started serving on {}", i);
			this.field_21115 = i;
			this.pinger = new LanServerPinger(this.getServerMotd(), i + "");
			this.pinger.start();
			this.getPlayerManager().setGameMode(gameMode);
			this.getPlayerManager().setCheatsAllowed(bl);
			int j = this.method_20318(this.client.player.getGameProfile());
			this.client.player.method_12267(j);

			for (ServerPlayerEntity serverPlayerEntity : this.getPlayerManager().getPlayers()) {
				this.method_2971().method_17532(serverPlayerEntity);
			}

			return true;
		} catch (IOException var7) {
			return false;
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
		Futures.getUnchecked(this.submit(() -> {
			for (ServerPlayerEntity serverPlayerEntity : Lists.newArrayList(this.getPlayerManager().getPlayers())) {
				if (!serverPlayerEntity.getUuid().equals(this.field_21116)) {
					this.getPlayerManager().method_12830(serverPlayerEntity);
				}
			}
		}));
		super.stopRunning();
		if (this.pinger != null) {
			this.pinger.interrupt();
			this.pinger = null;
		}
	}

	@Override
	public boolean shouldBroadcastConsoleToIps() {
		return this.field_21115 > -1;
	}

	@Override
	public int getServerPort() {
		return this.field_21115;
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
		return 2;
	}

	public void method_19612(UUID uUID) {
		this.field_21116 = uUID;
	}
}
