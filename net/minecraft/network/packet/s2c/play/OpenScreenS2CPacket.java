package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.Text;
import net.minecraft.util.PacketByteBuf;

public class OpenScreenS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private String name;
	private Text text;
	private int slots;
	private int entityId;

	public OpenScreenS2CPacket() {
	}

	public OpenScreenS2CPacket(int i, String string, Text text) {
		this(i, string, text, 0);
	}

	public OpenScreenS2CPacket(int i, String string, Text text, int j) {
		this.id = i;
		this.name = string;
		this.text = text;
		this.slots = j;
	}

	public OpenScreenS2CPacket(int i, String string, Text text, int j, int k) {
		this(i, string, text, j);
		this.entityId = k;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onOpenScreen(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.id = buf.readUnsignedByte();
		this.name = buf.readString(32);
		this.text = buf.readText();
		this.slots = buf.readUnsignedByte();
		if (this.name.equals("EntityHorse")) {
			this.entityId = buf.readInt();
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeByte(this.id);
		buf.writeString(this.name);
		buf.writeText(this.text);
		buf.writeByte(this.slots);
		if (this.name.equals("EntityHorse")) {
			buf.writeInt(this.entityId);
		}
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public Text getText() {
		return this.text;
	}

	public int getSlotCount() {
		return this.slots;
	}

	public int getEntityId() {
		return this.entityId;
	}

	public boolean hasSlots() {
		return this.slots > 0;
	}
}
