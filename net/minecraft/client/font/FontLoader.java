package net.minecraft.client.font;

import javax.annotation.Nullable;
import net.minecraft.resource.ResourceManager;

public interface FontLoader {
	@Nullable
	Font load(ResourceManager resourceManager);
}
