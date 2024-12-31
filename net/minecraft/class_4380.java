package net.minecraft;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class class_4380 implements Packet<ClientPlayPacketListener> {
	private Identifier field_21570;
	private SoundCategory field_21571;

	public class_4380() {
	}

	public class_4380(@Nullable Identifier identifier, @Nullable SoundCategory soundCategory) {
		this.field_21570 = identifier;
		this.field_21571 = soundCategory;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		int i = buf.readByte();
		if ((i & 1) > 0) {
			this.field_21571 = buf.readEnumConstant(SoundCategory.class);
		}

		if ((i & 2) > 0) {
			this.field_21570 = buf.readIdentifier();
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		if (this.field_21571 != null) {
			if (this.field_21570 != null) {
				buf.writeByte(3);
				buf.writeEnumConstant(this.field_21571);
				buf.writeIdentifier(this.field_21570);
			} else {
				buf.writeByte(1);
				buf.writeEnumConstant(this.field_21571);
			}
		} else if (this.field_21570 != null) {
			buf.writeByte(2);
			buf.writeIdentifier(this.field_21570);
		} else {
			buf.writeByte(0);
		}
	}

	@Nullable
	public Identifier method_20265() {
		return this.field_21570;
	}

	@Nullable
	public SoundCategory method_20266() {
		return this.field_21571;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_20201(this);
	}
}
