package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.recipe.RecipeDispatcher;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.PacketByteBuf;

public class CraftRecipeRequestC2SPacket implements Packet<ServerPlayPacketListener> {
	private int syncId;
	private RecipeType recipe;
	private boolean makeAll;

	public CraftRecipeRequestC2SPacket() {
	}

	public CraftRecipeRequestC2SPacket(int i, RecipeType recipeType, boolean bl) {
		this.syncId = i;
		this.recipe = recipeType;
		this.makeAll = bl;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.syncId = buf.readByte();
		this.recipe = RecipeDispatcher.getByRawId(buf.readVarInt());
		this.makeAll = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeByte(this.syncId);
		buf.writeVarInt(RecipeDispatcher.getRawId(this.recipe));
		buf.writeBoolean(this.makeAll);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onCraftRecipeRequest(this);
	}

	public int getSyncId() {
		return this.syncId;
	}

	public RecipeType getRecipe() {
		return this.recipe;
	}

	public boolean shouldMakeAll() {
		return this.makeAll;
	}
}
