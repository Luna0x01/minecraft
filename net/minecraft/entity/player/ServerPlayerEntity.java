package net.minecraft.entity.player;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.block.Block;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LockableScreenHandlerFactory;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.data.Trader;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.slot.CraftingResultSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.NetworkSyncedItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.ClientSettingsC2SPacket;
import net.minecraft.network.packet.s2c.play.BedSleepS2CPacket;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkMapS2CPacket;
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.CombatEventS2CPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttachS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.ExperienceBarUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundIdS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.network.packet.s2c.play.RemoveEntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.ResourcePackSendS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerPropertyUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.SetCameraEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.SignEditorOpenS2CPacket;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.screen.ChestScreenHandler;
import net.minecraft.screen.HorseScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.VillagerScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.OperatorEntry;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.JsonSet;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.UseAction;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.TraderOfferList;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.level.LevelInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPlayerEntity extends PlayerEntity implements ScreenHandlerListener {
	private static final Logger LOGGER = LogManager.getLogger();
	private String language = "en_US";
	public ServerPlayNetworkHandler networkHandler;
	public final MinecraftServer server;
	public final ServerPlayerInteractionManager interactionManager;
	public double serverPosX;
	public double serverPosZ;
	public final List<ChunkPos> loadedChunks = Lists.newLinkedList();
	private final List<Integer> removedEntities = Lists.newLinkedList();
	private final ServerStatHandler statHandler;
	private float syncedHealth = Float.MIN_VALUE;
	private float lastHealth = -1.0E8F;
	private int lastHungerLevel = -99999999;
	private boolean wasHungry = true;
	private int lastXp = -99999999;
	private int spawnProtectionTicks = 60;
	private PlayerEntity.ChatVisibilityType visibilityType;
	private boolean chatColors = true;
	private long lastActionTime = System.currentTimeMillis();
	private Entity spectatingEntity = null;
	private int screenHandlerSyncId;
	public boolean skipPacketSlotUpdates;
	public int ping;
	public boolean killedEnderdragon;

	public ServerPlayerEntity(
		MinecraftServer minecraftServer, ServerWorld serverWorld, GameProfile gameProfile, ServerPlayerInteractionManager serverPlayerInteractionManager
	) {
		super(serverWorld, gameProfile);
		serverPlayerInteractionManager.player = this;
		this.interactionManager = serverPlayerInteractionManager;
		BlockPos blockPos = serverWorld.getSpawnPos();
		if (!serverWorld.dimension.hasNoSkylight() && serverWorld.getLevelProperties().getGameMode() != LevelInfo.GameMode.ADVENTURE) {
			int i = Math.max(5, minecraftServer.getSpawnProtectionRadius() - 6);
			int j = MathHelper.floor(serverWorld.getWorldBorder().getDistanceInsideBorder((double)blockPos.getX(), (double)blockPos.getZ()));
			if (j < i) {
				i = j;
			}

			if (j <= 1) {
				i = 1;
			}

			blockPos = serverWorld.getTopPosition(blockPos.add(this.random.nextInt(i * 2) - i, 0, this.random.nextInt(i * 2) - i));
		}

		this.server = minecraftServer;
		this.statHandler = minecraftServer.getPlayerManager().createStatHandler(this);
		this.stepHeight = 0.0F;
		this.refreshPositionAndAngles(blockPos, 0.0F, 0.0F);

		while (!serverWorld.doesBoxCollide(this, this.getBoundingBox()).isEmpty() && this.y < 255.0) {
			this.updatePosition(this.x, this.y + 1.0, this.z);
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("playerGameType", 99)) {
			if (MinecraftServer.getServer().shouldForceGameMode()) {
				this.interactionManager.setGameMode(MinecraftServer.getServer().getDefaultGameMode());
			} else {
				this.interactionManager.setGameMode(LevelInfo.GameMode.byId(nbt.getInt("playerGameType")));
			}
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("playerGameType", this.interactionManager.getGameMode().getId());
	}

	@Override
	public void incrementXp(int xp) {
		super.incrementXp(xp);
		this.lastXp = -1;
	}

	@Override
	public void decrementXp(int xp) {
		super.decrementXp(xp);
		this.lastXp = -1;
	}

	public void listenToScreenHandler() {
		this.openScreenHandler.addListener(this);
	}

	@Override
	public void enterCombat() {
		super.enterCombat();
		this.networkHandler.sendPacket(new CombatEventS2CPacket(this.getDamageTracker(), CombatEventS2CPacket.Type.ENTER_COMBAT));
	}

	@Override
	public void endCombat() {
		super.endCombat();
		this.networkHandler.sendPacket(new CombatEventS2CPacket(this.getDamageTracker(), CombatEventS2CPacket.Type.END_COMBAT));
	}

	@Override
	public void tick() {
		this.interactionManager.tick();
		this.spawnProtectionTicks--;
		if (this.timeUntilRegen > 0) {
			this.timeUntilRegen--;
		}

		this.openScreenHandler.sendContentUpdates();
		if (!this.world.isClient && !this.openScreenHandler.canUse(this)) {
			this.closeHandledScreen();
			this.openScreenHandler = this.playerScreenHandler;
		}

		while (!this.removedEntities.isEmpty()) {
			int i = Math.min(this.removedEntities.size(), Integer.MAX_VALUE);
			int[] is = new int[i];
			Iterator<Integer> iterator = this.removedEntities.iterator();
			int j = 0;

			while (iterator.hasNext() && j < i) {
				is[j++] = (Integer)iterator.next();
				iterator.remove();
			}

			this.networkHandler.sendPacket(new EntitiesDestroyS2CPacket(is));
		}

		if (!this.loadedChunks.isEmpty()) {
			List<Chunk> list = Lists.newArrayList();
			Iterator<ChunkPos> iterator2 = this.loadedChunks.iterator();
			List<BlockEntity> list2 = Lists.newArrayList();

			while (iterator2.hasNext() && list.size() < 10) {
				ChunkPos chunkPos = (ChunkPos)iterator2.next();
				if (chunkPos != null) {
					if (this.world.blockExists(new BlockPos(chunkPos.x << 4, 0, chunkPos.z << 4))) {
						Chunk chunk = this.world.getChunk(chunkPos.x, chunkPos.z);
						if (chunk.isPopulated()) {
							list.add(chunk);
							list2.addAll(((ServerWorld)this.world).method_2134(chunkPos.x * 16, 0, chunkPos.z * 16, chunkPos.x * 16 + 16, 256, chunkPos.z * 16 + 16));
							iterator2.remove();
						}
					}
				} else {
					iterator2.remove();
				}
			}

			if (!list.isEmpty()) {
				if (list.size() == 1) {
					this.networkHandler.sendPacket(new ChunkDataS2CPacket((Chunk)list.get(0), true, 65535));
				} else {
					this.networkHandler.sendPacket(new ChunkMapS2CPacket(list));
				}

				for (BlockEntity blockEntity : list2) {
					this.updateBlockEntity(blockEntity);
				}

				for (Chunk chunk2 : list) {
					this.getServerWorld().getEntityTracker().method_4410(this, chunk2);
				}
			}
		}

		Entity entity = this.getSpectatingEntity();
		if (entity != this) {
			if (!entity.isAlive()) {
				this.method_10763(this);
			} else {
				this.updatePositionAndAngles(entity.x, entity.y, entity.z, entity.yaw, entity.pitch);
				this.server.getPlayerManager().method_2003(this);
				if (this.isSneaking()) {
					this.method_10763(this);
				}
			}
		}
	}

	public void tickPlayer() {
		try {
			super.tick();

			for (int i = 0; i < this.inventory.getInvSize(); i++) {
				ItemStack itemStack = this.inventory.getInvStack(i);
				if (itemStack != null && itemStack.getItem().isNetworkSynced()) {
					Packet packet = ((NetworkSyncedItem)itemStack.getItem()).createSyncPacket(itemStack, this.world, this);
					if (packet != null) {
						this.networkHandler.sendPacket(packet);
					}
				}
			}

			if (this.getHealth() != this.lastHealth
				|| this.lastHungerLevel != this.hungerManager.getFoodLevel()
				|| this.hungerManager.getSaturationLevel() == 0.0F != this.wasHungry) {
				this.networkHandler.sendPacket(new HealthUpdateS2CPacket(this.getHealth(), this.hungerManager.getFoodLevel(), this.hungerManager.getSaturationLevel()));
				this.lastHealth = this.getHealth();
				this.lastHungerLevel = this.hungerManager.getFoodLevel();
				this.wasHungry = this.hungerManager.getSaturationLevel() == 0.0F;
			}

			if (this.getHealth() + this.getAbsorption() != this.syncedHealth) {
				this.syncedHealth = this.getHealth() + this.getAbsorption();

				for (ScoreboardObjective scoreboardObjective : this.getScoreboard().getObjective(ScoreboardCriterion.HEALTH)) {
					this.getScoreboard().getPlayerScore(this.getTranslationKey(), scoreboardObjective).method_4867(Arrays.asList(this));
				}
			}

			if (this.totalExperience != this.lastXp) {
				this.lastXp = this.totalExperience;
				this.networkHandler.sendPacket(new ExperienceBarUpdateS2CPacket(this.experienceProgress, this.totalExperience, this.experienceLevel));
			}

			if (this.ticksAlive % 20 * 5 == 0 && !this.getStatHandler().hasAchievement(AchievementsAndCriterions.EXPLORE_ALL_BIOMES)) {
				this.updateExploredBiomes();
			}
		} catch (Throwable var4) {
			CrashReport crashReport = CrashReport.create(var4, "Ticking player");
			CrashReportSection crashReportSection = crashReport.addElement("Player being ticked");
			this.populateCrashReport(crashReportSection);
			throw new CrashException(crashReport);
		}
	}

	protected void updateExploredBiomes() {
		Biome biome = this.world.getBiome(new BlockPos(MathHelper.floor(this.x), 0, MathHelper.floor(this.z)));
		String string = biome.name;
		JsonSet jsonSet = this.getStatHandler().getStat(AchievementsAndCriterions.EXPLORE_ALL_BIOMES);
		if (jsonSet == null) {
			jsonSet = this.getStatHandler().setStat(AchievementsAndCriterions.EXPLORE_ALL_BIOMES, new JsonSet());
		}

		jsonSet.add(string);
		if (this.getStatHandler().hasParentAchievement(AchievementsAndCriterions.EXPLORE_ALL_BIOMES) && jsonSet.size() >= Biome.BIOMESET.size()) {
			Set<Biome> set = Sets.newHashSet(Biome.BIOMESET);

			for (String string2 : jsonSet) {
				Iterator<Biome> iterator2 = set.iterator();

				while (iterator2.hasNext()) {
					Biome biome2 = (Biome)iterator2.next();
					if (biome2.name.equals(string2)) {
						iterator2.remove();
					}
				}

				if (set.isEmpty()) {
					break;
				}
			}

			if (set.isEmpty()) {
				this.incrementStat(AchievementsAndCriterions.EXPLORE_ALL_BIOMES);
			}
		}
	}

	@Override
	public void onKilled(DamageSource source) {
		if (this.world.getGameRules().getBoolean("showDeathMessages")) {
			AbstractTeam abstractTeam = this.getScoreboardTeam();
			if (abstractTeam == null || abstractTeam.getDeathMessageVisibilityRule() == AbstractTeam.VisibilityRule.ALWAYS) {
				this.server.getPlayerManager().sendToAll(this.getDamageTracker().getDeathMessage());
			} else if (abstractTeam.getDeathMessageVisibilityRule() == AbstractTeam.VisibilityRule.HIDE_FOR_OTHER_TEAMS) {
				this.server.getPlayerManager().sendMessageToTeam(this, this.getDamageTracker().getDeathMessage());
			} else if (abstractTeam.getDeathMessageVisibilityRule() == AbstractTeam.VisibilityRule.HIDE_FOR_OWN_TEAM) {
				this.server.getPlayerManager().sendMessageToOtherTeams(this, this.getDamageTracker().getDeathMessage());
			}
		}

		if (!this.world.getGameRules().getBoolean("keepInventory")) {
			this.inventory.dropAll();
		}

		for (ScoreboardObjective scoreboardObjective : this.world.getScoreboard().getObjective(ScoreboardCriterion.DEATH_COUNT)) {
			ScoreboardPlayerScore scoreboardPlayerScore = this.getScoreboard().getPlayerScore(this.getTranslationKey(), scoreboardObjective);
			scoreboardPlayerScore.method_4865();
		}

		LivingEntity livingEntity = this.getOpponent();
		if (livingEntity != null) {
			EntityType.SpawnEggData spawnEggData = (EntityType.SpawnEggData)EntityType.SPAWN_EGGS.get(EntityType.getIdByEntity(livingEntity));
			if (spawnEggData != null) {
				this.incrementStat(spawnEggData.killedByEntityStat);
			}

			livingEntity.updateKilledAdvancementCriterion(this, this.field_6777);
		}

		this.incrementStat(Stats.DEATHS);
		this.method_11238(Stats.TIME_SINCE_DEATH);
		this.getDamageTracker().update();
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else {
			boolean bl = this.server.isDedicated() && this.isPvpEnabled() && "fall".equals(source.name);
			if (!bl && this.spawnProtectionTicks > 0 && source != DamageSource.OUT_OF_WORLD) {
				return false;
			} else {
				if (source instanceof EntityDamageSource) {
					Entity entity = source.getAttacker();
					if (entity instanceof PlayerEntity && !this.shouldDamagePlayer((PlayerEntity)entity)) {
						return false;
					}

					if (entity instanceof AbstractArrowEntity) {
						AbstractArrowEntity abstractArrowEntity = (AbstractArrowEntity)entity;
						if (abstractArrowEntity.owner instanceof PlayerEntity && !this.shouldDamagePlayer((PlayerEntity)abstractArrowEntity.owner)) {
							return false;
						}
					}
				}

				return super.damage(source, amount);
			}
		}
	}

	@Override
	public boolean shouldDamagePlayer(PlayerEntity player) {
		return !this.isPvpEnabled() ? false : super.shouldDamagePlayer(player);
	}

	private boolean isPvpEnabled() {
		return this.server.isPvpEnabled();
	}

	@Override
	public void teleportToDimension(int dimensionId) {
		if (this.dimension == 1 && dimensionId == 1) {
			this.incrementStat(AchievementsAndCriterions.THE_END_2);
			this.world.removeEntity(this);
			this.killedEnderdragon = true;
			this.networkHandler.sendPacket(new GameStateChangeS2CPacket(4, 0.0F));
		} else {
			if (this.dimension == 0 && dimensionId == 1) {
				this.incrementStat(AchievementsAndCriterions.THE_END);
				BlockPos blockPos = this.server.getWorld(dimensionId).getForcedSpawnPoint();
				if (blockPos != null) {
					this.networkHandler.requestTeleport((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), 0.0F, 0.0F);
				}

				dimensionId = 1;
			} else {
				this.incrementStat(AchievementsAndCriterions.PORTAL);
			}

			this.server.getPlayerManager().teleportToDimension(this, dimensionId);
			this.lastXp = -1;
			this.lastHealth = -1.0F;
			this.lastHungerLevel = -1;
		}
	}

	@Override
	public boolean isSpectatedBy(ServerPlayerEntity player) {
		if (player.isSpectator()) {
			return this.getSpectatingEntity() == this;
		} else {
			return this.isSpectator() ? false : super.isSpectatedBy(player);
		}
	}

	private void updateBlockEntity(BlockEntity blockEntity) {
		if (blockEntity != null) {
			Packet packet = blockEntity.getPacket();
			if (packet != null) {
				this.networkHandler.sendPacket(packet);
			}
		}
	}

	@Override
	public void sendPickup(Entity entity, int count) {
		super.sendPickup(entity, count);
		this.openScreenHandler.sendContentUpdates();
	}

	@Override
	public PlayerEntity.SleepStatus attemptSleep(BlockPos pos) {
		PlayerEntity.SleepStatus sleepStatus = super.attemptSleep(pos);
		if (sleepStatus == PlayerEntity.SleepStatus.OK) {
			Packet packet = new BedSleepS2CPacket(this, pos);
			this.getServerWorld().getEntityTracker().sendToOtherTrackingEntities(this, packet);
			this.networkHandler.requestTeleport(this.x, this.y, this.z, this.yaw, this.pitch);
			this.networkHandler.sendPacket(packet);
		}

		return sleepStatus;
	}

	@Override
	public void awaken(boolean bl, boolean bl2, boolean setSpawn) {
		if (this.isSleeping()) {
			this.getServerWorld().getEntityTracker().sendToAllTrackingEntities(this, new EntityAnimationS2CPacket(this, 2));
		}

		super.awaken(bl, bl2, setSpawn);
		if (this.networkHandler != null) {
			this.networkHandler.requestTeleport(this.x, this.y, this.z, this.yaw, this.pitch);
		}
	}

	@Override
	public void startRiding(Entity entity) {
		Entity entity2 = this.vehicle;
		super.startRiding(entity);
		if (entity != entity2) {
			this.networkHandler.sendPacket(new EntityAttachS2CPacket(0, this, this.vehicle));
			this.networkHandler.requestTeleport(this.x, this.y, this.z, this.yaw, this.pitch);
		}
	}

	@Override
	protected void fall(double heightDifference, boolean onGround, Block landedBlock, BlockPos landedPosition) {
	}

	public void handleFall(double distance, boolean bl) {
		int i = MathHelper.floor(this.x);
		int j = MathHelper.floor(this.y - 0.2F);
		int k = MathHelper.floor(this.z);
		BlockPos blockPos = new BlockPos(i, j, k);
		Block block = this.world.getBlockState(blockPos).getBlock();
		if (block.getMaterial() == Material.AIR) {
			Block block2 = this.world.getBlockState(blockPos.down()).getBlock();
			if (block2 instanceof FenceBlock || block2 instanceof WallBlock || block2 instanceof FenceGateBlock) {
				blockPos = blockPos.down();
				block = this.world.getBlockState(blockPos).getBlock();
			}
		}

		super.fall(distance, bl, block, blockPos);
	}

	@Override
	public void openEditSignScreen(SignBlockEntity sign) {
		sign.setEditor(this);
		this.networkHandler.sendPacket(new SignEditorOpenS2CPacket(sign.getPos()));
	}

	private void incrementSyncId() {
		this.screenHandlerSyncId = this.screenHandlerSyncId % 100 + 1;
	}

	@Override
	public void openHandledScreen(NamedScreenHandlerFactory screenHandlerFactory) {
		this.incrementSyncId();
		this.networkHandler.sendPacket(new OpenScreenS2CPacket(this.screenHandlerSyncId, screenHandlerFactory.getId(), screenHandlerFactory.getName()));
		this.openScreenHandler = screenHandlerFactory.createScreenHandler(this.inventory, this);
		this.openScreenHandler.syncId = this.screenHandlerSyncId;
		this.openScreenHandler.addListener(this);
	}

	@Override
	public void openInventory(Inventory inventory) {
		if (this.openScreenHandler != this.playerScreenHandler) {
			this.closeHandledScreen();
		}

		if (inventory instanceof LockableScreenHandlerFactory) {
			LockableScreenHandlerFactory lockableScreenHandlerFactory = (LockableScreenHandlerFactory)inventory;
			if (lockableScreenHandlerFactory.hasLock() && !this.isScreenLocked(lockableScreenHandlerFactory.getLock()) && !this.isSpectator()) {
				this.networkHandler.sendPacket(new ChatMessageS2CPacket(new TranslatableText("container.isLocked", inventory.getName()), (byte)2));
				this.networkHandler.sendPacket(new PlaySoundIdS2CPacket("random.door_close", this.x, this.y, this.z, 1.0F, 1.0F));
				return;
			}
		}

		this.incrementSyncId();
		if (inventory instanceof NamedScreenHandlerFactory) {
			this.networkHandler
				.sendPacket(new OpenScreenS2CPacket(this.screenHandlerSyncId, ((NamedScreenHandlerFactory)inventory).getId(), inventory.getName(), inventory.getInvSize()));
			this.openScreenHandler = ((NamedScreenHandlerFactory)inventory).createScreenHandler(this.inventory, this);
		} else {
			this.networkHandler.sendPacket(new OpenScreenS2CPacket(this.screenHandlerSyncId, "minecraft:container", inventory.getName(), inventory.getInvSize()));
			this.openScreenHandler = new ChestScreenHandler(this.inventory, inventory, this);
		}

		this.openScreenHandler.syncId = this.screenHandlerSyncId;
		this.openScreenHandler.addListener(this);
	}

	@Override
	public void openTradingScreen(Trader trader) {
		this.incrementSyncId();
		this.openScreenHandler = new VillagerScreenHandler(this.inventory, trader, this.world);
		this.openScreenHandler.syncId = this.screenHandlerSyncId;
		this.openScreenHandler.addListener(this);
		Inventory inventory = ((VillagerScreenHandler)this.openScreenHandler).getTraderInventory();
		Text text = trader.getName();
		this.networkHandler.sendPacket(new OpenScreenS2CPacket(this.screenHandlerSyncId, "minecraft:villager", text, inventory.getInvSize()));
		TraderOfferList traderOfferList = trader.getOffers(this);
		if (traderOfferList != null) {
			PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
			packetByteBuf.writeInt(this.screenHandlerSyncId);
			traderOfferList.toPacket(packetByteBuf);
			this.networkHandler.sendPacket(new CustomPayloadS2CPacket("MC|TrList", packetByteBuf));
		}
	}

	@Override
	public void openHorseInventory(HorseBaseEntity horse, Inventory inventory) {
		if (this.openScreenHandler != this.playerScreenHandler) {
			this.closeHandledScreen();
		}

		this.incrementSyncId();
		this.networkHandler
			.sendPacket(new OpenScreenS2CPacket(this.screenHandlerSyncId, "EntityHorse", inventory.getName(), inventory.getInvSize(), horse.getEntityId()));
		this.openScreenHandler = new HorseScreenHandler(this.inventory, inventory, horse, this);
		this.openScreenHandler.syncId = this.screenHandlerSyncId;
		this.openScreenHandler.addListener(this);
	}

	@Override
	public void openBookEditScreen(ItemStack stack) {
		Item item = stack.getItem();
		if (item == Items.WRITTEN_BOOK) {
			this.networkHandler.sendPacket(new CustomPayloadS2CPacket("MC|BOpen", new PacketByteBuf(Unpooled.buffer())));
		}
	}

	@Override
	public void onScreenHandlerSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
		if (!(handler.getSlot(slotId) instanceof CraftingResultSlot)) {
			if (!this.skipPacketSlotUpdates) {
				this.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(handler.syncId, slotId, stack));
			}
		}
	}

	public void refreshScreenHandler(ScreenHandler handler) {
		this.updateScreenHandler(handler, handler.getStacks());
	}

	@Override
	public void updateScreenHandler(ScreenHandler handler, List<ItemStack> list) {
		this.networkHandler.sendPacket(new InventoryS2CPacket(handler.syncId, list));
		this.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-1, -1, this.inventory.getCursorStack()));
	}

	@Override
	public void onScreenHandlerPropertyUpdate(ScreenHandler handler, int propertyId, int value) {
		this.networkHandler.sendPacket(new ScreenHandlerPropertyUpdateS2CPacket(handler.syncId, propertyId, value));
	}

	@Override
	public void onScreenHandlerInventoryUpdate(ScreenHandler handler, Inventory inventory) {
		for (int i = 0; i < inventory.getProperties(); i++) {
			this.networkHandler.sendPacket(new ScreenHandlerPropertyUpdateS2CPacket(handler.syncId, i, inventory.getProperty(i)));
		}
	}

	@Override
	public void closeHandledScreen() {
		this.networkHandler.sendPacket(new CloseScreenS2CPacket(this.openScreenHandler.syncId));
		this.closeOpenedScreenHandler();
	}

	public void method_2158() {
		if (!this.skipPacketSlotUpdates) {
			this.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-1, -1, this.inventory.getCursorStack()));
		}
	}

	public void closeOpenedScreenHandler() {
		this.openScreenHandler.close(this);
		this.openScreenHandler = this.playerScreenHandler;
	}

	public void method_6066(float f, float g, boolean bl, boolean bl2) {
		if (this.vehicle != null) {
			if (f >= -1.0F && f <= 1.0F) {
				this.sidewaysSpeed = f;
			}

			if (g >= -1.0F && g <= 1.0F) {
				this.forwardSpeed = g;
			}

			this.jumping = bl;
			this.setSneaking(bl2);
		}
	}

	@Override
	public void incrementStat(Stat stat, int amount) {
		if (stat != null) {
			this.statHandler.addStatLevel(this, stat, amount);

			for (ScoreboardObjective scoreboardObjective : this.getScoreboard().getObjective(stat.getCriterion())) {
				this.getScoreboard().getPlayerScore(this.getTranslationKey(), scoreboardObjective).incrementScore(amount);
			}

			if (this.statHandler.method_8278()) {
				this.statHandler.method_8273(this);
			}
		}
	}

	@Override
	public void method_11238(Stat stat) {
		if (stat != null) {
			this.statHandler.setStatLevel(this, stat, 0);

			for (ScoreboardObjective scoreboardObjective : this.getScoreboard().getObjective(stat.getCriterion())) {
				this.getScoreboard().getPlayerScore(this.getTranslationKey(), scoreboardObjective).setScore(0);
			}

			if (this.statHandler.method_8278()) {
				this.statHandler.method_8273(this);
			}
		}
	}

	public void method_2160() {
		if (this.rider != null) {
			this.rider.startRiding(this);
		}

		if (this.inBed) {
			this.awaken(true, false, false);
		}
	}

	public void markHealthDirty() {
		this.lastHealth = -1.0E8F;
	}

	@Override
	public void addMessage(Text text) {
		this.networkHandler.sendPacket(new ChatMessageS2CPacket(text));
	}

	@Override
	protected void useItem() {
		this.networkHandler.sendPacket(new EntityStatusS2CPacket(this, (byte)9));
		super.useItem();
	}

	@Override
	public void setUseItem(ItemStack item, int i) {
		super.setUseItem(item, i);
		if (item != null && item.getItem() != null && item.getItem().getUseAction(item) == UseAction.EAT) {
			this.getServerWorld().getEntityTracker().sendToAllTrackingEntities(this, new EntityAnimationS2CPacket(this, 3));
		}
	}

	@Override
	public void copyFrom(PlayerEntity player, boolean bl) {
		super.copyFrom(player, bl);
		this.lastXp = -1;
		this.lastHealth = -1.0F;
		this.lastHungerLevel = -1;
		this.removedEntities.addAll(((ServerPlayerEntity)player).removedEntities);
	}

	@Override
	protected void method_2582(StatusEffectInstance instance) {
		super.method_2582(instance);
		this.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(this.getEntityId(), instance));
	}

	@Override
	protected void method_6108(StatusEffectInstance instance, boolean bl) {
		super.method_6108(instance, bl);
		this.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(this.getEntityId(), instance));
	}

	@Override
	protected void method_2649(StatusEffectInstance instance) {
		super.method_2649(instance);
		this.networkHandler.sendPacket(new RemoveEntityStatusEffectS2CPacket(this.getEntityId(), instance));
	}

	@Override
	public void refreshPositionAfterTeleport(double x, double y, double z) {
		this.networkHandler.requestTeleport(x, y, z, this.yaw, this.pitch);
	}

	@Override
	public void addCritParticles(Entity target) {
		this.getServerWorld().getEntityTracker().sendToAllTrackingEntities(this, new EntityAnimationS2CPacket(target, 4));
	}

	@Override
	public void addEnchantedHitParticles(Entity target) {
		this.getServerWorld().getEntityTracker().sendToAllTrackingEntities(this, new EntityAnimationS2CPacket(target, 5));
	}

	@Override
	public void sendAbilitiesUpdate() {
		if (this.networkHandler != null) {
			this.networkHandler.sendPacket(new PlayerAbilitiesS2CPacket(this.abilities));
			this.updatePotionVisibility();
		}
	}

	public ServerWorld getServerWorld() {
		return (ServerWorld)this.world;
	}

	@Override
	public void setGameMode(LevelInfo.GameMode gameMode) {
		this.interactionManager.setGameMode(gameMode);
		this.networkHandler.sendPacket(new GameStateChangeS2CPacket(3, (float)gameMode.getId()));
		if (gameMode == LevelInfo.GameMode.SPECTATOR) {
			this.startRiding(null);
		} else {
			this.method_10763(this);
		}

		this.sendAbilitiesUpdate();
		this.markEffectsDirty();
	}

	@Override
	public boolean isSpectator() {
		return this.interactionManager.getGameMode() == LevelInfo.GameMode.SPECTATOR;
	}

	@Override
	public void sendMessage(Text text) {
		this.networkHandler.sendPacket(new ChatMessageS2CPacket(text));
	}

	@Override
	public boolean canUseCommand(int permissionLevel, String commandLiteral) {
		if ("seed".equals(commandLiteral) && !this.server.isDedicated()) {
			return true;
		} else if (!"tell".equals(commandLiteral) && !"help".equals(commandLiteral) && !"me".equals(commandLiteral) && !"trigger".equals(commandLiteral)) {
			if (this.server.getPlayerManager().isOperator(this.getGameProfile())) {
				OperatorEntry operatorEntry = this.server.getPlayerManager().getOpList().get(this.getGameProfile());
				return operatorEntry != null ? operatorEntry.getPermissionLevel() >= permissionLevel : this.server.getOpPermissionLevel() >= permissionLevel;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	public String getIp() {
		String string = this.networkHandler.connection.getAddress().toString();
		string = string.substring(string.indexOf("/") + 1);
		return string.substring(0, string.indexOf(":"));
	}

	public void method_2150(ClientSettingsC2SPacket clientSettingsC2SPacket) {
		this.language = clientSettingsC2SPacket.getLanguage();
		this.visibilityType = clientSettingsC2SPacket.getChatVisibility();
		this.chatColors = clientSettingsC2SPacket.hasChatColors();
		this.getDataTracker().setProperty(10, (byte)clientSettingsC2SPacket.getPlayerModelBitMask());
	}

	public PlayerEntity.ChatVisibilityType method_8137() {
		return this.visibilityType;
	}

	public void sendResourcePackUrl(String url, String hash) {
		this.networkHandler.sendPacket(new ResourcePackSendS2CPacket(url, hash));
	}

	@Override
	public BlockPos getBlockPos() {
		return new BlockPos(this.x, this.y + 0.5, this.z);
	}

	public void updateLastActionTime() {
		this.lastActionTime = MinecraftServer.getTimeMillis();
	}

	public ServerStatHandler getStatHandler() {
		return this.statHandler;
	}

	public void stopTracking(Entity entity) {
		if (entity instanceof PlayerEntity) {
			this.networkHandler.sendPacket(new EntitiesDestroyS2CPacket(entity.getEntityId()));
		} else {
			this.removedEntities.add(entity.getEntityId());
		}
	}

	@Override
	protected void updatePotionVisibility() {
		if (this.isSpectator()) {
			this.method_10981();
			this.setInvisible(true);
		} else {
			super.updatePotionVisibility();
		}

		this.getServerWorld().getEntityTracker().method_10747(this);
	}

	public Entity getSpectatingEntity() {
		return (Entity)(this.spectatingEntity == null ? this : this.spectatingEntity);
	}

	public void method_10763(Entity entity) {
		Entity entity2 = this.getSpectatingEntity();
		this.spectatingEntity = (Entity)(entity == null ? this : entity);
		if (entity2 != this.spectatingEntity) {
			this.networkHandler.sendPacket(new SetCameraEntityS2CPacket(this.spectatingEntity));
			this.refreshPositionAfterTeleport(this.spectatingEntity.x, this.spectatingEntity.y, this.spectatingEntity.z);
		}
	}

	@Override
	public void attack(Entity entity) {
		if (this.interactionManager.getGameMode() == LevelInfo.GameMode.SPECTATOR) {
			this.method_10763(entity);
		} else {
			super.attack(entity);
		}
	}

	public long getLastActionTime() {
		return this.lastActionTime;
	}

	public Text getDisplayName() {
		return null;
	}
}
