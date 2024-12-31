package net.minecraft.client.gui.screen.recipebook;

import java.util.Set;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class FurnaceRecipeBookScreen extends AbstractFurnaceRecipeBookScreen {
	private static final Text TOGGLE_SMELTABLE_RECIPES_TEXT = new TranslatableText("gui.recipebook.toggleRecipes.smeltable");

	@Override
	protected Text getToggleCraftableButtonText() {
		return TOGGLE_SMELTABLE_RECIPES_TEXT;
	}

	@Override
	protected Set<Item> getAllowedFuels() {
		return AbstractFurnaceBlockEntity.createFuelTimeMap().keySet();
	}
}
