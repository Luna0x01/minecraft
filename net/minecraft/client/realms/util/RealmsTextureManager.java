package net.minecraft.client.realms.util;

import com.google.common.collect.Maps;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
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
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsTextureManager {
	private static final Map<String, RealmsTextureManager.RealmsTexture> textures = Maps.newHashMap();
	static final Map<String, Boolean> skinFetchStatus = Maps.newHashMap();
	static final Map<String, String> fetchedSkins = Maps.newHashMap();
	static final Logger LOGGER = LogManager.getLogger();
	private static final Identifier ISLES = new Identifier("textures/gui/presets/isles.png");

	public static void bindWorldTemplate(String id, @Nullable String image) {
		if (image == null) {
			RenderSystem.setShaderTexture(0, ISLES);
		} else {
			int i = getTextureId(id, image);
			RenderSystem.setShaderTexture(0, i);
		}
	}

	public static void withBoundFace(String uuid, Runnable r) {
		bindFace(uuid);
		r.run();
	}

	private static void bindDefaultFace(UUID uuid) {
		RenderSystem.setShaderTexture(0, DefaultSkinHelper.getTexture(uuid));
	}

	private static void bindFace(String uuid) {
		UUID uUID = UUIDTypeAdapter.fromString(uuid);
		if (textures.containsKey(uuid)) {
			int i = ((RealmsTextureManager.RealmsTexture)textures.get(uuid)).textureId;
			RenderSystem.setShaderTexture(0, i);
		} else if (skinFetchStatus.containsKey(uuid)) {
			if (!(Boolean)skinFetchStatus.get(uuid)) {
				bindDefaultFace(uUID);
			} else if (fetchedSkins.containsKey(uuid)) {
				int j = getTextureId(uuid, (String)fetchedSkins.get(uuid));
				RenderSystem.setShaderTexture(0, j);
			} else {
				bindDefaultFace(uUID);
			}
		} else {
			skinFetchStatus.put(uuid, false);
			bindDefaultFace(uUID);
			Thread thread = new Thread("Realms Texture Downloader") {
				public void run() {
					Map<Type, MinecraftProfileTexture> map = RealmsUtil.getTextures(uuid);
					if (map.containsKey(Type.SKIN)) {
						MinecraftProfileTexture minecraftProfileTexture = (MinecraftProfileTexture)map.get(Type.SKIN);
						String string = minecraftProfileTexture.getUrl();
						HttpURLConnection httpURLConnection = null;
						RealmsTextureManager.LOGGER.debug("Downloading http texture from {}", string);

						try {
							try {
								httpURLConnection = (HttpURLConnection)new URL(string).openConnection(MinecraftClient.getInstance().getNetworkProxy());
								httpURLConnection.setDoInput(true);
								httpURLConnection.setDoOutput(false);
								httpURLConnection.connect();
								if (httpURLConnection.getResponseCode() / 100 != 2) {
									RealmsTextureManager.skinFetchStatus.remove(uuid);
									return;
								}

								BufferedImage bufferedImage;
								try {
									bufferedImage = ImageIO.read(httpURLConnection.getInputStream());
								} catch (Exception var17) {
									RealmsTextureManager.skinFetchStatus.remove(uuid);
									return;
								} finally {
									IOUtils.closeQuietly(httpURLConnection.getInputStream());
								}

								bufferedImage = new SkinProcessor().process(bufferedImage);
								ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
								ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
								RealmsTextureManager.fetchedSkins.put(uuid, new Base64().encodeToString(byteArrayOutputStream.toByteArray()));
								RealmsTextureManager.skinFetchStatus.put(uuid, true);
							} catch (Exception var19) {
								RealmsTextureManager.LOGGER.error("Couldn't download http texture", var19);
								RealmsTextureManager.skinFetchStatus.remove(uuid);
							}
						} finally {
							if (httpURLConnection != null) {
								httpURLConnection.disconnect();
							}
						}
					} else {
						RealmsTextureManager.skinFetchStatus.put(uuid, true);
					}
				}
			};
			thread.setDaemon(true);
			thread.start();
		}
	}

	private static int getTextureId(String id, String image) {
		RealmsTextureManager.RealmsTexture realmsTexture = (RealmsTextureManager.RealmsTexture)textures.get(id);
		if (realmsTexture != null && realmsTexture.image.equals(image)) {
			return realmsTexture.textureId;
		} else {
			int i;
			if (realmsTexture != null) {
				i = realmsTexture.textureId;
			} else {
				i = GlStateManager._genTexture();
			}

			IntBuffer intBuffer = null;
			int k = 0;
			int l = 0;

			try {
				InputStream inputStream = new ByteArrayInputStream(new Base64().decode(image));

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
			} catch (IOException var13) {
				var13.printStackTrace();
			}

			RenderSystem.activeTexture(33984);
			RenderSystem.bindTextureForSetup(i);
			TextureUtil.initTexture(intBuffer, k, l);
			textures.put(id, new RealmsTextureManager.RealmsTexture(image, i));
			return i;
		}
	}

	public static class RealmsTexture {
		final String image;
		final int textureId;

		public RealmsTexture(String image, int textureId) {
			this.image = image;
			this.textureId = textureId;
		}
	}
}
