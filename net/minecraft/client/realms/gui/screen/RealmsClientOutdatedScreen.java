package net.minecraft.client.realms.gui.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class RealmsClientOutdatedScreen extends RealmsScreen {
	private static final Text OUTDATED_TITLE = new TranslatableText("mco.client.outdated.title");
	private static final Text[] OUTDATED_LINES = new Text[]{
		new TranslatableText("mco.client.outdated.msg.line1"), new TranslatableText("mco.client.outdated.msg.line2")
	};
	private static final Text INCOMPATIBLE_TITLE = new TranslatableText("mco.client.incompatible.title");
	private static final Text[] INCOMPATIBLE_LINES = new Text[]{
		new TranslatableText("mco.client.incompatible.msg.line1"),
		new TranslatableText("mco.client.incompatible.msg.line2"),
		new TranslatableText("mco.client.incompatible.msg.line3")
	};
	private final Screen parent;
	private final boolean outdated;

	public RealmsClientOutdatedScreen(Screen parent, boolean outdated) {
		super(outdated ? OUTDATED_TITLE : INCOMPATIBLE_TITLE);
		this.parent = parent;
		this.outdated = outdated;
	}

	@Override
	public void init() {
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, row(12), 200, 20, ScreenTexts.BACK, button -> this.client.openScreen(this.parent)));
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, row(3), 16711680);
		Text[] texts = this.outdated ? INCOMPATIBLE_LINES : OUTDATED_LINES;

		for (int i = 0; i < texts.length; i++) {
			drawCenteredText(matrices, this.textRenderer, texts[i], this.width / 2, row(5) + i * 12, 16777215);
		}

		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode != 257 && keyCode != 335 && keyCode != 256) {
			return super.keyPressed(keyCode, scanCode, modifiers);
		} else {
			this.client.openScreen(this.parent);
			return true;
		}
	}
}
