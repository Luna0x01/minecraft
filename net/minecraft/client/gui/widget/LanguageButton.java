package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;

public class LanguageButton extends ButtonWidget {
	public LanguageButton(int i, int j, int k) {
		super(i, j, k, 20, 20, "");
	}

	@Override
	public void render(MinecraftClient client, int mouseX, int mouseY) {
		if (this.visible) {
			client.getTextureManager().bindTexture(ButtonWidget.WIDGETS_LOCATION);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			boolean bl = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
			int i = 106;
			if (bl) {
				i += this.height;
			}

			this.drawTexture(this.x, this.y, 0, i, this.width, this.height);
		}
	}
}
