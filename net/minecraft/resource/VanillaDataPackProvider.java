package net.minecraft.resource;

import java.util.Map;

public class VanillaDataPackProvider implements ResourcePackProvider {
	private final DefaultResourcePack pack = new DefaultResourcePack("minecraft");

	@Override
	public <T extends ResourcePackProfile> void register(Map<String, T> map, ResourcePackProfile.Factory<T> factory) {
		T resourcePackProfile = ResourcePackProfile.of("vanilla", false, () -> this.pack, factory, ResourcePackProfile.InsertionPosition.field_14281);
		if (resourcePackProfile != null) {
			map.put("vanilla", resourcePackProfile);
		}
	}
}
