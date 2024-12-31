package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.BrewingScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class BrewingStandScreen extends HandledScreen {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/brewing_stand.png");
	private static final int[] field_13330 = new int[]{29, 24, 20, 16, 11, 6, 0};
	private final PlayerInventory inventory;
	private final Inventory brewingInventory;

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
		int k = this.brewingInventory.getProperty(1);
		int l = MathHelper.clamp((18 * k + 20 - 1) / 20, 0, 18);
		if (l > 0) {
			this.drawTexture(i + 60, j + 44, 176, 29, l, 4);
		}

		int m = this.brewingInventory.getProperty(0);
		if (m > 0) {
			int n = (int)(28.0F * (1.0F - (float)m / 400.0F));
			if (n > 0) {
				this.drawTexture(i + 97, j + 16, 176, 0, 9, n);
			}

			n = field_13330[m / 2 % 7];
			if (n > 0) {
				this.drawTexture(i + 63, j + 14 + 29 - n, 185, 29 - n, 12, n);
			}
		}
	}
}
