package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.recipe.RecipeDispatcher;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.PacketByteBuf;

public class CraftingBlockData implements Packet<ServerPlayPacketListener> {
	private CraftingBlockData.Type type;
	private RecipeType recipeType;
	private boolean bookOpen;
	private boolean filterActive;

	public CraftingBlockData() {
	}

	public CraftingBlockData(RecipeType recipeType) {
		this.type = CraftingBlockData.Type.SHOWN;
		this.recipeType = recipeType;
	}

	public CraftingBlockData(boolean bl, boolean bl2) {
		this.type = CraftingBlockData.Type.SETTINGS;
		this.bookOpen = bl;
		this.filterActive = bl2;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.type = buf.readEnumConstant(CraftingBlockData.Type.class);
		if (this.type == CraftingBlockData.Type.SHOWN) {
			this.recipeType = RecipeDispatcher.getByRawId(buf.readInt());
		} else if (this.type == CraftingBlockData.Type.SETTINGS) {
			this.bookOpen = buf.readBoolean();
			this.filterActive = buf.readBoolean();
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeEnumConstant(this.type);
		if (this.type == CraftingBlockData.Type.SHOWN) {
			buf.writeInt(RecipeDispatcher.getRawId(this.recipeType));
		} else if (this.type == CraftingBlockData.Type.SETTINGS) {
			buf.writeBoolean(this.bookOpen);
			buf.writeBoolean(this.filterActive);
		}
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onCraftingBlockData(this);
	}

	public CraftingBlockData.Type getType() {
		return this.type;
	}

	public RecipeType getRecipeType() {
		return this.recipeType;
	}

	public boolean isBookOpen() {
		return this.bookOpen;
	}

	public boolean isFilterActive() {
		return this.filterActive;
	}

	public static enum Type {
		SHOWN,
		SETTINGS;
	}
}
