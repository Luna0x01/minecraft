package net.minecraft.client.resource;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.resource.DirectoryResourcePack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ZipResourcePack;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourcePackLoader {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final FileFilter FILE_FILTER = new FileFilter() {
		public boolean accept(File file) {
			boolean bl = file.isFile() && file.getName().endsWith(".zip");
			boolean bl2 = file.isDirectory() && new File(file, "pack.mcmeta").isFile();
			return bl || bl2;
		}
	};
	private final File resourcePackDir;
	public final ResourcePack defaultResourcePack;
	private final File serverResourcePackDir;
	public final net.minecraft.util.MetadataSerializer metadataSerializer;
	private ResourcePack serverContainer;
	private final ReentrantLock lock = new ReentrantLock();
	private ListenableFuture<Object> downloadTask;
	private List<ResourcePackLoader.Entry> availableResourcePacks = Lists.newArrayList();
	private List<ResourcePackLoader.Entry> selectedResourcePacks = Lists.newArrayList();

	public ResourcePackLoader(File file, File file2, ResourcePack resourcePack, net.minecraft.util.MetadataSerializer metadataSerializer, GameOptions gameOptions) {
		this.resourcePackDir = file;
		this.serverResourcePackDir = file2;
		this.defaultResourcePack = resourcePack;
		this.metadataSerializer = metadataSerializer;
		this.initResourcePackDir();
		this.initResourcePacks();
		Iterator<String> iterator = gameOptions.resourcePacks.iterator();

		while (iterator.hasNext()) {
			String string = (String)iterator.next();

			for (ResourcePackLoader.Entry entry : this.availableResourcePacks) {
				if (entry.getName().equals(string)) {
					if (entry.getFormat() == 1 || gameOptions.incompatibleResourcePacks.contains(entry.getName())) {
						this.selectedResourcePacks.add(entry);
						break;
					}

					iterator.remove();
					LOGGER.warn("Removed selected resource pack {} because it's no longer compatible", new Object[]{entry.getName()});
				}
			}
		}
	}

	private void initResourcePackDir() {
		if (this.resourcePackDir.exists()) {
			if (!this.resourcePackDir.isDirectory() && (!this.resourcePackDir.delete() || !this.resourcePackDir.mkdirs())) {
				LOGGER.warn("Unable to recreate resourcepack folder, it exists but is not a directory: " + this.resourcePackDir);
			}
		} else if (!this.resourcePackDir.mkdirs()) {
			LOGGER.warn("Unable to create resourcepack folder: " + this.resourcePackDir);
		}
	}

	private List<File> getResourcePacks() {
		return this.resourcePackDir.isDirectory() ? Arrays.asList(this.resourcePackDir.listFiles(FILE_FILTER)) : Collections.emptyList();
	}

	public void initResourcePacks() {
		List<ResourcePackLoader.Entry> list = Lists.newArrayList();

		for (File file : this.getResourcePacks()) {
			ResourcePackLoader.Entry entry = new ResourcePackLoader.Entry(file);
			if (!this.availableResourcePacks.contains(entry)) {
				try {
					entry.loadIcon();
					list.add(entry);
				} catch (Exception var6) {
					list.remove(entry);
				}
			} else {
				int i = this.availableResourcePacks.indexOf(entry);
				if (i > -1 && i < this.availableResourcePacks.size()) {
					list.add(this.availableResourcePacks.get(i));
				}
			}
		}

		this.availableResourcePacks.removeAll(list);

		for (ResourcePackLoader.Entry entry2 : this.availableResourcePacks) {
			entry2.close();
		}

		this.availableResourcePacks = list;
	}

	public List<ResourcePackLoader.Entry> getAvailableResourcePacks() {
		return ImmutableList.copyOf(this.availableResourcePacks);
	}

	public List<ResourcePackLoader.Entry> getSelectedResourcePacks() {
		return ImmutableList.copyOf(this.selectedResourcePacks);
	}

	public void setSelectedResourcePacks(List<ResourcePackLoader.Entry> selectedResourcePacks) {
		this.selectedResourcePacks.clear();
		this.selectedResourcePacks.addAll(selectedResourcePacks);
	}

	public File getResourcePackDir() {
		return this.resourcePackDir;
	}

	public ListenableFuture<Object> downloadResourcePack(String url, String hash) {
		String string;
		if (hash.matches("^[a-f0-9]{40}$")) {
			string = hash;
		} else {
			string = "legacy";
		}

		final File file = new File(this.serverResourcePackDir, string);
		this.lock.lock();

		try {
			this.clear();
			if (file.exists() && hash.length() == 40) {
				try {
					String string3 = Hashing.sha1().hashBytes(Files.toByteArray(file)).toString();
					if (string3.equals(hash)) {
						return this.loadServerPack(file);
					}

					LOGGER.warn("File " + file + " had wrong hash (expected " + hash + ", found " + string3 + "). Deleting it.");
					FileUtils.deleteQuietly(file);
				} catch (IOException var13) {
					LOGGER.warn("File " + file + " couldn't be hashed. Deleting it.", var13);
					FileUtils.deleteQuietly(file);
				}
			}

			this.deleteOldServerPack();
			final ProgressScreen progressScreen = new ProgressScreen();
			Map<String, String> map = MinecraftClient.getSessionInfoMap();
			final MinecraftClient minecraftClient = MinecraftClient.getInstance();
			Futures.getUnchecked(minecraftClient.submit(new Runnable() {
				public void run() {
					minecraftClient.setScreen(progressScreen);
				}
			}));
			final SettableFuture<Object> settableFuture = SettableFuture.create();
			this.downloadTask = NetworkUtils.downloadResourcePack(file, url, map, 52428800, progressScreen, minecraftClient.getNetworkProxy());
			Futures.addCallback(this.downloadTask, new FutureCallback<Object>() {
				public void onSuccess(Object object) {
					ResourcePackLoader.this.loadServerPack(file);
					settableFuture.set(null);
				}

				public void onFailure(Throwable t) {
					settableFuture.setException(t);
				}
			});
			return this.downloadTask;
		} finally {
			this.lock.unlock();
		}
	}

	private void deleteOldServerPack() {
		List<File> list = Lists.newArrayList(FileUtils.listFiles(this.serverResourcePackDir, TrueFileFilter.TRUE, null));
		Collections.sort(list, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
		int i = 0;

		for (File file : list) {
			if (i++ >= 10) {
				LOGGER.info("Deleting old server resource pack " + file.getName());
				FileUtils.deleteQuietly(file);
			}
		}
	}

	public ListenableFuture<Object> loadServerPack(File packZip) {
		this.serverContainer = new ZipResourcePack(packZip);
		return MinecraftClient.getInstance().reloadResourcesConcurrently();
	}

	public ResourcePack getServerContainer() {
		return this.serverContainer;
	}

	public void clear() {
		this.lock.lock();

		try {
			if (this.downloadTask != null) {
				this.downloadTask.cancel(true);
			}

			this.downloadTask = null;
			if (this.serverContainer != null) {
				this.serverContainer = null;
				MinecraftClient.getInstance().reloadResourcesConcurrently();
			}
		} finally {
			this.lock.unlock();
		}
	}

	public class Entry {
		private final File resourcePackFile;
		private ResourcePack resourcePack;
		private ResourcePackMetadata resourcePackData;
		private BufferedImage image;
		private Identifier id;

		private Entry(File file) {
			this.resourcePackFile = file;
		}

		public void loadIcon() throws IOException {
			this.resourcePack = (ResourcePack)(this.resourcePackFile.isDirectory()
				? new DirectoryResourcePack(this.resourcePackFile)
				: new ZipResourcePack(this.resourcePackFile));
			this.resourcePackData = this.resourcePack.parseMetadata(ResourcePackLoader.this.metadataSerializer, "pack");

			try {
				this.image = this.resourcePack.getIcon();
			} catch (IOException var2) {
			}

			if (this.image == null) {
				this.image = ResourcePackLoader.this.defaultResourcePack.getIcon();
			}

			this.close();
		}

		public void bindIcon(TextureManager textureManager) {
			if (this.id == null) {
				this.id = textureManager.registerDynamicTexture("texturepackicon", new NativeImageBackedTexture(this.image));
			}

			textureManager.bindTexture(this.id);
		}

		public void close() {
			if (this.resourcePack instanceof Closeable) {
				IOUtils.closeQuietly((Closeable)this.resourcePack);
			}
		}

		public ResourcePack getResourcePack() {
			return this.resourcePack;
		}

		public String getName() {
			return this.resourcePack.getName();
		}

		public String getDescription() {
			return this.resourcePackData == null
				? Formatting.RED + "Invalid pack.mcmeta (or missing 'pack' section)"
				: this.resourcePackData.getDescription().asFormattedString();
		}

		public int getFormat() {
			return this.resourcePackData.getPackFormat();
		}

		public boolean equals(Object object) {
			if (this == object) {
				return true;
			} else {
				return object instanceof ResourcePackLoader.Entry ? this.toString().equals(object.toString()) : false;
			}
		}

		public int hashCode() {
			return this.toString().hashCode();
		}

		public String toString() {
			return String.format(
				"%s:%s:%d", this.resourcePackFile.getName(), this.resourcePackFile.isDirectory() ? "folder" : "zip", this.resourcePackFile.lastModified()
			);
		}
	}
}
