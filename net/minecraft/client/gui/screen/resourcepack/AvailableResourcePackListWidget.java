package net.minecraft.client.gui.screen.resourcepack;

import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;

public class AvailableResourcePackListWidget extends ResourcePackListWidget {
	public AvailableResourcePackListWidget(MinecraftClient minecraftClient, int i, int j, List<ResourcePackWidget> list) {
		super(minecraftClient, i, j, list);
	}

	@Override
	protected String getTitle() {
		return I18n.translate("resourcePack.available.title");
	}
}
