package net.minecraft.client.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import net.minecraft.client.ClientTickable;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextureManager implements ClientTickable, ResourceReloadListener {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Map<Identifier, Texture> textures = Maps.newHashMap();
	private final List<ClientTickable> tickables = Lists.newArrayList();
	private final Map<String, Integer> dynamicIdCounter = Maps.newHashMap();
	private ResourceManager resourceManager;

	public TextureManager(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}

	public void bindTexture(Identifier id) {
		Texture texture = (Texture)this.textures.get(id);
		if (texture == null) {
			texture = new ResourceTexture(id);
			this.loadTexture(id, texture);
		}

		TextureUtil.bindTexture(texture.getGlId());
	}

	public boolean loadTickableTexture(Identifier identifier, TickableTexture texture) {
		if (this.loadTexture(identifier, texture)) {
			this.tickables.add(texture);
			return true;
		} else {
			return false;
		}
	}

	public boolean loadTexture(Identifier identifier, Texture texture) {
		boolean bl = true;

		try {
			texture.load(this.resourceManager);
		} catch (IOException var8) {
			LOGGER.warn("Failed to load texture: " + identifier, var8);
			texture = TextureUtil.MISSING_TEXTURE;
			this.textures.put(identifier, texture);
			bl = false;
		} catch (Throwable var9) {
			CrashReport crashReport = CrashReport.create(var9, "Registering texture");
			CrashReportSection crashReportSection = crashReport.addElement("Resource location being registered");
			crashReportSection.add("Resource location", identifier);
			crashReportSection.add("Texture object class", new Callable<String>() {
				public String call() throws Exception {
					return texture.getClass().getName();
				}
			});
			throw new CrashException(crashReport);
		}

		this.textures.put(identifier, texture);
		return bl;
	}

	public Texture getTexture(Identifier id) {
		return (Texture)this.textures.get(id);
	}

	public Identifier registerDynamicTexture(String prefix, NativeImageBackedTexture texture) {
		Integer integer = (Integer)this.dynamicIdCounter.get(prefix);
		if (integer == null) {
			integer = 1;
		} else {
			integer = integer + 1;
		}

		this.dynamicIdCounter.put(prefix, integer);
		Identifier identifier = new Identifier(String.format("dynamic/%s_%d", prefix, integer));
		this.loadTexture(identifier, texture);
		return identifier;
	}

	@Override
	public void tick() {
		for (ClientTickable clientTickable : this.tickables) {
			clientTickable.tick();
		}
	}

	public void close(Identifier id) {
		Texture texture = this.getTexture(id);
		if (texture != null) {
			TextureUtil.deleteTexture(texture.getGlId());
		}
	}

	@Override
	public void reload(ResourceManager resourceManager) {
		for (Entry<Identifier, Texture> entry : this.textures.entrySet()) {
			this.loadTexture((Identifier)entry.getKey(), (Texture)entry.getValue());
		}
	}
}
