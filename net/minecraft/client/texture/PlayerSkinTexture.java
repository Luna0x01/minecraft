package net.minecraft.client.texture;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.minecraft.class_4277;
import net.minecraft.class_4325;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferedImageSkinProvider;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerSkinTexture extends ResourceTexture {
	private static final Logger field_8079 = LogManager.getLogger();
	private static final AtomicInteger field_8080 = new AtomicInteger(0);
	@Nullable
	private final File field_8081;
	private final String field_6548;
	@Nullable
	private final BufferedImageSkinProvider field_6549;
	@Nullable
	private Thread field_6551;
	private volatile boolean field_20980;

	public PlayerSkinTexture(@Nullable File file, String string, Identifier identifier, @Nullable BufferedImageSkinProvider bufferedImageSkinProvider) {
		super(identifier);
		this.field_8081 = file;
		this.field_6548 = string;
		this.field_6549 = bufferedImageSkinProvider;
	}

	private void method_19451(class_4277 arg) {
		TextureUtil.prepareImage(this.getGlId(), arg.method_19458(), arg.method_19478());
		arg.method_19466(0, 0, 0, false);
	}

	public void method_19450(class_4277 arg) {
		if (this.field_6549 != null) {
			this.field_6549.setAvailable();
		}

		synchronized (this) {
			this.method_19451(arg);
			this.field_20980 = true;
		}
	}

	@Override
	public void load(ResourceManager manager) throws IOException {
		if (!this.field_20980) {
			synchronized (this) {
				super.load(manager);
				this.field_20980 = true;
			}
		}

		if (this.field_6551 == null) {
			if (this.field_8081 != null && this.field_8081.isFile()) {
				field_8079.debug("Loading http texture from local cache ({})", this.field_8081);
				class_4277 lv = null;

				try {
					try {
						lv = class_4277.method_19472(new FileInputStream(this.field_8081));
						if (this.field_6549 != null) {
							lv = this.field_6549.method_19128(lv);
						}

						this.method_19450(lv);
					} catch (IOException var8) {
						field_8079.error("Couldn't load skin {}", this.field_8081, var8);
						this.method_6993();
					}
				} finally {
					if (lv != null) {
						lv.close();
					}
				}
			} else {
				this.method_6993();
			}
		}
	}

	protected void method_6993() {
		this.field_6551 = new Thread("Texture Downloader #" + field_8080.incrementAndGet()) {
			public void run() {
				HttpURLConnection httpURLConnection = null;
				PlayerSkinTexture.field_8079.debug("Downloading http texture from {} to {}", PlayerSkinTexture.this.field_6548, PlayerSkinTexture.this.field_8081);

				try {
					httpURLConnection = (HttpURLConnection)new URL(PlayerSkinTexture.this.field_6548).openConnection(MinecraftClient.getInstance().getNetworkProxy());
					httpURLConnection.setDoInput(true);
					httpURLConnection.setDoOutput(false);
					httpURLConnection.connect();
					if (httpURLConnection.getResponseCode() / 100 == 2) {
						InputStream inputStream;
						if (PlayerSkinTexture.this.field_8081 != null) {
							FileUtils.copyInputStreamToFile(httpURLConnection.getInputStream(), PlayerSkinTexture.this.field_8081);
							inputStream = new FileInputStream(PlayerSkinTexture.this.field_8081);
						} else {
							inputStream = httpURLConnection.getInputStream();
						}

						MinecraftClient.getInstance().submit(() -> {
							class_4277 lv = null;

							try {
								lv = class_4277.method_19472(inputStream);
								if (PlayerSkinTexture.this.field_6549 != null) {
									lv = PlayerSkinTexture.this.field_6549.method_19128(lv);
								}

								class_4277 lv2 = lv;
								MinecraftClient.getInstance().submit(() -> PlayerSkinTexture.this.method_19450(lv2));
							} catch (IOException var7x) {
								PlayerSkinTexture.field_8079.warn("Error while loading the skin texture", var7x);
							} finally {
								if (lv != null) {
									lv.close();
								}

								IOUtils.closeQuietly(inputStream);
							}
						});
						return;
					}
				} catch (Exception var6) {
					PlayerSkinTexture.field_8079.error("Couldn't download http texture", var6);
					return;
				} finally {
					if (httpURLConnection != null) {
						httpURLConnection.disconnect();
					}
				}
			}
		};
		this.field_6551.setDaemon(true);
		this.field_6551.setUncaughtExceptionHandler(new class_4325(field_8079));
		this.field_6551.start();
	}
}
