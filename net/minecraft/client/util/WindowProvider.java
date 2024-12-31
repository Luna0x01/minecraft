package net.minecraft.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.WindowSettings;

public final class WindowProvider implements AutoCloseable {
	private final MinecraftClient client;
	private final MonitorTracker monitorTracker;

	public WindowProvider(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
		this.monitorTracker = new MonitorTracker(Monitor::new);
	}

	public Window createWindow(WindowSettings windowSettings, String string, String string2) {
		return new Window(this.client, this.monitorTracker, windowSettings, string, string2);
	}

	public void close() {
		this.monitorTracker.stop();
	}
}
