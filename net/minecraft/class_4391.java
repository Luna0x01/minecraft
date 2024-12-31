package net.minecraft;

import java.io.IOException;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class class_4391 implements Packet<ServerPlayPacketListener> {
	private BlockPos field_21618;
	private String field_21619;
	private boolean field_21620;
	private boolean field_21621;
	private boolean field_21622;
	private CommandBlockBlockEntity.class_2736 field_21623;

	public class_4391() {
	}

	public class_4391(BlockPos blockPos, String string, CommandBlockBlockEntity.class_2736 arg, boolean bl, boolean bl2, boolean bl3) {
		this.field_21618 = blockPos;
		this.field_21619 = string;
		this.field_21620 = bl;
		this.field_21621 = bl2;
		this.field_21622 = bl3;
		this.field_21623 = arg;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.field_21618 = buf.readBlockPos();
		this.field_21619 = buf.readString(32767);
		this.field_21623 = buf.readEnumConstant(CommandBlockBlockEntity.class_2736.class);
		int i = buf.readByte();
		this.field_21620 = (i & 1) != 0;
		this.field_21621 = (i & 2) != 0;
		this.field_21622 = (i & 4) != 0;
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeBlockPos(this.field_21618);
		buf.writeString(this.field_21619);
		buf.writeEnumConstant(this.field_21623);
		int i = 0;
		if (this.field_21620) {
			i |= 1;
		}

		if (this.field_21621) {
			i |= 2;
		}

		if (this.field_21622) {
			i |= 4;
		}

		buf.writeByte(i);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_20283(this);
	}

	public BlockPos method_20358() {
		return this.field_21618;
	}

	public String method_20359() {
		return this.field_21619;
	}

	public boolean method_20360() {
		return this.field_21620;
	}

	public boolean method_20361() {
		return this.field_21621;
	}

	public boolean method_20362() {
		return this.field_21622;
	}

	public CommandBlockBlockEntity.class_2736 method_20363() {
		return this.field_21623;
	}
}
