package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.advancement.SimpleAdvancement;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class AdvancementTabC2SPacket implements Packet<ServerPlayPacketListener> {
	private AdvancementTabC2SPacket.Action action;
	private Identifier identifier;

	public AdvancementTabC2SPacket() {
	}

	public AdvancementTabC2SPacket(AdvancementTabC2SPacket.Action action, @Nullable Identifier identifier) {
		this.action = action;
		this.identifier = identifier;
	}

	public static AdvancementTabC2SPacket openedTab(SimpleAdvancement advancement) {
		return new AdvancementTabC2SPacket(AdvancementTabC2SPacket.Action.OPENED_TAB, advancement.getIdentifier());
	}

	public static AdvancementTabC2SPacket closeScreen() {
		return new AdvancementTabC2SPacket(AdvancementTabC2SPacket.Action.CLOSED_SCREEN, null);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.action = buf.readEnumConstant(AdvancementTabC2SPacket.Action.class);
		if (this.action == AdvancementTabC2SPacket.Action.OPENED_TAB) {
			this.identifier = buf.readIdentifier();
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeEnumConstant(this.action);
		if (this.action == AdvancementTabC2SPacket.Action.OPENED_TAB) {
			buf.writeIdentifier(this.identifier);
		}
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onAdvancementTab(this);
	}

	public AdvancementTabC2SPacket.Action getAction() {
		return this.action;
	}

	public Identifier getIdentifier() {
		return this.identifier;
	}

	public static enum Action {
		OPENED_TAB,
		CLOSED_SCREEN;
	}
}
