package net.minecraft.client.network.packet;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class SynchronizeRecipesS2CPacket implements Packet<ClientPlayPacketListener> {
	private List<Recipe<?>> recipes;

	public SynchronizeRecipesS2CPacket() {
	}

	public SynchronizeRecipesS2CPacket(Collection<Recipe<?>> collection) {
		this.recipes = Lists.newArrayList(collection);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onSynchronizeRecipes(this);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.recipes = Lists.newArrayList();
		int i = packetByteBuf.readVarInt();

		for (int j = 0; j < i; j++) {
			this.recipes.add(readRecipe(packetByteBuf));
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.recipes.size());

		for (Recipe<?> recipe : this.recipes) {
			writeRecipe(recipe, packetByteBuf);
		}
	}

	public List<Recipe<?>> getRecipes() {
		return this.recipes;
	}

	public static Recipe<?> readRecipe(PacketByteBuf packetByteBuf) {
		Identifier identifier = packetByteBuf.readIdentifier();
		Identifier identifier2 = packetByteBuf.readIdentifier();
		return ((RecipeSerializer)Registry.field_17598
				.getOrEmpty(identifier)
				.orElseThrow(() -> new IllegalArgumentException("Unknown recipe serializer " + identifier)))
			.read(identifier2, packetByteBuf);
	}

	public static <T extends Recipe<?>> void writeRecipe(T recipe, PacketByteBuf packetByteBuf) {
		packetByteBuf.writeIdentifier(Registry.field_17598.getId(recipe.getSerializer()));
		packetByteBuf.writeIdentifier(recipe.getId());
		((RecipeSerializer<T>)recipe.getSerializer()).write(packetByteBuf, recipe);
	}
}
