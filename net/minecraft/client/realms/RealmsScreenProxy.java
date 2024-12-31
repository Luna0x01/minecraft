package net.minecraft.client.realms;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DelegatingRealmsButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;

public class RealmsScreenProxy extends Screen {
	private RealmsScreen realmsScreen;

	public RealmsScreenProxy(RealmsScreen realmsScreen) {
		this.realmsScreen = realmsScreen;
		super.buttons = Collections.synchronizedList(Lists.newArrayList());
	}

	public RealmsScreen getRealmsScreen() {
		return this.realmsScreen;
	}

	@Override
	public void init() {
		this.realmsScreen.init();
		super.init();
	}

	public void drawCenteredString(String text, int x, int y, int color) {
		super.drawCenteredString(this.textRenderer, text, x, y, color);
	}

	public void drawString(String text, int x, int y, int color, boolean shadow) {
		if (shadow) {
			super.drawWithShadow(this.textRenderer, text, x, y, color);
		} else {
			this.textRenderer.draw(text, x, y, color);
		}
	}

	@Override
	public void drawTexture(int x, int y, int u, int v, int width, int height) {
		this.realmsScreen.blit(x, y, u, v, width, height);
		super.drawTexture(x, y, u, v, width, height);
	}

	@Override
	public void fillGradient(int x1, int y1, int x2, int y2, int color1, int color2) {
		super.fillGradient(x1, y1, x2, y2, color1, color2);
	}

	@Override
	public void renderBackground() {
		super.renderBackground();
	}

	@Override
	public boolean shouldPauseGame() {
		return super.shouldPauseGame();
	}

	@Override
	public void renderBackground(int alpha) {
		super.renderBackground(alpha);
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.realmsScreen.render(mouseX, mouseY, tickDelta);
	}

	@Override
	public void renderTooltip(ItemStack stack, int x, int y) {
		super.renderTooltip(stack, x, y);
	}

	@Override
	public void renderTooltip(String text, int x, int y) {
		super.renderTooltip(text, x, y);
	}

	@Override
	public void renderTooltip(List<String> text, int x, int y) {
		super.renderTooltip(text, x, y);
	}

	@Override
	public void tick() {
		this.realmsScreen.tick();
		super.tick();
	}

	public int getFontHeight() {
		return this.textRenderer.fontHeight;
	}

	public int getStringWidth(String text) {
		return this.textRenderer.getStringWidth(text);
	}

	public void drawWithShadow(String text, int x, int y, int color) {
		this.textRenderer.drawWithShadow(text, (float)x, (float)y, color);
	}

	public List<String> wrapLines(String text, int i) {
		return this.textRenderer.wrapLines(text, i);
	}

	@Override
	public final void buttonClicked(ButtonWidget button) {
		this.realmsScreen.buttonClicked(((DelegatingRealmsButtonWidget)button).getDelegate());
	}

	public void clear() {
		super.buttons.clear();
	}

	public void addButton(RealmsButton button) {
		super.buttons.add(button.getProxy());
	}

	public List<RealmsButton> getButtons() {
		List<RealmsButton> list = Lists.newArrayListWithExpectedSize(super.buttons.size());

		for (ButtonWidget buttonWidget : super.buttons) {
			list.add(((DelegatingRealmsButtonWidget)buttonWidget).getDelegate());
		}

		return list;
	}

	public void removeButton(RealmsButton button) {
		super.buttons.remove(button.getProxy());
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		this.realmsScreen.mouseClicked(mouseX, mouseY, button);
		super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void handleMouse() {
		this.realmsScreen.mouseEvent();
		super.handleMouse();
	}

	@Override
	public void handleKeyboard() {
		this.realmsScreen.keyboardEvent();
		super.handleKeyboard();
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int button) {
		this.realmsScreen.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public void mouseDragged(int mouseX, int mouseY, int button, long mouseLastClicked) {
		this.realmsScreen.mouseDragged(mouseX, mouseY, button, mouseLastClicked);
	}

	@Override
	public void keyPressed(char id, int code) {
		this.realmsScreen.keyPressed(id, code);
	}

	@Override
	public void confirmResult(boolean confirmed, int id) {
		this.realmsScreen.confirmResult(confirmed, id);
	}

	@Override
	public void removed() {
		this.realmsScreen.removed();
		super.removed();
	}
}
