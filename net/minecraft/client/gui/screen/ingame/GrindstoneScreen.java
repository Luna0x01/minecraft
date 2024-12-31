package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.container.GrindstoneContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class GrindstoneScreen extends ContainerScreen<GrindstoneContainer> {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/grindstone.png");

	public GrindstoneScreen(GrindstoneContainer grindstoneContainer, PlayerInventory playerInventory, Text text) {
		super(grindstoneContainer, playerInventory, text);
	}

	@Override
	protected void drawForeground(int i, int j) {
		this.font.draw(this.title.asFormattedString(), 8.0F, 6.0F, 4210752);
		this.font.draw(this.playerInventory.getDisplayName().asFormattedString(), 8.0F, (float)(this.containerHeight - 96 + 2), 4210752);
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.drawBackground(f, i, j);
		super.render(i, j, f);
		this.drawMouseoverTooltip(i, j);
	}

	@Override
	protected void drawBackground(float f, int i, int j) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(TEXTURE);
		int k = (this.width - this.containerWidth) / 2;
		int l = (this.height - this.containerHeight) / 2;
		this.blit(k, l, 0, 0, this.containerWidth, this.containerHeight);
		if ((this.container.getSlot(0).hasStack() || this.container.getSlot(1).hasStack()) && !this.container.getSlot(2).hasStack()) {
			this.blit(k + 92, l + 31, this.containerWidth, 0, 28, 21);
		}
	}
}
