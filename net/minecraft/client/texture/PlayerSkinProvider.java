package net.minecraft.client.texture;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import net.minecraft.class_4277;
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
					try {
						return MinecraftClient.getInstance().getSessionService().getTextures(gameProfile, false);
					} catch (Throwable var3) {
						return Maps.newHashMap();
					}
				}
			});
	}

	public Identifier loadSkin(MinecraftProfileTexture profileTexture, Type type) {
		return this.loadSkin(profileTexture, type, null);
	}

	public Identifier loadSkin(MinecraftProfileTexture profileTexture, Type type, @Nullable PlayerSkinProvider.class_1890 callback) {
		String string = Hashing.sha1().hashUnencodedChars(profileTexture.getHash()).toString();
		final Identifier identifier = new Identifier("skins/" + string);
		Texture texture = this.field_8116.getTexture(identifier);
		if (texture != null) {
			if (callback != null) {
				callback.onSkinTextureAvailable(type, identifier, profileTexture);
			}
		} else {
			File file = new File(this.skinCacheDir, string.length() > 2 ? string.substring(0, 2) : "xx");
			File file2 = new File(file, string);
			final BufferedImageSkinProvider bufferedImageSkinProvider = type == Type.SKIN ? new DownloadedSkinParser() : null;
			PlayerSkinTexture playerSkinTexture = new PlayerSkinTexture(
				file2, profileTexture.getUrl(), DefaultSkinHelper.getTexture(), new BufferedImageSkinProvider() {
					@Override
					public class_4277 method_19128(class_4277 arg) {
						return bufferedImageSkinProvider != null ? bufferedImageSkinProvider.method_19128(arg) : arg;
					}

					@Override
					public void setAvailable() {
						if (bufferedImageSkinProvider != null) {
							bufferedImageSkinProvider.setAvailable();
						}

						if (callback != null) {
							callback.onSkinTextureAvailable(type, identifier, profileTexture);
						}
					}
				}
			);
			this.field_8116.loadTexture(identifier, playerSkinTexture);
		}

		return identifier;
	}

	public void loadProfileSkin(GameProfile profile, PlayerSkinProvider.class_1890 callback, boolean bl) {
		executorService.submit(() -> {
			Map<Type, MinecraftProfileTexture> map = Maps.newHashMap();

			try {
				map.putAll(this.sessionService.getTextures(profile, bl));
			} catch (InsecureTextureException var7) {
			}

			if (map.isEmpty()) {
				profile.getProperties().clear();
				if (profile.getId().equals(MinecraftClient.getInstance().getSession().getProfile().getId())) {
					profile.getProperties().putAll(MinecraftClient.getInstance().getSessionProperties());
					map.putAll(this.sessionService.getTextures(profile, false));
				} else {
					this.sessionService.fillProfileProperties(profile, bl);

					try {
						map.putAll(this.sessionService.getTextures(profile, bl));
					} catch (InsecureTextureException var6) {
					}
				}
			}

			MinecraftClient.getInstance().submit(() -> {
				if (map.containsKey(Type.SKIN)) {
					this.loadSkin((MinecraftProfileTexture)map.get(Type.SKIN), Type.SKIN, callback);
				}

				if (map.containsKey(Type.CAPE)) {
					this.loadSkin((MinecraftProfileTexture)map.get(Type.CAPE), Type.CAPE, callback);
				}
			});
		});
	}

	public Map<Type, MinecraftProfileTexture> getTextures(GameProfile profile) {
		return (Map<Type, MinecraftProfileTexture>)this.skinCache.getUnchecked(profile);
	}

	public interface class_1890 {
		void onSkinTextureAvailable(Type type, Identifier identifier, MinecraftProfileTexture minecraftProfileTexture);
	}
}
