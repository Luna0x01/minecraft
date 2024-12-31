package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;

public abstract class LanguageButton extends ButtonWidget {
	public LanguageButton(int i, int j, int k) {
		super(i, j, k, 20, 20, "");
	}

	@Override
	public void method_891(int i, int j, float f) {
		if (this.visible) {
			MinecraftClient.getInstance().getTextureManager().bindTexture(ButtonWidget.WIDGETS_LOCATION);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			boolean bl = i >= this.x && j >= this.y && i < this.x + this.width && j < this.y + this.height;
			int k = 106;
			if (bl) {
				k += this.height;
			}

			this.drawTexture(this.x, this.y, 0, k, this.width, this.height);
		}
	}
}
