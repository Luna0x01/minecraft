package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.recipe.RecipeDispatcher;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.PacketByteBuf;

public class RecipesUnlockS2CPacket implements Packet<ClientPlayPacketListener> {
	private RecipesUnlockS2CPacket.Action action;
	private List<RecipeType> recipes;
	private List<RecipeType> recipesToAdd;
	private boolean bookOpen;
	private boolean filterActive;

	public RecipesUnlockS2CPacket() {
	}

	public RecipesUnlockS2CPacket(RecipesUnlockS2CPacket.Action action, List<RecipeType> list, List<RecipeType> list2, boolean bl, boolean bl2) {
		this.action = action;
		this.recipes = list;
		this.recipesToAdd = list2;
		this.bookOpen = bl;
		this.filterActive = bl2;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onRecipesUnlock(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.action = buf.readEnumConstant(RecipesUnlockS2CPacket.Action.class);
		this.bookOpen = buf.readBoolean();
		this.filterActive = buf.readBoolean();
		int i = buf.readVarInt();
		this.recipes = Lists.newArrayList();

		for (int j = 0; j < i; j++) {
			this.recipes.add(RecipeDispatcher.getByRawId(buf.readVarInt()));
		}

		if (this.action == RecipesUnlockS2CPacket.Action.INIT) {
			i = buf.readVarInt();
			this.recipesToAdd = Lists.newArrayList();

			for (int k = 0; k < i; k++) {
				this.recipesToAdd.add(RecipeDispatcher.getByRawId(buf.readVarInt()));
			}
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeEnumConstant(this.action);
		buf.writeBoolean(this.bookOpen);
		buf.writeBoolean(this.filterActive);
		buf.writeVarInt(this.recipes.size());

		for (RecipeType recipeType : this.recipes) {
			buf.writeVarInt(RecipeDispatcher.getRawId(recipeType));
		}

		if (this.action == RecipesUnlockS2CPacket.Action.INIT) {
			buf.writeVarInt(this.recipesToAdd.size());

			for (RecipeType recipeType2 : this.recipesToAdd) {
				buf.writeVarInt(RecipeDispatcher.getRawId(recipeType2));
			}
		}
	}

	public List<RecipeType> getRecipes() {
		return this.recipes;
	}

	public List<RecipeType> getRecipesToAdd() {
		return this.recipesToAdd;
	}

	public boolean isBookOpen() {
		return this.bookOpen;
	}

	public boolean isFilterActive() {
		return this.filterActive;
	}

	public RecipesUnlockS2CPacket.Action getAction() {
		return this.action;
	}

	public static enum Action {
		INIT,
		ADD,
		REMOVE;
	}
}
