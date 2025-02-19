package net.minecraft.client.realms.task;

import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.exception.RetryCallException;
import net.minecraft.text.Text;

public abstract class ResettingWorldTask extends LongRunningTask {
	private final long serverId;
	private final Text title;
	private final Runnable callback;

	public ResettingWorldTask(long serverId, Text title, Runnable callback) {
		this.serverId = serverId;
		this.title = title;
		this.callback = callback;
	}

	protected abstract void resetWorld(RealmsClient client, long worldId) throws RealmsServiceException;

	public void run() {
		RealmsClient realmsClient = RealmsClient.createRealmsClient();
		this.setTitle(this.title);
		int i = 0;

		while (i < 25) {
			try {
				if (this.aborted()) {
					return;
				}

				this.resetWorld(realmsClient, this.serverId);
				if (this.aborted()) {
					return;
				}

				this.callback.run();
				return;
			} catch (RetryCallException var4) {
				if (this.aborted()) {
					return;
				}

				pause((long)var4.delaySeconds);
				i++;
			} catch (Exception var5) {
				if (this.aborted()) {
					return;
				}

				LOGGER.error("Couldn't reset world");
				this.error(var5.toString());
				return;
			}
		}
	}
}
