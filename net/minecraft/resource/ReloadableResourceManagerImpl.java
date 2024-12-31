package net.minecraft.resource;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.resource.FallbackResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.MetadataSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReloadableResourceManagerImpl implements ReloadableResourceManager {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Joiner JOINER = Joiner.on(", ");
	private final Map<String, FallbackResourceManager> fallbackManagers = Maps.newHashMap();
	private final List<ResourceReloadListener> listeners = Lists.newArrayList();
	private final Set<String> namespaces = Sets.newLinkedHashSet();
	private final MetadataSerializer metaSerializer;

	public ReloadableResourceManagerImpl(MetadataSerializer metadataSerializer) {
		this.metaSerializer = metadataSerializer;
	}

	public void add(ResourcePack pack) {
		for (String string : pack.getNamespaces()) {
			this.namespaces.add(string);
			FallbackResourceManager fallbackResourceManager = (FallbackResourceManager)this.fallbackManagers.get(string);
			if (fallbackResourceManager == null) {
				fallbackResourceManager = new FallbackResourceManager(this.metaSerializer);
				this.fallbackManagers.put(string, fallbackResourceManager);
			}

			fallbackResourceManager.addResourcePack(pack);
		}
	}

	@Override
	public Set<String> getAllNamespaces() {
		return this.namespaces;
	}

	@Override
	public Resource getResource(Identifier id) throws IOException {
		ResourceManager resourceManager = (ResourceManager)this.fallbackManagers.get(id.getNamespace());
		if (resourceManager != null) {
			return resourceManager.getResource(id);
		} else {
			throw new FileNotFoundException(id.toString());
		}
	}

	@Override
	public List<Resource> getAllResources(Identifier id) throws IOException {
		ResourceManager resourceManager = (ResourceManager)this.fallbackManagers.get(id.getNamespace());
		if (resourceManager != null) {
			return resourceManager.getAllResources(id);
		} else {
			throw new FileNotFoundException(id.toString());
		}
	}

	private void clear() {
		this.fallbackManagers.clear();
		this.namespaces.clear();
	}

	@Override
	public void reload(List<ResourcePack> resourcePacks) {
		this.clear();
		LOGGER.info("Reloading ResourceManager: " + JOINER.join(Iterables.transform(resourcePacks, new Function<ResourcePack, String>() {
			public String apply(ResourcePack resourcePack) {
				return resourcePack.getName();
			}
		})));

		for (ResourcePack resourcePack : resourcePacks) {
			this.add(resourcePack);
		}

		this.notifyListeners();
	}

	@Override
	public void registerListener(ResourceReloadListener listener) {
		this.listeners.add(listener);
		listener.reload(this);
	}

	private void notifyListeners() {
		for (ResourceReloadListener resourceReloadListener : this.listeners) {
			resourceReloadListener.reload(this);
		}
	}
}
