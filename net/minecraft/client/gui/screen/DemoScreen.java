package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class DemoScreen extends Screen {
	private static final Identifier BACKGROUND_TEXTURE = new Identifier("textures/gui/demo_background.png");

	@Override
	protected void init() {
		int i = -16;
		this.addButton(new ButtonWidget(1, this.width / 2 - 116, this.height / 2 + 62 + -16, 114, 20, I18n.translate("demo.help.buy")) {
			@Override
			public void method_18374(double d, double e) {
				this.active = false;
				Util.getOperatingSystem().method_20236("http://www.minecraft.net/store?source=demo");
			}
		});
		this.addButton(new ButtonWidget(2, this.width / 2 + 2, this.height / 2 + 62 + -16, 114, 20, I18n.translate("demo.help.later")) {
			@Override
			public void method_18374(double d, double e) {
				DemoScreen.this.client.setScreen(null);
				DemoScreen.this.client.field_19945.method_18253();
			}
		});
	}

	@Override
	public void renderBackground() {
		super.renderBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
		int i = (this.width - 248) / 2;
		int j = (this.height - 166) / 2;
		this.drawTexture(i, j, 0, 0, 248, 166);
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		int i = (this.width - 248) / 2 + 10;
		int j = (this.height - 166) / 2 + 8;
		this.textRenderer.method_18355(I18n.translate("demo.help.title"), (float)i, (float)j, 2039583);
		j += 12;
		GameOptions gameOptions = this.client.options;
		this.textRenderer
			.method_18355(
				I18n.translate(
					"demo.help.movementShort",
					gameOptions.forwardKey.method_18174(),
					gameOptions.leftKey.method_18174(),
					gameOptions.backKey.method_18174(),
					gameOptions.rightKey.method_18174()
				),
				(float)i,
				(float)j,
				5197647
			);
		this.textRenderer.method_18355(I18n.translate("demo.help.movementMouse"), (float)i, (float)(j + 12), 5197647);
		this.textRenderer.method_18355(I18n.translate("demo.help.jump", gameOptions.jumpKey.method_18174()), (float)i, (float)(j + 24), 5197647);
		this.textRenderer.method_18355(I18n.translate("demo.help.inventory", gameOptions.inventoryKey.method_18174()), (float)i, (float)(j + 36), 5197647);
		this.textRenderer.drawTrimmed(I18n.translate("demo.help.fullWrapped"), i, j + 68, 218, 2039583);
		super.render(mouseX, mouseY, tickDelta);
	}
}
