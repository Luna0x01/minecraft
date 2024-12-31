package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import java.util.Collection;
import net.minecraft.item.map.MapIcon;
import net.minecraft.item.map.MapState;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class MapUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private byte scale;
	private MapIcon[] icons;
	private int startX;
	private int startZ;
	private int width;
	private int height;
	private byte[] colors;

	public MapUpdateS2CPacket() {
	}

	public MapUpdateS2CPacket(int i, byte b, Collection<MapIcon> collection, byte[] bs, int j, int k, int l, int m) {
		this.id = i;
		this.scale = b;
		this.icons = (MapIcon[])collection.toArray(new MapIcon[collection.size()]);
		this.startX = j;
		this.startZ = k;
		this.width = l;
		this.height = m;
		this.colors = new byte[l * m];

		for (int n = 0; n < l; n++) {
			for (int o = 0; o < m; o++) {
				this.colors[n + o * l] = bs[j + n + (k + o) * 128];
			}
		}
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.id = buf.readVarInt();
		this.scale = buf.readByte();
		this.icons = new MapIcon[buf.readVarInt()];

		for (int i = 0; i < this.icons.length; i++) {
			short s = (short)buf.readByte();
			this.icons[i] = new MapIcon((byte)(s >> 4 & 15), buf.readByte(), buf.readByte(), (byte)(s & 15));
		}

		this.width = buf.readUnsignedByte();
		if (this.width > 0) {
			this.height = buf.readUnsignedByte();
			this.startX = buf.readUnsignedByte();
			this.startZ = buf.readUnsignedByte();
			this.colors = buf.readByteArray();
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.id);
		buf.writeByte(this.scale);
		buf.writeVarInt(this.icons.length);

		for (MapIcon mapIcon : this.icons) {
			buf.writeByte((mapIcon.getTypeId() & 15) << 4 | mapIcon.getRotation() & 15);
			buf.writeByte(mapIcon.getX());
			buf.writeByte(mapIcon.getY());
		}

		buf.writeByte(this.width);
		if (this.width > 0) {
			buf.writeByte(this.height);
			buf.writeByte(this.startX);
			buf.writeByte(this.startZ);
			buf.writeByteArray(this.colors);
		}
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onMapUpdate(this);
	}

	public int getId() {
		return this.id;
	}

	public void apply(MapState state) {
		state.scale = this.scale;
		state.icons.clear();

		for (int i = 0; i < this.icons.length; i++) {
			MapIcon mapIcon = this.icons[i];
			state.icons.put("icon-" + i, mapIcon);
		}

		for (int j = 0; j < this.width; j++) {
			for (int k = 0; k < this.height; k++) {
				state.colors[this.startX + j + (this.startZ + k) * 128] = this.colors[j + k * this.width];
			}
		}
	}
}
