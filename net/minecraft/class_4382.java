package net.minecraft;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.PacketByteBuf;

public class class_4382 implements Packet<ClientPlayPacketListener> {
	private List<RecipeType> field_21574;

	public class_4382() {
	}

	public class_4382(Collection<RecipeType> collection) {
		this.field_21574 = Lists.newArrayList(collection);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_20203(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.field_21574 = Lists.newArrayList();
		int i = buf.readVarInt();

		for (int j = 0; j < i; j++) {
			this.field_21574.add(class_3579.method_16219(buf));
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.field_21574.size());

		for (RecipeType recipeType : this.field_21574) {
			class_3579.method_16217(recipeType, buf);
		}
	}

	public List<RecipeType> method_20273() {
		return this.field_21574;
	}
}
