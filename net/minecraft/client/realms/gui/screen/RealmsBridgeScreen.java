package net.minecraft.client.realms.gui.screen;

import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

public class RealmsBridgeScreen extends RealmsScreen {
	private Screen previousScreen;

	public void switchToRealms(Screen parentScreen) {
		this.previousScreen = parentScreen;
		MinecraftClient.getInstance().openScreen(new RealmsMainScreen(this));
	}

	@Nullable
	public RealmsScreen getNotificationScreen(Screen parentScreen) {
		this.previousScreen = parentScreen;
		return new RealmsNotificationsScreen();
	}

	@Override
	public void init() {
		MinecraftClient.getInstance().openScreen(this.previousScreen);
	}
}
