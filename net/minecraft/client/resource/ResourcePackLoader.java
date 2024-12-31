package net.minecraft.client.resource;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.class_4277;
import net.minecraft.class_4285;
import net.minecraft.class_4286;
import net.minecraft.class_4456;
import net.minecraft.class_4458;
import net.minecraft.class_4461;
import net.minecraft.class_4463;
import net.minecraft.class_4465;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.resource.ZipResourcePack;
import net.minecraft.text.TranslatableText;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourcePackLoader implements class_4463 {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Pattern field_14991 = Pattern.compile("^[a-fA-F0-9]{40}$");
	private final class_4456 field_21038;
	private final File field_21039;
	private final ReentrantLock lock = new ReentrantLock();
	@Nullable
	private ListenableFuture<?> downloadTask;
	@Nullable
	private class_4286 field_8110;

	public ResourcePackLoader(File file, AssetsIndex assetsIndex) {
		this.field_21039 = file;
		this.field_21038 = new class_4285(assetsIndex);
	}

	@Override
	public <T extends class_4465> void method_21356(Map<String, T> map, class_4465.class_4467<T> arg) {
		T lv = class_4465.method_21359("vanilla", true, () -> this.field_21038, arg, class_4465.class_4466.BOTTOM);
		if (lv != null) {
			map.put("vanilla", lv);
		}

		if (this.field_8110 != null) {
			map.put("server", this.field_8110);
		}
	}

	public class_4456 method_19542() {
		return this.field_21038;
	}

	public static Map<String, String> method_13464() {
		Map<String, String> map = Maps.newHashMap();
		map.put("X-Minecraft-Username", MinecraftClient.getInstance().getSession().getUsername());
		map.put("X-Minecraft-UUID", MinecraftClient.getInstance().getSession().getUuid());
		map.put("X-Minecraft-Version", "1.13.2");
		map.put("X-Minecraft-Pack-Format", String.valueOf(4));
		map.put("User-Agent", "Minecraft Java/1.13.2");
		return map;
	}

	public ListenableFuture<?> method_19545(String string, String string2) {
		String string3 = DigestUtils.sha1Hex(string);
		final String string4 = field_14991.matcher(string2).matches() ? string2 : "";
		final File file = new File(this.field_21039, string3);
		this.lock.lock();

		try {
			this.clear();
			if (file.exists()) {
				if (this.method_13466(string4, file)) {
					return this.method_19544(file);
				}

				LOGGER.warn("Deleting file {}", file);
				FileUtils.deleteQuietly(file);
			}

			this.deleteOldServerPack();
			ProgressScreen progressScreen = new ProgressScreen();
			Map<String, String> map = method_13464();
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			Futures.getUnchecked(minecraftClient.submit(() -> minecraftClient.setScreen(progressScreen)));
			final SettableFuture<Object> settableFuture = SettableFuture.create();
			this.downloadTask = NetworkUtils.downloadResourcePack(file, string, map, 52428800, progressScreen, minecraftClient.getNetworkProxy());
			Futures.addCallback(this.downloadTask, new FutureCallback<Object>() {
				public void onSuccess(@Nullable Object object) {
					if (ResourcePackLoader.this.method_13466(string4, file)) {
						ResourcePackLoader.this.method_19544(file);
						settableFuture.set(null);
					} else {
						ResourcePackLoader.LOGGER.warn("Deleting file {}", file);
						FileUtils.deleteQuietly(file);
					}
				}

				public void onFailure(Throwable throwable) {
					FileUtils.deleteQuietly(file);
					settableFuture.setException(throwable);
				}
			});
			return this.downloadTask;
		} finally {
			this.lock.unlock();
		}
	}

	public void clear() {
		this.lock.lock();

		try {
			if (this.downloadTask != null) {
				this.downloadTask.cancel(true);
			}

			this.downloadTask = null;
			if (this.field_8110 != null) {
				this.field_8110 = null;
				MinecraftClient.getInstance().reloadResourcesConcurrently();
			}
		} finally {
			this.lock.unlock();
		}
	}

	private boolean method_13466(String string, File file) {
		try {
			String string2 = DigestUtils.sha1Hex(new FileInputStream(file));
			if (string.isEmpty()) {
				LOGGER.info("Found file {} without verification hash", file);
				return true;
			}

			if (string2.toLowerCase(Locale.ROOT).equals(string.toLowerCase(Locale.ROOT))) {
				LOGGER.info("Found file {} matching requested hash {}", file, string);
				return true;
			}

			LOGGER.warn("File {} had wrong hash (expected {}, found {}).", file, string, string2);
		} catch (IOException var4) {
			LOGGER.warn("File {} couldn't be hashed.", file, var4);
		}

		return false;
	}

	private void deleteOldServerPack() {
		try {
			List<File> list = Lists.newArrayList(FileUtils.listFiles(this.field_21039, TrueFileFilter.TRUE, null));
			list.sort(LastModifiedFileComparator.LASTMODIFIED_REVERSE);
			int i = 0;

			for (File file : list) {
				if (i++ >= 10) {
					LOGGER.info("Deleting old server resource pack {}", file.getName());
					FileUtils.deleteQuietly(file);
				}
			}
		} catch (IllegalArgumentException var5) {
			LOGGER.error("Error while deleting old server resource pack : {}", var5.getMessage());
		}
	}

	public ListenableFuture<Object> method_19544(File file) {
		class_4458 lv = null;
		class_4277 lv2 = null;

		try {
			ZipResourcePack zipResourcePack = new ZipResourcePack(file);
			Throwable var5 = null;

			try {
				lv = zipResourcePack.method_21329(class_4458.field_21894);

				try {
					InputStream inputStream = zipResourcePack.method_21330("pack.png");
					Throwable var7 = null;

					try {
						lv2 = class_4277.method_19472(inputStream);
					} catch (Throwable var34) {
						var7 = var34;
						throw var34;
					} finally {
						if (inputStream != null) {
							if (var7 != null) {
								try {
									inputStream.close();
								} catch (Throwable var33) {
									var7.addSuppressed(var33);
								}
							} else {
								inputStream.close();
							}
						}
					}
				} catch (IllegalArgumentException | IOException var36) {
				}
			} catch (Throwable var37) {
				var5 = var37;
				throw var37;
			} finally {
				if (zipResourcePack != null) {
					if (var5 != null) {
						try {
							zipResourcePack.close();
						} catch (Throwable var32) {
							var5.addSuppressed(var32);
						}
					} else {
						zipResourcePack.close();
					}
				}
			}
		} catch (IOException var39) {
		}

		if (lv == null) {
			return Futures.immediateFailedFuture(new RuntimeException("Invalid resourcepack"));
		} else {
			this.field_8110 = new class_4286(
				"server",
				true,
				() -> new ZipResourcePack(file),
				new TranslatableText("resourcePack.server.name"),
				lv.method_21336(),
				class_4461.method_21344(lv.method_21337()),
				class_4465.class_4466.TOP,
				true,
				lv2
			);
			return MinecraftClient.getInstance().reloadResourcesConcurrently();
		}
	}
}
