package net.minecraft.client.gui.screen.ingame;

import net.minecraft.client.gui.screen.recipebook.SmokerRecipeBookScreen;
import net.minecraft.container.SmokerContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SmokerScreen extends AbstractFurnaceScreen<SmokerContainer> {
	private static final Identifier BG_TEX = new Identifier("textures/gui/container/smoker.png");

	public SmokerScreen(SmokerContainer smokerContainer, PlayerInventory playerInventory, Text text) {
		super(smokerContainer, new SmokerRecipeBookScreen(), playerInventory, text, BG_TEX);
	}
}
