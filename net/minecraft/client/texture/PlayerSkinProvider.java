package net.minecraft.client.texture;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class PlayerSkinProvider {
	private final TextureManager textureManager;
	private final File skinCacheDir;
	private final MinecraftSessionService sessionService;
	private final LoadingCache<GameProfile, Map<Type, MinecraftProfileTexture>> skinCache;

	public PlayerSkinProvider(TextureManager textureManager, File file, MinecraftSessionService minecraftSessionService) {
		this.textureManager = textureManager;
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

	public Identifier loadSkin(MinecraftProfileTexture minecraftProfileTexture, Type type) {
		return this.loadSkin(minecraftProfileTexture, type, null);
	}

	public Identifier loadSkin(
		MinecraftProfileTexture minecraftProfileTexture, Type type, @Nullable PlayerSkinProvider.SkinTextureAvailableCallback skinTextureAvailableCallback
	) {
		String string = Hashing.sha1().hashUnencodedChars(minecraftProfileTexture.getHash()).toString();
		Identifier identifier = new Identifier("skins/" + string);
		AbstractTexture abstractTexture = this.textureManager.getTexture(identifier);
		if (abstractTexture != null) {
			if (skinTextureAvailableCallback != null) {
				skinTextureAvailableCallback.onSkinTextureAvailable(type, identifier, minecraftProfileTexture);
			}
		} else {
			File file = new File(this.skinCacheDir, string.length() > 2 ? string.substring(0, 2) : "xx");
			File file2 = new File(file, string);
			PlayerSkinTexture playerSkinTexture = new PlayerSkinTexture(
				file2, minecraftProfileTexture.getUrl(), DefaultSkinHelper.getTexture(), type == Type.SKIN, () -> {
					if (skinTextureAvailableCallback != null) {
						skinTextureAvailableCallback.onSkinTextureAvailable(type, identifier, minecraftProfileTexture);
					}
				}
			);
			this.textureManager.registerTexture(identifier, playerSkinTexture);
		}

		return identifier;
	}

	public void loadSkin(GameProfile gameProfile, PlayerSkinProvider.SkinTextureAvailableCallback skinTextureAvailableCallback, boolean bl) {
		Runnable runnable = () -> {
			Map<Type, MinecraftProfileTexture> map = Maps.newHashMap();

			try {
				map.putAll(this.sessionService.getTextures(gameProfile, bl));
			} catch (InsecureTextureException var7) {
			}

			if (map.isEmpty()) {
				gameProfile.getProperties().clear();
				if (gameProfile.getId().equals(MinecraftClient.getInstance().getSession().getProfile().getId())) {
					gameProfile.getProperties().putAll(MinecraftClient.getInstance().getSessionProperties());
					map.putAll(this.sessionService.getTextures(gameProfile, false));
				} else {
					this.sessionService.fillProfileProperties(gameProfile, bl);

					try {
						map.putAll(this.sessionService.getTextures(gameProfile, bl));
					} catch (InsecureTextureException var6) {
					}
				}
			}

			MinecraftClient.getInstance().execute(() -> RenderSystem.recordRenderCall(() -> ImmutableList.of(Type.SKIN, Type.CAPE).forEach(type -> {
						if (map.containsKey(type)) {
							this.loadSkin((MinecraftProfileTexture)map.get(type), type, skinTextureAvailableCallback);
						}
					})));
		};
		Util.getServerWorkerExecutor().execute(runnable);
	}

	public Map<Type, MinecraftProfileTexture> getTextures(GameProfile gameProfile) {
		return (Map<Type, MinecraftProfileTexture>)this.skinCache.getUnchecked(gameProfile);
	}

	public interface SkinTextureAvailableCallback {
		void onSkinTextureAvailable(Type type, Identifier identifier, MinecraftProfileTexture minecraftProfileTexture);
	}
}
