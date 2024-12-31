package net.minecraft.client.realms.task;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.WorldDownload;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.exception.RetryCallException;
import net.minecraft.client.realms.gui.screen.RealmsDownloadLatestWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsGenericErrorScreen;
import net.minecraft.text.TranslatableText;

public class DownloadTask extends LongRunningTask {
	private final long worldId;
	private final int slot;
	private final Screen lastScreen;
	private final String downloadName;

	public DownloadTask(long worldId, int slot, String downloadName, Screen lastScreen) {
		this.worldId = worldId;
		this.slot = slot;
		this.lastScreen = lastScreen;
		this.downloadName = downloadName;
	}

	public void run() {
		this.setTitle(new TranslatableText("mco.download.preparing"));
		RealmsClient realmsClient = RealmsClient.createRealmsClient();
		int i = 0;

		while (i < 25) {
			try {
				if (this.aborted()) {
					return;
				}

				WorldDownload worldDownload = realmsClient.download(this.worldId, this.slot);
				pause(1);
				if (this.aborted()) {
					return;
				}

				setScreen(new RealmsDownloadLatestWorldScreen(this.lastScreen, worldDownload, this.downloadName, bl -> {
				}));
				return;
			} catch (RetryCallException var4) {
				if (this.aborted()) {
					return;
				}

				pause(var4.delaySeconds);
				i++;
			} catch (RealmsServiceException var5) {
				if (this.aborted()) {
					return;
				}

				LOGGER.error("Couldn't download world data");
				setScreen(new RealmsGenericErrorScreen(var5, this.lastScreen));
				return;
			} catch (Exception var6) {
				if (this.aborted()) {
					return;
				}

				LOGGER.error("Couldn't download world data", var6);
				this.error(var6.getLocalizedMessage());
				return;
			}
		}
	}
}
