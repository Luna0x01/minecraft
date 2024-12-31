package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.Text;
import net.minecraft.util.PacketByteBuf;

public class TitleS2CPacket implements Packet<ClientPlayPacketListener> {
	private TitleS2CPacket.Action action;
	private Text title;
	private int fadeInTicks;
	private int stayTicks;
	private int fadeOutTicks;

	public TitleS2CPacket() {
	}

	public TitleS2CPacket(TitleS2CPacket.Action action, Text text) {
		this(action, text, -1, -1, -1);
	}

	public TitleS2CPacket(int i, int j, int k) {
		this(TitleS2CPacket.Action.TIMES, null, i, j, k);
	}

	public TitleS2CPacket(TitleS2CPacket.Action action, @Nullable Text text, int i, int j, int k) {
		this.action = action;
		this.title = text;
		this.fadeInTicks = i;
		this.stayTicks = j;
		this.fadeOutTicks = k;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.action = buf.readEnumConstant(TitleS2CPacket.Action.class);
		if (this.action == TitleS2CPacket.Action.TITLE || this.action == TitleS2CPacket.Action.SUBTITLE || this.action == TitleS2CPacket.Action.ACTIONBAR) {
			this.title = buf.readText();
		}

		if (this.action == TitleS2CPacket.Action.TIMES) {
			this.fadeInTicks = buf.readInt();
			this.stayTicks = buf.readInt();
			this.fadeOutTicks = buf.readInt();
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeEnumConstant(this.action);
		if (this.action == TitleS2CPacket.Action.TITLE || this.action == TitleS2CPacket.Action.SUBTITLE || this.action == TitleS2CPacket.Action.ACTIONBAR) {
			buf.writeText(this.title);
		}

		if (this.action == TitleS2CPacket.Action.TIMES) {
			buf.writeInt(this.fadeInTicks);
			buf.writeInt(this.stayTicks);
			buf.writeInt(this.fadeOutTicks);
		}
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onTitle(this);
	}

	public TitleS2CPacket.Action getAction() {
		return this.action;
	}

	public Text getText() {
		return this.title;
	}

	public int getFadeInTicks() {
		return this.fadeInTicks;
	}

	public int getStayTicks() {
		return this.stayTicks;
	}

	public int getFadeOutTicks() {
		return this.fadeOutTicks;
	}

	public static enum Action {
		TITLE,
		SUBTITLE,
		ACTIONBAR,
		TIMES,
		CLEAR,
		RESET;

		public static TitleS2CPacket.Action fromName(String name) {
			for (TitleS2CPacket.Action action : values()) {
				if (action.name().equalsIgnoreCase(name)) {
					return action;
				}
			}

			return TITLE;
		}

		public static String[] getNames() {
			String[] strings = new String[values().length];
			int i = 0;

			for (TitleS2CPacket.Action action : values()) {
				strings[i++] = action.name().toLowerCase(Locale.ROOT);
			}

			return strings;
		}
	}
}
