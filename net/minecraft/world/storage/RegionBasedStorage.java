package net.minecraft.world.storage;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.ThrowableDeliverer;
import net.minecraft.util.math.ChunkPos;

public final class RegionBasedStorage implements AutoCloseable {
	public static final String field_31425 = ".mca";
	private static final int field_31426 = 256;
	private final Long2ObjectLinkedOpenHashMap<RegionFile> cachedRegionFiles = new Long2ObjectLinkedOpenHashMap();
	private final File directory;
	private final boolean dsync;

	RegionBasedStorage(File directory, boolean dsync) {
		this.directory = directory;
		this.dsync = dsync;
	}

	private RegionFile getRegionFile(ChunkPos pos) throws IOException {
		long l = ChunkPos.toLong(pos.getRegionX(), pos.getRegionZ());
		RegionFile regionFile = (RegionFile)this.cachedRegionFiles.getAndMoveToFirst(l);
		if (regionFile != null) {
			return regionFile;
		} else {
			if (this.cachedRegionFiles.size() >= 256) {
				((RegionFile)this.cachedRegionFiles.removeLast()).close();
			}

			if (!this.directory.exists()) {
				this.directory.mkdirs();
			}

			File file = new File(this.directory, "r." + pos.getRegionX() + "." + pos.getRegionZ() + ".mca");
			RegionFile regionFile2 = new RegionFile(file, this.directory, this.dsync);
			this.cachedRegionFiles.putAndMoveToFirst(l, regionFile2);
			return regionFile2;
		}
	}

	@Nullable
	public NbtCompound getTagAt(ChunkPos pos) throws IOException {
		RegionFile regionFile = this.getRegionFile(pos);
		DataInputStream dataInputStream = regionFile.getChunkInputStream(pos);

		NbtCompound var8;
		label43: {
			try {
				if (dataInputStream == null) {
					var8 = null;
					break label43;
				}

				var8 = NbtIo.read(dataInputStream);
			} catch (Throwable var7) {
				if (dataInputStream != null) {
					try {
						dataInputStream.close();
					} catch (Throwable var6) {
						var7.addSuppressed(var6);
					}
				}

				throw var7;
			}

			if (dataInputStream != null) {
				dataInputStream.close();
			}

			return var8;
		}

		if (dataInputStream != null) {
			dataInputStream.close();
		}

		return var8;
	}

	protected void write(ChunkPos pos, @Nullable NbtCompound nbt) throws IOException {
		RegionFile regionFile = this.getRegionFile(pos);
		if (nbt == null) {
			regionFile.method_31740(pos);
		} else {
			DataOutputStream dataOutputStream = regionFile.getChunkOutputStream(pos);

			try {
				NbtIo.write(nbt, dataOutputStream);
			} catch (Throwable var8) {
				if (dataOutputStream != null) {
					try {
						dataOutputStream.close();
					} catch (Throwable var7) {
						var8.addSuppressed(var7);
					}
				}

				throw var8;
			}

			if (dataOutputStream != null) {
				dataOutputStream.close();
			}
		}
	}

	public void close() throws IOException {
		ThrowableDeliverer<IOException> throwableDeliverer = new ThrowableDeliverer();
		ObjectIterator var2 = this.cachedRegionFiles.values().iterator();

		while (var2.hasNext()) {
			RegionFile regionFile = (RegionFile)var2.next();

			try {
				regionFile.close();
			} catch (IOException var5) {
				throwableDeliverer.add(var5);
			}
		}

		throwableDeliverer.deliver();
	}

	public void sync() throws IOException {
		ObjectIterator var1 = this.cachedRegionFiles.values().iterator();

		while (var1.hasNext()) {
			RegionFile regionFile = (RegionFile)var1.next();
			regionFile.sync();
		}
	}
}
