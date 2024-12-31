package net.minecraft.client.gui.hud.spectator;

import net.minecraft.text.Text;

public interface SpectatorMenuCommand {
	void use(SpectatorMenu spectatorMenu);

	Text getName();

	void renderIcon(float f, int i);

	boolean isEnabled();
}
