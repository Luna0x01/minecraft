package net.minecraft.client.texture;

import com.mojang.blaze3d.platform.GlStateManager;
import java.io.IOException;
import net.minecraft.resource.ResourceManager;

public interface Texture {
	void pushFilter(boolean bilinear, boolean mipmap);

	void pop();

	void load(ResourceManager manager) throws IOException;

	int getGlId();

	default void method_19530() {
		GlStateManager.bindTexture(this.getGlId());
	}
}
