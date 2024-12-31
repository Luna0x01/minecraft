package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.FurnaceScreenHandler;
import net.minecraft.util.Identifier;

public class FurnaceScreen extends HandledScreen {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/furnace.png");
	private final PlayerInventory playerInventory;
	private final Inventory furnaceInventory;

	public FurnaceScreen(PlayerInventory playerInventory, Inventory inventory) {
		super(new FurnaceScreenHandler(playerInventory, inventory));
		this.playerInventory = playerInventory;
		this.furnaceInventory = inventory;
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		super.render(mouseX, mouseY, tickDelta);
		this.renderTooltip(mouseX, mouseY);
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		String string = this.furnaceInventory.getName().asUnformattedString();
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
		if (FurnaceBlockEntity.isLit(this.furnaceInventory)) {
			int k = this.getFuelProgress(13);
			this.drawTexture(i + 56, j + 36 + 12 - k, 176, 12 - k, 14, k + 1);
		}

		int l = this.getCookProgress(24);
		this.drawTexture(i + 79, j + 34, 176, 14, l + 1, 16);
	}

	private int getCookProgress(int cookBarSteps) {
		int i = this.furnaceInventory.getProperty(2);
		int j = this.furnaceInventory.getProperty(3);
		return j != 0 && i != 0 ? i * cookBarSteps / j : 0;
	}

	private int getFuelProgress(int fuelBarSteps) {
		int i = this.furnaceInventory.getProperty(1);
		if (i == 0) {
			i = 200;
		}

		return this.furnaceInventory.getProperty(0) * fuelBarSteps / i;
	}
}
