package net.minecraft.network.listener;

import net.minecraft.class_4376;
import net.minecraft.class_4379;
import net.minecraft.class_4380;
import net.minecraft.class_4381;
import net.minecraft.class_4382;
import net.minecraft.class_4383;
import net.minecraft.network.packet.c2s.play.AdvancementUpdatePacket;
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
import net.minecraft.network.packet.s2c.play.CraftRecipeResponseS2CPacket;
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
import net.minecraft.network.packet.s2c.play.RecipesUnlockS2CPacket;
import net.minecraft.network.packet.s2c.play.RemoveEntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.ResourcePackSendS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardDisplayS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardObjectiveUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardPlayerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerPropertyUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.SelectAdvancementTabS2CPacket;
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

public interface ClientPlayPacketListener extends PacketListener {
	void onEntitySpawn(EntitySpawnS2CPacket packet);

	void onExperienceOrbSpawn(ExperienceOrbSpawnS2CPacket packet);

	void onEntitySpawnGlobal(EntitySpawnGlobalS2CPacket packet);

	void onMobSpawn(MobSpawnS2CPacket packet);

	void onScoreboardObjectiveUpdate(ScoreboardObjectiveUpdateS2CPacket packet);

	void onPaintingSpawn(PaintingSpawnS2CPacket packet);

	void onPlayerSpawn(PlayerSpawnS2CPacket packet);

	void onEntityAnimation(EntityAnimationS2CPacket packet);

	void onStatsUpdate(StatsUpdateS2CPacket packet);

	void onRecipesUnlock(RecipesUnlockS2CPacket packet);

	void onBlockDestroyProgress(BlockBreakingProgressS2CPacket packet);

	void onSignEditorOpen(SignEditorOpenS2CPacket packet);

	void onBlockEntityUpdate(BlockEntityUpdateS2CPacket packet);

	void onBlockAction(BlockActionS2CPacket packet);

	void onBlockUpdate(BlockUpdateS2CPacket packet);

	void onChatMessage(ChatMessageS2CPacket packet);

	void onChunkDeltaUpdate(ChunkDeltaUpdateS2CPacket packet);

	void onMapUpdate(MapUpdateS2CPacket packet);

	void onGuiActionConfirm(ConfirmGuiActionS2CPacket packet);

	void onCloseScreen(CloseScreenS2CPacket packet);

	void onInventory(InventoryS2CPacket packet);

	void onOpenScreen(OpenScreenS2CPacket packet);

	void onScreenHandlerPropertyUpdate(ScreenHandlerPropertyUpdateS2CPacket packet);

	void onScreenHandlerSlotUpdate(ScreenHandlerSlotUpdateS2CPacket packet);

	void onCustomPayload(CustomPayloadS2CPacket packet);

	void onDisconnect(DisconnectS2CPacket packet);

	void onBedSleep(BedSleepS2CPacket packet);

	void onEntityStatus(EntityStatusS2CPacket packet);

	void onEntityAttach(EntityAttachS2CPacket packet);

	void onSetPassengers(SetPassengersS2CPacket packet);

	void onExplosion(ExplosionS2CPacket packet);

	void onGameStateChange(GameStateChangeS2CPacket packet);

	void onKeepAlive(KeepAliveS2CPacket packet);

	void onChunkData(ChunkDataS2CPacket packet);

	void onUnloadChunk(ChunkUnloadS2CPacket packet);

	void onWorldEvent(WorldEventS2CPacket packet);

	void onGameJoin(GameJoinS2CPacket packet);

	void onEntityUpdate(EntityS2CPacket packet);

	void onPlayerPositionLook(PlayerPositionLookS2CPacket packet);

	void onParticle(ParticleS2CPacket packet);

	void onPlayerAbilities(PlayerAbilitiesS2CPacket packet);

	void onPlayerList(PlayerListS2CPacket packet);

	void onEntitiesDestroy(EntitiesDestroyS2CPacket packet);

	void onRemoveEntityEffect(RemoveEntityStatusEffectS2CPacket packet);

	void onPlayerRespawn(PlayerRespawnS2CPacket packet);

	void onEntitySetHeadYaw(EntitySetHeadYawS2CPacket packet);

	void onHeldItemChange(HeldItemChangeS2CPacket packet);

	void onScoreboardDisplay(ScoreboardDisplayS2CPacket packet);

	void onEntityTrackerUpdate(EntityTrackerUpdateS2CPacket packet);

	void onVelocityUpdate(EntityVelocityUpdateS2CPacket packet);

	void onEquipmentUpdate(EntityEquipmentUpdateS2CPacket packet);

	void onHealthUpdate(ExperienceBarUpdateS2CPacket packet);

	void onExperienceBarUpdate(HealthUpdateS2CPacket packet);

	void onTeam(TeamS2CPacket packet);

	void onScoreboardPlayerUpdate(ScoreboardPlayerUpdateS2CPacket packet);

	void onPlayerSpawnPosition(PlayerSpawnPositionS2CPacket packet);

	void onWorldTimeUpdate(WorldTimeUpdateS2CPacket packet);

	void onPlaySound(PlaySoundIdS2CPacket packet);

	void onPlaySoundName(PlaySoundNameS2CPacket packet);

	void onChunkRenderDistanceCenter(ChunkRenderDistanceCenterS2CPacket packet);

	void onEntityPosition(EntityPositionS2CPacket packet);

	void onEntityAttributes(EntityAttributesS2CPacket packet);

	void onEntityPotionEffect(EntityStatusEffectS2CPacket packet);

	void method_20204(class_4383 arg);

	void onCombatEvent(CombatEventS2CPacket packet);

	void onDifficulty(DifficultyS2CPacket packet);

	void onSetCameraEntity(SetCameraEntityS2CPacket packet);

	void onWorldBorder(WorldBorderS2CPacket packet);

	void onTitle(TitleS2CPacket packet);

	void onPlayerListHeader(PlayerListHeaderS2CPacket packet);

	void onResourcePackSend(ResourcePackSendS2CPacket packet);

	void onBossBar(BossBarS2CPacket packet);

	void onChunkLoadDistance(ChunkLoadDistanceS2CPacket packet);

	void onVehicleMove(VehicleMoveS2CPacket packet);

	void onAdvancementsUpdate(AdvancementUpdatePacket packet);

	void onSelectAdvancementTab(SelectAdvancementTabS2CPacket packet);

	void onCraftRecipeResponse(CraftRecipeResponseS2CPacket packet);

	void method_20199(class_4376 arg);

	void method_20201(class_4380 arg);

	void onCommandSuggestions(CommandSuggestionsS2CPacket packet);

	void method_20203(class_4382 arg);

	void method_20200(class_4379 arg);

	void method_20202(class_4381 arg);
}
