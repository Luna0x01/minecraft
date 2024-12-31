package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class ScreenHandlerSlotUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private int syncId;
	private int slot;
	private ItemStack stack;

	public ScreenHandlerSlotUpdateS2CPacket() {
	}

	public ScreenHandlerSlotUpdateS2CPacket(int i, int j, @Nullable ItemStack itemStack) {
		this.syncId = i;
		this.slot = j;
		this.stack = itemStack == null ? null : itemStack.copy();
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onScreenHandlerSlotUpdate(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.syncId = buf.readByte();
		this.slot = buf.readShort();
		this.stack = buf.readItemStack();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeByte(this.syncId);
		buf.writeShort(this.slot);
		buf.writeItemStack(this.stack);
	}

	public int getSyncId() {
		return this.syncId;
	}

	public int getSlot() {
		return this.slot;
	}

	@Nullable
	public ItemStack getItemStack() {
		return this.stack;
	}
}
