package net.minecraft.world.chunk.palette;

import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.Validate;

public class PaletteData {
	private final long[] blockStateIds;
	private final int bitsPerBlock;
	private final long paletteSize;
	private final int maxBlockAmount;

	public PaletteData(int i, int j) {
		this(i, j, new long[MathHelper.roundUp(j * i, 64) / 64]);
	}

	public PaletteData(int i, int j, long[] ls) {
		Validate.inclusiveBetween(1L, 32L, (long)i);
		this.maxBlockAmount = j;
		this.bitsPerBlock = i;
		this.blockStateIds = ls;
		this.paletteSize = (1L << i) - 1L;
		int k = MathHelper.roundUp(j * i, 64) / 64;
		if (ls.length != k) {
			throw new RuntimeException("Invalid length given for storage, got: " + ls.length + " but expected: " + k);
		}
	}

	public void set(int position, int blockStateId) {
		Validate.inclusiveBetween(0L, (long)(this.maxBlockAmount - 1), (long)position);
		Validate.inclusiveBetween(0L, this.paletteSize, (long)blockStateId);
		int i = position * this.bitsPerBlock;
		int j = i / 64;
		int k = ((position + 1) * this.bitsPerBlock - 1) / 64;
		int l = i % 64;
		this.blockStateIds[j] = this.blockStateIds[j] & ~(this.paletteSize << l) | ((long)blockStateId & this.paletteSize) << l;
		if (j != k) {
			int m = 64 - l;
			int n = this.bitsPerBlock - m;
			this.blockStateIds[k] = this.blockStateIds[k] >>> n << n | ((long)blockStateId & this.paletteSize) >> m;
		}
	}

	public int get(int position) {
		Validate.inclusiveBetween(0L, (long)(this.maxBlockAmount - 1), (long)position);
		int i = position * this.bitsPerBlock;
		int j = i / 64;
		int k = ((position + 1) * this.bitsPerBlock - 1) / 64;
		int l = i % 64;
		if (j == k) {
			return (int)(this.blockStateIds[j] >>> l & this.paletteSize);
		} else {
			int m = 64 - l;
			return (int)((this.blockStateIds[j] >>> l | this.blockStateIds[k] << m) & this.paletteSize);
		}
	}

	public long[] getBlockStateIds() {
		return this.blockStateIds;
	}

	public int getMaxBlockAmount() {
		return this.maxBlockAmount;
	}

	public int method_21498() {
		return this.bitsPerBlock;
	}
}
