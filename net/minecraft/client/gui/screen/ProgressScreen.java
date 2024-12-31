package net.minecraft.client.gui.screen;

import java.util.Objects;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ProgressListener;

public class ProgressScreen extends Screen implements ProgressListener {
	private String title = "";
	private String task = "";
	private int progress;
	private boolean done;

	@Override
	public boolean method_18607() {
		return false;
	}

	@Override
	public void method_21524(Text text) {
		this.method_21525(text);
	}

	@Override
	public void method_21525(Text text) {
		this.title = text.asFormattedString();
		this.method_21526(new TranslatableText("progress.working"));
	}

	@Override
	public void method_21526(Text text) {
		this.task = text.asFormattedString();
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
			if (!Objects.equals(this.task, "") && this.progress != 0) {
				this.drawCenteredString(this.textRenderer, this.task + " " + this.progress + "%", this.width / 2, 90, 16777215);
			}

			super.render(mouseX, mouseY, tickDelta);
		}
	}
}
