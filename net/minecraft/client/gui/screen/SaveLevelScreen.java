package net.minecraft.client.gui.screen;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class SaveLevelScreen extends Screen {
	public SaveLevelScreen(Text text) {
		super(text);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackgroundTexture(0);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 70, 16777215);
		super.render(matrices, mouseX, mouseY, delta);
	}
}
