package net.minecraft.client.gui.screen.ingame;

import net.minecraft.client.gui.screen.recipebook.FurnaceRecipeBookScreen;
import net.minecraft.container.FurnaceContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class FurnaceScreen extends AbstractFurnaceScreen<FurnaceContainer> {
	private static final Identifier BG_TEX = new Identifier("textures/gui/container/furnace.png");

	public FurnaceScreen(FurnaceContainer furnaceContainer, PlayerInventory playerInventory, Text text) {
		super(furnaceContainer, new FurnaceRecipeBookScreen(), playerInventory, text, BG_TEX);
	}
}
