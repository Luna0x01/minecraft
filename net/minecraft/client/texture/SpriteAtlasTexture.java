package net.minecraft.client.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.class_4276;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.class_2901;
import net.minecraft.client.render.TextureStitchException;
import net.minecraft.client.render.TextureStitcher;
import net.minecraft.client.resource.AnimationMetadata;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SpriteAtlasTexture extends AbstractTexture implements TickableTexture {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final Identifier BLOCK_ATLAS_TEX = new Identifier("textures/atlas/blocks.png");
	private final List<Sprite> animatedSprites = Lists.newArrayList();
	private final Set<Identifier> field_21027 = Sets.newHashSet();
	private final Map<Identifier, Sprite> sprites = Maps.newHashMap();
	private final String name;
	private int maxTextureSize;
	private final Sprite texture = class_4276.method_19454();

	public SpriteAtlasTexture(String string) {
		this.name = string;
	}

	@Override
	public void load(ResourceManager manager) throws IOException {
	}

	public void method_19510(ResourceManager resourceManager, Iterable<Identifier> iterable) {
		this.field_21027.clear();
		iterable.forEach(identifier -> this.method_19511(resourceManager, identifier));
		this.method_7005(resourceManager);
	}

	public void method_7005(ResourceManager manager) {
		int i = MinecraftClient.getMaxTextureSize();
		TextureStitcher textureStitcher = new TextureStitcher(i, i, 0, this.maxTextureSize);
		this.method_19516();
		int j = Integer.MAX_VALUE;
		int k = 1 << this.maxTextureSize;

		for (Identifier identifier : this.field_21027) {
			if (!this.texture.method_5348().equals(identifier)) {
				Identifier identifier2 = this.method_19513(identifier);

				Sprite sprite;
				try {
					Resource resource = manager.getResource(identifier2);
					Throwable crashReport = null;

					try {
						class_2901 lv = new class_2901(resource);
						AnimationMetadata animationMetadata = resource.method_21371(AnimationMetadata.field_21048);
						sprite = new Sprite(identifier, lv, animationMetadata);
					} catch (Throwable var27) {
						crashReport = var27;
						throw var27;
					} finally {
						if (resource != null) {
							if (crashReport != null) {
								try {
									resource.close();
								} catch (Throwable var26) {
									crashReport.addSuppressed(var26);
								}
							} else {
								resource.close();
							}
						}
					}
				} catch (RuntimeException var29) {
					LOGGER.error("Unable to parse metadata from {} : {}", identifier2, var29);
					continue;
				} catch (IOException var30) {
					LOGGER.error("Using missing texture, unable to load {} : {}", identifier2, var30);
					continue;
				}

				j = Math.min(j, Math.min(sprite.getWidth(), sprite.getHeight()));
				int l = Math.min(Integer.lowestOneBit(sprite.getWidth()), Integer.lowestOneBit(sprite.getHeight()));
				if (l < k) {
					LOGGER.warn(
						"Texture {} with size {}x{} limits mip level from {} to {}", identifier2, sprite.getWidth(), sprite.getHeight(), MathHelper.log2(k), MathHelper.log2(l)
					);
					k = l;
				}

				textureStitcher.add(sprite);
			}
		}

		int m = Math.min(j, k);
		int n = MathHelper.log2(m);
		if (n < this.maxTextureSize) {
			LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", this.name, this.maxTextureSize, n, m);
			this.maxTextureSize = n;
		}

		this.texture.method_7013(this.maxTextureSize);
		textureStitcher.add(this.texture);

		try {
			textureStitcher.stitch();
		} catch (TextureStitchException var25) {
			throw var25;
		}

		LOGGER.info("Created: {}x{} {}-atlas", textureStitcher.getWidth(), textureStitcher.getHeight(), this.name);
		TextureUtil.prepareImage(this.getGlId(), this.maxTextureSize, textureStitcher.getWidth(), textureStitcher.getHeight());

		for (Sprite sprite4 : textureStitcher.getStitchedSprites()) {
			if (sprite4 == this.texture || this.method_12488(manager, sprite4)) {
				this.sprites.put(sprite4.method_5348(), sprite4);

				try {
					sprite4.method_19528();
				} catch (Throwable var24) {
					CrashReport crashReport = CrashReport.create(var24, "Stitching texture atlas");
					CrashReportSection crashReportSection = crashReport.addElement("Texture being stitched together");
					crashReportSection.add("Atlas path", this.name);
					crashReportSection.add("Sprite", sprite4);
					throw new CrashException(crashReport);
				}

				if (sprite4.hasMeta()) {
					this.animatedSprites.add(sprite4);
				}
			}
		}
	}

	private boolean method_12488(ResourceManager resourceManager, Sprite sprite) {
		Identifier identifier = this.method_19513(sprite.method_5348());
		Resource resource = null;

		label45: {
			boolean crashReport;
			try {
				resource = resourceManager.getResource(identifier);
				sprite.method_19521(resource, this.maxTextureSize + 1);
				break label45;
			} catch (RuntimeException var13) {
				LOGGER.error("Unable to parse metadata from {}", identifier, var13);
				return false;
			} catch (IOException var14) {
				LOGGER.error("Using missing texture, unable to load {}", identifier, var14);
				crashReport = false;
			} finally {
				IOUtils.closeQuietly(resource);
			}

			return crashReport;
		}

		try {
			sprite.method_7013(this.maxTextureSize);
			return true;
		} catch (Throwable var12) {
			CrashReport crashReport = CrashReport.create(var12, "Applying mipmap");
			CrashReportSection crashReportSection = crashReport.addElement("Sprite being mipmapped");
			crashReportSection.add("Sprite name", (CrashCallable<String>)(() -> sprite.method_5348().toString()));
			crashReportSection.add("Sprite size", (CrashCallable<String>)(() -> sprite.getWidth() + " x " + sprite.getHeight()));
			crashReportSection.add("Sprite frames", (CrashCallable<String>)(() -> sprite.getSize() + " frames"));
			crashReportSection.add("Mipmap levels", this.maxTextureSize);
			throw new CrashException(crashReport);
		}
	}

	private Identifier method_19513(Identifier identifier) {
		return new Identifier(identifier.getNamespace(), String.format("%s/%s%s", this.name, identifier.getPath(), ".png"));
	}

	public Sprite getSprite(String identifier) {
		return this.method_19509(new Identifier(identifier));
	}

	public void update() {
		this.method_19530();

		for (Sprite sprite : this.animatedSprites) {
			sprite.update();
		}
	}

	public void method_19511(ResourceManager resourceManager, Identifier identifier) {
		if (identifier == null) {
			throw new IllegalArgumentException("Location cannot be null!");
		} else {
			this.field_21027.add(identifier);
		}
	}

	@Override
	public void tick() {
		this.update();
	}

	public void setMaxTextureSize(int size) {
		this.maxTextureSize = size;
	}

	public Sprite method_19509(Identifier identifier) {
		Sprite sprite = (Sprite)this.sprites.get(identifier);
		return sprite == null ? this.texture : sprite;
	}

	public void method_19516() {
		for (Sprite sprite : this.sprites.values()) {
			sprite.clearFrames();
		}

		this.sprites.clear();
		this.animatedSprites.clear();
	}
}
