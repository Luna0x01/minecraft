package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.recipe.RecipeDispatcher;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.PacketByteBuf;

public class CraftRecipeResponseS2CPacket implements Packet<ClientPlayPacketListener> {
	private int syncId;
	private RecipeType recipe;

	public CraftRecipeResponseS2CPacket() {
	}

	public CraftRecipeResponseS2CPacket(int i, RecipeType recipeType) {
		this.syncId = i;
		this.recipe = recipeType;
	}

	public RecipeType getRecipe() {
		return this.recipe;
	}

	public int getSyncId() {
		return this.syncId;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.syncId = buf.readByte();
		this.recipe = RecipeDispatcher.getByRawId(buf.readVarInt());
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeByte(this.syncId);
		buf.writeVarInt(RecipeDispatcher.getRawId(this.recipe));
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onCraftRecipeResponse(this);
	}
}
