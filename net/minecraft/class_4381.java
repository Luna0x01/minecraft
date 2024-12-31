package net.minecraft;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class class_4381 implements Packet<ClientPlayPacketListener> {
	private int field_21572;
	@Nullable
	private NbtCompound field_21573;

	public class_4381() {
	}

	public class_4381(int i, @Nullable NbtCompound nbtCompound) {
		this.field_21572 = i;
		this.field_21573 = nbtCompound;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.field_21572 = buf.readVarInt();
		this.field_21573 = buf.readNbtCompound();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.field_21572);
		buf.writeNbtCompound(this.field_21573);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_20202(this);
	}

	public int method_20268() {
		return this.field_21572;
	}

	@Nullable
	public NbtCompound method_20269() {
		return this.field_21573;
	}

	@Override
	public boolean method_20197() {
		return true;
	}
}
