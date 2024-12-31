package net.minecraft.client.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.TextureStitchException;
import net.minecraft.client.render.TextureStitcher;
import net.minecraft.client.resource.AnimationMetadata;
import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SpriteAtlasTexture extends AbstractTexture implements TickableTexture {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final Identifier MISSING = new Identifier("missingno");
	public static final Identifier BLOCK_ATLAS_TEX = new Identifier("textures/atlas/blocks.png");
	private final List<Sprite> animatedSprites = Lists.newArrayList();
	private final Map<String, Sprite> spritesToLoad = Maps.newHashMap();
	private final Map<String, Sprite> sprites = Maps.newHashMap();
	private final String name;
	private final TextureCreator textureCreator;
	private int maxTextureSize;
	private final Sprite texture = new Sprite("missingno");

	public SpriteAtlasTexture(String string) {
		this(string, null);
	}

	public SpriteAtlasTexture(String string, TextureCreator textureCreator) {
		this.name = string;
		this.textureCreator = textureCreator;
	}

	private void method_5829() {
		int[] is = TextureUtil.field_6583;
		this.texture.setWidth(16);
		this.texture.setHeight(16);
		int[][] js = new int[this.maxTextureSize + 1][];
		js[0] = is;
		this.texture.setFrames(Lists.newArrayList(new int[][][]{js}));
	}

	@Override
	public void load(ResourceManager manager) throws IOException {
		if (this.textureCreator != null) {
			this.method_10315(manager, this.textureCreator);
		}
	}

	public void method_10315(ResourceManager manager, TextureCreator creator) {
		this.spritesToLoad.clear();
		creator.create(this);
		this.method_5829();
		this.clearGlId();
		this.method_7005(manager);
	}

	public void method_7005(ResourceManager manager) {
		int i = MinecraftClient.getMaxTextureSize();
		TextureStitcher textureStitcher = new TextureStitcher(i, i, true, 0, this.maxTextureSize);
		this.sprites.clear();
		this.animatedSprites.clear();
		int j = Integer.MAX_VALUE;
		int k = 1 << this.maxTextureSize;

		for (Entry<String, Sprite> entry : this.spritesToLoad.entrySet()) {
			Sprite sprite = (Sprite)entry.getValue();
			Identifier identifier = new Identifier(sprite.getName());
			Identifier identifier2 = this.method_7003(identifier, 0);

			try {
				Resource resource = manager.getResource(identifier2);
				BufferedImage[] bufferedImages = new BufferedImage[1 + this.maxTextureSize];
				bufferedImages[0] = TextureUtil.create(resource.getInputStream());
				TextureResourceMetadata textureResourceMetadata = resource.getMetadata("texture");
				if (textureResourceMetadata != null) {
					List<Integer> list = textureResourceMetadata.method_7049();
					if (!list.isEmpty()) {
						int l = bufferedImages[0].getWidth();
						int m = bufferedImages[0].getHeight();
						if (MathHelper.smallestEncompassingPowerOfTwo(l) != l || MathHelper.smallestEncompassingPowerOfTwo(m) != m) {
							throw new RuntimeException("Unable to load extra miplevels, source-texture is not power of two");
						}
					}

					for (int n : list) {
						if (n > 0 && n < bufferedImages.length - 1 && bufferedImages[n] == null) {
							Identifier identifier3 = this.method_7003(identifier, n);

							try {
								bufferedImages[n] = TextureUtil.create(manager.getResource(identifier3).getInputStream());
							} catch (IOException var22) {
								LOGGER.error("Unable to load miplevel {} from: {}", new Object[]{n, identifier3, var22});
							}
						}
					}
				}

				AnimationMetadata animationMetadata = resource.getMetadata("animation");
				sprite.method_7009(bufferedImages, animationMetadata);
			} catch (RuntimeException var23) {
				LOGGER.error("Unable to parse metadata from " + identifier2, var23);
				continue;
			} catch (IOException var24) {
				LOGGER.error("Using missing texture, unable to load " + identifier2, var24);
				continue;
			}

			j = Math.min(j, Math.min(sprite.getWidth(), sprite.getHeight()));
			int o = Math.min(Integer.lowestOneBit(sprite.getWidth()), Integer.lowestOneBit(sprite.getHeight()));
			if (o < k) {
				LOGGER.warn(
					"Texture {} with size {}x{} limits mip level from {} to {}",
					new Object[]{identifier2, sprite.getWidth(), sprite.getHeight(), MathHelper.log2(k), MathHelper.log2(o)}
				);
				k = o;
			}

			textureStitcher.add(sprite);
		}

		int p = Math.min(j, k);
		int q = MathHelper.log2(p);
		if (q < this.maxTextureSize) {
			LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", new Object[]{this.name, this.maxTextureSize, q, p});
			this.maxTextureSize = q;
		}

		for (final Sprite sprite2 : this.spritesToLoad.values()) {
			try {
				sprite2.method_7013(this.maxTextureSize);
			} catch (Throwable var21) {
				CrashReport crashReport = CrashReport.create(var21, "Applying mipmap");
				CrashReportSection crashReportSection = crashReport.addElement("Sprite being mipmapped");
				crashReportSection.add("Sprite name", new Callable<String>() {
					public String call() throws Exception {
						return sprite2.getName();
					}
				});
				crashReportSection.add("Sprite size", new Callable<String>() {
					public String call() throws Exception {
						return sprite2.getWidth() + " x " + sprite2.getHeight();
					}
				});
				crashReportSection.add("Sprite frames", new Callable<String>() {
					public String call() throws Exception {
						return sprite2.getSize() + " frames";
					}
				});
				crashReportSection.add("Mipmap levels", this.maxTextureSize);
				throw new CrashException(crashReport);
			}
		}

		this.texture.method_7013(this.maxTextureSize);
		textureStitcher.add(this.texture);

		try {
			textureStitcher.stitch();
		} catch (TextureStitchException var20) {
			throw var20;
		}

		LOGGER.info("Created: {}x{} {}-atlas", new Object[]{textureStitcher.getWidth(), textureStitcher.getHeight(), this.name});
		TextureUtil.prepareImage(this.getGlId(), this.maxTextureSize, textureStitcher.getWidth(), textureStitcher.getHeight());
		Map<String, Sprite> map = Maps.newHashMap(this.spritesToLoad);

		for (Sprite sprite3 : textureStitcher.getStitchedSprites()) {
			String string = sprite3.getName();
			map.remove(string);
			this.sprites.put(string, sprite3);

			try {
				TextureUtil.method_7027(sprite3.method_5831(0), sprite3.getWidth(), sprite3.getHeight(), sprite3.getX(), sprite3.getY(), false, false);
			} catch (Throwable var19) {
				CrashReport crashReport2 = CrashReport.create(var19, "Stitching texture atlas");
				CrashReportSection crashReportSection2 = crashReport2.addElement("Texture being stitched together");
				crashReportSection2.add("Atlas path", this.name);
				crashReportSection2.add("Sprite", sprite3);
				throw new CrashException(crashReport2);
			}

			if (sprite3.hasMeta()) {
				this.animatedSprites.add(sprite3);
			}
		}

		for (Sprite sprite4 : map.values()) {
			sprite4.copyData(this.texture);
		}
	}

	private Identifier method_7003(Identifier identifier, int i) {
		return i == 0
			? new Identifier(identifier.getNamespace(), String.format("%s/%s%s", this.name, identifier.getPath(), ".png"))
			: new Identifier(identifier.getNamespace(), String.format("%s/mipmaps/%s.%d%s", this.name, identifier.getPath(), i, ".png"));
	}

	public Sprite getSprite(String identifier) {
		Sprite sprite = (Sprite)this.sprites.get(identifier);
		if (sprite == null) {
			sprite = this.texture;
		}

		return sprite;
	}

	public void update() {
		TextureUtil.bindTexture(this.getGlId());

		for (Sprite sprite : this.animatedSprites) {
			sprite.update();
		}
	}

	public Sprite getSprite(Identifier identifier) {
		if (identifier == null) {
			throw new IllegalArgumentException("Location cannot be null!");
		} else {
			Sprite sprite = (Sprite)this.spritesToLoad.get(identifier);
			if (sprite == null) {
				sprite = Sprite.get(identifier);
				this.spritesToLoad.put(identifier.toString(), sprite);
			}

			return sprite;
		}
	}

	@Override
	public void tick() {
		this.update();
	}

	public void setMaxTextureSize(int size) {
		this.maxTextureSize = size;
	}

	public Sprite getTexture() {
		return this.texture;
	}
}
