package net.minecraft;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.network.packet.s2c.play.ChunkLoadDistanceS2CPacket;

public class class_2690 extends class_2686 {
	private final ServerPlayerEntity player;

	public class_2690(ServerPlayerEntity serverPlayerEntity) {
		this.player = serverPlayerEntity;
	}

	@Override
	protected void method_11386(Item item, int i) {
		super.method_11386(item, i);
		this.player.networkHandler.sendPacket(new ChunkLoadDistanceS2CPacket(item, i));
	}

	@Override
	protected void method_11387(Item item) {
		super.method_11387(item);
		this.player.networkHandler.sendPacket(new ChunkLoadDistanceS2CPacket(item, 0));
	}
}
