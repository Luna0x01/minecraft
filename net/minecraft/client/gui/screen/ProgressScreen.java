package net.minecraft.client.gui.screen;

import net.minecraft.util.ProgressListener;

public class ProgressScreen extends Screen implements ProgressListener {
	private String title = "";
	private String task = "";
	private int progress;
	private boolean done;

	@Override
	public void setTitle(String title) {
		this.setTitleAndTask(title);
	}

	@Override
	public void setTitleAndTask(String title) {
		this.title = title;
		this.setTask("Working...");
	}

	@Override
	public void setTask(String task) {
		this.task = task;
		this.setProgressPercentage(0);
	}

	@Override
	public void setProgressPercentage(int percentage) {
		this.progress = percentage;
	}

	@Override
	public void setDone() {
		this.done = true;
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		if (this.done) {
			if (!this.client.isConnectedToRealms()) {
				this.client.setScreen(null);
			}
		} else {
			this.renderBackground();
			this.drawCenteredString(this.textRenderer, this.title, this.width / 2, 70, 16777215);
			this.drawCenteredString(this.textRenderer, this.task + " " + this.progress + "%", this.width / 2, 90, 16777215);
			super.render(mouseX, mouseY, tickDelta);
		}
	}
}
