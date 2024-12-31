package net.minecraft.client.render;

import javax.annotation.Nullable;
import net.minecraft.client.render.world.ChunkRenderFactory;
import net.minecraft.client.world.BuiltChunk;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class BuiltChunkStorage {
	protected final WorldRenderer worldRenderer;
	protected final World world;
	protected int sizeY;
	protected int sizeX;
	protected int sizeZ;
	public BuiltChunk[] chunks;

	public BuiltChunkStorage(World world, int i, WorldRenderer worldRenderer, ChunkRenderFactory chunkRenderFactory) {
		this.worldRenderer = worldRenderer;
		this.world = world;
		this.setViewDistance(i);
		this.createChunks(chunkRenderFactory);
	}

	protected void createChunks(ChunkRenderFactory chunkRenderFactory) {
		int i = this.sizeX * this.sizeY * this.sizeZ;
		this.chunks = new BuiltChunk[i];
		int j = 0;

		for (int k = 0; k < this.sizeX; k++) {
			for (int l = 0; l < this.sizeY; l++) {
				for (int m = 0; m < this.sizeZ; m++) {
					int n = (m * this.sizeY + l) * this.sizeX + k;
					this.chunks[n] = chunkRenderFactory.method_10175(this.world, this.worldRenderer, j++);
					this.chunks[n].method_12427(k * 16, l * 16, m * 16);
				}
			}
		}
	}

	public void clear() {
		for (BuiltChunk builtChunk : this.chunks) {
			builtChunk.delete();
		}
	}

	protected void setViewDistance(int viewDistance) {
		int i = viewDistance * 2 + 1;
		this.sizeX = i;
		this.sizeY = 16;
		this.sizeZ = i;
	}

	public void updateCameraPosition(double x, double z) {
		int i = MathHelper.floor(x) - 8;
		int j = MathHelper.floor(z) - 8;
		int k = this.sizeX * 16;

		for (int l = 0; l < this.sizeX; l++) {
			int m = this.method_9934(i, k, l);

			for (int n = 0; n < this.sizeZ; n++) {
				int o = this.method_9934(j, k, n);

				for (int p = 0; p < this.sizeY; p++) {
					int q = p * 16;
					BuiltChunk builtChunk = this.chunks[(n * this.sizeY + p) * this.sizeX + l];
					builtChunk.method_12427(m, q, o);
				}
			}
		}
	}

	private int method_9934(int i, int j, int k) {
		int l = k * 16;
		int m = l - i + j / 2;
		if (m < 0) {
			m -= j - 1;
		}

		return l - m / j * j;
	}

	public void method_9935(int i, int j, int k, int l, int m, int n, boolean bl) {
		int o = MathHelper.floorDiv(i, 16);
		int p = MathHelper.floorDiv(j, 16);
		int q = MathHelper.floorDiv(k, 16);
		int r = MathHelper.floorDiv(l, 16);
		int s = MathHelper.floorDiv(m, 16);
		int t = MathHelper.floorDiv(n, 16);

		for (int u = o; u <= r; u++) {
			int v = u % this.sizeX;
			if (v < 0) {
				v += this.sizeX;
			}

			for (int w = p; w <= s; w++) {
				int x = w % this.sizeY;
				if (x < 0) {
					x += this.sizeY;
				}

				for (int y = q; y <= t; y++) {
					int z = y % this.sizeZ;
					if (z < 0) {
						z += this.sizeZ;
					}

					int aa = (z * this.sizeY + x) * this.sizeX + v;
					BuiltChunk builtChunk = this.chunks[aa];
					builtChunk.method_10162(bl);
				}
			}
		}
	}

	@Nullable
	protected BuiltChunk getRenderedChunk(BlockPos pos) {
		int i = MathHelper.floorDiv(pos.getX(), 16);
		int j = MathHelper.floorDiv(pos.getY(), 16);
		int k = MathHelper.floorDiv(pos.getZ(), 16);
		if (j >= 0 && j < this.sizeY) {
			i %= this.sizeX;
			if (i < 0) {
				i += this.sizeX;
			}

			k %= this.sizeZ;
			if (k < 0) {
				k += this.sizeZ;
			}

			int l = (k * this.sizeY + j) * this.sizeX + i;
			return this.chunks[l];
		} else {
			return null;
		}
	}
}
