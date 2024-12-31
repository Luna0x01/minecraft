package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class class_4481<T> extends TagContainer<T> {
	private final Registry<T> field_22224;

	public class_4481(Registry<T> registry, String string, String string2) {
		super(registry::containsId, registry::getByIdentifier, string, false, string2);
		this.field_22224 = registry;
	}

	public void method_21459(PacketByteBuf packetByteBuf) {
		packetByteBuf.writeVarInt(this.method_21491().size());

		for (Entry<Identifier, Tag<T>> entry : this.method_21491().entrySet()) {
			packetByteBuf.writeIdentifier((Identifier)entry.getKey());
			packetByteBuf.writeVarInt(((Tag)entry.getValue()).values().size());

			for (T object : ((Tag)entry.getValue()).values()) {
				packetByteBuf.writeVarInt(this.field_22224.getRawId(object));
			}
		}
	}

	public void method_21460(PacketByteBuf packetByteBuf) {
		int i = packetByteBuf.readVarInt();

		for (int j = 0; j < i; j++) {
			Identifier identifier = packetByteBuf.readIdentifier();
			int k = packetByteBuf.readVarInt();
			List<T> list = Lists.newArrayList();

			for (int l = 0; l < k; l++) {
				list.add(this.field_22224.getByRawId(packetByteBuf.readVarInt()));
			}

			this.method_21491().put(identifier, Tag.Builder.create().add(list).build(identifier));
		}
	}
}
