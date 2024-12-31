package net.minecraft;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.client.resource.FallbackResourceManager;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_4468 implements ReloadableResourceManager {
	private static final Logger field_21923 = LogManager.getLogger();
	private final Map<String, FallbackResourceManager> field_21924 = Maps.newHashMap();
	private final List<ResourceReloadListener> field_21925 = Lists.newArrayList();
	private final Set<String> field_21926 = Sets.newLinkedHashSet();
	private final class_4455 field_21927;

	public class_4468(class_4455 arg) {
		this.field_21927 = arg;
	}

	public void method_21373(class_4454 arg) {
		for (String string : arg.method_21327(this.field_21927)) {
			this.field_21926.add(string);
			FallbackResourceManager fallbackResourceManager = (FallbackResourceManager)this.field_21924.get(string);
			if (fallbackResourceManager == null) {
				fallbackResourceManager = new FallbackResourceManager(this.field_21927);
				this.field_21924.put(string, fallbackResourceManager);
			}

			fallbackResourceManager.method_5882(arg);
		}
	}

	@Override
	public Set<String> getAllNamespaces() {
		return this.field_21926;
	}

	@Override
	public Resource getResource(Identifier id) throws IOException {
		ResourceManager resourceManager = (ResourceManager)this.field_21924.get(id.getNamespace());
		if (resourceManager != null) {
			return resourceManager.getResource(id);
		} else {
			throw new FileNotFoundException(id.toString());
		}
	}

	@Override
	public List<Resource> getAllResources(Identifier id) throws IOException {
		ResourceManager resourceManager = (ResourceManager)this.field_21924.get(id.getNamespace());
		if (resourceManager != null) {
			return resourceManager.getAllResources(id);
		} else {
			throw new FileNotFoundException(id.toString());
		}
	}

	@Override
	public Collection<Identifier> method_21372(String string, Predicate<String> predicate) {
		Set<Identifier> set = Sets.newHashSet();

		for (FallbackResourceManager fallbackResourceManager : this.field_21924.values()) {
			set.addAll(fallbackResourceManager.method_21372(string, predicate));
		}

		List<Identifier> list = Lists.newArrayList(set);
		Collections.sort(list);
		return list;
	}

	private void method_21374() {
		this.field_21924.clear();
		this.field_21926.clear();
	}

	@Override
	public void reload(List<class_4454> resourcePacks) {
		this.method_21374();
		field_21923.info("Reloading ResourceManager: {}", resourcePacks.stream().map(class_4454::method_5899).collect(Collectors.joining(", ")));

		for (class_4454 lv : resourcePacks) {
			this.method_21373(lv);
		}

		if (field_21923.isDebugEnabled()) {
			this.method_21377();
		} else {
			this.method_21376();
		}
	}

	@Override
	public void registerListener(ResourceReloadListener listener) {
		this.field_21925.add(listener);
		if (field_21923.isDebugEnabled()) {
			field_21923.info(this.method_21375(listener));
		} else {
			listener.reload(this);
		}
	}

	private void method_21376() {
		for (ResourceReloadListener resourceReloadListener : this.field_21925) {
			resourceReloadListener.reload(this);
		}
	}

	private void method_21377() {
		field_21923.info("Reloading all resources! {} listeners to update.", this.field_21925.size());
		List<String> list = Lists.newArrayList();
		Stopwatch stopwatch = Stopwatch.createStarted();

		for (ResourceReloadListener resourceReloadListener : this.field_21925) {
			list.add(this.method_21375(resourceReloadListener));
		}

		stopwatch.stop();
		field_21923.info("----");
		field_21923.info("Complete resource reload took {} ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));

		for (String string : list) {
			field_21923.info(string);
		}

		field_21923.info("----");
	}

	private String method_21375(ResourceReloadListener resourceReloadListener) {
		Stopwatch stopwatch = Stopwatch.createStarted();
		resourceReloadListener.reload(this);
		stopwatch.stop();
		return "Resource reload for " + resourceReloadListener.getClass().getSimpleName() + " took " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " ms";
	}
}
