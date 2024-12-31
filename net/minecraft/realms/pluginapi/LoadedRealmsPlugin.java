package net.minecraft.realms.pluginapi;

import net.minecraft.realms.RealmsScreen;

public interface LoadedRealmsPlugin {
	RealmsScreen getMainScreen(RealmsScreen realmsScreen);

	RealmsScreen getNotificationsScreen(RealmsScreen realmsScreen);
}
