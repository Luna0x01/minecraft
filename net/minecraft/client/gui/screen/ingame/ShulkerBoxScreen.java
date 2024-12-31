package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.util.Identifier;

public class ShulkerBoxScreen extends HandledScreen {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/shulker_box.png");
	private final Inventory top;
	private final PlayerInventory bottom;

	public ShulkerBoxScreen(PlayerInventory playerInventory, Inventory inventory) {
		super(new ShulkerBoxScreenHandler(playerInventory, inventory, MinecraftClient.getInstance().player));
		this.bottom = playerInventory;
		this.top = inventory;
		this.backgroundHeight++;
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		super.render(mouseX, mouseY, tickDelta);
		this.renderTooltip(mouseX, mouseY);
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		this.textRenderer.draw(this.top.getName().asUnformattedString(), 8, 6, 4210752);
		this.textRenderer.draw(this.bottom.getName().asUnformattedString(), 8, this.backgroundHeight - 96 + 2, 4210752);
	}

	@Override
	protected void drawBackground(float delta, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int i = (this.width - this.backgroundWidth) / 2;
		int j = (this.height - this.backgroundHeight) / 2;
		this.drawTexture(i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
	}
}
