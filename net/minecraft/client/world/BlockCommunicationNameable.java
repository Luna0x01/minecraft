package net.minecraft.client.world;

import javax.annotation.Nullable;
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
	public Text method_15540() {
		return this.text;
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Nullable
	@Override
	public Text method_15541() {
		return this.text;
	}
}
