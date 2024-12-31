package net.minecraft.client.gui.screen.resourcepack;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TranslatableText;

public class AvailableResourcePackListWidget extends ResourcePackListWidget {
	public AvailableResourcePackListWidget(MinecraftClient minecraftClient, int i, int j) {
		super(minecraftClient, i, j, new TranslatableText("resourcePack.available.title"));
	}
}
