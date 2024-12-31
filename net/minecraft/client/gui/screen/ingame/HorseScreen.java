package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.class_3135;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.AbstractHorseEntity;
import net.minecraft.entity.LlamaEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.HorseScreenHandler;
import net.minecraft.util.Identifier;

public class HorseScreen extends HandledScreen {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/horse.png");
	private final Inventory armorInventory;
	private final Inventory chestInventory;
	private final AbstractHorseEntity field_15252;
	private float mouseX;
	private float mouseY;

	public HorseScreen(Inventory inventory, Inventory inventory2, AbstractHorseEntity abstractHorseEntity) {
		super(new HorseScreenHandler(inventory, inventory2, abstractHorseEntity, MinecraftClient.getInstance().player));
		this.armorInventory = inventory;
		this.chestInventory = inventory2;
		this.field_15252 = abstractHorseEntity;
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
		if (this.field_15252 instanceof class_3135) {
			class_3135 lv = (class_3135)this.field_15252;
			if (lv.method_13963()) {
				this.drawTexture(i + 79, j + 17, 0, this.backgroundHeight, lv.method_13965() * 18, 54);
			}
		}

		if (this.field_15252.method_13974()) {
			this.drawTexture(i + 7, j + 35 - 18, 18, this.backgroundHeight + 54, 18, 18);
		}

		if (this.field_15252.method_13984()) {
			if (this.field_15252 instanceof LlamaEntity) {
				this.drawTexture(i + 7, j + 35, 36, this.backgroundHeight + 54, 18, 18);
			} else {
				this.drawTexture(i + 7, j + 35, 0, this.backgroundHeight + 54, 18, 18);
			}
		}

		SurvivalInventoryScreen.renderEntity(i + 51, j + 60, 17, (float)(i + 51) - this.mouseX, (float)(j + 75 - 50) - this.mouseY, this.field_15252);
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.mouseX = (float)mouseX;
		this.mouseY = (float)mouseY;
		super.render(mouseX, mouseY, tickDelta);
	}
}
