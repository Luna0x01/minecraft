package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CraftingTableScreen extends HandledScreen {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/crafting_table.png");

	public CraftingTableScreen(PlayerInventory playerInventory, World world) {
		this(playerInventory, world, BlockPos.ORIGIN);
	}

	public CraftingTableScreen(PlayerInventory playerInventory, World world, BlockPos blockPos) {
		super(new CraftingScreenHandler(playerInventory, world, blockPos));
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		this.textRenderer.draw(I18n.translate("container.crafting"), 28, 6, 4210752);
		this.textRenderer.draw(I18n.translate("container.inventory"), 8, this.backgroundHeight - 96 + 2, 4210752);
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
