package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import java.util.UUID;
import net.minecraft.class_2957;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.Text;
import net.minecraft.util.PacketByteBuf;

public class BossBarS2CPacket implements Packet<ClientPlayPacketListener> {
	private UUID uuid;
	private BossBarS2CPacket.Action action;
	private Text title;
	private float health;
	private class_2957.Color color;
	private class_2957.Division division;
	private boolean field_13749;
	private boolean field_13750;
	private boolean field_13751;

	public BossBarS2CPacket() {
	}

	public BossBarS2CPacket(BossBarS2CPacket.Action action, class_2957 arg) {
		this.action = action;
		this.uuid = arg.getUuid();
		this.title = arg.getTitle();
		this.health = arg.getHealth();
		this.color = arg.getColor();
		this.division = arg.getDivision();
		this.field_13749 = arg.method_12929();
		this.field_13750 = arg.method_12930();
		this.field_13751 = arg.method_12931();
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.uuid = buf.readUuid();
		this.action = buf.readEnumConstant(BossBarS2CPacket.Action.class);
		switch (this.action) {
			case ADD:
				this.title = buf.readText();
				this.health = buf.readFloat();
				this.color = buf.readEnumConstant(class_2957.Color.class);
				this.division = buf.readEnumConstant(class_2957.Division.class);
				this.setFlags(buf.readUnsignedByte());
			case REMOVE:
			default:
				break;
			case UPDATE_PCT:
				this.health = buf.readFloat();
				break;
			case UPDATE_NAME:
				this.title = buf.readText();
				break;
			case UPDATE_STYLE:
				this.color = buf.readEnumConstant(class_2957.Color.class);
				this.division = buf.readEnumConstant(class_2957.Division.class);
				break;
			case UPDATE_PROPERTIES:
				this.setFlags(buf.readUnsignedByte());
		}
	}

	private void setFlags(int flags) {
		this.field_13749 = (flags & 1) > 0;
		this.field_13750 = (flags & 2) > 0;
		this.field_13751 = (flags & 2) > 0;
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeUuid(this.uuid);
		buf.writeEnumConstant(this.action);
		switch (this.action) {
			case ADD:
				buf.writeText(this.title);
				buf.writeFloat(this.health);
				buf.writeEnumConstant(this.color);
				buf.writeEnumConstant(this.division);
				buf.writeByte(this.method_12642());
			case REMOVE:
			default:
				break;
			case UPDATE_PCT:
				buf.writeFloat(this.health);
				break;
			case UPDATE_NAME:
				buf.writeText(this.title);
				break;
			case UPDATE_STYLE:
				buf.writeEnumConstant(this.color);
				buf.writeEnumConstant(this.division);
				break;
			case UPDATE_PROPERTIES:
				buf.writeByte(this.method_12642());
		}
	}

	private int method_12642() {
		int i = 0;
		if (this.field_13749) {
			i |= 1;
		}

		if (this.field_13750) {
			i |= 2;
		}

		if (this.field_13751) {
			i |= 2;
		}

		return i;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onBossBar(this);
	}

	public UUID getUuid() {
		return this.uuid;
	}

	public BossBarS2CPacket.Action getAction() {
		return this.action;
	}

	public Text getTitle() {
		return this.title;
	}

	public float getHealth() {
		return this.health;
	}

	public class_2957.Color getColor() {
		return this.color;
	}

	public class_2957.Division getDivision() {
		return this.division;
	}

	public boolean method_12639() {
		return this.field_13749;
	}

	public boolean method_12640() {
		return this.field_13750;
	}

	public boolean method_12641() {
		return this.field_13751;
	}

	public static enum Action {
		ADD,
		REMOVE,
		UPDATE_PCT,
		UPDATE_NAME,
		UPDATE_STYLE,
		UPDATE_PROPERTIES;
	}
}
