package net.minecraft.realms;

import com.mojang.util.UUIDTypeAdapter;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.realms.RealmsScreenProxy;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class RealmsScreen {
	public static final int SKIN_HEAD_U = 8;
	public static final int SKIN_HEAD_V = 8;
	public static final int SKIN_HEAD_WIDTH = 8;
	public static final int SKIN_HEAD_HEIGHT = 8;
	public static final int SKIN_HAT_U = 40;
	public static final int SKIN_HAT_V = 8;
	public static final int SKIN_HAT_WIDTH = 8;
	public static final int SKIN_HAT_HEIGHT = 8;
	public static final int SKIN_TEX_WIDTH = 64;
	public static final int SKIN_TEX_HEIGHT = 64;
	protected MinecraftClient minecraft;
	public int width;
	public int height;
	private final RealmsScreenProxy proxy = new RealmsScreenProxy(this);

	public RealmsScreenProxy getProxy() {
		return this.proxy;
	}

	public void init() {
	}

	public void init(MinecraftClient client, int i, int j) {
	}

	public void drawCenteredString(String text, int x, int y, int color) {
		this.proxy.drawCenteredString(text, x, y, color);
	}

	public void drawString(String text, int x, int y, int color) {
		this.drawString(text, x, y, color, true);
	}

	public void drawString(String text, int x, int y, int color, boolean shadow) {
		this.proxy.drawString(text, x, y, color, false);
	}

	public void blit(int x, int y, int u, int v, int width, int height) {
		this.proxy.drawTexture(x, y, u, v, width, height);
	}

	public static void blit(int x, int y, float u, float v, int i, int j, int width, int height, float textureWidth, float textureHeight) {
		DrawableHelper.drawTexture(x, y, u, v, i, j, width, height, textureWidth, textureHeight);
	}

	public static void blit(int x, int y, float u, float v, int width, int height, float textureWidth, float textureHeight) {
		DrawableHelper.drawTexture(x, y, u, v, width, height, textureWidth, textureHeight);
	}

	public void fillGradient(int x1, int y1, int x2, int y2, int color1, int color2) {
		this.proxy.fillGradient(x1, y1, x2, y2, color1, color2);
	}

	public void renderBackground() {
		this.proxy.renderBackground();
	}

	public boolean isPauseScreen() {
		return this.proxy.shouldPauseGame();
	}

	public void renderBackground(int alpha) {
		this.proxy.renderBackground(alpha);
	}

	public void render(int mouseX, int mouseY, float tickDelta) {
		for (int i = 0; i < this.proxy.getButtons().size(); i++) {
			((RealmsButton)this.proxy.getButtons().get(i)).render(mouseX, mouseY);
		}
	}

	public void renderTooltip(ItemStack stack, int x, int y) {
		this.proxy.renderTooltip(stack, x, y);
	}

	public void renderTooltip(String text, int x, int y) {
		this.proxy.renderTooltip(text, x, y);
	}

	public void renderTooltip(List<String> text, int x, int y) {
		this.proxy.renderTooltip(text, x, y);
	}

	public static void bindFace(String uuid, String playerName) {
		Identifier identifier = AbstractClientPlayerEntity.getSkinId(playerName);
		if (identifier == null) {
			identifier = DefaultSkinHelper.getTexture(UUIDTypeAdapter.fromString(uuid));
		}

		AbstractClientPlayerEntity.loadSkin(identifier, playerName);
		MinecraftClient.getInstance().getTextureManager().bindTexture(identifier);
	}

	public static void bind(String id) {
		Identifier identifier = new Identifier(id);
		MinecraftClient.getInstance().getTextureManager().bindTexture(identifier);
	}

	public void tick() {
	}

	public int width() {
		return this.proxy.width;
	}

	public int height() {
		return this.proxy.height;
	}

	public int fontLineHeight() {
		return this.proxy.getFontHeight();
	}

	public int fontWidth(String text) {
		return this.proxy.getStringWidth(text);
	}

	public void fontDrawShadow(String text, int x, int y, int color) {
		this.proxy.drawWithShadow(text, x, y, color);
	}

	public List<String> fontSplit(String text, int i) {
		return this.proxy.wrapLines(text, i);
	}

	public void buttonClicked(RealmsButton realmsButton) {
	}

	public static RealmsButton newButton(int id, int x, int y, String label) {
		return new RealmsButton(id, x, y, label);
	}

	public static RealmsButton newButton(int id, int x, int y, int width, int height, String message) {
		return new RealmsButton(id, x, y, width, height, message);
	}

	public void buttonsClear() {
		this.proxy.clear();
	}

	public void buttonsAdd(RealmsButton button) {
		this.proxy.addButton(button);
	}

	public List<RealmsButton> buttons() {
		return this.proxy.getButtons();
	}

	public void buttonsRemove(RealmsButton button) {
		this.proxy.removeButton(button);
	}

	public RealmsEditBox newEditBox(int id, int x, int y, int width, int height) {
		return new RealmsEditBox(id, x, y, width, height);
	}

	public void mouseClicked(int mouseX, int mouseY, int button) {
	}

	public void mouseEvent() {
	}

	public void keyboardEvent() {
	}

	public void mouseReleased(int mouseX, int mouseY, int button) {
	}

	public void mouseDragged(int mouseX, int mouseY, int button, long mouseLastClicked) {
	}

	public void keyPressed(char id, int code) {
	}

	public void confirmResult(boolean confirmed, int id) {
	}

	public static String getLocalizedString(String key) {
		return I18n.translate(key);
	}

	public static String getLocalizedString(String key, Object... args) {
		return I18n.translate(key, args);
	}

	public RealmsAnvilLevelStorageSource getLevelStorageSource() {
		return new RealmsAnvilLevelStorageSource(MinecraftClient.getInstance().getCurrentSave());
	}

	public void removed() {
	}
}
