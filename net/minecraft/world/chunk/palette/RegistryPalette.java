package net.minecraft.world.chunk.palette;

import net.minecraft.nbt.NbtList;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.collection.IdList;

public class RegistryPalette<T> implements Palette<T> {
	private final IdList<T> field_18877;
	private final T field_18878;

	public RegistryPalette(IdList<T> idList, T object) {
		this.field_18877 = idList;
		this.field_18878 = object;
	}

	@Override
	public int method_17098(T object) {
		int i = this.field_18877.getId(object);
		return i == -1 ? 0 : i;
	}

	@Override
	public T method_17096(int i) {
		T object = this.field_18877.fromId(i);
		return object == null ? this.field_18878 : object;
	}

	@Override
	public void read(PacketByteBuf buf) {
	}

	@Override
	public void write(PacketByteBuf buf) {
	}

	@Override
	public int packetSize() {
		return PacketByteBuf.getVarIntSizeBytes(0);
	}

	@Override
	public void method_17097(NbtList nbtList) {
	}
}
