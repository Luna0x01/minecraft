package net.minecraft.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Nameable;

public interface NamedScreenHandlerFactory extends Nameable {
	ScreenHandler createScreenHandler(PlayerInventory inventory, PlayerEntity player);

	String getId();
}
