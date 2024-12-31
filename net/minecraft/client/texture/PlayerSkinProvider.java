package net.minecraft.client.texture;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferedImageSkinProvider;
import net.minecraft.client.render.DownloadedSkinParser;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;

public class PlayerSkinProvider {
	private static final ExecutorService executorService = new ThreadPoolExecutor(0, 2, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue());
	private final TextureManager field_8116;
	private final File skinCacheDir;
	private final MinecraftSessionService sessionService;
	private final LoadingCache<GameProfile, Map<Type, MinecraftProfileTexture>> skinCache;

	public PlayerSkinProvider(TextureManager textureManager, File file, MinecraftSessionService minecraftSessionService) {
		this.field_8116 = textureManager;
		this.skinCacheDir = file;
		this.sessionService = minecraftSessionService;
		this.skinCache = CacheBuilder.newBuilder()
			.expireAfterAccess(15L, TimeUnit.SECONDS)
			.build(new CacheLoader<GameProfile, Map<Type, MinecraftProfileTexture>>() {
				public Map<Type, MinecraftProfileTexture> load(GameProfile gameProfile) throws Exception {
					return MinecraftClient.getInstance().getSessionService().getTextures(gameProfile, false);
				}
			});
	}

	public Identifier loadSkin(MinecraftProfileTexture profileTexture, Type type) {
		return this.loadSkin(profileTexture, type, null);
	}

	public Identifier loadSkin(MinecraftProfileTexture profileTexture, Type type, @Nullable PlayerSkinProvider.class_1890 callback) {
		final Identifier identifier = new Identifier("skins/" + profileTexture.getHash());
		Texture texture = this.field_8116.getTexture(identifier);
		if (texture != null) {
			if (callback != null) {
				callback.method_7047(type, identifier, profileTexture);
			}
		} else {
			File file = new File(this.skinCacheDir, profileTexture.getHash().length() > 2 ? profileTexture.getHash().substring(0, 2) : "xx");
			File file2 = new File(file, profileTexture.getHash());
			final BufferedImageSkinProvider bufferedImageSkinProvider = type == Type.SKIN ? new DownloadedSkinParser() : null;
			PlayerSkinTexture playerSkinTexture = new PlayerSkinTexture(
				file2, profileTexture.getUrl(), DefaultSkinHelper.getTexture(), new BufferedImageSkinProvider() {
					@Override
					public BufferedImage parseSkin(BufferedImage image) {
						if (bufferedImageSkinProvider != null) {
							image = bufferedImageSkinProvider.parseSkin(image);
						}

						return image;
					}

					@Override
					public void setAvailable() {
						if (bufferedImageSkinProvider != null) {
							bufferedImageSkinProvider.setAvailable();
						}

						if (callback != null) {
							callback.method_7047(type, identifier, profileTexture);
						}
					}
				}
			);
			this.field_8116.loadTexture(identifier, playerSkinTexture);
		}

		return identifier;
	}

	public void loadProfileSkin(GameProfile profile, PlayerSkinProvider.class_1890 callback, boolean bl) {
		executorService.submit(new Runnable() {
			public void run() {
				final Map<Type, MinecraftProfileTexture> map = Maps.newHashMap();

				try {
					map.putAll(PlayerSkinProvider.this.sessionService.getTextures(profile, bl));
				} catch (InsecureTextureException var3) {
				}

				if (map.isEmpty() && profile.getId().equals(MinecraftClient.getInstance().getSession().getProfile().getId())) {
					profile.getProperties().clear();
					profile.getProperties().putAll(MinecraftClient.getInstance().getSessionProperties());
					map.putAll(PlayerSkinProvider.this.sessionService.getTextures(profile, false));
				}

				MinecraftClient.getInstance().submit(new Runnable() {
					public void run() {
						if (map.containsKey(Type.SKIN)) {
							PlayerSkinProvider.this.loadSkin((MinecraftProfileTexture)map.get(Type.SKIN), Type.SKIN, callback);
						}

						if (map.containsKey(Type.CAPE)) {
							PlayerSkinProvider.this.loadSkin((MinecraftProfileTexture)map.get(Type.CAPE), Type.CAPE, callback);
						}
					}
				});
			}
		});
	}

	public Map<Type, MinecraftProfileTexture> getTextures(GameProfile profile) {
		return (Map<Type, MinecraftProfileTexture>)this.skinCache.getUnchecked(profile);
	}

	public interface class_1890 {
		void method_7047(Type type, Identifier identifier, MinecraftProfileTexture minecraftProfileTexture);
	}
}
