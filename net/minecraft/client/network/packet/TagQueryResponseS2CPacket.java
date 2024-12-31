package net.minecraft.client.network.packet;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class TagQueryResponseS2CPacket implements Packet<ClientPlayPacketListener> {
	private int transactionId;
	@Nullable
	private CompoundTag tag;

	public TagQueryResponseS2CPacket() {
	}

	public TagQueryResponseS2CPacket(int i, @Nullable CompoundTag compoundTag) {
		this.transactionId = i;
		this.tag = compoundTag;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.transactionId = packetByteBuf.readVarInt();
		this.tag = packetByteBuf.readCompoundTag();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.transactionId);
		packetByteBuf.writeCompoundTag(this.tag);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onTagQuery(this);
	}

	public int getTransactionId() {
		return this.transactionId;
	}

	@Nullable
	public CompoundTag getTag() {
		return this.tag;
	}

	@Override
	public boolean isErrorFatal() {
		return true;
	}
}
