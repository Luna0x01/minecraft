package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.util.Identifier;

public class HopperScreen extends HandledScreen {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/hopper.png");
	private final Inventory playerInventory;
	private final Inventory hopperInventory;

	public HopperScreen(PlayerInventory playerInventory, Inventory inventory) {
		super(new HopperScreenHandler(playerInventory, inventory, MinecraftClient.getInstance().player));
		this.playerInventory = playerInventory;
		this.hopperInventory = inventory;
		this.passEvents = false;
		this.backgroundHeight = 133;
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		super.render(mouseX, mouseY, tickDelta);
		this.renderTooltip(mouseX, mouseY);
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		this.textRenderer.method_18355(this.hopperInventory.getName().asFormattedString(), 8.0F, 6.0F, 4210752);
		this.textRenderer.method_18355(this.playerInventory.getName().asFormattedString(), 8.0F, (float)(this.backgroundHeight - 96 + 2), 4210752);
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
