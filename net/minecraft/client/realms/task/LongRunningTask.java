package net.minecraft.client.realms.task;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen;
import net.minecraft.client.realms.util.Errable;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class LongRunningTask implements Errable, Runnable {
	protected static final int MAX_RETRIES = 25;
	public static final Logger LOGGER = LogManager.getLogger();
	protected RealmsLongRunningMcoTaskScreen longRunningMcoTaskScreen;

	protected static void pause(long seconds) {
		try {
			Thread.sleep(seconds * 1000L);
		} catch (InterruptedException var3) {
			Thread.currentThread().interrupt();
			LOGGER.error("", var3);
		}
	}

	public static void setScreen(Screen screen) {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		minecraftClient.execute(() -> minecraftClient.openScreen(screen));
	}

	public void setScreen(RealmsLongRunningMcoTaskScreen longRunningMcoTaskScreen) {
		this.longRunningMcoTaskScreen = longRunningMcoTaskScreen;
	}

	@Override
	public void error(Text errorMessage) {
		this.longRunningMcoTaskScreen.error(errorMessage);
	}

	public void setTitle(Text title) {
		this.longRunningMcoTaskScreen.setTitle(title);
	}

	public boolean aborted() {
		return this.longRunningMcoTaskScreen.aborted();
	}

	public void tick() {
	}

	public void init() {
	}

	public void abortTask() {
	}
}
