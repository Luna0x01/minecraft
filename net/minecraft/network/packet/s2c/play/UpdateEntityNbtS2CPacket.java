package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.World;

public class UpdateEntityNbtS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private NbtCompound nbt;

	public UpdateEntityNbtS2CPacket() {
	}

	public UpdateEntityNbtS2CPacket(int i, NbtCompound nbtCompound) {
		this.id = i;
		this.nbt = nbtCompound;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.id = buf.readVarInt();
		this.nbt = buf.readNbtCompound();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.id);
		buf.writeNbtCompound(this.nbt);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onUpdateEntityNbt(this);
	}

	public NbtCompound getNbt() {
		return this.nbt;
	}

	public Entity getEntity(World world) {
		return world.getEntityById(this.id);
	}
}
