package net.minecraft.realms;

import java.lang.reflect.Constructor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.RealmsScreenProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsBridge extends RealmsScreen {
	private static final Logger LOGGER = LogManager.getLogger();
	private Screen previousScreen;

	public void switchToRealms(Screen parent) {
		this.previousScreen = parent;

		try {
			Class<?> class_ = Class.forName("com.mojang.realmsclient.RealmsMainScreen");
			Constructor<?> constructor = class_.getDeclaredConstructor(RealmsScreen.class);
			constructor.setAccessible(true);
			Object object = constructor.newInstance(this);
			MinecraftClient.getInstance().setScreen(((RealmsScreen)object).getProxy());
		} catch (ClassNotFoundException var5) {
			LOGGER.error("Realms module missing");
		} catch (Exception var6) {
			LOGGER.error("Failed to load Realms module", var6);
		}
	}

	public RealmsScreenProxy getNotificationScreen(Screen parent) {
		try {
			this.previousScreen = parent;
			Class<?> class_ = Class.forName("com.mojang.realmsclient.gui.screens.RealmsNotificationsScreen");
			Constructor<?> constructor = class_.getDeclaredConstructor(RealmsScreen.class);
			constructor.setAccessible(true);
			Object object = constructor.newInstance(this);
			return ((RealmsScreen)object).getProxy();
		} catch (ClassNotFoundException var5) {
			LOGGER.error("Realms module missing");
		} catch (Exception var6) {
			LOGGER.error("Failed to load Realms module", var6);
		}

		return null;
	}

	@Override
	public void init() {
		MinecraftClient.getInstance().setScreen(this.previousScreen);
	}
}
