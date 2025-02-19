package net.minecraft.client.gui.screen;

import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class DisconnectedScreen extends Screen {
	private final Text reason;
	private MultilineText reasonFormatted = MultilineText.EMPTY;
	private final Screen parent;
	private int reasonHeight;

	public DisconnectedScreen(Screen parent, Text title, Text reason) {
		super(title);
		this.parent = parent;
		this.reason = reason;
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Override
	protected void init() {
		this.reasonFormatted = MultilineText.create(this.textRenderer, this.reason, this.width - 50);
		this.reasonHeight = this.reasonFormatted.count() * 9;
		this.addDrawableChild(
			new ButtonWidget(
				this.width / 2 - 100,
				Math.min(this.height / 2 + this.reasonHeight / 2 + 9, this.height - 30),
				200,
				20,
				new TranslatableText("gui.toMenu"),
				button -> this.client.openScreen(this.parent)
			)
		);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, this.height / 2 - this.reasonHeight / 2 - 9 * 2, 11184810);
		this.reasonFormatted.drawCenterWithShadow(matrices, this.width / 2, this.height / 2 - this.reasonHeight / 2);
		super.render(matrices, mouseX, mouseY, delta);
	}
}
