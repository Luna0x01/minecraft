package net.minecraft.client.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.class_2901;
import net.minecraft.client.render.TextureStitchException;
import net.minecraft.client.render.TextureStitcher;
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

	public SpriteAtlasTexture(String string, @Nullable TextureCreator textureCreator) {
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
		TextureStitcher textureStitcher = new TextureStitcher(i, i, 0, this.maxTextureSize);
		this.sprites.clear();
		this.animatedSprites.clear();
		int j = Integer.MAX_VALUE;
		int k = 1 << this.maxTextureSize;

		for (Entry<String, Sprite> entry : this.spritesToLoad.entrySet()) {
			Sprite sprite = (Sprite)entry.getValue();
			Identifier identifier = this.method_12487(sprite);
			Resource resource = null;

			try {
				class_2901 lv = class_2901.method_12485(manager.getResource(identifier));
				resource = manager.getResource(identifier);
				boolean bl = resource.getMetadata("animation") != null;
				sprite.method_12491(lv, bl);
			} catch (RuntimeException var22) {
				LOGGER.error("Unable to parse metadata from " + identifier, var22);
				continue;
			} catch (IOException var23) {
				LOGGER.error("Using missing texture, unable to load " + identifier, var23);
				continue;
			} finally {
				IOUtils.closeQuietly(resource);
			}

			j = Math.min(j, Math.min(sprite.getWidth(), sprite.getHeight()));
			int l = Math.min(Integer.lowestOneBit(sprite.getWidth()), Integer.lowestOneBit(sprite.getHeight()));
			if (l < k) {
				LOGGER.warn(
					"Texture {} with size {}x{} limits mip level from {} to {}",
					new Object[]{identifier, sprite.getWidth(), sprite.getHeight(), MathHelper.log2(k), MathHelper.log2(l)}
				);
				k = l;
			}

			textureStitcher.add(sprite);
		}

		int m = Math.min(j, k);
		int n = MathHelper.log2(m);
		if (n < this.maxTextureSize) {
			LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", new Object[]{this.name, this.maxTextureSize, n, m});
			this.maxTextureSize = n;
		}

		this.texture.method_7013(this.maxTextureSize);
		textureStitcher.add(this.texture);

		try {
			textureStitcher.stitch();
		} catch (TextureStitchException var21) {
			throw var21;
		}

		LOGGER.info("Created: {}x{} {}-atlas", new Object[]{textureStitcher.getWidth(), textureStitcher.getHeight(), this.name});
		TextureUtil.prepareImage(this.getGlId(), this.maxTextureSize, textureStitcher.getWidth(), textureStitcher.getHeight());
		Map<String, Sprite> map = Maps.newHashMap(this.spritesToLoad);

		for (Sprite sprite2 : textureStitcher.getStitchedSprites()) {
			if (sprite2 == this.texture || this.method_12488(manager, sprite2)) {
				String string = sprite2.getName();
				map.remove(string);
				this.sprites.put(string, sprite2);

				try {
					TextureUtil.method_7027(sprite2.method_5831(0), sprite2.getWidth(), sprite2.getHeight(), sprite2.getX(), sprite2.getY(), false, false);
				} catch (Throwable var20) {
					CrashReport crashReport = CrashReport.create(var20, "Stitching texture atlas");
					CrashReportSection crashReportSection = crashReport.addElement("Texture being stitched together");
					crashReportSection.add("Atlas path", this.name);
					crashReportSection.add("Sprite", sprite2);
					throw new CrashException(crashReport);
				}

				if (sprite2.hasMeta()) {
					this.animatedSprites.add(sprite2);
				}
			}
		}

		for (Sprite sprite3 : map.values()) {
			sprite3.copyData(this.texture);
		}
	}

	private boolean method_12488(ResourceManager resourceManager, Sprite sprite) {
		Identifier identifier = this.method_12487(sprite);
		Resource resource = null;

		label45: {
			boolean crashReport;
			try {
				resource = resourceManager.getResource(identifier);
				sprite.method_12492(resource, this.maxTextureSize + 1);
				break label45;
			} catch (RuntimeException var13) {
				LOGGER.error("Unable to parse metadata from " + identifier, var13);
				return false;
			} catch (IOException var14) {
				LOGGER.error("Using missing texture, unable to load " + identifier, var14);
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
			crashReportSection.add("Sprite name", new CrashCallable<String>() {
				public String call() throws Exception {
					return sprite.getName();
				}
			});
			crashReportSection.add("Sprite size", new CrashCallable<String>() {
				public String call() throws Exception {
					return sprite.getWidth() + " x " + sprite.getHeight();
				}
			});
			crashReportSection.add("Sprite frames", new CrashCallable<String>() {
				public String call() throws Exception {
					return sprite.getSize() + " frames";
				}
			});
			crashReportSection.add("Mipmap levels", this.maxTextureSize);
			throw new CrashException(crashReport);
		}
	}

	private Identifier method_12487(Sprite sprite) {
		Identifier identifier = new Identifier(sprite.getName());
		return new Identifier(identifier.getNamespace(), String.format("%s/%s%s", this.name, identifier.getPath(), ".png"));
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
