package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import java.util.Collection;
import net.minecraft.class_3082;
import net.minecraft.item.map.MapState;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class MapUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private byte scale;
	private boolean field_13774;
	private class_3082[] field_11618;
	private int startX;
	private int startZ;
	private int width;
	private int height;
	private byte[] colors;

	public MapUpdateS2CPacket() {
	}

	public MapUpdateS2CPacket(int i, byte b, boolean bl, Collection<class_3082> collection, byte[] bs, int j, int k, int l, int m) {
		this.id = i;
		this.scale = b;
		this.field_13774 = bl;
		this.field_11618 = (class_3082[])collection.toArray(new class_3082[collection.size()]);
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
		this.field_13774 = buf.readBoolean();
		this.field_11618 = new class_3082[buf.readVarInt()];

		for (int i = 0; i < this.field_11618.length; i++) {
			class_3082.class_3083 lv = buf.readEnumConstant(class_3082.class_3083.class);
			this.field_11618[i] = new class_3082(lv, buf.readByte(), buf.readByte(), (byte)(buf.readByte() & 15), buf.readBoolean() ? buf.readText() : null);
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
		buf.writeBoolean(this.field_13774);
		buf.writeVarInt(this.field_11618.length);

		for (class_3082 lv : this.field_11618) {
			buf.writeEnumConstant(lv.method_13820());
			buf.writeByte(lv.method_13821());
			buf.writeByte(lv.method_13822());
			buf.writeByte(lv.method_13823() & 15);
			if (lv.method_17923() != null) {
				buf.writeBoolean(true);
				buf.writeText(lv.method_17923());
			} else {
				buf.writeBoolean(false);
			}
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
		state.trackingPosition = this.field_13774;
		state.icons.clear();

		for (int i = 0; i < this.field_11618.length; i++) {
			class_3082 lv = this.field_11618[i];
			state.icons.put("icon-" + i, lv);
		}

		for (int j = 0; j < this.width; j++) {
			for (int k = 0; k < this.height; k++) {
				state.colors[this.startX + j + (this.startZ + k) * 128] = this.colors[j + k * this.width];
			}
		}
	}
}
