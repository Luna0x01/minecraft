package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class ClickWindowC2SPacket implements Packet<ServerPlayPacketListener> {
	private int syncId;
	private int slot;
	private int button;
	private short transactionId;
	private ItemStack selectedStack;
	private int actionType;

	public ClickWindowC2SPacket() {
	}

	public ClickWindowC2SPacket(int i, int j, int k, int l, ItemStack itemStack, short s) {
		this.syncId = i;
		this.slot = j;
		this.button = k;
		this.selectedStack = itemStack != null ? itemStack.copy() : null;
		this.transactionId = s;
		this.actionType = l;
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onClickWindow(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.syncId = buf.readByte();
		this.slot = buf.readShort();
		this.button = buf.readByte();
		this.transactionId = buf.readShort();
		this.actionType = buf.readByte();
		this.selectedStack = buf.readItemStack();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeByte(this.syncId);
		buf.writeShort(this.slot);
		buf.writeByte(this.button);
		buf.writeShort(this.transactionId);
		buf.writeByte(this.actionType);
		buf.writeItemStack(this.selectedStack);
	}

	public int getSyncId() {
		return this.syncId;
	}

	public int getSlot() {
		return this.slot;
	}

	public int getButton() {
		return this.button;
	}

	public short getTransactionId() {
		return this.transactionId;
	}

	public ItemStack getSelectedStack() {
		return this.selectedStack;
	}

	public int getActionType() {
		return this.actionType;
	}
}
