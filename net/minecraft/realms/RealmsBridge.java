package net.minecraft.realms;

import java.lang.reflect.Constructor;
import net.minecraft.class_4152;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.RealmsScreenProxy;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
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
			this.showMissingRealmsErrorScreen();
		} catch (Exception var6) {
			LOGGER.error("Failed to load Realms module", var6);
			this.showMissingRealmsErrorScreen();
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

	public static void openUri(String string) {
		Util.getOperatingSystem().method_20236(string);
	}

	public static void setClipboard(String string) {
		MinecraftClient.getInstance().field_19946.method_18187(string);
	}

	private void showMissingRealmsErrorScreen() {
		MinecraftClient.getInstance()
			.setScreen(
				new class_4152(
					() -> MinecraftClient.getInstance().setScreen(this.previousScreen), new LiteralText(""), new TranslatableText("realms.missing.module.error.text")
				)
			);
	}
}
