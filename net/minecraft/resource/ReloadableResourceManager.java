package net.minecraft.resource;

import java.util.List;
import net.minecraft.class_4454;

public interface ReloadableResourceManager extends ResourceManager {
	void reload(List<class_4454> resourcePacks);

	void registerListener(ResourceReloadListener listener);
}
