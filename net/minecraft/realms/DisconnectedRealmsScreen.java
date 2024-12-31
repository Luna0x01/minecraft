package net.minecraft.realms;

import java.util.List;
import net.minecraft.text.Text;

public class DisconnectedRealmsScreen extends RealmsScreen {
	private String title;
	private Text reason;
	private List<String> lines;
	private final RealmsScreen parent;
	private int textHeight;

	public DisconnectedRealmsScreen(RealmsScreen realmsScreen, String string, Text text) {
		this.parent = realmsScreen;
		this.title = getLocalizedString(string);
		this.reason = text;
	}

	@Override
	public void init() {
		Realms.setConnectedToRealms(false);
		this.buttonsClear();
		this.lines = this.fontSplit(this.reason.asFormattedString(), this.width() - 50);
		this.textHeight = this.lines.size() * this.fontLineHeight();
		this.buttonsAdd(newButton(0, this.width() / 2 - 100, this.height() / 2 + this.textHeight / 2 + this.fontLineHeight(), getLocalizedString("gui.back")));
	}

	@Override
	public void keyPressed(char c, int i) {
		if (i == 1) {
			Realms.setScreen(this.parent);
		}
	}

	@Override
	public void buttonClicked(RealmsButton realmsButton) {
		if (realmsButton.getId() == 0) {
			Realms.setScreen(this.parent);
		}
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.drawCenteredString(this.title, this.width() / 2, this.height() / 2 - this.textHeight / 2 - this.fontLineHeight() * 2, 11184810);
		int k = this.height() / 2 - this.textHeight / 2;
		if (this.lines != null) {
			for (String string : this.lines) {
				this.drawCenteredString(string, this.width() / 2, k, 16777215);
				k += this.fontLineHeight();
			}
		}

		super.render(i, j, f);
	}
}
