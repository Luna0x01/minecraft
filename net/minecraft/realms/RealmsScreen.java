package net.minecraft.realms;

import com.google.common.util.concurrent.ListenableFuture;
import com.mojang.util.UUIDTypeAdapter;
import java.util.List;
import net.minecraft.class_4107;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.realms.RealmsScreenProxy;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public abstract class RealmsScreen extends RealmsGuiEventListener {
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
	private MinecraftClient minecraft;
	public int width;
	public int height;
	private final RealmsScreenProxy proxy = new RealmsScreenProxy(this);

	public RealmsScreenProxy getProxy() {
		return this.proxy;
	}

	public void init() {
	}

	public void init(MinecraftClient client, int i, int j) {
		this.minecraft = client;
	}

	public void drawCenteredString(String text, int x, int y, int color) {
		this.proxy.drawCenteredString(text, x, y, color);
	}

	public int draw(String string, int i, int j, int k, boolean bl) {
		return this.proxy.method_18515(string, i, j, k, bl);
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
			((RealmsButton)this.proxy.getButtons().get(i)).render(mouseX, mouseY, tickDelta);
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

	public ListenableFuture<Object> threadSafeSetScreen(RealmsScreen realmsScreen) {
		return this.minecraft.submit(() -> Realms.setScreen(realmsScreen));
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

	public void childrenClear() {
		this.proxy.method_18518();
	}

	public void addWidget(RealmsGuiEventListener realmsGuiEventListener) {
		this.proxy.method_18514(realmsGuiEventListener);
	}

	public void removeWidget(RealmsGuiEventListener realmsGuiEventListener) {
		this.proxy.method_18516(realmsGuiEventListener);
	}

	public boolean hasWidget(RealmsGuiEventListener realmsGuiEventListener) {
		return this.proxy.method_18517(realmsGuiEventListener);
	}

	public void buttonsAdd(RealmsButton button) {
		this.proxy.addButton(button);
	}

	public List<RealmsButton> buttons() {
		return this.proxy.getButtons();
	}

	protected void buttonsClear() {
		this.proxy.method_18519();
	}

	protected void focusOn(RealmsGuiEventListener realmsGuiEventListener) {
		this.proxy.method_18424(realmsGuiEventListener.getProxy());
	}

	public void focusNext() {
		this.proxy.method_18426();
	}

	public RealmsEditBox newEditBox(int id, int x, int y, int width, int height) {
		return new RealmsEditBox(id, x, y, width, height);
	}

	public void confirmResult(boolean confirmed, int id) {
	}

	public static String getLocalizedString(String key) {
		return I18n.translate(key);
	}

	public static String getLocalizedString(String key, Object... args) {
		return I18n.translate(key, args);
	}

	public List<String> getLocalizedStringWithLineWidth(String string, int i) {
		return this.minecraft.textRenderer.wrapLines(I18n.translate(string), i);
	}

	public RealmsAnvilLevelStorageSource getLevelStorageSource() {
		return new RealmsAnvilLevelStorageSource(MinecraftClient.getInstance().getCurrentSave());
	}

	public void removed() {
	}

	protected void removeButton(RealmsButton realmsButton) {
		this.proxy.removeButton(realmsButton);
	}

	protected void setKeyboardHandlerSendRepeatsToGui(boolean bl) {
		this.minecraft.field_19946.method_18191(bl);
	}

	protected boolean isKeyDown(int i) {
		return class_4107.method_18154(i);
	}
}
