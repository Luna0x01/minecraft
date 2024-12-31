package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class RecipesUnlockS2CPacket implements Packet<ClientPlayPacketListener> {
	private RecipesUnlockS2CPacket.Action action;
	private List<Identifier> recipes;
	private List<Identifier> recipesToAdd;
	private boolean bookOpen;
	private boolean filterActive;
	private boolean field_21555;
	private boolean field_21556;

	public RecipesUnlockS2CPacket() {
	}

	public RecipesUnlockS2CPacket(
		RecipesUnlockS2CPacket.Action action,
		Collection<Identifier> collection,
		Collection<Identifier> collection2,
		boolean bl,
		boolean bl2,
		boolean bl3,
		boolean bl4
	) {
		this.action = action;
		this.recipes = ImmutableList.copyOf(collection);
		this.recipesToAdd = ImmutableList.copyOf(collection2);
		this.bookOpen = bl;
		this.filterActive = bl2;
		this.field_21555 = bl3;
		this.field_21556 = bl4;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onRecipesUnlock(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.action = buf.readEnumConstant(RecipesUnlockS2CPacket.Action.class);
		this.bookOpen = buf.readBoolean();
		this.filterActive = buf.readBoolean();
		this.field_21555 = buf.readBoolean();
		this.field_21556 = buf.readBoolean();
		int i = buf.readVarInt();
		this.recipes = Lists.newArrayList();

		for (int j = 0; j < i; j++) {
			this.recipes.add(buf.readIdentifier());
		}

		if (this.action == RecipesUnlockS2CPacket.Action.INIT) {
			i = buf.readVarInt();
			this.recipesToAdd = Lists.newArrayList();

			for (int k = 0; k < i; k++) {
				this.recipesToAdd.add(buf.readIdentifier());
			}
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeEnumConstant(this.action);
		buf.writeBoolean(this.bookOpen);
		buf.writeBoolean(this.filterActive);
		buf.writeBoolean(this.field_21555);
		buf.writeBoolean(this.field_21556);
		buf.writeVarInt(this.recipes.size());

		for (Identifier identifier : this.recipes) {
			buf.writeIdentifier(identifier);
		}

		if (this.action == RecipesUnlockS2CPacket.Action.INIT) {
			buf.writeVarInt(this.recipesToAdd.size());

			for (Identifier identifier2 : this.recipesToAdd) {
				buf.writeIdentifier(identifier2);
			}
		}
	}

	public List<Identifier> getRecipes() {
		return this.recipes;
	}

	public List<Identifier> getRecipesToAdd() {
		return this.recipesToAdd;
	}

	public boolean isBookOpen() {
		return this.bookOpen;
	}

	public boolean isFilterActive() {
		return this.filterActive;
	}

	public boolean method_20245() {
		return this.field_21555;
	}

	public boolean method_20246() {
		return this.field_21556;
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
