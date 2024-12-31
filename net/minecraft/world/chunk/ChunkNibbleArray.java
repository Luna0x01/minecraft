package net.minecraft.world.chunk;

public class ChunkNibbleArray {
	private final byte[] bytes;

	public ChunkNibbleArray() {
		this.bytes = new byte[2048];
	}

	public ChunkNibbleArray(byte[] bs) {
		this.bytes = bs;
		if (bs.length != 2048) {
			throw new IllegalArgumentException("ChunkNibbleArrays should be 2048 bytes not: " + bs.length);
		}
	}

	public int get(int x, int y, int z) {
		return this.get(this.getIndex(x, y, z));
	}

	public void set(int x, int y, int z, int value) {
		this.set(this.getIndex(x, y, z), value);
	}

	private int getIndex(int x, int y, int z) {
		return y << 8 | z << 4 | x;
	}

	public int get(int index) {
		int i = this.getMetaIndex(index);
		return this.isLowerMetaIndex(index) ? this.bytes[i] & 15 : this.bytes[i] >> 4 & 15;
	}

	public void set(int index, int meta) {
		int i = this.getMetaIndex(index);
		if (this.isLowerMetaIndex(index)) {
			this.bytes[i] = (byte)(this.bytes[i] & 240 | meta & 15);
		} else {
			this.bytes[i] = (byte)(this.bytes[i] & 15 | (meta & 15) << 4);
		}
	}

	private boolean isLowerMetaIndex(int index) {
		return (index & 1) == 0;
	}

	private int getMetaIndex(int index) {
		return index >> 1;
	}

	public byte[] getValue() {
		return this.bytes;
	}
}
