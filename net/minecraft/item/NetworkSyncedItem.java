package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.world.World;

public class NetworkSyncedItem extends Item {
	protected NetworkSyncedItem() {
	}

	@Override
	public boolean isNetworkSynced() {
		return true;
	}

	public Packet createSyncPacket(ItemStack stack, World world, PlayerEntity player) {
		return null;
	}
}
