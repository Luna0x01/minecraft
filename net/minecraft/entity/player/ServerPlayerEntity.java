package net.minecraft.entity.player;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.class_2686;
import net.minecraft.class_2690;
import net.minecraft.class_2964;
import net.minecraft.advancement.Achievement;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.LockableScreenHandlerFactory;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.screen.options.HandOption;
import net.minecraft.client.sound.SoundCategory;
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
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.CombatEventS2CPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
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
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
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
import net.minecraft.sound.Sounds;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.JsonSet;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.TraderOfferList;
import net.minecraft.world.biome.Biome;
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
	private final List<Integer> removedEntities = Lists.newLinkedList();
	private final ServerStatHandler statHandler;
	private float syncedHealth = Float.MIN_VALUE;
	private int syncedFoodLevel = Integer.MIN_VALUE;
	private int field_13853 = Integer.MIN_VALUE;
	private int field_13854 = Integer.MIN_VALUE;
	private int field_13855 = Integer.MIN_VALUE;
	private int field_13856 = Integer.MIN_VALUE;
	private float lastHealth = -1.0E8F;
	private int lastHungerLevel = -99999999;
	private boolean wasHungry = true;
	private int lastXp = -99999999;
	private int spawnProtectionTicks = 60;
	private PlayerEntity.ChatVisibilityType visibilityType;
	private boolean chatColors = true;
	private long lastActionTime = System.currentTimeMillis();
	private Entity spectatingEntity = null;
	private boolean field_13857;
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
			int i = Math.max(0, minecraftServer.method_12834(serverWorld));
			int j = MathHelper.floor(serverWorld.getWorldBorder().getDistanceInsideBorder((double)blockPos.getX(), (double)blockPos.getZ()));
			if (j < i) {
				i = j;
			}

			if (j <= 1) {
				i = 1;
			}

			blockPos = serverWorld.getTopPosition(blockPos.add(this.random.nextInt(i * 2 + 1) - i, 0, this.random.nextInt(i * 2 + 1) - i));
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
			if (this.getMinecraftServer().shouldForceGameMode()) {
				this.interactionManager.setGameMode(this.getMinecraftServer().getDefaultGameMode());
			} else {
				this.interactionManager.setGameMode(LevelInfo.GameMode.byId(nbt.getInt("playerGameType")));
			}
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("playerGameType", this.interactionManager.getGameMode().getId());
		Entity entity = this.getRootVehicle();
		if (this.getVehicle() != null && entity != this & entity.getPassengersDeep(ServerPlayerEntity.class).size() == 1) {
			NbtCompound nbtCompound = new NbtCompound();
			NbtCompound nbtCompound2 = new NbtCompound();
			entity.saveToNbt(nbtCompound2);
			nbtCompound.putUuid("Attach", this.getVehicle().getUuid());
			nbtCompound.put("Entity", nbtCompound2);
			nbt.put("RootVehicle", nbtCompound);
		}
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
	protected class_2686 method_13272() {
		return new class_2690(this);
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
					Packet<?> packet = ((NetworkSyncedItem)itemStack.getItem()).createSyncPacket(itemStack, this.world, this);
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
				this.method_12788(ScoreboardCriterion.HEALTH, MathHelper.ceil(this.syncedHealth));
			}

			if (this.hungerManager.getFoodLevel() != this.syncedFoodLevel) {
				this.syncedFoodLevel = this.hungerManager.getFoodLevel();
				this.method_12788(ScoreboardCriterion.FOOD, MathHelper.ceil((float)this.syncedFoodLevel));
			}

			if (this.getAir() != this.field_13853) {
				this.field_13853 = this.getAir();
				this.method_12788(ScoreboardCriterion.AIR, MathHelper.ceil((float)this.field_13853));
			}

			if (this.getArmorProtectionValue() != this.field_13854) {
				this.field_13854 = this.getArmorProtectionValue();
				this.method_12788(ScoreboardCriterion.ARMOR, MathHelper.ceil((float)this.field_13854));
			}

			if (this.totalExperience != this.field_13856) {
				this.field_13856 = this.totalExperience;
				this.method_12788(ScoreboardCriterion.XP, MathHelper.ceil((float)this.field_13856));
			}

			if (this.experienceLevel != this.field_13855) {
				this.field_13855 = this.experienceLevel;
				this.method_12788(ScoreboardCriterion.LEVEL, MathHelper.ceil((float)this.field_13855));
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

	private void method_12788(ScoreboardCriterion scoreboardCriterion, int i) {
		for (ScoreboardObjective scoreboardObjective : this.getScoreboard().getObjective(scoreboardCriterion)) {
			ScoreboardPlayerScore scoreboardPlayerScore = this.getScoreboard().getPlayerScore(this.getTranslationKey(), scoreboardObjective);
			scoreboardPlayerScore.setScore(i);
		}
	}

	protected void updateExploredBiomes() {
		Biome biome = this.world.getBiome(new BlockPos(MathHelper.floor(this.x), 0, MathHelper.floor(this.z)));
		String string = biome.getName();
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
					if (biome2.getName().equals(string2)) {
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
		boolean bl = this.world.getGameRules().getBoolean("showDeathMessages");
		this.networkHandler.sendPacket(new CombatEventS2CPacket(this.getDamageTracker(), CombatEventS2CPacket.Type.ENTITY_DIED, bl));
		if (bl) {
			AbstractTeam abstractTeam = this.getScoreboardTeam();
			if (abstractTeam == null || abstractTeam.getDeathMessageVisibilityRule() == AbstractTeam.VisibilityRule.ALWAYS) {
				this.server.getPlayerManager().sendToAll(this.getDamageTracker().getDeathMessage());
			} else if (abstractTeam.getDeathMessageVisibilityRule() == AbstractTeam.VisibilityRule.HIDE_FOR_OTHER_TEAMS) {
				this.server.getPlayerManager().sendMessageToTeam(this, this.getDamageTracker().getDeathMessage());
			} else if (abstractTeam.getDeathMessageVisibilityRule() == AbstractTeam.VisibilityRule.HIDE_FOR_OWN_TEAM) {
				this.server.getPlayerManager().sendMessageToOtherTeams(this, this.getDamageTracker().getDeathMessage());
			}
		}

		if (!this.world.getGameRules().getBoolean("keepInventory") && !this.isSpectator()) {
			this.inventory.dropAll();
		}

		for (ScoreboardObjective scoreboardObjective : this.world.getScoreboard().getObjective(ScoreboardCriterion.DEATH_COUNT)) {
			ScoreboardPlayerScore scoreboardPlayerScore = this.getScoreboard().getPlayerScore(this.getTranslationKey(), scoreboardObjective);
			scoreboardPlayerScore.method_4865();
		}

		LivingEntity livingEntity = this.getOpponent();
		if (livingEntity != null) {
			EntityType.SpawnEggData spawnEggData = (EntityType.SpawnEggData)EntityType.SPAWN_EGGS.get(EntityType.getEntityName(livingEntity));
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

	@Nullable
	@Override
	public Entity changeDimension(int newDimension) {
		this.field_13857 = true;
		if (this.dimension == 1 && newDimension == 1) {
			this.world.removeEntity(this);
			if (!this.killedEnderdragon) {
				this.killedEnderdragon = true;
				if (this.method_13262(AchievementsAndCriterions.THE_END_2)) {
					this.networkHandler.sendPacket(new GameStateChangeS2CPacket(4, 0.0F));
				} else {
					this.incrementStat(AchievementsAndCriterions.THE_END_2);
					this.networkHandler.sendPacket(new GameStateChangeS2CPacket(4, 1.0F));
				}
			}

			return this;
		} else {
			if (this.dimension == 0 && newDimension == 1) {
				this.incrementStat(AchievementsAndCriterions.THE_END);
				newDimension = 1;
			} else {
				this.incrementStat(AchievementsAndCriterions.PORTAL);
			}

			this.server.getPlayerManager().teleportToDimension(this, newDimension);
			this.networkHandler.sendPacket(new WorldEventS2CPacket(1032, BlockPos.ORIGIN, 0, false));
			this.lastXp = -1;
			this.lastHealth = -1.0F;
			this.lastHungerLevel = -1;
			return this;
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
			BlockEntityUpdateS2CPacket blockEntityUpdateS2CPacket = blockEntity.getUpdatePacket();
			if (blockEntityUpdateS2CPacket != null) {
				this.networkHandler.sendPacket(blockEntityUpdateS2CPacket);
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
			this.incrementStat(Stats.SLEEP_IN_BED);
			Packet<?> packet = new BedSleepS2CPacket(this, pos);
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
	public boolean startRiding(Entity entity, boolean force) {
		Entity entity2 = this.getVehicle();
		if (!super.startRiding(entity, force)) {
			return false;
		} else {
			Entity entity3 = this.getVehicle();
			if (entity3 != entity2 && this.networkHandler != null) {
				this.networkHandler.requestTeleport(this.x, this.y, this.z, this.yaw, this.pitch);
			}

			return true;
		}
	}

	@Override
	public void stopRiding() {
		Entity entity = this.getVehicle();
		super.stopRiding();
		Entity entity2 = this.getVehicle();
		if (entity2 != entity && this.networkHandler != null) {
			this.networkHandler.requestTeleport(this.x, this.y, this.z, this.yaw, this.pitch);
		}
	}

	@Override
	public boolean isInvulnerableTo(DamageSource damageSource) {
		return super.isInvulnerableTo(damageSource) || this.method_12784();
	}

	@Override
	protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPos) {
	}

	@Override
	protected void method_13046(BlockPos blockPos) {
		if (!this.isSpectator()) {
			super.method_13046(blockPos);
		}
	}

	public void handleFall(double distance, boolean bl) {
		int i = MathHelper.floor(this.x);
		int j = MathHelper.floor(this.y - 0.2F);
		int k = MathHelper.floor(this.z);
		BlockPos blockPos = new BlockPos(i, j, k);
		BlockState blockState = this.world.getBlockState(blockPos);
		if (blockState.getMaterial() == Material.AIR) {
			BlockPos blockPos2 = blockPos.down();
			BlockState blockState2 = this.world.getBlockState(blockPos2);
			Block block = blockState2.getBlock();
			if (block instanceof FenceBlock || block instanceof WallBlock || block instanceof FenceGateBlock) {
				blockPos = blockPos2;
				blockState = blockState2;
			}
		}

		super.fall(distance, bl, blockState, blockPos);
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
		if (screenHandlerFactory instanceof class_2964 && ((class_2964)screenHandlerFactory).getLootTableId() != null && this.isSpectator()) {
			this.sendMessage(new TranslatableText("container.spectatorCantOpen").setStyle(new Style().setFormatting(Formatting.RED)));
		} else {
			this.incrementSyncId();
			this.networkHandler.sendPacket(new OpenScreenS2CPacket(this.screenHandlerSyncId, screenHandlerFactory.getId(), screenHandlerFactory.getName()));
			this.openScreenHandler = screenHandlerFactory.createScreenHandler(this.inventory, this);
			this.openScreenHandler.syncId = this.screenHandlerSyncId;
			this.openScreenHandler.addListener(this);
		}
	}

	@Override
	public void openInventory(Inventory inventory) {
		if (inventory instanceof class_2964 && ((class_2964)inventory).getLootTableId() != null && this.isSpectator()) {
			this.sendMessage(new TranslatableText("container.spectatorCantOpen").setStyle(new Style().setFormatting(Formatting.RED)));
		} else {
			if (this.openScreenHandler != this.playerScreenHandler) {
				this.closeHandledScreen();
			}

			if (inventory instanceof LockableScreenHandlerFactory) {
				LockableScreenHandlerFactory lockableScreenHandlerFactory = (LockableScreenHandlerFactory)inventory;
				if (lockableScreenHandlerFactory.hasLock() && !this.isScreenLocked(lockableScreenHandlerFactory.getLock()) && !this.isSpectator()) {
					this.networkHandler.sendPacket(new ChatMessageS2CPacket(new TranslatableText("container.isLocked", inventory.getName()), (byte)2));
					this.networkHandler.sendPacket(new PlaySoundIdS2CPacket(Sounds.BLOCK_CHEST_LOCKED, SoundCategory.BLOCKS, this.x, this.y, this.z, 1.0F, 1.0F));
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
	public void method_3201(ItemStack stack, Hand hand) {
		Item item = stack.getItem();
		if (item == Items.WRITTEN_BOOK) {
			PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
			packetByteBuf.writeEnumConstant(hand);
			this.networkHandler.sendPacket(new CustomPayloadS2CPacket("MC|BOpen", packetByteBuf));
		}
	}

	@Override
	public void method_13260(CommandBlockBlockEntity commandBlockBlockEntity) {
		if (this.canUseCommand(2, "")) {
			commandBlockBlockEntity.method_11652(true);
			this.updateBlockEntity(commandBlockBlockEntity);
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
		if (this.hasMount()) {
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
	public boolean method_13262(Achievement achievement) {
		return this.statHandler.hasAchievement(achievement);
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
		this.removeAllPassengers();
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
	protected void method_3217() {
		if (this.field_14546 != null && this.method_13061()) {
			this.networkHandler.sendPacket(new EntityStatusS2CPacket(this, (byte)9));
			super.method_3217();
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
		this.networkHandler.sendPacket(new RemoveEntityStatusEffectS2CPacket(this.getEntityId(), instance.getStatusEffect()));
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
			this.stopRiding();
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
	public boolean isCreative() {
		return this.interactionManager.getGameMode() == LevelInfo.GameMode.CREATIVE;
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

	public void method_12789(ClientSettingsC2SPacket clientSettingsC2SPacket) {
		this.language = clientSettingsC2SPacket.getLanguage();
		this.visibilityType = clientSettingsC2SPacket.getChatVisibility();
		this.chatColors = clientSettingsC2SPacket.hasChatColors();
		this.getDataTracker().set(field_14796, (byte)clientSettingsC2SPacket.getPlayerModelBitMask());
		this.getDataTracker().set(field_14797, (byte)(clientSettingsC2SPacket.method_12685() == HandOption.LEFT ? 0 : 1));
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

	public void method_12790(Entity entity) {
		this.removedEntities.remove(entity.getEntityId());
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
	protected void tickNetherPortalCooldown() {
		if (this.netherPortalCooldown > 0 && !this.field_13857) {
			this.netherPortalCooldown--;
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

	@Nullable
	public Text getDisplayName() {
		return null;
	}

	@Override
	public void swingHand(Hand hand) {
		super.swingHand(hand);
		this.method_13269();
	}

	public boolean method_12784() {
		return this.field_13857;
	}

	public void method_12785() {
		this.field_13857 = false;
	}

	public void method_12786() {
		this.setFlag(7, true);
	}

	public void method_12787() {
		this.setFlag(7, true);
		this.setFlag(7, false);
	}
}
