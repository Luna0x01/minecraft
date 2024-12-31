package net.minecraft.client.gui.hud.spectator;

import com.google.common.base.MoreObjects;
import java.util.List;

public class SpectatorMenuState {
	private final SpectatorMenuCommandGroup group;
	private final List<SpectatorMenuCommand> commands;
	private final int selectedSlot;

	public SpectatorMenuState(SpectatorMenuCommandGroup spectatorMenuCommandGroup, List<SpectatorMenuCommand> list, int i) {
		this.group = spectatorMenuCommandGroup;
		this.commands = list;
		this.selectedSlot = i;
	}

	public SpectatorMenuCommand getCommand(int slot) {
		return slot >= 0 && slot < this.commands.size()
			? (SpectatorMenuCommand)MoreObjects.firstNonNull(this.commands.get(slot), SpectatorMenu.BLANK_COMMAND)
			: SpectatorMenu.BLANK_COMMAND;
	}

	public int getSelectedSlot() {
		return this.selectedSlot;
	}
}
