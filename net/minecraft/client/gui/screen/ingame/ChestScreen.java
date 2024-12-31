package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ChestScreenHandler;
import net.minecraft.util.Identifier;

public class ChestScreen extends HandledScreen {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/generic_54.png");
	private final Inventory playerInventory;
	private final Inventory inventory;
	private final int rows;

	public ChestScreen(Inventory inventory, Inventory inventory2) {
		super(new ChestScreenHandler(inventory, inventory2, MinecraftClient.getInstance().player));
		this.playerInventory = inventory;
		this.inventory = inventory2;
		this.passEvents = false;
		int i = 222;
		int j = 114;
		this.rows = inventory2.getInvSize() / 9;
		this.backgroundHeight = 114 + this.rows * 18;
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		super.render(mouseX, mouseY, tickDelta);
		this.renderTooltip(mouseX, mouseY);
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		this.textRenderer.method_18355(this.inventory.getName().asFormattedString(), 8.0F, 6.0F, 4210752);
		this.textRenderer.method_18355(this.playerInventory.getName().asFormattedString(), 8.0F, (float)(this.backgroundHeight - 96 + 2), 4210752);
	}

	@Override
	protected void drawBackground(float delta, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int i = (this.width - this.backgroundWidth) / 2;
		int j = (this.height - this.backgroundHeight) / 2;
		this.drawTexture(i, j, 0, 0, this.backgroundWidth, this.rows * 18 + 17);
		this.drawTexture(i, j + this.rows * 18 + 17, 0, 126, this.backgroundWidth, 96);
	}
}
