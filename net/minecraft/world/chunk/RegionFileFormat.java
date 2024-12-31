package net.minecraft.world.chunk;

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
import net.minecraft.server.MinecraftServer;

public class RegionFileFormat {
	private static final byte[] BYTES = new byte[4096];
	private final File file;
	private RandomAccessFile fileBuffer;
	private final int[] sectorData = new int[1024];
	private final int[] saveTimes = new int[1024];
	private List<Boolean> field_9956;
	private int field_4773;
	private long lastModified;

	public RegionFileFormat(File file) {
		this.file = file;
		this.field_4773 = 0;

		try {
			if (file.exists()) {
				this.lastModified = file.lastModified();
			}

			this.fileBuffer = new RandomAccessFile(file, "rw");
			if (this.fileBuffer.length() < 4096L) {
				this.fileBuffer.write(BYTES);
				this.fileBuffer.write(BYTES);
				this.field_4773 += 8192;
			}

			if ((this.fileBuffer.length() & 4095L) != 0L) {
				for (int i = 0; (long)i < (this.fileBuffer.length() & 4095L); i++) {
					this.fileBuffer.write(0);
				}
			}

			int j = (int)this.fileBuffer.length() / 4096;
			this.field_9956 = Lists.newArrayListWithCapacity(j);

			for (int k = 0; k < j; k++) {
				this.field_9956.add(true);
			}

			this.field_9956.set(0, false);
			this.field_9956.set(1, false);
			this.fileBuffer.seek(0L);

			for (int l = 0; l < 1024; l++) {
				int m = this.fileBuffer.readInt();
				this.sectorData[l] = m;
				if (m != 0 && (m >> 8) + (m & 0xFF) <= this.field_9956.size()) {
					for (int n = 0; n < (m & 0xFF); n++) {
						this.field_9956.set((m >> 8) + n, false);
					}
				}
			}

			for (int o = 0; o < 1024; o++) {
				int p = this.fileBuffer.readInt();
				this.saveTimes[o] = p;
			}
		} catch (IOException var6) {
			var6.printStackTrace();
		}
	}

	public synchronized DataInputStream getChunkInputStream(int chunkX, int chunkZ) {
		if (this.isOutsideRange(chunkX, chunkZ)) {
			return null;
		} else {
			try {
				int i = this.getSectorData(chunkX, chunkZ);
				if (i == 0) {
					return null;
				} else {
					int j = i >> 8;
					int k = i & 0xFF;
					if (j + k > this.field_9956.size()) {
						return null;
					} else {
						this.fileBuffer.seek((long)(j * 4096));
						int l = this.fileBuffer.readInt();
						if (l > 4096 * k) {
							return null;
						} else if (l <= 0) {
							return null;
						} else {
							byte b = this.fileBuffer.readByte();
							if (b == 1) {
								byte[] bs = new byte[l - 1];
								this.fileBuffer.read(bs);
								return new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(bs))));
							} else if (b == 2) {
								byte[] cs = new byte[l - 1];
								this.fileBuffer.read(cs);
								return new DataInputStream(new BufferedInputStream(new InflaterInputStream(new ByteArrayInputStream(cs))));
							} else {
								return null;
							}
						}
					}
				}
			} catch (IOException var9) {
				return null;
			}
		}
	}

	public DataOutputStream getChunkOutputStream(int chunkX, int chunkZ) {
		return this.isOutsideRange(chunkX, chunkZ)
			? null
			: new DataOutputStream(new BufferedOutputStream(new DeflaterOutputStream(new RegionFileFormat.OutputStream(chunkX, chunkZ))));
	}

	protected synchronized void writeChunk(int chunkX, int chunkZ, byte[] bs, int i) {
		try {
			int j = this.getSectorData(chunkX, chunkZ);
			int k = j >> 8;
			int l = j & 0xFF;
			int m = (i + 5) / 4096 + 1;
			if (m >= 256) {
				return;
			}

			if (k != 0 && l == m) {
				this.writeChunk(k, bs, i);
			} else {
				for (int n = 0; n < l; n++) {
					this.field_9956.set(k + n, true);
				}

				int o = this.field_9956.indexOf(true);
				int p = 0;
				if (o != -1) {
					for (int q = o; q < this.field_9956.size(); q++) {
						if (p != 0) {
							if ((Boolean)this.field_9956.get(q)) {
								p++;
							} else {
								p = 0;
							}
						} else if ((Boolean)this.field_9956.get(q)) {
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
					this.writeSectorData(chunkX, chunkZ, o << 8 | m);

					for (int r = 0; r < m; r++) {
						this.field_9956.set(k + r, false);
					}

					this.writeChunk(k, bs, i);
				} else {
					this.fileBuffer.seek(this.fileBuffer.length());
					k = this.field_9956.size();

					for (int s = 0; s < m; s++) {
						this.fileBuffer.write(BYTES);
						this.field_9956.add(false);
					}

					this.field_4773 += 4096 * m;
					this.writeChunk(k, bs, i);
					this.writeSectorData(chunkX, chunkZ, k << 8 | m);
				}
			}

			this.writeSaveTime(chunkX, chunkZ, (int)(MinecraftServer.getTimeMillis() / 1000L));
		} catch (IOException var12) {
			var12.printStackTrace();
		}
	}

	private void writeChunk(int sectorData, byte[] data, int dataLength) throws IOException {
		this.fileBuffer.seek((long)(sectorData * 4096));
		this.fileBuffer.writeInt(dataLength + 1);
		this.fileBuffer.writeByte(2);
		this.fileBuffer.write(data, 0, dataLength);
	}

	private boolean isOutsideRange(int chunkX, int chunkZ) {
		return chunkX < 0 || chunkX >= 32 || chunkZ < 0 || chunkZ >= 32;
	}

	private int getSectorData(int chunkX, int chunkZ) {
		return this.sectorData[chunkX + chunkZ * 32];
	}

	public boolean chunkExists(int chunkX, int chunkZ) {
		return this.getSectorData(chunkX, chunkZ) != 0;
	}

	private void writeSectorData(int chunkX, int chunkZ, int length) throws IOException {
		this.sectorData[chunkX + chunkZ * 32] = length;
		this.fileBuffer.seek((long)((chunkX + chunkZ * 32) * 4));
		this.fileBuffer.writeInt(length);
	}

	private void writeSaveTime(int chunkX, int chunkZ, int timestamp) throws IOException {
		this.saveTimes[chunkX + chunkZ * 32] = timestamp;
		this.fileBuffer.seek((long)(4096 + (chunkX + chunkZ * 32) * 4));
		this.fileBuffer.writeInt(timestamp);
	}

	public void close() throws IOException {
		if (this.fileBuffer != null) {
			this.fileBuffer.close();
		}
	}

	class OutputStream extends ByteArrayOutputStream {
		private int chunkX;
		private int chunkZ;

		public OutputStream(int i, int j) {
			super(8096);
			this.chunkX = i;
			this.chunkZ = j;
		}

		public void close() {
			RegionFileFormat.this.writeChunk(this.chunkX, this.chunkZ, this.buf, this.count);
		}
	}
}
