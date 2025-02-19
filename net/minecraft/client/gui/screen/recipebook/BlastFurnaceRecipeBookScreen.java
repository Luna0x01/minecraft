package net.minecraft.client.gui.screen.recipebook;

import java.util.Set;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class BlastFurnaceRecipeBookScreen extends AbstractFurnaceRecipeBookScreen {
	private static final Text TOGGLE_BLASTABLE_RECIPES_TEXT = new TranslatableText("gui.recipebook.toggleRecipes.blastable");

	@Override
	protected Text getToggleCraftableButtonText() {
		return TOGGLE_BLASTABLE_RECIPES_TEXT;
	}

	@Override
	protected Set<Item> getAllowedFuels() {
		return AbstractFurnaceBlockEntity.createFuelTimeMap().keySet();
	}
}
