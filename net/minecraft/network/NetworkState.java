package net.minecraft.network;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginKeyC2SPacket;
import net.minecraft.network.packet.c2s.play.ButtonClickC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickWindowC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientSettingsC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.ConfirmGuiActionC2SPacket;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.c2s.play.GuiCloseC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.c2s.play.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.SpectatorTeleportC2SPacket;
import net.minecraft.network.packet.c2s.play.SteerBoatC2SPacket;
import net.minecraft.network.packet.c2s.play.SwingHandC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdatePlayerAbilitiesC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginCompressionS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginHelloS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;
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
import net.minecraft.network.packet.s2c.query.QueryPongS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import org.apache.logging.log4j.LogManager;

public enum NetworkState {
	HANDSHAKING(-1) {
		{
			this.register(NetworkSide.SERVERBOUND, HandshakeC2SPacket.class);
		}
	},
	PLAY(0) {
		{
			this.register(NetworkSide.CLIENTBOUND, EntitySpawnS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, ExperienceOrbSpawnS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, EntitySpawnGlobalS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, MobSpawnS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, PaintingSpawnS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, PlayerSpawnS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, EntityAnimationS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, StatsUpdateS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, BlockBreakingProgressS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, BlockEntityUpdateS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, BlockActionS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, BlockUpdateS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, BossBarS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, DifficultyS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, CommandSuggestionsS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, ChatMessageS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, ChunkDeltaUpdateS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, ConfirmGuiActionS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, CloseScreenS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, OpenScreenS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, InventoryS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, ScreenHandlerPropertyUpdateS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, ScreenHandlerSlotUpdateS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, ChunkLoadDistanceS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, CustomPayloadS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, PlaySoundNameS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, DisconnectS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, EntityStatusS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, ExplosionS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, ChunkUnloadS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, GameStateChangeS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, KeepAliveS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, ChunkDataS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, WorldEventS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, ParticleS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, GameJoinS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, MapUpdateS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, EntityS2CPacket.MoveRelative.class);
			this.register(NetworkSide.CLIENTBOUND, EntityS2CPacket.RotateAndMoveRelative.class);
			this.register(NetworkSide.CLIENTBOUND, EntityS2CPacket.Rotate.class);
			this.register(NetworkSide.CLIENTBOUND, EntityS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, VehicleMoveS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, SignEditorOpenS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, PlayerAbilitiesS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, CombatEventS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, PlayerListS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, PlayerPositionLookS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, BedSleepS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, EntitiesDestroyS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, RemoveEntityStatusEffectS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, ResourcePackSendS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, PlayerRespawnS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, EntitySetHeadYawS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, WorldBorderS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, SetCameraEntityS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, HeldItemChangeS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, ScoreboardDisplayS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, EntityTrackerUpdateS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, EntityAttachS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, EntityVelocityUpdateS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, EntityEquipmentUpdateS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, ExperienceBarUpdateS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, HealthUpdateS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, ScoreboardObjectiveUpdateS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, SetPassengersS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, TeamS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, ScoreboardPlayerUpdateS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, PlayerSpawnPositionS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, WorldTimeUpdateS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, TitleS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, PlaySoundIdS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, PlayerListHeaderS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, ChunkRenderDistanceCenterS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, EntityPositionS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, EntityAttributesS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, EntityStatusEffectS2CPacket.class);
			this.register(NetworkSide.SERVERBOUND, TeleportConfirmC2SPacket.class);
			this.register(NetworkSide.SERVERBOUND, RequestCommandCompletionsC2SPacket.class);
			this.register(NetworkSide.SERVERBOUND, ChatMessageC2SPacket.class);
			this.register(NetworkSide.SERVERBOUND, ClientStatusC2SPacket.class);
			this.register(NetworkSide.SERVERBOUND, ClientSettingsC2SPacket.class);
			this.register(NetworkSide.SERVERBOUND, ConfirmGuiActionC2SPacket.class);
			this.register(NetworkSide.SERVERBOUND, ButtonClickC2SPacket.class);
			this.register(NetworkSide.SERVERBOUND, ClickWindowC2SPacket.class);
			this.register(NetworkSide.SERVERBOUND, GuiCloseC2SPacket.class);
			this.register(NetworkSide.SERVERBOUND, CustomPayloadC2SPacket.class);
			this.register(NetworkSide.SERVERBOUND, PlayerInteractEntityC2SPacket.class);
			this.register(NetworkSide.SERVERBOUND, KeepAliveC2SPacket.class);
			this.register(NetworkSide.SERVERBOUND, PlayerMoveC2SPacket.PositionOnly.class);
			this.register(NetworkSide.SERVERBOUND, PlayerMoveC2SPacket.Both.class);
			this.register(NetworkSide.SERVERBOUND, PlayerMoveC2SPacket.LookOnly.class);
			this.register(NetworkSide.SERVERBOUND, PlayerMoveC2SPacket.class);
			this.register(NetworkSide.SERVERBOUND, VehicleMoveC2SPacket.class);
			this.register(NetworkSide.SERVERBOUND, SteerBoatC2SPacket.class);
			this.register(NetworkSide.SERVERBOUND, UpdatePlayerAbilitiesC2SPacket.class);
			this.register(NetworkSide.SERVERBOUND, PlayerActionC2SPacket.class);
			this.register(NetworkSide.SERVERBOUND, ClientCommandC2SPacket.class);
			this.register(NetworkSide.SERVERBOUND, PlayerInputC2SPacket.class);
			this.register(NetworkSide.SERVERBOUND, ResourcePackStatusC2SPacket.class);
			this.register(NetworkSide.SERVERBOUND, UpdateSelectedSlotC2SPacket.class);
			this.register(NetworkSide.SERVERBOUND, CreativeInventoryActionC2SPacket.class);
			this.register(NetworkSide.SERVERBOUND, UpdateSignC2SPacket.class);
			this.register(NetworkSide.SERVERBOUND, HandSwingC2SPacket.class);
			this.register(NetworkSide.SERVERBOUND, SpectatorTeleportC2SPacket.class);
			this.register(NetworkSide.SERVERBOUND, PlayerInteractBlockC2SPacket.class);
			this.register(NetworkSide.SERVERBOUND, SwingHandC2SPacket.class);
		}
	},
	STATUS(1) {
		{
			this.register(NetworkSide.SERVERBOUND, QueryRequestC2SPacket.class);
			this.register(NetworkSide.CLIENTBOUND, QueryResponseS2CPacket.class);
			this.register(NetworkSide.SERVERBOUND, QueryPingC2SPacket.class);
			this.register(NetworkSide.CLIENTBOUND, QueryPongS2CPacket.class);
		}
	},
	LOGIN(2) {
		{
			this.register(NetworkSide.CLIENTBOUND, LoginDisconnectS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, LoginHelloS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, LoginSuccessS2CPacket.class);
			this.register(NetworkSide.CLIENTBOUND, LoginCompressionS2CPacket.class);
			this.register(NetworkSide.SERVERBOUND, LoginHelloC2SPacket.class);
			this.register(NetworkSide.SERVERBOUND, LoginKeyC2SPacket.class);
		}
	};

	private static final NetworkState[] networkStates = new NetworkState[4];
	private static final Map<Class<? extends Packet<?>>, NetworkState> PACKET_TO_STATE = Maps.newHashMap();
	private final int stateId;
	private final Map<NetworkSide, BiMap<Integer, Class<? extends Packet<?>>>> packetClasses = Maps.newEnumMap(NetworkSide.class);

	private NetworkState(int j) {
		this.stateId = j;
	}

	protected NetworkState register(NetworkSide side, Class<? extends Packet<?>> packetClass) {
		BiMap<Integer, Class<? extends Packet<?>>> biMap = (BiMap<Integer, Class<? extends Packet<?>>>)this.packetClasses.get(side);
		if (biMap == null) {
			biMap = HashBiMap.create();
			this.packetClasses.put(side, biMap);
		}

		if (biMap.containsValue(packetClass)) {
			String string = side + " packet " + packetClass + " is already known to ID " + biMap.inverse().get(packetClass);
			LogManager.getLogger().fatal(string);
			throw new IllegalArgumentException(string);
		} else {
			biMap.put(biMap.size(), packetClass);
			return this;
		}
	}

	public Integer getRawId(NetworkSide side, Packet<?> packet) {
		return (Integer)((BiMap)this.packetClasses.get(side)).inverse().get(packet.getClass());
	}

	@Nullable
	public Packet<?> createPacket(NetworkSide side, int id) throws IllegalAccessException, InstantiationException {
		Class<? extends Packet<?>> class_ = (Class<? extends Packet<?>>)((BiMap)this.packetClasses.get(side)).get(id);
		return class_ == null ? null : (Packet)class_.newInstance();
	}

	public int getId() {
		return this.stateId;
	}

	public static NetworkState byId(int id) {
		return id >= -1 && id <= 2 ? networkStates[id - -1] : null;
	}

	public static NetworkState getPacketHandlerState(Packet<?> handler) {
		return (NetworkState)PACKET_TO_STATE.get(handler.getClass());
	}

	static {
		for (NetworkState networkState : values()) {
			int i = networkState.getId();
			if (i < -1 || i > 2) {
				throw new Error("Invalid protocol ID " + Integer.toString(i));
			}

			networkStates[i - -1] = networkState;

			for (NetworkSide networkSide : networkState.packetClasses.keySet()) {
				for (Class<? extends Packet<?>> class_ : ((BiMap)networkState.packetClasses.get(networkSide)).values()) {
					if (PACKET_TO_STATE.containsKey(class_) && PACKET_TO_STATE.get(class_) != networkState) {
						throw new Error("Packet " + class_ + " is already assigned to protocol " + PACKET_TO_STATE.get(class_) + " - can't reassign to " + networkState);
					}

					try {
						class_.newInstance();
					} catch (Throwable var10) {
						throw new Error("Packet " + class_ + " fails instantiation checks! " + class_);
					}

					PACKET_TO_STATE.put(class_, networkState);
				}
			}
		}
	}
}
