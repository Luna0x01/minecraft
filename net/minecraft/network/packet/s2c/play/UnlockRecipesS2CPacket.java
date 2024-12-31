package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.recipe.book.RecipeBookOptions;
import net.minecraft.util.Identifier;

public class UnlockRecipesS2CPacket implements Packet<ClientPlayPacketListener> {
	private UnlockRecipesS2CPacket.Action action;
	private List<Identifier> recipeIdsToChange;
	private List<Identifier> recipeIdsToInit;
	private RecipeBookOptions options;

	public UnlockRecipesS2CPacket() {
	}

	public UnlockRecipesS2CPacket(
		UnlockRecipesS2CPacket.Action action, Collection<Identifier> collection, Collection<Identifier> collection2, RecipeBookOptions options
	) {
		this.action = action;
		this.recipeIdsToChange = ImmutableList.copyOf(collection);
		this.recipeIdsToInit = ImmutableList.copyOf(collection2);
		this.options = options;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onUnlockRecipes(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.action = buf.readEnumConstant(UnlockRecipesS2CPacket.Action.class);
		this.options = RecipeBookOptions.fromPacket(buf);
		int i = buf.readVarInt();
		this.recipeIdsToChange = Lists.newArrayList();

		for (int j = 0; j < i; j++) {
			this.recipeIdsToChange.add(buf.readIdentifier());
		}

		if (this.action == UnlockRecipesS2CPacket.Action.INIT) {
			i = buf.readVarInt();
			this.recipeIdsToInit = Lists.newArrayList();

			for (int k = 0; k < i; k++) {
				this.recipeIdsToInit.add(buf.readIdentifier());
			}
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeEnumConstant(this.action);
		this.options.toPacket(buf);
		buf.writeVarInt(this.recipeIdsToChange.size());

		for (Identifier identifier : this.recipeIdsToChange) {
			buf.writeIdentifier(identifier);
		}

		if (this.action == UnlockRecipesS2CPacket.Action.INIT) {
			buf.writeVarInt(this.recipeIdsToInit.size());

			for (Identifier identifier2 : this.recipeIdsToInit) {
				buf.writeIdentifier(identifier2);
			}
		}
	}

	public List<Identifier> getRecipeIdsToChange() {
		return this.recipeIdsToChange;
	}

	public List<Identifier> getRecipeIdsToInit() {
		return this.recipeIdsToInit;
	}

	public RecipeBookOptions getOptions() {
		return this.options;
	}

	public UnlockRecipesS2CPacket.Action getAction() {
		return this.action;
	}

	public static enum Action {
		INIT,
		ADD,
		REMOVE;
	}
}
