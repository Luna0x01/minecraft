package net.minecraft.client.util;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.ProgressListener;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetworkUtils {
	public static final ListeningExecutorService downloadExecutor = MoreExecutors.listeningDecorator(
		Executors.newCachedThreadPool(new ThreadFactoryBuilder().setDaemon(true).setNameFormat("Downloader %d").build())
	);
	private static final AtomicInteger ids = new AtomicInteger(0);
	private static final Logger LOGGER = LogManager.getLogger();

	public static String urlEncode(Map<String, Object> data) {
		StringBuilder stringBuilder = new StringBuilder();

		for (Entry<String, Object> entry : data.entrySet()) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append('&');
			}

			try {
				stringBuilder.append(URLEncoder.encode((String)entry.getKey(), "UTF-8"));
			} catch (UnsupportedEncodingException var6) {
				var6.printStackTrace();
			}

			if (entry.getValue() != null) {
				stringBuilder.append('=');

				try {
					stringBuilder.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
				} catch (UnsupportedEncodingException var5) {
					var5.printStackTrace();
				}
			}
		}

		return stringBuilder.toString();
	}

	public static String snoop(URL uRL, Map<String, Object> map, boolean bl, @Nullable Proxy proxy) {
		return snoop(uRL, urlEncode(map), bl, proxy);
	}

	private static String snoop(URL uRL, String string, boolean bl, @Nullable Proxy proxy) {
		try {
			if (proxy == null) {
				proxy = Proxy.NO_PROXY;
			}

			HttpURLConnection httpURLConnection = (HttpURLConnection)uRL.openConnection(proxy);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			httpURLConnection.setRequestProperty("Content-Length", "" + string.getBytes().length);
			httpURLConnection.setRequestProperty("Content-Language", "en-US");
			httpURLConnection.setUseCaches(false);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
			dataOutputStream.writeBytes(string);
			dataOutputStream.flush();
			dataOutputStream.close();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
			StringBuffer stringBuffer = new StringBuffer();

			String string2;
			while ((string2 = bufferedReader.readLine()) != null) {
				stringBuffer.append(string2);
				stringBuffer.append('\r');
			}

			bufferedReader.close();
			return stringBuffer.toString();
		} catch (Exception var9) {
			if (!bl) {
				LOGGER.error("Could not post to {}", uRL, var9);
			}

			return "";
		}
	}

	public static ListenableFuture<Object> downloadResourcePack(
		File resourcePackFile, String url, Map<String, String> sessionInfo, int timeout, @Nullable ProgressListener progressListener, Proxy clientProxy
	) {
		return downloadExecutor.submit(new Runnable() {
			public void run() {
				HttpURLConnection httpURLConnection = null;
				InputStream inputStream = null;
				OutputStream outputStream = null;
				if (progressListener != null) {
					progressListener.setTitleAndTask(CommonI18n.translate("resourcepack.downloading"));
					progressListener.setTask(CommonI18n.translate("resourcepack.requesting"));
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
					int i = httpURLConnection.getContentLength();
					if (progressListener != null) {
						progressListener.setTask(CommonI18n.translate("resourcepack.progress", String.format("%.2f", g / 1000.0F / 1000.0F)));
					}

					if (resourcePackFile.exists()) {
						long l = resourcePackFile.length();
						if (l == (long)i) {
							if (progressListener != null) {
								progressListener.setDone();
							}

							return;
						}

						NetworkUtils.LOGGER.warn("Deleting {} as it does not match what we currently have ({} vs our {}).", resourcePackFile, i, l);
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
						int j;
						if ((j = inputStream.read(bs)) < 0) {
							if (progressListener != null) {
								progressListener.setDone();
							}

							return;
						}

						f += (float)j;
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
							NetworkUtils.LOGGER.error("INTERRUPTED");
							if (progressListener != null) {
								progressListener.setDone();
							}
							break;
						}

						outputStream.write(bs, 0, j);
					}
				} catch (Throwable var16) {
					var16.printStackTrace();
					if (httpURLConnection != null) {
						InputStream inputStream2 = httpURLConnection.getErrorStream();

						try {
							NetworkUtils.LOGGER.error(IOUtils.toString(inputStream2));
						} catch (IOException var15) {
							var15.printStackTrace();
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
			}
		});
	}

	public static int getFreePort() throws IOException {
		ServerSocket serverSocket = null;
		int i = -1;

		try {
			serverSocket = new ServerSocket(0);
			i = serverSocket.getLocalPort();
		} finally {
			try {
				if (serverSocket != null) {
					serverSocket.close();
				}
			} catch (IOException var8) {
			}
		}

		return i;
	}
}
