package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class CraftRecipeRequestC2SPacket implements Packet<ServerPlayPacketListener> {
	private int syncId;
	private Identifier field_21587;
	private boolean makeAll;

	public CraftRecipeRequestC2SPacket() {
	}

	public CraftRecipeRequestC2SPacket(int i, RecipeType recipeType, boolean bl) {
		this.syncId = i;
		this.field_21587 = recipeType.method_16202();
		this.makeAll = bl;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.syncId = buf.readByte();
		this.field_21587 = buf.readIdentifier();
		this.makeAll = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeByte(this.syncId);
		buf.writeIdentifier(this.field_21587);
		buf.writeBoolean(this.makeAll);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onCraftRecipeRequest(this);
	}

	public int getSyncId() {
		return this.syncId;
	}

	public Identifier method_14863() {
		return this.field_21587;
	}

	public boolean shouldMakeAll() {
		return this.makeAll;
	}
}
