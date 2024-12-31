package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class CraftingBlockData implements Packet<ServerPlayPacketListener> {
	private CraftingBlockData.Type type;
	private Identifier field_21588;
	private boolean bookOpen;
	private boolean filterActive;
	private boolean field_21589;
	private boolean field_21590;

	public CraftingBlockData() {
	}

	public CraftingBlockData(RecipeType recipeType) {
		this.type = CraftingBlockData.Type.SHOWN;
		this.field_21588 = recipeType.method_16202();
	}

	public CraftingBlockData(boolean bl, boolean bl2, boolean bl3, boolean bl4) {
		this.type = CraftingBlockData.Type.SETTINGS;
		this.bookOpen = bl;
		this.filterActive = bl2;
		this.field_21589 = bl3;
		this.field_21590 = bl4;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.type = buf.readEnumConstant(CraftingBlockData.Type.class);
		if (this.type == CraftingBlockData.Type.SHOWN) {
			this.field_21588 = buf.readIdentifier();
		} else if (this.type == CraftingBlockData.Type.SETTINGS) {
			this.bookOpen = buf.readBoolean();
			this.filterActive = buf.readBoolean();
			this.field_21589 = buf.readBoolean();
			this.field_21590 = buf.readBoolean();
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeEnumConstant(this.type);
		if (this.type == CraftingBlockData.Type.SHOWN) {
			buf.writeIdentifier(this.field_21588);
		} else if (this.type == CraftingBlockData.Type.SETTINGS) {
			buf.writeBoolean(this.bookOpen);
			buf.writeBoolean(this.filterActive);
			buf.writeBoolean(this.field_21589);
			buf.writeBoolean(this.field_21590);
		}
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onCraftingBlockData(this);
	}

	public CraftingBlockData.Type getType() {
		return this.type;
	}

	public Identifier method_14867() {
		return this.field_21588;
	}

	public boolean isBookOpen() {
		return this.bookOpen;
	}

	public boolean isFilterActive() {
		return this.filterActive;
	}

	public boolean method_20300() {
		return this.field_21589;
	}

	public boolean method_20301() {
		return this.field_21590;
	}

	public static enum Type {
		SHOWN,
		SETTINGS;
	}
}
