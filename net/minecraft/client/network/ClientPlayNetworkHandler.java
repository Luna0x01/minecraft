package net.minecraft.client.network;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.advancement.Achievement;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.block.entity.FlowerPotBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.entity.StructureBlockEntity;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.class_2844;
import net.minecraft.client.gui.StatsListener;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.DemoScreen;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.gui.screen.ingame.CommandBlockScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.VillagerTradingScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.IdentifiableBooleanConsumer;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.ServerList;
import net.minecraft.client.particle.ItemPickupParticle;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.client.realms.RealmsScreenProxy;
import net.minecraft.client.render.debug.class_2891;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.GuardianAttackSoundInstance;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.world.BlockCommunicationNameable;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EndCrystalEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.FireworkRocketEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningBoltEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ShulkerBulletEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.ai.pathing.PathMinHeap;
import net.minecraft.entity.attribute.AbstractEntityAttributeContainer;
import net.minecraft.entity.attribute.AttributeModifier;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.Merchant;
import net.minecraft.entity.data.Trader;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.OtherClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.entity.thrown.EggEntity;
import net.minecraft.entity.thrown.EnderPearlEntity;
import net.minecraft.entity.thrown.ExperienceBottleEntity;
import net.minecraft.entity.thrown.EyeOfEnderEntity;
import net.minecraft.entity.thrown.PotionEntity;
import net.minecraft.entity.thrown.SnowballEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.inventory.AnimalInventory;
import net.minecraft.inventory.ClientNetworkSyncedInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.ConfirmGuiActionC2SPacket;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.BedSleepS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockActionS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkLoadDistanceS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkRenderDistanceCenterS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkUnloadS2CPacket;
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.CombatEventS2CPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.minecraft.network.packet.s2c.play.ConfirmGuiActionS2CPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.play.DifficultyS2CPacket;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttachS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttributesS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnGlobalS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExperienceBarUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExperienceOrbSpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.HeldItemChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.KeepAliveS2CPacket;
import net.minecraft.network.packet.s2c.play.MapUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.MobSpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.PaintingSpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundIdS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundNameS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListHeaderS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerSpawnPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerSpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.RemoveEntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.ResourcePackSendS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardDisplayS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardObjectiveUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardPlayerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerPropertyUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.SetCameraEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.SetPassengersS2CPacket;
import net.minecraft.network.packet.s2c.play.SignEditorOpenS2CPacket;
import net.minecraft.network.packet.s2c.play.StatsUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.network.packet.s2c.play.VehicleMoveS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.Sounds;
import net.minecraft.stat.Stat;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.TraderOfferList;
import net.minecraft.world.GameMode;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.OverworldDimension;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.level.LevelInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientPlayNetworkHandler implements ClientPlayPacketListener {
	private static final Logger LOGGER = LogManager.getLogger();
	private final ClientConnection connection;
	private final GameProfile profile;
	private final Screen loginScreen;
	private MinecraftClient client;
	private ClientWorld world;
	private boolean positionLookSetup;
	private final Map<UUID, PlayerListEntry> playerListEntries = Maps.newHashMap();
	public int maxPlayers = 20;
	private boolean field_7920;
	private final Random random = new Random();

	public ClientPlayNetworkHandler(MinecraftClient minecraftClient, Screen screen, ClientConnection clientConnection, GameProfile gameProfile) {
		this.client = minecraftClient;
		this.loginScreen = screen;
		this.connection = clientConnection;
		this.profile = gameProfile;
	}

	public void clearWorld() {
		this.world = null;
	}

	@Override
	public void onGameJoin(GameJoinS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.interactionManager = new ClientPlayerInteractionManager(this.client, this);
		this.world = new ClientWorld(
			this,
			new LevelInfo(0L, packet.getGameMode(), false, packet.isHardcore(), packet.getGeneratorType()),
			packet.getChunkLoadDistance(),
			packet.getDifficulty(),
			this.client.profiler
		);
		this.client.options.difficulty = packet.getDifficulty();
		this.client.connect(this.world);
		this.client.player.dimension = packet.getChunkLoadDistance();
		this.client.setScreen(new DownloadingTerrainScreen(this));
		this.client.player.setEntityId(packet.getEntityId());
		this.maxPlayers = packet.getMaxPlayers();
		this.client.player.setReducedDebugInfo(packet.hasReducedDebugInfo());
		this.client.interactionManager.setGameMode(packet.getGameMode());
		this.client.options.onPlayerModelPartChange();
		this.connection.send(new CustomPayloadC2SPacket("MC|Brand", new PacketByteBuf(Unpooled.buffer()).writeString(ClientBrandRetriever.getClientModName())));
	}

	@Override
	public void onEntitySpawn(EntitySpawnS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		double d = packet.getX();
		double e = packet.getY();
		double f = packet.getZ();
		Entity entity = null;
		if (packet.getEntityData() == 10) {
			entity = AbstractMinecartEntity.createMinecart(this.world, d, e, f, AbstractMinecartEntity.Type.getById(packet.getDataId()));
		} else if (packet.getEntityData() == 90) {
			Entity entity2 = this.world.getEntityById(packet.getDataId());
			if (entity2 instanceof PlayerEntity) {
				entity = new FishingBobberEntity(this.world, d, e, f, (PlayerEntity)entity2);
			}

			packet.setDataId(0);
		} else if (packet.getEntityData() == 60) {
			entity = new ArrowEntity(this.world, d, e, f);
		} else if (packet.getEntityData() == 91) {
			entity = new SpectralArrowEntity(this.world, d, e, f);
		} else if (packet.getEntityData() == 61) {
			entity = new SnowballEntity(this.world, d, e, f);
		} else if (packet.getEntityData() == 71) {
			entity = new ItemFrameEntity(this.world, new BlockPos(d, e, f), Direction.fromHorizontal(packet.getDataId()));
			packet.setDataId(0);
		} else if (packet.getEntityData() == 77) {
			entity = new LeashKnotEntity(this.world, new BlockPos(MathHelper.floor(d), MathHelper.floor(e), MathHelper.floor(f)));
			packet.setDataId(0);
		} else if (packet.getEntityData() == 65) {
			entity = new EnderPearlEntity(this.world, d, e, f);
		} else if (packet.getEntityData() == 72) {
			entity = new EyeOfEnderEntity(this.world, d, e, f);
		} else if (packet.getEntityData() == 76) {
			entity = new FireworkRocketEntity(this.world, d, e, f, null);
		} else if (packet.getEntityData() == 63) {
			entity = new FireballEntity(
				this.world, d, e, f, (double)packet.getVelocityX() / 8000.0, (double)packet.getVelocityY() / 8000.0, (double)packet.getVelocityZ() / 8000.0
			);
			packet.setDataId(0);
		} else if (packet.getEntityData() == 93) {
			entity = new DragonFireballEntity(
				this.world, d, e, f, (double)packet.getVelocityX() / 8000.0, (double)packet.getVelocityY() / 8000.0, (double)packet.getVelocityZ() / 8000.0
			);
			packet.setDataId(0);
		} else if (packet.getEntityData() == 64) {
			entity = new SmallFireballEntity(
				this.world, d, e, f, (double)packet.getVelocityX() / 8000.0, (double)packet.getVelocityY() / 8000.0, (double)packet.getVelocityZ() / 8000.0
			);
			packet.setDataId(0);
		} else if (packet.getEntityData() == 66) {
			entity = new WitherSkullEntity(
				this.world, d, e, f, (double)packet.getVelocityX() / 8000.0, (double)packet.getVelocityY() / 8000.0, (double)packet.getVelocityZ() / 8000.0
			);
			packet.setDataId(0);
		} else if (packet.getEntityData() == 67) {
			entity = new ShulkerBulletEntity(
				this.world, d, e, f, (double)packet.getVelocityX() / 8000.0, (double)packet.getVelocityY() / 8000.0, (double)packet.getVelocityZ() / 8000.0
			);
			packet.setDataId(0);
		} else if (packet.getEntityData() == 62) {
			entity = new EggEntity(this.world, d, e, f);
		} else if (packet.getEntityData() == 73) {
			entity = new PotionEntity(this.world, d, e, f, null);
			packet.setDataId(0);
		} else if (packet.getEntityData() == 75) {
			entity = new ExperienceBottleEntity(this.world, d, e, f);
			packet.setDataId(0);
		} else if (packet.getEntityData() == 1) {
			entity = new BoatEntity(this.world, d, e, f);
		} else if (packet.getEntityData() == 50) {
			entity = new TntEntity(this.world, d, e, f, null);
		} else if (packet.getEntityData() == 78) {
			entity = new ArmorStandEntity(this.world, d, e, f);
		} else if (packet.getEntityData() == 51) {
			entity = new EndCrystalEntity(this.world, d, e, f);
		} else if (packet.getEntityData() == 2) {
			entity = new ItemEntity(this.world, d, e, f);
		} else if (packet.getEntityData() == 70) {
			entity = new FallingBlockEntity(this.world, d, e, f, Block.getStateFromRawId(packet.getDataId() & 65535));
			packet.setDataId(0);
		} else if (packet.getEntityData() == 3) {
			entity = new AreaEffectCloudEntity(this.world, d, e, f);
		}

		if (entity != null) {
			EntityTracker.method_12766(entity, d, e, f);
			entity.pitch = (float)(packet.getPitch() * 360) / 256.0F;
			entity.yaw = (float)(packet.getYaw() * 360) / 256.0F;
			Entity[] entitys = entity.getParts();
			if (entitys != null) {
				int i = packet.getId() - entity.getEntityId();

				for (Entity entity3 : entitys) {
					entity3.setEntityId(entity3.getEntityId() + i);
				}
			}

			entity.setEntityId(packet.getId());
			entity.setUuid(packet.getUuid());
			this.world.addEntity(packet.getId(), entity);
			if (packet.getDataId() > 0) {
				if (packet.getEntityData() == 60 || packet.getEntityData() == 91) {
					Entity entity4 = this.world.getEntityById(packet.getDataId() - 1);
					if (entity4 instanceof LivingEntity && entity instanceof AbstractArrowEntity) {
						((AbstractArrowEntity)entity).owner = entity4;
					}
				}

				entity.setVelocityClient((double)packet.getVelocityX() / 8000.0, (double)packet.getVelocityY() / 8000.0, (double)packet.getVelocityZ() / 8000.0);
			}
		}
	}

	@Override
	public void onExperienceOrbSpawn(ExperienceOrbSpawnS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		double d = packet.getX();
		double e = packet.getY();
		double f = packet.geZ();
		Entity entity = new ExperienceOrbEntity(this.world, d, e, f, packet.getExperience());
		EntityTracker.method_12766(entity, d, e, f);
		entity.yaw = 0.0F;
		entity.pitch = 0.0F;
		entity.setEntityId(packet.getId());
		this.world.addEntity(packet.getId(), entity);
	}

	@Override
	public void onEntitySpawnGlobal(EntitySpawnGlobalS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		double d = packet.getX();
		double e = packet.getY();
		double f = packet.getZ();
		Entity entity = null;
		if (packet.getEntityTypeId() == 1) {
			entity = new LightningBoltEntity(this.world, d, e, f, false);
		}

		if (entity != null) {
			EntityTracker.method_12766(entity, d, e, f);
			entity.yaw = 0.0F;
			entity.pitch = 0.0F;
			entity.setEntityId(packet.getId());
			this.world.addEntity(entity);
		}
	}

	@Override
	public void onPaintingSpawn(PaintingSpawnS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		PaintingEntity paintingEntity = new PaintingEntity(this.world, packet.getPos(), packet.getFacing(), packet.getName());
		paintingEntity.setUuid(packet.method_12627());
		this.world.addEntity(packet.getId(), paintingEntity);
	}

	@Override
	public void onVelocityUpdate(EntityVelocityUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = this.world.getEntityById(packet.getId());
		if (entity != null) {
			entity.setVelocityClient((double)packet.getVelocityX() / 8000.0, (double)packet.getVelocityY() / 8000.0, (double)packet.getVelocityZ() / 8000.0);
		}
	}

	@Override
	public void onEntityTrackerUpdate(EntityTrackerUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = this.world.getEntityById(packet.getId());
		if (entity != null && packet.getTrackedValues() != null) {
			entity.getDataTracker().writeUpdatedEntries(packet.getTrackedValues());
		}
	}

	@Override
	public void onPlayerSpawn(PlayerSpawnS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		double d = packet.method_12628();
		double e = packet.method_12629();
		double f = packet.method_12630();
		float g = (float)(packet.getYaw() * 360) / 256.0F;
		float h = (float)(packet.getPitch() * 360) / 256.0F;
		OtherClientPlayerEntity otherClientPlayerEntity = new OtherClientPlayerEntity(this.client.world, this.getPlayerListEntry(packet.getPlayerUuid()).getProfile());
		otherClientPlayerEntity.prevX = d;
		otherClientPlayerEntity.prevTickX = d;
		otherClientPlayerEntity.prevY = e;
		otherClientPlayerEntity.prevTickY = e;
		otherClientPlayerEntity.prevZ = f;
		otherClientPlayerEntity.prevTickZ = f;
		EntityTracker.method_12766(otherClientPlayerEntity, d, e, f);
		otherClientPlayerEntity.updatePositionAndAngles(d, e, f, g, h);
		this.world.addEntity(packet.getId(), otherClientPlayerEntity);
		List<DataTracker.DataEntry<?>> list = packet.getDataTrackerEntries();
		if (list != null) {
			otherClientPlayerEntity.getDataTracker().writeUpdatedEntries(list);
		}
	}

	@Override
	public void onEntityPosition(EntityPositionS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = this.world.getEntityById(packet.getId());
		if (entity != null) {
			double d = packet.getX();
			double e = packet.getY();
			double f = packet.getZ();
			EntityTracker.method_12766(entity, d, e, f);
			if (!entity.method_13003()) {
				float g = (float)(packet.getYaw() * 360) / 256.0F;
				float h = (float)(packet.getPitch() * 360) / 256.0F;
				if (!(Math.abs(entity.x - d) >= 0.03125) && !(Math.abs(entity.y - e) >= 0.015625) && !(Math.abs(entity.z - f) >= 0.03125)) {
					entity.updateTrackedPositionAndAngles(entity.x, entity.y, entity.z, g, h, 0, true);
				} else {
					entity.updateTrackedPositionAndAngles(d, e, f, g, h, 3, true);
				}

				entity.onGround = packet.isOnGround();
			}
		}
	}

	@Override
	public void onHeldItemChange(HeldItemChangeS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		if (PlayerInventory.method_13258(packet.getSlot())) {
			this.client.player.inventory.selectedSlot = packet.getSlot();
		}
	}

	@Override
	public void onEntityUpdate(EntityS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = packet.getEntity(this.world);
		if (entity != null) {
			entity.tracedX = entity.tracedX + (long)packet.method_7808();
			entity.tracedY = entity.tracedY + (long)packet.method_7809();
			entity.tracedZ = entity.tracedZ + (long)packet.method_7810();
			double d = (double)entity.tracedX / 4096.0;
			double e = (double)entity.tracedY / 4096.0;
			double f = (double)entity.tracedZ / 4096.0;
			if (!entity.method_13003()) {
				float g = packet.shouldRotate() ? (float)(packet.getYaw() * 360) / 256.0F : entity.yaw;
				float h = packet.shouldRotate() ? (float)(packet.getPitch() * 360) / 256.0F : entity.pitch;
				entity.updateTrackedPositionAndAngles(d, e, f, g, h, 3, false);
				entity.onGround = packet.isOnGround();
			}
		}
	}

	@Override
	public void onEntitySetHeadYaw(EntitySetHeadYawS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = packet.getEntity(this.world);
		if (entity != null) {
			float f = (float)(packet.getHeadYaw() * 360) / 256.0F;
			entity.setHeadYaw(f);
		}
	}

	@Override
	public void onEntitiesDestroy(EntitiesDestroyS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);

		for (int i = 0; i < packet.getEntityIds().length; i++) {
			this.world.removeEntity(packet.getEntityIds()[i]);
		}
	}

	@Override
	public void onPlayerPositionLook(PlayerPositionLookS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		PlayerEntity playerEntity = this.client.player;
		double d = packet.getX();
		double e = packet.getY();
		double f = packet.getZ();
		float g = packet.getYaw();
		float h = packet.getPitch();
		if (packet.getFlags().contains(PlayerPositionLookS2CPacket.Flag.X)) {
			d += playerEntity.x;
		} else {
			playerEntity.velocityX = 0.0;
		}

		if (packet.getFlags().contains(PlayerPositionLookS2CPacket.Flag.Y)) {
			e += playerEntity.y;
		} else {
			playerEntity.velocityY = 0.0;
		}

		if (packet.getFlags().contains(PlayerPositionLookS2CPacket.Flag.Z)) {
			f += playerEntity.z;
		} else {
			playerEntity.velocityZ = 0.0;
		}

		if (packet.getFlags().contains(PlayerPositionLookS2CPacket.Flag.X_ROT)) {
			h += playerEntity.pitch;
		}

		if (packet.getFlags().contains(PlayerPositionLookS2CPacket.Flag.Y_ROT)) {
			g += playerEntity.yaw;
		}

		playerEntity.updatePositionAndAngles(d, e, f, g, h);
		this.connection.send(new TeleportConfirmC2SPacket(packet.getTeleportId()));
		this.connection
			.send(new PlayerMoveC2SPacket.Both(playerEntity.x, playerEntity.getBoundingBox().minY, playerEntity.z, playerEntity.yaw, playerEntity.pitch, false));
		if (!this.positionLookSetup) {
			this.client.player.prevX = this.client.player.x;
			this.client.player.prevY = this.client.player.y;
			this.client.player.prevZ = this.client.player.z;
			this.positionLookSetup = true;
			this.client.setScreen(null);
		}
	}

	@Override
	public void onChunkDeltaUpdate(ChunkDeltaUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);

		for (ChunkDeltaUpdateS2CPacket.ChunkDeltaRecord chunkDeltaRecord : packet.getRecords()) {
			this.world.setBlockStateWithoutNeighborUpdates(chunkDeltaRecord.getBlockPos(), chunkDeltaRecord.getState());
		}
	}

	@Override
	public void onChunkData(ChunkDataS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		if (packet.shouldLoad()) {
			this.world.handleChunk(packet.getChunkX(), packet.getChunkZ(), true);
		}

		this.world.method_1251(packet.getChunkX() << 4, 0, packet.getChunkZ() << 4, (packet.getChunkX() << 4) + 15, 256, (packet.getChunkZ() << 4) + 15);
		Chunk chunk = this.world.getChunk(packet.getChunkX(), packet.getChunkZ());
		chunk.method_3895(packet.getReadBuffer(), packet.method_7760(), packet.shouldLoad());
		this.world.onRenderRegionUpdate(packet.getChunkX() << 4, 0, packet.getChunkZ() << 4, (packet.getChunkX() << 4) + 15, 256, (packet.getChunkZ() << 4) + 15);
		if (!packet.shouldLoad() || !(this.world.dimension instanceof OverworldDimension)) {
			chunk.method_3922();
		}

		for (NbtCompound nbtCompound : packet.getBlockEntityTagList()) {
			BlockPos blockPos = new BlockPos(nbtCompound.getInt("x"), nbtCompound.getInt("y"), nbtCompound.getInt("z"));
			BlockEntity blockEntity = this.world.getBlockEntity(blockPos);
			if (blockEntity != null) {
				blockEntity.fromNbt(nbtCompound);
			}
		}
	}

	@Override
	public void onUnloadChunk(ChunkUnloadS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.world.handleChunk(packet.getChunkX(), packet.getChunkZ(), false);
	}

	@Override
	public void onBlockUpdate(BlockUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.world.setBlockStateWithoutNeighborUpdates(packet.getPos(), packet.getState());
	}

	@Override
	public void onDisconnect(DisconnectS2CPacket packet) {
		this.connection.disconnect(packet.getReason());
	}

	@Override
	public void onDisconnected(Text reason) {
		this.client.connect(null);
		if (this.loginScreen != null) {
			if (this.loginScreen instanceof RealmsScreenProxy) {
				this.client.setScreen(new DisconnectedRealmsScreen(((RealmsScreenProxy)this.loginScreen).getRealmsScreen(), "disconnect.lost", reason).getProxy());
			} else {
				this.client.setScreen(new DisconnectedScreen(this.loginScreen, "disconnect.lost", reason));
			}
		} else {
			this.client.setScreen(new DisconnectedScreen(new MultiplayerScreen(new TitleScreen()), "disconnect.lost", reason));
		}
	}

	public void sendPacket(Packet<?> packet) {
		this.connection.send(packet);
	}

	@Override
	public void onChunkRenderDistanceCenter(ChunkRenderDistanceCenterS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = this.world.getEntityById(packet.getChunkX());
		LivingEntity livingEntity = (LivingEntity)this.world.getEntityById(packet.getChunkZ());
		if (livingEntity == null) {
			livingEntity = this.client.player;
		}

		if (entity != null) {
			if (entity instanceof ExperienceOrbEntity) {
				this.world
					.playSound(
						entity.x,
						entity.y,
						entity.z,
						Sounds.ENTITY_EXPERIENCE_ORB_PICKUP,
						SoundCategory.PLAYERS,
						0.2F,
						((this.random.nextFloat() - this.random.nextFloat()) * 0.7F + 1.0F) * 2.0F,
						false
					);
			} else {
				this.world
					.playSound(
						entity.x,
						entity.y,
						entity.z,
						Sounds.ENTITY_ITEM_PICKUP,
						SoundCategory.PLAYERS,
						0.2F,
						((this.random.nextFloat() - this.random.nextFloat()) * 0.7F + 1.0F) * 2.0F,
						false
					);
			}

			this.client.particleManager.method_12256(new ItemPickupParticle(this.world, entity, livingEntity, 0.5F));
			this.world.removeEntity(packet.getChunkX());
		}
	}

	@Override
	public void onChatMessage(ChatMessageS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		if (packet.getType() == 2) {
			this.client.inGameHud.setOverlayMessage(packet.getMessage(), false);
		} else {
			this.client.inGameHud.getChatHud().addMessage(packet.getMessage());
		}
	}

	@Override
	public void onEntityAnimation(EntityAnimationS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = this.world.getEntityById(packet.getId());
		if (entity != null) {
			if (packet.getAnimationId() == 0) {
				LivingEntity livingEntity = (LivingEntity)entity;
				livingEntity.swingHand(Hand.MAIN_HAND);
			} else if (packet.getAnimationId() == 3) {
				LivingEntity livingEntity2 = (LivingEntity)entity;
				livingEntity2.swingHand(Hand.OFF_HAND);
			} else if (packet.getAnimationId() == 1) {
				entity.animateDamage();
			} else if (packet.getAnimationId() == 2) {
				PlayerEntity playerEntity = (PlayerEntity)entity;
				playerEntity.awaken(false, false, false);
			} else if (packet.getAnimationId() == 4) {
				this.client.particleManager.addEmitter(entity, ParticleType.CRIT);
			} else if (packet.getAnimationId() == 5) {
				this.client.particleManager.addEmitter(entity, ParticleType.CRIT_MAGIC);
			}
		}
	}

	@Override
	public void onBedSleep(BedSleepS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		packet.getPlayer(this.world).attemptSleep(packet.getBedPos());
	}

	@Override
	public void onMobSpawn(MobSpawnS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		double d = packet.getX();
		double e = packet.getY();
		double f = packet.getZ();
		float g = (float)(packet.getYaw() * 360) / 256.0F;
		float h = (float)(packet.getPitch() * 360) / 256.0F;
		LivingEntity livingEntity = (LivingEntity)EntityType.createInstanceFromRawId(packet.getEntityTypeId(), this.client.world);
		EntityTracker.method_12766(livingEntity, d, e, f);
		livingEntity.bodyYaw = (float)(packet.getHeadYaw() * 360) / 256.0F;
		livingEntity.headYaw = (float)(packet.getHeadYaw() * 360) / 256.0F;
		Entity[] entitys = livingEntity.getParts();
		if (entitys != null) {
			int i = packet.getId() - livingEntity.getEntityId();

			for (Entity entity : entitys) {
				entity.setEntityId(entity.getEntityId() + i);
			}
		}

		livingEntity.setEntityId(packet.getId());
		livingEntity.setUuid(packet.getUuid());
		livingEntity.updatePositionAndAngles(d, e, f, g, h);
		livingEntity.velocityX = (double)((float)packet.getVelocityX() / 8000.0F);
		livingEntity.velocityY = (double)((float)packet.getVelocityY() / 8000.0F);
		livingEntity.velocityZ = (double)((float)packet.getVelocityZ() / 8000.0F);
		this.world.addEntity(packet.getId(), livingEntity);
		List<DataTracker.DataEntry<?>> list = packet.getEntries();
		if (list != null) {
			livingEntity.getDataTracker().writeUpdatedEntries(list);
		}
	}

	@Override
	public void onWorldTimeUpdate(WorldTimeUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.world.setTime(packet.getTime());
		this.client.world.setTimeOfDay(packet.getTimeOfDay());
	}

	@Override
	public void onPlayerSpawnPosition(PlayerSpawnPositionS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.player.setPlayerSpawn(packet.getPos(), true);
		this.client.world.getLevelProperties().setSpawnPos(packet.getPos());
	}

	@Override
	public void onSetPassengers(SetPassengersS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = this.world.getEntityById(packet.getEntityId());
		if (entity == null) {
			LOGGER.warn("Received passengers for unknown entity");
		} else {
			boolean bl = entity.hasPassengerDeep(this.client.player);
			entity.removeAllPassengers();

			for (int k : packet.getPassengerIds()) {
				Entity entity2 = this.world.getEntityById(k);
				if (entity2 == null) {
					LOGGER.warn("Received unknown passenger for {}", new Object[]{entity});
				} else {
					entity2.startRiding(entity, true);
					if (entity2 == this.client.player && !bl) {
						this.client
							.inGameHud
							.setOverlayMessage(I18n.translate("mount.onboard", GameOptions.getFormattedNameForKeyCode(this.client.options.sneakKey.getCode())), false);
					}
				}
			}
		}
	}

	@Override
	public void onEntityAttach(EntityAttachS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = this.world.getEntityById(packet.getId());
		Entity entity2 = this.world.getEntityById(packet.getHoldingEntityId());
		if (entity instanceof MobEntity) {
			if (entity2 != null) {
				((MobEntity)entity).attachLeash(entity2, false);
			} else {
				((MobEntity)entity).detachLeash(false, false);
			}
		}
	}

	@Override
	public void onEntityStatus(EntityStatusS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = packet.getEntity(this.world);
		if (entity != null) {
			if (packet.getStatus() == 21) {
				this.client.getSoundManager().play(new GuardianAttackSoundInstance((GuardianEntity)entity));
			} else {
				entity.handleStatus(packet.getStatus());
			}
		}
	}

	@Override
	public void onExperienceBarUpdate(HealthUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.player.updateHealth(packet.getHealth());
		this.client.player.getHungerManager().setFoodLevel(packet.getFood());
		this.client.player.getHungerManager().setSaturationLevelClient(packet.getSaturation());
	}

	@Override
	public void onHealthUpdate(ExperienceBarUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.player.setExperience(packet.getBarProgress(), packet.getExperienceLevel(), packet.getExperience());
	}

	@Override
	public void onPlayerRespawn(PlayerRespawnS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		if (packet.getDimensionId() != this.client.player.dimension) {
			this.positionLookSetup = false;
			Scoreboard scoreboard = this.world.getScoreboard();
			this.world = new ClientWorld(
				this,
				new LevelInfo(0L, packet.method_7848(), false, this.client.world.getLevelProperties().isHardcore(), packet.getGeneratorType()),
				packet.getDimensionId(),
				packet.getDifficulty(),
				this.client.profiler
			);
			this.world.setScoreboard(scoreboard);
			this.client.connect(this.world);
			this.client.player.dimension = packet.getDimensionId();
			this.client.setScreen(new DownloadingTerrainScreen(this));
		}

		this.client.setDimensionAndSpawn(packet.getDimensionId());
		this.client.interactionManager.setGameMode(packet.method_7848());
	}

	@Override
	public void onExplosion(ExplosionS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Explosion explosion = new Explosion(this.client.world, null, packet.getX(), packet.getY(), packet.getZ(), packet.getRadius(), packet.getAffectedBlocks());
		explosion.affectWorld(true);
		this.client.player.velocityX = this.client.player.velocityX + (double)packet.getPlayerVelocityX();
		this.client.player.velocityY = this.client.player.velocityY + (double)packet.getPlayerVelocityY();
		this.client.player.velocityZ = this.client.player.velocityZ + (double)packet.getPlayerVelocityZ();
	}

	@Override
	public void onOpenScreen(OpenScreenS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		ClientPlayerEntity clientPlayerEntity = this.client.player;
		if ("minecraft:container".equals(packet.getName())) {
			clientPlayerEntity.openInventory(new SimpleInventory(packet.getText(), packet.getSlotCount()));
			clientPlayerEntity.openScreenHandler.syncId = packet.getId();
		} else if ("minecraft:villager".equals(packet.getName())) {
			clientPlayerEntity.openTradingScreen(new Merchant(clientPlayerEntity, packet.getText()));
			clientPlayerEntity.openScreenHandler.syncId = packet.getId();
		} else if ("EntityHorse".equals(packet.getName())) {
			Entity entity = this.world.getEntityById(packet.getEntityId());
			if (entity instanceof HorseBaseEntity) {
				clientPlayerEntity.openHorseInventory((HorseBaseEntity)entity, new AnimalInventory(packet.getText(), packet.getSlotCount()));
				clientPlayerEntity.openScreenHandler.syncId = packet.getId();
			}
		} else if (!packet.hasSlots()) {
			clientPlayerEntity.openHandledScreen(new BlockCommunicationNameable(packet.getName(), packet.getText()));
			clientPlayerEntity.openScreenHandler.syncId = packet.getId();
		} else {
			Inventory inventory = new ClientNetworkSyncedInventory(packet.getName(), packet.getText(), packet.getSlotCount());
			clientPlayerEntity.openInventory(inventory);
			clientPlayerEntity.openScreenHandler.syncId = packet.getId();
		}
	}

	@Override
	public void onScreenHandlerSlotUpdate(ScreenHandlerSlotUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		PlayerEntity playerEntity = this.client.player;
		if (packet.getSyncId() == -1) {
			playerEntity.inventory.setCursorStack(packet.getItemStack());
		} else if (packet.getSyncId() == -2) {
			playerEntity.inventory.setInvStack(packet.getSlot(), packet.getItemStack());
		} else {
			boolean bl = false;
			if (this.client.currentScreen instanceof CreativeInventoryScreen) {
				CreativeInventoryScreen creativeInventoryScreen = (CreativeInventoryScreen)this.client.currentScreen;
				bl = creativeInventoryScreen.getSelectedTab() != ItemGroup.INVENTORY.getIndex();
			}

			if (packet.getSyncId() == 0 && packet.getSlot() >= 36 && packet.getSlot() < 45) {
				ItemStack itemStack = playerEntity.playerScreenHandler.getSlot(packet.getSlot()).getStack();
				if (packet.getItemStack() != null && (itemStack == null || itemStack.count < packet.getItemStack().count)) {
					packet.getItemStack().pickupTick = 5;
				}

				playerEntity.playerScreenHandler.setStackInSlot(packet.getSlot(), packet.getItemStack());
			} else if (packet.getSyncId() == playerEntity.openScreenHandler.syncId && (packet.getSyncId() != 0 || !bl)) {
				playerEntity.openScreenHandler.setStackInSlot(packet.getSlot(), packet.getItemStack());
			}
		}
	}

	@Override
	public void onGuiActionConfirm(ConfirmGuiActionS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		ScreenHandler screenHandler = null;
		PlayerEntity playerEntity = this.client.player;
		if (packet.getId() == 0) {
			screenHandler = playerEntity.playerScreenHandler;
		} else if (packet.getId() == playerEntity.openScreenHandler.syncId) {
			screenHandler = playerEntity.openScreenHandler;
		}

		if (screenHandler != null && !packet.wasAccepted()) {
			this.sendPacket(new ConfirmGuiActionC2SPacket(packet.getId(), packet.getActionId(), true));
		}
	}

	@Override
	public void onInventory(InventoryS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		PlayerEntity playerEntity = this.client.player;
		if (packet.getScreenId() == 0) {
			playerEntity.playerScreenHandler.updateSlotStacks(packet.getSlotStacks());
		} else if (packet.getScreenId() == playerEntity.openScreenHandler.syncId) {
			playerEntity.openScreenHandler.updateSlotStacks(packet.getSlotStacks());
		}
	}

	@Override
	public void onSignEditorOpen(SignEditorOpenS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		BlockEntity blockEntity = this.world.getBlockEntity(packet.getPos());
		if (!(blockEntity instanceof SignBlockEntity)) {
			blockEntity = new SignBlockEntity();
			blockEntity.setWorld(this.world);
			blockEntity.setPosition(packet.getPos());
		}

		this.client.player.openEditSignScreen((SignBlockEntity)blockEntity);
	}

	@Override
	public void onBlockEntityUpdate(BlockEntityUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		if (this.client.world.blockExists(packet.getPos())) {
			BlockEntity blockEntity = this.client.world.getBlockEntity(packet.getPos());
			int i = packet.getBlockEntityId();
			boolean bl = i == 2 && blockEntity instanceof CommandBlockBlockEntity;
			if (i == 1 && blockEntity instanceof MobSpawnerBlockEntity
				|| bl
				|| i == 3 && blockEntity instanceof BeaconBlockEntity
				|| i == 4 && blockEntity instanceof SkullBlockEntity
				|| i == 5 && blockEntity instanceof FlowerPotBlockEntity
				|| i == 6 && blockEntity instanceof BannerBlockEntity
				|| i == 7 && blockEntity instanceof StructureBlockEntity
				|| i == 8 && blockEntity instanceof EndGatewayBlockEntity
				|| i == 9 && blockEntity instanceof SignBlockEntity) {
				blockEntity.fromNbt(packet.getNbt());
			}

			if (bl && this.client.currentScreen instanceof CommandBlockScreen) {
				((CommandBlockScreen)this.client.currentScreen).method_12191();
			}
		}
	}

	@Override
	public void onScreenHandlerPropertyUpdate(ScreenHandlerPropertyUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		PlayerEntity playerEntity = this.client.player;
		if (playerEntity.openScreenHandler != null && playerEntity.openScreenHandler.syncId == packet.getSyncId()) {
			playerEntity.openScreenHandler.setProperty(packet.getPropertyId(), packet.getValue());
		}
	}

	@Override
	public void onEquipmentUpdate(EntityEquipmentUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = this.world.getEntityById(packet.getId());
		if (entity != null) {
			entity.equipStack(packet.getSlot(), packet.getStack());
		}
	}

	@Override
	public void onCloseScreen(CloseScreenS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.player.closeScreen();
	}

	@Override
	public void onBlockAction(BlockActionS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.world.addBlockAction(packet.getPos(), packet.getBlock(), packet.getType(), packet.getData());
	}

	@Override
	public void onBlockDestroyProgress(BlockBreakingProgressS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.world.setBlockBreakingInfo(packet.getEntityId(), packet.getPos(), packet.getProgress());
	}

	@Override
	public void onGameStateChange(GameStateChangeS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		PlayerEntity playerEntity = this.client.player;
		int i = packet.getChangeType();
		float f = packet.getValue();
		int j = MathHelper.floor(f + 0.5F);
		if (i >= 0 && i < GameStateChangeS2CPacket.REASON_MESSAGES.length && GameStateChangeS2CPacket.REASON_MESSAGES[i] != null) {
			playerEntity.addMessage(new TranslatableText(GameStateChangeS2CPacket.REASON_MESSAGES[i]));
		}

		if (i == 1) {
			this.world.getLevelProperties().setRaining(true);
			this.world.setRainGradient(0.0F);
		} else if (i == 2) {
			this.world.getLevelProperties().setRaining(false);
			this.world.setRainGradient(1.0F);
		} else if (i == 3) {
			this.client.interactionManager.setGameMode(GameMode.setGameModeWithId(j));
		} else if (i == 4) {
			if (j == 0) {
				this.client.player.networkHandler.sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.PERFORM_RESPAWN));
				this.client.setScreen(new DownloadingTerrainScreen(this));
			} else if (j == 1) {
				this.client.setScreen(new CreditsScreen());
			}
		} else if (i == 5) {
			GameOptions gameOptions = this.client.options;
			if (f == 0.0F) {
				this.client.setScreen(new DemoScreen());
			} else if (f == 101.0F) {
				this.client
					.inGameHud
					.getChatHud()
					.addMessage(
						new TranslatableText(
							"demo.help.movement",
							GameOptions.getFormattedNameForKeyCode(gameOptions.forwardKey.getCode()),
							GameOptions.getFormattedNameForKeyCode(gameOptions.leftKey.getCode()),
							GameOptions.getFormattedNameForKeyCode(gameOptions.backKey.getCode()),
							GameOptions.getFormattedNameForKeyCode(gameOptions.rightKey.getCode())
						)
					);
			} else if (f == 102.0F) {
				this.client
					.inGameHud
					.getChatHud()
					.addMessage(new TranslatableText("demo.help.jump", GameOptions.getFormattedNameForKeyCode(gameOptions.jumpKey.getCode())));
			} else if (f == 103.0F) {
				this.client
					.inGameHud
					.getChatHud()
					.addMessage(new TranslatableText("demo.help.inventory", GameOptions.getFormattedNameForKeyCode(gameOptions.inventoryKey.getCode())));
			}
		} else if (i == 6) {
			this.world
				.playSound(
					playerEntity,
					playerEntity.x,
					playerEntity.y + (double)playerEntity.getEyeHeight(),
					playerEntity.z,
					Sounds.ENTITY_ARROW_HIT_PLAYER,
					SoundCategory.PLAYERS,
					0.18F,
					0.45F
				);
		} else if (i == 7) {
			this.world.setRainGradient(f);
		} else if (i == 8) {
			this.world.setThunderGradient(f);
		} else if (i == 10) {
			this.world.addParticle(ParticleType.MOB_APPEARANCE, playerEntity.x, playerEntity.y, playerEntity.z, 0.0, 0.0, 0.0, new int[0]);
			this.world.playSound(playerEntity, playerEntity.x, playerEntity.y, playerEntity.z, Sounds.ENTITY_ELDER_GUARDIAN_CURSE, SoundCategory.HOSTILE, 1.0F, 1.0F);
		}
	}

	@Override
	public void onMapUpdate(MapUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		MapState mapState = FilledMapItem.getMapState(packet.getId(), this.client.world);
		packet.apply(mapState);
		this.client.gameRenderer.getMapRenderer().updateTexture(mapState);
	}

	@Override
	public void onWorldEvent(WorldEventS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		if (packet.isGlobal()) {
			this.client.world.method_4689(packet.getEventId(), packet.getPos(), packet.getEffectData());
		} else {
			this.client.world.syncGlobalEvent(packet.getEventId(), packet.getPos(), packet.getEffectData());
		}
	}

	@Override
	public void onStatsUpdate(StatsUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		boolean bl = false;

		for (Entry<Stat, Integer> entry : packet.getStatMap().entrySet()) {
			Stat stat = (Stat)entry.getKey();
			int i = (Integer)entry.getValue();
			if (stat.isAchievement() && i > 0) {
				if (this.field_7920 && this.client.player.getStatHandler().getStatLevel(stat) == 0) {
					Achievement achievement = (Achievement)stat;
					this.client.notification.display(achievement);
					if (stat == AchievementsAndCriterions.TAKING_INVENTORY) {
						this.client.options.showInventoryAchievementHint = false;
						this.client.options.save();
					}
				}

				bl = true;
			}

			this.client.player.getStatHandler().setStatLevel(this.client.player, stat, i);
		}

		if (!this.field_7920 && !bl && this.client.options.showInventoryAchievementHint) {
			this.client.notification.displayRaw(AchievementsAndCriterions.TAKING_INVENTORY);
		}

		this.field_7920 = true;
		if (this.client.currentScreen instanceof StatsListener) {
			((StatsListener)this.client.currentScreen).onStatsReady();
		}
	}

	@Override
	public void onEntityPotionEffect(EntityStatusEffectS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = this.world.getEntityById(packet.getEntityId());
		if (entity instanceof LivingEntity) {
			StatusEffect statusEffect = StatusEffect.byIndex(packet.getEffectId());
			if (statusEffect != null) {
				StatusEffectInstance statusEffectInstance = new StatusEffectInstance(
					statusEffect, packet.getDuration(), packet.getAmplifier(), packet.isAmbient(), packet.shouldShowParticles()
				);
				statusEffectInstance.setPermanent(packet.isPermanent());
				((LivingEntity)entity).addStatusEffect(statusEffectInstance);
			}
		}
	}

	@Override
	public void onCombatEvent(CombatEventS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		if (packet.type == CombatEventS2CPacket.Type.ENTITY_DIED) {
			Entity entity = this.world.getEntityById(packet.entityId);
			if (entity == this.client.player) {
				this.client.setScreen(new DeathScreen(packet.deathMessage));
			}
		}
	}

	@Override
	public void onDifficulty(DifficultyS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.world.getLevelProperties().setDifficulty(packet.getDifficulty());
		this.client.world.getLevelProperties().setDifficultyLocked(packet.isDifficultyLocked());
	}

	@Override
	public void onSetCameraEntity(SetCameraEntityS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = packet.getEntity(this.world);
		if (entity != null) {
			this.client.setCameraEntity(entity);
		}
	}

	@Override
	public void onWorldBorder(WorldBorderS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		packet.apply(this.world.getWorldBorder());
	}

	@Override
	public void onTitle(TitleS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		TitleS2CPacket.Action action = packet.getAction();
		String string = null;
		String string2 = null;
		String string3 = packet.getText() != null ? packet.getText().asFormattedString() : "";
		switch (action) {
			case TITLE:
				string = string3;
				break;
			case SUBTITLE:
				string2 = string3;
				break;
			case RESET:
				this.client.inGameHud.setTitles("", "", -1, -1, -1);
				this.client.inGameHud.setDefaultTitleFade();
				return;
		}

		this.client.inGameHud.setTitles(string, string2, packet.getFadeInTicks(), packet.getStayTicks(), packet.getFadeOutTicks());
	}

	@Override
	public void onPlayerListHeader(PlayerListHeaderS2CPacket packet) {
		this.client.inGameHud.getPlayerListWidget().setHeader(packet.getHeader().asFormattedString().isEmpty() ? null : packet.getHeader());
		this.client.inGameHud.getPlayerListWidget().setFooter(packet.getFooter().asFormattedString().isEmpty() ? null : packet.getFooter());
	}

	@Override
	public void onRemoveEntityEffect(RemoveEntityStatusEffectS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = packet.getEntity(this.world);
		if (entity instanceof LivingEntity) {
			((LivingEntity)entity).method_13052(packet.getEffect());
		}
	}

	@Override
	public void onPlayerList(PlayerListS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);

		for (PlayerListS2CPacket.Entry entry : packet.getEntries()) {
			if (packet.getAction() == PlayerListS2CPacket.Action.REMOVE_PLAYER) {
				this.playerListEntries.remove(entry.getProfile().getId());
			} else {
				PlayerListEntry playerListEntry = (PlayerListEntry)this.playerListEntries.get(entry.getProfile().getId());
				if (packet.getAction() == PlayerListS2CPacket.Action.ADD_PLAYER) {
					playerListEntry = new PlayerListEntry(entry);
					this.playerListEntries.put(playerListEntry.getProfile().getId(), playerListEntry);
				}

				if (playerListEntry != null) {
					switch (packet.getAction()) {
						case ADD_PLAYER:
							playerListEntry.setGameMode(entry.getGameMode());
							playerListEntry.setLatency(entry.getLatency());
							break;
						case UPDATE_GAME_MODE:
							playerListEntry.setGameMode(entry.getGameMode());
							break;
						case UPDATE_LATENCY:
							playerListEntry.setLatency(entry.getLatency());
							break;
						case UPDATE_DISPLAY_NAME:
							playerListEntry.setDisplayName(entry.getDisplayName());
					}
				}
			}
		}
	}

	@Override
	public void onKeepAlive(KeepAliveS2CPacket packet) {
		this.sendPacket(new KeepAliveC2SPacket(packet.getId()));
	}

	@Override
	public void onPlayerAbilities(PlayerAbilitiesS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		PlayerEntity playerEntity = this.client.player;
		playerEntity.abilities.flying = packet.isFlying();
		playerEntity.abilities.creativeMode = packet.isCreativeMode();
		playerEntity.abilities.invulnerable = packet.isInvulnerable();
		playerEntity.abilities.allowFlying = packet.allowFlying();
		playerEntity.abilities.setFlySpeed(packet.getFlySpeed());
		playerEntity.abilities.setWalkSpeed(packet.getFovModifier());
	}

	@Override
	public void onCommandSuggestions(CommandSuggestionsS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		String[] strings = packet.getSuggestions();
		if (this.client.currentScreen instanceof class_2844) {
			((class_2844)this.client.currentScreen).method_12182(strings);
		}
	}

	@Override
	public void onPlaySound(PlaySoundIdS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client
			.world
			.playSound(this.client.player, packet.getX(), packet.getY(), packet.getZ(), packet.getSound(), packet.getCategory(), packet.getVolume(), packet.getPitch());
	}

	@Override
	public void onPlaySoundName(PlaySoundNameS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client
			.getSoundManager()
			.play(
				new PositionedSoundInstance(
					new Identifier(packet.getName()),
					packet.getCategory(),
					packet.getVolume(),
					packet.getPitch(),
					false,
					0,
					SoundInstance.AttenuationType.LINEAR,
					(float)packet.getX(),
					(float)packet.getY(),
					(float)packet.getZ()
				)
			);
	}

	@Override
	public void onResourcePackSend(ResourcePackSendS2CPacket packet) {
		final String string = packet.getURL();
		final String string2 = packet.getHash();
		if (this.method_12229(string)) {
			if (string.startsWith("level://")) {
				String string3 = string.substring("level://".length());
				File file = new File(this.client.runDirectory, "saves");
				File file2 = new File(file, string3);
				if (file2.isFile()) {
					this.connection.send(new ResourcePackStatusC2SPacket(ResourcePackStatusC2SPacket.Status.ACCEPTED));
					Futures.addCallback(this.client.getResourcePackLoader().loadServerPack(file2), this.method_13423());
				} else {
					this.connection.send(new ResourcePackStatusC2SPacket(ResourcePackStatusC2SPacket.Status.FAILED_DOWNLOAD));
				}
			} else {
				ServerInfo serverInfo = this.client.getCurrentServerEntry();
				if (serverInfo != null && serverInfo.getResourcePack() == ServerInfo.ResourcePackState.ENABLED) {
					this.connection.send(new ResourcePackStatusC2SPacket(ResourcePackStatusC2SPacket.Status.ACCEPTED));
					Futures.addCallback(this.client.getResourcePackLoader().downloadResourcePack(string, string2), this.method_13423());
				} else if (serverInfo != null && serverInfo.getResourcePack() != ServerInfo.ResourcePackState.PROMPT) {
					this.connection.send(new ResourcePackStatusC2SPacket(ResourcePackStatusC2SPacket.Status.DECLINED));
				} else {
					this.client
						.submit(
							new Runnable() {
								public void run() {
									ClientPlayNetworkHandler.this.client
										.setScreen(
											new ConfirmScreen(
												new IdentifiableBooleanConsumer() {
													@Override
													public void confirmResult(boolean confirmed, int id) {
														ClientPlayNetworkHandler.this.client = MinecraftClient.getInstance();
														ServerInfo serverInfo = ClientPlayNetworkHandler.this.client.getCurrentServerEntry();
														if (confirmed) {
															if (serverInfo != null) {
																serverInfo.setResourcePackState(ServerInfo.ResourcePackState.ENABLED);
															}

															ClientPlayNetworkHandler.this.connection.send(new ResourcePackStatusC2SPacket(ResourcePackStatusC2SPacket.Status.ACCEPTED));
															Futures.addCallback(
																ClientPlayNetworkHandler.this.client.getResourcePackLoader().downloadResourcePack(string, string2),
																ClientPlayNetworkHandler.this.method_13423()
															);
														} else {
															if (serverInfo != null) {
																serverInfo.setResourcePackState(ServerInfo.ResourcePackState.DISABLED);
															}

															ClientPlayNetworkHandler.this.connection.send(new ResourcePackStatusC2SPacket(ResourcePackStatusC2SPacket.Status.DECLINED));
														}

														ServerList.updateServerListEntry(serverInfo);
														ClientPlayNetworkHandler.this.client.setScreen(null);
													}
												},
												I18n.translate("multiplayer.texturePrompt.line1"),
												I18n.translate("multiplayer.texturePrompt.line2"),
												0
											)
										);
								}
							}
						);
				}
			}
		}
	}

	private boolean method_12229(String string) {
		try {
			URI uRI = new URI(string.replace(' ', '+'));
			String string2 = uRI.getScheme();
			boolean bl = "level".equals(string2);
			if (!"http".equals(string2) && !"https".equals(string2) && !bl) {
				throw new URISyntaxException(string, "Wrong protocol");
			} else if (!bl || !string.contains("..") && string.endsWith("/resources.zip")) {
				return true;
			} else {
				throw new URISyntaxException(string, "Invalid levelstorage resourcepack path");
			}
		} catch (URISyntaxException var5) {
			this.connection.send(new ResourcePackStatusC2SPacket(ResourcePackStatusC2SPacket.Status.FAILED_DOWNLOAD));
			return false;
		}
	}

	private FutureCallback<Object> method_13423() {
		return new FutureCallback<Object>() {
			public void onSuccess(@Nullable Object object) {
				ClientPlayNetworkHandler.this.connection.send(new ResourcePackStatusC2SPacket(ResourcePackStatusC2SPacket.Status.SUCCESSFULLY_LOADED));
			}

			public void onFailure(Throwable throwable) {
				ClientPlayNetworkHandler.this.connection.send(new ResourcePackStatusC2SPacket(ResourcePackStatusC2SPacket.Status.FAILED_DOWNLOAD));
			}
		};
	}

	@Override
	public void onBossBar(BossBarS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.inGameHud.method_12167().method_12170(packet);
	}

	@Override
	public void onChunkLoadDistance(ChunkLoadDistanceS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		if (packet.getDistance() == 0) {
			this.client.player.getItemCooldownManager().method_11385(packet.getItem());
		} else {
			this.client.player.getItemCooldownManager().method_11384(packet.getItem(), packet.getDistance());
		}
	}

	@Override
	public void onVehicleMove(VehicleMoveS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = this.client.player.getRootVehicle();
		if (entity != this.client.player && entity.method_13003()) {
			entity.updatePositionAndAngles(packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch());
			this.connection.send(new VehicleMoveC2SPacket(entity));
		}
	}

	@Override
	public void onCustomPayload(CustomPayloadS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		if ("MC|TrList".equals(packet.getChannel())) {
			PacketByteBuf packetByteBuf = packet.getPayload();

			try {
				int i = packetByteBuf.readInt();
				Screen screen = this.client.currentScreen;
				if (screen != null && screen instanceof VillagerTradingScreen && i == this.client.player.openScreenHandler.syncId) {
					Trader trader = ((VillagerTradingScreen)screen).getTrader();
					TraderOfferList traderOfferList = TraderOfferList.fromPacket(packetByteBuf);
					trader.setTraderOfferList(traderOfferList);
				}
			} catch (IOException var10) {
				LOGGER.error("Couldn't load trade info", var10);
			} finally {
				packetByteBuf.release();
			}
		} else if ("MC|Brand".equals(packet.getChannel())) {
			this.client.player.setServerBrand(packet.getPayload().readString(32767));
		} else if ("MC|BOpen".equals(packet.getChannel())) {
			Hand hand = packet.getPayload().readEnumConstant(Hand.class);
			ItemStack itemStack = hand == Hand.OFF_HAND ? this.client.player.getOffHandStack() : this.client.player.getMainHandStack();
			if (itemStack != null && itemStack.getItem() == Items.WRITTEN_BOOK) {
				this.client.setScreen(new BookEditScreen(this.client.player, itemStack, false));
			}
		} else if ("MC|DebugPath".equals(packet.getChannel())) {
			PacketByteBuf packetByteBuf2 = packet.getPayload();
			int j = packetByteBuf2.readInt();
			float f = packetByteBuf2.readFloat();
			PathMinHeap pathMinHeap = PathMinHeap.read(packetByteBuf2);
			((class_2891)this.client.debugRenderer.field_14968).method_12434(j, pathMinHeap, f);
		} else if ("MC|StopSound".equals(packet.getChannel())) {
			PacketByteBuf packetByteBuf3 = packet.getPayload();
			String string = packetByteBuf3.readString(32767);
			String string2 = packetByteBuf3.readString(256);
			this.client.getSoundManager().stopSound(string2, SoundCategory.byName(string));
		}
	}

	@Override
	public void onScoreboardObjectiveUpdate(ScoreboardObjectiveUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Scoreboard scoreboard = this.world.getScoreboard();
		if (packet.getMode() == 0) {
			ScoreboardObjective scoreboardObjective = scoreboard.method_4884(packet.getName(), ScoreboardCriterion.DUMMY);
			scoreboardObjective.method_4846(packet.getDisplayName());
			scoreboardObjective.setRenderType(packet.getType());
		} else {
			ScoreboardObjective scoreboardObjective2 = scoreboard.getNullableObjective(packet.getName());
			if (packet.getMode() == 1) {
				scoreboard.removeObjective(scoreboardObjective2);
			} else if (packet.getMode() == 2) {
				scoreboardObjective2.method_4846(packet.getDisplayName());
				scoreboardObjective2.setRenderType(packet.getType());
			}
		}
	}

	@Override
	public void onScoreboardPlayerUpdate(ScoreboardPlayerUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Scoreboard scoreboard = this.world.getScoreboard();
		ScoreboardObjective scoreboardObjective = scoreboard.getNullableObjective(packet.getObjectiveName());
		if (packet.getType() == ScoreboardPlayerUpdateS2CPacket.UpdateType.CHANGE) {
			ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(packet.getPlayerName(), scoreboardObjective);
			scoreboardPlayerScore.setScore(packet.getScore());
		} else if (packet.getType() == ScoreboardPlayerUpdateS2CPacket.UpdateType.REMOVE) {
			if (ChatUtil.isEmpty(packet.getObjectiveName())) {
				scoreboard.resetPlayerScore(packet.getPlayerName(), null);
			} else if (scoreboardObjective != null) {
				scoreboard.resetPlayerScore(packet.getPlayerName(), scoreboardObjective);
			}
		}
	}

	@Override
	public void onScoreboardDisplay(ScoreboardDisplayS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Scoreboard scoreboard = this.world.getScoreboard();
		if (packet.getName().isEmpty()) {
			scoreboard.setObjectiveSlot(packet.getSlot(), null);
		} else {
			ScoreboardObjective scoreboardObjective = scoreboard.getNullableObjective(packet.getName());
			scoreboard.setObjectiveSlot(packet.getSlot(), scoreboardObjective);
		}
	}

	@Override
	public void onTeam(TeamS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Scoreboard scoreboard = this.world.getScoreboard();
		Team team;
		if (packet.getMode() == 0) {
			team = scoreboard.addTeam(packet.getTeamName());
		} else {
			team = scoreboard.getTeam(packet.getTeamName());
		}

		if (packet.getMode() == 0 || packet.getMode() == 2) {
			team.setDisplayName(packet.getDisplayName());
			team.setPrefix(packet.getPlayerPrefix());
			team.setSuffix(packet.getNameTagVisibilityRule());
			team.setFormatting(Formatting.byColorIndex(packet.getFormatting()));
			team.setFriendlyFlagsBitwise(packet.getFlags());
			AbstractTeam.VisibilityRule visibilityRule = AbstractTeam.VisibilityRule.getRuleByName(packet.getVisibilityRule());
			if (visibilityRule != null) {
				team.method_12128(visibilityRule);
			}

			AbstractTeam.CollisionRule collisionRule = AbstractTeam.CollisionRule.method_12132(packet.getCollisionRule());
			if (collisionRule != null) {
				team.method_9353(collisionRule);
			}
		}

		if (packet.getMode() == 0 || packet.getMode() == 3) {
			for (String string : packet.getPlayerList()) {
				scoreboard.addPlayerToTeam(string, packet.getTeamName());
			}
		}

		if (packet.getMode() == 4) {
			for (String string2 : packet.getPlayerList()) {
				scoreboard.removePlayerFromTeam(string2, team);
			}
		}

		if (packet.getMode() == 1) {
			scoreboard.removeTeam(team);
		}
	}

	@Override
	public void onParticle(ParticleS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		if (packet.getCount() == 0) {
			double d = (double)(packet.getSpeed() * packet.getOffsetX());
			double e = (double)(packet.getSpeed() * packet.getOffsetY());
			double f = (double)(packet.getSpeed() * packet.getOffsetZ());

			try {
				this.world.addParticle(packet.getParameters(), packet.isLongDistance(), packet.getX(), packet.getY(), packet.getZ(), d, e, f, packet.getArgs());
			} catch (Throwable var17) {
				LOGGER.warn("Could not spawn particle effect {}", new Object[]{packet.getParameters()});
			}
		} else {
			for (int i = 0; i < packet.getCount(); i++) {
				double g = this.random.nextGaussian() * (double)packet.getOffsetX();
				double h = this.random.nextGaussian() * (double)packet.getOffsetY();
				double j = this.random.nextGaussian() * (double)packet.getOffsetZ();
				double k = this.random.nextGaussian() * (double)packet.getSpeed();
				double l = this.random.nextGaussian() * (double)packet.getSpeed();
				double m = this.random.nextGaussian() * (double)packet.getSpeed();

				try {
					this.world
						.addParticle(packet.getParameters(), packet.isLongDistance(), packet.getX() + g, packet.getY() + h, packet.getZ() + j, k, l, m, packet.getArgs());
				} catch (Throwable var16) {
					LOGGER.warn("Could not spawn particle effect {}", new Object[]{packet.getParameters()});
					return;
				}
			}
		}
	}

	@Override
	public void onEntityAttributes(EntityAttributesS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = this.world.getEntityById(packet.getEntityId());
		if (entity != null) {
			if (!(entity instanceof LivingEntity)) {
				throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + entity + ")");
			} else {
				AbstractEntityAttributeContainer abstractEntityAttributeContainer = ((LivingEntity)entity).getAttributeContainer();

				for (EntityAttributesS2CPacket.Entry entry : packet.getEntries()) {
					EntityAttributeInstance entityAttributeInstance = abstractEntityAttributeContainer.get(entry.getId());
					if (entityAttributeInstance == null) {
						entityAttributeInstance = abstractEntityAttributeContainer.register(
							new ClampedEntityAttribute(null, entry.getId(), 0.0, Double.MIN_NORMAL, Double.MAX_VALUE)
						);
					}

					entityAttributeInstance.setBaseValue(entry.getBaseValue());
					entityAttributeInstance.clearModifiers();

					for (AttributeModifier attributeModifier : entry.getModifiers()) {
						entityAttributeInstance.addModifier(attributeModifier);
					}
				}
			}
		}
	}

	public ClientConnection getClientConnection() {
		return this.connection;
	}

	public Collection<PlayerListEntry> getPlayerList() {
		return this.playerListEntries.values();
	}

	public PlayerListEntry getPlayerListEntry(UUID uuid) {
		return (PlayerListEntry)this.playerListEntries.get(uuid);
	}

	@Nullable
	public PlayerListEntry getPlayerListEntry(String profileName) {
		for (PlayerListEntry playerListEntry : this.playerListEntries.values()) {
			if (playerListEntry.getProfile().getName().equals(profileName)) {
				return playerListEntry;
			}
		}

		return null;
	}

	public GameProfile getProfile() {
		return this.profile;
	}
}
