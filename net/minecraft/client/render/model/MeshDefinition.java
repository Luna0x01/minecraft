package net.minecraft.client.render.model;

import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.ItemStack;

public interface MeshDefinition {
	ModelIdentifier getIdentifier(ItemStack stack);
}
