package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.BrewingScreenHandler;
import net.minecraft.util.Identifier;

public class BrewingStandScreen extends HandledScreen {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/brewing_stand.png");
	private final PlayerInventory inventory;
	private Inventory brewingInventory;

	public BrewingStandScreen(PlayerInventory playerInventory, Inventory inventory) {
		super(new BrewingScreenHandler(playerInventory, inventory));
		this.inventory = playerInventory;
		this.brewingInventory = inventory;
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		String string = this.brewingInventory.getName().asUnformattedString();
		this.textRenderer.draw(string, this.backgroundWidth / 2 - this.textRenderer.getStringWidth(string) / 2, 6, 4210752);
		this.textRenderer.draw(this.inventory.getName().asUnformattedString(), 8, this.backgroundHeight - 96 + 2, 4210752);
	}

	@Override
	protected void drawBackground(float delta, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int i = (this.width - this.backgroundWidth) / 2;
		int j = (this.height - this.backgroundHeight) / 2;
		this.drawTexture(i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
		int k = this.brewingInventory.getProperty(0);
		if (k > 0) {
			int l = (int)(28.0F * (1.0F - (float)k / 400.0F));
			if (l > 0) {
				this.drawTexture(i + 97, j + 16, 176, 0, 9, l);
			}

			int m = k / 2 % 7;
			switch (m) {
				case 0:
					l = 29;
					break;
				case 1:
					l = 24;
					break;
				case 2:
					l = 20;
					break;
				case 3:
					l = 16;
					break;
				case 4:
					l = 11;
					break;
				case 5:
					l = 6;
					break;
				case 6:
					l = 0;
			}

			if (l > 0) {
				this.drawTexture(i + 65, j + 14 + 29 - l, 185, 29 - l, 12, l);
			}
		}
	}
}
