package net.minecraft.world.chunk.palette;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.collection.IdList;

public class LinearPalette<T> implements Palette<T> {
	private final IdList<T> field_18899;
	private final T[] field_18900;
	private final PaletteResizeListener<T> field_12916;
	private final Function<NbtCompound, T> field_18901;
	private final int bitsPerBlock;
	private int size;

	public LinearPalette(IdList<T> idList, int i, PaletteResizeListener<T> paletteResizeListener, Function<NbtCompound, T> function) {
		this.field_18899 = idList;
		this.field_18900 = (T[])(new Object[1 << i]);
		this.bitsPerBlock = i;
		this.field_12916 = paletteResizeListener;
		this.field_18901 = function;
	}

	@Override
	public int method_17098(T object) {
		for (int i = 0; i < this.size; i++) {
			if (this.field_18900[i] == object) {
				return i;
			}
		}

		int j = this.size;
		if (j < this.field_18900.length) {
			this.field_18900[j] = object;
			this.size++;
			return j;
		} else {
			return this.field_12916.onResize(this.bitsPerBlock + 1, object);
		}
	}

	@Nullable
	@Override
	public T method_17096(int i) {
		return i >= 0 && i < this.size ? this.field_18900[i] : null;
	}

	@Override
	public void read(PacketByteBuf buf) {
		this.size = buf.readVarInt();

		for (int i = 0; i < this.size; i++) {
			this.field_18900[i] = this.field_18899.fromId(buf.readVarInt());
		}
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.size);

		for (int i = 0; i < this.size; i++) {
			buf.writeVarInt(this.field_18899.getId(this.field_18900[i]));
		}
	}

	@Override
	public int packetSize() {
		int i = PacketByteBuf.getVarIntSizeBytes(this.method_17095());

		for (int j = 0; j < this.method_17095(); j++) {
			i += PacketByteBuf.getVarIntSizeBytes(this.field_18899.getId(this.field_18900[j]));
		}

		return i;
	}

	public int method_17095() {
		return this.size;
	}

	@Override
	public void method_17097(NbtList nbtList) {
		for (int i = 0; i < nbtList.size(); i++) {
			this.field_18900[i] = (T)this.field_18901.apply(nbtList.getCompound(i));
		}

		this.size = nbtList.size();
	}
}
