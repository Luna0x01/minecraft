package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class CraftRecipeResponseS2CPacket implements Packet<ClientPlayPacketListener> {
	private int syncId;
	private Identifier field_21547;

	public CraftRecipeResponseS2CPacket() {
	}

	public CraftRecipeResponseS2CPacket(int i, RecipeType recipeType) {
		this.syncId = i;
		this.field_21547 = recipeType.method_16202();
	}

	public Identifier method_14822() {
		return this.field_21547;
	}

	public int getSyncId() {
		return this.syncId;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.syncId = buf.readByte();
		this.field_21547 = buf.readIdentifier();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeByte(this.syncId);
		buf.writeIdentifier(this.field_21547);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onCraftRecipeResponse(this);
	}
}
