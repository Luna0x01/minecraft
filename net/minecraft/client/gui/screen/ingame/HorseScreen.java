package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.HorseScreenHandler;
import net.minecraft.util.Identifier;

public class HorseScreen extends HandledScreen {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/horse.png");
	private Inventory armorInventory;
	private Inventory chestInventory;
	private HorseBaseEntity entity;
	private float mouseX;
	private float mouseY;

	public HorseScreen(Inventory inventory, Inventory inventory2, HorseBaseEntity horseBaseEntity) {
		super(new HorseScreenHandler(inventory, inventory2, horseBaseEntity, MinecraftClient.getInstance().player));
		this.armorInventory = inventory;
		this.chestInventory = inventory2;
		this.entity = horseBaseEntity;
		this.passEvents = false;
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		this.textRenderer.draw(this.chestInventory.getName().asUnformattedString(), 8, 6, 4210752);
		this.textRenderer.draw(this.armorInventory.getName().asUnformattedString(), 8, this.backgroundHeight - 96 + 2, 4210752);
	}

	@Override
	protected void drawBackground(float delta, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int i = (this.width - this.backgroundWidth) / 2;
		int j = (this.height - this.backgroundHeight) / 2;
		this.drawTexture(i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
		if (this.entity.hasChest()) {
			this.drawTexture(i + 79, j + 17, 0, this.backgroundHeight, 90, 54);
		}

		if (this.entity.method_13129().method_13152()) {
			this.drawTexture(i + 7, j + 35, 0, this.backgroundHeight + 54, 18, 18);
		}

		SurvivalInventoryScreen.renderEntity(i + 51, j + 60, 17, (float)(i + 51) - this.mouseX, (float)(j + 75 - 50) - this.mouseY, this.entity);
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.mouseX = (float)mouseX;
		this.mouseY = (float)mouseY;
		super.render(mouseX, mouseY, tickDelta);
	}
}
