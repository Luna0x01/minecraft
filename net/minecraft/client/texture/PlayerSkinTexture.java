package net.minecraft.client.texture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferedImageSkinProvider;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerSkinTexture extends ResourceTexture {
	private static final Logger field_8079 = LogManager.getLogger();
	private static final AtomicInteger field_8080 = new AtomicInteger(0);
	private final File field_8081;
	private final String field_6548;
	private final BufferedImageSkinProvider field_6549;
	private BufferedImage field_6550;
	private Thread field_6551;
	private boolean field_6553;

	public PlayerSkinTexture(File file, String string, Identifier identifier, BufferedImageSkinProvider bufferedImageSkinProvider) {
		super(identifier);
		this.field_8081 = file;
		this.field_6548 = string;
		this.field_6549 = bufferedImageSkinProvider;
	}

	private void method_6997() {
		if (!this.field_6553) {
			if (this.field_6550 != null) {
				if (this.field_6555 != null) {
					this.clearGlId();
				}

				TextureUtil.method_5858(super.getGlId(), this.field_6550);
				this.field_6553 = true;
			}
		}
	}

	@Override
	public int getGlId() {
		this.method_6997();
		return super.getGlId();
	}

	public void method_6994(BufferedImage bufferedImage) {
		this.field_6550 = bufferedImage;
		if (this.field_6549 != null) {
			this.field_6549.setAvailable();
		}
	}

	@Override
	public void load(ResourceManager manager) throws IOException {
		if (this.field_6550 == null && this.field_6555 != null) {
			super.load(manager);
		}

		if (this.field_6551 == null) {
			if (this.field_8081 != null && this.field_8081.isFile()) {
				field_8079.debug("Loading http texture from local cache ({})", new Object[]{this.field_8081});

				try {
					this.field_6550 = ImageIO.read(this.field_8081);
					if (this.field_6549 != null) {
						this.method_6994(this.field_6549.parseSkin(this.field_6550));
					}
				} catch (IOException var3) {
					field_8079.error("Couldn't load skin " + this.field_8081, var3);
					this.method_6993();
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
				PlayerSkinTexture.field_8079
					.debug("Downloading http texture from {} to {}", new Object[]{PlayerSkinTexture.this.field_6548, PlayerSkinTexture.this.field_8081});

				try {
					httpURLConnection = (HttpURLConnection)new URL(PlayerSkinTexture.this.field_6548).openConnection(MinecraftClient.getInstance().getNetworkProxy());
					httpURLConnection.setDoInput(true);
					httpURLConnection.setDoOutput(false);
					httpURLConnection.connect();
					if (httpURLConnection.getResponseCode() / 100 == 2) {
						BufferedImage bufferedImage;
						if (PlayerSkinTexture.this.field_8081 != null) {
							FileUtils.copyInputStreamToFile(httpURLConnection.getInputStream(), PlayerSkinTexture.this.field_8081);
							bufferedImage = ImageIO.read(PlayerSkinTexture.this.field_8081);
						} else {
							bufferedImage = TextureUtil.create(httpURLConnection.getInputStream());
						}

						if (PlayerSkinTexture.this.field_6549 != null) {
							bufferedImage = PlayerSkinTexture.this.field_6549.parseSkin(bufferedImage);
						}

						PlayerSkinTexture.this.method_6994(bufferedImage);
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
		this.field_6551.start();
	}
}
