package com.mojang.realmsclient.util;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.util.UUIDTypeAdapter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsScreen;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsTextureManager {
	private static final Map<String, RealmsTextureManager.RealmsTexture> textures = new HashMap();
	private static final Map<String, Boolean> skinFetchStatus = new HashMap();
	private static final Map<String, String> fetchedSkins = new HashMap();
	private static final Logger LOGGER = LogManager.getLogger();

	public static void bindWorldTemplate(String string, String string2) {
		if (string2 == null) {
			RealmsScreen.bind("textures/gui/presets/isles.png");
		} else {
			int i = getTextureId(string, string2);
			GlStateManager.bindTexture(i);
		}
	}

	public static void withBoundFace(String string, Runnable runnable) {
		GLX.withTextureRestore(() -> {
			bindFace(string);
			runnable.run();
		});
	}

	private static void bindDefaultFace(UUID uUID) {
		RealmsScreen.bind((uUID.hashCode() & 1) == 1 ? "minecraft:textures/entity/alex.png" : "minecraft:textures/entity/steve.png");
	}

	private static void bindFace(String string) {
		UUID uUID = UUIDTypeAdapter.fromString(string);
		if (textures.containsKey(string)) {
			GlStateManager.bindTexture(((RealmsTextureManager.RealmsTexture)textures.get(string)).textureId);
		} else if (skinFetchStatus.containsKey(string)) {
			if (!(Boolean)skinFetchStatus.get(string)) {
				bindDefaultFace(uUID);
			} else if (fetchedSkins.containsKey(string)) {
				int i = getTextureId(string, (String)fetchedSkins.get(string));
				GlStateManager.bindTexture(i);
			} else {
				bindDefaultFace(uUID);
			}
		} else {
			skinFetchStatus.put(string, false);
			bindDefaultFace(uUID);
			Thread thread = new Thread("Realms Texture Downloader") {
				public void run() {
					Map<Type, MinecraftProfileTexture> map = RealmsUtil.getTextures(string);
					if (map.containsKey(Type.SKIN)) {
						MinecraftProfileTexture minecraftProfileTexture = (MinecraftProfileTexture)map.get(Type.SKIN);
						String string = minecraftProfileTexture.getUrl();
						HttpURLConnection httpURLConnection = null;
						RealmsTextureManager.LOGGER.debug("Downloading http texture from {}", string);

						try {
							try {
								httpURLConnection = (HttpURLConnection)new URL(string).openConnection(Realms.getProxy());
								httpURLConnection.setDoInput(true);
								httpURLConnection.setDoOutput(false);
								httpURLConnection.connect();
								if (httpURLConnection.getResponseCode() / 100 != 2) {
									RealmsTextureManager.skinFetchStatus.remove(string);
									return;
								}

								BufferedImage bufferedImage;
								try {
									bufferedImage = ImageIO.read(httpURLConnection.getInputStream());
								} catch (Exception var17) {
									RealmsTextureManager.skinFetchStatus.remove(string);
									return;
								} finally {
									IOUtils.closeQuietly(httpURLConnection.getInputStream());
								}

								bufferedImage = new SkinProcessor().process(bufferedImage);
								ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
								ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
								RealmsTextureManager.fetchedSkins.put(string, DatatypeConverter.printBase64Binary(byteArrayOutputStream.toByteArray()));
								RealmsTextureManager.skinFetchStatus.put(string, true);
							} catch (Exception var19) {
								RealmsTextureManager.LOGGER.error("Couldn't download http texture", var19);
								RealmsTextureManager.skinFetchStatus.remove(string);
							}
						} finally {
							if (httpURLConnection != null) {
								httpURLConnection.disconnect();
							}
						}
					} else {
						RealmsTextureManager.skinFetchStatus.put(string, true);
					}
				}
			};
			thread.setDaemon(true);
			thread.start();
		}
	}

	private static int getTextureId(String string, String string2) {
		int i;
		if (textures.containsKey(string)) {
			RealmsTextureManager.RealmsTexture realmsTexture = (RealmsTextureManager.RealmsTexture)textures.get(string);
			if (realmsTexture.image.equals(string2)) {
				return realmsTexture.textureId;
			}

			GlStateManager.deleteTexture(realmsTexture.textureId);
			i = realmsTexture.textureId;
		} else {
			i = GlStateManager.genTexture();
		}

		IntBuffer intBuffer = null;
		int k = 0;
		int l = 0;

		try {
			InputStream inputStream = new ByteArrayInputStream(new Base64().decode(string2));

			BufferedImage bufferedImage;
			try {
				bufferedImage = ImageIO.read(inputStream);
			} finally {
				IOUtils.closeQuietly(inputStream);
			}

			k = bufferedImage.getWidth();
			l = bufferedImage.getHeight();
			int[] is = new int[k * l];
			bufferedImage.getRGB(0, 0, k, l, is, 0, k);
			intBuffer = ByteBuffer.allocateDirect(4 * k * l).order(ByteOrder.nativeOrder()).asIntBuffer();
			intBuffer.put(is);
			intBuffer.flip();
		} catch (IOException var12) {
			var12.printStackTrace();
		}

		GlStateManager.activeTexture(GLX.GL_TEXTURE0);
		GlStateManager.bindTexture(i);
		TextureUtil.initTexture(intBuffer, k, l);
		textures.put(string, new RealmsTextureManager.RealmsTexture(string2, i));
		return i;
	}

	public static class RealmsTexture {
		String image;
		int textureId;

		public RealmsTexture(String string, int i) {
			this.image = string;
			this.textureId = i;
		}
	}
}
