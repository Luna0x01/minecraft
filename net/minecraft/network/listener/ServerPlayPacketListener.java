package net.minecraft.network.listener;

import net.minecraft.network.packet.c2s.play.AdvancementTabC2SPacket;
import net.minecraft.network.packet.c2s.play.ButtonClickC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickWindowC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientSettingsC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.ConfirmGuiActionC2SPacket;
import net.minecraft.network.packet.c2s.play.CraftRecipeRequestC2SPacket;
import net.minecraft.network.packet.c2s.play.CraftingBlockData;
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

public interface ServerPlayPacketListener extends PacketListener {
	void onHandSwing(HandSwingC2SPacket packet);

	void onChatMessage(ChatMessageC2SPacket packet);

	void onRequestCommandCompletions(RequestCommandCompletionsC2SPacket packet);

	void onClientStatus(ClientStatusC2SPacket packet);

	void onClientSettings(ClientSettingsC2SPacket packet);

	void onConfirmTransaction(ConfirmGuiActionC2SPacket packet);

	void onButtonClick(ButtonClickC2SPacket packet);

	void onClickWindow(ClickWindowC2SPacket packet);

	void onCraftRecipeRequest(CraftRecipeRequestC2SPacket packet);

	void onGuiClose(GuiCloseC2SPacket packet);

	void onCustomPayload(CustomPayloadC2SPacket packet);

	void onPlayerInteractEntity(PlayerInteractEntityC2SPacket packet);

	void onKeepAlive(KeepAliveC2SPacket packet);

	void onPlayerMove(PlayerMoveC2SPacket packet);

	void onPlayerAbilities(UpdatePlayerAbilitiesC2SPacket packet);

	void onPlayerAction(PlayerActionC2SPacket packet);

	void onClientCommand(ClientCommandC2SPacket packet);

	void onPlayerInput(PlayerInputC2SPacket packet);

	void onUpdateSelectedSlot(UpdateSelectedSlotC2SPacket packet);

	void onCreativeInventoryAction(CreativeInventoryActionC2SPacket packet);

	void onSignUpdate(UpdateSignC2SPacket packet);

	void onPlayerInteractBlock(PlayerInteractBlockC2SPacket packet);

	void onSwingHand(SwingHandC2SPacket packet);

	void onSpectatorTeleport(SpectatorTeleportC2SPacket packet);

	void onResourcePackStatus(ResourcePackStatusC2SPacket packet);

	void onSteerBoat(SteerBoatC2SPacket packet);

	void onVehicleMove(VehicleMoveC2SPacket packet);

	void onTeleportConfirm(TeleportConfirmC2SPacket packet);

	void onCraftingBlockData(CraftingBlockData packet);

	void onAdvancementTab(AdvancementTabC2SPacket packet);
}
