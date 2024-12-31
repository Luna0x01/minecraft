package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.Generic3x3ScreenHandler;
import net.minecraft.util.Identifier;

public class DispenserScreen extends HandledScreen {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/dispenser.png");
	private final PlayerInventory playerInventory;
	public Inventory inventory;

	public DispenserScreen(PlayerInventory playerInventory, Inventory inventory) {
		super(new Generic3x3ScreenHandler(playerInventory, inventory));
		this.playerInventory = playerInventory;
		this.inventory = inventory;
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		super.render(mouseX, mouseY, tickDelta);
		this.renderTooltip(mouseX, mouseY);
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		String string = this.inventory.getName().asUnformattedString();
		this.textRenderer.draw(string, this.backgroundWidth / 2 - this.textRenderer.getStringWidth(string) / 2, 6, 4210752);
		this.textRenderer.draw(this.playerInventory.getName().asUnformattedString(), 8, this.backgroundHeight - 96 + 2, 4210752);
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
