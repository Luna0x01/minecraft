package net.minecraft.client.realms;

import com.google.common.collect.Lists;
import java.util.HashSet;
import java.util.List;
import net.minecraft.class_4122;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DelegatingRealmsButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsGuiEventListener;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsScreenProxy extends Screen {
	private final RealmsScreen realmsScreen;
	private static final Logger field_20191 = LogManager.getLogger();

	public RealmsScreenProxy(RealmsScreen realmsScreen) {
		this.realmsScreen = realmsScreen;
	}

	public RealmsScreen getRealmsScreen() {
		return this.realmsScreen;
	}

	@Override
	public void init(MinecraftClient client, int width, int height) {
		this.realmsScreen.init(client, width, height);
		super.init(client, width, height);
	}

	@Override
	protected void init() {
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
			this.textRenderer.method_18355(text, (float)x, (float)y, color);
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

	public void method_18518() {
		this.field_20307.clear();
	}

	public void method_18514(RealmsGuiEventListener realmsGuiEventListener) {
		if (this.method_18517(realmsGuiEventListener) || !this.field_20307.add(realmsGuiEventListener.getProxy())) {
			field_20191.error("Tried to add the same widget multiple times: " + realmsGuiEventListener);
		}
	}

	public void method_18516(RealmsGuiEventListener realmsGuiEventListener) {
		if (!this.method_18517(realmsGuiEventListener) || !this.field_20307.remove(realmsGuiEventListener.getProxy())) {
			field_20191.error("Tried to add the same widget multiple times: " + realmsGuiEventListener);
		}
	}

	public boolean method_18517(RealmsGuiEventListener realmsGuiEventListener) {
		return this.field_20307.contains(realmsGuiEventListener.getProxy());
	}

	public void addButton(RealmsButton button) {
		this.addButton(button.getProxy());
	}

	public List<RealmsButton> getButtons() {
		List<RealmsButton> list = Lists.newArrayListWithExpectedSize(this.buttons.size());

		for (ButtonWidget buttonWidget : this.buttons) {
			list.add(((DelegatingRealmsButtonWidget)buttonWidget).getDelegate());
		}

		return list;
	}

	public void method_18519() {
		HashSet<class_4122> hashSet = new HashSet(this.buttons);
		this.field_20307.removeIf(hashSet::contains);
		this.buttons.clear();
	}

	public void removeButton(RealmsButton button) {
		this.field_20307.remove(button.getProxy());
		this.buttons.remove(button.getProxy());
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		return this.realmsScreen.mouseClicked(d, e, i) ? true : method_18513(this, d, e, i);
	}

	@Override
	public boolean mouseReleased(double d, double e, int i) {
		return this.realmsScreen.mouseReleased(d, e, i);
	}

	@Override
	public boolean mouseDragged(double d, double e, int i, double f, double g) {
		return this.realmsScreen.mouseDragged(d, e, i, f, g) ? true : super.mouseDragged(d, e, i, f, g);
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		return this.realmsScreen.keyPressed(i, j, k) ? true : super.keyPressed(i, j, k);
	}

	@Override
	public boolean charTyped(char c, int i) {
		return this.realmsScreen.charTyped(c, i) ? true : super.charTyped(c, i);
	}

	@Override
	public void confirmResult(boolean bl, int i) {
		this.realmsScreen.confirmResult(bl, i);
	}

	@Override
	public void removed() {
		this.realmsScreen.removed();
		super.removed();
	}

	public int method_18515(String string, int i, int j, int k, boolean bl) {
		return bl ? this.textRenderer.drawWithShadow(string, (float)i, (float)j, k) : this.textRenderer.method_18355(string, (float)i, (float)j, k);
	}
}
