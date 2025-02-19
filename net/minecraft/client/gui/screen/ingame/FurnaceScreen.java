package net.minecraft.client.gui.screen.ingame;

import net.minecraft.client.gui.screen.recipebook.FurnaceRecipeBookScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.FurnaceScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class FurnaceScreen extends AbstractFurnaceScreen<FurnaceScreenHandler> {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/furnace.png");

	public FurnaceScreen(FurnaceScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, new FurnaceRecipeBookScreen(), inventory, title, TEXTURE);
	}
}
