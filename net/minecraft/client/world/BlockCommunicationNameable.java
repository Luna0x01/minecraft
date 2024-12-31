package net.minecraft.client.world;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;

public class BlockCommunicationNameable implements NamedScreenHandlerFactory {
	private final String id;
	private final Text text;

	public BlockCommunicationNameable(String string, Text text) {
		this.id = string;
		this.text = text;
	}

	@Override
	public ScreenHandler createScreenHandler(PlayerInventory inventory, PlayerEntity player) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getTranslationKey() {
		return this.text.asUnformattedString();
	}

	@Override
	public boolean hasCustomName() {
		return true;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public Text getName() {
		return this.text;
	}
}
