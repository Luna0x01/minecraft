package net.minecraft.world.chunk.palette;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.collection.IdList;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.MathHelper;

public class PaletteContainer<T> implements PaletteResizeListener<T> {
	private final Palette<T> field_18902;
	private final PaletteResizeListener<T> field_18903 = (i, objectx) -> 0;
	private final IdList<T> field_18904;
	private final Function<NbtCompound, T> field_18905;
	private final Function<T, NbtCompound> field_18906;
	private final T field_18907;
	protected PaletteData storage;
	private Palette<T> palette;
	private int bitsPerBlock;
	private final ReentrantLock field_18908 = new ReentrantLock();

	private void method_17105() {
		if (this.field_18908.isLocked() && !this.field_18908.isHeldByCurrentThread()) {
			String string = (String)Thread.getAllStackTraces()
				.keySet()
				.stream()
				.filter(Objects::nonNull)
				.map(thread -> thread.getName() + ": \n\tat " + (String)Arrays.stream(thread.getStackTrace()).map(Object::toString).collect(Collectors.joining("\n\tat ")))
				.collect(Collectors.joining("\n"));
			CrashReport crashReport = new CrashReport("Writing into PalettedContainer from multiple threads", new IllegalStateException());
			CrashReportSection crashReportSection = crashReport.addElement("Thread dumps");
			crashReportSection.add("Thread dumps", string);
			throw new CrashException(crashReport);
		} else {
			this.field_18908.lock();
		}
	}

	private void method_17108() {
		this.field_18908.unlock();
	}

	public PaletteContainer(Palette<T> palette, IdList<T> idList, Function<NbtCompound, T> function, Function<T, NbtCompound> function2, T object) {
		this.field_18902 = palette;
		this.field_18904 = idList;
		this.field_18905 = function;
		this.field_18906 = function2;
		this.field_18907 = object;
		this.setPaletteSize(4);
	}

	private static int getPositionFor(int x, int y, int z) {
		return y << 8 | z << 4 | x;
	}

	private void setPaletteSize(int bitsPerBlock) {
		if (bitsPerBlock != this.bitsPerBlock) {
			this.bitsPerBlock = bitsPerBlock;
			if (this.bitsPerBlock <= 4) {
				this.bitsPerBlock = 4;
				this.palette = new LinearPalette<>(this.field_18904, this.bitsPerBlock, this, this.field_18905);
			} else if (this.bitsPerBlock < 9) {
				this.palette = new HashMapPalette<>(this.field_18904, this.bitsPerBlock, this, this.field_18905, this.field_18906);
			} else {
				this.palette = this.field_18902;
				this.bitsPerBlock = MathHelper.log2DeBruijn(this.field_18904.size());
			}

			this.palette.method_17098(this.field_18907);
			this.storage = new PaletteData(this.bitsPerBlock, 4096);
		}
	}

	@Override
	public int onResize(int i, T object) {
		this.method_17105();
		PaletteData paletteData = this.storage;
		Palette<T> palette = this.palette;
		this.setPaletteSize(i);

		for (int j = 0; j < paletteData.getMaxBlockAmount(); j++) {
			T object2 = palette.method_17096(paletteData.get(j));
			if (object2 != null) {
				this.method_17102(j, object2);
			}
		}

		int k = this.palette.method_17098(object);
		this.method_17108();
		return k;
	}

	public void method_17101(int i, int j, int k, T object) {
		this.method_17105();
		this.method_17102(getPositionFor(i, j, k), object);
		this.method_17108();
	}

	protected void method_17102(int i, T object) {
		int j = this.palette.method_17098(object);
		this.storage.set(i, j);
	}

	public T method_17100(int i, int j, int k) {
		return this.method_17099(getPositionFor(i, j, k));
	}

	protected T method_17099(int i) {
		T object = this.palette.method_17096(this.storage.get(i));
		return object == null ? this.field_18907 : object;
	}

	public void read(PacketByteBuf buf) {
		this.method_17105();
		int i = buf.readByte();
		if (this.bitsPerBlock != i) {
			this.setPaletteSize(i);
		}

		this.palette.read(buf);
		buf.readLongArray(this.storage.getBlockStateIds());
		this.method_17108();
	}

	public void write(PacketByteBuf buf) {
		this.method_17105();
		buf.writeByte(this.bitsPerBlock);
		this.palette.write(buf);
		buf.writeLongArray(this.storage.getBlockStateIds());
		this.method_17108();
	}

	public void method_17103(NbtCompound nbtCompound, String string, String string2) {
		this.method_17105();
		NbtList nbtList = nbtCompound.getList(string, 10);
		int i = Math.max(4, MathHelper.log2DeBruijn(nbtList.size()));
		if (i != this.bitsPerBlock) {
			this.setPaletteSize(i);
		}

		this.palette.method_17097(nbtList);
		long[] ls = nbtCompound.getLongArray(string2);
		int j = ls.length * 64 / 4096;
		if (this.palette == this.field_18902) {
			Palette<T> palette = new HashMapPalette<>(this.field_18904, i, this.field_18903, this.field_18905, this.field_18906);
			palette.method_17097(nbtList);
			PaletteData paletteData = new PaletteData(i, 4096, ls);

			for (int k = 0; k < 4096; k++) {
				this.storage.set(k, this.field_18902.method_17098(palette.method_17096(paletteData.get(k))));
			}
		} else if (j == this.bitsPerBlock) {
			System.arraycopy(ls, 0, this.storage.getBlockStateIds(), 0, ls.length);
		} else {
			PaletteData paletteData2 = new PaletteData(j, 4096, ls);

			for (int l = 0; l < 4096; l++) {
				this.storage.set(l, paletteData2.get(l));
			}
		}

		this.method_17108();
	}

	public void method_17107(NbtCompound nbtCompound, String string, String string2) {
		this.method_17105();
		HashMapPalette<T> hashMapPalette = new HashMapPalette<>(this.field_18904, this.bitsPerBlock, this.field_18903, this.field_18905, this.field_18906);
		hashMapPalette.method_17098(this.field_18907);
		int[] is = new int[4096];

		for (int i = 0; i < 4096; i++) {
			is[i] = hashMapPalette.method_17098(this.method_17099(i));
		}

		NbtList nbtList = new NbtList();
		hashMapPalette.method_17059(nbtList);
		nbtCompound.put(string, nbtList);
		int j = Math.max(4, MathHelper.log2DeBruijn(nbtList.size()));
		PaletteData paletteData = new PaletteData(j, 4096);

		for (int k = 0; k < is.length; k++) {
			paletteData.set(k, is[k]);
		}

		nbtCompound.putLongArray(string2, paletteData.getBlockStateIds());
		this.method_17108();
	}

	public int packetSize() {
		return 1 + this.palette.packetSize() + PacketByteBuf.getVarIntSizeBytes(this.storage.getMaxBlockAmount()) + this.storage.getBlockStateIds().length * 8;
	}
}
