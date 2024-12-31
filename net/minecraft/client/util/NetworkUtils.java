package net.minecraft.client.util;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import javax.annotation.Nullable;
import net.minecraft.class_4325;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ProgressListener;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetworkUtils {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final ListeningExecutorService downloadExecutor = MoreExecutors.listeningDecorator(
		Executors.newCachedThreadPool(
			new ThreadFactoryBuilder().setDaemon(true).setUncaughtExceptionHandler(new class_4325(LOGGER)).setNameFormat("Downloader %d").build()
		)
	);

	public static ListenableFuture<?> downloadResourcePack(
		File resourcePackFile, String url, Map<String, String> sessionInfo, int timeout, @Nullable ProgressListener progressListener, Proxy clientProxy
	) {
		return downloadExecutor.submit(() -> {
			HttpURLConnection httpURLConnection = null;
			InputStream inputStream = null;
			OutputStream outputStream = null;
			if (progressListener != null) {
				progressListener.method_21525(new TranslatableText("resourcepack.downloading"));
				progressListener.method_21526(new TranslatableText("resourcepack.requesting"));
			}

			try {
				byte[] bs = new byte[4096];
				URL uRL = new URL(url);
				httpURLConnection = (HttpURLConnection)uRL.openConnection(clientProxy);
				httpURLConnection.setInstanceFollowRedirects(true);
				float f = 0.0F;
				float g = (float)sessionInfo.entrySet().size();

				for (Entry<String, String> entry : sessionInfo.entrySet()) {
					httpURLConnection.setRequestProperty((String)entry.getKey(), (String)entry.getValue());
					if (progressListener != null) {
						progressListener.setProgressPercentage((int)(++f / g * 100.0F));
					}
				}

				inputStream = httpURLConnection.getInputStream();
				g = (float)httpURLConnection.getContentLength();
				int j = httpURLConnection.getContentLength();
				if (progressListener != null) {
					progressListener.method_21526(new TranslatableText("resourcepack.progress", String.format(Locale.ROOT, "%.2f", g / 1000.0F / 1000.0F)));
				}

				if (resourcePackFile.exists()) {
					long l = resourcePackFile.length();
					if (l == (long)j) {
						if (progressListener != null) {
							progressListener.setDone();
						}

						return;
					}

					LOGGER.warn("Deleting {} as it does not match what we currently have ({} vs our {}).", resourcePackFile, j, l);
					FileUtils.deleteQuietly(resourcePackFile);
				} else if (resourcePackFile.getParentFile() != null) {
					resourcePackFile.getParentFile().mkdirs();
				}

				outputStream = new DataOutputStream(new FileOutputStream(resourcePackFile));
				if (timeout > 0 && g > (float)timeout) {
					if (progressListener != null) {
						progressListener.setDone();
					}

					throw new IOException("Filesize is bigger than maximum allowed (file is " + f + ", limit is " + timeout + ")");
				}

				while (true) {
					int k;
					if ((k = inputStream.read(bs)) < 0) {
						if (progressListener != null) {
							progressListener.setDone();
						}

						return;
					}

					f += (float)k;
					if (progressListener != null) {
						progressListener.setProgressPercentage((int)(f / g * 100.0F));
					}

					if (timeout > 0 && f > (float)timeout) {
						if (progressListener != null) {
							progressListener.setDone();
						}

						throw new IOException("Filesize was bigger than maximum allowed (got >= " + f + ", limit was " + timeout + ")");
					}

					if (Thread.interrupted()) {
						LOGGER.error("INTERRUPTED");
						if (progressListener != null) {
							progressListener.setDone();
						}
						break;
					}

					outputStream.write(bs, 0, k);
				}
			} catch (Throwable var21) {
				var21.printStackTrace();
				if (httpURLConnection != null) {
					InputStream inputStream2 = httpURLConnection.getErrorStream();

					try {
						LOGGER.error(IOUtils.toString(inputStream2));
					} catch (IOException var20) {
						var20.printStackTrace();
					}
				}

				if (progressListener != null) {
					progressListener.setDone();
				}

				return;
			} finally {
				IOUtils.closeQuietly(inputStream);
				IOUtils.closeQuietly(outputStream);
			}
		});
	}

	public static int getFreePort() {
		try {
			ServerSocket serverSocket = new ServerSocket(0);
			Throwable var1 = null;

			int var2;
			try {
				var2 = serverSocket.getLocalPort();
			} catch (Throwable var12) {
				var1 = var12;
				throw var12;
			} finally {
				if (serverSocket != null) {
					if (var1 != null) {
						try {
							serverSocket.close();
						} catch (Throwable var11) {
							var1.addSuppressed(var11);
						}
					} else {
						serverSocket.close();
					}
				}
			}

			return var2;
		} catch (IOException var14) {
			return 25564;
		}
	}
}
