package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import javax.annotation.Nullable;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.ChunkPos;

public class RegionFile implements AutoCloseable {
	private static final byte[] EMPTY_SECTOR = new byte[4096];
	private final RandomAccessFile file;
	private final int[] offsets = new int[1024];
	private final int[] chunkTimestamps = new int[1024];
	private final List<Boolean> sectorFree;

	public RegionFile(File file) throws IOException {
		this.file = new RandomAccessFile(file, "rw");
		if (this.file.length() < 4096L) {
			this.file.write(EMPTY_SECTOR);
			this.file.write(EMPTY_SECTOR);
		}

		if ((this.file.length() & 4095L) != 0L) {
			for (int i = 0; (long)i < (this.file.length() & 4095L); i++) {
				this.file.write(0);
			}
		}

		int j = (int)this.file.length() / 4096;
		this.sectorFree = Lists.newArrayListWithCapacity(j);

		for (int k = 0; k < j; k++) {
			this.sectorFree.add(true);
		}

		this.sectorFree.set(0, false);
		this.sectorFree.set(1, false);
		this.file.seek(0L);

		for (int l = 0; l < 1024; l++) {
			int m = this.file.readInt();
			this.offsets[l] = m;
			if (m != 0 && (m >> 8) + (m & 0xFF) <= this.sectorFree.size()) {
				for (int n = 0; n < (m & 0xFF); n++) {
					this.sectorFree.set((m >> 8) + n, false);
				}
			}
		}

		for (int o = 0; o < 1024; o++) {
			int p = this.file.readInt();
			this.chunkTimestamps[o] = p;
		}
	}

	@Nullable
	public synchronized DataInputStream getChunkDataInputStream(ChunkPos chunkPos) throws IOException {
		int i = this.getOffset(chunkPos);
		if (i == 0) {
			return null;
		} else {
			int j = i >> 8;
			int k = i & 0xFF;
			if (j + k > this.sectorFree.size()) {
				return null;
			} else {
				this.file.seek((long)(j * 4096));
				int l = this.file.readInt();
				if (l > 4096 * k) {
					return null;
				} else if (l <= 0) {
					return null;
				} else {
					byte b = this.file.readByte();
					if (b == 1) {
						byte[] bs = new byte[l - 1];
						this.file.read(bs);
						return new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(bs))));
					} else if (b == 2) {
						byte[] cs = new byte[l - 1];
						this.file.read(cs);
						return new DataInputStream(new BufferedInputStream(new InflaterInputStream(new ByteArrayInputStream(cs))));
					} else {
						return null;
					}
				}
			}
		}
	}

	public boolean isChunkPresent(ChunkPos chunkPos) {
		int i = this.getOffset(chunkPos);
		if (i == 0) {
			return false;
		} else {
			int j = i >> 8;
			int k = i & 0xFF;
			if (j + k > this.sectorFree.size()) {
				return false;
			} else {
				try {
					this.file.seek((long)(j * 4096));
					int l = this.file.readInt();
					return l > 4096 * k ? false : l > 0;
				} catch (IOException var6) {
					return false;
				}
			}
		}
	}

	public DataOutputStream getChunkDataOutputStream(ChunkPos chunkPos) {
		return new DataOutputStream(new BufferedOutputStream(new DeflaterOutputStream(new RegionFile.ChunkBuffer(chunkPos))));
	}

	protected synchronized void write(ChunkPos chunkPos, byte[] bs, int i) throws IOException {
		int j = this.getOffset(chunkPos);
		int k = j >> 8;
		int l = j & 0xFF;
		int m = (i + 5) / 4096 + 1;
		if (m >= 256) {
			throw new RuntimeException(String.format("Too big to save, %d > 1048576", i));
		} else {
			if (k != 0 && l == m) {
				this.write(k, bs, i);
			} else {
				for (int n = 0; n < l; n++) {
					this.sectorFree.set(k + n, true);
				}

				int o = this.sectorFree.indexOf(true);
				int p = 0;
				if (o != -1) {
					for (int q = o; q < this.sectorFree.size(); q++) {
						if (p != 0) {
							if ((Boolean)this.sectorFree.get(q)) {
								p++;
							} else {
								p = 0;
							}
						} else if ((Boolean)this.sectorFree.get(q)) {
							o = q;
							p = 1;
						}

						if (p >= m) {
							break;
						}
					}
				}

				if (p >= m) {
					k = o;
					this.setOffset(chunkPos, o << 8 | m);

					for (int r = 0; r < m; r++) {
						this.sectorFree.set(k + r, false);
					}

					this.write(k, bs, i);
				} else {
					this.file.seek(this.file.length());
					k = this.sectorFree.size();

					for (int s = 0; s < m; s++) {
						this.file.write(EMPTY_SECTOR);
						this.sectorFree.add(false);
					}

					this.write(k, bs, i);
					this.setOffset(chunkPos, k << 8 | m);
				}
			}

			this.setTimestamp(chunkPos, (int)(SystemUtil.getEpochTimeMs() / 1000L));
		}
	}

	private void write(int i, byte[] bs, int j) throws IOException {
		this.file.seek((long)(i * 4096));
		this.file.writeInt(j + 1);
		this.file.writeByte(2);
		this.file.write(bs, 0, j);
	}

	private int getOffset(ChunkPos chunkPos) {
		return this.offsets[this.getPackedRegionRelativePosition(chunkPos)];
	}

	public boolean hasChunk(ChunkPos chunkPos) {
		return this.getOffset(chunkPos) != 0;
	}

	private void setOffset(ChunkPos chunkPos, int i) throws IOException {
		int j = this.getPackedRegionRelativePosition(chunkPos);
		this.offsets[j] = i;
		this.file.seek((long)(j * 4));
		this.file.writeInt(i);
	}

	private int getPackedRegionRelativePosition(ChunkPos chunkPos) {
		return chunkPos.getRegionRelativeX() + chunkPos.getRegionRelativeZ() * 32;
	}

	private void setTimestamp(ChunkPos chunkPos, int i) throws IOException {
		int j = this.getPackedRegionRelativePosition(chunkPos);
		this.chunkTimestamps[j] = i;
		this.file.seek((long)(4096 + j * 4));
		this.file.writeInt(i);
	}

	public void close() throws IOException {
		this.file.close();
	}

	class ChunkBuffer extends ByteArrayOutputStream {
		private final ChunkPos pos;

		public ChunkBuffer(ChunkPos chunkPos) {
			super(8096);
			this.pos = chunkPos;
		}

		public void close() throws IOException {
			RegionFile.this.write(this.pos, this.buf, this.count);
		}
	}
}
