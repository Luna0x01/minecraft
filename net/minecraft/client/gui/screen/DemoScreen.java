package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import java.net.URI;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DemoScreen extends Screen {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Identifier BACKGROUND_TEXTURE = new Identifier("textures/gui/demo_background.png");

	@Override
	public void init() {
		this.buttons.clear();
		int i = -16;
		this.buttons.add(new ButtonWidget(1, this.width / 2 - 116, this.height / 2 + 62 + -16, 114, 20, I18n.translate("demo.help.buy")));
		this.buttons.add(new ButtonWidget(2, this.width / 2 + 2, this.height / 2 + 62 + -16, 114, 20, I18n.translate("demo.help.later")));
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		switch (button.id) {
			case 1:
				button.active = false;

				try {
					Class<?> class_ = Class.forName("java.awt.Desktop");
					Object object = class_.getMethod("getDesktop").invoke(null);
					class_.getMethod("browse", URI.class).invoke(object, new URI("http://www.minecraft.net/store?source=demo"));
				} catch (Throwable var4) {
					LOGGER.error("Couldn't open link", var4);
				}
				break;
			case 2:
				this.client.setScreen(null);
				this.client.closeScreen();
		}
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
		this.textRenderer.draw(I18n.translate("demo.help.title"), i, j, 2039583);
		j += 12;
		GameOptions gameOptions = this.client.options;
		this.textRenderer
			.draw(
				I18n.translate(
					"demo.help.movementShort",
					GameOptions.getFormattedNameForKeyCode(gameOptions.forwardKey.getCode()),
					GameOptions.getFormattedNameForKeyCode(gameOptions.leftKey.getCode()),
					GameOptions.getFormattedNameForKeyCode(gameOptions.backKey.getCode()),
					GameOptions.getFormattedNameForKeyCode(gameOptions.rightKey.getCode())
				),
				i,
				j,
				5197647
			);
		this.textRenderer.draw(I18n.translate("demo.help.movementMouse"), i, j + 12, 5197647);
		this.textRenderer.draw(I18n.translate("demo.help.jump", GameOptions.getFormattedNameForKeyCode(gameOptions.jumpKey.getCode())), i, j + 24, 5197647);
		this.textRenderer.draw(I18n.translate("demo.help.inventory", GameOptions.getFormattedNameForKeyCode(gameOptions.inventoryKey.getCode())), i, j + 36, 5197647);
		this.textRenderer.drawTrimmed(I18n.translate("demo.help.fullWrapped"), i, j + 68, 218, 2039583);
		super.render(mouseX, mouseY, tickDelta);
	}
}
