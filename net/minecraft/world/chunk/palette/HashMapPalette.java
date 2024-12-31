package net.minecraft.world.chunk.palette;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.class_2929;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.collection.IdList;

public class HashMapPalette<T> implements Palette<T> {
	private final IdList<T> field_18879;
	private final class_2929<T> field_12908;
	private final PaletteResizeListener<T> field_12909;
	private final Function<NbtCompound, T> field_18880;
	private final Function<T, NbtCompound> field_18881;
	private final int bitsPerBlock;

	public HashMapPalette(
		IdList<T> idList, int i, PaletteResizeListener<T> paletteResizeListener, Function<NbtCompound, T> function, Function<T, NbtCompound> function2
	) {
		this.field_18879 = idList;
		this.bitsPerBlock = i;
		this.field_12909 = paletteResizeListener;
		this.field_18880 = function;
		this.field_18881 = function2;
		this.field_12908 = new class_2929<>(1 << i);
	}

	@Override
	public int method_17098(T object) {
		int i = this.field_12908.getId(object);
		if (i == -1) {
			i = this.field_12908.method_12864(object);
			if (i >= 1 << this.bitsPerBlock) {
				i = this.field_12909.onResize(this.bitsPerBlock + 1, object);
			}
		}

		return i;
	}

	@Nullable
	@Override
	public T method_17096(int i) {
		return this.field_12908.getById(i);
	}

	@Override
	public void read(PacketByteBuf buf) {
		this.field_12908.clear();
		int i = buf.readVarInt();

		for (int j = 0; j < i; j++) {
			this.field_12908.method_12864(this.field_18879.fromId(buf.readVarInt()));
		}
	}

	@Override
	public void write(PacketByteBuf buf) {
		int i = this.method_17058();
		buf.writeVarInt(i);

		for (int j = 0; j < i; j++) {
			buf.writeVarInt(this.field_18879.getId(this.field_12908.getById(j)));
		}
	}

	@Override
	public int packetSize() {
		int i = PacketByteBuf.getVarIntSizeBytes(this.method_17058());

		for (int j = 0; j < this.method_17058(); j++) {
			i += PacketByteBuf.getVarIntSizeBytes(this.field_18879.getId(this.field_12908.getById(j)));
		}

		return i;
	}

	public int method_17058() {
		return this.field_12908.size();
	}

	@Override
	public void method_17097(NbtList nbtList) {
		this.field_12908.clear();

		for (int i = 0; i < nbtList.size(); i++) {
			this.field_12908.method_12864((T)this.field_18880.apply(nbtList.getCompound(i)));
		}
	}

	public void method_17059(NbtList nbtList) {
		for (int i = 0; i < this.method_17058(); i++) {
			nbtList.add((NbtElement)this.field_18881.apply(this.field_12908.getById(i)));
		}
	}
}
