package net.minecraft.client.gui.screen.resourcepack;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.class_2846;
import net.minecraft.client.gui.screen.ResourcePackScreen;

public class DefaultResourcePackEntryWidget extends class_2846 {
	public DefaultResourcePackEntryWidget(ResourcePackScreen resourcePackScreen) {
		super(resourcePackScreen, MinecraftClient.getInstance().getResourcePackLoader().defaultResourcePack);
	}

	@Override
	protected String getName() {
		return "Default";
	}

	@Override
	public boolean method_12199() {
		return false;
	}
}
